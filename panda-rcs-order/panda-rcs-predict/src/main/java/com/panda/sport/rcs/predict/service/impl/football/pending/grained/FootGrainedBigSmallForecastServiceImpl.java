package com.panda.sport.rcs.predict.service.impl.football.pending.grained;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.PendingOrderDto;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.predict.pending.RcsPredictPendingForecastMapper;
import com.panda.sport.rcs.pojo.vo.predict.pending.RcsPredictPendingForecast;
import com.panda.sport.rcs.predict.common.Expiry;
import com.panda.sport.rcs.predict.service.ForecastPendingService;
import com.panda.sport.rcs.predict.service.impl.PredictCommonServiceImpl;
import com.panda.sport.rcs.predict.vo.ForecastScopeVo;
import com.panda.sport.rcs.utils.MarketValueUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 预约投注  最小维度 大小  Forecast计算
 * @author: joey
 * @date: 2022-05-23 14:30:00
 **/
@Slf4j
@Service("footGrainedBigSmallPendingForecastService")
public class FootGrainedBigSmallForecastServiceImpl implements ForecastPendingService {

    @Autowired
    protected RedisClient redisClient;

    @Autowired
    private PredictCommonServiceImpl predictCommonService;

    @Autowired
    private RcsPredictPendingForecastMapper rcsPredictPendingForecastMapper;

    /**
     * Forecast计算
     */
    @Override
    public void forecastData(PendingOrderDto pendingOrderDto, Integer type) {
        log.info("预约投注 预测数据计算-{}大小Forecast计算开始", pendingOrderDto.getOrderNo());
        //计算数据
        List<RcsPredictPendingForecast> list = calculate(pendingOrderDto, type);
        //数据库保存
        predictCommonService.updateRcsPredictPendingForecast(list);
        log.info("预约投注 预测数据计算-{}大小Forecast计算完成", pendingOrderDto.getOrderNo());
    }


    public List<RcsPredictPendingForecast> calculate(PendingOrderDto pendingOrderDto, Integer type) {
        List<RcsPredictPendingForecast> list = new ArrayList<>();
        //计算每个总进球数 对应的 预期 盈利
        ForecastScopeVo forecastScopeVo = predictCommonService.getBigSmallForecastScopeVo(pendingOrderDto.getPlayId().intValue());
        for (Integer i = forecastScopeVo.getMin(); i <= forecastScopeVo.getMax(); i++) {
            List<Double> marketValueList = MarketValueUtils.splitMarketValue(pendingOrderDto.getMarketValue());
            Double result = 0D;
            for (Double markValue : marketValueList) {
                //预期盈利额
                BigDecimal amount = BigDecimal.ZERO;
                //投注金额
                BigDecimal betAmount = new BigDecimal(pendingOrderDto.getBetAmount()).divide(new BigDecimal(100), 2, RoundingMode.DOWN);
                if (marketValueList.size() == 2) {
                    betAmount = new BigDecimal(pendingOrderDto.getBetAmount()).divide(new BigDecimal(100), 2, RoundingMode.DOWN).divide(BigDecimal.valueOf(2), 2, RoundingMode.FLOOR);
                }
                //买大  注单中奖 玩家赢  庄家输 预期盈利为负  需要乘以负一
                if (i - markValue > 0) {
                    if ("home".equals(handleGoalLine(pendingOrderDto))) {//赢
                        amount = betAmount.multiply(new BigDecimal(String.valueOf(pendingOrderDto.getOrderOdds())).
                                divide(new BigDecimal(100000), 2, RoundingMode.DOWN).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
                    } else {
                        amount = betAmount;
                    }
                } else if (i - markValue == 0) {
                    //走水
                } else {
                    //注单中奖 玩家赢  庄家输 预期盈利为负  需要乘以负一
                    if ("away".equals(handleGoalLine(pendingOrderDto))) {//赢
                        amount = betAmount.multiply(new BigDecimal(String.valueOf(pendingOrderDto.getOrderOdds())).
                                divide(new BigDecimal(100000), 2, RoundingMode.DOWN).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
                    } else {
                        amount = betAmount;
                    }
                }
                //如果是取消订单 取反
                amount = amount.multiply(new BigDecimal(type));
                //缓存处理  预期金额累加/减
                result = redisClient.hincrByFloat(getCacheKey(pendingOrderDto), i.toString(), amount.doubleValue());
                redisClient.expireKey(getCacheKey(pendingOrderDto), Expiry.MATCH_EXPIRY);

            }

            RcsPredictPendingForecast rcsProfitRectangle = new RcsPredictPendingForecast();
            rcsProfitRectangle.setSportId(pendingOrderDto.getSportId().intValue());
            rcsProfitRectangle.setMatchId(pendingOrderDto.getMatchId());
            rcsProfitRectangle.setMatchType(pendingOrderDto.getMatchType());
            rcsProfitRectangle.setPlayId(pendingOrderDto.getPlayId().intValue());
            rcsProfitRectangle.setMarketId(pendingOrderDto.getMarketId());
            rcsProfitRectangle.setOddsItem(pendingOrderDto.getOddType());
            rcsProfitRectangle.setBetScore("0:0");
            rcsProfitRectangle.setCreateTime(System.currentTimeMillis());
            rcsProfitRectangle.setMarketValueComplete(pendingOrderDto.getMarketValue());
            rcsProfitRectangle.setMarketValueCurrent(pendingOrderDto.getMarketValue());
            rcsProfitRectangle.setProfitAmount(new BigDecimal(String.valueOf(result)));
            rcsProfitRectangle.setForecastScore(i);
            list.add(rcsProfitRectangle);
        }
        log.info("预测数据计算-大小Forecast结果:{}", JSONObject.toJSONString(list));
        return list;
    }

    private String getCacheKey(PendingOrderDto pendingOrderDto) {
        String key = "rcs:risk:predict:pending:forecast.match_id.%s.match_type.%s.play_id.%s.market_id.%s";
        key = String.format(key, pendingOrderDto.getMatchId(), pendingOrderDto.getMatchType(), pendingOrderDto.getPlayId(), pendingOrderDto.getMarketValue(), pendingOrderDto.getOddType());
        return key;
    }

    private String handleGoalLine(PendingOrderDto pendingOrderDto) {
        if ("Over".equalsIgnoreCase(pendingOrderDto.getOddType())) {
            //大
            return "home";
        } else if ("Under".equalsIgnoreCase(pendingOrderDto.getOddType())) {
            //小
            return "away";
        }
        throw new RcsServiceException("选项错误：" + pendingOrderDto.getOddType());
    }

}