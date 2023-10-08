package com.panda.rcs.order.reject.mq;

import com.panda.rcs.order.reject.constants.RedisKey;
import com.panda.rcs.order.reject.service.RejectBetService;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 标准赔率数据消费，用于订单实时接拒判断
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = RedisKey.REJECT_BET_ORDER,
        consumerGroup = RedisKey.REJECT_BET_ORDER_GROUP,
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class RejectBetConsumer implements RocketMQListener<OrderBean>, RocketMQPushConsumerLifecycleListener {
    @Autowired
    RejectBetService rejectBetServiceImpl;
    @Autowired
    RedisClient redisClient;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(126);
    }

    @Override
    public void onMessage(OrderBean orderBean) {
        try {
            rejectBetServiceImpl.contactService(orderBean);
        } catch (Exception e) {
            log.error("::{}::风控简易投注处理异常{}", orderBean.getOrderNo(), e.getMessage(), e);
        }


    }

}
