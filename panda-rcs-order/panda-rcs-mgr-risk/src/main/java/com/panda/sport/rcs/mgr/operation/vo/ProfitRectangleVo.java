package com.panda.sport.rcs.mgr.operation.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.operation.vo
 * @Description :  期望值矩阵
 * @Date: 2019-12-10 21:53
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class ProfitRectangleVo {
    /**
     * 盘口值
     */
    private Integer marketValue;
    /**
     * 期望值
     */
    private BigDecimal profixValue;

    /**
     * 下注总金额
     */
    private BigDecimal betAmount;
    /**
     * 派奖金额
     */
    private BigDecimal paiAmount;
}
