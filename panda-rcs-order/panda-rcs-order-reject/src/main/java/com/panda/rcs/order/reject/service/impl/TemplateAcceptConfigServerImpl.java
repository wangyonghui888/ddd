package com.panda.rcs.order.reject.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.rcs.order.reject.constants.NumberConstant;
import com.panda.rcs.order.reject.constants.RedisKey;
import com.panda.rcs.order.reject.entity.MtsTemplateConfigVo;
import com.panda.rcs.order.reject.entity.RcsTemplateEventInfoConfig;
import com.panda.rcs.order.reject.entity.RcsTournamentTemplateAcceptConfigDto;
import com.panda.rcs.order.reject.entity.RcsTournamentTemplateAcceptConfigRps;
import com.panda.rcs.order.reject.enums.CategorySetCodeEnum;
import com.panda.rcs.order.reject.mapper.MatchInfoMapper;
import com.panda.rcs.order.reject.mapper.RcsGoalWarnSetMapper;
import com.panda.rcs.order.reject.mapper.RcsTemplateEventInfoConfigMapper;
import com.panda.rcs.order.reject.service.CommonSendMsgServer;
import com.panda.rcs.order.reject.service.RejectTemplateAcceptConfigServer;
import com.panda.rcs.order.reject.utils.RedisUtils;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.TemplateAcceptConfigServer;
import com.panda.sport.data.rcs.api.tournament.TournamentTemplateByMatchService;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.PreOrderDetailRequest;
import com.panda.sport.data.rcs.dto.order.MatchEventInfoRes;
import com.panda.sport.data.rcs.dto.tournament.MatchTemplateDataReqVo;
import com.panda.sport.data.rcs.dto.tournament.MatchTemplateDataResVo;
import com.panda.sport.data.rcs.vo.MatchEventInfo;
import com.panda.sport.data.rcs.vo.RcsTournamentTemplateAcceptConfig;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.mapper.MarketCategorySetMapper;
import com.panda.sport.rcs.mapper.RcsMarketCategorySetRelationMapper;
import com.panda.sport.rcs.mapper.RcsTournamentTemplateAcceptConfigMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsMarketCategorySet;
import com.panda.sport.rcs.pojo.enums.MatchEventConfigEnum;
import com.panda.sport.rcs.pojo.reject.RcsGoalWarnSet;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author : koala
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.sport.rcs.mgr.service
 * @Description : 查询接距配置公共方法
 * @Date: 2022-07-15 11:14
 * --------  ---------  --------------------------
 */
@Service
@org.springframework.stereotype.Service
@Slf4j
@RequiredArgsConstructor
public class TemplateAcceptConfigServerImpl implements TemplateAcceptConfigServer, RejectTemplateAcceptConfigServer {
    private final RcsMarketCategorySetRelationMapper rcsMarketCategorySetRelationMapper;
    private final RcsTournamentTemplateAcceptConfigMapper rcsTournamentTemplateAcceptConfigMapper;
    private final RedisUtils redisUtils;
    private final ProducerSendMessageUtils producerSendMessageUtils;
    private final CommonSendMsgServer commonSendMsgServerImpl;
    private final MatchInfoMapper matchInfoMapper;
    private final RcsTemplateEventInfoConfigMapper rcsTemplateEventInfoConfigMapper;

    private final MarketCategorySetMapper marketCategorySetMapper;
    @Reference(check = false, lazy = true, retries = 3, timeout = 3000)
    private TournamentTemplateByMatchService tournamentTemplateByMatchService;

    private static final List<String> VAR_EVENT_LIST = Arrays.asList("possible_var", "possible_video_assistant_referee", "var_reason", "var_reviewing", "video_assistant_referee");

    @Autowired
    RedisClient redisClient;

    private final RcsGoalWarnSetMapper rcsGoalWarnSetMapper;


    /**
     * 获取玩法集ID
     *
     * @param request 订单请求参数
     * @return 玩法集ID
     */
    @Override
    public Response<String> queryCategorySetByPlayId(Request<OrderItem> request) {
        OrderItem orderItem = request.getData();
        return Response.success(this.getPlayCollect(orderItem));
    }

    /**
     * 获取接距配置
     *
     * @param request 订单请求参数
     * @return 接距配置
     */
    @Override
    public Response<List<RcsTournamentTemplateAcceptConfig>> queryWaitTimeConfig(Request<OrderItem> request) {
        OrderItem orderItem = request.getData();
        List<RcsTournamentTemplateAcceptConfig> list = rcsTournamentTemplateAcceptConfigMapper.queryWaitTimeConfig(orderItem, Integer.parseInt(this.getPlayCollect(orderItem)));
        return Response.success(list);
    }

    /**
     * 查询接距等待时间
     *
     * @param orderNo   注单号
     * @param matchId   赛事ID
     * @param playSetId 玩法集ID
     * @return 接距等待时间
     */
    public RcsTournamentTemplateAcceptConfigRps getWaitTime(String orderNo, Long matchId, String playSetId) {
        RcsTournamentTemplateAcceptConfigRps rcsTournamentTemplateAcceptConfigRps;
        String waitTimeKey = String.format(RedisKey.MATCH_EVENT_WAIT_REDIS_KEY, matchId, playSetId);
        String waitTimeStr = RcsLocalCacheUtils.getValueInfo(waitTimeKey);
        if (StringUtils.isBlank(waitTimeStr)) {
            rcsTournamentTemplateAcceptConfigRps = matchInfoMapper.querWaitTimeInfo(matchId, Integer.parseInt(playSetId));
            if (Objects.isNull(rcsTournamentTemplateAcceptConfigRps)) {
                log.warn("::{}::数据库没有配置接距等待时间,默认使用安全事件等待时间", orderNo);
                return null;
            }
            log.info("::{}::数据库获取到玩法集接拒等待时间", orderNo);
            commonSendMsgServerImpl.sendMsg(waitTimeKey, rcsTournamentTemplateAcceptConfigRps);
        } else {
            rcsTournamentTemplateAcceptConfigRps = JSON.parseObject(waitTimeStr, RcsTournamentTemplateAcceptConfigRps.class);
            log.info("::{}::缓存获取到玩法集接拒等待时间", orderNo);
        }
        return rcsTournamentTemplateAcceptConfigRps;
    }

