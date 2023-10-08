package com.panda.sport.data.rcs.api;

import com.panda.sport.data.rcs.dto.PendingOrderDto;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author :  koala
 * @Project Name :  panda-rcs-api
 * @Description :  预约投注API
 * @Date: 2022-05-05 18:30
 */
public interface ReserveBetApi {

    /**
     * 用户点击投注选项返回当前选项最大可投注金额
     *
     * @param request 请求参数
     * @return Response
     */
    @POST
    @Path("/queryMaxBetAmountByOrder")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response queryMaxBetAmountByOrder(Request<PendingOrderDto> request);

    /**
     * 用户投注检查限额信息
     *
     * @param request 请求参数
     * @return 返回是否检查通过
     */
    @POST
    @Path("/saveOrderCheckAmount")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response saveOrderCheckAmount(Request<PendingOrderDto> request);

    /**
     * 取消订单
     * @param request 请求参数
     * @return 成功失败
     */
    @POST
    @Path("/cancelOrder")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response cancelOrder(Request<PendingOrderDto> request);
}
