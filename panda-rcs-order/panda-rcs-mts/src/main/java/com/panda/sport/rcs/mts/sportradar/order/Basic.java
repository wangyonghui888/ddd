/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.panda.sport.rcs.mts.sportradar.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.panda.sport.rcs.mts.sportradar.builder.TicketBuilderHelper;
import com.panda.sport.rcs.mts.sportradar.constants.Constants;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketAckHandler;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketCancelAckHandler;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketCancelResponseHandler;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketResponseHandler;
import com.sportradar.mts.sdk.api.Ticket;
import com.sportradar.mts.sdk.api.interfaces.MtsSdkApi;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.interfaces.TicketAckSender;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelAckSender;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelSender;
import com.sportradar.mts.sdk.api.interfaces.TicketSender;
import com.sportradar.mts.sdk.app.MtsSdk;

/**
 * Basic example of creating and sending ticket
 */
public class Basic {
    private static final Logger logger = LoggerFactory.getLogger(Basic.class);

    public static void main(String[] args) {
        Run();
    }


    public static void Run()    {
        SdkConfiguration config = MtsSdk.getConfiguration();
        Constants.setConfig(config);
        MtsSdkApi mtsSdk = new MtsSdk(config);
        mtsSdk.open();
        TicketAckSender ticketAckSender = mtsSdk.getTicketAckSender(new TicketAckHandler());
        TicketCancelAckSender ticketCancelAckSender = mtsSdk.getTicketCancelAckSender(new TicketCancelAckHandler());
        TicketCancelSender ticketCancelSender = mtsSdk.getTicketCancelSender(new TicketCancelResponseHandler(ticketCancelAckSender, mtsSdk.getBuilderFactory()));
        TicketResponseHandler ticketResponseHandler = new TicketResponseHandler(ticketCancelSender, ticketAckSender, mtsSdk.getBuilderFactory());
        TicketSender ticketSender = mtsSdk.getTicketSender(ticketResponseHandler);

        Ticket ticket = new TicketBuilderHelper(mtsSdk.getBuilderFactory()).getTicket();
        //Notice: there are two ways of sending tickets to MTS (non-blocking is recommended)

        //send non-blocking (the TicketResult will we handled in TicketResponseHandler)
        ticketSender.send(ticket);
        try {
            Thread.sleep(1000);
            logger.info("开始处理计算maxStake接口....");
//            long maxStake = mtsSdk.getClientApi().getMaxStake(ticket);
//            logger.info("maxStake接口结果"+maxStake);
        } catch (InterruptedException e) {
            logger.info("interrupted while sleeping");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mtsSdk.close();
    }
}
