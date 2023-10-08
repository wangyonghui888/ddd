package com.panda.sport.rcs.profit.constant;

/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.profit.constant
 * @Description :  常量值
 * @Date: 2019-12-09 18:00
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ProfixConstant {
    /**
     * 让球最小值
     */
    Double ASIANHANDICAP_MIN_MATRIX_VALUE = -12.0;
    /**
     * 让球最大值
     */
    Double ASIANHANDICAP_MAX_MATRIX_VALUE = 12.0;


    /**
     * 大小球矩阵起始值
     */
    Double GoalLine_MIN_MATRIX_VALUE = 0.0;

    /**
     * 大小球矩阵结束值
     */
    Double GoalLine_MAX_MATRIX_VALUE = 24.0;
}
