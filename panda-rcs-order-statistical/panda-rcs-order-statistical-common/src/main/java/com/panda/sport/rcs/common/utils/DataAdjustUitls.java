package com.panda.sport.rcs.common.utils;

import java.math.BigDecimal;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.common.utils
 * @description :  TODO
 * @date: 2020-06-23 13:25
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class DataAdjustUitls {
    
    public static long oddWideClassify(BigDecimal oddsValue)  {
        return  011;
    }

    public static long betAmountWideClassify(BigDecimal betAmount)  {
        /*** 单位:分 ***/
        long betAmountLong = betAmount.longValue();
        if(betAmountLong < 100000) {
            return 0;
        }
        if(betAmountLong < 200000) {
            return 1000;
        }
        if(betAmountLong < 500000) {
            return 2000;
        }
        return  0;
    }
}
