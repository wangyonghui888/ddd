package com.panda.rcs.push.entity.vo;

import com.panda.rcs.push.entity.vo.SettleOrder;
import com.panda.sport.data.rcs.dto.OrderItem;

/**
 * @Describtion
 * @Auther jstyDC
 * @Date 2022-02-2022/2/22 17:05
 */
public class OrderItemVo extends OrderItem {

    private Double oddsCount;

    private com.panda.rcs.push.entity.vo.SettleOrder settleOrder;

    public com.panda.rcs.push.entity.vo.SettleOrder getSettleOrder() {
        return settleOrder;
    }

    public void setSettleOrder(SettleOrder settleOrder) {
        this.settleOrder = settleOrder;
    }

    public Double getOddsCount() {
        return oddsCount;
    }

    public void setOddsCount(Double oddsCount) {
        this.oddsCount = oddsCount;
    }
}
