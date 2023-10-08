package com.panda.rcs.order.reject.utils;

import lombok.extern.slf4j.Slf4j;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class RedisLockUtils {

    @Resource
    private RedisClient redisClient;

    public boolean addLock(String lock, String val, Long time) {
        try {
            log.info("开始执行加锁：lock:{}", lock);
            boolean isLock = redisClient.setNX(lock, val, time);
            log.info("执行加锁：lock:{},isLock:{}", lock, isLock);
            if (!isLock) {
                log.warn("定时任务正在处理中，不在重复处理：{}", lock);
            }
            return isLock;
        } catch (Exception e) {
            log.error("::{}::执行加锁异常：lock:{}",lock,e.getMessage(), e);
        }
        return false;
    }

    public void removeLock(String lock) {
        try {
            redisClient.delete(lock);
        } catch (Exception e) {
            log.error("::{}::删除redis锁报错:{}", lock,e.getMessage(), e);
        }
    }

}
