package com.panda.sport.rcs.monitor.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * 心跳
 *
 */
@Data
public class HeartCollectorBean implements Serializable {
	
	public HeartCollectorBean(Long currentTime) {
		this.currentTime = currentTime;
	}

	private Long currentTime;
}
