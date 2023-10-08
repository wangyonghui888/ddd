//package com.panda.sport.rcs.core.cache.service;
//
//import com.panda.sport.rcs.core.bean.RedisScriptBean;
//import com.panda.sport.rcs.core.cache.client.RedisClient;
//import com.panda.sport.rcs.core.utils.JsonFormatUtils;
//import com.panda.sport.rcs.log.annotion.WriteLog;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.connection.ReturnType;
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//
//
//import javax.annotation.Resource;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
///**
// * 基于redis单点模式进行
// * reddis缓存进行操作
// *
// * @param <T>
// */
//@Slf4j
//@WriteLog
//public class RcsCache<T, E> implements RedisClient<T> {
//
//
//    private static String EXEC_RESULT = "1";
//
//    /**
//     * redis操作对象声明区
//     */
//    @Autowired(required = false)
//    private RedisTemplate<String, String> redisTemplate;
//    @Autowired(required = false)
//    private ValueOperations<String, String> valueOperations;
//    @Autowired(required = false)
//    private HashOperations<String, String, String> hashOperations;
//    @Resource(name = "lock")
//    RedisScriptBean lockRedisScript;
//    @Resource(name = "unlock")
//    RedisScriptBean unlockRedisScript;
//    @Resource(name="incr_expire_1")
//    RedisScriptBean increaseByScript;
//
//    /**
//     * 以字符串的形式
//     * 往redis中设置值
//     *
//     * @param key
//     * @param value
//     */
//    @Override
//    public <T> void set(String key, T value) {
//        valueOperations.set(key, JsonFormatUtils.toJson(value));
//    }
//
//    /**
//     * 以字符串的形式并且可以设置
//     * 过期时间
//     * 往redis中设置值
//     *
//     * @param key
//     * @param value
//     * @param expiry
//     */
//    @Override
//    public <T> void setExpiry(String key, T value, Long expiry) {
//        valueOperations.set(key, JsonFormatUtils.toJson(value), expiry, TimeUnit.SECONDS);
//    }
//
//    /**
//     * 过期，单位秒
//     * @param key
//     * @param expire
//     * @param <T1>
//     */
//    @Override
//    public <T1> void expireKey(String key, Integer expire) {
//        redisTemplate.expire(key,expire,TimeUnit.SECONDS);
//    }
//
//    /**
//     * 判断当前key值是否存在于
//     * redis中
//     * 往redis中设置值
//     *
//     * @param key
//     */
//    @Override
//    public boolean exist(String key) {
//        return redisTemplate.hasKey(key);
//    }
//
//    /**
//     * 根据指定的key值获取redis中
//     * 储存的字符串值
//     *
//     * @param key
//     * @return
//     */
//    @WriteLog
//    @Override
//    public String get(String key) {
//        return key == null ? null : valueOperations.get(key);
//    }
//
//    /**
//     * 根据key与泛型类型获取到储存到
//     * redis中的对象
//     *
//     * @param key
//     * @param clazz
//     * @return
//     */
//    @Override
//    public T getObj(String key, Class<T> clazz) {
//        String value = valueOperations.get(key);
//        if (value != null) {
//            return JsonFormatUtils.fromJson(value, clazz);
//        }
//        return null;
//    }
//
//    /**
//     * 删除掉指定key
//     *
//     * @param key
//     */
//    @Override
//    public void delete(String key) {
//        redisTemplate.delete(key);
//    }
//
//    /**
//     * 往hash中设置字符串值
//     *
//     * @param key       键
//     * @param hashKey   hash键
//     * @param hashValue hash值
//     */
//    @Override
//    public void hSet(String key, String hashKey, String hashValue) {
//        hashOperations.put(key, hashKey, hashValue);
//    }
//
//    /**
//     * 往hash中设置字对象
//     *
//     * @param key       键
//     * @param hashKey   hash键
//     * @param hashValue hash值
//     */
//    @Override
//    public <T> void hSetObj(String key, String hashKey, T hashValue) {
//        hashOperations.put(key, hashKey, JsonFormatUtils.toJson(hashValue));
//    }
//
//    /**
//     * 获取指定key，hashKey中的值（字符串）
//     *
//     * @param key     键
//     * @param hashKey hash键
//     */
//    @Override
//    public String hGet(String key, String hashKey) {
//        return hashOperations.get(key, hashKey);
//    }
//
//    /**
//     * 当前key hkey是否存在
//     *
//     * @param key
//     * @param hashKey
//     * @return
//     */
//    @Override
//    public boolean hexists(String key, String hashKey) {
//        return hashOperations.hasKey(key, hashKey);
//    }
//
//    /**
//     * 获取指定key，hashKey中的值(对象)
//     *
//     * @param key            键
//     * @param hashKey        hash键
//     * @param hashValueClazz 需要转换的类型
//     */
//    @Override
//    public Object hGetObj(String key, String hashKey, Class<?> hashValueClazz) {
//        String value = hashOperations.get(key, hashKey);
//        return value == null ? null : JsonFormatUtils.fromJson(value, hashValueClazz);
//    }
//
//    /**
//     * 移除掉key，hashKey中的值
//     *
//     * @param key     键
//     * @param hashKey hash键
//     */
//    @Override
//    public void hashRemove(String key, String hashKey) {
//        hashOperations.delete(key, hashKey);
//    }
//
//    @Override
//    public List hGetObjList(String key, String hashKey) {
////        return listOperations.range();
//        return null;
//    }
//
//    /**
//     * 获取Hash所有键值
//     *
//     * @param key
//     * @param hashValueClazz
//     * @return
//     */
//    @Override
//    public <E> Map<String, E> hGetAll(String key, Class<E> hashValueClazz) {
//        Map<String, String> entries = hashOperations.entries(key);
//        if (entries == null || entries.size() == 0) {
//            return Collections.emptyMap();
//        }
//        Map<String, E> resultMap = new LinkedHashMap<>(entries.size());
//        for (Map.Entry<String, String> entry : entries.entrySet()) {
//            E valueObj = JsonFormatUtils.fromJson(entry.getValue(), hashValueClazz);
//            resultMap.put(entry.getKey(), valueObj);
//        }
//        return resultMap;
//    }
//
//    /**
//     * 基于redis的set操作实现加锁
//     *
//     * @param key
//     * @param lockId     设置当前锁持有者的唯一ID
//     * @param expiryTime 锁时长
//     * @return
//     */
//    @Override
//    public boolean lock(String key, long lockId, int expiryTime) {
//        Object result = this.execute(lockRedisScript, key, Arrays.asList(String.valueOf(lockId), String.valueOf(expiryTime)));
//        /**
//         * 表达式执行成功
//         */
//        if (EXEC_RESULT.equals(result)) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 基于reids lua脚本实现原子
//     *
//     * @param key   --key
//     * @param value 设置当前锁持有者的唯一ID（不允许解锁其他线程的锁）
//     * @return
//     */
//    @Override
//    public boolean unlock(String key, Long value) {
//        if (value == null) {
//            return false;
//        }
//        Object result = this.execute(unlockRedisScript, key, Collections.singletonList(String.valueOf(value)));
//        /**
//         * 表达式执行成功
//         */
//        if (EXEC_RESULT.equals(result)) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 执行LUA脚本通用方法，需要定义脚本文件和脚本bean的信息
//     *
//     * @param redisScript
//     * @param key         所有的键
//     * @param args        所有的参数
//     * @return
//     */
//    @Override
//    public Object execute(RedisScriptBean redisScript, String key, List<String> args) {
//        return redisTemplate.execute(redisScript, Collections.singletonList(key), args);
//    }
//
//    /**
//     * 将 key 所储存的值加上增量 increment
//     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 命令*
//     *
//     * @param key
//     * @param integer
//     * @return 加上 increment 之后， key 的值
//     */
//    @Override
//    public Long incrBy(String key, long integer) {
//        return valueOperations.increment(key, integer);
//    }
//
//    /**
//     * 为哈希表 key 中的域 field 的值加上增量 increment
//     * 如果 key 不存在，一个新的哈希表被创建并执行 HINCRBY 命令
//     * 如果域 field 不存在，那么在执行命令前，域的值被初始化为 0
//     *
//     * @param key
//     * @param field
//     * @param integer
//     * @return 执行 HINCRBY 命令之后，哈希表 key 中域 field 的值
//     */
//    @Override
//    public Long hincrBy(String key, String field, long integer) {
//        return hashOperations.increment(key, field, integer);
//    }
//
//    @Override
//    public long hSetNx(String key, String hashKey, String hashValue) {
//        return hashOperations.putIfAbsent(key, hashKey, hashValue) ? 1 : 0;
//    }
//
//
//	@Override
//	public Set<String> keys(String pattern) {
//		return redisTemplate.keys(pattern);
//	}
//
//    /*
//     * @Description   原子累加
//     * @Param [key, step, defaultExpire]
//     * @Author toney
//     * @Date  10:37 2019/10/12
//     * @return java.lang.Long
//     **/
//    @Override
//    public Long incrBy(final String key, final long step, final int defaultExpire) {
//        List<String> list = new LinkedList<>();
//        list.add(String.valueOf(step));
//        list.add(String.valueOf(defaultExpire));
//        Object result= execute(increaseByScript,key,list );
//        return Long.parseLong(String.valueOf(result));
//    }
//
//    /**
//     * 加载执行脚本
//     */
//	@Override
//	public String scriptLoad(String text) {
//		RedisConnection connection = null;
//		try {
//			connection = redisTemplate.getConnectionFactory().getConnection();
//			return connection.scriptLoad(text.getBytes());
//		}catch (Exception e) {
//			log.error(e.getMessage(),e);
//			return null;
//		}finally {
//			if(connection != null && !connection.isClosed())
//				connection.close();
//		}
//	}
//
//	
//	@Override
//	public Object evalsha(String shakey, List<String> keys, List<String> args) {
//		RedisScriptBean<Object> scriptBean = new RedisScriptBean<>(); 
//		scriptBean.setSha1(shakey);
//		scriptBean.setResultType(Object.class);
//		return redisTemplate.execute(scriptBean, keys, args.toArray());
//	}
//}
