package com.panda.sport.rcs.core.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.panda.sport.rcs.core.hystrix.config.SetterFactory;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.core.hystrix
 * @Description :  hystrix过滤器
 * @Date: 2019-10-10 15:58
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Activate(group = "test")
public class HystrixFilter implements Filter {
	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		URL url = invoker.getUrl();
		String methodName = invocation.getMethodName();
		String interfaceName = invoker.getInterface().getName();
		//获取相关熔断配置
		HystrixCommand.Setter setter = SetterFactory.create(interfaceName, methodName, url);
		//获取降级方法
		String fallback = url.getMethodParameter(methodName, "fallback");

		DubboHystrixCommand command = new DubboHystrixCommand(setter, invoker, invocation, fallback);
		Result result = command.execute();
		return result;
	}
}