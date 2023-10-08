package com.panda.sport.rcs.oddin.common;

/**
 * redis key 常量定义
 */
public class RedisKeyConstants {

    /**
     * 注单校验链接reids缓存key
     */
    public static final String VALIDATE_TICKET_GRPC_CONNECTION = "validate:ticket:grpc:connection";

    /**
     * 拉单校验链接reids缓存key
     */
    public static final String VALIDATE_PULLSINGLE_GRPC_CONNECTION = "validate:pullsingle:grpc:connection";

    /**
     * 无效订单进行撤单的redis缓存所的key
     */
    public static final String RESULTING_STATUS_VOIDED_ORDER_REDIS_LOCK_KEY = "resulting:status:voided:order:redis:lock:key:%s";

    /**
     * 无效订单缓存标志
     */
    public static final String RESULTING_STATUS_VOIDED_ORDER_EXIST = "void-order-exist";
}
