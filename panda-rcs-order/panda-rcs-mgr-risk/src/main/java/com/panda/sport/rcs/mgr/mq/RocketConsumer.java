package com.panda.sport.rcs.mgr.mq;

import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.mgr.mq.config.RocketRpcProperties;
import com.panda.sport.rcs.mq.listener.ConsumerMessageListener;
import com.panda.sport.rcs.utils.SpringContextUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * black 
 * 消费消息
 */
@Component
@Slf4j
public class RocketConsumer {
	
	@Autowired
	private RocketRpcProperties rocketRpcProperties;

    @Autowired
    DefaultMQPushConsumer consumer;
    
    @Autowired
    private SpringContextUtils springContextUtils;

    @PostConstruct
	public void init() throws Exception{
		try{
			reSetConfig();
			ConsumerMessageListener listener = new ConsumerMessageListener(rocketRpcProperties);
			if(!listener.isSuccess()) {
				log.warn("rocket mq 消费中心配置异常，不启动rocketMq消费");
				return;
			}
			for(String topic : listener.getAllTopic()) {
				log.info("rocket mq 消费者订阅topic:{}",topic);
				consumer.subscribe(topic, "*");
			}
			
	        consumer.registerMessageListener(listener);
	        consumer.start();
	        log.info("rocket mq start finish!");
		}catch (Exception e) {
			log.error("rocket mq 消费中心启动异常：{}",e.getMessage(),e);
		}finally {
			LogContext.remove();
		}
	}
    
	private void reSetConfig() {
		consumer.setConsumerGroup(rocketRpcProperties.getGroup());
        consumer.setInstanceName(rocketRpcProperties.getInstance());
        consumer.setNamesrvAddr(rocketRpcProperties.getNameSrvAddr());
	}
	
	public static void main(String[] args) {
		System.out.println(Pattern.matches("abc|aaa", "aaa"));
	}
	
}
