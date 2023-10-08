package com.panda.sport.rcs.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.common.annotation
 * @description :  用于统计的注解
 * @date: 2020-06-23 13:23
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface StaticsItem {
    /*** 关键字,用于区分不同的统计指标   ***/
    String keyWord() default "";

    /*** 标识当前统计指标是否适用通用统计方法   ***/
    boolean different() default false;

    /*** 标识childType ***/
    int  order() default 0;
}
