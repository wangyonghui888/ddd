package com.panda.sport.rcs.mgr.paid.annotion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Z9-jing
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD})
public @interface BusinessLog {
}
