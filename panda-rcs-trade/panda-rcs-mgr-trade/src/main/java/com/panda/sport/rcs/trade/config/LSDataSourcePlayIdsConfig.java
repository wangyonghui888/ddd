package com.panda.sport.rcs.trade.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@RefreshScope
public class LSDataSourcePlayIdsConfig {

    @Value("${ls.playIds:1,2,4,7,17,18,19,113,114,111,119,121,122,118,229,115,116,123,124,306,307,308,309,313,312}")
    private String playIds;

}
