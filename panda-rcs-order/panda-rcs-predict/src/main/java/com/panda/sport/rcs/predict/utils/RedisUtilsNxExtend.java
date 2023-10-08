package com.panda.sport.rcs.predict.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;


/**
 * @author :  joey
 * @Description :  redis扩展 设置过期时间单位为毫秒
 * @Date: 2022-09-25 15:32
 * --------  ---------  --------------------------
 */
@Component
public class RedisUtilsNxExtend {

    @Autowired
    JedisCluster jedisCluster;


    /**
     * 使用毫秒为单位的过期时间
     *
     * @param key     redis key
     * @param value   值
     * @param expired 过期时间
     * @return
     */
    public boolean setNX(String key, String value, long expired) {
        String result = this.jedisCluster.set(key, value, "NX", "PX", expired);
        return "ok".equalsIgnoreCase(result);
    }
}
