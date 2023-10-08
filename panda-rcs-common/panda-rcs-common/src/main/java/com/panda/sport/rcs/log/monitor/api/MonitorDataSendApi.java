package com.panda.sport.rcs.log.monitor.api;

public interface MonitorDataSendApi {
	
	public void sendMonitorData(String topic,String tags ,String keys ,Object msg) ;

}
