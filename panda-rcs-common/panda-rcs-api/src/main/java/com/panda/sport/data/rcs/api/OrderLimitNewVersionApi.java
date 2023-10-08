package com.panda.sport.data.rcs.api;

import com.panda.sport.data.rcs.dto.OrderBean;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author :  koala
 * @Project Name :  panda-rcs-api
 * @Description :  订单新版限额查询接口
 * @Date: 2022-03-25 15:30
 */
public interface OrderLimitNewVersionApi {


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
    Response queryMaxBetAmountByOrder(Request<OrderBean> request);

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
    Response saveOrderCheckAmount(Request<OrderBean> request);

    /**
     * 查询商户的开关
     *
     * @param businessId 商户Id
     * @return
     */
    @POST
    @Path("/queryBusinessSwitch")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response queryBusinessSwitch(String businessId);
}
