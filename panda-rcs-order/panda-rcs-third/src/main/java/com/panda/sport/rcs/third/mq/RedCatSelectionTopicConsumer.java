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
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.constants.RedisKey.REDIS_MATCH_INFO;
import static com.panda.sport.rcs.constants.RedisKey.REDIS_MATCH_MARKET_ODDS_NEW;
import static com.panda.sport.rcs.third.common.Constants.RCS_RISK_THIRD_RED_CAT_SELECTION_TOPIC;
import static com.panda.sport.rcs.third.common.Constants.REDIS_RED_CAT_SELECTION_ID_KEY;

/**
 * 红猫盘口赔率缓存消费
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = RCS_RISK_THIRD_RED_CAT_SELECTION_TOPIC,
        consumerGroup = "rcs_red_cat_selection_group",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class RedCatSelectionTopicConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {
    private static final Long LOCAL_EXPIRED_TIME=10*60*1000L;

    private static final List<String> list = new ArrayList<String>() {{
        add(OrderTypeEnum.REDCAT.getDataSource());
    }};

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Override
    public void onMessage(String message) {
        try {
            DataRealTimeMessageBean<StandardMatchMarketMessage> msg = JSON.parseObject(message, new TypeReference<DataRealTimeMessageBean<StandardMatchMarketMessage>>() {
            });
            StandardMatchMarketMessage matchMessage = msg.getData();
            if (matchMessage.getStandardMatchInfoId() == null || !list.contains(matchMessage.getDataSourceCode())) {
                log.info("::{}::获取红猫盘口赔率下发mq，标准赛事id为空，直接返回",msg.getLinkId());
                return;
            }
            StandardMatchVo matchInfo = new StandardMatchVo();
            BeanUtils.copyProperties(matchMessage, matchInfo);
            String matchKey = String.format(REDIS_MATCH_INFO, matchInfo.getStandardMatchInfoId());
            //缓存赛事数据
            RcsLocalCacheUtils.timedCache.put(matchKey, JSON.toJSONString(matchInfo), LOCAL_EXPIRED_TIME);
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
                        RcsLocalCacheUtils.timedCache.put(key, JSON.toJSONString(cacheList), LOCAL_EXPIRED_TIME);
                    }
                }
                //投注项存储
                marketList.forEach(market->{
                    if (CollectionUtils.isEmpty(market.getMarketOddsList())) {
                        //赔率不存在
                        return;
                    }
                    market.getMarketOddsList().forEach(odd->{
                        try {
                            String key=String.format(REDIS_RED_CAT_SELECTION_ID_KEY,odd.getId());
                            RcsLocalCacheUtils.timedCache.put(key,odd.getThirdOddsFieldSourceId(), LOCAL_EXPIRED_TIME);
                        } catch (Exception ex) {
                            log.error("::{}::处理红猫盘口赔率下发mq失败，直接返回",msg.getLinkId(),ex);
                        }
                    });
                });
            }
        } catch (Exception ex) {
            log.error("::REDCAT_MARKET_ODDS::消费红猫赛事赔率失败,失败原因:{}",ex.getMessage(),ex);
        }
    }
}
