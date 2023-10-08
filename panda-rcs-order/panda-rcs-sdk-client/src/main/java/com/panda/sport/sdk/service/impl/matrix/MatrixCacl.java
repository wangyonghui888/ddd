package com.panda.sport.sdk.service.impl.matrix;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Singleton;

/**
 * 举证计算
 */
@Target({ElementType.TYPE})
@Retention(RUNTIME)
@Singleton
public @interface MatrixCacl {
	
	/**
	 * 配置体种ids，逗号分隔，支持多体种，多玩法
	 * 格式：1:1,2,3,4,5;2:1,2,3,4
	 * @return
	 */
	String configs() default "";

}
