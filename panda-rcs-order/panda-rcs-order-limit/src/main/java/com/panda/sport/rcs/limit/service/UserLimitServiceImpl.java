package com.panda.sport.rcs.limit.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.data.rcs.dto.limit.MatchLimitDataReqVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaUserSingleNoteVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaUserSingleSiteQuotaVo;
import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;
import com.panda.sport.data.rcs.dto.special.RcsUserSpecialBetLimitConfigDTO;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.DataSourceEnum;
import com.panda.sport.rcs.enums.YesNoEnum;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.limit.constants.LimitRedisKeys;
import com.panda.sport.rcs.limit.dto.api.UserSeriesAvailableLimitReqDto;
import com.panda.sport.rcs.limit.dto.api.UserSeriesAvailableLimitResDto;
import com.panda.sport.rcs.limit.dto.api.UserSingleAvailableLimitReqDto;
import com.panda.sport.rcs.limit.dto.api.UserSingleAvailableLimitResDto;
import com.panda.sport.rcs.limit.mapper.RcsMerchantLimitCompensationMapper;
import com.panda.sport.rcs.limit.mapper.RcsMerchantSeriesConfigMapper;
import com.panda.sport.rcs.limit.mapper.RcsMerchantSportLimitMapper;
import com.panda.sport.rcs.limit.vo.*;
import com.panda.sport.rcs.mapper.RcsQuotaUserDailyQuotaMapper;
import com.panda.sport.rcs.mapper.StandardSportTournamentMapper;
import com.panda.sport.rcs.mapper.TUserMapper;
import com.panda.sport.rcs.mapper.limit.LimitMapper;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.pojo.RcsQuotaUserDailyQuota;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportTournament;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.limit.constants.LimitConstants.AMOUNT_UNIT;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 用户限额服务
 * @Author : Paca
 * @Date : 2021-12-06 14:49
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service
public class UserLimitServiceImpl {

    private static final String LIMIT = "LIMIT";
    private static final String USED = "USED";

    private static final String USER_DAILY_TOTAL_LIMIT = "USER_DAILY_TOTAL_LIMIT";
    private static final String USER_DAILY_LIMIT = "USER_DAILY_LIMIT";

    @Autowired
    private MerchantLimitServiceImpl merchantLimitService;
    @Autowired
    private LimitServiceImpl limitService;

    @Autowired
    private LimitApiService limitApiService;

    @Autowired
    LimitMapper limitMapper;

    @Autowired
    private RcsQuotaUserDailyQuotaMapper rcsQuotaUserDailyQuotaMapper;
    @Autowired
    private StandardSportTournamentMapper standardSportTournamentMapper;
    @Autowired
    private RcsMerchantSeriesConfigMapper rcsMerchantSeriesConfigMapper;
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RcsMerchantSportLimitMapper rcsMerchantSportLimitMapper;

    @Autowired
    private TUserMapper tUserMapper;

    /**
     * C01用户限额配置
     */
    @Value("${redcat.limit.user}")
    private int readCatLimitUser;


    @Autowired
    private RcsMerchantLimitCompensationMapper rcsMerchantLimitCompensationMapper;

    private void seriesCheck(AvailableLimitQueryReqVo reqVo) {
        String merchantCode = reqVo.getMerchantCode();
        if (StringUtils.isBlank(merchantCode)) {
            throw new RcsServiceException("商户编码不能为空");
        }
        Long userId = reqVo.getUserId();
        if (userId == null) {
            throw new RcsServiceException("用户ID不能为空");
        }
    }

    public SeriesUserAvailableLimitResVo getSeriesUserAvailableLimit(AvailableLimitQueryReqVo reqVo) {
        seriesCheck(reqVo);
        RcsQuotaBusinessLimit rcsQuotaBusinessLimit = merchantLimitService.getMerchantLimitConfig(reqVo.getMerchantCode());
        log.info("::{}::投注查询-[串关]可用限额-商户配置:{}", reqVo.getUserId(), JSONObject.toJSONString(rcsQuotaBusinessLimit));
        String businessId = rcsQuotaBusinessLimit.getBusinessId();
        String userId = String.valueOf(reqVo.getUserId());
        // 用户串关限额比例
        BigDecimal userQuotaRatio = rcsQuotaBusinessLimit.getUserStrayQuotaRatio();
        // 特殊用户百分比
        BigDecimal percentage = getUserLimitPercentage(userId);
        List<RcsQuotaUserDailyQuota> list = rcsQuotaUserDailyQuotaMapper.selectList(null);
        Map<Integer, BigDecimal> seriesDailyLimitMap = list.stream().collect(Collectors.toMap(RcsQuotaUserDailyQuota::getSportId,
                limit -> limit.getCrossDayCompensation().multiply(userQuotaRatio).multiply(percentage)));

        log.info("::{}::投注查询-[串关]可用限额-用户单日配置:{}", reqVo.getUserId(), JSONObject.toJSONString(seriesDailyLimitMap));
        String dateExpect = DateUtils.getDateExpect(System.currentTimeMillis());
        String crossDayCompensationKey = LimitRedisKeys.getCrossDayCompensationKey(dateExpect, businessId, userId);
        Map<String, String> hash = (Map<String, String>) redisClient.hGetAllToObj(crossDayCompensationKey);
        log.info("::{}::投注查询-[串关]可用限额-串关单日限额已用={},key={}", reqVo.getUserId(), JSON.toJSONString(hash), crossDayCompensationKey);
        if (CollectionUtils.isEmpty(hash)) {
            hash = Maps.newHashMap();
        }

        String type = getUserSpecialLimitType(userId);
        String valueTotal = hash.get(LimitRedisKeys.TOTAL_FIELD);
        BigDecimal usedTotal = CommonUtils.toBigDecimal(valueTotal).divide(AMOUNT_UNIT, 2, RoundingMode.HALF_UP);
        BigDecimal limitTotal;
        if ("3".equals(type) || "4".equals(type)) {
            limitTotal = getSingleDayClaimLimit(userId, new BigDecimal(Integer.MAX_VALUE));
            log.info("::{}::投注查询-[串关]可用限额-[取]用户特殊单日:{}", reqVo.getUserId(), limitTotal);
        } else {
            limitTotal = seriesDailyLimitMap.getOrDefault(0, BigDecimal.ZERO);
        }

        SeriesUserAvailableLimitResVo resVo = new SeriesUserAvailableLimitResVo();
        resVo.setUserSpecialLimitType(type);
        resVo.setDailyTotalAvailableLimit(limitTotal.subtract(usedTotal).setScale(2, RoundingMode.HALF_UP));
        if (StringUtils.isNotBlank(reqVo.getMatchManageId())) {
            //查询赛事单场已用额度
            BigDecimal userStrayLimitPlay = this.getMatchSingleLimit(reqVo.getMatchManageId(), reqVo.getUserId(), businessId);
            resVo.setMatchSingleLimit(new BigDecimal(String.valueOf(rcsQuotaBusinessLimit.getUserSingleStrayLimit())).subtract(userStrayLimitPlay.divide(new BigDecimal(100), 0, BigDecimal.ROUND_DOWN)));
            log.info("::{}::投注查询-[串关]可用限额-赛事单场已用={},剩余={}", reqVo.getUserId(), userStrayLimitPlay, resVo.getMatchSingleLimit());
        }

        Map<String, BigDecimal> availableLimitMap = Maps.newHashMap();
        for (int sportId = 1; sportId <= 10; sportId++) {
            String value = hash.get(String.valueOf(sportId));
            BigDecimal used = CommonUtils.toBigDecimal(value).divide(AMOUNT_UNIT, 2, RoundingMode.HALF_UP);
            BigDecimal limit = seriesDailyLimitMap.getOrDefault(sportId, BigDecimal.ZERO);
            availableLimitMap.put(String.valueOf(sportId), limit.subtract(used).setScale(2, RoundingMode.HALF_UP));
        }
        resVo.setAvailableLimitMap(availableLimitMap);
        return resVo;
    }

