package com.panda.rcs.pending.order.enums;

import lombok.Getter;

/**
 * 盘口类型
 */
@Getter
public enum MatchTypeEnum {

    Live(0,"滚球"),
    early(1,"早盘"),
            ;

    private int code;
    private String value;

    MatchTypeEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
