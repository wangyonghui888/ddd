package com.panda.sport.rcs.mgr.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.order.RiskApiService;
import com.panda.sport.data.rcs.dto.order.OrderBeforeHandItemVo;
import com.panda.sport.data.rcs.dto.order.OrderBeforeHandReqVo;
import com.panda.sport.data.rcs.dto.order.OrderBeforeHandResVo;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.TOrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description dubbo api
 * @Param
 * @Author lithan
 * @Date 2021年2月21日20:08:06
 * @return
 **/
@Service(connections = 5, retries = 0)
@Slf4j
@org.springframework.stereotype.Service
@Path("")
public class RiskApiServiceImpl implements RiskApiService {

    @Autowired
    TOrderDetailMapper orderDetailMapper;

    @Autowired
    StandardSportMarketMapper standardSportMarketMapper;

    /**
     * 订单提前结算检查
     *
     * @param requestParam
     * @return
     */
    @Override
    @POST
    @Path("orderBeforeHand")
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Trace
    public Response<OrderBeforeHandResVo> orderBeforeHand(Request<OrderBeforeHandReqVo> requestParam) {
        log.info("订单提前结算检查处理开始:{}",JSONObject.toJSONString(requestParam));
        try {
            OrderBeforeHandResVo resVo = new OrderBeforeHandResVo();
            List<OrderBeforeHandItemVo> itemVoList = new ArrayList<>();
            String orderNo = requestParam.getData().getOrderNo();
            LambdaUpdateWrapper<TOrderDetail> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(TOrderDetail::getOrderNo, orderNo);
            List<TOrderDetail> orderLits = orderDetailMapper.selectList(wrapper);
            if(orderLits.size()==0){
                throw new RcsServiceException("订单不存在");
            }
            for (TOrderDetail detail : orderLits) {
                OrderBeforeHandItemVo itemVo = new OrderBeforeHandItemVo();
                itemVo.setBetNo(detail.getBetNo());
                itemVo.setMarketId(detail.getMarketId());
                itemVo.setOdds(new BigDecimal(detail.getOddsValue().toString()).multiply(new BigDecimal("100000")));
                itemVo.setPlayOptionsId(detail.getPlayOptionsId());
                //盘口状态0-5. 0:active, 1:suspended, 2:deactivated, 3:settled, 4:cancelled, 5:handedOver
                StandardSportMarket market = standardSportMarketMapper.selectById(detail.getMarketId());
                itemVo.setMarketStatus(market.getStatus());
                itemVoList.add(itemVo);
            }
            resVo.setOrderNo(orderNo);
            resVo.setIsPreSettle(true);
            resVo.setList(itemVoList);
            log.info("::{}::订单提前结算检查处理完成:{}", orderNo, JSONObject.toJSONString(resVo));
            return Response.success(resVo);
        } catch (Exception e) {
            log.error("::{}::订单提前结算检查处理异常:{}:{}",requestParam.getData().getOrderNo(),e.getMessage(), e);
            return Response.error(500, "订单提前结算检查异常:" + e.getMessage());
        }
    }
}

