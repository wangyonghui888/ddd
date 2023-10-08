package com.panda.sport.rcs.test;

import com.panda.sport.rcs.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.task.RcsTaskApplication;
import com.panda.sport.rcs.task.service.profit.ScoreMonitoringService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = RcsTaskApplication.class)
public class DeleteOddsTest {

	@Autowired
	MongoTemplate mongotemplate;
	
	@Autowired
	ProducerSendMessageUtils producerSendMessageUtils;
	
	@Autowired
	StandardSportMarketMapper standardSportMarketMapper;
	
    @Autowired
	private ScoreMonitoringService scoreMonitoringService;

	@Test
    public void test(){

	}

}
