package com.panda.sport.sdk.util;

import com.google.inject.Injector;

public class GuiceContext {
	
	private static Injector injector;
	
	public static void setInjector(Injector injector) {
		GuiceContext.injector = injector;
	}

	public static Injector getInjector() {
		return injector;
	}
	
	public static <T> T getInstance(Class<T> clazz) {
		return injector.getInstance(clazz);
	}

}
