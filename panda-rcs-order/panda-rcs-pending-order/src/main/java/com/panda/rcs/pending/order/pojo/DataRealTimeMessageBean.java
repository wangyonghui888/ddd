package com.panda.rcs.pending.order.pojo;

import lombok.Data;

@Data
public class DataRealTimeMessageBean<T> {
	
	private String linkId;
	
	private T data;

	private String dataType;
	
	private Long dataSourceTime;

	public DataRealTimeMessageBean(){}
}
