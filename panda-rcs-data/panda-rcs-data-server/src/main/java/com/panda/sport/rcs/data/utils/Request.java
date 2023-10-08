/**
 * 
 */
package com.panda.sport.rcs.data.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description  :  TODO
 * @author       :  v
 * @Date:  2019年9月11日 下午5:12:21
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
*/
@Data
public class Request<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String globalId;
	
    private T data;	

    private Long dataSourceTime;
}
