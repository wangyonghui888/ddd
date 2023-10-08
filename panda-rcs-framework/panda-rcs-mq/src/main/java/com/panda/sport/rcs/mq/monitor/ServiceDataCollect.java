package com.panda.sport.rcs.mq.monitor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.monitor.mq.SendMqApi;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;

@Component("sendMqApi")
@ConditionalOnProperty("rocketmq.nameSrvAddr")
public class ServiceDataCollect implements SendMqApi{
	
	@Autowired
	private ProducerSendMessageUtils producerUtils;

	@Override
	public void execute(String topic, String tag, String key, Object msg, Map<String, String> properties) {
		producerUtils.sendMessage(topic, tag, key, msg,properties);
	}

	
}
