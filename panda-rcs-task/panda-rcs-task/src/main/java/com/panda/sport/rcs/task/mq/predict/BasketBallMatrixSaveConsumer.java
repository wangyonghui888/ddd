package com.panda.sport.rcs.task.mq.predict;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.predict.RcsPredictBasketballMatrixMapper;
import com.panda.sport.rcs.mapper.predict.RcsPredictForecastMapper;
import com.panda.sport.rcs.mq.db.SaveDbDelayUpdateConsumer;
import com.panda.sport.rcs.pojo.RcsPredictBasketballMatrix;
import com.panda.sport.rcs.pojo.RcsPredictForecast;
import com.panda.sport.rcs.task.utils.TaskCommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 篮球矩阵保存
 */
@Component
@Slf4j
public class BasketBallMatrixSaveConsumer extends SaveDbDelayUpdateConsumer<List<RcsPredictBasketballMatrix>> {


    @Autowired
    private RcsPredictBasketballMatrixMapper basketballMatrixMapper;

    @Autowired
    private RedisClient redisClient;

    public BasketBallMatrixSaveConsumer() {
        super("RCS_PREDICT_BASKETBALL_MATRIX_SAVE");
    }

    @Override
    public String getCacheKey(List<RcsPredictBasketballMatrix> list, Map<String, String> paramsMap) {
    	String linkId = "BasketBallMatrixSaveConsumer";
    	log.info("::{}::-篮球BasketBallMatrixSaveConsumer 保存收到:{}", linkId, JSONObject.toJSONString(list));
        RcsPredictBasketballMatrix item = list.get(0);
        String uniqueKey = "match_id.%s.match_type.%s.play_id.%s";
        uniqueKey = String.format(uniqueKey, item.getMatchId(), item.getMatchType(), item.getPlayId());
        String lastTimeKey = String.format("rcs:lastTime:RcsPredictForecast:%s", uniqueKey);
        long lastTime = TaskCommonUtils.getLongValue(redisClient.get(lastTimeKey));
        long currTime = TaskCommonUtils.getLongValue(paramsMap.get("time"));
        if (currTime < lastTime) {
        	log.info("::{}::-篮球RcsPredictForecast 处理 时间已过期 跳过：{}", linkId, JSONObject.toJSONString(list));
            return null;
        }
        redisClient.set(lastTimeKey, currTime);
        return uniqueKey;
    }

    @Override
    public Boolean updateData(List<RcsPredictBasketballMatrix> list) {
    	String linkId = "BasketBallMatrixSaveConsumer";
        try {
            String key = "rcs.risk.predict.basketMatrixBall.match_id.%s.match_type.%s.play_id.%s";
            for (RcsPredictBasketballMatrix entity : list) {
                key = String.format(key, entity.getMatchId(), entity.getMatchType(), entity.getPlayId());
                Double profitAmount = redisClient.hincrByFloat(key, entity.getForecastScore().toString(), 0D);
                if(entity.getProfitAmount().intValue()!=profitAmount.intValue()){
                	log.info("::{}::-篮球forecast数据{}缓存更新为{}", linkId, JSONObject.toJSONString(entity), profitAmount);
                }
                entity.setProfitAmount(new BigDecimal(profitAmount));
                basketballMatrixMapper.saveOrUpdate(entity);
            }
            log.info("::{}::-篮球forecast保存成功:{}", linkId,  JSONObject.toJSONString(list));
        } catch (Exception e) {
        	log.error("::{}::-篮球forecast保存失败：{}" , linkId, e.getMessage(), e);
            return false;
        }
        return true;
    }
}

