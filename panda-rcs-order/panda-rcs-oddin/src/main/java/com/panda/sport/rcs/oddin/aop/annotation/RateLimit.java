package com.panda.sport.rcs.oddin.aop.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Beulah
 * @date 2023/4/6 10:50
 * @description 对外提供的接口请求频率限制
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RateLimit {

    /**
     * 限流的标识
     */
    String key() default "";

    /**
     * 最多的访问限制次数
     */
    double permitsPerSecond () ;

    /**
     * 获取令牌最大等待时间
     */
    long timeout();

    /**
     * 获取令牌最大等待时间,单位(例:分钟/秒/毫秒) 默认:毫秒
     */
    TimeUnit timeunit() default TimeUnit.MILLISECONDS;

    /**
     * 得不到令牌的提示语
     */
    String msg() default "The system is busy, please try again later.";


}
