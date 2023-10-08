package com.panda.sport.rcs.mgr.mq.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.sport.data.rcs.dto.PreOrderDetailRequest;
import com.panda.sport.data.rcs.dto.PreOrderRequest;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.enums.PreSettleInfoStatusEnum;
import com.panda.sport.rcs.mapper.RcsPreOrderDetailExtMapper;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.mgr.wrapper.ITOrderService;
import com.panda.sport.rcs.pojo.RcsPreOrderDetailExt;
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

import java.util.Arrays;
import java.util.List;

/**
 * 订单状态修改mq
 *
 * @author holly
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = MqConstants.RCS_PRE_SETTLE_UPDATE,
        consumerGroup = MqConstants.RCS_PRE_SETTLE_UPDATE_GROUP,
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class PreSettleUpdateConsumer implements RocketMQListener<PreOrderRequest>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    ITOrderService orderService;

    @Autowired
    RcsPreOrderDetailExtMapper rcsPreOrderDetailExtMapper;
    @Autowired
    TOrderDetailMapper orderDetailMapper;

    /**
     * 获取初始化订单信息
     *
     * @param orderBean 提前结算实体
     * @return RcsPreOrderDetailExt
     */
    private RcsPreOrderDetailExt getOrder(PreOrderRequest orderBean, RcsPreOrderDetailExt tOrder) {
        RcsPreOrderDetailExt order = new RcsPreOrderDetailExt();
        if (tOrder != null) {
            order = BeanCopyUtils.copyProperties(tOrder, RcsPreOrderDetailExt.class);
        }
        long startTime = System.currentTimeMillis();
        PreOrderDetailRequest preOrderDetail = orderBean.getDetailList().get(0);
        if (orderBean.getHandleStatus() == 0) {
            order.setUserId(orderBean.getUserId());
            order.setMatchId(preOrderDetail.getMatchId());
            order.setOrderNo(preOrderDetail.getOrderNo());
            order.setPreOrderNo(orderBean.getPreOrderNo());
            order.setBetNo(preOrderDetail.getBetNo());
            order.setOrderStatus(orderBean.getOrderStatus());
            order.setBetTime(preOrderDetail.getBetTime());
            order.setCreateTime(startTime);
            order.setUpdateTime(startTime);
            order.setSeriesType(orderBean.getSeriesType());
            order.setReason(orderBean.getReason());
            order.setInfoStatus(orderBean.getInfoStatus());
            order.setPreOrderNo(orderBean.getOrderNo());
            order.setMatchType(preOrderDetail.getMatchType());
            order.setReqTime(orderBean.getReqTime());
        } else {
            order.setUpdateTime(startTime);
            order.setOrderStatus(orderBean.getOrderStatus());
            order.setInfoStatus(orderBean.getInfoStatus());
            order.setMaxAcceptTime(preOrderDetail.getMaxAcceptTime());
            order.setMaxWait(preOrderDetail.getMaxWait());
            order.setMinWait(preOrderDetail.getMinWait());
            order.setCurrentEvent(preOrderDetail.getCurrentEvent());
            order.setCurrentEventType(preOrderDetail.getCurrentEventType());
            order.setCurrentEventTime(preOrderDetail.getCurrentEventTime());
            order.setCategorySetId(preOrderDetail.getCategorySetId());
            order.setEventAxis(preOrderDetail.getEventAxis());
            order.setReason(orderBean.getReason());
            order.setHandleStatus(orderBean.getHandleStatus());
        }

        log.info("::{}::获取初始化订单信息,耗时:{}", order.getOrderNo(), System.currentTimeMillis() - startTime);
        return order;
    }

    @Override
    public void onMessage(PreOrderRequest orderBeans) {
        long startTime = System.currentTimeMillis();
        String orderNo = orderBeans.getOrderNo();
        log.info("::{}::订单开始做更新或者插入", orderNo);
        try {
            RcsPreOrderDetailExt tOrder = rcsPreOrderDetailExtMapper.selectOne(new LambdaQueryWrapper<RcsPreOrderDetailExt>().eq(RcsPreOrderDetailExt::getOrderNo, orderNo));
            RcsPreOrderDetailExt order = this.getOrder(orderBeans, tOrder);
            long updateStartTime = System.currentTimeMillis();
            log.info("::{}::订单更新开始写库或者更新库:{}", orderNo, updateStartTime);
            if (tOrder == null) {
                rcsPreOrderDetailExtMapper.insert(order);
            } else {
                //修改提前结算订单状态
                rcsPreOrderDetailExtMapper.updateOrderStatus(order);
            }
            log.info("::{}::订单更新开始写库或者更新库结束耗时:{}", orderNo, System.currentTimeMillis() - updateStartTime);
            //如果不是秒接拒单，发送mq到reject或业务
            boolean easyReject= Arrays.asList(PreSettleInfoStatusEnum.CHECK_FAIL.getCode(),PreSettleInfoStatusEnum.EARLY_PASS.getCode(),
                    PreSettleInfoStatusEnum.HALFTIME_PASS.getCode(),PreSettleInfoStatusEnum.UPCOMING_PASS.getCode()).contains(orderBeans.getInfoStatus());
            if(!easyReject){
                orderService.updatePreOrderAndItemStatus(orderBeans, order);
            }
        } catch (Exception e) {
            log.error("::{}::订单更新，报错：{}", orderNo, e.getMessage(), e);
        }
        log.info("::{}::OrderUpdateConsumer订单更新:成功消费,耗时:{}", orderNo, System.currentTimeMillis() - startTime);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMqPushConsumer) {
        defaultMqPushConsumer.setConsumeThreadMin(256);
        defaultMqPushConsumer.setConsumeThreadMax(512);
    }


}
