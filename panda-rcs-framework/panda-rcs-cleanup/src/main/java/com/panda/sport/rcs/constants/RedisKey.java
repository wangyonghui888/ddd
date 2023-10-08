package com.panda.sport.rcs.constants;

/**
 * Redis常量
 *
 * @author enzo
 */
public interface RedisKey {

    interface Config {
        String PREFIX = "rcs:trade:config:";

        /**
         * 出涨封盘开关缓存key<br/>
         * 数据结构：Hash
         */
        static String getChuZhangSwitchKey(Long matchId, Integer matchType) {
            return String.format(PREFIX + "chuZhangSwitch:%s:%s", matchType, matchId);
        }
    }

    interface MainMarket {

        /**
         * 获取A+模式主盘口信息缓存 key
         *
         * @param matchId
         * @return
         */
        static String getAutoPlusMainMarketInfoKey(Long matchId) {
            return String.format("rcs:build:mainMarketInfo:autoPlus:%s", matchId);
        }

        /**
         * 获取L模式主盘口信息缓存 key
         *
         * @param matchId
         * @return
         */
        static String getLinkageMainMarketInfoKey(Long matchId) {
            return String.format("rcs:build:mainMarketInfo:linkage:%s", matchId);
        }

        static String getLinkageDataSourceTimeKey(Long matchId, Long playId, Long subPlayId) {
            return String.format("rcs:build:dataSourceTime:linkage:%s:%s:%s", matchId, playId, subPlayId);
        }
    }

    /**
     * 水差关联状态<br/>
     * 数据结构：Hash<br/>
     * key = rcs:relevanceType:{matchId}:{playId}<br/>
     * field = subPlayId
     *
     * @param matchId
     * @param playId
     * @return
     */
    static String getRelevanceTypeKey(Long matchId, Long playId) {
        return String.format("rcs:relevanceType:%s:%s", matchId, playId);
    }

    /**
     * 位置状态缓存key
     *
     * @param matchId
     * @param categoryId
     * @return
     */
    static String getMarketPlaceStatusConfigKey(Long matchId, Long categoryId) {
        return String.format("rcs:marketStatusConfig:%s:%s", matchId, categoryId);
    }

    /**
     * 位置状态缓存key，包含子玩法
     *
     * @param matchId
     * @param play
     * @return
     */
    static String getPlaceStatusConfigKey(Long matchId, String play) {
        return String.format("rcs:marketStatusConfig:%s:%s", matchId, play);
    }

    /**
     * 占位符主玩法状态缓存key
     *
     * @param matchId
     * @return
     */
    static String getPlaceholderMainPlayStatusKey(Long matchId) {
        return String.format("rcs:tradeStatus:placeholderMainPlay:%s", matchId);
    }

    /**
     * 赛事状态缓存key
     *
     * @param matchId
     * @return
     */
    static String getMatchTradeStatusKey(Long matchId) {
        return String.format("rcs:tradeStatus:match:%s", matchId);
    }

    /**
     * 玩法集编码状态缓存key，暂只适用于足球
     *
     * @param matchId
     * @return
     */
    static String getPlaySetCodeStatusKey(Long matchId) {
        return String.format("rcs:tradeStatus:playSetCode:%s", matchId);
    }

    /**
     * 普通玩法自动关盘状态缓存key
     *
     * @param matchId
     * @return
     */
    static String getAutoCloseStatusKey(Long matchId) {
        return String.format("rcs:tradeStatus:autoClose:%s", matchId);
    }

    /**
     * 操盘模式缓存key
     *
     * @param matchId
     * @return
     */
    static String getTradeModeKey(Long matchId) {
        return String.format("rcs:tradeMode:%s", matchId);
    }

    /**
     * A+模式切换标志
     */
    static String getAutoPlusSwitchFlagKey(Long matchId, Long playId) {
        return String.format("rcs:dataserver:odds:risk:%s:%s", matchId, playId);
    }

    /**
     * 联动模式切换标志
     */
    static String getLinkageSwitchFlagKey(Long matchId, Long playId) {
        return String.format("rcs:trade:linkageSwitchFlag:%s:%s", matchId, playId);
    }

    static String getLinkageModeSignKey(Long matchId, Long playId) {
        return String.format("rcs:trade:odds:linkageMode:sign:%s:%s", matchId, playId);
    }

    static String getLinkageModeMarketKey(Long matchId, Long playId) {
        return String.format("rcs:trade:odds:linkageMode:market:%s:%s", matchId, playId);
    }

    /**
     * 出涨预警标志缓存key<br/>
     * 数据结构：Hash
     */
    static String getChuZhangWarnSignKey(Long matchId, Integer matchType) {
        return String.format("rcs:trade:chuZhangWarnSign:%s:%s", matchType, matchId);
    }

    static String getChuZhangFrequencyKey(Long matchId) {
        return String.format("rcs:task:chuZhangFrequency:%s", matchId);
    }
}
