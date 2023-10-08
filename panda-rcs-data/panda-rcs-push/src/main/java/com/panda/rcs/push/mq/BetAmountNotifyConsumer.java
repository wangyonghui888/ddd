package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.cache.MatchInfoCache;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.MatchStatusEnums;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.MatchInfo;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.sport.rcs.pojo.vo.PredictOddsMqVo;
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

import java.util.*;

/**
 * @Description: 盘口位置货量推送
 * Topic=rcs_predict_odds_placeNum_ws
 * Group=RCS_PUSH_predict_odds_placeNum_ws_GROUP
 * 对应指令-> 30035
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "rcs_predict_odds_placeNum_ws",
        consumerGroup = "RCS_PUSH_predict_odds_placeNum_ws_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class BetAmountNotifyConsumer implements RocketMQListener<PredictOddsMqVo>, RocketMQPushConsumerLifecycleListener {

    private final static SubscriptionEnums subscriptionEnums = SubscriptionEnums.MARKET_PLACE_BET_AMOUNT;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(96);
        defaultMQPushConsumer.setConsumeThreadMax(196);
    }

    @Override
    public void onMessage(PredictOddsMqVo msg) {
        if(msg == null){
            return;
        }

        log.info("::{}::,::{}::,::{}::盘口位置货量-消费数据:{}", msg.getLinkId(), msg.getMatchId(), msg.getPlayId(), JSONObject.toJSON(msg));
        String msgId = UUID.randomUUID().toString();
        try {
            Object pushMessage = ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), msg, 0, msg.getLinkId(), msgId, null);

            //早盘进滚球 统一清理赛事货量
            if(msg.getPlayId() == null){
                clientManageService.sendMessage(subscriptionEnums, Long.toString(msg.getMatchId()), pushMessage);
                return;
            }

            MatchInfo matchInfo = MatchInfoCache.matchInfoMap.get(Long.toString(msg.getMatchId()));
            Integer matchType = msg.getMatchType() == 2 ? 1 : 0;
            if(matchInfo != null && matchInfo.getMatchStatus().equals(MatchStatusEnums.MATCH_STATUS_LIVE.getKey()) && !matchInfo.getMatchStatus().equals(matchType)){
                log.info("::{}::,::{}::,::{}::赛事状态不对应，不推送", msg.getLinkId(), msg.getMatchId(), msg.getPlayId());
                return;
            }

            List<String> playIds = new ArrayList<>();
            playIds.add(Long.toString(msg.getPlayId()));

            clientManageService.sendMessage(subscriptionEnums, Long.toString(msg.getMatchId()), playIds, pushMessage);
        } catch (Exception e){
            log.error("::{}::,::{}::,::{}::盘口位置货量推送-消费数据:{}，异常信息:", msg.getLinkId(), msg.getMatchId(), msg.getPlayId(), msg, e);
        }
    }
}
