package com.panda.sport.rcs.core.hystrix.config;


import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import org.apache.dubbo.common.URL;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.core.hystrix.config
 * @Description :  HystrixCommand.Setter 工厂，生成的配置会放入缓存，下次直接获取
 * @Date: 2019-10-10 15:57
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class SetterFactory {
	private static ConcurrentHashMap<String, HystrixCommand.Setter> setterMap = new ConcurrentHashMap<String, HystrixCommand.Setter>();

	public static HystrixCommand.Setter create(String interfaceName, String methodName, URL url) {

		String key = String.format("%s.%s", interfaceName, methodName);
		if (setterMap.containsKey(key)) {
			return setterMap.get(key);
		} else {
			setterMap.putIfAbsent(key, doCreate(interfaceName, methodName, url));
			return setterMap.get(key);
		}
	}

	private static HystrixCommand.Setter doCreate(String interfaceName, String methodName, URL url) {
		//线程池按class进行划分，一个class可以理解为一个领域服务，熔断保护按方法维度提供
		return HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(interfaceName))
				.andCommandKey(HystrixCommandKey.Factory.asKey(methodName))
				.andCommandPropertiesDefaults(HystrixCommandPropertiesFactory.create(url, methodName))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolPropertiesFactory.create(url));
	}
}
