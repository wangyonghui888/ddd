package com.panda.sport.rcs.common.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量定义
 *
 * @author lithan
 */
public class Constants {
    public static final int SUCCESS_CODE = 200;
    public static final String SUCCESS_MSG = "操作成功";
    public static final int FAIL_CODE = 500;
    public static final String FAIL_MSG = "操作失败";

    /**
     * bigdecimal精度
     */
    public static int PRECISION = 2;

    /**
     * 资讯投注
     * 事件code 进球、红牌
     */
    public final static String EVENT_CODES = "'red_card','goal','corner'";


    /**
     * 规则类型
     */
    public static Map<Integer, String> ruleTypeMap = new HashMap<>();
    /**
     * 标签类型
     */
    public static Map<Integer, String> tagTypeMap = new HashMap<>();

    static {
        ruleTypeMap.put(1, "基本属性类");
        ruleTypeMap.put(2, "投注特征类");
        ruleTypeMap.put(3, "访问特征类");
        ruleTypeMap.put(4, "财务特征类");
    }

    static {
        tagTypeMap.put(1, "基本属性类");
        tagTypeMap.put(2, "投注特征类");
        tagTypeMap.put(3, "访问特征类");
        tagTypeMap.put(4, "财务特征类");
    }



    public static final String RCS_BUSINESS_LOG_SAVE = "rcs_business_log_save";


}
