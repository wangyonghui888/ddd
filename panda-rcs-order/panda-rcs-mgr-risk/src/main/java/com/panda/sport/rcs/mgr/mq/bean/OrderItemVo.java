package com.panda.sport.rcs.mgr.mq.bean;

import com.panda.sport.data.rcs.dto.OrderItem;

/**
 * @Describtion
 * @Auther jstyDC
 * @Date 2022-02-2022/2/21 21:18
 */
public class OrderItemVo extends OrderItem {

    private SettleOrder settleOrder;

    public SettleOrder getSettleOrder() {
        return settleOrder;
    }

    public void setSettleOrder(SettleOrder settleOrder) {
        this.settleOrder = settleOrder;
    }
}
