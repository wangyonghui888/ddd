package com.panda.sport.rcs.mgr.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSlotBasedConnectionHandler;
import redis.clients.jedis.exceptions.JedisNoReachableClusterNodeException;
import redis.clients.util.JedisClusterCRC16;

import java.io.Serializable;
import java.util.Set;

/**
 * 根据solt获取到对应的redis pool
 * @date 2023-03-06
 * @author magic
 */
public class JedisSlotConnectionHandlerImp extends JedisSlotBasedConnectionHandler implements Serializable {
    public JedisSlotConnectionHandlerImp(Set<HostAndPort> nodes, GenericObjectPoolConfig poolConfig, int connectionTimeout, int soTimeout, String password) {
        super(nodes, poolConfig, connectionTimeout, soTimeout, password);
    }

    // 自定义通过slot获取JedisPool的方法
    // 为了保证后面一个JedisPool只取一个Jedis
    public JedisPool getJedisPoolFromSlot(String key) {
        int slot = JedisClusterCRC16.getSlot(key);
        JedisPool jedisPool = cache.getSlotPool(slot);
        if (jedisPool != null) {
            return jedisPool;
        } else {
            renewSlotCache();
            jedisPool = cache.getSlotPool(slot);
            if (jedisPool != null) {
                return jedisPool;
            } else {
                throw new JedisNoReachableClusterNodeException("No reachable node in cluster for slot " + slot);
            }
        }
    }
}