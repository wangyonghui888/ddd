package com.panda.sport.rcs.predict.mq.db;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.predict.RcsPredictBetStatisMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.vo.ActualVolumeVO;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictBetStatis;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        topic = "mq_data_rcs_predict_bet_statis",
        consumerGroup = "mq_data_rcs_predict_bet_statis_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class RcsPredictBetStatisConsumer implements RocketMQListener<RcsPredictBetStatis>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private RedisClient redisClient;


    @Autowired
    private RcsPredictBetStatisMapper rcsPredictBetStatisMapper;

    @Autowired
    ProducerSendMessageUtils producerSendMessageUtils;


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(160);
        defaultMQPushConsumer.setConsumeThreadMax(320);
    }

    public RcsPredictBetStatisConsumer() {
//        super("mq_data_rcs_predict_bet_statis");
    }

    //    @Override
    public String getCacheKey(RcsPredictBetStatis data, Map<String, String> map) {
        log.info("mq_data_rcs_predict_bet_statis保存收到:{}", JSONObject.toJSONString(data));
        String betStatisKey = "rcs:risk:predict:betSatis.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.market_id.%s.odds_item.%s.bet_score.%s";
        betStatisKey = String.format(betStatisKey, data.getMatchId(), data.getMatchType(), data.getPlayId(), data.getSubPlayId(), data.getMarketId(), data.getOddsItem(), data.getBetScore());
        String lastTimeKey = String.format("rcs:lastTime:rcs_predict_bet_statis:%s", betStatisKey);
        long lastTime = LongUtil.parseLong(redisClient.get(lastTimeKey));
        long currTime = LongUtil.parseLong(map.get("time"));
        if (currTime < lastTime) {
            log.info("mq_data_rcs_predict_bet_statis处理 时间已过期 跳过{}", JSONObject.toJSONString(data));
            return null;
        }
        redisClient.set(lastTimeKey, currTime);
        redisClient.expireKey(lastTimeKey, Expiry.MATCH_EXPIRY);
        return lastTimeKey;
    }

    @Override
    public void onMessage(RcsPredictBetStatis bean) {
        //记录到缓存
        String betStatisKey;
        if (!SportIdEnum.isFootball(bean.getSportId())) {
            betStatisKey = "rcs:risk:predict:betSatis.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.market_id.%s.odds_item.%s.0:0";
            betStatisKey = String.format(betStatisKey, bean.getMatchId(), bean.getMatchType(), bean.getPlayId(), bean.getSubPlayId(), bean.getMarketId(), bean.getOddsItem());

        } else {
            betStatisKey = "rcs:risk:predict:betSatis.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.market_id.%s.odds_item.%s.bet_score.%s";
            betStatisKey = String.format(betStatisKey, bean.getMatchId(), bean.getMatchType(), bean.getPlayId(), bean.getSubPlayId(), bean.getMarketId(), bean.getOddsItem(), bean.getBetScore());
        }
        //累加 总货量
        Long betAmount = redisClient.hincrBy(betStatisKey, "totalBetAmount", 0);
        //累加 总货量
        Long betAmountPay = redisClient.hincrBy(betStatisKey, "totalBetAmountPay", 0);
        //累加 总货量
        Long betAmountComplex = redisClient.hincrBy(betStatisKey, "totalBetAmountComplex", 0);
        //累加 注单数量
        Long betNum = redisClient.hincrBy(betStatisKey, "totalBetNum", 0);
        //累加 赔率和
        Double oddsSum = redisClient.hincrByFloat(betStatisKey, "oddsSum", 0D);

        bean.setBetAmount(BigDecimal.valueOf(betAmount).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN));
        bean.setBetAmountPay(BigDecimal.valueOf(betAmountPay).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN));
        bean.setBetAmountComplex(BigDecimal.valueOf(betAmountComplex).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN));
        bean.setBetNum(betNum);
        bean.setHashUnique(DigestUtil.md5Hex(betStatisKey));
        bean.setOddsSum(new BigDecimal(oddsSum).divide(new BigDecimal("100000"), 2, BigDecimal.ROUND_DOWN));
        rcsPredictBetStatisMapper.saveOrUpdate(bean);
        List<ActualVolumeVO> rcsPredictBetStatisVo = rcsPredictBetStatisMapper.getRcsPredictBetStatisVo(bean.getMarketId(), bean.getMatchType());
        if (!CollectionUtils.isEmpty(rcsPredictBetStatisVo)) {
            rcsPredictBetStatisVo.stream().filter(t -> !StringUtils.isEmpty(t.getMarketValueComplete())).forEach(item ->
                    item.setMarketValueComplete(convertOdds(item.getMarketValueComplete())));
        }
        bean.setActualVolumeVOList(rcsPredictBetStatisVo);
        log.info("RcsPredictBetStatis保存成功:{}", betStatisKey);
        producerSendMessageUtils.sendMessage("RCS_PREDICT_BET_STATIS_SAVE_WS", "", "", bean);
    }

    private String convertOdds(String oddsValue) {
        String newValue = "", temp = "";
        if (oddsValue.contains("/")) {
            if (oddsValue.startsWith("+")) {
                newValue = oddsValue.replace("+", "");
            } else if (oddsValue.startsWith("-")) {
                newValue = oddsValue.replace("-", "");
                temp = "-";
            } else {
                newValue = oddsValue;
            }
            String[] split = newValue.split("/");
            BigDecimal divide = new BigDecimal(split[0]).add(new BigDecimal(split[1])).divide(new BigDecimal(2), 2, RoundingMode.DOWN);
            return temp + divide;
        }
        return oddsValue;
    }
}
