package com.panda.sport.rcs.mq.utils;

import org.apache.rocketmq.common.message.MessageExt;

/**
 * 
 * @author black
 *
 * @param <T>
 */
public interface Consumer<T> {
	
	public Boolean isStart();
	
	public String getKeys();
	
	public Boolean handleMsg(MessageExt ext) ;
	
	public String getConsumerConfig();

	public T getBody();
	
	public default void setProducerSendMessageUtils(ProducerSendMessageUtils producerSendMessageUtils) {}

}
