package com.panda.sport.rcs.predict.service.impl.football.play;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecastPlay;
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

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:  足球玩法级别  让球 forecast
 * @author: lithan
 * @date:  2021-2-21 11:13:28
 **/
@Slf4j
@Service("footPlayLetPointForecastService")
public class FootPlayLetPointForecastServiceImpl implements ForecastService {

    @Autowired
    protected RedisClient redisClient;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Resource(name = "predictCommonService")
    private PredictCommonServiceImpl predictCommonService;

    @Autowired
    private PredictResetRedisKeyBo predictResetRedisKeyBo;

    /**
     * Forecast计算
     */
    @Override
    public void forecastData(OrderItem item, Integer type, boolean nx) {
        if (item.getMatchType() == 1) {
            predictResetRedisKeyBo.resetPlayForecastRedisKey(item, item.getPlayId(), -1);
        }
        List<RcsPredictForecastPlay> list = calculate(item, type);
        // 盘口变动立即将上一次被限制的数据推送出去
        String lastMarketChangeKey = String.format("rcs:risk:predict:marketChangePlay.match_id.%s.match_type.%s.play_id.%s", item.getMatchId(), item.getMatchType(), item.getPlayId());
        String lastMarketChangeData = redisClient.get(lastMarketChangeKey);
        if (!StringUtils.isEmpty(lastMarketChangeData)) {
            Map map = JSONObject.parseObject(lastMarketChangeData, Map.class);
            if (!map.get("marketId").equals(item.getMarketId())) {
                List<RcsPredictForecastPlay> rcsPredictForecastList = JSONArray.parseArray(map.get("list").toString(), RcsPredictForecastPlay.class);
                predictCommonService.updateRcsPredictForecastPlay(rcsPredictForecastList);
                redisClient.delete(lastMarketChangeKey);
            }
        }
        if (nx) {
            predictCommonService.updateRcsPredictForecastPlay(list);
            log.info("::{}::预测数据计算-玩法完成 ", item.getOrderNo());
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("list", JSONArray.toJSONString(list));
            map.put("marketId", item.getMarketId());
            redisClient.set(lastMarketChangeKey, JSONObject.toJSONString(map));
            Expiry.redisKeyExpiry(redisClient, item.getMatchType(), lastMarketChangeKey);
            log.info("::{}::预测数据计算-让分玩法频率限制！本次跳过", item.getOrderNo());
        }
    }



    public List<RcsPredictForecastPlay> calculate(OrderItem orderItem, Integer type) {
        List<RcsPredictForecastPlay> list = new ArrayList<>();
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
                    if ("1".equals(orderItem.getPlayOptions())) {//大  注单中奖 玩家赢  庄家输 预期盈利为负  需要乘以负一
                        amount = betAmount.multiply(new BigDecimal(String.valueOf(orderItem.getHandleAfterOddsValue())).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
                    } else {//注单没奖 玩家输  庄家赢 预期盈利为正
                        amount = betAmount;
                    }
                } else if (i + marketValue == 0) {//走水 不做处理
                } else {
                    if ("2".equals(orderItem.getPlayOptions())) {//小   注单中奖 玩家赢  庄家输 预期盈利为负  需要乘以负一
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
            }
            //添加到集合
            RcsPredictForecastPlay rcsProfitRectangle = new RcsPredictForecastPlay();
            rcsProfitRectangle.setMatchType(orderItem.getMatchType());
            rcsProfitRectangle.setUpdateTime(System.currentTimeMillis());
            rcsProfitRectangle.setCreateTime(System.currentTimeMillis());
            rcsProfitRectangle.setMatchId(orderItem.getMatchId());
            rcsProfitRectangle.setPlayId(orderItem.getPlayId().longValue());
            rcsProfitRectangle.setSubPlayId(orderItem.getSubPlayId());
            rcsProfitRectangle.setDataType(1);
            rcsProfitRectangle.setPlaceNum(-1);
            rcsProfitRectangle.setScore(i);
            rcsProfitRectangle.setProfitValue(new BigDecimal(String.valueOf(result)));
            list.add(rcsProfitRectangle);
        }

        return list;
    }

    private String getCacheKey(OrderItem orderItem) {
        String key = String.format("rcs:profit:match:%s:%s:%s:%s", orderItem.getMatchId(),orderItem.getMatchType(),orderItem.getPlayId(),-1);
        return key;
    }

}