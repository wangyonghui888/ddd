package com.panda.sport.rcs.monitor.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.monitor.entity.ThreadBean;
import com.panda.sport.rcs.monitor.service.ThreadInfoService;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * @author tycoding
 * @date 2019-05-10
 */
@Service
public class ThreadInfoServiceImpl implements ThreadInfoService {

    @Override
    public ThreadBean get() {
        return init();
    }

    private ThreadBean init() {
        ThreadBean bean = new ThreadBean();
        ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
        bean.setCurrentTime(mxBean.getCurrentThreadUserTime());
        bean.setDaemonCount(mxBean.getDaemonThreadCount());
        bean.setCount(mxBean.getThreadCount());
        bean.setTotalStartedThreadCount(mxBean.getTotalStartedThreadCount());
        return bean;
    }
    
    public static void main(String[] args) {
    	System.out.println(JSONObject.toJSONString(new ThreadInfoServiceImpl().init()));
    	
	}
}
