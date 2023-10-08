package com.panda.sport.rcs.monitor.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.monitor.entity.MemoryBean;
import com.panda.sport.rcs.monitor.mq.SendMqApi;
import com.panda.sport.rcs.monitor.service.MemoryService;

@Component
@ConditionalOnBean(name = "sendMqApi")
public class MemoryCollectTask extends CollectTaskApi{
	
    public MemoryCollectTask() {
		super(20l);
	}

	@Autowired
    private MemoryService memoryService;
    
    @Autowired
    private SendMqApi sendMqApi;
    
    private String key = String.format("MEMORY_%s_%s", ip , pid);
    
	@Override
	public void execute() {
		MemoryBean bean = memoryService.get();
		sendMqApi.execute(topic, String.valueOf(System.currentTimeMillis()), key, bean);
	}

}
