package com.panda.sport.rcs.console.dto;

import lombok.Data;

@Data
public class OrderDTO {
    /**
     * 赛事ID
     */
    private String matchId;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 订单状态
     */
    private Integer orderStatus;
    /**
     * 是否结算
     */
    private Integer isSettlement;
    /**
     * 验证渠道
     */
    private Integer riskChannel;
    /**
     * 订单类型（单关，串关）
     */
    private Integer seriesType;
    /**
     * 类型：1 ：早盘 ，2： 滚球盘， 3： 冠军盘
     */
    private String matchType;

    /**
     * 用户ID
     */
    private Long uid;

    private String marketId;

    private Integer sportId;

    private String startTime;

    private String endTime;
}
