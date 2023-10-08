package com.panda.sport.rcs.monitor.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.monitor.entity.ServiceInfoBean;
import com.panda.sport.rcs.monitor.entity.SysInfoBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CpuParsUtils {
	
	public static SysInfoBean parseLoad(SysInfoBean bean,String info) {
		String load = info.substring(info.indexOf("load average:")).split(":")[1];
		bean.setLoadAvg1(load.split(",")[0].trim());
		bean.setLoadAvg2(load.split(",")[1].trim());
		bean.setLoadAvg3(load.split(",")[2].trim());
		return bean;
	}
	
	public static SysInfoBean parseCpu(SysInfoBean bean,String info) {
		String[] cpu = info.split(":")[1].split(",");
		bean.setUsCpu(cpu[0].trim().split(" ")[0].trim());
		bean.setSyCpu(cpu[1].trim().split(" ")[0].trim());
		bean.setNiCpu(cpu[2].trim().split(" ")[0].trim());
		bean.setIdCpu(cpu[3].trim().split(" ")[0].trim());
		bean.setWaCpu(cpu[4].trim().split(" ")[0].trim());
		bean.setHiCpu(cpu[5].trim().split(" ")[0].trim());
		bean.setSiCpu(cpu[6].trim().split(" ")[0].trim());
		bean.setStCpu(cpu[7].trim().split(" ")[0].trim());
		return bean;
	}
	
	public static SysInfoBean parseMem(SysInfoBean bean,String info) {
		String[] mem = info.split(":")[1].split(",");
		bean.setTotalMem(mem[0].trim().split(" ")[0].trim());
		bean.setFreeMem(mem[1].trim().split(" ")[0].trim());
		bean.setUsedMem(mem[2].trim().split(" ")[0].trim());
		bean.setCacheMem(mem[3].trim().split(" ")[0].trim());
		return bean;
	}
	
	public static SysInfoBean parseSwap(SysInfoBean bean,String info) {
		String[] mem = info.split(":")[1].split(",");
		bean.setTotalSwap(mem[0].trim().split(" ")[0].trim());
		bean.setFreeSwap(mem[1].trim().split(" ")[0].trim());
		bean.setUsedSwap(mem[2].trim().split(" ")[0].trim());
//		bean.setCacheSwap(mem[3].trim().split(" ")[0].trim());
		return bean;
	}

	public static ServiceInfoBean parseServiceInfo(SysInfoBean bean, String info) {
		//28141 root      20   0   12.3g   2.6g   7924 S   0.0  8.4  33:17.77 java
		List<String> list = new ArrayList<String>();
		for(String key : info.split(" ")) {
			if(StringUtils.isEmpty(key)) continue;
			list.add(key);
		}
		if(list.size() < 10) {
			log.error("解析错误：{}，list:{}",info,JSONObject.toJSONString(list));
		}
		ServiceInfoBean serviceBean = new ServiceInfoBean();
		serviceBean.setPid(list.get(0));
		serviceBean.setUser(list.get(1));
		serviceBean.setPr(list.get(2));
		serviceBean.setNi(list.get(3));
		serviceBean.setVirt(list.get(4));
		serviceBean.setRes(list.get(5));
		serviceBean.setShr(list.get(6));
		serviceBean.setS(list.get(7));
		serviceBean.setCpu(list.get(8));
		serviceBean.setMem(list.get(9));
		serviceBean.setTime(list.get(10));
		
		if(bean != null) bean.setServiceBean(serviceBean);
		
		return serviceBean;
	}

}
