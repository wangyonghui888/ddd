package com.panda.sport.sdk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.data.rcs.api.tournament.TournamentTemplateByMatchService;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.limit.BetAmountLimitVo;
import com.panda.sport.data.rcs.dto.limit.MarkerPlaceLimitAmountReqVo;
import com.panda.sport.data.rcs.dto.limit.MarkerPlaceLimitAmountResVo;
import com.panda.sport.data.rcs.dto.limit.MatchLimitDataReqVo;
import com.panda.sport.data.rcs.dto.limit.MatchLimitDataVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaMerchantSingleFieldLimitVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaUserDailyQuotaVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaUserSingleNoteVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaUserSingleSiteQuotaVo;
import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;
import com.panda.sport.data.rcs.dto.special.RcsUserSpecialBetLimitConfigDTO;
import com.panda.sport.data.rcs.dto.tournament.MatchTemplatePlayMarginDataReqVo;
import com.panda.sport.data.rcs.dto.tournament.MatchTemplatePlayMarginDataResVo;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.dto.limit.UserDayLimit;
import com.panda.sport.rcs.enums.DataSourceEnum;
import com.panda.sport.rcs.enums.limit.UserSpecialLimitType;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.panda.sport.rcs.utils.i18n.CommonUtils;
import com.panda.sport.sdk.bean.LNBasktballEnum;
import com.panda.sport.sdk.bean.NacosProperitesConfig;
import com.panda.sport.sdk.bean.RedCatSingleLimitConfig;
import com.panda.sport.sdk.bean.SportIdEnum;
import com.panda.sport.sdk.common.OddsScope;
import com.panda.sport.sdk.constant.BaseConstants;
import com.panda.sport.sdk.constant.LimitRedisKeys;
import com.panda.sport.sdk.constant.RedisKeys;
import com.panda.sport.sdk.constant.SdkConstants;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.mapper.RcsTournamentTemplateJumpConfigMapper;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.mapper.RcsTournamentTemplateJumpConfigMapper;
import com.panda.sport.sdk.sdkenum.SeriesEnum;
import com.panda.sport.sdk.util.*;
import com.panda.sport.sdk.vo.RcsTournamentTemplateJumpConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.panda.sport.sdk.constant.BaseConstants.*;
import static com.panda.sport.sdk.constant.RedisKeys.TEMPLATE_TOURNAMENT_AMOUNT;

