package com.panda.sport.sdk.mapper;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * forecast 入库频率参数配置 主要用于数据库入库限制频率
 */
@Configuration
@Data
public class NasConfig {
    @Value("${sdk.categorySet.pk.config:134}")
    public String  pkCategorySet;

    @Value("${sdk.categorySet.addTime.config:135}")
    public String  addTimeCategorySet;

}

