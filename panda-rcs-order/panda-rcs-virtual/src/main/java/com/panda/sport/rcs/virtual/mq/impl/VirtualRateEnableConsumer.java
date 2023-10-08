package com.panda.sport.rcs.virtual.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.service.IRcsVirtualOrderExtService;
import com.panda.sport.rcs.virtual.constants.Constants;
import com.panda.sport.rcs.virtual.service.VirtualDataServiceImpl;
import com.panda.sport.rcs.virtual.service.VirtualServiceImpl;
import com.panda.sport.rcs.virtual.third.client.model.RcsQuotaBusinessRateDTO;
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
 * @author :  gulang
 * @Description :  商户VR藏单设置 mq消费
 * @Date: 2020-12-22 21:30:37
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "rcs_virtual_panda_rate_enable",
        consumerGroup = "rcs_virtual_panda_rate_enable",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class VirtualRateEnableConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Autowired
    private RedisClient redisClient;


    @Override
    public void onMessage(String jsonStr) {
        String linkId = "virtualRateEnable";
        try {
            log.info("虚拟赛事VR藏单设置mq消费 ：{}", jsonStr);
            RcsQuotaBusinessRateDTO dto = JSONObject.parseObject(jsonStr, RcsQuotaBusinessRateDTO.class);
            String vrEnableKey = String.format(Constants.VR_ENABLE_AMOUNT_RATE,dto.getBusinessId());
            redisClient.set(vrEnableKey,dto.getVrEnable());
        } catch (Exception e) {
            log.error("::{}::虚拟商户VR启用设置mq消费异常：{}{}", linkId,e.getMessage(), e);
        }
        return ;
    }
}
