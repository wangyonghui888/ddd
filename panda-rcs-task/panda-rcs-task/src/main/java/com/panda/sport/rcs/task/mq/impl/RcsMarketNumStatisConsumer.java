package com.panda.sport.rcs.task.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.RcsMarketNumStatisMapper;
import com.panda.sport.rcs.mapper.RcsMarketOddsConfigMapper;
import com.panda.sport.rcs.mq.db.SaveDbDelayUpdateConsumer;
import com.panda.sport.rcs.pojo.RcsMarketNumStatis;
import com.panda.sport.rcs.pojo.RcsMarketOddsConfig;
import com.panda.sport.rcs.task.utils.TaskCommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author :  lithan
 * @Date: 2020-10-03 16:23:39
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
public class RcsMarketNumStatisConsumer extends SaveDbDelayUpdateConsumer<RcsMarketNumStatis> {

    @Autowired
    private RcsMarketNumStatisMapper rcsMarketNumStatisMapper;

    @Autowired
    private RedisClient redisClient;

    public RcsMarketNumStatisConsumer() {
        super("rcs_market_num_statis_data");
    }

    @Override
    public String getCacheKey(RcsMarketNumStatis item, Map<String, String> map) {
        log.info("RcsMarketNumStatis 保存收到:{}",JSONObject.toJSONString(item));
        String key = String.format("%s_%s_%s_%s_%s", item.getMatchId(), item.getMarketCategoryId(), item.getPlaceNum(), item.getMatchType(), item.getOddsType());
        String lastTimeKey = String.format("rcs:lastTime:RcsMarketOddsConfig:%s", key);
        long lastTime = TaskCommonUtils.getLongValue(redisClient.get(lastTimeKey));
        long currTime = TaskCommonUtils.getLongValue(map.get("time"));
        if (currTime < lastTime) {
            log.info("RcsMarketNumStatis 处理 时间已过期 跳过{}", JSONObject.toJSONString(item));
            return null;
        }
        redisClient.set(lastTimeKey, currTime);
        return key;
    }

    @Override
    public Boolean updateData(RcsMarketNumStatis numStatis) {
        try {
            //盘口位置 总投注金额
            String placeNumTotalBetAmonutKey = "rcs.risk.predict.palceNumTotalBetAmount.match_id.%s.match_type.%s.play_id.%s.place_num.%s";
            placeNumTotalBetAmonutKey = String.format(placeNumTotalBetAmonutKey, numStatis.getMatchId(), numStatis.getMatchType(), numStatis.getMarketCategoryId(), numStatis.getPlaceNum());
            //盘口位置  总投注笔数
            String getPalceNumTotalBetNumKey = "rcs.risk.predict.palceNumTotalBetNum.match_id.%s.match_type.%s.play_id.%s.place_num.%s";
            getPalceNumTotalBetNumKey = String.format(getPalceNumTotalBetNumKey, numStatis.getMatchId(), numStatis.getMatchType(), numStatis.getMarketCategoryId(), numStatis.getPlaceNum());

            Long betOrderNum = redisClient.hincrBy(getPalceNumTotalBetNumKey, numStatis.getOddsType(),0);
            Long totalBetAmount = redisClient.hincrBy(placeNumTotalBetAmonutKey, numStatis.getOddsType(),0);
            numStatis.setBetOrderNum(new BigDecimal(betOrderNum));
            numStatis.setBetAmount(new BigDecimal(totalBetAmount).divide(BigDecimal.valueOf(100)));

            rcsMarketNumStatisMapper.insertOrUpdate(numStatis);
            log.info("RcsMarketNumStatis保存成功:" + JSONObject.toJSONString(numStatis));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}
