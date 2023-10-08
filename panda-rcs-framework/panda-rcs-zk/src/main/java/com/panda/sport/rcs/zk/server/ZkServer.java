package com.panda.sport.rcs.zk.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PreDestroy;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.zk.bean.ZkConfig;
import com.panda.sport.rcs.zk.listener.LeadListener;
import com.panda.sport.rcs.zk.listener.NodeChangeListener;
import com.panda.sport.rcs.zk.task.NodeChangeApi;
import com.panda.sport.rcs.zk.task.ZkTask;

public class ZkServer {
	
	Logger log = LoggerFactory.getLogger(ZkServer.class);
	
	private String prefixPath ;
	
	private CuratorFramework zkClient;
	
	private LeadListener leader; 
	
	private Boolean isStart = false;
	
	private Boolean isLeader = false;
	
	private String currentNodePath = null;
	
	public static Object lock = new Object();
	
	private ZkTask zkTask;
	
	private ZkConfig config;
	
	private PathChildrenCache pathChildrenCache;
	
	private NodeChangeApi nodeChangeApi;
	
	public synchronized CuratorFramework initZk(ZkConfig config) {
		this.config = config;
		
		if(isStart) {
			log.warn("当前服务zk已经启动不在重新启动");
			return zkClient;
		}
		
		if(this.getZkTask() == null) throw new RuntimeException("需要先配置ZkTask接口，才能初始化！");
		
		this.nodeChangeApi = config.getNodeChangeApi();
		
		CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString(config.getConnectString()).namespace(config.getNamespace())
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        zkClient.start();
        this.prefixPath = config.getPrefixPath();
		this.zkClient = zkClient;
//		this.isStart = true;

		leader = new LeadListener(zkClient,getPath("/leader"),this);
		
		registerWatcherNodeChanged("/node");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isLeader", isLeader);
		map.put("crtTime", System.currentTimeMillis());
		map.put("updateTime", System.currentTimeMillis());
		map.put("appId", this.config.getAppId() == null ? UUID.randomUUID().toString().replace("-", "") : this.config.getAppId());
		synchronized (lock) {
			currentNodePath = crateNode("/node/temp", CreateMode.EPHEMERAL_SEQUENTIAL, JSONObject.toJSONString(map));
			ZkServer.lock.notifyAll();
		}
		
		return zkClient;
	}
	
	public CuratorFramework initZk(String address,String namespace,String prefixPath) {
		ZkConfig config = new ZkConfig();
		config.setConnectString(address);
		config.setNamespace(namespace);
		config.setPrefixPath(prefixPath);
		return initZk(config);
	}
	
	private String getPath(String childPath) {
		return this.prefixPath + childPath;
	}
	
	/**
     * 创建节点
     *
     * @param path       路径
     * @param createMode 节点类型
     * @param data       节点数据
     * @return 是否创建成功
     */
	public String crateNode(String path, CreateMode createMode, String data){
        try{
        	return zkClient.create().withMode(createMode).forPath(getPath(path), data.getBytes());
        }catch (Exception e) {
        	log.error(e.getMessage(),e);
            return null;
        }
    }
	
	
    public NodeChangeApi getNodeChangeApi() {
		return nodeChangeApi;
	}

	public void setNodeChangeApi(NodeChangeApi nodeChangeApi) {
		this.nodeChangeApi = nodeChangeApi;
	}

	/**
     * 删除节点
     *
     * @param path 路径
     * @return 删除结果
     */
    public boolean deleteNode(String path) {
        try{
        	zkClient.delete().forPath(getPath(path));
        }catch (Exception e){
        	log.error(e.getMessage(),e);
            return false;
        }
 
        return true;
    }
    
	
    /**
     * 获取所有节点
     *
     * @param path 路径
     */
    public Map<String, Map<String, Object>> getAllNode(String path) {
    	Map<String, Map<String,Object>> result = new HashMap<String, Map<String,Object>>();
        try{
        	List<String> list = zkClient.getChildren().forPath(getPath(path));
        	if(list == null || list.size() <= 0 ) return result;
        	
        	for(String childPath : list) {
        		String nodePath = getPath(path) + "/" + childPath;
        		Stat stat = zkClient.checkExists().forPath(nodePath);
        		 
                if(stat != null) {
                	String data = new String(zkClient.getData().forPath(nodePath));
            		result.put(nodePath, JSONObject.parseObject(data,new TypeReference<Map<String, Object>>(){}));
                }
        	}
        	
        }catch (Exception e){
        	log.error(e.getMessage(),e);
        }
 
        return result;
    }


    /**
     * 删除一个节点，并且递归删除其所有的子节点
     *
     * @param path 路径
     * @return 删除结果
     */
    public boolean deleteChildrenIfNeededNode(String path)
    {
        try{
        	zkClient.delete().deletingChildrenIfNeeded().forPath(getPath(path));
        }catch (Exception e){
        	log.error(e.getMessage(),e);
            return false;
        }
 
        return true;
    }
    
