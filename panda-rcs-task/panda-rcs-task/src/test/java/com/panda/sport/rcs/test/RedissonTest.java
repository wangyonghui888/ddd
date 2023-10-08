package com.panda.sport.rcs.test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.panda.sport.rcs.core.cache.properties.RedisProperties;
import com.panda.sport.rcs.task.RcsTaskApplication;
import com.panda.sport.rcs.task.config.RedissonManager;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = RcsTaskApplication.class)
public class RedissonTest {

    public static void main(String[] args) {
    	RedissonManager redissonManager = new RedissonManager(new RedisProperties());
    	redissonManager.lock("test");
    	
    	System.out.println(1111);
    	
    	redissonManager.unlock("test");
	}
    

}
