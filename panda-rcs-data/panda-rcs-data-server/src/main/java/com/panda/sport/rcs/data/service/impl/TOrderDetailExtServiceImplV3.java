package com.panda.sport.rcs.data.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.data.mapper.*;
import com.panda.sport.rcs.data.repository.TOrderDetailExtRepository;
import com.panda.sport.rcs.data.service.IRcsSpecEventConfigService;
import com.panda.sport.rcs.data.service.ITOrderDetailExtService;
import com.panda.sport.rcs.data.service.MatchEventInfoService;
import com.panda.sport.rcs.data.service.MatchPeriodService;
import com.panda.sport.rcs.data.utils.RDSProducerSendMessageUtils;
import com.panda.sport.rcs.data.utils.SpecEventConfigEnum;
import com.panda.sport.rcs.data.utils.TOrderDetailExtUtils;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.enums.TradeLevelEnum;
import com.panda.sport.rcs.enums.TradeStatusEnum;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.pojo.enums.MatchEventConfigEnum;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTemplateEventInfoConfig;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfigAutoChange;
import com.panda.sport.rcs.pojo.tourTemplate.ThirdDataSourceCodeVo;
import com.panda.sport.rcs.pojo.vo.MatchSpecEventSwitchVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-01-31
 */
@Slf4j
@Service
public class TOrderDetailExtServiceImplV3 extends ServiceImpl<TOrderDetailExtMapper, TOrderDetailExt> implements ITOrderDetailExtService {


    @Autowired
    RcsTournamentTemplateAcceptConfigMapper templateAcceptConfigMapper;
    @Autowired
    RcsTournamentTemplateAcceptEventMapper templateEventMapper;
    @Autowired
    TOrderDetailExtRepository tOrderDetailExtRepository;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    RedisClient redisClient;
    @Autowired
    protected RDSProducerSendMessageUtils sendMessage;
    @Autowired
    TOrderDetailExtUtils tOrderDetailExtUtils;
    @Autowired
    MarketCategorySetMapper marketCategorySetMapper;
    @Autowired
    RcsTemplateEventInfoConfigMapper rcsTemplateEventInfoConfigMapper;


    @Autowired
    private RcsTournamentTemplateAcceptConfigAutoChangeMapper rcsTournamentTemplateAcceptConfigAutoChangeMapper;

    @Autowired
    private RcsTournamentTemplateMapper rcsTournamentTemplateMapper;

    @Autowired
    private RcsTournamentTemplateAcceptConfigSettleMapper rcsTournamentTemplateAcceptConfigSettleMapper;

    @Resource
    private IRcsSpecEventConfigService rcsSpecEventConfigService;
    @Resource
    private MatchEventInfoService matchEventInfoService;
    @Resource
    private MatchPeriodService matchPeriodService;

    private HashMap matchCahe = new HashMap();

    private static String MATCH_LIVE_RISK_MANAGERCODE = "rcs:match:live:risk:manageCode:%s";
    String RCS_TOURNAMENT_TEMPLATE_ACCEPT_AUTO_CHANGE_KEY = "rcs:tournament:template:accept:auto:change:matchId:%s:categorySetId:%s";
    private static Long MATCH_EVENT_CONFIG_ALIVE_TIME = 2 * 60 * 60L;
    //使用上一次事件编码 赛事:玩法集id:事件源编码
    /**
     * risk 接拒判断 getEventConfig
     */
    /**
     * risk 接拒判断 1840需求
     */
    //1601需求redis

    private static String DATA_LIVE_MATCH_CONFIG_DATASOURCE = "rcs:match:data:config:dataSource:matchId:%s:categorySetId:%s";
    private static String DATA_LIVE_MATCH_SETTLE_DATASOURCE = "rcs:match:data:settle:dataSource:matchId:%s:categorySetId:%s";
    private static String REDIS_EVENT_DATA_SOURCE_CODE = "rcs:match:%s:event:data:source:code:%s";

