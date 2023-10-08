package com.panda.sport.rcs.limit.constants;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * @description:常量
 * @author: lithan
 * @date: 2020-10-14 16:10
 **/
public class LimitConstants {
    /**
     * 金额元转分 倍数
     */
    public final static BigDecimal AMOUNT_UNIT = new BigDecimal("100");

    public static final int TWO_HOURS = Long.valueOf(TimeUnit.HOURS.toSeconds(2L)).intValue();
}