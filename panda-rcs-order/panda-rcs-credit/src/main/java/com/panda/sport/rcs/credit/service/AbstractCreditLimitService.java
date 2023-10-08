package com.panda.sport.rcs.credit.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSeriesLimit;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSingleMatchLimit;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSinglePlayLimit;
import com.panda.sport.rcs.cache.redis.RedisUtils;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.credit.constants.CreditRedisKey;
import com.panda.sport.rcs.credit.constants.ErrorCode;
import com.panda.sport.rcs.credit.enums.PlayClassify;
import com.panda.sport.rcs.credit.utils.CreditBizUtils;
import com.panda.sport.rcs.enums.Constants;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.enums.RedisCmdEnum;
import com.panda.sport.rcs.exeception.LogicException;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.pojo.credit.RcsCreditSinglePlayBetLimit;
import com.panda.sport.rcs.pojo.limit.RedisUpdateVo;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.vo.RcsBusinessPlayPaidConfigVo;
import com.panda.sport.rcs.wrapper.credit.RcsCreditSeriesLimitService;
import com.panda.sport.rcs.wrapper.credit.RcsCreditSingleMatchLimitService;
import com.panda.sport.rcs.wrapper.credit.RcsCreditSinglePlayBetLimitService;
import com.panda.sport.rcs.wrapper.credit.RcsCreditSinglePlayLimitService;
import com.panda.sport.rcs.wrapper.limit.RcsQuotaBusinessLimitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用限额服务
 * @Author : Paca
 * @Date : 2021-05-05 19:52
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
public abstract class AbstractCreditLimitService extends AbstractLimitService {

    @Autowired
    private RcsCreditSeriesLimitService rcsCreditSeriesLimitService;
    @Autowired
    private RcsCreditSingleMatchLimitService rcsCreditSingleMatchLimitService;
    @Autowired
    private RcsCreditSinglePlayBetLimitService rcsCreditSinglePlayBetLimitService;
    @Autowired
    private RcsCreditSinglePlayLimitService rcsCreditSinglePlayLimitService;

    @Autowired
    private RcsQuotaBusinessLimitService rcsQuotaBusinessLimitService;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public List<RcsBusinessPlayPaidConfigVo> queryBetLimit(OrderBean orderBean) {
        queryCheckOrderBean(orderBean);
        for (OrderItem orderItem : orderBean.getItems()) {
            queryCheckOrderItem(orderItem);
        }
        // 最高可投小于最低可投时，最高可投统一设置为0
        List<RcsBusinessPlayPaidConfigVo> list = queryBetLimit(orderBean, orderBean.getItems());
        for (RcsBusinessPlayPaidConfigVo vo : list) {
            if (vo.getOrderMaxPay() < vo.getMinBet()) {
                vo.setOrderMaxPay(0L);
            }
        }
        return list;
    }

    protected abstract List<RcsBusinessPlayPaidConfigVo> queryBetLimit(OrderBean orderBean, List<OrderItem> orderItems);

    @Override
    public Map<String, Object> checkOrder(OrderBean orderBean) {
        orderCheckOrderBean(orderBean);
        for (OrderItem orderItem : orderBean.getItems()) {
            orderCheckOrderItem(orderItem);
        }
        List<RedisUpdateVo> redisUpdateList = Lists.newArrayList();
        Map<String, Object> resultMap = checkOrder(orderBean, redisUpdateList);
        if ("0".equals(String.valueOf(resultMap.get("infoCode")))) {
            String key = String.format(CreditRedisKey.LIMIT_REDIS_UPDATE_RECORD_KEY, orderBean.getOrderNo());
            redisUtils.set(key, JSON.toJSONString(redisUpdateList));
            redisUtils.expire(key, 30L, TimeUnit.DAYS);
        }
        return resultMap;
    }

    protected abstract Map<String, Object> checkOrder(OrderBean orderBean, List<RedisUpdateVo> redisUpdateList);

    protected BigDecimal getOdds(OrderItem orderItem) {
        Double oddsValue = orderItem.getOddsValue();
        if (oddsValue == null) {
            return BigDecimal.ONE;
        }
        return new BigDecimal(oddsValue.toString()).divide(new BigDecimal(BaseConstants.MULTIPLE_VALUE), 2, RoundingMode.DOWN);
    }

    protected BigDecimal getHighOdds() {
        String key = getHighOddsKey();
        String value = redisUtils.get(key);
        return CommonUtils.toBigDecimal(value, new BigDecimal(2));
    }

    protected BigDecimal convertEuOdds(BigDecimal euOdds, BigDecimal highOdds) {
        // 大于高赔使用原始赔率计算，否则使用赔率2计算
        return euOdds.compareTo(highOdds) > 0 ? euOdds : new BigDecimal(2);
    }

    protected long getBetPayment(long betAmount, BigDecimal euOdds, BigDecimal highOdds) {
        // 大于高赔取赔付，否则取投注
        return new BigDecimal(betAmount).multiply(convertEuOdds(euOdds, highOdds).subtract(BigDecimal.ONE)).longValue();
    }

    /**
     * 获取高赔配置缓存key
     *
     * @return
     */
    protected abstract String getHighOddsKey();

    protected void redisCallback(List<RedisUpdateVo> redisUpdateList) {
        if (CollectionUtils.isEmpty(redisUpdateList)) {
            return;
        }
        redisUpdateList.forEach(vo -> {
            BigDecimal value = CommonUtils.toBigDecimal(vo.getValue()).negate();
            exeIncrByCmd(vo.getCmd(), vo.getKey(), vo.getField(), value);
        });
    }

