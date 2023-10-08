package com.panda.sport.rcs.credit.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.rcs.cache.redis.RedisUtils;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.constants.SeriesEnum;
import com.panda.sport.rcs.credit.constants.CreditRedisKey;
import com.panda.sport.rcs.entity.vo.RemainLimitResVo;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 剩余限额服务
 * @Author : Paca
 * @Date : 2021-07-08 14:20
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service
public class RemainLimitService extends CreditSingleLimitServiceImpl {

    @Autowired
    private RedisUtils redisUtils;

    public void getSeriesRemainLimit(Long merchantId, String creditAgentId, Long userId, RemainLimitResVo resVo) {
        String currentDateExpect = DateUtils.getDateExpect(System.currentTimeMillis());

        Map<String, String> userLimitMap = getUserSeriesAllLimit(merchantId, creditAgentId, userId);
        String userUsedKey = CreditRedisKey.Used.getUserSeriesKey(currentDateExpect, userId);
        Map<String, String> userUsedMap = redisUtils.hgetAll(userUsedKey);
        log.info("信用额度，获取用户串关已用限额：key={},map={}", userUsedKey, JSON.toJSONString(userUsedMap));
        if (CollectionUtils.isEmpty(userUsedMap)) {
            userUsedMap = Maps.newHashMap();
        }

        Map<String, String> agentLimitMap = getAgentSeriesAllLimit(merchantId, creditAgentId);
        String usedKey = CreditRedisKey.Used.getSeriesKey(currentDateExpect, merchantId, creditAgentId);
        Map<String, String> agentUsedMap = redisUtils.hgetAll(usedKey);
        log.info("信用额度，获取代理串关已用限额：key={},map={}", usedKey, JSON.toJSONString(agentUsedMap));
        if (CollectionUtils.isEmpty(agentUsedMap)) {
            agentUsedMap = Maps.newHashMap();
        }

        Map<Integer, Long> seriesUserRemainLimit = Maps.newHashMap();
        Map<Integer, Long> seriesAgentRemainLimit = Maps.newHashMap();
        for (int i = SeriesEnum.TWO.getSeriesNum(); i <= SeriesEnum.Ten.getSeriesNum(); i++) {
            String seriesType = String.valueOf(i);

            String userLimitValue = userLimitMap.getOrDefault(seriesType, "0");
            String userUsedValue = userUsedMap.getOrDefault(seriesType, "0");
            BigDecimal userUsed = CommonUtils.toBigDecimal(userUsedValue).divide(RcsConstant.HUNDRED, 2, RoundingMode.HALF_UP);
            long userRemain = CommonUtils.toBigDecimal(userLimitValue).subtract(userUsed).longValue();
            seriesUserRemainLimit.put(i, userRemain);

            String agentLimitValue = agentLimitMap.getOrDefault(seriesType, "0");
            String agentUsedValue = agentUsedMap.getOrDefault(seriesType, "0");
            BigDecimal agentUsed = CommonUtils.toBigDecimal(agentUsedValue).divide(RcsConstant.HUNDRED, 2, RoundingMode.HALF_UP);
            long agentRemain = CommonUtils.toBigDecimal(agentLimitValue).subtract(agentUsed).longValue();
            seriesAgentRemainLimit.put(i, agentRemain);
        }

        resVo.setSeriesUserLimit(seriesUserRemainLimit);
        resVo.setSeriesAgentLimit(seriesAgentRemainLimit);
    }

    /**
     * 获取用户串关限额
     *
     * @param tenantId
     * @param creditAgentId
     * @param userId
     * @return
     */
    private Map<String, String> getUserSeriesAllLimit(Long tenantId, String creditAgentId, Long userId) {
        // 获取用户串关限额
        Map<String, String> map = getUserSeriesLimit(tenantId, creditAgentId, userId);
        if (CollectionUtils.isNotEmpty(map)) {
            return map;
        }
        // 获取用户串关通用限额
        map = getUserSeriesLimit(tenantId, creditAgentId, -1L);
        if (CollectionUtils.isNotEmpty(map)) {
            return map;
        }
        // 获取用户串关通用限额默认值
        map = getUserSeriesLimit(0L, "0", -1L);
        if (CollectionUtils.isNotEmpty(map)) {
            return map;
        }
        return Maps.newHashMap();
    }

    /**
     * 获取代理串关限额
     *
     * @param tenantId
     * @param creditAgentId
     * @return
     */
    private Map<String, String> getAgentSeriesAllLimit(Long tenantId, String creditAgentId) {
        // 获取代理串关限额
        Map<String, String> map = getAgentSeriesLimit(tenantId, creditAgentId);
        if (CollectionUtils.isNotEmpty(map)) {
            return map;
        }
        // 获取代理串关限额默认值
        map = getAgentSeriesLimit(0L, "-1");
        if (CollectionUtils.isNotEmpty(map)) {
            return map;
        }
        return Maps.newHashMap();
    }

    public void getSingleRemainLimit(StandardMatchInfo standardMatchInfo, Long merchantId, String creditAgentId, Long userId, Integer playId, RemainLimitResVo resVo) {
        String dateExpect = DateUtils.getDateExpect(standardMatchInfo.getBeginTime());
        Long matchId = standardMatchInfo.getId();
        int sportId = standardMatchInfo.getSportId().intValue();
        Integer tournamentLevel = standardMatchInfo.getTournamentLevel();
        Integer matchType;
        if (Lists.newArrayList(1, 2, 10).contains(standardMatchInfo.getMatchStatus())) {
            resVo.setRiskManagerCode(standardMatchInfo.getLiveRiskManagerCode());
            matchType = 2;
        } else {
            resVo.setRiskManagerCode(standardMatchInfo.getPreRiskManagerCode());
            matchType = 1;
        }

        // 用户玩法累计限额
        long userSinglePlayLimit = getUserSinglePlayLimit(merchantId, creditAgentId, userId, sportId, tournamentLevel, playId, matchType);
        long userSinglePlayUsed = getUserSinglePlayUsed(dateExpect, userId, sportId, playId, matchType, matchId);
        long userSinglePlay = userSinglePlayLimit - userSinglePlayUsed;
        log.info("信用额度，用户玩法累计限额剩余额度：{} = {} - {}", userSinglePlay, userSinglePlayLimit, userSinglePlayUsed);
        resVo.setSingleUserPlayLimit(userSinglePlay / 100);

        // 代理玩法累计限额
        long singlePlayLimit = getAgentSinglePlayLimit(merchantId, creditAgentId, sportId, tournamentLevel, playId, matchType);
        long singlePlayUsed = getAgentSinglePlayUsed(dateExpect, merchantId, creditAgentId, sportId, playId, matchType, matchId);
        long singlePlay = singlePlayLimit - singlePlayUsed;
        log.info("信用额度，代理玩法累计限额剩余额度：{} = {} - {}", singlePlay, singlePlayLimit, singlePlayUsed);
        resVo.setSingleAgentPlayLimit(singlePlay / 100);

        // 代理单场赛事限额
        long singleMatchLimit = getAgentSingleMatchLimit(merchantId, creditAgentId, sportId, tournamentLevel);
        long singleMatchUsed = getAgentSingleMatchUsed(dateExpect, merchantId, creditAgentId, matchId);
        long singleMatch = singleMatchLimit - singleMatchUsed;
        log.info("信用额度，代理单场赛事限额剩余额度：{} = {} - {}", singleMatch, singleMatchLimit, singleMatchUsed);
        resVo.setSingleAgentLimit(singleMatch / 100);
    }
}
