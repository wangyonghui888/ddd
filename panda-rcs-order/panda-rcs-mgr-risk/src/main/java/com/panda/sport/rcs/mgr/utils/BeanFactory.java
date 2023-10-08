package com.panda.sport.rcs.mgr.utils;

import com.panda.sport.rcs.enums.MarketStatusEnum;
import com.panda.sport.rcs.pojo.RcsTradeConfig;

/**
 * @Project Name: panda-rcs-trade-group
 * @Package Name: com.panda.sport.rcs.factory
 * @Description : Bean 工厂
 * @Author : Paca
 * @Date : 2020-08-02 10:55
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class BeanFactory {

    /**
     * 默认赛事状态
     *
     * @return
     */
    public static RcsTradeConfig defaultMatchStatus() {
        return new RcsTradeConfig()
                // 时间顺序
                .setId(0)
                .setStatus(MarketStatusEnum.OPEN.getState());
    }

    /**
     * 默认盘口位置状态
     *
     * @return
     */
    public static RcsTradeConfig defaultMarketPlaceStatus() {
        return new RcsTradeConfig()
                // 时间顺序
                .setId(-1)
                .setStatus(MarketStatusEnum.OPEN.getState());
    }

    /**
     * 默认玩法状态
     *
     * @return
     */
    public static RcsTradeConfig defaultCategoryStatus() {
        return new RcsTradeConfig()
                // 时间顺序
                .setId(-2)
                .setStatus(MarketStatusEnum.OPEN.getState());
    }

    /**
     * 默认玩法集状态
     *
     * @return
     */
    public static RcsTradeConfig defaultCategorySetStatus() {
        return new RcsTradeConfig()
                // 时间顺序
                .setId(-3)
                .setStatus(MarketStatusEnum.OPEN.getState());
    }

}
