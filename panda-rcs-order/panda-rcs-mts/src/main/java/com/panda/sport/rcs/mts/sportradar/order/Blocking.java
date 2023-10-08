/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.panda.sport.rcs.mts.sportradar.order;

import com.panda.sport.rcs.mts.sportradar.listeners.TicketAckHandler;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketCancelAckHandler;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketCancelResponseHandler;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketResponseHandler;
import com.sportradar.mts.sdk.api.*;
import com.sportradar.mts.sdk.api.enums.TicketAcceptance;
import com.sportradar.mts.sdk.api.exceptions.ResponseTimeoutException;
import com.sportradar.mts.sdk.api.interfaces.*;
import com.sportradar.mts.sdk.app.MtsSdk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.panda.sport.rcs.mts.sportradar.builder.TicketBuilderHelper;

/**
 * Blocking example of creating and sending ticket
 */
public class Blocking {
    private static final Logger logger = LoggerFactory.getLogger(Blocking.class);

    public  void Run()
    {
        SdkConfiguration config = MtsSdk.getConfiguration();
        MtsSdkApi mtsSdk = new MtsSdk(config);
        mtsSdk.open();
        TicketAckSender ticketAckSender = mtsSdk.getTicketAckSender(new TicketAckHandler());
        TicketCancelAckSender ticketCancelAckSender = mtsSdk.getTicketCancelAckSender(new TicketCancelAckHandler());
        TicketCancelSender ticketCancelSender = mtsSdk.getTicketCancelSender(new TicketCancelResponseHandler(ticketCancelAckSender, mtsSdk.getBuilderFactory()));
        TicketSender ticketSender = mtsSdk.getTicketSender(new TicketResponseHandler(ticketCancelSender, ticketAckSender, mtsSdk.getBuilderFactory()));

        TicketBuilderHelper ticketBuilderHelper = new TicketBuilderHelper(mtsSdk.getBuilderFactory());

        Ticket ticket = ticketBuilderHelper.getTicket();

        try {
            TicketResponse ticketResponse = ticketSender.sendBlocking(ticket);
            logger.info("ticket {} was {}", ticketResponse.getTicketId(), ticketResponse.getStatus());

            if(ticketResponse.getStatus() == TicketAcceptance.ACCEPTED) {

                // required only if 'explicit acking' is enabled in MTS admin
                TicketAck ticketAcknowledgment = ticketBuilderHelper.getTicketAck(ticketResponse);
                ticketAckSender.send(ticketAcknowledgment);

                //if in some case we want to cancel already accepted ticket
                if (Math.random() > 0.3) {
                    TicketCancel ticketCancel = ticketBuilderHelper.getTicketCancel(ticketResponse.getTicketId());
                    //send blocking (the TicketCancelResult will we handled in TicketCancelResponseHandler)
                    TicketCancelResponse ticketCancelResponse = ticketCancelSender.sendBlocking(ticketCancel);

                    logger.info("ticket {} was {}", ticketCancelResponse.getTicketId(), ticketCancelResponse.getStatus());

                    // required only if 'explicit acking' is enabled in MTS admin
                    TicketCancelAck ticketCancelAck = ticketBuilderHelper.getTicketCancelAck(ticketCancelResponse);
                    ticketCancelAckSender.send(ticketCancelAck);
                }
            }
        } catch (ResponseTimeoutException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.info("interrupted while sleeping");
        }
        mtsSdk.close();
    }
}
