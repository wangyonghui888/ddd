
package com.panda.sport.rcs.trade.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.RedisCacheSyncBean;
import com.panda.sport.rcs.core.cache.client.RedisClient;
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


/**
 * 因为trade服务与risk服务这边进行redis集群拆分，所有遂于一些redis缓存数据需要进行同步，当trade发生变更需要同步发送mq在此处进行同步del或者set
 * 注意： 如果有本地缓存的需要额外在各自服务在进行特殊处理
 *
 * @Param
 * @Author Magic
 * @Date 14:00 2023/01/10
 * @return
 **/

@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "RCS_TRADE_REDIS_CACHE_SYNC",
        consumerGroup = "RCS_TRADE_REDIS_CACHE_SYNC_TOPIC",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class RedisCacheSyncConfigConsumer implements RocketMQListener<RedisCacheSyncBean>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Autowired
    public RedisClient redisClient;


    /**
     * RedisCacheSyncBean.build("tag_config","key");删除
     * RedisCacheSyncBean.build("tag_config","key","value",100L)重置
     *
     * @param bean
     */

    @Override
    public void onMessage(RedisCacheSyncBean bean) {
        log.info("redis缓存同步,接收到数据{}", JSONObject.toJSONString(bean));
        try {
            if (StringUtils.isNotBlank(bean.getSubKey())) {
                //hashkey 处理
                if (StringUtils.isNotBlank(bean.getValue())) {
                    //如果有value 直接进行set覆盖 否者del
                    redisClient.hSet(bean.getKey(), bean.getSubKey(), bean.getValue());
                    redisClient.expireKey(bean.getKey(), bean.getExpiry().intValue());
                } else {
                    redisClient.hashRemove(bean.getKey(), bean.getSubKey());
                }
            } else {
                if (StringUtils.isNotBlank(bean.getValue())) {
                    //如果有value 直接进行set覆盖 否者del
                    redisClient.setExpiry(bean.getKey(), bean.getValue(), bean.getExpiry());
                } else {
                    redisClient.delete(bean.getKey());
                }
            }
        } catch (Exception e) {
            log.error("redis缓存同步异常:{}", JSONObject.toJSONString(bean), e);
        }
    }
}

