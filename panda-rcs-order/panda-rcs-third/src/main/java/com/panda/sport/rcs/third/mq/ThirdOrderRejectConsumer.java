package com.panda.sport.rcs.third.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.third.entity.common.ThirdOrderDelayVo;
import com.panda.sport.rcs.third.entity.common.ThirdOrderExt;
import com.panda.sport.rcs.third.entity.common.pojo.ErrorMessagePrompt;
import com.panda.sport.rcs.third.enums.OrderStatusEnum;
import com.panda.sport.rcs.third.factory.ThirdStrategyFactory;
import com.panda.sport.rcs.third.service.handler.IOrderHandlerService;
import com.panda.sport.rcs.third.service.reject.IOrderAcceptService;
import com.panda.sport.rcs.third.util.SendMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import static com.panda.sport.rcs.third.common.Constants.*;


/**
 * @author Beulah
 * @date 2023/3/28 15:10
 * @description 三方订单简易接拒
 */
@Component
@Slf4j
@RocketMQMessageListener(topic = RCS_RISK_THIRD_ORDER_REJECT,
        consumerGroup = "rcs_risk_third_order_reject_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class ThirdOrderRejectConsumer implements RocketMQListener<JSONObject>, RocketMQPushConsumerLifecycleListener {

    @Resource
    IOrderAcceptService orderAcceptService;
    @Resource
    IOrderHandlerService orderHandlerService;
    @Resource
    ProducerSendMessageUtils producer;
    @Resource
    RedisClient redisClient;
    @Resource(name = "confirmPoolExecutor")
    private ThreadPoolExecutor confirmPoolExecutor;
    @Resource
    private SendMessageUtils sendMessageUtils;

    @Override
    public void onMessage(JSONObject message) {
        ThirdOrderDelayVo v = JSONObject.parseObject(message.toJSONString(), ThirdOrderDelayVo.class);
        String orderNo = v.getOrderNo();
        String third = v.getThird();
        String thirdOrderNo = v.getThirdOrderNo();
        try {
            log.info("::{}::{}订单接拒收到:{}", orderNo, third, JSON.toJSONString(v));
            long time = System.currentTimeMillis() - v.getAcceptTime();
            int mtsIsCache = orderHandlerService.getMtsIsCache(third, 3);
            if (v.getDelayTime() != null && time >= v.getDelayTime() * 1000) {
                log.info("::{}::{}订单接拒,到达时间开始接单", orderNo, third);
                String albyRejectOrder = String.format(GTS_IS_NOT_ALLOWED_ORDER_REJECT, orderNo);
                String rejectStatus = redisClient.get(albyRejectOrder);
                if ("1".equals(rejectStatus)) {
                    log.info("::{}::{}::Ably已经发送拒单消息，并且已经拒单成功了，所以不进行订单确认，订单:{}",orderNo, third, v);
                    return;
                }
                String canceledKey = String.format(THIRD_ORDER_CANCELED, orderNo);
                String status = redisClient.get(canceledKey);
                int orderStatus = 0;
                if (org.apache.commons.lang3.StringUtils.isNotBlank(status)) {
                    orderStatus = Integer.parseInt(status);
                }
                if (orderStatus == 1) {
                    log.info("::{}::{}订单延迟接拒,到达时间开始接单，此时的订单状态已经撤单状态，所以不进行订单确认，订单状态:{}", orderNo, third, orderStatus);
                    return;
                }
                orderStatus = OrderStatusEnum.ACCEPTED.getCode();
                Integer infoStatus = OrderInfoStatusEnum.SCROLL_PASS.getCode();
                String infoMsg = "到达时间接单";
                ThirdOrderExt ext = new ThirdOrderExt();
                ext.setOrderNo(orderNo);
                ext.setThirdOrderNo(thirdOrderNo);
                ext.setThird(v.getThird());
                ext.setOrderStatus(orderStatus);
                ext.setThirdResJson(v.getThirdRes());
                ext.setBusId(v.getBusId());
                ext.setPaTotalAmount(v.getTotalMoney());
                ext.setThirdOrderStatus(OrderStatusEnum.WAITING.getCode());
                ext.setList(v.getList());
                ext.setOrderGroup(v.getOrderGroup());
                ext.setSeriesType(v.getSeriesType());
                orderHandlerService.updateOrder(ext, infoStatus, v.getThird() + infoMsg, mtsIsCache);
                confirmPoolExecutor.execute(() -> {
                    orderHandlerService.notifyThirdUpdateOrder(ext);
                });
                return;
            } else {
                //检查实时接拒
                ErrorMessagePrompt errorMessage = new ErrorMessagePrompt();
                boolean checkStatus = orderAcceptService.dealWithData(v.getList(), errorMessage, third, 0);
                if (checkStatus) {
                    log.info("::{}::{}订单接拒,检查实时接拒信息拒单", orderNo, third);
                    ThirdOrderExt ext = new ThirdOrderExt();
                    ext.setOrderNo(orderNo);
                    ext.setThirdOrderNo(thirdOrderNo);
                    ext.setThird(v.getThird());
                    ext.setOrderStatus(OrderStatusEnum.REJECTED.getCode());
                    ext.setThirdResJson(v.getThirdRes());
                    ext.setBusId(v.getBusId());
                    ext.setPaTotalAmount(v.getTotalMoney());
                    ext.setThirdOrderStatus(OrderStatusEnum.WAITING.getCode());
                    ext.setList(v.getList());
                    ext.setOrderGroup(v.getOrderGroup());
                    ext.setSeriesType(v.getSeriesType());
                    mtsIsCache = orderHandlerService.getMtsIsCache(third, 1);
                    String infoMsg = v.getThird() + "滚球PA拒单";
                    if (StringUtils.isNotBlank(errorMessage.getHintMsg())) {
                        infoMsg = infoMsg + ":" + errorMessage.getHintMsg();
                    }
                    orderHandlerService.updateOrder(ext, OrderInfoStatusEnum.SCROLL_REFUSE.getCode(), infoMsg, mtsIsCache);
                    confirmPoolExecutor.execute(() -> {
                        orderHandlerService.notifyThirdUpdateOrder(ext);
                    });
                    return;
                }
            }
            //重推到接拒队列
            v.setRejectNum(v.getRejectNum() + 1);
            sendMessageUtils.sendDelayMessage(RCS_RISK_THIRD_ORDER_REJECT, third + "_ORDER_REJECT_" + v.getRejectNum(), v.getOrderNo(), v);
            //producer.sendMessage(RCS_RISK_THIRD_ORDER_REJECT, third + "_ORDER_REJECT_" + v.getRejectNum(), v.getOrderNo(), v);
        } catch (Exception e) {
            log.info("::{}::{}订单接拒消费异常:", orderNo, third, e);
        }
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    private void rejectBet() {

    }
}
