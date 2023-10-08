package com.panda.sport.rcs.gts.service.impl;

import com.panda.sport.data.rcs.api.GtsApiService;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.GtsGetMaxStakeDTO;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.gts.common.Constants;
import com.panda.sport.rcs.gts.service.GtsThirdApiService;
import com.panda.sport.rcs.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.MDC;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/*
 *gTS相关接口
 **/
@Service(connections = 5, retries = 0, timeout = 3000)
@Slf4j
@org.springframework.stereotype.Service
public class GtsServiceImpl implements GtsApiService {


    @Resource
    RedisClient redisClient;

    @Resource
    GtsThirdApiService gtsThirdApiService;

    /**
     * 查询限额
     *
     * @param requestParam
     * @return
     */
    @Override
    public Response<Long> getMaxStake(Request<GtsGetMaxStakeDTO> requestParam) {
        List<ExtendBean> list = requestParam.getData().getExtendBeanList();
        long t1 = System.currentTimeMillis();
        try {
            MDC.put("X-B3-TraceId", requestParam.getGlobalId());
            Long limit = gtsThirdApiService.getLimit(list, list.get(0).getSeriesType());
            return Response.success(getPaMaxAmount(limit, requestParam.getData().getExtendBeanList().get(0).getBusId()));
        } catch (Exception e) {
            log.error("::{}::请求限额异常:{}:{} ", e.getMessage(), e);
            return Response.success(Integer.MAX_VALUE);
        } finally {
            long t2 = System.currentTimeMillis();
            log.info("::{}::请求限额耗时:{} ", requestParam.getGlobalId(), (t2 - t1));
        }
    }

    /**
     * gts占额比例，换算成pa的金额
     *
     * @param amount
     * @return
     */
    private Long getPaMaxAmount(Long amount, String tenantId) {
        RedisClient redisClient = SpringContextUtils.getBeanByClass(RedisClient.class);
        String val = redisClient.get(String.format(Constants.GTS_AMOUNT_RATE, tenantId));
        String linkId = tenantId + amount;
        log.info("::{}::商户比例信息:{}比例{}", linkId, tenantId, val);
        if (StringUtils.isBlank(val)) {
            val = redisClient.get(Constants.GTS_AMOUNT_RATE_ALL);
        }
        if (StringUtils.isBlank(val)) {
            val = "1";
        }
        return new BigDecimal(String.valueOf(amount)).divide(new BigDecimal(val), 2, RoundingMode.FLOOR).longValue();
    }


}

