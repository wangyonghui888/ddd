package com.panda.sport.rcs.mq.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.monitor.utils.OsUtis;
import com.panda.sport.rcs.mq.bean.RocketProperties;
import com.panda.sport.rcs.mq.utils.Consumer;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.utils.SpringContextUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsumerMessageListener implements MessageListenerConcurrently{
	
	RocketProperties rocketProperties;

    private ProducerSendMessageUtils producerSendMessageUtils;
    
    @SuppressWarnings("rawtypes")
	private Map<String, Consumer> consumerMap = new HashMap<>();
    
	private Map<String, List<Consumer>> consumerList = new HashMap<>();
	
	private Integer retryTime;
	
	Environment env;
	
	boolean isSuccess = false;
	
	private List<String> allTopic = new ArrayList<String>();
	
	private String CONSUMER_RETRY = "CONSUMER_RETRY";
	
	public ConsumerMessageListener(RocketProperties rocketProperties) {
		if(rocketProperties == null ) {
			log.warn("配置属性为空，不做监听处理");
			return;
		}
		this.rocketProperties = rocketProperties;
		this.producerSendMessageUtils = SpringContextUtils.getBean("producerSendMessageUtils");
		this.env = SpringContextUtils.getBean("environment");
		if(env.getProperty("rocketmq.consumer.retry.timer") == null) retryTime = 3;
		else this.retryTime = Integer.parseInt(env.getProperty("rocketmq.consumer.retry.timer"));
		
		Collection<Consumer> list = SpringContextUtils.getBean(Consumer.class);
		log.info("rocket mq consumer Consumer:{}",JSONObject.toJSONString(list));
		if(list == null) {
			log.warn("rocket mq consumer 没有扫描到消息处理类");
			return;
		}
		for(Consumer cons : list) {
    		if(cons.isStart()) {
    			consumerMap.put(cons.getKeys(), cons);
    			
    			String consumerConfig = cons.getConsumerConfig();
    			String consumerTopic = consumerConfig.split(",")[0];
    			allTopic.add(consumerTopic);
    			
    			cons.setProducerSendMessageUtils(this.producerSendMessageUtils);
    			log.info("订阅消费配置：keys:{},class:{}",cons.getKeys(),cons.getClass());
    		}
    	}
		
		if(consumerMap.size() <= 0 || allTopic.size() <= 0) {
			log.warn("消费中心消费者为空，不启动rocketMq消费");
			return;
		}
		isSuccess = true;
		
		OsUtis.setMqGroupName(rocketProperties.getGroup());
        log.info("consumer register success :{}",JSONObject.toJSONString(consumerMap));
	}
	
	public List<String> getAllTopic(){
		return this.allTopic;
	}
	
	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getKeys(MessageExt ext) {
		String topic = ext.getTopic() == null ? "" : ext.getTopic();
		String tag = ext.getTags() == null ? "" : ext.getTags();
		String key = ext.getKeys() == null ? "" : ext.getKeys();
		return String.format("topic:%s;tag:%s;key:%s", topic,tag,key);
	}
	
	private Boolean validate(String val ,String msgVal) {
		if(val.split(":").length == 1) {
			return true;
		}else if(val.split(":").length >= 2) {
			if(Pattern.matches(msgVal, val.split(":")[1])) {
				return true;
			}
		}
		
		return false;
	}
	
	private List<Consumer> getPatternConsumerList(MessageExt ext){
		List<Consumer> list = new ArrayList<>();
		String topic = ext.getTopic() == null ? "" : ext.getTopic();
		String tag = ext.getTags() == null ? "" : ext.getTags();
		String key = ext.getKeys() == null ? "" : ext.getKeys();
		
		if(StringUtils.isBlank(topic)) return list;
		
//		String cacheKey = getKeys(ext);
//		if(consumerList.containsKey(cacheKey)) {
//			return consumerList.get(cacheKey);
//		}
		
		for(String consumerKey : consumerMap.keySet()) {
			String[] infoKeys = consumerKey.split(";");
			if(infoKeys[0].split(":")[1].equals(topic)) {
				if(infoKeys.length == 1) {
					list.add(consumerMap.get(consumerKey));
				}else if(infoKeys.length == 2) {
					if(validate(infoKeys[1], tag))  list.add(consumerMap.get(consumerKey));
				}else if(infoKeys.length == 3) {
					if(validate(infoKeys[1], tag) && validate(infoKeys[2], key))
						list.add(consumerMap.get(consumerKey));
				}
			}
		}
		
		Collections.sort(list, new Comparator<Consumer>() {
			@Override
			public int compare(Consumer c1, Consumer c2) {
				Order o1 = c1.getClass().getAnnotation(Order.class);
				Order o2 = c2.getClass().getAnnotation(Order.class);
				if(o1 != null && o2 != null) {
					return o1.value() - o2.value();
				}else if(o1 == null && o2 == null) {
					return 0;
				}else if(o1 != null && o2 == null) {
					return o1.value();
				}else if(o1 == null && o2 != null) {
					return o2.value() * -1;
				}
				return 0;
			}
		});
		
//		consumerList.put(cacheKey, list);
		
		return list;
	}

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
          for (MessageExt msg : msgs) {
        	  List<Consumer> handleList = getPatternConsumerList(msg);
        	  if(handleList == null || handleList.size() <= 0 ) {
        		  log.error("当前消息没有消费者处理，请检查：{}",msg);
        		  return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        	  }
        	  
        	  for(Consumer consumer : handleList) {
                  Boolean result = consumer.handleMsg(msg);
                  if(result== null ) {
                	  log.warn("消息处理返回为null，请确认是否需要重复消费，默认null不重复消费，class:{}",consumer.getClass());
                	  continue;
                  }
                  if(!result) {//结果处理失败 重新发送
                	  Map<String, String> params = msg.getProperties();
                	  String retry = msg.getProperty(CONSUMER_RETRY);
                	  if(!StringUtils.isBlank(retry)) {
                		  params.put(CONSUMER_RETRY, String.valueOf(Integer.parseInt(retry) + 1));
                	  }else {
                		  params.put(CONSUMER_RETRY, "1");
                	  }
                	  if(Integer.parseInt(params.get(CONSUMER_RETRY)) <= retryTime) {
                		  producerSendMessageUtils.sendMessage(consumer.getConsumerConfig(), consumer.getBody(),params);
                	  }
                  }
        	  }
          }
          return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}
	

}
