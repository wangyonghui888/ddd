package com.panda.sport.rcs.enums;

/***
 * 操作数据源 自动 手动
 */
public enum MarketMillionEnum {

    OPEN(0, 0),
    LOCK(11, 200000000),
    SEAL(1, 300000000),
    CLOSE(2, 400000000),
    SELLE(3, 400000000),
    CANCELL(4, 400000000),
    HANDOVER(5, 400000000);

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
