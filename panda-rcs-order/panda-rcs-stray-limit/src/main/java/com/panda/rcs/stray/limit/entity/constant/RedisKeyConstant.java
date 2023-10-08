package com.panda.rcs.stray.limit.entity.constant;

import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;


public class RedisKeyConstant {

    /**
     * 串关Redis更新记录
     * rcs:limit:redisUpdateRecord:series:订单号
     */
    public static String SERIES_REDIS_UPDATE_RECORD_KEY = "rcs:limit:redisUpdateRecord:series:%s";

    public static String TRADING_TYPE_KEY = "rcs:trading:type:%s:%s:%s";

    /**
     * 商户单日串关已用限额
     * rcs:paid:日期:busseries:商户ID
     */
    public static String PAID_DATE_BUS_SERIES_REDIS_CACHE = "rcs:paid:%s:busseries:%s";

    /**
     * 商户单日已用限额
     * rcs:paid:日期:bus:商户ID
     */
    public static String PAID_DATE_BUS_REDIS_CACHE = "rcs:paid:%s:bus:%s";

    /**
     * 商户单场串关已用限额
     * rcs:paid:日期:bus:商户ID:赛事ID
     */
    public static String PAID_DATE_BUS_REDIS_MATCH_CACHE = "rcs:user:%s:bus:%s:match:%s";

    /**
     * 商户单日限额预警标志
     *
     * @param dateExpect
     * @param merchantId
     * @return
     */
    public static String getMerchantLimitWarnSignKey(String dateExpect, Long merchantId) {
        return String.format("rcs:merchantLimitWarnSign:%s:%s", dateExpect, merchantId);
    }

    /**
     * 商户单日串关限额预警标志
     *
     * @param dateExpect
     * @param merchantId
     * @return
     */
    public static String getMerchantSeriesLimitWarnSignKey(String dateExpect, Long merchantId) {
        return String.format("rcs:merchantSeriesLimitWarnSign:%s:%s", dateExpect, merchantId);
    }

    public static final  String USER_SPECIAL_LIMIT_TYPE_FIELD = "type";
    public static final  String USER_SPECIAL_LIMIT_PERCENTAGE_FIELD = "percentage";


    public static final Integer MARKET_PLACE_EXPIRY_KEY = 30 * 24;
    public static final Integer COMPETITION_EXPIRY_KEY = 90 * 24 * 60 * 60;

    public static final Integer LIMIT_SPORT_EXPIRY_KEY = 3 * 30;
    /**
     * 玩法盘口位置限额
     */
    public static final String MARKET_PLACE_KEY = "rcs:limit:marketPlace:matchId.%s:playId.%s.subPlayId.%s";

    private static final String SINGLE_USER_PLAY_PAYMENT_FIELD = "matchId.%s:matchType.%s:playId.%s:amountType.%s:UserPlaySinglePayment";
    private static final String LIMIT_KEY = "rcs:limit:sportId.%s:tournamentLevel.%s:dataType.%s";
    /**
     * （单）用户单场限额-早盘 缓存field
     */
    public static final String SINGLE_USER_EARLY_PAYMENT_FIELD = "matchId.%s:UserEarlySinglePayment";

    /**
     * （单）用户单场限额-滚球 缓存field
     */
    public static final String SINGLE_USER_LIVE_PAYMENT_FIELD = "matchId.%s:UserLiveSinglePayment";

    /**
     * _{商户ID_赛事ID}
     */
    private static String suffix = "_{%s_%s}";

    /**
     * RCS:RISK:日期:商户ID:赛种:
     */
    private static String prefix = "RCS:RISK:%s:%s:%s:";
    private static String userMatchKey = prefix + "%s:%s:%s" + suffix;

    /**
     * 用户玩法限额 缓存field=玩法ID_投注阶段_玩法类型
     */
    public static final String USER_SINGLE_MATCH_PLAY_HASH_FIELD = "%s_%s_%s";
    /**
     * 用户单场限额 缓存field
     */
    public static final String USER_SINGLE_MATCH_HASH_FIELD = "USER_MATCH_ALL_PAID";
    public static final String RCS_STANDARD_SPORT_CATEGORY_ALL = "RCS_STANDARD_SPORT_CATEGORY_ALL:%s:%s";

