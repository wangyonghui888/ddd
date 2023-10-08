package com.panda.sport.data.rcs.api;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.data.rcs.dto.limit.UserLimitReferenceResVo;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @Description  对外提供风控相关接口
 * @Param
 * @Author  max
 * @Date  20:30 2019/12/10
 * @return
 **/
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
public interface OrderPaidApiService {

    /**
     * SDK 初始化参数加载
     * @return Response
     */
	@POST
    @Path("/loadSdkConfig")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
	Response loadSdkConfig();


    /**
     * 查询未登录用户最大最小可投注金额
     * @param requestParam 请求参数
     * @return Response
     */
	@POST
    @Path("/queryInitMaxBetMoneyBySelect")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response queryInitMaxBetMoneyBySelect(Request<OrderBean> requestParam);


    /**
     * 用户点击投注选项返回当前选项最大可投注金额
     * @param requestParam 请求参数
     * @return Response
     */
	@POST
    @Path("/queryMaxBetMoneyBySelect")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response queryMaxBetMoneyBySelect(Request<OrderBean> requestParam);


    /**
     * 校验当前订单是否超过最大赔付金额
     * @param requestParam 请求参数
     * @return Response
     */
	@POST
    @Path("/validateOrderMaxPaid")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response validateOrderMaxPaid(Request<OrderBean> requestParam);

    /**
     * 订单矩阵入库保存
     * @param requestParam 请求参数
     * @return Response
     */
	@POST
    @Path("/saveOrderAndValidateMaxPaid")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response saveOrderAndValidateMaxPaid(Request<OrderBean> requestParam);

    /**
     * 取消订单
     * @param requestParam 请求参数
     * @return Response
     */
	@POST
    @Path("/rejectOrder")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response rejectOrder(Request<OrderBean> requestParam);

    /**
     * 注单结算
     * @param requestParam 请求参数
     * @return Response
     */
	@POST
    @Path("/updateOrderAfterRefund")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response updateOrderAfterRefund(Request<SettleItem> requestParam);

    /**
     * 获取用户投注限额 上限 参考值
     *
     * @return
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<UserLimitReferenceResVo> getUserLimitReference(Request<Long> request);
}
