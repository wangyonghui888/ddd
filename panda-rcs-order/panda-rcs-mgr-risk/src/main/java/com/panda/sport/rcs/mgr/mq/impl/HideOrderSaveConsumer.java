package com.panda.sport.rcs.mgr.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.mq.bean.HideOrderDTO;
import com.panda.sport.rcs.mgr.service.orderhide.ITOrderHideService;
import com.panda.sport.rcs.mgr.utils.RedisUtils;
import com.panda.sport.rcs.pojo.AmountTypeVo;
import com.panda.sport.rcs.vo.TOrderHide;
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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
/**
 * 藏单异步保存入库
 *
 * @author skyKong
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = Constants.RCS_HIDE_ORDER_SAVE,
        consumerGroup = Constants.RCS_HIDE_ORDER_SAVE,
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class HideOrderSaveConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    final ITOrderHideService itOrderHideService;

    final RedisClient redisClient;
    @Autowired
    RedisUtils redisUtils;

    public  HideOrderSaveConsumer(ITOrderHideService itOrderHideService,RedisClient redisClient){
        this.redisClient = redisClient;
        this.itOrderHideService = itOrderHideService;
    }
    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(32);
        defaultMQPushConsumer.setConsumeThreadMax(128);
    }
    @Override
    public void onMessage(String data) {
        HideOrderDTO message = null;
        try {
            message = JSONObject.parseObject(data, HideOrderDTO.class);
            log.info("::{}:: hideOrderSaveConsumer 收到bean info ：{}", message.getOrderNo(), JSONObject.toJSONString(message));
            long stat = System.currentTimeMillis();
            doMessage(message);
            log.info("::{}:: hideOrderSaveConsumer {} times {}", message.getOrderNo(), JSONObject.toJSONString(message), System.currentTimeMillis() - stat);
        } catch (Exception ex) {
            log.error(" ::hideOrderSaveConsumer{}::  error {}", message.getOrderNo(), ex.getMessage());
        }
    }

    private void doMessage(HideOrderDTO message) {
        TOrderHide orderHide = setTOrderHide(message.getOrderNo(), message.getAmountTypeVo());
        orderHide.setCreateTime(getNowTime());
        redisUtils.rpush(Constants.RCS_HIDE_ORDER_BATCH_SAVE, JSONObject.toJSONString(orderHide));
    }

    private TOrderHide setTOrderHide(String orderNo, AmountTypeVo amountTypeVo) {
        TOrderHide orderHide = new TOrderHide();
        orderHide.setOrderNo(orderNo);
        orderHide.setVolumePercentage(amountTypeVo.getVolumePercentage());
        orderHide.setDynamicVolumePercentage(amountTypeVo.getDynamicVolumePercentage());
        orderHide.setEquipmentVolumePercentage(amountTypeVo.getEquipmentVolumePercentage());
        orderHide.setMerchantVolumePercentage(amountTypeVo.getMerchantVolumePercentage());
        orderHide.setCategory(amountTypeVo.getCategory());
        return orderHide;
    }

    private Long getNowTime() {
        return LocalDateTime.now().toInstant(ZoneOffset.ofHours(+8)).toEpochMilli();// tOrderHideMapper.getNowTime().toInstant(ZoneOffset.ofHours(+8)).toEpochMilli();
    }
}
