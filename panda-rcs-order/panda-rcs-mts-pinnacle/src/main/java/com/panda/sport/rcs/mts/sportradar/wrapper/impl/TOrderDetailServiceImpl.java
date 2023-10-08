package com.panda.sport.rcs.mts.sportradar.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.mts.sportradar.wrapper.ITOrderDetailService;
import com.panda.sport.rcs.pojo.TOrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TOrderDetailServiceImpl extends ServiceImpl<TOrderDetailMapper, TOrderDetail> implements ITOrderDetailService {

    @Autowired
    private TOrderDetailMapper orderDetailMapper;

    /**
     * 更新MTS订单状态
     *
     * @param orderNo
     * @param mtsOrderStatus
     * @Author carver
     */
    @Override
    @Async
    public void modifyMtsOrder(String orderNo, int mtsOrderStatus) {
        orderDetailMapper.updateMtsOrder(orderNo, mtsOrderStatus);
    }
}
