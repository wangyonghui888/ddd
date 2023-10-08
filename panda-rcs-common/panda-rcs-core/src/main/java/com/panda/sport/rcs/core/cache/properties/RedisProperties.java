package com.panda.sport.rcs.core.cache.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: kane
 * @Description:读取redis配置信息并装载
 * @since 2019-09-06
 */
@Data
@Component
@ConfigurationProperties(prefix = "jedis.cluster")
public class RedisProperties {
    /**
     * 节点的连接字符串（含:IP地址与端口号）
     */
    private String nodesString;
    /**
     *连接是否被空闲连接回收器(如果有)进行检验.如果检测失败,
     * 则连接将被从池中去除.
     */
    private Boolean testWhileIdle;
    /**
     * 连接超时时间
     */
    private Integer connectionTimeout;
    /**
     * 读取数据超时
     */
    private Integer soTimeout;
    /**
     * 失败最大重连次数
     */
    private Integer maxAttempts;
    /**
     * 密码
     */
    private String  password;
}
