package com.panda.sport.rcs.oddin.aop.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @Auther: Conway
 * @Date: 2023/08/13 17:33
 * @Description: 枚举校验器，要使用该枚举校验器必须在枚举中有isSupport静态方法
 */
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { ValidEnumValidator.class })
public @interface ValidEnum {

    String message() default "enum value not support";

    Class<?> enumClass();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
