package com.panda.rcs.sdk.monitor;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.monitor.mq.SendMqApi;
import com.panda.sport.sdk.mq.Producer;
import com.panda.sport.sdk.util.GuiceContext;

@Component("sendMqApi")
@ConditionalOnProperty("sdk.rocketmq.address")
public class ServiceDataCollect implements SendMqApi{
	
	private Producer producer = null;
	
	public ServiceDataCollect() {
		producer = GuiceContext.getInstance(Producer.class);
	}

	@Override
	public void execute(String topic, String tag, String key, Object msg, Map<String, String> properties) {
		producer.sendMessage(topic, tag, key, JSONObject.toJSONString(msg),properties);
	}

	
}
