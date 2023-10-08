package com.panda.sport.rcs.oddin.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.oddin.OddinOrderInfoDto;
import com.panda.sport.data.rcs.dto.oddin.TicketDto;
import com.panda.sport.data.rcs.vo.oddin.TicketVo;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.MatchTypeReportEnum;
import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrder;
import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrderDj;
import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrderTy;
import com.panda.sport.rcs.oddin.entity.ots.TicketOuterClass;
import com.panda.sport.rcs.oddin.enums.DataSourceEnum;
import com.panda.sport.rcs.oddin.grpc.handler.TicketGrpcHandler;
import com.panda.sport.rcs.oddin.service.RcsOrderService;
import com.panda.sport.rcs.oddin.service.TicketOrderService;
import com.panda.sport.rcs.oddin.service.handler.TicketOrderHandler;
import com.panda.sport.rcs.oddin.util.DjMqUtils;
import com.panda.sport.rcs.oddin.util.SendMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import static com.panda.sport.rcs.cache.CaCheKeyConstants.ODDIN_ORDER_INFO_KEY;
import static com.panda.sport.rcs.oddin.common.Constants.*;

/**
 * @author Beulah
 * @date 2023/6/3 11:54
 * @description oddin注单回调处理消费
 */
@Component
@Slf4j
@RocketMQMessageListener(topic = RCS_RISK_ODDIN_ORDER_CALLBACK, consumerGroup = "rcs_risk_oddin_order_callback_group",
        messageModel = MessageModel.CLUSTERING, consumeMode = ConsumeMode.CONCURRENTLY, consumeThreadMax = 256, consumeTimeout = 10000L)
public class OddinOrderCallbackConsumer implements RocketMQListener<JSONObject>, RocketMQPushConsumerLifecycleListener {


    @Resource
    private TicketGrpcHandler ticketGrpcHandler;
    @Resource
    private SendMessageUtils rocketProducer;
    @Resource
    private TicketOrderHandler ticketOrderHandler;
    @Resource
    private RedisClient redisClient;
    @Resource
    private RcsOrderService rcsOrderService;
    @Resource
    private DjMqUtils djMqUtils;
    @Resource
    private TicketOrderService ticketOrderService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);

    }


    @Override
    public void onMessage(JSONObject message) {
        try {
            //转换订单参数
            TicketVo vo = JSONObject.parseObject(message.toJSONString(), TicketVo.class);
            log.info("=={}::处理订单回调orderNo:{}回调数据===={}", MDC.get("linkId"), vo.getId(), JSONObject.toJSONString(vo));
            callbackTicket(vo);
        } catch (Exception e) {
            log.error("早盘投注-mq异步处理异常--{}", e.getMessage(), e);
        }

    }

    /**
     * 注单成功回调数据处理
     *
     * @param vo oddin注单回调数据
     */
    private void callbackTicket(TicketVo vo) {
        String orderNo = vo.getId();
        if (vo != null) {
            RcsOddinOrder order = null;
            if (DataSourceEnum.TY.getCode().equals(vo.getSourceId())) {
                order = new RcsOddinOrderTy();
            }
            if (DataSourceEnum.DJ.getCode().equals(vo.getSourceId())) {
                order = new RcsOddinOrderDj();
            }
            ticketGrpcHandler.transferOrder(order, vo);
            try {
                //从缓存中去除体育早盘赛事缓存信息
                String key = String.format(ODDIN_ORDER_INFO_KEY, orderNo);
                String orderInfoJson = redisClient.get(key);
                if (StringUtils.isNotBlank(orderInfoJson)) {
                    OddinOrderInfoDto dto = JSONObject.parseObject(orderInfoJson, OddinOrderInfoDto.class);
                    if (Objects.nonNull(dto)) {
                        //将体育相关的注单的orderGroup放到vo中，后续推到相应的mq
                        vo.setOrderGroup(dto.getOrderGroup());
                        //如果是早盘，则要删除响应早盘为了监听4秒超时的缓存
                        if (MatchTypeReportEnum.BEFORE_MATCH.getCode().equals(dto.getMatchType())) {
                            ticketOrderHandler.removeEarlyOrderBettingStatus(orderNo);
                        }
                    }
                }
                //体育的业务注单走内部接单逻辑
                if (DataSourceEnum.TY.getCode().equals(vo.getSourceId())) {
                    String voidedKey = "RESULTING_STATUS_VOIDED_ORDER_" + orderNo;
                    String status = redisClient.get(voidedKey);
                    if (StringUtils.isNotBlank(status)) {
                        //验证无效单的处理
                        vo.setTicket_status("RESULTING_STATUS_VOIDED");
                    } else {
                        vo.setTicket_status("ACCEPTANCE_STATUS_ACCEPTED");
                    }
                    redisClient.setExpiry(voidedKey, "1", 10L);
                    rcsOrderService.rejectOrder(vo);

                }
                //电竞的注单回调透传给电竞(电竞自己处理)
                if (DataSourceEnum.DJ.getCode().equals(vo.getSourceId())) {
                    djMqUtils.sendMessage(RCS_RISK_ODDIN_TICKET_TO_DJ, "ODDIN-DJ-ORDER", vo.getId(), JSONObject.toJSONString(vo));
                }
                  //更新订单表中的订单状态
                ticketOrderService.updateOrder(order, vo.getSourceId());

                Thread.sleep(5000L);
                rocketProducer.sendMessage(RCS_RISK_ODDIN_ORDER_CALLBACK, "oddin_ticket_callback", orderNo, vo);
            } catch (Exception e) {
                log.error("::{}::{}::处理注单回调异常:", ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, e);
            }
        }
    }
}
