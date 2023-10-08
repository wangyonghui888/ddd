package com.panda.sport.rcs.limit.mq.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;


/**
 * 更新本地缓存
 */
@Component
@Slf4j
@RocketMQMessageListener(
        topic = "rcs_order_limit_cache_update",
        consumerGroup = "rcs_order_limit_cache_update_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class CacheUpdateConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void onMessage(String message) {
        //log.info("更新本地缓存收到:{}", message);
       JSONObject map = JSON.parseObject(message);
        log.info("更新本地缓存转换map后:{}", map);
        RcsLocalCacheUtils.timedCache.put(map.getString("key"), map.get("value"), 30 * 24 * 60 * 60 * 1000L);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(64);
        consumer.setConsumeThreadMax(128);
    }
}
