package com.panda.sport.rcs.logback;

import ch.qos.logback.core.PropertyDefinerBase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RcsHostNameLogBack extends PropertyDefinerBase {
	
	private static String name = null;

	@Override
	public String getPropertyValue() {
		try {
			if(name == null) {
				name = System.getenv("HOSTNAME");
			}
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		if(name == null) {
			return "panda-rcs";
		}
		
		return name;
	}

}
