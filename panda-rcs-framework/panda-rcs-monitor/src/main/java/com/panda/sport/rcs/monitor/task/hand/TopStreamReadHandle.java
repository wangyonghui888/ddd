package com.panda.sport.rcs.monitor.task.hand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.monitor.entity.ServiceInfoBean;
import com.panda.sport.rcs.monitor.entity.SysInfoBean;
import com.panda.sport.rcs.monitor.utils.CpuParsUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TopStreamReadHandle implements Callable<SysInfoBean> {
	
	InputStream is;
	String type;
	OutputStream os;
	
	private SysInfoBean bean = new SysInfoBean();
	
	private String pid  = null;
	    
	public TopStreamReadHandle(InputStream is, String type,SysInfoBean infoBean,String pid) {
		 this.is = is;
	     this.type = type;
	     this.os = null;
		 this.bean = infoBean;
		 this.pid = pid;
	}


	@Override
	public SysInfoBean call() throws Exception {
		String info = null;
		try {
			log.info("解析cpu，TopStreamReadHandle：{}",JSONObject.toJSONString(bean));
			InputStreamReader isr = new InputStreamReader(is);
	    	BufferedReader brStat = new BufferedReader(isr);

			int i = 0;
			while(!StringUtils.isEmpty(info = brStat.readLine()) || i == 0 ) {
				if(StringUtils.isEmpty(info)) i ++ ;
				
				if(info.startsWith("top")) {//top - 10:33:56 up 22 days, 18:24,  2 users,  load average: 2.79, 2.42, 2.18
					CpuParsUtils.parseLoad(bean, info);
				}else if(info.contains("Cpu")) {//%Cpu(s): 78.9 us,  1.6 sy,  0.0 ni, 19.1 id,  0.0 wa,  0.0 hi,  0.4 si,  0.0 st
					CpuParsUtils.parseCpu(bean, info);
				}else if(info.startsWith("KiB Mem")) {//KiB Mem : 32778800 total,   547024 free, 22408428 used,  9823348 buff/cache
					CpuParsUtils.parseMem(bean, info);
				}else if(info.startsWith("KiB Swap")) {//KiB Swap:        0 total,        0 free,        0 used.  9906476 avail Mem
					CpuParsUtils.parseSwap(bean, info);
				}else if(info.contains(pid)) {//28141 root      20   0   12.3g   2.6g   7924 S   0.0  8.4  33:17.77 java
					CpuParsUtils.parseServiceInfo(bean, info);
				}
			}
		}catch (Exception e) {
			log.error("info : {},e:{}",info, e.getMessage());
			log.error(e.getMessage(),e);
		}
		return bean;
	}
	
} 
