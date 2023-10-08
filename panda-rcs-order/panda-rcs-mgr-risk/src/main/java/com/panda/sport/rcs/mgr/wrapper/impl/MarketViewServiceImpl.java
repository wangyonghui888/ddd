package com.panda.sport.rcs.mgr.wrapper.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.panda.sport.rcs.mgr.wrapper.RcsLanguageInternationService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.merge.api.ITradeMarketOddsApi;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardMarketDTO;
import com.panda.merge.dto.StandardMarketOddsDTO;
import com.panda.merge.dto.StandardMatchMarketDTO;
import com.panda.merge.dto.TradeMarketConfigDTO;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.sport.data.rcs.api.wrapper.LiveMarketOddsService;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.common.OddsValueConvertUtils;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.constants.MatchConstants;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.enums.MarketKindEnum;
import com.panda.sport.rcs.enums.MarketStatusEnum;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.mgr.wrapper.IRcsMatchMarketConfigService;
import com.panda.sport.rcs.mgr.wrapper.MarketViewService;
import com.panda.sport.rcs.mgr.wrapper.MatchPeriodService;
import com.panda.sport.rcs.mgr.wrapper.MatchStatisticsInfoService;
import com.panda.sport.rcs.mgr.wrapper.RcsMarketOddsConfigService;
import com.panda.sport.rcs.mgr.wrapper.RcsMatchCollectionService;
import com.panda.sport.rcs.mgr.wrapper.RcsMatchConfigService;
import com.panda.sport.rcs.mgr.wrapper.RcsOddsConvertMappingService;
import com.panda.sport.rcs.mgr.wrapper.StandardSportMarketCategoryService;
import com.panda.sport.rcs.mgr.wrapper.statistics.RcsMatchDimensionStatisticsService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.MatchBetChange;
import com.panda.sport.rcs.pojo.MatchStatisticsInfo;
import com.panda.sport.rcs.pojo.RcsMarketOddsConfig;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import com.panda.sport.rcs.pojo.statistics.RcsMatchDimensionStatistics;
import com.panda.sport.rcs.utils.DataRealtimeApiUtils;
import com.panda.sport.rcs.utils.DataRealtimeApiUtils.ApiCall;
import com.panda.sport.rcs.vo.MatchMarketLiveOddsVo;
import com.panda.sport.rcs.vo.SportMarketOddsVo;
import com.panda.sport.rcs.vo.SportMarketVo;
import com.panda.sport.rcs.vo.operation.RealTimeVolumeBean;
import com.panda.sport.rcs.vo.statistics.RcsMatchDimensionStatisticsVo;

import lombok.extern.slf4j.Slf4j;

/**
 * 盘口视图展示服务类
 */

@Service
@Slf4j
public class MarketViewServiceImpl implements MarketViewService {

    @Autowired
    MatchStatisticsInfoService matchStatisticsInfoService;
    @Autowired
    RcsMatchCollectionService rcsMatchCollectionService;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    RcsMatchConfigService rcsMatchConfigService;
    @Autowired
    IRcsMatchMarketConfigService matchMarketConfigService;
    @Autowired
    RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;
    @Autowired
    private RcsLanguageInternationService languageService;
    @Autowired
    private StandardSportMarketCategoryService marketCategoryService;
    @Autowired
    private RcsMarketOddsConfigService marketOddsConfigService;
    @Autowired
    private RcsOddsConvertMappingService rcsOddsConvertMappingService;
    @Autowired
    private StandardSportMarketServiceImpl standardSportMarketServiceImpl;
    @Autowired
    private StandardSportMarketOddsServiceImpl standardSportMarketOddsServiceImpl;
    @Reference(check = false, lazy = true, retries = 1, timeout = 5000)
    private LiveMarketOddsService liveMarketOddsService;
    @Autowired
    private RcsMatchDimensionStatisticsService matchDimensionStatisticsService;
    @Autowired
    private MatchPeriodService matchPeriodService;
    @Reference(check = false, lazy = true, retries = 1, timeout = 5000)
    private ITradeMarketConfigApi tradeMarketConfigApi;
    @Reference(check = false, lazy = true, retries = 1, timeout = 5000)
    private ITradeMarketOddsApi tradeMarketOddsApi;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    public static void main(String[] args) {
        Map<String, String> additionsMap = new HashMap<String, String>();
        additionsMap.put("addition1", "1111");
        TradeMarketConfigDTO bean = JSONObject.parseObject(JSONObject.toJSONString(new HashMap<>()), TradeMarketConfigDTO.class);
        bean.setAddition1("111");
        System.out.println(JSONObject.toJSONString(bean));
    }

