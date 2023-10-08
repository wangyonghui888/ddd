package com.panda.sport.rcs.mgr.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.constants.RedisKey;
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

import java.util.List;

import static com.panda.sport.rcs.constants.RedisKey.REDIS_HIDE_RANGE_CONFIG;

/**
 * @author :  bobi
 * @Description :  藏单配置修改变更本地缓存
 * @Date: 2023-08-01 14:40
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "RCS_HIDE_RANGE_CONFIG",
        consumerGroup = "RCS_HIDE_RANGE_CONFIG_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class RcsHideRangeConfigConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(4);
        defaultMQPushConsumer.setConsumeThreadMax(32);
    }

    @Override
    public void onMessage(String msg) {
        if(StringUtils.isNotBlank(msg)){
            List<String> lists = JSONObject.parseArray(msg, String.class);
            for (String key : lists) {
                RcsLocalCacheUtils.timedCache.remove(REDIS_HIDE_RANGE_CONFIG + key);
            }
        }
    }

}
