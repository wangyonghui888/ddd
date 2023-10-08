package com.panda.sport.rcs.task.mq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.mq.bean.RocketProperties;

@Component
@ConfigurationProperties("rocketmq.task")
public class RocketTaskProperties extends RocketProperties{

}
