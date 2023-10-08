package com.panda.sport.rcs.mgr.mq.bean;

import lombok.Data;

/**
 * @Describtion
 * @Auther jstyDC
 * @Date 2022-02-2022/2/21 16:20
 */
@Data
public class SettleOrder {

    String preOrderNo;

    String orderNo;

    Integer orderState;

    Double preSettleOdds;

    Integer waitTime;

    Long reqTime;

    String preSettleAmount;

    String orderAmount;
    /**
     * 0部分 1全额
     */
    Integer settleType;
    /**
     * 提前结算比分
     */
    private String preSettleScore;
    /**
     * 提前结算投注额
     */
    private String preSettleBetAmount;

}
