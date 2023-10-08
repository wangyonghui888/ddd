package com.panda.sport.rcs.task.mq.predict;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.predict.RcsPredictBetStatisMapper;
import com.panda.sport.rcs.mq.db.SaveDbDelayUpdateConsumer;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsPredictBetStatis;
import com.panda.sport.rcs.task.utils.TaskCommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 预测forecast表保存
 */
@Component
@Slf4j
public class PredictBetStatisSaveConsumer extends SaveDbDelayUpdateConsumer<RcsPredictBetStatis> {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RcsPredictBetStatisMapper rcsPredictBetStatisMapper;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    public PredictBetStatisSaveConsumer() {
        super("RCS_PREDICT_BET_STATIS_SAVE");
    }

    @Override
    public String getCacheKey(RcsPredictBetStatis item, Map<String, String> paramsMap) {
        log.info("RcsPredictBetStatis 保存收到:{}",JSONObject.toJSONString(item));
        String betStatisKey = "match_id.%s.match_type.%s.play_id.%s.market_id.%s.odds_item.%s.bet_score.%s";
        betStatisKey = String.format(betStatisKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getMarketId(), item.getOddsItem(), item.getBetScore());

        String lastTimeKey = String.format("rcs:lastTime:RcsPredictBetStatis:%s", betStatisKey);
        long lastTime = TaskCommonUtils.getLongValue(redisClient.get(lastTimeKey));
        long currTime = TaskCommonUtils.getLongValue(paramsMap.get("time"));
        if (currTime < lastTime) {
            log.info("RcsPredictBetStatis 处理 时间已过期 跳过{}", JSONObject.toJSONString(item));
            return null;
        }
        redisClient.set(lastTimeKey, currTime);
        return betStatisKey;
    }

    @Override
    public Boolean updateData(RcsPredictBetStatis bean) {
        //记录到缓存
        String betStatisKey = "rcs.risk.predict.betSatis.match_id.%s.match_type.%s.play_id.%s.market_id.%s.odds_item.%s.bet_score.%s";
        betStatisKey = String.format(betStatisKey, bean.getMatchId(), bean.getMatchType(), bean.getPlayId(), bean.getMarketId(), bean.getOddsItem(), bean.getBetScore());
        //累加 总货量
        Long betAmount = redisClient.hincrBy(betStatisKey, "totalBetAmount", 0);
        //累加 注单数量
        Long betNum = redisClient.hincrBy(betStatisKey, "totalBetNum", 0);
        //累加 赔率和
        Double oddsSum = redisClient.hincrByFloat(betStatisKey, "oddsSum", 0D);

        bean.setBetAmount(BigDecimal.valueOf(betAmount).divide(new BigDecimal("100"),2,BigDecimal.ROUND_DOWN));
        bean.setBetNum(betNum);
        bean.setOddsSum(new BigDecimal(oddsSum).divide(new BigDecimal("100000"),2,BigDecimal.ROUND_DOWN));

        rcsPredictBetStatisMapper.saveOrUpdate(bean);
        log.info("RcsPredictBetStatis保存成功:" + JSONObject.toJSONString(bean));
        producerSendMessageUtils.sendMessage("RCS_PREDICT_BET_STATIS_SAVE_WS", "", "", bean);
        return true;
    }
}

