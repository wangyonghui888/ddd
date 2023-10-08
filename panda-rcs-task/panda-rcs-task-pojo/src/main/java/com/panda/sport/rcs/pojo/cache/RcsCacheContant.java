package com.panda.sport.rcs.pojo.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.panda.sport.rcs.cache.RcsCacheUtils;
import com.panda.sport.rcs.pojo.StandardMatchInfo;

public interface RcsCacheContant {

	/**
	 * 多语言缓存
	 */
	public static String LANGUAGE = "RCS_LANGUAGE";
	public static Cache<Long, String> LANGUAGE_CACHE = RcsCacheUtils.newCache(LANGUAGE, 
			500, 20000, 30, 30, 30);
	
	
	/**
	 * 赛事缓存
	 */
	public static String MATCHINFO = "RCS_MATCHINFO";
	public static Cache<Long, StandardMatchInfo> MATCHINFO_CACHE = RcsCacheUtils.newCache(MATCHINFO, 
			200, 1000, 60, 60, 60);
	
	
	/**
	 * 球队缓存
	 */
	public static String TEAMINFO = "RCS_TEAMINFO";
	public static Cache<Long, String> TEAMINFO_CACHE = RcsCacheUtils.newCache(TEAMINFO, 
			400, 2000, 60, 60, 60);
	
	
	/**
	 *投注项模板缓存
	 */
	public static String ODDSTEMPLATE = "RCS_ODDSTEMPLATE";
	public static Cache<Long, String> ODDSTEMPLATE_CACHE = RcsCacheUtils.newCache(ODDSTEMPLATE, 
			800, 4000, 60, 60, 60);
	
	
	
	
}
