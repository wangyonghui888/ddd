/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.panda.sport.rcs.mts.sportradar.listeners;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.mapper.TOrderMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.mts.sportradar.builder.TicketBuilderHelper;
import com.panda.sport.rcs.mts.sportradar.constants.Constants;
import com.panda.sport.rcs.mts.sportradar.service.impl.MtsServiceImpl;
import com.panda.sport.rcs.mts.sportradar.wrapper.MtsCommonService;
import com.panda.sport.rcs.mts.sportradar.wrapper.RcsMtsOrderExtService;
import com.panda.sport.rcs.pojo.RcsMtsOrderExt;
import com.panda.sport.rcs.pojo.TOrder;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.sportradar.mts.sdk.api.*;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.enums.TicketAcceptance;
import com.sportradar.mts.sdk.api.interfaces.TicketAckSender;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelSender;
import com.sportradar.mts.sdk.api.interfaces.TicketResponseListener;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

/**
 * MTS订单接收回调处理
 */
public class TicketResponseHandler extends PublishResultHandler<Ticket> implements TicketResponseListener {

    private static final Logger logger = LoggerFactory.getLogger(TicketResponseHandler.class);

    private static TicketCancelSender ticketCancelSender;
    private static TicketAckSender ticketAckSender;
    private static BuilderFactory builderFactory;

    public TicketResponseHandler(TicketCancelSender ticketCancelSender,
                                 TicketAckSender ticketAckSender,
                                 BuilderFactory builderFactory) {
        TicketResponseHandler.ticketCancelSender = ticketCancelSender;
        TicketResponseHandler.ticketAckSender = ticketAckSender;
        TicketResponseHandler.builderFactory = builderFactory;
    }


    public static TicketCancelSender getTicketCancelSender() {
        return ticketCancelSender;
    }

    public static BuilderFactory getBuilderFactory() {
        return builderFactory;
    }

    public static void main(String[] args) {
        System.out.println(TicketAcceptance.ACCEPTED.name());
    }

    @Override
    public void responseReceived(TicketResponse ticketResponse) {
        try {
            logger.info("mts订单验证回调ticketId:{},status:{}", ticketResponse.getTicketId(), ticketResponse.getStatus());
            String ticketId = ticketResponse.getTicketId();
            String status = ticketResponse.getStatus().name();
            String jsonValue = ticketResponse.getJsonValue();
            int reasonCode = ticketResponse.getReason().getCode();
            String reasonMsg = ticketResponse.getReason().getMessage();
            List<AutoAcceptedOdds> autoAcceptedOddsList= ticketResponse.getAutoAcceptedOdds();
            if (StringUtils.isEmpty(ticketId) && StringUtils.isEmpty(status)) {
                return;
            }
            String orderNo = ticketId.substring(ticketId.indexOf("-") + 1);

            MtsCommonService commonService = SpringContextUtils.getBeanByClass(MtsCommonService.class);

            commonService.updateMtsOrder(ticketId, status, orderNo, ticketResponse.getAutoAcceptedOdds(), ticketResponse.getJsonValue(),reasonCode,reasonMsg, 1);

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
        }
    }









    @Override
    public void onTicketResponseTimedOut(Ticket ticket) {
        logger.warn("Sending ticket {} timed-out", ticket.getTicketId());
    }

}