    public static final String RCS_STANDARD_SPORT_CATEGORY_FIELD = "%s:%s";

    /**
     * 商户限额key
     */
    public static final String MERCHANT_LIMIT_KEY = "rcs:limit:merchants:%s";

    //用户单日串关已用总赔付 商户:用户:账务日
    public static final String SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_KEY = "single_day_stray_total_merchant:%s:user:%s:paid:%s";
    //用户单日赛种赔付限额额度 商户:用户:赛种id:账务日
    public static final String SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_RACE_KEY = "single_day_stray_total_merchant:%s:user:%s:race:%s:paid:%s";
    //用户单日串关类型可赔付额度 商户:用户:串关类型:账务日
    public static final String SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_SERIES_KEY = "single_day_stray_total_merchant:%s:user:%s:seriesType:%s:paid:%s";

    public static final String DATA_STR = "data";

    /**
     * 用户单场配置key
     */
    public static final String USER_MATCH_SINGLE_LIMIT_KEY = "rcs:limit:sportId.%s:matchType:%s:matchId:%s";
    /**
     * 用户玩法配置key
     */
    public static final String USER_MATCH_PLAY_LIMIT_KEY = "rcs:limit:sportId.%s:matchId:%s:matchType.%s:playId.%s";

    public static String getUserSingleMatchHashKey(String dateExpect, String businessId, String sportId, String userId, String matchId, String matchType) {
        return String.format(userMatchKey, dateExpect, businessId, sportId, userId, matchId, matchType, businessId, matchId);
    }

    /**
     * 用户标签缓存
     *
     * @return
     */
    public static String getTagKey() {
        return "risk:trade:rcs_limit_tag:";
    }

    /**
     * 标签限额key
     *
     * @param tagId
     * @return
     */
    public static String getUserTagLimitKey(String tagId) {
        return "risk:trade:rcs_user_special_tag_limit_config:" + tagId+":";
    }

    /**
     * 用户特殊限额模式key
     *
     * @param userId
     * @return
     */
    public static String getUserSpecialLimitKey(String userId) {
        return "risk:trade:rcs_user_special_bet_limit_config:" + userId;
    }

    /**
     * 用户单注单关限额 缓存field
     *
     * @param matchId    赛事ID
     * @param matchType  投注阶段，0-早盘，1-滚球
     * @param playId     玩法ID
     * @param amountType 1-单注投注赔付限额，2-玩法累计赔付限额
     * @return
     */
    public static String getSingleUserPlayPaymentField(String matchId, String matchType, String playId, Integer amountType) {
        return String.format(SINGLE_USER_PLAY_PAYMENT_FIELD, matchId, matchType, playId, amountType);
    }

    public static String getLimitKey(String sportId, Integer tournamentLevel, LimitDataTypeEnum limitDataTypeEnum) {
        return String.format(LIMIT_KEY, sportId, tournamentLevel, limitDataTypeEnum.getType());
    }

    /**
     * 限额 缓存key
     *
     * @param sportId           赛种
     * @param tournamentLevel   联赛等级
     * @param limitDataTypeEnum 限额数据类型
     * @return
     */
    static String getLimitKey(Integer sportId, Integer tournamentLevel, LimitDataTypeEnum limitDataTypeEnum) {
        return String.format(LIMIT_KEY, sportId, tournamentLevel, limitDataTypeEnum.getType());
    }

    /**
     * 单注赔付限额field
     *
     * @param betType 1-单关，2-串关
     * @param sportId 赛种
     * @return
     */
    public static String getSingleNoteClaimLimitField(String betType, String sportId) {
        return String.format("%s_%s_single_note_claim_limit", betType, sportId);
    }


    /**
     * 单日串关赔付限额field
     *
     * @param betType 1-单关，2-串关
     * @param sportId 赛种
     * @return
     */
    public static String getSingleGameClaimLimitField(String betType, String sportId) {
        return String.format("%s_%s_single_game_claim_limit", betType, sportId);
    }


    public static String getBusinessSwitchKey() {
        return "rcs:stray:limit:bussiness:switch:key:%s";
    }


}
