package com.panda.sport.rcs.mgr.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@RefreshScope
public class RealTimeControlUtils {

    @Value("${rcs.filter.merchantIds:2}")
    private String merchantIds;

    @Value("${rcs.trade.auto.filter.amount}")
    private Long automaticFilterAmount;

    @Value("${rcs.order.settle.handle.status:true}")
    private boolean settleHandleStatus;

    @Value("${rcs.quota.rollback.filter.amount}")
    private Long quotaRollbackFilterAmount;

}
