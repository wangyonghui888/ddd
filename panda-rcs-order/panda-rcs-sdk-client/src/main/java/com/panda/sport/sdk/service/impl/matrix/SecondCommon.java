package com.panda.sport.sdk.service.impl.matrix;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.TemplateAcceptConfigServer;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.data.rcs.dto.order.MatchEventInfoRes;
import com.panda.sport.data.rcs.vo.RcsTournamentTemplateAcceptConfig;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.panda.sport.sdk.bean.*;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.mapper.StandardMatchInfoMapper;
import com.panda.sport.sdk.sdkenum.MatchEventConfigEnum;
import com.panda.sport.sdk.util.DateUtils;
import com.panda.sport.sdk.util.RcsLocalCacheUtils;
import com.panda.sport.sdk.vo.StandardMatchInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author :  koala
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.sport.sdk.service.impl.matrix
 * @Description :  秒接公共方法
 * @Date: 2022-02-26 16:21
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Singleton
public class SecondCommon {
    private static final String RCS_TASK_MATCH_INFO_CACHE = "rcs:task:match:info:";
    //自动接距redis_key 赛事id和玩法id拼接
    private static final String ORDER_LABEL_DELAY_CONFIG = "rcs:label:order:delay:config:%s";
    //
    private static final String USER_LABEL_CONFIG = "rcs:special:user:order:delay:sencond:config:%s";
    private static final Logger logger = LoggerFactory.getLogger(SecondCommon.class);
    @Inject
    JedisClusterServer jedisClusterServer;

    @Inject
    private LimitApiService limitApiService;

    @Inject
    private TemplateAcceptConfigServer templateAcceptConfigServer;


