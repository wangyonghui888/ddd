package com.panda.sport.rcs.core.db.datasource;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于druid多数据源顶层
 * 抽象类,用于完成多DB的
 * 注入
 * @author kane
 * @since 2019-09-03
 * @version v1.1
 */
@Data
@Slf4j
@ConditionalOnProperty("jdbc.db.master.url")
@Component
public abstract class  AbstractDataSourceConfig {

    @Autowired
    private DruidDataSourceProperties druidDbProperties;

    /**
     * 创建数据源
     * @param url
     * @return
     */
    public DataSource createDataSource(String url,String userName,String password){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setInitialSize(druidDbProperties.getInitialSize());
        dataSource.setMinIdle(druidDbProperties.getMinIdle());
        dataSource.setMaxActive(druidDbProperties.getMaxActive());
        dataSource.setMaxWait(druidDbProperties.getMaxWait());
        dataSource.setTimeBetweenEvictionRunsMillis(druidDbProperties.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(druidDbProperties.getMinEvictableIdleTimeMillis());
        dataSource.setValidationQuery(druidDbProperties.getValidationQuery());
        dataSource.setTestWhileIdle(druidDbProperties.isTestWhileIdle());
        dataSource.setTestOnBorrow(druidDbProperties.isTestOnBorrow());
        dataSource.setTestOnReturn(druidDbProperties.isTestOnReturn());
        dataSource.setDriverClassName(druidDbProperties.getDriverClassName());
        dataSource.setQueryTimeout(druidDbProperties.getQueryTimeout());
        /**
         * 只有打开了德鲁伊监控才会设置监控配置到数据源中去
         */
        if(druidDbProperties.isOpenDruidStat()){
            try {
                dataSource.setFilters(druidDbProperties.getFilters());
            } catch (Exception e) {
                log.error("druid configuration initialization filter", e);
            }
            dataSource.setConnectionProperties(druidDbProperties.getConnectionProperties());
            dataSource.setUseGlobalDataSourceStat(druidDbProperties.isUseGlobalDataSourceStat());
        }
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        return dataSource;
    }
}
