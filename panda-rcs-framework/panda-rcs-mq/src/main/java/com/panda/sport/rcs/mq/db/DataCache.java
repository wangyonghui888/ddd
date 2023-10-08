package com.panda.sport.rcs.mq.db;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
* @ClassName: DataCache 
* @Description: 本地缓存数据
* @author black  
* @date 2020年7月30日 上午11:00:04 
*
 */
public class DataCache {
	
	/**
     * 存储对象 只存一条  后面的最新数据 会覆盖前面的数据
     */
	private static Map<String, Object> dataMap = new ConcurrentHashMap<String, Object>(3000); 
	
	private static Map<String, UpdateDataApi> apiMap = new HashMap<String, UpdateDataApi>();
	
	public static void addApi(UpdateDataApi api) {
		UpdateDbTask.startUpdateThread();
		apiMap.put(api.getClass().getName(), api);
	}
	
	public static <T> UpdateDataApi<T> getApi(String key){
		String apiKey = key.substring(0,key.indexOf("#"));
		return apiMap.get(apiKey);
	}

	public static void save(String key , Object obj , String className) {
		String cacheKey = String.format("%s#%s", className,key);
		dataMap.put(cacheKey, obj);
	}
	
	public synchronized static Map<String, Object> getAllCache(){
		Map<String, Object> tempMap = dataMap;
		dataMap = new ConcurrentHashMap<String, Object>(3000);  
		return tempMap;
	}
	
	public static Integer getSize() {
		return dataMap.size();
	}
	
	public static void main(String[] args) {
		DataCache.save("test", "abc", "class");
		DataCache.save("test1", "abc1", "class");
		DataCache.save("test2", "abc2", "class");
		
		System.out.println(DataCache.getAllCache());
	}
}
