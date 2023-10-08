package com.panda.rcs.pending.order.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.pending.order.constants.RedisKey;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
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
 * 赛事盘口结算状态 consumer
 * 接到赛事盘口状态信息,都是盘口已结算的盘口信息
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "OSMC_MARKET_RESULT",
        consumerGroup = "OSMC_MARKET_RESULT_GROUPS",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchMarketSettleConsumer implements RocketMQListener<Object>, RocketMQPushConsumerLifecycleListener {
    private static List<Long> SPORT_ID_LIST = Arrays.asList(1L, 2L);

    @Override
    public void onMessage(Object obj) {
        log.info("::::赛事盘口结算消息消费者::::消息数据->{}", obj);
        if (obj == null) {
            return;
        }
        JSONObject jsonObj = JSONObject.parseObject(JSON.toJSONString(obj));
        Long sportId = jsonObj.getLong("sportId");
        Long matchId = jsonObj.getLong("matchId");
        Long marketId = jsonObj.getLong("marketId");
        JSONArray array = jsonObj.getJSONArray("marketOptionsResults");
        if (SPORT_ID_LIST.contains(sportId)) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject json = array.getJSONObject(i);
                Long optionsId = json.getLong("optionsId");
                String key = RedisKey.getMatchMarketSettlement(sportId, matchId, marketId, optionsId);
                RcsLocalCacheUtils.timedCache.put(key, 1, 4 * 60 * 60 * 1000L);
            }
        }

    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(32);
        consumer.setConsumeThreadMax(64);
    }
}
