package com.panda.sport.rcs.mgr.mq.impl.settle;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.mapper.TOrderMapper;
import com.panda.sport.rcs.mgr.mq.bean.OrderBeanVo;
import com.panda.sport.rcs.mgr.mq.bean.OrderItemVo;
import com.panda.sport.rcs.mgr.mq.bean.PreSettleBean;
import com.panda.sport.rcs.mgr.mq.bean.SettleOrder;
import com.panda.sport.rcs.mgr.wrapper.ITOrderService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;

@Slf4j
@Service
@TraceCrossThread
@RocketMQMessageListener(
        topic = RcsConstant.PRE_SETTLE_HANDLE_STATUS,
        consumerGroup = RcsConstant.PRE_SETTLE_HANDLE_STATUS_CROUP,
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class PreSettleConsumer implements RocketMQListener<PreSettleBean>, RocketMQPushConsumerLifecycleListener {

    public static final String RCS_FORECAST_PRE_SETTLE_ORDER = "rcs_forecast_pre_settle_order";

    @Autowired
    ProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    private TOrderMapper tOrderMapper;
    @Autowired
    private ITOrderService orderService;

    @Override
    public void onMessage(PreSettleBean preSettleBean) {
        String orderNo = null;
        if (preSettleBean == null) {
            return;
        }
        try {
            orderNo = preSettleBean.getOrderNo();
            log.info("业务推送提前结算订单::{}::,消息体{},提前結算状态:{}", orderNo, JSONObject.toJSONString(preSettleBean), preSettleBean.getOrderState());
            //提前結算成功才推送訊息
            if (preSettleBean.getOrderState() == 1) {
                OrderBean orderBean = tOrderMapper.queryOrderAndDetailByOrderNo(orderNo);
                OrderBeanVo orderBeanVo = BeanCopyUtils.copyProperties(orderBean, OrderBeanVo.class);
                //目前僅處裡單關提前結算，串關需再調整
                OrderItem item = orderBeanVo.getItems().get(0);
                OrderItemVo itemvo = BeanCopyUtils.copyProperties(item, OrderItemVo.class);

                SettleOrder settleOrder = EntityPackaing(preSettleBean);

                itemvo.setSettleOrder(settleOrder);

                orderBeanVo.setItemsVo(Arrays.asList(itemvo));
                orderBeanVo.setValidateResult(item.getValidateResult());
                orderBeanVo.setSecondaryLabelIdsList(preSettleBean.getSecondaryLabelIdsList());

                orderBean.setValidateResult(item.getValidateResult());
                //推送ws，即時注單顯示
                orderBeanVo.setOrderStatus(100);
                orderService.sendOrderWs(orderBeanVo);
                //推送forecast計算
                producerSendMessageUtils.sendMessage(RCS_FORECAST_PRE_SETTLE_ORDER, "pre_settle_order", orderNo, orderBean);

                log.info("::{}::业务推送提前结算订单完成", orderNo);
            }
        } catch (Exception e) {
            log.info("业务推送提前结算订单异常::{}::,{},{}", orderNo, e.getMessage(), e);
        }
    }

    SettleOrder EntityPackaing(PreSettleBean preSettleBean) {
        SettleOrder settleOrder = new SettleOrder();
        settleOrder.setPreOrderNo(preSettleBean.getPreOrderNo());
        settleOrder.setOrderNo(preSettleBean.getOrderNo());
        settleOrder.setWaitTime(preSettleBean.getWaitTime() != null ? preSettleBean.getWaitTime() / 1000 : 0);
        settleOrder.setReqTime(preSettleBean.getReqTime());
        settleOrder.setOrderState(preSettleBean.getOrderState());
        settleOrder.setPreSettleScore(preSettleBean.getPreSettleScore());
        settleOrder.setPreSettleBetAmount(preSettleBean.getPreSettleBetAmount());
        settleOrder.setPreSettleOdds(preSettleBean.getPreSettleOdds());
        settleOrder.setPreSettleAmount(preSettleBean.getPreSettleAmount());
        settleOrder.setOrderAmount(preSettleBean.getOrderAmount());
        if (new BigDecimal(settleOrder.getPreSettleBetAmount()).setScale(0, BigDecimal.ROUND_DOWN).toString().equals(
                new BigDecimal(settleOrder.getOrderAmount()).setScale(0, BigDecimal.ROUND_DOWN).toString())) {
            settleOrder.setSettleType(1);
        } else {
            settleOrder.setSettleType(0);
        }
        return settleOrder;
    }


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

}
