package com.panda.sport.rcs.task.wrapper.order.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.TOrderDetailExtMapper;
import com.panda.sport.rcs.mapper.TOrderMapper;
import com.panda.sport.rcs.pojo.TOrder;
import com.panda.sport.rcs.task.wrapper.order.ITOrderService;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-03
 */
@Service
public class TOrderServiceImpl extends ServiceImpl<TOrderMapper, TOrder> implements ITOrderService {
    @Autowired
    private TOrderMapper orderMapper;

    @Autowired
    private TOrderDetailExtMapper orderDetailExtMapper;
    /**
     * @Description   通过订单明细扩展表
     * @Param [date]
     * @Author  toney
     * @Date  15:56 2020/1/31
     * @return java.util.List<com.panda.sport.rcs.pojo.TOrder>
     **/
    @Override
    public List<TOrder> queryByOrderDetailExt(Long date){
        return orderMapper.queryByOrderDetailExt(date);
    }

    /**
     * @Description   通过订单明细扩展表
     * @Param [date]
     * @Author  toney
     * @Date  15:56 2020/1/31
     * @return java.util.List<com.panda.sport.rcs.pojo.TOrder>
     **/
    @Override
    public List<TOrder> queryByOrderDetailExtAndIds(List<String> ids){
        return orderMapper.queryByOrderDetailExtAndIds(ids);
    }

    @Override
    @Transactional
    public int updateOrderStatusBatch(List<TOrder> orders) {
        Integer index= 0;
        for (TOrder order:orders){
            index += orderMapper.updateOrderStatusBatch(order);
        }
        return index;
    }

    @Override
    public int checkOrderStatus(String orderNo) {
        return orderDetailExtMapper.checkOrderStatus(orderNo);
    }

    @Override
    public List<TOrder> getLiveWaitedOrders(String orderNo) {
        return orderMapper.getLiveWaitedOrders(orderNo);
    }
}
