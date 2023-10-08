package com.panda.sport.rcs.zk.task;

import java.util.Map;

public interface ZkTask {
	
	public default void start(Map<String, Map<String, Object>> map) {};
	
	public default void stop() {};
	
	/*
	 * 任务分发
	 */
	public default Map<String, Map<String, Object>> taskDistribution(Map<String, Map<String, Object>> allNode) {
		return allNode;
	}
	
}
