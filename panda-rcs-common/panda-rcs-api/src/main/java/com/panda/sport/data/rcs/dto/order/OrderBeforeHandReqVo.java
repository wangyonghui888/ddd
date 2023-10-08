package com.panda.sport.data.rcs.dto.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderBeforeHandReqVo implements Serializable {
    /**
     * 订单号
     */
    String orderNo;
}
