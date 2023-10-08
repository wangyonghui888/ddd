package com.panda.sport.rcs.enums;

/***
 * 操作数据源 自动 手动
 */
public enum MarketRollEnum {

    ROLL(0, "滚球盘口"),
    PRE(1, "赛前盘口");

    private Integer code;
    private String value;

    MarketRollEnum(Integer code, String value) {
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