    /**
     * 是否秒接赛事
     *
     * @param orderBean 订单信息
     * @return 是和否
     */
    public boolean secondRace(OrderBean orderBean) {
        for (OrderItem orderItem : orderBean.getItems()) {
            if (!isSecondRest(orderBean, orderItem)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 如果不是安全事件直接返回false
     *
     * @param orderBean 订单信息
     * @return 是或否
     */
    private boolean isSafe(OrderBean orderBean) {
        for (OrderItem orderItem : orderBean.getItems()) {
            if (SportIdEnum.isFootball(orderItem.getSportId())) {
                Request<OrderItem> request = new Request<>();
                request.setData(orderItem);
                RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig = this.templateAcceptConfigServer.queryAcceptConfig(request).getData();
                //判断是否属于特殊事件
                if (MatchEventConfigEnum.EVENT_SPEC.getCode().equalsIgnoreCase(rcsTournamentTemplateAcceptConfig.getEventType())) {
                    if(rcsTournamentTemplateAcceptConfig.getEventCode().equalsIgnoreCase("penalty_awarded")){//PK-1
                        orderBean.setReason("16");
                    } else if (rcsTournamentTemplateAcceptConfig.getEventCode().equalsIgnoreCase("breakaway")) {//单刀-2
                        orderBean.setReason("17");
                    }else if (rcsTournamentTemplateAcceptConfig.getEventCode().equalsIgnoreCase("dfk")) {//危险任意球-3
                        orderBean.setReason("18");
                    }else if (rcsTournamentTemplateAcceptConfig.getEventCode().equalsIgnoreCase("danger_ball")) {//最后几分钟危险球-4
                        orderBean.setReason("19");
                    }
                    return true;
                }
                if (!MatchEventConfigEnum.EVENT_SAFETY.getCode().equalsIgnoreCase(rcsTournamentTemplateAcceptConfig.getEventType())) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 处理是否秒接逻辑
     *
     * @param orderBean 订单信息
     * @param orderItem 投注项信息
     * @return 是或者否
     */
    private boolean isSecondRest(OrderBean orderBean, OrderItem orderItem) {
        //如果是早盘直接返回true
        if (Objects.nonNull(orderItem.getMatchType()) && orderItem.getMatchType() == 1) {
            return true;
        }
        Long sportId = orderItem.getSportId().longValue();
        String orderNo = orderItem.getOrderNo();
        //43955 去掉网球5 乒乓球8、羽毛球10
        if (Arrays.asList(1, 2, 3, 5, 7, 8, 9, 10).contains(orderItem.getSportId())) {
            String period = getMatchPeriod(orderItem);
            if (Arrays.asList(1, 2, 3, 7, 9).contains(orderItem.getSportId()) && StringUtils.isNotBlank(period)) {
                Integer periodId = Integer.parseInt(period);
                if (SportIdEnum.isFootball(sportId) || SportIdEnum.isBasketball(sportId)) {
                    if (Arrays.asList(302, 31).contains(periodId)) {
                        //中场休息秒接
                        logger.info("::{}::中场休息秒接", orderNo);
                        return true;
                    }
                    //增加逻辑如果是加时赛和PK玩法集
                    String playSet = this.getPlaySet(orderItem);
                    logger.info("::{}::查询数据库获取玩法集ID:{}", orderNo, playSet);
                    //测试环境玩法集ID 点球大战588 加时赛587
                    if (StringUtils.equalsIgnoreCase("588", playSet) && Arrays.asList(0, 6, 61, 7, 100, 32, 41, 33, 42, 110, 34).contains(periodId)) {
                    //if (StringUtils.equalsIgnoreCase("134", playSet) && Arrays.asList(0, 6, 61, 7, 100, 32, 41, 33, 42, 110, 34).contains(periodId)) {
                        logger.info("::{}::点球大战玩法集赛阶段玩法秒接", orderNo);
                        return true;
                    }
                    if (StringUtils.equalsIgnoreCase("587", playSet) && Arrays.asList(0, 6, 61, 7, 100, 32, 33).contains(periodId)) {
//                    if (StringUtils.equalsIgnoreCase("135", playSet) && Arrays.asList(0, 6, 61, 7, 100, 32, 33).contains(periodId)) {
                        logger.info("::{}::加时赛玩法集阶段玩法秒接", orderNo);
                        return true;
                    }
                } else if (SportIdEnum.isTennis(sportId) || SportIdEnum.isPingPong(sportId) || SportIdEnum.isVolleyBall(sportId) || SportIdEnum.isBadminton(sportId.intValue())) {
                    if (Arrays.asList(301, 302, 303, 304, 305, 306, 800, 900, 1000, 1100, 1200).contains(periodId)) {
                        //中场休息秒接
                        logger.info("::{}::中场休息秒接", orderNo);
                        return true;
                    }
                }
            }
            if (recentGoing(orderItem.getMatchId())) {
                logger.info("::{}::即将开赛秒接", orderNo);
                //即将开赛秒接
                return true;
            }
        }

        //联赛赛事模板设置0秒接单
        Request<OrderItem> request = new Request<>();
        request.setData(orderItem);
        Response<String> response = templateAcceptConfigServer.queryMatchDelaySeconds(request);
        String redisStr = response.getData();
        String redisValue = RcsLocalCacheUtils.getValueSdkServer(String.format(ORDER_LABEL_DELAY_CONFIG, orderBean.getUserTagLevel()),
                orderItem.getSportId().toString(), jedisClusterServer::hget, 5 * 60 * 1000L);
        String userLabelKey = String.format(USER_LABEL_CONFIG, orderItem.getUid());
        String userLabel = jedisClusterServer.hget(userLabelKey, sportId.toString());
        if (StringUtils.isBlank(userLabel)) {
            limitApiService.queryRcsUserConfig(orderItem.getUid().toString());
            userLabel = jedisClusterServer.hget(userLabelKey, sportId.toString());
        }

        Response<RcsQuotaBusinessLimitResVo> busConfig = limitApiService.getRcsQuotaBusinessLimit(String.valueOf(orderBean.getTenantId()));
        //新增商户配置
        RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit = busConfig.getData();
        logger.info("::{}::赛事ID:{},用户ID:{}赛事模板秒接值:{},标签值:{},用户特殊标签值:{},商户配置:{}", orderNo, orderItem.getMatchId(), orderItem.getUid(), redisStr, redisValue, userLabel, rcsQuotaBusinessLimit);

        if (StringUtils.isNotBlank(redisStr)) {
            RcsTournamentTemplateAcceptConfigDto config = JSONObject.parseObject(redisStr, RcsTournamentTemplateAcceptConfigDto.class);

            if (sportId == 2L && Objects.nonNull(config) && config.getWaitSeconds() == 0) {
                logger.info("::{}::篮球滚球默认4S", orderNo);
                config.setWaitSeconds(4);
            }

            logger.info("::{}::获取到模板设置的延时配置:{}", orderNo, JSONObject.toJSONString(config));
            boolean waitTimeIs0 = false;

            //BE赛事模板延迟设置0，则可以触发秒接
            if ("BE".equalsIgnoreCase(orderItem.getDataSourceCode())) {
                if (Objects.nonNull(config) && Objects.nonNull(config.getWaitSeconds()) && config.getWaitSeconds() == 0) waitTimeIs0 = true;
            } else {
                if ((Objects.nonNull(config.getNormal()) && config.getNormal() == 0 && this.isSafe(orderBean)) || (Objects.nonNull(config.getWaitSeconds()) && config.getWaitSeconds() == 0)) {
                    waitTimeIs0 = true;
                }
            }
            if (waitTimeIs0) {
                RcsLabelLimitConfig rcsLabelLimitConfig = JSONObject.parseObject(redisValue, RcsLabelLimitConfig.class);
                RcsUserConfig rcsUserConfig = JSONObject.parseObject(userLabel, RcsUserConfig.class);
                if ((Objects.isNull(rcsLabelLimitConfig) || Objects.isNull(rcsLabelLimitConfig.getBetExtraDelay()) || rcsLabelLimitConfig.getBetExtraDelay() == 0) &&
                        (Objects.isNull(rcsUserConfig) || Objects.isNull(rcsUserConfig.getBetExtraDelay()) || rcsUserConfig.getBetExtraDelay() == 0) &&
                        (Objects.isNull(rcsQuotaBusinessLimit) || Objects.isNull(rcsQuotaBusinessLimit.getDelay()) || rcsQuotaBusinessLimit.getDelay() == 0)) {
                    logger.info("::{}::模板配置触发秒接", orderNo);
                    return true;
                }
            }
        }
        return false;
    }

    private String getPlaySet(OrderItem orderItem) {
        Request<OrderItem> request = new Request<>();
        request.setData(orderItem);
        Response<String> response = templateAcceptConfigServer.queryCategorySetByPlayId(request);
        logger.info("::{}::查询玩法集ID:{}", orderItem.getOrderNo(), response);
        if (null == response || null == response.getData()) {
            return "-1";
        }
        return response.getData();
    }

    /**
     * 查询赛事阶段信息
     *
     * @param orderItem 投注项信息
     * @return 赛事阶段
     */
    private String getMatchPeriod(OrderItem orderItem) {
        Request<MatchEventInfoRes> request = new Request<>();
        MatchEventInfoRes matchEventInfoRes = new MatchEventInfoRes();
        matchEventInfoRes.setMatchId(orderItem.getMatchId());
        request.setData(matchEventInfoRes);
        Object val = templateAcceptConfigServer.queryMatchEventInfo(request).getData();
        logger.info("::{}::获取到赛事阶段信息:{}", orderItem.getOrderNo(), val);
        return String.valueOf(val);

    }


    /**
     * 查询赛事阶段信息
     *
     * @param matchId 投注项信息
     * @return 赛事阶段
     */
    public String getMatchPeriodByMatchId(Long matchId) {
        Request<MatchEventInfoRes> request = new Request<>();
        MatchEventInfoRes matchEventInfoRes = new MatchEventInfoRes();
        matchEventInfoRes.setMatchId(matchId);
        request.setData(matchEventInfoRes);
        Object val = templateAcceptConfigServer.queryMatchEventInfo(request).getData();
        logger.info("::{}::获取到赛事阶段信息:{}", matchId, val);
        return String.valueOf(val);

    }

    /**
     * 查询是否即将开赛
     *
     * @param matchId 赛事id
     * @return 是否即将开赛
     */
    public boolean recentGoing(Long matchId) {
        String key = RCS_TASK_MATCH_INFO_CACHE + matchId;
        String value = RcsLocalCacheUtils.getValueSdkServer(key, jedisClusterServer::get, 2 * 1000L);
//        logger.info("赛事秒接是否即将开赛缓存key:{},value:{}", key, value);
        MatchMarketLiveBean match = new MatchMarketLiveBean();
        if (StringUtils.isBlank(value)) {
            if (jedisClusterServer.exists(key)) {
                value = jedisClusterServer.get(key);
                match = JSON.parseObject(value, MatchMarketLiveBean.class);
                logger.info("赛事秒接是否即将开赛redis缓存数据  key{} value{}", key, value);
            } else {
                try {
                    StandardMatchInfoMapper standardMatchInfoMapper = SpringContextUtils.getBeanByClass(StandardMatchInfoMapper.class);
                    StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectOne(new LambdaQueryWrapper<StandardMatchInfo>()
                            .eq(StandardMatchInfo::getId, matchId));
                    logger.info("赛事秒接是否即将开赛数据库查询  key{} value{}", key, JSON.toJSONString(standardMatchInfo));
                    if (Objects.nonNull(standardMatchInfo)) {
                        match.setMatchId(standardMatchInfo.getId());
                        match.setMatchStartTime(DateUtils.transferLongToDateStrings(standardMatchInfo.getBeginTime()));
                        match.setMatchStatus(standardMatchInfo.getMatchStatus());
                        match.setPeriod(standardMatchInfo.getMatchPeriodId().intValue());
                        jedisClusterServer.set(key, JSON.toJSONString(match));
                        jedisClusterServer.expire(key, 60 * 60);
                        RcsLocalCacheUtils.timedCache.put(key, JSON.toJSONString(match), 60 * 60 * 1000L);
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    logger.error("赛事秒接是否即将开赛数据库查询异常: key{} e{}", key, e);
                }
            }
        } else {
            match = JSON.parseObject(value, MatchMarketLiveBean.class);
            logger.info("内存缓存获取赛事秒接是否即将开赛key:{}", key);
        }
        if (match != null) {
            long timeMillis = System.currentTimeMillis();
            long beginTime = DateUtils.tranferStringToDate(match.getMatchStartTime()).getTime();
            Integer matchStatus = match.getMatchStatus();
            Integer period = match.getPeriod();
            return (timeMillis > beginTime && matchStatus == 0) || (period == 0 && matchStatus == 1) || (matchStatus == 0 && period > 0);
        }
        return false;
    }
}