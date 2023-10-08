package com.panda.sport.rcs.data.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.data.utils.CommonUtil;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.utils.i18n.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.MDC;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @Project Name : panda-rcs-trade-group
 * @Package Name : panda-rcs-trade-group
 * @Description : 消费基类
 * @Date : 2021-11-17 14:08
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
public abstract class RcsConsumer<T> implements RocketMQListener<Object>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(96);
        consumer.setConsumeThreadMax(128);
    }

    @Override
    public void onMessage(Object message) {
        try {
            long startTime = System.currentTimeMillis();
            mdcPut();
            try {
                String msg = JSONObject.toJSONString(message);
                String linkId= CommonUtil.getLinkId(msg);
                log.info("收到消息：topic=::{}::", this.getTopic()+linkId);
                T obj = this.convert(msg);
                this.handleMs(obj);
            } catch (RcsServiceException e) {
                log.error(String.format("消息消费失败：class=%s,topic=%s,message=%s", this.getClass(), this.getTopic(), JSONObject.toJSONString(message))+e.getMessage(), e);
                throw e;
            } catch (com.alibaba.fastjson.JSONException e) {
                log.error(String.format("消息解析失败：class=%s,topic=%s,message=%s", this.getClass(), this.getTopic(), JSONObject.toJSONString(message))+e.getMessage(), e);
            } catch (Exception e) {
                log.error(String.format("消息消费异常：class=%s,topic=%s,message=%s", this.getClass(), this.getTopic(), JSONObject.toJSONString(message))+e.getMessage(), e);
            } finally {
                long execTime = System.currentTimeMillis() - startTime;
                if (execTime > 1000L) {
                    log.error("消费完成：消费时长={},class={},topic={}", execTime, this.getClass(), this.getTopic());
                } else {
                    log.info("消费完成：消费时长={},class={},topic={}", execTime, this.getClass(), this.getTopic());
                }
                mdcRemove();
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    protected abstract String getTopic();

    protected abstract Boolean handleMs(T message);

    private T convert(String msg) {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        return (T) JSONObject.parseObject(msg, type);
    }

    public static final String LINK_ID = "linkId";

    public static String mdcPut() {
        String uuid = CommonUtils.getUUID();
        try {
            MDC.put(LINK_ID, uuid);
        } catch (Throwable t) {
            log.error("MDC.put 异常", t);
        }
        return uuid;
    }

    public static void mdcRemove() {
        try {
            MDC.remove(LINK_ID);
        } catch (Throwable t) {
            log.error("MDC.remove 异常", t);
        }
    }

}
