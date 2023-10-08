package com.panda.sport.rcs.monitor.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.monitor.entity.MqConsumerStatsBean;
import com.panda.sport.rcs.monitor.mq.MQConsumerAdminApi;
import com.panda.sport.rcs.monitor.mq.SendMqApi;
import com.panda.sport.rcs.monitor.utils.OsUtis;

@Component
@ConditionalOnBean(name = "mQConsumerAdminApi")
public class MQTopicCollectTask extends CollectTaskApi{
	
	@Autowired
	private MQConsumerAdminApi mQConsumerAdminApi;
	
    public MQTopicCollectTask() {
		super(30l);
	}
    
    @Autowired
    private SendMqApi sendMqApi;
    
    private String key = String.format("MQ_%s_%s", ip , pid);
    
	@Override
	public void execute() {
		if(StringUtils.isBlank(OsUtis.getMqGroupName())) return;
		MqConsumerStatsBean bean = mQConsumerAdminApi.getConsumeStats(OsUtis.getMqGroupName());
		bean.setGroupName(OsUtis.getMqGroupName());
		if(bean.getOffsetTable() != null && bean.getOffsetTable().size() > 0 ) {
			Map<String, Long> infoMap = new HashMap<String, Long>(); 
			for(Map<String, Object> key : bean.getOffsetTable().keySet()) {
				Map<String, Object> val = bean.getOffsetTable().get(key);
				String topic = String.valueOf(key.get("topic"));
				
				if(!infoMap.containsKey(topic))  infoMap.put(topic, 0l);
				
				Long diff = Long.parseLong(String.valueOf(val.get("brokerOffset"))) - Long.parseLong(String.valueOf(val.get("consumerOffset")));
				infoMap.put(topic, diff + infoMap.get(topic));
			}
			bean.setMergeTopicData(infoMap);
			bean.setOffsetTable(null);
			bean.setVersionId(System.currentTimeMillis() + "");
		}else {
			return;
		}
		
		sendMqApi.execute(topic, String.valueOf(System.currentTimeMillis()), key, bean);
	}

	@Override
	public boolean isStart() {
		return mQConsumerAdminApi.isCheckPass();
	}
	
}
