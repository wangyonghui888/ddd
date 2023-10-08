package com.panda.sport.rcs.third.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.OrderTypeEnum;
import com.panda.sport.rcs.third.entity.common.ThirdOrderExt;
import com.panda.sport.rcs.third.factory.ThirdStrategyFactory;
import com.panda.sport.rcs.third.service.handler.IOrderHandlerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

import static com.panda.sport.rcs.third.common.Constants.*;


@Component
@Slf4j
@RocketMQMessageListener(topic = "rcs_risk_third_order_retry", consumerGroup = "rcs_risk_third_order_retry_group",
        messageModel = MessageModel.CLUSTERING, consumeMode = ConsumeMode.CONCURRENTLY)
public class ThirdRetryConsumer implements RocketMQListener<JSONObject>, RocketMQPushConsumerLifecycleListener {
    @Resource(name = "orderHandlerServiceImpl")
    IOrderHandlerService orderHandlerService;

    @Resource(name = "betPoolExecutor")
    private ThreadPoolExecutor betPoolExecutor;

    @Resource(name = "cancelPoolExecutor")
    private ThreadPoolExecutor cancelPoolExecutor;

    @Resource(name = "confirmPoolExecutor")
    private ThreadPoolExecutor confirmPoolExecutor;
    @Resource
    RedisClient redisClient;


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }


    @Override
    public void onMessage(JSONObject dataMap) {
        StopWatch sw = new StopWatch();
        sw.start();
        String orderId = null, third = null;
        try {
            //转换订单参数
            ThirdOrderExt ext = JSONObject.parseObject(JSONObject.toJSONString(dataMap), ThirdOrderExt.class);
            if (ext == null || CollectionUtils.isEmpty(ext.getList())) {
                log.warn("注单信息转换为null,请检查!入参消息= {}", JSONObject.toJSONString(dataMap));
                return;
            }
            MDC.put("linkId", ext.getLinkId());
            ExtendBean firstExtendBean = ext.getList().get(0);
            orderId = firstExtendBean.getOrderId();
            third = ext.getThird();
            String finalThird = third;
            if (ext.getRetryType() == 1) {
                log.info("::{}::{}订单-投注重试处理收到:{}", orderId, third, JSONObject.toJSON(ext));
                if (OrderTypeEnum.CTS.getPlatFrom().equals(third)) {
                    //BC特殊处理
                    betPoolExecutor.execute(() -> {
                        ThirdStrategyFactory.getThirdStrategy(finalThird).placeBet(ext);
                    });
                } else {
                    betPoolExecutor.execute(() -> {
                        orderHandlerService.orderByThird(ext);
                    });
                }
                //重试一次之后不再重试
                //redisClient.setExpiry(String.format(GTS_RETRY_STATUS_BET, orderId), "1", 10L);
                redisClient.incrBy(String.format(GTS_RETRY_STATUS_BET, orderId), 1L, 10);
            }
            //取消 - 重试
            if (ext.getRetryType() == 2) {
                log.info("::{}::{}订单-取消重试处理收到:{}", orderId, third, JSONObject.toJSON(ext));
                cancelPoolExecutor.execute(() -> {
                    ThirdStrategyFactory.getThirdStrategy(finalThird).orderCancel(ext);
                });
                //重试一次之后不再重试
                //redisClient.setExpiry(String.format(GTS_RETRY_STATUS_CANCEL, orderId), "1", 10L);
                redisClient.incrBy(String.format(GTS_RETRY_STATUS_CANCEL, orderId), 1L, 10);
            }
            //确认 - 重试
            if (ext.getRetryType() == 3) {
                log.info("::{}::{}订单-确认重试处理收到:{}", orderId, third, JSONObject.toJSON(ext));
                confirmPoolExecutor.execute(() -> {
                    orderHandlerService.notifyThirdUpdateOrder(ext);
                });
                //重试一次之后不再重试
                //redisClient.setExpiry(String.format(GTS_RETRY_STATUS_RECEIVE, orderId), "1", 10L);
                redisClient.incrBy(String.format(GTS_RETRY_STATUS_RECEIVE, orderId), 1L, 10);
            }

        } catch (Exception e) {
            log.info("::{}::{}订单重试[rcs_risk_third_order_retry]处理异常", orderId, third, e);
        } finally {
            MDC.remove("linkId");
            sw.stop();
            log.info("::{}::{}订单重试[rcs_risk_third_order_retry]处理耗时:{}", orderId, third, sw.getTotalTimeMillis());
        }
    }
}
