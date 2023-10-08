package com.panda.sport.rcs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 开启Nacos 配置中心
 * @author skykong
 */
@Data
@ConfigurationProperties(prefix = "user-tag")
public class UserNameTagProperties {
    private List<String> tagNum;
}