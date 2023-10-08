package com.panda.sport.sdk.mq;

import com.panda.sport.sdk.listeners.LimitHandler;
import com.panda.sport.sdk.util.GuiceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.stereotype.Component;

/**
 * @Description 监控限额对应配置变更，集群刷新redis缓存
 * @Author beulah
 **/
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "rcs_limit_cache_clear_sdk",
        consumerGroup = "rcs_limit_cache_clear_redis_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class LimitCacheClearConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Override
    public void onMessage(String body) {
        try {
            LimitHandler limitHandler = GuiceContext.getInstance(LimitHandler.class);
            limitHandler.hand(body);
        } catch (Exception e) {
            log.error("限额刷新处理异常:", e);
        }
    }
}
