package com.panda.rcs.order.reject.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class StandardMarketOddsMessageDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 标准投注项ID
     */
    private Long id;
    /**
     * 投注项赔率. 单位: 0.00001
     */
    private Integer paOddsValue;
}
