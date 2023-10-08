package com.panda.sport.rcs.task.wrapper.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.pojo.TOrder;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-03
 */
public interface ITOrderService extends IService<TOrder> {
    /**
     * @Description   通过订单明细扩展表
     * @Param [date]
     * @Author  toney
     * @Date  15:56 2020/1/31
     * @return java.util.List<com.panda.sport.rcs.pojo.TOrder>
     **/
    List<TOrder> queryByOrderDetailExt(Long date);

    /**
     * @Description   通过订单明细扩展表
     * @Param [orderStatus,ids]
     * @Date  15:56 2020/1/31
     * @return java.util.List<com.panda.sport.rcs.pojo.TOrder>
     **/
    List<TOrder> queryByOrderDetailExtAndIds(List<String> ids);

    /**
     * 批量修改订单状态
     * @param orders
     * @return
     */
    int updateOrderStatusBatch(List<TOrder> orders);

    int checkOrderStatus(String orderNo);

    public List<TOrder> getLiveWaitedOrders(String orderNo);
}
