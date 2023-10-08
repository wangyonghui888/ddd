package com.panda.sport.rcs.credit.mq;

import com.panda.sport.rcs.mq.bean.RocketProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("rocketmq.credit")
public class RocketRpcProperties extends RocketProperties{

}
