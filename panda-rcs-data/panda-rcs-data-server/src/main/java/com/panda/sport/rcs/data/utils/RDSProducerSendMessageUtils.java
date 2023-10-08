package com.panda.sport.rcs.data.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.panda.sport.rcs.mq.utils.HashAlgorithms;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mq.config.RocketProducerConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * @author :  Felix
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.utils
 * @Date: 2019-10-10 17:06
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service
@TraceCrossThread
public class RDSProducerSendMessageUtils {

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
                    log.info("发送完成:{}", JSON.toJSONString(sendResult));
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

    public void sendMsg(String topic, String tags, String key, String data, HashMap<String, String> properties, String hashKey) {
        try {
            Message msg = new Message(topic, tags, key, data.getBytes());

            if (properties != null && properties.size() > 0) {
                for (String proKey : properties.keySet()) {
                    msg.getProperties().put(proKey, properties.get(proKey));
                }
            }
            msg.getProperties().put(SEND_TIME, System.currentTimeMillis() + "");
            msg.getProperties().put("SEND_SERVICE_GROUP", SEND_SERVICE);

            SendResult result = rocketProducerConfig.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                    if (list == null || list.size() <= 0) {return null;}
                    int hash = HashAlgorithms.java(String.valueOf(o));
                    return list.get(Math.abs(hash) % list.size());
                }
            }, hashKey);
            String linkId = CommonUtil.getLinkId(msg, key);
            log.info("发送消息：topic：::{}::，tag：{}，key：{}", topic+linkId, tags, key, data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 向rocketmq发送普通消息的生产者
     */
    public void sendMessage(String topic, String tags, String keys, Object msg, Map<String, String> properties) {
        try {

            Message message = new Message(topic, tags, keys, JSONObject.toJSONString(msg).getBytes("utf-8"));
            message.getProperties().put(SEND_TIME, System.currentTimeMillis() + "");
            message.getProperties().put("SEND_SERVICE_GROUP", SEND_SERVICE);
            if (properties != null) {
                message.getProperties().putAll(properties);
            }
            String linkId = CommonUtil.getLinkId(msg, keys);
            log.info("发送消息：topic::{}::,tags:{},keys:{}", topic+linkId, tags, keys);
            sendMessage(message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendMessage(String topic, String tags, String keys, Object msg) {
        try {
            sendMessage(topic, tags, keys, msg, null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
        if (StringUtils.isBlank(config)) {
            throw new RcsServiceException("消息参数不能为空：" + config);
        }
        String[] cons = config.split(",");
        if (cons.length == 1) {
            sendMessage(cons[0], "", "", msg, properties);
        } else if (cons.length == 2) {
            sendMessage(cons[0], cons[1], "", msg, properties);
        }
        if (cons.length >= 3) {
            sendMessage(cons[0], cons[1], cons[2], msg, properties);
        }
    }

    public void sendMessage(Object obj, MessageQueue messageQueue) {
        try {
            log.info("发送消息指定队列:{}", JSONObject.toJSONString(messageQueue));
            Message message = new Message();
            message.putUserProperty(SEND_TIME, System.currentTimeMillis() + "");
            message.putUserProperty("SEND_SERVICE", SEND_SERVICE);
            message.setBody(JSONObject.toJSONString(obj).getBytes("utf-8"));
            message.setTopic(messageQueue.getTopic());
            rocketProducerConfig.send(message, messageQueue);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
