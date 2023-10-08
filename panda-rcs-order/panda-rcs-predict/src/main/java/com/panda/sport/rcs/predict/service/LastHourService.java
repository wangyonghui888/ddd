package com.panda.sport.rcs.predict.service;

import com.panda.sport.data.rcs.dto.OrderItem;

/**
 * 最近一小时
 */
public interface LastHourService {


    /**
     * 记录每个赛事 最近一个小时内 的第一笔注单时间
     *
     * @param orderItem
     * @param type      1标识下单 增加计算  -1表示取消订单
     */
    void matchLastOrderTime(OrderItem orderItem, Integer type);
}
