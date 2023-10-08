package com.panda.sport.sdk.service;

import com.google.inject.Inject;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.sdk.service.impl.RcsPaidConfigServiceImp;

import java.math.BigDecimal;
import java.util.Map;


public abstract class AmountValidateAdapter implements AmountValidate {

	@Inject
	public RcsPaidConfigServiceImp configService;

	private AmountValidate next;

	/*
	 * 获取用户选项在当前维度最大下注金额
	 */
	public Long getUserSelectsMaxBetAmount(ExtendBean order) {
		Long amount = getSurplusAmount(order,null);
		if(amount <= 0 ) return 0l;

		BigDecimal odds = getOrderOdds(order);
		Long maxMoney = new BigDecimal(amount).divide(odds).longValue();

		return maxMoney;
	}

	public BigDecimal getOrderOdds(ExtendBean order) {
//		BigDecimal odds = new BigDecimal(1);
//		for(OrderItem item : order.getItems()) {
//			odds = odds.multiply(new BigDecimal(item.getOddsValue()));
//		}
		return new BigDecimal(order.getOdds());
	}

	/*
	 * 订单做入库计算
	 * 失败 缓存操作需要回滚之前的操作，数据库操作不用处理
	 */
	@Override
	public Boolean saveOrderAndValidate(ExtendBean order, Long[][] rec, Map<String,Object> data) {
		/*//Map<String, Object> data = new HashMap<String, Object>();
		try {
			boolean result = saveOrder(order, rec, data);
			if(!result) {
				//log.warn("超过最大赔付值：class:{},ExtendBean:{}",this.getClass(),order);
				log.warn("超过最大赔付值：超限金额:{},ExtendBean:{}", data ,  order);
				return false;
			}else {
				if(next() != null) {
					result = next().saveOrderAndValidate(order, rec, data);
				if(!result) rollBack(order, rec, data);
				return result;
			}
			}
		}catch (Exception e) {
			log.error(e.getMessage(),e);
			log.error(String.format("requestId:%s,超过最大赔付值：class:{},ExtendBean:{},exception:{}",LogContext.getContext().getRequestId(),this.getClass(),order,e));
			return false;
		}catch (Throwable e) {
			log.error(e.getMessage(),e);
			log.error(String.format("requestId:%s,超过最大赔付值：class:{},ExtendBean:{},exception:{}",LogContext.getContext().getRequestId(),this.getClass(),order,e));
			return false;
		}*/

		return true;
	}

	@Override
	public AmountValidate next() {
		return next;
	}

	@Override
	public void setNext(AmountValidate validate) {
		next = validate;
	}

	/*
	 * 对各个维度做计算操作，做缓存累加或者数据库累加
	 */
	public abstract Boolean saveOrder(ExtendBean order, Long[][] rec, Map<String, Object>  data );

}