    private BigDecimal getMatchSingleLimit(String matchManageId, Long userId, String businessId) {
        String matchId = matchManageId.substring(matchManageId.length() - 7);
        String userStrayLimitKey = String.format(LimitRedisKeys.PAID_DATE_BUS_REDIS_MATCH_CACHE, userId, businessId, matchId);
        String userStrayLimitPlayStr = redisClient.get(userStrayLimitKey);
        BigDecimal userStrayLimitPlay = CommonUtils.toBigDecimal(userStrayLimitPlayStr, BigDecimal.ZERO);
        return userStrayLimitPlay;
    }

    private void seriesCheck(UserSeriesAvailableLimitReqDto reqDto) {
        if (StringUtils.isBlank(reqDto.getMerchantId())) {
            throw new RcsServiceException("商户ID不能为空");
        }
        if (reqDto.getUserId() == null) {
            throw new RcsServiceException("用户ID不能为空");
        }
    }

    public UserSeriesAvailableLimitResDto userSeriesAvailableLimit(UserSeriesAvailableLimitReqDto reqDto) {
        seriesCheck(reqDto);
        String merchantId = reqDto.getMerchantId();
        String userId = String.valueOf(reqDto.getUserId());

        RcsQuotaBusinessLimit rcsQuotaBusinessLimit = merchantLimitService.getMerchantLimitConfigByMerchantId(merchantId);
        // 用户限额比例
        //BigDecimal userQuotaRatio = rcsQuotaBusinessLimit.getUserQuotaRatio();
        BigDecimal userQuotaRatio = rcsQuotaBusinessLimit.getUserStrayQuotaRatio();
        // 特殊用户百分比
        BigDecimal percentage = getUserLimitPercentage(userId);
        // 用户特殊限额类型
        String type = getUserSpecialLimitType(userId);
        String dateExpect = DateUtils.getDateExpect(System.currentTimeMillis());
        //查询新版串关单日总额度
        Integer straySwitchVal = rcsQuotaBusinessLimit.getStraySwitchVal();
        BigDecimal limitTotal;
        if ("3".equals(type) || "4".equals(type)) {
            limitTotal = getSingleDayClaimLimit(userId, new BigDecimal(Integer.MAX_VALUE));
        } else {
            LambdaQueryWrapper<RcsQuotaUserDailyQuota> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(RcsQuotaUserDailyQuota::getSportId, 0)
                    .last("LIMIT 1");
            RcsQuotaUserDailyQuota rcsQuotaUserDailyQuota = rcsQuotaUserDailyQuotaMapper.selectOne(wrapper);
            RcsMerchantSeriesConfig rcsMerchantSeriesConfig = rcsMerchantSeriesConfigMapper.selectOne(new LambdaQueryWrapper<RcsMerchantSeriesConfig>().eq(RcsMerchantSeriesConfig::getStatus, YesNoEnum.N).
                    orderByDesc(RcsMerchantSeriesConfig::getCreateTime).last("LIMIT 1"));
            if (rcsQuotaUserDailyQuota != null && straySwitchVal == 0) {
                limitTotal = rcsQuotaUserDailyQuota.getCrossDayCompensation().multiply(userQuotaRatio).multiply(percentage);
            } else if (rcsMerchantSeriesConfig != null && straySwitchVal == 1) {
                limitTotal = rcsMerchantSeriesConfig.getSeriesPayoutTotalAmount().multiply(userQuotaRatio).multiply(percentage);
            } else {
                limitTotal = BigDecimal.ZERO;
            }
        }

        String crossDayCompensationKey = LimitRedisKeys.getCrossDayCompensationKey(dateExpect, merchantId, userId);
        String valueTotal = redisClient.hGet(crossDayCompensationKey, LimitRedisKeys.TOTAL_FIELD);
        log.info("额度查询-Redis获取用户串关单日总限额-已用：key={},field={},value={}", crossDayCompensationKey, LimitRedisKeys.TOTAL_FIELD, valueTotal);
        BigDecimal usedTotal = CommonUtils.toBigDecimal(valueTotal).divide(AMOUNT_UNIT, 2, RoundingMode.HALF_UP);
        //查询新版串关已用额度
        String strayPaymentKey = String.format(LimitRedisKeys.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_KEY, merchantId, userId, dateExpect);
        String strayTotalValue = redisClient.get(strayPaymentKey);
        BigDecimal strayTotal = StringUtils.isNotBlank(strayTotalValue) ? new BigDecimal(strayTotalValue).divide(AMOUNT_UNIT, 2, RoundingMode.HALF_DOWN) : BigDecimal.ZERO;

        UserSeriesAvailableLimitResDto resDto = new UserSeriesAvailableLimitResDto();
        resDto.setMerchantId(merchantId);
        resDto.setUserId(reqDto.getUserId());
        resDto.setDateExpect(dateExpect);
        resDto.setUserSpecialLimitType(type);
        resDto.setDailySeriesPaymentTotalLimit(limitTotal);
        resDto.setDailySeriesPaymentTotalUsedLimit(straySwitchVal == 1 ? strayTotal : usedTotal);
        resDto.setDailySeriesPaymentTotalAvailableLimit(straySwitchVal == 1 ? limitTotal.subtract(strayTotal) : limitTotal.subtract(usedTotal));
        return resDto;
    }

