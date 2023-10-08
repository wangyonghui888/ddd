package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.BetForPlaceResWsVo;
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
 * @Description: 盘口货量-坑位 forecast
 * Topic=rcs_predict_place_bet_forecats_ws
 * Group=RCS_PUSH_predict_place_bet_forecats_ws_GROUP
 * 对应指令-> 30045
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "rcs_predict_place_bet_forecats_ws",
        consumerGroup = "RCS_PUSH_predict_place_bet_forecats_ws_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class PredictPlaceBetForecastConsumer implements RocketMQListener<List<BetForPlaceResWsVo>>, RocketMQPushConsumerLifecycleListener {

    private static final SubscriptionEnums subscriptionEnums = SubscriptionEnums.PLACE_PREDICT_FORECAST;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(160);
        defaultMQPushConsumer.setConsumeThreadMax(320);
    }

    @Override
    public void onMessage(List<BetForPlaceResWsVo> betForPlaceResWsVos) {
        if(betForPlaceResWsVos == null){
            return;
        }

        log.info("::{}::,::{}::,::{}::位置货量-赛事Id={},playId={},消费数据={}", betForPlaceResWsVos.get(0).getLinkId(), betForPlaceResWsVos.get(0).getMatchId(), betForPlaceResWsVos.get(0).getPlayId(), JSONObject.toJSON(betForPlaceResWsVos));

        try {
            String msgId= UUID.randomUUID().toString();
            List<String> playIds = Arrays.asList(Integer.toString(betForPlaceResWsVos.get(0).getPlayId()));
            clientManageService.sendMessage(subscriptionEnums, Long.toString(betForPlaceResWsVos.get(0).getMatchId()), playIds, ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), betForPlaceResWsVos, 1, betForPlaceResWsVos.get(0).getLinkId(), msgId, null));
        } catch (Exception e){
            log.error("::{}::,::{}::,::{}::位置货量消费数据->{}，异常信息：", betForPlaceResWsVos.get(0).getLinkId(), betForPlaceResWsVos.get(0).getMatchId(), betForPlaceResWsVos.get(0).getPlayId(), betForPlaceResWsVos, e);
        }

    }
}
