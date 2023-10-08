package com.panda.rcs.stray.limit.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
//@TraceCrossThread
//@RocketMQMessageListener(
//        topic = "RCS_TOUR_TEMPLATE_CASHOUT_TOPIC",
//        consumerGroup = "RCS_PUSH_TOUR_TEMPLATE_CASHOUT_TOPIC_GROUP",
//        messageModel = MessageModel.CLUSTERING,
//        consumeMode = ConsumeMode.CONCURRENTLY)
public class AdvanceSettlementMarkConsumer implements RocketMQListener<Object>, RocketMQPushConsumerLifecycleListener {


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(32);
        defaultMQPushConsumer.setConsumeThreadMax(64);
    }

    @Override
    public void onMessage(Object object) {

    }
}
