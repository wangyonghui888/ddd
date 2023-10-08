//package com.panda.sport.rcs.core.cache.config;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import lombok.extern.slf4j.Slf4j;
//import org.redisson.Redisson;
//import org.redisson.api.RedissonClient;
//import org.redisson.config.ClusterServersConfig;
//import org.redisson.config.Config;
//import org.redisson.config.SingleServerConfig;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.panda.sport.rcs.core.cache.properties.RedisProperties;
//
//@Configuration
//@Slf4j
//public class RedissonConfig {
//
//    @Autowired
//    private RedisProperties redisProperties;
//
//    @Bean
//    public RedissonClient redissonClient() {
//        String[] nodes = redisProperties.getNodesString().split(",");
//        RedissonClient redisson = null;
//
//        try {
//            log.info("RedissonClient nodes:{]",redisProperties.getNodesString());
//            List<String> nodeList = Arrays.asList(nodes);
//            nodeList = nodeList.stream().map(n -> "redis://" + n).collect(Collectors.toList());
//            nodeList.toArray(nodes);
//            Config config = new Config();
//            config.useClusterServers().setScanInterval(2000).addNodeAddress(nodes).setPassword(redisProperties.getPassword());
//            redisson = Redisson.create(config);
//        }
//        catch (Exception ex){
//            log.error("RedissonClient 初始化异常 ,{}",ex);
//            log.error("RedissonClient error:",ex);
//        }
//        return redisson;
//    }
//}
