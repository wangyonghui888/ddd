package com.panda.sport.rcs.predict.service;

import com.panda.sport.data.rcs.dto.OrderBean;

/**
 * <p>
 * Forecast Serveice 篮球Forecast
 * </p>
 *
 * @author lithan
 * @since 2021年1月14日11:42:01
 */
public interface BasketBallForecastService {


    /**
     * forecast计算
     *
     * @param orderBean
     * @param type
     * @param nx
     */
    void forecastData(OrderBean orderBean, Integer type, boolean nx);


}
