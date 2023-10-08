package com.panda.sport.rcs.zk.listener;

import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.zk.ZkBootstrap;
import com.panda.sport.rcs.zk.server.ZkServer;

public class LeadListener extends LeaderSelectorListenerAdapter {
	
	Logger log = LoggerFactory.getLogger(LeadListener.class);
	
	private LeaderSelector leaderSelector;
	private ZkServer zkServer;

	public LeadListener(CuratorFramework zkClient, String path,ZkServer zkServer) {
		leaderSelector = new LeaderSelector(zkClient, path, this);
		leaderSelector.start();
		this.zkServer = zkServer;
	}

	@Override
	public void takeLeadership(CuratorFramework client) throws Exception {
		log.info("获取到领导节点");
		zkServer.setIsLeader(true);
		zkServer.getConfig().setIsLeader(true);
		
		if(zkServer.getCurrentNodePath() == null) {
			synchronized (ZkServer.lock) {
				if(zkServer.getCurrentNodePath() == null) {
					ZkServer.lock.wait();
				}
			}
		}
		
		String nodeData = zkServer.getNodeDataByAllPath(zkServer.getCurrentNodePath());
		Map<String, Object> data = JSONObject.parseObject(nodeData,Map.class);
		data.put("isLeader", true);
		data.put("updateTime", System.currentTimeMillis());
		
		zkServer.updateNodeDataByALlPath(zkServer.getCurrentNodePath(), JSONObject.toJSONString(data));
		
		Map<String, Map<String, Object>> zkMap = this.zkServer.getAllNode("/node");
		
		//做任务分发
		zkMap = this.zkServer.getZkTask().taskDistribution(zkMap);
		
		int startCount = 0;
		for(String key : zkMap.keySet()) {
			Map<String, Object> obj = zkMap.get(key);
			
			if(startCount < zkServer.getConfig().getNodeStartCount() || -1 == zkServer.getConfig().getNodeStartCount()) {
				obj.put("isStart", true);
				startCount ++;
			}else {
				obj.put("isStart", false);
			}
			obj.put("index", startCount + zkServer.getConfig().getNodeStartId());
			obj.put("updateTime", System.currentTimeMillis());
    	}
		
		for(String key : zkMap.keySet()) {
			Map<String, Object> obj = zkMap.get(key);
			obj.put("startCount", startCount);
			zkServer.updateNodeDataByALlPath(key, JSONObject.toJSONString(obj));
		}
		
		ZkBootstrap.latch.await();
		log.info("释放领导节点");
	}
	
	
	public void close() {
		try {
			leaderSelector.close();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

}
