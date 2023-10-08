package com.panda.sport.rcs.core.cache.service;

import java.lang.management.LockInfo;
import java.util.*;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.panda.sport.rcs.core.bean.RedisScriptBean;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.log.annotion.WriteLog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisNoScriptException;

/**
 * 基于redis集群模式
 * redis缓存进行操作
 *
 * @param <T>
 * @author kane
 * @version v1.1
 * @since 2019-09-07
 */
@Slf4j
@WriteLog
public class RcsClusterCache<T> implements RedisClient<T> {

    @Autowired
    JedisCluster jedisCluster;
    @Resource(name = "unlock")
    RedisScriptBean unlockRedisScript;
    @Resource(name="incr_expire_1")
    RedisScriptBean increaseByScript;

    private static final String UNLOCK_SUCCESS_CODE = "1";

    private static final String LOCK_SUCCESS_CODE = "ok";

    private String LUA_NAME_KEY = "PANDA_RCS";

    /**
     * 以字符串的形式
     * 往redis中设置值
     *
     * @param key
     * @param value
     */
    @Override
    public <T> void set(String key, T value) {
        jedisCluster.set(key, JsonFormatUtils.toJson(value));
    }

    /**
     * 设置值，并指定有效期
     * @param key
     * @param value
     * @param expiry
     * @param <T>
     */
    @Override
    public <T> void setExpiry(String key, T value, Long expiry) {
        jedisCluster.setex(key, expiry.intValue(), JsonFormatUtils.toJson(value));
    }

    @Override
    public <T1> void expireKey(String key, Integer expire) {
        jedisCluster.expire(key, expire);
    }

    /**
     * 判断当前key值是否存在于
     * redis中
     * 往redis中设置值
     *
     * @param key
     */
    @Override
    public boolean exist(String key) {
        return jedisCluster.exists(key);
    }

    /**
     * 根据指定的key值获取redis中
     * 储存的字符串值
     *
     * @param key
     * @return
     */
    @Override
    public String get(String key) {
        return jedisCluster.get(key);
    }

    /**
     * 根据key与泛型类型获取到储存到
     * redis中的对象
     *
     * @param key
     * @param clazz
     * @return
     */
    @Override
    public T getObj(String key, Class<T> clazz) {
        return jedisCluster.get(key) == null ? null : JsonFormatUtils.fromJson(jedisCluster.get(key), clazz);
    }

    /**
     * 删除掉指定key
     *
     * @param key
     */
    @Override
    public void delete(String key) {
        jedisCluster.del(key);
    }

    /**
     * 根据key前缀批量删除缓存
     * @param key
     * @return
     */
    @Override
    public long batchDel(String key){
        long result = 0;
        try {
            Set<String> set = keys(key +"*");
            Iterator<String> it = set.iterator();
            while(it.hasNext()){
                String keyStr = it.next();
                result += jedisCluster.del(keyStr);
            }
        } catch (Exception e){
            log.error("批量删除缓存出错",e.getMessage(),e);
        }
        return result;
    }


    /**
     * 往hash中设置字符串值
     *
     * @param key       键
     * @param hashKey   hash键
     * @param hashValue hash值
     */
    @Override
    public void hSet(String key, String hashKey, String hashValue) {
        jedisCluster.hset(key, hashKey, hashValue);
    }

    /**
     * 往hash中设置字对象
     *
     * @param key       键
     * @param hashKey   hash键
     * @param hashValue hash值
     */
    @Override
    public <T> void hSetObj(String key, String hashKey, T hashValue) {
        if (hashValue != null) {
            jedisCluster.hset(key, hashKey, JsonFormatUtils.toJson(hashValue));
        }
    }


    /**
     * 获取指定key，hashKey中的值（字符串）
     *
     * @param key     键
     * @param hashKey hash键
     */
    @Override
    public String hGet(String key, String hashKey) {
        return jedisCluster.hget(key, hashKey);
    }

    /**
     * 获取指定key，hashKey中的值(对象)
     *
     * @param key            键
     * @param hashKey        hash键
     * @param hashValueClazz 需要转换的类型
     */
    @Override
    public Object hGetObj(String key, String hashKey, Class<?> hashValueClazz) {
        String stringValue = jedisCluster.hget(key, hashKey);
        if (stringValue != null) {
            return JsonFormatUtils.fromJson(stringValue, hashValueClazz);
        }
        return null;
    }