    private void exeIncrByCmd(String cmd, String key, String field, BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        if (RedisCmdEnum.isIncrBy(cmd)) {
            redisUtils.incrBy(key, value.longValue());
        } else if (RedisCmdEnum.isIncrByFloat(cmd)) {
            redisUtils.incrByFloat(key, value.doubleValue());
        } else if (RedisCmdEnum.isHincrBy(cmd)) {
            redisUtils.hincrBy(key, field, value.longValue());
        } else if (RedisCmdEnum.isHincrByFloat(cmd)) {
            redisUtils.hincrByFloat(key, field, value.doubleValue());
        }
        redisUtils.expire(key, 90L, TimeUnit.DAYS);
    }

    /**
     * 获取代理串关限额，单位分
     *
     * @param tenantId
     * @param creditAgentId
     * @param seriesNum
     * @return
     */
    protected long getAgentSeriesLimit(Long tenantId, String creditAgentId, int seriesNum) {
        String field = String.valueOf(seriesNum);
        // 获取代理串关限额
        Map<String, String> map = getAgentSeriesLimit(tenantId, creditAgentId);
        if (CollectionUtils.isNotEmpty(map)) {
            return CommonUtils.toBigDecimal(map.get(field)).multiply(RcsConstant.HUNDRED).longValue();
        }
        // 获取代理串关限额默认值
        map = getAgentSeriesLimit(0L, "-1");
        if (CollectionUtils.isNotEmpty(map)) {
            return CommonUtils.toBigDecimal(map.get(field)).multiply(RcsConstant.HUNDRED).longValue();
        }
        return 0L;
    }

    protected Map<String, String> getAgentSeriesLimit(Long tenantId, String creditAgentId) {
        String target = getAgentLimitTarget(creditAgentId);
        String key = CreditRedisKey.Limit.getSeriesKey(tenantId, creditAgentId);
        Map<String, String> hashMap = redisUtils.hgetAll(key);
        log.info("信用额度，获取代理串关限额->{}：key={},hashMap={}", target, key, JSON.toJSONString(hashMap));
        if ("-1".equals(creditAgentId) && CollectionUtils.isEmpty(hashMap)) {
            // 缓存中没有默认值，查询数据库
            List<RcsCreditSeriesLimit> list = rcsCreditSeriesLimitService.querySeriesLimit(tenantId, creditAgentId, 0L);
            log.info("信用额度，查询代理串关限额->{}：list={}", target, JSON.toJSONString(list));
            if (CollectionUtils.isNotEmpty(list)) {
                hashMap = list.stream().collect(Collectors.toMap(config -> String.valueOf(config.getSeriesType()), config -> CommonUtils.toString(config.getValue())));
                redisUtils.hmset(key, hashMap);
                log.info("信用额度，缓存代理串关限额->{}：key={},hashMap={}", target, key, JSON.toJSONString(hashMap));
            }
        }
        return hashMap;
    }

    /**
     * 获取代理串关已用限额，单位分
     *
     * @param currentDateExpect
     * @param tenantId
     * @param creditAgentId
     * @param seriesNum
     * @return
     */
    protected long getAgentSeriesUsed(String currentDateExpect, Long tenantId, String creditAgentId, int seriesNum) {
        String key = CreditRedisKey.Used.getSeriesKey(currentDateExpect, tenantId, creditAgentId);
        String field = String.valueOf(seriesNum);
        String value = redisUtils.hget(key, field);
        log.info("信用额度，获取代理串关已用限额：key={},field={},value={}", key, field, value);
        return NumberUtils.toLong(value);
    }

