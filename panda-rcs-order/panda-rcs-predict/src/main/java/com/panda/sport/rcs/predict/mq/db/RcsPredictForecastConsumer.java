package com.panda.sport.rcs.predict.mq.db;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.predict.RcsPredictForecastMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.vo.api.response.BetForMarketResWsVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecast;
import com.panda.sport.rcs.predict.common.Expiry;
import com.panda.sport.rcs.predict.common.ForecastPlayIds;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author :  lithan
 * @Date: 2021-2-24 10:38:25
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@RocketMQMessageListener(
        topic = "mq_data_rcs_predict_forecast",
        consumerGroup = "mq_data_rcs_predict_forecast_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class RcsPredictForecastConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RcsPredictForecastMapper rcsPredictForecastMapper;


    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(160);
        defaultMQPushConsumer.setConsumeThreadMax(320);
    }


    public RcsPredictForecastConsumer() {
//        super("mq_data_rcs_predict_forecast");
    }

//    @Override
    public String getCacheKey(List<RcsPredictForecast> data, Map<String, String> map) {
        RcsPredictForecast item = data.get(0);
        log.info("mq_data_rcs_predict_forecast保存收到:{}", JSONObject.toJSONString(data));
        String uniqueKey = "match_id.%s.match_type.%s.play_id.%s.market_id.%s.odds_item.%s.bet_score.%s";
        uniqueKey = String.format(uniqueKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getMarketId(),item.getOddsItem(), item.getBetScore());
        String lastTimeKey = String.format("rcs:lastTime:RcsPredictForecast:%s", uniqueKey);
        long lastTime = LongUtil.parseLong(redisClient.get(lastTimeKey));
        long currTime = LongUtil.parseLong(map.get("time"));
        if (currTime < lastTime) {
            log.info("mq_data_rcs_predict_forecast处理 时间已过期 跳过{}", JSONObject.toJSONString(data));
            return null;
        }
        redisClient.set(lastTimeKey, currTime);
        redisClient.expireKey(lastTimeKey, Expiry.MATCH_EXPIRY);
        return lastTimeKey;
    }

    @Override
    public void onMessage(String str) {
        try {
            List<RcsPredictForecast> list = JSONObject.parseObject(str, new TypeReference<List<RcsPredictForecast>>() {
            });
            String key = "rcs:risk:predict:forecast.match_id.%s.match_type.%s.play_id.%s.market_id.%s.odds_item.%s.bet_score.%s";
            for (RcsPredictForecast entity : list) {
                key = String.format(key, entity.getMatchId(), entity.getMatchType(), entity.getPlayId(), entity.getMarketId(), entity.getOddsItem(), entity.getBetScore());
                Double profitAmount = redisClient.hincrByFloat(key, entity.getForecastScore().toString(), 0D);
                entity.setProfitAmount(new BigDecimal(profitAmount));
                entity.setHashUnique(DigestUtil.md5Hex(key));
            }
            rcsPredictForecastMapper.saveOrUpdate(list);
            log.info("forecast保存成功:" + JSONObject.toJSONString(list));
            tows(list);
        } catch (Exception e) {
            log.error("forecast保存成功失败" + e.getMessage(), e);
        }
    }

    /**
     * 推送坑位货量表
     */
    private void tows(List<RcsPredictForecast> list) {
        RcsPredictForecast data = list.get(0);
        BetForMarketResWsVo wsVo = new BetForMarketResWsVo();
        wsVo.setMatchId(data.getMatchId());
        wsVo.setPlayId(data.getPlayId().intValue());
        wsVo.setSubPlayId(data.getSubPlayId());
        wsVo.setMarketId(data.getMarketId());
        wsVo.setMatchType(data.getMatchType());
        wsVo.setBetScore(data.getBetScore());
        wsVo.setMarketValueComplete(data.getMarketValueComplete());
        wsVo.setMarketValueCurrent(data.getMarketValueCurrent());


        //获取主队oddstype
        String oddsType = getOddsTypeByPlayId(data.getPlayId().intValue(), 1);
        String betStatisKey = "rcs:risk:predict:betSatis.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.market_id.%s.odds_item.%s.bet_score.%s";
        betStatisKey = String.format(betStatisKey, data.getMatchId(), data.getMatchType(), data.getPlayId(), data.getSubPlayId(), data.getMarketId(), oddsType, data.getBetScore());
        Long betAmount = redisClient.hincrBy(betStatisKey, "totalBetAmount", 0);
        Long betAmountPay = redisClient.hincrBy(betStatisKey, "totalBetAmountPay", 0);
        Long betAmountComplex = redisClient.hincrBy(betStatisKey, "totalBetAmountComplex", 0);
        //货量-纯投注额-主队
        wsVo.setHomeBetAmount(BigDecimal.valueOf(betAmount).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN));
        //货量-纯赔付额-主队
        wsVo.setHomeBetAmountPay(BigDecimal.valueOf(betAmountPay).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN));
        //货量-混合型（
        wsVo.setHomeBetAmountComplex(BigDecimal.valueOf(betAmountComplex).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN));

        //获取客队oddstype
        oddsType = getOddsTypeByPlayId(data.getPlayId().intValue(), 2);
        betStatisKey = "rcs:risk:predict:betSatis.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.market_id.%s.odds_item.%s.bet_score.%s";
        betStatisKey = String.format(betStatisKey, data.getMatchId(), data.getMatchType(), data.getPlayId(), data.getSubPlayId(), data.getMarketId(), oddsType, data.getBetScore());
        betAmount = redisClient.hincrBy(betStatisKey, "totalBetAmount", 0);
        betAmountPay = redisClient.hincrBy(betStatisKey, "totalBetAmountPay", 0);
        betAmountComplex = redisClient.hincrBy(betStatisKey, "totalBetAmountComplex", 0);
        //货量-纯投注额-客队
        wsVo.setAwayBetAmount(BigDecimal.valueOf(betAmount).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN));
        //纯赔付额-客队
        wsVo.setAwayBetAmountPay(BigDecimal.valueOf(betAmountPay).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN));
        //货量-混合型
        wsVo.setAwayBetAmountComplex(BigDecimal.valueOf(betAmountComplex).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN));

        //纯投注额-平衡值
        wsVo.setBetAmountEquilibriumValue(wsVo.getHomeBetAmount().subtract(wsVo.getAwayBetAmount()));
        // 纯赔付额-平衡值
        wsVo.setBetAmountPayEquilibriumValue(wsVo.getHomeBetAmountPay().subtract(wsVo.getAwayBetAmountPay()));
        //混合型-平衡值
        wsVo.setBetAmountComplexEquilibriumValue(wsVo.getHomeBetAmountComplex().subtract(wsVo.getAwayBetAmountComplex()));


        Map<String, BigDecimal> forecastMap = new HashMap<>();
        //List<RcsPredictForecast> shoreForeCastList = shoreForeCast(list, data.getPlayId());


        for (RcsPredictForecast forecast : list) {
            //获取主队oddstype
            String key = "rcs:risk:predict:forecast.match_id.%s.match_type.%s.play_id.%s.market_id.%s.odds_item.%s.bet_score.%s";
            oddsType = getOddsTypeByPlayId(data.getPlayId().intValue(), 1);
            key = String.format(key, data.getMatchId(), data.getMatchType(), data.getPlayId(), data.getMarketId(),oddsType, data.getBetScore());
            Double homeProfitAmount = redisClient.hincrByFloat(key, forecast.getForecastScore().toString(), 0D);
            //获取客队队oddstype
            oddsType = getOddsTypeByPlayId(data.getPlayId().intValue(), 2);
            key = "rcs:risk:predict:forecast.match_id.%s.match_type.%s.play_id.%s.market_id.%s.odds_item.%s.bet_score.%s";
            key = String.format(key, data.getMatchId(), data.getMatchType(), data.getPlayId(), data.getMarketId(), oddsType, data.getBetScore());
            Double awayProfitAmount = redisClient.hincrByFloat(key, forecast.getForecastScore().toString(), 0D);
            forecastMap.put(forecast.getForecastScore().toString(), BigDecimal.valueOf(homeProfitAmount).add(BigDecimal.valueOf(awayProfitAmount)).setScale(2, BigDecimal.ROUND_DOWN));
        }
        wsVo.setForecastMap(forecastMap);
        String linkId = UUID.randomUUID().toString().replace("-", "");
        wsVo.setLinkId(linkId);
        producerSendMessageUtils.sendMessage("rcs_predict_market_bet_forecats_ws", "", linkId, wsVo);
        log.info("rcs_predict_market_bet_forecats_ws 推送成功:{}数据{}", linkId, JSONObject.toJSONString(wsVo));
    }

    /**
     * @param playId 玩法id
     * @param type   1主队 2客队
     * @return
     */
    private String getOddsTypeByPlayId(Integer playId, int type) {
        //让球
        Integer letPoint[] = ForecastPlayIds.letPoint;
        if (Arrays.asList(letPoint).contains(playId)) {
            if (type == 1) {
                return "1";
            }
            if (type == 2) {
                return "2";
            }
        }

        //大小
        Integer bigSmall[] = ForecastPlayIds.bigSmall;
        if (Arrays.asList(bigSmall).contains(playId)) {
            if (type == 1) {
                return "Over";
            }
            if (type == 2) {
                return "Under";
            }
        }
        return null;
    }

    private List<RcsPredictForecast> shoreForeCast(List<RcsPredictForecast> v ,Integer playId){

        int homeScore =12;
        int awayScore =0;

        //让球
        Integer letPoints[] = ForecastPlayIds.letPoint;
        //大小
        Integer bigSmall[] = ForecastPlayIds.bigSmall;

        List<RcsPredictForecast> tempResult = new ArrayList<>();

        if (Arrays.asList(letPoints).contains(playId)) {
            //让球是主队（前面的）减客队（后面的）的值等于6,就取4,5,6,7,8
            int num = homeScore - awayScore;
            if(num == -12){
                tempResult = v.stream().filter(e ->  e.getForecastScore() >= -12 && e.getForecastScore() <= -8).collect(Collectors.toList());
            }else if(num == -11){
                tempResult = v.stream().filter(e ->  e.getForecastScore() >= -11 && e.getForecastScore() <= -7).collect(Collectors.toList());
            }else if(num == 12){
                tempResult = v.stream().filter(e ->  e.getForecastScore() >= 8 && e.getForecastScore() <= 12).collect(Collectors.toList());
            }else if(num == 11){
                tempResult = v.stream().filter(e ->  e.getForecastScore() >= 7 && e.getForecastScore() <= 11).collect(Collectors.toList());
            }else{
                int start = num-2;
                int end  = num+2;
                //tempResult = v.subList(start, end + 1);
                tempResult = v.stream().filter(e ->  e.getForecastScore() >= start && e.getForecastScore() <= end).collect(Collectors.toList());
            }
        }else if (Arrays.asList(bigSmall).contains(playId)) {
            //大小是两个比分加起来如果等于1就取-1,0,1,2,3
            int num = homeScore - awayScore;
            if(num == 0){
                tempResult = v.stream().filter(e ->  e.getForecastScore() >= 0 && e.getForecastScore() <= 4).collect(Collectors.toList());
            }else if(num == 1){
                tempResult = v.stream().filter(e ->  e.getForecastScore() >= 1 && e.getForecastScore() <= 5).collect(Collectors.toList());
            }else if(num == 23){
                tempResult = v.stream().filter(e ->  e.getForecastScore() >= 19 && e.getForecastScore() <= 23).collect(Collectors.toList());
            }else if(num == 24){
                tempResult = v.stream().filter(e ->  e.getForecastScore() >= 20 && e.getForecastScore() <= 24).collect(Collectors.toList());
            }else{
                int start = num-2;
                int end  = num+2;
//                tempResult = v.subList(start, end + 1);
                tempResult = v.stream().filter(e ->  e.getForecastScore() >= start && e.getForecastScore() <= end).collect(Collectors.toList());
            }
        }
        return tempResult;
    }
}