    @Override
    public void batchUpdateOrderExt(MatchEventInfo event, String linkedId) {
        Long matchId = event.getStandardMatchId();
        log.info("::{}::赛事ID:{}::事件接距时间处理开始::={}", linkedId, matchId, JSONObject.toJSONString(event));
        StandardMatchInfo match = getMatchInfo(matchId);
        if (match == null) {
            log.warn("::{}::赛事ID:{} 不存在标准赛事表里", linkedId, matchId);
            return;
        }
        if (match.getSportId() != null && !"1".equals(String.valueOf(match.getSportId()))) {
            log.warn("::{}::赛事ID:{}::不是足球事件，暂时弃用：{}", linkedId, matchId, JSONObject.toJSONString(match));
            return;
        }

        //1788自动切换数据源
        this.toggleDataSource(match, event, linkedId);
        List<RcsTemplateEventInfoConfig> configs = getTemplateConfig(event);
        if (!CollectionUtils.isEmpty(configs)) {
            String riskManagerCode = liveRiskManageCode(match);
            log.warn("::{}::赛事ID:{}::,riskManagerCode:{}", linkedId, matchId, riskManagerCode);
            //bug42782 K01数据源断线，需自动封盘 判断条件
            boolean k01SealFlag = Boolean.FALSE;
            for (RcsTemplateEventInfoConfig config : configs) {
                if(!MatchEventConfigEnum.EVENT_REJECT.getCode().equals(config.getEventType())){
                    continue;
                }
                if (!Arrays.asList("BTS","MTS","GTS","CTS","OTS").contains(riskManagerCode) && event.getMatchPeriodId() > 0) {
                    Integer categorySetId = config.getCategorySetId();
                    //获取赛事实时数据源
                    Long startTime = System.currentTimeMillis();
                    String realTimeDataSource = this.getRealTimeDataSource(matchId, categorySetId);
                    log.info("::{}::赛事ID:{}:玩法集ID:{}:数据库获取到玩法集数据源配置:{},事件数据源:{},耗时:{}", linkedId, matchId, categorySetId, realTimeDataSource, event.getDataSourceCode(), System.currentTimeMillis() - startTime);
                    if(StringUtils.isBlank(realTimeDataSource) || !StringUtils.equals(event.getDataSourceCode(), realTimeDataSource)){
                        continue;
                    }
                    //bug42919 拒单需玩法集封盘 拒单事件非`其他玩法集`
                    if (categorySetId > 0) {
                        JSONObject json = new JSONObject()
                                .fluentPut("tradeLevel", TradeLevelEnum.PLAY_SET.getLevel())
                                .fluentPut("matchId", matchId)
                                .fluentPut("playSetId", categorySetId)
                                .fluentPut("status", TradeStatusEnum.SEAL.getStatus())
                                .fluentPut("linkedType", 111)
                                .fluentPut("remark", "拒单事件玩法级封盘");;

                        String keys = linkedId + "_" + matchId + "_" + match.getSportId() + "_" + categorySetId;
                        Request<JSONObject> request = new Request<>();
                        request.setData(json);
                        request.setLinkId(keys);
                        request.setDataSourceTime(System.currentTimeMillis());
                        String topic = "RCS_TRADE_UPDATE_MARKET_STATUS";
                        String tags = matchId + "_" + match.getSportId() + "_" + categorySetId;
                        log.info("::{}::赛事ID:{}::发送拒单事件玩法集封盘消息队列topic={},tags={}", linkedId, matchId, topic, tags);
                        sendMessage.sendMessage(topic, tags, keys, request);
                    }

                    //bug42782 K01数据源断线，需自动封盘 lost_connection=连线中断,active_connection_checking_status=比赛链接，核查中
                    List<String> connectionEvents = Lists.newArrayList("lost_connection", "active_connection_checking_status");
                    if (connectionEvents.contains(config.getEventCode()) && MatchEventConfigEnum.EVENT_SOURCE_KO.getCode().equalsIgnoreCase(realTimeDataSource)) {
                        k01SealFlag = Boolean.TRUE;
                    }
                }
            }
            //bug42782 K01数据源断线，需自动封盘
            if(k01SealFlag){
                JSONObject json = new JSONObject()
                        .fluentPut("tradeLevel", TradeLevelEnum.MATCH.getLevel())
                        .fluentPut("matchId", matchId)
                        .fluentPut("sportId", match.getSportId())
                        .fluentPut("status", TradeStatusEnum.SEAL.getStatus())
                        .fluentPut("linkedType", 111)
                        .fluentPut("remark", "K01数据源断线事件赛事级封盘");

                Request<JSONObject> request = new Request<>();
                request.setData(json);
                request.setLinkId(linkedId + "_eventSeal");
                request.setDataSourceTime(System.currentTimeMillis());
                String topic = "RCS_TRADE_UPDATE_MARKET_STATUS";
                String tags = matchId + "_" + match.getSportId();
                String keys = linkedId + "_" + matchId + "_" + match.getSportId();
                log.info("::{}::赛事ID:{}::发送K01拒单事件赛事封盘消息队列topic={},tags={}", linkedId, matchId, topic, tags);
                sendMessage.sendMessage(topic, tags, keys, request);
            }
        }
    }


