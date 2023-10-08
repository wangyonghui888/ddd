package com.panda.sport.rcs;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

@SpringBootApplication
@MapperScan(basePackages = {"com.panda.**.mapper"})
@Slf4j
@EnableDubbo
@EnableDiscoveryClient
@EnableSwagger2
@EnableScheduling
public class OddinBootstrap {

    public static void main(String[] args){
        ApplicationContext ctx = SpringApplication.run(OddinBootstrap.class, args);
        Environment env = ctx.getEnvironment();
        String[] activeProfiles = env.getActiveProfiles();
        log.info("##################################################");
        log.info("startSuccess : " + Arrays.toString(activeProfiles));
        log.info("##################################################");
       /* ConfigurableApplicationContext run=SpringApplication
                .run(OddInBootstrap.class,args);*/
       /* BetGuardApiConfig betGuardApiConfig=run.getBean(BetGuardApiConfig.class);
        System.out.println(betGuardApiConfig.getSharedKey());*/

       /* log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\thttp://localhost:{}\n\t" +
                        "Swagger: http://localhost:{}/swagger-ui.html\n\t" +
                        "External: \thttp://{}:{}\n\t" +
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));ots

        log.info("-------------------------注册检查---------------------------------");
        Map<String, ThirdOrderService> thirdStrategyList = ThirdStrategyFactory.getThirdStrategyList();
        thirdStrategyList.forEach((k, v) -> {
            log.info("key:{},value:{}", k, v);
        });
        log.info("-------------------------注册检查---------------------------------");*/

    }
}