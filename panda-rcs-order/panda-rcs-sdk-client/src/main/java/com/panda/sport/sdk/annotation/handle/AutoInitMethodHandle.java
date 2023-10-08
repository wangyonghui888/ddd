package com.panda.sport.sdk.annotation.handle;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Singleton;
import com.panda.sport.sdk.annotation.AutoInitMethod;
import com.panda.sport.sdk.annotation.Handle;
import com.panda.sport.sdk.util.GuiceContext;

@Singleton
public class AutoInitMethodHandle implements Handle{
	
    private static final Logger logger = LoggerFactory.getLogger(AutoInitMethodHandle.class);

	@Override
	public void handle(Class clazz) {
		AutoInitMethod auto = (AutoInitMethod) clazz.getAnnotation(AutoInitMethod.class);
		if(auto == null) return;
		
		handle(clazz, true);
	}
	
	public static void main(String[] args) {
		Long test = JSONObject.parseObject("123",Long.class);
		System.out.println(test);
	}

	@Override
	public void handle(Class clazz, Boolean isAuto) {
		AutoInitMethod auto = (AutoInitMethod) clazz.getAnnotation(AutoInitMethod.class);
		if(auto == null) return;
		
		Object obj = GuiceContext.getInstance(clazz);
		handle(clazz, obj);
	}

	@Override
	public void handle(Class clazz, Object obj) {
		AutoInitMethod auto = (AutoInitMethod) clazz.getAnnotation(AutoInitMethod.class);
		if(auto == null) return;
		
		try {
			String init = auto.init();
			if(init == null || init.length() <= 0) return;
			
			Method[] list = clazz.getMethods();
			for(Method method : list) {
				if(!method.getName().equals(init)) continue;
				
				method.invoke(obj, null);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
}
