package com.panda.sport.rcs.monitor.task;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.monitor.entity.ErrorLogCollectorBean;
import com.panda.sport.rcs.monitor.mq.SendMqApi;
import com.panda.sport.rcs.monitor.task.log.LogMonitorApi;
import com.panda.sport.rcs.monitor.task.log.LogbackMonitorAppender;
import com.panda.sport.rcs.monitor.utils.FileContentReadUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnBean(name = "sendMqApi")
@Slf4j
public class ErrorLogCollectTask extends CollectTaskApi{
	
    public ErrorLogCollectTask() {
		super(1l);
	}
    
    @Autowired
    private SendMqApi sendMqApi;
    
    private String key = String.format("ERROR_LOG_%s_%s", ip , pid);
    
	@Override
	public void execute() {
		try {
			if(LogMonitorApi.getAppenderList() == null || LogMonitorApi.getAppenderList().size() <= 0 ) return;
			for(String name : LogMonitorApi.getAppenderList().keySet()) {
				LogbackMonitorAppender appender = LogMonitorApi.getAppenderList().get(name);
				
				//文件切换日期，改为初始化0位置
				if(!appender.getLastFileName().equals(appender.getFile())) {
					appender.setLastTimeFileSize(0l);
					appender.setLastFileName(appender.getFile());
				}
				
				String content = FileContentReadUtils.realtimeShowLog(appender);
				if(StringUtils.isBlank(content)) continue;
				
				if(content.getBytes("utf-8").length >  1024 * 1024 * 2) {
					continue;
				}
				
				ErrorLogCollectorBean bean = new ErrorLogCollectorBean();
				bean.setLogContent(content);
				bean.setCurrentDate(System.currentTimeMillis());
				sendMqApi.execute(topic, String.valueOf(System.currentTimeMillis()), key, bean);
			}
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
	}

	@Override
	public boolean isStart() {
		return LogMonitorApi.getAppenderList() != null && LogMonitorApi.getAppenderList().size() > 0 ;
	}
}
