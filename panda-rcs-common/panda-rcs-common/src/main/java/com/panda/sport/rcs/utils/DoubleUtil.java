package com.panda.sport.rcs.utils;

/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.operation.utils
 * @Description :  Double操作类
 * @Date: 2019-12-13 21:52
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class DoubleUtil {
    /**
     * @Description   默认比较精度
     * @Param
     * @Author  toney
     * @Date  21:53 2019/12/13
     * @return
     **/
    private static final double DEFAULT_DELTA = 0.000001;

    /**
     * @Description   比较2个double值是否相等（默认精度）
     * @Param [v1, v2]
     * @Author  toney
     * @Date  21:53 2019/12/13
     * @return boolean
     **/
    public static boolean considerEqual(double v1, double v2) {
        return considerEqual(v1, v2, DEFAULT_DELTA);
    }

    /**
     * @Description   比较2个double值是否相等（指定精度）
     * @Param [v1, v2, delta]
     * @Author  toney
     * @Date  21:53 2019/12/13
     * @return boolean
     **/
    public static boolean considerEqual(double v1, double v2, double delta) {
        return Double.compare(v1, v2) == 0 || considerZero(v1 - v2, delta);
    }

    /**
     * @Description   判断指定double是否为0（默认精度）
     * @Param [value]
     * @Author  toney
     * @Date  21:53 2019/12/13
     * @return boolean
     **/
    public static boolean considerZero(double value) {
        return considerZero(value, DEFAULT_DELTA);
    }

    /**
     * @Description   判断指定double是否为0（指定精度）
     * @Param [value, delta]
     * @Author  toney
     * @Date  21:54 2019/12/13
     * @return boolean
     **/
    public static boolean considerZero(double value, double delta) {
        return Math.abs(value) <= delta;
    }

}
