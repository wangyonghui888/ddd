package com.panda.sport.rcs.enums;

/***
 * 操作数据源 自动 手动
 */
public enum TradeTypeEnum {

    AUTO(0, "自动"),
    MANUAD(1, "手动");

    private Integer code;
    private String value;

    TradeTypeEnum(Integer code, String value) {
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
