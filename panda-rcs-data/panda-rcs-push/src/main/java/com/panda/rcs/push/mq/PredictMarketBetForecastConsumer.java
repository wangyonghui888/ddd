package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.BetForMarketResWsVo;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * @Description: 盘口货量 forecast
 * Topic=rcs_predict_market_bet_forecats_ws
 * Group=RCS_PUSH_predict_market_bet_forecats_ws_GROUP
 * 对应指令-> 30044
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "rcs_predict_market_bet_forecats_ws",
        consumerGroup = "RCS_PUSH_predict_market_bet_forecats_ws_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class PredictMarketBetForecastConsumer implements RocketMQListener<BetForMarketResWsVo>, RocketMQPushConsumerLifecycleListener {

    private static final SubscriptionEnums subscriptionEnums = SubscriptionEnums.MARKET_PREDICT_FORECAST;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(160);
        defaultMQPushConsumer.setConsumeThreadMax(320);
    }

    @Override
    public void onMessage(BetForMarketResWsVo betForMarketResWsVo) {
        if(betForMarketResWsVo == null){
            return;
        }
        String msgId= UUID.randomUUID().toString();
        log.info("::{}::,::{}::,::{}::盘口货量消费数据={}", betForMarketResWsVo.getLinkId(),betForMarketResWsVo.getMatchId(), betForMarketResWsVo.getPlayId(), JSONObject.toJSON(betForMarketResWsVo));
        try {
            List<String> playIds = Arrays.asList(Integer.toString(betForMarketResWsVo.getPlayId()));
            clientManageService.sendMessage(subscriptionEnums, Long.toString(betForMarketResWsVo.getMatchId()), playIds, ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), betForMarketResWsVo, 1, betForMarketResWsVo.getLinkId(), msgId, null));
        } catch (Exception e){
            log.error("::{}::,::{}::,::{}::盘口货量盘口货量消费数据={}，异常信息：", betForMarketResWsVo.getLinkId(),betForMarketResWsVo.getMatchId(), betForMarketResWsVo.getPlayId(), betForMarketResWsVo, e);
        }
    }
}
