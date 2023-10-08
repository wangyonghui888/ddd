package com.panda.sport.rcs.core.db.datasource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@ConditionalOnProperty("jdbc.db.master.url")
@Component
public class DruidDataSourceProperties {
    /**
     * mysql 驱动名称
     */
	@Value("${jdbc.db.driverClassName:com.mysql.cj.jdbc.Driver}")
    private String driverClassName;

    /**
     * 初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时
     */
	@Value("${jdbc.db.initialSize:10}")
    private int initialSize = 10;

    /**
     * 最小连接池数量
     */
	@Value("${jdbc.db.minIdle:20}")
    private int minIdle = 20;

    /**
     * 最大连接池数量
     */
	@Value("${jdbc.db.maxActive:50}")
    private int maxActive = 50;

    /**
     * 获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁。
     */
	@Value("${jdbc.db.maxWait:10000}")
    private int maxWait = 10000;
	
	@Value("${jdbc.db.queryTimeout:30}")
    private int queryTimeout = 30;

    /**
     * 有两个含义： 1)
     * Destroy线程会检测连接的间隔时间，如果连接空闲时间大于等于minEvictableIdleTimeMillis则关闭物理连接。 2)
     * testWhileIdle的判断依据，详细看testWhileIdle属性的说明
     */
    private int timeBetweenEvictionRunsMillis = 60000;

    /**
     * 连接保持空闲而不被驱逐的最长时间
     */
    private int minEvictableIdleTimeMillis = 3600000;

    /**
     * 用来检测连接是否有效的sql，要求是一个查询语句，常用select
     * 'x'。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会其作用。
     */
    private String validationQuery = "SELECT USER()";

    /**
     * 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
     */
    private boolean testWhileIdle = true;

    /**
     * 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
     */
    private boolean testOnBorrow = false;

    /**
     * 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
     */
    private boolean testOnReturn = false;

    /**
     * 属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有： 监控统计用的filter:stat 日志用的filter:log4j
     * 防御sql注入的filter:wall
     */
    private String filters = "stat,wall";

    private String connectionProperties;
    /**
     * 白名单
     */
    private String allow;
    /**
     * 黑名单
     */
    private String deny;

    /**
     * 主库用户名
     */
    @Value("${jdbc.db.masterusername:admin}")
    private String masterusername = "admin";
    /**
     * 主库密码
     */
    @Value("${jdbc.db.masterpassword:admin}")
    private String masterpassword = "admin";

    /**
     * 合并多个dataSource监控
     */
    private boolean useGlobalDataSourceStat=true;
    /**
     * 是否开启druid监控
     */
    private boolean openDruidStat=false;
}
