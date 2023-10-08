package com.panda.sport.rcs.oddin.grpc.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.panda.sport.data.rcs.dto.oddin.CancelOrderDto;
import com.panda.sport.data.rcs.dto.oddin.TicketDto;
import com.panda.sport.rcs.oddin.entity.ots.*;
import com.panda.sport.rcs.oddin.grpc.FutureGrpcService;
import com.panda.sport.rcs.oddin.grpc.handler.TicketGrpcHandler;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ExecutionException;

import static com.panda.sport.rcs.oddin.util.ParamUtils.montageOrderNo;

@Slf4j
@Service
public class FutureGrpcServiceImpl implements FutureGrpcService {
    @Resource
    private TicketGrpcHandler ticketGrpcHandler;
    @Resource
    ManagedChannel grpcChannel;
    /**
     * 取消订单
     *
     * @param cancelOrderDto
     */
    @Override
    public TicketCancel.TicketCancelResponse cancelOrder(CancelOrderDto cancelOrderDto) {
        otsGrpc.otsFutureStub stub = null;
        /**
         * 获取 grpc链接
         */
        try {
            stub = otsGrpc.newFutureStub(grpcChannel);
        } catch (Exception e) {
            log.error("::{}::撤单-获取oddin grp客户端错误", cancelOrderDto.getId(), e);
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        com.google.protobuf.Timestamp timestamp = com.google.protobuf.Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build();
        TicketCancel.TicketCancelRequest request = TicketCancel.TicketCancelRequest.newBuilder().setId(montageOrderNo(cancelOrderDto.getId(), cancelOrderDto.getSourceId()))
//                .addCancelBetInfo(TicketCancel.CancelBetInfo.newBuilder()
//                        .setCancelPercent(cancelOrderDto.getCancelPercent())
//                        .setId(cancelOrderDto.getId()).build())
                .setTimestamp(timestamp)
//                .setCancelPercent(cancelOrderDto.getCancelPercent())
                .setCancelReason(Enums.CancelReason.valueOf(cancelOrderDto.getCancelReason())).setCancelReasonDetail(cancelOrderDto.getCancelReasonDetail()).build();
        try {
            return stub.cancelTicket(request).get();
        } catch (InterruptedException e) {
            log.error("::{}::撤单-请求oddin grpc撤单接口出错", cancelOrderDto.getId(), e);
        } catch (ExecutionException e) {
            log.error("::{}::撤单-请求oddin grpc撤单接口出错", cancelOrderDto.getId(), e);
        }
        return null;
    }

    /**
     * 限额
     * @param ticketDto
     * @return
     */
    @Override
    public TicketMaxStake.TicketMaxStakeResponse queryMaxBetMoneyBySelect(TicketDto ticketDto) {
        otsGrpc.otsFutureStub stub = null;
        String userId = ticketDto.getCustomer().getId();
        try {
            //建立连接和响应体
            stub = otsGrpc.newFutureStub(grpcChannel);
        } catch (Exception e) {
            log.error("::{}::获取oddIn grpc链接错误", userId, e);
        }
        TicketOuterClass.Ticket ticket = ticketGrpcHandler.getTicket(ticketDto);
        TicketMaxStake.TicketMaxStakeRequest requestObserver = TicketMaxStake.TicketMaxStakeRequest.newBuilder().setTicket((ticket)).build();
        //限额接口响应体
        TicketMaxStake.TicketMaxStakeResponse ticketMaxStakeResponse = null;
        try {
            ticketMaxStakeResponse = stub.ticketMaxStake(requestObserver).get();
        } catch (Exception e) {
            log.error("::{}::请求oddin grpc 获取限额接口异常", userId, e);
        }
        try {
            log.info("::{}::获取oddIn最大限额:{}", userId, com.google.protobuf.util.JsonFormat.printer().print(ticketMaxStakeResponse));
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        return ticketMaxStakeResponse;
    }

}
