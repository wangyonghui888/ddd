package com.panda.sport.rcs.mq.config;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.Validators;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.ServiceState;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.log.monitor.MonitorContant;
import com.panda.sport.rcs.log.monitor.ServiceMonitorBean;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author :  Felix
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mq.config
 * @Description :  TODO
 * @Date: 2019-10-09 11:45
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Component
@ConditionalOnProperty("rocketmq.producer.namesrvAddr")
public class RocketProducerConfig {
    @Value("${rocketmq.producer.groupName}")
    private String groupName;
    @Value("${rocketmq.producer.namesrvAddr}")
    private String namesrvAddr;
    /**
     * 消息最大大小，默认4M
     */
    @Value("${rocketmq.producer.maxMessageSize}")
    private Integer maxMessageSize;
    /**
     * 消息发送超时时间，默认3秒
     */
    @Value("${rocketmq.producer.sendMsgTimeout}")
    private Integer sendMsgTimeout;
    /**
     * 消息发送失败重试次数，默认2次
     */
    @Value("${rocketmq.producer.retryTimesWhenSendFailed}")
    private Integer retryTimesWhenSendFailed;
    
    private static DefaultMQProducer producer;

	   /**
	 * 
	 */
	@PostConstruct
    public void init() {
		try {
			if(this.producer != null && this.producer.getDefaultMQProducerImpl().getServiceState() == ServiceState.RUNNING) {
				log.warn("mq发送端已经启动不在重新启动！");   
				return;
			}
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	   
		ProducerSendMessageUtils.SEND_SERVICE = this.groupName;
        producer = new DefaultMQProducer(this.groupName);
        this.producer.setNamesrvAddr(this.namesrvAddr);
        //如果需要同一个jvm中不同的this.producer往不同的mq集群发送消息，需要设置不同的instanceName
        //this.producer.setInstanceName(instanceName);
        if (this.maxMessageSize != null) {
            this.producer.setMaxMessageSize(this.maxMessageSize);
        }
        if (this.sendMsgTimeout != null) {
            this.producer.setSendMsgTimeout(this.sendMsgTimeout);
        }
        //如果发送消息失败，设置重试次数，默认为2次
        if (this.retryTimesWhenSendFailed != null) {
            this.producer.setRetryTimesWhenSendFailed(this.retryTimesWhenSendFailed);
        }

        try {
            this.producer.start();
            log.info(String.format("this.producer is start ! groupName:[%s],namesrvAddr:[%s]"
                    , this.groupName, this.namesrvAddr));
        } catch (MQClientException e) {
            log.error(e.getMessage(),e);
        }
       log.info("生产者启动完成");
    }

    /**
     * 向rocketmq发送普通消息的生产者
     */
    public void send(Message msg, SendCallback sendCallback) throws MQClientException, RemotingException, InterruptedException {
        //校验消息
        Validators.checkMessage(msg, this.producer);
        putUUidMsg(msg);
        this.producer.send(msg, sendCallback);
    }
    
    public SendResult send(Message msg, MessageQueue  messageQueue) throws Exception {
        Validators.checkMessage(msg, this.producer);
        putUUidMsg(msg);
        return this.producer.send(msg, messageQueue);
    }

	public SendResult send(Message msg, MessageQueueSelector messageQueueSelector, String hashKey) throws Exception {
		Validators.checkMessage(msg, this.producer);
		putUUidMsg(msg);
        return this.producer.send(msg, messageQueueSelector,hashKey);
	}
	
	private void putUUidMsg(Message msg) {
		LogContext context = LogContext.getContext();
		String uuid = context.getRequestId();
		msg.putUserProperty(MonitorContant.MONITOR_UUID, uuid);
		
		if(!StringUtils.isBlank(context.getMonitorCode())) {
			msg.putUserProperty(MonitorContant.MONITOR_TAG, "1");
		}
	}

}
