package com.panda.sport.rcs.mq.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.log.annotion.monnitor.MonitorAnnotion;
import com.panda.sport.rcs.log.monitor.MonitorContant;
import com.panda.sport.rcs.log.monitor.ServiceMonitorBean;
import com.panda.sport.rcs.monitor.utils.OsUtis;

import lombok.extern.slf4j.Slf4j;
/**
 * black 
 */
@Slf4j
public abstract class ConsumerAdapter<T> implements Consumer<T>{
	
	private String topic;
	
	private String tag;
	
	private String key;
	
	private Boolean isStart = false;
	
	private String consumerConfig;
	
	private T body;
	
	private AtomicLong count = new AtomicLong(0);
	
	private ProducerSendMessageUtils producerSendMessageUtils;
	
	private String hostName = OsUtis.getHostName();
	
	public ConsumerAdapter() {}
	
	public ConsumerAdapter(String consumerConfig, String keyName) {
		if(StringUtils.isBlank(consumerConfig)) {
			log.warn("当前消费者配置错误，请检查：{}",keyName);
			return;
		}
		
		this.consumerConfig = consumerConfig;
		String[] configs = consumerConfig.split(",");
		if(configs.length == 1) {
			init(configs[0], "", "");
		}else if(configs.length == 2) {
			init(configs[0], configs[1], "");
		}else if(configs.length >= 3) {
			init(configs[0], configs[1], configs[2]);
		}
	}
	
	public void init(String topic, String tag, String key) {
		this.topic = topic;
		this.tag = tag;
		this.key = key;
		this.setStart(true);
	}
	
	public String getConsumerConfig() {
		return this.consumerConfig;
	}
	
	public T getBody() {
		return this.body;
	}

	private T convert(String msg) {
		Type genericSuperclass = this.getClass().getGenericSuperclass();
//	    Class type = (Class) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
		Type type = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
		return (T) JSONObject.parseObject(msg,type);
	}

	@Override
	public Boolean handleMsg(MessageExt ext) {
		MonitorAnnotion monitorAnno = this.getClass().getAnnotation(MonitorAnnotion.class);
		Long startTime = System.currentTimeMillis();
		LogContext context = LogContext.getContext();
		
		String uuid = context.getRequestId();
		byte[] byteBody = ext.getBody();
		Map<String, String> paramsMap = ext.getProperties();
		if(paramsMap.containsKey(MonitorContant.MONITOR_UUID)) {
			uuid = paramsMap.get(MonitorContant.MONITOR_UUID);
		}
		
		context.setRequestId(uuid);
		ServiceMonitorBean monitorBean = null;
		if(monitorAnno != null) {
			try {
				//SEND_SERVICE_GROUP 有这个key的表示我们自己的消息，如果需要添加监控，需要遵从规范，其他上下游过来的消息可以直接支持监控
				if(paramsMap.containsKey(MonitorContant.MONITOR_TAG) 
						||  (!paramsMap.containsKey("SEND_SERVICE_GROUP")) ) {
					Integer isMain = paramsMap.containsKey(MonitorContant.MONITOR_MAIN_DATE) ? 2 :1;
					if(isMain == 1) {
						monitorBean = new ServiceMonitorBean("MQ", uuid, isMain, this.getClass().getName(), this.hostName,DateUtils.parseDate(new Date().getTime(), DateUtils.YYYYMMDD));
					}else {
						monitorBean = new ServiceMonitorBean("MQ", uuid, isMain, this.getClass().getName(), this.hostName);
						monitorBean.setMainDateStr(paramsMap.get(MonitorContant.MONITOR_MAIN_DATE));
					}
					monitorBean.setMonitorCode(monitorAnno.code());
					
					context.setServiceMonitorBean(monitorBean);
				}
				
			}catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
		
		try {
			String body = new String(ext.getBody(), "utf-8");
			ext.setBody(null);
			log.info("收到消息：body:{},ext:{},class:{},uuid:{},count：{}",body,ext,this.getClass(),uuid,count.incrementAndGet());
			
			paramsMap.put("MQ_TOPIC", this.getTopic());
			if(!StringUtils.isBlank(ext.getTags())) paramsMap.put("MQ_TAG", ext.getTags());
			if(!StringUtils.isBlank(ext.getKeys())) paramsMap.put("MQ_KEY", ext.getKeys());
			
			if(StringUtils.isBlank(body)) {
				log.warn("接收参数为空，不做处理:{}",ext);
				return true;
			}

			T obj = convert(body);
			this.body = obj;

			return handleMs(obj, paramsMap);
		}catch (RcsServiceException e) {
			log.warn("{} 消費消息失敗 ,消息：{}",this.getClass(),this.body);
			return false;
		}catch (com.alibaba.fastjson.JSONException e) {
			log.warn("{} 解析消息失敗 ,消息：{}",this.getClass(),this.body);
			return true;
		}catch (Exception e) {
			log.warn("{} 消費消息異常,消息：{}",this.getClass(),this.body);
			return false;
		}finally {
			ext.setBody(byteBody);
			Long exeTime = System.currentTimeMillis() - startTime;
			if(exeTime > 1000l) {
				log.error("消费完成：uuid:{},消费时长：{}，count:{}，class:{}",uuid,exeTime,count.decrementAndGet(),this.getClass());
			}else {
				log.info("消费完成：uuid:{},消费时长：{}，count:{}，class:{}",uuid,exeTime,count.decrementAndGet(),this.getClass());
			}
			
			if(monitorBean != null) {
				monitorBean.setExeTime(exeTime);
//				producerSendMessageUtils.sendMessage("RCS_MONITOR_DATA", monitorBean.getMonitorCode(), uuid, monitorBean);
			}
			
			context = null;
			LogContext.remove();
		}
	}

	public abstract Boolean handleMs(T msg,Map<String, String> paramsMap) throws Exception;
	
	@Override
	public String getKeys() {
		return String.format("topic:%s;tag:%s;key:%s", this.getTopic(),this.getTag(),this.getKey());
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public Boolean isStart() {
		return isStart;
	}
	
	public void setStart(Boolean isStart) {
		this.isStart = isStart;
	}

	public ProducerSendMessageUtils getProducerSendMessageUtils() {
		return producerSendMessageUtils;
	}

	@Override
	public void setProducerSendMessageUtils(ProducerSendMessageUtils producerSendMessageUtils) {
		this.producerSendMessageUtils = producerSendMessageUtils;
	}
	
	
}
