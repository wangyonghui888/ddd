package com.panda.sport.rcs.task.mq;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.mq.listener.ConsumerMessageListener;
import com.panda.sport.rcs.task.mq.config.RocketTaskProperties;
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
	private RocketTaskProperties rocketTaskProperties;
    
    @Autowired
    DefaultMQPushConsumer consumer;
    
    @Autowired
    private SpringContextUtils springContextUtils;
    
    @PostConstruct
	public void init() throws Exception{
		try{
			reSetConfig();
			ConsumerMessageListener listener = new ConsumerMessageListener(rocketTaskProperties);
			if(!listener.isSuccess()) {
				log.warn("消费中心配置异常，不启动rocketMq消费");
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
			log.error(e.getMessage(),e);
		}finally {
			LogContext.remove();
		}
	}

	private void reSetConfig() {
		consumer.setConsumerGroup(rocketTaskProperties.getGroup());
        consumer.setInstanceName(rocketTaskProperties.getInstance());
        consumer.setNamesrvAddr(rocketTaskProperties.getNameSrvAddr());
	}
    
    
}
