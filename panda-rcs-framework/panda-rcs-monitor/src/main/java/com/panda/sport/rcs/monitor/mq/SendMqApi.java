package com.panda.sport.rcs.monitor.mq;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.panda.sport.rcs.monitor.utils.OsUtis;

public interface SendMqApi {
	
	public void execute(String topic,String tag,String key ,Object msg,Map<String, String> properties);
	
	public default void execute(String subConfig ,Object msg,Map<String, String> properties) {
		if(StringUtils.isEmpty(subConfig)) throw new RuntimeException("配置不能为空，subConfig：" + subConfig ) ;
		
		String[] config = subConfig.split(",");
		if(config.length == 1) {
			execute(config[0], "", "", msg, properties);
		}else if(config.length == 2) {
			execute(config[0], config[1], "", msg, properties);
		}else if(config.length == 3) {
			execute(config[0], config[1], config[2], msg, properties);
		}
	}
	
	public default void execute(String topic,String tag,String key ,Object msg) {
		Map<String, String> map = new HashMap<>();
		map.put("IP", OsUtis.getIp());
		map.put("PID", OsUtis.getPid());
		map.put("SEND_TIME", System.currentTimeMillis() + "");
		map.put("SEND_SERVER_NAME", OsUtis.SERVER_NAME);
		execute(topic,tag,key ,msg,map); 
	}
	
	public default void execute(String subConfig ,Object msg) {
		execute(subConfig, msg, new HashMap<>());
	}

}
