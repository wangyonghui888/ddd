package com.panda.sport.rcs.oddin.dubbo;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.third.OddinApiService;
import com.panda.sport.data.rcs.dto.oddin.CancelOrderDto;
import com.panda.sport.data.rcs.dto.oddin.TicketDto;
import com.panda.sport.data.rcs.dto.oddin.TicketResultDto;
import com.panda.sport.data.rcs.vo.oddin.TicketStateVo;
import com.panda.sport.rcs.oddin.service.TicketOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author conway
 * @date 2023/5/16 14:15
 * @description 提供给sdk Dubbo接口
 */
@Component
@Slf4j
@Service(connections = 5, retries = 0, timeout = 3000)
public class OddInApiServiceImpl implements OddinApiService {

    @Resource
    private TicketOrderService ticketOrderService;


    /**
     * 获取最大限额
     *
     * @param requestParam 请求参数
     * @return 最大限额
     */
    @Override
    public Response<Long> getMaxBetAmount(Request<TicketDto> requestParam) {
        Long startTime = System.currentTimeMillis();
        MDC.put("linkId", requestParam.getGlobalId());
        Request<TicketDto> req = new Request();
        req.setData(requestParam.getData());
        /* log.info("::{}::请求数据商{}最大限额收到:{}", .getCustomer().getId(), JSONObject.toJSONString(reqDto));*/
        /* Long maxBetAmount;*/
        TicketStateVo ticketStateVo = null;
        try {
            Response<TicketStateVo> response = ticketOrderService.queryMaxBetMoneyBySelect(req);
            ticketStateVo = response.getData();
        } catch (Exception e) {
            log.error("::{}::请求数据商{}最大限额异常：", req.getData().getCustomer().getId(), e);
            return Response.fail(2000L);
        } finally {
            MDC.remove("linkId");
        }
        log.info("::{}::请求Oddin数据商限额-耗时:{}毫秒,返回:{}", requestParam.getData().getCustomer().getId(), System.currentTimeMillis() - startTime, JSONObject.toJSONString(ticketStateVo));
        return Response.success(ticketStateVo);
    }

    /**
     * 注单
     *
     * @param requestParam
     * @return
     */
    @Override
    public Response saveOrder(Request<TicketDto> requestParam) {
        return ticketOrderService.saveOrder(requestParam);
    }

    /**
     * 撤单接口
     *
     * @param requestParam
     * @return
     */
    @Override
    public Response cancelOrder(Request<CancelOrderDto> requestParam) {
        return ticketOrderService.cancelOrder(requestParam);
    }


    /**
     * 拉单接口
     *既可以单个拉单也可以全量拉单
     * @param ticketRequestDto
     * @return
     */
    @Override
    public Response pullSingle(Request<TicketResultDto> ticketRequestDto) {
        return ticketOrderService.pullSingle(ticketRequestDto);
    }
}