    /**
     * 判断节点是否存在
     *
     * @param path 路径
     * @return true-存在  false-不存在
     */
    public boolean isExistNode(String path)
    {
        try
        {
            Stat stat = zkClient.checkExists().forPath(getPath(path));
 
            return stat != null ? true : false;
        }
        catch (Exception e)
        {
        	log.error(e.getMessage(),e);
            return false;
        }
    }
    
    public boolean isExistNodeByAllPath(String path)
    {
        try
        {
            Stat stat = zkClient.checkExists().forPath(path);
 
            return stat != null ? true : false;
        }
        catch (Exception e)
        {
        	log.error(e.getMessage(),e);
            return false;
        }
    }
 
    /**
     * 判断节点是否是持久化节点
     * @param path 路径
     * @return 2-节点不存在  | 1-是持久化 | 0-临时节点
     */
    public int isPersistentNode(String path){
        try
        {
            Stat stat = zkClient.checkExists().forPath(getPath(path));
 
            if (stat == null)
            {
                return 2;
            }
 
            if (stat.getEphemeralOwner() > 0)
            {
                return 1;
            }
 
            return 0;
        }
        catch (Exception e)
        {
        	log.error(e.getMessage(),e);
            return 2;
        }
    }
 
    /**
     * 获取节点数据
     *
     * @param path 路径
     * @return 节点数据，如果出现异常，返回null
     */
    public String getNodeData(String path) {
 
        try
        {
            byte[] bytes = zkClient.getData().forPath(getPath(path));
            return new String(bytes);
        }
        catch (Exception e)
        {
        	log.error(e.getMessage(),e);
        	return null;
        }
    }
    
    public String getNodeDataByAllPath(String path) {
    	 
        try
        {
            byte[] bytes = zkClient.getData().forPath(path);
            return new String(bytes);
        }
        catch (Exception e)
        {
        	log.error(e.getMessage(),e);
        	return null;
        }
    }
 
    /**
     * 注册节点数据变化事件
     *
     * @param path              节点路径
     * @param nodeCacheListener 监听事件
     * @return 注册结果
     */
    public boolean registerWatcherNodeChanged(String path, PathChildrenCacheListener listener)
    {
        try
        {
        	pathChildrenCache = new PathChildrenCache(zkClient, getPath(path), false);
        	pathChildrenCache.getListenable().addListener(listener);
        	pathChildrenCache.start(StartMode.BUILD_INITIAL_CACHE);
        }
        catch (Exception e)
        {
        	log.error(e.getMessage(),e);
            return false;
        }
 
        return true;
    }
    
    public boolean registerWatcherNodeChanged(String path){
    	return registerWatcherNodeChanged(path, new NodeChangeListener(this));
    }
    /**
     * 更新节点数据
     *
     * @param path     路径
     * @param newValue 新的值
     * @return 更新结果
     */
    public boolean updateNodeDataByALlPath(String path, String newValue)
    {
        //判断节点是否存在
        if (!isExistNodeByAllPath(path))
        {
            return false;
        }
 
        try
        {
        	zkClient.setData().forPath(path, newValue.getBytes());
        }
        catch (Exception e)
        {
        	log.error(e.getMessage(),e);
            return false;
        }
 
        return true;

    }
 
    /**
     * 更新节点数据
     *
     * @param path     路径
     * @param newValue 新的值
     * @return 更新结果
     */
    public boolean updateNodeData(String path, String newValue)
    {
        //判断节点是否存在
        if (!isExistNode(path))
        {
            return false;
        }
 
        try
        {
        	zkClient.setData().forPath(getPath(path), newValue.getBytes());
        }
        catch (Exception e)
        {
        	log.error(e.getMessage(),e);
            return false;
        }
 
        return true;

    }
    
    @PreDestroy
    public  void close() {
    	try {
			pathChildrenCache.close();
			leader.close();
	    	zkClient.close();
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
    }

	public Boolean getIsLeader() {
		return isLeader;
	}

	public void setIsLeader(Boolean isLeader) {
		this.isLeader = isLeader;
	}

	public String getCurrentNodePath() {
		return currentNodePath;
	}

	public void setCurrentNodePath(String currentNodePath) {
		this.currentNodePath = currentNodePath;
	}

	public void setZkTask(ZkTask zkTask) {
		this.zkTask = zkTask;
	}

	public ZkTask getZkTask() {
		return zkTask;
	}

	public ZkConfig getConfig() {
		return config;
	}

	public void setConfig(ZkConfig config) {
		this.config = config;
	}

	public Boolean getIsStart() {
		return isStart;
	}

	public void setIsStart(Boolean isStart) {
		this.isStart = isStart;
	}
}
