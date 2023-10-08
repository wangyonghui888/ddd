package com.panda.sport.rcs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Project Name : panda-rcs-trade-group
 * @Package Name : panda-rcs-trade-group
 * @Description : 操盘模式枚举
 * @Author : Paca
 * @Date : 2020-11-05 10:48
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Getter
@AllArgsConstructor
public enum TradeModeEnum {
    AUTO(0, "自动模式(A)"),
    MANUAL(1, "手动模式(M)"),
    AUTO_PLUS(2, "自动加强模式(A+)"),
    LINKAGE(3, "联动模式(L)");

    private Integer mode;
    private String value;

    public static boolean isAuto(Integer tradeType) {
        return AUTO.getMode().equals(tradeType);
    }

    public static boolean isManual(Integer tradeType) {
        return MANUAL.getMode().equals(tradeType);
    }

    public static boolean isAutoPlus(Integer tradeType) {
        return AUTO_PLUS.getMode().equals(tradeType);
    }

    public static boolean isLinkage(Integer tradeType) {
        return LINKAGE.getMode().equals(tradeType);
    }

    public static boolean isNotAuto(Integer tradeType) {
        return isManual(tradeType) || isAutoPlus(tradeType) || isLinkage(tradeType);
    }
}
