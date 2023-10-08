package com.panda.sport.rcs.core.hystrix.config;


import com.netflix.hystrix.HystrixCommandProperties;
import org.apache.dubbo.common.URL;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.core.hystrix.config
 * @Description :  命令相关配置
 * @Date: 2019-10-10 15:56
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class HystrixCommandPropertiesFactory {
	public static HystrixCommandProperties.Setter create(URL url, String method) {
		return HystrixCommandProperties.Setter()
				//熔断后的重试时间窗口，且在该时间窗口内只允许一次重试。即在熔断开关打开后，在该时间窗口允许有一次重试，如果重试成功，则将重置Health采样统计并闭合熔断开关实现快速恢复，否则熔断开关还是打开状态，执行快速失败。
				.withCircuitBreakerSleepWindowInMilliseconds(url.getMethodParameter(method, "sleepWindowInMilliseconds", 5000))
				//如果在一个采样时间窗口内，失败率超过该配置，则自动打开熔断开关实现降级处理，即快速失败。默认配置下采样周期为10s，失败率为50%。
				.withCircuitBreakerErrorThresholdPercentage(url.getMethodParameter(method, "errorThresholdPercentage", 50))
				//在熔断开关闭合情况下，在进行失败率判断之前，一个采样周期内必须进行至少N个请求才能进行采样统计，目的是有足够的采样使得失败率计算正确，默认为20。
				.withCircuitBreakerRequestVolumeThreshold(url.getMethodParameter(method, "requestVolumeThreshold", 20))
				//当隔离策略为THREAD时，当执行线程执行超时时，是否进行中断处理，默认为true。
				.withExecutionIsolationThreadInterruptOnTimeout(true)
				//执行超时时间，默认为1000毫秒，如果命令是线程隔离，且配置了executionIsolationThreadInterruptOnTimeout=true，则执行线程将执行中断处理。如果命令是信号量隔离，则进行终止操作，因为信号量隔离与主线程是在一个线程中执行，其不会中断线程处理，所以要根据实际情况来决定是否采用信号量隔离，尤其涉及网络访问的情况。
				.withExecutionTimeoutInMilliseconds(url.getMethodParameter(method, "timeoutInMilliseconds", 1000))
				//fallback方法的信号量配置，配置getFallback方法并发请求的信号量，如果请求超过了并发信号量限制，则不再尝试调用getFallback方法，而是快速失败，默认信号量为10。
				.withFallbackIsolationSemaphoreMaxConcurrentRequests(url.getMethodParameter(method, "fallbackMaxConcurrentRequests", 50))
				//设置HystrixCommand.run()的隔离策略,
				.withExecutionIsolationStrategy(IsolationStrategy.getIsolationStrategy(url))
				//设置调用线程产生的HystrixCommand.getFallback()方法的允许最大请求数目,如果达到最大并发数目，后续请求将会被拒绝，如果没有实现回退，则抛出异常。
				.withExecutionIsolationSemaphoreMaxConcurrentRequests(url.getMethodParameter(method, "maxConcurrentRequests", 10));

	}
}
