package com.panda.sport.rcs.mq.db;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;

import lombok.extern.slf4j.Slf4j;

/**
 * 
* @ClassName: SaveDbDelayCacheConsumer 
* @Description: 延迟更新数据库，只适用于只更新最后一条数据也不会影响数据库数据
* @author black  
* @date 2020年7月29日 下午5:48:15 
* 
* @param <T>
 */
@Slf4j
public abstract class SaveDbDelayUpdateConsumer<T> extends ConsumerAdapter<T> implements UpdateDataApi<T>{
	
    public SaveDbDelayUpdateConsumer(String consumerConfig) {
    	super(consumerConfig,"");
    	DataCache.addApi(this);
    }

	@Override
	public Boolean handleMs(T msg, Map<String, String> paramsMap) throws Exception {
		try {
			String cacheKey = getCacheKey(msg,paramsMap);
			if(StringUtils.isBlank(cacheKey)) {
				log.warn("当前缓存key获取是空，不做数据缓存，{}",JSONObject.toJSONString(msg));
				return true;
			}
			DataCache.save(cacheKey,msg,this.getClass().getName());
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
		return true;
	}
	
	public abstract String getCacheKey(T msg,Map<String, String> paramsMap) ;
	
}
