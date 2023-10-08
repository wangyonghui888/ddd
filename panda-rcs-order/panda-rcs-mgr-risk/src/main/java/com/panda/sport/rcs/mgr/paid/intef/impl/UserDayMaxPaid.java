package com.panda.sport.rcs.mgr.paid.intef.impl;

import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.pojo.RcsBusinessUserPaidConfig;
import com.panda.sport.rcs.mgr.paid.annotion.Order;
import com.panda.sport.rcs.mgr.paid.intef.AmountValidateAdapter;
import com.panda.sport.rcs.mgr.wrapper.impl.RcsRectanglePlayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/*
 * 用户最高当日赔付
 */
@Order(value = 4)
@Slf4j
public class UserDayMaxPaid extends AmountValidateAdapter{
	
    @Autowired
    private RedisClient redisClient;

	@Autowired
	private RcsRectanglePlayServiceImpl playService;
	
	@Override
	public Long getSurplusAmount(ExtendBean order, Long[][] rec) {
		return 0L;
	}
	
	private RcsBusinessUserPaidConfig getUserConfig(ExtendBean order) {
		RcsBusinessUserPaidConfig userConfig = order.getUserConfig();
		if(userConfig == null ) {
			userConfig = configService.getUserPaidConfig(order.getBusId(), order.getUserId());
			if(userConfig == null ) return null;
			order.setUserConfig(userConfig); 
		}
		return userConfig;
	}

	@Override
	public Boolean saveOrder(ExtendBean order, Long[][] rec, Map<String, Object> data) {
		Long amount=getSurplusAmount(order, rec);

		data.put("addVal", amount);
		data.put("type", "用户最高当日赔付");

		if(amount < 0) {
			return false;
		}
		else {
			return true;
		}
	}
	
}
