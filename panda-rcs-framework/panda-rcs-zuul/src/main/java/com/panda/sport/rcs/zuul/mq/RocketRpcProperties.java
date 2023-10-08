package com.panda.sport.rcs.zuul.mq;

import com.panda.sport.rcs.mq.bean.RocketProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("rocketmq.zuul")
public class RocketRpcProperties extends RocketProperties{

}
