package com.panda.rcs.pending.order.controller;

import com.panda.rcs.pending.order.service.IOpenOrderAllPlaysService;
import com.panda.rcs.pending.order.service.impl.OpenOrderAllPlaysServiceImpl;
import com.panda.rcs.pending.order.service.impl.ReserveBetApiImpl;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.PendingOrderDto;
import com.panda.sport.rcs.vo.HttpResponse;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 预约订单controller测试类
 */
@Slf4j
@RestController
@Api(value = "预约订单controller")
@RequestMapping(value = "/pendingOrder")
public class PendingOrderTestController {

    @Autowired
    private ReserveBetApiImpl reserveBetApi;
    @Autowired
    private IOpenOrderAllPlaysService openOrderAllPlaysService;


    /**
     * 预约订单校验
     */
    @ResponseBody
    @ApiOperation("预约订单校验")
    @RequestMapping(value = "/queryMaxBetAmountByOrder")
    public Response<?> queryMaxBetAmountByOrder(@RequestBody Request<PendingOrderDto> requestParam) {
        return reserveBetApi.queryMaxBetAmountByOrder(requestParam);
    }

    /**
     * 预约订单入库
     */
    @ResponseBody
    @ApiOperation("预约订单入库")
    @RequestMapping(value = "/checkAndSavePendingOrder")
    public Response<?> checkAndSavePendingOrder(@RequestBody Request<PendingOrderDto> requestParam) {
        return reserveBetApi.saveOrderCheckAmount(requestParam);
    }

    /**
     * 关闭预约订单
     */
    @ResponseBody
    @ApiOperation("关闭预约订单")
    @RequestMapping(value = "/cancelOrder")
    public Response<?> cancelOrder(@RequestBody Request<PendingOrderDto> requestParam) {
        return reserveBetApi.cancelOrder(requestParam);
    }

    /**
     * 关闭预约订单
     */
    @ResponseBody
    @ApiOperation("开启预约投注早盘所有玩法")
    @GetMapping(value = "/openOrderPreAllPlays")
    public HttpResponse<?> openOrderAllPlays() {
        return openOrderAllPlaysService.openOrderPreAllPlays();
    }
}
