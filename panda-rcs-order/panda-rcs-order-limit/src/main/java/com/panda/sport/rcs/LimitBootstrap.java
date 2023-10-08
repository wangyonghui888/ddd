package com.panda.sport.rcs;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

@SpringBootApplication
@MapperScan(basePackages = "com.panda.sport.rcs.**.mapper")
@Slf4j
@ServletComponentScan
@EnableDubbo
@EnableDiscoveryClient
public class LimitBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(LimitBootstrap.class, args);
    }
}
