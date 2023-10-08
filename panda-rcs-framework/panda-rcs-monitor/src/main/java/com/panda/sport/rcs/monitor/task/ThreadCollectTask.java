package com.panda.sport.rcs.monitor.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.monitor.entity.ThreadBean;
import com.panda.sport.rcs.monitor.mq.SendMqApi;
import com.panda.sport.rcs.monitor.service.impl.ThreadInfoServiceImpl;

@Component
@ConditionalOnBean(name = "sendMqApi")
public class ThreadCollectTask extends CollectTaskApi{
	
    public ThreadCollectTask() {
    	super(20l);
	}

	@Autowired
    private ThreadInfoServiceImpl threadInfoServiceImpl;
    
    @Autowired
    private SendMqApi sendMqApi;
    
    private String key = String.format("THREAD_%s_%s", ip , pid);
    
	@Override
	public void execute() {
		ThreadBean bean = threadInfoServiceImpl.get();
		sendMqApi.execute(topic, String.valueOf(System.currentTimeMillis()), key, bean);
	}

}
