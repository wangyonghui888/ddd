package com.panda.sport.sdk.mq;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.log.monitor.MonitorContant;
import com.panda.sport.sdk.annotation.AutoInitMethod;
import com.panda.sport.sdk.common.HashAlgorithms;
import com.panda.sport.sdk.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Singleton
@AutoInitMethod(init = "init")
public class Producer {

    @Inject
    PropertiesUtil propertiesUtil;

    private DefaultMQProducer producer = null;
    
    private String SEND_NAME = "RCS_SDK_CLIENT";

    private static final Logger log = LoggerFactory.getLogger(Producer.class);

    public synchronized void init() {
        if (producer == null) {
            try {
                producer = new DefaultMQProducer("Producer");
                String address =propertiesUtil.getValue("sdk.rocketmq.address");
                producer.setNamesrvAddr(address);
                producer.start();
            } catch (Exception e) {
                log.error("生产者初始化失败",e);
            }
        }
    }

    public void sendMsg(String data) {
        try {
            Message msg = new Message("SdkTopic", "tag_", "1", data.getBytes());
            SendResult result = producer.send(msg);
            log.info("Producer发送数据:"+data);
            log.info("Producer发送状态:"+result.getSendStatus());
        } catch (Exception e) {
            log.error("Producer发送失败",e);
        }
    }

    public void sendMsg(String topic,String data) {
        
    	sendMessage(topic, "", "", data, null);
    }
    
    private void setProperties(Message msg) {
    	if(msg == null) return;
    	
    	msg.putUserProperty("SEND_TIME", System.currentTimeMillis() + "");
    	msg.putUserProperty("SEND_NAME", this.SEND_NAME);
    	
    	LogContext context = LogContext.getContext();
    	msg.putUserProperty(MonitorContant.MONITOR_UUID, context.getRequestId());
    	
    	if(StringUtils.isBlank(context.getPath())) {
    		LogContext.remove();
    	}
    }

    public void sendMsg(String topic,String tags,String data) {
           	sendMessage(topic, tags, "", data, null);
    }

	public void sendMessage(String topic, String tag, String key, String data, Map<String, String> properties) {
		try {
            Message msg = new Message(topic, tag,key,data.getBytes("utf-8"));
            setProperties(msg);
            if(properties != null ) {
            	for(String str : properties.keySet()) {
            		msg.putUserProperty(str, properties.get(str));
            	}
            }
            SendResult result = producer.send(msg);

            log.info("发送消息：topic：{}，tag：{}，key：{}，data：{}",topic,tag,key,data);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
	}
    public void sendMsg(String topic,String tags, String key, String data) {
    	sendMessage(topic, tags, key, data, new HashMap<String, String>());
    }

    public void sendMsg(String topic,String tags, String key, String data,String hashKey) {
        try {
            Message msg = new Message(topic, tags,key,data.getBytes());
            setProperties(msg);

            SendResult result = producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                    if(list == null || list.size() <= 0 ) return null;
                    int hash = HashAlgorithms.java(String.valueOf(o));
                    return list.get(Math.abs(hash) % list.size());
                }
            },hashKey);
            log.info("发送消息：topic：{}，tag：{}，key：{}，data：{}",topic,tags,key,data);
        } catch (Exception e) {
        	log.error(e.getMessage(),e);
        }
    }
}
