package com.panda.sport.rcs.predict.mq.db;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.predict.RcsPredictBetOddsMapper;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictBetOdds;
import com.panda.sport.rcs.predict.common.Expiry;
import com.panda.sport.rcs.predict.utils.LongUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        topic = "mq_data_rcs_predict_bet_odds",
        consumerGroup = "mq_data_rcs_predict_bet_odds_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class RcsPredictBetOddsConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private RedisClient redisClient;


    @Autowired
    private RcsPredictBetOddsMapper mapper;

    public RcsPredictBetOddsConsumer() {
//        super("mq_data_rcs_predict_bet_odds");
    }


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(160);
        defaultMQPushConsumer.setConsumeThreadMax(320);
    }

    //    @Override
    public String getCacheKey(RcsPredictBetOdds data, Map<String, String> map) {
        log.info("mq_data_rcs_predict_bet_odds保存收到:{},订单号:{}", JSONObject.toJSONString(data), map.get("orderNo"));
        String lastTimeKey = String.format("rcs:lastTime:mq_data_rcs_predict_bet_odds:%s_%s_%s_%s_%s_%s", data.getMatchId(), data.getPlayId(), data.getMatchType(), data.getDataType(), data.getOddsType(), data.getSeriesType());
        long lastTime = LongUtil.parseLong(redisClient.get(lastTimeKey));
        long currTime = LongUtil.parseLong(map.get("time"));
        if (currTime < lastTime) {
            log.info("mq_data_rcs_predict_bet_odds处理 时间已过期 跳过{}", JSONObject.toJSONString(data));
            return null;
        }
        redisClient.set(lastTimeKey, currTime);
        redisClient.expireKey(lastTimeKey, Expiry.MATCH_EXPIRY);
        return lastTimeKey;
    }

    @Override
    public void onMessage(String str) {
        try {
            List<RcsPredictBetOdds> list = JSONObject.parseObject(str, new TypeReference<List<RcsPredictBetOdds>>() {
            });
            mapper.insertOrUpdate(list);
            log.info("mq_data_rcs_predict_bet_odds保存成功:" + JSONObject.toJSONString(list));
        } catch (Exception e) {
            log.error("mq_data_rcs_predict_bet_odds保存异常" + e.getMessage(), e);
        }
    }
}
