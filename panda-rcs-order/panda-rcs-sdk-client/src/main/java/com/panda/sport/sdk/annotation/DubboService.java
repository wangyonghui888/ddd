package com.panda.sport.sdk.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.Singleton;

@Singleton
@Target({ElementType.TYPE})
@Retention(RUNTIME)
public @interface DubboService {

}
