package com.panda.sport.rcs.oddin.client;


import com.panda.sport.rcs.oddin.entity.ots.TicketResultOuterClass;
import com.panda.sport.rcs.oddin.entity.ots.otsGrpc;
import com.panda.sport.rcs.oddin.grpc.impl.PullSingleGrpcServiceImpl;
import com.panda.sport.rcs.oddin.util.DateUtil;
import com.panda.sport.rcs.utils.SpringContextUtils;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class GrpcPullSingleClient {

    public StreamObserver<TicketResultOuterClass.TicketResultRequest> requestStreamObserver;

    public GrpcPullSingleClient() {
        ManagedChannel channel = SpringContextUtils.getBeanByClass(ManagedChannel.class);
        PullSingleGrpcServiceImpl pullSingleGrpcService = SpringContextUtils.getBeanByClass(PullSingleGrpcServiceImpl.class);
        log.info("开始建立拉单GRPC-Stub连接....");
        otsGrpc.otsStub stub = otsGrpc.newStub(channel);
        //设置拉单回调
        StreamObserver<TicketResultOuterClass.TicketResultResponse> ticketResponseStreamObserver = pullSingleGrpcService.createTicketResponseStreamObserver();
        //设置观察者
        requestStreamObserver = stub.ticketResult(ticketResponseStreamObserver);
        log.info("拉单GRPC-Stub连接建立完毕...." + requestStreamObserver);

        new Timer("GPRC拉单心跳维持").schedule(new TimerTask() {
            @Override
            public void run() {
                requestStreamObserver.onNext(TicketResultOuterClass.TicketResultRequest.newBuilder().setKeepalive(TicketResultOuterClass.TicketResultRequest.newBuilder().getKeepaliveBuilder()).build());
                log.info("维持拉单9s心跳--->" + DateUtil.format_sss(new Date()));
            }
        }, 1000, 9000);

    }
}
