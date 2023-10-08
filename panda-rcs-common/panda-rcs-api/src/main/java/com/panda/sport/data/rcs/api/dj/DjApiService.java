package com.panda.sport.data.rcs.api.dj;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.dj.*;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

/**
 * @Description 电竞赛事api
 * @Param
 * @Author zerone
 * @Date  2020-12-22 14:31:17
 * @return
 **/
public interface DjApiService {

    /**
     * 获取DJ赛事   最大最小值限额
     *
     * @param request
     * @return Response
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<DJAmountLimitResVo> getBetAmountLimit(Request<DJLimitAmoutRequest> request);

    /**
     *  DJ赛事 投注
     *
     * @param request
     * @return Response
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<DJBetResVo> bet(Request<DJBetReqVo> request);


    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<DJCancelOrderResVo> cancelOrder(Request<DjCancelOrderReqVo> request);
}
