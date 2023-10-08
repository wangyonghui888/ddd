package com.panda.sport.rcs.mts.sportradar.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.panda.sport.data.rcs.api.MtsApiService;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.MtsgGetMaxStakeDTO;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketOddsMapper;
import com.panda.sport.rcs.mapper.StandardSportTournamentMapper;
import com.panda.sport.rcs.mts.sportradar.builder.MtsMaxStakeDefaultUtils;
import com.panda.sport.rcs.mts.sportradar.builder.MtsSdkInit;
import com.panda.sport.rcs.mts.sportradar.builder.TicketBuilderHelper;
import com.panda.sport.rcs.mts.sportradar.wrapper.MtsCommonService;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.sportradar.mts.sdk.api.Ticket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

/*
 *MTS相关接口
 **/
@Service(connections = 5, retries = 0, timeout = 3000)
@Slf4j
@org.springframework.stereotype.Service
public class MtsServiceImpl implements MtsApiService {

    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    StandardSportTournamentMapper standardSportTournamentMapper;
    @Autowired
    StandardSportMarketMapper standardSportMarketMapper;
    @Autowired
    StandardSportMarketOddsMapper standardSportMarketOddsMapper;

    @Autowired
    MtsCommonService mtsCommonService;

    @Autowired
    RedisClient redisClient;

    /**
     * 查询串关最大限额
     *
     * @param requestParam
     * @return
     */
    @Override
    public Response<Long> getMaxStake(Request<MtsgGetMaxStakeDTO> requestParam) {
        StopWatch sw = new StopWatch();
        sw.start();
        try {
        	MDC.put("X-B3-TraceId", requestParam.getGlobalId());
        	
        	mtsCommonService.convertAllParam(requestParam.getData().getExtendBeanList());
            MtsgGetMaxStakeDTO mtsDTO = requestParam.getData();
            TicketBuilderHelper ticketBuilderHelper = MtsSdkInit.getTicketBuilderHelper();
            Ticket ticket = ticketBuilderHelper.getSeriesTickets(mtsDTO.getExtendBeanList(), mtsDTO.getN(), mtsDTO.getFlag());
            log.info("::{}::请求ticket:{} ",requestParam.getGlobalId(), JSONObject.toJSONString(ticket, SerializerFeature.DisableCircularReferenceDetect));
            
            long amount = MtsSdkInit.getMtsSdk().getClientApi().getMaxStake(ticket) / 10000;
            log.info("::{}::请求getMaxStake返回:{} ",requestParam.getGlobalId(), amount);
            return Response.success(getPaMaxAmount(amount, requestParam.getData().getExtendBeanList().get(0).getBusId()));
        } catch (RcsServiceException e) {
        	Long defaultStake = MtsMaxStakeDefaultUtils.getMaxStakeDefault();
        	log.warn("::{}::获取最大最小值异常，获取默认值：{}，default:{}",requestParam.getGlobalId(),JSONObject.toJSONString(requestParam),defaultStake);
        	return Response.success(defaultStake);
        } catch (Exception e) {
        	Long defaultStake = MtsMaxStakeDefaultUtils.getMaxStakeDefault();
        	log.error(e.getMessage() + ",默认值：" + defaultStake,e);
        	return Response.success(defaultStake);
        }finally {
            sw.stop();
            log.info("::{}::请求串关耗时:{} ", requestParam.getGlobalId(), sw.getTotalTimeMillis());
        }
    }

    /**
     * 查询单注最大下注额
     *
     * @param requestParam
     * @return
     */
    @Override
    public Response<Long> getSingleMaxStake(Request<ExtendBean> requestParam) {
        StopWatch sw = new StopWatch();
        sw.start();
        try {
        	MDC.put("X-B3-TraceId", requestParam.getGlobalId());

        	mtsCommonService.convertSingleParam(requestParam.getData());
            TicketBuilderHelper ticketBuilderHelper = MtsSdkInit.getTicketBuilderHelper();
            Ticket ticket = ticketBuilderHelper.getMaxAmountTicket(requestParam.getData());
            log.info("::{}::请求ticket:{} ",requestParam.getGlobalId(), JSONObject.toJSONString(ticket, SerializerFeature.DisableCircularReferenceDetect));

            long amount = MtsSdkInit.getMtsSdk().getClientApi().getMaxStake(ticket) / 10000;
            log.info("::{}::请求getMaxStake返回:{} ",requestParam.getGlobalId(), amount);
            return Response.success(getPaMaxAmount(amount, requestParam.getData().getBusId()));
        } catch (RcsServiceException e) {
        	Long defaultStake = MtsMaxStakeDefaultUtils.getMaxStakeDefault();
        	log.warn("::{}::获取最大最小值异常，获取默认值：{}，default:{}",requestParam.getGlobalId(),JSONObject.toJSONString(requestParam),defaultStake);
        	return Response.success(defaultStake);
        } catch (Exception e) {
        	Long defaultStake = MtsMaxStakeDefaultUtils.getMaxStakeDefault();
        	log.error(e.getMessage() + ",默认值：" + defaultStake,e);
        	return Response.success(defaultStake);
        }finally {
            sw.stop();
            log.info("::{}::请求Single耗时:{} ", requestParam.getGlobalId(), sw.getTotalTimeMillis());
        }
    }

    /**
     *  如果mts查询失败返回一个默认值 通过redis可配置
     */
    private Long getMtsDefaultAmount() {
        String key = "rcs:mts:default_max_stake";
        String defaultAmount = redisClient.get(key);
        if(StringUtils.isEmpty(defaultAmount)){
            return 0L;
        }
        return NumberUtils.toLong(defaultAmount);
    }

    /**
     * mts占额比例，换算成pa的金额
     * @param mtsAmount
     * @return
     */
    private Long getPaMaxAmount(Long mtsAmount,String tenantId) {
    	RedisClient redisClient = SpringContextUtils.getBeanByClass(RedisClient.class);
		String val = redisClient.get(String.format(TicketBuilderHelper.MTS_AMOUNT_RATE, tenantId));
		String linkId = tenantId + mtsAmount;
        log.info("::{}::商户比例信息:{}比例{}", linkId,tenantId, val);
        if(StringUtils.isBlank(val)){
            val = redisClient.get(TicketBuilderHelper.MTS_AMOUNT_RATE_ALL);
        }
    	if(StringUtils.isBlank(val)){
    	    val = "1";
        }
    	
    	
    	return new BigDecimal(String.valueOf(mtsAmount)).divide(new BigDecimal(val),2,RoundingMode.FLOOR).longValue();
    }


}

