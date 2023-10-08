package com.panda.sport.sdk.constant;

import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;

/**
 * @Project Name: panda-rcs-order-group
 * @Package Name: com.panda.sport.sdk.constant
 * @Description : 额度管控 Redis Key
 * @Author : Paca
 * @Date : 2020-09-25 10:14
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface LimitRedisKeys {

    /**
     * 计入串关已用额度的比例 缓存key
     */
    String SERIES_USED_RATIO_KEY = "rcs:limit:config:seriesUsedRatio";

    /**
     * 限额 缓存key
     *
     * @see
     */
    String LIMIT_KEY = "rcs:limit:sportId.%s:tournamentLevel.%s:dataType.%s";

    /**
     * 赛事模板单注玩法限额缓存key
     */
    String MATCH_SINGLE_BET_PLAY_LIMIT_KEY = "rcs:limit:sportId.%s:dataType.%s:matchId.%s:matchType.%s:playId.%s:UserPlaySinglePayment";
    /**
     * 用户模板单注玩法限额缓存key
     */
    String COMMON_SINGLE_BET_PLAY_LIMIT_KEY = "rcs:limit:sportId.%s:dataType.%s:matchType.%s:playId.%s:UserPlaySinglePayment";

    /**
     * 赛事模板用户单场限额缓存key
     */
    String MATCH_SINGLE_LIMIT_KEY = "rcs:limit:sportId.%s:dataType.%s:matchId.%s:matchType.%s:UserSingle";
    /**
     * 用户模板用户单场限额缓存key
     */
    String COMMON_SINGLE_LIMIT_KEY = "rcs:limit:sportId.%s:tournamentLevel:%s.dataType.%s:matchType.%s:UserSingle";

    /**
     * 赛事模板商户单场限额缓存key
     */
    String MATCH_MERCHANT_SINGLE_LIMIT_KEY = "rcs:limit:sportId.%s:dataType.%s:matchId.%s:matchType.%s:MerchantSingle";
    /**
     * 商户模板单场限额缓存key
     */
    String COMMON_MERCHANT_SINGLE_LIMIT_KEY = "rcs:limit:sportId.%s:tournamentLevel:%s.dataType.%s:matchType.%s:MerchantSingle";



    /**
     * 用户单场配置key
     */
    public static final String USER_MATCH_SINGLE_LIMIT_KEY = "rcs:limit:sportId.%s:matchType:%s:matchId:%s";
    /**
     * 用户玩法配置key
     */
    public static final String USER_MATCH_PLAY_LIMIT_KEY = "rcs:limit:sportId.%s:matchId:%s:matchType.%s:playId.%s";

    String SPECIAL_USER_CONFIG = "rcs:special:user:order:delay:config:%s";
    String USER_LABEL_CONFIG = "rcs:special:user:order:delay:sencond:config:%s";

    String MERCHANT_SINGLE_ALERT_KEY = "rcs:sdk:over:num:matchId:%s";

    String MERCHANT_DAILY_ALERT_KEY =  "rcs:sdk:over:num:daily:%s:warnType:%s";


    static String getMerchantSingleAlertKey(String matchId) {
        return String.format(MERCHANT_SINGLE_ALERT_KEY,matchId);
    }
    static String getMerchantDailyAlertKey(String dateExpect,Integer warnType) {
        return String.format(MERCHANT_DAILY_ALERT_KEY,dateExpect,warnType);
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
     * 赛事模板单注玩法限额缓存key
     *
     * @param sportId
     * @param limitDataTypeEnum
     * @param matchId
     * @param isScroll
     * @param playId
     * @return
     */
    static String getMatchSingleBetPlayLimitKey(Integer sportId, LimitDataTypeEnum limitDataTypeEnum, String matchId, String isScroll, String playId) {
        return String.format(MATCH_SINGLE_BET_PLAY_LIMIT_KEY, sportId, limitDataTypeEnum.getType(), matchId, isScroll, playId);
    }

    /**
     * 用户 玩法限额 koala 那边的key
     *
     * @param sportId
     * @param isScroll
     * @return
     */
    static String getSinglePlayLimitKey(String sportId, String matchId,String isScroll, String playId) {
        return String.format(USER_MATCH_PLAY_LIMIT_KEY, sportId, matchId, isScroll, playId);
    }

    /**
     * 通用配置单注玩法限额缓存key
     *
     * @param sportId
     * @param limitDataTypeEnum
     * @param isScroll
     * @param playId
     * @return
     */
    static String getCommonSingleBetPlayLimitKey(String sportId, LimitDataTypeEnum limitDataTypeEnum, String isScroll, String playId) {
        return String.format(COMMON_SINGLE_BET_PLAY_LIMIT_KEY, sportId, limitDataTypeEnum.getType(), isScroll, playId);
    }


    /**
     * 赛事模板用户单场限额缓存key
     *
     * @param sportId
     * @param limitDataTypeEnum
     * @param matchId
     * @param isScroll
     * @return
     */
    static String getMatchSingleLimitKey(String sportId, LimitDataTypeEnum limitDataTypeEnum, String matchId, String isScroll) {
        return String.format(MATCH_SINGLE_LIMIT_KEY, sportId, limitDataTypeEnum.getType(), matchId,isScroll);
    }

    /**
     * 通用配置用户单场限额缓存key
     *
     * @param sportId
     * @param limitDataTypeEnum
     * @param isScroll
     * @return
     */
    static String getCommonSingleLimitKey(String sportId, Integer tournamentLevel, LimitDataTypeEnum limitDataTypeEnum, String isScroll) {
        return String.format(COMMON_SINGLE_LIMIT_KEY, sportId, tournamentLevel, limitDataTypeEnum.getType(), isScroll);
    }


    /**
     * 用户单场 koala那边的key
     * @param sportId
     * @param matchId
     * @param isScroll
     * @return
     */
    static String getUserSingleLimitKey(String sportId, String matchId, String isScroll) {
        return String.format(USER_MATCH_SINGLE_LIMIT_KEY, sportId,isScroll, matchId);
    }




    /**
     * 赛事模板商户单场限额缓存key
     *
     * @param sportId
     * @param limitDataTypeEnum
     * @param matchId
     * @param isScroll
     * @return
     */
    static String getMatchMerchantSingleLimitKey(String sportId, LimitDataTypeEnum limitDataTypeEnum, String matchId, String isScroll) {
        return String.format(MATCH_MERCHANT_SINGLE_LIMIT_KEY, sportId, limitDataTypeEnum.getType(), matchId,isScroll);
    }

    /**
     * 通用配置商户单场限额缓存key
     *
     * @param sportId
     * @param limitDataTypeEnum
     * @param isScroll
     * @return
     */
    static String getCommonMerchantSingleLimitKey(String sportId, Integer tournamentLevel, LimitDataTypeEnum limitDataTypeEnum, String isScroll) {
        return String.format(COMMON_MERCHANT_SINGLE_LIMIT_KEY, sportId, tournamentLevel, limitDataTypeEnum.getType(), isScroll);
    }





    //******************用户单日限额begin********************//

    /**
     * 用户单日赔付限额key
     */
    String USER_DAY_LIMIT_KEY = "rcs:limit:userDay:";

    /**
     * 用户单日串关赔付限额key
     */
    String USER_DAY_SERIES_LIMIT_KEY = "rcs:limit:userDaySeries:";

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
    String MARKET_PLACE_KEY = "rcs:limit:marketPlace:matchId.%s:playId.%s.subPlayId.%s:placeNum:%s";

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

    static String getMerchantSingleMatchHashKey(String dateExpect, String businessId, String sportId, String matchId) {
        return String.format(singleMatchInfoKey, dateExpect, businessId, sportId, matchId, businessId, matchId);
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

    String TRADING_TYPE_KEY = "rcs:trading:type:%s:%s:%s";

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

    static String getTradingTypeStatusKey(String matchId, String playId,String matchType) {
        return String.format(TRADING_TYPE_KEY, matchId, playId,matchType);
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

    static String getBussinessSwitchKey() {
        return "stray:limit:bussiness:switch:key";
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


    //VR藏单商户(1--关闭 2--开启)
    public static String VR_ENABLE_AMOUNT_RATE = "rcs:vr:enable:tenantId:%s";

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
        return "risk:trade:rcs_user_special_tag_limit_config:" + tagId+":";
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

    /**
     * 商户串关限额预警标志
     *
     * @param dateExpect
     * @param businessId
     * @return
     */
    static String getMerchantSeriesLimitWarnSignKey(String dateExpect, Long businessId) {
        return String.format("rcs:merchantSeriesLimitWarnSign:%s:%s", dateExpect, businessId);
    }

    static String getMissedOrderConfigurationKey(Long merchantIds) {
        return "rcs:dynamic_missed_order_rcs_configuration:merchants_id:" + merchantIds;
    }
}
