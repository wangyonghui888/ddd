package com.panda.sport.rcs.common.constants;

import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;

import java.time.LocalDateTime;

/**
 * redis 常量
 */
public class RedisConstants {
    /**
     * 用户redis key
     */
    public static final String PREFIX = "rcs:user:monitor:";
    //默认缓存时间
    public static final Long CACHE_TIME = 5 * 60L;
    //当日开关
    public static final String DAY_TAST_STATUS = "rcs:user:monitor:day:status:%s";
    //当前用户规则的结果 key
    public static final String DAY_TAST_USER_RULE_RESULT = "rcs:user:monitor:tag:%s:user:%s:rule:%s:result";
    //当前用户规则的结果 key
    public static final String USER_RULE_LAST_SEND_TIME = "USER_RULE_LAST_SEND_TIME";
    //规则时间分界点 超过这个时间 就就缓存里面的规则
    public static final String USER_BOUNDARY_TIME = "rcs:user:monitor:user:boundary:time";

}
