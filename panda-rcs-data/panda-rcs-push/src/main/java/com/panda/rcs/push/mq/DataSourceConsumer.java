package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.ClientResponseVo;
import com.panda.rcs.push.entity.vo.MatchStatusAndDataSuorceVo;
import com.panda.rcs.push.utils.ClientResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Description: 推送赛事级别、玩法集状态、带X玩法总玩法状态盘口状态、操盘模式/mts与pa操盘切换
 * Topic=DATA_SOURCE_DURING_GAME_PLAY_TOPIC  DATA_SOURCE_DURING_GAME_PLAY_TAG
 * Group=RCS_PUSH_DATA_SOURCE_DURING_GAME_PLAY_TAG_GROUP
 * 对应指令-> 30012
 * message:
 * 玩法集开关封锁：{"categoryIdList":[224,138,139,140,306,307,308,309,310,311,312,313,314,315,316,317,318,319,320,321,322,323],"level":9,"matchId":1679635,"playSetCode":"FOOTBALL_PENALTY_CARD","sportId":1,"status":1}
 * 水差关联：{"matchId":1614716,"playId":39,"relevanceType":0,"sportId":2}
 * 赛事级别开关封锁：
 * mts与pa互换：
 * 带X玩法开关封锁：
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "DATA_SOURCE_DURING_GAME_PLAY_TOPIC",
        selectorExpression = "DATA_SOURCE_DURING_GAME_PLAY_TAG",
        consumerGroup = "RCS_PUSH_DATA_SOURCE_DURING_GAME_PLAY_TAG_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY,
        selectorType = SelectorType.TAG)
public class DataSourceConsumer implements RocketMQListener<MatchStatusAndDataSuorceVo>, RocketMQPushConsumerLifecycleListener {

    private final static SubscriptionEnums subscriptionEnums = SubscriptionEnums.MATCH_TRADER_STATUS;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(32);
        defaultMQPushConsumer.setConsumeThreadMax(64);
    }

    @Override
    public void onMessage(MatchStatusAndDataSuorceVo matchStatusAndDataSuorceVo) {
        if(matchStatusAndDataSuorceVo == null){
            return;
        }
        String msgId = UUID.randomUUID().toString();
        log.info("::{}::,::{}::赛事相关状态消费数据->{}", matchStatusAndDataSuorceVo.getLinkId(), matchStatusAndDataSuorceVo.getMatchId(), JSONObject.toJSON(matchStatusAndDataSuorceVo));

        try {
            String matchId = Long.toString(matchStatusAndDataSuorceVo.getMatchId());

            ClientResponseVo responseContext = ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), matchStatusAndDataSuorceVo, 0, null, msgId, null);
            List<String> playIds = new ArrayList<>();

            //玩法级别开关封锁推送
            if(matchStatusAndDataSuorceVo.getCategoryIdList() != null){
                matchStatusAndDataSuorceVo.getCategoryIdList().forEach(play -> {
                    playIds.add(Long.toString(play));
                });

                clientManageService.sendMessage(subscriptionEnums, matchId, playIds, responseContext);
                return;
            }

            //玩法开关封锁推送
            if(matchStatusAndDataSuorceVo.getPlayId() != null){
                playIds.add(Long.toString(matchStatusAndDataSuorceVo.getPlayId()));
                clientManageService.sendMessage(subscriptionEnums, matchId, playIds, responseContext);
                return;
            }

            //带X玩法开关封锁推送
            if(matchStatusAndDataSuorceVo.getMainPlayStatusMap() != null){
                matchStatusAndDataSuorceVo.getMainPlayStatusMap().forEach((k, v) -> {
                    playIds.add(k);
                });
                clientManageService.sendMessage(subscriptionEnums, matchId, playIds, responseContext);
                return;
            }

            //单个玩法操盘模式切换
            if(matchStatusAndDataSuorceVo.getLevel() != null && matchStatusAndDataSuorceVo.getLevel() == 2 && matchStatusAndDataSuorceVo.getDataSource() != null){
                playIds.add(matchStatusAndDataSuorceVo.getId());
                clientManageService.sendMessage(subscriptionEnums, matchId, playIds, responseContext);
                return;
            }

            // 货量出涨预警标志
            if (matchStatusAndDataSuorceVo.getFunction() != null && matchStatusAndDataSuorceVo.getFunction() == 121) {
                clientManageService.sendMessage(subscriptionEnums, matchId, responseContext);
                return;
            }

            //赛事维度开关封锁/mts、pa互换
            clientManageService.sendMessage(subscriptionEnums, matchId, responseContext);
        } catch (Exception e){
            log.error("::{}::,::{}::赛事相关状态消费数据->{},异常信息：", matchStatusAndDataSuorceVo.getLinkId(), matchStatusAndDataSuorceVo.getMatchId(), matchStatusAndDataSuorceVo, e);
        }
    }
}
