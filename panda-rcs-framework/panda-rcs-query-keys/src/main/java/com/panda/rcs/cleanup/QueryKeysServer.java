package com.panda.rcs.cleanup;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"com.panda.rcs.cleanup.*"})
@MapperScan(basePackages = "com.panda.rcs.cleanup.**.mapper")
public class QueryKeysServer {

    public static void main(String[] args) {
        SpringApplication.run(QueryKeysServer.class, args);
    }

}
