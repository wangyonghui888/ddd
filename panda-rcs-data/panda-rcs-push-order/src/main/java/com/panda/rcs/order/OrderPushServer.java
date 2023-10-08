package com.panda.rcs.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;

@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@EnableFeignClients(value = {"com.panda.*"})
@ComponentScan({"com.panda.rcs.order.*", "com.panda.sport.rcs.*"})
@MapperScan({"com.panda.rcs.order.mapper", "com.panda.sport.rcs.mapper"})
public class OrderPushServer {

    public static void main(String[] args) {
        SpringApplication.run(OrderPushServer.class, args);
    }

    @Bean
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

}
