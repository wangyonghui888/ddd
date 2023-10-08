package com.panda.rcs.order.reject.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.LocalCacheSyncBean;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * 因为trade服务与risk服务这边进行redis集群拆分，有需要在risk服务中使用到的一些redis将以RPC的方式进行提供，广播保存到每一个节点中
 *
 * @Param
 * @Author Waldkir
 * @Date 14:00 2023/03/01
 * @return
 **/

@Component
@Slf4j
@RocketMQMessageListener(
        topic = "RCS_TRADE_LOCAL_CACHE_SYNC",
        consumerGroup = "RCS_REJECT_LOCAL_CACHE_SYNC_TOPIC",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class LocalCacheSyncConfigConsumer implements RocketMQListener<LocalCacheSyncBean>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Override
    public void onMessage(LocalCacheSyncBean bean) {
        log.info("本地local缓存同步,接收到数据:{}", JSONObject.toJSONString(bean));
        try {
            if (!ObjectUtils.isEmpty(bean.getValue())) {
                log.info("本地local缓存同步,保存成功,key为:{},过期时间为:{}", bean.getKey(), bean.getExpiry());
                RcsLocalCacheUtils.timedCache.put(bean.getKey(), bean.getValue(), bean.getExpiry());
            } else {
                //删除缓存
                log.info("本地local缓存同步删除,key为:{}", bean.getKey());
                RcsLocalCacheUtils.timedCache.remove(bean.getKey());
            }
        } catch (Exception e) {
            log.error("本地local缓存同步,出现异常:{}", JSONObject.toJSONString(bean), e);
        }
    }
}