package com.panda.sport.rcs.limit.mq.bean;

import com.panda.sport.rcs.mq.bean.RocketProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("rocketmq.limit")
public class RocketRpcProperties extends RocketProperties{

}
