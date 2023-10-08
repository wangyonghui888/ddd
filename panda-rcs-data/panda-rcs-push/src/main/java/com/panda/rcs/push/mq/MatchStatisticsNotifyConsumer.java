package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.ClientResponseVo;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.sport.rcs.pojo.dto.MatchStatisticsInfoDTO;
import com.panda.sport.rcs.pojo.dto.StatisticsNotifyDTO;
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
 * @Description:  uof统计信息推送
 * Topic=WS_STATISTICS_NOTIFY_TOPIC
 * Group=RCS_PUSH_WS_STATISTICS_NOTIFY_TOPIC_GROUP
 * 对应指令-> 30014
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "WS_STATISTICS_NOTIFY_TOPIC",
        consumerGroup = "RCS_PUSH_WS_STATISTICS_NOTIFY_TOPIC_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchStatisticsNotifyConsumer implements RocketMQListener<StatisticsNotifyDTO>, RocketMQPushConsumerLifecycleListener {

    private final static SubscriptionEnums subscriptionEnums = SubscriptionEnums.LIVE_ODDS_UOF_DATA;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(96);
    }

    @Override
    public void onMessage(StatisticsNotifyDTO msg) {
        if(msg == null){
            return;
        }

        log.info("::{}::,::{}::UOF统计信息消费数据->{}",msg.getGlobalId(), msg.getStandardMatchId(), JSONObject.toJSON(msg));
        try {
            String matchId = Long.toString(msg.getStandardMatchId());
            MatchStatisticsInfoDTO matchStatisticsInfoDTO = new MatchStatisticsInfoDTO();
            matchStatisticsInfoDTO.setStandardMatchId(msg.getStandardMatchId());
            //-1是时间推送  1为统计数据
            if (msg.getChannel() == -1){
                matchStatisticsInfoDTO.setSecondsFromStart(msg.getSecondsFromStart());
                matchStatisticsInfoDTO.setPeriod(msg.getPeriod());
                matchStatisticsInfoDTO.setCategoryCount(msg.getCategoryCount());
            } else if(msg.getChannel() == 1){
                matchStatisticsInfoDTO.setMatchStatisticsInfoDetailList(msg.getList());
            }
            String msgId = UUID.randomUUID().toString();
            ClientResponseVo responseContext = ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), matchStatisticsInfoDTO, 0, msg.getGlobalId(), msgId, null);

            clientManageService.sendMessage(subscriptionEnums, matchId, responseContext);
        } catch (Exception e){
            log.error("::{}::UOF统计信息消费数据->{}，异常信息：", msg.getGlobalId(), msg, e);
        }
    }
}