    /**
     * 当前key hkey是否存在
     *
     * @param key
     * @param hashKey
     * @return
     */
    @Override
    public boolean hexists(String key, String hashKey) {
        return jedisCluster.hexists(key, hashKey);
    }

    /**
     * 移除掉key，hashKey中的值
     *
     * @param key     键
     * @param hashKey hash键
     */
    @Override
    public Long hashRemove(String key, String hashKey) {
        return jedisCluster.hdel(key, hashKey);
    }


    @Override
    public List hGetObjList(String key, String hashKey) {
        return null;
    }

    /**
     * 获取Hash所有键值
     *
     * @param key
     * @param hashValueClazz
     * @return
     */
    @Override
    public <E> Map<String, E> hGetAll(String key, Class<E> hashValueClazz) {
        Map<String, String> entries = jedisCluster.hgetAll(key);
        if (entries == null || entries.size() == 0) {
            return Collections.emptyMap();
        }
        Map<String, E> resultMap = new LinkedHashMap<>(entries.size());
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            E valueObj = JsonFormatUtils.fromJson(entry.getValue(), hashValueClazz);
            resultMap.put(entry.getKey(), valueObj);
        }
        return resultMap;
    }
    
    @Override
    public <E> Map<String, E> hGetAllByJson(String key, Class<E> hashValueClazz) {
        Map<String, String> entries = jedisCluster.hgetAll(key);
        if (entries == null || entries.size() == 0) {
            return Collections.emptyMap();
        }
        Map<String, E> resultMap = new LinkedHashMap<>(entries.size());
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            resultMap.put(entry.getKey(), JSONObject.parseObject(entry.getValue(),hashValueClazz));
        }
        return resultMap;
    }

    @Override
    public Map hGetAllToObj(String key) {
        Map<String, String> entries = jedisCluster.hgetAll(key);
        if (entries == null || entries.size() == 0) {
            return Collections.emptyMap();
        }
        return entries;
    }

    /**
     * 基于redis的set操作实现加锁
     *
     * @param key
     * @param lockId     锁ID（可以理解成自己随机生成的,用于标识）
     *                   当前redis锁时谁占有的
     * @param expiryTime key 失效时间 (秒)
     * @return
     */
    @Override
    public boolean lock(String key, long lockId, int expiryTime) {
        //相比一般的分布式锁，这里把setNx和setExpiry操作合并到一起，jedis保证原子性，避免连个命令之间出现宕机等问题
        //这里也可以我们使用lua脚本实现
        String result = jedisCluster.set(key, String.valueOf(lockId), "NX", "EX", expiryTime);
        return LOCK_SUCCESS_CODE.equalsIgnoreCase(result);
    }

    /**
     * 基于reids lua脚本实现原子
     *
     * @param key   redis 锁key
     * @param value 当前线程对应的ID与lock相对应一致
     * @return
     */
    @Override
    public boolean unlock(String key, Long value) {
        try {
            Object result = this.execute(unlockRedisScript, key, Arrays.asList(String.valueOf(value)));
            return UNLOCK_SUCCESS_CODE.equals(result);
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
    }


    @Override
    public long hSetNx(String key, String hashKey, String hashValue) {
        // TODO Auto-generated method stub
        return jedisCluster.hsetnx(key, hashKey, hashValue);
    }

    /**
     * 将 key 所储存的值加上增量 increment
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 命令*
     *
     * @param key
     * @param integer
     * @return 加上 increment 之后， key 的值
     */
    @Override
    public Long incrBy(String key, long integer) {
        return jedisCluster.incrBy(key, integer);
    }
    
    @Override
    public Double incrByFloat(String key, Double integer) {
        return jedisCluster.incrByFloat(key, integer);
    }

    /**
     * 为哈希表 key 中的域 field 的值加上增量 increment
     * 如果 key 不存在，一个新的哈希表被创建并执行 HINCRBY 命令
     * 如果域 field 不存在，那么在执行命令前，域的值被初始化为 0
     *
     * @param key
     * @param field
     * @param integer
     * @return 执行 HINCRBY 命令之后，哈希表 key 中域 field 的值
     */
    @Override
    public Long hincrBy(String key, String field, long integer) {
        return jedisCluster.hincrBy(key, field, integer);
    }


    @Override
    public Double hincrByFloat(String key, String field, Double val) {
        return jedisCluster.hincrByFloat(key, field, val);
    }

    /**
     * 执行LUA脚本通用方法，需要定义脚本文件和脚本bean的信息
     *
     * @param redisScript
     * @param key         键
     * @param args        所有的参数
     * @return
     */
    @Override
    public Object execute(RedisScriptBean redisScript, String key, List<String> args) {
        try {
            if (Strings.isNullOrEmpty(redisScript.getSha1())) {
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


	@Override
	public Set<String> keys(String pattern) {
		TreeSet<String> keys = new TreeSet<>();
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
 
        for (String key : clusterNodes.keySet()) {
            JedisPool jedisPool = clusterNodes.get(key);
            Jedis jedisConn = jedisPool.getResource();
            try {
                keys.addAll(jedisConn.keys(pattern));
            } catch (Exception e) {
            	log.error(e.getMessage(),e);
            } finally {
                jedisConn.close();
            }
        }
 
        return keys;
	}
    /*
     * @Description   原子累加
     * @Param [key, step, defaultExpire]
     * @Author toney
     * @Date  10:37 2019/10/12
     * @return java.lang.Long
     **/
    @Override
    public Long incrBy(final String key, final long step, final int defaultExpire) {
        List<String> list = new LinkedList<>();
        list.add(String.valueOf(step));
        list.add(String.valueOf(defaultExpire));
        Object result= execute(increaseByScript,key,list );
        return Long.parseLong(String.valueOf(result));
    }

    /**
     * 加载脚本到缓存
     */
	@Override
	public String scriptLoad(String text) {
        //return jedisCluster.scriptLoad(text,LUA_NAME_KEY);
		String sha = null;
		Collection<JedisPool> pool = jedisCluster.getClusterNodes().values();
		for(JedisPool node : pool) {
			sha = node.getResource().scriptLoad(text);
		}
		return sha;
	}

	@Override
	public Object evalsha(String shakey, List<String> keys, List<String> args) {
		Object obj = jedisCluster.evalsha(shakey, keys, args);
		log.info("执行脚本结果：shakey:{},keys：{},args：{},result:{}",shakey,keys,args,JSONObject.toJSONString(obj));
		return obj;
	}

    /**
     * 锁(简易版,待完善)
     *
     * @param lockKey
     * @param expire  过期时间 单位:秒
     * @param timeout 获取锁最大等待时间 单位:秒
     */
    @Override
    public String getLock(String lockKey, int expire, long timeout) {
        String value = UUID.randomUUID().toString().toLowerCase();
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < (timeout * 1000)) {
            long result = jedisCluster.setnx(lockKey, value);
            jedisCluster.expire(lockKey, expire);
            if (result == 1) {
                return value;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
        throw new RuntimeException("获取锁超时!,lockKey=" + lockKey);
    }

    /**
     * @Description   设置nx
     * @Param [key, value, expired]
     * @Author  toney
     * @Date  15:05 2020/4/1
     * @return boolean
     **/
    @Override
    public boolean setNX(String key, String value, long expired){
        String result = jedisCluster.set(key, value, "NX", "EX", expired);

        return LOCK_SUCCESS_CODE.equalsIgnoreCase(result);
    }
    /**
     * @Description   执行lua脚本
     * @Param [script, keys, args]
     * @Author  toney
     * @Date  15:05 2020/4/1
     * @return java.lang.Object
     **/
    @Override
    public Object lua(String script, List<String> keys, List<String> args){
        try{
            Object obj = jedisCluster.evalsha(script, keys, args);
            return obj;
        }catch (JedisNoScriptException  ex){
            if (!StringUtils.isEmpty(script) || !jedisCluster.scriptExists(script,  keys.get(0))) {
                jedisCluster.scriptLoad(script, keys.get(0));
            }
            Object obj = jedisCluster.evalsha(script, keys, args);
            return obj;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
