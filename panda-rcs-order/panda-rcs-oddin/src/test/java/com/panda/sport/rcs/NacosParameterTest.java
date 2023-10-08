package com.panda.sport.rcs;

import com.panda.sport.rcs.oddin.config.NacosParameter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OddinBootstrap.class)
@Slf4j
public class NacosParameterTest {

    @Resource
    private NacosParameter nacosParameter;

    @Value("${earlay.cancel.time:4}")
    private Long cancelTime;
    @Test
    public void NacosParameterTests() {

        Long oldDate = 0L;
        while (true) {
//            Long earlayCancelTime = nacosParameter.getEarlayCancelTime();
            Long earlayCancelTime = cancelTime;

            if (earlayCancelTime == oldDate) {
                log.info("==从nacos获取到的earlayCancelTime :{}", earlayCancelTime);
            } else {
                log.info("==参数配置值改变了---从nacos获取到的earlayCancelTime :{}", earlayCancelTime);
            }
            oldDate = earlayCancelTime;
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
