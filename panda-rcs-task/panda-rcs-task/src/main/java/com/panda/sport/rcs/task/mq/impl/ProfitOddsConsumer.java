package com.panda.sport.rcs.task.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.RcsMarketOddsConfigMapper;
import com.panda.sport.rcs.mq.db.SaveDbDelayUpdateConsumer;
import com.panda.sport.rcs.pojo.RcsMarketOddsConfig;
import com.panda.sport.rcs.task.utils.TaskCommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

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
public class ProfitOddsConsumer extends SaveDbDelayUpdateConsumer<RcsMarketOddsConfig> {

    @Autowired
    private RcsMarketOddsConfigMapper rcsMarketOddsConfigMapper;

    @Autowired
    private RedisClient redisClient;

    public ProfitOddsConsumer() {
        super("MYSQL_PROFIT_ODDS");
    }

    @Override
    public String getCacheKey(RcsMarketOddsConfig rcsMarketOddsConfig, Map<String, String> map) {
        log.info("RcsMarketOddsConfig 保存收到:{}",JSONObject.toJSONString(rcsMarketOddsConfig));
        String key = String.format("%s_%s", rcsMarketOddsConfig.getMarketOddsId(), rcsMarketOddsConfig.getMatchType());
        String lastTimeKey = String.format("rcs:lastTime:RcsMarketOddsConfig:%s", key);
        long lastTime = TaskCommonUtils.getLongValue(redisClient.get(lastTimeKey));
        long currTime = TaskCommonUtils.getLongValue(map.get("time"));
        if (currTime < lastTime) {
            log.info("RcsMarketOddsConfig处理 时间已过期 跳过{}", JSONObject.toJSONString(rcsMarketOddsConfig));
            return null;
        }
        redisClient.setExpiry(lastTimeKey, currTime,60 * 60 * 12L);
        return key;
    }

    @Override
    public Boolean updateData(RcsMarketOddsConfig rcsMarketOddsConfig) {
        try {

            //投注项总投注金额
            String oddsTotalBetAmountKey = "rcs.risk.predict.oddsTotalBetAmount.match_id.%s.match_type.%s.play_id.%s.market_id.%s";
            oddsTotalBetAmountKey = String.format(oddsTotalBetAmountKey, rcsMarketOddsConfig.getMatchId(), rcsMarketOddsConfig.getMatchType(), rcsMarketOddsConfig.getMarketCategoryId(), rcsMarketOddsConfig.getMatchMarketId());
            //投注项总投注笔数
            String oddsTotalBetNumKey  = "rcs.risk.predict.oddsTotalBetNum.match_id.%s.match_type.%s.play_id.%s.market_id.%s";
            oddsTotalBetNumKey = String.format(oddsTotalBetNumKey, rcsMarketOddsConfig.getMatchId(), rcsMarketOddsConfig.getMatchType(), rcsMarketOddsConfig.getMarketCategoryId(), rcsMarketOddsConfig.getMatchMarketId());
            //投注项最大赔付
            String oddsTotalPaidAmonutKey = "rcs.risk.predict.oddsTotalPaidAmonut.match_id.%s.match_type.%s.play_id.%s.market_id.%s";
            oddsTotalPaidAmonutKey = String.format(oddsTotalPaidAmonutKey, rcsMarketOddsConfig.getMatchId(), rcsMarketOddsConfig.getMatchType(), rcsMarketOddsConfig.getMarketCategoryId(), rcsMarketOddsConfig.getMatchMarketId());
            //投注项级别的期望值=盘口下所有下注项金额汇总-当前投注项级别最大赔付金额
            String oddsProfitAmonutKey = "rcs.risk.predict.oddsProfitAmonut.match_id.%s.match_type.%s.play_id.%s.market_id.%s";
            oddsProfitAmonutKey = String.format(oddsProfitAmonutKey, rcsMarketOddsConfig.getMatchId(), rcsMarketOddsConfig.getMatchType(), rcsMarketOddsConfig.getMarketCategoryId(), rcsMarketOddsConfig.getMatchMarketId());

            Long betOrderNum = redisClient.hincrBy(oddsTotalBetNumKey, rcsMarketOddsConfig.getMarketOddsId().toString(),0);
            Long profitValue = redisClient.hincrBy(oddsProfitAmonutKey, rcsMarketOddsConfig.getMarketOddsId().toString(),0);
            Long totalBetAmount = redisClient.hincrBy(oddsTotalBetAmountKey, rcsMarketOddsConfig.getMarketOddsId().toString(),0);
            Long paidAmount = redisClient.hincrBy(oddsTotalPaidAmonutKey, rcsMarketOddsConfig.getMarketOddsId().toString(),0);
            rcsMarketOddsConfig.setBetOrderNum(new BigDecimal(betOrderNum));
            rcsMarketOddsConfig.setBetAmount(new BigDecimal(totalBetAmount).divide(BigDecimal.valueOf(100)));
            rcsMarketOddsConfig.setProfitValue(new BigDecimal(profitValue).divide(BigDecimal.valueOf(100)));
            rcsMarketOddsConfig.setPaidAmount(new BigDecimal(paidAmount).divide(BigDecimal.valueOf(100)));


            rcsMarketOddsConfigMapper.insertOrUpdate(rcsMarketOddsConfig);
            log.info("RcsMarketOddsConfig保存成功:" + JSONObject.toJSONString(rcsMarketOddsConfig));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}
