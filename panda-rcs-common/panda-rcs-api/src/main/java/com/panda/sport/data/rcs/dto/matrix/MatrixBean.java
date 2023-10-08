package com.panda.sport.data.rcs.dto.matrix;

import lombok.Data;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.data.rcs.dto.matrix
 * @Description :  矩阵相关信息
 * @Date: 2019-11-06 11:23
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatrixBean {
    /**
     * 值
     */
    private Long value;
    /**
     * 层级
     */
    private int level;
    /**
     * 层级
     */
    private Boolean isOutcome;
}
