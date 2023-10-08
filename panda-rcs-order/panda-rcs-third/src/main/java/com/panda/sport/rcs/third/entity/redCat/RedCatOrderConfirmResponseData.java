package com.panda.sport.rcs.third.entity.redCat;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 红猫订单确认信息返回结果
 * @author vere
 * @date 2023-05-24
 * @version 1.0.0
 */
@Data
public class RedCatOrderConfirmResponseData {
    /**
     * 投注id
     */
    private Long betId;
    /**
     * 投注金额
     */
    private BigDecimal amount;
    /**
     * 币种
     */
    private String currency;
    /**
     * 投注项id
     */
    private Long selectionId;
    /**
     * 变更金额
     */
    private BigDecimal reofferPrice;
    /**
     * 状态
     */
    private String betStatus;
    /**
     * 描述
     */
    private String message;
    /**
     * 红猫投注单号
     */
    private Long redcatBetId;
}
