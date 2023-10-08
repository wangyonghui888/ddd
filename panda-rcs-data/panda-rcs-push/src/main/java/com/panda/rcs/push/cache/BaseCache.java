package com.panda.rcs.push.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.panda.rcs.push.entity.constant.BaseConstant;
import com.panda.rcs.push.entity.vo.PlayInfoVo;
import com.panda.sport.rcs.cache.RcsCacheUtils;

public class BaseCache {

    public static Cache<String, PlayInfoVo> PLAY_SET_CACHE = RcsCacheUtils.newCache(BaseConstant.CACHE_PLAY_SET,
            500, 30000, 7200, 7200, 7200);

}