    /**
     * 查询接距等待时间
     *
     * @param orderNo   注单号
     * @param matchId   赛事ID
     * @param playSetId 玩法集ID
     * @return 接距等待时间
     */
    public RcsTournamentTemplateAcceptConfigRps getPreSettleWaitTime(String orderNo, Long matchId, String playSetId) {
        RcsTournamentTemplateAcceptConfigRps rcsTournamentTemplateAcceptConfigRps;
        String waitTimeKey = String.format(RedisKey.MATCH_PRE_SETTLE_EVENT_WAIT_TIME, matchId, playSetId);
        String waitTimeStr = RcsLocalCacheUtils.getValueInfo(waitTimeKey);
        if (StringUtils.isBlank(waitTimeStr)) {
            rcsTournamentTemplateAcceptConfigRps = matchInfoMapper.querSettleWaitTimeInfo(matchId, Integer.parseInt(playSetId));
            if (Objects.isNull(rcsTournamentTemplateAcceptConfigRps)) {
                log.warn("::{}::数据库没有配置接距等待时间,默认使用安全事件等待时间", orderNo);
                return null;
            }
            log.info("::{}::数据库获取到玩法集接拒等待时间", orderNo);
            commonSendMsgServerImpl.sendMsg(waitTimeKey, rcsTournamentTemplateAcceptConfigRps);
        } else {
            rcsTournamentTemplateAcceptConfigRps = JSON.parseObject(waitTimeStr, RcsTournamentTemplateAcceptConfigRps.class);
            log.info("::{}::缓存获取到玩法集接拒等待时间", orderNo);
        }
        return rcsTournamentTemplateAcceptConfigRps;
    }

    /**
     * 查询接距玩法集配置
     *
     * @param orderNo   注单号
     * @param playSetId 玩法集ID
     * @return 接距玩法集配置
     */
    public List<RcsTemplateEventInfoConfig> getRcsTemplateEventInfoConfigs(String orderNo,  String playSetId) {
        List<RcsTemplateEventInfoConfig> rcsTemplateEventInfoConfigs;
        String eventKey = String.format(RedisKey.MATCH_EVENT_REDIS_KEY, playSetId);
        String redisStr = RcsLocalCacheUtils.getValueInfo(eventKey);
        if (StringUtils.isBlank(redisStr)) {
            log.info("::{}::事件配置缓存为空，key：{},玩法集:{},从数据库读取", orderNo, eventKey, Integer.parseInt(playSetId));
            rcsTemplateEventInfoConfigs = rcsTemplateEventInfoConfigMapper.selectList(new LambdaQueryWrapper<RcsTemplateEventInfoConfig>().eq(RcsTemplateEventInfoConfig::getCategorySetId, Integer.parseInt(playSetId)).eq(RcsTemplateEventInfoConfig::getRejectType, NumberConstant.NUM_ONE).ne(RcsTemplateEventInfoConfig::getEventType, "special"));
            if (CollectionUtils.isEmpty(rcsTemplateEventInfoConfigs)) {
                log.info("::{}::数据库没有获取到玩法集:{}的事件配置,默认常规事件配置", orderNo, playSetId);
                return null;
            }
            log.info("::{}::数据库获取到玩法集的事件配置,key:{}", orderNo, eventKey);
            commonSendMsgServerImpl.sendMsg(eventKey, rcsTemplateEventInfoConfigs);
        } else {
            rcsTemplateEventInfoConfigs = JSON.parseArray(redisStr, RcsTemplateEventInfoConfig.class);
            log.info("::{}::缓存查到赛事玩法集事件配置,key:{}", orderNo, eventKey);
        }
        return rcsTemplateEventInfoConfigs;
    }


    /**
     * 查询提前结算接距玩法集配置
     *
     * @param orderNo   注单号
     * @param playSetId 玩法集ID
     * @return 提前结算接距玩法集配置
     */
    public List<RcsTemplateEventInfoConfig> getSettleRcsTemplateEventInfoConfigs(String orderNo, String playSetId) {
        List<RcsTemplateEventInfoConfig> rcsTemplateEventInfoConfigs;
        String eventKey = String.format(RedisKey.MATCH_PRE_SETTLE_EVENT_REDIS_KEY, playSetId);
        String redisStr = RcsLocalCacheUtils.getValueInfo(eventKey);
        if (StringUtils.isBlank(redisStr)) {
            log.info("::{}::事件配置缓存为空，key：{},玩法集:{},从数据库读取", orderNo, eventKey, Integer.parseInt(playSetId));
            rcsTemplateEventInfoConfigs = rcsTemplateEventInfoConfigMapper.selectList(new LambdaQueryWrapper<RcsTemplateEventInfoConfig>().eq(RcsTemplateEventInfoConfig::getCategorySetId, Integer.parseInt(playSetId)).eq(RcsTemplateEventInfoConfig::getRejectType, NumberConstant.NUM_TWO).ne(RcsTemplateEventInfoConfig::getEventType, "special"));
            if (CollectionUtils.isEmpty(rcsTemplateEventInfoConfigs)) {
                log.info("::{}::数据库没有获取到玩法集:{}的事件配置,默认常规事件配置", orderNo, playSetId);
                return null;
            }
            log.info("::{}::数据库获取到玩法集的事件配置,key:{}", orderNo, eventKey);
            commonSendMsgServerImpl.sendMsg(eventKey, rcsTemplateEventInfoConfigs);
        } else {
            rcsTemplateEventInfoConfigs = JSON.parseArray(redisStr, RcsTemplateEventInfoConfig.class);
            log.info("::{}::缓存查到赛事玩法集事件配置,key:{}", orderNo, eventKey);
        }
        return rcsTemplateEventInfoConfigs;
    }

