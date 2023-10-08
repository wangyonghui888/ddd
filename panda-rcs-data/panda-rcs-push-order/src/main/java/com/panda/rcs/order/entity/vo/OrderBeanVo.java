package com.panda.rcs.order.entity.vo;

import com.panda.sport.data.rcs.dto.OrderBean;

import java.util.List;

/**
 * @Describtion
 * @Auther jstyDC
 * @Date 2022-02-2022/2/22 17:05
 */
public class OrderBeanVo extends OrderBean {

    private List<OrderItemVo> itemsVo;

    /**
     * 是否为预约投注订单 0为false ,1 为true;
     */
    private Integer isPendingOrder;

    private List<String> secondaryLabelIdsList;

    public List<OrderItemVo> getItemsVo() {
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

    public List<String> getSecondaryLabelIdsList() { return secondaryLabelIdsList; }

    public void setSecondaryLabelIdsList(List<String> secondaryLabelIdsList) { this.secondaryLabelIdsList = secondaryLabelIdsList; }
}
