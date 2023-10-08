package com.panda.sport.rcs.oddin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties("grpcpool")
public class PoolProperties {
    // 池中的最大连接数
    private int maxTotal = 10;

    // 最少的空闲连接数
    private int minIdle = 10;

    // 最多的空闲连接数
    private int maxIdle = 10;

    // 当连接池资源耗尽时,调用者最大阻塞的时间,超时时抛出异常 单位:毫秒数
    private long maxWaitMillis = -1L;

    // 连接池存放池化对象方式,true放在空闲队列最前面,false放在空闲队列最后
    private boolean lifo = true;

    // 连接空闲的最小时间,达到此值后空闲连接可能会被移除,默认即为30分钟
    private long minEvictableIdleTimeMillis = 1000L * 60L * 30L;

    // 连接耗尽时是否阻塞,默认为true
    private boolean blockWhenExhausted = true;
}
