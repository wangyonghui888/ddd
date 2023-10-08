package com.panda.rcs.order.reject.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.order.reject.constants.RedisKey;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 特殊事件开关监听
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "RCS_SPECIAL_EVENT_STATUS_SYNC",
        consumerGroup = "rcs_reject_special_event_info_group",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchEventSpecInfoConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(126);
    }

    private static final List<String> SPECEVENT = Arrays.asList("penalty_awarded", "breakaway", "dfk", "danger_ball");

    @Override
    public void onMessage(String message) {
        try {
            JSONObject map = JSON.parseObject(message);
            if (StringUtils.isNotBlank(map.getString("matchId"))) {
                log.info("linkId::{}::matchId:{},更新特殊事件缓存active信息:{}", map.getString("linkId"),map.getString("matchId"), message);
                String specAllKey = String.format(RedisKey.RCS_SPECIAL_EVENT_ALL_INFO, map.getString("matchId"));
                RcsLocalCacheUtils.timedCache.put(specAllKey, message, RedisKey.CACHE_TIME_OUT);
                for (String specEvent : SPECEVENT) {
                    String redisKey = String.format(RedisKey.RCS_SPECIAL_EVENT_INFO, map.getString("matchId"), specEvent);
                    String avtiveEvent = map.getString("active");
                    if (specEvent.equalsIgnoreCase(avtiveEvent)) {
                        RcsLocalCacheUtils.timedCache.put(redisKey, "1", RedisKey.CACHE_TIME_OUT);
                    } else {
                        RcsLocalCacheUtils.timedCache.put(redisKey, "0", RedisKey.CACHE_TIME_OUT);
                    }
                }
            }
        } catch (Exception e) {
            log.error("linkId::{}::matchId:{},更新特殊事件缓存信息异常{}", JSON.parseObject(message).getString("linkId"), JSON.parseObject(message).getString("matchId"),  e.getMessage());
        }
    }
}
