package com.panda.sport.rcs.core.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
/**
 * 定义针对一些比较特殊的业务场景
 * 需要查主库的时候才配置该注解
 * @author kane
 * @since 2019-09-04
 * @see java.lang.annotation.Annotation
 * @verion v1.1
 */
public @interface Master {
}
