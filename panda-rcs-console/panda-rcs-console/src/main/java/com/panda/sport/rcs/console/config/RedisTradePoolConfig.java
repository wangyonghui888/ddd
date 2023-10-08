package com.panda.sport.rcs.console.config;

import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("jedis.cluster2")
@Data
public class RedisTradePoolConfig extends GenericObjectPoolConfig {
    public RedisTradePoolConfig() {
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
