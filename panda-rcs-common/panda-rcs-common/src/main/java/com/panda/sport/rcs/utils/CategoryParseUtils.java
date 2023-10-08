package com.panda.sport.rcs.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CategoryParseUtils {
	
	private static String COMPETITOR_1 = "{$competitor1}";
	
	private static String COMPETITOR_2 = "{$competitor2}";
	
	public static String parseName(String name ) {
		try {
			if(name.contains(COMPETITOR_1)) {
				return name.replace(COMPETITOR_1, "主队");
			}else if(name.contains(COMPETITOR_2)) {
				return name.replace(COMPETITOR_2, "客队");
			}
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
		return name;
	}

}