    /**
     * 自动切换数据源
     *
     * @param standardMatchInfo 赛事信息
     * @param event             事件信息
     * @param linkedId          link
     */
    private void toggleDataSource(StandardMatchInfo standardMatchInfo, MatchEventInfo event, String linkedId) {
        long matchId = event.getStandardMatchId();
        //需求1788 足球接拒源自动切换配置功能
        List<ThirdDataSourceCodeVo> thirdData = getThirdData(standardMatchInfo);
        log.info("::{}::足球自动接距事件源配置:{}", matchId, thirdData);
        if (SportIdEnum.isFootball(event.getSportId()) && thirdData.size() > NumberUtils.INTEGER_ONE) {
            RcsMarketCategorySet rcsMarketCategorySet = marketCategorySetMapper.queryPlaySetCode();
            if (Objects.nonNull(rcsMarketCategorySet)) {
                //判断进球类玩法集 危险事件
                Long categorySetId = rcsMarketCategorySet.getId();
                RcsTemplateEventInfoConfig rcsTemplateEventInfoConfig = templateAcceptConfigMapper.selectOrderAcceptConfigNew(event.getStandardMatchId(), categorySetId, event.getEventCode());
                //查询如果在表示有配置危险事件
                if (Objects.nonNull(rcsTemplateEventInfoConfig)) {
                    //接拒源自动切换开关
                    String autoChangeKey = String.format(RCS_TOURNAMENT_TEMPLATE_ACCEPT_AUTO_CHANGE_KEY, matchId, categorySetId);
                    String value = redisClient.get(autoChangeKey);
                    log.info("::接拒源自动切换开关::linkedId={},matchId={},autoChangeKey={},value={}", linkedId, matchId, autoChangeKey, value);
                    if (StringUtils.isNotBlank(value) && "1".equals(value)) {
                        TempleteDateSource source = new TempleteDateSource();
                        source.setMatchId(event.getStandardMatchId());
                        source.setCategorySetId(categorySetId);
                        source.setSportId(event.getSportId());
                        source.setDataSourceCode(event.getDataSourceCode());
                        source.setEventCode(event.getEventCode());
                        //发送切换数据商MQ 到trade
                        this.dataSourceCodeChange(source);
                    }
                }
            }

        }

    }

    private List<ThirdDataSourceCodeVo> getThirdData(StandardMatchInfo standardMatchInfo) {
        List<ThirdDataSourceCodeVo> thirdDataSources = new ArrayList<>();
        if (standardMatchInfo != null && StringUtils.isNotBlank(standardMatchInfo.getThirdMatchListStr())) {
            thirdDataSources = JSON.parseArray(standardMatchInfo.getThirdMatchListStr(), ThirdDataSourceCodeVo.class);
            thirdDataSources = thirdDataSources.stream()
                    .filter(filter -> (filter.getCommerce().equals(String.valueOf(NumberUtils.INTEGER_ONE))
                            && filter.getEventSupport().equals(String.valueOf(NumberUtils.INTEGER_ONE)))
                            || filter.getDataSourceCode().equals("PD"))
                    .collect(Collectors.toList());
        }

        return thirdDataSources;
    }


    private void sendCacheMsg(String key, Object value) {
        JSONObject json = new JSONObject();
        json.put("key", key);
        json.put("value", value);
        log.info("广播同步刷新reject缓存：{}", json.toJSONString());
        sendMessage.sendMessage("rcs_order_reject_cache_update", "", key, json);
    }


