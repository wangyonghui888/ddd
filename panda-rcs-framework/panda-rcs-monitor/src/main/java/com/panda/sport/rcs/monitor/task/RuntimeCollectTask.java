package com.panda.sport.rcs.monitor.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import com.panda.sport.rcs.monitor.entity.RuntimeBean;
import com.panda.sport.rcs.monitor.mq.SendMqApi;
import com.panda.sport.rcs.monitor.service.RuntimeInfoService;

@Component
@ConditionalOnBean(name = "sendMqApi")
public class RuntimeCollectTask extends CollectTaskApi{
	
    public RuntimeCollectTask() {
		super(Long.MAX_VALUE);
		this.isStartOne = true;
	}

	@Autowired
    private RuntimeInfoService runtimeInfoService;
    
    @Autowired
    private SendMqApi sendMqApi;
    
    private String key = String.format("RUNTIME_%s_%s", ip , pid);
    
	@Override
	public void execute() {
		RuntimeBean bean = runtimeInfoService.get();
		sendMqApi.execute(topic, String.valueOf(System.currentTimeMillis()), key, bean);
	}

}
