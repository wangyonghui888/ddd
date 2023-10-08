package com.panda.sport.rcs.third.entity.redCat;

import lombok.Data;

import java.math.BigDecimal;

/**
 * redcat 响应结果
 * @author vere
 * @date 2023-05-24
 * @version 1.0.0
 */
@Data
public class RedCatBetPlaceResponseData {
    /**
     * 注单Id
     */
    private Long betId;
    /**
     * 投注金额
     */
    private BigDecimal amount;
    /**
     * 1=Receive better odds automatically, 2 = Automatically accept any
     * odds changes, 3 =Do not automatically accept odds changes
     */
    private Integer acceptOdds;
    /**
     * 币种
     */
    private String currency;
    /**
     * 投注项id
     */
    private Long selectionID;
    /**
     * 投注状态
     */
    private String betStatus;
    /**
     * 描述
     */
    private String message;
    /**
     * 红猫数据方生成的投注id
     */
    private Long redcatBetId;
}
