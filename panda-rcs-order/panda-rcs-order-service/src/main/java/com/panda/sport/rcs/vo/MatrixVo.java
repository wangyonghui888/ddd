package com.panda.sport.rcs.vo;

import lombok.Data;

/**
 * @author :  holly
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.rcs.vo
 * @Description :
 * @Date: 2019-11-05 18:17
 */
@Data
public class MatrixVo {
    //投注累加值
    private Long value;
    //所属等级，显示不同颜色
    private int level;
    //是否出局
    private Boolean isOutcome;
}
