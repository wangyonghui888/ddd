package com.panda.sport.rcs.mts.sportradar.vo;

import com.panda.sport.rcs.pojo.TOrderDetail;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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

    /**
     * 订单信息
     */
    List<TOrderDetail> tOrderDetailList;
}
