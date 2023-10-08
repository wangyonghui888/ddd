package com.panda.rcs.order.reject.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @Project Name: panda-rcs-trade-group
 * @Package Name: com.panda.sport.rcs.cache.redis
 * @Description : Redis工具类
 * @Author : Paca
 * @Date : 2020-07-31 9:40
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Component
public class RedisUtils {

    @Autowired
    private JedisCluster jedisCluster;

    private static final String SUCCESS = "OK";

    public long del(final String key) {
        return doExec(jedis -> jedis.del(key)).orElse(0L);
    }

    public long expire(final String key, final long timeToLive, final TimeUnit timeUnit) {
        return doExec(jedis -> jedis.expire(key, toSeconds(timeToLive, timeUnit))).orElse(-1L);
    }

    public boolean set(final String key, final String value) {
        return isSuccess(doExec(jedis -> jedis.set(key, value)).orElse(""));
    }

    public boolean exists(final String key) {
        return jedisCluster.exists(key);
    }

    public <T> void setex(final String key, final T value, final long timeToLive, final TimeUnit timeUnit) {
        doExec(jedis -> jedis.setex(key, toSeconds(timeToLive, timeUnit), JsonFormatUtils.toJson(value)));
    }

    public String get(final String key) {
        return doExec(jedis -> jedis.get(key)).orElse("");
    }

    public long incrBy(final String key, final long value) {
        return doExec(jedis -> jedis.incrBy(key, value)).orElse(0L);
    }

    public double incrByFloat(final String key, final double value) {
        return doExec(jedis -> jedis.incrByFloat(key, value)).orElse(0.0D);
    }

    public long hset(final String key, final String field, final String value) {
        return doExec(jedis -> jedis.hset(key, field, value)).orElse(-1L);
    }

    public String hget(final String key, final String field) {
        return doExec(jedis -> jedis.hget(key, field)).orElse("");
    }

    public boolean hmset(final String key, final Map<String, String> hash) {
        return isSuccess(doExec(jedis -> jedis.hmset(key, hash)).orElse(""));
    }

    public List<String> hmget(final String key, final String... fields) {
        return doExec(jedis -> jedis.hmget(key, fields)).orElse(Lists.newArrayList());
    }

    public Map<String, String> hgetAll(final String key) {
        return doExec(jedis -> jedis.hgetAll(key)).orElse(Maps.newHashMap());
    }

    public long hincrBy(final String key, final String field, final long value) {
        return doExec(jedis -> jedis.hincrBy(key, field, value)).orElse(0L);
    }

    public double hincrByFloat(final String key, final String field, final double value) {
        return doExec(jedis -> jedis.hincrByFloat(key, field, value)).orElse(0.0D);
    }

    public long hdel(final String key, final String... fields) {
        return doExec(jedis -> jedis.hdel(key, fields)).orElse(0L);
    }

    public String scriptLoad(String text) {
        String sha = null;
        Collection<JedisPool> pool = jedisCluster.getClusterNodes().values();
        for (JedisPool node : pool) {
            sha = node.getResource().scriptLoad(text);
        }
        return sha;
    }

    public Object evalsha(String shakey, List<String> keys, List<String> args) {
        return doExec(jedis -> jedis.evalsha(shakey, keys, args)).get();
    }

    private int toSeconds(long timeToLive, TimeUnit timeUnit) {
        return Long.valueOf(timeUnit.toSeconds(timeToLive)).intValue();
    }

    private boolean isSuccess(String result) {
        return SUCCESS.equalsIgnoreCase(result);
    }

    private <R> Optional<R> doExec(Function<JedisCluster, R> function) {
        R result = null;
        try {
            result = function.apply(jedisCluster);
        } catch (Throwable t) {
            log.error("Redis缓存异常", t);
        }
        return Optional.ofNullable(result);
    }

}
