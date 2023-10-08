package com.panda.sport.rcs.constants;

import java.util.concurrent.TimeUnit;

/**
 * @Project Name : rcs-parent
 * @Package Name : rcs-parent
 * @Description : Redis 常亮类
 * @Author : Paca
 * @Date : 2021-12-15 12:11
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RedisKey {

    interface Second {

        int DAYS_3 = getByDays(3);

        int DAYS_30 = getByDays(30);

        /**
         * 天数转换成秒数
         *
         * @param days
         * @return
         */
        static int getByDays(int days) {
            return (int) TimeUnit.DAYS.toSeconds(days);
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
         * 获取A+模式主盘口信息缓存 hashKey
         *
         * @param playId
         * @param subPlayId
         * @return
         */
        static String getAutoPlusMainMarketInfoHashKey(Long playId, Long subPlayId) {
            return String.format("%s:%s", playId, subPlayId);
        }

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
     * kir-1788 redisKey
     */
    String RCS_TOURNAMENT_TEMPLATE_ACCEPT_AUTO_CHANGE_KEY = "rcs:tournament:template:accept:auto:change:matchId:%s:categorySetId:%s";
    static String getRcsTournamentTemplateAcceptAutoChangeKey(Long matchId, Long categorySetId) {
        return String.format(RCS_TOURNAMENT_TEMPLATE_ACCEPT_AUTO_CHANGE_KEY, matchId, categorySetId);
    }

    /**
     * AO特殊事件开关
     */
    String SPECIAL_EVENT_STATUS_KEY = "rcs:trade:type:%s:typeVal:%s:special:event:switch";

}
