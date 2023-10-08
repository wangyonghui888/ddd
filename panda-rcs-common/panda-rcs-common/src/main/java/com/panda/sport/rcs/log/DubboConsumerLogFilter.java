package com.panda.sport.rcs.log;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.log.monitor.MonitorContant;
import com.panda.sport.rcs.log.monitor.ServiceMonitorBean;
import com.panda.sport.rcs.log.monitor.api.MonitorDataSendApi;
import com.panda.sport.rcs.utils.OsUtis;
import com.panda.sport.rcs.utils.SpringContextUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * dubbo日志过滤器
 *
 */
@Activate(group = CommonConstants.CONSUMER)
@Slf4j
public class DubboConsumerLogFilter implements Filter {
	
	private String hostName = OsUtis.getHostName();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
    	ServiceMonitorBean serviceMonitorBean = null;
    	Map<String, String> attachments = invocation.getAttachments();
    	Long startTime = System.currentTimeMillis();
    	Result result = null;
    	String monitorCode = LogContext.getContext().getMonitorCode();
    	attachments.put(MonitorContant.MONITOR_UUID, LogContext.getContext().getRequestId());
        try {
        	if(!StringUtils.isBlank(monitorCode)) {//需要支持监控
        		serviceMonitorBean = LogContext.getContext().getServiceMonitorBean();
        		if(serviceMonitorBean == null) {
        			String uuid = UUID.randomUUID().toString().replace("-", "");
        			serviceMonitorBean = new ServiceMonitorBean("DUBBO", uuid, 1, invoker.getInterface().getName(), this.hostName,DateUtils.parseDate(new Date().getTime(), DateUtils.YYYYMMDD));
        		}else {
        			ServiceMonitorBean tempServiceMonitorBean = new ServiceMonitorBean("DUBBO", serviceMonitorBean.getUuid(), 2, invoker.getInterface().getName(), this.hostName);
        			tempServiceMonitorBean.setMainDateStr(serviceMonitorBean.getMainDateStr());
        			serviceMonitorBean = tempServiceMonitorBean;
        		}
        		serviceMonitorBean.setMonitorCode(monitorCode);
        		
        		attachments.put(MonitorContant.MONITOR_MAIN_DATE, serviceMonitorBean.getMainDateStr());
        	}
        	
        	result = invoker.invoke(invocation);
        	return result;
        } catch (Exception e) {
        	log.error(e.getMessage(),e);
            throw e;
        } catch (Throwable e) {
        	log.error(e.getMessage(),e);
        	throw e;
        } finally {
        	Long exeTime = System.currentTimeMillis() - startTime;
        	log.info("[dubbo-consumer-info] methodName:{}，attachments：{},exeTime:{}",invocation.getMethodName(),JSONObject.toJSONString(attachments),exeTime);
        	if(serviceMonitorBean != null) {
        		serviceMonitorBean.setExeTime(exeTime);
//        		MonitorDataSendApi monitorDataSendApi = SpringContextUtils.getBean("monitorDataSendApi");
//        		monitorDataSendApi.sendMonitorData("RCS_MONITOR_DATA", serviceMonitorBean.getMonitorCode(), serviceMonitorBean.getUuid(), serviceMonitorBean);
        	}
        }
    }
}
