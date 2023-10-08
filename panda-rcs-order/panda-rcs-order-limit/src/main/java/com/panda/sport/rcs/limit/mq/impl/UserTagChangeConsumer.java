package com.panda.sport.rcs.limit.mq.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.limit.service.LimitServiceImpl;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.service.ITUserService;
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

import java.util.HashMap;
import java.util.Map;

/**
 * @author :  lithan
 * @Description :  用户标签更改通知
 * @Date: 2021-2-13 18:44:45
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "RCS_LIMIT_USER_TAG_CHANGE",
        consumerGroup = "RCS_LIMIT_USER_TAG_CHANGE",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class UserTagChangeConsumer implements RocketMQListener<JSONObject>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    LimitServiceImpl limitService;

    @Autowired
    ITUserService userService;

    public UserTagChangeConsumer() {
//        super("RCS_LIMIT_USER_TAG_CHANGE", "");
    }

    @Override
    public void onMessage(JSONObject json) {
        try {
            log.info("MQ：{} 用户标签更改通知收到更新 ：{}", "RCS_LIMIT_USER_TAG_CHANGE", JSONObject.toJSONString(json));
            String userId = json.getString("userId");
            String tagId = json.getString("tagId");

            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("tagId", tagId);

            if (StringUtils.isNotBlank(tagId)) {
                userService.updateUserTagId(Long.parseLong(userId), Integer.parseInt(tagId));
            }
            //通知sdk 清除缓存
            producerSendMessageUtils.sendMessage("rcs_limit_user_tag_change_sdk,," + userId, map);


            JSONArray data = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", userId);
            jsonObject.put("value", tagId);
            jsonObject.put("type", "user_tag");
            data.add(jsonObject);
            producerSendMessageUtils.sendMessage("rcs_local_cache_clear_sdk", userId, userId, data);

            log.info("用户标签更改通知sdk完成 ：{}", JSONObject.toJSONString(map));
        } catch (Exception e) {
            log.error("用户标签更改通知异常：{}{}", e.getMessage(), e);
        }
        return;
    }
}
