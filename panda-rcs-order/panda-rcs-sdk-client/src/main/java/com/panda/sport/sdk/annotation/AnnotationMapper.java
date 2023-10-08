package com.panda.sport.sdk.annotation;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.inject.Singleton;
import com.panda.sport.sdk.annotation.handle.AutoInitMethodHandle;
import com.panda.sport.sdk.annotation.handle.AutoInsertHandle;
import com.panda.sport.sdk.util.GuiceContext;

@Singleton
public class AnnotationMapper {
	
	private Map<String, Handle> allMapper = new LinkedHashMap<String, Handle>();

	public AnnotationMapper() {
		allMapper.put(AutoInsert.class.getName(), GuiceContext.getInstance(AutoInsertHandle.class));
		allMapper.put(AutoInitMethod.class.getName(), GuiceContext.getInstance(AutoInitMethodHandle.class));
	}
	
	public Map<String, Handle> getAllMapper(){
		return this.allMapper;
	}
	
}
