package com.panda.sport.rcs.task.job.clean;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.panda.sport.rcs.pojo.statistics.RcsProfitMarket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 清除缓存数据，通过赛事
 * @author Administrator
 *
 */
@JobHandler(value = "cleanCacheByMatch")
@Component
@Slf4j
public class CleanCacheByMatch extends IJobHandler {
	
	private String PROFIT_CACHE_KEY="Rcs:realVolume:matchId=%s";
	
    @Autowired
    protected  RedisClient redisClient;
    
    @Autowired
    private StandardMatchInfoMapper mapper;


	@Override
	public ReturnT<String> execute(String param) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		List<String> matchIds = mapper.queryMatchEndIds(params);
		
		if(matchIds != null && matchIds.size() > 0 ) {
			matchIds.forEach(matchId -> {
				cleanCache(matchId);
			});
		}
		
		return SUCCESS;
	}

	/**
	 * 清队缓存
	 * @param matchId
	 */
	private void cleanCache(String matchId) {
//		String cacheKey=String.format(PROFIT_CACHE_KEY,matchId);
//		redisClient.batchDel(cacheKey);
//
//		String key = String.format("rcs:profit:match:%s:", matchId);
//		redisClient.batchDel(key);
//
//		String predictKey = String.format("rcs.risk.predict.*match_id.%s", matchId);
//		redisClient.batchDel(predictKey);

	}



}
