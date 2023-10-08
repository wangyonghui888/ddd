package com.panda.sport.rcs.oddin.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * 到门口rocketmq工具类
 *
 * @author Z9-conway
 */
@Slf4j
@Component
public class DjMqUtils {

    @Resource
    private MQProducer djProducer;

    private DjMqUtils() {

    }

    /**
     * 格式化消息
     *
     * @param topic
     * @param msg
     * @return
     */
    public Message parseMsg(String topic, String tags, String key, String msg) {
        Message message = null;
        try {
//            message = new Message(topic, msg.getBytes(StandardCharsets.UTF_8));
            message = new Message(topic, tags, key, msg.getBytes("utf-8"));
        } catch (Exception e) {
            log.error("格式化消息失败");
        }
        return message;
    }

    public Message parseMsg(String topic, String msg) {
        Message message = null;
        try {
            message = new Message(topic, msg.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("格式化消息失败");
        }
        return message;
    }

    /**
     * 发送同步消息
     *
     * @param topic
     * @param str
     * @return
     */
    public boolean sendMessage(String topic, String tags, String key, String msg) {
        Message message = parseMsg(topic, tags, key, msg);
        boolean flag = false;
        try {
            SendResult sendResult = djProducer.send(message);
            flag = sendResult.getSendStatus().equals(SendStatus.SEND_OK);
            if (flag) {
                log.info("MqUtils发送消息发送成功, topic ：{}， msg：{}", topic, JSONObject.toJSONString(msg));
            } else {
                log.info("MqUtils发送消息发送失败, topic ：{}， msg：{}", topic, JSONObject.toJSONString(msg));
            }
        } catch (Exception e) {
            log.error("消息发送失败：{}", e);
            return false;
        }
        return flag;
    }

    public boolean sendMessage(String topic, String msg) {
        Message message = parseMsg(topic, msg);
        boolean flag = false;
        try {
            SendResult sendResult = djProducer.send(message);
            flag = sendResult.getSendStatus().equals(SendStatus.SEND_OK);
            if (flag) {
                log.info("MqUtils发送消息发送成功, topic ：{}， msg：{}", topic, JSONObject.toJSONString(msg));
            } else {
                log.info("MqUtils发送消息发送失败, topic ：{}， msg：{}", topic, JSONObject.toJSONString(msg));
            }
        } catch (Exception e) {
            log.error("消息发送失败：{}", e);
            return false;
        }
        return flag;
    }
}
