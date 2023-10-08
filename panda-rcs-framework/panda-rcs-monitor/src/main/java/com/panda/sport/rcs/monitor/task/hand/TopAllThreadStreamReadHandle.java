package com.panda.sport.rcs.monitor.task.hand;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.monitor.entity.ServiceInfoBean;
import com.panda.sport.rcs.monitor.entity.SysInfoBean;
import com.panda.sport.rcs.monitor.utils.CpuParsUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TopAllThreadStreamReadHandle implements Callable<SysInfoBean> {
	
	InputStream is;
	String type;
	OutputStream os;
	
	private SysInfoBean bean = new SysInfoBean();
	
	private String pid  = null;
	    
	public TopAllThreadStreamReadHandle(InputStream is, String type,SysInfoBean infoBean,String pid) {
		 this.is = is;
	     this.type = type;
	     this.os = null;
		 this.bean = infoBean;
		 this.pid = pid;
	}


	@Override
	public SysInfoBean call() throws Exception {
		BufferedReader in = null;
		String info = null;
		try {
			log.info("解析cpu，TopAllThreadStreamReadHandle：{}",JSONObject.toJSONString(bean));
			
			List<ServiceInfoBean> list = new ArrayList<ServiceInfoBean>();
			in = new BufferedReader(new InputStreamReader(is));
   			int i = 0;
			while(!StringUtils.isEmpty(info = in.readLine()) || i == 0 ) {
				if(StringUtils.isEmpty(info)) {
					i ++ ;
					continue;
				}
				
				if(i == 1 && !info.contains("PID")) {
					list.add(CpuParsUtils.parseServiceInfo(null, info));
				}
			}
			
			bean.setServiceBeanList(list);
		}catch (Exception e) {
			log.error("info : {},e:{}",info, e.getMessage());
			log.error(e.getMessage(),e);
		}
		return bean;
	}
	
	
} 
