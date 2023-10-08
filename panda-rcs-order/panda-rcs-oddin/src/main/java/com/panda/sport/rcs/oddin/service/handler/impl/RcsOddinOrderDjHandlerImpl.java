package com.panda.sport.rcs.oddin.service.handler.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrderDj;
import com.panda.sport.rcs.oddin.mapper.RcsOddinOrderDjMapper;
import com.panda.sport.rcs.oddin.service.handler.RcsOddinOrderDjHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Z9-conway
 */
@Slf4j
@Service
public class RcsOddinOrderDjHandlerImpl implements RcsOddinOrderDjHandler {

    @Resource
    private RcsOddinOrderDjMapper rcsOddinOrderDjMapper;

    @Override
    public void save(RcsOddinOrderDj ext) {
        rcsOddinOrderDjMapper.insert(ext);
    }

    @Override
    public void update(RcsOddinOrderDj order) {
        UpdateWrapper<RcsOddinOrderDj> wrapper = new UpdateWrapper<>();
        wrapper.eq("order_no", order.getOrderNo());
        rcsOddinOrderDjMapper.update(order, wrapper);
    }
}
