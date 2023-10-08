package com.panda.sport.sdk.core;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.sdk.bean.RedisPoolConfig;
import com.panda.sport.sdk.log.NotWriteLog;
import com.panda.sport.sdk.util.FileUtil;
import com.panda.sport.sdk.util.GuiceContext;
import com.panda.sport.sdk.util.PropertiesUtil;
import com.panda.sport.sdk.vo.RedisScriptBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisNoScriptException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Singleton
public class JedisClusterServer {
    private static final Logger log = LoggerFactory.getLogger(JedisClusterServer.class);

    private final static String SCRIPT_KEY_INCREASE = "increase";

    private JedisCluster jedisCluster;
    
    @Inject
    PropertiesUtil propertiesUtil;

    public JedisClusterServer() {
        if (jedisCluster == null) {
            Set<HostAndPort> nodes = new HashSet<HostAndPort>();
            RedisPoolConfig poolConfig = GuiceContext.getInstance(RedisPoolConfig.class);

            for (String hostport : poolConfig.getAddress().split(",")) {
                String[] ipport = hostport.split(":");
                String ip = ipport[0];
                int port = Integer.parseInt(ipport[1]);
                nodes.add(new HostAndPort(ip, port));
            }
            jedisCluster = new JedisCluster(nodes, poolConfig.getConnectionTimeout(), 
            		poolConfig.getSoTimeout(), poolConfig.getMaxAttempts(), poolConfig.getPassword(), poolConfig);
        }
    }

    public JedisCluster getJedisCluster(){
        return this.jedisCluster;
    }
    public void set(String key, String value) {
        jedisCluster.set(key, value);
    }

    public void setex(String key, int second, String value) {
        jedisCluster.setex(key, second, value);
    }
    public boolean setnx(String key,String value,long expired) {
        String result = jedisCluster.set(key, value, "NX", "EX", expired);
        return "ok".equalsIgnoreCase(result);
    }


    /**设置过期时间*/
    public void expire(String key, int time) {
        jedisCluster.expire(key, time);
    }

    public String get(String key) {
        return jedisCluster.get(key);
    }

    @NotWriteLog
    public String getNoLog(String key) {
        return jedisCluster.get(key);
    }

    public void del(String key) {
        jedisCluster.del(key);
    }

    public String hget(String key, String field) {
        return jedisCluster.hget(key, field);
    }

    public void hset(String key, String field, String value) {
        jedisCluster.hset(key, field, value);
    }

    @NotWriteLog
    public String hgetNoLog(String key, String field) {
        return jedisCluster.hget(key, field);
    }

    public String scriptLoad(String text) {
        String sha = null;
        Collection<JedisPool> pool = jedisCluster.getClusterNodes().values();
        for(JedisPool node : pool) {
            sha = node.getResource().scriptLoad(text);
        }
        return sha;
    }

    public <E> Map<String, E> hGetAll(String key, Class<E> hashValueClazz) {
        Map<String, String> entries = jedisCluster.hgetAll(key);
        if (entries == null || entries.size() == 0) {
            return Collections.emptyMap();
        }
        Map<String, E> resultMap = new LinkedHashMap<>(entries.size());
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            E valueObj = JSONObject.parseObject(entry.getValue(), hashValueClazz);
            resultMap.put(entry.getKey(), valueObj);
        }
        return resultMap;
    }

    public Long hincrBy(String key, String field, long integer) {
        return jedisCluster.hincrBy(key, field, integer);
    }

    public Double hincrByFloat(final String key, final String field, final double value) {
        return jedisCluster.hincrByFloat(key, field, value);
    }

    public Object evalsha(String shakey, List<String> keys, List<String> args) {
        return jedisCluster.evalsha(shakey, keys, args);
    }

    public void delete(String key) {
        jedisCluster.del(key);
    }

    public void hdel(String key, String field) {
        jedisCluster.hdel(key, field);
    }

    public Long incrBy(String key, long integer) {
        return jedisCluster.incrBy(key, integer);
    }
    
    public Double incrByFloat(String key, Double val) {
        return jedisCluster.incrByFloat(key, val);
    }
    
    public Long incrBy(final String key, final long step, final int defaultExpire) {
        List<String> list = new LinkedList<>();
        list.add(String.valueOf(step));
        list.add(String.valueOf(defaultExpire));
        FileUtil fileUtil = new FileUtil();

        RedisScriptBean<Boolean> redisScript = new RedisScriptBean<>();
        redisScript.setScriptSource(fileUtil.getFileTxt("/scripts/lua/incr_expire_1.lua"));
        redisScript.setResultType(Boolean.class);
        redisScript.setScriptKey(SCRIPT_KEY_INCREASE);

        Object result= execute(redisScript,key,list );
        return Long.parseLong(String.valueOf(result));
    }
    public Object execute(RedisScriptBean redisScript, String key, List<String> args) {
        try {
            if (StringUtils.isEmpty((redisScript.getSha1()))) {
                log.info("Reload lua script for first time:{}", redisScript.getScriptKey());
                String sha1 = jedisCluster.scriptLoad(redisScript.getScriptSource(), key);
                redisScript.setSha1(sha1);
            }
            return jedisCluster.evalsha(redisScript.getSha1(), Collections.singletonList(key), args);
        } catch (JedisNoScriptException e) {
            //没有脚本缓存时，重新发送缓存
            log.info("Reload lua script for no script exception:{}", redisScript.getScriptKey());
            //由于使用redis集群，因此每个节点都需要各自缓存一份脚本数据
            // redis支持脚本缓存，返回哈希码，后续可以继续用来调用脚本
            String sha1 = jedisCluster.scriptLoad(redisScript.getScriptSource(), key);
            redisScript.setSha1(sha1);
            return jedisCluster.evalsha(redisScript.getSha1(), Collections.singletonList(key), args);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }


    public Boolean exists(String key) {
        return jedisCluster.exists(key);
    }

    public void hmset(String key, Map<String, String> hash) {
        jedisCluster.hmset(key, hash);
    }

    public Map<String, String> hgetAll(String key) {
        Map<String, String> map = jedisCluster.hgetAll(key);
        if (CollectionUtils.isEmptyMap(map)) {
            return Maps.newHashMap();
        }
        return map;
    }

    @NotWriteLog
    public Map<String, String> hgetAllNoLog(String key) {
        Map<String, String> map = jedisCluster.hgetAll(key);
        if (CollectionUtils.isEmptyMap(map)) {
            return Maps.newHashMap();
        }
        return map;
    }

    /**
     * 返回值顺序与fields顺序一致
     *
     * @param key
     * @param fields
     * @return
     */
    public List<String> hmget(String key, String... fields) {
        List<String> values = jedisCluster.hmget(key, fields);
        if (CollectionUtils.isEmpty(values) || isAllBlank(values)) {
            return new ArrayList<>();
        }
        return values;
    }

    @NotWriteLog
    public List<String> hmgetNoLog(String key, String... fields) {
        List<String> values = jedisCluster.hmget(key, fields);
        if (CollectionUtils.isEmpty(values) || isAllBlank(values)) {
            return new ArrayList<>();
        }
        return values;
    }

    private boolean isAllBlank(List<String> values) {
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return false;
            }
        }
        return true;
    }
}
