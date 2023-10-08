package com.panda.rcs.order.reject.entity;

import com.panda.sport.data.rcs.dto.OrderBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderBeanVo  extends OrderBean {

    /**
     * 当前事件Code;
     */
    private String currentEventCode;

}
