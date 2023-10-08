package com.panda.sport.rcs.customdb.service.impl;

import com.panda.sport.rcs.customdb.mapper.OrderDetailMapper;
import com.panda.sport.rcs.customdb.service.IOrderDetailService;
import com.panda.sport.rcs.db.entity.OrderOptionOddChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.service.impl
 * @description :  TODO
 * @date: 2020-07-18 12:00
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service("orderDetailServiceImpl")
public class OrderDetailServiceImpl implements IOrderDetailService {

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Override
    public List<OrderOptionOddChange> queryPreMatchOrder(long timeBegin, long timeEnd, int betAmount)  {
        return orderDetailMapper.queryPreMatchOrder(timeBegin, timeEnd, betAmount);
    }


    @Override
    public  List<OrderOptionOddChange> queryLiveMatchOrder(long timeBegin, long timeEnd,int orderOddsValue, int betAmount) {
        return orderDetailMapper.queryLiveMatchOrder(timeBegin, timeEnd,orderOddsValue,betAmount );
    }
}
