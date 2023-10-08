package com.panda.sport.rcs.mgr.paid;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;

import com.panda.sport.rcs.mgr.service.settle.ITSettleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.mgr.wrapper.RcsPaidConfigService;
import com.panda.sport.rcs.mgr.wrapper.RcsTournamentOperateMarketService;
import com.panda.sport.rcs.mgr.wrapper.impl.RcsPaidConfigServiceImp;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsBusinessDayPaidConfig;
import com.panda.sport.rcs.pojo.RcsBusinessSingleBetConfig;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaidService {

    @Autowired
    private RcsPaidConfigService rcsPaidConfigService;
	@Autowired
	public RcsPaidConfigServiceImp configService;
	@Autowired
	ITSettleService settleService;
	@Autowired
	private RcsTournamentOperateMarketService rcsTournamentOperateMarketService;
	@Autowired
	private ProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    private LuaPaidService luaPaidService;
	@Autowired
	private RedisClient redisClient;


	@PostConstruct
	//@Scheduled(cron="0/30 * *  * * ? ")   //每30秒执行一次
    public void initConfigCache() {
        rcsPaidConfigService.initConfigCache();
		rcsPaidConfigService.sendCacheConfigMQ();

		//Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> initConfigCache(), 30, 30, TimeUnit.SECONDS);
    }

    /**
     * 获取当前用户在当前选项下剩余最大投注金额
     */
    public Long getUserSelectsMaxBetAmount(ExtendBean order, Long[][] rec) {
    	Long minAmount = Long.MAX_VALUE;
    	Long singleOrderMaxBet = rcsTournamentOperateMarketService.queryMatchAndTournamentMaxBetAmount(order);
    	minAmount = Math.min(minAmount, singleOrderMaxBet);

		// 商户维度最大限额效验
		Long busAmount = queryBusinessMaxPaid(order);
		minAmount = Math.min(minAmount, busAmount);

		// 玩法最大单注限额
		Long singleBetAmount = querySingleBetMaxPaid(order);
		minAmount = Math.min(minAmount, singleBetAmount);
    	
		if(minAmount <= 0 ){
			return 0L;
		}
		
		Long redisMaxAmount = luaPaidService.getUserSelectsMaxBetAmount(order, rec);

		//各维度限额最大赔付需要除以赔率
		redisMaxAmount = new BigDecimal(redisMaxAmount).divide(new BigDecimal(order.getOdds()).subtract(new BigDecimal("1")),0,BigDecimal.ROUND_DOWN).longValue();
		minAmount = Math.min(minAmount, redisMaxAmount);

		log.info("::{}::查询用户下注最大限额,联赛和赛事的投注限额:{},商户维度最大限额:{},玩法最大单注限额:{},redisMaxAmount:{},minAmount:{}",order.getOrderId(),singleOrderMaxBet
		,busAmount,singleBetAmount,redisMaxAmount,minAmount);
    	
        return minAmount;
    }
  

    /**
     * @Description  查询商户维度下注最大限额
     * @Param [order]
     * @Author  max
     * @Date  14:01 2019/12/16
     * @return void
     **/
    private Long queryBusinessMaxPaid(ExtendBean order){
		String stopKey = String.format(RedisKeys.PAID_DATE_BUS_STOP_REDIS_CACHE, order.getDateExpect(), order.getBusId());
		if("1".equals(String.valueOf(redisClient.get(stopKey)))) {
			//log.warn(String.format("requestId:%s,当前商户已经维护，不在收单:%s", LogContext.getContext().getRequestId(),stopKey));
			log.warn("::{}::requestId:{},当前商户已经维护，不在收单:{}", order.getOrderId(),LogContext.getContext().getRequestId(),stopKey);
			return -1L;
		}

		//获取配置数据
		RcsBusinessDayPaidConfig config = configService.getDayPaidConfig(order.getBusId());
		//数据库没有配置数据，返回0
		if (config == null ) {
			//log.warn(String.format("requestId:%s,没有找到商户id:%s单日最大赔付的配置项",LogContext.getContext().getRequestId(),order.getBusId()));
			log.warn("::{}::requestId:{},没有找到商户id:{}单日最大赔付的配置项",order.getOrderId(),LogContext.getContext().getRequestId(),order.getBusId());
			return -1L;
		}
		//状态（1-高危 2-危险 3-正常 4-健康 5-满负荷制动 6-手动制动 7-已停用）
		if(config.getStatus() == 7) {
			//log.warn(String.format("requestId:%s,商户id:%s已经停止接单",LogContext.getContext().getRequestId(),order.getBusId()));
			log.warn("::{}::requestId:{},商户id:{}已经停止接单",order.getOrderId(),LogContext.getContext().getRequestId(),order.getBusId());
			return -1L;
		}else if(config.getStatus() == 6 && config.getExpireTime() > System.currentTimeMillis() ) {
			//手动制动需要判断过期时间
			//log.warn(String.format("requestId:%s,商户id:%s当前是手动制动状态，已经停止接单",LogContext.getContext().getRequestId(),order.getBusId()));
			log.warn(String.format("::{}::requestId:{},商户id:{}当前是手动制动状态，已经停止接单",order.getOrderId(),LogContext.getContext().getRequestId(),order.getBusId()));
			return -1L;
		}

		//计算配置中最大数据
		BigDecimal maxMoneySet = BigDecimal.ZERO;
		//获取缓存值
		String cacheValue = redisClient.get(String.format(RedisKeys.PAID_DATE_BUS_REDIS_CACHE,order.getDateExpect(), order.getBusId()));
		if(StringUtils.isBlank(cacheValue)){
			cacheValue = "0";
		}

		//商户维度不计算预期赔付值
		maxMoneySet = config.getStopVal().subtract(new BigDecimal(cacheValue));

		log.info("::{}::商户维度配置:{},maxMoneySet:{}",order.getOrderId(),config.getStopVal(),order,maxMoneySet.longValue());
		return maxMoneySet.longValue();
	}

	/**
	 * @Description  用户下单个维度额度效验
	 * @Param [order, rec]
	 * @Author  max
	 * @Date  14:24 2019/12/16
	 * @return java.util.Map<java.lang.String,java.lang.Object>
	 **/
	public Boolean saveOrderAndValidateV2(ExtendBean order, String  rec){
		return true;
	}

	/**
	 * @Description
	 * @Param [order]
	 * @Author  max 查询 玩法最大单注限额
	 * @Date  14:04 2019/12/16
	 * @return java.lang.Long
	 **/
	private Long querySingleBetMaxPaid(ExtendBean order){
		RcsBusinessSingleBetConfig singleBetConfig = configService.getSingleBetConfig(order.getBusId(),order.getSportId(),order.getIsScroll(),order.getPlayType(),order.getPlayId(), order.getTournamentLevel().toString());
		if (singleBetConfig == null) {
			log.warn("::{}::requestId:{},单关校验获取失败，ExtendBean:{}", order.getOrderId(),LogContext.getContext().getRequestId(), order);
			throw new RcsServiceException("联赛玩法级别获取配置失败");
		}
		//获取当前玩法合并之后最大赔付
		BigDecimal orderMaxValue = singleBetConfig.getOrderMaxValue().divide(new BigDecimal(100));
		if(order.getCurrentMaxPaid() == null ){
			return orderMaxValue.longValue();
		}
		log.info("::{}::orderMaxValue{}, currentPaid:{}",order.getOrderId(),orderMaxValue,order.getCurrentMaxPaid());
		return orderMaxValue.subtract(new BigDecimal(String.valueOf(order.getCurrentMaxPaid()/100))).longValue();
	}


	public void prizeHandle(SettleItem settleItem, ExtendBean extendBean) {
		luaPaidService.prizeHandle(extendBean,settleItem);
	}

}
