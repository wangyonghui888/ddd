package com.panda.sport.rcs.zuul.filter;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.panda.sport.rcs.zuul.contant.LogContext;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ErrorRequestLogFilter extends ZuulFilter {

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		LogContext context = LogContext.getContext();
		try {
			context.setEndTime(System.currentTimeMillis());
			
			RequestContext reqContext = RequestContext.getCurrentContext();
	        Throwable throwable = reqContext.getThrowable();
	        log.error("[http-log-info] error context:{} error :{}",JSONObject.toJSONString(context),throwable.getCause().getMessage());
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			context.remove();
		}
		
		return null;
	}

	@Override
	public String filterType() {
		return "error";
	}

	@Override
	public int filterOrder() {
		return 0;
	}

}
