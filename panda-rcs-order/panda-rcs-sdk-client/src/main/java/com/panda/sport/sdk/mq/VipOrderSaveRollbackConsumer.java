package com.panda.sport.sdk.mq;

import com.panda.sport.sdk.service.impl.LuaPaidService;
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
 * VIP用户限额回滚 刷新redis
 * 逻辑： 见【OrderSaveRollbackConsumer】注释
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "ORDER_SAVE_ROLLBACK_VIP",
        consumerGroup = "ORDER_SAVE_ROLLBACK_VIP_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class VipOrderSaveRollbackConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {
    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Override
    public void onMessage(String body) {
        try {
            LuaPaidService luaPaidService = GuiceContext.getInstance(LuaPaidService.class);

            luaPaidService.rallBackShakeyVip(body);
        } catch (Exception e) {
            log.error("VIP用户限额回滚处理异常:", e);
        }
    }
}
