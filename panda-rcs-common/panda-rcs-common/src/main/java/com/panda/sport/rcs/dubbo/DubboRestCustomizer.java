package com.panda.sport.rcs.dubbo;

import org.apache.dubbo.remoting.http.servlet.BootstrapListener;
import org.apache.dubbo.remoting.http.servlet.DispatcherServlet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("dubbo.address")
public class DubboRestCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
	
	@Bean
	public ServletRegistrationBean<DispatcherServlet> registerServlet() {
		ServletRegistrationBean<DispatcherServlet> bean = new ServletRegistrationBean<DispatcherServlet>(new DispatcherServlet(), "/dubbo/*");
		bean.setName("dubboServlet");
		return bean;
	}

	@Override
	public void customize(TomcatServletWebServerFactory factory) {
		factory.addContextCustomizers((context) -> context.addApplicationListener(BootstrapListener.class.getName()));
	}

}
