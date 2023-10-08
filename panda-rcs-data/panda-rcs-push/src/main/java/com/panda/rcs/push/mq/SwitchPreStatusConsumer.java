package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.sport.rcs.pojo.StandardMatchSwitchStatusMessage;
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
 * @Description: 赛前转滚球状态
 * Topic=STANDARD_MATCH_SWITCH_STATUS
 * Group=RCS_PUSH_STANDARD_MATCH_SWITCH_STATUS_GROUP
 * 对应指令-> 30001
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "STANDARD_MATCH_SWITCH_STATUS",
        consumerGroup = "RCS_PUSH_STANDARD_MATCH_SWITCH_STATUS_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class SwitchPreStatusConsumer implements RocketMQListener<Request<StandardMatchSwitchStatusMessage>>, RocketMQPushConsumerLifecycleListener {

    private static final SubscriptionEnums subscriptionEnums = SubscriptionEnums.MATCH_STATUS;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(16);
        defaultMQPushConsumer.setConsumeThreadMax(32);
    }

    @Override
    public void onMessage(Request<StandardMatchSwitchStatusMessage> standardMatchSwitchStatusMessageRequest) {
        if(standardMatchSwitchStatusMessageRequest ==null  &&standardMatchSwitchStatusMessageRequest.getData()==null){
            return;
        }
        if(standardMatchSwitchStatusMessageRequest.getData().getOddsLive()!=null){
            standardMatchSwitchStatusMessageRequest.getData().setMatchStatus(standardMatchSwitchStatusMessageRequest.getData().getOddsLive());
        }
        log.info("::{}::,::{}::赛事状态消费数据->{}",standardMatchSwitchStatusMessageRequest.getLinkId(), standardMatchSwitchStatusMessageRequest.getData().getStandardMatchId(), JSONObject.toJSON(standardMatchSwitchStatusMessageRequest));

        clientManageService.sendMessage(subscriptionEnums, Long.toString(standardMatchSwitchStatusMessageRequest.getData().getStandardMatchId()), ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), standardMatchSwitchStatusMessageRequest, 0, standardMatchSwitchStatusMessageRequest.getLinkId(), null, null));

    }
}
