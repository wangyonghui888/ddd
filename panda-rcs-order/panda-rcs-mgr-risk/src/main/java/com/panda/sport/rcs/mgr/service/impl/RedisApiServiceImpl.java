package com.panda.sport.rcs.mgr.service.impl;

import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.trade.RedisApiService;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mgr.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @Description risk redis dubbo服务接口
 * @Author wealth
 * @Date 2023-03-03 13:50
 **/
@Service(connections = 5, retries = 0, timeout = 3000)
@Slf4j
@org.springframework.stereotype.Service
public class RedisApiServiceImpl implements RedisApiService {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Response<Object> hGetAllToObj(String key) {
        log.info("根据key查询redis-hGetAllToObj:{}",key);
        return Response.success(redisClient.hGetAllToObj(key));
    }

    @Override
    public Response<Map<String, String>> hgetAll(String key) {
        log.info("根据key查询redis-hGetAllToObj:{}",key);
        return Response.success(redisUtils.hgetAll(key));
    }
}
