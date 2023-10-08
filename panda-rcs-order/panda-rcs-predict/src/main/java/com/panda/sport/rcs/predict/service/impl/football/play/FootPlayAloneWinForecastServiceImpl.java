package com.panda.sport.rcs.predict.service.impl.football.play;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecastPlay;
import com.panda.sport.rcs.predict.common.Expiry;
import com.panda.sport.rcs.predict.service.ForecastService;
import com.panda.sport.rcs.predict.service.impl.PredictCommonServiceImpl;
import com.panda.sport.rcs.predict.service.impl.PredictResetRedisKeyBo;
import com.panda.sport.rcs.predict.vo.ForecastScopeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
@Service("footPlayAloneWinForecastService")
public class FootPlayAloneWinForecastServiceImpl implements ForecastService {

    @Autowired
    protected RedisClient redisClient;

    @Resource(name = "predictCommonService")
    private PredictCommonServiceImpl predictCommonService;

    public static HashMap<String, String> playMapping = new HashMap<String, String>() {
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
    @Autowired
    private PredictResetRedisKeyBo predictResetRedisKeyBo;

    /**
     * Forecast计算
     */
    @Override
    public void forecastData(OrderItem item, Integer type, boolean nx) {
        if (!playMapping.containsKey(String.valueOf(item.getPlayId()))) {
            return;
        }
        if (item.getMatchType() == 1) {
            predictResetRedisKeyBo.resetPlayForecastRedisKey(item, Integer.valueOf(playMapping.get(String.valueOf(item.getPlayId()))), -1);
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
            log.info("::{}::预测数据计算-独赢玩法完成", item.getOrderNo());
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("list", JSONArray.toJSONString(list));
            map.put("marketId", item.getMarketId());
            redisClient.set(lastMarketChangeKey, JSONObject.toJSONString(map));
            Expiry.redisKeyExpiry(redisClient, item.getMatchType(), lastMarketChangeKey);
            log.info("::{}::预测数据计算-独赢玩法频率限制！本次跳过！", item.getOrderNo());
        }

    }



    public List<RcsPredictForecastPlay> calculate(OrderItem orderItem, Integer type) {
        List<RcsPredictForecastPlay> list = new ArrayList<>();
        
        String playOptions = orderItem.getPlayOptions();
        String placePlayId = playMapping.get(String.valueOf(orderItem.getPlayId()));
        Double result = 0d;
        ForecastScopeVo forecastScopeVo = predictCommonService.getLetPointForecastScopeVo(Integer.valueOf(placePlayId));
        for (Integer i = forecastScopeVo.getMin(); i <= forecastScopeVo.getMax(); i++) {
        	BigDecimal amount = BigDecimal.ZERO;
        	BigDecimal betAmount = orderItem.getBetAmount1();
        	
        	if(playOptions.equals("1")) {
        		if(i > 0 ) {//胜  注单中奖 玩家赢  庄家输 预期盈利为负  需要乘以负一
        			amount = betAmount.multiply(new BigDecimal(String.valueOf(orderItem.getHandleAfterOddsValue())).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
        		}else {
        			amount = betAmount;
        		}
        	}else if (playOptions.equals("X")) {
        		if(i == 0 ) {//胜  注单中奖 玩家赢  庄家输 预期盈利为负  需要乘以负一
        			amount = betAmount.multiply(new BigDecimal(String.valueOf(orderItem.getHandleAfterOddsValue())).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
        		}else {
        			amount = betAmount;
        		}
        	}else if (playOptions.equals("2")) {
        		if(i < 0 ) {//胜  注单中奖 玩家赢  庄家输 预期盈利为负  需要乘以负一
        			amount = betAmount.multiply(new BigDecimal(String.valueOf(orderItem.getHandleAfterOddsValue())).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
        		}else {
                    amount = betAmount;
                }
            } else {
                return null;
            }

            //如果是取消订单 取反
            amount = amount.multiply(new BigDecimal(type));
            //缓存处理  预期金额累加/减
            result = redisClient.hincrByFloat(getCacheKey(orderItem, placePlayId), i.toString(), amount.doubleValue());
            Expiry.redisKeyExpiry(redisClient, orderItem.getMatchType(), getCacheKey(orderItem, placePlayId));

            //添加到集合
            RcsPredictForecastPlay rcsProfitRectangle = new RcsPredictForecastPlay();
            rcsProfitRectangle.setMatchType(orderItem.getMatchType());
            rcsProfitRectangle.setUpdateTime(System.currentTimeMillis());
            rcsProfitRectangle.setCreateTime(System.currentTimeMillis());
            rcsProfitRectangle.setMatchId(orderItem.getMatchId());
            rcsProfitRectangle.setPlayId(Long.parseLong(placePlayId));
            rcsProfitRectangle.setSubPlayId(orderItem.getSubPlayId());
            rcsProfitRectangle.setDataType(1);
            rcsProfitRectangle.setPlaceNum(-1);
            rcsProfitRectangle.setScore(i);
            rcsProfitRectangle.setProfitValue(new BigDecimal(String.valueOf(result)));
            list.add(rcsProfitRectangle);
        }

        return list;
    }

    private String getCacheKey(OrderItem orderItem, String playId) {
        String key = String.format("rcs:profit:match:%s:%s:%s:%s", orderItem.getMatchId(),orderItem.getMatchType(),playId,-1);
        return key;
    }

}