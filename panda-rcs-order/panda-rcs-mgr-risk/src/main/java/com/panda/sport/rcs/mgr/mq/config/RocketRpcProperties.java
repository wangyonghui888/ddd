package com.panda.sport.rcs.mgr.mq.config;

import com.panda.sport.rcs.mq.bean.RocketProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("rocketmq.rpc")
public class RocketRpcProperties extends RocketProperties{

}
