package com.panda.sport.rcs.oddin.config;

import com.panda.sport.rcs.oddin.djmq.Sandbox;
import com.panda.sport.rcs.oddin.djmq.SandboxCannotCreateObjectException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author Z9-conway
 */
@Slf4j
@Configuration
public class DjRocketMqConfig {

    @Resource
    private NacosParameter nacosParameter;

    @Bean(autowire = Autowire.BY_NAME, value = "djProducer")
    MQProducer producerSandbox1() throws MQClientException, SandboxCannotCreateObjectException {
        DefaultMQProducer producer = createProducerInSandbox();
        initProducer(producer, nacosParameter.getDjRocketmqAddress(), nacosParameter.getDjRocketmqGroup());
        log.info("=====获取到 dj.rocketmq.address ：{},dj.rocketmq.group ：{} ====",  nacosParameter.getDjRocketmqAddress(), nacosParameter.getDjRocketmqGroup());

        return producer;
    }

    private DefaultMQProducer createProducerInSandbox() throws SandboxCannotCreateObjectException {
        Sandbox sandbox = new Sandbox("org.apache.rocketmq.client");
        return sandbox.createObject(DefaultMQProducer.class);
    }

    private void initProducer(DefaultMQProducer producer, String namesrvAddr, String group) throws MQClientException {
        producer.setNamesrvAddr(namesrvAddr);
        producer.setProducerGroup(group);
        producer.setRetryAnotherBrokerWhenNotStoreOK(true);
        producer.start();
    }
}
