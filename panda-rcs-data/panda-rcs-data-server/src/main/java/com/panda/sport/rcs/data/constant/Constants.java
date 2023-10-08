package com.panda.sport.rcs.data.constant;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.constant
 * @Description :  TODO
 * @Date: 2020-09-26 15:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class Constants {
    /**
     * 商户单日限额基础值 1亿
     */
    public static  final BigDecimal BUSINESS_SINGLE_DAY_LIMIT=new BigDecimal(100000000);

    /**
     * 商户单日串关限额基础值 1亿
     */
    public static  final BigDecimal BUSINESS_SINGLE_DAY_SERIES_LIMIT=new BigDecimal(100000000);

    /**
     * 用户串关单场限额基础值 10万
     */
    public static  final BigDecimal USER_SINGLE_STRAY_LIMIT=new BigDecimal(100000);

    /**
     * 最小比例
     */
    public static final BigDecimal  MIN_PROPORTION=new BigDecimal("0.0001");
    /**
     * 最大比例
     */
    public static final BigDecimal  MAX_PROPORTION=new BigDecimal("10");
    /**
     * 前端传过来的数据除以100
     */
    public static final BigDecimal BASE=new BigDecimal(100);
    /**
     * 限额基数
     */
    public static final Long QUOTA_BASE=1000000L;
    /**
     * 单日单关赔付比例
     */
    public static  final BigDecimal DAY_COMPENSATION_PROPORTION=new BigDecimal(0.2);

    /**
     * 单日串关赔付比例
     */
    public static  final BigDecimal DAY_SERIES_COMPENSATION_PROPORTION=new BigDecimal(0.1);
    /**
     * 货量百分比
     */
    public static  final BigDecimal BUSINESS_BET_PROPORTION=new BigDecimal(0);
    /**
     * 单注比例
     */
    public static  final BigDecimal SINGLE_BET_LIMIT_RATIO=new BigDecimal(0.2);


    //映射赛事阶段对应比分阶段
    public static final Map<String, Integer> footballSetMap = new HashMap();
    public static final Map<String, Integer> basketballSetMap = new HashMap();
    public static final Map<String, Integer> basketballPeriodMap = new HashMap();
    static {
        footballSetMap.put("6", 1);
        footballSetMap.put("7", 2);

        basketballSetMap.put("13", 1);
        basketballSetMap.put("14", 2);
        basketballSetMap.put("15", 3);
        basketballSetMap.put("16", 4);

        basketballPeriodMap.put("13", 1);
        basketballPeriodMap.put("14", 1);
        basketballPeriodMap.put("15", 2);
        basketballPeriodMap.put("16", 2);
        basketballPeriodMap.put("1", 1);
        basketballPeriodMap.put("2", 2);
    }

    public static final String EVENT_CLOSING_CATEGORYSET = "EVENT_CLOSING_CATEGORYSET_DATASERVER";

    // 篮球单双玩法增加A+操盘模式
    public static final List<Long> BASKETBALL_SINGLE_DOUBLE_PLAY = Arrays.asList(40L, 42L, 47L, 53L, 59L, 65L, 75L, 15L);
}
