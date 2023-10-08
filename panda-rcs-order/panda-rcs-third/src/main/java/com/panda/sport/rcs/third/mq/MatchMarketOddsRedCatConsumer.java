package com.panda.sport.rcs.third.mq;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMatchMarketMessage;
import com.panda.sport.rcs.constants.CommonConstants;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.OrderTypeEnum;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.third.entity.common.DataRealTimeMessageBean;
import com.panda.sport.rcs.third.entity.common.pojo.StandardMarketVo;
import com.panda.sport.rcs.third.entity.common.pojo.StandardMatchVo;
import com.panda.sport.rcs.third.entity.redCat.RedCatSelectionData;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @author vere
 * @date 2023/6/07
 * @description 监听红猫赛事信息，盘口，赔率变动 广播到本地
 * @version 1.0.0
 */

@Component
@Slf4j
@RocketMQMessageListener(
        topic = "REDCAT_MARKET_ODDS",
        consumerGroup = "rcs_risk_redCat_market_odds_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchMarketOddsRedCatConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {
    @Autowired
    ProducerSendMessageUtils sendMessage;
    @Autowired
    RedisClient redisClient;
    @Override
    public void onMessage(String message) {
        //处理数据
        handlerData(message);

    }
    private static final List<String> list = new ArrayList<String>() {{
        add(OrderTypeEnum.REDCAT.getDataSource());
    }};
    private static final Long REDIS_EXPIRED_TIME=10 * 60L;
    /**
     * 处理数据
     * @param message
     */
    public void handlerData(String message){

        String tag = "redCatSelection";
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
            //缓存盘口数据
            List<StandardMarketMessage> marketList = matchMessage.getMarketList();
            if (!CollectionUtils.isEmpty(marketList)) {
                //投注项存储
                marketList.forEach(market->{
                    if (CollectionUtils.isEmpty(market.getMarketOddsList())) {
                        //赔率不存在
                        return;
                    }
                    market.getMarketOddsList().forEach(odd->{
                        try {
                            String key=String.format(REDIS_RED_CAT_SELECTION_ID_KEY,odd.getId());
                            redisClient.setExpiry(key, odd.getThirdOddsFieldSourceId(), REDIS_EXPIRED_TIME);
                        } catch (Exception ex) {
                            log.error("::{}::处理红猫盘口赔率下发mq失败，直接返回",msg.getLinkId(),ex);
                        }
                    });
                });
            }
            sendMessage.sendMessage(RCS_RISK_THIRD_RED_CAT_SELECTION_TOPIC, tag,msg.getLinkId(), msg);
        } catch (Exception ex) {
            log.error("::REDCAT_MARKET_ODDS::消费红猫赛事赔率失败,失败原因:{}",ex.getMessage(),ex);

        }
    }
    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }
    public String getMessage(){
        String a="";
        return a;
    }
}
