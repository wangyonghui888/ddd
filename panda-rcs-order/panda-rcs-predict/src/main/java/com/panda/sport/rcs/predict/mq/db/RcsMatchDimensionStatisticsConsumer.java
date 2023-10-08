package com.panda.sport.rcs.predict.mq.db;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.statistics.RcsMatchDimensionStatisticsMapper;
import com.panda.sport.rcs.pojo.statistics.RcsMatchDimensionStatistics;
import com.panda.sport.rcs.predict.common.Expiry;
import com.panda.sport.rcs.predict.utils.LongUtil;
import com.panda.sport.rcs.predict.utils.RcsPredictMysqlFrequencyNacosConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author :  lithan
 * @Date: 2021-2-24 10:38:25
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@RocketMQMessageListener(
        topic = RcsConstant.RCS_MATCH_DIMENSION_STATISTICS,
        consumerGroup = RcsConstant.RCS_MATCH_DIMENSION_STATISTICS,
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class RcsMatchDimensionStatisticsConsumer implements RocketMQListener<RcsMatchDimensionStatistics>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private RedisClient redisClient;

    /**
     * 数据库入库限频 redis key
     */
    private static final String RCS_MATCH_DIMENSION_STATISTICS_MYSQL_FREQUENCY_INSERT_KEY = "rcs_match_dimension_statistics_mysql_frequency_insert.matchId.%s";
    @Autowired
    private RcsPredictMysqlFrequencyNacosConfig rcsPredictMysqlFrequencyNacosConfig;

    @Autowired
    private RcsMatchDimensionStatisticsMapper mapper;

    public RcsMatchDimensionStatisticsConsumer() {
//        super("mq_data_rcs_match_dimension_statistics");
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(160);
        defaultMQPushConsumer.setConsumeThreadMax(320);
    }

//    @Override
    public String getCacheKey(RcsMatchDimensionStatistics data, Map<String, String> map) {
        log.info("mq_data_rcs_match_dimension_statistics保存收到:{}", JSONObject.toJSONString(data));
        String lastTimeKey = String.format("rcs:lastTime:mq_data_rcs_match_dimension_statistics:%s", data.getMatchId());
        long lastTime = LongUtil.parseLong(redisClient.get(lastTimeKey));
        long currTime = LongUtil.parseLong(map.get("time"));
        if (currTime < lastTime) {
            log.info("mq_data_rcs_match_dimension_statistics处理 时间已过期 跳过{}", JSONObject.toJSONString(data));
            return null;
        }
        redisClient.set(lastTimeKey, currTime);
        redisClient.expireKey(lastTimeKey, Expiry.MATCH_EXPIRY);
        return lastTimeKey;
    }

    @Override
    public void onMessage(RcsMatchDimensionStatistics data) {
        try {
            String key = String.format(RCS_MATCH_DIMENSION_STATISTICS_MYSQL_FREQUENCY_INSERT_KEY, data.getMatchId());
            if (redisClient.setNX(key, "1", rcsPredictMysqlFrequencyNacosConfig.getForecastInsertMysqlFrequency())) {
                mapper.insertOrSave(data);
                log.info("mq_data_rcs_match_dimension_statistics保存成功:" + JSONObject.toJSONString(data));
            } else {
                log.info("mq_data_rcs_match_dimension_statistics 频率限制！本次跳过入库:" + JSONObject.toJSONString(data));
            }
        } catch (Exception e) {
            log.error("mq_data_rcs_match_dimension_statistics保存异常" + e.getMessage(), e);
        }
    }
}
