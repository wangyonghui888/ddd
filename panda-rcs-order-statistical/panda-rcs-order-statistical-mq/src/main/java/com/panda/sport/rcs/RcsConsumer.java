package com.panda.sport.rcs;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class RcsConsumer<T> implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    private Logger log = LoggerFactory.getLogger(RcsConsumer.class);

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(32);
        consumer.setConsumeThreadMax(64);
    }

    @Override
    public void onMessage(String message) {
        long startTime = System.currentTimeMillis();
        CommonUtils.mdcPut();
        try {
            log.info("收到消息：topic={},message={}", this.getTopic(), message);
            T obj = this.convert(message);
            this.handleMs(obj);
        } catch (com.alibaba.fastjson.JSONException e) {
            log.error(String.format("消息解析失败：class=%s,topic=%s,message=%s", this.getClass(), this.getTopic(), message), e);
        } catch (Exception e) {
            log.error(String.format("消息消费异常：class=%s,topic=%s,message=%s", this.getClass(), this.getTopic(), message), e);
        } finally {
            long execTime = System.currentTimeMillis() - startTime;
            if (execTime > 1000L) {
                log.error("消费完成：消费时长={},class={},topic={}", execTime, this.getClass(), this.getTopic());
            } else {
                log.info("消费完成：消费时长={},class={},topic={}", execTime, this.getClass(), this.getTopic());
            }
            CommonUtils.mdcRemove();
        }
    }

    protected abstract String getTopic();

    protected abstract Boolean handleMs(T message);

    private T convert(String msg) {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        return (T) JSONObject.parseObject(msg, type);
    }

}
