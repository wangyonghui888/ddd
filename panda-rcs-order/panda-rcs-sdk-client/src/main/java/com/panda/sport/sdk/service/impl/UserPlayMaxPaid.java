package com.panda.sport.sdk.service.impl;


import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.pojo.RcsBusinessPlayPaidConfig;
import com.panda.sport.sdk.service.AmountValidateAdapter;

import java.util.Map;

/*
 * 用户最高玩法赔付
 */
public class UserPlayMaxPaid extends AmountValidateAdapter {

	@Override
	public Long getSurplusAmount(ExtendBean order, Long[][] rec) {
		return 0L;
	}

	public RcsBusinessPlayPaidConfig getPlayConfig(ExtendBean order) {
		RcsBusinessPlayPaidConfig playConfig = order.getPlayConfig();
		if (playConfig == null) {
			playConfig = configService.getPlayPaidConfig(order.getBusId(), order.getSportId(),
					order.getIsScroll(), order.getPlayType(), order.getPlayId());
			if (playConfig == null) {
				//没有查询到走其它玩法
				playConfig = configService.getPlayPaidConfig(order.getBusId(), order.getSportId(),
						order.getIsScroll(), order.getPlayType(), "-1");

			}
			order.setPlayConfig(playConfig);
		}
		return playConfig;
	}

	@Override
	public Boolean saveOrder(ExtendBean order, Long[][] rec, Map<String, Object> data) {
		return true;
	}
}
