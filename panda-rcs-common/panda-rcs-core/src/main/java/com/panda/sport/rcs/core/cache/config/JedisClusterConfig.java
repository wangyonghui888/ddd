package com.panda.sport.rcs.core.cache.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.cache.properties.RedisProperties;
import com.panda.sport.rcs.core.cache.service.RcsClusterCache;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

@Component
@ConditionalOnProperty("jedis.cluster.nodesString")
public class JedisClusterConfig  {

    @Autowired
    private RedisProperties redisProperties;

    @Value("${spring.redis.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.pool.min-idle}")
    private int minIdle;
    @Value("${spring.redis.pool.max-wait}")
    private long maxWaitMillis;
    @Value("${spring.redis.pool.max-active}")
    private int maxActive;
    @Value("${spring.redis.timeout}")
    private int timeout;

    /**
     * redis cluster模式下
     * @return
     */
    @Bean
    public JedisCluster jedisCluster() {
        String[] nodes = redisProperties.getNodesString().split(",");
        Set<HostAndPort> hostAndPortSet = new HashSet<>();
        for (String node : nodes) {
            String[] nodeAttrs = node.trim().split(":");
            if(nodeAttrs!=null && nodeAttrs.length>1){
                HostAndPort hap = new HostAndPort(nodeAttrs[0], Integer.parseInt(nodeAttrs[1]));
                hostAndPortSet.add(hap);
            }
        }


        /**
         * 集群模式下jedisPool 采用默认的配置
         */
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setBlockWhenExhausted(false);
        JedisCluster jedisCluster = new JedisCluster(hostAndPortSet,
                redisProperties.getConnectionTimeout(),
                redisProperties.getSoTimeout(),
                redisProperties.getMaxAttempts(),
                redisProperties.getPassword(), poolConfig);
        return jedisCluster;
    }
    
//    @Bean
//    public JedisPoolConfig jedisPoolConfig() {
//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMaxTotal(maxActive);
//        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
//        jedisPoolConfig.setMaxIdle(maxIdle);
//        jedisPoolConfig.setMinIdle(minIdle);
//        return jedisPoolConfig;
//    }

    /**
     * 注入RedisConnectionFactory
     * @return
     */
//    @Bean("redisConnectionFactory")
//    public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
//    	RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
//        //Set<RedisNode> clusterNodes
//        String[] serverArray = redisProperties.getNodesString().split(",");
//        Set<RedisNode> nodes = new HashSet<RedisNode>();
//        for(String ipPort:serverArray){
//            String[] ipAndPort = ipPort.split(":");
//            nodes.add(new RedisNode(ipAndPort[0].trim(),Integer.valueOf(ipAndPort[1])));
//        }
//        redisClusterConfiguration.setClusterNodes(nodes);
//        redisClusterConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
//        return new JedisConnectionFactory(redisClusterConfiguration,jedisPoolConfig);
//    }
 
    /**
     * 重新实现StringRedisTmeplate：键值都是String的的数据
     * @param redisConnectionFactory
     * @return
     */
//    @Bean("redisTemplate")
//    public RedisTemplate stringRedisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
//        StringRedisTemplate template = new StringRedisTemplate();
//        template.setConnectionFactory(redisConnectionFactory);
//        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//        // key采用String的序列化方式
//        template.setKeySerializer(stringRedisSerializer);
//        // hash的key采用String的序列化方式
//        template.setHashKeySerializer(stringRedisSerializer);
//        template.setValueSerializer(stringRedisSerializer);
//        template.setHashValueSerializer(stringRedisSerializer);
//        return template;
//    }

    @Bean
    public RedisClient redisClient(){
        /**
         * 如果是集群环境
         */
    	//redisIsCluster
//        if(IS_YES_CLUSTER.equals(IS_YES_CLUSTER)){
//            return new RcsClusterCache();
//        }else{
//            return new RcsCache();
//        }
    	return new RcsClusterCache();
    }


//    @Bean
//    public HashOperations<String, String, String> hashOperations(RedisTemplate<String, String> redisTemplate) {
//        return redisTemplate.opsForHash();
//    }
//
//    @Bean
//    public ValueOperations<String, String> valueOperations(RedisTemplate<String, String> redisTemplate) {
//        return redisTemplate.opsForValue();
//    }
}