package com.panda.sport.rcs.third.entity.common;

import lombok.Data;

@Data
public class DataRealTimeMessageBean<T> {
	
	private String linkId;
	
	private T data;

	private String dataType;
	
	private Long dataSourceTime;

}