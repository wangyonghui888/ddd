package com.panda.sport.rcs.monitor.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.monitor.entity.SysInfoBean;
import com.panda.sport.rcs.monitor.mq.SendMqApi;
import com.panda.sport.rcs.monitor.service.CpuInfoService;
import com.panda.sport.rcs.monitor.task.log.LogMonitorApi;
import com.panda.sport.rcs.monitor.utils.OsUtis;

import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnBean(name = "sendMqApi")
@Slf4j
public class CpuCollectTask extends CollectTaskApi{
	
    public CpuCollectTask() {
		super(60l);
	}

	@Autowired
    private CpuInfoService cpuInfoService;
    
    @Autowired
    private SendMqApi sendMqApi;
    
    private String key = String.format("CPU_%s_%s", ip , pid);
    
	@Override
	public void execute() {
		log.info("cpu 数据获取开始");
		SysInfoBean bean = cpuInfoService.get();
		log.info("cpu 数据获取数据：{}",JSONObject.toJSONString(bean));
		sendMqApi.execute(topic, String.valueOf(System.currentTimeMillis()), key, bean);
		log.info("cpu 数据获取结束：{}",JSONObject.toJSONString(bean));
	}
	
	@Override
	public boolean isStart() {
		return OsUtis.isLinux();
	}

}
