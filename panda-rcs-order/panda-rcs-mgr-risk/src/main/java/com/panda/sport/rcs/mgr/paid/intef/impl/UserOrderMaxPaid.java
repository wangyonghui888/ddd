package com.panda.sport.rcs.mgr.paid.intef.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.pojo.RcsBusinessPlayPaidConfig;
import com.panda.sport.rcs.mgr.paid.annotion.Order;
import com.panda.sport.rcs.mgr.paid.intef.AmountValidateAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/*
 * 用户最高单注赔付
 */
@Order(value = 0)
@Slf4j
public class UserOrderMaxPaid extends AmountValidateAdapter{
    
    @Value("${paid.single.min}")
    private long singleMinAmount;
    
    @Value("${paid.multi.min}")
    private long multiMinAmount;

	@Override
	public Long getSurplusAmount(ExtendBean bean, Long[][] rec) {
		RcsBusinessPlayPaidConfig config = getPlayConfig(bean);
		log.info("::{}:: rcs config is:{}",bean.getOrderId(), JSONObject.toJSONString(config));
		if(config == null ) {
			log.warn("::{}:: requestId:{},赔付校验配置获取失败，ExtendBean:{}",bean.getOrderId(),LogContext.getContext().getRequestId(),JSONObject.toJSONString(bean));
			return -1L;
		}
		log.info("::{}:: requestId:{},UserOrderMaxPaid, getOrderMaxPay:{},getCurrentMaxPaid:{}",bean.getOrderId(),LogContext.getContext().getRequestId(),config.getOrderMaxPay().longValue(),bean.getCurrentMaxPaid());

		//订单金额为空的时候，返回最大投注金额
		if(bean.getOrderMoney() == null)
		{
			return config.getOrderMaxPay().longValue();
		}

		return config.getOrderMaxPay().longValue() - bean.getCurrentMaxPaid();
	}
	
	private RcsBusinessPlayPaidConfig getPlayConfig(ExtendBean order) {
		RcsBusinessPlayPaidConfig playConfig = order.getPlayConfig();
		if(playConfig == null ) {
			playConfig = configService.getPlayPaidConfig(order.getBusId(), order.getSportId(), 
					order.getIsScroll(), order.getPlayType(), order.getPlayId());
			if(playConfig == null ) return null;
			order.setPlayConfig(playConfig);
		}
		return playConfig;
	}

	@Override
	public Boolean saveOrder(ExtendBean order, Long[][] rec, Map<String, Object> data) {
		if(order.getSeriesType() == 0 && order.getOrderMoney() < singleMinAmount) {
			log.warn("::{}::requestId:{},赔付校验单关投注校验失败，OrderMoney:{},singleMinAmount:{}",order.getOrderId(),LogContext.getContext().getRequestId(),order.getOrderMoney(),singleMinAmount);
			return false;
		}else {
			if(order.getSeriesType() == 1 && order.getOrderMoney() < multiMinAmount) {
				log.warn("::{}::requestId:{},赔付校验串关投注校验失败，OrderMoney:{},singleMinAmount:{}",order.getOrderId(),LogContext.getContext().getRequestId(),order.getOrderMoney(),multiMinAmount);
				return false;
			}
		}
		Long amount = getSurplusAmount(order, rec);

		data.put("addVal", amount);
		data.put("type", "用户最高单注赔付");
		
		if(amount < 0 ) {
			return false;
		}else {
			return true;
		}
	}

}
