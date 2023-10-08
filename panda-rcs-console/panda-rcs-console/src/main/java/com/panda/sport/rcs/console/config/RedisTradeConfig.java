package com.panda.sport.rcs.console.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class RedisTradeConfig {


    @Bean(name = "jedisClusterTrade")
    public JedisCluster getJedisClusterTrade(RedisTradePoolConfig poolConfig) {
        Set<HostAndPort> nodes = new HashSet<HostAndPort>();
        for (String hostport : poolConfig.getNodesString().split(",")) {
            String[] ipport = hostport.split(":");
            String ip = ipport[0];
            int port = Integer.parseInt(ipport[1]);
            nodes.add(new HostAndPort(ip, port));
        }
        return new JedisCluster(nodes, poolConfig.getConnectionTimeout(),
                poolConfig.getSoTimeout(), poolConfig.getMaxAttempts(), poolConfig.getPassword(), poolConfig);

    }
}