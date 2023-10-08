package com.panda.sport.rcs;


import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProducerSendMessageUtils {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RocketMQTemplate rocketMQTemplate;

    public void sendMessage(String topic, String message) {
        try {
            rocketMQTemplate.convertAndSend(topic, message);
            log.info("发送消息完成:{}:{}", topic, message);
        } catch (MessagingException e) {
            log.info("发送消息异常:{}:{}:{}:{}", topic, message, e.getMessage(), e);
        }
    }

    public void sendMessage(String topic, String tag, String message) {
        try {
            rocketMQTemplate.convertAndSend(topic + ":" + tag, message);
            log.info("发送消息完成:{}:{}", topic, message);
        } catch (MessagingException e) {
            log.info("发送消息异常:{}:{}:{}:{}", topic + ":" + tag, message, e.getMessage(), e);
        }
    }

    public void sendMessage(String topic, String tag, String keys, String message) {
        try {
            String newTopic = topic;
            if (StringUtils.isNotBlank(tag)) {
                newTopic = topic + ":" + tag;
            }
            Map<String, Object> headers = null;
            if (StringUtils.isNotBlank(keys)) {
                headers = new HashMap<>();
                headers.put(RocketMQHeaders.KEYS, keys);
            }
            rocketMQTemplate.convertAndSend(newTopic, message, headers);
            log.info("发送消息完成:{}:{}:{}:{}", topic, tag, keys, message);
        } catch (MessagingException e) {
            log.info("发送消息异常:{}:{}:{}:{}:{}", topic + ":" + tag, keys, message, e.getMessage(), e);
        }
    }
}
