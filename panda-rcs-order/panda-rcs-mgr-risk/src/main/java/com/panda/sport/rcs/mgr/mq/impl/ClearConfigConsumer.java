package com.panda.sport.rcs.mgr.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.api.BalanceValueService;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigMapper;
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

/**
 * @Description 清空一些配置的MQ消息处理
 * @Param
 * @Author Sean
 * @Date 10:48 2020/3/12
 * @return
 **/
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "MATCH_MARKET_CLEAR_CONFIG_TOPIC",
        consumerGroup = "MATCH_MARKET_CLEAR_CONFIG_TOPIC",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class ClearConfigConsumer implements RocketMQListener<JSONObject>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Autowired
    private RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;

    @Autowired
    private BalanceValueService balanceValueService;

    public ClearConfigConsumer() {
//        super(MQProducerConstants.MATCH_MARKET_CLEAR_CONFIG_TOPIC, null);
    }

    @Override
    public void onMessage(JSONObject jsonObject) {
        log.info("接收到清除盘口配置mq,实体bean{}", jsonObject.toJSONString());
        try {
//            Long marketId = jsonObject.getLong("marketId");
//            Long matchId = jsonObject.getLong("matchId");
//            balanceValueService.zeroBalanceValue(matchId, marketId);
//            //早盘切换到滚球 赔率累计变化需要清零
//            rcsMatchMarketConfigMapper.updateMarginChange(marketId, BigDecimal.ZERO);
        } catch (Exception e) {
            log.error("清除盘口配置报错{}",e.getMessage(), e);
        }
        return ;
    }
}
