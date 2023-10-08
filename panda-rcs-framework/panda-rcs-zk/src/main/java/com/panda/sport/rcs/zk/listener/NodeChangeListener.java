package com.panda.sport.rcs.zk.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.zk.ZkBootstrap;
import com.panda.sport.rcs.zk.server.ZkServer;

public class NodeChangeListener implements PathChildrenCacheListener{
	
	Logger log = LoggerFactory.getLogger(NodeChangeListener.class);
	
	private ZkServer zkServer;
	
	private Boolean isLoseConnect = false;
	
	public NodeChangeListener(ZkServer zkServer) {
		this.zkServer = zkServer;
	}

	/**
	 *
	 */
	@Override
	public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
		log.info("节点状态变更：{}",JSONObject.toJSONString(event));
		
		if(zkServer.getNodeChangeApi() != null) 
			zkServer.getNodeChangeApi().nodeChange(event, this.zkServer.getAllNode("/node"));
		
		switch(event.getType()) {
			case CONNECTION_SUSPENDED:
			case CONNECTION_LOST:{
				isLoseConnect = true;
				log.warn("zk连接丢失，暂时不做处理，保持连接之前的状态！：{}",JSONObject.toJSONString(event));
				break;
			}
			case CONNECTION_RECONNECTED:{
				//重新分配节点
				if(isLoseConnect) {
					log.warn("节点重连，重启服务：{}",JSONObject.toJSONString(event));
					ZkBootstrap.latch.countDown();
				}
				break;
			}
			case CHILD_ADDED:{
				//判断是否有leader，把领导节点path保存本地
				if(!this.zkServer.getIsLeader()) {
					//判断数据是否一致
					String path = event.getData().getPath();
					if(!path.equals(this.zkServer.getCurrentNodePath()))  break;
					
					JSONObject data = JSONObject.parseObject(zkServer.getNodeDataByAllPath(path));
					
					log.info("节点新增：{}",data);
					dataUpdate(data);
					
					break;
				}
				
				reStart();
				
				break;
			}
			case CHILD_UPDATED:{
				//如果更新节点与自己的节点是一致的，保存自己的数据
				//如果不是，则判断是否leader节点变更
				String path = event.getData().getPath();
				if(!path.equals(this.zkServer.getCurrentNodePath()))  break;
				
				JSONObject data = JSONObject.parseObject(zkServer.getNodeDataByAllPath(path));
				
				log.info("节点数据有更新：{}",data);
				dataUpdate(data);
				break;
			}
			case CHILD_REMOVED:{
				//节点挂了，任务是否重新分配
				if(!this.zkServer.getIsLeader()) break;
				
				reStart();
				
				break;
			}
			default:{
				log.warn("当前节点状态变更没有处理：{}",JSONObject.toJSONString(event));
				break;
			}
		}
	}
	
	private synchronized void dataUpdate(JSONObject data) {
		this.zkServer.getConfig().setNodeNumber(data.getInteger("index"));
		this.zkServer.getConfig().setNodeTotal(data.getInteger("startCount"));
		this.zkServer.getConfig().setData(data);
		
		if(data.containsKey("isStart") && data.getBoolean("isStart")) {
			if(!this.zkServer.getIsStart()) {
				this.zkServer.setIsStart(true);
				this.zkServer.getZkTask().stop();
				this.zkServer.getZkTask().start(this.zkServer.getAllNode("/node"));
			}
		}else {
			if(this.zkServer.getIsStart()) {
				this.zkServer.setIsStart(false);
				this.zkServer.getZkTask().stop();
			}
		}
	}
	
	private void reStart() {
		Map<String, Map<String, Object>> zkMap = this.zkServer.getAllNode("/node");
		
		//做任务分发
		zkMap = this.zkServer.getZkTask().taskDistribution(zkMap);
		
		int startCount = 0;
		List<String> notStartNodePath = new ArrayList<String>();
		List<Integer> useList = new ArrayList<Integer>();
		for(String key : zkMap.keySet()) {
			Map<String, Object> obj = zkMap.get(key);
			if(obj.containsKey("isStart") && Boolean.valueOf(String.valueOf(obj.get("isStart")))) {
				startCount ++;
			}else {
				notStartNodePath.add(key);
			}
			obj.put("updateTime", System.currentTimeMillis());
			
			if(obj.containsKey("index")) {
				Integer index = Integer.parseInt(String.valueOf(obj.get("index")));
				if(index <= zkMap.size()) useList.add(Integer.parseInt(String.valueOf(obj.get("index"))));
			}
    	}
		
		for(String key : notStartNodePath) {
			//节点不够，是否添加启动
			if(startCount < zkServer.getConfig().getNodeStartCount() || -1 == zkServer.getConfig().getNodeStartCount()) {
				Map<String, Object> obj = zkMap.get(key);
				startCount ++;
				for(int i = 1 ; i <= zkMap.size() ; i ++) {
					if(!useList.contains(Integer.parseInt(String.valueOf(i)))) {
						useList.add(Integer.parseInt(String.valueOf(i)));
						obj.put("index", i + zkServer.getConfig().getNodeStartId());
						obj.put("isStart", true);
					}
				}
			}
		}
		
		
		for(String key : zkMap.keySet()) {
			Map<String, Object> obj = zkMap.get(key);
			obj.put("startCount", startCount);
			zkServer.updateNodeDataByALlPath(key, JSONObject.toJSONString(obj));
		}
		
	}
	
}
