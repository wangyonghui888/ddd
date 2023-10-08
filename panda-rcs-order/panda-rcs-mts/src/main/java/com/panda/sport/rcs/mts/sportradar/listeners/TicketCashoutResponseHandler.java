/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.panda.sport.rcs.mts.sportradar.listeners;

import com.sportradar.mts.sdk.api.TicketCashout;
import com.sportradar.mts.sdk.api.TicketCashoutResponse;
import com.sportradar.mts.sdk.api.interfaces.TicketCashoutResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TicketCashoutResponseHandler extends PublishResultHandler<TicketCashout> implements TicketCashoutResponseListener {

    private static final Logger logger = LoggerFactory.getLogger(TicketCashoutResponseHandler.class);

    @Override
    public void responseReceived(TicketCashoutResponse ticketCashoutResponse) {
        String reason = ticketCashoutResponse.getReason() != null ? ticketCashoutResponse.getReason().getMessage() : "No reason";
        logger.info("Ticket cashout response received: {}, with status {}, reason: {}",
                ticketCashoutResponse.getTicketId(), ticketCashoutResponse.getStatus().toString(), reason);
    }
}
