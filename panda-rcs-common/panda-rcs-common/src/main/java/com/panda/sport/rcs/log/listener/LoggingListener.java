package com.panda.sport.rcs.log.listener;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

public class LoggingListener implements ApplicationListener,Ordered {

    /**
     * 提供给日志文件读取配置的key，使用时需要在前面加上 sys:
     */
    private final static String LOG_PATH = "log.path";

    /**
     * spring 内部设置的日志文件的配置key
     */
    private final static String SPRING_LOG_PATH_PROP = "log.path";

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ApplicationEnvironmentPreparedEvent) {
            ConfigurableEnvironment environment = ((ApplicationEnvironmentPreparedEvent) applicationEvent).getEnvironment();
            String key = (isOSLinux() ? "linux" : "win" ) + "." + SPRING_LOG_PATH_PROP;
            String filePath = environment.getProperty(key);
            if (!StringUtils.isBlank(filePath)) {
                System.setProperty(LOG_PATH,filePath);
            }
        }
    }
    
    public static boolean isOSLinux() {
    	Properties prop = System.getProperties();

    	String os = prop.getProperty("os.name");
    	if (os != null && os.toLowerCase().indexOf("linux") > -1) {
    	return true;
    	} else {
		    return false;
		}
	}

    @Override
    public int getOrder() {
    	// 当前监听器的启动顺序需要在日志配置监听器的前面，所以此处减 1
        return LoggingApplicationListener.DEFAULT_ORDER - 1;
    }
}

