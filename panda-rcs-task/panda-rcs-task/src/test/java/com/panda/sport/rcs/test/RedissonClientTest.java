package com.panda.sport.rcs.test;

import java.util.concurrent.TimeUnit;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedissonClientTest {
	
	public static void main(String[] args) throws Exception {
        Config config = new Config();
        config.useClusterServers() //这是用的集群server 
        .setScanInterval(2000) //设置集群状态扫描时间 
        .setMasterConnectionPoolSize(10000) //设置连接数 
        .setSlaveConnectionPoolSize(10000) 
        .setIdleConnectionTimeout(1000 * 60 * 5)
                .setPassword("26n-@kq?^j:_phsZN,*uwskX699w7_*A?FoN+wC")
                .addNodeAddress("172.18.178.232:7000,172.18.178.233:7000,172.18.178.234:7000".split(","));
        RedissonClient redisson = Redisson.create(config);
        
        RLock lock = redisson.getLock("TEST_LOCK");
        lock.lock(100, TimeUnit.SECONDS);
        System.err.println("2222222222222222222");
        
        lock.unlock();
	}

}
