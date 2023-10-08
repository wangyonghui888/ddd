package com.panda.sport.rcs.mq.db;

public interface UpdateDataApi<T> {
	
	public Boolean updateData(T bean);

}
