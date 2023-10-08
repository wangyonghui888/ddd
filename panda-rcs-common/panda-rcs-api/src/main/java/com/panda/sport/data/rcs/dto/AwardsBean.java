package com.panda.sport.data.rcs.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.data.rcs.dto
 * @Description :  派奖
 * @Date: 2019-10-07 19:41
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AwardsBean {
    /**
     * 注单编号
     */
    private String betNo;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 派奖金额
     */
    private BigDecimal awardsMoney;
}
