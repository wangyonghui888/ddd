//package com.panda.sport.rcs.oddin.config;
//
//import com.panda.sport.rcs.oddin.entity.OderObserverDto;
//import com.panda.sport.rcs.oddin.entity.ots.TicketOuterClass;
//import io.grpc.stub.StreamObserver;
//
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * 单例模式确保获取到的orderObserverMap是同一个
// */
//public class OddinTicketGrpcConnectPool {
//
//    private ConcurrentHashMap<String, OderObserverDto> orderObserverMap = new ConcurrentHashMap<>(1024);
//
//    private OddinTicketGrpcConnectPool(){
//
//    }
//
//    private static OddinTicketGrpcConnectPool pool = new OddinTicketGrpcConnectPool();
//
//    public static OddinTicketGrpcConnectPool getPool() {
//        return pool;
//    }
//    public ConcurrentHashMap<String, OderObserverDto>  getConnectPool()  {
//        return orderObserverMap;
//    }
//}
