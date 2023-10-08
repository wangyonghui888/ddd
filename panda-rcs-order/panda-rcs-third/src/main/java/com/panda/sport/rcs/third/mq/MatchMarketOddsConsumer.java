package com.panda.sport.rcs.third.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMatchMarketMessage;
import com.panda.sport.rcs.enums.OrderTypeEnum;
import com.panda.sport.rcs.third.entity.common.DataRealTimeMessageBean;
import com.panda.sport.rcs.third.entity.common.pojo.StandardMarketVo;
import com.panda.sport.rcs.third.entity.common.pojo.StandardMatchVo;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.constants.RedisKey.REDIS_MATCH_INFO;
import static com.panda.sport.rcs.constants.RedisKey.REDIS_MATCH_MARKET_ODDS_NEW;
import static com.panda.sport.rcs.third.common.Constants.LINKID;

/**
 * @author Beulah
 * @date 2023/4/1 20:24
 * @description 监听赛事信息，盘口，赔率变动 广播到本地
 */

@Component
@Slf4j
@RocketMQMessageListener(
        topic = "STANDARD_MARKET_ODDS",
        consumerGroup = "rcs_risk_third_market_odds_group",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchMarketOddsConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    private static final List<String> list = new ArrayList<String>() {{
        add(OrderTypeEnum.GTS.getDataSource());
        add(OrderTypeEnum.CTS.getDataSource());
    }};


    @Override
    public void onMessage(String message) {
        try {
            DataRealTimeMessageBean<StandardMatchMarketMessage> msg = JSON.parseObject(message, new TypeReference<DataRealTimeMessageBean<StandardMatchMarketMessage>>() {
            });
            if(msg.getLinkId()!=null && msg.getLinkId().endsWith("HeartBeat")){
                //过滤心跳数据
                return;
            }
            MDC.put(LINKID,msg.getLinkId());
            StandardMatchMarketMessage matchMessage = msg.getData();
            if (matchMessage.getStandardMatchInfoId() == null || !list.contains(matchMessage.getDataSourceCode())) {
                return;
            }
            //log.info("::::监听赛事,盘口数据到本地开始::::");
            StandardMatchVo matchInfo = new StandardMatchVo();
            BeanUtils.copyProperties(matchMessage, matchInfo);
            String matchKey = String.format(REDIS_MATCH_INFO, matchInfo.getStandardMatchInfoId());
            //缓存赛事数据
            RcsLocalCacheUtils.timedCache.put(matchKey, JSON.toJSONString(matchInfo), 10 * 60 * 1000L);
            //缓存盘口数据
            List<StandardMarketMessage> marketList = matchMessage.getMarketList();
            if (!CollectionUtils.isEmpty(marketList)) {
                Map<Long, List<StandardMarketMessage>> map = marketList.stream().collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));
                for (Long playId : map.keySet()) {
                    if (map.get(playId).get(0).getMarketType() == 1) {//如果是早盘，不缓存
                        //log.info("linkId::{}::,赛事ID:{},玩法ID:{},监听赔率到本地缓存-早盘不缓存", msg.getLinkId(), matchMessage.getStandardMatchInfoId(), playId);
                    } else {//如果是滚球，缓存到本地
                        List<StandardMarketMessage> standardMarket = map.get(playId);
                        List<StandardMarketVo> cacheList = JSON.parseArray(JSON.toJSONString(standardMarket), StandardMarketVo.class);
                        String key = String.format(REDIS_MATCH_MARKET_ODDS_NEW, playId, matchMessage.getStandardMatchInfoId());
                        RcsLocalCacheUtils.timedCache.put(key, JSON.toJSONString(cacheList), 10 * 60 * 1000L);
                    }
                }
            }
        } catch (Exception e) {
            log.error("监听赔率到本地缓存异常：{}", message);
        }finally {
            MDC.remove(LINKID);
        }
    }


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }
}
