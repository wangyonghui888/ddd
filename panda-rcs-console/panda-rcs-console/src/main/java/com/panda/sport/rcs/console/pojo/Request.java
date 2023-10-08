/**
 * 
 */
package com.panda.sport.rcs.console.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description  :  TODO
 * @author       :  Vito
 * @Date:  2019年9月11日 下午5:12:21
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
*/
@Data
public class Request<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String linkId;

	private String globalId;

    private T data;

	private Long dataSourceTime;


}
