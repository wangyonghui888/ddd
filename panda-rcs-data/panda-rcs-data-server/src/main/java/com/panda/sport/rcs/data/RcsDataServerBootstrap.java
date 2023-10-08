package com.panda.sport.rcs.data;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ClassName: RcsDataServerBootstrap <br/>
 * Description: xxl-job定时任务job的启动服务入口<br/>
 * date: 2019/9/23 22:02<br/>
 * @author Administrator<br />
 * @since JDK 1.8
 */
@SpringBootApplication
@ComponentScan(basePackages="com.panda.sport.rcs")
@MapperScan(basePackages="com.panda.sport.rcs.**.mapper")
@DubboComponentScan("com.panda.sport.rcs")
@EnableDubboConfig
@EnableScheduling
public class RcsDataServerBootstrap  {
    public static void main(String[] args) {
        SpringApplication.run(RcsDataServerBootstrap.class,args);
    }
}
