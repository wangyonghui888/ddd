package com.panda.sport.rcs.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 开启Nacos 配置中心
 * @author skykong
 */
@Configuration
@RefreshScope
@EnableDiscoveryClient
public class NacosConfig {
}