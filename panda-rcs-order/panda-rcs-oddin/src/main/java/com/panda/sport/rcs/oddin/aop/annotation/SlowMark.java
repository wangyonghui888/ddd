package com.panda.sport.rcs.oddin.aop.annotation;

import java.lang.annotation.*;

/**
 * @author Beulah
 * @date 2023/4/6 14:13
 * @description 接口慢查询标记
 * third 对对应接口 url 在 rate频率内出现 5次 delay的延迟，则在cache时间内，改接口尝试走缓存
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface SlowMark {

    /**
     * 第三方标识
     */
    String third();

    /**
     * 具体的第三方接口
     */
    String url() default "";

    /**
     * 频率 单位:分钟 1分钟能缓慢次数达到5次则需要记录该接口响应慢
     */
    long rate() default 1;

    /**
     * 规定时间出现缓慢的次数
     */
    int num();

    /**
     * 接口延时最大容忍时间 默认500毫秒
     */
    long delay() default 500;

    /**
     * 接口延时走缓存时间 默认3小时
     */
    int cache() default 3;


}
