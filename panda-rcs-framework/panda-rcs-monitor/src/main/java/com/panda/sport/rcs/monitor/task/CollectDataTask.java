package com.panda.sport.rcs.monitor.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.monitor.utils.OsUtis;

import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnBean(name = "sendMqApi")
@ConditionalOnProperty("service.data.collect")
@Slf4j
public class CollectDataTask implements CommandLineRunner{
	
	List<CollectTaskApi> taskList = new ArrayList<CollectTaskApi>();
	
	public ScheduledExecutorService execute = new ScheduledThreadPoolExecutor(4, new ThreadFactory() {
		
		private AtomicInteger atomicInteger = new AtomicInteger();
		
		@Override
		public Thread newThread(Runnable run) {
			atomicInteger.incrementAndGet();
			Thread thread =  new Thread(run ,"RCS_DATA_MONITOR_" + atomicInteger);
	        return thread;
		}
	});
	
	public CollectDataTask(List<CollectTaskApi> list,Environment env) {
		if(env == null) {
			log.warn("spring 属性参数获取为空，不启动监控");
			return ;
		}
		if(!env.containsProperty("spring.application.name")) {
			log.warn("spring 属性spring.application.name参数获取为空，不启动监控");
			return ;
		}
		OsUtis.setServerName(env.getProperty("spring.application.name"));
		this.taskList = list;
	}

	@Override
	public void run(String... args) throws Exception {
		if(taskList == null || taskList.size() <= 0 ) return ;
		log.info("开始启动服务监控！{}",taskList);
		for(CollectTaskApi api : taskList) {
			if(!api.isStart()) continue;
			if(api.isStartOne) {
				new Thread(api).start();
			}else {
				execute.scheduleWithFixedDelay(api, api.getExetime(), api.getExetime(), TimeUnit.SECONDS);
			}
		}
	}
	
}
