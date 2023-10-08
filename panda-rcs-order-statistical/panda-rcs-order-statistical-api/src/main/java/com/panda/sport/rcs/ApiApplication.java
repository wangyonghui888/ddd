package com.panda.sport.rcs;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@ServletComponentScan
@MapperScan({"com.panda.sport.rcs.db.mapper*","com.panda.sport.rcs.customdb.mapper*"})
@EnableAsync
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableDubbo
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}