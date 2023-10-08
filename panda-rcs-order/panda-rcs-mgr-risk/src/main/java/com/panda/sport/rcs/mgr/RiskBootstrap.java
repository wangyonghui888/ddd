package com.panda.sport.rcs.mgr;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

@SpringBootApplication
@Slf4j
@MapperScan(basePackages = "com.panda.sport.rcs.**.mapper")
@ImportResource(locations= {"classpath:app-provider.xml"})
@ComponentScan(basePackages = {"com.panda.sport.rcs", "com.panda.sports.auth"})
//@ComponentScan(basePackages = {"com.panda.sport.rcs"})
@ServletComponentScan
public class RiskBootstrap {

    public static void main(String[] args) throws UnknownHostException {
        ApplicationContext ctx = SpringApplication.run(RiskBootstrap.class, args);
        Environment env = ctx.getEnvironment();
        String[] activeProfiles = env.getActiveProfiles();
        log.info("##################################################");
        log.info("startSuccess : " + Arrays.toString(activeProfiles));
        log.info("##################################################");

        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\thttp://localhost:{}\n\t" +
                        "External: \thttp://{}:{}\n\t" +
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
        //SpringApplication.run(DjBootstrap.class, args);
    }
}
