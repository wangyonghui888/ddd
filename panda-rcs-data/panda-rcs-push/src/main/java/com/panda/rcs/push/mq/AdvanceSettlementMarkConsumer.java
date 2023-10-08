package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.sport.rcs.vo.ConfigCashOutVo;
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

/**
 * @Description: 提前结算标识推送
 * Topic=STANDARD_MARKET_PRE_RESULT_TASK
 * Group=RCS_PUSH_STANDARD_MARKET_PRE_RESULT_TASK_GROUP
 * 对应指令-> 60001
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "RCS_TOUR_TEMPLATE_CASHOUT_TOPIC",
        consumerGroup = "RCS_PUSH_TOUR_TEMPLATE_CASHOUT_TOPIC_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class AdvanceSettlementMarkConsumer implements RocketMQListener<ConfigCashOutVo>, RocketMQPushConsumerLifecycleListener {

    private final static SubscriptionEnums subscriptionEnums = SubscriptionEnums.PRE_RESULT_MARKET;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(32);
        defaultMQPushConsumer.setConsumeThreadMax(64);
    }

    @Override
    public void onMessage(ConfigCashOutVo configCashOutVo) {
        try {
            if (configCashOutVo == null || configCashOutVo.getMatchId() == null) {
                return;
            }
            log.info("::{}::,::{}::提前结算标识-消费数据:{}", configCashOutVo.getMatchId(),configCashOutVo.getMarketCategoryId(), JSONObject.toJSON(configCashOutVo));
            //会有提前结算标识的玩法为null
            if(configCashOutVo.getMarketCategoryId()!=null){
                List<String> playIds = new ArrayList<String>();
                playIds.add(Long.toString(configCashOutVo.getMarketCategoryId()));
                clientManageService.sendMessage(subscriptionEnums, Long.toString(configCashOutVo.getMatchId()), playIds, ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), configCashOutVo, 0, "", "", null));
            }else {
                clientManageService.sendMessage(subscriptionEnums, Long.toString(configCashOutVo.getMatchId()), ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), configCashOutVo, 0, "", "", null));
            }
        } catch (Exception e) {
            log.error("::{}::,::{}::,提前结算标识异常-消费数据:{},异常信息:{}", configCashOutVo.getMatchId(),configCashOutVo.getMarketCategoryId(), configCashOutVo, e);
        }
    }
}
