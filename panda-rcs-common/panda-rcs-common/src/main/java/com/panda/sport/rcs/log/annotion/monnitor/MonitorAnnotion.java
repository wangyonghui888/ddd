package com.panda.sport.rcs.log.annotion.monnitor;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention (RUNTIME)
@Target({ElementType.TYPE ,ElementType.METHOD})
public @interface MonitorAnnotion {

	public String code();
	
	public String value() default "";
}
