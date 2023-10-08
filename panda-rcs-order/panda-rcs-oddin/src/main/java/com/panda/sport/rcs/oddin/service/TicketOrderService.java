package com.panda.sport.rcs.oddin.service;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.oddin.CancelOrderDto;
import com.panda.sport.data.rcs.dto.oddin.TicketDto;
import com.panda.sport.data.rcs.dto.oddin.TicketResultDto;
import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrder;


/**
 * @author Z9-conway
 */
public interface TicketOrderService {
    void ticket(TicketDto dto);

    /**
     * 取消订单
     * @param requestParam
     */
    Response cancelOrder(Request<CancelOrderDto> requestParam);

    /**
     * 拉单接口
     * @param ticketRequestDto
     * @return
     */
    Response pullSingle(Request<TicketResultDto> ticketRequestDto);

    /**
     * 注单接口
     * @param requestParam
     * @return
     */
    Response saveOrder(Request<TicketDto> requestParam);

    /**
     * 订单更新接口
     *
     * @param order
     * @param sourceId
     */
    void updateOrder(RcsOddinOrder order, Integer sourceId);

    /**
     * 获取限额接口
     * @param requestParam
     * @return
     */
    Response queryMaxBetMoneyBySelect(Request<TicketDto> requestParam);

    /**
     * 全量拉单接口
     * @param ticketRequestDto
     * @return
     */
    Response pullSingleTime(Request<TicketResultDto> ticketRequestDto);


}
