package com.panda.sport.rcs.zk;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.panda.sport.rcs.zk.bean.ZkConfig;
import com.panda.sport.rcs.zk.listener.LeadListener;
import com.panda.sport.rcs.zk.server.ZkServer;
import com.panda.sport.rcs.zk.task.ZkTask;

public class ZkBootstrap {
	
	private static Logger log = LoggerFactory.getLogger(LeadListener.class);
	
	public static CountDownLatch latch = new CountDownLatch(1);
	
	public static ZkServer zkServer = null;
	
	public static void start(ZkConfig config, ZkTask zkTask) throws Exception {
		while(true) {
    		zkServer = new ZkServer();
    		zkServer.setZkTask(zkTask);
        	zkServer.initZk(config);
        	log.warn("zk服务启动成功....");
        	latch.await();
        	
        	log.warn("zk服务自动重启中....");
        	zkServer.close();
        	latch = new CountDownLatch(1);
    	}
	}
	
    public static void main(String[] args) throws Exception {
    	ZkConfig config = new ZkConfig();
    	config.setConnectString("127.0.0.1:2181");
    	config.setNamespace("rcs");
    	config.setPrefixPath("/local/test");
//    	config.setNodeStartId(10);
    	start(config,new ZkTask() {

			@Override
			public void start(Map<String, Map<String, Object>> map) {
				log.info("服务启动:{}",zkServer.getConfig());
			}

			@Override
			public void stop() {
				log.info("服务停止：{}",zkServer.getConfig());
			}

			@Override
			public Map<String, Map<String, Object>> taskDistribution(Map<String, Map<String, Object>> allNode) {
				log.info("领导节点分配服务");
				return allNode;
			}
			
		});
    }
	
}
