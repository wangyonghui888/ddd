package com.panda.sport.rcs.data.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.panda.sport.rcs.cache.RcsCacheUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author V
 */
public class DataCache {

    //public static Cache<String, String> eventMarkCache = RcsCacheUtils.newSyncSimpleCache( 1000, 10000, 60*60);

//    public static Cache<String, String> dataSourceMarkCache = RcsCacheUtils.newSyncSimpleCache( 1000, 10000, 60*60);

//    public static Cache<String, String> dataSourceMarkCache = RcsCacheUtils.newCache("dataSourceMarkCache",100, 1000,60,60,60);

    //国际化缓存md5  language_internation
    public static Cache<String, String> insertCheckCache = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(100000).build();

}