    /**
     * 查询接距配置
     *
     * @param orderItem 投注项对象
     * @return 接距配置
     */
    @Override
    public RcsTournamentTemplateAcceptConfig queryAcceptConfig(OrderItem orderItem) {
        String playSetId = this.getPlayCollect(orderItem);
        MatchEventInfo matchEventInfo = this.getMatchEventInfo(RedisKey.REDIS_EVENT_INFO, playSetId, orderItem);
        String orderNo = orderItem.getOrderNo();
        String eventCode = Objects.nonNull(matchEventInfo) ? matchEventInfo.getEventCode() : "";
        log.info("::{}::当前事件code='{}'", orderItem.getOrderNo(), eventCode);
        try {
            RcsTournamentTemplateAcceptConfigRps rcsTournamentTemplateAcceptConfigRps = this.getWaitTime(orderNo, orderItem.getMatchId(), playSetId);
            List<RcsTemplateEventInfoConfig> rcsTemplateEventInfoConfigs = this.getRcsTemplateEventInfoConfigs(orderNo, playSetId);
            if (Objects.isNull(rcsTournamentTemplateAcceptConfigRps) || Objects.isNull(rcsTemplateEventInfoConfigs)) {
                log.info("::{}::没有获取到接距等待时间或接距玩法集配置，默认安全事件配置", orderItem.getOrderNo());
                return this.initEventConfig(MatchEventConfigEnum.EVENT_SAFETY.getCode());
            }
            List<RcsTournamentTemplateAcceptConfig> allEventConfigs = this.initConfig(rcsTournamentTemplateAcceptConfigRps, rcsTemplateEventInfoConfigs);
            RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig = this.getConfig(allEventConfigs, eventCode);
            //如果是空并且是初始化的时候才去找
            if (Objects.isNull(rcsTournamentTemplateAcceptConfig)) {
                //获取上一次事件类型
                MatchEventInfo lastMatchInfo = this.getMatchEventInfo(RedisKey.MATCH_LAST_TIME_EVENT_CODE, playSetId, orderItem);
                eventCode = Objects.nonNull(lastMatchInfo) ? lastMatchInfo.getEventCode() : "";
                rcsTournamentTemplateAcceptConfig = this.getConfig(allEventConfigs, eventCode);
                log.info("::{}::当前事件没有匹配到,则获取上一次事件code={}，配置和等待时间={}", orderItem.getOrderNo(), eventCode, JSON.toJSONString(rcsTournamentTemplateAcceptConfig));
            } else {
                log.info("::{}::当前事件玩法集配置和等待时间={}", orderItem.getOrderNo(), JSONObject.toJSONString(rcsTournamentTemplateAcceptConfig));
            }
            List<String> eventCodes = Arrays.asList("yellow_card", "yellow_red_card", "red_card");
            if (StringUtils.isNotBlank(eventCode) && !Objects.isNull(rcsTournamentTemplateAcceptConfig) && MatchEventConfigEnum.EVENT_SOURCE_RB.getCode().equalsIgnoreCase(rcsTournamentTemplateAcceptConfig.getDataSource()) && MatchEventConfigEnum.EVENT_REJECT.getCode().equalsIgnoreCase(rcsTournamentTemplateAcceptConfig.getEventType()) && eventCodes.contains(eventCode)) {
                rcsTournamentTemplateAcceptConfig.setMaxWaitTime(350);
                rcsTournamentTemplateAcceptConfig.setEventCode(eventCode);
                rcsTournamentTemplateAcceptConfig.setEventType(MatchEventConfigEnum.EVENT_DANGER.getCode());
                log.info("::{}::RB特殊拒单事件,设置最大等待时间350s", orderItem.getOrderNo());
            }
            //bug41130 G01/K01事件源Goal事件特殊接拒优化 特殊事件需要限制进球类玩法集
            //bug44126 R01进球特殊事件,只做进球类玩法集 增加RO1数据源
            String categorySetCode = getCategoryPlaySetCodeById(orderNo, rcsTournamentTemplateAcceptConfig);
            boolean isFootballGoal = CategorySetCodeEnum.FOOTBALL_GOAL.name().equalsIgnoreCase(categorySetCode);
            List<String> G01_K01_R01_EventCodes = Arrays.asList("goal");
            List<String> G01_K01_R01_DataSource = Arrays.asList(MatchEventConfigEnum.EVENT_SOURCE_BG.getCode(), MatchEventConfigEnum.EVENT_SOURCE_KO.getCode(), MatchEventConfigEnum.EVENT_SOURCE_RB.getCode());
            if (isFootballGoal && StringUtils.isNotBlank(eventCode) && Objects.nonNull(rcsTournamentTemplateAcceptConfig) && G01_K01_R01_DataSource.contains(StringUtils.upperCase(rcsTournamentTemplateAcceptConfig.getDataSource())) && MatchEventConfigEnum.EVENT_REJECT.getCode().equalsIgnoreCase(rcsTournamentTemplateAcceptConfig.getEventType()) && G01_K01_R01_EventCodes.contains(eventCode)) {
                rcsTournamentTemplateAcceptConfig.setMaxWaitTime(350);
                rcsTournamentTemplateAcceptConfig.setEventCode(eventCode);
                rcsTournamentTemplateAcceptConfig.setEventType(MatchEventConfigEnum.EVENT_DANGER.getCode());
                log.info("::{}::{}特殊拒单事件,设置最大等待时间350s", orderItem.getOrderNo(), rcsTournamentTemplateAcceptConfig.getDataSource());
            }
            if (Objects.isNull(rcsTournamentTemplateAcceptConfig)) {
                rcsTournamentTemplateAcceptConfig = this.initEventConfig(MatchEventConfigEnum.EVENT_SAFETY.getCode());
                log.info("::{}::没有匹配到事件配置信息，默认安全事件配置", orderItem.getOrderNo());
            }
            rcsTournamentTemplateAcceptConfig.setId(Objects.nonNull(matchEventInfo) ? matchEventInfo.getId() : null);
            return rcsTournamentTemplateAcceptConfig;

        } catch (Exception e) {
            log.error("::{}::配置接拒单事件报错,默认拒单事件:{}", orderNo, e.getMessage(), e);
            return this.initEventConfig(MatchEventConfigEnum.EVENT_REJECT.getCode());
        }
    }

