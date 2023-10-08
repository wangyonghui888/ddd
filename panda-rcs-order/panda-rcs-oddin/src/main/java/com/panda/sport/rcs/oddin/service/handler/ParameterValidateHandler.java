package com.panda.sport.rcs.oddin.service.handler;

import com.panda.sport.data.rcs.dto.oddin.CancelOrderDto;
import com.panda.sport.data.rcs.dto.oddin.TicketDto;
import com.panda.sport.data.rcs.dto.oddin.TicketResultDto;

/**
 * 参数校验类
 */
public interface ParameterValidateHandler {
    /**
     * 校验注单入参
     *
     * @param dto
     */
    void validateSaveArguments(TicketDto dto);


    /**
     * 校验限额入参
     *
     * @param ticketDto
     */
    void validateMaxBetMoneyBySelectArguments(TicketDto ticketDto);

    /**
     * 校验撤单入参
     *
     * @param dto
     */
    void validateCancelArguments(CancelOrderDto dto);

    /**
     * 校验单个拉单入参
     *
     * @param ticketRequestDto
     */
    void validatePullSingleByOrderNoArguments(TicketResultDto ticketRequestDto);

    /**
     * 校验全量拉单入参
     *
     * @param ticketRequestDto
     */
    void validatePullSingleByTimeArguments(TicketResultDto ticketRequestDto);
}
