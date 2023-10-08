package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.sport.rcs.pojo.dto.ThirdStatisticsNotifyDTO;
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
 * @Description: 三方统计比分  -- 事件列表
 * Topic=WS_THIRD_STATISTICS_NOTIFY_TOPIC
 * Group=RCS_PUSH_WS_THIRD_STATISTICS_NOTIFY_TOPIC_GROUP
 * 对应指令 -> 30051
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "WS_THIRD_STATISTICS_NOTIFY_TOPIC",
        consumerGroup = "RCS_PUSH_WS_THIRD_STATISTICS_NOTIFY_TOPIC_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchThirdStatisticsNotifyConsumer implements RocketMQListener<ThirdStatisticsNotifyDTO>, RocketMQPushConsumerLifecycleListener {

    private final static SubscriptionEnums subscriptionEnums = SubscriptionEnums.SELF_MATCH_EVENT_SOURCE;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(8);
        defaultMQPushConsumer.setConsumeThreadMax(16);
    }

    @Override
    public void onMessage(ThirdStatisticsNotifyDTO thirdStatisticsNotifyDTO) {
        log.info("::{}::,::{}::自研报球板消费数据->{}", thirdStatisticsNotifyDTO.getGlobalId(), thirdStatisticsNotifyDTO.getStandardMatchId(), JSONObject.toJSON(thirdStatisticsNotifyDTO));

        Object sendMessage = ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), thirdStatisticsNotifyDTO, 0, thirdStatisticsNotifyDTO.getGlobalId(), thirdStatisticsNotifyDTO.getGlobalId(), null);
        clientManageService.sendMessage(subscriptionEnums, Long.toString(thirdStatisticsNotifyDTO.getStandardMatchId()), sendMessage);
    }
}
