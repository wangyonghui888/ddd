package com.panda.sport.data.rcs.api.credit;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.credit.CreditConfigDto;
import com.panda.sport.data.rcs.dto.credit.CreditConfigSaveDto;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用玩法额度管控 api dubbo 接口
 * @Author : Paca
 * @Date : 2021-05-01 13:12
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface CreditLimitApiService {

    /**
     * 查询信用网限额配置
     *
     * @return
     */
    @POST
    @Path("/queryCreditLimitConfig")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<CreditConfigDto> queryCreditLimitConfig(Request<CreditConfigDto> request);

    /**
     * 保存或更新信用限额配置
     *
     * @param request
     * @return
     */
    @POST
    @Path("/saveOrUpdateCreditLimitConfig")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<Boolean> saveOrUpdateCreditLimitConfig(Request<CreditConfigSaveDto> request);

    /**
     * 用户点击投注选项返回当前选项最大可投注金额
     *
     * @param requestParam 请求参数
     * @return Response
     */
    @POST
    @Path("/queryMaxBetMoneyBySelect")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response queryMaxBetMoneyBySelect(Request<OrderBean> requestParam);

    /**
     * 订单矩阵入库保存
     *
     * @param requestParam 请求参数
     * @return Response
     */
    @POST
    @Path("/saveOrderAndValidateMaxPaid")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response saveOrderAndValidateMaxPaid(Request<OrderBean> requestParam);
}
