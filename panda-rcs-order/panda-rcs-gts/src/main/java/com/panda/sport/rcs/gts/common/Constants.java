package com.panda.sport.rcs.gts.common;

public class Constants {

    /**
     * gts 商户折扣 rediskey
     */
    public static String GTS_AMOUNT_RATE = "rcs:gts:amount_rate:tenantId:%s";

    /**
     * gts 全部折扣 rediskey
     */
    public static String GTS_AMOUNT_RATE_ALL = "rcs:gts:amount_rate";

    /**
     * gts订单是否处理过
     */
    public final static String GTS_ORDER_OPSTATUS = "rcs:gts:order:opstatus.%s";

    /**
     * gts订单主题
     */
    public final static String RCS_RIKS_GTS_ORDER = "rcs_riks_gts_order";

    /**
     * gts订单接受赔率模式
     */
    public final static String GTS_ORDER_ODDSCHANGETYPE = "rcs:gts:order:oddsChangeType:orderNo.%s";

    /**
     * 是否走缓存
     */
    public final static String GTS_ORDER_CACHE = "rcs:gts:order:oddsChangeType:optionId.%s.odds.%s.oddsChangeType.%s";

    /**
     * 缓存接单概率
     */
    public final static String GTS_ORDER_RATE = "rcs:gts:order:rate";

    /**
     * 按照商户级别控制是否某个商户的注单都不提交gts  1不走gts
     */
    public final static String GTS_MERCHANT_SENDTICKET_STATUS = "rcs:gts:merchant:sendticket:status:list";

    //1666需求
    public final static String ODDS_SCOPE_KEY = "rcs:risk:order:oddsScope:match.%s.play_id.%s.match_type.%s";
    public final static String REDIS_MATCH_MARKET_ODDS_NEW = "rcs:redis:playId:%s:match:%s:odds:new";
    public final static String REDIS_MATCH_INFO = "rcs:redis:standard:match:%s";

    public final static String GTS_ORDER_EXPIRE = "rcs:mts:order:expire";

    /**
     * token缓存
     */
    public final static String GTS_TOKEN = "rcs:gts:token:%s";
    /**
     * token缓存
     */
    public final static String GTS_TOKEN_TOPIC = "rcs_gts_token";
    /**
     * gts早盘标识
     */
    public final static String GTS_PREMATCH = "PreMatch";
    /**
     * gts滚球标识
     */
    public final static String GTS_INPLAY = "InPlay";

    /**
     * 内部接单标识
     */
    public final static String ACCEPTED = "ACCEPTED";
    /**
     * 内部拒单单标识
     */
    public final static String REJECTED = "REJECTED";
    /**
     * gts 内部处理订单 生成订单号
     */
    public final static String GTS_AUTO_TICKETID = "rcs:gts:auto:ticketId";

    /**
     * gts 内部处理订单 生成订单号
     */
    public final static String GTS_SELECTION_MAXLIMIT = "rcs:gts:selection:%s:odds:%s:maxLimit";

    /**
     * 是否中场休息
     */
    public static final String RCS_DATA_KEYCACHE_MATCHTEMPINFO = "rcs:data:keyCache:matchTempInfo:%s";
}
