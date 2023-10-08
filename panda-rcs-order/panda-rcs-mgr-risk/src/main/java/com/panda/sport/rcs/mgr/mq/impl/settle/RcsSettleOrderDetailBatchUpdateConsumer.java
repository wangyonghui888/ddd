//package com.panda.sport.rcs.mgr.mq.impl.settle;
//
//import com.alibaba.fastjson.JSONObject;
//import com.panda.sport.rcs.mapper.TOrderDetailMapper;
//import com.panda.sport.rcs.mgr.utils.RedisUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@Component
//@RefreshScope
//public class RcsSettleOrderDetailBatchUpdateConsumer {
//
//    @Autowired
//    RedisUtils redisUtils;
//    @Autowired
//    TOrderDetailMapper orderDetailMapper;
//
//    @Value("${settle.orderDetailBatchUpdate.time:1000}")
//    private int batchTime;
//    @Value("${settle.orderDetailBatchUpdate.size:100}")
//    private int batchSize;
//
//    @PostConstruct
//    public void initRedisConsumerMethod() {
//        new Thread(() -> {
//            List<Map<String, Object>> list = new ArrayList<>();
//            long startTime = System.currentTimeMillis();
//            String lockKey = "RCS:SETTLE:ORDER:DETAIL:BATCH:LOCK";
//            while (true) {
//                try {
//                    //一个批次的任务同时一台机器跑
//                    if (list.isEmpty()) {
//                        if (!redisUtils.setNX(lockKey, "1", 60L)) {
//                            Thread.sleep(batchTime);
//                            continue;
//                        }
//                    }
//                    List<String> values = redisUtils.blpop("RCS:SETTLE:ORDER:DETAIL:BATCH:SAVE", 1);
//                    long endTime = System.currentTimeMillis();
//                    if (CollectionUtils.isNotEmpty(values) && values.size() == 2) {
//                        String value = values.get(1);
//                        list.add((JSONObject) JSONObject.parse(value));
//
//                        if (endTime - startTime > batchTime || list.size() >= batchSize) {
//                            startTime = System.currentTimeMillis();
//                            log.info("RCS_SETTLE_ORDER_DETAIL_BATCH_SAVE size: {},{}", list.size(), JSONObject.toJSONString(list));
////                             执行批量
//                            orderDetailMapper.updateBatchOrderDetailStatus(list);
//                            list.clear();
//                            redisUtils.del(lockKey);
//                        }
//                    } else {
//                        if (endTime - startTime > batchTime && !list.isEmpty()) {
//                            startTime = System.currentTimeMillis();
//                            log.info("RCS_SETTLE_ORDER_DETAIL_BATCH_SAVE size: {},{}", list.size(), JSONObject.toJSONString(list));
////                             执行批量
//                            orderDetailMapper.updateBatchOrderDetailStatus(list);
//                            list.clear();
//                            redisUtils.del(lockKey);
//                        }
//                    }
//                } catch (Exception e) {
//                    //异常之后把数据重新塞回队列，可以做重试次数
//                    log.error("orderDetailBatchUpdate Exception", e);
//                }
//            }
//
//        }).start();
//    }
//}
