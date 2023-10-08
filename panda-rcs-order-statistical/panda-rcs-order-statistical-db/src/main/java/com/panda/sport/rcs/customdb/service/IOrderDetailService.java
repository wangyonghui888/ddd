package com.panda.sport.rcs.customdb.service;

import com.panda.sport.rcs.db.entity.OrderOptionOddChange;

import java.util.List;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.service
 * @description :  TODO
 * @date: 2020-07-18 11:56
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface IOrderDetailService {

    List<OrderOptionOddChange> queryPreMatchOrder(long timeBegin, long timeEnd, int betAmount);

    List<OrderOptionOddChange> queryLiveMatchOrder(long timeBegin, long timeEnd,int orderOddsValue, int betAmount);
}
