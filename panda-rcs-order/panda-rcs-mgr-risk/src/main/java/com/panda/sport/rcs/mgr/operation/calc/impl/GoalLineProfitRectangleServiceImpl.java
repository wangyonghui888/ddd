package com.panda.sport.rcs.mgr.operation.calc.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mgr.operation.calc.AbstractProfitRectangle;
import com.panda.sport.rcs.mgr.operation.calc.IProfitRectangle;
import com.panda.sport.rcs.pojo.statistics.RcsProfitMarket;
import com.panda.sport.rcs.pojo.statistics.RcsProfitRectangle;
import com.panda.sport.rcs.profit.constant.ProfixConstant;
import com.panda.sport.rcs.profit.utils.ProfitUtil;
import com.panda.sport.rcs.utils.MarketValueUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.operation.order.impl
 * @Description :  大小球期望详情处理
 *  针对玩法：
 *  大小球: 2:全场大小 18
 * @Date: 2019-12-11 18:13
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
public class GoalLineProfitRectangleServiceImpl extends AbstractProfitRectangle implements IProfitRectangle {
    /**
     * @Description   处理
     * @Param [orderBean]
     * @Author  toney
     * @Date  12:24 2019/12/13
     * @return void
     **/
    @Override
    public void handle(OrderBean orderBean,RcsProfitMarket bean, Integer type) {
        ConcurrentHashMap<Double, RcsProfitRectangle> map = new  ConcurrentHashMap<Double, RcsProfitRectangle>();
        handleData(orderBean,map,ProfixConstant.GoalLine_MIN_MATRIX_VALUE,ProfixConstant.GoalLine_MAX_MATRIX_VALUE,bean, type);
    }


    /**
     * @Description   校验规则
     * @Param [orderItem]
     * @Author  toney
     * @Date  9:52 2019/12/19
     * @return java.lang.Boolean
     **/
    @Override
    public Boolean checkParams(OrderItem orderItem) {
        return ProfitUtil.checkGoalLine(orderItem.getPlayId());
    }


    private String handleGoalLine(OrderItem item) {
        if ("Over".equalsIgnoreCase(item.getPlayOptions())) {
            //大
            return "home";
        } else if ("Under".equalsIgnoreCase(item.getPlayOptions())) {
            //小
            return "away";
        }
        throw new RcsServiceException("选项错误：" + item.getPlayOptions());
    }


    /**
     * 大小球
     * @param orderItem
     */
    @Override
    public ConcurrentHashMap<Double, RcsProfitRectangle> logicHandle(OrderItem orderItem,ConcurrentHashMap<Double, RcsProfitRectangle> map,RcsProfitMarket bean, Integer type) {
        //List<RcsProfitMarket> rcsProfitMarketList = getRcsProfitMakeretList(orderItem);
    	String key = String.format("rcs:profit:match:%s:%s:%s", orderItem.getMatchId(),orderItem.getMatchType(),orderItem.getPlayId());

        for (Double i = ProfixConstant.GoalLine_MIN_MATRIX_VALUE; i <= ProfixConstant.GoalLine_MAX_MATRIX_VALUE; i++) {
        	RcsProfitRectangle rcsProfitRectangle = map.get(i);
            List<Double> marketValueList = MarketValueUtils.splitMarketValue(orderItem.getMarketValue());


            Double result = 0D;
            for (Double markValue : marketValueList) {
                BigDecimal amount = BigDecimal.ZERO;
            	BigDecimal betAmount = marketValueList.size() == 2 ?
            			new BigDecimal(String.valueOf(orderItem.getBetAmount1())).divide(BigDecimal.valueOf(2),2,RoundingMode.FLOOR) : orderItem.getBetAmount1();
                //买大
                //赢
                if(i - markValue > 0){
                	if("home".equals(handleGoalLine(orderItem))) {//赢
                		amount = betAmount.multiply(new BigDecimal(String.valueOf(orderItem.getHandleAfterOddsValue())).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
                	}else {
                		amount = betAmount;
                	}
                }else if (i - markValue ==0){
                    //走水
                }else{
                	if("away".equals(handleGoalLine(orderItem))) {//赢
                		amount = betAmount.multiply(new BigDecimal(String.valueOf(orderItem.getHandleAfterOddsValue())).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
                	}else {
                		amount = betAmount;
                	}
                }
                result = redisClient.hincrByFloat(key, i.toString(), amount.doubleValue()*type);
            }

            rcsProfitRectangle.setUpdateTime(new Date());
            rcsProfitRectangle.setMatchType(orderItem.getMatchType());

            rcsProfitRectangle.setProfitValue(new BigDecimal(String.valueOf(result)));
        }

        log.info("::{}::期望详情计算结果matchId=%s,playId=%s,map实体bean{}",orderItem.getOrderNo(),orderItem.getMatchId(),orderItem.getPlayId(),JsonFormatUtils.toJson(map));

        return map;
    }
}