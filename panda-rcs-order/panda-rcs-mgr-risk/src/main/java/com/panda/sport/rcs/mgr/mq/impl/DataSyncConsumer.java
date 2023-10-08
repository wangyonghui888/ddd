package com.panda.sport.rcs.mgr.mq.impl;


import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.panda.sport.rcs.constants.RedisKeys.RCS_MARKET_MAX_AMOUNT;
import static com.panda.sport.rcs.constants.RedisKeys.RCS_TOURNAMENT_MAX_AMOUNT;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.mq.impl
 * @Description :  redis 数据更新 管理后台更新数据以后 实时同步到redis
 * @Date: 2019-12-31 17:09
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "queue_redis_sync",
        consumerGroup = "queue_redis_sync",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class DataSyncConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }
    @Autowired
    public RedisClient redisClient;

    public DataSyncConsumer(@Value("${rocketmq.redis.data.sync}") String consumerConfig) {
//        super(consumerConfig, "rocketmq.redis.data.sync");
    }

    @Override
    public void onMessage(String redis_sync_type) {
        log.info("{} DataSyncConsumer 收到bean info ：{}", this.getClass(), redis_sync_type);

        String redisKey ;
        if(redis_sync_type.equalsIgnoreCase(RCS_MARKET_MAX_AMOUNT)){
            redisKey = String.format(RCS_MARKET_MAX_AMOUNT,"*","*","*");
            redisClient.delete(redisKey);

            redisKey = String.format(RedisKeys.RCS_MATCH_MARKET_CONFIG,"*","*","*","*");
            redisClient.delete(redisKey);
        }
        if(redis_sync_type.equalsIgnoreCase(RCS_TOURNAMENT_MAX_AMOUNT)){
            redisKey = String.format(RCS_TOURNAMENT_MAX_AMOUNT,"*","*");
            redisClient.delete(redisKey);
        }

       /* if(item.getRemark().equalsIgnoreCase("")){
            String stopKey = String.format(RedisKeys.PAID_DATE_BUS_STOP_REDIS_CACHE, item.getDateExpect(), "1");
            redisClient.delete(stopKey);
        }*/
        return  ;
    }
}