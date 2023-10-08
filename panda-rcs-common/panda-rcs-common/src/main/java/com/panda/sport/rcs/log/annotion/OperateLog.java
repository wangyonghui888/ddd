package com.panda.sport.rcs.log.annotion;

import com.panda.sport.rcs.enums.OperateLogEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Date;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention (RUNTIME)
@Target({ElementType.METHOD})
public @interface OperateLog {
    /**
     * 操作類別
     * @return
     */
    OperateLogEnum operateType() default OperateLogEnum.NONE ;
    /**
     * 參數名稱
     * @return
     */
    OperateLogEnum operateParamter() default OperateLogEnum.NONE;
}
