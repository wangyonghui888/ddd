package com.panda.sport.rcs.constants;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @Project Name: panda-rcs-trade-group
 * @Package Name: com.panda.sport.rcs.constants
 * @Description : 风控常量类
 * @Author : Paca
 * @Date : 2020-07-25 14:20
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsConstant {

    /**
     * 赛事ID
     */
    String MATCH_ID = "match_id_";

    /**
     * 玩法ID
     */
    String PLAY_ID = "play_id_";

    /**
     * 盘口ID
     */
    String MARKET_ID = "market_id_";

    String HOME_POSITION = "home";

    String AWAY_POSITION = "away";

    String HOME = "T1";

    String AWAY = "T2";

    BigDecimal BASE = new BigDecimal(100);

    /**
     * 单关
     * */
    Integer Single_LEVEL=1;
    /**
     * 2串1
     * */
    Integer Two_LEVEL=2001;
    /**
     * 3串1
     * */
    Integer THREE_LEVEL=3001;
    /**
     * 4串1
     * */
    Integer FOUR_LEVEL=4001;
    /**
     * 5串1
     * */
    Integer FIVE_LEVEL=5001;
    Integer THREE_LEVEL_4=3004;
    Integer FOUR_LEVEL_11=40011;
    Integer FIVE_LEVEL_26=50026;
    Integer SIX_LEVEL=6001;
    Integer SENVEN_LEVEL=7001;
    Integer ENGIT_LEVEL=8001;
    Integer NINE_LEVEL=9001;
    Integer TEN_LEVEL=10001;

    //普通注单
    Integer ORDER_DJ_ONE = 1;
    //普通串关
    Integer ORDER_DJ_TWO = 2;
    ////局内串关
    Integer ORDER_DJ_THREE = 3;
    //复合玩法
    Integer ORDER_DJ_FOUR = 4;
    /**
     * 默认盘口位置数量
     */
    int DEFAULT_MARKET_PLACE_AMOUNT = 12;

    String RCS_CREDIT_SETTLE_RESULT = "RCS_CREDIT_SETTLE_RESULT";

    String OSMC_SETTLE_RESULT = "OSMC_SETTLE_RESULT";

    String RCS_SETTLE = "RCS_SETTLE";

    String PRE_SETTLE_HANDLE_STATUS = "PRE_SETTLE_HANDLE_STATUS";

    String PRE_SETTLE_HANDLE_STATUS_GROUP = "pre_settle_handle_status_group";


    String RCS_FORECAST_PRE_SETTLE_ORDER = "rcs_forecast_pre_settle_order";

    String PRE_SETTLE_HANDLE_STATUS_CROUP = "risk_pre_settle_operate";

    String OSMC_SETTLE_RESULT_SDK = "OSMC_SETTLE_RESULT_SDK";

    String RISK_SEND_REFUSAL = "risk_send_refusal";

    String TAG_MQ_ORDER_PREDICT_CALC = "TAG_MQ_ORDER_PREDICT_CALC";

    String RISK_ORDER_TRIGGER_CHANGE = "RISK_ORDER_TRIGGER_CHANGE";

    String RISK_ORDER_STATUS_OPERATE = "risk_order_status_operate";


    String TAG_MQ_ORDER_INFO_WS = "TAG_MQ_ORDER_INFO_WS";

    String RCS_PREDICT_SETTLE_ORDER = "rcs_predict_settle_order";

    String RCS_MATCH_DIMENSION_STATISTICS="mq_data_rcs_match_dimension_statistics";

    /**
     * 特殊盘口值0.5
     */
    BigDecimal SPECIAL_MARKET_VALUE = new BigDecimal("0.5");

    BigDecimal HUNDRED = new BigDecimal("100");

    /**
     * 限额扩大倍数，单关
     */
    BigDecimal LIMIT_MULTIPLE_SINGLE = new BigDecimal("1.05");

    /**
     * 限额扩大倍数，串关
     */
    BigDecimal LIMIT_MULTIPLE = new BigDecimal("1.1");

    /**
     * 滚球赛事状态
     */
    List<Integer> LIVE_MATCH_STATUS = Lists.newArrayList(1, 2, 10);

    /**
     * 基准分玩法
     */
    List<Long> BENCHMARK_SCORE = Lists.newArrayList(4L, 19L, 143L);

    /**
     * 大小盘
     */
    List<Long> TOTAL = Lists.newArrayList(2L, 10L, 11L, 18L, 26L, 34L, 38L, 45L, 51L, 57L, 63L, 87L, 88L, 97L, 98L, 114L, 115L, 116L, 122L, 123L, 124L, 127L, 134L, 145L, 146L, 156L, 157L, 164L, 169L, 173L, 177L, 182L, 186L, 198L, 199L, 202L, 220L, 221L, 233L, 244L, 245L, 246L, 250L, 251L, 252L, 254L, 257L, 258L, 262L, 263L, 264L);

    List<Long> OVER_X_UNDER = Lists.newArrayList(217L);

    List<Long> TOTAL_AND_OTHER = Lists.newArrayList(13L, 102L, 171L, 216L, 217L);

    /**
     * 让球/分赛果，三项盘，有盘口值，比 让球盘 多一个平局投注项
     */
    List<Long> HANDICAP_1X2 = Lists.newArrayList(3L, 69L, 71L);

    /**
     * 让球/分盘，都是两项盘，有盘口值
     */
    List<Long> HANDICAP = Lists.newArrayList(4L, 19L, 33L, 113L, 121L, 128L, 130L, 143L, 232L,
            19L, 39L, 46L, 52L, 58L, 64L, 143L);

    /**
     * 主要玩法-常规进球
     */
    List<Long> MAIN_GOAL = Lists.newArrayList(1L, 2L, 4L, 17L, 18L, 19L);

    /**
     * 主要玩法-常规角球
     */
    List<Long> MAIN_CORNER = Lists.newArrayList(111L, 113L, 114L, 119L, 121L, 122L);

    /**
     * 主要玩法-加时进球
     */
    List<Long> MAIN_OVERTIME_GOAL = Lists.newArrayList(126L, 127L, 128L, 129L, 130L);

    /**
     * 主要玩法-篮球-让分，全场/上半场/下半场/第一节/第二节/第三节/第四节
     */
    List<Long> MAIN_BASKETBALL_HANDICAP = Lists.newArrayList(39L, 19L, 143L, 46L, 52L, 58L, 64L);

    /**
     * 主要玩法-篮球-大小，全场/上半场/下半场/第一节/第二节/第三节/第四节
     */
    List<Long> MAIN_BASKETBALL_TOTAL = Lists.newArrayList(38L, 18L, 26L, 45L, 51L, 57L, 63L,209L);

    List<Long> MAIN_BASKETBALL_AO= Lists.newArrayList(200L,210L,211L,212L,216L,219L,49L,55L,61L,67L,147L,213L,209L,201L,214L,215L);

    List<Long> BASKETBALL_TOTAL = Lists.newArrayList(38L, 18L, 26L, 45L, 51L, 57L, 63L, 87L, 88L, 97L, 98L, 145L, 146L, 198L, 199L);

    /**
     * 主要玩法-篮球-单双，全场/上半场/下半场/第一节/第二节/第三节/第四节
     */
    List<Long> MAIN_BASKETBALL_ODD_EVEN = Lists.newArrayList(40L, 42L, 75L, 47L, 53L, 59L, 65L);

    /**
     * 净胜分
     */
    List<Long> WINNING_MARGIN = Lists.newArrayList(49L, 55L, 61L, 67L, 141L, 200L, 209L, 211L, 212L, 219L, 238L);

    /**
     * 进球球员
     */
    List<Long> GOALSCORER = Lists.newArrayList(35L, 36L, 148L, 150L, 151L, 152L, 363L, 364L, 365L, 366L);
    public static List<Integer> BASKETBALL_MY_PLAYS = Lists.newArrayList(39,38,40,19,18,42,46,45,47,52,51,53,58,57,59,64,63,65,143,26,75,198,199,87,88,97,98,145,146,209);
    /**
     *AO数据源支持篮球标准玩法 第2期(16个玩法) 200,209,210,211,212,216,219,49,55,61,67,147,213
     */
    public static List<Integer> BASKETBALL_EU_PLAYS = Lists.newArrayList(5,37,41,43,48,54,60,66,142);
    public static List<Integer> FOOTBALL_MY_PLAYS = Lists.newArrayList(4,2,12,15,78,92,81,82,79,80,77,91,10,11,19,18,42,43,24,87,97,90,100,83,86,93,96,84,94,143,26,75,76,88,98,89,99,142,135,136,144,138,139,140,114,122,113,121,118,229,115,116,123,124,127,128,130,131,234,335,334,134,240,307,309,312,313,306,308,314,315,316,317,324,325,327,328,330,331,332,269,270,132,371,372,373,374,375,376,377,378,381,382);
    public static List<Integer> FOOTBALL_EU_PLAYS = Lists.newArrayList(1,5,6,3,27,17,70,69,29,85,95,16,25,72,71,149,111,117,119,112,126,129,310,311,326,329,228,43,142,333,344,345,346,347,348,349,350,351,352,353,354,355,356,357,359,360,367,368,369,370,379,380,383);
    public static List<Integer> FOOTBALL_MOST_PLAYS = Lists.newArrayList(35,7,141,8,9,14,68,223,20,104,21,22,23,103,108,74,73,13,101,102,105,106,107,137,36,150,151,152,226,227,236,238,239,241,318,319,320,321,322,323,361,367,368,369,370,379,380,383);
    public static List<Integer> FOOTBALL_X_MOST_PLAYS = Lists.newArrayList(31,148,222,363,364,365,366,362);
    public static List<Integer> FOOTBALL_X_EU_PLAYS = Lists.newArrayList(28,30,32,225,120,125,230,231,224,235,237,357);
    public static List<Integer> FOOTBALL_X_MY_PLAYS = Lists.newArrayList(336,109,110,34,33,233,232,133);
    public static List<Integer> TINNIS_EU_PLAYS = Lists.newArrayList();
    public static List<Integer> TINNIS_MY_PLAYS = Lists.newArrayList(153,154,155,162,163,164,165,168,202);
    public static List<Integer> TINNIS_MOST_PLAYS = Lists.newArrayList();
    //拆2项的玩法 BASKETBALL_X_EU_PLAYS = Lists.newArrayList(147,201,214,215);
    public static List<Integer> BASKETBALL_X_EU_PLAYS = Lists.newArrayList(147,214,215,201);

    public static List<Integer> BASKETBALL_TWO_PLAYS = Lists.newArrayList(201,214,215);

    public static List<Integer> BASKETBALL_X_MY_PLAYS = Lists.newArrayList(220,221,271,272,145,146);
    public static List<Integer> BASKETBALL_AO= Lists.newArrayList(200,210,211,212,216,219,49,55,61,67,147,213,209);

    public static List<Integer> BASKETBALL_X_MARKET_PLAYS = Lists.newArrayList(145,146);

    /***************************************/
    public static List<Integer> SNOOKER_EU_PLAYS = Lists.newArrayList();
    public static List<Integer> SNOOKER_EU_MOST_PLAYS = Lists.newArrayList();
    public static List<Integer> SNOOKER_MY_PLAYS = Lists.newArrayList(153,180,181,182,183,184,185,186,187,189);
    /***************************************/
    public static List<Integer> PING_PONG_EU_PLAYS = Lists.newArrayList();
    public static List<Integer> PING_PONG_EU_MOST_PLAYS = Lists.newArrayList(204,174);
    public static List<Integer> PING_PONG_MY_PLAYS = Lists.newArrayList(153,172,173,175,176,177,178,179,203);
    /***************************************/
    public static List<Integer> VOLLEYBALL_EU_PLAYS = Lists.newArrayList(159);
    public static List<Integer> VOLLEYBALL_EU_MOST_PLAYS = Lists.newArrayList(204);
    public static List<Integer> VOLLEYBALL_MY_PLAYS = Lists.newArrayList(153,162,172,173,253,254,255,256);
    /***************************************/
    public static List<Integer> BADMINTON_EU_PLAYS = Lists.newArrayList();
    public static List<Integer> BADMINTON_EU_MOST_PLAYS = Lists.newArrayList();
    public static List<Integer> BADMINTON_MY_PLAYS = Lists.newArrayList(153,172,173,175,176,177,179,203);
    /***************************************/
    public static List<Integer> BASEBALL_EU_PLAYS = Lists.newArrayList(277);
    public static List<Integer> BASEBALL_EU_MOST_PLAYS = Lists.newArrayList();
    public static List<Integer> BASEBALL_MY_PLAYS = Lists.newArrayList(242,243,244,245,246,249,250,251,252,274,278,284,285,286,290,291,292);
    /***************************************/
    public static List<Integer> ICE_HOCKEY_EU_PLAYS = Lists.newArrayList(1,6,28,149,3,259,261);
    public static List<Integer> ICE_HOCKEY_EU_MOST_PLAYS = Lists.newArrayList(14,8,9,204,260,265,267,296,297,298);
    public static List<Integer> ICE_HOCKEY_MY_PLAYS = Lists.newArrayList(4,2,5,257,258,15,12,262,263,264,266,268,41,294,295);
    /***************************************/
    public static List<Integer> AMERICAN_FOOTBALL_EU_PLAYS = Lists.newArrayList(17);
    public static List<Integer> AMERICAN_FOOTBALL_EU_MOST_PLAYS = Lists.newArrayList();
    public static List<Integer> AMERICAN_FOOTBALL_MY_PLAYS = Lists.newArrayList(18,19,37,38,39,87,97,198,199);
    /***************************************/
    public static List<Integer> HANDBALL_EU_PLAYS = Lists.newArrayList(1,6,17,70,259);
    public static List<Integer> HANDBALL_EU_MOST_PLAYS = Lists.newArrayList();
    public static List<Integer> HANDBALL_MY_PLAYS = Lists.newArrayList(4,2,19,18,5,15,43,42,127,128);
    /***************************************/
    public static List<Integer> BEACH_VOLLEYBALL_EU_PLAYS = Lists.newArrayList(153,159,162);
    public static List<Integer> BEACH_VOLLEYBALL_EU_MOST_PLAYS = Lists.newArrayList(204);
    public static List<Integer> BEACH_VOLLEYBALL_MY_PLAYS = Lists.newArrayList(172,173,253,254,255,256);
    /*****************联合式橄榄球**********************/
    public static List<Integer> RUGBY_UNION_EU_PLAYS = Lists.newArrayList(1,3,6,16,17,69,70,126);
    public static List<Integer> RUGBY_UNION_EU_MOST_PLAYS = Lists.newArrayList(141,218);
    public static List<Integer> RUGBY_UNION_MY_PLAYS = Lists.newArrayList(135,136,2,4,5,10,11,15,18,19,42,43,87,97);
    /********************曲棍球*******************/
    public static List<Integer> HOCKEY_EU_PLAYS = Lists.newArrayList(1,17,6,28,3,104,70,69,48,44,54,50,60,56,66,62);
    public static List<Integer> HOCKEY_EU_MOST_PLAYS = Lists.newArrayList(223,213);
    public static List<Integer> HOCKEY_MY_PLAYS = Lists.newArrayList(4,2,19,18,5,10,11,12,15,81,79,43,87,97,42,24,46,45,47,52,51,53,58,57,59,64,63,65,145,146);
    /******************水球*********************/
    public static List<Integer> WATER_POLO_EU_PLAYS = Lists.newArrayList(1,259,17,44,50,56,62);
    public static List<Integer> WATER_POLO_EU_MOST_PLAYS = Lists.newArrayList();
    public static List<Integer> WATER_POLO_MY_PLAYS = Lists.newArrayList(4,2,19,46,45,47,52,51,53,58,57,59,64,63,65);
    /******************拳击*********************/
    public static List<Integer> BOXING_EU_PLAYS = Lists.newArrayList(153,337,338,339);
    public static List<Integer> BOXING_EU_MOST_PLAYS = Lists.newArrayList();
    public static List<Integer> BOXING_MY_PLAYS = Lists.newArrayList(2);
    // 统计跳分次数
    public static final String RCS_MARKET_TIMES = "rcs:risk:change:match:times:%s";
    // 统计跳分次数
    public static final String RCS_COUNT_TIMES = "rcs:risk:change:count:times:%s";
    //货量百分比Key
    String RCS_ORDER_VOLUME_PERCENTAGE="rcs:order:volumePercentage:%s";

    // 需要统计跳分次数的玩法
    public static final List<Integer> RCS_COUNT_TIMES_PLAY = Lists.newArrayList(1,2,4,17,18,19);

    List<Integer> ALL_SPORT_MOST_PLAY = Lists.newArrayList(35,7,141,8,9,14,68,223,20,104,21,22,23,103,108,74,73,13,101,102,105,106,107,137,36,150,151,152,226,227,236,238,239,241,318,319,320,321,322,323,31,148,222,204,190,197,260,265,267,296,297,298,213,363,364,365,366,361,362,367,368,369,370,379,380,383);
    List<Integer> ALL_SPORT_MY_PLAY = Lists.newArrayList(2,4,5,12,15,18,19,38,39,40,41,42,43,45,46,51,52,57,58,63,64,87,97,127,128,172,173,176,177,178,179,180,181,182,183,185,186,187,188,189,191,192,193,194,195,196,198,199,203,243,244,245,246,247,248,249,250,251,252,253,254,255,256,257,258,262,263,264,266,268,274,276,278,279,280,281,282,284,285,286,287,288,289,290,291,292,294,295,305);
    public static Long BET_EXIST_TIME = 35 * 24 *60 * 60L;

    // 不能操盘的赛种
    public static List<Integer> OTHER_BALL = Lists.newArrayList(6,11,12,13,14,15,16);
    // 足篮外的可操盘赛种
    public static List<Integer> OTHER_CAN_TRADE_SPORT = Lists.newArrayList(3,4,5,7,8,9,10);

    // 足球多项盘玩法
    public static List<Integer> FOOTBALL_MOST_ODDS_TYPE_PLAYS = Arrays.asList(35,7,141,8,9,14,68,223,20,104,21,22,23,103,108,74,73,13,101,102,105,106,107,137,36,150,151,152,226,227,236,238,239,241,318,319,320,321,322,323,340,344,345,346,347,348,349,350,351,353,360,361,362);

    // 足球带参数玩法
    public static List<Integer> FOOTBALL_X_NO_INSERT_PLAYS = Lists.newArrayList(31,148,222,363,364,365,366,362);

    // 足球带参数玩法
    public static List<Integer> FOOTBALL_X_INSERT_PLAYS = Lists.newArrayList(336,28,30,109,110,34,32,33,233,225,120,125,230,231,232,224,235,133,237,357,362);

    // 蓝球带参数玩法
    public static List<Integer> BASKETBALL_X_PLAYS = Lists.newArrayList(220,221,271,272,145,146,147,215,201,214);

    //网球等
    public static List<Integer> TETC = Arrays.asList(3,4,5,7,8,9,10);

    // 子玩法配置
    public static String REDIS_MATCH_MARKET_SUB_CONFIG = "rcs:redis:match:market:sub:config:%s:%s:%s";
    // 玩法配置
    public static String REDIS_MATCH_MARKET_CONFIG = "rcs:redis:match:market:config:%s:%s";
    // 盘口差配置
    //这个key没有使用的地方
    //public static String REDIS_MATCH_MARKET_HEAD_CONFIG = "rcs:redis:match:market:head:%s:%s:%s";
    // 赛事赔率
    //public static String REDIS_MATCH_MARKET_ODDS = "rcs:redis:match:odds:%s";
    // 赛事赔率
    public static String REDIS_MATCH_MARKET_ODDS = "rcs:playId:%s:redis:match:%s:odds";

    public static String REDIS_MATCH_MARKET_ODDS_NEW = "rcs:redis:playId:%s:match:%s:odds:new";

    // 水差
    public static String REDIS_MATCH_MARKET_WATER = "rcs:redis:match:market:water:%s";

    /**
     * 投注货量动态风控开关
     */
    String RCS_RISK_BET_VOLUME_SWITCH = "rcs:risk:bet:volume:switch:";

    /**
     * 藏单比例key
     */
    String RCS_DYNAMIC_HIDE_ORDER_RATE = "rcs:dynamic:hide:order:rate:%s:%s";

    /**
     * 商户货量设置
     * */
   String RCS_TRADE_BUSINESS_BET_PERCENT="risk:trade:businessBetPercent:";

    String RCS_TRADE_USER_SPECIAL_BET_LIMIT_CONFIG = "risk:trade:rcs_user_special_bet_limit_config:";


    String RCS_TRADE_USER_SPORT_BET_LIMIT_CONFIG = "risk:trade:rcs_user_sport_type_bet_limit_config:";

    /**
     * 用户投注特征标签 "进球点投注”,"UFO-进球点投注","KY-进球点投注"
     */
    public static final List<Integer> RCS_REJECT_GOAL_WARN_USER_TAG_LEVELS = Lists.newArrayList(144,242,246);
}
