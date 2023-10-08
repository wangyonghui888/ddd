package com.panda.rcs.stray.limit.entity.enums;


/**
 * <p>
 * 串关相关redis Key 枚举Key说明 Key 过期时间 1个月
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
public interface SeriesRedisKeyEnums {

    /**
     * 高风险单注赔付限额 串关类型:赛种id:联赛登记
     */
    String HIGH_RISK_KEY = "rcs:seriesType:%s:sportId:%s:tournamentLevel:%s";
    String HIGH_RISK_KEY_DAY ="rcs:merchant_high_risk_day";

    /**
     * 单日每个串关类型赔付总限额
     */
    String MERCHANT_LIMIT_COMPENSATION_KEY = "rcs:merchant_limit_compensation:%s";

    /**
     * 单日串关赔付总限额
     */
    String SERIES_PAYOUT_AND_PAICAI_TOTAL_AMOUNT = "rcs:series_payout_total_amount";


    /**
     * 高风险单注赛种投注限制 %s 赛种ID_串关类型
     */
    String MERCHANT_HIGH_SINGLE_LIMIT = "rcs:merchant_high_single_limit:%s:sportId:%s";


    /**
     * 单日串关赛种赔付总限额及总派彩限额
     */
    String MERCHANT_SPORT_LIMIT_TOTAL = "rcs:merchant_sport_limit_total:%s";

    /**
     * 单日额度用完最低可投注金额配置
     */
    String MERCHANT_LOW_LIMIT_AMOUNT = "rcs:merchant_low_limit_amount:%s";

    /**
     * 高风险单注区间最高赔付金额
     */
    String MERCHANT_INTERVAL_MAX_AMOUNT = "rcs:merchant_interval_max_amount:sportId:%s:strayType:%s";

}
