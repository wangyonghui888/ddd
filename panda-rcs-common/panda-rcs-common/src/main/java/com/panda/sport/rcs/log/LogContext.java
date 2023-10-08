package com.panda.sport.rcs.log;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.MDC;
import org.slf4j.spi.MDCAdapter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.log.annotion.format.LogFormatAnnotion;
import com.panda.sport.rcs.log.format.LogFormatBean;
import com.panda.sport.rcs.log.format.LogFormatDynamicBean;
import com.panda.sport.rcs.log.format.LogFormatPublicBean;
import com.panda.sport.rcs.log.monitor.ServiceMonitorBean;
import com.panda.sport.rcs.utils.StringUtils;

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
	
	/**
	 * 消费者使用当前字段，其余模式这个字段无效，不需要入库
	 */
	private String monitorCode;
	
	private ServiceMonitorBean serviceMonitorBean;
	
	private List<Map<String, Object>> formatList = new ArrayList<Map<String, Object>>();
	
	private Map<String, String> logMap = new HashMap<String, String>();
	
	private Map<Integer, Map<String, Object>> methodsInfo = new HashMap<Integer, Map<String,Object>>();
	
	private static ThreadLocal<LogContext> INSTANCE = new ThreadLocal<LogContext>() {

		@Override
		protected LogContext initialValue() {
			LogContext context = new LogContext();
//			MDCAdapter mdc = MDC.getMDCAdapter();
//			Map<String, String > mdcPropertyMap = null;
//            if (mdc instanceof LogbackMDCAdapter)
//                mdcPropertyMap = ((LogbackMDCAdapter) mdc).getPropertyMap();
            
			if(ip == null) initIp();
			
            context.setRequestId(UUID.randomUUID().toString().replace("-", ""));
            context.setStartTime(System.currentTimeMillis());
            
			return context;
		}

	};
	
	public void addFormatBean(LogFormatPublicBean publicBean ,Object dynamicBean  , Object oldVal , Object newVal) {
		try {
			if(newVal == null ) {
				return ;
			}
			
			LogFormatAnnotion logformatAnno = newVal.getClass().getAnnotation(LogFormatAnnotion.class);
			if(logformatAnno == null) return;
			
			Map<String, Object> oldMap = new HashMap<String, Object>();
			if(oldVal != null ) {
				oldMap = JSONObject.parseObject(JSONObject.toJSONString(oldVal),new TypeReference<Map<String,Object>>(){});
			}
			
			Field[] fields = newVal.getClass().getDeclaredFields();
			for(Field field : fields) {
				LogFormatAnnotion fieldAnno = field.getAnnotation(LogFormatAnnotion.class);
				if(fieldAnno == null) {
					continue;
				}
				
				field.setAccessible(true);
				String fileName = field.getName();
				
				String name = fieldAnno.name();
				String format = fieldAnno.format();
				
				if(StringUtils.isBlank(format)) format = "%s";
				
				String oldFieldVal = String.format(format, oldMap.get(fileName));
				Object newFieldObj = field.get(newVal);
				if(newFieldObj == null && fieldAnno.isIgnoreBlank()) {//忽略空值
					continue;
				}
				
				String newFieldVal = field.get(newVal) == null ? "" : String.valueOf(field.get(newVal)); 
				
				if(oldMap.containsKey(fileName) && !oldFieldVal.equals(newFieldVal) ) {
					addFormatBean(publicBean, dynamicBean, new LogFormatBean(name, oldFieldVal, newFieldVal, format));
				}
			}
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	
	public void addFormatBean(LogFormatPublicBean publicBean ,Object dynamicBean  , LogFormatBean beanList ) {
		addFormatBean(publicBean, dynamicBean, Arrays.asList(beanList));
	}
	
	public void addFormatBean(LogFormatPublicBean publicBean ,Object dynamicBean  , List<LogFormatBean> beanList ) {
		if(publicBean == null || beanList == null || beanList.size() <= 0) return;
		
		Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(publicBean) , new TypeReference<Map<String, Object>>(){});
		Map<String, Object> dynamicMap = LogFormatDynamicBean.parseAnno(dynamicBean);
		map.put("dynamicBean", dynamicMap);
		
		for(LogFormatBean formatBean : beanList) {
			if(!StringUtils.isBlank(formatBean.getFormat())) {
				formatBean.setOldVal(String.format(formatBean.getFormat(), formatBean.getOldVal()));
				formatBean.setNewVal(String.format(formatBean.getFormat(), formatBean.getNewVal()));
			}
			
			Map<String, Object> temMap = JSONObject.parseObject(JSONObject.toJSONString(map) , new TypeReference<Map<String, Object>>(){});
			temMap.putAll(JSONObject.parseObject(JSONObject.toJSONString(formatBean) , new TypeReference<Map<String, Object>>(){}));
			formatList.add(temMap);
		}
		
	}
	
	private static void initIp() {
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.error(e.getMessage(),e);
		}
	}

	
	public ServiceMonitorBean getServiceMonitorBean() {
		return serviceMonitorBean;
	}

	public void setServiceMonitorBean(ServiceMonitorBean serviceMonitorBean) {
		this.serviceMonitorBean = serviceMonitorBean;
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
	
	public String getMonitorCode() {
		return monitorCode;
	}

	public void setMonitorCode(String monitorCode) {
		this.monitorCode = monitorCode;
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

	public List<Map<String, Object>> getFormatList() {
		return formatList;
	}

	public void setFormatList(List<Map<String, Object>> formatList) {
		this.formatList = formatList;
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
	
	public void putLogMap(String key , String val) {
		if ( this.logMap == null ) {
			this.logMap = new HashMap<String, String>();
		}
		
		this.logMap.put(key, val);
	}
	
	public Map<String, String> getLogMap() {
		return logMap;
	}

	public void setLogMap(Map<String, String> logMap) {
		this.logMap = logMap;
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
		try {
			MDC.put("reqId", requestId);
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
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
