package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.cache.MatchInfoCache;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.MatchStatusEnums;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.MatchInfo;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.sport.rcs.pojo.vo.ForecastMqVo;
import com.panda.sport.rcs.pojo.vo.PredictForecastVo;
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
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 货量forecast推送消费
 * Topic=rcs_predict_forecast_play_mongo
 * Group=RCS_PUSH_predict_forecast_play_mongo_GROUP
 * 对应指令-> 30036
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "rcs_predict_forecast_play_mongo",
        consumerGroup = "RCS_PUSH_predict_forecast_play_mongo_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class ForecastConsumer implements RocketMQListener<ForecastMqVo>, RocketMQPushConsumerLifecycleListener {

    private final static SubscriptionEnums subscriptionEnums = SubscriptionEnums.FOOTBALL_FORECAST;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(96);
        defaultMQPushConsumer.setConsumeThreadMax(196);
    }

    @Override
    public void onMessage(ForecastMqVo vo) {
        if (vo == null) {
            return;
        }
        log.info("::{}::,::{}::,::{}::足球货量Forecast-消费数据->{}", vo.getLinkId(), vo.getMatchId(), vo.getPlayId(), JSONObject.toJSON(vo));

        try {
            if (CollectionUtils.isEmpty(vo.getList())) {
                return;
            }

            MatchInfo matchInfo = MatchInfoCache.matchInfoMap.get(Long.toString(vo.getMatchId()));
            Integer matchType = vo.getMatchType() == 2 ? 1 : 0;
            if(matchInfo != null && matchInfo.getMatchStatus().equals(MatchStatusEnums.MATCH_STATUS_LIVE.getKey()) && !matchInfo.getMatchStatus().equals(matchType)){
                log.info("::{}::足球货量Forecast-matchId={}，赛事状态不对应，不推送", vo.getLinkId(), vo.getMatchId());
                return;
            }

            List<PredictForecastVo> forecast = null;
            if (Arrays.asList(2L, 18L, 114L, 122L, 127L, 332L, 134L, 240L, 307L, 309L).contains(vo.getPlayId())) {
                forecast = vo.getList().stream().sorted(Comparator.comparing(PredictForecastVo::getScore)).collect(Collectors.toList());
            } else {
                forecast = vo.getList().stream().sorted(Comparator.comparing(PredictForecastVo::getScore).reversed()).collect(Collectors.toList());
            }

            vo.setList(forecast);
            List<String> playIds = new ArrayList<>();
            playIds.add(Long.toString(vo.getPlayId()));
            String msgId = UUID.randomUUID().toString();

            clientManageService.sendMessage(subscriptionEnums, Long.toString(vo.getMatchId()),playIds , ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), vo, 1, vo.getLinkId(), msgId, null));
        } catch (Exception e){
            log.error("::{}::,::{}::,::{}::足球货量-消费数据->{}，异常信息：", vo.getLinkId(), vo.getMatchId(), vo.getPlayId(), vo, e);
        }
    }
}
