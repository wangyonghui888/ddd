package com.panda.sport.sdk.mq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.sdk.service.impl.OrderPaidApiImpl;
import com.panda.sport.sdk.util.GuiceContext;
import com.panda.sport.rcs.pojo.RcsMissedOrderConfigStatus;
import com.panda.sport.sdk.util.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.panda.sport.sdk.constant.RedisKeys.REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY;

/**
 * @author wiker
 * @date 2023/8/20 0:26
 * 2566动态漏单需求,商户配置相关信息通知变更消费类
 *
 **/
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "RCS_RISK_REDIS_CACHE_UPDATE_SYNC_INFO",
        consumerGroup = "RCS_RISK_MERCHANTSId_CONFIG",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class RcsMissedOrderConfigConsumer implements RocketMQListener<JSONObject>, RocketMQPushConsumerLifecycleListener {
    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(4);
        defaultMQPushConsumer.setConsumeThreadMax(32);
    }

    @Override
    public void onMessage(JSONObject message) {
        RcsMissedOrderConfigStatus orderConfigStatus = JSONObject.parseObject(message.toJSONString(), RcsMissedOrderConfigStatus.class);
        if ((ObjectUtils.isNotEmpty(orderConfigStatus) && orderConfigStatus.getMerchantIds().size() > 0)) {
            List<Long> merchantIdList = orderConfigStatus.getMerchantIds();
            for (Long merchantIds : merchantIdList) {
                RcsLocalCacheUtils.timedCache.remove(REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY + merchantIds);
            }
        }
        log.info("清理本地缓存商户旧的漏单配置数据");
    }
}

