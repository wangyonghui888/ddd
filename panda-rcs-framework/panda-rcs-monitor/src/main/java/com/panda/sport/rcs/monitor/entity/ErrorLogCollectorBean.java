package com.panda.sport.rcs.monitor.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * 请求超时监控
 *
 */
@Data
public class ErrorLogCollectorBean implements Serializable {
	
	private String logContent;
	
	private Long currentDate;
	
}
