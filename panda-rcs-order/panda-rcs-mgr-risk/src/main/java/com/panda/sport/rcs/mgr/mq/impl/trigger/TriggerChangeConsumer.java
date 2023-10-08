package com.panda.sport.rcs.mgr.mq.impl.trigger;

import com.alibaba.fastjson.JSON;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mgr.utils.RealTimeControlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * SDK 缓存同步
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = RcsConstant.RISK_ORDER_TRIGGER_CHANGE,
        consumerGroup = RcsConstant.RISK_ORDER_TRIGGER_CHANGE,
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class TriggerChangeConsumer implements RocketMQListener<OrderBean>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private RealTimeControlUtils realTimeControlUtils;

    @Autowired
    TriggerChangeImpl triggerChangeImpl;
    @Autowired
    private RedisClient redisClient;

    private static String jumpPointsConfigKey = "rsc:jump:point:config:key";

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Override
    public void onMessage(OrderBean orderBean) {
        try {

            String val = redisClient.get(jumpPointsConfigKey);
            if(StringUtils.isNotBlank(val)){
                Map<String, String> map = JSON.parseObject(val, Map.class);
                //如果操盘后台过滤的开关打开了
                if (StringUtils.equals("1", map.get("key"))) {
                    String amount = map.get("amount");
                    if (StringUtils.isNotBlank(amount) && orderBean.getProductAmountTotal() != null && Long.valueOf(amount) * 100 > orderBean.getProductAmountTotal()) {
                        log.warn("::{}::当前订单金额小于操盘后台设置过滤金额，不处理，设置金额={}，订单金额={}", orderBean.getOrderNo(), amount, orderBean.getProductAmountTotal());
                        return;
                    }
                }
            }


            Long filterAmount = realTimeControlUtils.getAutomaticFilterAmount();
            if (filterAmount != null && orderBean.getProductAmountTotal() != null && filterAmount > orderBean.getProductAmountTotal()) {
                log.warn("::{}::当前订单金额小于设置过滤金额，不处理，设置金额={}，订单金额={}", orderBean.getOrderNo(), filterAmount, orderBean.getProductAmountTotal());
                return;
            }
            triggerChangeImpl.orderHandle(orderBean);
        } catch (Exception e) {
            log.error("::{}::SDK 缓存同步错误：{}",orderBean.getOrderNo(),e.getMessage());
        }

        return;
    }
}
