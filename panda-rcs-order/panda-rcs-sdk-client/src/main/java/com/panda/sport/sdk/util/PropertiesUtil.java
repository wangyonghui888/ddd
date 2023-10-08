package com.panda.sport.sdk.util;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

@Singleton
public class PropertiesUtil {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    private Properties properties = null;

    /**
     * 使用-DconfigName=value配置文件     * 
     * 使用System.getProperty(configName) 获取配置数据
     */
    public PropertiesUtil() {
    	 Properties pro = new Properties();
    	 String configName = "/sdk.properties";
         try {
        	 if(!StringUtils.isBlank(System.getProperty("configName"))) {
        		 configName = "/" + System.getProperty("configName");
        	 }
        	 logger.info("sdk读取配置文件：{}",configName);
             InputStream fileStream = PropertiesUtil.class.getResourceAsStream(configName);
             pro.load(fileStream);
         } catch (Exception e) {
             logger.error("PropertiesUtil文件在加载配置文件时出现异常:" + e.getMessage());
         }
         properties = pro;
         logger.info("属性加载：{}",JSONObject.toJSONString(pro));
    }
    
    public void put(String key,String value) {
        properties.put(key, value);
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }

    public Integer getInt(String key) {
        return NumberUtils.toInt(properties.getProperty(key),0);
    }

    public Integer getInt(String key ,int defaultValue) {
        return NumberUtils.toInt(properties.getProperty(key),defaultValue);
    }
    
    public Properties getProperties() {
    	return this.properties;
    }
}