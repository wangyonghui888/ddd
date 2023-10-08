package com.panda.sport.sdk.bean;

import org.apache.dubbo.config.ApplicationConfig;

import com.panda.sport.sdk.annotation.AutoInsert;
import com.panda.sport.sdk.annotation.handle.AutoInsertHandle;
import com.panda.sport.sdk.util.GuiceContext;
	
@AutoInsert(prefix = "sdk.dubbo.application,dubbo.application,spring.dubbo.application")
public class DubboApplicationConfig extends ApplicationConfig{

	public void init() {
		AutoInsertHandle handle = GuiceContext.getInstance(AutoInsertHandle.class);
		handle.handle(DubboApplicationConfig.class, this);
	}
	
}