/**
 * @Project Name: panda-rcs-order-group
 * @Package Name: com.panda.sport.sdk.service.impl
 * @Description : 限额配置服务
 * @Author : Paca
 * @Date : 2020-09-26 9:25
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Singleton
public class LimitConfigService {

    private static final Logger log = LoggerFactory.getLogger(LimitConfigService.class);

    /**
     * 过期时间2小时
     */
    private static final int EXPIRE = Long.valueOf(TimeUnit.HOURS.toSeconds(2L)).intValue();

    @Inject
    private JedisClusterServer jedisClusterServer;

    @Inject
    private LimitApiService limitApiService;

    @Inject
    private TournamentTemplateByMatchService tournamentTemplateByMatchService;

    /**
     * rpc获取商户限额
     *
     * @param businessId 商户ID
     * @return 商户信息
     * @author beulah
     */
    public RcsQuotaBusinessLimitResVo getBusinessLimit(final Long businessId) {
        String key = LimitRedisKeys.MERCHANT_LIMIT_KEY + businessId;
        return RcsLocalCacheUtils.getValue(key, (k) -> {
            Response<RcsQuotaBusinessLimitResVo> response;
            try {
                response = limitApiService.getRcsQuotaBusinessLimit(businessId.toString());
            } catch (Exception e) {
                log.error("::{}::获取商户配置信息RPC异常:{}", businessId.toString(), e);
                throw new RcsServiceException("获取商户配置异常");
            }
            if (response == null || response.getCode() != 200) {
                throw new RcsServiceException("获取商户配置异常");
            }
            return response.getData();
        }, 60 * 1000L);
    }

    /**
     * 累加商户单日已用限额
     *
     * @param time       当前时间
     * @param businessId 商户id
     * @param incrValue  累计值
     * @return 累计结果
     */
    public long businessLimitIncrBy(Long time, String businessId, Long incrValue) {
        if (time == null) {
            time = System.currentTimeMillis();
        }
        String dateExpect = DateUtils.getDateExpect(time);
        String key = String.format(RedisKeys.PAID_DATE_BUS_REDIS_CACHE, dateExpect, businessId);
        Long value = jedisClusterServer.incrBy(key, incrValue);
        log.info("::{}::累加商户单日单关已用限额::key={},value={},result={}", businessId, key, incrValue, value);
        jedisClusterServer.expire(key, 26 * 60 * 60);
        if (value == null) {
            value = 0L;
        }
        return value;
    }

    /**
     * 累加商户单日 串关 已用限额
     *
     * @param time       当前时间
     * @param businessId 商户id
     * @param incrValue  累计值
     * @return 累计结果
     */
    public long businessSeriesLimitIncrBy(Long time, String businessId, Long incrValue) {
        if (time == null) {
            time = System.currentTimeMillis();
        }
        String dateExpect = DateUtils.getDateExpect(time);
        String key = String.format(RedisKeys.PAID_DATE_BUS_SERIES_REDIS_CACHE, dateExpect, businessId);
        Long value = jedisClusterServer.incrBy(key, incrValue);
        log.info("累加商户单日串关已用限额,key={},value={},result={}", key, incrValue, value);
        jedisClusterServer.expire(key, 26 * 60 * 60);
        if (value == null) {
            value = 0L;
        }
        return value;
    }

    /**
     * 获取商户限额
     *
     * @param businessId            商户id
     * @param indexKey              日志key
     * @param rcsQuotaBusinessLimit 商户信息
     * @return 商户限额
     */
    public long getBusinessAvailablePaymentV2(final Long businessId, String indexKey, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit) {
        try {
            Integer businessSingleDayLimitSwitch = rcsQuotaBusinessLimit.getBusinessSingleDayLimitSwitch();
            if (businessSingleDayLimitSwitch == null) {
                businessSingleDayLimitSwitch = 0;
            }
            if (!NumberUtils.INTEGER_ONE.equals(businessSingleDayLimitSwitch)) {
                log.info("::{}::商户单日限额开关没开:{},返回默认限额值:{}", indexKey, businessSingleDayLimitSwitch, Long.MAX_VALUE);
                return Long.MAX_VALUE;
            }
            // 是否已经停止接单
            String dateExpect = DateUtils.getDateExpect(System.currentTimeMillis());
            String stopKey = String.format(RedisKeys.PAID_DATE_BUS_STOP_REDIS_CACHE, dateExpect, businessId);
            String stopFlag = RcsLocalCacheUtils.getValue(stopKey, jedisClusterServer::getNoLog, 5 * 60 * 1000L);
            if (BaseConstants.MERCHANT_STOP_ORDER_SIGN.equals(stopFlag)) {
                log.warn("::{}::商户单日限额不足,不再收单:{}", indexKey, stopKey);
                return 0L;
            } else {
                log.info("::{}::商户单日限额默认取值:{}", indexKey, Long.MAX_VALUE);
                return Long.MAX_VALUE;
            }
        } catch (Exception e) {
            log.error("::{}::商户单日限额异常:{}", indexKey, e.getMessage(),e);
            return 0L;
        }
    }


    /**
     * 获取商户串关可用赔付，单位：分
     *
     * @param businessId 商户id
     * @param dateExpect 账务日
     * @return 串关可用赔付
     * @author Paca
     */
    public long getBusinessSeriesAvailablePayment(final Long businessId, final String dateExpect) {
        RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit = getBusinessLimit(businessId);
        Integer businessSingleDayLimitSwitch = rcsQuotaBusinessLimit.getBusinessSingleDayLimitSwitch();
        if (businessSingleDayLimitSwitch == null) {
            businessSingleDayLimitSwitch = 0;
        }
        log.info("额度查询-商户单日限额开关:" + businessSingleDayLimitSwitch);
        if (!NumberUtils.INTEGER_ONE.equals(businessSingleDayLimitSwitch)) {
            return Long.MAX_VALUE;
        }
        // 是否已经停止接单
        String stopKey = String.format(RedisKeys.PAID_DATE_BUS_STOP_REDIS_CACHE, dateExpect, businessId);
        String stopFlag = RcsLocalCacheUtils.getValue(stopKey, jedisClusterServer::getNoLog, 5 * 60 * 1000L);
        log.info("额度查询-Redis商户停止接单标志:key={},value={}", stopKey, stopFlag);
        if (BaseConstants.MERCHANT_STOP_ORDER_SIGN.equals(stopFlag)) {
            log.warn("额度查询，商户维护，不再收单：{}", stopKey);
            return 0L;
        } else {
            // 查询商户单日串关可用额度
            String key = String.format(RedisKeys.PAID_DATE_BUS_SERIES_REDIS_CACHE, dateExpect, businessId);
            String value = jedisClusterServer.get(key);
            log.info("额度查询-Redis 商户单日串关已用额度:key={},value={}", key, value);
            long used = NumberUtils.toLong(value);
            // 如果串关限额大于已用额度
            if (rcsQuotaBusinessLimit.getBusinessSingleDaySeriesLimit() > used) {
                return Long.MAX_VALUE;
            }
            return 0L;
        }

    }

    /**
     * 赛事单场串关限额 剩余/可用
     *
     * @param businessId
     * @param matchIds
     * @return
     */
    public long getSeriesSingleMatchAvailableLimit(final Long businessId, String userId, List<Long> matchIds) {
        RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit = getBusinessLimit(businessId);
        if (rcsQuotaBusinessLimit.getUserSingleStrayLimit() == null) {
            return Long.MAX_VALUE;
        }
        Long min = matchIds.stream().map(matchId -> {
            String key = String.format(RedisKeys.PAID_DATE_BUS_REDIS_MATCH_CACHE, userId, businessId, matchId);
            String value = jedisClusterServer.get(key);
            log.info("额度查询，赛事单场串关限额已用：key={},value={}", key, value);
            // 配置-已用=剩余
            return rcsQuotaBusinessLimit.getUserSingleStrayLimit() - CommonUtils.toBigDecimal(value).longValue();
        }).min(Long::compareTo).orElse(0L);
        // 只判断是否有额度，不精确计算
        if (min > 0) {
            return Long.MAX_VALUE;
        }
        return 0L;
    }

    /**
     * 获取单关用户单日限额
     *
     * @param order
     * @return
     * @author Paca
     */
    public UserDayLimit getUserDailyLimit(final ExtendBean order) {
        String indexKey = StringUtils.isBlank(order.getOrderId()) ? order.getUserId() : order.getOrderId();

        final int tournamentLevel = convertTournamentLevel(order.getTournamentLevel());
        // 先从缓存取          //优化 用户单日限额key 拆分
        return getUserDailyLimit(order.getBusId(), order.getSportId(), order.getUserId(), tournamentLevel, order.getMatchId(), 1, indexKey);
    }

    private UserDayLimit getUserDayLimit(String sportId, Map<String, String> userDayMap, Map<String, String> userDaySeriesMap, Long busId, String uid, int playType, String indexKey) {
        if (userDayMap.get(sportId) == null || userDaySeriesMap.get(sportId) == null) {
            sportId = "-1";
        }
        // 用户限额比例 如果是单关就取单关的比例 1是单关，2是串关
        BigDecimal userQuotaRatio = playType == 1 ? getBusinessLimit(busId).getUserQuotaRatio() : getBusinessLimit(busId).getUserStrayQuotaRatio();

        String userDay = userDayMap.getOrDefault(sportId, "0");
        String userDayTotal = userDayMap.getOrDefault("0", "0");
        String userDaySeries = userDaySeriesMap.getOrDefault(sportId, "0");
        String userDaySeriesTotal = userDaySeriesMap.getOrDefault("0", "0");

        //特殊用户百分比
        BigDecimal percentage = getUserLimitPercentage(uid, indexKey);

        UserDayLimit userDayLimit = new UserDayLimit();
        userDayLimit.setSportId(Integer.valueOf(sportId));
        userDayLimit.setDayCompensation(new BigDecimal(userDay).multiply(userQuotaRatio).multiply(percentage));
        userDayLimit.setDayCompensationTotal(new BigDecimal(userDayTotal).multiply(userQuotaRatio).multiply(percentage));
        userDayLimit.setCrossDayCompensation(new BigDecimal(userDaySeries).multiply(userQuotaRatio).multiply(percentage));
        userDayLimit.setCrossDayCompensationTotal(new BigDecimal(userDaySeriesTotal).multiply(userQuotaRatio).multiply(percentage));
        return userDayLimit;
    }

    public UserDayLimit getUserDailyLimit(String businessId, String sportId, String userId, Integer tournamentLevel, String matchId, int playType, String indexKey) {
        String userDayLimitKey = LimitRedisKeys.USER_DAY_LIMIT_KEY;
        String userDaySeriesLimitKey = LimitRedisKeys.USER_DAY_SERIES_LIMIT_KEY;
        String userDay = RcsLocalCacheUtils.getValue(userDayLimitKey + sportId, jedisClusterServer::get);
        String userDayNull = RcsLocalCacheUtils.getValue(userDayLimitKey + "-1", jedisClusterServer::get);
        String userDayTotal = RcsLocalCacheUtils.getValue(userDayLimitKey + "0", jedisClusterServer::get);
        Map<String, String> userDayMap = new HashMap<>();
        if (StringUtils.isNotBlank(userDay)) {
            userDayMap.put(sportId, userDay);
        }
        if (StringUtils.isNotBlank(userDayNull)) {
            userDayMap.put("-1", userDayNull);
        }
        if (StringUtils.isNotBlank(userDayTotal)) {
            userDayMap.put("0", userDayTotal);
        }
        log.info("::{}::Redis获取用户单日限额：key={},value={}", indexKey, userDayLimitKey, userDayMap);
        String userDaySeries = RcsLocalCacheUtils.getValue(userDaySeriesLimitKey + sportId, jedisClusterServer::get);
        String userDaySeriesNull = RcsLocalCacheUtils.getValue(userDaySeriesLimitKey + "-1", jedisClusterServer::get);
        String userDaySeriesTotal = RcsLocalCacheUtils.getValue(userDaySeriesLimitKey + "0", jedisClusterServer::get);
        Map<String, String> userDaySeriesMap = new HashMap<>();
        if (StringUtils.isNotBlank(userDaySeries)) {
            userDaySeriesMap.put(sportId, userDaySeries);
        }
        if (StringUtils.isNotBlank(userDaySeriesNull)) {
            userDaySeriesMap.put("-1", userDaySeriesNull);
        }
        if (StringUtils.isNotBlank(userDaySeriesTotal)) {
            userDaySeriesMap.put("0", userDaySeriesTotal);
        }
        log.info("::{}::Redis获取用户单日串关限额：key={},value={}", indexKey, userDaySeriesLimitKey, userDaySeriesMap);
        if (CollectionUtils.isNotEmptyMap(userDayMap) && CollectionUtils.isNotEmptyMap(userDaySeriesMap) && userDayMap.size() == 3 && userDayMap.size() == 3) {
            return getUserDayLimit(sportId, userDayMap, userDaySeriesMap, Long.valueOf(businessId), userId, 1, indexKey);
        }

        // 缓存没有调用rpc接口查询
        Response<MatchLimitDataVo> response = getMatchLimitData(sportId, tournamentLevel, matchId, LimitDataTypeEnum.USER_DAILY_LIMIT);
        log.info("::{}::调用rpc获取用户单日限额：response={}", indexKey, JSON.toJSONString(response));
        checkResponse(response, "调用rpc获取用户单日限额失败");
        List<RcsQuotaUserDailyQuotaVo> userDailyQuotaList = response.getData().getUserDailyQuotaList();
        if (CollectionUtils.isEmpty(userDailyQuotaList)) {
            log.error("::{}::未配置用户单日限额：sportId={},tournamentLevel={},matchId={}", indexKey, sportId, tournamentLevel, matchId);
            throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, "未配置用户单日限额");
        }
        userDayMap.clear();
        userDaySeriesMap.clear();
        userDailyQuotaList.forEach(config -> {
            String key = String.valueOf(config.getSportId());
            jedisClusterServer.setex(userDayLimitKey + key, 30 * 24 * 60 * 60, config.getDayCompensation().toPlainString());
            userDayMap.put(key, config.getDayCompensation().toString());
            jedisClusterServer.setex(userDaySeriesLimitKey + key, 30 * 24 * 60 * 60, config.getCrossDayCompensation().toPlainString());
            userDaySeriesMap.put(key, config.getCrossDayCompensation().toString());

            RcsLocalCacheUtils.timedCache.put(userDayLimitKey + key, config.getDayCompensation().toPlainString());
            RcsLocalCacheUtils.timedCache.put(userDaySeriesLimitKey + key, config.getCrossDayCompensation().toPlainString());
        });
        return getUserDayLimit(sportId, userDayMap, userDaySeriesMap, Long.valueOf(businessId), userId, playType, indexKey);
    }


    /**
     * 获取用户单关单日赔付限额
     *
     * @param sportId         赛种
     * @param userId          用户id
     * @param tournamentLevel 联赛等级
     * @param matchId         赛事
     * @param indexKey        日志
     * @return 单日赔付限额配置
     */
    public UserDayLimit getUserSingleDailyLimit(String sportId, String userId, Integer tournamentLevel, String matchId, BigDecimal userQuotaRatio, String indexKey) {
        String userDayLimitKey = LimitRedisKeys.USER_DAY_LIMIT_KEY;
        //赛种单日限额
        String userDay = RcsLocalCacheUtils.getValue(userDayLimitKey + sportId, jedisClusterServer::get);
        //赛种单日限额累计
        String userDayTotal = RcsLocalCacheUtils.getValue(userDayLimitKey + "0", jedisClusterServer::get);
        if (StringUtils.isBlank(userDay)) {
            //赛种默认限额
            sportId = "-1";
            userDay = RcsLocalCacheUtils.getValue(userDayLimitKey + sportId, jedisClusterServer::get);
        }
        UserDayLimit userDayLimit = new UserDayLimit();
        //特殊用户百分比
        BigDecimal percentage = getUserLimitPercentage(userId, indexKey);
        if (StringUtils.isNotBlank(userDay) && StringUtils.isNotBlank(userDayTotal)) {
            userDayLimit.setSportId(Integer.valueOf(sportId));
            userDayLimit.setDayCompensation(new BigDecimal(userDay).multiply(userQuotaRatio).multiply(percentage));
            userDayLimit.setDayCompensationTotal(new BigDecimal(userDayTotal).multiply(userQuotaRatio).multiply(percentage));
            log.info("::{}::缓存获取到用户单日限额配置:{},缓存key:{}", indexKey, JSON.toJSONString(userDayLimit), userDayLimitKey);
            return userDayLimit;
        }

        // 缓存没有调用rpc接口查询【单串关都查出来缓存】
        final int level = convertTournamentLevel(tournamentLevel);
        Response<MatchLimitDataVo> response = getMatchLimitData(sportId, level, matchId, LimitDataTypeEnum.USER_DAILY_LIMIT);
        log.info("::{}::用户单日限额配置调用rpc返回:{}", indexKey, JSON.toJSONString(response));
        checkResponse(response, "调用rpc获取用户单日限额失败");
        List<RcsQuotaUserDailyQuotaVo> userDailyQuotaList = response.getData().getUserDailyQuotaList();
        if (CollectionUtils.isEmpty(userDailyQuotaList)) {
            log.error("::{}::用户单日未获取到配置,赛种:{},赛事:{}", indexKey, sportId, matchId);
            throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, "未配置用户单日限额");
        }
        String userDaySeriesLimitKey = LimitRedisKeys.USER_DAY_SERIES_LIMIT_KEY;
        final String currentSportId = sportId;  //当前球种
        userDailyQuotaList.forEach(config -> {
            String key = String.valueOf(config.getSportId());
            String dayCompensation = config.getDayCompensation().toPlainString();
            String crossDayCompensation = config.getCrossDayCompensation().toPlainString();
            jedisClusterServer.setex(userDayLimitKey + key, 30 * 24 * 60 * 60, dayCompensation);
            jedisClusterServer.setex(userDaySeriesLimitKey + key, 30 * 24 * 60 * 60, crossDayCompensation);
            RcsLocalCacheUtils.timedCache.put(userDayLimitKey + key, config.getDayCompensation().toPlainString());
            RcsLocalCacheUtils.timedCache.put(userDaySeriesLimitKey + key, config.getCrossDayCompensation().toPlainString());
            //拿到对应赛种配置返回
            if (currentSportId.equals(key)) {
                userDayLimit.setSportId(Integer.valueOf(key));
                userDayLimit.setDayCompensation(new BigDecimal(dayCompensation).multiply(userQuotaRatio).multiply(percentage));
                userDayLimit.setDayCompensationTotal(new BigDecimal(crossDayCompensation).multiply(userQuotaRatio).multiply(percentage));

            }
        });
        if (userDayLimit.getDayCompensation() != null) {
            log.info("::{}::用户单日RPC取到单日限额:{},单日总限额:{},赛种:{}", indexKey, userDayLimit.getDayCompensation(), userDayLimit.getDayCompensationTotal(), userDayLimit.getSportId());
            return userDayLimit;
        }
        //如果走数据查询还是没有获取到对应赛种的配置，则看下其他赛种的配置 sportId =-1
        userDailyQuotaList.forEach(config -> {
            String key = String.valueOf(config.getSportId());
            if ("-1".equals(key)) {
                String dayCompensation = config.getDayCompensation().toPlainString();
                String crossDayCompensation = config.getCrossDayCompensation().toPlainString();
                userDayLimit.setSportId(Integer.valueOf(key));
                userDayLimit.setDayCompensation(new BigDecimal(dayCompensation).multiply(userQuotaRatio).multiply(percentage));
                userDayLimit.setDayCompensationTotal(new BigDecimal(crossDayCompensation).multiply(userQuotaRatio).multiply(percentage));
                log.info("::{}::用户单日RPC取到单日限额:{},单日总限额:{},赛种:{}", indexKey, dayCompensation, crossDayCompensation, -1);
            }
        });
        return userDayLimit;
    }


    /**
     * 获取用户串关单日限额
     *
     * @param businessId 商户ID
     * @param sportIds   赛事ID
     * @param userId     用户ID
     * @return 返回用户限额
     */
    public List<UserDayLimit> getUserDailyLimit(String businessId, Set<String> sportIds, String userId) {
        return sportIds.stream().map(e ->
                getUserDailyLimit(businessId, e, userId, 0, "0", 2, null)
        ).collect(Collectors.toList());
    }

    /**
     * 获取用户单注单关限额重构
     * 包含用户单注赛事模板配置 ，调价窗口 ， 特殊限额 ， 综合球类
     *
     * @param order 注单信息
     * @param resVo 商户配置信息
     * @return 单注单关限额配置
     * @Author beulah
     */

    public RcsQuotaUserSingleNoteVo getAllRcsQuotaUserSingleNote(ExtendBean order, RcsQuotaBusinessLimitResVo resVo) {
        String indexKey = StringUtils.isBlank(order.getOrderId()) ? order.getUserId() : order.getOrderId();
        String logKey = StringUtils.isBlank(order.getOrderId()) ? "限额" : "投注";
        BigDecimal userQuotaRatio = resVo.getUserQuotaBetRatio() == null ? new BigDecimal("1") : resVo.getUserQuotaBetRatio();
        log.info("::{}::{}-用户单关限额比例:{}", indexKey, logKey, userQuotaRatio);
        //1、用户单注单关模板限额(单注投注赔付限额/玩法累计赔付限额) - 赛事模板或通用模板配置 本地缓存2分钟
        RcsQuotaUserSingleNoteVo rcsQuotaUserSingleNoteVo = getRcsQuotaUserSingleNoteVoNew(order, resVo);

        //纳入综合操盘的赛种单注单关限额以联赛模板为准 25198
        if(order.getDataSourceCode().equals(DataSourceEnum.RC.getDataSource())){
            //C01限额逻辑
            //按照赛事时长计算C01限额处理
            Integer matchLength= order.getItemBean().getMatchLength();
            //定义C01限额默认值
            BigDecimal redCatLimit=new BigDecimal(NacosProperitesConfig.redCatLimitConfig.getDefaultSingle());
            List<RedCatSingleLimitConfig> singleList=NacosProperitesConfig.redCatLimitConfig.getSingle();
            if (matchLength!=null&&matchLength>0) {
                Optional<RedCatSingleLimitConfig> optional=singleList.stream().filter(i->i.getMatchLength().equals(matchLength)).findAny();
                if (optional.isPresent()) {
                    redCatLimit=new BigDecimal(optional.get().getLimit()) ;
                }
            }
            //C01赛事没有盘口调价窗口所以直接计算户单关限额比例
            BigDecimal singlePayLimit = redCatLimit.multiply(userQuotaRatio);
            rcsQuotaUserSingleNoteVo.setSinglePayLimit(singlePayLimit);
            rcsQuotaUserSingleNoteVo.setSingleBetLimit(null);
            log.info("::{}::{}-C01赛事id:{},时长:{},最大限额值:{}", indexKey, logKey,order.getItemBean().getMatchId(),matchLength, rcsQuotaUserSingleNoteVo.getSinglePayLimit());
        }

        //2、优先读取调价窗口 - 调价窗口有设置赔付额，则优先使用这个配置  本地缓存30S
        //需求2607
        String zkPlayId = order.getPlayId();
        if(order.getSportId().equals(SportIdEnum.BASKETBALL.getId().toString())) {
            String key = LimitRedisKeys.getTradingTypeStatusKey(order.getMatchId(),zkPlayId,order.getItemBean().getMatchType().toString());
            log.info("::{}::读取调价窗口,LN模式下联控玩法额度跟随主控玩法KEY: {}",key);
            if(jedisClusterServer.exists(key)) {
                String lnValue = jedisClusterServer.get(key);
                if(lnValue.equals("4")) {
                    zkPlayId = LNBasktballEnum.getNameById(Integer.valueOf(zkPlayId)).toString();
                    log.info("::{}::读取调价窗口,LN模式下联控玩法额度跟随主控玩法{}::", zkPlayId);
                }
            }
        }
        //TODO subPlayId=playId暂时这么弄，过了提测再去看怎么查找subPlayId
        String marketPlaceKey = String.format(LimitRedisKeys.MARKET_PLACE_KEY, order.getMatchId(), zkPlayId, zkPlayId, order.getItemBean().getPlaceNum());
        String limitAmount = RcsLocalCacheUtils.getValue(marketPlaceKey, jedisClusterServer::get, 30 * 1000L);
        if (StringUtils.isEmpty(limitAmount) || "null".equals(limitAmount)) {
            limitAmount = getMarketPlaceLimit(order);
            log.info("::{}::{}-盘口调价窗口限额RPC查库返回:{}", indexKey, logKey, limitAmount);
        }
        if (StringUtils.isNotBlank(limitAmount) && !"null".equals(limitAmount)) {
            BigDecimal singlePayLimit = new BigDecimal(limitAmount).multiply(userQuotaRatio);
            rcsQuotaUserSingleNoteVo.setSinglePayLimit(singlePayLimit);
            log.info("::{}::{}-【优先】读取操盘调价:{},赔付限额值:{}", indexKey, logKey, limitAmount, singlePayLimit);
        }

        //3、特殊用户单注赔付限额
        BigDecimal specialUserAmount = getSpecialUserBetAmount(order);
        if (specialUserAmount.compareTo(rcsQuotaUserSingleNoteVo.getSinglePayLimit()) < 0) {
            rcsQuotaUserSingleNoteVo.setSinglePayLimit(specialUserAmount);
            log.info("::{}::{}-赔付限额【取】特殊会员赔付值:{}", indexKey, logKey, specialUserAmount);
        }
        //4、获取用户百分比
        BigDecimal percentage = getUserLimitPercentage(order.getUserId(), indexKey);
        rcsQuotaUserSingleNoteVo.setSinglePayLimit(rcsQuotaUserSingleNoteVo.getSinglePayLimit().multiply(percentage));
        rcsQuotaUserSingleNoteVo.setCumulativeCompensationPlaying(rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying().multiply(percentage));
        //5、纳入综合操盘的赛种单注单关限额以联赛模板为准 25198
        if (PaidService.sportIds.contains(order.getSportId())) {
            Long maxBetOrMaxPay = getTournamentTemplateJumpConfig(order.getTournamentId(), "0".equals(order.getIsScroll()) ? "1" : "0", indexKey, logKey);
            if (maxBetOrMaxPay != null) {
                maxBetOrMaxPay = userQuotaRatio.multiply(BigDecimal.valueOf(maxBetOrMaxPay)).longValue() * 100;
                log.info("::{}::{}-综合球类用户比例后:{}", indexKey, logKey, maxBetOrMaxPay);
                //特殊用户百分比
                rcsQuotaUserSingleNoteVo.setSinglePayLimit(new BigDecimal(maxBetOrMaxPay).multiply(percentage));
            }
        }

        return rcsQuotaUserSingleNoteVo;
    }

    /**
     * 获取综合球种联赛限额配置
     *
     * @param tournamentId 联赛id
     * @param matchType    赛事阶段
     * @param indexKey     日志key
     * @param logKey       日志key
     * @return 限额值
     */
    private Long getTournamentTemplateJumpConfig(Long tournamentId, String matchType, String indexKey, String logKey) {
        String newKey = TEMPLATE_TOURNAMENT_AMOUNT + String.format("%s_%s", tournamentId, matchType);
        String redisVal = jedisClusterServer.get(newKey);
        log.info("::{}::{}-综合球类联赛级别的限额和限付金额:{}，联赛id={},获取key:{}", indexKey, logKey, redisVal, tournamentId, newKey);
        if (!StringUtils.isEmpty(redisVal)) {
            return Long.valueOf(redisVal);
        }
        try {
            RcsTournamentTemplateJumpConfigMapper rcsTournamentTemplateJumpConfigMapper = SpringContextUtils.getBeanByClass(RcsTournamentTemplateJumpConfigMapper.class);
            log.info("::{}::{}综合球种限额数据库查询:{},tournamentId={},matchType={}", indexKey, logKey, rcsTournamentTemplateJumpConfigMapper, tournamentId, matchType);
            RcsTournamentTemplateJumpConfig rcsTournamentTemplateJumpConfig = rcsTournamentTemplateJumpConfigMapper.selectOne(new LambdaQueryWrapper<RcsTournamentTemplateJumpConfig>()
                    .eq(RcsTournamentTemplateJumpConfig::getTournamentId, tournamentId)
                    .eq(RcsTournamentTemplateJumpConfig::getMatchType, matchType));
            log.info("::{}::{}综合球种限额数据库查询:{}", indexKey, logKey, JSONObject.toJSONString(rcsTournamentTemplateJumpConfig));
            if (Objects.nonNull(rcsTournamentTemplateJumpConfig)) {
                jedisClusterServer.set(newKey, String.valueOf(rcsTournamentTemplateJumpConfig.getMaxSingleBetAmount()));
                return rcsTournamentTemplateJumpConfig.getMaxSingleBetAmount();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("::{}::{}综合球种限额数据库查询异常:", indexKey, logKey, e);
        }
        return null;
    }

    /**
     * 特殊抽水配置
     *
     * @param order          订单信息
     * @param userQuotaRatio 用户单关限额比例
     * @return 特殊抽水限额信息
     * @Author beulah
     */
    public Map<String, BigDecimal> dynamicLimitNew(ExtendBean order, BigDecimal userQuotaRatio) {
        String indexKey = StringUtils.isBlank(order.getOrderId()) ? order.getUserId() : order.getOrderId();
        String logKey = StringUtils.isBlank(order.getOrderId()) ? "限额" : "投注";
        try {
            OrderItem item = order.getItemBean();
            //1、业务调用风控单关限额查询接口时需要传当前投注项赔率、另一个投注项的赔率。只限足球，具体玩法见附件，不在附件中的玩法，不需要传另一个投注项赔率。
            if (item.getSportId() != 1 || item.getOtherOddsValue() == null) {
                log.info("::{}::{}-非足球特殊抽水无需计算,赛种:{},赔率:{}", indexKey, logKey, item.getSportId(), item.getOtherOddsValue());
                return null;
            }
            //2、用户限额模式为特殊VIP、特殊单注单场限额时，走现有单关限额逻辑。  0 无  1标签限额  2特殊百分比限额 3特殊单注单场限额 4特殊vip限额
            String key = LimitRedisKeys.getUserSpecialLimitKey(order.getUserId());
            String type = RcsLocalCacheUtils.getValue(key, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, jedisClusterServer::hget);
            if (StringUtils.isNotBlank(type) && (type.equals(UserSpecialLimitType.SINGLE.getType()) || type.equals(UserSpecialLimitType.VIP.getType()))) {
                log.info("::{}::{}-特殊抽水特殊用户类型:{} 无需计算", indexKey, logKey, type);
                return null;
            }
            String matchType = String.valueOf(item.getMatchType()).equals("2") ? "0" : "1";
            //rpc 获取trade本地缓存的特殊抽水配置
            Request<MatchTemplatePlayMarginDataReqVo> var1 = new Request<>();
            MatchTemplatePlayMarginDataReqVo vo = new MatchTemplatePlayMarginDataReqVo();
            vo.setMatchId(Long.valueOf(order.getMatchId()));
            vo.setPlayId(Integer.valueOf(order.getPlayId()));
            vo.setMatchType(Integer.valueOf(matchType));
            vo.setSportId(Integer.valueOf(order.getSportId()));
            var1.setData(vo);
            MatchTemplatePlayMarginDataResVo result;
            Response<MatchTemplatePlayMarginDataResVo> response;
            try {
                response = tournamentTemplateByMatchService.queryMatchTemplatePlayMarginData(var1);
                result = response.getData();
            } catch (Exception e) {
                log.error("::{}::{}-特殊抽水rpc获取异常:{}", indexKey, logKey, e.getMessage(), e);
                return null;
            }
            if (result == null) {
                log.warn("::{}::{}-特殊抽水rpc没有获取到配置信息,响应:{}", indexKey, logKey, JSONObject.toJSONString(response));
                return null;
            }
            //3、用户限额模式为无、特殊百分比限额，但玩法特殊抽水未生效时，走现有单关限额逻辑。
            Integer isSpecialPumping = result.getIsSpecialPumping();
            if (isSpecialPumping != null && isSpecialPumping != 1) {
                log.info("::{}::{}-特殊抽水玩法开关没开,开关值:{}", indexKey, logKey, isSpecialPumping);
                return null;
            }

            //赔率类型 0低赔 1高赔
            int lowHighType = 0;
            //找到低赔
            Double lowOddValue = item.getOddsValue();
            if (lowOddValue.compareTo(item.getOtherOddsValue()) > 0) {
                lowOddValue = item.getOtherOddsValue();
                lowHighType = 1;
            }
            lowOddValue = lowOddValue / 100000;
            String scope = OddsScope.getScope(lowOddValue.toString(), matchType);
            log.info("::{}::{}-特殊抽水投注项赔率为:{},赔率类型:{}", indexKey, logKey, lowOddValue, lowHighType);
            //4、赔率区间开关状态判断 {"1.01-1.05":1,"1.06-1.25":1,"1.26-1.39":1,"1.40-1.60":1,"1.61-1.85":1,"1.86-1.88":1,"1.89-2.00":1}
            Map<String, Integer> scopeSwitchVal = JSON.parseObject(result.getSpecialOddsIntervalStatus(), Map.class);
            if (org.springframework.util.CollectionUtils.isEmpty(scopeSwitchVal) || 1 != scopeSwitchVal.get(scope)) {
                log.info("::{}::{}-特殊抽水赔率区间开关没开,开关值:{},区间值:{}", indexKey, logKey, scopeSwitchVal == null ? null : scopeSwitchVal.get(scope), scope);
                return null;
            }
            // 5、高赔取高赔的限额配置 低赔取低赔的限额配置 {"1.01-1.05":40000,"1.06-1.25":40000,"1.26-1.39":40000,"1.40-1.60":56000,"1.61-1.85":80000,"1.86-1.88":80000,"1.89-2.00":80000}
            Map oddScopeLimitMap;

            if (lowHighType == 1) {
                oddScopeLimitMap = JSON.parseObject(result.getSpecialOddsIntervalHigh(), Map.class);
            } else {
                oddScopeLimitMap = JSON.parseObject(result.getSpecialOddsIntervalLow(), Map.class);
            }
            if (org.springframework.util.CollectionUtils.isEmpty(oddScopeLimitMap) || oddScopeLimitMap.get(scope) == null) {
                log.info("::{}::{}-特殊抽水赔率区间限额没有找到,赛事:{},玩法:{},区间值:{}", indexKey, logKey, order.getMatchId(), order.getPlayId(), JSON.toJSONString(oddScopeLimitMap));
                return null;
            }
            BigDecimal oddScopeLimit = new BigDecimal(String.valueOf(oddScopeLimitMap.get(scope)));
            RcsQuotaUserSingleNoteVo rcsQuotaUserSingleNoteVo = new RcsQuotaUserSingleNoteVo();
            log.info("::{}::{}-特殊抽水赔率区间:{},限额值:{},用户限额比例:{}", indexKey, logKey, scope, oddScopeLimit, userQuotaRatio);
            rcsQuotaUserSingleNoteVo.setSinglePayLimit(oddScopeLimit.multiply(new BigDecimal(100)).multiply(userQuotaRatio));
            //赔率处理
            if (lowHighType == 0) {
                BigDecimal lastLowOdds = new BigDecimal(order.getOdds()).subtract(new BigDecimal("1"));
                rcsQuotaUserSingleNoteVo.setSinglePayLimit(rcsQuotaUserSingleNoteVo.getSinglePayLimit().divide(lastLowOdds, 2, RoundingMode.DOWN));
                log.info("::{}::{}-特殊抽水,低赔再次计算,赔率值:{},限额值:{}", indexKey, logKey, lastLowOdds, rcsQuotaUserSingleNoteVo.getSinglePayLimit());
            } else if (new BigDecimal(order.getOdds()).compareTo(new BigDecimal("2")) > 0) {
                //  高赔 赔率大于2 需要除以港赔 magic 2022-07-06 需要产品确认，先加上给测试测
                BigDecimal lastLowOdds = new BigDecimal(order.getOdds()).subtract(new BigDecimal("1"));
                rcsQuotaUserSingleNoteVo.setSinglePayLimit(rcsQuotaUserSingleNoteVo.getSinglePayLimit().divide(lastLowOdds, 2, RoundingMode.DOWN));
                log.info("::{}::{}-特殊抽水,高赔大于2需要除以港赔再次计算,赔率值:{},限额值:{}", indexKey, logKey, lastLowOdds, rcsQuotaUserSingleNoteVo.getSinglePayLimit());
            }

            Map<String, BigDecimal> dataMap = new HashMap<>();
            //如果是高赔  获取到高赔单注保底限额 {"1.01-1.05":40000,"1.06-1.25":40000,"1.26-1.39":40000,"1.40-1.60":56000,"1.61-1.85":80000,"1.86-1.88":80000,"1.89-2.00":80000}
            if (lowHighType == 1) {
                //Map<String, Integer> highOddScopeBetMap = JSON.parseObject(result.getSpecialBettingIntervalHigh(), Map.class);
                Map highOddScopeBetMap = (Map) JSON.parse(result.getSpecialBettingIntervalHigh());
                if (org.springframework.util.CollectionUtils.isEmpty(highOddScopeBetMap) || highOddScopeBetMap.get(scope) == null) {
                    log.info("::{}::{}特殊抽水高赔保底投注限额没有找到,赛事:{},玩法:{},区间值:：{}", indexKey, logKey, order.getMatchId(), order.getPlayId(), JSON.toJSONString(highOddScopeBetMap));
                } else {
                    BigDecimal highOddScopeBetLimit = new BigDecimal(String.valueOf(highOddScopeBetMap.get(scope)));
                    dataMap.put("highOddScopeBetLimit", highOddScopeBetLimit.multiply(new BigDecimal(100)));
                }
            }
            //动态抽水的 允许单注最大的投注额度
            dataMap.put("dynamicLimit", rcsQuotaUserSingleNoteVo.getSinglePayLimit());
            //高赔保底投注限额
            dataMap.put("lowHighType", new BigDecimal(lowHighType));
            log.info("::{}::{}-特殊抽水动态限额结果:{}", indexKey, logKey, JSONObject.toJSONString(dataMap));
            return dataMap;
        } catch (NumberFormatException e) {
            log.error("::{}::{}-获取特殊抽水动态限额异常:", logKey, indexKey, e);
            return null;
        }
    }

    public static void main(String[] args) {
        String str = "{\"1.01-1.05\":40000,\"1.06-1.25\":40000,\"1.26-1.39\":40000,\"1.40-1.60\":56000,\"1.61-1.85\":80000,\"1.86-1.88\":80000,\"1.89-2.00\":80000}";
        List<abc> o = JsonFormatUtils.fromJsonArray(str, abc.class);

        o.forEach(k->{
            System.out.println(k);

        });





    }

    class abc {
        String a;
        String b;
    }


    /**
     * 获取单关单注限额
     *
     * @param order 订单
     * @param resVo 商户
     * @return 限额值
     * @Author beulah
     */
    public RcsQuotaUserSingleNoteVo getRcsQuotaUserSingleNoteVoNew(ExtendBean order, RcsQuotaBusinessLimitResVo resVo) {
        String indexKey = StringUtils.isBlank(order.getOrderId()) ? order.getUserId() : order.getOrderId();
        String logKey = StringUtils.isBlank(order.getOrderId()) ? "限额" : "投注";
        String sportId = order.getSportId();
        Integer tournamentLevel = convertTournamentLevel(order.getTournamentLevel());
        String matchId = order.getMatchId();
        String isScroll = order.getIsScroll();

        // 需求2607 Ln（4）：如果是篮球，且是Ln模式,那么联控玩法跟随主控玩法限额值
        String playId = order.getPlayId();
        if(order.getSportId().equals(SportIdEnum.BASKETBALL.getId().toString())) {
            String key = LimitRedisKeys.getTradingTypeStatusKey(matchId,playId,order.getItemBean().getMatchType().toString());
            log.info("::{}::LN模式下联控玩法额度跟随主控玩法KEY: {}",key);
            if(jedisClusterServer.exists(key)) {
                String lnValue = jedisClusterServer.get(key);
                if(lnValue.equals("4")) {
                    playId = LNBasktballEnum.getNameById(Integer.valueOf(playId)).toString();
                    log.info("::{}::LN模式下联控玩法额度跟随主控玩法2{}::", playId);
                }
            }
        }

        BigDecimal userQuotaBetRatio = resVo.getUserQuotaBetRatio();
        BigDecimal userQuotaRatio = resVo.getUserQuotaRatio();
        if(order.getDataSourceCode().equals(DataSourceEnum.RC.getDataSource())){//红猫赛事默认走其他
            sportId = "-1";
            playId ="-1";
        }
        // 缓存获取
        RcsQuotaUserSingleNoteVo vo = getUserSingleNoteVoFromTemplate(sportId, matchId, isScroll, playId, userQuotaBetRatio, userQuotaRatio, indexKey);
        if (vo != null) return vo;

        // 缓存没有调用rpc接口查询 获取改赛事模板或者通用模板 对应的【所有玩法】的配置
        Response<MatchLimitDataVo> response = getMatchLimitData(sportId, tournamentLevel, matchId, LimitDataTypeEnum.USER_SINGLE_BET_LIMIT);
        log.info("::{}::{}-调用rpc获取单注单关限额,返回:{}", indexKey, logKey, JSON.toJSONString(response));
        checkResponse(response, "调用rpc获取单注单关限额失败");
        List<RcsQuotaUserSingleNoteVo> list = response.getData().getRcsQuotaUserSingleNoteVoList();
        if (CollectionUtils.isEmpty(list)) {
            log.error("::{}::{}-调用rpc未获取到单注单关限额:{}", indexKey, logKey, JSON.toJSONString(order));
        } else {
            //判断数据来自【赛事模板】还是【单注单关通用模板】
            boolean isFromTournamentTemplate = list.get(0).getMatchId() != null;
            //写入缓存
            for (RcsQuotaUserSingleNoteVo config : list) {
                String betState = String.valueOf(config.getBetState());
                String configPlayId = String.valueOf(config.getPlayId());
                // 1 单注赔付限额
                BigDecimal singlePayLimit = config.getSinglePayLimit();
                if (singlePayLimit != null && singlePayLimit.compareTo(BigDecimal.ZERO) == 0) {
                    log.error("::{}::{}-单注单关出现【singlePayLimit】为0的配置,赛事ID:{},玩法ID:{}", indexKey, logKey, matchId, configPlayId);
                }
                // 2 玩法累计赔付限额
                BigDecimal playPaymentLimit = config.getCumulativeCompensationPlaying();
                // 3 单注投注限额
                BigDecimal singleBetLimit = config.getSingleBetLimit() != null ? config.getSingleBetLimit() : null;
                if (singleBetLimit != null && singleBetLimit.compareTo(BigDecimal.ZERO) == 0) {
                    log.error("::{}::{}-单注单关出现【singleBetLimit】为0的配置,赛事ID:{},玩法ID:{}", indexKey, logKey, matchId, configPlayId);
                }
                String limitKey;
                if (isFromTournamentTemplate) {
                    limitKey = LimitRedisKeys.getMatchSingleBetPlayLimitKey(Integer.parseInt(sportId), LimitDataTypeEnum.USER_SINGLE_BET_LIMIT, matchId, betState, configPlayId);
                } else {
                    limitKey = LimitRedisKeys.getCommonSingleBetPlayLimitKey(sportId, LimitDataTypeEnum.USER_SINGLE_BET_LIMIT, isScroll, playId);
                }
                //1-滚球4小时 0-早盘120小时
                int expire = "1".equals(betState) ? EXPIRE_SCROLL : EXPIRE_NO_SCROLL;
                if (singlePayLimit != null) {
                    jedisClusterServer.setex(limitKey + ":singlePay", expire, singlePayLimit.toPlainString());
                }
                if (playPaymentLimit != null) {
                    jedisClusterServer.setex(limitKey + ":playTotal", expire, playPaymentLimit.toPlainString());
                }
                if (singleBetLimit != null) {
                    jedisClusterServer.setex(limitKey + ":singleBet", expire, singleBetLimit.toPlainString());
                }
                if (configPlayId.equals(playId) && betState.equals(isScroll)) {
                    vo = config;
                }
            }
        }
        //滚球没取到 就拿早盘的配置 兼容早盘提前开滚球
        if (isScroll.equals(MATCH_LIMIT_AMOUNT_TYPE_ONE) && vo == null) {
            vo = getUserSingleNoteVoFromTemplate(sportId, matchId, "0", playId, userQuotaBetRatio, userQuotaRatio, indexKey);
            log.info("::{}::{}-本次滚球未获取到单注单关限额,读取早盘配置:{}", indexKey, logKey, JSON.toJSONString(vo));
        }
        // 对应球种没取到 则去球种默认配置
        if (vo == null && "-1".equals(order.getSportId())) {
            log.error("::{}::{}-用户单注单关-未配置用户单注单关限额：{}", indexKey, logKey, JSON.toJSONString(order));
            throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, "未配置用户单注单关限额");
        }
        if (vo == null) {
            order.setSportId("-1");
            RcsQuotaUserSingleNoteVo result = getRcsQuotaUserSingleNoteVoNew(order, resVo);
            log.info("::{}::{}-单注单关使用其他配置,sportId:-1", indexKey, logKey);
            order.setSportId(sportId);
            return result;
        }
        return vo;
    }


    /**
     * 获取赛事模板单关单注限额配置
     *
     * @param sportId           球种
     * @param matchId           赛事
     * @param isScroll          阶段
     * @param playId            玩法
     * @param userQuotaBetRatio 单注限额
     * @param userQuotaRatio    单注累计限额
     * @param indexKey          日志key
     * @return 赛事模板限额
     */
    private RcsQuotaUserSingleNoteVo getUserSingleNoteVoFromTemplate(String sportId, String matchId, String isScroll, String playId, BigDecimal userQuotaBetRatio, BigDecimal userQuotaRatio, String indexKey) {
        // 1、先获取赛种对应的联赛模板设置限额
        String limitKey = LimitRedisKeys.getMatchSingleBetPlayLimitKey(Integer.parseInt(sportId), LimitDataTypeEnum.USER_SINGLE_BET_LIMIT, matchId, isScroll, playId);
        RcsQuotaUserSingleNoteVo vo = getRcsQuotaUserSingleNoteVoFromRedis(limitKey, userQuotaBetRatio, userQuotaRatio);
        if (vo != null) {
            log.info("::{}::缓存获取到联赛模板设置限额取值:{}", indexKey, JSON.toJSONString(vo));
            return vo;
        }
        // 2、对应赛种没拿到 则去读取默认的玩法联赛模板配置
        limitKey = LimitRedisKeys.getMatchSingleBetPlayLimitKey(Integer.parseInt(sportId), LimitDataTypeEnum.USER_SINGLE_BET_LIMIT, matchId, isScroll, "-1");
        vo = getRcsQuotaUserSingleNoteVoFromRedis(limitKey, userQuotaBetRatio, userQuotaRatio);
        if (vo != null) {
            log.info("::{}::对应赛种没拿到-缓存获取到赛事模板默认限额取值:{}", indexKey, JSON.toJSONString(vo));
            return vo;
        }
        // 3、模板没有获取到 则去获取用户通用模板的配置 - 单前赛种当前玩法
        limitKey = LimitRedisKeys.getCommonSingleBetPlayLimitKey(sportId, LimitDataTypeEnum.USER_SINGLE_BET_LIMIT, isScroll, playId);
        vo = getRcsQuotaUserSingleNoteVoFromRedis(limitKey, userQuotaBetRatio, userQuotaRatio);
        if (vo != null) {
            log.info("::{}::缓存获取到通用模板限额取值:{}", indexKey, JSON.toJSONString(vo));
            return vo;
        }
        // 4、用户赛种通用模板的对应玩法配置没找到 则找当前赛种其他玩法 - 单前赛种其他玩法
        limitKey = LimitRedisKeys.getCommonSingleBetPlayLimitKey(sportId, LimitDataTypeEnum.USER_SINGLE_BET_LIMIT, isScroll, "-1");
        vo = getRcsQuotaUserSingleNoteVoFromRedis(limitKey, userQuotaBetRatio, userQuotaRatio);
        if (vo != null) {
            log.info("::{}::赛种其他玩法-缓存获取到通用模板限额取值:{}", indexKey, JSON.toJSONString(vo));
            return vo;
        }
        // 5、用户赛种通用模板的对应玩法配置没找到 则找当前赛种其他玩法 - 默认赛种当前玩法
        limitKey = LimitRedisKeys.getCommonSingleBetPlayLimitKey("-1", LimitDataTypeEnum.USER_SINGLE_BET_LIMIT, isScroll, playId);
        vo = getRcsQuotaUserSingleNoteVoFromRedis(limitKey, userQuotaBetRatio, userQuotaRatio);
        if (vo != null) {
            log.info("::{}::赛种通用模板-缓存获取到通用模板限额取值:{}", indexKey, JSON.toJSONString(vo));
            return vo;
        }
        // 6、用户赛种通用模板的对应玩法配置没找到 则找当前赛种其他玩法 - 默认赛种其他玩法
        limitKey = LimitRedisKeys.getCommonSingleBetPlayLimitKey("-1", LimitDataTypeEnum.USER_SINGLE_BET_LIMIT, isScroll, "-1");
        vo = getRcsQuotaUserSingleNoteVoFromRedis(limitKey, userQuotaBetRatio, userQuotaRatio);
        if (vo != null) {
            log.info("::{}::玩法配置没找到-缓存获取到通用模板限额取值:{}", indexKey, JSON.toJSONString(vo));
        }
        return vo;
    }


    /**
     * 缓存获取单关单注限额配置
     *
     * @param limitKey          前置key
     * @param userQuotaBetRatio 单关单注限额比例
     * @param userQuotaRatio    单关累计限额比例
     * @return 限额配置
     */
    private RcsQuotaUserSingleNoteVo getRcsQuotaUserSingleNoteVoFromRedis(String limitKey, BigDecimal userQuotaBetRatio, BigDecimal userQuotaRatio) {
        //单注赔付限额
        String singlePayLimit = RcsLocalCacheUtils.getValue(limitKey + ":singlePay", jedisClusterServer::get);
        //玩法累计赔付限额
        String playPaymentLimit = RcsLocalCacheUtils.getValue(limitKey + ":playTotal", jedisClusterServer::get);
        //玩法单注赔付限额
        String singleBetLimit = RcsLocalCacheUtils.getValue(limitKey + ":singleBet", jedisClusterServer::get);
        if (StringUtils.isNotBlank(singlePayLimit) && StringUtils.isNotBlank(playPaymentLimit) && !singlePayLimit.equals("0")) {
            RcsQuotaUserSingleNoteVo vo = new RcsQuotaUserSingleNoteVo();
            if (StringUtils.isNotBlank(singleBetLimit)) {
                vo.setSingleBetLimit(new BigDecimal(singleBetLimit).multiply(userQuotaBetRatio));
            }
            vo.setSinglePayLimit(new BigDecimal(singlePayLimit).multiply(userQuotaBetRatio));
            vo.setCumulativeCompensationPlaying(new BigDecimal(playPaymentLimit).multiply(userQuotaRatio));
            return vo;
        }
        return null;
    }


    /**
     * 获取限额百分比
     *
     * @param uid      用户id
     * @param indexKey 日志key
     * @return 用户限额百分比
     * 1、优先获取用户特殊限额百分比
     * 2、特殊百分比获取不到 则去获取用户标签百分比
     */
    public BigDecimal getUserLimitPercentage(String uid, String indexKey) {
        String key = LimitRedisKeys.getUserSpecialLimitKey(uid);
        String type = RcsLocalCacheUtils.getValue(key, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, jedisClusterServer::hget);
        String percentage = RcsLocalCacheUtils.getValue(key, LimitRedisKeys.USER_PERCENTAGE_LIMIT_FIELD, jedisClusterServer::hget);
        log.info("::{}::缓存获取到用户特殊百分比::类型:{},百分比:{}", indexKey, type, percentage);
        if (StringUtils.isNotBlank(type) && type.equals("2") && StringUtils.isNotBlank(percentage)) {
            log.info("::{}::缓存获取到用户特殊百分比【生效】::类型:{},百分比:{}", indexKey, type, percentage);
            return new BigDecimal(percentage);
        }
        //如果特殊限额取不到  则从标签去取比例    0 无  1标签限额  2特殊百分比限额 3特殊单注单场限额 4特殊vip限额
        String tagId = getUserTag(uid, indexKey);
        String tagKey = LimitRedisKeys.getUserTagLimitKey(tagId);
        //5分钟本地缓存
        percentage = RcsLocalCacheUtils.getValue(tagKey + LimitRedisKeys.USER_PERCENTAGE_LIMIT_FIELD, jedisClusterServer::get);
        if ((StringUtils.isBlank(type) || type.equals("1")) && StringUtils.isNotBlank(percentage)) {
            log.info("::{}::缓存用户标签限额【生效】::类型:{},标签:{},百分比:{}", indexKey, type, tagId, percentage);
            return new BigDecimal(percentage);
        }
        //RPC查询
        Request<Long> request = new Request<>();
        request.setData(Long.valueOf(tagId));
        request.setGlobalId(MDC.get("X-B3-TraceId"));
        Response<String> response = limitApiService.getTagPercentage(request);
        if (response == null || response.getCode() != Response.SUCCESS) {
            log.error("::{}::调用rpc获取用户标签限额返回:{}", indexKey, JSON.toJSONString(response));
            throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, "调用rpc获取用户标签限额失败");
        }
        percentage = response.getData();
        if (StringUtils.isBlank(percentage)) {
            percentage = "1";
            log.info("::{}::rpc没有获取到用户百分比取默认:{},类型:{}", indexKey, 1, type);
        } else {
            log.info("::{}::调用rpc获取用户标签限额:{}", indexKey, percentage);
        }
        jedisClusterServer.setex(tagKey + LimitRedisKeys.USER_PERCENTAGE_LIMIT_FIELD, 30 * 24 * 60 * 60, percentage);
        RcsLocalCacheUtils.timedCache.put(tagKey + LimitRedisKeys.USER_PERCENTAGE_LIMIT_FIELD, percentage);
        return new BigDecimal(percentage);
    }

    /**
     * 获取用户标签
     *
     * @param userId
     * @return
     */
    private String getUserTag(String userId, String indexKey) {
        //先从缓存查
        String tagKey = LimitRedisKeys.getTagtKey();
        String tagId = RcsLocalCacheUtils.getValue(tagKey + userId, jedisClusterServer::get);
        if (StringUtils.isNotBlank(tagId)) {
            return tagId;
        }
        //RPC查询
        Request<Long> request = new Request<>();
        request.setData(Long.valueOf(userId));
        request.setGlobalId(MDC.get("X-B3-TraceId"));
        Response<Integer> response = limitApiService.getUserTag(request);
        log.info("::{}::调用rpc获取用户标签返回:{}", indexKey, JSON.toJSONString(response));
        if (response == null || response.getCode() != Response.SUCCESS) {
            throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, "调用rpc获取取用户标失败");
        }
        tagId = response.getData().toString();
        jedisClusterServer.setex(tagKey + userId, 30 * 24 * 60 * 60, tagId);
        RcsLocalCacheUtils.timedCache.put(tagKey + userId, tagId);
        return tagId;
    }

    /**
     * 获取串关单注赔付限额
     *
     * @param order
     * @return
     * @author Paca
     */
    public BigDecimal getSeriesPaymentLimit(final ExtendBean order) {
        String indexKey = StringUtils.isBlank(order.getOrderId()) ? order.getUserId() : order.getOrderId();

        final String sportId = order.getSportId();
        final int tournamentLevel = convertTournamentLevel(order.getTournamentLevel());
        final String matchId = order.getMatchId();
        // 获取M串N中的M
        final Integer seriesType = SeriesTypeUtils.getSeriesType(order.getSeriesType());
        // 先从缓存取
        final String key = LimitRedisKeys.getLimitKey(Integer.parseInt(sportId), tournamentLevel, LimitDataTypeEnum.SERIES_PAYMENT_LIMIT);
        final String seriesPaymentField = String.format(LimitRedisKeys.SERIES_PAYMENT_FIELD, matchId, seriesType);
        String seriesPayment = jedisClusterServer.hgetNoLog(key, seriesPaymentField);
        log.info("::{}::Redis获取串关单注赔付限额：key={},field={},value={}", indexKey, key, seriesPaymentField, seriesPayment);
        // 用户单关限额比例
        //BigDecimal userQuotaRatio = getBusinessLimit(Long.valueOf(order.getBusId())).getUserQuotaRatio();
        // 用户串关限额比例
        //BigDecimal userStrayQuotaRatio= getBusinessLimit(Long.valueOf(order.getBusId())).getUserStrayQuotaRatio();
        // 限额百分比
        BigDecimal percentage = getUserLimitPercentage(order.getUserId(), indexKey);
        if (StringUtils.isNotBlank(seriesPayment)) {
            return new BigDecimal(seriesPayment).multiply(percentage);
        }
        // 缓存没有调用rpc接口查询
        Response<MatchLimitDataVo> response = getMatchLimitData(sportId, tournamentLevel, matchId, LimitDataTypeEnum.SERIES_PAYMENT_LIMIT);
        log.info("::{}::,调用rpc获取串关单注赔付限额：response={}", indexKey, JSON.toJSONString(response));
        checkResponse(response, "调用rpc获取串关单注赔付限额失败");
        Map<Integer, BigDecimal> limitMap = response.getData().getSeriesPaymentLimitMap();
        if ((limitMap == null || limitMap.size() == 0) && "-1".equals(order.getSportId())) {
            log.error("::{}::未配置串关单注赔付限额：sportId={},tournamentLevel={},matchId={},seriesType={}", indexKey, sportId, tournamentLevel, matchId, seriesType);
            throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, "未配置串关单注赔付限额");
        }
        if (limitMap == null || limitMap.size() == 0) {
            log.info("::{}::额度查询-获取串关单注赔付限额-使用其他-1配置", indexKey);
            order.setSportId("-1");
            BigDecimal result = getSeriesPaymentLimit(order);
            order.setSportId(sportId);
            return result;
        }
        Map<String, String> hash = Maps.newHashMap();
        limitMap.forEach((type, v) -> {
            String field = String.format(LimitRedisKeys.SERIES_PAYMENT_FIELD, matchId, type);
            String value = v.toPlainString();
            hash.put(field, value);
            // 将8串1的配置赋予9串1和10串1
            if (SeriesEnum.Eight.getSeriesNum().equals(type)) {
                String field9 = String.format(LimitRedisKeys.SERIES_PAYMENT_FIELD, matchId, SeriesEnum.Nine.getSeriesNum());
                String field10 = String.format(LimitRedisKeys.SERIES_PAYMENT_FIELD, matchId, SeriesEnum.Ten.getSeriesNum());
                hash.put(field9, value);
                hash.put(field10, value);
            }
        });
        Boolean exists = jedisClusterServer.exists(key);
        jedisClusterServer.hmset(key, hash);
        if (!exists) {
            jedisClusterServer.expire(key, EXPIRE);
        }


        if (!hash.containsKey(seriesPaymentField)) {
            log.error("::{}::未配置串关单注赔付限额：sportId={},tournamentLevel={},matchId={},seriesType={}", indexKey, sportId, tournamentLevel, matchId, seriesType);
            throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, "未配置串关单注赔付限额");
        }
        return new BigDecimal(hash.get(seriesPaymentField)).multiply(percentage);
    }

    public Map<Integer, BigDecimal> getSeriesRatio(Integer sportId, Integer tournamentLevel, Long matchId) {
        tournamentLevel = convertTournamentLevel(tournamentLevel);
        // 先从缓存取
        String limitKey = LimitRedisKeys.getLimitKey(sportId, tournamentLevel, LimitDataTypeEnum.SERIES_RATIO);
        List<Integer> typeList = Lists.newArrayList(2, 3, 4, 5, 6, 7, 8, 9, 10);
        String[] fields = new String[typeList.size()];
        for (int i = 0; i < typeList.size(); i++) {
            fields[i] = String.format(LimitRedisKeys.SERIES_RATIO_FIELD, matchId, typeList.get(i));
        }
        List<String> values = jedisClusterServer.hmgetNoLog(limitKey, fields);
        log.info("Redis获取各投注项计入单关限额的投注比例：key={},fields={},values={}", limitKey, fields, values);
        if (CollectionUtils.isNotEmpty(values)) {
            Map<Integer, BigDecimal> map = Maps.newHashMap();
            for (int i = 0; i < typeList.size(); i++) {
                int index = i;
                map.put(typeList.get(i), StringUtil.toBigDecimal(() -> values.get(index), BigDecimal.ONE));
            }
            return map;
        }
        // 缓存没有调用rpc接口查询
        Response<MatchLimitDataVo> response = getMatchLimitData(String.valueOf(sportId), tournamentLevel, String.valueOf(matchId), LimitDataTypeEnum.SERIES_RATIO);
        log.info("调用rpc获取各投注项计入单关限额的投注比例：response={}", JSON.toJSONString(response));
        checkResponse(response, "调用rpc获取各投注项计入单关限额的投注比例失败");
        Map<Integer, BigDecimal> seriesRatioMap = response.getData().getSeriesRatioMap();
        if ((seriesRatioMap == null || seriesRatioMap.size() == 0) && -1 == sportId) {
            log.error("未配置各投注项计入单关限额的投注比例：sportId={},tournamentLevel={},matchId={}", sportId, tournamentLevel, matchId);
            throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, "未配置各投注项计入单关限额的投注比例");
        }
        if (seriesRatioMap == null || seriesRatioMap.size() == 0) {
            return getSeriesRatio(-1, tournamentLevel, matchId);
        }
        Map<String, String> hash = Maps.newHashMap();
        seriesRatioMap.forEach((k, v) -> hash.put(String.format(LimitRedisKeys.SERIES_RATIO_FIELD, matchId, k), v.toPlainString()));
        Boolean exists = jedisClusterServer.exists(limitKey);
        jedisClusterServer.hmset(limitKey, hash);
        if (!exists) {
            jedisClusterServer.expire(limitKey, EXPIRE);
        }
        return seriesRatioMap;
    }

    /**
     * 获取计入串关已用额度的比例
     *
     * @param seriesNum M串N中的M
     * @return
     * @author Paca
     */
    public BigDecimal getSeriesUsedRatio(Integer seriesNum) {
        // 先从缓存取
        String value = jedisClusterServer.hgetNoLog(LimitRedisKeys.SERIES_USED_RATIO_KEY, seriesNum.toString());
        log.info("Redis获取计入串关已用额度的比例：key={},field={},value={}", LimitRedisKeys.SERIES_USED_RATIO_KEY, seriesNum, value);
        if (StringUtils.isNotBlank(value)) {
            return StringUtil.toBigDecimal(value, BigDecimal.ONE);
        }
        // 缓存没有调用rpc接口查询
        Response<MatchLimitDataVo> response = getMatchLimitData("0", 0, "0", LimitDataTypeEnum.SERIES_USED_RATIO);
        log.info("调用rpc获取计入串关已用额度的比例：response={}", JSON.toJSONString(response));
        checkResponse(response, "调用rpc获取计入串关已用额度的比例失败");
        Map<Integer, BigDecimal> seriesUsedRatioMap = response.getData().getSeriesUsedRatioMap();
        if (seriesUsedRatioMap == null) {
            log.error("未配置计入串关已用额度的比例");
            throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, "未配置计入串关已用额度的比例");
        }
        Map<String, String> hash = Maps.newHashMap();
        seriesUsedRatioMap.forEach((k, v) -> hash.put(k.toString(), v.toPlainString()));
        jedisClusterServer.hmset(LimitRedisKeys.SERIES_USED_RATIO_KEY, hash);
        jedisClusterServer.expire(LimitRedisKeys.SERIES_USED_RATIO_KEY, EXPIRE);
        return StringUtil.toBigDecimal(hash.get(seriesNum.toString()), BigDecimal.ONE);
    }

    /**
     * 获取最低/最高投注额限制
     *
     * @param order
     * @return
     * @author Paca
     */
    public BetAmountLimitVo getBetAmountLimit(final ExtendBean order) {
        final String sportId = order.getSportId();
        final int tournamentLevel = convertTournamentLevel(order.getTournamentLevel());
        final String matchId = order.getMatchId();
        // 先从缓存取
        final String key = LimitRedisKeys.getLimitKey(Integer.parseInt(sportId), tournamentLevel, LimitDataTypeEnum.BET_AMOUNT_LIMIT);
//        final String singleMinBetField = String.format(LimitRedisKeys.SINGLE_MIN_BET_FIELD, matchId);
//        final String seriesMinBetField = String.format(LimitRedisKeys.SERIES_MIN_BET_FIELD, matchId);
        final String seriesMaxBetRatioField = String.format(LimitRedisKeys.SERIES_MAX_BET_RATIO_FIELD, matchId);
//        String singleMinBet = jedisClusterServer.hgetNoLog(key, singleMinBetField);
//        log.info("Redis获取单关最低投注额：key={},field={},value={}", key, singleMinBetField, singleMinBet);
//        String seriesMinBet = jedisClusterServer.hgetNoLog(key, seriesMinBetField);
//        log.info("Redis获取串关最低投注额：key={},field={},value={}", key, seriesMinBetField, seriesMinBet);
        String seriesMaxBetRatio = jedisClusterServer.hgetNoLog(key, seriesMaxBetRatioField);
        log.info("Redis获取串关占单关的比例：key={},field={},value={}", key, seriesMaxBetRatioField, seriesMaxBetRatio);
        if (StringUtils.isNotBlank(seriesMaxBetRatio)) {
            BetAmountLimitVo vo = new BetAmountLimitVo();
            vo.setSingleMinBet(BigDecimal.ZERO);
            vo.setSeriesMinBet(BigDecimal.ZERO);
            vo.setSeriesMaxBetRatio(new BigDecimal(seriesMaxBetRatio));
            return vo;
        }
        // 缓存没有调用rpc接口查询
        Response<MatchLimitDataVo> response = getMatchLimitData(sportId, tournamentLevel, matchId, LimitDataTypeEnum.BET_AMOUNT_LIMIT);
        log.info("调用rpc获取最低/最高投注额限制：response={}", JSON.toJSONString(response));
        checkResponse(response, "调用rpc获取最低/最高投注额限制失败");
        BetAmountLimitVo result = response.getData().getBetAmountLimitVo();
        if (result == null) {
            log.error("未配置最低/最高投注额限制：sportId={},tournamentLevel={},matchId={}", sportId, tournamentLevel, matchId);
            throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, "未配置最低/最高投注额限制");
        }
        Map<String, String> hash = Maps.newHashMap();
