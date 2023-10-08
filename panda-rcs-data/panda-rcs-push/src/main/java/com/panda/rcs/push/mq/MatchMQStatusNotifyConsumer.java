package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.rcs.push.cache.MatchInfoCache;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.MatchStatusEnums;
import com.panda.rcs.push.entity.enums.SportEnum;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.ClientResponseVo;
import com.panda.rcs.push.entity.vo.MatchInfo;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.pojo.dto.StandardMatchStatusDTO;
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

import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * @Description: 赛事状态
 * Topic=STANDARD_MATCH_STATUS
 * Group=RCS_PUSH_STANDARD_MATCH_STATUS_GROUP
 * 对应指令 -> 30001
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "STANDARD_MATCH_STATUS",
        consumerGroup = "RCS_PUSH_STANDARD_MATCH_STATUS_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchMQStatusNotifyConsumer implements RocketMQListener<Request<StandardMatchStatusDTO>>, RocketMQPushConsumerLifecycleListener {

    private final static SubscriptionEnums subscriptionEnums = SubscriptionEnums.MATCH_STATUS;

    @Autowired
    private ClientManageService clientManageService;

    @Autowired
    private RedisClient redisClient;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(32);
        defaultMQPushConsumer.setConsumeThreadMax(64);
    }

    @Override
    public void onMessage(Request<StandardMatchStatusDTO> standardMatchStatusDTORequest) {
        if(standardMatchStatusDTORequest == null){
            return;
        }

        StandardMatchStatusDTO dto = standardMatchStatusDTORequest.getData();
        log.info("::{}::,::{}::消费数据->{}", standardMatchStatusDTORequest.getLinkId(), dto.getStandardMatchId(),standardMatchStatusDTORequest);
        SubscriptionEnums subscriptionEnums = SubscriptionEnums.getSubscriptionEnums(SubscriptionEnums.MATCH_STATUS.getKey());

        try {
            String matchId = Long.toString(standardMatchStatusDTORequest.getData().getStandardMatchId());
            ClientResponseVo responseContext = ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), standardMatchStatusDTORequest, 0, standardMatchStatusDTORequest.getLinkId(), standardMatchStatusDTORequest.getLinkId(), null);

            if(Objects.nonNull(dto.getSportId())){
                if(SportEnum.SPORT_FOOTBALL.getKey() == dto.getSportId().intValue() && MatchStatusEnums.MATCH_STATUS_LIVE.getKey() == dto.getMatchStatus()){
                    MatchInfo matchInfo = MatchInfoCache.matchInfoMap.get(matchId);
                    if(matchInfo == null){
                        matchInfo = new MatchInfo();
                        matchInfo.setMatchId(matchId);
                        matchInfo.setMatchStatus(dto.getMatchStatus());
                        matchInfo.setCreateTime(System.currentTimeMillis());
                    }

                    matchInfo.setMatchStatus(dto.getMatchStatus());
                    MatchInfoCache.matchInfoMap.put(matchId, matchInfo);
                }
            }
            clientManageService.sendMessage(subscriptionEnums, matchId, responseContext);
        } catch (Exception e){
            log.error("::{}::赛事状态消费数据->{}，异常信息：", standardMatchStatusDTORequest.getLinkId(), JSONObject.toJSONString(standardMatchStatusDTORequest), e);
        }

    }
}
