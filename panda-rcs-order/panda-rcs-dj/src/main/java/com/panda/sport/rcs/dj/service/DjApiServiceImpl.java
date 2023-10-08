package com.panda.sport.rcs.dj.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.dj.DjApiService;
import com.panda.sport.data.rcs.dto.dj.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName DjApiServiceImpl
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/14 18:35
 * @Version 1.0
 **/
@Service(connections = 5, retries = 0, timeout = 3000,validation = "true")
@Slf4j
@org.springframework.stereotype.Service
public class DjApiServiceImpl implements DjApiService {

    @Autowired
    private ParamCheckUtilService paramCheckUtilService;

    @Autowired
    private DjServiceImpl djService;


    @Override
    public Response<List<DJAmountLimitResVo>> getBetAmountLimit(Request<DJLimitAmoutRequest> request) {
        try {
            MDC.put("X-B3-TraceId", request.getGlobalId());
            log.info("::{}::查询限额开始:{}",request.getGlobalId(), JSONObject.toJSONString(request));

            //DJLimitAmoutRequest reqVo = JSONObject.parseObject(JSON.toJSONString(request.getData()),DJLimitAmoutRequest.class);
            DJLimitAmoutRequest reqVo = request.getData();

            //校验请求参数
            paramCheckUtilService.checkDJLimitAmoutParam(reqVo);

            List<DJAmountLimitResVo> betAmountLimit = djService.getBetAmountLimit(reqVo);

            log.info("::{}::电竞限额查询返回结果:{}", request.getGlobalId(),JSON.toJSONString(betAmountLimit));
            return Response.success(betAmountLimit);
        } catch (Exception e) {
            log.error("::{}::查询限额异常:{},{}",request.getGlobalId(), e.getMessage(), e);
            return Response.error(-1, "查询限额失败:" + e.getMessage());
        }
    }

    @Override
    public Response<DJAmountLimitResVo> getBetAmountLimitOld(Request<DJLimitAmoutRequest> request) {
        try {
            MDC.put("X-B3-TraceId", request.getGlobalId());
            log.info("::{}::查询限额开始:{}",request.getGlobalId(), JSONObject.toJSONString(request));

            DJLimitAmoutRequest reqVo = request.getData();

            //校验请求参数
            paramCheckUtilService.checkDJLimitAmoutParam(reqVo);

            DJAmountLimitResVo djAmountLimitResVo = djService.getBetAmountLimitOld(reqVo);

            log.info("::{}::电竞限额查询返回结果:{}", request.getGlobalId(),JSON.toJSONString(djAmountLimitResVo));
            return Response.success(djAmountLimitResVo);
        } catch (Exception e) {
            log.error("::{}::查询限额异常:{},{}",request.getGlobalId(), e.getMessage(), e);
            return Response.error(-1, "查询限额失败:" + e.getMessage());
        }
    }

    @Override
    public Response<DJBetResVo> bet(Request<DJBetReqVo> request) {
        try {
            MDC.put("X-B3-TraceId", request.getGlobalId());
            log.info("::{}::电竞投注开始:{}", request.getGlobalId(),JSONObject.toJSONString(request));

            DJBetReqVo reqVo = request.getData();

            //校验请求参数
            paramCheckUtilService.checkDJBetParam(reqVo);

            Response<DJBetResVo> response = djService.djBet(reqVo);

            log.info("::{}::电竞投注返回结果:{}",reqVo.getOrderNo(),JSON.toJSONString(response));

            return response;
        } catch (Exception e) {
            log.error("::{}::电竞投注异常:{},{}",request.getData().getOrderNo(), e.getMessage(), e);
            DJBetResVo djBetResVo = new DJBetResVo(request.getData().getOrderNo(), 0, null, null);
            return Response.error(-1, "风控拒单中", djBetResVo);
        }
    }

    @Override
    public Response<DJCancelOrderResVo> cancelOrder(Request<DjCancelOrderReqVo> request) {
        try {
            MDC.put("X-B3-TraceId", request.getGlobalId());
            log.info("::{}::电竞取消注单开始:{}",request.getGlobalId(),JSONObject.toJSONString(request));

            DjCancelOrderReqVo reqVo = request.getData();

            //校验请求参数
            paramCheckUtilService.checkDJCancelParam(reqVo);

            Response<DJCancelOrderResVo> response = djService.cancelOrder(reqVo);

            log.info("::{}::电竞取消注单返回结果:{}", request.getGlobalId(),JSON.toJSONString(response));

            return response;
        } catch (Exception e) {
            log.error("::{}::电竞体育取消注单异常:{},{}", request.getGlobalId(),e.getMessage(), e);
            return Response.error(-1, "取消注单失败:" + e.getMessage());
        }
    }


}
