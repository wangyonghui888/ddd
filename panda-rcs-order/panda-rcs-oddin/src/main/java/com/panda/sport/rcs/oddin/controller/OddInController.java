package com.panda.sport.rcs.oddin.controller;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.oddin.CancelOrderDto;
import com.panda.sport.data.rcs.dto.oddin.TicketDto;
import com.panda.sport.data.rcs.dto.oddin.TicketResultDto;
import com.panda.sport.rcs.oddin.service.TicketOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author :wiker
 * @Date: 2023-19 15:37
 * oddIn第三方数据商提供给电竞的相关接口
 **/
@Slf4j
@RestController
@RequestMapping("/v1/oddin")
@Api(value = "提供给电竞请求oddIn数据商接口")
public class OddInController {
    @Resource
    private TicketOrderService ticketOrderService;

    /**
     * 限额接口
     *
     * @param requestParam
     * @return
     */
    @PostMapping(value = "/ticketMaxStake")
    @ResponseBody
    @ApiOperation(value = "限额请求接口")
    @ApiParam(name = "TicketDto", value = "requestParam入参")
    public Response queryMaxBetMoneyBySelect(@RequestBody Request<TicketDto> requestParam) {
        return ticketOrderService.queryMaxBetMoneyBySelect(requestParam);
    }


    /**
     * 注单接口
     *
     * @param requestParam
     * @return
     */
    @PostMapping(value = "/saveOrder")
    @ResponseBody
    @ApiOperation(value = "注单请求接口")
    @ApiParam(name = "TicketDto", value = "requestParam入参")
    public Response saveOrder(@RequestBody Request<TicketDto> requestParam) {
        return ticketOrderService.saveOrder(requestParam);

    }

    /**
     * 撤单接口
     *
     * @param requestParam
     * @return
     */
    @PostMapping(value = "/cancelOrder")
    @ResponseBody
    @ApiOperation(value = "撤单接口")
    @ApiParam(name = "TicketDto", value = "requestParam入参")
    public Response cancelOrder(@RequestBody Request<CancelOrderDto> requestParam) {
        return ticketOrderService.cancelOrder(requestParam);
    }


    /**
     * 单个拉单接口(目前只针对注单号拉取注单信息)
     *
     * @param ticketRequestDto
     * @return
     */
    @PostMapping(value = "/pullSingle")
    @ResponseBody
    @ApiOperation(value = "电竞单个拉单接口")
    @ApiParam(name = "TicketDto", value = "ticketRequestDto入参")
    public Response pullSingle(@RequestBody Request<TicketResultDto> ticketRequestDto) {
        return ticketOrderService.pullSingle(ticketRequestDto);

    }

    /**
     * 全量拉单接口(可以根据时间段进行全量拉单)
     *
     * @param ticketRequestDto
     * @return
     */
    @PostMapping(value = "/pullSingleTime")
    @ResponseBody
    @ApiOperation(value = "大数据全量拉单接口")
    @ApiParam(name = "TicketDto", value = "ticketRequestDto入参")
    public Response pullSingleTime(@RequestBody Request<TicketResultDto> ticketRequestDto) {
        return ticketOrderService.pullSingleTime(ticketRequestDto);

    }
}
