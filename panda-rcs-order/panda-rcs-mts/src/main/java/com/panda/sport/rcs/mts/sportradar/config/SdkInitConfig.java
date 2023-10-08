package com.panda.sport.rcs.mts.sportradar.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author :  koala
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mts.sportradar.config
 * @Description :  配置文件
 * @Date: 2022-02-08 13:21
 */
@Component
@ConfigurationProperties(prefix = "mts.sdk")
@Data
public class SdkInitConfig {
    private String username;
    private String password;
    private String hostname;
    private String vhost;
    private Integer limitId;
    private Integer bookmakerId;
    private String keycloakHost;
    private String keycloakUsername;
    private String keycloakPassword;
    private String keycloakSecret;
    private String mtsClientApiHost;
}
