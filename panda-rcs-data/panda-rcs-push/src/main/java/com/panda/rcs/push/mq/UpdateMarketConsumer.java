package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.sport.rcs.vo.MatchStatusAndDataSuorceVo;
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
 * @Description: 推送赛事级别以下状态
 * Topic=MATCH_SNAPSHOT_MARKET_UPDATE
 * Group=RCS_PUSH_MATCH_SNAPSHOT_MARKET_UPDATE_GROUP
 * 对应指令-> 30022
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "MATCH_SNAPSHOT_MARKET_UPDATE",
        consumerGroup = "RCS_PUSH_MATCH_SNAPSHOT_MARKET_UPDATE_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class UpdateMarketConsumer implements RocketMQListener<MatchStatusAndDataSuorceVo>, RocketMQPushConsumerLifecycleListener {

    private static final SubscriptionEnums subscriptionEnums = SubscriptionEnums.MATCH_OTHER_STATUS;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(8);
        defaultMQPushConsumer.setConsumeThreadMax(16);
    }

    @Override
    public void onMessage(MatchStatusAndDataSuorceVo matchStatusAndDataSuorceVo) {
        if(matchStatusAndDataSuorceVo == null){
            return;
        }
        String msgId = UUID.randomUUID().toString();

        log.info("::{}::赛事级别操盘状态-操盘平台={}::消费数据->{}",matchStatusAndDataSuorceVo.getMatchId(), matchStatusAndDataSuorceVo.getRiskManagerCode(), JSONObject.toJSON(matchStatusAndDataSuorceVo));

        clientManageService.sendMessage(subscriptionEnums, Long.toString(matchStatusAndDataSuorceVo.getMatchId()), ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), matchStatusAndDataSuorceVo, 0, matchStatusAndDataSuorceVo.getId(), msgId, null));

    }
}
