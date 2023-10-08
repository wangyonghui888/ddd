package com.panda.sport.rcs.predict.service;

import com.panda.sport.data.rcs.dto.PendingOrderDto;

/**
 * <p>
 * 赛事预测Serveice
 * 计算 货量  / Forecast 等
 * </p>
 *
 * @author lithan
 * @since 2020-07-18 19:12:24
 */
public interface PredictPendingService {

    /**
     * 赛事预测计算
     *
     * @param pendingOrderDto
     * @param type            1标识下单 增加计算  -1表示取消订单
     */
    void calculate(PendingOrderDto pendingOrderDto, Integer type);






}
