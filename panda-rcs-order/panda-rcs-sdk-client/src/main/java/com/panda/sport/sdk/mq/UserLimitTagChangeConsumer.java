package com.panda.sport.sdk.mq;

import com.panda.sport.sdk.service.impl.LuaPaidService;
import com.panda.sport.sdk.util.GuiceContext;
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
 * 同步限额标签变更 刷新redis
 * trade服修改配置之后，发送此mq刷新用户标签redis配置信息
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "rcs_limit_user_tag_change_sdk",
        consumerGroup = "rcs_limit_user_tag_change_sdk_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class UserLimitTagChangeConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Override
    public void onMessage(String body) {
        try {
            if (StringUtils.isBlank(body)) {
                log.warn("接收参数为空,跳过:{}", body);
                return;
            }
            LuaPaidService luaPaidService = GuiceContext.getInstance(LuaPaidService.class);
            luaPaidService.updateUserTag(body);
        } catch (Exception e) {
            log.error("限额标签变更刷新处理异常:", e);
        }
    }
}
