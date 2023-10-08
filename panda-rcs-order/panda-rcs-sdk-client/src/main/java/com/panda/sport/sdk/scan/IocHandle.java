package com.panda.sport.sdk.scan;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.panda.sport.sdk.annotation.AnnotationMapper;
import com.panda.sport.sdk.annotation.Handle;
import com.panda.sport.sdk.util.GuiceContext;

@Singleton
public class IocHandle {
	
	private static final Logger logger = LoggerFactory.getLogger(IocHandle.class);
	
	AnnotationMapper mapper;
	
	ClasspathPackageScanner scan;
	
	public IocHandle(){
		try {
			this.mapper = GuiceContext.getInstance(AnnotationMapper.class);
			this.scan = GuiceContext.getInstance(ClasspathPackageScanner.class);

			Map<String, Handle> all = mapper.getAllMapper();
			for(String key : all.keySet()) {
				List<String> list = scan.getAllMatchByAnnotion(Class.forName(key));
				for(String obj : list) {
					ioc(Class.forName(obj), all.get(key));
				}
			}
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public void ioc(Class obj ,Handle handle) {
		ioc(obj, handle, true);
	}
	
	public void ioc(Class clazz ,Handle handle,boolean isAuto) {
		if(isAuto)
			handle.handle(clazz);
		else
			handle.handle(clazz,isAuto);
	}
	
	public void ioc(Class clazz ,Handle handle,Object obj) {
		handle.handle(clazz, obj);
	}
	
}
