package com.panda.sport.sdk.constant;

import java.util.Arrays;
import java.util.List;

/**
 * @description:
 * @author: lithan
 * @date: 2020-08-07 13:32
 **/
public class SdkConstants {

    /***************************校验订单 拒单错误消息对应的code********************************/
    /**
     * 正常
     **/
    public static final int ORDER_ERROR_CODE_OK = 0;
    /**
     * 风控拒单:内部错误
     **/
    public static final int ORDER_ERROR_CODE_RISK = -1;
    /**
     * 配置错误
     **/
    public static final int ORDER_ERROR_CODE_CONFIG = -2;

    /**
     * 限额错误
     **/
    public static final int ORDER_ERROR_CODE_LIMIT = -3;

    /**
     * 虚拟赛事种类
     * */
    public static final List<Integer> VIRSTUAL_SPORT = Arrays.asList( 1001,1004,1002,1007,1008,1009,1010,1011,1012);

    /**
     * 漏单总开关的编码值
     */
    public static final String SWITCH_CODE = "LOUDAN";

}