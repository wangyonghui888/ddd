package com.panda.sport.rcs.mgr.paid.intef.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.mgr.paid.intef.AmountValidateAdapter;
import com.panda.sport.rcs.pojo.RcsTournamentMarketConfig;
import com.panda.sport.rcs.mgr.wrapper.RcsTournamentOperateMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * @author :  Sean
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.rpc.paid.intef.impl
 * @Description :  “最大投注/派奖限额”表示单个用户一次下注最多能下注成功的额度或者最大赔付值，
 *                  两者中谁先达到预设值，即采用哪一个
 * @Date: 2019-10-23 11:41
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
//@Order(value = 6)
@Slf4j
public class QueryMinAmountWithBetAndPaid extends AmountValidateAdapter {

    @Autowired
    private RcsTournamentOperateMarketService rcsTournamentOperateMarketService;

    @Override
    public Long getSurplusAmount(ExtendBean order, Long[][] rec) {
        log.info("::{}:: QueryMinAmountWithBetAndPaid order:{}",order.getOrderId(),JSONObject.toJSONString(order));
        return rcsTournamentOperateMarketService.queryMatchAndTournamentMaxBetAmount(order);
    }

    @Override
    public Boolean saveOrder(ExtendBean order, Long[][] rec, Map<String, Object> data) {
        Long amount = getSurplusAmount(order, rec);
        data.put("addVal", amount);
        data.put("type", "单个用户最大投注/派奖限额");
        if(amount < (order.getOrderMoney()/ BaseConstants.MULTIPLE_BET_AMOUNT_VALUE)) {
            return false;
        }
        //最大赔付需要除以赔率
        Long odds = new BigDecimal(order.getItemBean().getOddsValue()).divide(new BigDecimal(BaseConstants.MULTIPLE_VALUE),2, RoundingMode.DOWN).multiply(new BigDecimal(BaseConstants.MULTIPLE_BET_AMOUNT_VALUE)).subtract(new BigDecimal(NumberUtils.LONG_ONE)).longValue();
        Long orderMoney = new BigDecimal(amount).divide(new BigDecimal(odds),2,RoundingMode.DOWN).longValue();
        if(orderMoney < order.getOrderMoney()){
            return false;
        }
        return true;
    }

    private static Long conversion (Long l){
        return ObjectUtils.isEmpty(l) ? Long.MAX_VALUE : new BigDecimal(l).multiply(new BigDecimal(BaseConstants.MULTIPLE_BET_AMOUNT_VALUE)).longValue();
    }
}
