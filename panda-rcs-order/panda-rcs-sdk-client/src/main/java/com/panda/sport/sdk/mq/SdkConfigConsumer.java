package com.panda.sport.sdk.mq;

import com.panda.sport.sdk.constant.RedisKeys;
import com.panda.sport.sdk.listeners.BusConfigHandler;
import com.panda.sport.sdk.util.GuiceContext;
import com.panda.sport.sdk.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "rcs_sdk_config_",
        consumerGroup = "rcs_sdk_config_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class SdkConfigConsumer implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }


    @Override
    public void onMessage(MessageExt ext) {
        StopWatch sw = new StopWatch();
        sw.start();
        String traceId = getTraceId(ext);
        MDC.put("X-B3-TraceId", traceId);
        String topic = ext.getTopic();
        byte[] byteBody = ext.getBody();
        String keys = ext.getKeys();
        String tags = ext.getTags();
        try {
            String body = new String(byteBody, StandardCharsets.UTF_8);
            ext.setBody(null);
            log.info(topic + ",sdk收到消息：body:{},uuid:{}", body, traceId);

            if (StringUtils.isBlank(body)) {
                log.warn("接收参数为空,不做处理:{}", ext);
                return;
            }
            BusConfigHandler busConfigHandler = GuiceContext.getInstance(BusConfigHandler.class);

            busConfigHandler.handleBusConfigMsg(topic, tags, keys, body);
        } catch (Exception e) {
            log.error("rcs_sdk_config_处理异常:", e);
        }
    }

    private String getTraceId(MessageExt ext) {
        String topic = ext.getTopic();
        if (RedisKeys.RCS_SDK_SETTLE.equals(topic)) {
            String tags = ext.getTags();
            if (StringUtils.isNotBlank(tags) && tags.length() > 24) {
                return tags;
            }
        }
        String keys = ext.getKeys();
        if (StringUtils.isNotBlank(keys) && keys.length() > 24) {
            return keys;
        }
        String msgId = ext.getMsgId();
        if (StringUtils.isNotBlank(msgId) && msgId.length() > 24) {
            return msgId;
        }
        return StringUtil.getUUID();
    }

}
