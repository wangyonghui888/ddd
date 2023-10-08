package com.panda.sport.rcs.mq.bean;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;

@Data
public class RocketProperties {
	
	private String group;
	
	private String instance;
	
	@Value("${rocketmq.nameSrvAddr}")
	private String nameSrvAddr;
	
	private String topics;
	
}
