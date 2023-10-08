package com.panda.rcs.push.mq;

import cn.hutool.json.JSONUtil;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.ClientResponseVo;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.sport.rcs.pojo.vo.MatchSpecEventSwitchVo;
import com.panda.rcs.push.entity.vo.MqMessageVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * 特殊事件切换通知前端
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "WS_MATCH_SPEC_EVENT_SWITCH",
        consumerGroup = "MATCH_SPEC_EVENT_SWITCH_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchSpecEventSwitchConsumer implements RocketMQListener<MatchSpecEventSwitchVo>, RocketMQPushConsumerLifecycleListener {
    private final static SubscriptionEnums subscriptionEnums = SubscriptionEnums.MATCH_SPEC_EVENT_SWITCH;
    @Resource
    private ClientManageService clientManageService;

    @Override
    public void onMessage(MatchSpecEventSwitchVo msg) {
        log.info("::{}::特殊事件切换通知前端->{}", msg.getGlobalId(), JSONUtil.toJsonStr(msg));

        try {
            String msgId = UUID.randomUUID().toString();
            MatchSpecEventSwitchVo dto = msg;
            ClientResponseVo voData = ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), dto, 1, msg.getGlobalId(), msgId, null);
            log.info("::{}::特殊事件切换通知前端->{}", msg.getGlobalId(), JSONUtil.toJsonStr(voData));
            clientManageService.sendMessage(subscriptionEnums, dto.getMatchId() + "", voData);
        } catch (Exception e) {
            log.error("::{}特殊事件切换通知前端->{}，异常信息：", msg.getGlobalId(), msg, e);
        }

    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(96);
    }
}
