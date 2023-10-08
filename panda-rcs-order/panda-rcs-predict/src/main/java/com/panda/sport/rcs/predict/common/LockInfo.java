package com.panda.sport.rcs.predict.common;

import lombok.Data;

/**
 */
@Data
public class LockInfo {

    public LockInfo(String lockKey, String lockValue, long expire){
        this(lockKey, lockValue, expire, 0L);
    }

    public LockInfo(String lockKey, String lockValue, long expire, long timeout){
        this.lockKey = lockKey;
        this.lockValue = lockValue;
        this.expire = expire;
        this.timeout = timeout;
    }

    /**
     * 锁键
     **/
    private String lockKey;

    /**
     * 锁值
     **/
    private String lockValue;

    /**
     * 锁失效时间
     **/
    private long expire;

    /**
     * 获取锁超时时间
     **/
    private long timeout;

}
