package com.panda.sport.rcs.mgr.constant;

import com.github.benmanes.caffeine.cache.Cache;
import com.panda.sport.rcs.cache.RcsCacheUtils;
import com.panda.sport.rcs.pojo.RcsStandardOutrightMatchInfo;
import com.panda.sport.rcs.pojo.StandardMatchInfo;

import java.util.concurrent.TimeUnit;

/**
 * @author :  sean
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.sport.rcs.mgr.constant
 * @Description :  TODO
 * @Date: 2021-01-22 19:02
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class RcsCacheContant {
    /**
     * 多语言缓存
     */
    public static String MATCH = "RCS_MATCH";
    public static Cache<Long, StandardMatchInfo> MATCH_CACHE = RcsCacheUtils.newCache(MATCH,
            100, 1000, 60, 60, 60);
    /**
     * 多语言缓存
     */
    public static String CHAMPION_MATCH = "RCS_MATCH_CHAMPION";
    public static Cache<Long, RcsStandardOutrightMatchInfo> RCS_MATCH_CHAMPION = RcsCacheUtils.newCache(CHAMPION_MATCH,
            100, 1000, 60, 60, 60);

    public static String USER = "rcs:user:%s";
    public static String SPECIAL_USER_CONFIG = "rcs:special:user:order:delay:config:%s";
    public static String LAVBEL_USER_CONFIG = "rcs:label:order:delay:config:%s";
    public static String REDIS_EVENT = "rcs:order:accept:seans:event:%s:%s";
    public static String MATCH_EVENT_REDIS_KEY = "rcs:match:event:play:categorySetId:%s:collection:%s";
    //使用上一次事件编码 赛事:玩法集id:事件源编码
    public static String MATCH_LAST_TIME_EVENT_CODE = "rcs:match:last:time:event:match:%s:setId:%s:code:%s";

    public static Long MATCH_EVENT_WAIT_TIME = 2 * 60 * 60L;
//    public static String MATCH_PLAY_SET_KEY = "rcs:match:event:play:set:%s:%s";
    public static final String FREE_ORDER = "rcs:order:second:config:%s:%s";
    public static final Long FREE_ORDER_TIME = 5000L;

    public static Integer FOOT_BALL_DEFAULT_WAIT_TIME = 3;
    public static Integer BASKET_BALL_DEFAULT_WAIT_TIME = 5;
    public static String DEFAULT_PLAY_SET_ID = "-1";
    public static Integer FREE_ACCEPT = 12;
    public static String TIMEOUT = "timeout";

    public static Long PAUSE_ORDER_5_MINS = 300L;
    public static String RCS_PAUSE_ORDER = "rcs:pause:order:%s";

    public static String RCS_DATA_KEYCACHE_MATCHTEMPINFO = "rcs:data:keyCache:matchTempInfo:%s";
    public static String RCS_TASK_MATCH_INFO_CACHE = "rcs:task:match:info:";
    public static long EXPRIY_TIME_2_HOURS = TimeUnit.HOURS.toSeconds(2);
    public static String REDIS_MATCH_DETAIL_EXT_INFO_KEY = "rcs:match:event:detail:ext:info:list:%s";
}