    /**
     * 构建玩法数据
     *
     * @param
     * @return
     */
//    private List<MatchMarketLiveOddsVo.MatchMarketCategoryVo> buildMatchMarketCategoryVoList(Map<Long, List<MatchMarketLiveOddsVo.MatchMarketVo>> marketGroupMap) {
//        if (CollectionUtils.isEmpty(marketGroupMap)) {
//            return Collections.emptyList();
//        }
//        List<MatchMarketLiveOddsVo.MatchMarketCategoryVo> results = Lists.newArrayListWithCapacity(marketGroupMap.size());
//        for (Map.Entry<Long, List<MatchMarketLiveOddsVo.MatchMarketVo>> marketGroupEntry : marketGroupMap.entrySet()) {
//            if (CollectionUtils.isEmpty(marketGroupEntry.getValue())) {
//                continue;
//            }
//            MatchMarketLiveOddsVo.MatchMarketCategoryVo marketCategoryVo = new MatchMarketLiveOddsVo.MatchMarketCategoryVo();
//            marketCategoryVo.setId(marketGroupEntry.getKey());
//            // 设置玩法类型，供前端过滤显示置顶玩法使用
//            if (marketCategoryVo.getId() != null) {
//                StandardSportMarketCategory marketCategory = marketCategoryService.getCachedMarketCategoryById(marketCategoryVo.getId());
//                if (marketCategory != null) {
//                    marketCategoryVo.setType(marketCategory.getType());
//                }
//            }
//            marketCategoryVo.setMatchMarketVoList(marketGroupEntry.getValue());
//            // 按赔率差值排序  只取3个，小于3个按size取
//            Collections.sort(marketCategoryVo.getMatchMarketVoList());
//            List<MatchMarketLiveOddsVo.MatchMarketVo> matchMarketVos;
//
//            matchMarketVos = marketCategoryVo.getMatchMarketVoList().subList(0, marketCategoryVo.getMatchMarketVoList().size());
//
//            marketCategoryVo.setMatchMarketVoList(matchMarketVos);
//            marketCategoryVo.setNameCode(marketGroupEntry.getValue().get(0).getNameCode());
//            marketCategoryVo.setNames(languageService.getCachedNamesMapByCode(marketCategoryVo.getNameCode()));
//            results.add(marketCategoryVo);
//        }
//        return results;
//    }
    @Override
    public boolean updateMatchOdds(RealTimeVolumeBean realTimeVolumeBean) {
        log.info("::{}:: updateMatchOdds接收实货量、期望值{}",realTimeVolumeBean.getStandardTournamentId(),JsonFormatUtils.toJson(realTimeVolumeBean));
        RcsMarketOddsConfig marketOdds = new RcsMarketOddsConfig();
        marketOdds.setMatchId(realTimeVolumeBean.getMatchId());
        marketOdds.setMatchMarketId(realTimeVolumeBean.getMatchMarketId());
        marketOdds.setMarketCategoryId(realTimeVolumeBean.getPlayId());
        marketOdds.setMarketOddsId(realTimeVolumeBean.getPlayOptionsId());
        if (realTimeVolumeBean.getStandardTournamentId() != null) {
            marketOdds.setStandardTournamentId(realTimeVolumeBean.getStandardTournamentId());
        }
        marketOdds.setBetAmount(realTimeVolumeBean.getSumMoney());
        marketOdds.setProfitValue(realTimeVolumeBean.getProfitValue());
        marketOdds.setBetOrderNum(realTimeVolumeBean.getBetOrderNum());
        marketOdds.setPaidAmount(realTimeVolumeBean.getPaidAmount());
        marketOdds.setMatchType(realTimeVolumeBean.getMatchType());
        marketOdds.setSportId(realTimeVolumeBean.getSportId().longValue());
        marketOdds.setPlayPhaseType(marketCategoryService.selectPlayPhase(Long.parseLong(String.valueOf(realTimeVolumeBean.getPlayId()))));

        //marketOddsConfigService.insertOrUpdate(marketOdds) > 0 ? true : false;
        HashMap<String, String> map = new HashMap<>();
        map.put("time", "" + System.currentTimeMillis());
        String hashKey = String.format("%s_%s", marketOdds.getMarketOddsId(), marketOdds.getMatchType());
        producerSendMessageUtils.sendMsg("MYSQL_PROFIT_ODDS", "", "", JSONObject.toJSONString(marketOdds), map, hashKey);
        log.info("::{}:: 投注项级别MYSQL_PROFIT_ODDS推送:投注项:{},内容:{}",realTimeVolumeBean.getStandardTournamentId(),realTimeVolumeBean.getPlayOptionsId(), JsonFormatUtils.toJson(marketOdds));

        producerSendMessageUtils.sendMessage(MqConstants.WS_ODDS_CHANGED_TOPIC, MqConstants.WS_ODDS_CHANGED_TAG, "", marketOdds);
        producerSendMessageUtils.sendMessage(MqConstants.ORDER_AMOUNT_CHANGE_TOPIC, realTimeVolumeBean);
        log.info("::{}::updateMatchOdds推送实货量、期望值{}",realTimeVolumeBean.getStandardTournamentId(),JsonFormatUtils.toJson(marketOdds));

        return true;
    }