    private void singleCheck(AvailableLimitQueryReqVo reqVo) {
        String merchantCode = reqVo.getMerchantCode();
        if (StringUtils.isBlank(merchantCode)) {
            throw new RcsServiceException("商户编码不能为空");
        }
        Long userId = reqVo.getUserId();
        if (userId == null) {
            throw new RcsServiceException("用户ID不能为空");
        }
    }

    public SingleUserAvailableLimitResVo getSingleUserAvailableLimit(AvailableLimitQueryReqVo reqVo) {
        singleCheck(reqVo);
        String userId = String.valueOf(reqVo.getUserId());
        log.info("::{}::投注查询-单关-用户单场可用限额开始", userId);
        RcsQuotaBusinessLimit rcsQuotaBusinessLimit = merchantLimitService.getMerchantLimitConfig(reqVo.getMerchantCode());
        log.info("::{}::投注查询-单关-用户单场可用限额-商户配置:{}", userId, JSONObject.toJSONString(rcsQuotaBusinessLimit));
        String businessId = rcsQuotaBusinessLimit.getBusinessId();
        SingleUserAvailableLimitResVo resVo = new SingleUserAvailableLimitResVo();
        // 用户限额比例
        BigDecimal userQuotaRatio = rcsQuotaBusinessLimit.getUserQuotaRatio();
        // 特殊用户百分比
        BigDecimal percentage = getUserLimitPercentage(userId);

        log.info("::{}::投注查询-单关-用户单场可用限额,比例:{},百分比:{}", userId, userQuotaRatio, percentage);
        Map<String, Map<String, BigDecimal>> userDailyLimit = getUserDailyLimit(businessId, userId, null, userQuotaRatio, percentage);
        Map<String, BigDecimal> userDailyTotalLimitMap = userDailyLimit.get(USER_DAILY_TOTAL_LIMIT);
        resVo.setDailyTotalAvailableLimit(getAvailableLimit(userDailyTotalLimitMap));
        String type = getUserSpecialLimitType(userId);

        resVo.setUserSpecialLimitType(type);
        if (StringUtils.isNotBlank(reqVo.getMatchManageId())) {
            StandardMatchInfo matchInfo = limitService.getMatchInfoByMatchManageId(reqVo.getMatchManageId());
            Long sportId = matchInfo.getSportId();
            Map<String, Map<String, BigDecimal>> userSingeDailyLimit = getUserDailyLimit(businessId, userId, sportId, userQuotaRatio, percentage);
            Map<String, BigDecimal> userDailyLimitMap = userSingeDailyLimit.get(USER_DAILY_LIMIT);
            resVo.setDailyAvailableLimit(getAvailableLimit(userDailyLimitMap));
            resVo.setMatchType(matchInfo.getMatchType());
            resVo.setRiskManagerCode(matchInfo.getRiskManagerCode());
            resVo.setSportId(sportId);
            if ("MTS".equalsIgnoreCase(matchInfo.getRiskManagerCode())) {
                return resVo;
            }
            Integer tournamentLevel = getTournamentLevel(matchInfo);
            Map<String, String> userUsedMap = getUserUsed(businessId, userId, matchInfo);

            //获取用户赛事单场限额
            Map<String, BigDecimal> userSingleMatchLimitMap = getUserSingleMatchLimit(userId, matchInfo, tournamentLevel, type, userUsedMap, userQuotaRatio, percentage);
            resVo.setSingleMatchAvailableLimit(getAvailableLimit(userSingleMatchLimitMap));
            Long playId = reqVo.getPlayId();
            if (playId == null) {
                return resVo;
            }
            Map<String, BigDecimal> userPlayLimitMap = getUserPlayLimit(matchInfo, tournamentLevel, playId, userUsedMap, userQuotaRatio, percentage, userId);
            log.info("::{}::可用额度查询-单关-用户玩法限额：{}", userId, JSON.toJSONString(userPlayLimitMap));
            resVo.setPlayAvailableLimit(getAvailableLimit(userPlayLimitMap));
        }
        return resVo;
    }

    private void singleCheck(UserSingleAvailableLimitReqDto reqDto) {
        if (StringUtils.isBlank(reqDto.getMerchantId())) {
            throw new RcsServiceException("商户ID不能为空");
        }
        if (reqDto.getUserId() == null) {
            throw new RcsServiceException("用户ID不能为空");
        }
    }

    public UserSingleAvailableLimitResDto userSingleAvailableLimit(UserSingleAvailableLimitReqDto reqDto) {
        singleCheck(reqDto);
        String merchantId = reqDto.getMerchantId();
        String userId = String.valueOf(reqDto.getUserId());
        Long matchId = reqDto.getMatchId();
        Long playId = reqDto.getPlayId();

        StandardMatchInfo matchInfo = limitService.getMatchInfoByMatchId(matchId);
        Long sportId = matchInfo.getSportId();

        UserSingleAvailableLimitResDto resDto = new UserSingleAvailableLimitResDto();
        resDto.setMerchantId(merchantId);
        resDto.setUserId(reqDto.getUserId());
        resDto.setMatchId(matchId);
        resDto.setPlayId(playId);
        resDto.setMatchType(matchInfo.getMatchType());
        resDto.setRiskManagerCode(matchInfo.getRiskManagerCode());
        resDto.setSportId(sportId);
        if ("MTS".equalsIgnoreCase(matchInfo.getRiskManagerCode())) {
            return resDto;
        }
        RcsQuotaBusinessLimit rcsQuotaBusinessLimit = merchantLimitService.getMerchantLimitConfigByMerchantId(merchantId);
        // 用户限额比例
        BigDecimal userQuotaRatio = rcsQuotaBusinessLimit.getUserQuotaRatio();
        // 特殊用户百分比
        BigDecimal percentage = getUserLimitPercentage(userId);

        Map<String, Map<String, BigDecimal>> userDailyLimit = getUserDailyLimit(merchantId, userId, sportId, userQuotaRatio, percentage);
        Map<String, BigDecimal> userDailyTotalLimitMap = userDailyLimit.get(USER_DAILY_TOTAL_LIMIT);
        Map<String, BigDecimal> userDailyLimitMap = userDailyLimit.get(USER_DAILY_LIMIT);

        List<Map<String, BigDecimal>> mapList = Lists.newArrayList();
        mapList.add(userDailyTotalLimitMap);
        mapList.add(userDailyLimitMap);

        // 用户特殊限额类型
        String type = getUserSpecialLimitType(userId);
        resDto.setUserSpecialLimitType(type);

        Integer tournamentLevel = getTournamentLevel(matchInfo);
        Map<String, String> userUsedMap = getUserUsed(merchantId, userId, matchInfo);

        Map<String, BigDecimal> userSingleMatchLimitMap = getUserSingleMatchLimit(userId, matchInfo, tournamentLevel, type, userUsedMap, userQuotaRatio, percentage);
        mapList.add(userSingleMatchLimitMap);

        Map<String, BigDecimal> userPlayLimitMap = getUserPlayLimit(matchInfo, tournamentLevel, playId, userUsedMap, userQuotaRatio, percentage, userId);
        mapList.add(userPlayLimitMap);

        BigDecimal minAvailable = mapList.stream().map(this::getAvailableLimit).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        log.info("用户单关可用额度：minAvailable={},list={}", minAvailable.toPlainString(), JSON.toJSONString(mapList));

        resDto.setPlayLimit(userPlayLimitMap.get(LIMIT));
        resDto.setPlayAvailableLimit(minAvailable);
        return resDto;
    }

