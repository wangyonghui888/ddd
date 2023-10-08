package com.panda.sport.rcs.core.hystrix.config;

import com.netflix.hystrix.HystrixThreadPoolProperties;
import org.apache.dubbo.common.URL;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.core.hystrix.config
 * @Description :  线程池相关配置生成
 * @Date: 2019-10-10 15:58
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class HystrixThreadPoolPropertiesFactory {
	public static HystrixThreadPoolProperties.Setter create(URL url) {
		return HystrixThreadPoolProperties.Setter()
				//设置线程池最大并发数量
				.withCoreSize(url.getParameter("coreSize", 10))
				//确认maximumSize参数是否起作用，默认false
				.withAllowMaximumSizeToDivergeFromCoreSize(true)
				//设置线程池最大并发数量
				.withMaximumSize(url.getParameter("maximumSize", 20))
				//最大队列长度，默认-1 SynchronizeQueue 实现
				.withMaxQueueSize(-1)
				//设置keep-alive时间，默认1分钟，只有当maximumSize起作用的时候才会生效，该配置控制一个线程多久没有使用后释放
				.withKeepAliveTimeMinutes(url.getParameter("keepAliveTimeMinutes", 1));
	}
}
