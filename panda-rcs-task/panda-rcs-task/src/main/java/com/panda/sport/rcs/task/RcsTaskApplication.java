package com.panda.sport.rcs.task;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.panda.sport.rcs")
@MapperScan("com.panda.sport.rcs.mapper")
@EnableScheduling
public class RcsTaskApplication {
    public static void main(String[] args) {
        SpringApplication.run(RcsTaskApplication.class, args);
    }
}