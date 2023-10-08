package com.panda.sport.rcs.mgr.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.api.OrderPaidApiService;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.mgr.service.limit.LimitCallbackService;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


/**
 * 订单取消，调用结算接口，回滚矩阵
 *
 * @author :  carver
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "queue_rcs_refusal_order",
        consumerGroup = "queue_rcs_refusal_order",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class OrderRefusalConsumer implements RocketMQListener<SettleItem>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Autowired
    @Qualifier("orderPaidApiImpl")
    private OrderPaidApiService orderPaidApiService;
    @Autowired
    private LimitCallbackService limitCallbackService;

    public OrderRefusalConsumer() {
//        super(MqConstants.RCS_ORDER_REFUSAL_TOPIC, "");
    }

    @Override
    public void onMessage(SettleItem src) {
        log.info("::{}::接收到拒单MQ,实体bean:{}",src.getOrderNo(),JSONObject.toJSONString(src));
        try {
            Request<SettleItem> requestParam = new Request<>();
            requestParam.setData(src);
            Integer outCome = src.getOutCome();
            if (outCome != null && outCome == 9) {
                limitCallbackService.seriesLimitCallback(src.getOrderNo());
            }
            orderPaidApiService.updateOrderAfterRefund(requestParam);
        } catch (Exception ex) {
            log.error("::{}::接收到拒单错误 {}",src.getOrderNo(),ex.getMessage(),ex);
        }
        return ;
    }

}