    /**
     * 查询接距配置
     *
     * @param orderItem 投注项对象
     * @return 接距配置
     */
    @Override
    public RcsTournamentTemplateAcceptConfig queryAcceptConfig(PreOrderDetailRequest orderItem) {
        String playSetId = this.getPlayCollect(orderItem);
        MatchEventInfo matchEventInfo = this.getMatchEventInfo(RedisKey.REDIS_EVENT_INFO, playSetId, orderItem);
        String orderNo = orderItem.getOrderNo();
        String eventCode = Objects.nonNull(matchEventInfo) ? matchEventInfo.getEventCode() : "";
        log.info("::{}::提前結算当前事件code='{}'", orderItem.getOrderNo(), eventCode);
        if (StringUtils.isNotBlank(eventCode) && VAR_EVENT_LIST.contains(eventCode)&& matchEventInfo != null) {
            RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig = new RcsTournamentTemplateAcceptConfig();
            rcsTournamentTemplateAcceptConfig.setEventCode(eventCode);
            rcsTournamentTemplateAcceptConfig.setEventType(MatchEventConfigEnum.EVENT_REJECT.getCode());
            rcsTournamentTemplateAcceptConfig.setEventTime(matchEventInfo.getEventTime());
            return rcsTournamentTemplateAcceptConfig;
        }
        try {
            RcsTournamentTemplateAcceptConfigRps rcsTournamentTemplateAcceptConfigRps = this.getPreSettleWaitTime(orderNo, orderItem.getMatchId(), playSetId);
            List<RcsTemplateEventInfoConfig> rcsTemplateEventInfoConfigs = this.getSettleRcsTemplateEventInfoConfigs(orderNo, playSetId);
            if (Objects.isNull(rcsTournamentTemplateAcceptConfigRps) || Objects.isNull(rcsTemplateEventInfoConfigs)) {
                log.info("::{}::提前結算没有获取到接距等待时间或接距玩法集配置，默认安全事件配置", orderItem.getOrderNo());
                return this.initEventConfig(MatchEventConfigEnum.EVENT_SAFETY.getCode());
            }
            List<RcsTournamentTemplateAcceptConfig> allEventConfigs = this.initPreSettleConfig(rcsTournamentTemplateAcceptConfigRps, rcsTemplateEventInfoConfigs);
            RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig = this.getConfig(allEventConfigs, eventCode);
            if (rcsTournamentTemplateAcceptConfig != null && matchEventInfo != null) {
                rcsTournamentTemplateAcceptConfig.setEventTime(matchEventInfo.getEventTime());
            }
            //如果是空并且是初始化的时候才去找
            if (Objects.isNull(rcsTournamentTemplateAcceptConfig)) {
                //获取上一次事件类型
                MatchEventInfo lastMatchInfo = this.getMatchEventInfo(RedisKey.MATCH_LAST_TIME_EVENT_CODE, playSetId, orderItem);
                eventCode = Objects.nonNull(lastMatchInfo) ? lastMatchInfo.getEventCode() : "";
                rcsTournamentTemplateAcceptConfig = this.getConfig(allEventConfigs, eventCode);
                log.info("::{}::提前結算当前事件没有匹配到,则获取上一次事件code={}，配置和等待时间={}", orderItem.getOrderNo(), eventCode, JSON.toJSONString(rcsTournamentTemplateAcceptConfig));
            } else {
                log.info("::{}::提前結算当前事件玩法集配置和等待时间={}", orderItem.getOrderNo(), JSONObject.toJSONString(rcsTournamentTemplateAcceptConfig));
            }
            if (Objects.isNull(rcsTournamentTemplateAcceptConfig)) {
                rcsTournamentTemplateAcceptConfig = this.initEventConfig(MatchEventConfigEnum.EVENT_SAFETY.getCode());
                log.info("::{}::提前結算没有匹配到事件配置信息，默认安全事件配置", orderItem.getOrderNo());
            }
            rcsTournamentTemplateAcceptConfig.setId(Objects.nonNull(matchEventInfo) ? matchEventInfo.getId() : null);
            return rcsTournamentTemplateAcceptConfig;

        } catch (Exception e) {
            log.error("::{}::提前結算配置接拒单事件报错,默认拒单事件:{}", orderNo, e.getMessage(), e);
            return this.initEventConfig(MatchEventConfigEnum.EVENT_REJECT.getCode());
        }
    }

    private RcsTournamentTemplateAcceptConfig getConfig(List<RcsTournamentTemplateAcceptConfig> configs, String eventCode) {
        if (!CollectionUtils.isEmpty(configs)) {
            List<RcsTournamentTemplateAcceptConfig> rcsTournamentTemplateAcceptConfigList = configs.stream().filter(s -> s.getEventCode().equalsIgnoreCase(eventCode)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(rcsTournamentTemplateAcceptConfigList)) {
                return rcsTournamentTemplateAcceptConfigList.get(NumberConstant.NUM_ZERO);
            }
        }
        return null;
    }