    /**
     * 获取用户单日限额
     *
     * @param businessId
     * @param userId
     * @param sportId
     * @param userQuotaRatio
     * @param percentage
     * @return
     */
    private Map<String, Map<String, BigDecimal>> getUserDailyLimit(String businessId, String userId, Long sportId, BigDecimal userQuotaRatio, BigDecimal percentage) {
        List<RcsQuotaUserDailyQuota> list = rcsQuotaUserDailyQuotaMapper.selectList(null);
        Map<Integer, BigDecimal> singleDailyLimitMap = list.stream().
                collect(Collectors.toMap(RcsQuotaUserDailyQuota::getSportId, limit -> limit.getDayCompensation().multiply(userQuotaRatio).multiply(percentage)));

        String dateExpect = DateUtils.getDateExpect(System.currentTimeMillis());
        String dayCompensationKey = LimitRedisKeys.getDayCompensationKey(dateExpect, businessId, userId);
        Map<String, String> hash = (Map<String, String>) redisClient.hGetAllToObj(dayCompensationKey);
        log.info("::{}::可用额度查询-单关-单日限额已用：key={},hash={}", userId, dayCompensationKey, JSON.toJSONString(hash));
        if (CollectionUtils.isEmpty(hash)) {
            hash = Maps.newHashMap();
        }

        // 用户单日总限额
        String valueTotal = hash.get(LimitRedisKeys.TOTAL_FIELD);
        BigDecimal usedTotal = CommonUtils.toBigDecimal(valueTotal).divide(AMOUNT_UNIT, 2, RoundingMode.HALF_UP);
        BigDecimal limitTotal = singleDailyLimitMap.getOrDefault(0, BigDecimal.ZERO);
        Map<String, BigDecimal> dailyTotalLimitMap = Maps.newHashMap();
        dailyTotalLimitMap.put(LIMIT, limitTotal);
        dailyTotalLimitMap.put(USED, usedTotal);
        Map<String, Map<String, BigDecimal>> map = Maps.newHashMap();
        map.put(USER_DAILY_TOTAL_LIMIT, dailyTotalLimitMap);
        log.info("::{}::可用额度查询-单关-单日总限额：{}", userId, JSON.toJSONString(dailyTotalLimitMap));
        if (Objects.nonNull(sportId)) {
            // 用户单日限额
            String value = hash.get(String.valueOf(sportId));
            BigDecimal used = CommonUtils.toBigDecimal(value).divide(AMOUNT_UNIT, 2, RoundingMode.HALF_UP);
            Integer key = singleDailyLimitMap.containsKey(sportId.intValue()) ? sportId.intValue() : -1;
            BigDecimal limit = singleDailyLimitMap.getOrDefault(key, BigDecimal.ZERO);
            Map<String, BigDecimal> dailyLimitMap = Maps.newHashMap();
            dailyLimitMap.put(LIMIT, limit);
            dailyLimitMap.put(USED, used);
            map.put(USER_DAILY_LIMIT, dailyLimitMap);
            log.info("::{}::可用额度查询-单关-单日赛种限额：{}", userId, JSON.toJSONString(dailyLimitMap));
        }
        return map;
    }

    private Integer getTournamentLevel(StandardMatchInfo matchInfo) {
        StandardSportTournament standardSportTournament = standardSportTournamentMapper.selectById(matchInfo.getStandardTournamentId());
        Integer tournamentLevel = matchInfo.getTournamentLevel();
        if (standardSportTournament != null) {
            tournamentLevel = standardSportTournament.getTournamentLevel();
        }
        return tournamentLevel;
    }

    /**
     * 获取用户已用限额
     *
     * @param businessId
     * @param userId
     * @param matchInfo
     * @return
     */
    private Map<String, String> getUserUsed(String businessId, String userId, StandardMatchInfo matchInfo) {
        Long sportId = matchInfo.getSportId();
        Long matchId = matchInfo.getId();
        String matchDateExpect = DateUtils.getDateExpect(matchInfo.getBeginTime());
        String isScroll = 0 == matchInfo.getMatchType() ? "1" : "0";
        if (matchInfo.getMatchStatus() == 3) {
            //赛事结束使用滚球的已用额度做计算
            isScroll = "1";
        }
        String userSingleMatchHashKey = LimitRedisKeys.getUserSingleMatchHashKey(matchDateExpect, businessId, String.valueOf(sportId), userId, String.valueOf(matchId), isScroll);
        Map<String, String> map = (Map<String, String>) redisClient.hGetAllToObj(userSingleMatchHashKey);
        log.info("::{}::可用额度查询-单关-用户单场已用：{}，key：{}", userId, JSON.toJSONString(map), userSingleMatchHashKey);
        if (CollectionUtils.isEmpty(map)) {
            map = Maps.newHashMap();
        }
        return map;
    }

