package com.panda.sport.rcs.predict.service;

import com.panda.sport.data.rcs.dto.OrderItem;

/**
 * <p>
 * Forecast Serveice 不同玩法有不同的
 * </p>
 *
 * @author lithan
 * @since 2020-07-18 19:12:24
 */
public interface ForecastService {

    /**
     * Forecast计算
     *
     * @param orderItem
     * @param type      1标识下单 增加计算  -1表示取消订单
     */
    void forecastData(OrderItem orderItem, Integer type, boolean nx);


}
