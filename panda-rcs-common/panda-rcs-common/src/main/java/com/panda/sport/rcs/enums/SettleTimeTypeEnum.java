package com.panda.sport.rcs.enums;

/**
 * @author :  kimi
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.constants
 * @Description :  投注阶段枚举
 * @Date: 2019-10-12 11:32
 */
public enum SettleTimeTypeEnum {
    //
    MATCH_TIME(1, "开始时间"),

    BET_TIME(2, "投注时间"),

    SETTLE_TIME(3, "结算时间");

    private Integer code;

    private String value;

    SettleTimeTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