    /**
     * 获取用户单场限额
     *
     * @param userId
     * @param matchInfo
     * @param tournamentLevel
     * @param userSpecialLimitType
     * @param userUsedMap
     * @param userQuotaRatio
     * @param percentage
     * @return
     */
    private Map<String, BigDecimal> getUserSingleMatchLimit(String userId, StandardMatchInfo matchInfo, Integer tournamentLevel, String userSpecialLimitType, Map<String, String> userUsedMap, BigDecimal userQuotaRatio, BigDecimal percentage) {
        Long sportId = matchInfo.getSportId();
        Long matchId = matchInfo.getId();
        int matchType = matchInfo.getMatchType();
        if (matchInfo.getMatchStatus() == 3) {
            matchType = 0;
        }
        BigDecimal userSingleMatchLimit = null;
        if (matchInfo.getDataSourceCode().equals(DataSourceEnum.RC.getDataSource())){//如果为C01赛事，直接返回固定限额值
              userSingleMatchLimit = new BigDecimal(readCatLimitUser).multiply(userQuotaRatio).multiply(percentage);
        }else{
            userSingleMatchLimit = getUserSingleMatchLimit(sportId, tournamentLevel, matchId, matchType).multiply(userQuotaRatio).multiply(percentage);
        }
        if ("3".equals(userSpecialLimitType) || "4".equals(userSpecialLimitType)) {
            BigDecimal specialMatchUserAmount = getSpecialMatchUserAmount(userId, String.valueOf(sportId)).divide(AMOUNT_UNIT, 2, RoundingMode.HALF_UP);
            if (specialMatchUserAmount.compareTo(userSingleMatchLimit) < 0) {
                userSingleMatchLimit = specialMatchUserAmount;
                log.info("::{}::可用额度查询-单关-用户单场取特殊限额单场：{}", userId, userSingleMatchLimit);
            }
        }
        String singleMatchUsed = userUsedMap.get(LimitRedisKeys.USER_SINGLE_MATCH_HASH_FIELD);
        BigDecimal userSingleMatchUsed = CommonUtils.toBigDecimal(singleMatchUsed).divide(AMOUNT_UNIT, 2, RoundingMode.HALF_UP);

        Map<String, BigDecimal> userSingleMatchLimitMap = Maps.newHashMap();
        userSingleMatchLimitMap.put(LIMIT, userSingleMatchLimit);
        userSingleMatchLimitMap.put(USED, userSingleMatchUsed);
        log.info("::{}::可用额度查询-单关-用户单场限额：{}", userId, JSON.toJSONString(userSingleMatchLimitMap));
        return userSingleMatchLimitMap;
    }

    /**
     * 用户玩法限额
     *
     * @param matchInfo
     * @param tournamentLevel
     * @param playId
     * @param userUsedMap
     * @param userQuotaRatio
     * @param percentage
     * @return
     */
    private Map<String, BigDecimal> getUserPlayLimit(StandardMatchInfo matchInfo, Integer tournamentLevel, Long playId, Map<String, String> userUsedMap,
                                                     BigDecimal userQuotaRatio, BigDecimal percentage, String userId) {
        Long sportId = matchInfo.getSportId();
        Long matchId = matchInfo.getId();
        List<RcsQuotaUserSingleNoteVo> singleNoteData = getUserSingleNoteList(sportId, tournamentLevel, matchId);
        int matchType = matchInfo.getMatchType();
        if (matchInfo.getMatchStatus() == 3) {
            matchType = 0;
        }
        BigDecimal playLimit = getUserPlayLimit(singleNoteData, playId.intValue(), matchType).multiply(userQuotaRatio).multiply(percentage);
        log.info("::{}::可用额度查询-单关-用户玩法限额：{},配置：{}", userId, playLimit, JSONObject.toJSONString(singleNoteData));
        int isScroll = 0 == matchInfo.getMatchType() ? 1 : 0;
        if (matchInfo.getMatchStatus() == 3) {
            isScroll = 1;
        }
        String start = playId + "_" + isScroll + "_";
        String playValue = "";
        for (Map.Entry<String, String> entry : userUsedMap.entrySet()) {
            if (entry.getKey().startsWith(start)) {
                playValue = entry.getValue();
            }
        }
        BigDecimal userPlayUsed = CommonUtils.toBigDecimal(playValue).divide(AMOUNT_UNIT, 2, RoundingMode.HALF_UP);

        Map<String, BigDecimal> userPlayLimitMap = Maps.newHashMap();
        userPlayLimitMap.put(LIMIT, playLimit);
        userPlayLimitMap.put(USED, userPlayUsed);
        return userPlayLimitMap;
    }

    private BigDecimal getUserPlayLimit(List<RcsQuotaUserSingleNoteVo> list, int playId, int matchType) {
        BigDecimal playLimit = null;
        BigDecimal otherPlayLimit = null;
        for (RcsQuotaUserSingleNoteVo config : list) {
            if (0 == matchType) {
                if (1 == config.getBetState()) {
                    if (config.getPlayId() == -1) {
                        otherPlayLimit = config.getCumulativeCompensationPlaying();
                    }
                    if (config.getPlayId() == playId) {
                        playLimit = config.getCumulativeCompensationPlaying();
                    }
                }
            } else {
                if (0 == config.getBetState()) {
                    if (config.getPlayId() == -1) {
                        otherPlayLimit = config.getCumulativeCompensationPlaying();
                    }
                    if (config.getPlayId() == playId) {
                        playLimit = config.getCumulativeCompensationPlaying();
                    }
                }
            }
        }
        if (playLimit == null) {
            playLimit = otherPlayLimit;
        }
        if (playLimit == null) {
            playLimit = BigDecimal.ZERO;
        }
        return playLimit;
    }

    private BigDecimal getUserSingleMatchLimit(Long sportId, Integer tournamentLevel, Long matchId, int matchType) {
        MatchLimitDataReqVo reqVo = new MatchLimitDataReqVo();
        reqVo.setSportId(sportId.intValue());
        reqVo.setTournamentLevel(convertTournamentLevel(tournamentLevel));
        reqVo.setMatchId(matchId);
        reqVo.setDataTypeList(Lists.newArrayList(LimitDataTypeEnum.USER_SINGLE_LIMIT.getType()));
        RcsQuotaUserSingleSiteQuotaVo singleQuotaData = limitService.getRcsQuotaUserSingleSiteQuotaData(reqVo);
        if (singleQuotaData == null) {
            log.info("额度查询-获取用户单场限额-其它赛种");
            reqVo.setSportId(-1);
            singleQuotaData = limitService.getRcsQuotaUserSingleSiteQuotaData(reqVo);
        }
        if (singleQuotaData == null) {
            throw new RcsServiceException("未查询到用户单场限额");
        }
        BigDecimal limit;
        if (0 == matchType) {
            limit = singleQuotaData.getLiveUserSingleSiteQuota();
        } else {
            limit = singleQuotaData.getEarlyUserSingleSiteQuota();
        }
        log.info("额度查询-获取用户单场限额：sportId={},tournamentLevel={},matchId={},matchType={},limit={},config={}", sportId, tournamentLevel, matchId, matchType, limit.toPlainString(), JSON.toJSONString(singleQuotaData));
        return limit;
    }

