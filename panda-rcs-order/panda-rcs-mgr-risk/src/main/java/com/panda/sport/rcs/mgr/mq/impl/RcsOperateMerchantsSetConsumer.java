package com.panda.sport.rcs.mgr.mq.impl;

import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.stereotype.Component;

/**
 * @author :  Magic
 * @Description :  商户修改mq同步变更本地缓存
 * @Date: 2022-10-01 14:40
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "RCS_OPERATE_MERCHANTS_SET",
        consumerGroup = "RCS_OPERATE_MERCHANTS_SET_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class RcsOperateMerchantsSetConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Override
    public void onMessage(String merchantId) {
        if(StringUtils.isNotBlank(merchantId)){
            RcsLocalCacheUtils.timedCache.remove("rcsOperateMerchantsSet:" + merchantId.replaceAll("\"",""));
        }
    }

}
