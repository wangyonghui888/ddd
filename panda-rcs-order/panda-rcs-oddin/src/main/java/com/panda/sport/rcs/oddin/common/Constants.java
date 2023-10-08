package com.panda.sport.rcs.oddin.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author z9-wiker
 */
public class Constants {

    /**
     * 获取商户折扣redis key
     */
    public static final String MTS_AMOUNT_RATE = "rcs:mts:amount_rate:tenantId:%s";

    /**
     * String类型的10000常量
     */
    public static final String TEN_THOUSAND = "10000";

    /**
     * 注单结果返回后续处理topic
     */

    public static final String RCS_RISK_ODDIN_ORDER_CALLBACK = "rcs_risk_oddin_order_callback";

    /**
     * 电竞注单结果以MQ方式通知电竞，topic
     */
    public static final String RCS_RISK_ODDIN_TICKET_TO_DJ = "rcs_risk_oddin_ticket_to_dj";
    /**
     * 告知电竞oddin投注接口连接状态topic
     */
    public static final String RCS_RISK_ODDIN_ON_PRODUCER_STATUS_CHANGE_TO_DJ = "topic_oddin_onConnectionStatusChange";

    public static final String RCS_RISK_ODDIN_TICKET_TO_TY = "rcs_risk_oddin_ticket_to_ty";

    /**
     * 电竞拉单结果以MQ方式通知电竞，topic
     */
    public static final String RCS_RISK_ODDIN_TICKET_RESULT_TO_DJ = "rcs_risk_oddin_ticket_result_to_dj";

    /**
     * 拉单返回给体育
     */
    public static final String RCS_RISK_ODDIN_TICKET_RESULT_TO_TY = "rcs_risk_oddin_ticket_result_to_ty";
    /**
     * 拉单推送给大数据
     */
    public static final String RCS_RISK_ODDIN_TICKET_RESULT_TO_BIG_DATA = "rcs_risk_oddin_ticket_result_to_big_data";

    /**
     * 大数据拉单的topic
     */
    public static final String RCS_RISK_ODDIN_SETTLE_BET_TY = "oddin-order-settle-ty";

    /**
     * 接收SelectionId
     */
    public static final String RCS_RISK_ODDIN_TICKET_RESULT_TO_Selecionds = "rcs_sdk_selection_id";

    /**
     * 限额除以1000常量
     */
    public static final BigDecimal STAKE = BigDecimal.valueOf(10000);

    /**
     * 取消订单
     */
    public final static String QUEUE_REJECT_ORDER = "queue_reject_mts_order";

    /**
     * 自动接距redis_key 赛事id和玩法id拼接
     */
    public static final String ORDER_LABEL_DELAY_CONFIG = "rcs:label:order:delay:config:%s";

    /**
     * 用户标签延迟配置
     */
    public static final String USER_LABEL_CONFIG = "rcs:special:user:order:delay:sencond:config:%s";

    /**
     * 赛事阶段
     */
    public static final String RCS_DATA_THIRD_MATCH_INFO = "rcs:data:keyCache:matchTempInfo:%s";

    /**
     * 可以触发秒接场景的球种
     */
    public static final List<Long> speedSportList = new ArrayList<Long>() {{
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
     * 内部接单标识
     */
    public final static String ACCEPTED = "ACCEPTED";

    /**
     * 内部拒单单标识
     */
    public final static String REJECTED = "REJECTED";

    /**
     * 是否走缓存
     */
    public final static String THIRD_ORDER_CACHE = "rcs:third.%s:order:oddsChangeType:optionId.%s.odds.%s.oddsChangeType.%s";

    /**
     * 缓存过期时间
     */
    public final static String THIRD_ORDER_EXPIRE = "rcs:third:order:expire";

    /**
     * 订单消费
     */
    public final static String RCS_RISK_THIRD_ORDER_REJECT = "rcs_risk_third_order_reject";

    public final static String TENANT_DEFAULT_DISCOUONT = "0.8";

    /**
     * 体育ots商户折扣利率
     */
    public static String OTS_AMOUNT_RATE = "rcs:ots:amount_rate:tenantId:%s";
    public static String OTS_CACHE_AMOUNT_RATE = "rcs:ots:cache:amount_rate:tenantId:%s";
    /**
     * 电竞ots商户折扣利率
     */
    public static String DJ_RTS_AMOUNT_RATE = "rcs:dj:ots:amount_rate:tenantId:%s";
    public static String DJ_RTS_CACHE_AMOUNT_RATE = "rcs:dj:ots:cache:amount_rate:tenantId:%s";
    /**
     * 体育ots通用商户折扣利率
     */
    public static String OTS_AMOUNT_RATE_ALL = "rcs:ots:amount_rate";
    /**
     * 电竞ots通用商户折扣利率
     */
    public static String DJ_OTS_AMOUNT_RATE_ALL = "rcs:dj:ots:amount_rate";

    public static final String RCS_BUS_THIRD_ORDER_STATUS = "queue_mts_order";

    /**
     * 订单正在下注中状态
     */
    public static final String ORDER_BETTING = "order_betting";

    /**
     * 早盘投注状态存redis时间 8秒
     */
    public static final Long EARLY_ORDER_BETTING_TIME = 8000L;

    /**
     * 早盘投注4s超时，返回业务的拒单原因
     */
    public static final String TIME_OUT_FOUR_SECONDS_REJECT_RESON = "数据商4秒超时,主动撤单";
    public static final String TIME_OUT_FOUR_SECONDS_REJECT_CODE = "10";

    /**
     * 早盘投注4s超时，返回业务的拒单原因
     */
    public static final String RESULTING_STATUS_VOIDED_REJECT_RESON = "数据商返回无效订单,主动撤单";
    public static final String RESULTING_STATUS_VOIDED_REJECT_CODE = "11";

    /**
     * oddin返回订单key前缀
     */
    public static final String ODDIN_ROBACK_ORDER_TAG = "oddin_roback_order_";

    /**
     * oddin 拉单返回key前缀
     */
    public static final String ODDIN_ROBACK_PULLSINGLE_TAG = "oddin_roback_pullSingle_";

    /**
     * oddin返回订单状态描述
     */
    public static final String ODDIN_ROBACK_ORDER_EXISTS = "oddin_roback_order_exists";

    /**
     * oddin拉单返回订单状态描述
     */
    public static final String ODDIN_ROBACK_PULLSINGLE_EXISTS = "oddin_roback_pullsingle_exists";

    /**
     * oddin返回订单状态在缓存中存在的时间
     */
    public static final Long ODDIN_ROBACK_ORDER_TIME = 30000L;

    /**
     * ty注单缓存matchId的缓存key
     */
    public static final String TY_MATCH_ID_KEY = "order:ty:match:cache:temp:TY-%s";

    /**
     * oddin监听赛事盘口变化保存的缓存key
     */
    public static final String STANDAR_MATCH_MARKET_INFO_OF_ODDIN = "rcs:redis:standard:match:market:oddin:%s";

    /**
     * oddin注单topic
     */
    public static final String RCS_RISK_ODDIN_TICKET = "rcs_risk_oddin_ticket";

    /**
     * 注单用户二级标签list
     */
    public static final String USER_SECONDE_LABEL_IDS_LIST_REDIS_KEY = "user:second:label:ids:list:oddin:%s";

    /**
     * gprc链接状态reids key
     */
    public static final String ODDIN_GRPC_CONNECT_STATUS_KEY = "oddin:grpc:connect:status";


    /**
     * 注单校验链接reids缓存超时时间
     */
    public static final Long VALIDATE_TICKET_GRPC_CONNECTION_TIME = 60L;
}
