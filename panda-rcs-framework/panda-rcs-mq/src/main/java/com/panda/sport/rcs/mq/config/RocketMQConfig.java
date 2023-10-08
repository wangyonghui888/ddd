package com.panda.sport.rcs.mq.config;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;


/**
 * @author :  holly
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.rcs.mq.config
 * @Description :订单同步MQ
 * @Date: 2019-10-05 19:07
 */
@Component
@ConditionalOnProperty("rocketmq.nameSrvAddr")
@Slf4j
public class RocketMQConfig {
    /*
    RocketMQ 订单派奖消费者
     */
    @Value("${rocketmq.nameSrvAddr}")
    private String nameSrvAddr;

    @Value("${rocketmq.rpc.group:panda-rcs-group}")
    private String consumerGroup;

    @Value("${rocketmq.rpc.instance:panda-rcs-instance}")
    private String instanceName;

    @Value("${rocketmq.rpc.threadmin:96}")
    private Integer threadmin;

    @Value("${rocketmq.rpc.threadmax:128}")
    private Integer threadmax;

    @Bean(destroyMethod = "shutdown")
    public DefaultMQPushConsumer orderInfoSyncConsumer() throws MQClientException {
    	DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(nameSrvAddr);
        consumer.setInstanceName(instanceName);
        // 设置消费地点,从最后一个进行消费(其实就是消费策略)
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setConsumeThreadMin(threadmin);
        consumer.setConsumeThreadMax(threadmax);
        log.info("rocket mq init finish! threadmin:" + threadmin + "threadmin:" + threadmax);
        return consumer;
    }
    
}
