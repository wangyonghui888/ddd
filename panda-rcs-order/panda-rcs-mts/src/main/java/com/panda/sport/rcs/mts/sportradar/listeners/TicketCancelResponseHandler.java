/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.panda.sport.rcs.mts.sportradar.listeners;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.mts.sportradar.builder.TicketBuilderHelper;
import com.panda.sport.rcs.mts.sportradar.wrapper.RcsMtsOrderExtService;
import com.panda.sport.rcs.pojo.RcsMtsOrderExt;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.sportradar.mts.sdk.api.TicketCancel;
import com.sportradar.mts.sdk.api.TicketCancelAck;
import com.sportradar.mts.sdk.api.TicketCancelResponse;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelAckSender;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class TicketCancelResponseHandler extends PublishResultHandler<TicketCancel> implements TicketCancelResponseListener {

    private static final Logger logger = LoggerFactory.getLogger(TicketResponseHandler.class);

    private final TicketCancelAckSender ticketCancelAckSender;
    private final BuilderFactory builderFactory;

    public TicketCancelResponseHandler(TicketCancelAckSender ticketCancelAckSender,
                                       BuilderFactory builderFactory) {
        this.ticketCancelAckSender = ticketCancelAckSender;
        this.builderFactory = builderFactory;
    }

    @Override
    public void responseReceived(TicketCancelResponse ticketCancelResponse) {
        logger.info("TicketCancelResponseHandler-ticket {} was {}", JSONObject.toJSONString(ticketCancelResponse), ticketCancelResponse.getStatus());

        String ticketId = ticketCancelResponse.getTicketId();
        String orderNo =  ticketId.replace("TicketId-","");

        RcsMtsOrderExtService rcsMtsOrderExtService = SpringContextUtils.getBeanByClass(RcsMtsOrderExtService.class);

        LambdaQueryWrapper<RcsMtsOrderExt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsMtsOrderExt::getOrderNo,orderNo);
        RcsMtsOrderExt rcsMtsOrderExt = rcsMtsOrderExtService.getOne(wrapper);

        rcsMtsOrderExt.setCancelResult(JSONObject.toJSONString(ticketCancelResponse));
        rcsMtsOrderExtService.saveOrUpdate(rcsMtsOrderExt);
        logger.info("订单取消处理:{}",rcsMtsOrderExt);

        if(rcsMtsOrderExt.getCancelId() != 102) {
            TicketCancelAck ticketCancelAck = new TicketBuilderHelper(builderFactory).getTicketCancelAck(ticketCancelResponse);
            ticketCancelAckSender.send(ticketCancelAck);
        }

        //通知业务 取消状态 0 未取消  1取消成功
        int cancelStatus =0;
        String msg = "取消失败:"+ticketCancelResponse.getReason().getMessage();
        if (ticketCancelResponse.getReason().getCode() == -999) {
            cancelStatus = 1;
            msg = "取消成功(mts未接受到此订单)" + ticketCancelResponse.getReason().getMessage();
        }
        if (ticketCancelResponse.getReason().getCode() == 1024) {
            cancelStatus = 1;
            msg = "取消成功:"+ticketCancelResponse.getReason().getMessage();
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderNo", orderNo);
        map.put("msg", msg);
        map.put("cancelStatus", cancelStatus);
        ProducerSendMessageUtils sendMessage = SpringContextUtils.getBeanByClass(ProducerSendMessageUtils.class);
        sendMessage.sendMessage("mts_order_cancel_status,," + orderNo, map);
        logger.info("mts订单取消处理:通知业务完成{}", JSONObject.toJSONString(map));
    }
}
