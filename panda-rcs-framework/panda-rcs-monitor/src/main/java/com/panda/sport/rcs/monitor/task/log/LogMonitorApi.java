package com.panda.sport.rcs.monitor.task.log;

import java.util.HashMap;
import java.util.Map;

public class LogMonitorApi {
	
	@SuppressWarnings("rawtypes")
	private static Map<String , LogbackMonitorAppender> appenderList = new HashMap<String, LogbackMonitorAppender>();

	@SuppressWarnings("rawtypes")
	public static void addAppender(String name , LogbackMonitorAppender appender) {
		appenderList.put(name,appender);
	}
	
	@SuppressWarnings("rawtypes")
	public static Map<String , LogbackMonitorAppender> getAppenderList() {
		return appenderList;
	}
	
	public static boolean containsKey(String name ) {
		return appenderList.containsKey(name);
	}
}
