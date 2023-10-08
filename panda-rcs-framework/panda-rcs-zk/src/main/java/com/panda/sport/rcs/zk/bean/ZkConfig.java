package com.panda.sport.rcs.zk.bean;

import java.util.Properties;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.zk.task.NodeChangeApi;

public class ZkConfig {
	
	private String connectString;
	
	private Integer nodeNumber;
	
	private Integer nodeStartId = 0;
	
	private Integer nodeTotal;
	
	private String prefixPath;
	
	private String namespace;
	
	private Integer nodeStartCount = -1;
	
	private Boolean isLeader = false;
	
	private JSONObject data;
	
	private String appId;
	
	private NodeChangeApi nodeChangeApi;
	
	public ZkConfig() {}
	
	public ZkConfig(Properties properties) {
		this.connectString = properties.getProperty("zk.connectString");
		this.prefixPath = properties.getProperty("zk.prefixPath");
		this.namespace = properties.getProperty("zk.namespace");
		if(properties.containsKey("zk.nodeStartId")) 
			this.nodeStartId = Integer.parseInt(properties.getProperty("zk.nodeStartId"));
	}
	
	public NodeChangeApi getNodeChangeApi() {
		return nodeChangeApi;
	}

	public void setNodeChangeApi(NodeChangeApi nodeChangeApi) {
		this.nodeChangeApi = nodeChangeApi;
	}

	public Integer getNodeNumber() {
		return nodeNumber;
	}

	public void setNodeNumber(Integer nodeNumber) {
		this.nodeNumber = nodeNumber;
	}

	public String getPrefixPath() {
		return prefixPath;
	}

	public void setPrefixPath(String prefixPath) {
		this.prefixPath = prefixPath;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public String getConnectString() {
		return connectString;
	}

	public Boolean getIsLeader() {
		return isLeader;
	}

	public void setIsLeader(Boolean isLeader) {
		this.isLeader = isLeader;
	}


	public Integer getNodeStartId() {
		return nodeStartId;
	}

	public void setNodeStartId(Integer nodeStartId) {
		this.nodeStartId = nodeStartId;
	}

	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	public Integer getNodeTotal() {
		return nodeTotal;
	}

	public void setNodeTotal(Integer nodeTotal) {
		this.nodeTotal = nodeTotal;
	}

	public Integer getNodeStartCount() {
		return nodeStartCount;
	}

	public void setNodeStartCount(Integer nodeStartCount) {
		this.nodeStartCount = nodeStartCount;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
	
	

}
