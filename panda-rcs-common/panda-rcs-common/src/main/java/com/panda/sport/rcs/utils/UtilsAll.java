package com.panda.sport.rcs.utils;

import java.util.UUID;

public class UtilsAll {
	
	public static String getUUid() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	public static String getUUid(String prefix) {
		return prefix + "-" + UUID.randomUUID().toString().replace("-", "");
	}

}
