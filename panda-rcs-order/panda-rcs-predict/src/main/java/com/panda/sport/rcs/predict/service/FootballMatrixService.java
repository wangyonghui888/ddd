package com.panda.sport.rcs.predict.service;

import com.panda.sport.data.rcs.dto.OrderItem;

/**
 * <p>
 * 足球矩阵
 * </p>
 *
 * @author Kir
 */
public interface FootballMatrixService {

    /**
     * 足球矩阵入库
     */
    void footballMatrixData(OrderItem orderItem, Integer type, Long tenantId);

    public String getMatrixTypeAndPlayType(OrderItem item);
}
