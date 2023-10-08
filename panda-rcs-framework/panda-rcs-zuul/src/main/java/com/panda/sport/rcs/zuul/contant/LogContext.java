package com.panda.sport.rcs.zuul.contant;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.slf4j.MDC;
import org.slf4j.spi.MDCAdapter;

import com.alibaba.fastjson.JSONObject;

import ch.qos.logback.classic.util.LogbackMDCAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogContext {
	
	private String requestId ; 
	
	private static String ip;
	
	private String clientIp;
	
	private String path;
	
	private Map<String, String> head;
	
	private String paramsMap;
	
	private String body;
	
	private String result;
	
	private Long startTime;
	
	private Long endTime;
	
	private int index = 0 ;
	
	private boolean isClean;
	
	private boolean currnetClean;
	
	private int cleanIndex;
	
	private Map<Integer, Map<String, Object>> methodsInfo = new HashMap<Integer, Map<String,Object>>();
	
	private static ThreadLocal<LogContext> INSTANCE = new ThreadLocal<LogContext>() {

		@Override
		protected LogContext initialValue() {
			LogContext context = new LogContext();
			MDCAdapter mdc = MDC.getMDCAdapter();
			Map<String, String > mdcPropertyMap = null;
            if (mdc instanceof LogbackMDCAdapter)
                mdcPropertyMap = ((LogbackMDCAdapter) mdc).getPropertyMap();
            
            if(mdcPropertyMap == null) {
            	if(ip == null) initIp();
            	context.setRequestId(ip + "-" + UUID.randomUUID().toString().replace("-", ""));
            }else {
            	context.setRequestId(mdcPropertyMap.get("traceId") );
            }
            
            context.setStartTime(System.currentTimeMillis());
            context.setEndTime(context.getStartTime());
            
			return context;
		}

	};
	
	private static void initIp() {
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.error(e.getMessage(),e);
		}
	}

	public String getLogStr() {
		String logStr = String.format("requestId:%s,path:%s,clientIp:%s,head:%s,paramsMap:%s,body:%s,result:%s,exeTime:%s", 
				this.getRequestId(),this.getPath(),this.getClientIp(),
				JSONObject.toJSONString(this.getHead()),
				this.getParamsMap(),this.getBody(),this.getResult(),
				this.getEndTime() - this.getStartTime());
		return logStr;
	}
	
	public int getCleanIndex() {
		return cleanIndex;
	}

	public boolean isCurrnetClean() {
		return currnetClean;
	}

	public void setCurrnetClean(boolean currnetClean) {
		this.currnetClean = currnetClean;
	}

	public void setCleanIndex(int cleanIndex) {
		this.cleanIndex = cleanIndex;
	}

	public static LogContext getContext() {
		return INSTANCE.get();
	}
	
	public static void remove() {
		INSTANCE.remove();
	}
	
	public static String getIp() {
		return ip;
	}

	public boolean isClean() {
		return isClean;
	}

	public void setClean(boolean isClean) {
		this.isClean = isClean;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public static void setIp(String ip) {
		LogContext.ip = ip;
	}
	
	public int addIndex() {
		INSTANCE.get().setIndex(INSTANCE.get().getIndex() + 1);
		return  INSTANCE.get().getIndex();
	}
	
	public int subIndex() {
		int temp = INSTANCE.get().getIndex();
		INSTANCE.get().setIndex(temp - 1);
		return  temp;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Map<String, String> getHead() {
		return head;
	}

	public void setHead(Map<String, String> head) {
		this.head = head;
	}

	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Map<Integer, Map<String, Object>> getMethodsInfo() {
		return methodsInfo;
	}

	public void setMethodsInfo(Map<Integer, Map<String, Object>> methodsInfo) {
		this.methodsInfo = methodsInfo;
	}

	public String getParamsMap() {
		return paramsMap;
	}

	public void setParamsMap(String paramsMap) {
		this.paramsMap = paramsMap;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getRequestId() {
		return requestId;
	}

	public Long getStartTime() {
		return startTime;
	}


	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}


	public Long getEndTime() {
		return endTime;
	}


	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}


	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/*public static void main(String[] args)
	{
		Long id = 1191415105644920833L;
		Multimap<Long,Object> map =  ArrayListMultimap.create();
		map.put(id, Lists.newArrayListWithCapacity(2));
		List list =(List) map.get(id);
		System.out.println(Long.MAX_VALUE);
		System.out.println(LogContext.getContext().getRequestId());
	}*/
}
