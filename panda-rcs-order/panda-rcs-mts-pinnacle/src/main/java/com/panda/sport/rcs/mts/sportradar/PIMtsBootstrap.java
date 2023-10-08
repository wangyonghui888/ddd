package com.panda.sport.rcs.mts.sportradar;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "com.panda.sport.rcs")
@MapperScan(basePackages = "com.panda.sport.rcs.**.mapper")
@ImportResource(locations= {"classpath:app-provider.xml"})
@Slf4j
@EnableDubbo
@EnableScheduling
public class PIMtsBootstrap {
    public static void main(String[] args) { SpringApplication.run(PIMtsBootstrap.class, args); }
}
