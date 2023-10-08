/**
 * 
 */
package com.panda.sport.rcs.mgr.mq.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class Request<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String linkId;
	
    private T data;	
    
    
	public Request(){}

}
