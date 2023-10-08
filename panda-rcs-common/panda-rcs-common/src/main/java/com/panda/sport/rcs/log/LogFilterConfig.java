package com.panda.sport.rcs.log;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogFilterConfig {

	@Bean
    public FilterRegistrationBean<LogFilter> logFilter() {
        FilterRegistrationBean<LogFilter> registration = new FilterRegistrationBean<LogFilter>();
        registration.setFilter(new LogFilter());
        registration.addUrlPatterns("/*");
        registration.setName("logFilter");
        registration.setOrder(2);
        return registration;
    }
	
	@Bean
    public FilterRegistrationBean<ModifyBodyFilter> testFilterRegistration() {
        FilterRegistrationBean<ModifyBodyFilter> registration = new FilterRegistrationBean<ModifyBodyFilter>();
        registration.setFilter(new ModifyBodyFilter());
        registration.addUrlPatterns("/*");
        registration.setName("modifyUpdateFilter");
        registration.setOrder(1);
        return registration;
    }
}
