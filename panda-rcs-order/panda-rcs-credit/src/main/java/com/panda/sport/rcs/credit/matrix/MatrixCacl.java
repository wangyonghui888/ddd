package com.panda.sport.rcs.credit.matrix;

import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 举证计算
 */
@Target({ElementType.TYPE})
@Retention(RUNTIME)
@Service
public @interface MatrixCacl {
	
	/**
	 * 配置体种ids，逗号分隔，支持多体种，多玩法
	 * 格式：1:1,2,3,4,5;2:1,2,3,4
	 * @return
	 */
	String configs() default "";

}
