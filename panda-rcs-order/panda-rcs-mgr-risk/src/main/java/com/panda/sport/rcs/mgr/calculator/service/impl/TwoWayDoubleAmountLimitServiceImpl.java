package com.panda.sport.rcs.mgr.calculator.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.ThreewayOverLoadTriggerItem;
import com.panda.sport.data.rcs.dto.TwowayDoubleOverLoadTriggerItem;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.LogicException;
import com.panda.sport.rcs.mgr.wrapper.MarketOddsChangeCalculationService;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("twoWayDoubleAmountLimitServiceImpl")
@Slf4j
public class TwoWayDoubleAmountLimitServiceImpl extends TwoWayAmountLimitServiceAdapter {

    @Autowired
    public TwoWayDoubleAmountLimitServiceImpl(RedisClient redisClient) {
        super(redisClient);
    }

    @Autowired
    MarketOddsChangeCalculationService marketOddsChangeCalculationService;


//    @Override
//    public void fireCountAndFix(OrderItem item,long balance,RcsMatchMarketConfig result) {
//
//        Boolean isHome = isHome(item.getPlayOptions(), item.getMarketValue());
//
//        executeCalcLua(item, result, "1",item.getBetAmount()/OrderItem.PlUSTIMES,item.getHandleAfterOddsValue(),result.getHomeLevelFirstMaxAmount(),
//        		result.getHomeLevelSecondMaxAmount(),item.getPlayOptionsId(),isHome ? 1 : 3);
//    }
    
    @Override
    public void triggerChange(RcsMatchMarketConfig result,OrderItem item,JSONArray exeResultArray) {
    	Integer level = exeResultArray.getInteger(2);
        Integer isHome = exeResultArray.getInteger(5);
    	BigDecimal homeRate = level == 1 ?  result.getHomeLevelFirstOddsRate() : result.getHomeLevelSecondOddsRate();
    	BigDecimal awayRate = level == 1 ?  result.getAwayLevelFirstOddsRate() : result.getAwayLevelSecondOddsRate();
    	ThreewayOverLoadTriggerItem trigger = composeTriggerItem(item,isHome,exeResultArray.getInteger(2), homeRate, awayRate, ThreewayOverLoadTriggerItem.FixDirectionEnum.DESC);
        composeMarketParams(result,trigger);
        trigger.setMatchType(item.getMatchType());
        // 需要传递盘口位置，这里临时处理一下
        trigger.setPlaceNum(item.getPlaceNum());
        trigger.replaceMsg("match_info",item.getMatchInfo()).replaceMsg("play_option_name", item.getPlayName());
    	triggerForOverLoad(result,trigger);
    }

    public TwowayDoubleOverLoadTriggerItem composeTriggerItem(OrderItem item,Integer isHome,Integer level, BigDecimal homeRate,BigDecimal awayRate ,ThreewayOverLoadTriggerItem.FixDirectionEnum direction){
        TwowayDoubleOverLoadTriggerItem target = getTriggerItem(item,TwowayDoubleOverLoadTriggerItem.class);
        target.setLimitLevel(level);
        if(homeRate == null || awayRate == null){
            log.warn("::{}:: TwoWayDoubleAmountLimitServiceImpl composeTriggerItem  上下盘赔率/水差变化率均不可以为空，item：{}",item.getOrderNo(), JSONObject.toJSONString(item));
            throw new LogicException("60021", "上下盘赔率/水差变化率均不可以为空");
        }
        if(level == 2){
            target.setHomeLevelSecondOddsRate(homeRate);
            target.setAwayLevelSecondOddsRate(awayRate);
        }else if(level == 1){
            target.setHomeLevelFirstOddsRate(homeRate);
            target.setAwayLevelFirstOddsRate(awayRate);
        }
        target.setFixDirectionEnum(ThreewayOverLoadTriggerItem.FixDirectionEnum.DESC);
        target.setOddsType(item.getPlayOptions());
        return target;
    }

}
