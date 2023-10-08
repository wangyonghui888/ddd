package com.panda.sport.rcs.mgr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderHideEnum {
    UN_CHECKED(0, "未结算"),
    CHECKED(1, "已结算");
    private Integer code;
    private String value;
}
