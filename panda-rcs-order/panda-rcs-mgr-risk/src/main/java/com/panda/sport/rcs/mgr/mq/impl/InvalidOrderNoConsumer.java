package com.panda.sport.rcs.mgr.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.enums.OrderStatusEnum;
import com.panda.sport.rcs.mapper.TOrderMapper;
import com.panda.sport.rcs.mgr.mq.bean.InvalidOrderNoMsgDTO;
import com.panda.sport.rcs.mgr.mq.bean.OrderBeanVo;
import com.panda.sport.rcs.mgr.utils.RedisUtils;
import com.panda.sport.rcs.mgr.utils.SendMessageUtils;
import com.panda.sport.rcs.mgr.wrapper.ITOrderService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
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
import java.util.Objects;

/**
 * @author :  Regan
 * @since : 2023/9/16
 * @Description :  及时注单页面经常出现不存在得注单 (通知无效订单不显示)
 *          // 44669 【日常】【生产】投注后，及时注单页面经常出现不存在的注单
 *         // 首先判断订单是否需要回滚，然后按以下流程处理
 *         // 1.修改订单状态
 *         // 2.货量理应回滚
 *         // 3.告知前端该订单应该剔除展示（状态传-1）
 * @version : 1.0.0
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "INVALID_ORDERNO_MQ",
        consumerGroup = "INVALID_ORDERNO_MQ_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class InvalidOrderNoConsumer implements RocketMQListener<InvalidOrderNoMsgDTO>, RocketMQPushConsumerLifecycleListener {


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
        log.info("::推送无效及时注单开始:{}::{}", JSONObject.toJSONString(invalidOrderNoMsgDTO));
        try {
            String orderNo = invalidOrderNoMsgDTO.getOrderNo();
            if (StringUtils.isEmpty(orderNo)) {
                log.info("::上游订单号为空，不需要做任何操作！");
                return;
            }

            // 由于入库都是异步操作，这里有概率还没入库就拿数据了
            TOrder tOrder = orderMapper.selectOne(new LambdaQueryWrapper<TOrder>().eq(TOrder::getOrderNo, orderNo));
            log.info("::无效及时注单订单信息:{}", JSONObject.toJSONString(tOrder));

            //由于有等待状态的订单也会过来，这里考虑到下面做了一个更新库的操作成功才走下面逻辑，所以把状态拦截取消
            //判断订单状态是否需要回滚
            tOrder.setOrderStatus(OrderStatusEnum.ORDER_REJECT.getCode());
            if (this.orderMapper.updateById(tOrder) > 0) {
                log.info("::开始执行推送ws:{}", tOrder.getOrderNo());

                String saveOrderKey = "rcs:order:save:info:" + tOrder.getOrderNo();
                String orderBeanMsg = redisUtils.get(saveOrderKey);
                log.info("::获取到订单信息:{}::{}", orderBeanMsg);

                if (StringUtils.isBlank(orderBeanMsg)) {
                    log.info("::未获取到订单信息，无需推送/回滚::");
                    return;
                }
                OrderBean orderBean = JSONObject.parseObject(orderBeanMsg, OrderBean.class);

                //获取用户货量
//            BigDecimal volumePercentage = orderService.getVolumePercentage(orderBean, true);
//            for(OrderItem item : orderBean.getItems()){
//                item.setVolumePercentage(volumePercentage);
//            }
                // 构建货量回滚参数
                // 发送mq给货量
                // 可能存在无效注单货量回滚MQ在注单货量增加MQ前面，导致回滚MQ发在前面，这里货量统一发延迟MQ处理
//            String forecastStatus = redisUtils.get("rcs:predict:calculate:bet:" + invalidOrderNoMsgDTO.getOrderNo());
//            log.info("::推送无效及时注单货量状态forecastStatus:{}::{}", forecastStatus,orderNo);
//            if (StringUtils.isBlank(forecastStatus) || !forecastStatus.equals("1")) {
//                sendMessage.sendMessage("INVALID_ORDERNO_RESULT_MQ", "货量未计算-发送延迟队列", invalidOrderNoMsgDTO.getOrderNo(), orderBean);
//            }else {
//                sendMessage.sendMessage("rcs_forecast_cancel_order",null,invalidOrderNoMsgDTO.getOrderNo(), orderBean);
//            }
                sendMessage.sendDelayMsg("INVALID_ORDERNO_RESULT_MQ", "货量-发送延迟队列", invalidOrderNoMsgDTO.getOrderNo(), invalidOrderNoMsgDTO, 1);


                //构造及时注单,推送客户端;
                orderBean.setOrderStatus(-1);
                orderBean.setIsUpdateOdds(true);
//                OrderBeanVo orderBeanVo = new OrderBeanVo();
//                orderBeanVo.setOrderNo(invalidOrderNoMsgDTO.getOrderNo());
//                orderBeanVo.setOrderStatus(-1);
//                orderBeanVo.setUid(tOrder.getUid());

                sendMessage.sendMessage(MqConstants.WS_ORDER_BET_RECORD_TOPIC, MqConstants.WS_ORDER_BET_RECORD_TAG, orderBean.getOrderNo(), orderBean);
                log.info("::结束执行推送ws:{}::{}", JSONObject.toJSONString(orderBean));
            }
        }catch (Exception e){
            log.error("::{}::无效注单异常：{}", invalidOrderNoMsgDTO.getOrderNo(), e.getMessage(), e);
        }
        log.info("::无效注单消费成功:{}", JSONObject.toJSONString(invalidOrderNoMsgDTO));
    }
}
