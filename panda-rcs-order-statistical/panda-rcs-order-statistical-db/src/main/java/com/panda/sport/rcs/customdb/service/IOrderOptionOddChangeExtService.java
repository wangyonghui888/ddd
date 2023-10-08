package com.panda.sport.rcs.customdb.service;

import com.panda.sport.rcs.db.entity.OrderOptionOddChange;

import java.util.List;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.service
 * @description :
 * @date: 2020-07-19 10:07
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface IOrderOptionOddChangeExtService {

    void saveOrUpdate(List<OrderOptionOddChange> orderOptionOddChanges);
    
}
