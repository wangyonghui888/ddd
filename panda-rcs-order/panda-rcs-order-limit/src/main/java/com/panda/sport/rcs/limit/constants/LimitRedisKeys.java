package com.panda.sport.rcs.limit.constants;

import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 额度管控 Redis Key
 * @Author : Paca
 * @Date : 2021-12-05 18:38
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface LimitRedisKeys {

    /**
     * 计入串关已用额度的比例 缓存key
     */
    String SERIES_USED_RATIO_KEY = "rcs:limit:config:seriesUsedRatio";

    /**
     * 单日串关赔付总限额
     */
    String SERIES_PAYOUT_AND_PAICAI_TOTAL_AMOUNT = "rcs:series_payout_total_amount";

    String SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_KEY = "single_day_stray_total_merchant:%s:user:%s:paid:%s";

    String PAID_DATE_BUS_REDIS_MATCH_CACHE = "rcs:user:%s:bus:%s:match:%s";
    //用户单日赛种赔付限额额度 商户:用户:赛种id:账务日
    String SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_RACE_KEY = "single_day_stray_total_merchant:%s:user:%s:race:%s:paid:%s";
    //用户单日串关类型可赔付额度 商户:用户:串关类型:账务日
    String SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_SERIES_KEY = "single_day_stray_total_merchant:%s:user:%s:seriesType:%s:paid:%s";

    /**
     * 限额 缓存key
     *
     * @see
     */
    String LIMIT_KEY = "rcs:limit:sportId.%s:tournamentLevel.%s:dataType.%s";

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

    static String getLimitKey(String sportId, Integer tournamentLevel, LimitDataTypeEnum limitDataTypeEnum) {
        return String.format(LIMIT_KEY, sportId, tournamentLevel, limitDataTypeEnum.getType());
    }

    //******************用户单日限额begin********************//

    /**
     * 用户单日赔付限额key
     */
    String USER_DAY_LIMIT_KEY = "rcs:limit:userDay";

    /**
     * 用户单日串关赔付限额key
     */
    String USER_DAY_SERIES_LIMIT_KEY = "rcs:limit:userDaySeries";

    //******************用户单日限额 end********************//

    //******************用户单注串关限额 begin*******************//

    /**
     * （单）单注最低投注额 缓存field
     */
    String SINGLE_MIN_BET_FIELD = "matchId.%s:singleMinBet";

    /**
     * （串）单注最低投注额 缓存field
     */
    String SERIES_MIN_BET_FIELD = "matchId.%s:seriesMinBet";

    /**
     * （串）单注最高投注额比例 缓存field
     */
    String SERIES_MAX_BET_RATIO_FIELD = "matchId.%s:seriesMaxBetRatio";

    /**
     * （串）单注赔付限额 缓存field
     */
    String SERIES_PAYMENT_FIELD = "matchId.%s:seriesPayment:seriesType.%s";

    /**
     * （串）各投注项计入单关限额的投注比例 缓存field
     */
    String SERIES_RATIO_FIELD = "matchId.%s:seriesRatio:seriesType.%s";

    //******************用户单注串关限额 end********************//

    //******************商户单场限额 begin********************//

    /**
     * （单）商户单场限额-早盘 缓存field
     */
    String SINGLE_MERCHANTS_EARLY_PAYMENT_FIELD = "matchId.%s:merchantsEarlySinglePayment";

    /**
     * （单）商户单场限额-滚球 缓存field
     */
    String SINGLE_MERCHANTS_LIVE_PAYMENT_FIELD = "matchId.%s:merchantsLiveSinglePayment";

    //******************商户单场限额 end********************//

    //******************用户单场限额 begin********************//

    /**
     * （单）用户单场限额-早盘 缓存field
     */
    String SINGLE_USER_EARLY_PAYMENT_FIELD = "matchId.%s:UserEarlySinglePayment";

    /**
     * （单）用户单场限额-滚球 缓存field
     */
    String SINGLE_USER_LIVE_PAYMENT_FIELD = "matchId.%s:UserLiveSinglePayment";
    //******************用户单场限额 end********************//

    /**
     * （单）用户单注单关限额 缓存field
     * matchType  0-早盘 1-滚球
     * playType 玩法类型
     * playId 玩法id
     * amountType 1-单注投注赔付限额    2-玩法累积赔付
     */
    String SINGLE_USER_PLAY_PAYMENT_FIELD = "matchId.%s:matchType.%s:playId.%s:amountType.%s:UserPlaySinglePayment";

    /**
     * 用户单注单关限额 缓存field
     *
     * @param matchId    赛事ID
     * @param matchType  投注阶段，0-早盘，1-滚球
     * @param playId     玩法ID
     * @param amountType 1-单注投注赔付限额，2-玩法累计赔付限额
     * @return
     */
    static String getSingleUserPlayPaymentField(String matchId, String matchType, String playId, Integer amountType) {
        return String.format(SINGLE_USER_PLAY_PAYMENT_FIELD, matchId, matchType, playId, amountType);
    }

    /**
     * 商户限额key
     */
    String MERCHANT_LIMIT_KEY = "rcs:limit:merchants:";

    /**
     * 玩法盘口位置限额
     */
    String MARKET_PLACE_KEY = "rcs:limit:marketPlace:matchId.%s:playId.%s.subPlayId.%s";

    /* ********** 单场限额 已用额度 start ********** */

    /**
     * _{商户ID_赛事ID}
     */
    String suffix = "_{%s_%s}";

    /**
     * RCS:RISK:日期:商户ID:赛种:
     */
    String prefix = "RCS:RISK:%s:%s:%s:";

    /**
     * 用户限额
     * key=RCS:RISK:%s:%s:%s:%s:%s:%s_{%s_%s}
     * key=RCS:RISK:日期:商户ID:赛种:用户ID:赛事ID:投注阶段_{商户ID_赛事ID}
     */
    String userMatchKey = prefix + "%s:%s:%s" + suffix;

    static String getUserSingleMatchHashKey(String dateExpect, String businessId, String sportId, String userId, String matchId, String matchType) {
        return String.format(userMatchKey, dateExpect, businessId, sportId, userId, matchId, matchType, businessId, matchId);
    }

    /**
     * 用户单场限额 缓存field
     */
    String USER_SINGLE_MATCH_HASH_FIELD = "USER_MATCH_ALL_PAID";

    /**
     * 用户玩法限额 缓存field=玩法ID_投注阶段_玩法类型
     */
    String USER_SINGLE_MATCH_PLAY_HASH_FIELD = "%s_%s_%s";

    /**
     * 商户单场限额
     * key=RCS:RISK:%s:%s:%s:%s:V2_{%s_%s}
     * key=RCS:RISK:日期:商户ID:赛种:赛事ID:V2_{商户ID_赛事ID}
     */
    String singleMatchInfoKey = prefix + "%s:%s:V2" + suffix;

    static String getMerchantSingleMatchHashKey(String dateExpect, String businessId, String sportId, String matchId, String matchType) {
        return String.format(singleMatchInfoKey, dateExpect, businessId, sportId, matchId, matchType, businessId, matchId);
    }

    /**
     * 商户单场限额 缓存field
     * key=RCS:RISK:商户ID:赛种:赛事ID:V2_{商户ID_赛事ID}
     */
    String MERCHANT_SINGLE_MATCH_HASH_FIELD = "MAX_MATCH_PAID";

    /* ********** 单场限额 已用额度 end ********** */


    /* ********** 用户单日限额 已用额度 start ********** */

    /**
     * 总限额 缓存field
     */
    String TOTAL_FIELD = "total";

    String CROSS_DAY_COMPENSATION_KEY = "rcs:limit:crossDayCompensation:%s:%s:%s";

    String DAY_COMPENSATION_KEY = "rcs:limit:dayCompensation:%s:%s:%s";

    /**
     * 单日串关赔付限额/总限额，key=rcs:limit:crossDayCompensation:日期:商户ID:用户ID，field=赛种ID/total
     *
     * @param dateExpect 日期
     * @param businessId 商户ID
     * @param userId     用户ID
     * @return
     */
    static String getCrossDayCompensationKey(String dateExpect, String businessId, String userId) {
        return String.format(CROSS_DAY_COMPENSATION_KEY, dateExpect, businessId, userId);
    }

    /**
     * 单日赔付限额/总限额，key=rcs:limit:dayCompensation:日期:商户ID:用户ID，field=赛种ID/total
     *
     * @param dateExpect 日期
     * @param businessId 商户ID
     * @param userId     用户ID
     * @return
     */
    static String getDayCompensationKey(String dateExpect, String businessId, String userId) {
        return String.format(DAY_COMPENSATION_KEY, dateExpect, businessId, userId);
    }

    /* ********** 用户单日限额 已用额度 end ********** */

    /**
     * 串关Redis更新记录
     * rcs:limit:redisUpdateRecord:series:订单号
     */
    String SERIES_REDIS_UPDATE_RECORD_KEY = "rcs:limit:redisUpdateRecord:series:%s";

    /* ********** 特殊会员限额 配置 start ********** */

    /**
     * 用户特殊限额key
     *
     * @param userId
     * @return
     */
    static String getUserSpecialLimitKey(String userId) {
        return "risk:trade:rcs_user_special_bet_limit_config:" + userId;
    }

    /**
     * 用户特殊限额类型field
     * 0-无,1-标签限额,2-特殊百分比限额,3-特殊单注单场限额,4-特殊vip限额
     */
    String USER_SPECIAL_LIMIT_TYPE_FIELD = "type";

    /**
     * 百分比限额field
     */
    String USER_PERCENTAGE_LIMIT_FIELD = "percentage";

    /**
     * 特殊货量百分比
     */
    String SPECIAL_QUANTITY_PERCENTAGE_FIELD = "specialQuantityPercentage";

    /**
     * 单注赔付限额field
     *
     * @param betType 1-单关，2-串关
     * @param sportId 赛种
     * @return
     */
    static String getSingleNoteClaimLimitField(String betType, String sportId) {
        return String.format("%s_%s_single_note_claim_limit", betType, sportId);
    }

    /**
     * 单场赔付限额field
     * 单日赔付限额field
     *
     * @param betType 1-单关，2-串关
     * @param sportId 赛种
     * @return
     */
    static String getSingleGameClaimLimitField(String betType, String sportId) {
        return String.format("%s_%s_single_game_claim_limit", betType, sportId);
    }

    /**
     * 标签限额key
     *
     * @param tagId
     * @return
     */
    static String getUserTagLimitKey(String tagId) {
        return "risk:trade:rcs_user_special_tag_limit_config:" + tagId + ":";
    }

    /* ********** 特殊会员限额 配置 end ********** */

    /**
     * 用户标签缓存
     *
     * @return
     */
    static String getTagtKey() {
        return "risk:trade:rcs_limit_tag:";
    }

    /**
     * 商户限额预警标志
     *
     * @param dateExpect
     * @param businessId
     * @return
     */
    static String getMerchantLimitWarnSignKey(String dateExpect, Long businessId) {
        return String.format("rcs:merchantLimitWarnSign:%s:%s", dateExpect, businessId);
    }
}
