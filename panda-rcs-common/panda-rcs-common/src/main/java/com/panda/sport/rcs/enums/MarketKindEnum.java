package com.panda.sport.rcs.enums;

/**
 * @Description   //盘口类别枚举
 * @Param
 * @Author kimi
 * @Date 2019/12/11
 * @return
 **/
public enum MarketKindEnum {
    /**
     * 1.欧盘；
     * 2.香港盘；
     * 3.印尼盘；
     * 4.马来盘；
     * 5.英盘；
     * 6.美盘。
     */
    Europe("EU"), HongKong("HK"), Indonesia("ID"), Malaysia("MY"), UnitedKingdom("GB"), UnitedStates("US");


    /**
     * 盘口类别取值
     */
    private String value;

    MarketKindEnum(String value) {
        this.value = value;
    }

    /**
     * 根据类型值返回类型枚举
     *
     * @param valueParam
     * @return
     */
    public static MarketKindEnum getMarketKindByValue(String valueParam) {
        for (MarketKindEnum kind : MarketKindEnum.values()) {
            if (kind.value.equals(valueParam)) {
                return kind;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}
