package com.panda.sport.rcs.predict.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.predict.common.LockInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 货量锁 service
 **/
@Service
@Slf4j
public class LockServiceImpl {

    @Autowired
    private RedisClient redisClient;

    public LockInfo lock(String lockKey, long expire, long timeout) throws RcsServiceException {
        long start = System.currentTimeMillis();
        //获取次数
        int acquireCount = 1;
        while (System.currentTimeMillis() - start < timeout * 1000) {
            String uuid = UUID.randomUUID().toString();
            Boolean flag = redisClient.setNX(lockKey, uuid, expire);
            if (flag) {
                LockInfo lockInfo = new LockInfo(lockKey, uuid, expire, timeout);
                log.info("获取锁完成lockKey=" + lockKey + "尝试次数 " + acquireCount);
                return lockInfo;
            }
            acquireCount++;
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
        log.info("获取锁超时! 尝试次数=" + acquireCount + ", lockKey=" + lockKey);
        throw new RcsServiceException("获取锁超时! 尝试次数=" + acquireCount + ", lockKey=" + lockKey);
    }

    public void release(LockInfo lockInfo) {
        if (lockInfo == null) {
            log.info("锁lockInfo为空");
            return;
        }

        String value = redisClient.get(lockInfo.getLockKey());
        if(value.equals(lockInfo.getLockValue())){
            redisClient.delete(lockInfo.getLockKey());
            log.info("释放锁完成lockKey=" + JSONObject.toJSONString(lockInfo));
            return;
        }
        log.info("释放锁完成(未匹配对应value) lockKey=" + lockInfo.getLockKey());
    }
}