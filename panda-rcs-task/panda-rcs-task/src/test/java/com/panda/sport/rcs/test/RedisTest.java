package com.panda.sport.rcs.test;

import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.task.RcsTaskApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.test
 * @Description :  TODO
 * @Date: 2020-01-27 13:44
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RcsTaskApplication.class)
public class RedisTest {
    @Autowired
    public RedisClient redisClient;

    @Test
    public void test(){
        Long i =  redisClient.hincrBy("a:1","b",1);
        redisClient.hSet("a:1","b","1.0");
    }
}
