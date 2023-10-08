package com.panda.sport.rcs.mgr.config;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;

import java.util.HashSet;
import java.util.Set;


/**
 * 初始化 JedisSlotConnectionHandlerImp 配置
 * @date 2023-03-06
 * @author magic
 */
@Configuration
@Data
public class RedisConfig {

    @Bean
    public JedisSlotConnectionHandlerImp getJedisSlotConnectionHandlerImp(RedisPoolConfig redisPoolConfig) {
        Set<HostAndPort> nodes = new HashSet<HostAndPort>();
        for (String hostport : redisPoolConfig.getNodesString().split(",")) {
            String[] ipport = hostport.split(":");
            String ip = ipport[0];
            int port = Integer.parseInt(ipport[1]);
            nodes.add(new HostAndPort(ip, port));
        }
        return new JedisSlotConnectionHandlerImp(nodes, redisPoolConfig, redisPoolConfig.getConnectionTimeout(), redisPoolConfig.getSoTimeout(), redisPoolConfig.getPassword());
    }

}
