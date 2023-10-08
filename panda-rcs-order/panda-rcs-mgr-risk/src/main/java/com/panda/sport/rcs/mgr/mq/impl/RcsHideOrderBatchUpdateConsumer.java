package com.panda.sport.rcs.mgr.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.service.orderhide.ITOrderHideService;
import com.panda.sport.rcs.mgr.utils.RedisUtils;
import com.panda.sport.rcs.vo.TOrderHide;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RefreshScope
public class RcsHideOrderBatchUpdateConsumer {

    @Autowired
    RedisUtils redisUtils;
    @Autowired
    TOrderDetailMapper orderDetailMapper;

    @Autowired
    ITOrderHideService itOrderHideService;
    @Value("${hide.order.batch.Update.time:1000}")
    private int batchTime;
    @Value("${hide.order.batch.Update.size:100}")
    private int batchSize;

    @PostConstruct
    public void initRedisConsumerMethod() {
        new Thread(() -> {
            List<TOrderHide> list=new ArrayList<>();
            long startTime = System.currentTimeMillis();
            String lockKey = "RCS:HIDE:ORDER:BATCH:LOCK";
            while (true) {
                try {
                    //一个批次的任务同时一台机器跑
                    if (list.isEmpty()) {
                        if (!redisUtils.setNX(lockKey, "1", 60L)) {
                            Thread.sleep(batchTime);
                            continue;
                        }
                    }
                    List<String> values = redisUtils.blpop(Constants.RCS_HIDE_ORDER_BATCH_SAVE, 1);
                    long endTime = System.currentTimeMillis();
                    if (CollectionUtils.isNotEmpty(values) && values.size() == 2) {
                        String value = values.get(1);
                        list.add(JSONObject.parseObject(value,TOrderHide.class));
                        if (endTime - startTime > batchTime || list.size() >= batchSize) {
                            startTime = System.currentTimeMillis();
                            log.info("RCS:HIDE:ORDER:BATCH:SAVE list size: {}", list.size());
                            //执行批量
                            itOrderHideService.insertOrUpdates(list);
                            list.clear();
                            redisUtils.del(lockKey);
                        }
                    } else {
                        if (endTime - startTime > batchTime && !list.isEmpty()) {
                            startTime = System.currentTimeMillis();
                            log.info("RCS:HIDE:ORDER:BATCH:SAVE  list size: {}", list.size());
                            //执行批量
                            itOrderHideService.insertOrUpdates(list);
                            list.clear();
                            redisUtils.del(lockKey);
                        }
                    }
                } catch (Exception e) {
                    log.error("orderHideBatchUpdate Exception{}", e.getMessage());
                }
            }

        }).start();
    }
}
