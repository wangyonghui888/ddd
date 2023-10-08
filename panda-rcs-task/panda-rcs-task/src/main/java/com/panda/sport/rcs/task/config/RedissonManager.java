package com.panda.sport.rcs.task.config;

import com.panda.sport.rcs.core.cache.properties.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedissonManager {

    private Config config = new Config();
    private Redisson redisson;

    public RedissonManager(RedisProperties redisProperties) {
        try {
            config.useClusterServers() //这是用的集群server 
                    .setScanInterval(2000) //设置集群状态扫描时间 
                    .setMasterConnectionPoolSize(10000) //设置连接数 
                    .setSlaveConnectionPoolSize(10000)
                    .setIdleConnectionTimeout(1000 * 60 * 5)
                    .setPassword(redisProperties.getPassword())
                    .addNodeAddress(redisProperties.getNodesString().split(","));
            redisson = (Redisson) Redisson.create(config);
        } catch (Exception e) {
            log.error("redisson初始化异常:", e);
            if(null == redisson){
                throw e;
            }
        }
    }

    private static final String LOCK_TITLE = "RCS:lock:";

    public boolean lock(String lockName, int timeOut, TimeUnit timeUnit) {
        String key = LOCK_TITLE + lockName;
        RLock myLock = redisson.getLock(key);
        myLock.lock(timeOut, timeUnit);
        return true;
    }

    public boolean lock(String lockName, int timeOut) {
        return lock(lockName, timeOut, TimeUnit.SECONDS);
    }

    public boolean lock(String lockName) {
        return lock(lockName, 10);
    }

    public void unlock(String lockName) {
        String key = LOCK_TITLE + lockName;
        RLock myLock = redisson.getLock(key);
        if (myLock.isHeldByCurrentThread()) {
            myLock.unlock();
        }

    }

}
