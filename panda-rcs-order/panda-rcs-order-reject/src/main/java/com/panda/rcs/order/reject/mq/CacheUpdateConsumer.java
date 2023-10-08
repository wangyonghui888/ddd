package com.panda.rcs.order.reject.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.order.reject.constants.RedisKey;
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
        topic = "rcs_order_reject_cache_update",
        consumerGroup = "rcs_order_reject_cache_update_group",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class CacheUpdateConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void onMessage(String message) {
        JSONObject map = JSON.parseObject(message);
        String redisKey = map.getString("key");
        long timeOut = redisKey.contains("rcs:order:accept:dataSourceCode") || redisKey.contains("rcs:match:last:time:event:match") ? RedisKey.ORDINARY_TIME_OUT : RedisKey.CACHE_TIME_OUT;
        if (map.getString(RedisKey.VALUE).equalsIgnoreCase("1")) {
            log.info("::{}::开始删除事件配置缓存", redisKey);
            RcsLocalCacheUtils.timedCache.remove(redisKey);
        } else {
            log.info("::{}::reject开始刷新缓存", redisKey);
            RcsLocalCacheUtils.timedCache.put(redisKey, map.get("value"), timeOut);
        }

    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(64);
        consumer.setConsumeThreadMax(128);
    }
}
