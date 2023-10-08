package com.panda.rcs.pending.order.pojo;

import com.panda.sport.data.rcs.dto.OrderBean;
import lombok.Data;

@Data
public class OrderBeanVo  extends OrderBean {

    /**
     * 是否为预约投注订单 0为false ,1 为true;
     */
    private Integer isPendingOrder;


}
