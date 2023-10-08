package com.panda.sport.rcs.oddin.service.handler;

import com.panda.sport.data.rcs.dto.oddin.TicketDto;

/**
 * @author Z9-conway
 */
public interface TicketOrderHandler {

    /**
     * TY早盘下注同时发mq进行监听整个oddin注单的响应时间
     * @param dto
     */
    public void sendEarlyCancelMq(TicketDto dto);


    /**
     * 获取商户折扣
     * @param locationId
     * @param sourceId
     * @return
     */
    String getDiscount(Long locationId, Integer sourceId);

    /**
     * 校验注单的最大限额与下注额度是否匹配
     * @param dto
     * @param discount
     * @param reqState
     */
    void verificationMaxState(TicketDto dto, String discount, int reqState);

    /**
     * 下游注单保存记录
     * @param dto
     */
    void saveRcsGtsOrderExt(TicketDto dto);

    /**
     * 删除TY早盘缓存
     * @param orderNo 订单编号
     */
    void removeEarlyOrderBettingStatus(String orderNo);

    /**
     * 根据orderNo从缓存中获取globalId
     */
    String getGlobalIdFromCacheByOrderNo(String orderNo);
}
