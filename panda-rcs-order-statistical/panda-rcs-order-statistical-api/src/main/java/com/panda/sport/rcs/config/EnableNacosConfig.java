package com.panda.sport.rcs.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 开启Nacos 配置中心
 * @author skykong
 */
@Configuration
@EnableConfigurationProperties({
        ObserveNameNumProperties.class,
        UserNameTagProperties.class})
public class EnableNacosConfig {
}