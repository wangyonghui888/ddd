package com.panda.sport.rcs.third.entity.redCat;

import lombok.Data;

import java.math.BigDecimal;

/**
 * redcat 取消注单返回结果
 * @author vere
 * @date 2023-05-24
 * @version 1.0.0
 */
@Data
public class RedCatBetCancelResponseData {
    /**
     * 投注id
     */
    private Long betId;
    /**
     * 投注金额
     */
    private BigDecimal amount;
    /**
     * 接收赔率类型
     */
    private Integer acceptOdds;
    /**
     * 取消状态
     */
    private String betStatus;
    /**
     * 描述
     */
    private String message;
    /**
     * 币种
     */
    private String currency;
    /**
     * 投注项id
     */
    private Long selectionID;
    /**
     * 红猫的投注id
     */
    private Long redcatBetId;

}
