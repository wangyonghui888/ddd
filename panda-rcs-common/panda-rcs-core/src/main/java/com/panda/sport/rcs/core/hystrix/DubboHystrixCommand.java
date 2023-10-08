package com.panda.sport.rcs.core.hystrix;

import com.alibaba.druid.util.StringUtils;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.core.hystrix
 * @Description : Dubbo命令
 * @Date: 2019-10-10 16:00
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class DubboHystrixCommand extends HystrixCommand<AsyncRpcResult> {

	private static Logger logger = LoggerFactory.getLogger(DubboHystrixCommand.class);

	private Invoker<?> invoker;
	private Invocation invocation;
	private String fallbackName;

	public DubboHystrixCommand(Setter setter, Invoker<?> invoker, Invocation invocation, String fallbackName) {
		super(setter);
		this.invoker = invoker;
		this.invocation = invocation;
		this.fallbackName = fallbackName;
	}
	@Override
	protected AsyncRpcResult run() throws Exception {
		AsyncRpcResult result =AsyncRpcResult.newDefaultAsyncResult(invocation);
		//如果远程调用异常，抛出异常执行降级逻辑
		if (result.hasException()) {
			throw new HystrixRuntimeException(HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, DubboHystrixCommand.class, result.getException().getMessage(), result.getException(), null);
		}

		return result;
	}

	@Override
	protected AsyncRpcResult getFallback() {

		if (StringUtils.isEmpty(fallbackName)) {
			//抛出原本的异常
			return super.getFallback();
		}
		try {
			//基于SPI扩展加载fallback实现
			ExtensionLoader<Fallback> loader = ExtensionLoader.getExtensionLoader(Fallback.class);
			Fallback fallback = loader.getExtension(fallbackName);
			Object value = fallback.invoke();
			return AsyncRpcResult.newDefaultAsyncResult(value,invocation);
		} catch (RuntimeException ex) {
			logger.error("fallback failed", ex);
			throw ex;
		}
	}
}