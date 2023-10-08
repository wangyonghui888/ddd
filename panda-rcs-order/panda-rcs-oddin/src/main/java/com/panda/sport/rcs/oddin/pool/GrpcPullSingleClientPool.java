package com.panda.sport.rcs.oddin.pool;

import com.panda.sport.rcs.oddin.client.GrpcPullSingleClient;
import com.panda.sport.rcs.oddin.config.PoolProperties;
import com.panda.sport.rcs.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

@Slf4j
public class GrpcPullSingleClientPool {
    private static GenericObjectPool<GrpcPullSingleClient> objectPool = null;

    static {
        PoolProperties poolProperties = SpringContextUtils.getBeanByClass(PoolProperties.class);
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        // 池中的最大连接数
        poolConfig.setMaxTotal(poolProperties.getMaxTotal());
        // 最少的空闲连接数
        poolConfig.setMinIdle(poolProperties.getMinIdle());
        // 最多的空闲连接数
        poolConfig.setMaxIdle(poolProperties.getMaxIdle());
        // 当连接池资源耗尽时,调用者最大阻塞的时间,超时时抛出异常 单位:毫秒数
        poolConfig.setMaxWaitMillis(poolProperties.getMaxWaitMillis());
        // 连接池存放池化对象方式,true放在空闲队列最前面,false放在空闲队列最后
        poolConfig.setLifo(poolProperties.isLifo());
        // 连接空闲的最小时间,达到此值后空闲连接可能会被移除,默认即为30分钟
        poolConfig.setMinEvictableIdleTimeMillis(poolProperties.getMinEvictableIdleTimeMillis());
        // 连接耗尽时是否阻塞,默认为true
        poolConfig.setBlockWhenExhausted(poolProperties.isBlockWhenExhausted());
        objectPool = new GenericObjectPool<>(new GrpcPullSingleClientFactory(), poolConfig);

    }

    public static GrpcPullSingleClient borrowObject() {
        try {
            GrpcPullSingleClient client = objectPool.borrowObject();
            log.info("=======total pullSingle threads created: " + objectPool.getCreatedCount());
            return client;
        } catch (Exception e) {
            log.error("objectPool.borrowObject error, msg:{}, exception:{}", e.toString(), e);
        }
        return createClient();
    }

    public static void returnObject(GrpcPullSingleClient client) {
        try {
            objectPool.returnObject(client);
        } catch (Exception e) {
            log.error("objectPool.returnObject error, msg:{}, exception:{}", e.toString(), e);
        }
    }

    /**
     * 销毁已经建立的连接
     */
    public static void destroyPool() {
        long len = objectPool.getCreatedCount();
        for (int i = 0; i <len; i++) {
            try {
                GrpcPullSingleClient client = objectPool.borrowObject();
                client.requestStreamObserver.onCompleted();
            } catch (Exception e) {
                log.error("销毁grpc注单连接池时，发送错误",e);
            }
        }
    }
    private static GrpcPullSingleClient createClient() {
        return new GrpcPullSingleClient();
    }
}
