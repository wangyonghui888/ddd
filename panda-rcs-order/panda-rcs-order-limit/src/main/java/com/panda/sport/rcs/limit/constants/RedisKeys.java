package com.panda.sport.rcs.limit.constants;

/**
 * 	定义redis key的常量类
 *
 */
public class RedisKeys {
    
    /**
     *	 商户单日串关限额
     * rcs:paid:日期:bus:商户ID
     */
    public static String PAID_DATE_BUS_SERIES_REDIS_CACHE = "rcs:paid:%s:busseries:%s";

}
