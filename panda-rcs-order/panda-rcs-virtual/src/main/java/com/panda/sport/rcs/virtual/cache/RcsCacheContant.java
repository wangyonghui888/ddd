package com.panda.sport.rcs.virtual.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.panda.sport.rcs.cache.RcsCacheUtils;
import com.panda.sport.rcs.pojo.RcsOperateMerchantsSet;
import com.panda.sport.rcs.pojo.virtual.RcsVirtualUser;

public interface RcsCacheContant {

    /**
     * 赛事缓存
     */
    public static String VIRTUAL_USER = "RCS_VIRTUAL_USER";
    public static String RCS_OPERATE_MERCHANTS_SET = "RCS_OPERATE_MERCHANTS_SET";
    public static Cache<Long, RcsVirtualUser> VIRTUAL_CACHE = RcsCacheUtils.newCache(VIRTUAL_USER,
            200, 100000, 60, 60, 60);
    public static Cache<Long, RcsOperateMerchantsSet> RCS_OPERATE_MERCHANTS_SET_CACHE = RcsCacheUtils.newCache(RCS_OPERATE_MERCHANTS_SET,
            200, 100000, 60, 60, 60);

}
