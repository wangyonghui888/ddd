package com.panda.sport.rcs.predict.service;

import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;

/**
 * <p>
 * 赛事预测Serveice
 * 计算 货量  / Forecast 等
 * </p>
 *
 * @author lithan
 * @since 2020-07-18 19:12:24
 */
public interface PredictService {

    /**
     * 赛事预测计算
     *
     * @param orderBean
     * @param type      1标识下单 增加计算  -1表示取消订单
     */
    public void calculate(OrderBean orderBean, Integer type);

    /**
     * 串关计算
     * @param orderBean
     * @param type
     */
    public void calculateSeries(OrderBean orderBean, Integer type);


    /**
     * 赛事预测计算
     *
     * @param orderBean
     * @param type      1标识下单 增加计算  -1表示取消订单
     * @param seriesType      1单关 2串关
     */
    public void calculate(OrderBean orderBean, Integer type, Integer seriesType );

    /**
     * Forecast计算
     *
     * @param orderItem
     * @param type      1标识下单 增加计算  -1表示取消订单
     */
    public void calculateForecast(OrderItem orderItem, Integer type, boolean nx);

    /**
     * 投注项级别实货货量、期望值以及投注笔数，
     *
     * @param orderItem
     * @param type      1标识下单 增加计算  -1表示取消订单
     */
    public void calculateOddsData(OrderItem orderItem, Integer type, Integer seriesType, Boolean nx);

    /**
     * 赛事级别 计算
     *
     * @param orderItem
     * @param type      1标识下单 增加计算  -1表示取消订单
     */
    public void calculateMatchData(OrderItem orderItem, Integer type);

    /**
     * 货量计算
     *
     * @param orderItem
     * @param type      1标识下单 增加计算  -1表示取消订单
     */
    public void calculateBetStatis(OrderItem orderItem, Integer type, boolean nx);

    /**
     * 盘口位置货量 /笔数
     *
     * @param orderItem
     * @param type      1标识下单 增加计算  -1表示取消订单
     */
    public void calculatePlaceNumData(OrderItem orderItem, Integer type, Integer seriesType, boolean nx);

    /**
     * 篮球矩阵计算
     *
     * @param orderBean
     * @param type      1标识下单 增加计算  -1表示取消订单
     */
    public void calculateBasketballMatrix(OrderBean orderBean, Integer type, boolean nx);

    /**
     * 足球矩阵计算
     * kir
     *
     * @param orderBean
     * @param type      1标识下单 增加计算  -1表示取消订单
     */
    public void calculateFootballMatrix(OrderBean orderBean, Integer type);

    /**
     * 足球玩法级别forecast
     *
     * @param orderBean
     * @param type      1标识下单 增加计算  -1表示取消订单
     */
    public void calculatePlayData(OrderBean orderBean, Integer type, boolean nx);


}
