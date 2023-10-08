package com.panda.sport.rcs.core.hystrix.config;

import com.netflix.hystrix.HystrixCommandProperties;
import org.apache.dubbo.common.URL;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.core.hystrix.config
 * @Description :  隔离策略
 * @Date: 2019-10-10 15:56
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class IsolationStrategy {
	/**
	 * 线程池隔离
	 */
	public static final String THREAD = "THREAD";
	/**
	 * 信号量隔离
	 */
	public static final String SEMAPHORE = "SEMAPHORE";

	/**
	 * 获取隔离策略，默认使用线程池
	 *
	 * @param url
	 * @return
	 */
	public static HystrixCommandProperties.ExecutionIsolationStrategy getIsolationStrategy(URL url) {
		String isolation = url.getParameter("isolation", THREAD);
		if (!isolation.equalsIgnoreCase(THREAD) && !isolation.equalsIgnoreCase(SEMAPHORE)) {
			isolation = THREAD;
		}
		if (isolation.equalsIgnoreCase(THREAD)) {
			return HystrixCommandProperties.ExecutionIsolationStrategy.THREAD;
		} else {
			return HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE;
		}
	}
}
