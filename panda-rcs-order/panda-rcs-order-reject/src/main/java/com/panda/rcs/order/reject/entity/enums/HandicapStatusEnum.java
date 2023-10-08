package com.panda.rcs.order.reject.entity.enums;

import lombok.Getter;

/**
 * 盘口状态枚举类
 */
@Getter
public enum HandicapStatusEnum {
    /**
     * 盘口状态0-5. 0:active 开盘, 1:suspended 封盘, 2:deactivated 关盘, 3:settled 已结算, 4:cancelled 已取消, 5:handedOver  盘口的中间状态，该状态的盘口后续不会有赔率过来 11:锁盘状态
     */

    active(0, "开盘"),
    suspended(1, "封盘"),
    deactivated(2, "关盘"),
    settled(3, "已结算"),
    cancelled(4, "已取消"),
    handedOver(5, "盘口的中间状态"),
    ;

    private int code;
    private String value;

    HandicapStatusEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

}
