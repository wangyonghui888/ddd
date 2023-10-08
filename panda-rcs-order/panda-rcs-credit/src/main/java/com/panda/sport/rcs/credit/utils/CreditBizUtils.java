package com.panda.sport.rcs.credit.utils;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.panda.sport.data.rcs.dto.credit.CreditConfigDto;
import com.panda.sport.data.rcs.dto.credit.CreditSeriesConfigDto;
import com.panda.sport.data.rcs.dto.credit.CreditSingleMatchConfigDto;
import com.panda.sport.data.rcs.dto.credit.CreditSinglePlayBetConfigDto;
import com.panda.sport.data.rcs.dto.credit.CreditSinglePlayConfigDto;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSeriesLimit;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSingleMatchLimit;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSinglePlayLimit;
import com.panda.sport.rcs.entity.dto.CreditConfigHttpQueryDto;
import com.panda.sport.rcs.enums.BetStageEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.pojo.credit.RcsCreditSinglePlayBetLimit;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用业务工具类
 * @Author : Paca
 * @Date : 2021-05-02 13:50
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public final class CreditBizUtils {

    private CreditBizUtils() {
    }

    public static CreditSeriesConfigDto toCreditSeriesConfigDto(RcsCreditSeriesLimit seriesLimit) {
        CreditSeriesConfigDto seriesConfigDto = new CreditSeriesConfigDto();
        seriesConfigDto.setSeriesType(seriesLimit.getSeriesType());
        seriesConfigDto.setValue(seriesLimit.getValue());
        return seriesConfigDto;
    }

    public static RcsCreditSeriesLimit toRcsCreditSeriesLimit(Long merchantId, String creditId, Long userId, CreditSeriesConfigDto seriesConfigDto) {
        RcsCreditSeriesLimit seriesLimit = new RcsCreditSeriesLimit();
        seriesLimit.setMerchantId(merchantId);
        seriesLimit.setCreditId(creditId);
        seriesLimit.setUserId(userId);
        seriesLimit.setSeriesType(seriesConfigDto.getSeriesType());
        seriesLimit.setValue(seriesConfigDto.getValue());
        return seriesLimit;
    }

    public static CreditSingleMatchConfigDto toCreditSingleMatchConfigDto(RcsCreditSingleMatchLimit singleMatchLimit) {
        CreditSingleMatchConfigDto singleMatchConfigDto = new CreditSingleMatchConfigDto();
        singleMatchConfigDto.setSportId(singleMatchLimit.getSportId());
        singleMatchConfigDto.setTournamentLevel(singleMatchLimit.getTournamentLevel());
        singleMatchConfigDto.setValue(singleMatchLimit.getValue());
        return singleMatchConfigDto;
    }

    public static RcsCreditSingleMatchLimit toRcsCreditSingleMatchLimit(Long merchantId, String creditId, CreditSingleMatchConfigDto singleMatchConfigDto) {
        RcsCreditSingleMatchLimit singleMatchLimit = new RcsCreditSingleMatchLimit();
        singleMatchLimit.setMerchantId(merchantId);
        singleMatchLimit.setCreditId(creditId);
        singleMatchLimit.setSportId(singleMatchConfigDto.getSportId());
        singleMatchLimit.setTournamentLevel(singleMatchConfigDto.getTournamentLevel());
        singleMatchLimit.setValue(singleMatchConfigDto.getValue());
        return singleMatchLimit;
    }

    public static CreditSinglePlayConfigDto toCreditSinglePlayConfigDto(RcsCreditSinglePlayLimit singlePlayLimit) {
        CreditSinglePlayConfigDto singlePlayConfigDto = new CreditSinglePlayConfigDto();
        singlePlayConfigDto.setSportId(singlePlayLimit.getSportId());
        singlePlayConfigDto.setPlayClassify(singlePlayLimit.getPlayClassify());
        singlePlayConfigDto.setBetStage(singlePlayLimit.getBetStage());
        singlePlayConfigDto.setTournamentLevel(singlePlayLimit.getTournamentLevel());
        singlePlayConfigDto.setValue(singlePlayLimit.getValue());
        return singlePlayConfigDto;
    }

    public static RcsCreditSinglePlayLimit toRcsCreditSinglePlayLimit(Long merchantId, String creditId, Long userId, CreditSinglePlayConfigDto singlePlayConfigDto) {
        RcsCreditSinglePlayLimit singlePlayLimit = new RcsCreditSinglePlayLimit();
        singlePlayLimit.setMerchantId(merchantId);
        singlePlayLimit.setCreditId(creditId);
        singlePlayLimit.setUserId(userId);
        singlePlayLimit.setSportId(singlePlayConfigDto.getSportId());
        singlePlayLimit.setPlayClassify(singlePlayConfigDto.getPlayClassify());
        singlePlayLimit.setBetStage(singlePlayConfigDto.getBetStage());
        singlePlayLimit.setTournamentLevel(singlePlayConfigDto.getTournamentLevel());
        singlePlayLimit.setValue(singlePlayConfigDto.getValue());
        return singlePlayLimit;
    }

    public static CreditSinglePlayBetConfigDto toCreditSinglePlayBetConfigDto(RcsCreditSinglePlayBetLimit singlePlayBetLimit) {
        CreditSinglePlayBetConfigDto singlePlayBetConfigDto = new CreditSinglePlayBetConfigDto();
        singlePlayBetConfigDto.setSportId(singlePlayBetLimit.getSportId());
        singlePlayBetConfigDto.setPlayClassify(singlePlayBetLimit.getPlayClassify());
        singlePlayBetConfigDto.setBetStage(singlePlayBetLimit.getBetStage());
        singlePlayBetConfigDto.setTournamentLevel(singlePlayBetLimit.getTournamentLevel());
        singlePlayBetConfigDto.setValue(singlePlayBetLimit.getValue());
        return singlePlayBetConfigDto;
    }

    public static RcsCreditSinglePlayBetLimit toRcsCreditSinglePlayBetLimit(Long merchantId, String creditId, Long userId, CreditSinglePlayBetConfigDto singlePlayBetConfigDto) {
        RcsCreditSinglePlayBetLimit singlePlayBetLimit = new RcsCreditSinglePlayBetLimit();
        singlePlayBetLimit.setMerchantId(merchantId);
        singlePlayBetLimit.setCreditId(creditId);
        singlePlayBetLimit.setUserId(userId);
        singlePlayBetLimit.setSportId(singlePlayBetConfigDto.getSportId());
        singlePlayBetLimit.setPlayClassify(singlePlayBetConfigDto.getPlayClassify());
        singlePlayBetLimit.setBetStage(singlePlayBetConfigDto.getBetStage());
        singlePlayBetLimit.setTournamentLevel(singlePlayBetConfigDto.getTournamentLevel());
        singlePlayBetLimit.setValue(singlePlayBetConfigDto.getValue());
        return singlePlayBetLimit;
    }

    public static List<RcsCreditSinglePlayBetLimit> getCreditSinglePlayBetLimit(CreditConfigDto configDto) {
        if (CollectionUtils.isEmpty(configDto.getSinglePlayBetConfigList())) {
            return Lists.newArrayList();
        }
        Long merchantId = getMerchantId(configDto.getMerchantId());
        String creditId = getCreditId(configDto.getCreditId());
        Long userId = getUserId(configDto);
        return configDto.getSinglePlayBetConfigList().stream().map(config -> toRcsCreditSinglePlayBetLimit(merchantId, creditId, userId, config)).collect(Collectors.toList());
    }

    public static List<RcsCreditSinglePlayLimit> getCreditSinglePlayLimit(CreditConfigDto configDto) {
        if (CollectionUtils.isEmpty(configDto.getSinglePlayConfigList())) {
            return Lists.newArrayList();
        }
        Long merchantId = getMerchantId(configDto.getMerchantId());
        String creditId = getCreditId(configDto.getCreditId());
        Long userId = getUserId(configDto);
        return configDto.getSinglePlayConfigList().stream().map(config -> toRcsCreditSinglePlayLimit(merchantId, creditId, userId, config)).collect(Collectors.toList());
    }

    public static List<RcsCreditSingleMatchLimit> getCreditSingleMatchLimit(CreditConfigDto configDto) {
        if (CollectionUtils.isEmpty(configDto.getSingleMatchConfigList())) {
            return Lists.newArrayList();
        }
        Long merchantId = getMerchantId(configDto.getMerchantId());
        String creditId = getCreditId(configDto.getCreditId());
        return configDto.getSingleMatchConfigList().stream().map(config -> toRcsCreditSingleMatchLimit(merchantId, creditId, config)).collect(Collectors.toList());
    }

    public static List<RcsCreditSeriesLimit> getCreditSeriesLimitList(CreditConfigDto configDto) {
        if (CollectionUtils.isEmpty(configDto.getSeriesConfigList())) {
            return Lists.newArrayList();
        }
        Long merchantId = getMerchantId(configDto.getMerchantId());
        String creditId = getCreditId(configDto.getCreditId());
        Long userId = getUserId(configDto);
        return configDto.getSeriesConfigList().stream().map(config -> toRcsCreditSeriesLimit(merchantId, creditId, userId, config)).collect(Collectors.toList());
    }

    public static Long getMerchantId(Long merchantId) {
        if (merchantId == null) {
            return 0L;
        }
        return merchantId;
    }

    public static String getCreditId(String creditId) {
        if (StringUtils.isBlank(creditId)) {
            return "0";
        }
        return creditId;
    }

    public static Long getUserId(CreditConfigDto configDto) {
        if (!(configDto instanceof CreditConfigHttpQueryDto)) {
            return 0L;
        }
        CreditConfigHttpQueryDto httpQueryDto = (CreditConfigHttpQueryDto) configDto;
        if (httpQueryDto.getUserId() == null) {
            return 0L;
        }
        return httpQueryDto.getUserId();
    }

    public static String convertBetStage(Integer matchType) {
        if (matchType != null && matchType == 2) {
            return BetStageEnum.LIVE.getCode();
        }
        return BetStageEnum.PRE.getCode();
    }

    /**
     * 信用联赛等级
     *
     * @param tournamentLevel
     * @return
     */
    public static Integer creditTournamentLevel(Integer tournamentLevel) {
        if (tournamentLevel == null) {
            return -1;
        }
        if (tournamentLevel >= 1 && tournamentLevel <= 6) {
            return tournamentLevel;
        }
        return -1;
    }

    /**
     * 标准联赛等级
     *
     * @param tournamentLevel
     * @return
     */
    public static Integer standardTournamentLevel(Integer tournamentLevel) {
        if (tournamentLevel != null && tournamentLevel > 0) {
            return tournamentLevel;
        }
        return -1;
    }

    /**
     * 信用赛种
     *
     * @param sportId
     * @return
     */
    public static Integer creditSportId(Integer sportId) {
        if (SportIdEnum.isFootball(sportId) || SportIdEnum.isBasketball(sportId) || SportIdEnum.isTennis(sportId)) {
            return sportId;
        }
        return -1;
    }

    /**
     * 标准赛种
     *
     * @param sportId
     * @return
     */
    public static Integer standardSportId(Integer sportId) {
        if (sportId != null && sportId > 0) {
            return sportId;
        }
        return -1;
    }
}
