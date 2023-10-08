package com.panda.rcs.pending.order.mq;

import com.alibaba.fastjson.JSON;
import com.panda.rcs.pending.order.constants.RedisKey;
import com.panda.sport.rcs.pojo.vo.PeningOrderCacheClearVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Describtion
 * @Auther jstyDC
 * @Date 2022-04-2022/4/29 10:44
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "PENDING_ORDER_DELETECACHE",
        consumerGroup = "RCS_PENDING_ORDER_DELETECACHE_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class PendingOrderConsumer implements RocketMQListener<PeningOrderCacheClearVo>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private RedisClient redisClient;


    @Override
    public void onMessage(PeningOrderCacheClearVo peningOrderCacheClearVo) {
        try {
            log.info("::::预约投注缓存删除通知::::消费模板Id->{}", JSON.toJSONString(peningOrderCacheClearVo));
            if (Objects.nonNull(peningOrderCacheClearVo)) {
                if (StringUtils.equals(peningOrderCacheClearVo.getTemplate(), "template")) {
                    String key = String.format(RedisKey.PENDING_ORDER_KEY, peningOrderCacheClearVo.getTypeVal(), peningOrderCacheClearVo.getMatchType());
                    redisClient.delete(key);
                    String key1 = String.format(RedisKey.PENDING_ORDER_LIMIT_KEY, peningOrderCacheClearVo.getTypeVal(), peningOrderCacheClearVo.getMatchType());
                    redisClient.delete(key1);
                    log.info("删除模板缓存结束");
                }
                if (StringUtils.equals(peningOrderCacheClearVo.getMarginRef(), "marginRef")) {
                    String key = String.format(RedisKey.PENDING_ORDER_MARGIN_REF_KEY, peningOrderCacheClearVo.getMatchId(), peningOrderCacheClearVo.getSportId(), peningOrderCacheClearVo.getPlayId());
                    redisClient.delete(key);
                    log.info("删除分时节点缓存结束");
                }
            }
        } catch (Exception e) {
            log.error("预约投注缓存清理 MQ异常：{}{}", e.getMessage(), e);
        }
        return;

    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(32);
        consumer.setConsumeThreadMax(64);
    }
}
