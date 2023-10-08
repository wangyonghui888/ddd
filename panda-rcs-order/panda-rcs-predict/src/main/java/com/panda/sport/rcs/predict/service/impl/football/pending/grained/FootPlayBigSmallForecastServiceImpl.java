package com.panda.sport.rcs.predict.service.impl.football.pending.grained;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.PendingOrderDto;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.pojo.vo.predict.pending.RcsPredictPendingForecastPlay;
import com.panda.sport.rcs.predict.common.Expiry;
import com.panda.sport.rcs.predict.service.ForecastPendingService;
import com.panda.sport.rcs.predict.service.impl.PredictCommonServiceImpl;
import com.panda.sport.rcs.predict.vo.ForecastScopeVo;
import com.panda.sport.rcs.utils.MarketValueUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 预约投注  足球玩法级别  大小 forecast
 * @author: joey
 * @date: 2022-5-23 11:13:28
 **/
@Slf4j
@Service("footPlayBigSmallPendingForecastService")
public class FootPlayBigSmallForecastServiceImpl implements ForecastPendingService {

    @Autowired
    protected RedisClient redisClient;

    @Resource(name = "predictCommonService")
    private PredictCommonServiceImpl predictCommonService;


    /**
     * Forecast计算
     */
    @Override
    public void forecastData(PendingOrderDto pendingOrderDto, Integer type) {
        List<RcsPredictPendingForecastPlay> list = calculate(pendingOrderDto, type);
        log.info("预约投注 预测数据计算-玩法:{}完成:{}", pendingOrderDto.getOrderNo(), JSONObject.toJSONString(list));
        predictCommonService.updateRcsPredictPendingForecastPlay(list);
    }


    public List<RcsPredictPendingForecastPlay> calculate(PendingOrderDto pendingOrderDto, Integer type) {
        List<RcsPredictPendingForecastPlay> list = new ArrayList<>();
        ForecastScopeVo forecastScopeVo = predictCommonService.getBigSmallForecastScopeVo(pendingOrderDto.getPlayId().intValue());
        for (Integer i = forecastScopeVo.getMin(); i <= forecastScopeVo.getMax(); i++) {
            List<Double> marketValueList = MarketValueUtils.splitMarketValue(pendingOrderDto.getMarketValue());
            Double result = 0D;
            for (Double markValue : marketValueList) {
                BigDecimal amount = BigDecimal.ZERO;
                BigDecimal betAmount = new BigDecimal(pendingOrderDto.getBetAmount()).divide(new BigDecimal(100), 2, RoundingMode.DOWN);
                betAmount = marketValueList.size() == 2 ?
                        betAmount.divide(BigDecimal.valueOf(2), 2, RoundingMode.FLOOR) : betAmount;
                //买大 赢
                if (i - markValue > 0) {
                    if ("Over".equals(pendingOrderDto.getOddType())) {//赢
                        amount = betAmount.multiply(new BigDecimal(String.valueOf(pendingOrderDto.getOrderOdds())).
                                divide(new BigDecimal(100000), 2, RoundingMode.DOWN).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
                    } else {
                        amount = betAmount;
                    }
                } else if (i - markValue == 0) {
                    //走水
                } else {
                    if ("Under".equals((pendingOrderDto.getOddType()))) {//赢
                        amount = betAmount.multiply(new BigDecimal(String.valueOf(pendingOrderDto.getOrderOdds())).
                                divide(new BigDecimal(100000), 2, RoundingMode.DOWN).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
                    } else {
                        amount = betAmount;
                    }
                }
                result = redisClient.hincrByFloat(getCacheKey(pendingOrderDto), i.toString(), amount.doubleValue() * type);
                redisClient.expireKey(getCacheKey(pendingOrderDto), Expiry.MATCH_EXPIRY);
            }
            RcsPredictPendingForecastPlay rcsProfitRectangle = new RcsPredictPendingForecastPlay();
            //添加到集合
            rcsProfitRectangle.setMatchType(pendingOrderDto.getMatchType());
            rcsProfitRectangle.setUpdateTime(System.currentTimeMillis());
            rcsProfitRectangle.setCreateTime(System.currentTimeMillis());
            rcsProfitRectangle.setMatchId(pendingOrderDto.getMatchId());
            rcsProfitRectangle.setPlayId(pendingOrderDto.getPlayId());
            rcsProfitRectangle.setDataType(1);
            rcsProfitRectangle.setPlaceNum(-1);
            rcsProfitRectangle.setScore(i);
            rcsProfitRectangle.setProfitValue(new BigDecimal(String.valueOf(result)));
            list.add(rcsProfitRectangle);
        }

        return list;
    }

    private String getCacheKey(PendingOrderDto pendingOrderDto) {
        String key = String.format("rcs:profit:pending:match:%s:%s:%s:%s", pendingOrderDto.getMatchId(), pendingOrderDto.getMatchType(), pendingOrderDto.getPlayId(), -1);
        return key;
    }
}