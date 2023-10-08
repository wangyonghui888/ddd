package com.panda.sport.data.rcs.api;


import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
/**
 * @Description  对外提供风控相关接口-用户异常
 * @Param
 * @Author  skyKong
 * @Date  20:30 2022/07/18
 * @return
 **/
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
public interface UserExceptionService {

    /**
     * 查询用户线上异常
     * @param request
     * @return
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response queryUserExceptionByOnline(Request request);

}
