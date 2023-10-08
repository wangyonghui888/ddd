package com.panda.sport.rcs.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@RefreshScope
public class RealTimeControlUtils {

    @Value("${rcs.order.limit.linkup.value:null}")
    private String limitLinkUpVal;
}
