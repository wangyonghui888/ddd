package com.panda.sport.sdk.core;

import com.panda.sport.sdk.util.GuiceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisNoReachableClusterNodeException;
import redis.clients.util.JedisClusterCRC16;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 基于JedisCluster实现管道的使用
 * 核心对象：JedisClusterInfoCache和JedisSlotBasedConnectionHandler
 * 使用构造方法将JedisCluster对象传递进来
 *
 * @author Magic
 * @date 2023/03/19
 */
@Slf4j
public class JedisClusterPipeline {

    /**
     * 构造方法
     * 通过JedisCluster获取JedisClusterInfoCache和JedisSlotBasedConnectionHandler
     *
     * @param jedisCluster
     */
    public JedisClusterPipeline(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
        MetaObject metaObject = SystemMetaObject.forObject(jedisCluster);
        clusterInfoCache = (JedisClusterInfoCache) metaObject.getValue("connectionHandler.cache");
        connectionHandler = (JedisSlotBasedConnectionHandler) metaObject.getValue("connectionHandler");
    }

    /**
     * 管道命令提交阈值
     */
    private final int MAX_COUNT = 1000;
    /**
     * Redis集群缓存信息对象 Jedis提供
     */
    private JedisClusterInfoCache clusterInfoCache;
    /**
     * Redis链接处理对象 继承于JedisClusterConnectionHandler,对其提供友好的调用方法 Jedis提供
     */
    private JedisSlotBasedConnectionHandler connectionHandler;
    /**
     * Redis集群操作对象 Jedis提供
     */
    private JedisCluster jedisCluster;

    /**
     * 存储获取的Jedis对象，用于统一释放对象
     */
    private CopyOnWriteArrayList<Jedis> jedisList = new CopyOnWriteArrayList();
    /**
     * 存储获取的Jedis连接池对象与其对应开启的管道，用于保证slot(哈希槽)对应的节点链接的管道只被开启一次
     */
    private ConcurrentHashMap<JedisPool, Pipeline> pipelines = new ConcurrentHashMap<>();
    /**
     * 存储每个开启的管道需要处理的命令（数据）数，当计数达到提交阈值时进行提交
     */
    private ConcurrentHashMap<Pipeline, Integer> nums = new ConcurrentHashMap<>();

    public void hincrByPipeline(String key, String field, long value) {
        Pipeline pipeline = getPipeline(key);
        pipeline.hincrBy(key, field, value);
        nums.put(pipeline, nums.get(pipeline) + 1);
        this.maxSync(pipeline, key);
    }

    public void incrByPipeline(String key, long value) {
        Pipeline pipeline = getPipeline(key);
        pipeline.incrBy(key, value);
        nums.put(pipeline, nums.get(pipeline) + 1);
        this.maxSync(pipeline, key);
    }


    public void setByPipeline(String key, String value) {
        Pipeline pipeline = getPipeline(key);
        pipeline.set(key, value);
        nums.put(pipeline, nums.get(pipeline) + 1);
        this.maxSync(pipeline, key);
    }


    public void expirePipeline(String key, int value) {
        Pipeline pipeline = getPipeline(key);
        pipeline.expire(key, value);
        nums.put(pipeline, nums.get(pipeline) + 1);
        this.maxSync(pipeline, key);
    }

    public void testPipeline(String key, String value) {
        Pipeline pipeline = getPipeline(key);
        pipeline.set(key, value);
        nums.put(pipeline, nums.get(pipeline) + 1);
        this.maxSync(pipeline, key);
    }

    /**
     * 释放获取的Jedis链接
     * 释放的过程中会强制执行PipeLine sync
     */
    public void releaseConnection() {
        jedisList.forEach(jedis -> {
            try {
                if (jedis.isConnected()) {
                    jedis.close();
                }
            } catch (Exception e) {
                log.error("JedisClusterPipeline连接关闭异常:{}",e.getMessage(), e);
            }
        });
    }

    /**
     * 获取JedisPool
     * 第一次获取不到尝试刷新缓存的SlotPool再获取一次
     *
     * @param key
     * @return
     */
    private JedisPool getJedisPool(String key) {
        /** 通过key计算出slot */
        int slot = JedisClusterCRC16.getSlot(key);
        /** 通过slot获取到对应的Jedis连接池 */
        JedisPool jedisPool = clusterInfoCache.getSlotPool(slot);
        if (null != jedisPool) {
            return jedisPool;
        } else {
            /** 刷新缓存的SlotPool */
            connectionHandler.renewSlotCache();
            jedisPool = clusterInfoCache.getSlotPool(slot);
            if (jedisPool != null) {
                return jedisPool;
            } else {
                throw new JedisNoReachableClusterNodeException("No reachable node in cluster for slot " + slot);
            }
        }
    }

    /**
     * 获取Pipeline对象
     * 缓存在pipelines中，保证集群中同一节点的Pipeline只被开启一次
     * 管道第一次开启，jedisList，pipelines，nums存入与该管道相关信息
     *
     * @param key
     * @return
     */
    private Pipeline getPipeline(String key) {
        JedisPool jedisPool = getJedisPool(key);
        /** 检查管道是否已经开启 */
        Pipeline pipeline = pipelines.get(jedisPool);
        if (null == pipeline) {
            Jedis jedis = jedisPool.getResource();
            pipeline = jedis.pipelined();
            jedisList.add(jedis);
            pipelines.put(jedisPool, pipeline);
            nums.put(pipeline, 0);
        }
        return pipeline;
    }

    /**
     * 管道对应的命令计数，并在达到阈值时触发提交
     * 提交后计数归零
     *
     * @param pipeline
     * @return
     */
    private void maxSync(Pipeline pipeline, String key) {
        Integer num = nums.get(pipeline);
        if (null != num) {
            if (num % MAX_COUNT == 0) {
                log.info("redisPipeline批量提交:{},{}", MAX_COUNT, key);
                pipeline.sync();
                nums.put(pipeline, 0);
            }
        }
    }

    public static void main(String[] args) {
//        JedisClusterServer jedisClusterServer = GuiceContext.getInstance(JedisClusterServer.class);
//
//        JedisClusterPipeline jedisClusterPipeline = new JedisClusterPipeline(jedisClusterServer.getJedisCluster());
//
//        for (int i = 0; i < 100000; i++) {
//            jedisClusterPipeline.testPipeline("magic:test:pipline:" + i, String.valueOf(i));
//        }
//        jedisClusterPipeline.releaseConnection();


//        JedisClusterServer jedisClusterServer = GuiceContext.getInstance(JedisClusterServer.class);
//
//        JedisClusterPipeline jedisClusterPipeline = new JedisClusterPipeline(jedisClusterServer.getJedisCluster());
//        jedisClusterPipeline.setByPipeline("magic_test","123");
//        jedisClusterPipeline.releaseConnection();
//
//        jedisClusterPipeline = new JedisClusterPipeline(jedisClusterServer.getJedisCluster());
//        jedisClusterPipeline.setByPipeline("magic_test1","123");
//        jedisClusterPipeline.releaseConnection();
//
//        jedisClusterPipeline = new JedisClusterPipeline(jedisClusterServer.getJedisCluster());
//        jedisClusterPipeline.setByPipeline("magic_test2","123");
//        jedisClusterPipeline.releaseConnection();
    }
}