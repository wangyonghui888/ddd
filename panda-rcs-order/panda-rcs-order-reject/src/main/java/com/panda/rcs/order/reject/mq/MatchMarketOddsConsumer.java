package com.panda.rcs.order.reject.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMatchMarketMessage;
import com.panda.rcs.order.reject.constants.RedisKey;
import com.panda.rcs.order.reject.entity.DataRealTimeMessageBean;
import com.panda.rcs.order.reject.entity.StandardMarketMessageDto;
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
import org.springframework.util.StopWatch;

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
        consumerGroup = "rcs_reject_standard_market_odds_group",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchMarketOddsConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {
    @Override
    public void onMessage(String message) {
        StopWatch sw = new StopWatch();
        sw.start();
        try {
            DataRealTimeMessageBean<StandardMatchMarketMessage> msg = JSON.parseObject(message, new TypeReference<DataRealTimeMessageBean<StandardMatchMarketMessage>>() {
            });
            /*心跳数据，不做处理*/
            if (msg != null && msg.getLinkId().endsWith("HeartBeat")) {
                return;
            }
            StandardMatchMarketMessage matchMessage = msg.getData();
            if (matchMessage.getStandardMatchInfoId() == null) {
                return;
            }
            //1666需求 缓存赛事数据
            String matchKey = String.format(RedisKey.REDIS_MATCH_INFO, matchMessage.getStandardMatchInfoId());
            RcsLocalCacheUtils.timedCache.put(matchKey, JSON.toJSONString(matchMessage), RedisKey.ODDS_CACHE_TIME_OUT);
            //缓存盘口数据
            List<StandardMarketMessage> marketList = matchMessage.getMarketList();
            if (!CollectionUtils.isEmpty(marketList)) {
                Map<Long, List<StandardMarketMessage>> map = marketList.stream().collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));
                for (Long playId : map.keySet()) {
                    if (map.get(playId).get(0).getMarketType() == 1) {//如果是早盘，不缓存
//                        log.info("linkId::{}::,赛事ID:{},玩法ID:{},监听赔率到本地缓存-早盘不缓存", msg.getLinkId(), matchMessage.getStandardMatchInfoId(), playId);
                    } else {//如果是滚球，缓存到本地
                        List<StandardMarketMessageDto> cacheList = JSON.parseArray(JSON.toJSONString(map.get(playId)), StandardMarketMessageDto.class);
                        String key = String.format(RedisKey.REDIS_MATCH_MARKET_ODDS_NEW, playId, matchMessage.getStandardMatchInfoId());
                        RcsLocalCacheUtils.timedCache.put(key, JSON.toJSONString(cacheList), RedisKey.ODDS_CACHE_TIME_OUT);
//                        log.info("linkId::{}::,赛事ID:{},玩法ID:{},监听赔率到本地缓存-滚球", msg.getLinkId(), matchMessage.getStandardMatchInfoId(), playId);
                    }
                }
            }
            sw.stop();
//            log.info("linkId::{}::,赛事ID:{},监听赔率到本地缓存-耗时：{}毫秒", msg.getLinkId(), matchMessage.getStandardMatchInfoId(),sw.getTotalTimeMillis());
        } catch (Exception e) {
            sw.stop();
            log.error("linkId::{}::,赛事ID:{},监听赔率到本地缓存MatchMarketOddsConsumer异常,耗时{}", JSON.parseObject(message).getString("linkId"), "-1" ,sw.getTotalTimeMillis(),e);
        }
    }


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(126);
    }
}
