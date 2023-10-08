package com.panda.sport.data.rcs.api.trade;

import com.panda.sport.data.rcs.api.Response;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Map;

/**
 * @Description 调用risk服务接口
 * @Author wealth
 * @Date 2023-03-03 13:50
 **/
public interface RedisApiService {

    /**
     * 根据key查询redis
     * @param key
     * @return Object
     */
    @POST
    @Path("hGetAllToObj")
    @Consumes({"application/json; charset=UTF-8"})
    @Produces({"application/json; charset=UTF-8"})
    Response<Object>  hGetAllToObj(String key);

    /**
     * 根据key查询redis
     * @param key
     * @return Map<String, String>
     */
    @POST
    @Path("hgetAll")
    @Consumes({"application/json; charset=UTF-8"})
    @Produces({"application/json; charset=UTF-8"})
    Response<Map<String, String>> hgetAll(String key);
}
