package com.panda.sport.sdk.constant;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface BaseConstants {

    /**
     * 处理小数扩大/缩小的倍数值
     */
    int MULTIPLE_VALUE = 100000;

    /**
     * 处理注单金额扩大/缩小的倍数值
     */
    int MULTIPLE_BET_AMOUNT_VALUE = 100;

    BigDecimal MULTIPLE = new BigDecimal(MULTIPLE_VALUE);

    BigDecimal HUNDRED = new BigDecimal(MULTIPLE_BET_AMOUNT_VALUE);

    /**
     * 限额扩大倍数
     */
    BigDecimal LIMIT_MULTIPLE = new BigDecimal("1.1");

    int MONEY_SCALE = 2;

    int CENT_SCALE = 0;

    int PERCENT_SCALE = 4;

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
     * 商户停止接单标志
     */
    String MERCHANT_STOP_ORDER_SIGN = "1";

    public static final String MERCHANT_STRAY_SWITCH_NEW_VAL="1";

    /**
     * 联赛模板单注限额
     */
    public static final String MATCH_LIMIT_AMOUNT_TYPE_ONE="1";
    /**
     * 联赛模板单注累计限额
     */
    public static final String MATCH_LIMIT_AMOUNT_TYPE_TWO="2";
    /**
     * 联赛模板玩法单注限额
     */
    public static final String MATCH_LIMIT_AMOUNT_TYPE_THREE="3";

    /**
     * 滚球赛事 redis过期时间
     */
    public static final int EXPIRE_SCROLL = Long.valueOf(TimeUnit.HOURS.toSeconds(4L)).intValue();
    /**
     * 早盘赛事过期时间
     */
    public static final int EXPIRE_NO_SCROLL = Long.valueOf(TimeUnit.HOURS.toSeconds(120L)).intValue();



}
