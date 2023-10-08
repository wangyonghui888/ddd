package com.panda.sport.sdk.bean;

import com.google.inject.Singleton;
import com.panda.sport.sdk.annotation.AutoInsert;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

@AutoInsert(prefix = "sdk.redis")
@Singleton
public class RedisPoolConfig extends GenericObjectPoolConfig {
    public RedisPoolConfig() {
        this.setTestWhileIdle(true);
        this.setMinEvictableIdleTimeMillis(60000L);
        this.setTimeBetweenEvictionRunsMillis(30000L);
        this.setNumTestsPerEvictionRun(-1);
    }

    private String address;

    private String password;

    private Integer maxAttempts = 3;

    private Integer connectionTimeout = 5000;

    private Integer soTimeout = 1000;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(Integer soTimeout) {
        this.soTimeout = soTimeout;
    }


}
