package com.panda.sport.rcs.task.mq.predict;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.predict.RcsPredictForecastMapper;
import com.panda.sport.rcs.mq.db.SaveDbDelayUpdateConsumer;
import com.panda.sport.rcs.pojo.RcsPredictForecast;
import com.panda.sport.rcs.task.utils.TaskCommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 预测货量表rcs_predict_bet_statis保存
 */
@Component
@Slf4j
public class PredictForecastSaveConsumer extends SaveDbDelayUpdateConsumer<List<RcsPredictForecast>> {
    private String uniqueKey = "match_id.%s.match_type.%s.play_id.%s.market_id.%s.odds_item.%s.bet_score.%s";

    @Autowired
    private RcsPredictForecastMapper rcsPredictForecastMapper;

    @Autowired
    private RedisClient redisClient;

    public PredictForecastSaveConsumer() {
        super("RCS_PREDICT_FORECAST_SAVE");
    }

    @Override
    public String getCacheKey(List<RcsPredictForecast> list, Map<String, String> paramsMap) {
        log.info("RcsPredictForecast 保存收到:{}", JSONObject.toJSONString(list));
        RcsPredictForecast item = list.get(0);
        String uniqueKey = "match_id.%s.match_type.%s.play_id.%s.market_id.%s.bet_score.%s";
        uniqueKey = String.format(uniqueKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getMarketId(), item.getBetScore());
        String lastTimeKey = String.format("rcs:lastTime:RcsPredictForecast:%s", uniqueKey);
        long lastTime = TaskCommonUtils.getLongValue(redisClient.get(lastTimeKey));
        long currTime = TaskCommonUtils.getLongValue(paramsMap.get("time"));
        if (currTime < lastTime) {
            log.info("RcsPredictForecast 处理 时间已过期 跳过{}", JSONObject.toJSONString(list));
            return null;
        }
        redisClient.set(lastTimeKey, currTime);
        return uniqueKey;
    }

    @Override
    public Boolean updateData(List<RcsPredictForecast> list) {
        try {
            String key = "rcs.risk.predict.forecast.match_id.%s.match_type.%s.play_id.%s.market_id.%s.bet_score.%s";
            for (RcsPredictForecast entity : list) {
                key = String.format(key, entity.getMatchId(), entity.getMatchType(), entity.getPlayId(), entity.getMarketId(), entity.getBetScore());
                Double profitAmount = redisClient.hincrByFloat(key, entity.getForecastScore().toString(), 0D);
                if(entity.getProfitAmount().intValue()!=profitAmount.intValue()){
                    log.info("forecast数据{}缓存更新为{}", JSONObject.toJSONString(entity), profitAmount);
                }
                entity.setProfitAmount(new BigDecimal(profitAmount));
                rcsPredictForecastMapper.saveOrUpdate(entity);
            }
            log.info("forecast保存成功:" + JSONObject.toJSONString(list));
        } catch (Exception e) {
            log.error("forecast保存成功失败" + e.getMessage(), e);
            return false;
        }
        return true;
    }
}

