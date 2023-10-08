package com.panda.sport.rcs.log.interceptors;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName(value = "rcs_log_record")
public class LogBean {

	
	private Long id;
	
	private Long exeTime;
	
	private String url;
	
	private String code;
	
	private String uuid;
	
	private String name;
	
	private String title;
	
	private String values;
	
	private String requestVal;
	
	private String returnVal;
	
	private String createTime;
	
	private String userId;
	
	private String ip;
	
	private String urlType;
	
	private String urlTypeVal;
	

}
