package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.MqMessageVo;
import com.panda.rcs.push.entity.vo.PlayMapShowVo;
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

/**
 * @Description: 玩法集展示设置推送
 * Topic=TRADE_CATEGORYSET_SHOW
 * Group=rcs_TRADE_CATEGORYSET_SHOW_PUSH_GROUP
 * 对应指令-> 30061
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "TRADE_CATEGORYSET_SHOW",
        consumerGroup = "RCS_PUSH_TRADE_CATEGORYSET_SHOW_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class CategorySetShowConsumer implements RocketMQListener<MqMessageVo<PlayMapShowVo>>, RocketMQPushConsumerLifecycleListener {

    private final static SubscriptionEnums subscriptionEnums = SubscriptionEnums.PLAY_SHOW_STATUS;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(16);
        defaultMQPushConsumer.setConsumeThreadMax(32);
    }

    @Override
    public void onMessage(MqMessageVo<PlayMapShowVo> mapShowVoMqMessage) {
        if(mapShowVoMqMessage == null){
            return;
        }

        log.info("::{}::,::{}::玩法集展示设置-赛事Id={}::::消费数据->{}", mapShowVoMqMessage.getLinkId(),mapShowVoMqMessage.getData().getMatchId(), JSONObject.toJSON(mapShowVoMqMessage));
        try {
            clientManageService.sendMessage(subscriptionEnums, mapShowVoMqMessage.getData().getMatchId(), ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), mapShowVoMqMessage, 0, mapShowVoMqMessage.getLinkId(), null, null));
        } catch (Exception e){
            log.error("::{}::,::{}::玩法集展示设置推送消费数据->{}，异常信息：", mapShowVoMqMessage.getLinkId(), mapShowVoMqMessage, e);
        }
    }
}
