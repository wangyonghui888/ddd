package com.panda.sport.rcs.core.db.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.core.db.config.DBTypeEnum;
import com.panda.sport.rcs.core.db.routingdatasource.RCSRoutingDataSource;

/**
 * druid多数据源配置类
 * @author kane
 * @since 2019-09-03
 * @version v1.1
 */
@ConditionalOnProperty("jdbc.db.master.url")
@Component
public class DataSourceConfig extends AbstractDataSourceConfig{
    /**
     * DB主库地址
     */
    @Value("${jdbc.db.master.url}")
    private  String dbMasterUrl;

    @Autowired
    DruidDataSourceProperties druidDataSourceProperties;
    /**
     * 定义mapper路径
     */
    private final static String MAPPER_LOCATION="classpath*:mapper/**/*.xml";
    /**
     * 创建db1主库数据源
     * @return
     */
    @Bean("masterDataSource")
    public DataSource dataSourceDbMaster(){
        return  super.createDataSource(dbMasterUrl,druidDataSourceProperties.getMasterusername(),druidDataSourceProperties.getMasterpassword());
    }

    @Bean("routingDataSource")
    public DataSource routingDataSource(@Qualifier("masterDataSource") DataSource masterDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DBTypeEnum.DB1_MASTER, masterDataSource);
        RCSRoutingDataSource routingDataSource = new RCSRoutingDataSource();
        routingDataSource.setDefaultTargetDataSource(masterDataSource);
        routingDataSource.setTargetDataSources(targetDataSources);
        return routingDataSource;
    }
}
