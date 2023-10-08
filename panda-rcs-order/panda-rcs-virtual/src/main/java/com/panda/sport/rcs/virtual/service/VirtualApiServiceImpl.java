package com.panda.sport.rcs.virtual.service;


import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.virtual.VirtualApiService;
import com.panda.sport.data.rcs.dto.virtual.BetAmountLimitReqVo;
import com.panda.sport.data.rcs.dto.virtual.BetAmountLimitResVo;
import com.panda.sport.data.rcs.dto.virtual.BetReqVo;
import com.panda.sport.data.rcs.dto.virtual.BetResVo;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.panda.sport.rcs.virtual.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


/**
 * @Description 虚拟赛事api
 * @Param
 * @Author lithan
 * @Date 2020-09-13 16:11:20
 * @return
 **/
@Service(connections = 5, retries = 0, timeout = 3000)
@Slf4j
@org.springframework.stereotype.Service
public class VirtualApiServiceImpl implements VirtualApiService {

    @Autowired
    VirtualServiceImpl virtualService;

    @Autowired
    RedisClient redisClient;

    /**
     * 获取虚拟赛事  最大最小值限额
     *
     * @param request
     * @return Response
     */
    @Override
    public Response<List<BetAmountLimitResVo>> getBetAmountLimit(Request<BetAmountLimitReqVo> request) {
        String linkId = "getBetAmountLimit";
        try {
            MDC.put("X-B3-TraceId", request.getGlobalId());
            BetAmountLimitReqVo reqVo = request.getData();
            linkId = reqVo.getTenantId()+""+ reqVo.getUserId();
            log.info("::{}::查询限额开始:{}", linkId,JSONObject.toJSONString(request));
            if (reqVo == null || reqVo.getUserId() == null || reqVo.getSeriesType() == null) {
                throw new Exception("参数错误");
            }
            List<BetAmountLimitResVo>  limitResVoList = virtualService.getBetAmountLimit(reqVo);
            //转换比例
            for (BetAmountLimitResVo betAmountLimitResVo : limitResVoList) {
                betAmountLimitResVo.setMaxStake(getPaMaxAmount(betAmountLimitResVo.getMaxStake(), reqVo.getTenantId()));
                betAmountLimitResVo.setMinStake(getPaMaxAmount(betAmountLimitResVo.getMinStake(),reqVo.getTenantId()));
            }
            log.info("::{}::查询限额返回:{}", linkId,JSONObject.toJSONString(limitResVoList));
            return Response.success(limitResVoList);
        } catch (Exception e) {
            BetAmountLimitReqVo reqVo = request.getData();
            if (reqVo != null){
                linkId = reqVo.getTenantId()+""+ reqVo.getUserId();
            }
            log.error("::{}::查询限额异常:{}", linkId,e.getMessage(), e);
            return Response.error(-1, "查询限额失败:" + e.getMessage());
        }
    }

    /**
     * 虚拟赛事占额比例，换算成pa的金额
     *
     * @param amount
     * @return
     */
    private Double getPaMaxAmount(double amount, Long tenantId) {
        String val = redisClient.get(String.format(Constants.VIRTUAL_AMOUNT_RATE, tenantId));
        if (StringUtils.isBlank(val)) {
            val = redisClient.get(Constants.VIRTUAL_AMOUNT_RATE_ALL);
        }
        if (StringUtils.isBlank(val)) {
            val = "0.3";
        }
        return new BigDecimal(String.valueOf(amount)).divide(new BigDecimal(val), 2, RoundingMode.UP).doubleValue();
    }

    /**
     * 获取虚拟赛事 投注 最大最小值限额
     *
     * @param request
     * @return Response
     */
    @Override
    public Response<BetResVo> bet(Request<BetReqVo> request) {
        try {
            MDC.put("X-B3-TraceId", request.getGlobalId());
            StopWatch sw = new StopWatch();
            sw.start();
            log.info("投注开始:{}", JSONObject.toJSONString(request));
            BetResVo resVo = virtualService.bet(request.getData());
            sw.stop();
            log.info("投注返回:{}:耗时{}", JSONObject.toJSONString(resVo), sw.getTotalTimeMillis());
            return Response.success(resVo);
        } catch (Exception e) {
            log.error("投注异常:{}", e.getMessage(), e);
            //返回给业务的时候 0是拒单
            return Response.error(0, "投注失败:" + e.getMessage(), new BetResVo(request.getData().getOrderNo(), 0));
        }
    }
}

