package com.panda.sport.rcs.oddin.grpc.handler;

import com.panda.sport.data.rcs.dto.oddin.TicketDto;
import com.panda.sport.data.rcs.vo.oddin.TicketResultVo;
import com.panda.sport.data.rcs.vo.oddin.TicketStateVo;
import com.panda.sport.data.rcs.vo.oddin.TicketVo;
import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrder;
import com.panda.sport.rcs.oddin.entity.ots.TicketMaxStake;
import com.panda.sport.rcs.oddin.entity.ots.TicketOuterClass;
import com.panda.sport.rcs.oddin.entity.ots.TicketResultOuterClass;

public interface TicketGrpcHandler {
    /**
     * 将接受到oddin注单回调数据转成TickeTvo
     * @param value
     * @return
     */
    TicketVo transferResponseToVo(TicketOuterClass.TicketResponse value);

    /**
     * 将接受到oddin注单回调数据专场RcsOddinOrder
     * @param order
     * @param value
     */
    void transferOrder(RcsOddinOrder order, TicketVo vo);

    /**
     * 将接收到的下游请求数据转换成TicketOuterClass。Ticket
     * @param ticketDto
     * @return
     */
    TicketOuterClass.Ticket getTicket(TicketDto ticketDto);

    /**
     * 缓存体育业务数据，包括赛事ID/盘口OD/用户组/赛事类型
     * @param ticketDto
     */
    void putTyMatch2cache(TicketDto ticketDto);

    /**
     * 拉单响应数据转换成TicketResultVo
     * @param vo
     * @param value
     */
    TicketResultVo ticketResultResponse(TicketResultOuterClass.TicketResultResponse value);

    /**
     * 获取限额响应数据转换成ticketMaxStakeResponse
     * @param ticketMaxStakeResponse
     * @return
     */
    TicketStateVo queryMaxBetMoneyBySelect( TicketMaxStake.TicketMaxStakeResponse ticketMaxStakeResponse);

}
