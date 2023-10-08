package com.panda.sport.rcs.oddin.util;

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

}