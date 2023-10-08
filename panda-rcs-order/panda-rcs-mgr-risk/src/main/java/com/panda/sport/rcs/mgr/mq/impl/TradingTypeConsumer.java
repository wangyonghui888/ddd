package com.panda.sport.rcs.mgr.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mgr.constant.LimitRedisKeys;
import com.panda.sport.rcs.mgr.mq.bean.TradingTypeVo;
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

import java.util.Objects;

/**
 * @Description 监控操盘篮球-是否更改LN
 * @Author regan
 **/
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "rcs_trading_type_cache",
        consumerGroup = "rcs_trading_type_cache",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class TradingTypeConsumer implements RocketMQListener<TradingTypeVo>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    public RedisClient redisClient;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Override
    public void onMessage(TradingTypeVo msg) {
        // 拿到操盘方MQ，存redis
        // 滚球：4小时
        // 早盘：120小时
        log.info("::进入缓存/删除LN模式: {}", JSONObject.toJSONString(msg));
        if(Objects.isNull(msg)){
            return;
        }

        //操盘滚球为0.需要处理下
        if (msg.getMatchType().toString().equals("0")) {
            msg.setMatchType(2);
        }


        String key = LimitRedisKeys.getTradingTypeStatusKey(msg.getMatchId().toString(),msg.getPlayId().toString(),msg.getMatchType().toString());
        log.info("进入缓存/删除LN模式redisKey: {}",key);
        //滚球 赛事key 默认过期时间 1天
            Integer MATCH_ONE_DAY_EXPIRY = 24 * 60 * 60;
            //这里做判断，如果存在key，说明有过设置，那么就不再更新缓存时间
            boolean flag = this.redisClient.exist(key);
            log.info("LN模式key是否存在: {}",flag);
            if(flag){
                this.redisClient.set(key,msg.getTradeType().toString());
            }else {
                this.redisClient.setExpiry(key,msg.getTradeType().toString(), Long.valueOf(MATCH_ONE_DAY_EXPIRY));
            }
    }
}
