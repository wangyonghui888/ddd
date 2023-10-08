package com.panda.sport.rcs.predict.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.RcsLockMapper;
import com.panda.sport.rcs.predict.service.PredictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import java.util.UUID;

/**
 * forecast计算 消费
 *
 * @author lithan
 * @since 2021-2-21 10:31:19
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "rcs_forecast_cancel_order",
        consumerGroup = "rcs_forecast_cancel_order_java",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class ForecastCancelOrderConsumer implements RocketMQListener<OrderBean>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }
    @Autowired
    private RcsLockMapper lockMapper;

    @Autowired
    private PredictService predictService;

    @Autowired
    private RedisClient redisClient;

    public ForecastCancelOrderConsumer() {
//        super("rcs_forecast_cancel_order", "");
    }

    @Override
    public void onMessage(OrderBean orderBean) {
        try {
            MDC.put("X-B3-TraceId", UUID.randomUUID().toString().replace(",", ""));
            log.info("预测数据计算 业务拒单反计算:{}", JSONObject.toJSONString(orderBean));

            boolean calculateStatus = redisClient.setNX("rcs:predict:calculate:cancel:bet:" + orderBean.getOrderNo(), "1", 3 * 24 * 60 * 60L);
            /**
             * 货量 期望 回滚
             * 说明:如先成功 再失败:calculateStatus会标志为1  则做回滚
             *     如先失败 再成功:calculateStatus为空,不做回滚,  再成功更新的时候,上面就return了 不做下面的逻辑(也不会增加货量)
             */
            String status = redisClient.get("rcs:predict:calculate:bet:" + orderBean.getOrderNo());
            if (StringUtils.isBlank(status)) {
                log.info("预测数据计算 业务拒单反计算-跳过,之前并无成功计算:{}", JSONObject.toJSONString(orderBean));
                return ;
            }

            //boolean calculateStatus = lockMapper.saveLock("rcs:predict:calculate:cancel:bet:" + orderBean.getOrderNo()) > 0;
            if (calculateStatus) {
                //拒单实货量 forecast等 反计算
                if (orderBean.getSeriesType() != 1) {
                    //串关
                    predictService.calculateSeries(orderBean, -1);
                } else {
                    //单关
                    predictService.calculate(orderBean, -1);
                }

            } else {
                log.error("预测数据计算 反计算订单已处理,不再做重复计算,订单号:" + orderBean.getOrderNo());
            }
        } catch (Exception e) {
            log.error("{}预测数据反计算  MQ异常：{}{}", orderBean.getOrderNo(), e.getMessage(), e);
        }
        return ;
    }
}
