package com.panda.sport.rcs.predict.utils;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;


/**
 * forecast 入库频率参数配置 主要用于数据库入库限制频率
 */
@Configuration
@Data
@RefreshScope
public class RcsPredictMysqlFrequencyNacosConfig {

    @Value("${forecast.insert.mysql.frequency:1000}")
    public Long forecastInsertMysqlFrequency;

    @Value("${rcs.risk.order.calc:1}")
    private Integer calcStatus;

}

