package com.panda.sport.data.rcs.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderBeforeHandResVo implements Serializable {
    /**
     * 订单号
     */
    String orderNo;
    
    /**
     * 是否可以提前结算
     */
    Boolean isPreSettle;

    /**
     * 投注项赔率 和  盘口状态
     */
    List<OrderBeforeHandItemVo> list;
}
