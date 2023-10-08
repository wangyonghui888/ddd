package com.panda.sport.rcs.predict.service.impl.football.grained;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecast;
import com.panda.sport.rcs.predict.common.Expiry;
import com.panda.sport.rcs.predict.service.ForecastService;
import com.panda.sport.rcs.predict.service.impl.PredictCommonServiceImpl;
import com.panda.sport.rcs.predict.service.impl.PredictResetRedisKeyBo;
import com.panda.sport.rcs.predict.vo.ForecastScopeVo;
import com.panda.sport.rcs.utils.MarketValueUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 最小维度 让球  Forecast计算
 * @author: lithan
 * @date: 2020-07-19 14:30:00
 **/
@Slf4j
@Service("footGrainedLetPointForecastService")
public class FootGrainedLetPointForecastServiceImpl implements ForecastService {

    @Autowired
    protected RedisClient redisClient;

    @Autowired
    private PredictCommonServiceImpl predictCommonService;

    @Autowired
    private PredictResetRedisKeyBo predictResetRedisKeyBo;

    /**
     * Forecast计算
     */
    @Override
    public void forecastData(OrderItem item, Integer type, boolean nx) {
        log.info("::{}::预测数据计算-让球Forecast计算开始:{}", item.getOrderNo(), item.getBetNo());
        if (item.getMatchType() == 1) {
            predictResetRedisKeyBo.resetForecastStatisRedisKey(item);
        }
        //计算数据
        List<RcsPredictForecast> list = calculate(item, type);
        // 盘口变动立即将上一次被限制的数据推送出去
        String lastMarketChangeKey = String.format("rcs:risk:predict:marketChange.match_id.%s.match_type.%s.play_id.%s", item.getMatchId(), item.getMatchType(), item.getPlayId());
        String lastMarketChangeData = redisClient.get(lastMarketChangeKey);
        if (!StringUtils.isEmpty(lastMarketChangeData)) {
            Map map = JSONObject.parseObject(lastMarketChangeData, Map.class);
            if (!map.get("marketId").equals(item.getMarketId())) {
                List<RcsPredictForecast> rcsPredictForecastList = JSONArray.parseArray(map.get("list").toString(), RcsPredictForecast.class);
                predictCommonService.updateRcsPredictForecast(rcsPredictForecastList);
                redisClient.delete(lastMarketChangeKey);
            }
        }
        //数据库保存
        if (nx) {
            predictCommonService.updateRcsPredictForecast(list);
            log.info("::{}::预测数据计算-让球Forecast计算完成:{}", item.getOrderNo(), item.getBetNo());
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("list", JSONArray.toJSONString(list));
            map.put("marketId", item.getMarketId());
            redisClient.set(lastMarketChangeKey, JSONObject.toJSONString(map));
            Expiry.redisKeyExpiry(redisClient, item.getMatchType(), lastMarketChangeKey);
            log.info("::{}::预测数据计算-订单号让球Forecast计算频率限制，本次跳过入库", item.getOrderNo());
        }

    }

