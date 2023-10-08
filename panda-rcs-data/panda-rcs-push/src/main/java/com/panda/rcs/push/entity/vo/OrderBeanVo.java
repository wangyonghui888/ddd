package com.panda.rcs.push.entity.vo;

import com.panda.rcs.push.entity.vo.OrderItemVo;
import com.panda.sport.data.rcs.dto.OrderBean;

import java.util.List;

/**
 * @Describtion
 * @Auther jstyDC
 * @Date 2022-02-2022/2/22 17:05
 */
public class OrderBeanVo extends OrderBean {

    private List<com.panda.rcs.push.entity.vo.OrderItemVo> itemsVo;

    /**
     * 是否为预约投注订单 0为false ,1 为true;
     */
    private Integer isPendingOrder;

    public List<com.panda.rcs.push.entity.vo.OrderItemVo> getItemsVo() {
        return itemsVo;
    }

    public void setItemsVo(List<OrderItemVo> itemsVo) {
        this.itemsVo = itemsVo;
    }

    public Integer getIsPendingOrder() {
        return isPendingOrder;
    }

    public void setIsPendingOrder(Integer pendingOrder) {
        this.isPendingOrder = pendingOrder;
    }
}
