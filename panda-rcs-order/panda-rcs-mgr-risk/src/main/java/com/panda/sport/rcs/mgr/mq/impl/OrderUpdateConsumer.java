package com.panda.sport.rcs.mgr.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.enums.OrderStatusEnum;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.mapper.TOrderMapper;
import com.panda.sport.rcs.mgr.mq.bean.OrderBeanVo;
import com.panda.sport.rcs.mgr.wrapper.ITOrderService;
import com.panda.sport.rcs.pojo.TOrder;
import com.panda.sport.rcs.pojo.TOrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
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
        topic = MqConstants.RCS_ORDER_UPDATE,
        consumerGroup = "rcs_order_update_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class OrderUpdateConsumer implements RocketMQListener<OrderBeanVo>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    ITOrderService orderService;
    @Autowired
    RedisClient redisClient;

    @Autowired
    TOrderMapper orderMapper;
    @Autowired
    TOrderDetailMapper orderDetailMapper;

    /**
     * 获取初始化订单信息
     *
     * @param orderBean
     * @return
     */
    private TOrder getOrder(OrderBean orderBean) {
        StopWatch sw = new StopWatch();
        sw.start();
        TOrder order = BeanCopyUtils.copyProperties(orderBean, TOrder.class);
        order.setFirstTag(orderBean.getUserTagLevel());//一级标签
        order.setSecondTag(orderBean.getSecondaryTag());//二级标签
        order.setLimitType(orderBean.getLimitType() == null ? 1 : orderBean.getLimitType());//限额类型，1-标准模式，2-信用模式
        order.setCreditId(orderBean.getAgentId());//信用代理ID
        order.setModifyTime(System.currentTimeMillis());
        BigDecimal volumePercentage = orderService.getVolumePercentage(orderBean, true);//获取用户货量
        if (!ObjectUtils.isEmpty(orderBean.getItems()) && orderBean.getItems().size() > NumberUtils.INTEGER_ZERO) {
            List<TOrderDetail> list = Lists.newArrayList();
            for (OrderItem item : orderBean.getItems()) {
                item.setOrderStatus(orderBean.getOrderStatus());
                TOrderDetail detail = BeanCopyUtils.copyProperties(item, TOrderDetail.class);
                if (item.getIsRelationScore() != null) {
                    detail.setIsRelationScore(item.getIsRelationScore());
                }
                detail.setValidateResult(orderBean.getValidateResult());
                detail.setOddsValue(item.getHandleAfterOddsValue());
                detail.setMarketId(item.getMarketId());
                detail.setPlaceNum(item.getPlaceNum());
                detail.setOrderStatus(orderBean.getOrderStatus());
                detail.setSeriesType(orderBean.getSeriesType());
                if (orderBean.getExtendBean() != null) {
                    String riskChannel = orderBean.getExtendBean().getRiskChannel();
                    detail.setRiskChannel(StringUtils.isBlank(riskChannel) ? NumberUtils.INTEGER_ONE : Integer.parseInt(riskChannel));
                }
                detail.setVolumePercentage(volumePercentage);
                list.add(detail);
            }
            order.setOrderDetailList(list);
        }
        sw.stop();
        log.info("::{}::获取初始化订单信息,耗时:'{}'毫秒", JSONObject.toJSONString(order), sw.getTotalTimeMillis());
        return order;
    }

    @Override
    public void onMessage(OrderBeanVo orderBeans) {
        long startTime = System.currentTimeMillis();
        String orderNo = orderBeans.getOrderNo();
        log.info("::{}::订单开始做更新或者插入", orderNo);
        try {
            TOrder tOrder = orderMapper.selectOne(new LambdaQueryWrapper<TOrder>().eq(TOrder::getOrderNo, orderNo));
            TOrder order = this.getOrder(orderBeans);
            long updateStartTime = System.currentTimeMillis();
            log.info("::{}::订单更新开始写库或者更新库:{}", orderNo, updateStartTime);
            if (tOrder == null) {
                orderService.saveOrderAndItem(orderBeans, order);
            } else {
                //修改主订单状态
                orderMapper.updateOrderStatusBatch(order);
                //修改次订单状态
                orderDetailMapper.updateOrderDetailStatusBatch(order.getOrderDetailList());
            }
            log.info("::{}::订单更新开始写库或者更新库结束耗时:{}", orderNo, System.currentTimeMillis() - updateStartTime);
            //如果多线程线程池没有
            orderService.updateOrderAndItemStatus(orderBeans, order);
        } catch (Exception e) {
            log.error("::{}::订单更新，报错：{}", orderNo, e.getMessage(), e);
        }
        log.info("::{}::OrderUpdateConsumer订单更新:成功消费,耗时:{}", orderNo, System.currentTimeMillis() - startTime);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(256);
        defaultMQPushConsumer.setConsumeThreadMax(512);
    }
}
