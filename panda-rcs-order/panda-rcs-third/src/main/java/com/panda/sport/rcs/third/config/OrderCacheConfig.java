package com.panda.sport.rcs.third.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author Beulah
 * @date 2023/6/17 14:28
 * @description todo
 */

@Data
@RefreshScope
@Component
@ConfigurationProperties(prefix = "order.strategy.cache")
public class OrderCacheConfig {

    //@Value("${order.strategy.cache.rate:70}")
    private String rate = "70";
    //@Value("${order.strategy.cache.time:2}")
    private String time = "2";
}
