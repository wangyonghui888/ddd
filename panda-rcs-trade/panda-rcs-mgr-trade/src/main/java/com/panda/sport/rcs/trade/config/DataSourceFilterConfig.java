package com.panda.sport.rcs.trade.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@RefreshScope
public class DataSourceFilterConfig {

    @Value("${rcs.data.source.config.filter:AO}")
    private String dataSourceCon;

}
