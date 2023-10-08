package com.panda.rcs.order.reject.entity.enums;

import lombok.Getter;

/**
 * 盘口类型
 * @author admin
 */
@Getter
public enum VarSwitchEnum {

    Close("1","关闭"),
    Open("0","打开"),
            ;

    private String code;
    private String value;

    VarSwitchEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }
}
