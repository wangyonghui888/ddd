package com.panda.rcs.order.reject.mq;

import com.panda.rcs.order.reject.service.RejectBetService;
import com.panda.sport.data.rcs.dto.PreOrderRequest;
import com.panda.sport.rcs.common.MqConstants;
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
 *
 * @author Eamon
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MqConstants.RCS_PRE_SETTLE_ORDER_REJECT,
        consumerGroup = MqConstants.RCS_PRE_SETTLE_ORDER_REJECT_GROUP,
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class PreSettleRejectConsumer implements RocketMQListener<PreOrderRequest>, RocketMQPushConsumerLifecycleListener {
    @Autowired
    RejectBetService rejectBetServiceImpl;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMqPushConsumer) {
        defaultMqPushConsumer.setConsumeThreadMin(128);
        defaultMqPushConsumer.setConsumeThreadMax(128);
    }

    @Override
    public void onMessage(PreOrderRequest orderBean) {
        try {
            rejectBetServiceImpl.preSettleReject(orderBean);
        } catch (Exception e) {
            log.error("::{}::风控提前结算接拒处理异常", orderBean.getOrderNo(), e);
        }


    }

}
