package com.panda.sport.rcs.task.mq.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.mq.db.SaveDbDelayUpdateConsumer;
import com.panda.sport.rcs.task.utils.TaskCommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.statistics.RcsMatchDimensionStatisticsMapper;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import com.panda.sport.rcs.pojo.statistics.RcsMatchDimensionStatistics;

import lombok.extern.slf4j.Slf4j;

/**
 * 赛事盘口赔率改变更新对应mongodb match_market_live
 *
 * @author black
 */
@Component
@Slf4j
public class MatchDimensionStatisticsConsumer extends SaveDbDelayUpdateConsumer<RcsMatchDimensionStatistics> {
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RcsMatchDimensionStatisticsMapper rcsMatchDimensionStatisticsMapper;

    public MatchDimensionStatisticsConsumer() {
        super("MYSQL_DIMENSION_STATISTICS");
    }

    @Override
    public String getCacheKey(RcsMatchDimensionStatistics rcsMatchDimensionStatistics, Map<String, String> map) {
        log.info("RcsMatchDimensionStatistics 保存收到:{}",JSONObject.toJSONString(rcsMatchDimensionStatistics));
        String lastTimeKey = String.format("rcs:lastTime:RcsMatchDimensionStatistics:%s", rcsMatchDimensionStatistics.getMatchId());
        long lastTime = TaskCommonUtils.getLongValue(redisClient.get(lastTimeKey));
        long currTime = TaskCommonUtils.getLongValue(map.get("time"));
        if (currTime < lastTime) {
            log.info("RcsMatchDimensionStatistics处理 时间已过期 跳过{}", JSONObject.toJSONString(rcsMatchDimensionStatistics));
            return null;
        }
        redisClient.setExpiry(lastTimeKey, currTime,60 * 60 * 24L);
        return rcsMatchDimensionStatistics.getMatchId().toString();
    }

    @Override
    public Boolean updateData(RcsMatchDimensionStatistics rcsMatchDimensionStatistics) {
        try {
            rcsMatchDimensionStatisticsMapper.insertOrSave(rcsMatchDimensionStatistics);
            log.info("RcsMatchDimensionStatistics保存成功:" + JSONObject.toJSONString(rcsMatchDimensionStatistics));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}

