package com.panda.sport.rcs.mgr.paid.intef.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.pojo.RcsBusinessPlayPaidConfig;
import com.panda.sport.rcs.mgr.paid.annotion.Order;
import com.panda.sport.rcs.mgr.paid.intef.AmountValidateAdapter;
import com.panda.sport.rcs.mgr.wrapper.impl.RcsRectanglePlayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;

/*
 * 用户最高玩法赔付
 */
@Slf4j
@Order(value = 2)
public class UserPlayMaxPaid extends AmountValidateAdapter {

	@Autowired
	private RcsRectanglePlayServiceImpl playService;

	@Override
	public Long getSurplusAmount(ExtendBean order, Long[][] rec) {
		RcsBusinessPlayPaidConfig config = getPlayConfig(order);
		if (config == null) {
			log.warn("::{}:: requestId:{},赔付校验配置获取失败，ExtendBean:{}",order.getOrderId(),LogContext.getContext().getRequestId(), JSONObject.toJSONString(order));
			return -1L;
		}

		order.setCurrentPlayType(Integer.parseInt(String.valueOf(config.getPlayId())));

		//获取当前玩法合并之后最大赔付
		Long currentPaid = playService.queryPlayCurrentPaid(order);
		BigDecimal playMaxPay = new BigDecimal(config.getPlayMaxPay());

		log.info("::{}:: requestId:{},UserPlayMaxPaid, currentPaid:{},playMaxPay:{}",order.getOrderId(),LogContext.getContext().getRequestId(), currentPaid, playMaxPay);

		return playMaxPay.subtract(new BigDecimal(currentPaid)).longValue();
	}

	public RcsBusinessPlayPaidConfig getPlayConfig(ExtendBean order) {
		RcsBusinessPlayPaidConfig playConfig = order.getPlayConfig();
		if (playConfig == null) {
			playConfig = configService.getPlayPaidConfig(order.getBusId(), order.getSportId(),
					order.getIsScroll(), order.getPlayType(), order.getPlayId());
			if (playConfig == null) return null;
			order.setPlayConfig(playConfig);
		}
		return playConfig;
	}

	@Override
	public Boolean saveOrder(ExtendBean order, Long[][] rec, Map<String, Object> data) {
		Long amount = getSurplusAmount(order, rec);
		data.put("addVal", amount);
		data.put("type", "用户最高玩法赔付");
		if (amount < 0) {
			log.warn("requestId:{},用户玩法维度最校验失败，ExtendBean:{}", LogContext.getContext().getRequestId(), order);
			return false;
		}

		return true;
	}
}
