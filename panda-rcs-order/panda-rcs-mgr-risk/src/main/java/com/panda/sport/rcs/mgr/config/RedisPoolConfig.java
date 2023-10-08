package com.panda.sport.rcs.mgr.config;

import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 获取redis配置数据
 * @date 2023-03-06
 * @author magic
 */
@ConfigurationProperties(prefix = "jedis.cluster")
@Configuration
@Data
public class RedisPoolConfig extends GenericObjectPoolConfig {
    public RedisPoolConfig() {
        this.setTestWhileIdle(true);
        this.setMinEvictableIdleTimeMillis(60000L);
        this.setTimeBetweenEvictionRunsMillis(30000L);
        this.setNumTestsPerEvictionRun(-1);
    }

    private String nodesString;

    private String password;

    private Integer maxAttempts = 3;

    private Integer connectionTimeout = 5000;

    private Integer soTimeout = 1000;

}
