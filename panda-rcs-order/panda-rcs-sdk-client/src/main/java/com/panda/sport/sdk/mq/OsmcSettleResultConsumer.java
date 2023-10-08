package com.panda.sport.sdk.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.sdk.service.impl.OrderPaidApiImpl;
import com.panda.sport.sdk.util.GuiceContext;
import com.panda.sport.sdk.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * 结算派彩
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "OSMC_SETTLE_RESULT_SDK",
        consumerGroup = "OSMC_SETTLE_RESULT_SDK_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class OsmcSettleResultConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {
    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Override
    public void onMessage(String message) {
        SettleItem requestParam = null;
        try {
            requestParam = JSONObject.parseObject(message, SettleItem.class);
        } catch (Exception e) {
            log.error("::{}::结算派彩message转换异常：", message, e);
        }
        if (requestParam == null) {
            log.warn("::{}::结算派彩转换对象requestParam为空,跳过::", message);
            return;
        }
        log.info("::{}::结算派彩处理,接收到：{}", requestParam.getOrderNo(), JSON.toJSONString(requestParam));
        Request<SettleItem> settleItemRequest = new Request<>();
        settleItemRequest.setData(requestParam);
        try {
            // 串关结算标志
            if (requestParam.getSeriesType() != 1) {
                requestParam.setRemark("series_settle");
            }
            GuiceContext.getInstance(OrderPaidApiImpl.class).updateOrderAfterRefund(settleItemRequest);
        } catch (Exception e) {
            log.error("::{}::结算派彩处理异常：{}", requestParam.getOrderNo(), e.getMessage(),e);
        }
    }

}
