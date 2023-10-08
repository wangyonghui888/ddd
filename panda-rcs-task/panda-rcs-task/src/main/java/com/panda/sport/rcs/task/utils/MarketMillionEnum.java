package com.panda.sport.rcs.task.utils;

/***
 * 操作数据源 自动 手动
 */
public enum MarketMillionEnum {

    OPEN(0, 0),
    LOCK(11, 10000),
    SEAL(1, 0),
    CLOSE(2, 10000),
    SELLE(3, 10000),
    CANCELL(4, 10000),
    HANDOVER(5, 10000);

    private Integer code;
    private Integer value;

    MarketMillionEnum(Integer code, Integer value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public Integer getValue() {
        return value;
    }
    public static MarketMillionEnum getEnum(Integer code) {
        for (MarketMillionEnum marketMillionEnum : values()) {
            if (marketMillionEnum.getCode() .equals(code) ) {
                return marketMillionEnum;
            }
        }
        return null;
    }
}

