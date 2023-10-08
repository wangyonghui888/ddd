package com.panda.sport.rcs.limit.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.google.common.collect.Lists;
import com.panda.sport.data.rcs.dto.limit.MatchLimitDataReqVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaMerchantSingleFieldLimitVo;
import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.limit.constants.LimitRedisKeys;
import com.panda.sport.rcs.limit.vo.AvailableLimitQueryReqVo;
import com.panda.sport.rcs.limit.vo.MerchantAvailableLimitResVo;
import com.panda.sport.rcs.mapper.StandardSportTournamentMapper;
import com.panda.sport.rcs.pojo.RcsOperateMerchantsSet;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportTournament;
import com.panda.sport.rcs.service.IRcsOperateMerchantsSetService;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.wrapper.limit.RcsQuotaBusinessLimitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 商户限额服务
 * @Author : Paca
 * @Date : 2021-12-05 18:35
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service
public class MerchantLimitServiceImpl {

    @Autowired
    private RcsQuotaBusinessLimitService rcsQuotaBusinessLimitService;
    @Autowired
    private IRcsOperateMerchantsSetService rcsOperateMerchantsSetService;

    @Autowired
    private StandardSportTournamentMapper standardSportTournamentMapper;

    @Autowired
    private LimitServiceImpl limitService;

    @Autowired
    private RedisClient redisClient;
    /**
     * C01商户单场限额配置
     */
    @Value("${redcat.limit.merchant}")
    private Long readCatLimitMerchant;

    /**
     * 通过商户编码获取商户限额配置
     *
     * @param merchantCode
     * @return
     */
    public RcsQuotaBusinessLimit getMerchantLimitConfig(final String merchantCode) {
        if (StringUtils.isBlank(merchantCode)) {
            throw new RcsServiceException("商户编码不能为空");
        }
        RcsOperateMerchantsSet merchantInfo = rcsOperateMerchantsSetService.getByMerchantCode(merchantCode);
        if (merchantInfo == null) {
            throw new RcsServiceException("商户不存在：merchantCode=" + merchantCode);
        }
        return getMerchantLimitConfigByMerchantId(merchantInfo.getMerchantsId());
    }

    /**
     * 通过商户ID获取商户限额配置
     *
     * @param merchantId
     * @return
     */
    public RcsQuotaBusinessLimit getMerchantLimitConfigByMerchantId(final String merchantId) {
        if (StringUtils.isBlank(merchantId)) {
            throw new RcsServiceException("商户ID不能为空");
        }
        RcsQuotaBusinessLimit rcsQuotaBusinessLimit = rcsQuotaBusinessLimitService.getByBusinessId(merchantId);
        if (rcsQuotaBusinessLimit == null) {
            throw new RcsServiceException("未查询到商户限额配置：merchantId=" + merchantId);
        }
        return rcsQuotaBusinessLimit;
    }

