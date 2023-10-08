package com.panda.sport.rcs.limit.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.limit.service.LimitServiceImpl;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfigSub;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author :  lithan
 * @Description :  盘口位置限额 更新消费
 * @Date: 2020年9月16日 10:42:51
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "TEMPLATE_MARKET_CONFIG_TOPIC",
        consumerGroup = "TEMPLATE_MARKET_CONFIG_TOPIC",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class MarketPlaceConsumer implements RocketMQListener<JSONObject>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    LimitServiceImpl limitService;

    public MarketPlaceConsumer() {
//        super("TEMPLATE_MARKET_CONFIG_TOPIC", "");
    }

    @Override
    public void onMessage(JSONObject data) {
        try {
            log.info("盘口位置限额收到更新 ：{}", JSONObject.toJSONString(data));
            Long matchId = data.getLong("matchId");
            Long playId = data.getLong("playId");
            String subPlayId = data.getString("subPlayId");
            Integer placeNum = data.getInteger("marketIndex");
            if (StringUtils.isBlank(subPlayId)) {
                subPlayId = playId.toString();
            }
            Map<String, Object> map = new HashMap<>();
            map.put("matchId", matchId);
            map.put("playId", playId);
            map.put("subPlayId", subPlayId);
            map.put("marketIndex", placeNum);
            map.put("dataType", -1);
            map.put("val", data.getLong("maxSingleBetAmount") * 100L);
            //通知sdk 清除缓存
            String mqKey = matchId + "_" + playId + "_-1";
            producerSendMessageUtils.sendMessage("rcs_limit_cache_clear_sdk,," + mqKey, map);
            log.info("盘口位置限额 通知sdk删除缓存 完成 ：{}", JSONObject.toJSONString(map));
        } catch (Exception e) {
            log.error("盘口位置限额MQ异常：{}{}", e.getMessage(), e);
        }
        return;
    }
}
