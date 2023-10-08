package com.panda.sport.rcs.mts.sportradar.mq.config;

import com.panda.sport.rcs.mq.bean.RocketProperties;
import com.panda.sport.rcs.mts.sportradar.mq.impl.OrderSendMtsConsumer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("rocketmq.mts")
public class RocketRpcProperties extends RocketProperties {

}
