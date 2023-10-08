package com.panda.sport.rcs.oddin.grpc;

import com.panda.sport.data.rcs.dto.oddin.CancelOrderDto;
import com.panda.sport.data.rcs.dto.oddin.TicketDto;
import com.panda.sport.rcs.oddin.entity.ots.TicketCancel;
import com.panda.sport.rcs.oddin.entity.ots.TicketMaxStake;

/**
 * grpc同步服务接口
 */
public interface FutureGrpcService {
    /**
     * 撤单
     * @param cancelOrderDto
     * @return
     */
    TicketCancel.TicketCancelResponse cancelOrder(CancelOrderDto cancelOrderDto);


    /**
     * 获取oddIn最大限额接口
     * @param ticketDto
     * @return
     */
    TicketMaxStake.TicketMaxStakeResponse queryMaxBetMoneyBySelect(TicketDto ticketDto);

}
