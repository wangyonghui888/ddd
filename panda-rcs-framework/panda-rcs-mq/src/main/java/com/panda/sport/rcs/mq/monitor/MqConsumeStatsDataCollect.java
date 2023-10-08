package com.panda.sport.rcs.mq.monitor;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.admin.ConsumeStats;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.monitor.entity.MqConsumerStatsBean;
import com.panda.sport.rcs.monitor.mq.MQConsumerAdminApi;
import com.panda.sport.rcs.monitor.utils.OsUtis;

@Component("mQConsumerAdminApi")
@ConditionalOnBean(name = "sendMqApi")
public class MqConsumeStatsDataCollect implements MQConsumerAdminApi{
	
	private Logger log = LoggerFactory.getLogger(MqConsumeStatsDataCollect.class);
	
	private DefaultMQAdminExt defaultMQAdminExt;
	
	private Boolean isCheckPass = false;
	
	public MqConsumeStatsDataCollect(Environment env) {
		String name = OsUtis.getMqNameServerConfigName();
		name = StringUtils.isBlank(name) ? "rocketmq.nameSrvAddr" : name;
		String nameServer = env.getProperty(name);
		if(StringUtils.isBlank(nameServer)) return;
		
		try {
			log.info("load rocketMq collect Namesrv addr:{}",nameServer);
			defaultMQAdminExt = new DefaultMQAdminExt();
		    defaultMQAdminExt.setNamesrvAddr(nameServer);
			defaultMQAdminExt.start();
			isCheckPass = true;
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	@Override
	public MqConsumerStatsBean getConsumeStats(String groupName) {
		try {
			MqConsumerStatsBean bean = JSONObject.parseObject(JSONObject.toJSONString(defaultMQAdminExt.examineConsumeStats(groupName)) , MqConsumerStatsBean.class);
			return bean;
		}catch (Exception e) {
			log.error(e.getMessage(),e);
			return null;
		}
	}

	@Override
	public Boolean isCheckPass() {
		return this.isCheckPass;
	}
	
	public static void main(String[] args) {
		try {
			DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
		    defaultMQAdminExt.setNamesrvAddr("127.0.0.1:9876");
			defaultMQAdminExt.start();
			
			ConsumeStats consumeStats = defaultMQAdminExt.examineConsumeStats("panda-rcs-task-group");
			System.out.println(JSONObject.toJSONString(consumeStats));
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
