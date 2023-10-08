package com.panda.sport.rcs.mgr.operation.utils;

import java.math.BigDecimal;

/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.operation.utils
 * @Description :  BigDecimal操作类
 * @Date: 2019-12-16 9:47
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class BigDecimalUtil {
    /**
     * @Description   除以100
     * @Param [bigDecimal]
     * @Author  toney
     * @Date  9:48 2019/12/16
     * @return java.math.BigDecimal
     **/
    public static BigDecimal BigDecimalDivide100 (BigDecimal bigDecimal){
        if(bigDecimal == null){
            return BigDecimal.ZERO;
        }
        return bigDecimal.divide(new BigDecimal(100));
    }
    /**
     * @Description   long转 BigDecimal除以100
     * @Param [bigDecimal]
     * @Author  toney
     * @Date  9:49 2019/12/16
     * @return java.math.BigDecimal
     **/
    public static BigDecimal LongDivide100 (Long arg){
        if(arg == null){
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(arg).divide(new BigDecimal(100));
    }


    /**
     * @Description   long转 BigDecimal除以100
     * @Param [bigDecimal]
     * @Author  toney
     * @Date  9:49 2019/12/16
     * @return java.math.BigDecimal
     **/
    public static BigDecimal StringDivide100 (String arg){
        if(arg == null){
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(arg).divide(new BigDecimal(100));
        }
        catch (Exception ex){
            return BigDecimal.ZERO;
        }
    }
}
