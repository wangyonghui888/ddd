package com.panda.sport.rcs.trade.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.UuidUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.UpdateMarketCategoryDataSourceCodeDTO;
import com.panda.sport.manager.api.IMarketCategorySellApi;
import com.panda.sport.manager.api.bo.QueryMarketCategorySellBO;
import com.panda.sport.manager.api.dto.QueryMarketCategorySellDTO;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.RcsSysUserMapper;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplateMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainMapper;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsBatchChangeDataSourceRecord;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimitLog;
import com.panda.sport.rcs.pojo.RcsSysUser;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.dto.ClearDTO;
import com.panda.sport.rcs.pojo.dto.ClearSubDTO;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.pojo.vo.ThirdDataSourceCodeVo;
import com.panda.sport.rcs.trade.config.LSDataSourcePlayIdsConfig;
import com.panda.sport.rcs.trade.enums.TempTypeEnum;
import com.panda.sport.rcs.trade.service.PreAllMarketDataSourceSwitchService;
import com.panda.sport.rcs.trade.service.RcsBatchChangeDataSourceRecordService;
import com.panda.sport.rcs.trade.util.CommonUtil;
import com.panda.sport.rcs.trade.utils.mongopage.PageResult;
import com.panda.sport.rcs.trade.wrapper.SportMatchViewService;
import com.panda.sport.rcs.trade.wrapper.tourTemplate.IRcsTournamentTemplatePlayMargainService;
import com.panda.sport.rcs.utils.DataRealtimeApiUtils;
import com.panda.sport.rcs.utils.TradeUserUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import com.panda.sport.rcs.vo.MarketLiveOddsQueryVo;
import com.panda.sport.rcs.vo.PreAllMarketDataSourceVo;
import com.panda.sport.rcs.vo.mq.PlayOddsConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class PreAllMarketDataSourceSwitchServiceImpl implements PreAllMarketDataSourceSwitchService {

    @Autowired
    private RcsSysUserMapper rcsSysUserMapper;

    @Reference(check = false, lazy = true, retries = 3, timeout = 5000)
    private ITradeMarketConfigApi tradeMarketConfigApi;

    @Reference(check = false, lazy = true, retries = 1, timeout = 100000)
    IMarketCategorySellApi marketCategorySellApi;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    private SportMatchViewService sportMatchViewService;
    @Autowired
    private RcsTournamentTemplateMapper rcsTournamentTemplateMapper;

    @Autowired
    private RcsTournamentTemplatePlayMargainMapper playMargainMapper;
    @Autowired
    private IRcsTournamentTemplatePlayMargainService rcsTournamentTemplatePlayMargainService;
    @Autowired
    private RcsBatchChangeDataSourceRecordService rcsBatchChangeDataSourceRecordService;

    @Resource(name = "asyncPoolTaskExecutor")
    private ThreadPoolTaskExecutor asyncPoolTaskExecutor;
    //按每50个一组分割
    private static final Integer MAX_MATCH_LIMIT = 50;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private LSDataSourcePlayIdsConfig lsDataSourcePlayIdsConfig;

    @Override
    public void dataSourceSwitch(String before, String after, Integer userId) {
        MarketLiveOddsQueryVo matchQueryVo = new MarketLiveOddsQueryVo();
        matchQueryVo.setTradeId(Long.valueOf(userId));
        matchQueryVo.setSortType(1);
        matchQueryVo.setSportId(1L);
        matchQueryVo.setOperateMatchStatus(-1);
        List<MatchMarketLiveBean> matchInfoList = new ArrayList<>();//早盘赛事集合
        try {
            //获取早盘赛事
            Criteria criteriaPre = sportMatchViewService.buildMongoCriteria(matchQueryVo, 2);
            PageResult<MatchMarketLiveBean> matchIdPagePre = sportMatchViewService.queryMatchList(matchQueryVo, criteriaPre);
            if (Objects.nonNull(matchIdPagePre)) {
                matchInfoList.addAll(matchIdPagePre.getList());
            }
            if (CollectionUtils.isEmpty(matchInfoList)) {
                return;
            }
            RcsSysUser rcsSysUser = rcsSysUserMapper.selectById(userId);
            //计算切分次数
            int limit = (matchInfoList.size() + MAX_MATCH_LIMIT - 1) / MAX_MATCH_LIMIT;
            //MAX_MATCH_LIMIT场晒是1批 循环处理 间隔500毫秒
            Stream.iterate(0, n -> n + 1).limit(limit).map(i ->
                    matchInfoList.stream().skip(i * MAX_MATCH_LIMIT).limit(MAX_MATCH_LIMIT)
                            .collect(Collectors.toList())).forEach(e -> {
                Map<Long, List<RcsBatchChangeDataSourceRecord>> collect = getMatchList(null, e.stream().map(MatchMarketLiveBean::getMatchId).collect(Collectors.toList()), before, after);
                if (CollectionUtils.isEmpty(collect)) {
                    log.info("没有需要执行的数据1");
                    return;
                }
                log.info("批量切换数据源赛事玩法：{}", JSONObject.toJSONString(collect.keySet()));
                collect = changeDataSource(collect, rcsSysUser);
                if (CollectionUtils.isEmpty(collect)) {
                    log.info("没有需要执行的数据2");
                    return;
                }
                log.info("批量切换数据源赛事玩法changeDataSource结束：{}", JSONObject.toJSONString(collect.keySet()));
                // 保存入库
                rcsBatchChangeDataSourceRecordService.batchSave(collect.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    log.error("睡眠异常", ex);
                }
            });
            // 生成日志
            RcsQuotaBusinessLimitLog rcsQuotaBusinessLimitLog = setDataSourceLog(before, after, rcsSysUser.getId(), "10090");
            producerSendMessageUtils.sendMessage(CommonUtil.RCS_BUSINESS_LOG_SAVE, null, "10090", JSONObject.toJSONString(Arrays.asList(rcsQuotaBusinessLimitLog)));
        } catch (Exception e) {
            log.error("早盘批量处理发生异常,异常信息：{}", e.getMessage(), e);
        } finally {
            redisClient.delete("rcs:trade:updatePreAllMarketDataSourceStatus");
            log.info("批量修改数据源完成");
        }
    }


    @Override
    public HttpResponse batchRestoreDataSource(int maxDay, PreAllMarketDataSourceVo preAllMarketDataSourceVo, Integer userId) {
        try {
            List<RcsBatchChangeDataSourceRecord> list = rcsBatchChangeDataSourceRecordService.findLastList(maxDay, preAllMarketDataSourceVo);
            if (list.isEmpty()) {
                log.info("batchRestoreDataSource恢复数据源为空，近{}天没有需要恢复的数据", maxDay);
                return HttpResponse.failure("恢复数据源为空，近" + maxDay + "天没有需要恢复的数据");
            } else {
                log.info("batchRestoreDataSource:需要恢复的数据{}", list.stream().map(e -> e.getMatchId() + "_" + e.getPlayId()).collect(Collectors.joining(",")));
                Map<Long, List<RcsBatchChangeDataSourceRecord>> groupMatchRecord = list.stream().collect(Collectors.groupingBy(RcsBatchChangeDataSourceRecord::getMatchId));
                //计算切分次数
                int limit = (groupMatchRecord.size() + MAX_MATCH_LIMIT - 1) / MAX_MATCH_LIMIT;
                RcsSysUser rcsSysUser = rcsSysUserMapper.selectById(userId);
                //MAX_MATCH_LIMIT场晒是1批 循环处理 间隔500毫秒
                Stream.iterate(0, n -> n + 1).limit(limit).map(i ->
                        groupMatchRecord.keySet().stream().skip(i * MAX_MATCH_LIMIT).limit(MAX_MATCH_LIMIT)
                                .collect(Collectors.toList())).forEach(mathcIds -> {
                    List<RcsBatchChangeDataSourceRecord> o = mathcIds.stream().map(groupMatchRecord::get).flatMap(Collection::stream).collect(Collectors.toList());
                    // 获取 赛事id 和 玩法id 映射集合
                    Map<Long, List<RcsBatchChangeDataSourceRecord>> collect = getMatchList(o, mathcIds, null, null);
                    if (CollectionUtils.isEmpty(collect)) {
                        log.info("没有需要执行的数据1");
                    }
                    log.info("批量切换数据源赛事玩法：{}", JSONObject.toJSONString(collect.keySet()));
                    collect = changeDataSource(collect, rcsSysUser);
                    if (CollectionUtils.isEmpty(collect)) {
                        log.info("没有需要执行的数据2");
                    }
                    log.info("批量切换数据源赛事玩法changeDataSource结束：{}", JSONObject.toJSONString(collect.keySet()));
                    rcsBatchChangeDataSourceRecordService.removeBatchByIds(collect.values().stream().flatMap(Collection::stream).map(RcsBatchChangeDataSourceRecord::getId).collect(Collectors.toList()));
                    log.info("批量切换数据源当前批次执行完成:{}", collect.values().size());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        log.error("睡眠异常", ex);
                    }
                });
                // 生成日志
                RcsQuotaBusinessLimitLog rcsQuotaBusinessLimitLog = setDataSourceLog(preAllMarketDataSourceVo.getBefore(), preAllMarketDataSourceVo.getAfter(), rcsSysUser.getId(), "10091");
                producerSendMessageUtils.sendMessage(CommonUtil.RCS_BUSINESS_LOG_SAVE, null, "10091", JSONObject.toJSONString(Arrays.asList(rcsQuotaBusinessLimitLog)));
            }
        } catch (Exception e) {
            log.error("batchRestoreDataSource恢复数据源处理异常：{}", e.getMessage(), e);
        } finally {
            redisClient.delete("rcs:trade:updatePreAllMarketDataSourceStatus");
            redisClient.delete("rcs:trade:updatePreAllMarketDataSource");
        }
        log.info("批量切换数据源执行完成");
        return HttpResponse.success();
    }

    private Map<Long, List<RcsBatchChangeDataSourceRecord>> getMatchList(List<RcsBatchChangeDataSourceRecord> oldList, List<Long> filterMatchIds, String beforeDataSourceCode, String afterDataSourceCode) {
        //查询融合该赛事已开售玩法
        List<List<QueryMarketCategorySellBO>> list = filterMatchIds.stream().map(e ->
                        CompletableFuture.supplyAsync(() -> {
                            QueryMarketCategorySellDTO queryBean = new QueryMarketCategorySellDTO();
                            queryBean.setMatchId(e);
                            queryBean.setMarketTypes(Arrays.asList("PRE"));
                            queryBean.setSellStatus("Sold");
                            queryBean.setMatchStatus("Enable");
                            queryBean.setSportId(1L);
                            com.panda.merge.dto.Request<QueryMarketCategorySellDTO> request = new com.panda.merge.dto.Request<>();
                            request.setData(queryBean);
                            request.setDataSourceTime(System.currentTimeMillis());
                            request.setLinkId(UUID.randomUUID().toString().replace("-", ""));
                            request.setOperaterId(TradeUserUtils.getUserIdNoException().longValue());
                            com.panda.merge.dto.Response<List<QueryMarketCategorySellBO>> response = marketCategorySellApi.queryMarketCategorySell(request);
                            return response;
                        }, asyncPoolTaskExecutor))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .filter(e -> e.getData() != null && e.getData().size() > 0)
                .map(e -> e.getData()
                        .stream()
                        .filter(o -> o.getIsSell() == 1 && o.getSellStatus().equals("Sold"))
                        .collect(Collectors.toList())
                ).collect(Collectors.toList());


        //bug 40872 只处理固定配置的25个ls玩法
        if (StringUtils.isBlank(lsDataSourcePlayIdsConfig.getPlayIds())) {
            log.error("ls 玩法列表没有配置");
            throw new RcsServiceException("ls 玩法列表没有配置");
        }
        List<Long> lsPlayIds  = Arrays.stream(lsDataSourcePlayIdsConfig.getPlayIds().split(",")).map(Long::parseLong).distinct().collect(Collectors.toList());

        return list.stream().map(item ->
                        CompletableFuture.supplyAsync(() -> {
                            // 根据玩法id 获取 该玩法数据源
                            List<Long> categoryIdList = item.stream().map(QueryMarketCategorySellBO::getMarketCategoryId).collect(Collectors.toList());
                            //bug 40872 只处理固定配置的25个ls玩法
                            categoryIdList = categoryIdList.stream().filter(lsPlayIds::contains).collect(Collectors.toList());
                            if(categoryIdList.isEmpty()){
                                return null;
                            }
                            List<RcsTournamentTemplatePlayMargain> rcsTournamentTemplatePlayMargains = playMargainMapper.selectAllByMarketDataSource(item.get(0).getMatchId(), categoryIdList, beforeDataSourceCode);
                            if (StringUtils.isNotBlank(beforeDataSourceCode)) {
                                //需要变更的数据，如果是切换 每条 before 与 after 都是一样的
                                return rcsTournamentTemplatePlayMargains
                                        .stream()
                                        .map(t2 -> {
                                            RcsBatchChangeDataSourceRecord rcsBatchChangeDataSourceRecord = new RcsBatchChangeDataSourceRecord();
                                            rcsBatchChangeDataSourceRecord.setNewDataSourceCode(afterDataSourceCode);
                                            rcsBatchChangeDataSourceRecord.setOldDataSourceCode(beforeDataSourceCode);
                                            rcsBatchChangeDataSourceRecord.setMatchId(item.get(0).getMatchId());
                                            rcsBatchChangeDataSourceRecord.setPlayId(t2.getPlayId().longValue());
                                            rcsBatchChangeDataSourceRecord.setPlayMargainId(t2.getId());
                                            rcsBatchChangeDataSourceRecord.setCreateTime(new Date());
                                            return rcsBatchChangeDataSourceRecord;
                                        })
                                        .collect(Collectors.toList());
                            } else {
                                //需要变更的数据，如果是恢复 每条 before 与 after 都是不一样的 将历史数据反转 before和after
                                Map<String, RcsBatchChangeDataSourceRecord> oldDataSourceCodes = oldList.stream().collect(Collectors.toMap(e -> e.getMatchId() + "_" + e.getPlayId(), e -> e));
                                return rcsTournamentTemplatePlayMargains.stream().filter(e ->
                                                oldDataSourceCodes.get(e.getMatchId() + "_" + e.getPlayId()) != null &&
                                                        e.getDataSource().equals(oldDataSourceCodes.get(e.getMatchId() + "_" + e.getPlayId()).getNewDataSourceCode())
                                        )
                                        .map(t2 -> {
                                            RcsBatchChangeDataSourceRecord oldRcsBatchChangeDataSourceRecord = oldDataSourceCodes.get(t2.getMatchId() + "_" + t2.getPlayId());
                                            RcsBatchChangeDataSourceRecord rcsBatchChangeDataSourceRecord = new RcsBatchChangeDataSourceRecord();
                                            rcsBatchChangeDataSourceRecord.setNewDataSourceCode(oldRcsBatchChangeDataSourceRecord.getOldDataSourceCode());
                                            rcsBatchChangeDataSourceRecord.setOldDataSourceCode(oldRcsBatchChangeDataSourceRecord.getNewDataSourceCode());
                                            rcsBatchChangeDataSourceRecord.setMatchId(oldRcsBatchChangeDataSourceRecord.getMatchId());
                                            rcsBatchChangeDataSourceRecord.setPlayId(oldRcsBatchChangeDataSourceRecord.getPlayId());
                                            rcsBatchChangeDataSourceRecord.setId(oldRcsBatchChangeDataSourceRecord.getId());
                                            rcsBatchChangeDataSourceRecord.setCreateTime(new Date());
                                            rcsBatchChangeDataSourceRecord.setPlayMargainId(t2.getId());
                                            return rcsBatchChangeDataSourceRecord;
                                        })
                                        .collect(Collectors.toList());
                            }

                        }, asyncPoolTaskExecutor))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(RcsBatchChangeDataSourceRecord::getMatchId));
    }

    private Map<Long, List<RcsBatchChangeDataSourceRecord>> changeDataSource(Map<Long, List<RcsBatchChangeDataSourceRecord>> collect, RcsSysUser rcsSysUser) {
        Map<Long, List<RcsBatchChangeDataSourceRecord>> successRecord = new ConcurrentHashMap<>();
        // 构造数据发送融合
        Map<Long, StandardMatchInfo> standardMatchInfos = standardMatchInfoMapper.selectBatchIds(collect.keySet())
                .stream().collect(Collectors.toMap(StandardMatchInfo::getId, e2 -> e2, (e1, e2) -> e1));
        collect.keySet().stream().map(matchId -> CompletableFuture.runAsync(() -> {
            List<RcsBatchChangeDataSourceRecord> item = collect.get(matchId);
            StandardMatchInfo standardMatchInfo = standardMatchInfos.get(matchId);
            List<UpdateMarketCategoryDataSourceCodeDTO> dtoList = Lists.newArrayList();
            if (!ObjectUtils.isEmpty(standardMatchInfo) && !StringUtils.isEmpty(standardMatchInfo.getThirdMatchListStr())) {
                item.forEach(e -> {
                    List<ThirdDataSourceCodeVo> thirdDataSourceCodeVos = JSONArray.parseArray(standardMatchInfo.getThirdMatchListStr(), ThirdDataSourceCodeVo.class);
                    boolean b = thirdDataSourceCodeVos.stream().anyMatch(filter -> null != filter.getCommerce() && !"RB".equals(filter.getDataSourceCode())
                            && filter.getCommerce().equals(String.valueOf(NumberUtils.INTEGER_ONE)) && filter.getDataSourceCode().equals(e.getNewDataSourceCode()));
                    if (b) {
                        UpdateMarketCategoryDataSourceCodeDTO dto = new UpdateMarketCategoryDataSourceCodeDTO();
                        dto.setMatchId(matchId);
                        dto.setMarketType("1");
                        dto.setDataSourceCode(e.getNewDataSourceCode());
                        dto.setMarketCategoryId(e.getPlayId());
                        dto.setSellStatus("Sold");
                        dto.setUserName(rcsSysUser.getUserCode());
                        dtoList.add(dto);
                    }
                });
            } else {
                log.info("赛事过滤，数据源不匹配:{}", matchId);
            }
            //如果该赛事没有玩法需要处理  就不发送接口
            if (!dtoList.isEmpty()) {
                try {
                    List<UpdateMarketCategoryDataSourceCodeDTO> requestList = dtoList.stream().filter(t -> !ObjectUtils.isEmpty(t)).collect(Collectors.toList());
                    Response<Object> response = DataRealtimeApiUtils.handleApi(requestList, new DataRealtimeApiUtils.ApiCall() {
                        @Override
                        @Trace
                        public <R> Response<R> callApi(Request request) {
                            Response rs = tradeMarketConfigApi.updateMarketCategoryDataSourceCode(request);
                            return rs;
                        }
                    });
                    if (!ObjectUtils.isEmpty(response) && response.isSuccess()) {
                        //只处理成功的数据
                        successRecord.put(matchId, item.stream().filter(o ->
                                        dtoList
                                                .stream()
                                                .map(UpdateMarketCategoryDataSourceCodeDTO::getMarketCategoryId)
                                                .collect(Collectors.toList())
                                                .contains(o.getPlayId()))
                                .collect(Collectors.toList()));
                    }
                } catch (Exception ex) {
                    log.error("修改玩法数据源失败：{}", ex.getMessage(), ex);
                }
            } else {
                log.info("赛事过滤，不请求:{}", matchId);
            }

        }, asyncPoolTaskExecutor)).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList());

        log.info("批量修改数据源融合成功结果：{}", JSONObject.toJSONString(successRecord.keySet()));
        if (!successRecord.isEmpty()) {
            QueryWrapper<RcsTournamentTemplate> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(RcsTournamentTemplate::getType, TempTypeEnum.MATCH.getId()).in(RcsTournamentTemplate::getTypeVal, successRecord.keySet()).eq(RcsTournamentTemplate::getMatchType, 1L);
            List<RcsTournamentTemplate> rcsTournamentTemplates = rcsTournamentTemplateMapper.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(rcsTournamentTemplates)) {
                //修改赛事模板玩法赔率源

                rcsTournamentTemplatePlayMargainService.updateBatchById(
                        successRecord
                                .values()
                                .stream()
                                .flatMap(Collection::stream)
                                .map(e -> {
                                    RcsTournamentTemplatePlayMargain rcsTournamentTemplatePlayMargain = new RcsTournamentTemplatePlayMargain();
                                    rcsTournamentTemplatePlayMargain.setId(e.getPlayMargainId());
                                    rcsTournamentTemplatePlayMargain.setDataSource(e.getNewDataSourceCode());
                                    rcsTournamentTemplatePlayMargain.setUpdateTime(new Date());
                                    return rcsTournamentTemplatePlayMargain;
                                }).collect(Collectors.toList()), 20);
            }
            //玩法清理水差，盘口差
            successRecord.forEach((k, item) -> {
                StandardMatchInfo info = standardMatchInfos.get(k);
                ArrayList<ClearSubDTO> objects = new ArrayList<>();
                item.forEach(categorySet -> {
                    ClearSubDTO clearSubDTO = new ClearSubDTO();
                    clearSubDTO.setPlayId(categorySet.getPlayId());
                    clearSubDTO.setMatchId(k);
                    objects.add(clearSubDTO);
                });
                ClearDTO clearDTO = new ClearDTO();
                clearDTO.setType(0);
                clearDTO.setClearType(8);
                clearDTO.setMatchId(info.getId());
                clearDTO.setBeginTime(info.getBeginTime());
                clearDTO.setList(objects);
                producerSendMessageUtils.sendMessage("RCS_CLEAR_MATCH_MARKET_TAG", null, UuidUtils.generateUuid(), clearDTO);


                Request r = new Request();
                String linkId = UUID.randomUUID().toString().replace("-", "") + "_play_odds_config";
                r.setLinkId(linkId);
                PlayOddsConfigVo playOddsConfigVo = new PlayOddsConfigVo();
                playOddsConfigVo.setMatchId(k);
                playOddsConfigVo.setMatchType(1);
                Map<String, List<Long>> map = item.stream().collect(Collectors.groupingBy(RcsBatchChangeDataSourceRecord::getNewDataSourceCode,
                        Collectors.collectingAndThen(Collectors.toList(), t -> t.stream().map(RcsBatchChangeDataSourceRecord::getPlayId).collect(Collectors.toList()))));
                playOddsConfigVo.setPlayDataSource(map);
                r.setData(playOddsConfigVo);
                producerSendMessageUtils.sendMessage("RCS_CATEGORY_ODDS_CONFIG_TOPIC", linkId, String.valueOf(k), r);
            });
            // 分时节点是否处理
        }
        return successRecord;
    }

    private RcsQuotaBusinessLimitLog setDataSourceLog(String beforeVal, String afterVal, Long userId, String operateType) {
        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        limitLoglog.setOperateCategory("紧急开关");
        limitLoglog.setObjectId(null);
        limitLoglog.setObjectName("早盘赔率数据源切换");
        limitLoglog.setExtObjectId("-");
        limitLoglog.setExtObjectName("-");
        limitLoglog.setOperateType(operateType);
        limitLoglog.setParamName("足球");
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        limitLoglog.setUserId(userId.toString());
        return limitLoglog;
    }
}
