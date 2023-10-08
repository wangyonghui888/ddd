package com.panda.sport.rcs.mgr.constant;


import java.math.BigDecimal;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.constant
 * @Description :  TODO
 * @Date: 2020-09-26 15:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class Constants {
    /**
     * 基础值 1亿
     */
    public static final BigDecimal BUSINESS_SINGLE_DAY_LIMIT = new BigDecimal(100000000);
    /**
     * 最小比例
     */
    public static final BigDecimal MIN_PROPORTION = new BigDecimal("0.0001");
    /**
     * 最大比例
     */
    public static final BigDecimal MAX_PROPORTION = new BigDecimal("10");
    /**
     * 前端传过来的数据除以100
     */
    public static final BigDecimal BASE = new BigDecimal(100);
    /**
     * 限额基数
     */
    public static final Long QUOTA_BASE = 1000000L;
    /**
     * 单日赔付比例
     */
    public static final BigDecimal DAY_COMPENSATION_PROPORTION = new BigDecimal(1);
    /**
     * 单日赔付比例
     */
    public static final BigDecimal DAY_COMPENSATION_PROPORTION_DATA = new BigDecimal(0.15);
    /**
     * 单注比例
     */
    public static final BigDecimal SINGLE_BET_LIMIT_RATIO = new BigDecimal(0.2);
    /**
     * 单注比例
     */
    public static final BigDecimal SINGLE_BET_LIMIT_RATIO_DATA = new BigDecimal(0.06);

    public static final String BUSINESS_TODAY_SWITCH_KEY = "risk:trade:businessTodaySwitchKey";

    /**
     * 商户限额key
     */
    public static final String MERCHANT_LIMIT_KEY = "rcs:limit:merchants:%s";

    public static String getBusinessSwitchKey() {
        return "stray:limit:bussiness:switch:key";
    }

    //商户配置
    public static final String RCS_QUOTA_BUSINESS_LIMIT = "rcs:quota:business:limit";

    public static final String RCS_HIDE_ORDER_SAVE = "rcs_queue_hide_order_save";

    /**
     * 藏单比例key
     */
    public static String RCS_DYNAMIC_HIDE_ORDER_RATE = "rcs:dynamic:hide:order:rate:%s:%s";

    /**
     * 藏单比例key过期时间(3个月)
     */
    public static Long RCS_DYNAMIC_HIDE_ORDER_RATE_EXPIRT = 60 * 60 * 24 * 90L;

    /**
     * 投注货量动态风控开关
     */
    public static String RCS_RISK_BET_VOLUME_SWITCH = "rcs:risk:bet:volume:switch:";

    /**
     * 信用商户单注限额比例
     */
    public static  String RCS_TRADE_CREDIT_BUSINESS_BET_PERCENT="risk:trade:credit:businessSingleBetPercent:%s";
    /**
     * 信用商户通用货量百分比
     */
    public static  String RCS_TRADE_CREDIT_BUSINESS_VOLUME_PERCENT="risk:trade:credit:business:volumePercent";

    public static  String RCS_HIDE_ORDER_BATCH_SAVE="RCS:HIDE:ORDER:BATCH:SAVE";

    public static final String RCS_BUSINESS_LOG_SAVE = "rcs_business_log_save";

    //mts商户折扣利率


    //mts通用商户折扣利率
    public static final String MTS_AMOUNT_RATE_ALL = "rcs:mts:amount_rate";
    public static final String VIRTUAL_AMOUNT_RATE_ALL = "rcs:virtual:amount_rate";
    public static final String VR_ENABLE_AMOUNT_RATE_ALL = "rcs:vr:enable";
    public static final String CTS_AMOUNT_RATE_ALL = "rcs:cts:amount_rate";
    public static final String GTS_AMOUNT_RATE_ALL = "rcs:gts:amount_rate";
    public static final String OTS_AMOUNT_RATE_ALL = "rcs:ots:amount_rate";
    public static final String RTS_AMOUNT_RATE_ALL = "rcs:rts:amount_rate";

    public static final String VR_ENABLE_AMOUNT_RATE = "rcs:vr:enable:tenantId:%s";
    public static final String CTS_AMOUNT_RATE = "rcs:cts:amount_rate:tenantId:%s";
    public static final String GTS_AMOUNT_RATE = "rcs:gts:amount_rate:tenantId:%s";
    public static final String OTS_AMOUNT_RATE = "rcs:ots:amount_rate:tenantId:%s";
    public static final String RTS_AMOUNT_RATE = "rcs:rts:amount_rate:tenantId:%s";
    public static final String MTS_AMOUNT_RATE = "rcs:mts:amount_rate:tenantId:%s";
    public static final String VIRTUAL_AMOUNT_RATE = "rcs:virtual:amount_rate:tenantId:%s";

    //日志操作类型
    //通用设置
    public static final String  VR_TYSZ = "10100";
    //批量设置
    public static final String  VR_PLSZ = "10101";
    //例外批量设置
    public static final String  VR_LWPLSZ = "10102";
    //单商户变更
    public static final String  VR_DSHBG = "10103";


}
