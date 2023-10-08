package com.panda.rcs.cleanup.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "spring.redis.cluster")
@Data
public class RedisClusterConfigProperties {

    private List<String> nodes;

    private Integer maxAttempts;

    private Integer connectionTimeout;

    private Integer soTimeout;

    private String password;
}

