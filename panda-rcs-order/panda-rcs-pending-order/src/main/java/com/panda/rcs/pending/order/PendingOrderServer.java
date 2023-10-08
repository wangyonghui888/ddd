package com.panda.rcs.pending.order;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

@Slf4j
@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@EnableDubbo
@ComponentScan({"com.panda"})
@MapperScan(basePackages = {"com.panda.rcs.pending.order.mapper", "com.panda.sport.rcs.**.mapper"})
public class PendingOrderServer implements ApplicationListener {

    public static void main(String[] args) throws UnknownHostException {

        //SpringApplication.run(PendingOrderServer.class, args);
        ApplicationContext ctx = SpringApplication.run(PendingOrderServer.class, args);
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
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            log.info("初始化环境变量 ");
        }
        if (event instanceof ApplicationPreparedEvent) {
            log.info("初始化完成");
        }
        if (event instanceof ContextRefreshedEvent) {
            log.info("应用刷新");
        }
        if (event instanceof ApplicationReadyEvent) {
            log.info("应用已启动完成");
        }
        if (event instanceof ContextStartedEvent) {
            log.info("应用启动，需要在代码动态添加监听器才可捕获");
        }
        if (event instanceof ContextStoppedEvent) {
            log.info("应用停止");
        }
        if (event instanceof ContextClosedEvent) {
            log.info("应用关闭");
        }
    }

}