    @Override
    public boolean updateMatchBetChange(RcsMatchDimensionStatistics matchDimensionStatistics) {
        log.info("::{}::更新同联赛赛事实货量updateMatchBetChange{}",matchDimensionStatistics.getMatchId(),JsonFormatUtils.toJson(matchDimensionStatistics));
        try {
            if (matchDimensionStatistics != null) {
                MatchBetChange matchBetChange = new MatchBetChange();
                BeanUtils.copyProperties(matchDimensionStatistics, matchBetChange);
                MatchStatisticsInfo info = matchStatisticsInfoService.getMatchInfoByMatchId(matchBetChange.getMatchId());
                if (info != null) {
                    matchBetChange.setSet1Score(info.getSet1Score());
                    matchBetChange.setScore(info.getScore());
                    matchBetChange.setPeriod(info.getPeriod());
                    matchBetChange.setSecondsMatchStart(info.getSecondsMatchStart());
                    matchBetChange.setMatchPeriodId(info.getPeriod());
//                    MatchPeriod one = matchPeriodService.getOne(matchBetChange.getMatchId(), info.getPeriod());
//                    if (one != null) {
//                        matchBetChange.setPeriodScore(one.getScore());
//                    }
                    Long[] matchIds = {matchBetChange.getMatchId()};
                    //近一小时货量
                    List<RcsMatchDimensionStatisticsVo> rcsMatchDimensionStatisticsVos = matchDimensionStatisticsService.searchNearlyOneHourRealTimeValue(matchIds);

                    if (!CollectionUtils.isEmpty(rcsMatchDimensionStatisticsVos) &&
                            rcsMatchDimensionStatisticsVos.size() > 0 &&
                            rcsMatchDimensionStatisticsVos.get(0) != null &&
                            rcsMatchDimensionStatisticsVos.get(0).getRealTimeValue() != null) {
                        matchBetChange.setTotalValueOneHour(rcsMatchDimensionStatisticsVos.get(0).getRealTimeValue());
                    } else {
                        matchBetChange.setTotalValueOneHour(BigDecimal.ZERO);
                    }
                }

                matchBetChange.setTotalValue(matchDimensionStatistics.getTotalValue());
                matchBetChange.setSettledProfitValue(matchDimensionStatistics.getSettledProfitValue());
                matchBetChange.setSettledRealTimeValue(matchDimensionStatistics.getSettledRealTimeValue());

                //liveMarketOddsService.macthBetChanged(matchBetChange);
                producerSendMessageUtils.sendMessage(MqConstants.WS_MATCH_BET_CHANGED_TOPIC, MqConstants.WS_MATCH_BET_CHANGED_TAG, matchDimensionStatistics.getMatchId().toString(), matchBetChange);

            }
        } catch (Exception e) {
            log.error("::{}::推送同联赛赛事实货量失败:{}",matchDimensionStatistics.getMatchId(),e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 构建盘口及赔率数据
     *
     * @param marketKindEnum
     * @param sourceMarketMap
     * @return
     */
    private List<MatchMarketLiveOddsVo.MatchMarketVo> buildMarketOddsVoList(MarketKindEnum marketKindEnum, Map<Long, SportMarketVo> sourceMarketMap) {
        if (CollectionUtils.isEmpty(sourceMarketMap)) {
            return Collections.emptyList();
        }
        // 确保正确的排序
        List<MatchMarketLiveOddsVo.MatchMarketVo> resultList = Lists.newArrayListWithCapacity(sourceMarketMap.size());
        for (SportMarketVo sourceMarketVo : sourceMarketMap.values()) {
            MatchMarketLiveOddsVo.MatchMarketVo matchMarketVo = new MatchMarketLiveOddsVo.MatchMarketVo();
            matchMarketVo.setId(sourceMarketVo.getId());
            matchMarketVo.setNameCode(sourceMarketVo.getNameCode());
            matchMarketVo.setMarketCategoryId(sourceMarketVo.getMarketCategoryId());
            matchMarketVo.setStatus(sourceMarketVo.getStatus());
            boolean marketActive = true;
            if (MarketStatusEnum.CLOSE.getState() == sourceMarketVo.getStatus()) {
                marketActive = false;
            }
            matchMarketVo.setMarketActive(marketActive);
            // 国际化
            matchMarketVo.setNames(languageService.getCachedNamesMapByCode(sourceMarketVo.getNameCode()));
            Map<Long, SportMarketOddsVo> sourceMarketOddsMap = sourceMarketVo.getMarketOddsMap();
            // 确保正确的排序
            if (!CollectionUtils.isEmpty(sourceMarketOddsMap)) {
                List<MatchMarketLiveOddsVo.MatchMarketOddsFieldVo> oddsFieldVoList = Lists.newArrayListWithCapacity(sourceMarketOddsMap.size());
                matchMarketVo.setOddsFieldsList(oddsFieldVoList);
                for (SportMarketOddsVo sourceMarketOddsVo : sourceMarketOddsMap.values()) {
                    MatchMarketLiveOddsVo.MatchMarketOddsFieldVo marketOddsVo = new MatchMarketLiveOddsVo.MatchMarketOddsFieldVo();
                    Integer fieldOddsValue = sourceMarketOddsVo.getOddsValue();
                    marketOddsVo.setId(sourceMarketOddsVo.getId());
                    marketOddsVo.setFieldOddsOriginValue(fieldOddsValue);
                    // 赔率转换
                    String displayOddsVal = OddsValueConvertUtils.convertAndDefaultDisply(marketKindEnum, fieldOddsValue);
                    String nextLevelOdds = rcsOddsConvertMappingService.getNextLevelOdds(displayOddsVal);

                    marketOddsVo.setNextLevelOddsValue(nextLevelOdds);
                    marketOddsVo.setFieldOddsValue(displayOddsVal);
                    marketOddsVo.setNameCode(sourceMarketOddsVo.getNameCode());
                    marketOddsVo.setActive(sourceMarketOddsVo.getActive());
                    // 国际化
                    marketOddsVo.setNames(languageService.getCachedNamesMapByCode(sourceMarketOddsVo.getNameCode()));
                    marketOddsVo.setTargetSide(sourceMarketOddsVo.getTargetSide());
                    marketOddsVo.setNameExpressionValue(sourceMarketOddsVo.getNameExpressionValue());
                    marketOddsVo.setOddsType(sourceMarketOddsVo.getOddsType());

                    //期望值
                    marketOddsVo.setBetNum(sourceMarketOddsVo.getBetNum());
                    marketOddsVo.setBetAmount(sourceMarketOddsVo.getBetAmount());
                    marketOddsVo.setProfitValue(sourceMarketOddsVo.getProfitValue());

                    oddsFieldVoList.add(marketOddsVo);
                }
            }
            resultList.add(matchMarketVo);
        }
        return resultList;
    }


    public StandardMatchMarketDTO getCurrentMarketInfo(Long marketId, Map<String, Object> oddMap, Boolean isInsert) {
        StandardSportMarket marketBean = standardSportMarketServiceImpl.getById(marketId);
        if (marketBean == null) {
            throw new RcsServiceException("该盘口id的数据不存在");
        }
        StandardMarketDTO bean = BeanCopyUtils.copyProperties(marketBean, StandardMarketDTO.class);
        QueryWrapper<StandardSportMarketOdds> queryWrapper = new QueryWrapper<StandardSportMarketOdds>();
        queryWrapper.lambda().eq(StandardSportMarketOdds::getMarketId, marketId);
        List<StandardSportMarketOdds> list = standardSportMarketOddsServiceImpl.list(queryWrapper);

        if (list == null || list.size() <= 0) {
            throw new RcsServiceException("当前盘口id数据异常 ！" + marketId);
        }

        List<StandardMarketOddsDTO> oddsList = new ArrayList<StandardMarketOddsDTO>();
        for (StandardSportMarketOdds obj : list) {
            StandardMarketOddsDTO dto = BeanCopyUtils.copyProperties(obj, StandardMarketOddsDTO.class);
            dto.setThirdOddsFieldSourceId(String.valueOf(obj.getId()));
            if (isInsert) {
                dto.setDataSourceCode(BaseConstants.DATA_SOURCE_CODE);
                dto.setThirdOddsFieldSourceId(null);
            }
            dto.setOddsFieldsTemplateId(obj.getOddsFieldsTempletId());
            dto.setActive(1);
            if (oddMap.containsKey(obj.getId().toString())) {
                Object o = oddMap.get(obj.getId().toString());
                dto.setOddsValue(Double.valueOf(o.toString()).intValue());
                dto.setOriginalOddsValue(dto.getOddsValue());
            }
            oddsList.add(dto);
        }
        bean.setMarketOddsList(oddsList);
        bean.setThirdMarketSourceId(String.valueOf(marketId));
        if (isInsert) {
            bean.setDataSourceCode(BaseConstants.DATA_SOURCE_CODE);
            bean.setThirdMarketSourceId(null);
        }
        StandardMatchMarketDTO standardMatchMarketDTO = new StandardMatchMarketDTO();
        standardMatchMarketDTO.setStandardMatchInfoId(marketBean.getStandardMatchInfoId());
        ArrayList<StandardMarketDTO> standardMarketDTOS = new ArrayList<>();
        standardMarketDTOS.add(bean);
        standardMatchMarketDTO.setMarketList(standardMarketDTOS);
        return standardMatchMarketDTO;
    }

    /**
     * 发送盘口增删改到融合
     *
     * @param bean
     */
    public StandardMarketMessage putTradeMarketOdds(StandardMatchMarketDTO bean) {
        Response response = DataRealtimeApiUtils.handleApi(bean, new ApiCall() {
            @Override
            public <R> Response<R> callApi(Request request) {
                return tradeMarketOddsApi.putTradeMarketOdds(request);
            }
        });
        if (response == null) {
            return null;
        }
        return JSONObject.parseObject(JSONObject.toJSONString(response.getData()), StandardMarketMessage.class);
    }

    /**
     * 发送状态变更，自动手动切换
     *
     * @param type         修改类型   1：玩法   2：联赛 3：赛事 4：盘口
     * @param configId     配置表id
     * @param marketStatus 盘口状态 0:active 开, 1:suspended 封, 2:deactivated 关, 11:锁 null 表示不修改当前盘口状态
     * @param targetId     目标id
     * @param tradeType    0:自动操盘 1:手动操盘 null 表示不修改当前操盘类型
     */
    public void putTradeMarketConfig(Integer type, String configId, Integer marketStatus, Long targetId, Integer tradeType, Map<String, String> additionsMap) {
        if (additionsMap == null) {
            additionsMap = new HashMap<>();
        }
        TradeMarketConfigDTO bean = JSONObject.parseObject(JSONObject.toJSONString(additionsMap), TradeMarketConfigDTO.class);
        if (marketStatus != null && marketStatus != 0 && marketStatus != 1 && marketStatus != 2 && marketStatus != 11) {
            throw new RcsServiceException("状态参数marketStatus错误，不是指定的几个参数");
        }
        if (tradeType != null && tradeType != 0 && tradeType != 1) {
            throw new RcsServiceException("操盘类型tradeType 参数错误，不是指定的几个参数");
        }

        bean.setActive(1);
        if (type == 1) {
            configId = MatchConstants.PLAY_ID + configId;
        } else if (type == 2) {
            configId = MatchConstants.TOURNAMENT_ID + configId;
        } else if (type == 3) {
            configId = MatchConstants.MATCH_ID + configId;
        } else if (type == 4) {
            configId = MatchConstants.MATCH_MARKET_ID + configId;
        }
        bean.setConfigId(configId);
        bean.setLevel(type);
        bean.setMarketStatus(marketStatus);
        bean.setModifyTime(System.currentTimeMillis());
        bean.setOperaterId(1L);
        bean.setSourceSystem(2);
        bean.setTargetId(String.valueOf(targetId));
        bean.setTradeType(tradeType);

        Response response = DataRealtimeApiUtils.handleApi(bean, new ApiCall() {
            @Override
            public <R> Response<R> callApi(Request request) {
                return tradeMarketConfigApi.putTradeMarketConfig(request);
            }
        });
    }


}
