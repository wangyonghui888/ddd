package com.panda.sport.rcs.mgr.aspect;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.aspect
 * @Description :  RcsLock
 * @Date: 2020-04-01 15:21
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RcsLockable {
    String key() default "";

    /**
     * 0：所有；1：单关；2：串关
     * @return
     */
    RcsLockSeriesTypeEnum seriesType() default RcsLockSeriesTypeEnum.All;
}
