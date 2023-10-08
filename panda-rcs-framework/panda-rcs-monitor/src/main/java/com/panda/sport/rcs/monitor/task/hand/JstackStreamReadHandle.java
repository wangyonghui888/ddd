package com.panda.sport.rcs.monitor.task.hand;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.Callable;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.monitor.entity.SysInfoBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JstackStreamReadHandle implements Callable<SysInfoBean> {
	
	InputStream is;
	String type;
	OutputStream os;
	
	private SysInfoBean bean = new SysInfoBean();
	
	private String pid  = null;
	    
	public JstackStreamReadHandle(InputStream is, String type,SysInfoBean infoBean,String pid) {
		 this.is = is;
	     this.type = type;
	     this.os = null;
		 this.bean = infoBean;
		 this.pid = pid;
	}


	@Override
	public SysInfoBean call() throws Exception {
		BufferedReader in = null;
		try {
			log.info("解析cpu，JstackStreamReadHandle：{}",JSONObject.toJSONString(bean));
			
			in = new BufferedReader(new InputStreamReader(is));
   			StringBuilder sb = new StringBuilder();
   			char[] buff = new char[2048];
   			int length = 0;
			while( (length = in.read(buff)) > 0 ) {
				sb.append(new String(Arrays.copyOf(buff, length)));
			}
			String result = sb.toString();
			
			if(bean.getServiceBean() != null)
				bean.getServiceBean().setStackInfo(result);
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return bean;
	}
	
} 
