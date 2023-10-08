package com.panda.rcs.order.reject.constants;

/**
 * 常量类
 */
public class RedisKey {

    public static final String MATCH_EVENT_KOALA_REDIS_KEY = "rcs:order:accept:koala:event:%s";

    public static final String REDIS_MATCH_MARKET_ODDS_NEW = "rcs:redis:playId:%s:match:%s:odds:new";


    public static final String RCS_BUS_MTS_ORDER_STATUS = "queue_mts_order";

    public static final String REJECT_BET_ORDER = "rcs_reject_bet_order";
    public static final String REJECT_BET_ORDER_GROUP = "rcs_reject_bet_order_group";
    public static final String BET_ORDER_PLACE_NUM_CHANGE = "bet_order_place_num_change";

    public static final String KEY = "key";
    public static final String VALUE = "value";

    public static final String SPECIAL_USER_CONFIG = "rcs:special:user:order:delay:config:%s";

    public static final String LABEL_USER_CONFIG = "rcs:label:order:delay:config:%s";
    public static final String MATCH_EVENT_REDIS_KEY = "rcs:event:config:categorySetId:%s";
    public static final String MATCH_PRE_SETTLE_EVENT_REDIS_KEY = "rcs:event:config:advance:categorySetId:%s";
    public static final String MATCH_EVENT_WAIT_REDIS_KEY = "rcs:event:wait:match:%s:config:categorySetId:%s";
    public static final String MATCH_PRE_SETTLE_EVENT_WAIT_TIME = "rcs:pre:settle:event:wait:match:%s:config:categorySetId:%s";

    //2576 VAR收单开关
    public static final String RCS_SYS_TOURNAMENT_VAR_SWITCH = "rcs:var:system:tournament:switch";
    public static final String RCS_VAR_SWITCH_MATCH = "rcs:var:switch:matchId:%s";
    public static final String RCS_ORDER_VAR_ACCEPT_STATUS = "rcs:var:order:accept:matchId:%s";
    public static final String REDIS_VAR_ORDER_LIST_MATCH_INDEX = "rcs:var:order:list:matchId:%s:index:%s";
    public static final String REDIS_VAR_ORDER_LIST_COUNT = "rcs:var:order:list:matchId:%s";



    //使用上一次事件编码 赛事:玩法集id:事件源编码
    public static final String MATCH_LAST_TIME_EVENT_CODE = "rcs:match:last:time:event:code:%s:match:%s";
    //rcs:match:event:play:set:赛种:玩法
    public static final String MATCH_PLAY_SET_KEY = "rcs:match:event:play:set:%s:%s";
    public static final String REDIS_EVENT_INFO = "rcs:order:accept:dataSourceCode:%s:koala:event:matchId:%s";
    public static final String REDIS_EVENT_DATA_SOURCE_CODE = "rcs:match:%s:event:data:source:code:%s";
    public static final String REDIS_MATCH_PERIOD = "rcs:match:period:matchId:%s";
    public static final String REDIS_MATCH_INFO = "rcs:redis:standard:match:%s";

    public static final String DATA_LIVE_MATCH_SETTLE_DATASOURCE = "rcs:match:data:settle:dataSource:matchId:%s:categorySetId:%s";
    public static final String RCS_ORDER_REJECT_CACHE_UPDATE = "rcs_order_reject_cache_update";
    public static final long CACHE_TIME_OUT = 4 * 60 * 60 * 1000L;
    public static final int BASKET_BALL_DEFAULT_WAIT_TIME = 5;
    public static final long ODDS_CACHE_TIME_OUT = 10  * 60 * 1000L;
    /**两分钟缓存*/
    public static final long ORDINARY_TIME_OUT = 2 * 60 * 1000L;

    public static final String REDIS_EVENT = "rcs:order:accept:dataSourceCode:%s:koala:event:matchId:%s";

    public static final String MATCH_PLAY_SECOND_REDIS_KEY = "rcs:match:play:second:redis:key:%s:%s";
    public static final String REDIS_MATCH_MARKET_SECOND_CONFIG = "rcs:redis:match:market:second:config:%s:%s:%s";
    public static final String REDIS_MATCH_MARKET_SUB_SECOND_CONFIG = "rcs:redis:match:market:sub:second:config:%s:%s:%s:%s";
    public static final String RCS_SPECIAL_EVENT_INFO = "rcs:matchId:%s:spec:event:info:%s";
    public static final String RCS_SPECIAL_EVENT_ALL_INFO = "rcs:matchId:%s:spec:event:info";
    /**
     * 玩法集对应的code
     */
    public static final String RCS_MATCH_PLAY_SET_CODE = "rcs:match:event:play:set:code:%s";

    /**
     * 赛事的球队id缓存
     */
    public static final String RCS_MATCH_TEAM_ID = "rcs:matchId:%s:teamIds";

    /**
     * 赛事的球队id缓存
     */
    public static final String RCS_MATCH_POSITION_TEAM_ID = "rcs:matchId:%s:position:%s:teamId";
    /**
     * 赛事的联赛id缓存
     */
    public static final String RCS_MATCH_TOURNAMENT_ID = "rcs:matchId:%s:tournamentId";
    /**
     * 进球点预警页面设置
     */
    public static final String RCS_GOAL_WARN_SET = "rcs:goal:warn:set:tournamentId:%s:matchId:%s:teamId:%s";
    /**
     * 进球点预警注单符合要求的用户缓存设置
     */
    public static final String RCS_GOAL_WARN_SET_USER = "rcs:goal:warn:set:user:tournamentId:%s:matchId:%s:teamId:%s";
    /**
     * 2536进球点预警发送给业务的topic key
     */
    public static final String RCS_GOAL_WARNING_TIME_PERIOD = "RCS_GOAL_WARNING_TIME_PERIOD";
    public static final String RCS_GOAL_WARNING_TIME_PERIOD_GROUP = "RCS_GOAL_WARNING_TIME_PERIOD_GROUP";
}
