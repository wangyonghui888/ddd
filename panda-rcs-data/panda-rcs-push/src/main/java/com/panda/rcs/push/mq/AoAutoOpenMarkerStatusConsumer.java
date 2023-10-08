package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.ClientResponseVo;
import com.panda.rcs.push.utils.ClientResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

/**AO玩法自动开盘ws推送*/
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "RCS_AO_AUTO_OPEN_MARKET_STATUS",
        consumerGroup = "RCS_AO_AUTO_OPEN_MARKET_STATUS_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class AoAutoOpenMarkerStatusConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    private final static SubscriptionEnums subscriptionEnums = SubscriptionEnums.AO_AUTO_OPEN_MARKET_CONFIRM;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void onMessage(String msg) {
        if(StringUtils.isBlank(msg)){
            return;
        }
        JSONObject jsonObject = JSON.parseObject(msg);
        String matchId = jsonObject.getString("matchId");
        String linkId = jsonObject.getString("linkId");
        log.info("::{}::,::{}::AO玩法自动开盘ws推送消费数据->{}", linkId, matchId,msg);
        String msgId = UUID.randomUUID().toString();
        ClientResponseVo responseContext = ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(),
                jsonObject, 0, null, msgId, matchId);
        clientManageService.sendMessage(subscriptionEnums, matchId, responseContext);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(8);
        defaultMQPushConsumer.setConsumeThreadMax(16);
    }
}
