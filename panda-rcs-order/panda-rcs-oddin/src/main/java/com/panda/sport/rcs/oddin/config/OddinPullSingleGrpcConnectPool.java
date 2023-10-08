//package com.panda.sport.rcs.oddin.config;
//
//import com.panda.sport.rcs.oddin.entity.RequestStreamObserverDto;
//
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * 单例模式确保获取到的requestStreamObserverMap是同一个
// * @author z9-wiker
// */
//public class OddinPullSingleGrpcConnectPool {
//
//    private final ConcurrentHashMap<String, RequestStreamObserverDto> requestStreamObserverMap = new ConcurrentHashMap<>(1024);
//
//    private OddinPullSingleGrpcConnectPool(){
//
//    }
//
//    private static final OddinPullSingleGrpcConnectPool POOL = new OddinPullSingleGrpcConnectPool();
//
//    public static OddinPullSingleGrpcConnectPool getPool() {
//        return POOL;
//    }
//    public ConcurrentHashMap<String, RequestStreamObserverDto>  getConnectPool()  {
//        return requestStreamObserverMap;
//    }
//}
