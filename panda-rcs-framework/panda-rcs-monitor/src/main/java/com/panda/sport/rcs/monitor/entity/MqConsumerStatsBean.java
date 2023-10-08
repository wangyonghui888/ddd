package com.panda.sport.rcs.monitor.entity;

import java.util.HashMap;
import java.util.Map;

public class MqConsumerStatsBean {
	
	private HashMap<Map<String, Object>, Map<String, Object>> offsetTable = new HashMap<Map<String,Object>, Map<String,Object>>();
    private double consumeTps = 0;
    
    private Map<String, Long> mergeTopicData;
    
    private String groupName;
    
    private String versionId;
    
    
	public HashMap<Map<String, Object>, Map<String, Object>> getOffsetTable() {
		return offsetTable;
	}
	public void setOffsetTable(HashMap<Map<String, Object>, Map<String, Object>> offsetTable) {
		this.offsetTable = offsetTable;
	}
	public double getConsumeTps() {
		return consumeTps;
	}
	public void setConsumeTps(double consumeTps) {
		this.consumeTps = consumeTps;
	}
	public Map<String, Long> getMergeTopicData() {
		return mergeTopicData;
	}
	public void setMergeTopicData(Map<String, Long> mergeTopicData) {
		this.mergeTopicData = mergeTopicData;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getVersionId() {
		return versionId;
	}
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	
}
