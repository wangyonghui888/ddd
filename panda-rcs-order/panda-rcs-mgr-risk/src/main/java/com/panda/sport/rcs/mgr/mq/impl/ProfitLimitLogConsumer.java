package com.panda.sport.rcs.mgr.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaUserSingleNoteService;
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
 * 盈利限额修改参数值业务下发通知，需要记录到风控日志表
 * 菜单路径：风控设置->商户风控管理->用户单日限额->盈利限额
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "queue_profit_limit_log",
        consumerGroup = "queue_profit_limit_log_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class ProfitLimitLogConsumer implements RocketMQListener<JSONObject>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    RcsQuotaUserSingleNoteService rcsQuotaUserSingleNoteService;
    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Override
    public void onMessage(JSONObject data) {
        log.info("::{}::盈利限额修改日志消息,data:{}",data.getString("userId"),data);
        rcsQuotaUserSingleNoteService.insertLimitLog(data);
    }
}
