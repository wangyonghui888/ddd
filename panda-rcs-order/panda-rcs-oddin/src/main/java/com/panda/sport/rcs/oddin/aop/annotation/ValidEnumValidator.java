package com.panda.sport.rcs.oddin.aop.annotation;


import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;

/**
 * @Auther: Conway
 * @Date: 2023/08/13 17:33
 * @Description: 枚举校验器具体校验规则实现
 */
@Slf4j
public class ValidEnumValidator implements ConstraintValidator<ValidEnum, Integer> {

    private Class<?> enumClass;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if(value != null && enumClass != null){
            try {
                Method isSupportMethod = enumClass.getDeclaredMethod("isSupport", Integer.class);
                Object result = isSupportMethod.invoke(null, value);
                boolean isSupport = Boolean.parseBoolean(result.toString());
                return isSupport;
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage(), e);
            }
        }
        return true;
    }

}