    /**
     * 获取商户单日已用限额
     *
     * @param time
     * @param businessId
     * @return 单位（元）
     */
    public BigDecimal getBusinessDailyUsedLimit(Long time, String businessId) {
        if (time == null) {
            time = System.currentTimeMillis();
        }
        String dateExpect = DateUtils.getDateExpect(time);
        String key = String.format(RedisKeys.PAID_DATE_BUS_REDIS_CACHE, dateExpect, businessId);
        String value = redisClient.get(key);
        log.info("额度查询-Redis获取商户单日已用限额：key={},value={}", key, value);
        return CommonUtils.toBigDecimal(value).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * 获取商户串关单日已用限额
     *
     * @param time
     * @param businessId
     * @return 单位（元）
     */
    public BigDecimal getBusinessSeriesDailyUsedLimit(Long time, String businessId) {
        if (time == null) {
            time = System.currentTimeMillis();
        }
        String dateExpect = DateUtils.getDateExpect(time);
        String key = String.format(com.panda.sport.rcs.limit.constants.RedisKeys.PAID_DATE_BUS_SERIES_REDIS_CACHE, dateExpect, businessId);
        String value = redisClient.get(key);
        log.info("额度查询-Redis获取商户串关单日已用限额：key={},value={}", key, value);
        return CommonUtils.toBigDecimal(value).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    }

    private int convertTournamentLevel(Integer tournamentLevel) {
        if (tournamentLevel == null || tournamentLevel < 1 || tournamentLevel > 20) {
            // -1表示未评级
            return -1;
        }
        return tournamentLevel;
    }

    public MerchantAvailableLimitResVo getMerchantAvailableLimit(AvailableLimitQueryReqVo reqVo) {
        log.info("::{}::投注查询-商户单场可用限额开始", reqVo.getMerchantCode());
        RcsQuotaBusinessLimit rcsQuotaBusinessLimit = getMerchantLimitConfig(reqVo.getMerchantCode());
        log.info("::{}::投注查询-商户单场可用限额-商户配置:{}", reqVo.getMerchantCode(), JSONObject.toJSONString(rcsQuotaBusinessLimit));
        String businessId = rcsQuotaBusinessLimit.getBusinessId();
        Long configLimit = rcsQuotaBusinessLimit.getBusinessSingleDayLimit();
        BigDecimal usedLimit = getBusinessDailyUsedLimit(System.currentTimeMillis(), businessId);
        BigDecimal availableLimit = new BigDecimal(configLimit).subtract(usedLimit).setScale(2, RoundingMode.HALF_UP);
        log.info("::{}::投注查询-商户单场可用限额-[单关]限额:{},已用:{},可用:{}", reqVo.getMerchantCode(), configLimit, usedLimit, availableLimit);

        // 串关限额
        Long configLimitSeries = rcsQuotaBusinessLimit.getBusinessSingleDaySeriesLimit();
        BigDecimal usedLimitSeries = getBusinessSeriesDailyUsedLimit(System.currentTimeMillis(), businessId);
        BigDecimal availableLimitSeries = new BigDecimal(configLimitSeries).subtract(usedLimitSeries).setScale(2, RoundingMode.HALF_UP);
        log.info("::{}::投注查询-商户单场可用限额-[串关]限额:{},已用:{},可用:{}", reqVo.getMerchantCode(), configLimitSeries, usedLimitSeries, availableLimitSeries);

        MerchantAvailableLimitResVo resVo = new MerchantAvailableLimitResVo();
        resVo.setMerchantDailyAvailableLimit(availableLimit);
        resVo.setMerchantDailySeriesAvailableLimit(availableLimitSeries);

        String matchManageId = reqVo.getMatchManageId();
        if (StringUtils.isBlank(matchManageId)) {
            return resVo;
        }

        StandardMatchInfo matchInfo = limitService.getMatchInfoByMatchManageId(matchManageId);
        resVo.setMatchType(matchInfo.getMatchType());
        resVo.setRiskManagerCode(matchInfo.getRiskManagerCode());
        if ("MTS".equalsIgnoreCase(matchInfo.getRiskManagerCode())) {
            log.warn("::{}::投注查询-商户单场可用限额-[MTS]操盘赛事,不处理", reqVo.getMerchantCode());
            return resVo;
        }
        Long matchId = matchInfo.getId();
        Long sportId = matchInfo.getSportId();
        String dateExpect = DateUtils.getDateExpect(matchInfo.getBeginTime());
        Integer tournamentLevel = matchInfo.getTournamentLevel();
        StandardSportTournament standardSportTournament = standardSportTournamentMapper.selectById(matchInfo.getStandardTournamentId());
        if (standardSportTournament != null) {
            tournamentLevel = standardSportTournament.getTournamentLevel();
        }
        // 商户单场限额比例
        BigDecimal merchantSingleMatchLimitRatio = rcsQuotaBusinessLimit.getBusinessSingleDayGameProportion();
        RcsQuotaMerchantSingleFieldLimitVo merchantSingleMatchLimitVo = getMerchantSingleMatchLimit(sportId, tournamentLevel, matchId);
        Long merchantSingleMatchLimit;
        //赛事表 0早盘 1滚球
        //商户配置表 1早盘 0滚球
        int matchType = matchInfo.getMatchType();
        if(matchInfo.getMatchStatus() ==3){
            matchType = 0;
        }
        if (0 == matchType) {
            merchantSingleMatchLimit = merchantSingleMatchLimitVo.getLiveBallPayoutLimit();
        } else {
            merchantSingleMatchLimit = merchantSingleMatchLimitVo.getEarlyMorningPaymentLimit();
        }
        if (matchInfo.getDataSourceCode().equals("RC")) {
            merchantSingleMatchLimit=readCatLimitMerchant!=null? readCatLimitMerchant:0;
        }
        log.info("::{}::投注查询-商户单场可用限额-[赛事]限额:{},比例:{}", reqVo.getMerchantCode(), merchantSingleMatchLimit, merchantSingleMatchLimitRatio);
        BigDecimal merchantMatchInitLimit = new BigDecimal(merchantSingleMatchLimit).multiply(merchantSingleMatchLimitRatio).setScale(2, RoundingMode.HALF_UP);
        resVo.setMerchantMatchInitLimit(merchantMatchInitLimit);
        BigDecimal merchantSingleMatchUsedLimit = getMerchantSingleMatchUsedLimit(businessId, sportId, matchId, dateExpect, 0==matchType?"1":"0");
        /**
        bug-44834
        //如果滚球没数据就查早盘的
        if (merchantSingleMatchUsedLimit.compareTo(BigDecimal.ZERO) == 0) {
            merchantSingleMatchUsedLimit = getMerchantSingleMatchUsedLimit(businessId, sportId, matchId, dateExpect, "0");
            log.warn("::{}::投注查询-商户单场可用限额-[取]早盘已用限额:{}", reqVo.getMerchantCode(), merchantSingleMatchUsedLimit);
        }*/
        log.warn("::{}::投注查询-商户单场可用限额-已用限额:{}", reqVo.getMerchantCode(), merchantSingleMatchUsedLimit);
        // 初始限额 - 已用限额 = 可用限额
        resVo.setMerchantMatchAvailableLimit(merchantMatchInitLimit.subtract(merchantSingleMatchUsedLimit).setScale(2, RoundingMode.HALF_UP));

        return resVo;
    }

    private RcsQuotaMerchantSingleFieldLimitVo getMerchantSingleMatchLimit(Long sportId, Integer tournamentLevel, Long matchId) {
        MatchLimitDataReqVo reqVo = new MatchLimitDataReqVo();
        reqVo.setSportId(sportId.intValue());
        reqVo.setTournamentLevel(convertTournamentLevel(tournamentLevel));
        reqVo.setMatchId(matchId);
        reqVo.setDataTypeList(Lists.newArrayList(LimitDataTypeEnum.MERCHANT_SINGLE_LIMIT.getType()));
        RcsQuotaMerchantSingleFieldLimitVo merchantSingleMatchLimitVo = limitService.getRcsQuotaMerchantSingleFieldLimitData(reqVo);
        if (merchantSingleMatchLimitVo == null) {
            log.info("额度查询-获取商户单场限额-其它赛种");
            reqVo.setSportId(-1);
            merchantSingleMatchLimitVo = limitService.getRcsQuotaMerchantSingleFieldLimitData(reqVo);
        }
        if (merchantSingleMatchLimitVo == null) {
            throw new RcsServiceException("未查询到商户单场限额");
        }
        log.info("额度查询-获取商户单场限额：sportId={},tournamentLevel={},matchId={}", sportId, tournamentLevel, matchId);
        return merchantSingleMatchLimitVo;
    }

    private BigDecimal getMerchantSingleMatchUsedLimit(String businessId, Long sportId, Long matchId, String dateExpect, String matchType) {
        String key = LimitRedisKeys.getMerchantSingleMatchHashKey(dateExpect, businessId, String.valueOf(sportId), String.valueOf(matchId), matchType);
        // 商户单场已用限额
        String value = redisClient.hGet(key, LimitRedisKeys.MERCHANT_SINGLE_MATCH_HASH_FIELD);
        log.info("额度查询-Redis获取商户单场已用限额：key={},field={},value={}", key, LimitRedisKeys.MERCHANT_SINGLE_MATCH_HASH_FIELD, value);
        // 转化成 元
        return CommonUtils.toBigDecimal(value).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
}
