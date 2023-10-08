package com.panda.rcs.order.reject.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.rcs.order.reject.constants.RedisKey;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.rcs.order.reject.mq
 * @Description :  监听赛事阶段到本地
 * @Date: 2023-02-07 16:24
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@RocketMQMessageListener(
        topic = "MATCH_EVENT_INFO",
        consumerGroup = "rcs_reject_match_event_info_group",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchEventInfoConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {
    @Override
    public void onMessage(String message) {
        Request<List<MatchEventInfoMessage>> requests = JSON.parseObject(message, new TypeReference<Request<List<MatchEventInfoMessage>>>() {
        });
        List<MatchEventInfoMessage> matchEventInfoMessages = requests.getData();
        if (!CollectionUtils.isEmpty(matchEventInfoMessages)) {
            for (MatchEventInfoMessage matchEventInfoMessage : matchEventInfoMessages) {
                String key = String.format(RedisKey.REDIS_MATCH_PERIOD, matchEventInfoMessage.getStandardMatchId());
                log.info("::{}::下发的阶段信息:{}",matchEventInfoMessage.getStandardMatchId(),matchEventInfoMessage.getMatchPeriodId());
                RcsLocalCacheUtils.timedCache.put(key, String.valueOf(matchEventInfoMessage.getMatchPeriodId()), RedisKey.CACHE_TIME_OUT);
            }
        }
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(126);
    }
}
