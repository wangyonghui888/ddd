package com.panda.sport.rcs.mgr.mq.impl;

import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author :  Black
 * @Description :  串关订单同步队列
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "SERIES_ORDER_SYN",
        consumerGroup = "SERIES_ORDER_SYN",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class SeriesOrderConsumer implements RocketMQListener<OrderBean>, RocketMQPushConsumerLifecycleListener {
    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    private static String SERIES_ORDER_TOPIC = "SERIES_ORDER_SYN";

//	@Value("${rocketmq.order.save.config}")
//	private String saveOrderConfig;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    public SeriesOrderConsumer() {
//        super(SERIES_ORDER_TOPIC,"SERIES_ORDER_TOPIC");
    }

    @Override
    public void onMessage(OrderBean src) {
        producerSendMessageUtils.sendMessage("queue_settle_item", src);
    }


}
