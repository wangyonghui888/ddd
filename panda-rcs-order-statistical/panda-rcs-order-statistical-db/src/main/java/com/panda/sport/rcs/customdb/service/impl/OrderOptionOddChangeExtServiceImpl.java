package com.panda.sport.rcs.customdb.service.impl;

import com.panda.sport.rcs.customdb.mapper.OrderOptionOddChangeExtMapper;
import com.panda.sport.rcs.customdb.service.IOrderOptionOddChangeExtService;
import com.panda.sport.rcs.db.entity.OrderOptionOddChange;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.service.impl
 * @description :   
 * @date: 2020-07-19 10:07
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service("orderOptionOddChangeExtServiceImpl")
public class OrderOptionOddChangeExtServiceImpl implements IOrderOptionOddChangeExtService {

    @Autowired
    OrderOptionOddChangeExtMapper orderOptionOddChangeExtMapper;

    @Override
    public void saveOrUpdate(List<OrderOptionOddChange> orderOptionOddChanges) {
        orderOptionOddChangeExtMapper.batchSaveOrUpdate(orderOptionOddChanges);
    }
}
