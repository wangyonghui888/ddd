package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.WarningRiskVo;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Description: 赔率超过一分钟未下发赔率消息
 * Topic=MATCH_ODDS_WARNING_RISK
 * Group=rcs_MATCH_ODDS_WARNING_RISK_PUSH_GROUP
 * 对应指令-> 30015
 * message:{"standardMatchId":2986572,"marketCategoryId":19,"sign":false,"linkId":"1473288351567138817_AUTO_CLOSE_MARKET"}
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "MATCH_ODDS_WARNING_RISK",
        consumerGroup = "RCS_PUSH_MATCH_ODDS_WARNING_RISK_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class CategoryOddsWarnConsumer implements RocketMQListener<WarningRiskVo>, RocketMQPushConsumerLifecycleListener {

    private final static SubscriptionEnums subscriptionEnums = SubscriptionEnums.EXCEED_FIFTEEN_ODDS;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(16);
        defaultMQPushConsumer.setConsumeThreadMax(32);
    }

    @Override
    public void onMessage(WarningRiskVo warningRisk) {
        if(warningRisk == null){
            return;
        }
        log.info("::{}::,::{}::,::{}::赔率超过一分钟未下发赔率消息消费数据->{}", warningRisk.getLinkId(), warningRisk.getStandardMatchId(), warningRisk.getMarketCategoryId(), JSONObject.toJSON(warningRisk));
        try {
            List<String> playIds = new ArrayList<>();
            playIds.add(Integer.toString(warningRisk.getMarketCategoryId()));
            String msgId = UUID.randomUUID().toString();
            clientManageService.sendMessage(subscriptionEnums, warningRisk.getStandardMatchId(), playIds, ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), warningRisk, 0, warningRisk.getLinkId(), msgId, null));
        } catch (Exception e){
            log.error("::{}::,::{}::,::{}::赔率超过一分钟未下发赔率消息消费数据->{}，异常信息:", warningRisk.getLinkId(), warningRisk.getStandardMatchId(), warningRisk.getMarketCategoryId(), warningRisk, e);
        }

    }
}
