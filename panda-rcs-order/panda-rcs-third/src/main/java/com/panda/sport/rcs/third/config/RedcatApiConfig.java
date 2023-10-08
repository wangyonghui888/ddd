package com.panda.sport.rcs.third.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Beulah
 * @date 2023/5/9 20:38
 * @description todo
 */
@Component
@Data
@ConfigurationProperties(prefix = "redcat")
public class RedcatApiConfig {

    /**
     * 三方接口域名
     */
    private String url;

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String pwd;
}
