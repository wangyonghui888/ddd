package com.panda.sport.rcs.zuul.filter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.panda.sport.rcs.zuul.contant.LogContext;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PreRequestLogFilter extends ZuulFilter {

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		try {
			LogContext context = LogContext.getContext();
			
			RequestContext currentContext = RequestContext.getCurrentContext();
			HttpServletRequest request = currentContext.getRequest();
			Map<String, String[]> paramsMap = request.getParameterMap();
			Enumeration<String> headerNames = request.getHeaderNames();
			Map<String, String> headMap = new HashMap<String,  String>();
			while (headerNames.hasMoreElements()) {
				String header = headerNames.nextElement();
				headMap.put(header, request.getHeader(header));
			}
			headMap.put("zuul-request-id", context.getRequestId());
			
			context.setHead(headMap);
			context.setParamsMap(JSONObject.toJSONString(paramsMap));
			context.setPath(request.getRequestURI() + "?" + request.getQueryString());
			context.setClientIp(getIpAddr(request));
			
			initBody(context, request);
			
			log.info("[http-log-info] pre :{}",JSONObject.toJSONString(context));
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
		return null;
	}
	
	public static String getIpAddr(HttpServletRequest request) {  
	    String ip = request.getHeader("x-forwarded-for");  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getHeader("Proxy-Client-IP");  
	    }  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getHeader("WL-Proxy-Client-IP");  
	    } 
	    if(ip == null || ip.length() == 0 || "X-Real-IP".equalsIgnoreCase(ip)) {  
	        ip = request.getHeader("X-Real-IP");  
	    }  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getRemoteAddr();  
	    }  
	    return ip;  
	}
	
	private void initBody(LogContext context, ServletRequest request) {
		if(request instanceof MultiReadHttpServletRequest) {
			context.setBody(((MultiReadHttpServletRequest)request).getBodyString());
		}	
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 0;
	}

}
