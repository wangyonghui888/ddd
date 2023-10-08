package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.sport.rcs.pojo.dto.MatchTradeMemoRemindDTO;
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

import java.util.List;

/**
 * @Describtion
 * @Auther jstyDC
 * @Date 2022-04-2022/4/17 11:22
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "WS_REMIND_TRADE_READ_MEMO",
        consumerGroup = "RCS_PUSH_WS_REMIND_TRADE_READ_MEMO_TOPIC_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchTradeMemoRemindConsumer implements RocketMQListener<Request<List<MatchTradeMemoRemindDTO>>>, RocketMQPushConsumerLifecycleListener {


    private final static SubscriptionEnums subscriptionEnums = SubscriptionEnums.MATCH_TRADE_MEMO_REMIND;

    @Autowired
    private ClientManageService clientManageService;
    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(4);
        consumer.setConsumeThreadMax(8);
    }

    @Override
    public void onMessage(Request<List<MatchTradeMemoRemindDTO>> message) {
        log.info("::{}::操盘备注读取信息消费数据->{}", message.getLinkId(), JSONObject.toJSONString(message));
        try {
            if (message == null || message.getData() == null) {
                return;
            }
            List<MatchTradeMemoRemindDTO> sendDatas = message.getData();
            for (MatchTradeMemoRemindDTO to:sendDatas){
                Object sendMessage = ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), to, 0, null, null, null);
                clientManageService.sendMessage(subscriptionEnums,Long.toString(to.getMatchId()), sendMessage);
            }
        } catch (Exception e) {
            log.error("::{}::操盘备注读取信息消费数据->{}，异常信息->{}：", message.getLinkId(), message, e);
        }

    }
}
