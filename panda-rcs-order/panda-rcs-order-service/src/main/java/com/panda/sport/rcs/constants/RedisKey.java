package com.panda.sport.rcs.constants;

import java.util.concurrent.TimeUnit;

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

    long EXPRIY_TIME_7_DAYS = TimeUnit.DAYS.toSeconds(7);

    long EXPRIY_TIME_2_HOURS = TimeUnit.HOURS.toSeconds(2);

    long EXPRIY_TIME_5_HOURS = TimeUnit.HOURS.toSeconds(5);

    long EXPRIY_TIME_1_MINS = TimeUnit.MINUTES.toSeconds(1);

    int SECONDS_OF_7_DAYS = Long.valueOf(TimeUnit.DAYS.toSeconds(7)).intValue();

    /**
     * 玩法模板缓存
     */
    String RCS_CATEGORY_TEMPLATE = "rcs:category:template:";

    String RCS_CATEGORY_CONS = "rcs:category:cons:";

    String RCS_TASK_MATCH_LIVE = "rcs:task:matchLive:";

    String RCS_TASK_MATCH_SET_EFFECTIVE = "rcs:task:matchSet:effective:";

    String CACHE_CATEGORY_CON = "CACHE_CATEGORY_CON_";

    String CACHE_MAIN_CATEGORY_CON = "CACHE_MAIN_CATEGORY_CON_";

    String CACHE_CATEGORY = "rcs:cache:category:%s:%s";

    String RCS_TASK_MATCH_CATEGORY_CACHE = "rcs:task:matchCategory:";


    String RCS_TASK_GOAL_SCORE = "rcs:task:goal:score:";

    String RCS_TASK_RED_SCORE = "rcs:task:red:score:";

    String RCS_TASK_CORNER_SCORE = "rcs:task:corner:score:";

    String RCS_TASK_YELLOW_SCORE = "rcs:task:yellow:score:";

    String RCS_TASK_KICK_SCORE = "rcs:task:kick:score:";

    String RCS_TASK_MATCH_INFO_CACHE = "rcs:task:match:info:";

    String RCS_TASK_LANGUAGE_CACHE = "rcs:task:language:cache:";

    String RCS_TASK_RCS_LANGUAGE_CACHE = "rcs:task:rcslanguage:cache:";

    String RCS_TASK_RCS_LANGUAGE_NEW_CACHE = "rcs:task:rcslanguagenew:cache:";

    String RCS_TASK_TEAMINFO_CACHE = "rcs:task:teams:info:cache:";

    String RCS_TASK_MATCH_ALL_SCORE = "rcs:task:match:all:score:%s:%s";

    String RCS_TASK_MATCH_PERIOD = "rcs:task:matchPeriod:cache:";

    String RCS_TASK_MATCH_FIFTEEN_CLOSE = "rcs:task:fifteen:close:";

    String RCS_CATEGORYSET_IDS = "rcs:categorySet:categoryIds:";

    String MATCH_LIVE_RISK_MANAGERCODE = "rcs:match:live:risk:manageCode:%s";

    /**
     * 赛事开关 0关 1开 (模板设置)
     */
    String ODDSSCOPE_MATCH_SWITCH = "rcs:risk:order:oddsScope:match.%s.match_type.%s";
    /**
     * 玩法级别赔率范围 (模板设置)
     */
    String ODDSSCOPE_MATCH_PALY = "rcs:risk:order:oddsScope:match.%s.play_id.%s.match_type.%s";


    String RCS_CASHOUT_MATCH_CATEGORY = "rcs:cashout:match:category:%s:%s";

    //1601需求redis
    String MATCH_EVENT_KOALA_REDIS_KEY = "rcs:order:accept:koala:event:%s";

    //1666需求
    String ODDS_SCOPE_KEY = "rcs:risk:order:oddsScope:match.%s.play_id.%s.match_type.%s";
    public final static String REDIS_MATCH_MARKET_ODDS_NEW = "rcs:redis:playId:%s:match:%s:odds:new";
    String REDIS_MATCH_INFO = "rcs:redis:standard:match:%s";

    String REDIS_MATCH_DETAIL_EXT_INFO_KEY = "rcs:match:event:detail:ext:info:list:%s";

  //  String REDIS_MATCH_ORDER_EXT_INFO_KEY = "rcs:match:event:order:ext:info:list";
    String LOCK_KEY = "rcs:auto:job:order:ext:%s";
    //1682需求mts接距配置缓存
    String REDIS_MTS_CONTACT_CONFIG_KEY = "rcs:redis:mts:contact:config:matchId:%s:matchType:%s";
    static String getChuZhangFrequencyKey(Long matchId) {
        return String.format("rcs:task:chuZhangFrequency:%s", matchId);
    }

    String REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY = "rcs:dynamic_missed_order_rcs_configuration:merchants_id";
    String REDIS_RCS_SWITCH_CONFIG_KEY = "rcs:rcs_switch:switchCode:%s";

     String LOCK_PROCESSED_KEY="rcs:order:detai:ext:order:%s";
    static String getChuZhangFrequencyHashKey(Integer playId, String subPlayId, Integer placeNum, Integer matchType, String oddsType) {
        String type = matchType == 0 ? "live" : "pre";
        return String.format("%s:%s:%s:%s:%s", playId, subPlayId, placeNum, type, oddsType);
    }

    String MERCHANT_LIMIT_KEY = "rcs:limit:merchants:%s";

    //藏单投注货量金额区间配置
    String REDIS_MATCH_HIDE_RANGE_LIST_KEY = "rcs:risk:order:hide:range:list";

    String REDIS_HIDE_RANGE_CONFIG  ="rcs:risk:rcs_merchants_hide_range_config:";

    String TRADING_TYPE_KEY = "rcs:trading:type:%s:%s:%s";
    static String getTradingTypeStatusKey(String matchId, String playId,String matchType) {
        return String.format(TRADING_TYPE_KEY, matchId, playId,matchType);
    }
    /**
     * 本地缓存key
     * 商户id + 赛种id
     */
    String CACHE_KEY = "bus_id:%s:sport_id:%s";
    static String getCacheKey(String busId, Integer sportId) {
        return String.format(CACHE_KEY, busId, sportId);
    }
}
