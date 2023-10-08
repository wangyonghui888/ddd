package com.panda.rcs.stray.limit.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@RefreshScope
public class RealTimeControlUtils {

    @Value("${rcs.order.settle.handle.status:false}")
    private boolean settleHandleStatus;


    @Value("${rcs.order.check.limit.switch:false}")
    private boolean limitSwitch;
}
