package com.panda.sport.rcs.redis.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.core.cache.service.RcsClusterCache;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class RcsDataRedis extends RcsClusterCache {
    @Autowired
    JedisCluster jedisCluster;

    /**
     * reides hashMap 平分数量
     */
    private static final int redisMapAverageNumber = 10;

    public <E> Map<String, E> hGetAll2(String key, Class<E> hashValueClazz) {
        Map<String, String> entries = jedisCluster.hgetAll(key);
        if (entries == null || entries.size() == 0) {
            return Collections.emptyMap();
        }
        Map<String, E> resultMap = new LinkedHashMap<>(entries.size());
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            E valueObj = JSONObject.parseObject(entry.getValue(), hashValueClazz);
            resultMap.put(entry.getKey(), valueObj);
        }
        return resultMap;
    }

    public <T> T getObj2(String key, Class<T> clazz) {
        return jedisCluster.get(key) == null ? null : JsonFormatUtils.fromJson(jedisCluster.get(key), clazz);
    }

    public <T> T getObj3(String key, TypeReference<T> typeReference) {
        return jedisCluster.get(key) == null ? null : JSONObject.parseObject(jedisCluster.get(key), typeReference);
    }

    /**
     * hashmap 平均分配节点
     * @param key
     * @param hashKey
     * @param hashValue
     */
    public void hSetAverage(String key, String hashKey, String hashValue) {
        if(StringUtils.isBlank(hashKey)||StringUtils.isBlank(key)){return;}
        int i = hashKey.hashCode() % redisMapAverageNumber;
        String nKey=key+":%s";
        String nKey2 = String.format(nKey,Math.abs(i));
        hSet(nKey2, hashKey, hashValue);
    }



    /**
     * hashmap 平均分配节点get
     * @param key
     * @param hashKey
     */
    public String hGetAverage(String key, String hashKey) {
        if(StringUtils.isBlank(hashKey)||StringUtils.isBlank(key)){return null;}
        int i = hashKey.hashCode() % redisMapAverageNumber;
        String nKey=key+":%s";
        String nKey2 = String.format(nKey,Math.abs(i));
        return hGet(nKey2,hashKey);
    }


    /**
     * hashmap 平均分配节点
     * @param key
     * @param hashKey
     */
    public Long hashRemoveAverage(String key, String hashKey) {
        if(StringUtils.isBlank(hashKey)||StringUtils.isBlank(key)){return null;}
        int i = hashKey.hashCode() % redisMapAverageNumber;
        String nKey=key+":%s";
        String nKey2 = String.format(nKey,Math.abs(i));
        return hashRemove(nKey2,hashKey);
    }

    /**
     * 得到所有节点的hashMap
     * @param key
     * @param hashValueClazz
     * @param <E>
     * @return
     */
    public <E> Map<String, E> hGetAllAverage(String key, Class<E> hashValueClazz) {
        if(StringUtils.isBlank(key)){return null;}
        return hGetAll2(key+":*", hashValueClazz);
    }

    /**
     * 得到所有节点的hashMap2
     * @param key
     * @param hashValueClazz
     * @param <E>
     * @return
     */
    public <E> Map<String, E> hGetAllAverage2(String key, Class<E> hashValueClazz) {
        if(StringUtils.isBlank(key)){return null;}
        Map<String, E> grossMap =new HashMap<>();
        for (int i = 0; i < redisMapAverageNumber; i++) {
            String nKey=key+":"+i;
            Map<String, E> stringEMap = hGetAll2(nKey, hashValueClazz);
            grossMap.putAll(stringEMap);
        }
        return grossMap;
    }
}
