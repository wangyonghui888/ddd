package com.panda.rcs.pending.order.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMatchMarketMessage;
import com.panda.rcs.pending.order.constants.RedisKey;
import com.panda.rcs.pending.order.pojo.DataRealTimeMessageBean;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 监听赔率到本地缓存
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "STANDARD_MARKET_ODDS",
        consumerGroup = "rcs_pending_order_standard_market_odds_group",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchMarketOddsConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {
    @Override
    public void onMessage(String message) {
        try {
            DataRealTimeMessageBean<StandardMatchMarketMessage> msg = JSON.parseObject(message, new TypeReference<DataRealTimeMessageBean<StandardMatchMarketMessage>>() {
            });
            StandardMatchMarketMessage matchMessage = msg.getData();
            if (matchMessage.getStandardMatchInfoId() == null) {
                return;
            }
            //缓存盘口数据
            List<StandardMarketMessage> marketList = matchMessage.getMarketList();
            if (!CollectionUtils.isEmpty(marketList)) {
                Map<Long, List<StandardMarketMessage>> map = marketList.stream().collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));
                for (Long playId : map.keySet()) {
                    List<StandardMarketMessage> standardMarketMessages = map.get(playId);
                    List<com.panda.rcs.pending.order.pojo.StandardMarketMessage> cacheList = JSON.parseArray(JSON.toJSONString(standardMarketMessages), com.panda.rcs.pending.order.pojo.StandardMarketMessage.class);
                    String key = String.format(RedisKey.REDIS_MATCH_MARKET_ODDS_NEW, playId, matchMessage.getStandardMatchInfoId());
                    RcsLocalCacheUtils.timedCache.put(key, JSON.toJSONString(cacheList), 10 * 60 * 1000L);
                }
            }
        } catch (Exception e) {
            log.error("监听赔率到本地缓存 - MatchMarketOddsConsumer处异常：{}", e.getMessage(), e);
        }
    }


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(126);
    }
}
