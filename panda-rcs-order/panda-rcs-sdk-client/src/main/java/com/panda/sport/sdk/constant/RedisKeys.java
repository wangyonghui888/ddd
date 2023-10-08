package com.panda.sport.sdk.constant;

/**
 * 定义redis key的常量类
 *
 * @author :  kane
 * @Project Name :  panda-rcs-common
 * @Package Name :  com.panda.sport.rcs.constants
 * @Description :  TODO
 * @Creation Date:  2019-09-06 13:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class RedisKeys {

    //用户维度
    public static String PAID_DATE_USER_REDIS_CACHE = "rcs:paid:%s:user:%s:%s";

    //赛事维度  日期  赛事级别  赛事id
    public static String PAID_DATE_MATCH_REDIS_CACHE = "rcs:paid:%s:match:%s:%s";

    /**
     * 商户单日已用限额
     * rcs:paid:日期:bus:商户ID
     */
    public static String PAID_DATE_BUS_REDIS_CACHE = "rcs:paid:%s:bus:%s";
    
    /**
     *	 商户单日串关已用限额
     * rcs:paid:日期:bus:商户ID
     */
    public static String PAID_DATE_BUS_SERIES_REDIS_CACHE = "rcs:paid:%s:busseries:%s";

    /**
     * 商户停止收单标识
     * rcs:paid:日期:bus:商户ID_stop
     */
    public static String PAID_DATE_BUS_STOP_REDIS_CACHE = "rcs:paid:%s:bus:%s_stop";

    //赔付配置
    public static String PAID_CONFIG_REDIS_CACHE = "ccca:rcs:sdk:config:";
    public static String MONITOR_PAID_CONFIG_REDIS_CACHE = "rcs:sdk:config:monitor";

    /**
     * 商户单场串关已用限额
     * rcs:paid:日期:bus:商户ID:赛事ID
     */
    public static String PAID_DATE_BUS_REDIS_MATCH_CACHE = "rcs:user:%s:bus:%s:match:%s";

    /**
     * Rcs SDK Config
     **/
    public static String RCS_SDK_CONFIG = "rcs_sdk_config_";

    /**
     * 结算派彩MQ
     **/
    public static String RCS_SDK_SETTLE = "OSMC_SETTLE_RESULT_SDK";

    /**
     * 多赛种联赛限额调整
     */
    public static String TEMPLATE_TOURNAMENT_AMOUNT = "rcs:template:tournament:";

    /**
     * 紧急限额key
     */
    public static String LIMIT_SWITCH_KEY = "rcs:limit:switch:urgent";

    /**
     * 内部商户默认内部接单
     */
    public final static String THIRD_MERCHANT_STATUS = "rcs:third:merchant:status:list";

    /**
     * 动态漏单总开关key
     */
    public static String REDIS_RCS_SWITCH_CONFIG_KEY = "rcs:rcs_switch:switchCode:%s";
    /**
     * 商户漏单配置相关数据key
     */
    public static String REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY ="rcs:dynamic_missed_order_rcs_configuration:merchants_id";

    public static String KEY ="CS";
}
