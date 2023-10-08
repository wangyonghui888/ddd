/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.panda.sport.rcs.mts.sportradar.listeners;

import com.sportradar.mts.sdk.api.TicketCancelAck;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelAckResponseListener;

public class TicketCancelAckHandler extends PublishResultHandler<TicketCancelAck> implements TicketCancelAckResponseListener {

}
