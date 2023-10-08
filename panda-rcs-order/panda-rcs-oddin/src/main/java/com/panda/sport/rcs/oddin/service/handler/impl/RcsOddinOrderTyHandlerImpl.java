package com.panda.sport.rcs.oddin.service.handler.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrderTy;
import com.panda.sport.rcs.oddin.mapper.RcsOddinOrderTyMapper;
import com.panda.sport.rcs.oddin.service.handler.RcsOddinOrderTyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Z9-conway
 */
@Slf4j
@Service
public class RcsOddinOrderTyHandlerImpl implements RcsOddinOrderTyHandler {

    @Resource
    private RcsOddinOrderTyMapper rcsOddinOrderTyMapper;

    @Override
    public void save(RcsOddinOrderTy ext) {
        rcsOddinOrderTyMapper.insert(ext);
    }

    @Override
    public void update(RcsOddinOrderTy order) {
        UpdateWrapper<RcsOddinOrderTy> wrapper = new UpdateWrapper<>();
        wrapper.eq("order_no", order.getOrderNo());
        rcsOddinOrderTyMapper.update(order, wrapper);
    }

    @Override
    public RcsOddinOrderTy selectOne(String orderNo) {
        LambdaQueryWrapper<RcsOddinOrderTy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsOddinOrderTy::getOrderNo, orderNo);
        return rcsOddinOrderTyMapper.selectOne(wrapper);
    }

}
