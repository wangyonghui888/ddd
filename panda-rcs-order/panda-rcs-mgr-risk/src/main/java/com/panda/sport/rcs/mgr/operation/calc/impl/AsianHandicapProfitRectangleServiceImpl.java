package com.panda.sport.rcs.mgr.operation.calc.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
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
 * @Description :  让球盘期望详情处理
 * 让球: 4: 全场让球 19:亚盘让球-上半场
 * @Date: 2019-12-13 11:13
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
public class AsianHandicapProfitRectangleServiceImpl extends AbstractProfitRectangle implements IProfitRectangle {
    /**
     * @param bean
     * @return void
     * @Description 处理
     * @Param [orderBean]
     * @Author toney
     * @Date 12:24 2019/12/13
     **/
    @Override
    public void handle(OrderBean orderBean, RcsProfitMarket bean, Integer type) {
        ConcurrentHashMap<Double, RcsProfitRectangle> map = new ConcurrentHashMap<Double, RcsProfitRectangle>();
        handleData(orderBean, map, ProfixConstant.ASIANHANDICAP_MIN_MATRIX_VALUE, ProfixConstant.ASIANHANDICAP_MAX_MATRIX_VALUE, bean,  type);
    }

    /**
     * @return java.lang.Boolean
     * @Description 校验规则
     * @Param []
     * @Author toney
     * @Date 9:49 2019/12/19
     **/
    @Override
    public Boolean checkParams(OrderItem orderItem) {
        return ProfitUtil.checkAsianHandicap(orderItem.getPlayId());
    }


    private String handleAsianHandicap(OrderItem item){
        if("1".equals(item.getPlayOptions())){
           return "home";
        }else if("2".equals(item.getPlayOptions())){
            //客队让球
        	return "away";
        }
        throw new RcsServiceException("选项错误：" + item.getPlayOptions());
    }

    /**
     * 亚洲让球盘
     *
     * @param orderItem
     */
    @Override
    public ConcurrentHashMap<Double, RcsProfitRectangle> logicHandle(OrderItem orderItem, ConcurrentHashMap<Double, RcsProfitRectangle> map, RcsProfitMarket bean, Integer type) {
//        List<RcsProfitMarket> rcsProfitMarketList = getRcsProfitMakeretList(orderItem);
    	String key = String.format("rcs:profit:match:%s:%s:%s", orderItem.getMatchId(),orderItem.getMatchType(),orderItem.getPlayId());
    	for (Double i = ProfixConstant.ASIANHANDICAP_MIN_MATRIX_VALUE;  i <= ProfixConstant.ASIANHANDICAP_MAX_MATRIX_VALUE; i++) {
            RcsProfitRectangle rcsProfitRectangle = map.get(i);

            List<Double> marketValueList = null;
            if(StringUtils.isNotEmpty( orderItem.getMarketValueNew())){
                marketValueList = MarketValueUtils.splitMarketValue(orderItem.getMarketValueNew());
            }else {
                marketValueList = MarketValueUtils.splitMarketValue(orderItem.getMarketValue());
            }

            Double result = 0d;
            for (Double marketValue : marketValueList) {
                BigDecimal amount = BigDecimal.ZERO;
            	BigDecimal betAmount = marketValueList.size() == 2 ?
            			new BigDecimal(String.valueOf(orderItem.getBetAmount1())).divide(BigDecimal.valueOf(2),2,RoundingMode.FLOOR) : orderItem.getBetAmount1();

                if(i + marketValue > 0) {
                    //大
                	if("home".equals(handleAsianHandicap(orderItem))) {//赢
                		amount = betAmount.multiply(new BigDecimal(String.valueOf(orderItem.getHandleAfterOddsValue())).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
                	}else {
                		amount = betAmount;
                	}
                }else if(i + marketValue ==0) {
                    //走水 不做处理
                }else {
                    //小
                	if("away".equals(handleAsianHandicap(orderItem))) {//赢
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

        log.info("::{}:: 期望详情计算结果matchId{},playId{},map实体bean{}",orderItem.getMatchId(),orderItem.getPlayId(),JsonFormatUtils.toJson(map));

        return map;
    }

    public static void main(String[] args){
        String order= "{\"traceId\":null,\"sportId\":null,\"orderNo\":\"15487938117633\",\"uid\":128691269827117056,\"orderStatus\":0,\"handleStatus\":null,\"productCount\":1,\"seriesType\":1,\"productAmountTotal\":10000,\"orderAmountTotal\":10000,\"deviceType\":1,\"ip\":\"172.18.180.20\",\"remark\":\"用户下注\",\"tenantId\":1,\"tenantName\":\"test\",\"delFlag\":null,\"createTime\":1585967478308,\"userFlag\":\"\",\"createUser\":\"系统\",\"modifyUser\":\"系统\",\"modifyTime\":1585967478308,\"currencyCode\":\"CNY\",\"currencyName\":null,\"tOrdercol\":null,\"ipArea\":\"局域网,局域网,\",\"validateResult\":1,\"items\":[{\"id\":null,\"betNo\":\"25487938117632\",\"orderNo\":\"15487938117633\",\"uid\":128691269827117056,\"sportId\":1,\"sportName\":\"足球\",\"playId\":113,\"playName\":\"角球让球盘\",\"matchId\":366695,\"matchName\":\"白俄罗斯足球超级联赛\",\"betTime\":1585967478308,\"matchType\":1,\"marketType\":\"EU\",\"marketValue\":\"-2.5\",\"marketValueNew\":null,\"matchInfo\":\"索里格尔斯克 VS 格罗德诺 尼曼\",\"betAmount\":10000,\"handleStatus\":0,\"marketId\":1246243568059727873,\"oddsValue\":213000.0,\"oddFinally\":\"2.13\",\"acceptBetOdds\":null,\"maxWinAmount\":11300.0,\"isValid\":1,\"scoreBenchmark\":\"\",\"playOptionsId\":1246243568135225346,\"playOptionsName\":\"+2.5\",\"playOptions\":\"2\",\"playOptionsRange\":null,\"delFlag\":null,\"matchProcessId\":0,\"remark\":null,\"createTime\":1585967478308,\"createUser\":\"系统\",\"modifyUser\":\"系统\",\"modifyTime\":1585967478308,\"tournamentId\":1152,\"isResult\":null,\"recType\":null,\"recVal\":null,\"isRelationScore\":null,\"validateResult\":1,\"currentPlayType\":null,\"dateExpect\":\"2020-04-04\",\"riskChannel\":1,\"mtsOrderStatus\":null,\"orderStatus\":0,\"tradeType\":null,\"turnamentLevel\":5,\"platform\":\"PA\",\"dataSourceCode\":\"SR\"}]}";
        OrderBean orderBean= JsonFormatUtils.fromJson(order,OrderBean.class);
        AsianHandicapProfitRectangleServiceImpl asianHandicapProfitRectangleService = new AsianHandicapProfitRectangleServiceImpl();
        ConcurrentHashMap<Double, RcsProfitRectangle> map = new ConcurrentHashMap<Double, RcsProfitRectangle>();
        ConcurrentHashMap<Double, RcsProfitRectangle> list = asianHandicapProfitRectangleService.initRcsProfitRectangle(orderBean.getItems().get(0),map,0.0,24.0);
        asianHandicapProfitRectangleService.logicHandle(orderBean.getItems().get(0),map,null,1);
    }
}
