package com.panda.sport.rcs.monitor.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.monitor.entity.HeartCollectorBean;
import com.panda.sport.rcs.monitor.mq.SendMqApi;

@Component
@ConditionalOnBean(name = "sendMqApi")
public class HeartCollectTask extends CollectTaskApi{
	
    public HeartCollectTask() {
		super(10l);
	}
    
    @Autowired
    private SendMqApi sendMqApi;
    
    private String key = String.format("HEART_%s_%s", ip , pid);
    
	@Override
	public void execute() {
		HeartCollectorBean heartBean = new HeartCollectorBean(System.currentTimeMillis());
		sendMqApi.execute(topic, String.valueOf(System.currentTimeMillis()), key, heartBean);
	}

}
