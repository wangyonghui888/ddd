package com.panda.sport.sdk.annotation;

public interface Handle {
	
	public void handle(Class clazz);
	
	public void handle(Class clazz,Boolean isAuto);
	
	public void handle(Class clazz,Object obj);

}
