package com.panda.sport.rcs.mgr.mq.bean;

import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;

import java.util.List;

/**
 * @Describtion
 * @Auther jstyDC
 * @Date 2022-02-2022/2/21 21:19
 */
public class OrderBeanVo extends OrderBean {

    private List<OrderItemVo> itemsVo;



    private List<String> secondaryLabelIdsList;

    public List<OrderItemVo> getItemsVo() {
        return itemsVo;
    }

    public void setItemsVo(List<OrderItemVo> itemsVo) {
        this.itemsVo = itemsVo;
    }

    public List<String> getSecondaryLabelIdsList() { return secondaryLabelIdsList; }

    public void setSecondaryLabelIdsList(List<String> secondaryLabelIdsList) { this.secondaryLabelIdsList = secondaryLabelIdsList; }
}
