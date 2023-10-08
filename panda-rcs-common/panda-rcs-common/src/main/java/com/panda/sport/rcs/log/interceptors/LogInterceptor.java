package com.panda.sport.rcs.log.interceptors;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.log.annotion.LogAnnotion;
import com.panda.sport.rcs.log.annotion.format.LogFormatAnnotion;
import com.panda.sport.rcs.log.annotion.monnitor.MonitorAnnotion;
import com.panda.sport.rcs.log.monitor.MonitorContant;
import com.panda.sport.rcs.log.monitor.ServiceMonitorBean;
import com.panda.sport.rcs.log.monitor.api.MonitorDataSendApi;
import com.panda.sport.rcs.log.response.CharResponseWrapper;
import com.panda.sport.rcs.utils.MapUrlParamsUtils;
import com.panda.sport.rcs.utils.OsUtis;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.panda.sport.rcs.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {
	
	private LogMqInteface logMqInteface;
	
	private String hostName = OsUtis.getHostName();
	
	public LogInterceptor(LogMqInteface logMqInteface) {
		this.logMqInteface = logMqInteface;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj)
			throws Exception {
		try {
			if(obj instanceof HandlerMethod) {
				HandlerMethod handle = (HandlerMethod) obj;
				
				MonitorAnnotion monitorAnnotion = handle.getMethodAnnotation(MonitorAnnotion.class);
				if(monitorAnnotion == null ) {
					return true;
				}
				
				LogContext context = LogContext.getContext();
				String clazz = handle.getBeanType().getName();
				String allName = clazz + "." + handle.getMethod().getName();
				ServiceMonitorBean monitorBean = null;
				
				Integer isMain = StringUtils.isBlank(request.getHeader(MonitorContant.MONITOR_UUID)) ? 1 :2;
				if(isMain == 1) {
					monitorBean = new ServiceMonitorBean("HTTP", context.getRequestId(), isMain, allName, this.hostName,DateUtils.parseDate(new Date().getTime(), DateUtils.YYYYMMDD));
				}else {
					monitorBean = new ServiceMonitorBean("HTTP", context.getRequestId(), isMain, allName, this.hostName);
					monitorBean.setMainDateStr(request.getHeader(MonitorContant.MONITOR_MAIN_DATE));
				}
				monitorBean.setMonitorCode(monitorAnnotion.code());
				
				context.setServiceMonitorBean(monitorBean);
			}
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
		return true;
	}

	@Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,Object obj,Exception e)throws Exception {
		try {
			if(obj instanceof HandlerMethod) {
				String url = request.getRequestURI();
				LogBean logBean = new LogBean();
				LogContext context = LogContext.getContext();
				
				HandlerMethod handle = (HandlerMethod) obj;
				
				LogFormatAnnotion logFormatAnnotion = handle.getMethodAnnotation(LogFormatAnnotion.class);
				if(logFormatAnnotion != null && context.getFormatList() !=  null && context.getFormatList().size() > 0 ) {
					logMqInteface.sendMsg("RCS_LOG_FORMAT", request.getHeader("user-id") , request.getHeader("request-id"), context.getFormatList());
				}
				
				Long exeTime = System.currentTimeMillis() - context.getStartTime();
				if(context.getServiceMonitorBean() != null) {//性能接口数据收集
					context.getServiceMonitorBean().setExeTime(exeTime);
//					MonitorDataSendApi monitorDataSendApi = SpringContextUtils.getBean("monitorDataSendApi");
//            		monitorDataSendApi.sendMonitorData("RCS_MONITOR_DATA", context.getServiceMonitorBean().getMonitorCode(), context.getServiceMonitorBean().getUuid(), context.getServiceMonitorBean());
				}
				
				LogAnnotion logAnnotion = handle.getMethodAnnotation(LogAnnotion.class);
				if(logAnnotion == null ) return;
				
				String code = logAnnotion.code();
				if(StringUtils.isBlank(code)) {
					code = url;
				}
				
				logBean.setCode(code);
				logBean.setUrl(url);
				logBean.setName(logAnnotion.name());
				logBean.setIp(getIp(request));
				String body = context.getBody();
				if(StringUtils.isBlank(body)) body = "{}";
				
				JSONObject bodyJson = JSONObject.parseObject(body);
				Map<String, String> queryMap = MapUrlParamsUtils.getUrlParams(request.getQueryString());
				bodyJson.putAll(queryMap);
				
				if(context.getLogMap() != null && context.getLogMap().size() > 0 ) {
					bodyJson.putAll(context.getLogMap());
				}
				
				logBean.setRequestVal(bodyJson.toJSONString());
				
				if(!StringUtils.isBlank(logAnnotion.urlTypeVal())) {
					String urlTypeVal = logAnnotion.urlTypeVal();
					
					if(bodyJson.containsKey(urlTypeVal)) {
						logBean.setUrlTypeVal(bodyJson.getString(urlTypeVal));
					}
					
					logBean.setUrlType(logAnnotion.urlType());
				}
				
				logBean.setExeTime(exeTime);
				logBean.setUserId(request.getHeader("user-id"));
				logBean.setUuid(request.getHeader("request-id"));
				parseValues(logBean,logAnnotion,code);
				
				if(response instanceof CharResponseWrapper) {
					CharResponseWrapper crw = (CharResponseWrapper) response;
					logBean.setReturnVal(crw.getContent());
				}
				
				logMqInteface.sendMsg("RCS_HTTP_LOG", code, logBean.getUserId(), logBean);
			}
		}catch (Exception ex) {
			log.error(ex.getMessage(),ex);
		}
    }
	
	private String getIp(HttpServletRequest request) {
		String ip = request.getHeader("x-original-forwarded-for");
		if(org.apache.commons.lang3.StringUtils.isBlank(ip)) {
			ip = request.getHeader("x-real-ip");
		}
		if(StringUtils.isBlank(ip)) {
			ip = request.getHeader("x-forwarded-for");
		}
		
		if(!StringUtils.isBlank(ip)) {
			ip = ip.split(",")[0].trim();
		}
		
		return ip;
	}
	
	private void parseValues(LogBean logBean,LogAnnotion logAnnotion,String code) {
		try {
			String[] keys = logAnnotion.keys();
			if(keys == null || keys.length <= 0) {
				return;
			}
			Map<String, Object> values = new LinkedHashMap<String, Object>();
			Map<String, Object> title = new LinkedHashMap<String, Object>();
			
			String requestVal = logBean.getRequestVal();
			JSONObject jsonVal = JSONObject.parseObject(requestVal);
			for(int i = 0 ; i < Math.min(logAnnotion.keys().length, logAnnotion.title().length) ; i ++) {
				String key = logAnnotion.keys()[i];
				values.put(key, jsonVal.get(key));
				title.put(key, logAnnotion.title()[i]);
			}
			logBean.setValues(JSONObject.toJSONString(values));
			logBean.setTitle(JSONObject.toJSONString(title));
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	public static void main(String[] args) {
		String ip = "103.82.19.10,18.162.143.225, 172.18.178.114, 172.18.178.160";
		System.out.println(ip.split(",")[3].trim());
	}
	
}
