package com.panda.sport.rcs.oddin.util;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mq.config.RocketProducerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class SendMessageUtils {

    @Autowired(required = false)
    public RocketProducerConfig rocketProducerConfig;

    private String SEND_TIME = "SEND_TIME";

    public static String SEND_SERVICE = "PANDA_RCS";

    /**
     * 向rocketmq发送普通消息的生产者
     */
    public void sendMessage(Message message) {
        //默认3秒超时
        try {
            rocketProducerConfig.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {

                    //log.info("发送完成:{}", JSON.toJSONString(sendResult));
                }

                @Override
                public void onException(Throwable throwable) {
                    try {
                        log.error("发送失败：" + new String(message.getBody(), "utf-8") + ",error : " + throwable.getMessage(), throwable);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 向rocketmq发送普通消息的生产者
     */
    public void sendMessage(String topic, String tags, String keys, Object msg, Map<String, String> properties, Integer delayTimeLevel) {
        try {

            Message message = new Message(topic, tags, keys, JSONObject.toJSONString(msg).getBytes("utf-8"));
            message.getProperties().put(SEND_TIME, System.currentTimeMillis() + "");
            message.getProperties().put("SEND_SERVICE_GROUP", SEND_SERVICE);
            if (properties != null) {
                message.getProperties().putAll(properties);
            }
            if (delayTimeLevel != null) {
                message.setDelayTimeLevel(delayTimeLevel);
            }
            //   log.info("发送消息：topic:{},tags:{},keys:{},msg:{},map：{}", topic, tags, keys, JSONObject.toJSONString(msg), JSONObject.toJSONString(message.getProperties()));
            sendMessage(message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendMessage(String topic, String tags, String keys, Object msg) {
        try {
            log.info("::{}::发送队列topic:{},TAG:{},msg:{}", keys, topic, tags, msg);
            sendMessage(topic, tags, keys, msg, null, null);
        } catch (Exception e) {
            log.error("::{}::发送MQ异常topic:{},TAG:{},msg:{},error:{}", keys, topic, tags, msg, e.getMessage(), e);
        }
    }

    public void sendDelayMessage(String topic, String tags, String keys, Object msg) {
        try {
            log.info("::{}::发送MQ延迟队列topic:{},TAG:{},msg:{}", keys, topic, tags, msg);
            sendMessage(topic, tags, keys, msg, null, 1);
        } catch (Exception e) {
            log.error("::{}::发送MQ延迟队列异常topic:{},TAG:{},msg:{},error:{}", keys, topic, tags, msg, e.getMessage(), e);
        }
    }


    /**
     * 向rocketmq发送普通消息的生产者
     */
    public void sendMessage(String config, Object msg) {
        sendMessage(config, msg, null);
    }


    /**
     * 向rocketmq发送普通消息的生产者
     */
    public void sendMessage(String config, Object msg, Map<String, String> properties) {
        if (StringUtils.isBlank(config))
            throw new RcsServiceException("消息参数不能为空：" + config);

        String[] cons = config.split(",");
        if (cons.length == 1)
            sendMessage(cons[0], "", "", msg, properties, null);
        else if (cons.length == 2)
            sendMessage(cons[0], cons[1], "", msg, properties, null);
        if (cons.length >= 3)
            sendMessage(cons[0], cons[1], cons[2], msg, properties, null);
    }
}
