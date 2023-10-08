package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.CategoryOddsConfigDataVo;
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

import java.util.UUID;

/**
 * @Description: 玩法配置推送
 * Topic=RCS_CATEGORY_ODDS_CONFIG_TOPIC
 * Group=RCS_PUSH_CATEGORY_ODDS_CONFIG_TOPIC_GROUP
 * 对应指令-> 30041
 * message:{"data":{"matchId":2989301,"matchType":0,"playDataSource":{"SR":[153,159,162,172,173,204,253,254,255,256]}},"dataSourceTime":1640105834002,"linkId":"0acd597a72be45629bf38e6fd9290f8c_play_odds_config"}
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "RCS_CATEGORY_ODDS_CONFIG_TOPIC",
        consumerGroup = "RCS_PUSH_CATEGORY_ODDS_CONFIG_TOPIC_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class RcsCategoryOddsConfigConsumer implements RocketMQListener<MqMessageVo<CategoryOddsConfigDataVo>>, RocketMQPushConsumerLifecycleListener {

    private static final SubscriptionEnums subscriptionEnums = SubscriptionEnums.PLAY_CONFIGURATION;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(16);
        defaultMQPushConsumer.setConsumeThreadMax(32);
    }

    @Override
    public void onMessage(MqMessageVo<CategoryOddsConfigDataVo> mapRequest ) {
        if(mapRequest == null){
            return;
        }
        log.info("::{}::,::{}::,::{}::玩法配置推送-赛事Id={},消费数据->{}", mapRequest.getLinkId(),mapRequest.getData().getMatchId(), JSONObject.toJSON(mapRequest));
        if(mapRequest.getData() == null){
            return;
        }
        String msgId = UUID.randomUUID().toString();
        clientManageService.sendMessage(subscriptionEnums, mapRequest.getData().getMatchId(), ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), mapRequest.getData(), 0, mapRequest.getLinkId(), msgId, null));
    }
}
