package com.panda.sport.rcs.task.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.statistics.RcsMatchDimensionStatisticsMapper;
import com.panda.sport.rcs.mapper.statistics.RcsProfitMarketMapper;
import com.panda.sport.rcs.mapper.statistics.RcsProfitRectangleMapper;
import com.panda.sport.rcs.mq.db.SaveDbDelayUpdateConsumer;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import com.panda.sport.rcs.pojo.statistics.RcsMatchDimensionStatistics;
import com.panda.sport.rcs.pojo.statistics.RcsProfitMarket;
import com.panda.sport.rcs.pojo.statistics.RcsProfitRectangle;
import com.panda.sport.rcs.task.utils.TaskCommonUtils;
import com.panda.sport.rcs.vo.statistics.ProfitRectangleMQVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.task.mq.impl
 * @Description :  TODO
 * @Date: 2020-06-14 21:25
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
public class ProfitRectangleConsumer extends SaveDbDelayUpdateConsumer<ProfitRectangleMQVo> {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RcsProfitRectangleMapper rcsProfitRectangleMapper;

    public ProfitRectangleConsumer() {
        super("MYSQL_Profit_Rectangle");
    }

    @Override
    public String getCacheKey(ProfitRectangleMQVo profitRectangleMQVo, Map<String, String> map) {
        log.info("ProfitRectangleMQVo 保存收到:{}",JSONObject.toJSONString(profitRectangleMQVo));
        String lastTimeKey = String.format("rcs:lastTime:ProfitRectangle:%s_%s_%s", profitRectangleMQVo.getMatchId(), profitRectangleMQVo.getPlayId(), profitRectangleMQVo.getMatchType());
        long lastTime = TaskCommonUtils.getLongValue(redisClient.get(lastTimeKey));
        long currTime = TaskCommonUtils.getLongValue(map.get("time"));
        if (currTime < lastTime) {
            log.info("RcsMatchDimensionStatistics处理 时间已过期 跳过{}", JSONObject.toJSONString(profitRectangleMQVo));
            return null;
        }
        redisClient.set(lastTimeKey, currTime);
        return profitRectangleMQVo.getId();
    }

    @Override
    public Boolean updateData(ProfitRectangleMQVo mqVo) {
        try {
            ArrayList<RcsProfitRectangle> rcsProfitRectangles = new ArrayList<>();
            Iterator<Map.Entry<Double, RcsProfitRectangle>> iterator = mqVo.getProfitRectangleList().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Double, RcsProfitRectangle> next = iterator.next();
                RcsProfitRectangle bean = next.getValue();
                String key = String.format("rcs:profit:match:%s:%s:%s", bean.getMatchId(),bean.getMatchType(),bean.getPlayId());
                Double profitValue = redisClient.hincrByFloat(key, Double.valueOf(bean.getScore()).toString(), 0D);
                bean.setProfitValue(new BigDecimal(profitValue));
                rcsProfitRectangles.add(bean);
            }
            rcsProfitRectangleMapper.batchInsert(rcsProfitRectangles);
            log.info("RcsProfitRectangle保存成功:" + JSONObject.toJSONString(mqVo.getId()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}
