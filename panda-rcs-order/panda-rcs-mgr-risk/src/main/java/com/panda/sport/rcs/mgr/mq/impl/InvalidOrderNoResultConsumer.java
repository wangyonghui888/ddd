package com.panda.sport.rcs.mgr.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.enums.OrderStatusEnum;
import com.panda.sport.rcs.mapper.TOrderMapper;
import com.panda.sport.rcs.mgr.mq.bean.InvalidOrderNoMsgDTO;
import com.panda.sport.rcs.mgr.utils.RedisUtils;
import com.panda.sport.rcs.mgr.utils.SendMessageUtils;
import com.panda.sport.rcs.mgr.wrapper.ITOrderService;
import com.panda.sport.rcs.pojo.TOrder;
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

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * @author :  Regan
 * @since : 2023/9/16
 * @Description :  及时注单页面经常出现不存在得注单 (通知无效订单不显示)
 * @version : 1.0.0
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "INVALID_ORDERNO_RESULT_MQ",
        consumerGroup = "INVALID_ORDERNO_RESULT_MQ_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class InvalidOrderNoResultConsumer implements RocketMQListener<InvalidOrderNoMsgDTO>, RocketMQPushConsumerLifecycleListener {


    @Autowired
    private SendMessageUtils sendMessage;

    @Autowired
    private TOrderMapper orderMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ITOrderService orderService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Override
    public void onMessage(InvalidOrderNoMsgDTO invalidOrderNoMsgDTO) {
        // 44669 【日常】【生产】投注后，及时注单页面经常出现不存在的注单
        // 首先判断订单是否需要回滚，然后按以下流程处理
        // 1.修改订单状态
        // 2.货量理应回滚
        // 3.告知前端该订单应该剔除展示（状态传-1）
        log.info("::推送无效及时注单回滚货量开始2:{}::{}", JSONObject.toJSONString(invalidOrderNoMsgDTO));


        // 这里做一个保底机制5次后直接发过去-1状态(由于有些时候接单需要延迟几秒钟，那么就可能存在这里保底5次也获取不到货量新增标识)
        // Tip:这里可以做一个优化，无效注单可以正常及时推，但是在货量回滚上还是根据新增货量标识判断是否发回滚MQ，因为不管怎么样，回滚都需要在新增之后
        // 具体方式是新建一个回滚MQ，判断增加货量MQ标识，如果已经新增，那么无效注单走正常逻辑；如果没有，那么发回滚MQ单独处理，无效注单正常往WS推送,这样不会影响及时性
//        if(invalidOrderNoMsgDTO.getStatus() < 1) {
//            String forecastStatus = redisUtils.get("rcs:predict:calculate:bet:" + invalidOrderNoMsgDTO.getOrderNo());
//            log.info("::推送无效及时注单货量状态forecastStatus:{}::{}", forecastStatus,invalidOrderNoMsgDTO.getOrderNo());
//            if (StringUtils.isBlank(forecastStatus) || !forecastStatus.equals("1")) {
//                invalidOrderNoMsgDTO.setStatus(invalidOrderNoMsgDTO.getStatus() + 1);
//                sendMessage.sendDelayMsg("INVALID_ORDERNO_RESULT_MQ", "货量未计算-重发无效队列", invalidOrderNoMsgDTO.getOrderNo(), invalidOrderNoMsgDTO,2);
//            }else {
//                String saveOrderKey = "rcs:order:save:info:" + invalidOrderNoMsgDTO.getOrderNo();
//                String orderBeanMsg = redisUtils.get(saveOrderKey);
//                log.info("::获取到订单信息:{}::{}",orderBeanMsg);
//
//                if(StringUtils.isBlank(orderBeanMsg)){
//                    log.info("::未获取到订单信息，无需推送/回滚::");
//                    return;
//                }
//                OrderBean orderBean = JSONObject.parseObject(orderBeanMsg,OrderBean.class);
//
//                //获取用户货量
//                BigDecimal volumePercentage = orderService.getVolumePercentage(orderBean, true);
//                for(OrderItem item : orderBean.getItems()){
//                    item.setVolumePercentage(volumePercentage);
//                }
//
//                sendMessage.sendDelayMsg("rcs_forecast_cancel_order",null,invalidOrderNoMsgDTO.getOrderNo(), orderBean,1);
//            }
//        }else {
//            //此时存redis，告诉货量不需要增加
//            String invalidOrder = "rcs:invalid:order:first:" + invalidOrderNoMsgDTO.getOrderNo();
//            redisUtils.setex(invalidOrder,"1",30L, TimeUnit.SECONDS);
//            log.info("::无效及时注单回滚货量异常:{}::{}", JSONObject.toJSONString(invalidOrderNoMsgDTO));
//        }


        String forecastStatus = redisUtils.get("rcs:predict:calculate:bet:" + invalidOrderNoMsgDTO.getOrderNo());
        log.info("::推送无效及时注单货量状态forecastStatus:{}::{}", forecastStatus,invalidOrderNoMsgDTO.getOrderNo());
        if (StringUtils.isBlank(forecastStatus) || !forecastStatus.equals("1")) {
            //此时存redis，告诉货量不需要增加
            String invalidOrder = "rcs:invalid:order:first:" + invalidOrderNoMsgDTO.getOrderNo();
            redisUtils.setex(invalidOrder,"1",3000L, TimeUnit.SECONDS);
        }else {

            String saveOrderKey = "rcs:order:save:info:" + invalidOrderNoMsgDTO.getOrderNo();
            String orderBeanMsg = redisUtils.get(saveOrderKey);
            log.info("::获取到订单信息:{}::{}",orderBeanMsg);

            if(StringUtils.isBlank(orderBeanMsg)){
                log.info("::未获取到订单信息，无需推送/回滚::");
                return;
            }
            OrderBean orderBean = JSONObject.parseObject(orderBeanMsg,OrderBean.class);

            //获取用户货量
            BigDecimal volumePercentage = orderService.getVolumePercentage(orderBean, true);
            for(OrderItem item : orderBean.getItems()){
                item.setVolumePercentage(volumePercentage);
            }
            sendMessage.sendMessage("rcs_forecast_cancel_order",null,invalidOrderNoMsgDTO.getOrderNo(), orderBean);
        }
    }
}
