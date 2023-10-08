package com.panda.sport.rcs.log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.config.invoker.DelegateProviderMetaDataInvoker;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.proxy.AbstractProxyInvoker;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.log.annotion.NotWriteLog;
import com.panda.sport.rcs.log.annotion.monnitor.MonitorAnnotion;
import com.panda.sport.rcs.log.monitor.MonitorContant;
import com.panda.sport.rcs.log.monitor.ServiceMonitorBean;
import com.panda.sport.rcs.log.monitor.api.MonitorDataSendApi;
import com.panda.sport.rcs.utils.LogUUidParseUtils;
import com.panda.sport.rcs.utils.OsUtis;
import com.panda.sport.rcs.utils.SpringContextUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * dubbo日志过滤器
 *
 */
@Activate(group = CommonConstants.PROVIDER , order = Integer.MAX_VALUE)
@Slf4j
public class DubboLogFilter implements Filter {
	
	ConcurrentHashMap<String, String> apiIsAnnoMap = new ConcurrentHashMap<String, String>();
	
	private String hostName = OsUtis.getHostName();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
    	LogContext context = null;
    	
        long start = System.currentTimeMillis();
        
        ServiceMonitorBean monitorBean = null;
        try {
        	Object[] args = invocation.getArguments();
        	Map<String, String> attachments = invocation.getAttachments();
        	String clientIp = RpcContext.getContext().getRemoteHost();
        	
        	context = LogContext.getContext();
        	String uuid = LogUUidParseUtils.getUuids(args,context.getRequestId());
        	if(attachments.containsKey(MonitorContant.MONITOR_UUID)) {
        		uuid = attachments.get(MonitorContant.MONITOR_UUID);
        	}
        	context.setRequestId(uuid);
        	
        	context.setClean(true);
        	context.setPath(invocation.getMethodName());
        	context.setClientIp(clientIp);
        	context.setHead(attachments);
        	context.setParamsMap(JSONObject.toJSONString(args));
        	monitorBean = parsetLogData(invoker,invocation,args);
        	
        	context.setServiceMonitorBean(monitorBean);
        	Result result = invoker.invoke(invocation);
        	context.setResult(JSONObject.toJSONString(result.getValue()));
        	return result;
        } catch (Exception e) {
        	log.error("requestId:"+ context.getRequestId() + e.getMessage(),e);
            throw e;
        } catch (Throwable e) {
        	log.error("requestId:"+ context.getRequestId() + e.getMessage(),e);
        	throw e;
        } finally {
        	context.setEndTime(System.currentTimeMillis());
        	if(context.getEndTime() - context.getStartTime() > 2000) {
        		/*log.error("[dubbo-log-info] time out ,requestId:{},method:{},clientIp:{},head:{},paramsMap:{},result:{},exeTime:{}" ,
						context.getRequestId(),context.getPath(),context.getClientIp(),context.getHead(),context.getParamsMap(),context.getResult(),
						context.getEndTime() - start);*/
			}
        	
        	try {
        		Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
        		NotWriteLog notWriteLog = method.getAnnotation(NotWriteLog.class);
            	if(notWriteLog == null) {
            		/*log.info("[dubbo-log-info],requestId:{},method:{},clientIp:{},head:{},paramsMap:{},result:{},exeTime:{}" ,
    						context.getRequestId(),context.getPath(),context.getClientIp(),context.getHead(),context.getParamsMap(),context.getResult(),
    						context.getEndTime() - start);*/
            	}
            	
            	if(monitorBean != null){
            		monitorBean.setExeTime(context.getEndTime() - start);
//            		MonitorDataSendApi monitorDataSendApi = SpringContextUtils.getBean("monitorDataSendApi");
//            		monitorDataSendApi.sendMonitorData("RCS_MONITOR_DATA", monitorBean.getMonitorCode(), monitorBean.getUuid(), monitorBean);
            	}
        	}catch (Exception e) {
        		//log.error(e.getMessage(),e);
        		/*log.info("[dubbo-log-info],requestId:{},method:{},clientIp:{},head:{},paramsMap:{},result:{},exeTime:{}" ,
						context.getRequestId(),context.getPath(),context.getClientIp(),context.getHead(),context.getParamsMap(),context.getResult(),
						context.getEndTime() - start);*/
        	}
        	
        	LogContext.remove();
        }
    }

	private ServiceMonitorBean parsetLogData(Invoker<?> invoker, Invocation invocation,Object[] args) {
		try {
			Map<String, String> attachments = invocation.getAttachments();
			if(attachments == null ) attachments = new HashMap<String, String>();
			
			if(!attachments.containsKey("path")) {
				log.warn("dubbo没有获取到路径：{}",attachments);
				return null;
			}
			
			MonitorAnnotion anno = null;
			String key = String.format("%s_%s", attachments.get("path").replaceAll("\\.", "_"),invocation.getMethodName());
			if(!apiIsAnnoMap.containsKey(key)) {
				Class clazz = getTargetClass(invoker);
				if(clazz == null ) return null;
				
				if(clazz.getSimpleName().contains("$$")) {
					try {
						String clazzName = clazz.getName();
						String srcClass = clazzName.substring(0, clazzName.indexOf("$$"));
						clazz = Class.forName(srcClass);
					}catch (Exception e) {
						log.error(e.getMessage(),e);
					}
				}
				
				Method method = clazz.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
				anno = method.getAnnotation(MonitorAnnotion.class);
				
				if(anno == null ) {
					apiIsAnnoMap.put(key, "");
				}else {
					apiIsAnnoMap.put(key, anno.code());
				}
			}
			
			String code = apiIsAnnoMap.get(key);
			if(StringUtils.isBlank(code)) {
				log.warn("当前没有注解，不需要监控 ：{}",attachments);
				return null;
			}
			
			ServiceMonitorBean monitorBean = null;
			String uuid = LogContext.getContext().getRequestId();
			Integer isMain = attachments.containsKey(MonitorContant.MONITOR_UUID) ? 2 : 1;
			if(isMain == 1) {
				monitorBean = new ServiceMonitorBean("DUBBO", uuid, isMain, invoker.getInterface().getName(), this.hostName,DateUtils.parseDate(new Date().getTime(), DateUtils.YYYYMMDD));
			}else {
				monitorBean = new ServiceMonitorBean("DUBBO", uuid, isMain, invoker.getInterface().getName(), this.hostName);
				monitorBean.setMainDateStr(attachments.get(MonitorContant.MONITOR_MAIN_DATE));
			}
			monitorBean.setMonitorCode(code);
			
			return monitorBean;
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
		return null;
	}
	
    private Class getTargetClass(Object invoker) { 
    	try {
    		  Field invokerField = invoker.getClass().getDeclaredField("invoker");
    	  	  invokerField.setAccessible(true);
    	  	  Object obj = invokerField.get(invoker) ;
    	  	  if(obj instanceof DelegateProviderMetaDataInvoker) {
    	  		  DelegateProviderMetaDataInvoker delegateProviderMetaDataInvoker = (DelegateProviderMetaDataInvoker) obj;
    	  		  invokerField = delegateProviderMetaDataInvoker.getClass().getDeclaredField("invoker");
    	      	  invokerField.setAccessible(true);
    	      	  
    	      	  obj = invokerField.get(delegateProviderMetaDataInvoker) ;
    	  	  }
    	  	  
    	  	  if(!(obj instanceof AbstractProxyInvoker)) {
    	  		  return null;
    	  	  }
    	  	  
    	  	  // 此时javassistProxyFactoryInvoker是JavassistProxyFactory内部方法getInvoker返回的AbstractProxyInvoker匿名实现类
    	  	  AbstractProxyInvoker javassistProxyFactoryInvoker = (AbstractProxyInvoker)obj;
    	  	  
    	  	  /**
    	  	  ** javassistProxyFactoryInvoker.getClass() 是AbstractProxyInvoker匿名实现类的运行实例
    	  	  ** javassistProxyFactoryInvoker.getClass().getSuperclass() 是 AbstractProxyInvoker 实例
    	  	  */
    	  	  Field proxy = javassistProxyFactoryInvoker.getClass().getSuperclass().getDeclaredField("proxy");
    	  	  proxy.setAccessible(true);
    	  	  // proxyValue即是实现类(如BActServiceImpl实例)
    	  	  Object proxyValue = proxy.get(javassistProxyFactoryInvoker);
    	  	  
    	  	  
    	  	  
    	  	  return proxyValue.getClass();
    	}catch (Exception e) {
    		log.error(e.getMessage(),e);
    	}
    	
    	return null;
  	}
}