    /**
     * @return java.util.List<com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfig>
     * @Description //查询接拒单配置
     * @Param [event]
     * @Author sean
     * @Date 2020/11/7
     **/
    private List<RcsTemplateEventInfoConfig> getTemplateConfig(MatchEventInfo event) {
        List<RcsTemplateEventInfoConfig> eventConfigs = Lists.newArrayList();
        //bug42919 匹配常规接拒类型
        List<RcsTemplateEventInfoConfig> configs = rcsTemplateEventInfoConfigMapper.selectList(
                Wrappers.<RcsTemplateEventInfoConfig>lambdaQuery().eq(RcsTemplateEventInfoConfig::getRejectType, 1)
        );
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(configs)) {
            Map<Integer, List<RcsTemplateEventInfoConfig>> mapList = configs.stream().collect(Collectors.groupingBy(RcsTemplateEventInfoConfig::getCategorySetId));
            for (Integer categorySetId : mapList.keySet()) {
                for (RcsTemplateEventInfoConfig config : mapList.get(categorySetId)) {
                    if (event.getEventCode().equalsIgnoreCase(config.getEventCode())) {
                        eventConfigs.add(config);
                    }
                }
            }
        }
        log.info("::{}::事件信息：{},接拒单赛事参数配置：{}", event.getStandardMatchId(), JSON.toJSONString(event), JSON.toJSONString(eventConfigs));
        return eventConfigs;
    }

    /**
     * @return com.panda.sport.rcs.pojo.StandardMatchInfo
     * @Description 获取比赛信息
     * @Param [matchId]
     * @Author toney
     * @Date 11:07 2020/4/14
     **/
    public StandardMatchInfo getMatchInfo(Long matchId) {
        if (matchCahe.size() > 1000) {
            matchCahe = new HashMap();
        }
        if (matchCahe.get(matchId) != null) {
            return (StandardMatchInfo) matchCahe.get(matchId);
        } else {
            StandardMatchInfo info = standardMatchInfoMapper.selectMatchById(matchId);
            if (info != null) {
                matchCahe.put(matchId, info);
            }
            return info;
        }
    }

    private String liveRiskManageCode(StandardMatchInfo standardMatchInfo) {
        String key = String.format(MATCH_LIVE_RISK_MANAGERCODE, standardMatchInfo.getId());
        String value = redisClient.get(key);
        if (StringUtils.isBlank(value) && StringUtils.isNotBlank(standardMatchInfo.getLiveRiskManagerCode())) {
            redisClient.setExpiry(key, standardMatchInfo.getLiveRiskManagerCode(), MATCH_EVENT_CONFIG_ALIVE_TIME);
            return standardMatchInfo.getLiveRiskManagerCode();
        }
        return value;
    }


    private void dataSourceCodeChange(TempleteDateSource source) {
        String dataSourceCode = source.getDataSourceCode();
        Long sportId = source.getSportId();
        Long matchId = source.getMatchId();
        Long categorySetId = source.getCategorySetId();
        // 查询模板id
        QueryWrapper<RcsTournamentTemplate> oneTemplateQuery = new QueryWrapper<>();
        oneTemplateQuery.lambda().eq(RcsTournamentTemplate::getSportId, sportId)
                .eq(RcsTournamentTemplate::getType, 3).eq(RcsTournamentTemplate::getTypeVal, matchId)
                .eq(RcsTournamentTemplate::getMatchType, 0); // 滚球才有
        RcsTournamentTemplate oneTemplate = rcsTournamentTemplateMapper.selectOne(oneTemplateQuery);

        // 开关是否开启，不开启不更新
        QueryWrapper<RcsTournamentTemplateAcceptConfigAutoChange> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("template_id", oneTemplate.getId());
        queryWrapper.eq("category_set_id", categorySetId);
        RcsTournamentTemplateAcceptConfigAutoChange rcsTournamentTemplateAcceptConfigAutoChange = rcsTournamentTemplateAcceptConfigAutoChangeMapper.selectOne(queryWrapper);
        // 开关关闭，不更新
        if (rcsTournamentTemplateAcceptConfigAutoChange.getIsOpen() == null || rcsTournamentTemplateAcceptConfigAutoChange.getIsOpen() == 0) {
            log.info("足球进球类接拒源自动切换-开关关闭，templateId:{},categorySetId:{},不进行更新，执行结束", oneTemplate.getId(), categorySetId);
            return;
        }
        // 所有玩法集
        QueryWrapper<RcsMarketCategorySet> rcsMarketCategorySetQuery = new QueryWrapper<>();
        rcsMarketCategorySetQuery.lambda().eq(RcsMarketCategorySet::getType, 1);
        rcsMarketCategorySetQuery.lambda().eq(RcsMarketCategorySet::getSportId, 1);
        List<RcsMarketCategorySet> selectList = marketCategorySetMapper.selectList(rcsMarketCategorySetQuery);

        String configKey = String.format(DATA_LIVE_MATCH_CONFIG_DATASOURCE, matchId, categorySetId);
        // 当前的数据源缓存
        String configValue = redisClient.get(configKey);
        log.info("足球进球类接拒源自动切换-config:dataSource缓存值:{}", configValue);
        // 如果缓存为空，查询数据库
        if (StringUtils.isBlank(configValue)) {
            configValue = templateAcceptConfigMapper.queryConfigCode(sportId, matchId, categorySetId);
        }
        String settleKey = String.format(DATA_LIVE_MATCH_SETTLE_DATASOURCE, matchId, categorySetId);
        String settleValue = redisClient.get(settleKey);
        // 如果缓存为空，查询数据库
        if (StringUtils.isBlank(settleValue)) {
            settleValue = rcsTournamentTemplateAcceptConfigSettleMapper.querySettleCode(sportId, matchId, categorySetId);
        }
        // 如果传入的数据源和当前的不一致，更新
        if (StringUtils.isNotBlank(configValue) && !dataSourceCode.equals(configValue) && !dataSourceCode.equals(settleValue)) {
            String key = String.format("rcs:match:%s:event:data:source:code:%s", matchId, configValue);
            this.sendCacheMsg(key, "1");
            //发送MQ获取最新的数据源配置
            String redisKey = String.format(REDIS_EVENT_DATA_SOURCE_CODE, matchId, categorySetId);
            this.sendCacheMsg(redisKey, dataSourceCode);
            //修改配置
            templateAcceptConfigMapper.updateMatchDataSourceByTemplateId(dataSourceCode,
                    oneTemplate.getId());
            //修改接距配置
            rcsTournamentTemplateAcceptConfigSettleMapper.updateMatchDataSourceByTemplateId(dataSourceCode,
                    oneTemplate.getId());
            // 更新玩法集对应的数据源
            for (RcsMarketCategorySet rcsMarketCategorySet : selectList) {
                String configKeySigle = String.format(DATA_LIVE_MATCH_CONFIG_DATASOURCE, matchId, rcsMarketCategorySet.getId());
                redisClient.setExpiry(configKeySigle, dataSourceCode, MATCH_EVENT_CONFIG_ALIVE_TIME);
                String settleKeySigle = String.format(DATA_LIVE_MATCH_SETTLE_DATASOURCE, matchId, rcsMarketCategorySet.getId());
                redisClient.setExpiry(settleKeySigle, dataSourceCode, MATCH_EVENT_CONFIG_ALIVE_TIME);
            }
        }


        // 关闭开关，下次不进行更新
        rcsTournamentTemplateAcceptConfigAutoChange.setIsOpen(0);
        rcsTournamentTemplateAcceptConfigAutoChangeMapper.updateById(rcsTournamentTemplateAcceptConfigAutoChange);
        // 更新缓存
        String rcsTournamentTemplateAcceptAutoChangeKey = RedisKey.getRcsTournamentTemplateAcceptAutoChangeKey(matchId, categorySetId);
        redisClient.setExpiry(rcsTournamentTemplateAcceptAutoChangeKey, 0, 3600 * 24L);
        log.info("足球进球类接拒源自动切换-更新结束，templateId:{},categorySetId:{},设置开关关闭", matchId, categorySetId);

    }

    /**
     * 特殊事件处理
     */
    @Override
    public void specEventHandler(MatchEventInfo event, String linkedId) {
        if (!SportIdEnum.isFootball(event.getSportId())) {
            return;
        }
        log.info("{}::特殊事件处理::{}::={}", linkedId, event.getStandardMatchId(), JSONObject.toJSONString(event));
        if (!SpecEventConfigEnum.hasSpecEvent(event.getEventCode())) {
            specEventNotHandler(event, linkedId);
            return;
        }
        //判断系统级别的特殊事件是否开启 只有进球类玩法才有特殊事件
        List<RcsTemplateEventInfoConfig> list = rcsTemplateEventInfoConfigMapper.selectList(
                new LambdaQueryWrapper<RcsTemplateEventInfoConfig>()
                        .eq(RcsTemplateEventInfoConfig::getCategorySetId, 131)
                        .eq(RcsTemplateEventInfoConfig::getRejectType, 1)
                        .eq(RcsTemplateEventInfoConfig::getEventType,"special"));
        if(CollUtil.isEmpty(list)){
            log.info("{}::特殊事件处理::系统级别特殊事件未开启::{}::={}", linkedId, event.getStandardMatchId(), JSONObject.toJSONString(list));
            return;
        }
        Optional<RcsTemplateEventInfoConfig> eventInfoConfig = list.stream().filter(o->StrUtil.equals(event.getEventCode(),o.getEventCode())).findFirst();
        if(!eventInfoConfig.isPresent()){
            log.info("{}::特殊事件处理::{}::系统级别特殊事件未开启::{}::={}", linkedId, event.getStandardMatchId(),
                    JSONObject.toJSONString(event), JSONObject.toJSONString(list));
            return;
        }
        //无主队客队区分的事件不处理
        if (StrUtil.isEmpty(event.getHomeAway())) {
            log.info("{}::特殊事件处理::无主客区分::{}::={}", linkedId, event.getStandardMatchId(), JSONObject.toJSONString(event));
            return;
        }

        Long matchId = event.getStandardMatchId();
        //赛事级别特殊事件开关
        String specEventStatusKey = String.format(RedisKey.SPECIAL_EVENT_STATUS_KEY, "3", matchId);
        //0:关 1:开
        String switchStr = redisClient.get(specEventStatusKey);
        if (StringUtils.isEmpty(switchStr)) {
            switchStr = "0";
        }
        if (!"1".equals(switchStr)) {
            log.info("{}::特殊事件处理::{}::switch::{}", linkedId, matchId, switchStr);
            return;
        }
        List<RcsSpecEventConfig> matchSpecEventConfigs = rcsSpecEventConfigService.getByMatchId(matchId);
        if (matchSpecEventConfigs == null || matchSpecEventConfigs.isEmpty()) {
            log.info("{}::特殊事件处理::{}::event configs is null}", linkedId, matchId);
            return;
        }
        //查询事件配置
        Optional<RcsSpecEventConfig> specEventConfig = matchSpecEventConfigs.stream().filter(o -> o.getEventCode().equals(event.getEventCode())).findFirst();
        if (!specEventConfig.isPresent()) {
            log.info("{}::特殊事件处理::{}::event config is null::code={}", linkedId, matchId, event.getEventCode());
            return;
        }
        if (specEventConfig.get().getEventSwitch() == null || 1 != specEventConfig.get().getEventSwitch()) {
            log.info("{}::特殊事件处理::{}::switch event::{}", linkedId, matchId, specEventConfig.get().getEventSwitch());
            return;
        }

        //将赛事提前结算关闭
        JSONObject matchPreStatusParam = new JSONObject();
        matchPreStatusParam.put("standardMatchId",event.getStandardMatchId());
        matchPreStatusParam.put("linkedId",linkedId);
        sendMessage.sendMessage("RCS_STANDARD_MATCH_PRE_CLOSE", null, linkedId, matchPreStatusParam);

        //这里使用homeActive还是awayActive呢？
        Optional<RcsSpecEventConfig> lastExists = matchSpecEventConfigs.stream().filter(o -> o.getHomeActive() == 1 || o.getAwayActive() == 1).findFirst();
       // String linkId = UuidUtils.generateUuid();
        //MatchEventInfo lastEvent = matchEventInfoService.getLast(matchId);
        if (lastExists.isPresent()) {
            log.info("{}::特殊事件处理开始::{}::last id spec event::{}", linkedId, matchId, JSONObject.toJSONString(lastExists.get()));
            //ws通知操盘手是否切换
            MatchSpecEventSwitchVo vo
                    = new MatchSpecEventSwitchVo(matchId,
                    MatchSpecEventSwitchVo.TYPE_CHANGE_EVENT,
                    event.getEventCode(),
                    specEventConfig.get().getEventName(),
                    lastExists.get().getEventCode(),
                    lastExists.get().getEventName(),
                    null,
                    event.getHomeAway(),
                    lastExists.get().getHomeActive() == 1 ? RcsConstant.HOME_POSITION : RcsConstant.AWAY_POSITION);
            vo.setGlobalId(linkedId);
            sendMessage.sendMessage("WS_MATCH_SPEC_EVENT_SWITCH", null, linkedId, vo);
            return;
        }

        //上一次不是特殊事件逻辑
        changeSpecEvent(matchSpecEventConfigs, specEventConfig.get(), event.getEventCode(), linkedId, RcsConstant.HOME_POSITION.equalsIgnoreCase(event.getHomeAway()));
    }

    /**
     * 非特殊事件处理逻辑
     *
     * @param event
     * @param linkedId
     */
    private void specEventNotHandler(MatchEventInfo event, String linkedId) {
        //上一次是否是特殊事件
        List<RcsSpecEventConfig> matchSpecEventConfigs = rcsSpecEventConfigService.getByMatchId(event.getStandardMatchId());
        if (matchSpecEventConfigs == null || matchSpecEventConfigs.isEmpty()) {
            log.info("{}::特殊事件处理::{}::event configs is null。。}", linkedId, event.getStandardMatchId());
            return;
        }
        Optional<RcsSpecEventConfig> lastExists = matchSpecEventConfigs.stream().filter(o -> o.getHomeActive() == 1 || o.getAwayActive() == 1).findFirst();
        if (!lastExists.isPresent()) {
            log.info("{}::特殊事件处理::{}::上一次不是特殊事件->{}", linkedId, event.getStandardMatchId(), JSONUtil.toJsonStr(matchSpecEventConfigs));
            //退出特殊事件
           // exitSpecEvent(event.getStandardMatchId(), linkedId);
            sendMQRongHe(matchSpecEventConfigs, linkedId, event.getStandardMatchId());
            return;
        }
        //如果上一次是特殊事件，并且比分有变化：赛事锁盘,并且通知是否退出特殊事件
        MatchPeriod lastMatchPeriod = matchPeriodService.getLast(event.getMatchPeriodId());
        //原则上lastMatchPeriod 不可能为空
        String lastScore = lastMatchPeriod == null ? "0:0" : lastMatchPeriod.getScore();
        String currScore = event.getT1() + ":" + event.getT2();

        if (!currScore.equals(lastScore)) {
            log.info("{}::特殊事件处理::{}::比分发生变化->event:{}->lastMatchPeriod:{}", linkedId,
                    event.getStandardMatchId(),
                    JSONUtil.toJsonStr(event),
                    JSONUtil.toJsonStr(lastMatchPeriod));

            //比分发生变化自动退出特殊事件
            exitSpecEvent(event.getStandardMatchId(), linkedId);
            return;
        }

        //bug41991（点球未进：penalty_missed/点球取消：canceled_penalty时，不做收盘动作并且自动退出特殊事件）
        if("penalty_missed".equals(event.getEventCode()) || "canceled_penalty".equals(event.getEventCode())){
            log.info("{}::特殊事件处理::{}::点球未进|点球取消->event:{}", linkedId,
                    event.getStandardMatchId(),
                    JSONUtil.toJsonStr(event));

            //比分发生变化自动退出特殊事件
            exitSpecEvent(event.getStandardMatchId(), linkedId);
            return;
        }

        //赛事级别收盘
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tradeLevel", TradeLevelEnum.MATCH.getLevel());
        jsonObject.put("sportId", event.getSportId());
        jsonObject.put("matchId", event.getStandardMatchId());
        jsonObject.put("status", 13);//5-12 产品确认改为收盘状态
        jsonObject.put("linkedType", "88");
        jsonObject.put("remark", "AO特殊事件比分未变化收盘");
        Request<JSONObject> request = new Request<>();
        request.setData(jsonObject);
        request.setLinkId(linkedId);
        request.setDataSourceTime(System.currentTimeMillis());
        log.info("{}::特殊事件处理::{}::比分未变化锁盘->event:{}->lastMatchPeriod:{}", linkedId,
                event.getStandardMatchId(),
                JSONUtil.toJsonStr(event),
                JSONUtil.toJsonStr(lastMatchPeriod));
        sendMessage.sendMessage("RCS_TRADE_UPDATE_MARKET_STATUS", linkedId + "_" + event.getStandardMatchId(), request.getLinkId(), request);

        //关闭提前结算开关


        //ws通知退出特殊事件
        MatchSpecEventSwitchVo vo
                = new MatchSpecEventSwitchVo(event.getStandardMatchId(),
                MatchSpecEventSwitchVo.TYPE_EXIT_SPEC,
                event.getEventCode(),
                null,
                lastExists.get().getEventCode(),
                lastExists.get().getEventName(),
                null,
                null,
                lastExists.get().getHomeActive() == 1 ? RcsConstant.HOME_POSITION : RcsConstant.AWAY_POSITION);
        vo.setGlobalId(linkedId);
        log.info("{}::特殊事件处理::{}::比分变化发送退出MQ->event:{}->lastMatchPeriod:{}", linkedId,
                event.getStandardMatchId(),
                JSONUtil.toJsonStr(event),
                JSONUtil.toJsonStr(lastMatchPeriod));
        sendMessage.sendMessage("WS_MATCH_SPEC_EVENT_SWITCH", null, linkedId, vo);

    }

    /**
     * 切换特殊事件源
     *
     * @param currentEventCode
     * @param matchId
     * @param linkId
     * @param isHome
     */
    @Override
    public void changeSpecEvent(String currentEventCode, Long matchId, String linkId, boolean isHome) {
        List<RcsSpecEventConfig> matchSpecEventConfigs = rcsSpecEventConfigService.getByMatchId(matchId);
        if (matchSpecEventConfigs == null || matchSpecEventConfigs.isEmpty()) {
            log.info("{}::特殊事件处理::{}::event config is null::code={}", linkId, matchId, currentEventCode);
            return;
        }

        Optional<RcsSpecEventConfig> specEventConfig = matchSpecEventConfigs.stream()
                .filter(o -> o.getEventCode().equals(currentEventCode)).findFirst();
        if (!specEventConfig.isPresent()) {
            log.info("{}::特殊事件处理::{}::event config is null::code={}", linkId, matchId, currentEventCode);
            return;
        }
        changeSpecEvent(matchSpecEventConfigs, specEventConfig.get(), currentEventCode, linkId, isHome);
    }

    /**
     * @param matchSpecEventConfigs
     * @param curSpecEventConfig    当前赛事特殊事件配置
     * @param currentEventCode
     * @param isHome                是否主队事件
     */
    private void changeSpecEvent(List<RcsSpecEventConfig> matchSpecEventConfigs,
                                 RcsSpecEventConfig curSpecEventConfig, String currentEventCode,
                                 String linkId, boolean isHome) {
        //先将所有的设置为0，下一步在设置当前事件的激活状态
        matchSpecEventConfigs.forEach(o -> {
            o.setHomeActive(0);
            o.setAwayActive(0);
        });
        matchSpecEventConfigs.forEach(o -> {
            if (StrUtil.equals(o.getEventCode(), currentEventCode)) {
                if (isHome) {
                    o.setHomeActive(1);
                    int newCount = o.getHomeActiveCount() == null ? 1 : o.getHomeActiveCount() + 1;
                    o.setHomeActiveCount(newCount);
                } else {
                    int newCount = o.getAwayActiveCount() == null ? 1 : o.getAwayActiveCount() + 1;
                    o.setAwayActive(1);
                    o.setAwayActiveCount(newCount);
                }
                o.setEffectiveTime(System.currentTimeMillis());
            }
        });

        sendSpecActiveStatus(matchSpecEventConfigs,curSpecEventConfig.getTypeVal(),linkId);

        rcsSpecEventConfigService.updateBatchById(matchSpecEventConfigs);
        //给融合发送MQ
        sendMQRongHe(matchSpecEventConfigs, linkId, curSpecEventConfig.getTypeVal());
        //如果单边抽水控制开关是关，那么要通知操盘手是否修改概率
        if (curSpecEventConfig.getOneSideSwitch() != null && curSpecEventConfig.getOneSideSwitch() == 0) {
            //发送ws通知
            MatchSpecEventSwitchVo vo
                    = new MatchSpecEventSwitchVo(curSpecEventConfig.getTypeVal(),
                    MatchSpecEventSwitchVo.TYPE_CHANGE_ODDS,
                    curSpecEventConfig.getEventCode(),
                    curSpecEventConfig.getEventName(),
                    null,
                    null,
                    isHome ? curSpecEventConfig.getHomeGoalProb() : curSpecEventConfig.getAwayGoalProb(),
                    isHome ? RcsConstant.HOME_POSITION : RcsConstant.AWAY_POSITION,
                    null);
            vo.setGlobalId(linkId);
            sendMessage.sendMessage("WS_MATCH_SPEC_EVENT_SWITCH", null, linkId, vo);
        }
    }

    /**
     * 给融合发送MQ，AO重新计算赔率
     */
    private void sendMQRongHe(List<RcsSpecEventConfig> rcsSpecEventConfigs, String linkId, Long matchId) {
        log.info("{}::特殊事件处理::{}->{}", linkId, matchId, JSON.toJSONString(rcsSpecEventConfigs));
        if (CollUtil.isEmpty(rcsSpecEventConfigs)) {
            return;
        }
        sendMessage.sendMessage("RCS_DATA_AO_SPECIAL_EVENT_CONFIG", null, linkId, rcsSpecEventConfigs);
    }

    /**
     * 退出特殊事件
     *
     * @param matchId 赛事ID
     * @param linkId
     */
    @Override
    public void exitSpecEvent(Long matchId, String linkId) {
        List<RcsSpecEventConfig> matchSpecEventConfigs = rcsSpecEventConfigService.getByMatchId(matchId);
        if (matchSpecEventConfigs == null || matchSpecEventConfigs.isEmpty()) {
            log.info("{}::特殊事件处理::{}::退出特殊事件::event config is null", linkId, matchId);
            return;
        }
        matchSpecEventConfigs.forEach(o -> {
            o.setHomeActive(0);
            o.setAwayActive(0);
        });
        rcsSpecEventConfigService.updateBatchById(matchSpecEventConfigs);
        //给融合发送MQ
        sendMQRongHe(matchSpecEventConfigs, linkId, matchId);
        sendSpecActiveStatus(matchSpecEventConfigs,matchId,linkId);
    }

    /**
     * 特殊事件激活状态变化通知接据
     * @param matchSpecEventConfigs
     * @param matchId
     */
    private void sendSpecActiveStatus(List<RcsSpecEventConfig> matchSpecEventConfigs,Long matchId, String linkId){
        //{"matchId":2477693,"active":"penalty_awarded"}
        Optional<RcsSpecEventConfig> specEventExists = matchSpecEventConfigs.stream().filter(o -> o.getHomeActive() == 1 || o.getAwayActive() == 1).findFirst();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("matchId",matchId);
        jsonObject.put("linkId",linkId);
        if(specEventExists.isPresent()){
            jsonObject.put("active",specEventExists.get().getEventCode());
        } else {
            jsonObject.put("active","");
        }
        log.info("{}::特殊事件处理::{}::特殊事件激活状态变化通知接据::{}", linkId, matchId,JSONUtil.toJsonStr(jsonObject));
        sendMessage.sendMessage("RCS_SPECIAL_EVENT_STATUS_SYNC", String.valueOf(matchId), linkId, jsonObject);
    }


    /**
     * 获取赛事玩法集实时数据源
     * @param matchId
     * @param categorySetId
     * @return
     */
    public String getRealTimeDataSource(Long matchId, Integer categorySetId) {
        return templateAcceptConfigMapper.queryConfigCode(1L, matchId, Long.valueOf(categorySetId));
    }
}
