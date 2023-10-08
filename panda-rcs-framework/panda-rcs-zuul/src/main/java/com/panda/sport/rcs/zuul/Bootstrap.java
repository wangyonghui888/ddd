package com.panda.sport.rcs.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableZuulProxy
@ComponentScan(basePackages="com.panda.sport.rcs")
public class Bootstrap {
	
    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }
}
