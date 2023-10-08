package com.panda.sport.rcs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Project Name: panda-rcs-trade-group
 * @Package Name: com.panda.sport.rcs.enums
 * @Description : 操盘级别枚举
 * @Author : Paca
 * @Date : 2020-10-27 9:36
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Getter
@AllArgsConstructor
public enum TradeLevelEnum {
    MATCH(1, "赛事级别"),
    PLAY(2, "玩法级别"),
    MARKET(3, "盘口级别"),
    PLAY_SET(4, "玩法集级别"),
    BATCH_PLAY(5, "批量玩法级别");

    private Integer level;
    private String name;

    public static boolean isMatchLevel(Integer tradeLevel) {
        return MATCH.getLevel().equals(tradeLevel);
    }

    public static boolean isPlayLevel(Integer tradeLevel) {
        return PLAY.getLevel().equals(tradeLevel);
    }

    public static boolean isMarketLevel(Integer tradeLevel) {
        return MARKET.getLevel().equals(tradeLevel);
    }

    public static boolean isPlaySetLevel(Integer tradeLevel) {
        return PLAY_SET.getLevel().equals(tradeLevel);
    }

    public static boolean isBatchPlayLevel(Integer tradeLevel) {
        return BATCH_PLAY.getLevel().equals(tradeLevel);
    }

    /**
     * 操盘模式切换 支持的操盘级别
     *
     * @param tradeLevel
     * @return
     */
    public static boolean updateTradeTypeCheck(Integer tradeLevel) {
        return isPlayLevel(tradeLevel) || isBatchPlayLevel(tradeLevel);
    }

    /**
     * 状态变更 支持的操盘级别
     *
     * @param tradeLevel
     * @return
     */
    public static boolean updateStatusCheck(Integer tradeLevel) {
        return isMatchLevel(tradeLevel) || isPlayLevel(tradeLevel) || isMarketLevel(tradeLevel) || isPlaySetLevel(tradeLevel) || isBatchPlayLevel(tradeLevel);
    }
}
