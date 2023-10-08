package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.MqMessageVo;
import com.panda.rcs.push.utils.ClientResponseUtils;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 操盘右侧为读消息数量
 * Topic=MESSAGE_NO_READ_NUM_TOPIC
 * Group=RCS_PUSH_MESSAGE_NO_READ_NUM_TOPIC_GROUP
 * 对应指令 -> 30039
 * 数据：{"data":{"1":230,"2":3,"3":0,"4":0,"traderId":"527"},"dataSourceTime":1640875605989,"linkId":"ee5f90ee9b34430e8346891d0cd480e6-1640875596213"}
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "MATCH_STAGE_BACK_TOPIC",
        consumerGroup = "RCS_PUSH_MESSAGE_MATCH_STAGE_BACK_TOPIC_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchStageBackReadMessageConsumer implements RocketMQListener<MqMessageVo<JSONObject>>, RocketMQPushConsumerLifecycleListener {

    private static final SubscriptionEnums subscriptionEnums = SubscriptionEnums.MATCH_STAGE_BACK;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(8);
        defaultMQPushConsumer.setConsumeThreadMax(16);
    }

    @Override
    public void onMessage(MqMessageVo<JSONObject> messageVo) {
        log.info("::{}::未读消息-消费数据->{}", messageVo.getLinkId(), JSONObject.toJSON(messageVo));
        if(messageVo.getData() != null){

            String sportId = messageVo.getData().getString("sportId");

            Object zsMessage = ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), messageVo, 0, messageVo.getLinkId(), messageVo.getLinkId(), null);

            log.info("Topic:{}:Code:{}:发送参数:{}","MATCH_STAGE_BACK_TOPIC",subscriptionEnums.getValue(),JSONObject.toJSON(zsMessage));
            clientManageService.sendMessage(subscriptionEnums, Integer.valueOf(sportId), null, zsMessage, zsMessage);
        }
    }
}
