package com.panda.sport.rcs.mq.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.panda.sport.rcs.log.interceptors.LogInterceptor;
import com.panda.sport.rcs.monitor.mq.SendMqApi;
import com.panda.sport.rcs.utils.SpringContextUtils;

import lombok.extern.slf4j.Slf4j;

@ConditionalOnBean(name = "sendMqApi")
@ConditionalOnProperty("log.operate.switch")
@Configuration
@Slf4j
public class LogRecordConfig extends WebMvcConfigurationSupport  {
	
	@Autowired
	private SendMqApi sendMqApi;
	
	@Autowired
	private Environment env;
	
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
		LogInterceptor interceptor = new LogInterceptor((topic,tag,key,msg) ->  {
			sendMqApi.execute(topic, tag, key, msg);
        });
        registry.addInterceptor(interceptor).addPathPatterns("/**");
        if("1".equals(env.getProperty("rcs.sys.auth"))) {//开启权限
        	try {
        		HandlerInterceptor authInterceptor = SpringContextUtils.getBean("authInterceptorBean");
             	if(authInterceptor == null) {
             		log.warn("没有找到对应权限的拦截器类！");
             		return;
             	}
             	registry.addInterceptor(authInterceptor).addPathPatterns("/**");
        	}catch (Exception e) {
        		log.error(e.getMessage(),e);
        	}
        }else {
        	log.info("当前系统不开启权限！");
        }
    }
	
}

