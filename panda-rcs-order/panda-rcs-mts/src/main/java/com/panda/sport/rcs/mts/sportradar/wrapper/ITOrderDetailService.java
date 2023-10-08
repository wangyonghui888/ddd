package com.panda.sport.rcs.mts.sportradar.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.TOrderDetail;

public interface ITOrderDetailService extends IService<TOrderDetail> {

    /**
     * 更新MTS订单状态
     *
     * @param orderNo
     * @param mtsOrderStatus
     */
    void modifyMtsOrder(String orderNo, int mtsOrderStatus);
}
