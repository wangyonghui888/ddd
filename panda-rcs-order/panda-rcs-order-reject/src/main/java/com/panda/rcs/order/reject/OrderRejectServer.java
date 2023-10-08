package com.panda.rcs.order.reject;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@EnableDubbo
@ComponentScan({"com.panda"})
@MapperScan(basePackages = {"com.panda.rcs.order.reject.mapper", "com.panda.sport.rcs.**.mapper"})
public class OrderRejectServer{

    public static void main(String[] args) {
        SpringApplication.run(OrderRejectServer.class, args);
    }



}
