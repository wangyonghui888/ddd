package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.rcs.push.service.MatchEventService;
import com.panda.sport.rcs.pojo.dto.MatchEventInfoDTO;
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
 * @Description: uof事件信息
 * Topic=FAKE_MATCH_EVENT
 * Group=RCS_PUSH_FAKE_MATCH_EVENT_GROUP
 * 对应指令 -> 30003
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "FAKE_MATCH_EVENT",
        consumerGroup = "RCS_PUSH_FAKE_MATCH_EVENT_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchLiveUofEventConsumer implements RocketMQListener<Request<MatchEventInfoDTO>>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private MatchEventService matchEventService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(96);
    }

    @Override
    public void onMessage(Request<MatchEventInfoDTO> matchEventInfoDTORequest) {
        log.info("::{}::,::{}::标准事件(UOF)-赛事事件消费->{}", matchEventInfoDTORequest.getLinkId(), matchEventInfoDTORequest.getData().getStandardMatchId(), JSONObject.toJSON(matchEventInfoDTORequest));
        matchEventService.handlerMatchEvent(matchEventInfoDTORequest.getData(), matchEventInfoDTORequest.getLinkId());
    }
}
