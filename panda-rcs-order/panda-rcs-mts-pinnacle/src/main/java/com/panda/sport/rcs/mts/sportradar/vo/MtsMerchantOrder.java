package com.panda.sport.rcs.mts.sportradar.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MtsMerchantOrder implements Serializable {
    /**
     * 订单号
     */
    String orderNo;

    /**
     * 订单时间
     */
    String orderTime;

    /**
     * 延迟时间
     */
    String delayTime;
}
