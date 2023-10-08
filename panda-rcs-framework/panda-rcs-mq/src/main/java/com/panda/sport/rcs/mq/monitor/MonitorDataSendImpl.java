package com.panda.sport.rcs.mq.monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.log.monitor.api.MonitorDataSendApi;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;

@Component("monitorDataSendApi")
@ConditionalOnProperty("rocketmq.nameSrvAddr")
public class MonitorDataSendImpl implements MonitorDataSendApi {
	
	@Autowired
	private ProducerSendMessageUtils producerUtils;

	@Override
	public void sendMonitorData(String topic,String tags ,String keys ,Object msg) {
		producerUtils.sendMessage(topic, tags, keys, msg);
	}

}
