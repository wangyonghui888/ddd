package com.panda.sport.rcs.cache;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RcsCacheUtils {
	
	private static Map<String, Cache> cacheMap = new HashMap<>();
	
	/**
	 * 创建一个简单的缓存器
	 * 手动加载缓存
	* @Title: newSyncSimpleCache 
	* @Description: TODO 
	* @param @param <K>
	* @param @param <V>
	* @param @param initSize
	* @param @param maxSize
	* @param @param seconds
	* @param @return    设定文件 
	* @return LoadingCache<K,V>    返回类型 
	* @throws
	 */
	public static <K ,V > Cache<K ,V > newSyncSimpleCache(int initSize , int maxSize ,int seconds){
		Cache<K ,V > cache = Caffeine.newBuilder().initialCapacity(initSize)
//					.weakKeys()
					.softValues()
					.maximumSize(maxSize)
					.expireAfterWrite(seconds, TimeUnit.SECONDS)
					.build();
		
		return cache;
	}
	
	/**
	 * 创建一个自动同步加载
	* @Title: newSyncSimpleCache 
	* @Description: TODO 
	* @param @param <K>
	* @param @param <V>
	* @param @param initSize
	* @param @param maxSize
	* @param @param seconds
	* @param @param load
	* @param @return    设定文件 
	* @return LoadingCache<K,V>    返回类型 
	* @throws
	 */
	public static <K ,V > LoadingCache<K ,V > newSyncSimpleCache(int initSize , int maxSize ,int seconds,
			CacheLoader<K, V> load){
		 return Caffeine.newBuilder().initialCapacity(initSize).softValues()
				.maximumSize(maxSize)
				.expireAfterWrite(seconds, TimeUnit.SECONDS)
				.build(load);
	}
	
	
	public static <K ,V > Cache<K ,V > newSyncCache(int initSize , int maxSize, 
			int createTime , int updateTime , int readTime){
		return Caffeine.newBuilder().initialCapacity(initSize).softValues()
				.maximumSize(maxSize)
				.expireAfter(new Expiry<K, V>() {

					@Override
					public long expireAfterCreate(@NonNull K key, @NonNull V value, long currentTime) {
						return TimeUnit.SECONDS.toNanos(createTime);
					}

					@Override
					public long expireAfterUpdate(@NonNull K key, @NonNull V value, long currentTime,
							@NonNegative long currentDuration) {
						return TimeUnit.SECONDS.toNanos(updateTime);
					}

					@Override
					public long expireAfterRead(@NonNull K key, @NonNull V value, long currentTime,
							@NonNegative long currentDuration) {
						return TimeUnit.SECONDS.toNanos(readTime);
					}
				})
				.build();
	}
	
	/**
	 * 创建异步缓存
	* @Title: newAsyncCache 
	* @Description: TODO 
	* @param @param <K>
	* @param @param <V>
	* @param @param initSize
	* @param @param maxSize
	* @param @param createTime
	* @param @param updateTime
	* @param @param readTime
	* @param @return    设定文件 
	* @return Cache<K,V>    返回类型 
	* @throws
	 */
	public static <K ,V > AsyncCache<K ,V > newAsyncCache(int initSize , int maxSize, 
			int createTime , int updateTime , int readTime){
		return Caffeine.newBuilder().initialCapacity(initSize).softValues()
				.maximumSize(maxSize)
				.expireAfter(new Expiry<K, V>() {

					@Override
					public long expireAfterCreate(@NonNull K key, @NonNull V value, long currentTime) {
						return TimeUnit.SECONDS.toNanos(createTime);
					}

					@Override
					public long expireAfterUpdate(@NonNull K key, @NonNull V value, long currentTime,
							@NonNegative long currentDuration) {
						return TimeUnit.SECONDS.toNanos(updateTime);
					}

					@Override
					public long expireAfterRead(@NonNull K key, @NonNull V value, long currentTime,
							@NonNegative long currentDuration) {
						return TimeUnit.SECONDS.toNanos(readTime);
					}
				})
				.buildAsync();
	}
	
	
	public static synchronized <K ,V > Cache<K ,V > newCache(String cacheType , int initSize , int maxSize, 
			int createTime , int updateTime , int readTime) {
		Cache<K ,V > cache = newSyncCache(initSize, maxSize, createTime, updateTime, readTime);
		
		cacheMap.put(cacheType, cache);
		return cache;
	}
	
	public static <K ,V > V getCacheVal(String cacheType , K key , Function<K, V> func ,
			int initSize , int maxSize, 
			int createTime , int updateTime , int readTime) {
		try {
			if(!cacheMap.containsKey(cacheType)) {
				newCache(cacheType , 200, 5000, 30, 30, 30);
			}
			
			Cache<K, V> cache = cacheMap.get(cacheType);
			
			return cache.get(key, func);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
		return null;
	}
	
	
	public static <K ,V > V getCacheVal(String cacheType , K key , Function<K, V> func) {
		return getCacheVal(cacheType, key, func, 200, 5000, 30, 30, 30);
	}
	
	public static void main(String[] args) throws InterruptedException {
//		System.out.println(getCacheVal("test1", "key1", key -> {System.out.println("1111");return key + key ;}));
//		System.out.println(getCacheVal("test1", "key1", key -> {System.out.println("1111");return key + key ;}));
//
//		System.out.println(getCacheVal("test2", "key1", key -> {System.out.println("222");return 1 ;}));
//		System.out.println(getCacheVal("test2", "key1", key -> {System.out.println("222");return 2 ;}));
//
//		Cache<String , Object> cache = newSyncSimpleCache(10,10,2);
//		System.out.println(cache.get("123", key -> {System.out.println("3333");return key + key ;}));
//		System.out.println(cache.get("123", key -> {System.out.println("3333");return key + key ;}));
		BigDecimal rate = new BigDecimal("8559.99").divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_DOWN);
		System.out.println(rate);
	}
	

}
