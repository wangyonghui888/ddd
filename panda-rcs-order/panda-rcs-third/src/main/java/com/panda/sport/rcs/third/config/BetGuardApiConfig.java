package com.panda.sport.rcs.third.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Beulah
 * @date 2023/3/20 16:42
 * @description todo
 */
@Component
@Data
@ConfigurationProperties(prefix = "cts")
@RefreshScope
public class BetGuardApiConfig {


    /**
     * 提供给三方的token
     */
    private String sharedKey;
    /**
     * 三方接口域名
     */
    private String url;

    /**
     * 默认限额值
     */
    private long defaultLimit = 2000L;


}
