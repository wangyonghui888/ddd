package com.panda.sport.rcs.utils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsUtis {
	
	static Logger log = LoggerFactory.getLogger(OsUtis.class);
	
	public static String SERVER_NAME = null; 
	
	private static String ip = null;
	
	//rocket name server 配置名称
	private static String ROCKET_NAME_SERVER_CONFIG_NAME = "rocketmq.producer.namesrvAddr";
	
	private static String mqGroupName = null;
	
	private static String pid = null;
	
	private static String hostName = null;
	
	public static boolean isLinux() {
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().startsWith("windows")) {
			return false;
		} else if (osName.toLowerCase().startsWith("linux")) {
			return true;
		}
		return false;
	}
	
	public static String getHostName() {
		if(hostName == null ) {
			hostName = System.getProperty("user.name");
		}
		return hostName;
	}
	
	public static String getPid() {
		if(pid == null ) {
			String name = ManagementFactory.getRuntimeMXBean().getName();  
			pid = name.split("@")[0];  
		}
		return pid;
	}
	
	public static String getIp() {
		try {
			if(ip == null ) {
				ip = InetAddress.getLocalHost().getHostAddress();
			}
			return ip;
		} catch (UnknownHostException e) {
			log.error(e.getMessage(),e);
		}
		return null;
	}

	public static String getServerName() {
		return SERVER_NAME;
	}
	public static void setServerName(String serverName) {
		SERVER_NAME = serverName;
	}

	public static String getMqNameServerConfigName() {
		return ROCKET_NAME_SERVER_CONFIG_NAME;
	}

	public static void setMqNameServerConfigName(String mqNameServerConfigName) {
		ROCKET_NAME_SERVER_CONFIG_NAME = mqNameServerConfigName;
	}

	public static String getMqGroupName() {
		return mqGroupName;
	}

	public static void setMqGroupName(String mqGroupName) {
		OsUtis.mqGroupName = mqGroupName;
	}
	
}
