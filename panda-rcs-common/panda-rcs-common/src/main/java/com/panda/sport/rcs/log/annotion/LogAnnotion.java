package com.panda.sport.rcs.log.annotion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention (RUNTIME)
@Target({ElementType.METHOD})
public @interface LogAnnotion {

    String code() default "";

    String name() default "";

    String[] title() default {};

    String[] keys() default {};
    
    String urlType() default "";
    
    String urlTypeVal() default "";
}
