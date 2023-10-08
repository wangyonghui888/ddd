package com.panda.rcs.sdk.monitor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.log.monitor.api.MonitorDataSendApi;
import com.panda.sport.sdk.mq.Producer;
import com.panda.sport.sdk.util.GuiceContext;

@Component("monitorDataSendApi")
@ConditionalOnProperty("sdk.rocketmq.address")
public class MonitorDataSendApiImple implements MonitorDataSendApi {
	
	private Producer producer = null;
	
	public MonitorDataSendApiImple() {
		producer = GuiceContext.getInstance(Producer.class);
	}

	@Override
	public void sendMonitorData(String topic, String tags, String keys, Object msg) {
		producer.sendMsg(topic, tags, keys,JSONObject.toJSONString(msg));
	}

}
