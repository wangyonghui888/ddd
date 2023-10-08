package com.panda.sport.rcs.third.common;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static final String LINKID = "linkId";

    //profile redis key
    public final static String BETER_PROFILE_KEY = "bc:beter:profile";

    /**
     * 订单消费
     */
    public final static String RCS_RISK_THIRD_ORDER =  "rcs_risk_third_order";

    /**
     * 订单消费
     */
    public final static String RCS_RISK_THIRD_ORDER_REJECT =  "rcs_risk_third_order_reject";

    /**
     * 取消订单
     */
    public final static String QUEUE_REJECT_THIRD_ORDER =  "queue_reject_mts_order";

    /**
     * 订单重复标识
     */
    public final static String THIRD_ORDER_REPEAT_STATUS =  "rcs:third:order:repeat:%s";

    /**
     * 按照商户级别控制是否某个商户的注单都不提交bts  1不走bts
     */
    public final static String THIRD_MERCHANT_STATUS = "rcs:third:merchant:status:list";

    /**
     * 是否走缓存
     */
    public final static String THIRD_ORDER_CACHE = "rcs:third.%s:order:oddsChangeType:optionId.%s.odds.%s.oddsChangeType.%s";

    /**
     * gts注单返回isBetAllowed为false的订单存入缓存
     */
    public final static String GTS_IS_NOT_ALLOWED_ORDER = "rcs:gts:is:not:allowed:order:%s";

    /**
     * gts注单返回isBetAllowed为false的订单已经拒绝状态
     */
    public final static String GTS_IS_NOT_ALLOWED_ORDER_REJECT = "rcs:gts:is:not:allowed:order:reject:%s";

    /**
     * 订单接受赔率模式
     */
    public final static String THIRD_ORDER_ODDS_CHANGE_TYPE = "rcs:third:order:oddsChangeType:orderNo.%s";


    /**
     * 缓存过期时间
     */
    public final static String THIRD_ORDER_EXPIRE = "rcs:third:order:expire";

    /**
     * 内部接单标识
     */
    public final static String ACCEPTED = "ACCEPTED";
    /**
     * 内部拒单单标识
     */
    public final static String REJECTED = "REJECTED";

    /**
     * 赛事阶段
     */
    public static final String RCS_DATA_THIRD_MATCH_INFO = "rcs:data:keyCache:matchTempInfo:%s";

    /**
     * 可以触发秒接场景的球种
     */
    public static final List<Long> speedSportList = new ArrayList<Long>(){{
        add(1L);
        add(2L);
        add(3L);
        add(5L);
        add(7L);
        add(8L);
        add(9L);
        add(10L);
    }};

    /**
     * 赛事信息
     */
    public static final String RCS_TASK_MATCH_INFO_CACHE = "rcs:task:match:info:";


    //自动接距redis_key 赛事id和玩法id拼接
    public static final String ORDER_LABEL_DELAY_CONFIG = "rcs:label:order:delay:config:%s";

    /**
     * 用户标签延迟配置
     */
    public static final String USER_LABEL_CONFIG = "rcs:special:user:order:delay:sencond:config:%s";

    public static final String MATCH_PLAY_SECOND_REDIS_KEY = "rcs:match:play:second:redis:key:%s:%s";
    public static final long CACHE_TIME_OUT = 4 * 60 * 60 * 1000L;
    public static final String REDIS_MATCH_MARKET_SECOND_CONFIG = "rcs:redis:match:market:second:config:%s:%s:%s";
    public static final String REDIS_MATCH_MARKET_SUB_SECOND_CONFIG = "rcs:redis:match:market:sub:second:config:%s:%s:%s:%s";


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


    /**
     * 用户信息缓存
     */
    public static String USER_CACHE_KEY = "rcs:third:user:%s";

    /**
     * 用户信息缓存
     */
    public static String USER_TOKEN_CACHE_KEY = "rcs:third:user:token:%s";

    /**
     * 投注请求唯一标识
     */
    public static String BC_BET_PLACED_TRANSACTION_ID = "rcs:third:bc:bet:placed:%s";
    /**
     * 三方确认开关
     */
    public static String BC_BET_PLACED_ORDER_NO = "rcs:third:bc:bet:placed:order:%s";

    /**
     * 投注取消请求唯一标识
     */
    public static String BC_BET_RESULTED_TRANSACTION_ID = "rcs:third:bc:bet:resulted:%s";

    /**
     * 投注回滚请求唯一标识
     */
    public static String BC_BET_ROLLBACK_TRANSACTION_ID = "rcs:third:bc:bet:rollback:%s";


    /**
     * BC订单临时存储
     */
    public final static String THIRD_BC_ORDER_CACHE =  "rcs:third.bc:order:%s";
    /**
     * 订单临时存储
     */
    public final static String THIRD_ORDER_CANCELED =  "rcs:third:order:canceled:%s";




    /**
     * redcat 用户请求token
     */
    public final static String THIRD_RED_CAT_REQUEST_TOKEN =  "rcs:third.redcat:request";
    /**
     * C01token缓存
     */
    public final static String RC_TOKEN = "rcs:rc:token:%s";

    /**
     * 三方确认开关
     */
    public static String RED_CAT_BET_PLACED_ORDER_NO = "rcs:third:red:cat:bet:placed:order:%s";



    //mts商户折扣利率
    public static String MTS_AMOUNT_RATE = "rcs:mts:amount_rate:tenantId:%s";
    //cts商户折扣利率
    public static String CTS_AMOUNT_RATE = "rcs:cts:amount_rate:tenantId:%s";
    //gts商户折扣利率
    public static String GTS_AMOUNT_RATE = "rcs:gts:amount_rate:tenantId:%s";
    //rts商户折扣利率
    public static String RTS_AMOUNT_RATE = "rcs:rts:amount_rate:tenantId:%s";

    //mts通用商户折扣利率
    public static String MTS_AMOUNT_RATE_ALL = "rcs:mts:amount_rate";

    //cts通用商户折扣利率
    public static String CTS_AMOUNT_RATE_ALL = "rcs:cts:amount_rate";

    //gts通用商户折扣利率
    public static String GTS_AMOUNT_RATE_ALL = "rcs:gts:amount_rate";

    //ots通用商户折扣利率
    public static String OTS_AMOUNT_RATE_ALL = "rcs:ots:amount_rate";

    //rts通用商户折扣利率
    public static String RTS_AMOUNT_RATE_ALL = "rcs:rts:amount_rate";

    public static final String RCS_BUS_THIRD_ORDER_STATUS = "queue_mts_order";

    /**
     * 盘口赔率缓存topic
     */
    public static final String RCS_RISK_THIRD_RED_CAT_SELECTION_TOPIC="RCS_RISK_THIRD_RED_CAT_MATCH_TOPIC";

    /**
     * 红锚注单缓存topic
     */
    public static final String RCS_RISK_THIRD_RED_CAT_ORDER_TOPIC="RCS_RISK_THIRD_RED_CAT_ORDER_TOPIC";
    /**
     * C01redis 赔率缓存
     */
    public final static String REDIS_RED_CAT_SELECTION_ID_KEY = "rcs:redis:redCat:selectionId:%s";

    /**
     * 注单临时缓存
     */
    public static final String RCS_THIRD_ORDER_REDIS = "rcs:third:order:%s";

    /**
     * 收到处理完注单历史存储
     */
    public static final String RCS_THIRD_ORDER_RECEIVED_REDIS="rcs:third:order:received:%s";
    /**
     * 收到处理完注单历史存储锁
     */
    public static final String RCS_THIRD_ORDER_RECEIVED_LOCK="rcs:third:order:received:%s";




    //gts 赛事基本数据缓存
    public static final String GTS_OUTRIGHT_MATCH_TOURNAMENTID = "RCS:STANDARD:OUTRIGHT:MATCH:%s";
    public static final String GTS_MATCH_TOURNAMENTID = "RCS:STANDARD:MATCH:%s";
    public static final String GTS_MATCH_TOURNAMENTCODE = "RCS:STANDARD:TOURNAMENTNAMECODE:MATCH:%s";
    public static final String GTS_MATCH_LANGUAGEINTERNATION = "RCS:STANDARD:LANGUAGEINTERNATION:MATCH:%s";
    public static final long GTS_MATCH_TIMOEOUT = 90 * 60 * 1000L;



    //cts 三方投注项id
    public static final String CTS_THIRD_SELECTID = "RCS:THIRD:CTS:SELECTID:%s";

    /**
     * redis 注单缓存
     */
    public static final Long ORDER_REDIS_EXPIRED = 30L;

    public static String GTS_BET_PLACED_ORDER_NO = "rcs:third:gts:bet:placed:order:%s";

    // 第三方投注,取消缓存锁
    public static final String THIRD_ORDER_NO_REDIS_LOCK="third:order:no:lock:%s";
    //第三方投注,取消缓存锁
    public static final String THIRD_ORDER_NO_REDIS="third:order:no:%s";
    //设置为5秒超时
    public static final Long THIRD_ORDER_NO_EXPIRED=5L;
    //设置为1秒超时
    public static final Integer THIRD_ORDER_NO_KEY_EXPIRED=2;
    /**
     * 折扣金额缓存
     */
    public static String GTS_BET_AMOUNT = "rcs:gts:bet:amount:%s";




    public final static String ORDER_REQUEST_FAILED = "rcs:third:order:request:failed:%s";
    public final static String GTS_SETTLE_INFO = "rcs:third:order:gts:settle:%s";



    public final static String GTS_RETRY_STATUS_BET = "rcs:third:order:gts:retryed:bet:%s";
    public final static String GTS_RETRY_STATUS_CANCEL = "rcs:third:order:gts:retryed:cancel:%s";
    public final static String GTS_RETRY_STATUS_RECEIVE = "rcs:third:order:gts:retryed:receive:%s";


    public final static String CTS_RETRY_STATUS_BET = "rcs:third:order:cts:retryed:bet:%s";
    public final static String CTS_RETRY_STATUS_CANCEL = "rcs:third:order:cts:retryed:cancel:%s";












}
