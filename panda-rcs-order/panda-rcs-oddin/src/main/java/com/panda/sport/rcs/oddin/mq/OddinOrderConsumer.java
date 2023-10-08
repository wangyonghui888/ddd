package com.panda.sport.rcs.oddin.mq;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.panda.sport.data.rcs.dto.oddin.TicketDto;
import com.panda.sport.rcs.oddin.service.TicketOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.panda.sport.rcs.oddin.common.Constants.RCS_RISK_ODDIN_TICKET;

/**
 * @author Beulah
 * @date 2023/6/3 11:54
 * @description oddin注单消费类
 */
@Component
@Slf4j
@RocketMQMessageListener(topic = RCS_RISK_ODDIN_TICKET, consumerGroup = "rcs_risk_oddin_ticket_group",
        messageModel = MessageModel.CLUSTERING, consumeMode = ConsumeMode.CONCURRENTLY, consumeTimeout = 10000L)
public class OddinOrderConsumer implements RocketMQListener<JSONObject>, RocketMQPushConsumerLifecycleListener {


    @Resource
    private TicketOrderService ticketOrderService;

    public static ExecutorService tyTicketExt = new ThreadPoolExecutor(64, 256,
            0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1024),
            new ThreadFactoryBuilder().setNameFormat("o01-tyTicket-%d").build(), new ThreadPoolExecutor.AbortPolicy());

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }


    @Override
    public void onMessage(JSONObject message) {
        String linkId = "";
        try {
            //转换订单参数
            TicketDto dto = JSONObject.parseObject(message.toJSONString(), TicketDto.class);
            if (Objects.isNull(dto)) {
                log.info("监听oddin注单请求入mq参为空，topic:{}", RCS_RISK_ODDIN_TICKET);
                return;
            }
            MDC.put("X-B3-TraceId", dto.getGlobalId());
            MDC.put("linkId", dto.getGlobalId() + " , " + dto.getCustomer().getId());
            linkId = MDC.get("linkId");

            log.info("==orderNo:{}监听注单请求===={}", dto.getId(), JSONObject.toJSONString(dto));

            tyTicketExt.execute(()->ticketOrderService.ticket(dto));

        } catch (Throwable e) {
            log.error("::{}::投注-mq异步处理异常--{}", linkId, e.getMessage(), e);
        }

    }

}
