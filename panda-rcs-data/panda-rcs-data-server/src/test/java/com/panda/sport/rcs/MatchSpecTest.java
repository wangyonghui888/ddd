package com.panda.sport.rcs;

import com.panda.sport.rcs.data.RcsDataServerBootstrap;
import com.panda.sport.rcs.data.service.IRcsSpecEventConfigService;
import com.panda.sport.rcs.pojo.RcsSpecEventConfig;
import groovy.util.logging.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RcsDataServerBootstrap.class)
@Slf4j
public class MatchSpecTest {

    @Resource
    private IRcsSpecEventConfigService rcsSpecEventConfigService;

    @Test
    public void eventConfigTest(){
        List<RcsSpecEventConfig> matchSpecEventConfigs = rcsSpecEventConfigService.getByMatchId(1234567L);
        System.out.println(matchSpecEventConfigs);
    }
}
