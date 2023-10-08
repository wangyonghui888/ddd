package com.panda.sport.rcs.logback;

import java.net.InetAddress;

import ch.qos.logback.core.PropertyDefinerBase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RcsIpLogBack extends PropertyDefinerBase {
	
	private static String name = null;

	@Override
	public String getPropertyValue() {
		try {
			if(name == null) {
				name = InetAddress.getLocalHost().getHostAddress();
			}
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		if(name == null) {
			return "127.0.0.1";
		}
		
		return name;
	}
	
}
