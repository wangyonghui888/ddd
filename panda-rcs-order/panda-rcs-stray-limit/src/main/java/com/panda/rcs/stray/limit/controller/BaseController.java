package com.panda.rcs.stray.limit.controller;

import com.panda.rcs.stray.limit.service.BaseService;
import com.panda.rcs.stray.limit.service.OrderLimitNewVersionApi;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.vo.HttpResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * @author :  myname
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.rcs.stray.limit.controller
 * @Description :  TODO
 * @Date: 2022-03-26 19:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@RestController
@RequestMapping(value = "/rcsStrayLimit")
@Api(tags = "公共服务模块")
public class BaseController {
    private final BaseService baseService;
   // private final OrderLimitNewVersionApi orderLimitNewVersionApi;

    public BaseController(BaseService baseService
    ) {
        this.baseService = baseService;
    }

    @GetMapping(value = "/queryBusinessSwitch")
    @ApiOperation(value = "查询商户串关模式开关", notes = "查询商户串关模式开关")
    @ApiImplicitParam(name = "businessId", value = "商户ID", required = true, dataType = "String")
    public HttpResponse queryBusinessSwitch(String businessId) {
        return HttpResponse.success(baseService.queryBusinessSwitch(businessId));

    }


//    @PostMapping(value = "/queryMaxBetAmountByOrder")
//    @ApiOperation(value = "2.0串关查询限额接口", notes = "2.0串关查询限额接口")
//    public Response queryMaxBetAmountByOrder(@RequestBody Request<OrderBean> request) {
//        return orderLimitNewVersionApi.queryMaxBetAmountByOrder(request);
//    }
//
//    @PostMapping(value = "/saveOrderCheckAmount")
//    @ApiOperation(value="2.0串关注单入库接口",notes="2.0串关注单入库接口")
//    public Response saveOrderCheckAmount(@RequestBody OrderBean orderBean) {
//        return orderLimitNewVersionApi.saveOrderCheckAmount(orderBean);
//    }

}