//        hash.put(singleMinBetField, result.getSingleMinBet().toPlainString());
//        hash.put(seriesMinBetField, result.getSeriesMinBet().toPlainString());
        hash.put(seriesMaxBetRatioField, result.getSeriesMaxBetRatio().toPlainString());
        Boolean exists = jedisClusterServer.exists(key);
        jedisClusterServer.hmset(key, hash);
        if (!exists) {
            jedisClusterServer.expire(key, EXPIRE);
        }
        result.setSingleMinBet(BigDecimal.ZERO);
        result.setSeriesMinBet(BigDecimal.ZERO);
        return result;
    }

    /**
     * 获取最大投注和最大赔付
     *
     * @param order
     * @return
     */
    public BigDecimal getMaxBetAndMaxPaidLimit(final ExtendBean order, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit) {
        // 先从缓存获取
        String marketPlaceKey = String.format(LimitRedisKeys.MARKET_PLACE_KEY, order.getMatchId(), order.getPlayId(), order.getSubPlayId(), order.getItemBean().getPlaceNum());
        String value = jedisClusterServer.get(marketPlaceKey);
        log.info("::{}::Redis获取位置限额：key={},value={}", order.getUserId(), marketPlaceKey, value);
        // 用户单关限额比例
        BigDecimal userQuotaRatio = getBusinessLimit(Long.valueOf(order.getBusId())).getUserQuotaRatio();
        // BigDecimal userStrayQuotaRatio =getBusinessLimit(Long.valueOf(order.getBusId())).getUserStrayQuotaRatio();
        if (StringUtils.isNotEmpty(value)) {
            return new BigDecimal(value).multiply(userQuotaRatio);
        }
        // 重新查库读取(赛事配置)
        value = getMarketPlaceLimit(order);
        if (StringUtils.isNotBlank(value)) {
            return new BigDecimal(value).multiply(userQuotaRatio);
        }
        // 读取用户单注单关限额
        RcsQuotaUserSingleNoteVo rcsQuotaUserSingleNoteVo = getRcsQuotaUserSingleNoteVoNew(order, rcsQuotaBusinessLimit);
        return rcsQuotaUserSingleNoteVo.getSinglePayLimit();
    }

    private String getMarketPlaceLimit(ExtendBean order) {
        String matchId = order.getMatchId();
        //rpc获取所有正副盘的限额
        MarkerPlaceLimitAmountReqVo reqVo = new MarkerPlaceLimitAmountReqVo();
        reqVo.setSportId(Integer.valueOf(order.getSportId()));
        Integer placeNum = order.getItemBean().getPlaceNum();

        //需求2607
        Integer playId = order.getItemBean().getPlayId();
        String subPlayId = order.getItemBean().getSubPlayId();
        if(order.getSportId().equals(SportIdEnum.BASKETBALL.getId().toString())) {
            String key = LimitRedisKeys.getTradingTypeStatusKey(order.getMatchId(),playId.toString(),order.getItemBean().getMatchType().toString());
            log.info("::{}::读取调价窗口查库,LN模式下联控玩法额度跟随主控玩法KEY: {}",key);
            if(jedisClusterServer.exists(key)) {
                String lnValue = jedisClusterServer.get(key);
                if(lnValue.equals("4")) {
                    playId = LNBasktballEnum.getNameById(playId);
                    subPlayId = playId.toString();
                    log.info("::{}::读取调价窗口查库,LN模式下联控玩法额度跟随主控玩法{}::", playId);
                }
            }
        }


        reqVo.setPlaceNum(order.getItemBean().getPlaceNum());
        reqVo.setMatchId(Long.valueOf(matchId));
        reqVo.setPlayId(playId);
        reqVo.setSubPlayId(subPlayId);
        Request<MarkerPlaceLimitAmountReqVo> request = new Request<>();
        request.setData(reqVo);
        request.setGlobalId(MDC.get("X-B3-TraceId"));
        Response<MarkerPlaceLimitAmountResVo> response = limitApiService.getMarketPlaceLimit(request);
        String indexKey = org.springframework.util.StringUtils.isEmpty(order.getOrderId()) ? order.getUserId() : order.getOrderId();
        String logKey = org.springframework.util.StringUtils.isEmpty(order.getOrderId()) ? "限额" : "投注";
        log.info("::{}::{}调用rpc获取盘口位置限额:{}", indexKey, logKey, JSON.toJSONString(response));
        if (response == null || response.getCode() != Response.SUCCESS) {
            throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, "调用rpc获取盘口位置限额失败");
        }
        // 缓存处理
        MarkerPlaceLimitAmountResVo markerPlaceLimitAmountResVo = response.getData();
        if (Objects.isNull(markerPlaceLimitAmountResVo) || markerPlaceLimitAmountResVo.getLimitAmount() == null) {
            log.warn("::{}::{}调用rpc未获取到盘口位置限额", indexKey, logKey);
            return null;
        }
        String key = String.format(LimitRedisKeys.MARKET_PLACE_KEY, matchId, playId, subPlayId, placeNum);
        jedisClusterServer.setex(key, 5 * 24 * 60 * 60, String.valueOf(markerPlaceLimitAmountResVo.getLimitAmount()));

        // 返回当前要查的数据
        return String.valueOf(markerPlaceLimitAmountResVo.getLimitAmount());
    }

    private Response<MatchLimitDataVo> getMatchLimitData(final String sportId, final Integer tournamentLevel, final String matchId, final LimitDataTypeEnum limitDataTypeEnum) {
        MatchLimitDataReqVo reqVo = new MatchLimitDataReqVo();
        reqVo.setSportId(Integer.valueOf(sportId));
        reqVo.setTournamentLevel(convertTournamentLevel(tournamentLevel));
        reqVo.setMatchId(Long.valueOf(matchId));
        reqVo.setDataTypeList(Lists.newArrayList(limitDataTypeEnum.getType()));
        Request<MatchLimitDataReqVo> request = new Request<>();
        request.setData(reqVo);
        request.setGlobalId(MDC.get("X-B3-TraceId"));
        return limitApiService.getMatchLimitData(request);
    }

    private void checkResponse(Response response, String msg) {
        if (response == null || response.getCode() != Response.SUCCESS || response.getData() == null) {
            throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, msg);
        }
    }

    private int convertTournamentLevel(Integer tournamentLevel) {
        if (tournamentLevel == null || tournamentLevel <= 0) {
            // -1表示未评级
            return -1;
        }
        return tournamentLevel;
    }

    /**
     * 检查用户特殊限额缓存兜底
     *
     * @param userId
     */
    public void setUserSpecialBetLimitConfigCache(Long userId) {
        //缓存查询
        String key = LimitRedisKeys.getUserSpecialLimitKey(userId.toString());
        String type = RcsLocalCacheUtils.getValue(key, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, jedisClusterServer::hget);
        //脏数据标识
        String errorMark = RcsLocalCacheUtils.getValue(key, "errorMark", jedisClusterServer::hget);
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(errorMark)) {
            return;
        }
        //组装参数请求rpc
        Request<RcsUserSpecialBetLimitConfigDTO> request = new Request<>();
        RcsUserSpecialBetLimitConfigDTO config = new RcsUserSpecialBetLimitConfigDTO();
        config.setUserId(userId);
        config.setStatus(1);
        request.setData(config);
        request.setGlobalId(MDC.get("X-B3-TraceId"));
        Response<List<RcsUserSpecialBetLimitConfigDTO>> response;
        try {
            response = limitApiService.queryUserSpecialBetLimitConfig(request);
            log.info("::{}::rpc获取特殊限额数据返回:{}", userId, JSONObject.toJSONString(response));
            if (response == null || response.getCode() != Response.SUCCESS) {
                log.warn("::{}::rpc获取特殊限额数据失败", userId);
            } else {
                if (response.getData() != null && response.getData().size() > 0) {
                    List<RcsUserSpecialBetLimitConfigDTO> list = response.getData();
                    //设置type，一个用户只能是一种类型，所以有多条数据类型也是一致
                    type = list.get(0).getSpecialBettingLimitType().toString();
                    Map<String, String> data = new HashMap<>();
                    data.put(LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, type);
                    data.put("errorMark", "1");
                    for (RcsUserSpecialBetLimitConfigDTO limitConfig : list) {
                        if (type.equals("2")) {
                            //限额百分比，放入缓存
                            String percentage = LimitRedisKeys.USER_PERCENTAGE_LIMIT_FIELD;
                            data.put(percentage, String.valueOf(limitConfig.getPercentageLimit().doubleValue()));
                        } else {
                            //特殊单注单场限额、特殊VIP限额 放入缓存
                            if (limitConfig.getSingleNoteClaimLimit() != null) {
                                //单注赔付限额
                                String Hkey2 = "%s_%s_single_note_claim_limit";
                                data.put(String.format(Hkey2, limitConfig.getOrderType(),
                                        limitConfig.getSportId()), String.valueOf(limitConfig.getSingleNoteClaimLimit() * 100));
                            }
                            if (limitConfig.getSingleGameClaimLimit() != null) {
                                //单场赔付限额
                                String Hkey3 = "%s_%s_single_game_claim_limit";
                                data.put(String.format(Hkey3, limitConfig.getOrderType(),
                                        limitConfig.getSportId()), String.valueOf(limitConfig.getSingleGameClaimLimit() * 100));
                            }
                        }
                    }
                    jedisClusterServer.hmset(key, data);
                    jedisClusterServer.expire(key, 30 * 24 * 60 * 60);
                    data.forEach((k, v) -> RcsLocalCacheUtils.timedCache.put(key + k, v));
                }
            }
        } catch (Exception e) {
            log.error("::{}::设置用户特殊限额缓存异常:", userId, e);
        }

    }


    /**
     * 获取用户特殊限额配置
     *
     * @param order 订单
     * @return 用户特殊限额配置
     * @Author beulah
     */
    public BigDecimal getSpecialUserBetAmount(ExtendBean order) {
        String indexKey = StringUtils.isBlank(order.getOrderId()) ? order.getUserId() : order.getOrderId();
        String logKey = StringUtils.isBlank(order.getOrderId()) ? "限额" : "投注";
        String key = LimitRedisKeys.getUserSpecialLimitKey(order.getUserId());
        String type = RcsLocalCacheUtils.getValue(key, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, jedisClusterServer::hget);
        if (StringUtils.isBlank(type) || (!type.equals(UserSpecialLimitType.SINGLE.getType()) && !type.equals(UserSpecialLimitType.VIP.getType()))) {
            log.info("::{}::{} - 【非】特殊会员类型:{},默认返回:{} ", indexKey, logKey, type, Long.MAX_VALUE);
            return new BigDecimal(Long.MAX_VALUE);
        }
        //先查全部的配置
        String specialUserAmountKey = "1_-1_single_note_claim_limit";
        String specialUserAmountStr = RcsLocalCacheUtils.getValue(LimitRedisKeys.getUserSpecialLimitKey(order.getUserId()), specialUserAmountKey, jedisClusterServer::hget);

        log.info("::{}::{} - 特殊会员取【全部】单注赔付:{},key：{}", indexKey, logKey, specialUserAmountStr, specialUserAmountKey);
        if (StringUtils.isNotBlank(specialUserAmountStr) && !specialUserAmountStr.equals("null")) {
            return new BigDecimal(specialUserAmountStr);
        }
        //再查赛种的配置
        specialUserAmountKey = "1_" + order.getSportId() + "_single_note_claim_limit";
        specialUserAmountStr = RcsLocalCacheUtils.getValue(LimitRedisKeys.getUserSpecialLimitKey(order.getUserId()), specialUserAmountKey, jedisClusterServer::hget);
        log.info("::{}::{} - 特殊会员取【赛种】单注赔付:{},key：{}", indexKey, logKey, specialUserAmountStr, specialUserAmountKey);
        if (StringUtils.isNotBlank(specialUserAmountStr) && !specialUserAmountStr.equals("null")) {
            return new BigDecimal(specialUserAmountStr);
        }
        //优化单43403 如果特殊限额类型不为空,限额数据为空则走一遍兜底
        String userSpecialBetLimitKey = String.format("risk:trade:rcs_user_special_bet_limit_config:%s", order.getUserId());
        jedisClusterServer.hdel(userSpecialBetLimitKey, "errorMark");
        setUserSpecialBetLimitConfigCache(Long.valueOf(order.getUserId()));
        specialUserAmountStr = RcsLocalCacheUtils.getValue(LimitRedisKeys.getUserSpecialLimitKey(order.getUserId()), specialUserAmountKey, jedisClusterServer::hget);
        if (StringUtils.isNotBlank(specialUserAmountStr) && !specialUserAmountStr.equals("null")) {
            return new BigDecimal(specialUserAmountStr);
        }
        //足/篮 不再往下读取其他配置
        if (order.getSportId().equals("1") || order.getSportId().equals("2")) {
            log.info("::{}::{} - 特殊会员,足/篮/网球不再往下读取其他配置,默认返回:{}", indexKey, logKey, Long.MAX_VALUE);
            return new BigDecimal(Long.MAX_VALUE);
        }
        //再查其他赛种的配置
        specialUserAmountKey = "1_0_single_note_claim_limit";
        specialUserAmountStr = RcsLocalCacheUtils.getValue(LimitRedisKeys.getUserSpecialLimitKey(order.getUserId()), specialUserAmountKey, jedisClusterServer::hget);
        log.info("::{}::{} - 特殊会员取【其他】单注赔付:{},key:{}", indexKey, logKey, specialUserAmountStr, specialUserAmountKey);
        if (StringUtils.isNotBlank(specialUserAmountStr) && !specialUserAmountStr.equals("null")) {
            return new BigDecimal(specialUserAmountStr);
        }
        log.info("::{}::{} - 特殊会员,没有配置....默认返回:{}", indexKey, logKey, Long.MAX_VALUE);
        return new BigDecimal(Long.MAX_VALUE);
    }


    /**
     * 获取用户单场限额
     *
     * @param order
     * @return
     */
    public RcsQuotaUserSingleSiteQuotaVo getRcsQuotaUserSingleSiteQuotaData(ExtendBean order) {
        String indexKey = StringUtils.isBlank(order.getOrderId()) ? order.getUserId() : order.getOrderId();

        RcsQuotaUserSingleSiteQuotaVo vo = new RcsQuotaUserSingleSiteQuotaVo();

        String limitKey = LimitRedisKeys.getLimitKey(Integer.valueOf(order.getSportId()), order.getTournamentLevel(), LimitDataTypeEnum.USER_SINGLE_LIMIT);
        String singelField = String.format(LimitRedisKeys.SINGLE_USER_EARLY_PAYMENT_FIELD, order.getMatchId());
        String earlyUserSingleSiteQuota = jedisClusterServer.hget(limitKey, singelField);

        String liveField = String.format(LimitRedisKeys.SINGLE_USER_LIVE_PAYMENT_FIELD, order.getMatchId());
        String liveUserSingleSiteQuota = jedisClusterServer.hget(limitKey, liveField);

        // 用户限额比例
        BigDecimal userQuotaRatio = getBusinessLimit(Long.valueOf(order.getBusId())).getUserQuotaRatio();
        //特殊用户百分比
        BigDecimal percentage = getUserLimitPercentage(order.getUserId(), indexKey);
        if (StringUtils.isBlank(earlyUserSingleSiteQuota) || StringUtils.isBlank(liveUserSingleSiteQuota)) {
            // 没读到缓存  调用接口查询
            Response<MatchLimitDataVo> response = getMatchLimitData(order.getSportId(), order.getTournamentLevel(), order.getMatchId(), LimitDataTypeEnum.USER_SINGLE_LIMIT);
            log.info("::{}::额度查询-调用rpc获取用户单场限额：response={}", indexKey, JSON.toJSONString(response));
            RcsQuotaUserSingleSiteQuotaVo singleSiteQuotaVo = null;
            if (response != null && response.getData() != null && response.getCode() == Response.SUCCESS) {
                singleSiteQuotaVo = response.getData().getRcsQuotaUserSingleSiteQuotaVo();
            }
            if (singleSiteQuotaVo == null && "-1".equals(order.getSportId())) {
                throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, "读取用户单场限额数据异常");
            }
            if (singleSiteQuotaVo == null) {
                log.info("::{}::额度查询-用户单场限额-使用其他-1配置", indexKey);
                String sportId = order.getSportId();
                order.setSportId("-1");
                RcsQuotaUserSingleSiteQuotaVo result = getRcsQuotaUserSingleSiteQuotaData(order);
                order.setSportId(sportId);
                return result;
            }
            BigDecimal earlyLimit = singleSiteQuotaVo.getEarlyUserSingleSiteQuota();
            BigDecimal liveLimit = singleSiteQuotaVo.getLiveUserSingleSiteQuota();

            Boolean exists = jedisClusterServer.exists(limitKey);
            //设置缓存
            jedisClusterServer.hset(limitKey, String.format(LimitRedisKeys.SINGLE_USER_EARLY_PAYMENT_FIELD, order.getMatchId()), earlyLimit.toString());
            jedisClusterServer.hset(limitKey, String.format(LimitRedisKeys.SINGLE_USER_LIVE_PAYMENT_FIELD, order.getMatchId()), liveLimit.toString());
            if (!exists) {
                jedisClusterServer.expire(limitKey, 2 * 60 * 60);
            }

            vo.setEarlyUserSingleSiteQuota(earlyLimit.multiply(userQuotaRatio).multiply(percentage));
            vo.setLiveUserSingleSiteQuota(liveLimit.multiply(userQuotaRatio).multiply(percentage));
            vo.setMatchId(singleSiteQuotaVo.getMatchId());

            log.info("::{}::额度查询-获取 用户单场限额rpc处理完成:{}", indexKey, JSON.toJSONString(singleSiteQuotaVo));
            return vo;
        }

        vo.setEarlyUserSingleSiteQuota(new BigDecimal(earlyUserSingleSiteQuota).multiply(userQuotaRatio).multiply(percentage));
        vo.setLiveUserSingleSiteQuota(new BigDecimal(liveUserSingleSiteQuota).multiply(userQuotaRatio).multiply(percentage));
        log.info("::{}::额度查询-用户单注单关限额:{}", indexKey, JSON.toJSONString(vo));
        return vo;
    }

}
