package com.panda.rcs.order.reject.entity.enums;

import lombok.Getter;

/**
 * 预约订单状态枚举类
 */
@Getter
public enum OrderStatusEnum {

    /**
     * 预约订单状态(0.待处理  1.成功  2.失败   3.取消订单)
     */
    PENDING(0, "待处理"),
    SUCCESS(1, "成功"),
    FAIL(2, "失败"),
    CANCEL(3, "取消订单"),
    ;

    private int code;
    private String value;

    OrderStatusEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