    public List<RcsPredictForecast> calculate(OrderItem orderItem, Integer type) {
        List<RcsPredictForecast> list = new ArrayList<>();
        //计算每个分差 对应的 预期 盈利
        ForecastScopeVo forecastScopeVo = predictCommonService.getLetPointForecastScopeVo(orderItem.getPlayId());
        for (Integer i = forecastScopeVo.getMin(); i <= forecastScopeVo.getMax(); i++) {
            // 盘口值  1/4盘口拆分成两个盘口 计算两次
            List<Double> marketValueList = null;
            if (StringUtils.isNotEmpty(orderItem.getMarketValueNew())) {
                marketValueList = MarketValueUtils.splitMarketValue(orderItem.getMarketValueNew());
            } else {
                marketValueList = MarketValueUtils.splitMarketValue(orderItem.getMarketValue());
            }
            Double result = 0d;
            for (Double marketValue : marketValueList) {
                BigDecimal amount = BigDecimal.ZERO;
                BigDecimal betAmount = orderItem.getBetAmount1();
                if (marketValueList.size() == 2) {
                    betAmount = new BigDecimal(String.valueOf(orderItem.getBetAmount1())).divide(BigDecimal.valueOf(2), 2, RoundingMode.FLOOR);
                }
                if (i + marketValue > 0) {
                    if ("home".equals(handleAsianHandicap(orderItem))) {//大  注单中奖 玩家赢  庄家输 预期盈利为负  需要乘以负一
                        amount = betAmount.multiply(new BigDecimal(String.valueOf(orderItem.getHandleAfterOddsValue())).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
                    } else {//注单没奖 玩家输  庄家赢 预期盈利为正
                        amount = betAmount;
                    }
                } else if (i + marketValue == 0) {//走水 不做处理
                } else {
                    if ("away".equals(handleAsianHandicap(orderItem))) {//小   注单中奖 玩家赢  庄家输 预期盈利为负  需要乘以负一
                        amount = betAmount.multiply(new BigDecimal(String.valueOf(orderItem.getHandleAfterOddsValue())).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
                    } else {//注单没奖 玩家输  庄家赢 预期盈利为正
                        amount = betAmount;
                    }
                }
                //如果是取消订单 取反
                amount = amount.multiply(new BigDecimal(type));
                //缓存处理  预期金额累加/减
                result = redisClient.hincrByFloat(getCacheKey(orderItem), i.toString(), amount.doubleValue());
                Expiry.redisKeyExpiry(redisClient, orderItem.getMatchType(), getCacheKey(orderItem));
                /**升级兼容过渡处理**/
                String key = getCacheKey(orderItem);
                if (key.contains("0:0")) {
                    Double oldvalue = redisClient.hincrByFloat(key.replace("0:0", ""), i.toString(), 0D);
                    if (oldvalue.compareTo(0D) != 0) {
                        log.info("升级数据过渡记录{},{}", key, oldvalue);
                    }
                    result = redisClient.hincrByFloat(key, i.toString(), oldvalue);
                    redisClient.hincrByFloat(key.replace("0:0", ""), i.toString(), oldvalue * -1);
                }
                /**升级兼容过渡处理**/
            }
            //添加到集合
            RcsPredictForecast rcsProfitRectangle = new RcsPredictForecast();
            rcsProfitRectangle.setSportId(orderItem.getSportId());
            rcsProfitRectangle.setMatchId(orderItem.getMatchId());
            rcsProfitRectangle.setMatchType(orderItem.getMatchType());
            rcsProfitRectangle.setPlayId(orderItem.getPlayId());
            rcsProfitRectangle.setSubPlayId(orderItem.getSubPlayId());
            rcsProfitRectangle.setMarketId(orderItem.getMarketId());
            rcsProfitRectangle.setOddsItem(orderItem.getPlayOptions());
            rcsProfitRectangle.setBetScore(orderItem.getScoreBenchmark());
            rcsProfitRectangle.setCreateTime(System.currentTimeMillis());
            rcsProfitRectangle.setMarketValueComplete(orderItem.getMarketValueNew());
            rcsProfitRectangle.setMarketValueCurrent(orderItem.getMarketValue());
            rcsProfitRectangle.setForecastScore(i);
            rcsProfitRectangle.setProfitAmount(new BigDecimal(String.valueOf(result)));
            list.add(rcsProfitRectangle);
        }
        log.info("预测数据计算-让球Forecast结果:{}", JSONObject.toJSONString(list));
        return list;
    }

    private String getCacheKey(OrderItem orderItem) {
        String key = "rcs:risk:predict:forecast.match_id.%s.match_type.%s.play_id.%s.market_id.%s.odds_item.%s.bet_score.%s";
        key = String.format(key, orderItem.getMatchId(), orderItem.getMatchType(), orderItem.getPlayId(), orderItem.getMarketId(), orderItem.getPlayOptions(), StringUtils.isEmpty(orderItem.getScoreBenchmark()) ? "0:0" : orderItem.getScoreBenchmark());
        return key;
    }

    private String handleAsianHandicap(OrderItem item) {
        if ("1".equals(item.getPlayOptions())) {
            return "home";
        } else if ("2".equals(item.getPlayOptions())) {
            //客队让球
            return "away";
        }
        throw new RcsServiceException("选项错误：" + item.getPlayOptions());
    }
}