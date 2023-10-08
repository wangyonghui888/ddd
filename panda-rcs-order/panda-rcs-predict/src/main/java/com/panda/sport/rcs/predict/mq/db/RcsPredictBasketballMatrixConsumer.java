package com.panda.sport.rcs.predict.mq.db;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.statistics.RcsPredictBasketballMatrixMapper;
import com.panda.sport.rcs.pojo.statistics.RcsPredictBasketballMatrix;
import com.panda.sport.rcs.predict.common.Expiry;
import com.panda.sport.rcs.predict.utils.LongUtil;
import com.panda.sport.rcs.predict.utils.RedisUtilsNxExtend;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author :  lithan
 * @Date: 2021-2-24 10:38:25
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@RocketMQMessageListener(
        topic = "mq_data_rcs_predict_basketball_matrix",
        consumerGroup = "mq_data_rcs_predict_basketball_matrix_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class RcsPredictBasketballMatrixConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RcsPredictBasketballMatrixMapper basketballMatrixMapper;

    @Autowired
    private RedisUtilsNxExtend redisUtilsNxExtend;

    public RcsPredictBasketballMatrixConsumer() {
//        super("mq_data_rcs_predict_basketball_matrix");
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(160);
        defaultMQPushConsumer.setConsumeThreadMax(320);
    }

    //    @Override
    public String getCacheKey(List<RcsPredictBasketballMatrix> data, Map<String, String> map) {
        log.info("mq_data_rcs_predict_basketball_matrix 保存收到:{}", JSONObject.toJSONString(data));
        RcsPredictBasketballMatrix item = data.get(0);
        String uniqueKey = "mq_data_rcs_predict_basketball_matrix:match_id.%s.match_type.%s.play_id.%s";
        uniqueKey = String.format(uniqueKey, item.getMatchId(), item.getMatchType(), item.getPlayId());
        String lastTimeKey = String.format("rcs:lastTime:RcsPredictForecast:%s", uniqueKey);
        long lastTime = LongUtil.parseLong(redisClient.get(lastTimeKey));
        long currTime = LongUtil.parseLong(map.get("time"));
        if (currTime < lastTime) {
            log.info("篮球RcsPredictForecast 处理 时间已过期 跳过{}", JSONObject.toJSONString(data));
            return null;
        }
        redisClient.set(lastTimeKey, currTime);
        redisClient.expireKey(lastTimeKey, Expiry.MATCH_EXPIRY);
        return uniqueKey;
    }

    @Override
    public void onMessage(String str) {
        try {
            List<RcsPredictBasketballMatrix> list = JSONObject.parseObject(str, new TypeReference<List<RcsPredictBasketballMatrix>>() {
            });
            String key = "rcs:risk:predict:basketMatrixBall.match_id.%s.match_type.%s.play_id.%s.forecast_score.%s";
            for (RcsPredictBasketballMatrix entity : list) {
                key = String.format(key, entity.getMatchId(), entity.getMatchType(), entity.getPlayId(), entity.getForecastScore());
                Double profitAmount = redisClient.hincrByFloat(key, entity.getForecastScore().toString(), 0D);
                entity.setProfitAmount(new BigDecimal(profitAmount));
                entity.setHashUnique(DigestUtil.md5Hex(key));
            }
            basketballMatrixMapper.saveOrUpdate(list);
        } catch (Exception e) {
            log.error("篮球forecast保存成功失败" + e.getMessage(), e);
        }
    }
}
