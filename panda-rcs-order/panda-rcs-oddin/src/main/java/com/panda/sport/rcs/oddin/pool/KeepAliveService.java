//package com.panda.sport.rcs.oddin.pool;
//
//import com.panda.sport.rcs.oddin.client.GrpcPullSingleClient;
//import com.panda.sport.rcs.oddin.client.GrpcTicketClient;
//import com.panda.sport.rcs.oddin.entity.ots.TicketOuterClass;
//import com.panda.sport.rcs.oddin.entity.ots.TicketResultOuterClass;
//import com.panda.sport.rcs.oddin.util.DateUtil;
//import io.grpc.stub.StreamObserver;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//
//@Slf4j
//@Service
//public class KeepAliveService {
//
//    public StreamObserver<TicketOuterClass.TicketRequest> orderObserver;
//    private StreamObserver<TicketResultOuterClass.TicketResultRequest> requestStreamObserver;
//
//    /**
//     * 建立注单心跳
//     */
//    @Scheduled(fixedDelay = 9000)
//    public void TicketKeepAlive() {
//        try {
//            if (orderObserver == null) {
//                GrpcTicketClient client = GrpcTicketClientPool.borrowObject();
//                orderObserver = client.orderObserver;
//            }
//            orderObserver.onNext(TicketOuterClass.TicketRequest.newBuilder().setKeepalive(TicketOuterClass.TicketRequest.newBuilder().getKeepaliveBuilder()).build());
//            log.info("维持注单9s心跳--->" + DateUtil.format_sss(new Date()));
//        } catch (Exception e) {
//            log.info("投注KeepAlive发生错误...:", e.getMessage());
//        }
//    }
//
//    /**
//     * 建立拉单心跳
//     */
//    @Scheduled(fixedDelay = 9000)
//    public void PullSingleKeepAlive() {
//        try {
//            if (requestStreamObserver == null) {
//                GrpcPullSingleClient client = GrpcPullSingleClientPool.borrowObject();
//                requestStreamObserver = client.requestStreamObserver;
//            }
//            requestStreamObserver.onNext(TicketResultOuterClass.TicketResultRequest.newBuilder().setKeepalive(TicketResultOuterClass.TicketResultRequest.newBuilder().getKeepaliveBuilder()).build());
//            log.info("维持拉单9s心跳--->" + DateUtil.format_sss(new Date()));
//        } catch (Exception e) {
//            log.info("投注KeepAlive发生错误...:", e.getMessage());
//        }
//    }
//}
