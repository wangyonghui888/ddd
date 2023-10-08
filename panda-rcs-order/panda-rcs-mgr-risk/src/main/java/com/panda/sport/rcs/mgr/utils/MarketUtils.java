package com.panda.sport.rcs.mgr.utils;

import com.panda.sport.rcs.enums.MarketStatusEnum;
import com.panda.sport.rcs.enums.TradeTypeEnum;
import com.panda.sport.rcs.enums.TraderLevelEnum;

/**
 * @Project Name: panda-rcs-trade-group
 * @Package Name: com.panda.sport.rcs.utils
 * @Description : Market 工具类
 * @Author : Paca
 * @Date : 2020-07-16 13:51
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class MarketUtils {

    /**
     * 是否赛事级别
     *
     * @param tradeLevel
     * @return
     */
    public static boolean isMatchLevel(Integer tradeLevel) {
        return TraderLevelEnum.MATCH.getLevel().equals(tradeLevel);
    }

    /**
     * 是否玩法级别
     *
     * @param tradeLevel
     * @return
     */
    public static boolean isPlayLevel(Integer tradeLevel) {
        return TraderLevelEnum.PLAY.getLevel().equals(tradeLevel);
    }

    /**
     * 是否盘口级别
     *
     * @param tradeLevel
     * @return
     */
    public static final boolean isMarketLevel(Integer tradeLevel) {
        return TraderLevelEnum.MARKET.getLevel().equals(tradeLevel);
    }

    /**
     * 是否玩法集
     *
     * @param tradeLevel
     * @return
     */
    public static boolean isPlaySetLevel(Integer tradeLevel) {
        return TraderLevelEnum.STATE.getLevel().equals(tradeLevel);
    }

    /**
     * 是否自动操盘
     *
     * @param tradeType
     * @return
     */
    public static boolean isAuto(Integer tradeType) {
        return TradeTypeEnum.AUTO.getCode().equals(tradeType);
    }

    /**
     * 开
     *
     * @param marketStatus
     * @return
     */
    public static boolean isOpen(Integer marketStatus) {
        return Integer.valueOf(MarketStatusEnum.OPEN.getState()).equals(marketStatus);
    }

    /**
     * 关
     *
     * @param marketStatus
     * @return
     */
    public static boolean isClose(Integer marketStatus) {
        return Integer.valueOf(MarketStatusEnum.CLOSE.getState()).equals(marketStatus);
    }

    /**
     * 封
     *
     * @param marketStatus
     * @return
     */
    public static boolean isSeal(Integer marketStatus) {
        return Integer.valueOf(MarketStatusEnum.SEAL.getState()).equals(marketStatus);
    }

    /**
     * 锁
     *
     * @param marketStatus
     * @return
     */
    public static boolean isLock(Integer marketStatus) {
        return Integer.valueOf(MarketStatusEnum.LOCK.getState()).equals(marketStatus);
    }

    public static boolean checkTradeLevel(Integer tradeLevel) {
        return isMatchLevel(tradeLevel) || isPlayLevel(tradeLevel) || isMarketLevel(tradeLevel) || isPlaySetLevel(tradeLevel);
    }

    public static boolean checkMarketStatus(Integer marketStatus) {
        return isOpen(marketStatus) || isClose(marketStatus) || isSeal(marketStatus) || isLock(marketStatus);
    }

}
