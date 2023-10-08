package com.panda.sport.rcs.cache.local;



import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.github.benmanes.caffeine.cache.Cache;
import com.panda.sport.rcs.cache.RcsCacheUtils;

/**
 * @author V
 */
public class WebsocketConstants {

    /**
     * 赛事类别编码集
     */
    public final static List<Integer> matchCodes = Arrays.asList(30001, 30002, 30003, 30004, 30005, 30013, 30014, 30036, 30041, 30044, 30045, 30051, 40005);


    /**
     * 投注项模板缓存
     */
    public static String MARKETOPTIONNAME = "RCS_MARKET_OPTIONNAME";
    public static Cache<String, String> MARKETOPTIONNAME_CACHE = RcsCacheUtils.newCache(MARKETOPTIONNAME,
            500, 40000, 7200, 7200, 7200);

    public static String LANGUAGE = "RCS_LANGUAGE";
    public static Cache<Long, String> LANGUAGE_CACHE = RcsCacheUtils.newCache(LANGUAGE,
            500, 30000, 7200, 7200, 7200);

    public static String TOURNAMENT = "RCS_TOURNAMENT";
    public static Cache<Long, String> TOURNAMENT_CACHE = RcsCacheUtils.newCache(TOURNAMENT,
            500, 30000, 7200, 7200, 7200);

    public static String OUTRIGHTMATCH = "RCS_OUTRIGHTMATCH";
    public static Cache<Long, String> OUTRIGHTMATCH_CACHE = RcsCacheUtils.newCache(OUTRIGHTMATCH,
            500, 30000, 7200, 7200, 7200);

    public static String CHAMPION_OPTION = "RCS_CHAMPION_OPTION";
    public static Cache<Long, String> CHAMPION_OPTION_CACHE = RcsCacheUtils.newCache(CHAMPION_OPTION,
            500, 30000, 7200, 7200, 7200);

    public static String SPECIAL_OPTION = "RCS_SPECIAL_OPTION";
    public static Cache<Long, String> SPECIAL_OPTION_CACHE = RcsCacheUtils.newCache(SPECIAL_OPTION,
            500, 30000, 7200, 7200, 7200);


    public static String SPECIAL_OPTION_PLAYER = "RCS_SPECIAL_OPTION_PLAYER";
    public static Cache<Long, String> SPECIAL_OPTION__PLAYERCACHE = RcsCacheUtils.newCache(SPECIAL_OPTION_PLAYER,
            500, 30000, 7200, 7200, 7200);
    public static String CACHE_CATEGORY = "cacheWSCategory:%s:%s";

    public static String CACHE_MARKET = "rcs:cache:ws:market:";

    public static String CACHE_LANGUAGE_TYPE = "rcs:cache:ws:language";

    public static long EXPRIY_TIME_7_DAYS = TimeUnit.DAYS.toSeconds(7);

    public static long EXPRIY_TIME_3_HOURS = TimeUnit.HOURS.toSeconds(3);

    public static final String TITLE_SHOW_NAME_GOALNR = "{!goalnr}";

    public static final String TITLE_SHOW_NAME_HOME = "Home";

    public static final String TITLE_SHOW_NAME_AWAY = "Away";

    public static final String TITLE_SHOW_NAME_HCPDA = "{hcp}";

}
