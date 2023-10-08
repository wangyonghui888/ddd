package com.panda.sport.rcs.monitor.mq;

import com.panda.sport.rcs.monitor.entity.MqConsumerStatsBean;

public interface MQConsumerAdminApi {

	public MqConsumerStatsBean getConsumeStats(String groupName);
	
	public Boolean isCheckPass();
	
}
