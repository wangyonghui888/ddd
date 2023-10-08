package com.panda.sport.rcs.log;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.log.monitor.MonitorContant;
import com.panda.sport.rcs.log.response.CharResponseWrapper;
import com.panda.sport.rcs.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LogFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		LogContext context = null;
		String content = null;
		try {
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			
			context = LogContext.getContext();
			String uuid = httpRequest.getHeader(MonitorContant.MONITOR_UUID);
			if(!StringUtils.isBlank(uuid)) {
				context.setRequestId(uuid);
			}
			
			context.setClean(true);
			context.setPath(httpRequest.getRequestURI() + "?" + httpRequest.getQueryString());
			initHead(context,httpRequest);
			context.setParamsMap(JSONObject.toJSONString(request.getParameterMap()));
			initBody(context,request);
			context.setClientIp(getIpAddr(httpRequest));
			
 			if(!StringUtils.isBlank(httpRequest.getRequestURI()) 
 					&& httpRequest.getRequestURI().contains("down")) {
 				log.info("文件下载：{}",httpRequest.getQueryString());
 				chain.doFilter(request, response);
			}else {
				response.setContentType("application/json;charset=UTF-8");
				
				CharResponseWrapper crw = new CharResponseWrapper((HttpServletResponse)response);
				chain.doFilter(request, crw);
				content = crw.getContent();//response流的内容  
				context.setResult(content);
				response.getWriter().write(content);
			}
		}catch (Exception e) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("code", "200");
			map.put("sucess", false);
			map.put("fail", true);
			map.put("msg", "系统失败");
			PrintWriter pw = response.getWriter();
			pw.println(JSONObject.toJSONString(map));
			pw.flush();
			pw.close();
			log.error("requestId:"+ context.getRequestId() + e.getMessage(),e);
		}finally {
			context.setEndTime(System.currentTimeMillis());
			if(context.getEndTime() - context.getStartTime() > 2000) {
				log.error("[http-log-info], time out  ," + context.getLogStr());
			}else {
				log.info("[http-log-info]," + context.getLogStr());
			}
			
			context.remove();
		}
		
	}
	
	public static String getIpAddr(HttpServletRequest request) {  
		String ip = null;
		//X-Forwarded-For：Squid 服务代理
		String ipAddresses = request.getHeader("X-Forwarded-For");
		if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			//Proxy-Client-IP：apache 服务代理
			ipAddresses = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			//WL-Proxy-Client-IP：weblogic 服务代理
			ipAddresses = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			//HTTP_CLIENT_IP：有些代理服务器
			ipAddresses = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			//X-Real-IP：nginx服务代理
			ipAddresses = request.getHeader("X-Real-IP");
		}

		//有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
		if (ipAddresses != null && ipAddresses.length() != 0) {
			ip = ipAddresses.split(",")[0];
		}

		//还是不能获取到，最后再通过request.getRemoteAddr();获取
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			ip = request.getRemoteAddr();
		}
		return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	}
	
	private void initBody(LogContext context, ServletRequest request) {
		if(request instanceof MultiReadHttpServletRequest) {
			context.setBody(((MultiReadHttpServletRequest)request).getBodyString());
		}	
	}

	private void initHead(LogContext context ,HttpServletRequest httpRequest) {
		Enumeration<String> names = httpRequest.getHeaderNames();
		Map<String, String > head = new HashMap<String, String>();
		while(names.hasMoreElements()){
			String name = names.nextElement();
			String value = httpRequest.getHeader(name);
			head.put(name, value);
		}
		context.setHead(head);
	}


}
