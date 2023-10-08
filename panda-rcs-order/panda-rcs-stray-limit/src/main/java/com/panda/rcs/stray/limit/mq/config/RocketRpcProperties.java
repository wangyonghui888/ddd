package com.panda.rcs.stray.limit.mq.config;

import com.panda.sport.rcs.mq.bean.RocketProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("rocketmq.stray")
public class RocketRpcProperties extends RocketProperties {

}
