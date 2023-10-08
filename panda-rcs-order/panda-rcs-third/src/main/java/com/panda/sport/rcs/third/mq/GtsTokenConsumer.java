package com.panda.sport.rcs.third.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.third.entity.gts.GtsAuthorizationVo;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.panda.sport.rcs.third.common.Constants.GTS_TOKEN;
import static com.panda.sport.rcs.third.common.Constants.GTS_TOKEN_TOPIC;

/**
 * token刷新消费
 *
 * @author lithan
 * @date 2023-01-07 18:28:02
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = GTS_TOKEN_TOPIC,
        consumerGroup = "rcs_gts_token_group",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class GtsTokenConsumer implements RocketMQListener<GtsAuthorizationVo>, RocketMQPushConsumerLifecycleListener {


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(20);
        defaultMQPushConsumer.setConsumeThreadMax(20);
    }

    @Override
    public void onMessage(GtsAuthorizationVo authorizationVo) {
        try {
            long timed = System.currentTimeMillis() - authorizationVo.getRefreshTime();
            long expire = (authorizationVo.getExpiresIn() - 20 * 60L) * 1000L;
            String key = String.format(GTS_TOKEN, authorizationVo.getType());
            RcsLocalCacheUtils.timedCache.put(key, authorizationVo, expire - timed );
            log.info("::{}::GTS广播刷新token,value = {}", key, JSONObject.toJSONString(authorizationVo));
        } catch (Exception e) {
            log.info("::GTS广播刷新token异常::,", e);
        }
    }


}