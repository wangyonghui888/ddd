package com.panda.sport.rcs.predict.service.impl.football.pending.grained;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.PendingOrderDto;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.pojo.vo.predict.pending.RcsPredictPendingForecastPlay;
import com.panda.sport.rcs.predict.common.Expiry;
import com.panda.sport.rcs.predict.service.ForecastPendingService;
import com.panda.sport.rcs.predict.service.impl.PredictCommonServiceImpl;
import com.panda.sport.rcs.predict.vo.ForecastScopeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @description:  足球玩法级别  让球 forecast
 * @author: lithan
 * @date:  2021-2-21 11:13:28
 **/
@Slf4j
@Service("footPlayAloneWinPendingForecastService")
public class FootPlayAloneWinForecastServiceImpl implements ForecastPendingService {

    @Autowired
    protected RedisClient redisClient;

    @Resource(name = "predictCommonService")
    private PredictCommonServiceImpl predictCommonService;


    HashMap<String, String> playMapping = new HashMap<String, String>() {
        {
            put("1", "4");
            put("17", "19");
            put("111", "113");
            put("119", "121");
            put("126", "128");
            put("129", "130");
            put("310", "306");
            put("311", "308");
            put("333", "334");
    	}
    };

    /**
     * Forecast计算
     */
    @Override
    public void forecastData(PendingOrderDto pendingOrderDto, Integer type) {
        if (!playMapping.containsKey(String.valueOf(pendingOrderDto.getPlayId()))) {
            return;
        }
        List<RcsPredictPendingForecastPlay> list = calculate(pendingOrderDto, type);
        log.info("预测数据计算-玩法:{}完成:{}", pendingOrderDto.getOrderNo(), JSONObject.toJSONString(list));
        predictCommonService.updateRcsPredictPendingForecastPlay(list);
    }


    public List<RcsPredictPendingForecastPlay> calculate(PendingOrderDto pendingOrderDto, Integer type) {
        List<RcsPredictPendingForecastPlay> list = new ArrayList<>();
        String playOptions = pendingOrderDto.getOddType();
        String placePlayId = playMapping.get(String.valueOf(pendingOrderDto.getPlayId()));
        Double result;
        ForecastScopeVo forecastScopeVo = predictCommonService.getLetPointForecastScopeVo(Integer.valueOf(placePlayId));
        for (Integer i = forecastScopeVo.getMin(); i <= forecastScopeVo.getMax(); i++) {
            BigDecimal amount;
            BigDecimal betAmount = new BigDecimal(pendingOrderDto.getBetAmount()).divide(new BigDecimal(100), 2, RoundingMode.DOWN);
            if (playOptions.equals("1")) {
                if (i > 0) {//胜  注单中奖 玩家赢  庄家输 预期盈利为负  需要乘以负一
                    amount = betAmount.multiply(new BigDecimal(String.valueOf(pendingOrderDto.getOrderOdds())).
                            divide(new BigDecimal(100000), 2, RoundingMode.DOWN).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
                } else {
                    amount = betAmount;
                }
            } else if (playOptions.equals("X")) {
                if (i == 0) {//胜  注单中奖 玩家赢  庄家输 预期盈利为负  需要乘以负一
                    amount = betAmount.multiply(new BigDecimal(String.valueOf(pendingOrderDto.getOrderOdds())).
                            divide(new BigDecimal(100000), 2, RoundingMode.DOWN).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
                }else {
        			amount = betAmount;
        		}
        	}else if (playOptions.equals("2")) {
        		if(i < 0 ) {//胜  注单中奖 玩家赢  庄家输 预期盈利为负  需要乘以负一
                    amount = betAmount.multiply(new BigDecimal(String.valueOf(pendingOrderDto.getOrderOdds())).
                            divide(new BigDecimal(100000), 2, RoundingMode.DOWN).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
                } else {
                    amount = betAmount;
                }
            } else {
                return null;
            }

            //如果是取消订单 取反
            amount = amount.multiply(new BigDecimal(type));
            //缓存处理  预期金额累加/减
            result = redisClient.hincrByFloat(getCacheKey(pendingOrderDto, placePlayId), i.toString(), amount.doubleValue());
            redisClient.expireKey(getCacheKey(pendingOrderDto, placePlayId), Expiry.MATCH_EXPIRY);
            //添加到集合
            RcsPredictPendingForecastPlay rcsPredictPendingForecastPlay = new RcsPredictPendingForecastPlay();
            rcsPredictPendingForecastPlay.setMatchType(pendingOrderDto.getMatchType());
            rcsPredictPendingForecastPlay.setUpdateTime(System.currentTimeMillis());
            rcsPredictPendingForecastPlay.setCreateTime(System.currentTimeMillis());
            rcsPredictPendingForecastPlay.setMatchId(pendingOrderDto.getMatchId());
            rcsPredictPendingForecastPlay.setPlayId(Long.parseLong(placePlayId));
            rcsPredictPendingForecastPlay.setDataType(1);
            rcsPredictPendingForecastPlay.setPlaceNum(-1);
            rcsPredictPendingForecastPlay.setScore(i);
            rcsPredictPendingForecastPlay.setProfitValue(new BigDecimal(String.valueOf(result)));
            list.add(rcsPredictPendingForecastPlay);
        }
        return list;
    }

    private String getCacheKey(PendingOrderDto pendingOrderDto, String playId) {
        return String.format("rcs:profit:pending:match:%s:%s:%s:%s", pendingOrderDto.getMatchId(), pendingOrderDto.getMatchType(), playId, -1);
    }

}