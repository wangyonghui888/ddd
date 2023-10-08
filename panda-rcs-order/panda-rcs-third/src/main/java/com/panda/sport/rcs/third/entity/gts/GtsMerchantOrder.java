package com.panda.sport.rcs.third.entity.gts;

import com.panda.sport.rcs.pojo.TOrderDetail;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GtsMerchantOrder implements Serializable {
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

    /************************以下为gts返回延迟的订单 用的字段********************************/
    /**
     * 是否是gts 延时
     */
    Integer isGts;

    /**
     * 投注详情gts扩展对象
     */
    List<GtsExtendBean> gtsExtendBeanList;

    /**
     * 总投注金额
     */
    Long totalMoney;

    /**
     * 串关字符串
     */
    Integer seriesType;


}
