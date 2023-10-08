package com.panda.sport.rcs.oddin.grpc;

import com.panda.sport.data.rcs.dto.oddin.TicketDto;

/**
 * @Author wiker
 * @Date 2023/6/17 18:29
 **/
public interface TicketGrpcService {
    void ticket(TicketDto ticketDto);
}
