package com.panda.sport.sdk.annotation.handle;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.sdk.annotation.AutoInsert;
import com.panda.sport.sdk.annotation.Handle;
import com.panda.sport.sdk.util.GuiceContext;
import com.panda.sport.sdk.util.PropertiesUtil;

@Singleton
public class AutoInsertHandle implements Handle{
	
    private static final Logger logger = LoggerFactory.getLogger(AutoInsertHandle.class);
	
	@Inject
	PropertiesUtil propertiesUtil;

	@Override
	public void handle(Class clazz) {
		AutoInsert auto = (AutoInsert) clazz.getAnnotation(AutoInsert.class);
		if(auto == null) return;
		
		if(auto.isAuto()) handle(clazz, true);
	}
	
	private Field getFieldByClass(Class clazz,String className) {
		try {
			Field field = clazz.getDeclaredField(className);
			return field;
		}catch (NoSuchFieldException e) {
			if(clazz.getSuperclass() != null) {
				return getFieldByClass(clazz.getSuperclass(), className);
			}else {
				return null;
			}
		}
	}

	@Override
	public void handle(Class clazz, Boolean isAuto) {
		AutoInsert auto = (AutoInsert) clazz.getAnnotation(AutoInsert.class);
		if(auto == null) return;
		
		Object obj = GuiceContext.getInstance(clazz);
		handle(clazz, obj);
	}

	@Override
	public void handle(Class clazz, Object obj) {
		AutoInsert auto = (AutoInsert) clazz.getAnnotation(AutoInsert.class);
		if(auto == null || auto.prefix() == null || "".equals(auto.prefix())) return;
		
		for(String prefix : auto.prefix().split(",")) {
			prefix = prefix + ".";
			Properties properties = propertiesUtil.getProperties();
			Enumeration<?> enu = properties.propertyNames();
			while (enu.hasMoreElements()) {
				String key = String.valueOf(enu.nextElement());
				if(!key.startsWith(prefix))  continue;
				
				String fieldStr = key.replace(prefix, "");
				try {
					Field field = getFieldByClass(obj.getClass(), fieldStr);
					if(field == null ) continue;

					field.setAccessible(true);
					if(field.getType().equals(String.class)) {
						field.set(obj, String.valueOf(properties.get(key)));
					}else {
//						Method method = field.getType().getMethod("valueOf", String.class);
//						field.set(obj, field.getType().cast(properties.get(key)));
						field.set(obj, TypeUtils.cast(String.valueOf(properties.get(key)).trim(), field.getType(), new ParserConfig()));
					}
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println(TypeUtils.cast("1000", int.class, new ParserConfig()));
	}
	
}
