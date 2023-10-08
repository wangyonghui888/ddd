/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.panda.sport.rcs.mts.sportradar.listeners;

import com.sportradar.mts.sdk.api.SdkTicket;
import com.sportradar.mts.sdk.api.interfaces.PublishResultListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PublishResultHandler<T extends SdkTicket> implements PublishResultListener<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void publishFailure(T t) {
        logger.error("publish failed! we should check if we want to republish msg. message : {}", t);
    }

    @Override
    public void publishSuccess(T t) {
        logger.error("publish succeeded", t);
    }
}
