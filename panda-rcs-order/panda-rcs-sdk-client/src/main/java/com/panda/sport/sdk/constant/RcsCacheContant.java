package com.panda.sport.sdk.constant;

import com.github.benmanes.caffeine.cache.Cache;
import com.panda.sport.rcs.cache.RcsCacheUtils;

public interface RcsCacheContant {
	/**
	 * MTS熔断缓存
	 */
	public static String MTS_CIRCUIT_TAG = "MTS_CIRCUIT_TAG";
	public static Cache<String, String> MTS_CIRCUIT_TAG_CACHE = RcsCacheUtils.newSyncSimpleCache(1,5,10);
	
	
	
}
