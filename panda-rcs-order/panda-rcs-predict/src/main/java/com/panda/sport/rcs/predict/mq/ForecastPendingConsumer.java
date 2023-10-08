package com.panda.sport.rcs.predict.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.PendingOrderDto;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.log.annotion.monnitor.MonitorAnnotion;
import com.panda.sport.rcs.predict.service.PredictPendingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * 预约投注 forecast计算 消费
 *
 * @author lithan
 * @since 2021-2-21 10:31:19
 */
@Component
@Slf4j
@MonitorAnnotion(code = "MQ_ORDER_PREDICT_CALC")
@TraceCrossThread
@RocketMQMessageListener(
        topic = "queue_realtimevolume_order_pending",
        consumerGroup = "queue_realtimevolume_order_pending_risk_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class ForecastPendingConsumer implements RocketMQListener<PendingOrderDto>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private PredictPendingService pendingService;


    public ForecastPendingConsumer() {
    }


    private static String convertOdds(String oddsValue) {
        String newValue = "", temp = "";
        if (oddsValue.contains("/")) {
            if (oddsValue.startsWith("+")) {
                newValue = oddsValue.replace("+", "");
            } else if (oddsValue.startsWith("-")) {
                newValue = oddsValue.replace("-", "");
                temp = "-";
            } else {
                newValue = oddsValue;
            }
            String[] split = newValue.split("/");
            BigDecimal divide = new BigDecimal(split[0]).add(new BigDecimal(split[1])).divide(new BigDecimal(2), 2, RoundingMode.DOWN);
            return temp + divide;
        }
        return oddsValue;
    }

    private static String convert(String oddsValue) {
        String newValue = "", temp = "";
        if (!oddsValue.contains("/") && (oddsValue.contains(".25") || oddsValue.contains(".75"))) {
            if (oddsValue.startsWith("+")) {
                newValue = oddsValue.replace("+", "");
            } else if (oddsValue.startsWith("-")) {
                newValue = oddsValue.replace("-", "");
                temp = "-";
            } else {
                newValue = oddsValue;
            }
            BigDecimal subtract = new BigDecimal(newValue).subtract(new BigDecimal("0.25")).stripTrailingZeros();
            BigDecimal add = new BigDecimal(newValue).add(new BigDecimal("0.25")).stripTrailingZeros();
            return temp + subtract + "/" + add;
        }
        return oddsValue;
    }

    @Override
    public void onMessage(PendingOrderDto pendingOrderDto) {
        try {
            MDC.put("X-B3-pending-TraceId", UUID.randomUUID().toString().replace(",", ""));
            log.info("MQ消息 预约投注 预测数据计算 收到订单 ：{}", JSONObject.toJSONString(pendingOrderDto));
            pendingOrderDto.setMarketValue(convert(pendingOrderDto.getMarketValue()));
            pendingService.calculate(pendingOrderDto, pendingOrderDto.getOrderStatus() == 0 ? 1 : -1);
            log.info("MQ消息 预约投注 预测数据计算完成： {}", MDC.get("X-B3-pending-TraceId"));
        } catch (Exception e) {
            log.error("{}预约投注 预测数据计算  MQ异常：{}{}", pendingOrderDto.getOrderNo(), e.getMessage(), e);
            redisClient.delete("Rcs:realVolume:queue:" + pendingOrderDto.getOrderNo());
        }
        return;
    }
}

