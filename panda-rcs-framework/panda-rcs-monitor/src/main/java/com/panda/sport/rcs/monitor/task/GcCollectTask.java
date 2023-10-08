package com.panda.sport.rcs.monitor.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import com.panda.sport.rcs.monitor.entity.GarbageCollectorBean;
import com.panda.sport.rcs.monitor.mq.SendMqApi;
import com.panda.sport.rcs.monitor.service.GarbageCollectorService;

@Component
@ConditionalOnBean(name = "sendMqApi")
public class GcCollectTask extends CollectTaskApi{
	
    public GcCollectTask() {
		super(20l);
	}

	@Autowired
    private GarbageCollectorService garbageCollectorService;
    
    @Autowired
    private SendMqApi sendMqApi;
    
    private String key = String.format("GC_%s_%s", ip , pid);
    
	@Override
	public void execute() {
		GarbageCollectorBean gcBean = garbageCollectorService.get();
		sendMqApi.execute(topic, String.valueOf(System.currentTimeMillis()), key, gcBean);
	}

}
