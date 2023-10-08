/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.panda.sport.rcs.mts.sportradar.order;

import com.panda.sport.rcs.mts.sportradar.listeners.*;
import com.sportradar.mts.sdk.api.Ticket;
import com.sportradar.mts.sdk.api.TicketAck;
import com.sportradar.mts.sdk.api.TicketCashout;
import com.sportradar.mts.sdk.api.TicketResponse;
import com.sportradar.mts.sdk.api.enums.TicketAcceptance;
import com.sportradar.mts.sdk.api.exceptions.ResponseTimeoutException;
import com.sportradar.mts.sdk.api.interfaces.*;
import com.sportradar.mts.sdk.app.MtsSdk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.panda.sport.rcs.mts.sportradar.builder.TicketBuilderHelper;

/**
 * Example of creating and sending cashout ticket
 */
public class Cashout {
    private static final Logger logger = LoggerFactory.getLogger(Cashout.class);

    public  void Run()
    {
        SdkConfiguration config = MtsSdk.getConfiguration();
        MtsSdkApi mtsSdk = new MtsSdk(config);
        mtsSdk.open();
        TicketAckSender ticketAckSender = mtsSdk.getTicketAckSender(new TicketAckHandler());
        TicketCancelAckSender ticketCancelAckSender = mtsSdk.getTicketCancelAckSender(new TicketCancelAckHandler());
        TicketCancelSender ticketCancelSender = mtsSdk.getTicketCancelSender(new TicketCancelResponseHandler(ticketCancelAckSender, mtsSdk.getBuilderFactory()));
        TicketSender ticketSender = mtsSdk.getTicketSender(new TicketResponseHandler(ticketCancelSender, ticketAckSender, mtsSdk.getBuilderFactory()));
        TicketCashoutSender ticketCashoutSender = mtsSdk.getTicketCashoutSender(new TicketCashoutResponseHandler());

        TicketBuilderHelper ticketBuilderHelper = new TicketBuilderHelper(mtsSdk.getBuilderFactory());

        Ticket ticket = ticketBuilderHelper.getTicket();
        //Notice: there are two way of sending tickets to MTS (non-blocking is recommended)

        try {
            TicketResponse ticketResponse = ticketSender.sendBlocking(ticket);
            logger.info("ticket {} was {}", ticketResponse.getTicketId(), ticketResponse.getStatus());

            if(ticketResponse.getStatus() == TicketAcceptance.ACCEPTED) {

                // required only if 'explicit acking' is enabled in MTS admin
                TicketAck ticketAcknowledgment = ticketBuilderHelper.getTicketAck(ticketResponse);
                ticketAckSender.send(ticketAcknowledgment);

                TicketCashout ticketCashout = ticketBuilderHelper.getTicketCashout(ticket.getTicketId());
                ticketCashoutSender.send(ticketCashout);
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
