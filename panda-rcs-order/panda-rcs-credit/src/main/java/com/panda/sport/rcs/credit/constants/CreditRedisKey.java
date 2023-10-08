package com.panda.sport.rcs.credit.constants;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用 Redis key
 * @Author : Paca
 * @Date : 2021-05-04 19:45
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface CreditRedisKey {

    /**
     * 商户或信用代理单日限额<br/>
     * 数据结构：Hash<br/>
     * field = 商户ID或信用代理ID
     */
    String BUSINESS_LIMIT_KEY = "rcs:limit:merchants:";

    /**
     * 限额Redis更新记录
     * rcs:limit:redisUpdateRecord:credit:订单号
     */
    String LIMIT_REDIS_UPDATE_RECORD_KEY = "rcs:limit:redisUpdateRecord:credit:%s";

    String PREFIX = "rcs:credit:";

    /**
     * 单关高赔配置缓存key
     */
    String SINGLE_HIGH_ODDS_KEY = PREFIX + "config:single:highOdds";

    /**
     * 串关高赔配置缓存key
     */
    String SERIES_HIGH_ODDS_KEY = PREFIX + "config:series:highOdds";

    /**
     * 信用商户通用货量百分比
     */
    String RCS_TRADE_CREDIT_BUSINESS_VOLUME_PERCENT = PREFIX + "credit:business:volumePercent";


    /**
     * 限额配置，缓存数据单位元
     */
    interface Limit {

        String LIMIT_PREFIX = PREFIX + "limit:";

        /**
         * 串关每日投注限额<br/>
         * 数据结构：Hash<br/>
         * key = rcs:credit:limit:seriesAgent:{merchantId}:{creditId}<br/>
         * field = seriesType
         *
         * @param merchantId
         * @param creditId
         * @return
         */
        static String getSeriesKey(Long merchantId, String creditId) {
            return String.format(LIMIT_PREFIX + "seriesAgent:%s:%s", merchantId, creditId);
        }

        static String getSeriesOldKey(Long merchantId, String creditId) {
            return String.format(LIMIT_PREFIX + "series:%s:%s", merchantId, creditId);
        }

        /**
         * 赛事累计可投额度<br/>
         * 数据结构：Hash<br/>
         * key = rcs:credit:limit:singleMatch:{merchantId}:{creditId}:{creditSportId}<br/>
         * field = creditTournamentLevel（信用联赛等级）
         *
         * @param merchantId
         * @param creditId
         * @param creditSportId 信用赛种
         * @return
         */
        static String getSingleMatchKey(Long merchantId, String creditId, Integer creditSportId) {
            return String.format(LIMIT_PREFIX + "match:%s:%s:%s", merchantId, creditId, creditSportId);
        }

        static String getSingleMatchOldKey(Long merchantId, String creditId, Integer creditSportId) {
            return String.format(LIMIT_PREFIX + "singleMatch:%s:%s:%s", merchantId, creditId, creditSportId);
        }

        /**
         * 玩法累计可投额度<br/>
         * 数据结构：Hash<br/>
         * key = rcs:credit:limit:singlePlay:{merchantId}:{creditId}:{creditSportId}:{playClassify}:{betStage}<br/>
         * field = creditTournamentLevel（信用联赛等级）
         *
         * @param merchantId
         * @param creditId
         * @param creditSportId 信用赛种
         * @param playClassify
         * @param betStage      投注阶段，pre-早盘，live-滚球
         * @return
         */
        static String getSinglePlayKey(Long merchantId, String creditId, Integer creditSportId, Integer playClassify, String betStage) {
            return String.format(LIMIT_PREFIX + "singlePlay:%s:%s:%s:%s:%s", merchantId, creditId, creditSportId, playClassify, betStage);
        }

        static String getSinglePlayOldKey(Long merchantId, String creditId, Integer creditSportId, Integer creditTournamentLevel, Integer playClassify) {
            return String.format(LIMIT_PREFIX + "singlePlay:%s:%s:%s:%s:%s", merchantId, creditId, creditSportId, creditTournamentLevel, playClassify);
        }

        /**
         * 用户串关每日投注限额<br/>
         * 数据结构：Hash<br/>
         * key = rcs:credit:limit:seriesUser:{merchantId}:{creditId}:{userId}<br/>
         * field = seriesType
         *
         * @param merchantId
         * @param creditId
         * @param userId
         * @return
         */
        static String getUserSeriesKey(Long merchantId, String creditId, Long userId) {
            return String.format(LIMIT_PREFIX + "seriesUser:%s:%s:%s", merchantId, creditId, userId);
        }

        static String getUserSeriesOldKey(Long merchantId, String creditId, Long userId) {
            return String.format(LIMIT_PREFIX + "series:user:%s:%s:%s", merchantId, creditId, userId);
        }

        /**
         * 用户玩法累计可投额度<br/>
         * 数据结构：Hash<br/>
         * key = rcs:credit:limit:singlePlay:user:{merchantId}:{creditId}:{userId}:{creditSportId}:{playClassify}:{betStage}<br/>
         * field = creditTournamentLevel（信用联赛等级）
         *
         * @param merchantId
         * @param creditId
         * @param userId
         * @param creditSportId 信用赛种
         * @param playClassify
         * @param betStage      投注阶段，pre-早盘，live-滚球
         * @return
         */
        static String getUserSinglePlayKey(Long merchantId, String creditId, Long userId, Integer creditSportId, Integer playClassify, String betStage) {
            return String.format(LIMIT_PREFIX + "singlePlay:user:%s:%s:%s:%s:%s:%s", merchantId, creditId, userId, creditSportId, playClassify, betStage);
        }

        static String getUserSinglePlayOldKey(Long merchantId, String creditId, Long userId, Integer creditSportId, Integer creditTournamentLevel, Integer playClassify) {
            return String.format(LIMIT_PREFIX + "singlePlay:user:%s:%s:%s:%s:%s:%s", merchantId, creditId, userId, creditSportId, creditTournamentLevel, playClassify);
        }

        /**
         * 用户玩法单注可投额度<br/>
         * 数据结构：Hash<br/>
         * key = rcs:credit:limit:singlePlayBet:user:{merchantId}:{creditId}:{userId}:{creditSportId}:{playClassify}:{betStage}<br/>
         * field = creditTournamentLevel（信用联赛等级）
         *
         * @param merchantId
         * @param creditId
         * @param userId
         * @param creditSportId 信用赛种
         * @param playClassify
         * @param betStage      投注阶段，pre-早盘，live-滚球
         * @return
         */
        static String getUserSinglePlayBetKey(Long merchantId, String creditId, Long userId, Integer creditSportId, Integer playClassify, String betStage) {
            return String.format(LIMIT_PREFIX + "singlePlayBet:user:%s:%s:%s:%s:%s:%s", merchantId, creditId, userId, creditSportId, playClassify, betStage);
        }
    }

    /**
     * 已用额度，缓存数据单位分
     */
    interface Used {

        String USED_PREFIX = PREFIX + "used:";

        /**
         * 串关每日已用额度<br/>
         * 数据结构：Hash<br/>
         * key = rcs:credit:used:series:{currentDateExpect}:{merchantId}:{creditId}<br/>
         * field = seriesType
         *
         * @param currentDateExpect
         * @param merchantId
         * @param creditId
         * @return
         */
        static String getSeriesKey(String currentDateExpect, Long merchantId, String creditId) {
            return String.format(USED_PREFIX + "series:%s:%s:%s", currentDateExpect, merchantId, creditId);
        }

        /**
         * 赛事累计已用额度<br/>
         * 数据结构：String<br/>
         * key = rcs:credit:used:singleMatch:{dateExpect}:{merchantId}:{creditId}:{matchId}<br/>
         *
         * @param dateExpect
         * @param merchantId
         * @param creditId
         * @param matchId
         * @return
         */
        static String getSingleMatchKey(String dateExpect, Long merchantId, String creditId, Long matchId) {
            return String.format(USED_PREFIX + "singleMatch:%s:%s:%s:%s", dateExpect, merchantId, creditId, matchId);
        }

        static String getSingleMatchOldKey(String dateExpect, Long merchantId, String creditId, Integer standardSportId, Long matchId) {
            return String.format(USED_PREFIX + "singleMatch:%s:%s:%s:%s:%s", dateExpect, merchantId, creditId, standardSportId, matchId);
        }

        /**
         * 玩法累计已用额度<br/>
         * 数据结构：Hash<br/>
         * key = rcs:credit:used:singlePlay:{dateExpect}:{merchantId}:{creditId}:{matchId}:{playClassify}<br/>
         * field = betStage
         *
         * @param dateExpect
         * @param merchantId
         * @param creditId
         * @param matchId
         * @param playClassify
         * @return
         */
        static String getSinglePlayKey(String dateExpect, Long merchantId, String creditId, Long matchId, Integer playClassify) {
            return String.format(USED_PREFIX + "singlePlay:%s:%s:%s:%s:%s", dateExpect, merchantId, creditId, matchId, playClassify);
        }

        static String getSinglePlayOldKey(String dateExpect, Long merchantId, String creditId, Integer standardSportId, Integer standardTournamentLevel, Integer playClassify, Long matchId) {
            return String.format(USED_PREFIX + "singlePlay:%s:%s:%s:%s:%s:%s:%s", dateExpect, merchantId, creditId, standardSportId, standardTournamentLevel, playClassify, matchId);
        }

        /**
         * 用户串关每日已用额度<br/>
         * 数据结构：Hash<br/>
         * key = rcs:credit:used:series:user:{currentDateExpect}:{userId}<br/>
         * field = seriesType
         *
         * @param currentDateExpect
         * @param userId
         * @return
         */
        static String getUserSeriesKey(String currentDateExpect, Long userId) {
            return String.format(USED_PREFIX + "series:user:%s:%s", currentDateExpect, userId);
        }

        /**
         * 用户玩法累计已用额度<br/>
         * 数据结构：Hash<br/>
         * key = rcs:credit:used:singlePlay:user:{dateExpect}:{userId}:{sportId}:{standardTournamentLevel}:{playClassify}:{matchId}<br/>
         * field = betStage
         *
         * @param dateExpect
         * @param userId
         * @param matchId
         * @param playClassify
         * @return
         */
        static String getUserSinglePlayKey(String dateExpect, Long userId, Long matchId, Integer playClassify) {
            return String.format(USED_PREFIX + "singlePlay:user:%s:%s:%s:%s", dateExpect, userId, matchId, playClassify);
        }

        static String getUserSinglePlayOldKey(String dateExpect, Long userId, Integer sportId, Integer standardTournamentLevel, Integer playClassify, Long matchId) {
            return String.format(USED_PREFIX + "singlePlay:user:%s:%s:%s:%s:%s:%s", dateExpect, userId, sportId, standardTournamentLevel, playClassify, matchId);
        }
    }

    /**
     * 商户限额预警标志
     *
     * @param dateExpect
     * @param businessId
     * @return
     */
    static String getMerchantLimitWarnSignKey(String dateExpect, String businessId) {
        return String.format("rcs:merchantLimitWarnSign:%s:%s", dateExpect, businessId);
    }
}
