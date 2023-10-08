package com.panda.sport.rcs;

import com.panda.sport.rcs.third.factory.ThirdStrategyFactory;
import com.panda.sport.rcs.third.service.third.ThirdOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;

@SpringBootApplication
@MapperScan(basePackages = {"com.panda.**.mapper"})
@Slf4j
@EnableDubbo
@EnableDiscoveryClient
@EnableSwagger2
@RefreshScope
@EnableScheduling
public class ThirdBootstrap {

    public static void main(String[] args) throws UnknownHostException {
        ApplicationContext ctx = SpringApplication.run(ThirdBootstrap.class, args);
        Environment env = ctx.getEnvironment();
        String[] activeProfiles = env.getActiveProfiles();
        log.info("##################################################");
        log.info("startSuccess : " + Arrays.toString(activeProfiles));
        log.info("##################################################");

        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\thttp://{}:{}\n\t" +
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));

        log.info("-------------------------注册检查---------------------------------");
        Map<String, ThirdOrderService> thirdStrategyList = ThirdStrategyFactory.getThirdStrategyList();
        thirdStrategyList.forEach((k, v) -> {
            log.info("key:{},value:{}", k, v);
        });
        log.info("-------------------------注册检查---------------------------------");

    }
}