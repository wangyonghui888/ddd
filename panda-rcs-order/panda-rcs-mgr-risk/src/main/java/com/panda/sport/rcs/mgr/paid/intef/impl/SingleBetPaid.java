package com.panda.sport.rcs.mgr.paid.intef.impl;

import java.math.BigDecimal;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.mgr.paid.annotion.Order;
import com.panda.sport.rcs.mgr.paid.intef.AmountValidateAdapter;
import com.panda.sport.rcs.pojo.RcsBusinessSingleBetConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * @author :   vector
 * @Description :  单关
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
//@Order(value = 7)
@Slf4j
public class SingleBetPaid extends AmountValidateAdapter {

    @Override
    public Long getSurplusAmount(ExtendBean order, Long[][] rec) {
    	log.info("::{}:: SingleBetPaid-getSurplusAmount-order:{} rec:{}",order.getOrderId(), JSONObject.toJSONString(order),JSONObject.toJSONString(rec));
		RcsBusinessSingleBetConfig singleBetConfig = getSingleBetConfig(order);
		if (singleBetConfig == null) {
			log.warn("::{}::requestId:{},单关校验获取失败，ExtendBean:{}",order.getOrderId(),LogContext.getContext().getRequestId(), JSONObject.toJSONString(order));
			throw new RcsServiceException("联赛玩法级别获取配置失败");
		}
		//获取当前玩法合并之后最大赔付
 		BigDecimal orderMaxValue = singleBetConfig.getOrderMaxValue();
		if(order.getCurrentMaxPaid() == null )  return orderMaxValue.longValue();
		log.info("::{}:: requestId:{},UserPlayMaxPaid, currentPaid:{},playMaxPay:{}",order.getOrderId(),LogContext.getContext().getRequestId(), null, orderMaxValue);
		return orderMaxValue.subtract(new BigDecimal(String.valueOf(order.getCurrentMaxPaid()))).longValue();
    }

    @Override
    public Boolean saveOrder(ExtendBean order, Long[][] rec, Map<String, Object> data) {
		Long amount = getSurplusAmount(order, rec);
		data.put("addVal", amount);
		data.put("type", "联赛玩法维度校验");
		if (amount < 0) {
			log.warn("::{}:: requestId:{},单关校验失败，ExtendBean:{}",order.getOrderId(),LogContext.getContext().getRequestId(), JSONObject.toJSONString(order));
			return false;
		}
        return true;
    }

    private RcsBusinessSingleBetConfig getSingleBetConfig(ExtendBean order) {
        RcsBusinessSingleBetConfig singleBetConfig = order.getSingleBetConfig();
        if (singleBetConfig == null) {
			singleBetConfig = configService.getSingleBetConfig(order.getBusId(),order.getSportId(),order.getIsScroll(),order.getPlayType(),order.getPlayId(), order.getTournamentLevel().toString());

			if (singleBetConfig == null) return null;
            order.setSingleBetConfig(singleBetConfig);
        }
        return singleBetConfig;
    }

}

