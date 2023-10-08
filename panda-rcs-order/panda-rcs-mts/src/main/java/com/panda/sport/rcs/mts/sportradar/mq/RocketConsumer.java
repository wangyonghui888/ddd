package com.panda.sport.rcs.mts.sportradar.mq;

import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.mq.listener.ConsumerMessageListener;
import com.panda.sport.rcs.mts.sportradar.mq.config.RocketRpcProperties;
import com.panda.sport.rcs.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * black 
 * 消费消息
 */
@Component
@Slf4j
@DependsOn(value = "springContextUtils")
public class RocketConsumer {
	
	@Autowired
	private RocketRpcProperties rocketRpcProperties;

    @Autowired
    DefaultMQPushConsumer consumer;

    @PostConstruct
	public void init() throws Exception{
		try{
			reSetConfig();
			ConsumerMessageListener listener = new ConsumerMessageListener(rocketRpcProperties);
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
		consumer.setConsumerGroup(rocketRpcProperties.getGroup());
        consumer.setInstanceName(rocketRpcProperties.getInstance());
        consumer.setNamesrvAddr(rocketRpcProperties.getNameSrvAddr());
	}
	
}