    private List<RcsQuotaUserSingleNoteVo> getUserSingleNoteList(Long sportId, Integer tournamentLevel, Long matchId) {
        MatchLimitDataReqVo vo = new MatchLimitDataReqVo();
        vo.setSportId(sportId.intValue());
        vo.setTournamentLevel(tournamentLevel);
        vo.setMatchId(matchId);
        vo.setDataTypeList(Lists.newArrayList(LimitDataTypeEnum.USER_SINGLE_BET_LIMIT.getType()));
        List<RcsQuotaUserSingleNoteVo> list = limitService.getRcsQuotaUserSingleNoteData(vo);
        if (CollectionUtils.isEmpty(list)) {
            log.info("额度查询-获取用户单注单关限额-其它赛种");
            vo.setSportId(-1);
            list = limitService.getRcsQuotaUserSingleNoteData(vo);
        }
        if (CollectionUtils.isEmpty(list)) {
            throw new RcsServiceException("未查询到用户单注单关限额");
        }
        log.info("额度查询-获取用户单注单关限额：sportId={},tournamentLevel={},matchId={},config={}", sportId, tournamentLevel, matchId, JSON.toJSONString(list));
        return list;
    }

    /**
     * 获取用户特殊限额
     *
     * @param uid
     * @return
     */
    private BigDecimal getUserLimitPercentage(String uid) {
        try {
            String key = LimitRedisKeys.getUserSpecialLimitKey(uid);
            String type = RcsLocalCacheUtils.getValue(key, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, redisClient::hGet);
            String percentage = RcsLocalCacheUtils.getValue(key, LimitRedisKeys.USER_PERCENTAGE_LIMIT_FIELD, redisClient::hGet);
            log.info("::{}::额度查询-Redis获取用户特殊限额：key={},type={},percentage={}", uid, key, type, percentage);
            /*if(StringUtils.isBlank(type) || StringUtils.isBlank(percentage)){
                setUserSpecialBetLimitConfigCache(uid);
                type = RcsLocalCacheUtils.getValue(key, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, redisClient::hGet);
                percentage = RcsLocalCacheUtils.getValue(key, LimitRedisKeys.USER_PERCENTAGE_LIMIT_FIELD, redisClient::hGet);
            }*/
            if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(percentage) && "2".equals(type) && StringUtils.isNotBlank(percentage)) {
                log.info("::{}::额度查询-特殊百分比~~~生效：type={},percentage={}", uid, type, percentage);
                return new BigDecimal(percentage);
            }
            String userTagKey = LimitRedisKeys.getTagtKey();
            String userTag = RcsLocalCacheUtils.getValue(userTagKey + uid, redisClient::get);
            if (StringUtils.isBlank(userTag)) {
                userTag = limitService.getUserTagByUserId(NumberUtils.toLong(uid)).toString();
            }
            // 如果特殊限额取不到  则从标签去取比例    0 无  1标签限额  2特殊百分比限额 3特殊单注单场限额 4特殊vip限额
            String tagKey = LimitRedisKeys.getUserTagLimitKey(String.valueOf(userTag));

            //5分钟本地缓存
            percentage = RcsLocalCacheUtils.getValue(tagKey + LimitRedisKeys.USER_PERCENTAGE_LIMIT_FIELD, redisClient::get);
            if (StringUtils.isBlank(percentage)) {
                percentage = limitService.getTagPercentage(Integer.parseInt(userTag));
                RcsLocalCacheUtils.timedCache.put(tagKey + LimitRedisKeys.USER_PERCENTAGE_LIMIT_FIELD, "1");
                redisClient.setExpiry(tagKey + LimitRedisKeys.USER_PERCENTAGE_LIMIT_FIELD, percentage, 30 * 24 * 60 * 60L);
            }
            log.info("::{}::额度查询-Redis获取标签限额：key={},field={},value={}", uid, tagKey, LimitRedisKeys.USER_PERCENTAGE_LIMIT_FIELD, percentage);
            if ((StringUtils.isBlank(type) || "1".equals(type)) && StringUtils.isNotBlank(percentage)) {
                log.info("::{}::额度查询-标签限额~~~生效：type={},percentage={}", uid, type, percentage);
                return new BigDecimal(percentage);
            }
            if (StringUtils.isBlank(percentage)) {
                RcsLocalCacheUtils.timedCache.put(tagKey + LimitRedisKeys.USER_PERCENTAGE_LIMIT_FIELD, "1");
            }
        }catch (Exception e){
            log.error("::{}::{}", uid, e.getMessage(), e);
        }
        return new BigDecimal("1");
    }

    /**
     * 检查用户特殊限额缓存兜底
     * @param userId
     */
    public void setUserSpecialBetLimitConfigCache(String userId) {
        String key = LimitRedisKeys.getUserSpecialLimitKey(userId);
        String type = null;
        //组装参数请求rpc
        Request<RcsUserSpecialBetLimitConfigDTO> request = new Request<>();
        RcsUserSpecialBetLimitConfigDTO config = new RcsUserSpecialBetLimitConfigDTO();
        config.setUserId(Long.valueOf(userId));
        config.setStatus(1);
        request.setData(config);
        request.setGlobalId(MDC.get("X-B3-TraceId"));
        Response<List<RcsUserSpecialBetLimitConfigDTO>> response;
        try {
            response = limitApiService.queryUserSpecialBetLimitConfig(request);
            log.info("::{}::获取数据库特殊限额数据返回:{}", userId, JSONObject.toJSONString(response));
            if (response == null || response.getCode() != Response.SUCCESS) {
                log.warn("::{}::获取数据库特殊限额数据失败", userId);
            } else {
                if (response.getData() != null && response.getData().size() > 0) {
                    List<RcsUserSpecialBetLimitConfigDTO> list = response.getData();
                    //设置type，一个用户只能是一种类型，所以有多条数据类型也是一致
                    type = list.get(0).getSpecialBettingLimitType().toString();
                    Map<String, String> data = new HashMap<>();
                    redisClient.hSet(key,"type",type);
                    for (RcsUserSpecialBetLimitConfigDTO limitConfig : list) {
                        BigDecimal percentageLimit = limitConfig.getPercentageLimit();
                        if (type.equals("2") && null != percentageLimit) {
                            //限额百分比，放入缓存
                            String percentage = LimitRedisKeys.USER_PERCENTAGE_LIMIT_FIELD;
                            redisClient.hSet(key, percentage, String.valueOf(percentageLimit.doubleValue()));
                        } else {
                            //特殊单注单场限额、特殊VIP限额 放入缓存
                            if (limitConfig.getSingleNoteClaimLimit() != null) {
                                //单注赔付限额
                                String Hkey2 = "%s_%s_single_note_claim_limit";
                                String format = String.format(Hkey2, limitConfig.getOrderType(), limitConfig.getSportId());
                                redisClient.hSet(key,format, String.valueOf(limitConfig.getSingleNoteClaimLimit() * 100));
                            }
                            if (limitConfig.getSingleGameClaimLimit() != null) {
                                //单场赔付限额
                                String Hkey3 = "%s_%s_single_game_claim_limit";
                                String format = String.format(Hkey3, limitConfig.getOrderType(), limitConfig.getSportId());
                                redisClient.hSet(key,Hkey3, String.valueOf(limitConfig.getSingleGameClaimLimit() * 100));
                            }
                        }
                    }
                    redisClient.expireKey(key, 30 * 24 * 60 * 60);
                    data.forEach((k, v) -> RcsLocalCacheUtils.timedCache.put(key + k, v));
                }
            }
        } catch (Exception e) {
            log.error("::{}::设置用户特殊限额缓存异常:", userId, e);
        }

    }

    private String getUserSpecialLimitType(String userId) {
        // 用户特殊限额类型,0-无,1-标签限额,2-特殊百分比限额,3-特殊单注单场限额,4-特殊vip限额
        String userSpecialLimitKey = LimitRedisKeys.getUserSpecialLimitKey(userId);
        String userSpecialLimitType = RcsLocalCacheUtils.getValue(userSpecialLimitKey, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, redisClient::hGet);
        log.info("额度查询-Redis获取用户特殊限额类型：key={},field={},value={}", userSpecialLimitKey, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, userSpecialLimitType);
        return userSpecialLimitType;
    }

    private BigDecimal getSingleDayClaimLimit(String userId, BigDecimal defaultValue) {
        String key = LimitRedisKeys.getUserSpecialLimitKey(userId);
        String field = LimitRedisKeys.getSingleGameClaimLimitField("2", "-1");
        String value = RcsLocalCacheUtils.getValue(key, field, redisClient::hGet);
        log.info("额度查询-串关-用户特殊限额-单日赔付限额：key={},field={},value={}", key, field, value);
        if (StringUtils.isNotBlank(value)) {
            return CommonUtils.toBigDecimal(value).divide(AMOUNT_UNIT, 2, RoundingMode.HALF_UP);
        }
        return defaultValue;
    }

    public BigDecimal getSpecialMatchUserAmount(String userId, String sportId) {
        BigDecimal max = new BigDecimal(Long.MAX_VALUE);
        String key = LimitRedisKeys.getUserSpecialLimitKey(userId);
        String type = RcsLocalCacheUtils.getValue(key, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, redisClient::hGet);
        log.info("额度查询-Redis获取用户特殊限额类型：key={},field={},value={}", key, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, type);
        if (!"3".equals(type) && !"4".equals(type)) {
            return max;
        }
        // 先查全部的配置
        String specialUserAmountKey = "1_-1_single_game_claim_limit";
        String specialUserAmountStr = RcsLocalCacheUtils.getValue(key, specialUserAmountKey, redisClient::hGet);
        log.info("额度查询-特殊会员单场-全部单注赔付：key={},field={},value={}", key, specialUserAmountKey, specialUserAmountStr);
        if (StringUtils.isNotBlank(specialUserAmountStr) && !"null".equals(specialUserAmountStr)) {
            return CommonUtils.toBigDecimal(specialUserAmountStr, max);
        }
        // 再查赛种的配置
        specialUserAmountKey = "1_" + sportId + "_single_game_claim_limit";
        specialUserAmountStr = RcsLocalCacheUtils.getValue(key, specialUserAmountKey, redisClient::hGet);
        log.info("额度查询-特殊会员单场-赛种单场赔付：key={},field={},value={}", key, specialUserAmountKey, specialUserAmountStr);
        if (StringUtils.isNotBlank(specialUserAmountStr) && !"null".equals(specialUserAmountStr)) {
            return CommonUtils.toBigDecimal(specialUserAmountStr, max);
        }
        // 足/篮 不再往下读取其他配置
        if ("1".equals(sportId) || "2".equals(sportId)) {
            log.info("额度查询-足/篮 不再往下读取其他配置");
            return max;
        }
        // 再查其他赛种的配置
        specialUserAmountKey = "1_0_single_game_claim_limit";
        specialUserAmountStr = RcsLocalCacheUtils.getValue(key, specialUserAmountKey, redisClient::hGet);
        log.info("额度查询-特殊会员单场-其他单场赔付：key={},field={},value={}", key, specialUserAmountKey, specialUserAmountStr);
        if (StringUtils.isNotBlank(specialUserAmountStr) && !"null".equals(specialUserAmountStr)) {
            return CommonUtils.toBigDecimal(specialUserAmountStr, max);
        }
        return max;
    }

    private int convertTournamentLevel(Integer tournamentLevel) {
        if (tournamentLevel == null || tournamentLevel < 1 || tournamentLevel > 20) {
            // -1表示未评级
            return -1;
        }
        return tournamentLevel;
    }

    private BigDecimal getAvailableLimit(Map<String, BigDecimal> limitMap) {
        return limitMap.get(LIMIT).subtract(limitMap.get(USED)).setScale(2, RoundingMode.HALF_UP);
    }

    private List<Integer> seriesTypeList = Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9, 10);
    private List<Integer> sportList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);


    public StrayUserAvailableLimitResVo userStrayLimitQuery(AvailableLimitQueryReqVo reqVo) {
        StrayUserAvailableLimitResVo strayUserAvailableLimitResVo = new StrayUserAvailableLimitResVo();
        Long userId = reqVo.getUserId();
        TUser tUser = tUserMapper.selectByUserId(userId);
        if (tUser == null) {
            throw new RcsServiceException("用户ID输入不正确");
        }
        //查询商户信息
        RcsQuotaBusinessLimit rcsQuotaBusinessLimit = merchantLimitService.getMerchantLimitConfig(reqVo.getMerchantCode());
        String businessId = rcsQuotaBusinessLimit.getBusinessId();
        if (StringUtils.isNotBlank(reqVo.getMatchManageId())) {
            //查询赛事单场已用额度
            BigDecimal userStrayLimitPlay = this.getMatchSingleLimit(reqVo.getMatchManageId(), userId, businessId);
            if (userStrayLimitPlay != null) {
                strayUserAvailableLimitResVo.setMatchSingleLimit(new BigDecimal(String.valueOf(rcsQuotaBusinessLimit.getUserSingleStrayLimit())).subtract(userStrayLimitPlay.divide(new BigDecimal(100), 0, BigDecimal.ROUND_DOWN)));
            }
        }
        String type = getUserSpecialLimitType(String.valueOf(userId));
        // 用户限额比例
        BigDecimal userQuotaRatio = rcsQuotaBusinessLimit.getUserStrayQuotaRatio();
        // 特殊用户百分比
        BigDecimal percentage = getUserLimitPercentage(String.valueOf(userId));
        log.info("::{}::可用额度查询-串关-用户限额比例：{}，限额百分比：{}", userId, userQuotaRatio, percentage);

        BigDecimal limitTotal;
        if ("3".equals(type) || "4".equals(type)) {
            limitTotal = getSingleDayClaimLimit(String.valueOf(userId), new BigDecimal(Integer.MAX_VALUE));
            log.info("::{}::可用额度查询-串关-取特殊单日总限额：{}", userId, limitTotal);
        } else {
            RcsMerchantSeriesConfig rcsMerchantSeriesConfig = rcsMerchantSeriesConfigMapper.selectOne(new LambdaQueryWrapper<RcsMerchantSeriesConfig>().eq(RcsMerchantSeriesConfig::getStatus, YesNoEnum.N).
                    orderByDesc(RcsMerchantSeriesConfig::getCreateTime).last("LIMIT 1"));
            if (rcsMerchantSeriesConfig != null) {
                limitTotal = rcsMerchantSeriesConfig.getSeriesPayoutTotalAmount().multiply(userQuotaRatio).multiply(percentage);
                log.info("::{}::可用额度查询-串关-单日总限额：{}", userId, limitTotal);
            } else {
                limitTotal = BigDecimal.ZERO;
            }
        }
        //获取当前日期
//        String dateExpect = DateUtils.DateToString(new Date());
        String dateExpect = DateUtils.getDateExpect(System.currentTimeMillis());
        String strayPaymentKey = String.format(LimitRedisKeys.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_KEY, businessId, userId, dateExpect);
        String strayTotalValue = redisClient.get(strayPaymentKey);
        BigDecimal usedTotal = CommonUtils.toBigDecimal(strayTotalValue).divide(AMOUNT_UNIT, 2, RoundingMode.HALF_UP);
        log.info("::{}::可用额度查询-串关-单日总限额已用：{}", userId, usedTotal);

        strayUserAvailableLimitResVo.setDailyTotalAvailableLimit(limitTotal.subtract(usedTotal).setScale(2, RoundingMode.HALF_DOWN));
        strayUserAvailableLimitResVo.setUserSpecialLimitType(type);
        List<StrayUserSeriesLimitResVo> strayUserSeriesLimitResVoList = new ArrayList<>();
        List<StrayUserSportLimitResVo> strayUserSportLimitResVos = new ArrayList<>();
        //赛种
        sportList.forEach(s -> {
            StrayUserSportLimitResVo strayUserSeriesLimitResVo = new StrayUserSportLimitResVo();
            strayUserSeriesLimitResVo.setSportId(s);
            //查询数据库配置额度
            RcsMerchantSportLimit rcsMerchantSportLimit = rcsMerchantSportLimitMapper.selectOne(new LambdaQueryWrapper<RcsMerchantSportLimit>().eq(RcsMerchantSportLimit::getSportId, s));
            //获取缓存里面已用的值
            String sportTypeKey = String.format(LimitRedisKeys.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_RACE_KEY, businessId, userId, s, dateExpect);
            String cashStr = redisClient.get(sportTypeKey);
            BigDecimal cashAmount = BigDecimal.ZERO;
            if (StringUtils.isNotBlank(cashStr)) {
                cashAmount = new BigDecimal(cashStr);
            }
            BigDecimal strayLimitAmount = rcsMerchantSportLimit.getStrayLimitAmount().multiply(userQuotaRatio).multiply(percentage);
            strayUserSeriesLimitResVo.setDailyStrayLimit(strayLimitAmount.subtract(cashAmount.divide(new BigDecimal(100), 0, BigDecimal.ROUND_DOWN)));
            strayUserSportLimitResVos.add(strayUserSeriesLimitResVo);
        });
        //串关类型
        seriesTypeList.forEach(s -> {
            StrayUserSeriesLimitResVo strayUserSportLimitResVo = new StrayUserSeriesLimitResVo();
            strayUserSportLimitResVo.setSeriesType(s);
            //查询数据库配置
            RcsMerchantLimitCompensation rcsMerchantLimitCompensation = rcsMerchantLimitCompensationMapper.selectOne(new LambdaQueryWrapper<RcsMerchantLimitCompensation>().eq(RcsMerchantLimitCompensation::getSeriesType, s));
            String strayTypePaymentKey = String.format(LimitRedisKeys.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_SERIES_KEY, businessId, userId, s, dateExpect);
            String strayTypeStr = redisClient.get(strayTypePaymentKey);
            BigDecimal strayTypeAmount = BigDecimal.ZERO;
            if (StringUtils.isNotBlank(strayTypeStr)) {
                strayTypeAmount = new BigDecimal(strayTypeStr);
            }
            BigDecimal seriesLimitAmount = rcsMerchantLimitCompensation.getSeriesLimitAmount().multiply(userQuotaRatio).multiply(percentage);
            strayUserSportLimitResVo.setDailyStrayLimit(seriesLimitAmount.subtract(strayTypeAmount.divide(new BigDecimal(100), 0, BigDecimal.ROUND_DOWN)));
            strayUserSeriesLimitResVoList.add(strayUserSportLimitResVo);
        });
        strayUserAvailableLimitResVo.setStrayUserSeriesLimitResVoList(strayUserSeriesLimitResVoList);
        strayUserAvailableLimitResVo.setStrayUserSportLimitResVoList(strayUserSportLimitResVos);
        return strayUserAvailableLimitResVo;
    }
}
