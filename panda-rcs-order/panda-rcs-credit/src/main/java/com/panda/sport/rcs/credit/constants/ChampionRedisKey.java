package com.panda.sport.rcs.credit.constants;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 冠军玩法 Redis key
 * @Author : Paca
 * @Date : 2021-06-09 15:05
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ChampionRedisKey {

    /**
     * 限额Redis更新记录
     * rcs:limit:redisUpdateRecord:champion:订单号
     */
    String LIMIT_REDIS_UPDATE_RECORD_KEY = "rcs:limit:redisUpdateRecord:champion:%s";

    String PREFIX = "rcs:champion:";

    /**
     * 限额配置，缓存数据单位元
     */
    interface Limit {

        String LIMIT_PREFIX = PREFIX + "limit:";

        /**
         * 商户玩法赔付限额
         */
        String MERCHANT_PLAY_FIELD = "merchantPlay";

        /**
         * 用户玩法赔付限额
         */
        String USER_PLAY_FIELD = "userPlay";

        /**
         * 用户单注投注赔付限额
         */
        String USER_SINGLE_BET_FIELD = "userSingleBet";

        /**
         * 冠军玩法限额配置
         *
         * @param matchId
         * @param marketId
         * @return
         */
        static String getChampionKey(Long matchId, Long marketId) {
            return String.format(LIMIT_PREFIX + "%s:%s", matchId, marketId);
        }
    }

    /**
     * 已用额度，缓存数据单位分
     */
    interface Used {

        String USED_PREFIX = PREFIX + "used:";

        /**
         * 商户维度每个投注项投注额累计
         *
         * @param tenantId
         * @param creditId
         * @param matchId
         * @param marketId
         * @return
         */
        static String getMerchantBetKey(Long tenantId, String creditId, Long matchId, Long marketId) {
            return String.format(USED_PREFIX + "merchantBet:%s:%s:%s{%s}", tenantId, creditId, matchId, marketId);
        }

        /**
         * 商户维度每个投注项期望赔付累计
         *
         * @param tenantId
         * @param creditId
         * @param matchId
         * @param marketId
         * @return
         */
        static String getMerchantPaymentKey(Long tenantId, String creditId, Long matchId, Long marketId) {
            return String.format(USED_PREFIX + "merchantPayment:%s:%s:%s{%s}", tenantId, creditId, matchId, marketId);
        }

        /**
         * 用户维度每个投注项投注额累计
         *
         * @param userId
         * @param matchId
         * @param marketId
         * @return
         */
        static String getUserBetKey(Long userId, Long matchId, Long marketId) {
            return String.format(USED_PREFIX + "usedBet:%s:%s{%s}", userId, matchId, marketId);
        }

        /**
         * 用户维度每个投注项期望赔付累计
         *
         * @param userId
         * @param matchId
         * @param marketId
         * @return
         */
        static String getUserPaymentKey(Long userId, Long matchId, Long marketId) {
            return String.format(USED_PREFIX + "usedPayment:%s:%s{%s}", userId, matchId, marketId);
        }
    }
}
