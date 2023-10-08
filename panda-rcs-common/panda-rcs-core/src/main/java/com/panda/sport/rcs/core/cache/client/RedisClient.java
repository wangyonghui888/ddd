package com.panda.sport.rcs.core.cache.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.panda.sport.rcs.core.bean.RedisScriptBean;

/**
 * 封装好的redis 客户端
 * 不需要关心当前redis是集群模式
 * 还是单机模式
 * @author kane
 * @since 2019-09-06
 * @version v1.1
 * @param <T>
 */
public interface RedisClient<T> {
    /**
     * 往redis中设置值
     * @param key
     * @param value
     */
    <T> void set(final String key, T value);

    /**
     * 设置值，并指定有效期
     * @param key
     * @param value
     * @param expiry
     * @param <T>
     */
    <T> void setExpiry(String key, T value, Long expiry);

    /**
     * 指定有效期
     * @param key
     * @param expire
     * @param <T>
     */
    <T> void expireKey(String key, Integer expire);

    /**
     *  查询当前redis中是否存在此key
     * @param key
     * @return
     */
    boolean exist(String key);

    /**
     * 根据key获取 redis中的值
     * @param key
     * @return
     */
    String get(final String key);

    /**
     * 根据key获取 redis中的值
     * @param key
     * @param clazz
     * @return
     */
    T getObj(final String key,Class<T> clazz);

    /**
     * 根据key删除指定的redis中的值
     * @param key
     * @param <T>
     */
    <T> void delete(String key);

    /**
     * 根据key前缀批量删除缓存
     * @param key
     * @return
     */
    long batchDel(String key);


    /**
     * 根据key hashKey 设置hash值
     * @param key
     * @param hashKey
     * @param hashValue
     */
     void hSet(String key, String hashKey, String hashValue);
     
     /**
      * 根据key hashKey 设置hash值
      * @param key
      * @param hashKey
      * @param hashValue
      */
     long hSetNx(String key, String hashKey, String hashValue);
    /**
     * 根据key hashKey 设置hash值
     * @param key
     * @param hashKey
     * @param hashValue
     * @param <T>
     */
    <T> void hSetObj(String key, String hashKey, T hashValue);


    /**
     * 根据当前key hkey 获取hash 对象中的字符串
     * @param key
     * @param hashKey
     * @return
     */
    String hGet(String key, String hashKey);

    /**
     * 当前key hkey是否存在
     * @param key
     * @param hashKey
     * @return
     */
    boolean hexists(String key, String hashKey);

    /**
     * 根据当前key hkey 获取hash 对象
     * @param key
     * @param hashKey
     * @param hashValueClazz
     * @return
     */
    Object hGetObj(String key, String hashKey, Class<?> hashValueClazz);

    Long hashRemove(String key, String hashKey);

    List hGetObjList(String key, String hashKey);

    /**
     * 获取Hash所有键值
     * @param key
     * @param hashValueClazz
     * @return
     */
    <E> Map<String, E> hGetAll(String key, Class<E> hashValueClazz);
    
    /**基于redis的set操作实现加锁
     * @param key
     * @param lockId 锁时长
     * @param  expiryTime key 失效时间
     * @return
     */
    boolean lock(String key,long lockId,int expiryTime);

    /**
     *  基于reids lua脚本实现原子
     *  性操作进行解锁
     * @param key
     * @param value
     * @return
     */
    boolean unlock(String key, Long value);


    /**
     * 执行LUA脚本通用方法，需要定义脚本文件和脚本bean的信息
     *
     * @param redisScript
     * @param key 键
     * @param args 所有的参数
     * @return
     */
    Object execute(RedisScriptBean redisScript, String key, List<String> args);

    /**
     * 将 key 所储存的值加上增量 increment
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 命令*
     *
     * @param key
     * @param integer
     * @return 加上 increment 之后， key 的值
     */
    Long incrBy(String key, long integer);
    
    
    Double incrByFloat(String key, Double integer);

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
    Long hincrBy(String key, String field, long integer);


	/**
	 * 模糊匹配
	 */
	Set<String> keys(String pattern);

    /**
     * @Description  原子累加并设置有效期
     * @Param [key 键, step 值, defaultExpire 有效期，单位为秒]
     * @Author toney
     * @Date  10:32 2019/10/12
     * @return java.lang.Long
     **/
	Long incrBy(final String key, final long step, final int defaultExpire);

	
	/**
	 * 加载脚本
	 * @param text
	 * @return
	 */
	String scriptLoad(String text);

	
	/**
	 * 执行缓存脚本  
	 * @param shakey
	 * @param keys
	 * @param args
	 * @return  list
	 */
	Object evalsha(String shakey, List<String> keys, List<String> args);

	<E> Map<String, E> hGetAllByJson(String key, Class<E> hashValueClazz);

    Object hGetAllToObj(String key);

    String getLock(String lockKey, int expire, long timeout);
    /**
     * @Description   设置nx
     * @Param [key, value, expired]
     * @Author  toney
     * @Date  15:11 2020/4/1
     * @return boolean
     **/
    boolean setNX(String key, String value, long expired);
    /**
     * @Description   执行lua脚本
     * @Param [script, keys, args]
     * @Author  toney
     * @Date  15:05 2020/4/1
     * @return java.lang.Object
     **/
    Object lua(String script, List<String> keys, List<String> args);

    Double hincrByFloat(String key, String field, Double val);
}