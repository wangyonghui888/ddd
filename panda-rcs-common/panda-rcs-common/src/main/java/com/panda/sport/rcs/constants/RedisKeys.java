package com.panda.sport.rcs.constants;

/**
 * 定义redis key的常量类
 *
 * @author :  kane
 * @Project Name :  panda-rcs-common
 * @Package Name :  com.panda.sport.rcs.constants
 * @Description :  TODO
 * @Creation Date:  2019-09-06 13:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class RedisKeys {

    /**
     * 定义redis key
     * 命名规范:RCSCACHE_模块名_功能名
     */
    //过期时间
    public static long EXP_TIME = 24 * 3600;

    public static long EXP_TIME_TWO_HOURS = 1000 * 60 * 60 * 2;

    public static long getExpireTime() {
        return EXP_TIME + (int) (Math.random() * 1000);
    }

    /**************************盘口赔率**************************/
    /**
     * 实时赛事盘口赔率
     */
    public static final String RCSCACHE_MARKETODDS_LIVE_MATCH_ODDS = "rcs:marketodds_live_match_odds:";

    /**
     * 玩法配置
     */
    public static final String RCSCACHE_MARKETODDS_MARKET_CATEGORY = "rcs:marketodds_market_category:";

    /**
     * 玩法投注项模板
     */
    public static final String RCSCACHE_MARKETODDS_ODDSFIELDS_TEMPLET = "rcs:marketodds_oddsfields_templet:";

    /**************************玩法集**************************/
    /**
     * 玩法集
     */
    public static final String RCSCACHE_MARKET_CATEGORY_SET = "rcs:market_category_set:%s";

    /**
     * 玩法集列表&内容定制国际化
     */
    public static final String RCSCACHE_MARKET_CATEGORY_SET_CUSTOM_MADE_LANGUAGE = "rcs:market_category_set_custom_made_language:";

    /**
     * 风控型玩法
     */
    public static final String RCSCACHE_WIND_CONTROL_TYPE_CATEGORY = "rcs:wind_control_type_category:";


    /**************************基础数据**************************/
    /**
     * 国际化语言
     */
    public static final String RCSCACHE_BASEDATA_LANGUAGEINTERNATION = "rcs:basedata_languageinternation:";


    /**************************分布式锁**************************/
    /**
     * 实时赛事锁
     */
    public static final String RCSCACHE_LOCK_MARKETODDS_LIVE_MATCH_ODDS = "rcs:lock_marketodds_live_match_odds:";

    //赔付参数 时间日期
    public static String PAID_DATE_REDIS_CACHE = "rcs:paid:%s:";

    public static String PAID_DATE_ALL_USER_REDIS_CACHE = "rcs:paid:%s:user:%s:*";

    //用户维度
    public static String PAID_DATE_USER_REDIS_CACHE = "rcs:paid:%s:user:%s:%s";

    //赛事维度  日期  赛事级别  赛事id
    public static String PAID_DATE_MATCH_REDIS_CACHE = "rcs:paid:%s:match:%s:%s";

    //商户维度  日期  商户id
    public static String PAID_DATE_BUS_REDIS_CACHE = "rcs:paid:%s:bus:%s";

    //商户停止收单标识  日期  商户id
    public static String PAID_DATE_BUS_STOP_REDIS_CACHE = "rcs:paid:%s:bus:%s_stop";

    //赔付配置
    public static String PAID_CONFIG_REDIS_CACHE = "rcs:paid:config:";

    /**
     * 赛事配置
     */

    public static String RCS_CODE = "rcs:code";

    public static String RCS_CODE_CACHE = "rcs:code:math:%s:%s";

    /**************************socket计数的版本号**************************/


    /**************************************************************************/


    /**
     * 赛事时段分类
     */
    public static String TIMEPERIOD_VALUE_CACHE = "rcs:singleBet:timePeriod:%s:%s";
    /**
     * 玩法阶段
     */
    public static String CATEGORY_PHASE_CACHE = "rcs:category:phase";
    //定时任务配置
    public static String RCS_TASK_CACHE_KEY = "rcs:task:cache:%s";
    //wsHash缓存
    public static final String RCS_WS_CACHE_KEY = "rcs:ws:cache:%s";
    //ws key缓存
    public static final String RCS_WS_KEY_CACHE_KEY = "rcs:ws:keyCache:%s:%s";
    //dataServer缓存
    public static final String RCS_DATA_KEY_CACHE_KEY = "rcs:data:keyCache:%s:%s";


    //货币汇率
    public static String RCS_CURRENCY_RATE = "rcs:task:currencyRate:%s";

    //货币汇率
    public static String RCS_SELL_CONFIG = "rcs:task:sellConfig:%s";


    public static String MARGAIN_CACHE_KEY = "rcs:margain";

    //投注额记录
    //投注额+赔付组合记录
    public static String CALC_AMOUNT_ODDS_CHANGE = "rcs:odds:calc:%s:%s";
    public static String CALC_AMOUNT_ODDS_CHANGE_PLUS = "rcs:odds:calcPlus:%s:%s";

    //操盘界面切换mongo数据
    public static String RCS_MATCH_MONGO_SET = "rcs:match:mongo:set";

    //操盘界面盘口投注项最大额  赛事ID 玩法ID 盘口ID
    public static String RCS_MARKET_MAX_AMOUNT = "rcs:match:max:%s:%s:%s";
    //操盘界面联赛投注项最大值 联赛ID 玩法ID
    public static String RCS_TOURNAMENT_MAX_AMOUNT = "rcs:match:max:%s:%s";
    //操盘界面联赛投注项最大值 联赛ID 玩法ID
    public static String RCS_REPORT_BET = "rcs:report:bet:%s:%s:%s:%s:%s";
    //操盘界面联赛投注项最大值 联赛ID 玩法ID
    public static String RCS_REPORT_MATCH = "rcs:report:match:%s:%s:%s:%s:%s";
    //操盘界面联赛投注项最大值 联赛ID 玩法ID
    public static String RCS_REPORT_SETTLE = "rcs:report:settle:%s:%s:%s:%s:%s";
    //赛事设置
    public static String RCS_MATCH_MARKET_CONFIG = "rcs:match:market:%s:%s:%s:%s";


    /****  实货量统计 *************/
    /**
     * 赛事级别期望详情
     */
    public static String ODDS_ID_PROFIT_VALUE = "Rcs:realVolume:matchId=%s:matchType=%s:profit:odds:profitValue:marketId=%s";

    public static String ODDS_ID_TOTAL_BET_TIMES = "Rcs:realVolume:matchId=%s:matchType=%s:profit:odds:totalBetTimes:marketId=%s";

    public static String ODDS_ID_TOTAL_PAID_AMOUNT = "Rcs:realVolume:matchId=%s:matchType=%s:profit:odds:totalPaidAmount:marketId:%s";


    public static String ODDS_ID_TOTAL_BET_AMOUNT = "Rcs:realVolume:matchId=%s:matchType=%s:profit:odds:totalBetAmount:marketId:";

    public static String MARKET_ID_TOTAL_BET_AMONUT = "Rcs:realVolume:matchId=%s:matchType=%s:profit:odds:totalBetAmount:marketId:";


    public static String AUTO_ORDER_JOB_MATCH_CONFIG="Rcs:realVolume:matchId=%s:matchInfo";



    /************************赛事维度缓存*****************************************/
    /**
     * 期望详情结果
     */
    public static String PROFIT_DETAIL_RESULT = "Rcs:realVolume:matchId=%s:matchType=%s:profitDetail;playId=%s";
    /**
     * 赛事维度
     */
    public static String SUM_MATCH_ORDER_NUMS_REDIS_CACHE = "rcs:realVolume:matchId=%s:matchDimension:";

    /**
     * 真实货量
     */
    public static String REAL_TIME_VOLUME_BY_MATCH_DIMENSION_REDIS_CACHE = "rcs:realVolume:matchId=%s:matchDimension";

    /**
     * 已结算货量统计
     */
    public static String SETTLE_REAL_VOLUME_REDIS_CACHE = "rcs:realVolume:matchId=%s:matchDimension";

    /**
     * 已结算期望值
     */
    public static String SETTLE_PROFIT_REDIS_CACHE = "rcs:realVolume:matchId=%s:matchDimension";


    public static String PROFIT_PLAYOPTION_ID="Rcs:realVolume:matchId=%s:playOptionsId:%s";


    /************************赛事维度缓存*****************************************/


    /***********************滚球盘口设置开始*********************************/

    /***********************滚球盘口设置结束*********************************/


    public static String IS_CLOSE = "rcs:is:close";

    public static String ROLL_INIT_BETAMOUNT = "rcs:match:betamount:%s";

    public static String RCS_OTHER_CATEGORY_SHOW = "rcs:category:show:list";

    public static String RCS_OTHER_CATEGORY_TEMPLET = "rcs:category:templet:list";

    public static String MARKET_ODDS_ADDTION = "rcs:market:%s:%s";

	/**自动接拒单 配置 开始**/
    public static String MATCH_EVENT_CONFIG= "Rcs:EventConfig:MatchId:%s";

    public static String TOURNAMENT_EVENT_CONFIG="Rcs:EventConfig:Tournament:%s";

    public static String MATCH_ACCEPT_CONFIG= "Rcs:AcceptConfig:MatchId:%s";

    public static String TOURNAMENT_ACCEPT_CONFIG="Rcs:AcceptConfig:Tournament:%s";
    /**自动接拒单 配置 结束**/

    /**
     * 赛事状态缓存
     */
    public static String MATCH_STATUS_CACHE = "rcs:match:status:%s";

    /****************************************************************************************************
     * 联赛模板开始
     */
    //联赛模板-联赛
    public static String TOURTEMPLATE_TOUR_ID="TourTemplate:TourId:%s:%s:%s";
    //联赛模板-模板
    public static String TOURTEMPLATE_TOUR_LEVEL="TourTemplate:TourLevel:%s:%s:%s";

    /****************************************************************************************************
     * 联赛模板结束
     */


    public static String PROFIT_MARKET_INFO="Rcs:realVolume:matchId=%s:ProfitMarket:%s:%s";
}
