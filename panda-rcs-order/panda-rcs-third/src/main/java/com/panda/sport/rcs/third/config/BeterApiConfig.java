package com.panda.sport.rcs.third.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Beulah
 * @date 2023/3/20 16:42
 * @description todo
 */
@Component
@Data
@ConfigurationProperties(prefix = "beter")
public class BeterApiConfig {

    /**
     * 三方提供的apiKey
     */
    private String apiKey;
    /**
     * 三方提供的token 获取接口获取
     */
    private String token;
    /**
     * 三方接口域名
     */
    private String url;
    /**
     * 打折
     */
    private BigDecimal discount;

}
