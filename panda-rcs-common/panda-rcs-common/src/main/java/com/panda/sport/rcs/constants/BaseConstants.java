package com.panda.sport.rcs.constants;

public interface BaseConstants {
    /**
     * 处理小数扩大/缩小的倍数值
     */
    int MULTIPLE_VALUE = 100000;
    /**
     * 处理注单金额扩大/缩小的倍数值
     */
    int MULTIPLE_BET_AMOUNT_VALUE = 100;

    /**
     * 缓存失效时间
     */
    long CACHE_EXPIRE_SECONDS = 300L;
    /**
     * 赔率计算保留小数位
     */
    Integer ODDS_CALCULATION_SCALE = 6;

    
    static String DATA_SOURCE_CODE = "PA";
    /**
     * @Description  欧洲盘
     * @Param 
     * @Author  Sean
     * @Date  20:16 2019/12/10
     * @return 
     **/
    String EU_MARKET_TYPE = "EU";
    /**
     * @Description  马来盘
     * @Param
     * @Author  Sean
     * @Date  20:16 2019/12/10
     * @return
     **/
    String MY_MARKET_TYPE = "MY";
    /**
     * @Description  0 否 1 是 自动封盘
     * @Param
     * @Author  Sean
     * @Date  20:16 2019/12/10
     * @return
     **/
    String AUTO_BET_OPEN = "0";
    /**
     * @Description  0 否 1 是 自动封盘
     * @Param
     * @Author  Sean
     * @Date  20:16 2019/12/10
     * @return
     **/
    String AUTO_BET_STOP = "1";
    /*2 关盘*/
    String AUTO_BET_CLOSE = "2";

    Integer PAGE_NUM = 10;
    /**
     * @Description //最大赔率值
     * @Param
     * @Author kimi
     * @Date 2019/12/18
     * @return
     **/
    Integer MAX_ODDS_VALUE = 100;
    /**
     * @Description //最小赔率值
     * @Param
     * @Author kimi
     * @Date 2019/12/18
     * @return
     **/
    Double MIN_ODDS_VALUE = 1.01;
    /**
     * 注单统计下注量第一层界限
     */
    Long AMOUNT_ONE_THOUSAND = 1000L;
    /**
     * 注单统计下注量第一层界限
     */
    Long AMOUNT_FIVE_THOUSAND = 5000L;
    /**
     * 注单统计下注量第一层界限
     */
    Long AMOUNT_TWO_THOUSAND = 2000L;
    /**
     * 注单统计下注量第一层界限
     */
    Long AMOUNT_TEN_THOUSAND = 10000L;
    /**
     * 根据玩法阶段查找所有玩法
     */
    String BALL_PHASE = "ballPhase";
    String ODD_TYPE_1 = "1";
    String ODD_TYPE_X = "X";
    String ODD_TYPE_2 = "2";
    String ODD_TYPE_OVER = "Over";
    String ODD_TYPE_UNDER = "Under";
    String ODD_TYPE_ODD = "Odd";
    String ODD_TYPE_EVEN = "Even";
    String ODD_TYPE_YES = "Yes";
    String ODD_TYPE_NO = "No";
    String ODD_TYPE_HOME = "home";
    String ODD_TYPE_TIE = "tie";
    String ODD_TYPE_AWAY = "away";

    public static String SAVE_ORDER_TAGS = "SAVE_ORDER";
}
