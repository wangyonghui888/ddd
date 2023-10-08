package com.panda.sport.rcs.zk.task;

import java.util.Map;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

public interface NodeChangeApi {
	
	public void nodeChange( PathChildrenCacheEvent event, Map<String, Map<String, Object>> map);

}
