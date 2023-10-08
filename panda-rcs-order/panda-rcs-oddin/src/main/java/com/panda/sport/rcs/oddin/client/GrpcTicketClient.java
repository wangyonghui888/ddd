package com.panda.sport.rcs.oddin.client;


import com.panda.sport.rcs.oddin.entity.ots.TicketOuterClass;
import com.panda.sport.rcs.oddin.entity.ots.otsGrpc;
import com.panda.sport.rcs.oddin.grpc.impl.TicketGrpcServiceImpl;
import com.panda.sport.rcs.oddin.util.DateUtil;
import com.panda.sport.rcs.utils.SpringContextUtils;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class GrpcTicketClient {

    public StreamObserver<TicketOuterClass.TicketRequest> orderObserver;

    public GrpcTicketClient() {
        ManagedChannel channel = SpringContextUtils.getBeanByClass(ManagedChannel.class);
        TicketGrpcServiceImpl ticketGrpcService = SpringContextUtils.getBeanByClass(TicketGrpcServiceImpl.class);
        log.info("开始建立投注GRPC-Stub连接....");
        otsGrpc.otsStub stub = otsGrpc.newStub(channel);
        //设置回调
        StreamObserver<TicketOuterClass.TicketResponse> ticketResponseStreamObserver = ticketGrpcService.createTicketResponseStreamObserver();
        //设置观察者
        orderObserver = stub.ticket(ticketResponseStreamObserver);
        log.info("投注GRPC-Stub连接建立完毕...." + orderObserver);

        new Timer("GPRC注单心跳维持").schedule(new TimerTask() {
            @Override
            public void run() {
                orderObserver.onNext(TicketOuterClass.TicketRequest.newBuilder().setKeepalive(TicketOuterClass.TicketRequest.newBuilder().getKeepaliveBuilder()).build());
                log.info("维持注单9s心跳--->" + DateUtil.format_sss(new Date()));
            }
        }, 1000, 9000);

    }
}
