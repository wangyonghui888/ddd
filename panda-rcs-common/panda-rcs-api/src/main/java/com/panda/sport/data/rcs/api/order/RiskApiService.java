package com.panda.sport.data.rcs.api.order;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.order.OrderBeforeHandReqVo;
import com.panda.sport.data.rcs.dto.order.OrderBeforeHandResVo;

public interface RiskApiService {

    /**
     * 订单提前结算检查
     * @param requestParam
     * @return
     */
    @POST
    @Path("/orderBeforeHand")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<OrderBeforeHandResVo> orderBeforeHand(Request<OrderBeforeHandReqVo> requestParam);
}
