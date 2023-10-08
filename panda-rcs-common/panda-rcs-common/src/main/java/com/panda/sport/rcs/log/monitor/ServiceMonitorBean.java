package com.panda.sport.rcs.log.monitor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.panda.sport.rcs.common.DateUtils;

import lombok.Data;

@Data
public class ServiceMonitorBean {
	
	/**
	 * 监控类型  MQ , DUBBO , HTTP
	 */
	private String monitorType;
	
	/**
	 * 监控编码
	 * 一般和uuid一起使用
	 */
	private String monitorCode;
	
	/**
	 * 日志id
	 */
	private String uuid;
	
	/**
	 * 日志归属时间，以第一个主日志为主
	 * 日志太多，后期考虑分库分表
	 */
	private String mainDateStr;
	
	/**
	 * 是不是主节点类型
	 * 1:是  2：否
	 */
	private Integer mainType;
	
	private String createTime;
	
	/**
	 * 接口处理类和方法
	 */
	private String handleClass;
	
	/**
	 * 服务节点名称
	 */ 
	private String serviceName;
	
	/**
	 * 执行时间
	 */
	private Long exeTime;
	
	/**
	 * 扩展参数
	 */
	private Map<String, String> extMap = new HashMap<String, String>();

	public ServiceMonitorBean(String monitorType, String uuid, Integer mainType, String handleClass,
			String serviceName) {
		this();
		this.monitorType = monitorType;
		this.uuid = uuid;
		this.mainType = mainType;
		this.handleClass = handleClass;
		this.serviceName = serviceName;
	}
	
	public ServiceMonitorBean(String monitorType, String uuid, Integer mainType, String handleClass,
			String serviceName,String mainDateStr) {
		this();
		this.monitorType = monitorType;
		this.uuid = uuid;
		this.mainType = mainType;
		this.handleClass = handleClass;
		this.serviceName = serviceName;
		this.mainDateStr = mainDateStr;
	}
	
	public ServiceMonitorBean() {
		this.createTime = DateUtils.parseDate(new Date().getTime(), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS);
	}
	
	public ServiceMonitorBean(String mainDateStr) {
		this.mainDateStr = mainDateStr;
	}
}
