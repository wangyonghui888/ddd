package com.panda.sport.rcs.mgr.repository;

import com.panda.sport.rcs.pojo.TOrderDetailExtDO;

import java.util.List;

public interface TOrderDetailExtRepository {

    /**
     * 查询ext订单状态
     * @param betNo
     * @return
     */
    Integer queryOrderStatus(String betNo);

    void updateOrderDetailExtStatus(String orderNo, String orderStatus);

    void saveOrUpdateTOrderDetailExt(List<TOrderDetailExtDO> exts);

    void updateOrderDetailExt(List<TOrderDetailExtDO> exts);
}
