package com.panda.sport.data.rcs.api.config;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.limit.*;
import com.panda.sport.data.rcs.dto.special.RcsUserSpecialBetLimitConfigDTO;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

/**
 * @Description 配置读取
 * @Param
 * @Author lithan
 * @Date 2023-02-15 11:04:20
 * @return
 **/
public interface ConfigApiService {

    /**
     * 获取mts-1开关配置
     *
     * @param request
     * @return Response
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<String> getMts1Status(Request<Mts1StatusReqVo> request);

}
