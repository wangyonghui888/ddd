package com.panda.sport.rcs.mts.sportradar.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mts.sportradar.constants.Constants;
import lombok.extern.slf4j.Slf4j;
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
 * 业务主动拒单
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "rcs_risk_mts_cache_config",
        consumerGroup = "rcs_risk_mts_cache_config",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class MtsConfigConsumer implements RocketMQListener<JSONObject>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(10);
        defaultMQPushConsumer.setConsumeThreadMax(20);
    }


    @Autowired
    RedisClient redisClient;

    public MtsConfigConsumer() {
    }

    @Override
    public void onMessage(JSONObject jsonObject) {
        try {
            log.info("mts缓存接单配置收到:{}", jsonObject.toString());
            String mtsCacheExpire = jsonObject.getString("mtsCacheExpire");
            String mtsCacheRate = jsonObject.getString("mtsCacheRate");
            if (Integer.valueOf(mtsCacheExpire) > 0 && Integer.valueOf(mtsCacheRate) > 0) {
            }
            redisClient.set(Constants.MTS_ORDER_RATE, mtsCacheRate);
            redisClient.set(Constants.MTS_ORDER_EXPIRE, mtsCacheExpire);
            log.info("mts缓存接单配置设置完成:{}", jsonObject.toString());
        } catch (NumberFormatException e) {
            log.info("mts缓存接单配置设置异常:{}", jsonObject.toString());
        }

    }
}