    /**
     * 累加代理串关已用限额，单位分
     *
     * @param currentDateExpect
     * @param tenantId
     * @param creditAgentId
     * @param seriesNum
     * @param value
     * @param redisUpdateList
     * @return
     */
    protected long incrAgentSeriesUsed(String currentDateExpect, Long tenantId, String creditAgentId, int seriesNum, long value, List<RedisUpdateVo> redisUpdateList) {
        String key = CreditRedisKey.Used.getSeriesKey(currentDateExpect, tenantId, creditAgentId);
        String field = String.valueOf(seriesNum);
        long used = redisUtils.hincrBy(key, field, value);
        log.info("信用额度，累加代理串关已用限额：key={},field={},value={},used={}", key, field, value, used);
        redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBY.getCmd(), key, field, String.valueOf(value), String.valueOf(used)));
        redisUtils.expire(key, 90L, TimeUnit.DAYS);
        return used;
    }

    /**
     * 获取用户串关限额，单位分
     *
     * @param tenantId
     * @param creditAgentId
     * @param userId
     * @param seriesNum
     * @return
     */
    protected long getUserSeriesLimit(Long tenantId, String creditAgentId, Long userId, int seriesNum) {
        String field = String.valueOf(seriesNum);
        // 获取用户串关限额
        Map<String, String> map = getUserSeriesLimit(tenantId, creditAgentId, userId);
        if (CollectionUtils.isNotEmpty(map)) {
            return CommonUtils.toBigDecimal(map.get(field)).multiply(RcsConstant.HUNDRED).longValue();
        }
        // 获取用户串关通用限额
        map = getUserSeriesLimit(tenantId, creditAgentId, -1L);
        if (CollectionUtils.isNotEmpty(map)) {
            return CommonUtils.toBigDecimal(map.get(field)).multiply(RcsConstant.HUNDRED).longValue();
        }
        // 获取用户串关通用限额默认值
        map = getUserSeriesLimit(0L, "-1", -1L);
        if (CollectionUtils.isNotEmpty(map)) {
            return CommonUtils.toBigDecimal(map.get(field)).multiply(RcsConstant.HUNDRED).longValue();
        }
        return 0L;
    }

    protected Map<String, String> getUserSeriesLimit(Long tenantId, String creditAgentId, Long userId) {
        String target = getUserLimitTarget(creditAgentId, userId);
        String key = CreditRedisKey.Limit.getUserSeriesKey(tenantId, creditAgentId, userId);
        Map<String, String> hashMap = redisUtils.hgetAll(key);
        log.info("信用额度，获取代理串关限额->{}：key={},hashMap={}", target, key, JSON.toJSONString(hashMap));
        if ("-1".equals(creditAgentId) && CollectionUtils.isEmpty(hashMap)) {
            // 缓存中没有默认值，查询数据库
            List<RcsCreditSeriesLimit> list = rcsCreditSeriesLimitService.querySeriesLimit(tenantId, creditAgentId, userId);
            log.info("信用额度，查询代理串关限额->{}：list={}", target, JSON.toJSONString(list));
            if (CollectionUtils.isNotEmpty(list)) {
                hashMap = list.stream().collect(Collectors.toMap(config -> String.valueOf(config.getSeriesType()), config -> CommonUtils.toString(config.getValue())));
                redisUtils.hmset(key, hashMap);
                log.info("信用额度，缓存代理串关限额->{}：key={},hashMap={}", target, key, JSON.toJSONString(hashMap));
            }
        }
        return hashMap;
    }

    /**
     * 获取用户串关已用限额，单位分
     *
     * @param currentDateExpect
     * @param userId
     * @param seriesNum
     * @return
     */
    protected long getUserSeriesUsed(String currentDateExpect, Long userId, int seriesNum) {
        String key = CreditRedisKey.Used.getUserSeriesKey(currentDateExpect, userId);
        String field = String.valueOf(seriesNum);
        String value = redisUtils.hget(key, field);
        log.info("信用额度，获取用户串关已用限额：key={},field={},value={}", key, field, value);
        return NumberUtils.toLong(value);
    }

    /**
     * 累加用户串关已用限额，单位分
     *
     * @param currentDateExpect
     * @param userId
     * @param seriesNum
     * @param value
     * @param redisUpdateList
     * @return
     */
    protected long incrUserSeriesUsed(String currentDateExpect, Long userId, int seriesNum, long value, List<RedisUpdateVo> redisUpdateList) {
        String key = CreditRedisKey.Used.getUserSeriesKey(currentDateExpect, userId);
        String field = String.valueOf(seriesNum);
        long used = redisUtils.hincrBy(key, field, value);
        log.info("信用额度，累加用户串关已用限额：key={},field={},value={},used={}", key, field, value, used);
        redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBY.getCmd(), key, field, String.valueOf(value), String.valueOf(used)));
        redisUtils.expire(key, 90L, TimeUnit.DAYS);
        return used;
    }

    /**
     * 获取代理单场赛事限额，单位分
     *
     * @param tenantId
     * @param creditAgentId
     * @param sportId
     * @param tournamentLevel
     * @return
     */
    protected long getAgentSingleMatchLimit(Long tenantId, String creditAgentId, Integer sportId, Integer tournamentLevel) {
        sportId = CreditBizUtils.creditSportId(sportId);
        tournamentLevel = CreditBizUtils.creditTournamentLevel(tournamentLevel);
        log.info("信用额度，获取限额,代理单场赛事限额：商户：{}，代理：{}，赛种：{}，联赛等级：{}", tenantId, creditAgentId, sportId, tournamentLevel);

        // 获取代理单场赛事限额
        BigDecimal value = getAgentMatchLimit(tenantId, creditAgentId, sportId, tournamentLevel);
        if (value != null) {
            log.info("信用额度，获取限额,代理单场赛事限额：{}", value);
            return value.multiply(RcsConstant.HUNDRED).longValue();
        }
        // 获取代理单场赛事通用限额配置值
        value = getAgentMatchLimit(0L, "0", sportId, tournamentLevel);
        if (value != null) {
            log.info("信用额度，获取限额,代理单场赛事通用限额：{}", value);
            return value.multiply(RcsConstant.HUNDRED).longValue();
        }
        // 获取代理单场赛事限额默认值
        value = getAgentMatchLimit(0L, "-1", sportId, tournamentLevel);
        if (value != null) {
            log.info("信用额度，获取限额,代理单场赛事默认限额：{}", value);
            return value.multiply(RcsConstant.HUNDRED).longValue();
        }
        return 0L;
    }

    private BigDecimal getAgentMatchLimit(Long tenantId, String creditAgentId, Integer sportId, Integer tournamentLevel) {
        String target = getAgentLimitTarget(creditAgentId);
        String key = CreditRedisKey.Limit.getSingleMatchKey(tenantId, creditAgentId, sportId);
        String field = String.valueOf(tournamentLevel);
        String value = redisUtils.hget(key, field);
        log.info("信用额度，获取限额,获取代理单场赛事限额->{}：key={},field={},value={}", target, key, field, value);
        if (("-1".equals(creditAgentId) || "0".equals(creditAgentId)) && StringUtils.isBlank(value)) {
            // 缓存中没有默认值，查询数据库
            List<RcsCreditSingleMatchLimit> list = rcsCreditSingleMatchLimitService.querySingleMatchLimit(tenantId, creditAgentId, sportId);
            log.info("信用额度，获取限额,查询代理单场赛事限额->{}：list={}", target, JSON.toJSONString(list));
            if (CollectionUtils.isNotEmpty(list)) {
                Map<String, String> hashMap = list.stream().collect(Collectors.toMap(config -> String.valueOf(config.getTournamentLevel()), config -> CommonUtils.toString(config.getValue())));
                redisUtils.hmset(key, hashMap);
                log.info("信用额度，获取限额,缓存代理单场赛事限额->{}：key={},hashMap={}", target, key, JSON.toJSONString(hashMap));
                value = hashMap.get(field);
            }
        }
        if (StringUtils.isNotBlank(value)) {
            return CommonUtils.toBigDecimal(value);
        }
        return null;
    }

    /**
     * 获取代理单场赛事已用限额，单位分
     *
     * @param dateExpect
     * @param tenantId
     * @param creditAgentId
     * @param matchId
     * @return
     */
    protected long getAgentSingleMatchUsed(String dateExpect, Long tenantId, String creditAgentId, Long matchId) {
        String key = CreditRedisKey.Used.getSingleMatchKey(dateExpect, tenantId, creditAgentId, matchId);
        String value = redisUtils.get(key);
        log.info("信用额度，获取代理单场赛事已用限额：key={},value={}", key, value);
        return NumberUtils.toLong(value);
    }

    /**
     * 累加代理单场赛事已用限额，单位分
     *
     * @param dateExpect
     * @param tenantId
     * @param creditAgentId
     * @param matchId
     * @param value
     * @param redisUpdateList
     * @return
     */
    protected long incrAgentSingleMatchUsed(String dateExpect, Long tenantId, String creditAgentId, Long matchId, long value, List<RedisUpdateVo> redisUpdateList) {
        String key = CreditRedisKey.Used.getSingleMatchKey(dateExpect, tenantId, creditAgentId, matchId);
        long used = redisUtils.incrBy(key, value);
        log.info("信用额度，累加代理单场赛事已用限额：key={},value={},used={}", key, value, used);
        redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.INCRBY.getCmd(), key, null, String.valueOf(value), String.valueOf(used)));
        redisUtils.expire(key, 90L, TimeUnit.DAYS);
        return used;
    }

    /**
     * 获取代理玩法累计限额，单位分
     *
     * @param tenantId
     * @param creditAgentId
     * @param sportId
     * @param tournamentLevel
     * @param playId
     * @param matchType
     * @return
     */
    protected long getAgentSinglePlayLimit(Long tenantId, String creditAgentId, Integer sportId, Integer tournamentLevel, Integer playId, Integer matchType) {
        sportId = CreditBizUtils.creditSportId(sportId);
        tournamentLevel = CreditBizUtils.creditTournamentLevel(tournamentLevel);
        Integer playClassify = PlayClassify.getPlayClassify(sportId, playId);
        String betStage = CreditBizUtils.convertBetStage(matchType);
        log.info("信用额度，获取限额,代理玩法累计限额：商户：{}，代理：{}，赛种：{}，联赛等级：{}，玩法：{}，赛事类型：{}", tenantId, creditAgentId, sportId, tournamentLevel, playClassify, betStage);
        // 获取代理玩法累计限额
        BigDecimal value = getAgentSinglePlayLimit(tenantId, creditAgentId, sportId, tournamentLevel, playClassify, betStage);
        if (value != null) {
            log.info("信用额度，获取限额,代理玩法累计限额：{}", value);
            return value.multiply(RcsConstant.HUNDRED).longValue();
        }
        // 获取代理玩法累计限额默认值
        value = getAgentSinglePlayLimit(0L, "-1", sportId, tournamentLevel, playClassify, betStage);
        if (value != null) {
            log.info("信用额度，获取限额,代理玩法累计默认限额：{}", value);
            return value.multiply(RcsConstant.HUNDRED).longValue();
        }
        return 0L;
    }

    private BigDecimal getAgentSinglePlayLimit(Long tenantId, String creditAgentId, Integer sportId, Integer tournamentLevel, Integer playClassify, String betStage) {
        String target = getAgentLimitTarget(creditAgentId);
        String key = CreditRedisKey.Limit.getSinglePlayKey(tenantId, creditAgentId, sportId, playClassify, betStage);
        String field = String.valueOf(tournamentLevel);
        String value = redisUtils.hget(key, field);
        log.info("信用额度，获取代理玩法累计限额->{}：key={},field={},value={}", target, key, field, value);
        if ("-1".equals(creditAgentId) && StringUtils.isBlank(value)) {
            // 缓存中没有默认值，查询数据库
            List<RcsCreditSinglePlayLimit> list = rcsCreditSinglePlayLimitService.querySinglePlayLimit(tenantId, creditAgentId, 0L, sportId, playClassify, betStage);
            log.info("信用额度，查询代理玩法累计限额->{}：list={}", target, JSON.toJSONString(list));
            if (CollectionUtils.isNotEmpty(list)) {
                Map<String, String> hashMap = list.stream().collect(Collectors.toMap(config -> String.valueOf(config.getTournamentLevel()), config -> CommonUtils.toString(config.getValue())));
                redisUtils.hmset(key, hashMap);
                log.info("信用额度，缓存代理玩法累计限额->{}：key={},hashMap={}", target, key, JSON.toJSONString(hashMap));
                value = hashMap.get(field);
            }
        }
        if (StringUtils.isNotBlank(value)) {
            return CommonUtils.toBigDecimal(value);
        }
        return null;
    }

    /**
     * 获取代理玩法累计已用限额，单位分
     *
     * @param dateExpect
     * @param tenantId
     * @param creditAgentId
     * @param sportId
     * @param playId
     * @param matchType
     * @param matchId
     * @return
     */
    protected long getAgentSinglePlayUsed(String dateExpect, Long tenantId, String creditAgentId, Integer sportId, Integer playId, Integer matchType, Long matchId) {
        sportId = CreditBizUtils.standardSportId(sportId);
        Integer playClassify = PlayClassify.getPlayClassify(sportId, playId);
        String betStage = CreditBizUtils.convertBetStage(matchType);
        String key = CreditRedisKey.Used.getSinglePlayKey(dateExpect, tenantId, creditAgentId, matchId, playClassify);
        String value = redisUtils.hget(key, betStage);
        log.info("信用额度，获取代理玩法累计已用限额：key={},field={},value={}", key, betStage, value);
        return NumberUtils.toLong(value);
    }

    /**
     * 累加代理玩法累计已用限额，单位分
     *
     * @param dateExpect
     * @param tenantId
     * @param creditAgentId
     * @param sportId
     * @param playId
     * @param matchType
     * @param matchId
     * @param value
     * @param redisUpdateList
     * @return
     */
    protected long incrAgentSinglePlayUsed(String dateExpect, Long tenantId, String creditAgentId, Integer sportId, Integer playId, Integer matchType, Long matchId, long value, List<RedisUpdateVo> redisUpdateList) {
        sportId = CreditBizUtils.standardSportId(sportId);
        Integer playClassify = PlayClassify.getPlayClassify(sportId, playId);
        String betStage = CreditBizUtils.convertBetStage(matchType);
        String key = CreditRedisKey.Used.getSinglePlayKey(dateExpect, tenantId, creditAgentId, matchId, playClassify);
        long used = redisUtils.hincrBy(key, betStage, value);
        log.info("信用额度，累加代理玩法累计已用限额：key={},field={},value={},used={}", key, betStage, value, used);
        redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBY.getCmd(), key, betStage, String.valueOf(value), String.valueOf(used)));
        redisUtils.expire(key, 90L, TimeUnit.DAYS);
        return used;
    }

    /**
     * 获取用户玩法累计限额，单位分
     *
     * @param tenantId
     * @param creditAgentId
     * @param sportId
     * @param tournamentLevel
     * @param playId
     * @param matchType
     * @return
     */
    protected long getUserSinglePlayLimit(Long tenantId, String creditAgentId, Long userId, Integer sportId, Integer tournamentLevel, Integer playId, Integer matchType) {
        sportId = CreditBizUtils.creditSportId(sportId);
        tournamentLevel = CreditBizUtils.creditTournamentLevel(tournamentLevel);
        Integer playClassify = PlayClassify.getPlayClassify(sportId, playId);
        String betStage = CreditBizUtils.convertBetStage(matchType);
        log.info("信用额度，获取限额,用户玩法累计限额：商户：{}，代理：{}，用户：{}，赛种：{}，联赛等级：{}，玩法：{}，赛事类型：{}", tenantId, creditAgentId, userId, sportId, tournamentLevel, playClassify, betStage);
        // 获取用户玩法累计限额
        BigDecimal value = getUserSinglePlayLimit(tenantId, creditAgentId, userId, sportId, tournamentLevel, playClassify, betStage);
        if (value != null) {
            log.info("信用额度，获取限额,用户玩法累计限额：{}", value);
            return value.multiply(RcsConstant.HUNDRED).longValue();
        }
        // 获取用户玩法累计通用限额
        value = getUserSinglePlayLimit(tenantId, creditAgentId, -1L, sportId, tournamentLevel, playClassify, betStage);
        if (value != null) {
            log.info("信用额度，获取限额,用户玩法累计通用限额：{}", value);
            return value.multiply(RcsConstant.HUNDRED).longValue();
        }
        // 获取用户玩法累计通用（配置）限额
        value = getUserSinglePlayLimit(0L, "0", -1L, sportId, tournamentLevel, playClassify, betStage);
        if (value != null) {
            log.info("信用额度，获取限额,用户玩法累计配模板限额：{}", value);
            return value.multiply(RcsConstant.HUNDRED).longValue();
        }
        // 获取用户玩法累计通用限额默认值
        value = getUserSinglePlayLimit(0L, "-1", -1L, sportId, tournamentLevel, playClassify, betStage);
        if (value != null) {
            log.info("信用额度，获取限额,用户玩法累计默认限额：{}", value);
            return value.multiply(RcsConstant.HUNDRED).longValue();
        }
        return 0L;
    }

    private BigDecimal getUserSinglePlayLimit(Long tenantId, String creditAgentId, Long userId, Integer sportId, Integer tournamentLevel, Integer playClassify, String betStage) {
        String target = getUserLimitTarget(creditAgentId, userId);
        String key = CreditRedisKey.Limit.getUserSinglePlayKey(tenantId, creditAgentId, userId, sportId, playClassify, betStage);
        String field = String.valueOf(tournamentLevel);
        String value = redisUtils.hget(key, field);
        log.info("信用额度，获取用户玩法累计限额->{}：key={},field={},value={}", target, key, field, value);
        if (("-1".equals(creditAgentId) || "0".equals(creditAgentId)) && StringUtils.isBlank(value)) {
            // 缓存中没有默认值，查询数据库
            List<RcsCreditSinglePlayLimit> list = rcsCreditSinglePlayLimitService.querySinglePlayLimit(tenantId, creditAgentId, userId, sportId, playClassify, betStage);
            log.info("信用额度，查询用户玩法累计限额->{}：list={}", target, JSON.toJSONString(list));
            if (CollectionUtils.isNotEmpty(list)) {
                Map<String, String> hashMap = list.stream().collect(Collectors.toMap(config -> String.valueOf(config.getTournamentLevel()), config -> CommonUtils.toString(config.getValue())));
                redisUtils.hmset(key, hashMap);
                log.info("信用额度，查询用户玩法累计限额->{}：key={},hashMap={}", target, key, JSON.toJSONString(hashMap));
                value = hashMap.get(field);
            }
        }
        if (StringUtils.isNotBlank(value)) {
            return CommonUtils.toBigDecimal(value);
        }
        return null;
    }

    /**
     * 获取用户玩法累计已用限额，单位分
     *
     * @param dateExpect
     * @param userId
     * @param sportId
     * @param playId
     * @param matchType
     * @param matchId
     * @return
     */
    protected long getUserSinglePlayUsed(String dateExpect, Long userId, Integer sportId, Integer playId, Integer matchType, Long matchId) {
        sportId = CreditBizUtils.standardSportId(sportId);
        Integer playClassify = PlayClassify.getPlayClassify(sportId, playId);
        String betStage = CreditBizUtils.convertBetStage(matchType);
        String key = CreditRedisKey.Used.getUserSinglePlayKey(dateExpect, userId, matchId, playClassify);
        String value = redisUtils.hget(key, betStage);
        log.info("信用额度，获取用户玩法累计已用限额：key={},field={},value={}", key, betStage, value);
        return NumberUtils.toLong(value);
    }

    /**
     * 累加用户玩法累计已用限额，单位分
     *
     * @param dateExpect
     * @param userId
     * @param sportId
     * @param playId
     * @param matchType
     * @param matchId
     * @param value
     * @param redisUpdateList
     * @return
     */
    protected long incrUserSinglePlayUsed(String dateExpect, Long userId, Integer sportId, Integer playId, Integer matchType, Long matchId, long value, List<RedisUpdateVo> redisUpdateList) {
        sportId = CreditBizUtils.standardSportId(sportId);
        Integer playClassify = PlayClassify.getPlayClassify(sportId, playId);
        String betStage = CreditBizUtils.convertBetStage(matchType);
        String key = CreditRedisKey.Used.getUserSinglePlayKey(dateExpect, userId, matchId, playClassify);
        long used = redisUtils.hincrBy(key, betStage, value);
        log.info("信用额度，累加用户玩法累计已用限额：key={},field={},value={},used={}", key, betStage, value, used);
        redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBY.getCmd(), key, betStage, String.valueOf(value), String.valueOf(used)));
        redisUtils.expire(key, 90L, TimeUnit.DAYS);
        return used;
    }

    /**
     * 获取用户玩法单注限额，单位分
     *
     * @param tenantId
     * @param creditAgentId
     * @param sportId
     * @param tournamentLevel
     * @param playId
     * @param matchType
     * @return
     */
    protected long getUserSinglePlayBetLimit(Long tenantId, String creditAgentId, Long userId, Integer sportId, Integer tournamentLevel, Integer playId, Integer matchType) {
        sportId = CreditBizUtils.creditSportId(sportId);
        tournamentLevel = CreditBizUtils.creditTournamentLevel(tournamentLevel);
        Integer playClassify = PlayClassify.getPlayClassify(sportId, playId);
        String betStage = CreditBizUtils.convertBetStage(matchType);
        log.info("信用额度，获取限额,用户玩法单注限额：商户：{}，代理：{}，用户：{}，赛种：{}，联赛等级：{}，玩法：{}，赛事类型：{}", tenantId, creditAgentId, userId, sportId, tournamentLevel, playClassify, betStage);

        // 获取用户玩法单注限额
        BigDecimal value = getUserSinglePlayBetLimit(tenantId, creditAgentId, userId, sportId, tournamentLevel, playClassify, betStage);
        if (value != null) {
            log.info("信用额度，获取限额,用户玩法单注限额：{}", value);
            return value.multiply(RcsConstant.HUNDRED).longValue();
        }
        // 获取用户玩法单注通用限额
        value = getUserSinglePlayBetLimit(tenantId, creditAgentId, -1L, sportId, tournamentLevel, playClassify, betStage);
        if (value != null) {
            log.info("信用额度，获取限额,用户玩法单注通用限额：{}", value);
            return value.multiply(RcsConstant.HUNDRED).longValue();
        }
        //获取用户玩法单注通用设置限额 (credit_id =0 后台配置的单注限额   -1 默认的限额)
        value = getUserSinglePlayBetLimit(0L, "0", -1L, sportId, tournamentLevel, playClassify, betStage);
        if (value != null) {
            log.info("信用额度，获取限额,用户玩法单注通用设置限额：{}", value);
            return value.multiply(RcsConstant.HUNDRED).longValue();
        }
        // 获取用户玩法单注通用限额默认值
        value = getUserSinglePlayBetLimit(0L, "-1", -1L, sportId, tournamentLevel, playClassify, betStage);
        if (value != null) {
            log.info("信用额度，获取限额,用户玩法单注默认限额：{}", value);
            return value.multiply(RcsConstant.HUNDRED).longValue();
        }
        return 0L;
    }

    private BigDecimal getUserSinglePlayBetLimit(Long tenantId, String creditAgentId, Long userId, Integer sportId, Integer tournamentLevel, Integer playClassify, String betStage) {
        String target = getUserLimitTarget(creditAgentId, userId);
        String key = CreditRedisKey.Limit.getUserSinglePlayBetKey(tenantId, creditAgentId, userId, sportId, playClassify, betStage);
        String field = String.valueOf(tournamentLevel);
        String value = redisUtils.hget(key, field);
        log.info("信用额度，获取用户玩法单注限额->{}：key={},field={},value={}", target, key, field, value);
        if (("-1".equals(creditAgentId) || "0".equals(creditAgentId)) && StringUtils.isBlank(value)) {
            // 缓存中没有默认值，查询数据库
            List<RcsCreditSinglePlayBetLimit> list = rcsCreditSinglePlayBetLimitService.querySinglePlayBetLimit(tenantId, creditAgentId, userId, sportId, playClassify, betStage);
            log.info("信用额度，查询用户玩法单注限额->{}：list={}", target, JSON.toJSONString(list));
            if (CollectionUtils.isNotEmpty(list)) {
                Map<String, String> hashMap = list.stream().collect(Collectors.toMap(config -> String.valueOf(config.getTournamentLevel()), config -> CommonUtils.toString(config.getValue())));
                redisUtils.hmset(key, hashMap);
                log.info("信用额度，缓存用户玩法单注限额->{}：key={},hashMap={}", target, key, JSON.toJSONString(hashMap));
                value = hashMap.get(field);
            }
        }
        if (StringUtils.isNotBlank(value)) {
            return CommonUtils.toBigDecimal(value);
        }
        return null;
    }

    protected ExtendBean buildExtendBean(OrderBean bean, OrderItem item) {
        ExtendBean extend = new ExtendBean();
        extend.setSeriesType(bean.getSeriesType());
        extend.setItemId(item.getBetNo());
        extend.setOrderId(item.getOrderNo());
        extend.setBusId(String.valueOf(bean.getTenantId()));
        extend.setHandicap(item.getMarketValue());
        extend.setCurrentScore(item.getScoreBenchmark());
        // 1-早盘，2-滚球盘，3-冠军盘
        extend.setIsScroll("0");
        extend.setIsChampion(0);
        if (item.getMatchType() != null) {
            if (item.getMatchType() == 2) {
                extend.setIsScroll("1");
            }
            if (item.getMatchType() == 3) {
                extend.setIsChampion(1);
            }
        } else {
            extend.setIsScroll("0");
        }
        extend.setMatchId(String.valueOf(item.getMatchId()));
        extend.setPlayId(String.valueOf(item.getPlayId()));
        extend.setSelectId(String.valueOf(item.getPlayOptionsId()));
        extend.setSportId(String.valueOf(item.getSportId()));
        extend.setUserId(String.valueOf(item.getUid()));
        extend.setOdds(String.valueOf(item.getHandleAfterOddsValue()));
        extend.setMarketId(item.getMarketId().toString());
        //阶段  冠军玩法走mts 可以不设置此字段
//        if (item.getMatchType() != 3) {
//            extend.setPlayType(rcsPaidConfigService.getPlayProcess(String.valueOf(item.getSportId()), String.valueOf(item.getPlayId())));
//        }
        if (item.getBetAmount() != null) {
            extend.setOrderMoney(item.getBetAmount());
            extend.setCurrentMaxPaid(new BigDecimal(item.getBetAmount()).multiply((new BigDecimal(extend.getOdds()).subtract(new BigDecimal(1)))).longValue());
        } else {
            extend.setOrderMoney(0L);
            extend.setCurrentMaxPaid(0L);
        }
        extend.setTournamentLevel(item.getTurnamentLevel());
        extend.setTournamentId(item.getTournamentId());
        extend.setDateExpect(item.getDateExpect());
        extend.setDataSourceCode(item.getDataSourceCode());

        extend.setItemBean(item);
        if (StringUtils.isBlank(extend.getHandicap())) {
            extend.setHandicap("0");
        }
        if (StringUtils.isBlank(extend.getCurrentScore())) {
            extend.setCurrentScore("0:0");
        }
        extend.setLimitType(bean.getLimitType());
        return extend;
    }

    protected Map<String, Object> mtsCheckOrder(OrderBean orderBean) {
        log.info("信用限额，MTS下单验证");
        List<ExtendBean> extendBeanList = orderBean.getItems().stream().map(orderItem -> {
            ExtendBean extendBean = buildExtendBean(orderBean, orderItem);
            extendBean.setValidateResult(1);
            return extendBean;
        }).collect(Collectors.toList());

        // 状态更新
        orderBean.setValidateResult(1);
        orderBean.setOrderStatus(0);
        orderBean.setInfoStatus(OrderInfoStatusEnum.MTS_PROCESSING.getCode());
        producerSendMessageUtils.sendMessage(MqConstants.RCS_ORDER_UPDATE, "", orderBean.getOrderNo(), orderBean);

        // 发送MTS订单MQ消息
        Map<String, Object> map = Maps.newHashMap();
        map.put("list", extendBeanList);
        map.put("seriesNum", orderBean.getSeriesType());
        map.put("ip", orderBean.getIp());
        map.put("totalMoney", orderBean.getProductAmountTotal());
        map.put("deviceType", orderBean.getDeviceType());
        map.put("acceptOdds", orderBean.getAcceptOdds());
        producerSendMessageUtils.sendMessage(MqConstants.RCS_VALIDATE_MTS_ORDER, MqConstants.RCS_VALIDATE_MTS_ORDER_TAG, orderBean.getOrderNo(), map);
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("status", 2);
        resultMap.put("infoStatus", OrderInfoStatusEnum.MTS_PROCESSING.getCode());
        resultMap.put("infoMsg", "MTS处理中");
        resultMap.put("infoCode", 0);
        resultMap.put(orderBean.getOrderNo(), true);
        return resultMap;
    }

    protected void commonCheckOrderBean(OrderBean orderBean) {
        if (CreditLimitService.checkNo(orderBean.getTenantId())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "商户ID不能为空！");
        }
        if (StringUtils.isBlank(orderBean.getAgentId())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "信用代理ID不能为空！");
        }
        if (CreditLimitService.checkNo(orderBean.getUid())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "用户ID不能为空！");
        }
        if (CollectionUtils.isEmpty(orderBean.getItems())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "投注项items不能为空！");
        }
    }

    protected abstract void queryCheckOrderBean(OrderBean orderBean);

    protected abstract void orderCheckOrderBean(OrderBean orderBean);

    protected void commonCheckOrderItem(OrderItem orderItem) {
        if (CreditLimitService.checkNo(orderItem.getSportId())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items赛种sportId不能为空！");
        }
        if (CreditLimitService.checkNo(orderItem.getMatchId())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items赛事matchId不能为空！");
        }
        if (CreditLimitService.checkNo(orderItem.getPlayId())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items玩法playId不能为空！");
        }
        if (orderItem.getMatchType() == null) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items投注类型matchType不能为空！");
        }
        if (CreditLimitService.checkNo(orderItem.getOddsValue())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items赔率oddsValue不能为空！");
        }
        if (orderItem.getTurnamentLevel() == null) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items联赛等级turnamentLevel不能为空！");
        }
        if (StringUtils.isBlank(orderItem.getDateExpect())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items赛事账务日dateExpect不能为空！");
        }
    }

    protected abstract void queryCheckOrderItem(OrderItem orderItem);

    protected abstract void orderCheckOrderItem(OrderItem orderItem);

    private String getAgentLimitTarget(String creditAgentId) {
        if ("-1".equals(creditAgentId)) {
            return "默认限额";
        }
        if ("0".equals(creditAgentId)) {
            return "后台配置限额";
        }
        return "配置限额";
    }

    private String getUserLimitTarget(String creditAgentId, Long userId) {
        if ("-1".equals(creditAgentId)) {
            return "默认限额";
        }
        if ("0".equals(creditAgentId)) {
            return "后台配置限额";
        }
        if (NumberUtils.LONG_MINUS_ONE.equals(userId)) {
            return "通用限额";
        }
        return "配置限额";
    }

    /**
     * 获取信用商户通用单注限额比例
     *
     * @param tenantId
     * @return
     */
    protected BigDecimal getBusinessSinglePlayBetRatio(Long tenantId, String creditAgentId) {
        String key = "risk:trade:credit:businessSingleBetPercent:%s";
        String value = redisUtils.hget(String.format(key, tenantId), creditAgentId);
        log.info("信用额度，获取商户代理通用单注限额比例->：key={},filed={},value={}", String.format(key, tenantId), creditAgentId, value);
        if (StringUtils.isBlank(value)) {
            // 缓存中没有默认值，查询数据库
            LambdaQueryWrapper<RcsQuotaBusinessLimit> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(RcsQuotaBusinessLimit::getBusinessId, tenantId)
                    .eq(RcsQuotaBusinessLimit::getCreditName, creditAgentId);
            RcsQuotaBusinessLimit dbRcsQuotaBusinessLimit = rcsQuotaBusinessLimitService.getOne(wrapper);
            log.info("信用额度，数据库查询商户-代理:{}-{} 通用单注限额比例：result={}", tenantId, creditAgentId, JSON.toJSONString(dbRcsQuotaBusinessLimit));
            if (dbRcsQuotaBusinessLimit != null && dbRcsQuotaBusinessLimit.getCreditBetRatio() != null) {
                redisUtils.hset(String.format(key, tenantId), creditAgentId, dbRcsQuotaBusinessLimit.getCreditBetRatio().toString());
                log.info("信用额度，获取商户代理通用单注限额比例->：key={},filed={},value={}", String.format(key, tenantId), creditAgentId, value);
                value = dbRcsQuotaBusinessLimit.getCreditBetRatio().toString();
            }
        }
        if (StringUtils.isNotBlank(value)) {
            return new BigDecimal(value);
        }
        return new BigDecimal(1);
    }

}
