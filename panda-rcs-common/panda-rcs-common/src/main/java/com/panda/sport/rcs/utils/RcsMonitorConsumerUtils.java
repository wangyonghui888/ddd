package com.panda.sport.rcs.utils;

import com.panda.sport.rcs.log.LogContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RcsMonitorConsumerUtils {
	
	private static String hostName = OsUtis.getHostName();
	
    public static <T> T handleApi(String monitorCode , ApiCall call) {
    	try {
    		LogContext.getContext().setMonitorCode(monitorCode);
    		return (T) call.callApi();
    	}catch (Exception e) {
    		throw e;
    	}finally {
    		LogContext.getContext().setMonitorCode(null);
		}
    	 
    }
    
    public interface ApiCall {

        public Object callApi();

    }

}
