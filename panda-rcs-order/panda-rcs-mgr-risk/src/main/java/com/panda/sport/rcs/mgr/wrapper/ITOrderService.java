package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.mgr.mq.bean.OrderBeanVo;
import com.panda.sport.data.rcs.dto.PreOrderRequest;
import com.panda.sport.rcs.pojo.RcsPreOrderDetailExt;
import com.panda.sport.rcs.pojo.TOrder;

import java.math.BigDecimal;

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
     * 保持订单到RCS数据库
     *
     * @param orderBean
     */
    void saveOrderAndItem(OrderBean orderBean, TOrder order);

    /**
     * 更新订单到RCS数据库
     *
     * @param orderBeans
     */
    void updateOrderAndItemStatus(OrderBean orderBeans, TOrder order);

    /**
     * 更新订单到RCS数据库
     *
     * @param orderBeans
     */
    void updatePreOrderAndItemStatus(PreOrderRequest orderBeans, RcsPreOrderDetailExt order);

    /**
     * 信用模式回滚额度
     *
     * @param orderNo
     */
    void creditLimitCallback(String orderNo);

    /**
     * 冠军玩法回滚额度
     *
     * @param orderNo
     */
    void championLimitCallback(String orderNo);

    TOrder queryOrderInfo(String orderNo, OrderBean orderBeans);

    TOrder getOrderInfo(String orderNo);

    /**
     * 获取用户货量
     *
     * @param orderBean
     */
    BigDecimal getVolumePercentage(OrderBean orderBean, boolean isSave);

    void sendOrderWs(OrderBean orderBean);
}
