package com.panda.sport.rcs.virtual.mq.bean;

import com.panda.sport.rcs.mq.bean.RocketProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("rocketmq.virtual")
public class RocketRpcProperties extends RocketProperties{

}
