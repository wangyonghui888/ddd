package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
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
 * 待确定
 * @Description: 更新盘口索引
 * Topic=WS_UPDATE_MARKET_INDEX_TOPIC
 * Group=RCS_PUSH_WS_UPDATE_MARKET_INDEX_TOPIC_GROUP
 * 对应指令-> 30033
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "WS_UPDATE_MARKET_INDEX_TOPIC",
        consumerGroup = "RCS_PUSH_WS_UPDATE_MARKET_INDEX_TOPIC_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class UpdateMarketIndexConsumer implements RocketMQListener<RcsMatchMarketConfig>, RocketMQPushConsumerLifecycleListener {

    private static final SubscriptionEnums subscriptionEnums = SubscriptionEnums.UPDATE_MARKET_INDEX;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(16);
        defaultMQPushConsumer.setConsumeThreadMax(32);
    }

    @Override
    public void onMessage(RcsMatchMarketConfig rcsMatchMarketConfig) {
        if(rcsMatchMarketConfig == null){
            return;
        }

        log.info("::{}::,::{}::,::{}::赛事级别状态消费数据->{}", rcsMatchMarketConfig.getGlobalId(),rcsMatchMarketConfig.getMatchId(), rcsMatchMarketConfig.getPlayId(), JSONObject.toJSON(rcsMatchMarketConfig));
        String msgId = UUID.randomUUID().toString();
        clientManageService.sendMessage(subscriptionEnums, Long.toString(rcsMatchMarketConfig.getMatchId()), ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), rcsMatchMarketConfig, 0, rcsMatchMarketConfig.getGlobalId(), msgId, null));
    }
}
