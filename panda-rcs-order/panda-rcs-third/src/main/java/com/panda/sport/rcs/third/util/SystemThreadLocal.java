package com.panda.sport.rcs.third.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lithan
 * 线程临时属性设置
 */
public class SystemThreadLocal {
	private static ThreadLocal<Map<String,Object>> local = new ThreadLocal<>();

	public static void set(Map<String,Object> value) {
		local.set(value);
	}

	public static Map<String,Object> get() {
		return local.get();
	}

	public static void remove() {
		local.remove();
	}

	public static void set(String key,String value) {
		Map<String, Object> map = new HashMap<>();
		map.put(key, value);
		local.set(map);
	}

}
