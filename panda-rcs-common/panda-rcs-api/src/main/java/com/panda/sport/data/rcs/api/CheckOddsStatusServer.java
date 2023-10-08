package com.panda.sport.data.rcs.api;

import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.vo.OddStatusMessagePrompt;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author :  koala
 * @Project Name :  panda-rcs-common
 * @Package Name :  com.panda.sport.data.rcs.api
 * @Description :  检测赔率状态服务
 * @Date: 2023-02-08 10:51
 * --------  ---------  --------------------------
 */
public interface CheckOddsStatusServer {

    /**
     * 查询玩法集ID
     *
     * @param request
     * @return
     */
    @POST
    @Path("/checkOddsStatus")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<OddStatusMessagePrompt>  checkOddsStatus(Request<OrderBean> request);
}
