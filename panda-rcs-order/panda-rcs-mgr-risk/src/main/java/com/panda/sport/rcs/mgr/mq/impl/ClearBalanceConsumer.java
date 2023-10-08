package com.panda.sport.rcs.mgr.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.mgr.config.JedisSlotConnectionHandlerImp;
import com.panda.sport.rcs.vo.ClearBalanceVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author :  wealth
 * @Project Name :  panda-rcs-mgr-risk
 * @Package Name :  com.panda.sport.rcs.mgr.mq.impl.ClearBalanceConsumer
 * @Description :  平衡值清理
 * @Date: 2023-02-27 17:30
 * @ModificationHistory --------  ---------  --------------------------
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "RISK_CLEAR_BALANCE_TOPIC",
        consumerGroup = "RISK_CLEAR_BALANCE_TOPIC_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class ClearBalanceConsumer implements RocketMQListener<ClearBalanceVO> {

    @Autowired
    private JedisSlotConnectionHandlerImp jedisSlotConnectionHandlerImp;

    private static List<Integer> X_PLAYS = new ArrayList<>();

    @PostConstruct
    public void init() {
        X_PLAYS.addAll(RcsConstant.FOOTBALL_X_NO_INSERT_PLAYS);
        X_PLAYS.addAll(RcsConstant.FOOTBALL_X_INSERT_PLAYS);
        X_PLAYS.addAll(RcsConstant.BASKETBALL_X_PLAYS);
    }

    @Override
    public void onMessage(ClearBalanceVO clearBalanceVO) {
        log.info("::{}::接收到RISK_CLEAR_BALANCE_TOPIC清理平衡值marketId:{},playId:{}",clearBalanceVO.getMatchId(), clearBalanceVO.getMarketId(),clearBalanceVO.getPlayId());
        try {
            if (Objects.nonNull(clearBalanceVO)) {
                this.clearBalanceValue(clearBalanceVO);
            }
        } catch (Exception e) {
            log.error("::{}::RISK_CLEAR_BALANCE_TOPIC清理平衡值出错:{}", clearBalanceVO.getMatchId(), e.getMessage(), e);
        }
    }

    /**
     * 清理盘口的平称值
     *
     * @param clearBalanceVO
     */
    private void clearBalanceValue(ClearBalanceVO clearBalanceVO) {
        Integer clearType = clearBalanceVO.getClearType();
        if (null == clearBalanceVO.getClearType()) {
            clearType = 0;
        }
        String dateExpect = clearBalanceVO.getDateExpect();
        String oddsType = clearBalanceVO.getOddsType();
        Long sportId = clearBalanceVO.getSportId();
        Long marketId = clearBalanceVO.getMarketId();
        Long matchId = clearBalanceVO.getMatchId();
        Long playId = clearBalanceVO.getPlayId();
        Long subPlayId = clearBalanceVO.getSubPlayId();
        Integer placeNum = clearBalanceVO.getPlaceNum();


        //足
        String key = String.format(RedisKeys.CALC_AMOUNT_ODDS_CHANGE, dateExpect, marketId);
        String keyPlus = String.format(RedisKeys.CALC_AMOUNT_ODDS_CHANGE_PLUS, dateExpect, marketId);
        String keyjumpbet = String.format("rcs:odds:jumpMarket:%s:%s:bet{%s}", dateExpect, dateExpect, marketId);
        String keyjumpmix = String.format("rcs:odds:jumpMarket:%s:%s:mix{%s}", dateExpect, dateExpect, marketId);
        String suffixKey = "{" + marketId + "}";
        log.info("::{}::clearBalanceValue-0-key:{},keyPlus{},suffixKey{}", "RTRCMMTG_" + matchId + "_" + clearType, key, keyPlus, suffixKey);
        if (RcsConstant.FOOTBALL_MOST_ODDS_TYPE_PLAYS.contains(playId.intValue()) && (1 == clearType || 2 == clearType)) {
            log.info("::{}::clearBalanceValue-1-key:{},keyPlus{},suffixKey{}", "RTRCMMTG_" + matchId + "_" + clearType, key, keyPlus, suffixKey);

            List<Function<Pipeline, Object>> list = new ArrayList<>();
            String finalKey = key;
            String finalSuffixKey = suffixKey;
            String finalKeyPlus = keyPlus;
            list.add(e -> e.hdel(finalKey + finalSuffixKey, String.valueOf(oddsType)));
            list.add(e -> e.hdel(finalKeyPlus + finalSuffixKey, String.valueOf(oddsType)));
            list.add(e -> e.hdel(finalKey + ":count" + finalSuffixKey, String.valueOf(oddsType)));
            list.add(e -> e.hdel(finalKey + ":lock" + finalSuffixKey, String.valueOf(oddsType)));
            exePipeline(list, finalKey + finalSuffixKey);
        } else {
            log.info("::{}::clearBalanceValue-2-key:{},keyPlus{},suffixKey{}", "RTRCMMTG_" + matchId + "_" + clearType, key, keyPlus, suffixKey);

            List<Function<Pipeline, Object>> list = new ArrayList<>();
            String finalKey = key;
            String finalSuffixKey = suffixKey;
            String finalKeyPlus = keyPlus;
            list.add(e -> e.del(finalKey + finalSuffixKey));
            list.add(e -> e.del(finalKeyPlus + finalSuffixKey));
            list.add(e -> e.del(finalKey + ":count" + finalSuffixKey));
            list.add(e -> e.del(finalKey + ":lock" + finalSuffixKey));
            exePipeline(list, finalKey + finalSuffixKey);
        }
        //篮网
        //带x玩法判断
        if (X_PLAYS.contains(playId.intValue()) || RcsConstant.TETC.contains(sportId.intValue())) {
            suffixKey = "{" + String.format("%s_%s_%s_%s", matchId, playId, subPlayId, placeNum) + "}";
            key = String.format(RedisKeys.CALC_AMOUNT_ODDS_CHANGE, dateExpect, String.format("%s_%s_%s_%s", matchId, playId, subPlayId, placeNum));
            keyPlus = String.format(RedisKeys.CALC_AMOUNT_ODDS_CHANGE_PLUS, dateExpect, String.format("%s_%s_%s_%s", matchId, playId, subPlayId, placeNum));
            String placeId = String.format("%s_%s_%s", matchId, playId, placeNum);
            keyjumpbet = String.format("rcs:odds:jumpMarket:%s:%s:bet{%s}", dateExpect, placeId, placeId);
            keyjumpmix = String.format("rcs:odds:jumpMarket:%s:%s:mix{%s}", dateExpect, placeId, placeId);
        } else {
            suffixKey = "{" + String.format("%s_%s_%s", matchId, playId, placeNum) + "}";
            key = String.format(RedisKeys.CALC_AMOUNT_ODDS_CHANGE, dateExpect, String.format("%s_%s_%s", matchId, playId, placeNum));
            keyPlus = String.format(RedisKeys.CALC_AMOUNT_ODDS_CHANGE_PLUS, dateExpect, String.format("%s_%s_%s", matchId, playId, placeNum));
            String placeId = String.format("%s_%s_%s", matchId, playId, placeNum);
            keyjumpbet = String.format("rcs:odds:jumpMarket:%s:%s:bet{%s}", dateExpect, placeId, placeId);
            keyjumpmix = String.format("rcs:odds:jumpMarket:%s:%s:mix{%s}", dateExpect, placeId, placeId);
        }
        if (!(RcsConstant.FOOTBALL_MOST_ODDS_TYPE_PLAYS.contains(playId.intValue()) && (1 == clearType || 2 == clearType))) {
            log.info("::{}::clearBalanceValue-3-key:{},keyPlus{},suffixKey{},keyjumpbet{},keyjumpmix{}", "RTRCMMTG_" + matchId + "_" + clearType, key, keyPlus, suffixKey, keyjumpbet, keyjumpmix);


            List<Function<Pipeline, Object>> list = new ArrayList<>();
            String finalKey = key;
            String finalSuffixKey = suffixKey;
            String finalKeyPlus = keyPlus;
            list.add(e -> e.del(finalKey + finalSuffixKey));
            list.add(e -> e.del(finalKeyPlus + finalSuffixKey));
            list.add(e -> e.del(finalKey + ":count" + finalSuffixKey));
            list.add(e -> e.del(finalKey + ":lock" + finalSuffixKey));
            exePipeline(list, finalKey + finalSuffixKey);

            list.clear();
            String finalKeyJumpBet = keyjumpbet;
            String finalKeyJumpMix = keyjumpmix;
            list.add(e -> e.del(finalKeyJumpBet));
            list.add(e -> e.del(finalKeyJumpMix));
            exePipeline(list, finalKeyJumpBet);
        }
    }


    /**
     * 同一个节点数据批量管道执行
     *
     * @param list
     * @param key
     */
    public void exePipeline(List<Function<Pipeline, Object>> list, String key) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Jedis jedis = null;
        Pipeline pipeline = null;
        try {
            JedisPool jedisPool = jedisSlotConnectionHandlerImp.getJedisPoolFromSlot(key);
            jedis = jedisPool.getResource();
            pipeline = jedis.pipelined();
            for (Function<Pipeline, Object> pipelineObjectFunction : list) {
                pipelineObjectFunction.apply(pipeline);
            }
        } catch (Exception e) {
            log.error("批量执行清理平衡值exePipeline error:{}", e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (pipeline != null) {
                    pipeline.close();
                }
            } catch (IOException e) {
                log.error("批量执行清理平衡值exePipeline error:{}", e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
//        log.info("批量执行清理平衡值：key{},数量:{},完成", key, list.size());
    }
}