    private List<RcsTournamentTemplateAcceptConfig> initConfig(RcsTournamentTemplateAcceptConfigRps rps, List<RcsTemplateEventInfoConfig> configs) {
        List<RcsTournamentTemplateAcceptConfig> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(configs)) {
            for (RcsTemplateEventInfoConfig config : configs) {
                RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig = new RcsTournamentTemplateAcceptConfig();
                BeanCopyUtils.copyProperties(config, rcsTournamentTemplateAcceptConfig);
                rcsTournamentTemplateAcceptConfig.setDataSource(rps.getDataSource());
                rcsTournamentTemplateAcceptConfig.setMinWaitTime(rps.getNormal());
                rcsTournamentTemplateAcceptConfig.setEventCode(rcsTournamentTemplateAcceptConfig.getEventCode().trim());
                if (StringUtils.equalsIgnoreCase(config.getEventType(), "safety")) {
                    rcsTournamentTemplateAcceptConfig.setMaxWaitTime(rps.getNormal());
                }else if (StringUtils.equalsIgnoreCase(config.getEventType(), "danger")) {
                    rcsTournamentTemplateAcceptConfig.setMaxWaitTime(rps.getMinWait());
                }else if (StringUtils.equalsIgnoreCase(config.getEventType(), "closing")) {
                    rcsTournamentTemplateAcceptConfig.setMaxWaitTime(rps.getMaxWait());
                }else if (StringUtils.equalsIgnoreCase(config.getEventType(), "reject")) {
                    rcsTournamentTemplateAcceptConfig.setMaxWaitTime(NumberConstant.NUM_ZERO);
                }
                list.add(rcsTournamentTemplateAcceptConfig);
            }
        }
        return list;
    }


    private List<RcsTournamentTemplateAcceptConfig> initPreSettleConfig(RcsTournamentTemplateAcceptConfigRps rps, List<RcsTemplateEventInfoConfig> configs) {
        List<RcsTournamentTemplateAcceptConfig> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(configs)) {
            for (RcsTemplateEventInfoConfig config : configs) {
                RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig = new RcsTournamentTemplateAcceptConfig();
                BeanCopyUtils.copyProperties(config, rcsTournamentTemplateAcceptConfig);
                rcsTournamentTemplateAcceptConfig.setDataSource(rps.getDataSource());
                rcsTournamentTemplateAcceptConfig.setMinWaitTime(rps.getNormal());
                rcsTournamentTemplateAcceptConfig.setEventCode(rcsTournamentTemplateAcceptConfig.getEventCode().trim());
                if (StringUtils.equalsIgnoreCase(config.getEventType(), "safety")) {
                    rcsTournamentTemplateAcceptConfig.setMaxWaitTime(rps.getNormal());
                } else if (StringUtils.equalsIgnoreCase(config.getEventType(), "danger")) {
                    rcsTournamentTemplateAcceptConfig.setMaxWaitTime(rps.getMinWait());
                } else if (StringUtils.equalsIgnoreCase(config.getEventType(), "closing")) {
                    rcsTournamentTemplateAcceptConfig.setMaxWaitTime(rps.getMaxWait());
                } else if (StringUtils.equalsIgnoreCase(config.getEventType(), "reject")) {
                    rcsTournamentTemplateAcceptConfig.setMaxWaitTime(NumberConstant.NUM_ZERO);
                }
                list.add(rcsTournamentTemplateAcceptConfig);
            }
        }
        return list;
    }

    /**
     * 获取当前事件的接距配置
     *
     * @param request 订单请求信息
     * @return 当前事件的接距配置
     */
    @Override
    public Response<RcsTournamentTemplateAcceptConfig> queryAcceptConfig(Request<OrderItem> request) {
        OrderItem orderItem = request.getData();
        return Response.success(this.queryAcceptConfig(orderItem));
    }

    /**
     * 查询赛事阶段服务类
     *
     * @param request 赛事id
     * @return 返回赛事阶段
     */
    @Override
    public Response<String> queryMatchEventInfo(Request<MatchEventInfoRes> request) {
        MatchEventInfoRes res = request.getData();
        String key = String.format(RedisKey.REDIS_MATCH_PERIOD, res.getMatchId());
        String cacheVal = RcsLocalCacheUtils.getValueInfo(key);
        log.info("rpc从本地查询事件缓存信息:{}", JSON.toJSONString(cacheVal));
        if (StringUtils.isNotBlank(cacheVal)) {
            return Response.success(cacheVal);
        }
        String period = rcsMarketCategorySetRelationMapper.queryMatchPeriodInfo(res.getMatchId());
        if (StringUtils.isNotBlank(period)) {
            commonSendMsgServerImpl.sendMsg(key, period);
            log.info("rpc从数据库查询事件缓存信息:{}", JSON.toJSONString(period));
            return Response.success(period);
        }
        return Response.success(String.valueOf(NumberConstant.NUM_ZERO));
    }

    @Override
    public Response<String> queryMatchDelaySeconds(Request<OrderItem> request) {
        OrderItem orderItem = request.getData();
        String redisStr = "";
        if (Objects.isNull(orderItem)) {
            log.info("GlobalId::{}::,queryMatchDelaySeconds获取到模板配置请求参数为空", JSONObject.toJSONString(request.getGlobalId()));
            return Response.success(redisStr);
        }
        log.info("::{}::获取到模板配置赛种id :{}", orderItem.getOrderNo(), orderItem.getSportId());
        if (Objects.nonNull(orderItem.getSportId()) && orderItem.getSportId() == 1) {
            String key = String.format(RedisKey.MATCH_PLAY_SECOND_REDIS_KEY, orderItem.getMatchId(), orderItem.getPlayId());
            redisStr = RcsLocalCacheUtils.getValueInfo(key);
            if (StringUtils.isBlank(redisStr)) {
                RcsTournamentTemplateAcceptConfigDto config = new RcsTournamentTemplateAcceptConfigDto();
                if ("BE".equalsIgnoreCase(orderItem.getDataSourceCode())) {
                    MtsTemplateConfigVo mtsTemplateConfigVo = this.queryMtsConfig(orderItem);
                    if (null != mtsTemplateConfigVo) {
                        config.setWaitSeconds(mtsTemplateConfigVo.getWaitTime());
                    }
                } else {
                    RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig = this.queryAcceptConfig(orderItem);
                    if (null != rcsTournamentTemplateAcceptConfig) {
                        config.setNormal(rcsTournamentTemplateAcceptConfig.getMinWaitTime());
                    }
                }
                redisStr = JSON.toJSONString(config);
                RcsLocalCacheUtils.timedCache.put(key, redisStr, RedisKey.CACHE_TIME_OUT);
            }

        } else {
            //查询config
            String configKey = String.format(RedisKey.REDIS_MATCH_MARKET_SECOND_CONFIG, orderItem.getMatchId(), orderItem.getPlayId(), orderItem.getPlaceNum());
            String val = RcsLocalCacheUtils.getValueInfo(configKey);
            log.info("::{}::缓存获取到模板配置 :{}", orderItem.getOrderNo(), val);
            if (StringUtils.isNotBlank(val)) {
                return Response.success(val);
            }
            if (StringUtils.isBlank(val)) {
                //去查数据库
                RcsTournamentTemplateAcceptConfigDto dto = matchInfoMapper.queryWaitSecondsInfo(orderItem);
                redisStr = Objects.nonNull(dto) ? JSON.toJSONString(dto) : "";
                log.info("::{}::缓存未获取到模板配置，走mysql查询:{}", orderItem.getOrderNo(), redisStr);
                this.commonSendMsgServerImpl.sendMsg(configKey, redisStr);
            }
            //查询SUB
            if (StringUtils.isBlank(redisStr)) {
                String subConfigKey = String.format(RedisKey.REDIS_MATCH_MARKET_SUB_SECOND_CONFIG, orderItem.getMatchId(), orderItem.getPlayId(), orderItem.getSubPlayId(), orderItem.getPlaceNum());
                String subVal = RcsLocalCacheUtils.getValueInfo(subConfigKey);
                log.info("::{}::缓存获取到sub模板配置:{}", orderItem.getOrderNo(), subVal);
                if (StringUtils.isBlank(subVal)) {
                    //去查数据库
                    RcsTournamentTemplateAcceptConfigDto dto = matchInfoMapper.querySubWaitSecondsInfo(orderItem);
                    redisStr = Objects.nonNull(dto) ? JSON.toJSONString(dto) : "";
                    log.info("::{}::未获取到模板配置走sub表获取:{}", orderItem.getOrderNo(), redisStr);
                    this.commonSendMsgServerImpl.sendMsg(configKey, redisStr);
                } else {
                    redisStr = subVal;
                }
            }
        }
        return Response.success(redisStr);
    }

    /**
     * 查询 MTS-1配置
     *
     * @param orderItem 订单对象
     * @return MTS-1配置
     */
    public MtsTemplateConfigVo queryMtsConfig(OrderItem orderItem) {
        Integer matchType = orderItem.getMatchType() == 2 ? 0 : 1;
        Request<MatchTemplateDataReqVo> requestParam = new Request<>();
        MatchTemplateDataReqVo matchTemplateDataReqVo = new MatchTemplateDataReqVo();
        matchTemplateDataReqVo.setSportId(orderItem.getSportId());
        matchTemplateDataReqVo.setMatchId(orderItem.getMatchId());
        matchTemplateDataReqVo.setMatchType(matchType);
        requestParam.setData(matchTemplateDataReqVo);
        Response<MatchTemplateDataResVo> resVoResponse = tournamentTemplateByMatchService.queryMatchTemplateData(requestParam);
        if (Objects.isNull(resVoResponse) || resVoResponse.getData() == null) {
            log.info("::{}::没有找到赛事模板信息", orderItem.getOrderNo());
            return null;
        }
        String config = resVoResponse.getData().getMtsConfigValue();
        return JSON.parseObject(config, MtsTemplateConfigVo.class);
    }

    /**
     * 设置当前事件的事件ID
     *
     * @param orderItem 投注项
     */
    @Override
    public void setEventId(OrderItem orderItem) {
        String playSet = this.getPlayCollect(orderItem);
        MatchEventInfo matchEventInfo = this.getMatchEventInfo(RedisKey.REDIS_EVENT_INFO, playSet, orderItem);
        if (matchEventInfo != null) {
            //设置比赛阶段
            orderItem.setEventId(matchEventInfo.getId());
        }else{
            orderItem.setEventId(-1L);
        }
    }

    /**
     * 设置当前事件的事件ID
     *
     * @param orderItem 投注项
     */
    @Override
    public void setPreSettleEventId(PreOrderDetailRequest orderItem) {
        String playSet = this.getPlayCollect(orderItem);
        MatchEventInfo matchEventInfo = this.getMatchEventInfo(RedisKey.REDIS_EVENT_INFO, playSet, orderItem);
        if (matchEventInfo != null) {
            //设置比赛阶段
            orderItem.setEventId(matchEventInfo.getId());
        }else{
            orderItem.setEventId(-1L);
        }
    }

    /**
     * 获取玩法集ID
     *
     * @param orderItem 订单信息
     * @return 玩法集ID
     */
    @Override
    public String getPlayCollect(OrderItem orderItem) {
        String key = String.format(RedisKey.MATCH_PLAY_SET_KEY, orderItem.getSportId(), orderItem.getPlayId());
        String playSet = RcsLocalCacheUtils.getValueInfo(key);
        log.info("::{}::查询缓存玩法集Id值={},赛事：{}，玩法ID:{}", orderItem.getOrderNo(), playSet, orderItem.getSportId(), orderItem.getPlayId());
        if (StringUtils.isBlank(playSet)) {
            playSet = rcsMarketCategorySetRelationMapper.queryCategorySetByPlayId(orderItem);
            log.info("::{}::数据库查询赛种ID:{}，玩法ID:{},玩法集Id值={}", orderItem.getOrderNo(), orderItem.getSportId(), orderItem.getPlayId(), playSet);
            if (StringUtils.isBlank(playSet)) {
                playSet = String.valueOf(NumberConstant.NUM_MINUS_ONE);
                log.info("::{}::没有配置玩法集，默认-1", orderItem.getOrderNo());
            }
            commonSendMsgServerImpl.sendMsg(key, playSet);
        }
        return playSet;
    }

    /**
     * 获取玩法集ID
     *
     * @param orderItem 订单信息
     * @return 玩法集ID
     */
    private String getPlayCollect(PreOrderDetailRequest orderItem) {
        String key = String.format(RedisKey.MATCH_PLAY_SET_KEY, orderItem.getSportId(), orderItem.getPlayId());
        String playSet = RcsLocalCacheUtils.getValueInfo(key);
        log.info("::{}::查询缓存玩法集Id值={},赛事：{}，玩法ID:{}", orderItem.getOrderNo(), playSet, orderItem.getSportId(), orderItem.getPlayId());
        if (StringUtils.isBlank(playSet)) {
            playSet = rcsMarketCategorySetRelationMapper.queryCategorySetByPlayId(orderItem);
            log.info("::{}::数据库查询赛种ID:{}，玩法ID:{},玩法集Id值={}", orderItem.getOrderNo(), orderItem.getSportId(), orderItem.getPlayId(), playSet);
            if (StringUtils.isBlank(playSet)) {
                playSet = String.valueOf(NumberConstant.NUM_MINUS_ONE);
                log.info("::{}::没有配置玩法集，默认-1", orderItem.getOrderNo());
            }
            commonSendMsgServerImpl.sendMsg(key, playSet);
        }
        return playSet;
    }

    @Override
    public MatchEventInfo getMatchEventInfo(String key, String playSet, OrderItem orderItem) {
        Long matchId = orderItem.getMatchId();
        String dataSourceCode = this.getDataSourceCode(playSet, orderItem);
        String redisKey = String.format(key, dataSourceCode, matchId);
        String obj = RcsLocalCacheUtils.getValueInfo(redisKey);
        log.info("::{}::查询事件缓存返回key:{},value={}", orderItem.getOrderNo(), redisKey, obj);
        if (StringUtils.isBlank(obj)) {
            log.info("::{}::查询事件缓存返回null;key:{}", orderItem.getOrderNo(), redisKey);
            return null;
        }
        return JSON.parseObject(obj, MatchEventInfo.class);
    }

    private MatchEventInfo getMatchEventInfo(String key, String playSet, PreOrderDetailRequest orderItem) {
        Long matchId = orderItem.getMatchId();
        String dataSourceCode = this.getDataSourceCode(playSet, orderItem);
        String redisKey = String.format(key, dataSourceCode, matchId);
        String obj = RcsLocalCacheUtils.getValueInfo(redisKey);
        log.info("::{}::查询事件缓存返回key:{},value={}", orderItem.getOrderNo(), redisKey, obj);
        if (StringUtils.isBlank(obj)) {
            log.info("::{}::查询事件缓存返回null;key:{}", orderItem.getOrderNo(), redisKey);
            return null;
        }
        return JSON.parseObject(obj, MatchEventInfo.class);
    }

    private String getDataSourceCode(String playSet, OrderItem orderItem) {
        Long matchId = orderItem.getMatchId();
        String dataSourceCode = RcsLocalCacheUtils.getValueInfo(String.format(RedisKey.REDIS_EVENT_DATA_SOURCE_CODE, matchId, playSet));
        log.info("::{}::缓存查询matchId:{},playSetId:{},玩法集数据源={}", orderItem.getOrderNo(), matchId, playSet, dataSourceCode);
        if (StringUtils.isBlank(dataSourceCode)) {
            //查询数据库信息
            dataSourceCode = matchInfoMapper.queryDataSourceCode(matchId, Long.valueOf(playSet));
            log.info("::{}::数据库查询matchId:{},playSetId:{},玩法集数据源={}", orderItem.getOrderNo(), matchId, playSet, dataSourceCode);
            commonSendMsgServerImpl.sendMsg(String.format(RedisKey.REDIS_EVENT_DATA_SOURCE_CODE, matchId, playSet), dataSourceCode);
        }
        return dataSourceCode;
    }

    @Override
    public String getDataSourceCode(String playSet, Long matchId) {
        String dataSourceCode = RcsLocalCacheUtils.getValueInfo(String.format(RedisKey.REDIS_EVENT_DATA_SOURCE_CODE, matchId, playSet));
        log.info("缓存查询matchId:{},playSetId:{},玩法集数据源={}", matchId, playSet, dataSourceCode);
        if (StringUtils.isBlank(dataSourceCode)) {
            //查询数据库信息
            dataSourceCode = matchInfoMapper.queryDataSourceCode(matchId, Long.valueOf(playSet));
            log.info("数据库查询matchId:{},playSetId:{},玩法集数据源={}", matchId, playSet, dataSourceCode);
            commonSendMsgServerImpl.sendMsg(String.format(RedisKey.REDIS_EVENT_DATA_SOURCE_CODE, matchId, playSet), dataSourceCode);
        }
        return dataSourceCode;
    }
    private String getDataSourceCode(String playSet, PreOrderDetailRequest orderItem) {
        Long matchId = orderItem.getMatchId();
        String dataSourceCode = RcsLocalCacheUtils.getValueInfo(String.format(RedisKey.DATA_LIVE_MATCH_SETTLE_DATASOURCE, matchId, playSet));
        log.info("::{}::缓存查询matchId:{},playSetId:{},玩法集数据源={}", orderItem.getOrderNo(), matchId, playSet, dataSourceCode);
        if (StringUtils.isBlank(dataSourceCode)) {
            //查询数据库信息
            dataSourceCode = matchInfoMapper.queryPreSettleDataSourceCode(matchId, Long.valueOf(playSet));
            log.info("::{}::数据库查询matchId:{},playSetId:{},玩法集数据源={}", orderItem.getOrderNo(), matchId, playSet, dataSourceCode);
            commonSendMsgServerImpl.sendMsg(String.format(RedisKey.DATA_LIVE_MATCH_SETTLE_DATASOURCE, matchId, playSet), dataSourceCode);
        }
        return dataSourceCode;
    }

    /**
     * 初始化接距配置
     *
     * @param eventType 时间类型
     * @return 接距配置
     */
    private RcsTournamentTemplateAcceptConfig initEventConfig(String eventType) {
        RcsTournamentTemplateAcceptConfig config = new RcsTournamentTemplateAcceptConfig();
        config.setDataSource(MatchEventConfigEnum.EVENT_SOURCE_SR.getCode());
        config.setMinWaitTime(MatchEventConfigEnum.EVENT_SAFETY.getValue());
        if (MatchEventConfigEnum.EVENT_SAFETY.getCode().equalsIgnoreCase(eventType)) {
            config.setMaxWaitTime(MatchEventConfigEnum.EVENT_SAFETY.getValue());
            config.setEventType(MatchEventConfigEnum.EVENT_SAFETY.getCode());
            config.setEventCode("ball_safe");
            config.setEventName("默认安全事件");
        } else if (MatchEventConfigEnum.EVENT_DANGER.getCode().equalsIgnoreCase(eventType)) {
            config.setMaxWaitTime(MatchEventConfigEnum.EVENT_DANGER.getValue());
            config.setEventType(MatchEventConfigEnum.EVENT_DANGER.getCode());
        } else if (MatchEventConfigEnum.EVENT_CLOSING.getCode().equalsIgnoreCase(eventType)) {
            config.setMaxWaitTime(MatchEventConfigEnum.EVENT_CLOSING.getValue());
            config.setEventType(MatchEventConfigEnum.EVENT_CLOSING.getCode());
        } else {
            config.setMinWaitTime(MatchEventConfigEnum.EVENT_REJECT.getValue());
            config.setMaxWaitTime(MatchEventConfigEnum.EVENT_REJECT.getValue());
            config.setEventType(MatchEventConfigEnum.EVENT_REJECT.getCode());
        }
        return config;
    }

    @Override
    public String getCategoryPlaySetCodeById(String logKey, RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig) {
        if (Objects.isNull(rcsTournamentTemplateAcceptConfig) || Objects.isNull(rcsTournamentTemplateAcceptConfig.getCategorySetId())) {
            log.info("::{}::玩法集Id值为空,查询玩法集code值返回null", logKey);
            return null;
        }
        Integer playSetId = rcsTournamentTemplateAcceptConfig.getCategorySetId();
        String key = String.format(RedisKey.RCS_MATCH_PLAY_SET_CODE, playSetId);
        String playSetCode = RcsLocalCacheUtils.getValueInfo(key);
        log.info("::{}::玩法集Id值={},查询玩法集code值={}", logKey, playSetId, playSetCode);
        if (StringUtils.isBlank(playSetCode)) {
            RcsMarketCategorySet marketCategorySet = marketCategorySetMapper.selectById(playSetId);
            if (Objects.nonNull(marketCategorySet)) {
                playSetCode = marketCategorySet.getPlaySetCode();
                log.info("::{}::玩法集Id值={},数据库查询玩法集code值{}", logKey, playSetId, playSetCode);
                RcsLocalCacheUtils.timedCache.put(key, playSetCode, RedisKey.CACHE_TIME_OUT);
            }
        }
        return playSetCode;
    }

    /**
     * 根据联赛id,赛事id,球队id查询设置缓存,缓存没有从数据库取
     * @param standardTournamentId
     * @param standardMatchId
     * @param teamId
     * @return
     */
    @Override
    public RcsGoalWarnSet getGoalWarnSet(Long standardTournamentId, Long standardMatchId, String teamId){
        //联赛+赛事+球队维度获取设置
        RcsGoalWarnSet goalWarnSet = queryGoalWarnSet(standardTournamentId, standardMatchId, teamId);
        if(Objects.nonNull(goalWarnSet)){
            return goalWarnSet;
        }
        //联赛+球队维度获取设置
        goalWarnSet = queryGoalWarnSet(standardTournamentId, 0L, teamId);
        if(Objects.nonNull(goalWarnSet)){
            return goalWarnSet;
        }
        //联赛+赛事维度获取设置
        goalWarnSet = queryGoalWarnSet(standardTournamentId, standardMatchId, "0");
        if(Objects.nonNull(goalWarnSet)){
            return goalWarnSet;
        }
        //联赛维度获取设置
        goalWarnSet = queryGoalWarnSet(standardTournamentId, 0L, "0");
        if(Objects.nonNull(goalWarnSet)){
            return goalWarnSet;
        }
        return null;
    }

    /**
     * 从缓存或者数据库获取设置
     * @param tournamentId
     * @param matchId
     * @param teamId
     * @return
     */
    private RcsGoalWarnSet queryGoalWarnSet(Long tournamentId, Long matchId, String teamId) {
        String key = String.format(RedisKey.RCS_GOAL_WARN_SET, tournamentId, matchId, teamId);
        String tournamentGoalWarnSetStr = RcsLocalCacheUtils.getValueInfo(key);
        //没有对应的数据使用占位符缓存,避免重复查询数据库
        final String placeholder = "-1";
        //-1占位符表示没有该配置
        if(!placeholder.equals(tournamentGoalWarnSetStr)){
            if(Objects.nonNull(tournamentGoalWarnSetStr)){
                //缓存取到直接返回
                log.info("::{}::{}::{}::获取进球点预警设置缓存:{}", tournamentId, matchId, teamId, tournamentGoalWarnSetStr);
                return JSONObject.parseObject(tournamentGoalWarnSetStr, RcsGoalWarnSet.class);
            }else{
                //去数据库查
                LambdaQueryWrapper<RcsGoalWarnSet> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(RcsGoalWarnSet::getStandardTournamentId, tournamentId)
                        .eq(RcsGoalWarnSet::getStandardMatchId, matchId)
                        .eq(RcsGoalWarnSet::getStandardTeamId, teamId);
                RcsGoalWarnSet goalWarnSet = rcsGoalWarnSetMapper.selectOne(wrapper);
                RcsLocalCacheUtils.timedCache.put(key, Objects.nonNull(goalWarnSet) ? JSONObject.toJSONString(goalWarnSet) : placeholder, RedisKey.CACHE_TIME_OUT);
                log.info("::{}::{}::{}::获取进球点预警设置数据库查询:{}", tournamentId, matchId, teamId, JSONObject.toJSONString(goalWarnSet));
                return goalWarnSet;
            }
        }
        log.info("::{}::{}::{}::获取进球点预警设置为:{},没有匹配到预警设置返回null", tournamentId, matchId, teamId, tournamentGoalWarnSetStr);
        return null;
    }
}
