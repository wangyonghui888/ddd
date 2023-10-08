package com.panda.rcs.pending.order.constants;

/**
 * redisKey
 */
public class RedisKey {

    private static final String prefix = "rcs:data:";

    /**
     * 赛事状态key,Data项目Topic=STANDARD_MATCH_STATUS
     * 缓存key =rcs:data:matchStatus:matchId
     */
    public static String getMatchStatusKey(Long matchId) {
        return String.format(prefix + "matchStatus:%s", matchId);
    }

    /**
     * 赛事盘口状态key,Data项目Topic=STANDARD_MARKET_ODDS
     * 缓存key =rcs:data:marketInfo:matchId:marketCategoryId
     */
    public static String getMatchMarketInfoKey(Long matchId, Long marketCategoryId) {
        return String.format(prefix + "marketInfo:%s:%s", matchId, marketCategoryId);
    }

    /**
     * 赛事盘口结算状态key
     * 缓存key =rcs:data:marketSettlement:sportId:matchId:marketId:optionsId
     */
    public static String getMatchMarketSettlement(Long sportId, Long matchId, Long marketId,Long optionsId) {
        return String.format(prefix + "marketSettlement:%s:%s:%s:%s", sportId, matchId, marketId,optionsId);
    }



    public static String getUserSingleMatchHashKey(String dateExpect, String businessId, String sportId, String userId, String matchId, String matchType) {
        return String.format(userReserveMatchKey, dateExpect, businessId, sportId, userId, matchId, matchType, businessId, matchId);
    }

    /**
     * _{商户ID_赛事ID}
     */
    private static String suffix = "_{%s_%s}";

    /**
     * RCS:RISK:日期:商户ID:赛种:
     */
    private static String singPrefix = "RCS:RESERVE:%s:%s:%s:";
    private static String userReserveMatchKey = singPrefix + "%s:%s:%s" + suffix;


    public static final String RCS_STANDARD_SPORT_CATEGORY_ALL = "RCS_STANDARD_SPORT_CATEGORY_ALL:%s:%s";

    public static final String RCS_STANDARD_SPORT_CATEGORY_FIELD = "%s:%s";

    /**
     * 商户限额key
     */
    public static final String MERCHANT_LIMIT_KEY = "rcs:limit:merchants:%s";

    /**
     * 用户特殊限额模式key
     *
     * @param userId
     * @return
     */
    public static String getUserSpecialLimitKey(String userId) {
        return "risk:trade:rcs_user_special_bet_limit_config:" + userId;
    }


    public static final String USER_SPECIAL_LIMIT_TYPE_FIELD = "type";

    public static final String USER_SPECIAL_LIMIT_PERCENTAGE_FIELD = "percentage";

    /**
     * 用户玩法限额 缓存field=玩法ID_投注阶段_玩法类型
     */
    public static final String USER_SINGLE_MATCH_PLAY_HASH_FIELD = "%s_%s_%s";

    public static final String TRADING_TYPE_KEY = "rcs:trading:type:%s:%s:%s";

    /**
     * 用户单场限额 缓存field
     */
    public static final String USER_SINGLE_MATCH_HASH_FIELD = "USER_MATCH_ALL_PAID";

    /**
     * 串关Redis更新记录
     * rcs:limit:redisUpdateRecord:series:订单号
     */
    public static final String SERIES_REDIS_UPDATE_RECORD_KEY = "rcs:reserve:redisUpdateRecord:series:%s";
    //最新的赔率变化
    public static final String REDIS_MATCH_MARKET_ODDS_NEW = "rcs:redis:playId:%s:match:%s:odds:new";
    /**
     * 商户单场额度累计key
     */
    public static  final String RESERVE_REDIS_BUS_KEY="reserve_redis_bus_key:%s:match:%s";


    public static final String PENDING_ORDER_KEY = "pending:order:matchId:%s:matchType:%s";
    public static final String PENDING_ORDER_LIMIT_KEY = "pending:order:limitRate:matchId:%s:matchType:%s";
    public static final String PENDING_ORDER_MARGIN_REF_KEY = "pending:orderMarginRef:matchId:%s:sportId:%s:playId:%s";
    public static final String OPEN_ALL_PRE_ORDER_KEY = "open:all:pre:order:matchId:%s:sportId:%s:matchType:%s";
    public static final String RCS_DATA_KEYCACHE_MATCHTEMPINFO = "rcs:data:keyCache:matchTempInfo:%s";


}
