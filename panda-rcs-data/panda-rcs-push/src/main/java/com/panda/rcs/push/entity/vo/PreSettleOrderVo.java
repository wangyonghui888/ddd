package com.panda.rcs.push.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PreSettleOrderVo implements Serializable {

    /**
     * 提前结算订单编号
     */
    private String preOrderNo;

    /**
     * 母单编号
     */
    private String orderNo;

    /**
     * 订单状态 1:接  2：拒  3：取消  4：取消回滚
     */
    private int orderState;

    /**
     * 提前结算赔率
     */
    private String preSettleOdds;

    /**
     * 提前结算耗时
     */
    private Integer waitTime;

    /**
     * 提前结算时间
     */
    private String reqTime;

    /**
     * 提前结算金额
     */
    private String preSettleAmount;

    /**
     * 订单总金额
     */
    private String orderAmount;

}
