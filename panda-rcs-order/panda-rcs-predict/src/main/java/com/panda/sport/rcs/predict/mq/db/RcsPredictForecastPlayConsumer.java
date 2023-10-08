package com.panda.sport.rcs.predict.mq.db;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.predict.RcsPredictForecastPlayMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.vo.api.response.BetForPlaceResWsVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecastPlay;
import com.panda.sport.rcs.predict.common.Expiry;
import com.panda.sport.rcs.predict.common.ForecastPlayIds;
import com.panda.sport.rcs.predict.utils.CopyUtils;
import com.panda.sport.rcs.predict.utils.LongUtil;
import com.panda.sport.rcs.predict.vo.RcsPredictForecastPlayMqVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author :  lithan
 * @Date: 2021-2-24 10:38:25
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@RocketMQMessageListener(
        topic = "mq_data_rcs_predict_forecast_play",
        consumerGroup = "mq_data_rcs_predict_forecast_play_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class RcsPredictForecastPlayConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RcsPredictForecastPlayMapper mapper;


    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;



    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(160);
        defaultMQPushConsumer.setConsumeThreadMax(320);
    }

    public RcsPredictForecastPlayConsumer() {
//        super("mq_data_rcs_predict_forecast_play");
    }

//    @Override
    public String getCacheKey(List<RcsPredictForecastPlay> data, Map<String, String> map) {
        RcsPredictForecastPlay bean = data.get(0);
        log.info("mq_data_rcs_predict_forecast_play保存收到:{}", JSONObject.toJSONString(data));
        String lastTimeKey = String.format("rcs:lastTime:mq_data_rcs_predict_forecast_play:%s_%s_%s_%s", bean.getMatchId(), bean.getPlayId(), bean.getMatchType(), bean.getPlaceNum());
        long lastTime = LongUtil.parseLong(redisClient.get(lastTimeKey));
        long currTime = LongUtil.parseLong(map.get("time"));
        if (currTime < lastTime) {
            log.info("mq_data_rcs_predict_forecast_play处理 时间已过期 跳过{}", JSONObject.toJSONString(data));
            return null;
        }
        redisClient.set(lastTimeKey, currTime);
        redisClient.expireKey(lastTimeKey, Expiry.MATCH_EXPIRY);
        return lastTimeKey;
    }

    @Override
    public void onMessage(String str) {
        try {
            List<RcsPredictForecastPlay> list = JSONObject.parseObject(str, new TypeReference<List<RcsPredictForecastPlay>>() {
            });
            for (RcsPredictForecastPlay bean : list) {
                String key = String.format("rcs:profit:match:%s:%s:%s:%s", bean.getMatchId(), bean.getMatchType(), bean.getPlayId(), bean.getPlaceNum());
                Double profitValue = redisClient.hincrByFloat(key, bean.getScore().toString(), 0.0);
                bean.setProfitValue(new BigDecimal(profitValue));
                bean.setHashUnique(DigestUtil.md5Hex(key));
            }
            mapper.saveOrUpdate(list);
            //推送存到mango
            RcsPredictForecastPlay bean = list.get(0);
            if (bean.getDataType() == 1) {
                String key = bean.getMatchId() + "_" + bean.getPlayId() + "_" + bean.getMatchType();
                RcsPredictForecastPlayMqVo forecastPlayMqVo = new RcsPredictForecastPlayMqVo();
                BeanUtils.copyProperties(bean, forecastPlayMqVo);
                forecastPlayMqVo.setList(list);
                String linkId = UUID.randomUUID().toString().replace("-", "");
                forecastPlayMqVo.setLinkId(linkId);
                log.info("rcs_predict_forecast_play_mongo 开始推送Mango 赛事ID:{} 玩法：{}", bean.getMatchId(), bean.getPlayId());
                producerSendMessageUtils.sendMessage("rcs_predict_forecast_play_mongo", "", linkId + "_" + key, forecastPlayMqVo);
                log.info("rcs_predict_forecast_play_mongo 推送成功:{}", linkId + "_" + key);
            }
            //盘口位置 ws推送
            if (bean.getDataType() == 2) {
                List<BetForPlaceResWsVo> wsList = new ArrayList<>();
                String linkId = UUID.randomUUID().toString().replace("-", "");
                //早盘
                list.forEach(e -> e.setMatchType(1));
                BetForPlaceResWsVo earlyVo = tows(list);
                earlyVo.setLinkId(linkId);
                wsList.add(earlyVo);
                //滚球
                list.forEach(e -> e.setMatchType(2));
                BetForPlaceResWsVo liveVo = tows(list);
                liveVo.setLinkId(linkId);
                wsList.add(liveVo);
                //汇总
                BetForPlaceResWsVo totalVo = toTal(earlyVo, liveVo);
                totalVo.setLinkId(linkId);
                wsList.add(totalVo);
                producerSendMessageUtils.sendMessage("rcs_predict_place_bet_forecats_ws", "", linkId, wsList);
                log.info("rcs_predict_place_bet_forecats_ws 推送成功:{}", linkId);
            }

        } catch (Exception e) {
            log.error("mq_data_rcs_predict_forecast_play保存异常" + e.getMessage(), e);
        }
    }

    /**
     * 推送坑位货量表
     */
    private BetForPlaceResWsVo tows(List<RcsPredictForecastPlay> list){
        RcsPredictForecastPlay data = list.get(0);
        BetForPlaceResWsVo wsVo = new BetForPlaceResWsVo();
        wsVo.setMatchId(data.getMatchId());
        wsVo.setPlayId(data.getPlayId().intValue());
        wsVo.setDataTypeValue(data.getPlaceNum());
        wsVo.setMatchType(data.getMatchType());

        //盘口位置 总投注金额
        String placeNumTotalBetAmonutKey = "rcs:risk:predict:palceNumTotalBetAmount.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.place_num.%s";
        placeNumTotalBetAmonutKey = String.format(placeNumTotalBetAmonutKey, data.getMatchId(), data.getMatchType(), data.getPlayId(), data.getSubPlayId(), data.getPlaceNum());
        //盘口位置 纯赔付额
        String placeNumTotalBetAmonutPayKey = "rcs:risk:predict:palceNumTotalBetAmountPay.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.place_num.%s";
        placeNumTotalBetAmonutPayKey = String.format(placeNumTotalBetAmonutPayKey, data.getMatchId(), data.getMatchType(), data.getPlayId(), data.getSubPlayId(), data.getPlaceNum());
        //盘口位置 混合型
        String placeNumTotalBetAmonutComplexKey = "rcs:risk:predict:palceNumTotalBetAmountComplex.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.place_num.%s";
        placeNumTotalBetAmonutComplexKey = String.format(placeNumTotalBetAmonutComplexKey, data.getMatchId(), data.getMatchType(), data.getPlayId(), data.getSubPlayId(), data.getPlaceNum());

        //获取主队oddstype
        String oddsType = getOddsTypeByPlayId(data.getPlayId().intValue(), 1);
        Long totalBetAmount = redisClient.hincrBy(placeNumTotalBetAmonutKey, oddsType,0);
        Long totalBetAmountPay = redisClient.hincrBy(placeNumTotalBetAmonutPayKey, oddsType,0);
        Long totalBetAmountComplex = redisClient.hincrBy(placeNumTotalBetAmonutComplexKey, oddsType,0);
        //货量-纯投注额-主队
        wsVo.setHomeBetAmount(BigDecimal.valueOf(totalBetAmount).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN));
        //货量-纯赔付额-主队
        wsVo.setHomeBetAmountPay(BigDecimal.valueOf(totalBetAmountPay).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN));
        //货量-混合型（
        wsVo.setHomeBetAmountComplex(BigDecimal.valueOf(totalBetAmountComplex).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN));

        //获取客队队oddstype
        oddsType = getOddsTypeByPlayId(data.getPlayId().intValue(), 2);
        totalBetAmount = redisClient.hincrBy(placeNumTotalBetAmonutKey, oddsType, 0);
        totalBetAmountPay = redisClient.hincrBy(placeNumTotalBetAmonutPayKey, oddsType, 0);
        totalBetAmountComplex = redisClient.hincrBy(placeNumTotalBetAmonutComplexKey, oddsType, 0);
        //货量-纯投注额-客队
        wsVo.setAwayBetAmount(BigDecimal.valueOf(totalBetAmount).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN));
        //纯赔付额-客队
        wsVo.setAwayBetAmountPay(BigDecimal.valueOf(totalBetAmountPay).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN));
        //货量-混合型
        wsVo.setAwayBetAmountComplex(BigDecimal.valueOf(totalBetAmountComplex).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN));

        //纯投注额-平衡值
        wsVo.setBetAmountEquilibriumValue(wsVo.getHomeBetAmount().subtract(wsVo.getAwayBetAmount()));
        // 纯赔付额-平衡值
        wsVo.setBetAmountPayEquilibriumValue(wsVo.getHomeBetAmountPay().subtract(wsVo.getAwayBetAmountPay()));
        //混合型-平衡值
        wsVo.setBetAmountComplexEquilibriumValue(wsVo.getHomeBetAmountComplex().subtract(wsVo.getAwayBetAmountComplex()));


        Map<String, BigDecimal> forecastMap = new HashMap<>();
        for (RcsPredictForecastPlay forecastPlay : list) {
            String key = String.format("rcs:profit:match:%s:%s:%s:%s", forecastPlay.getMatchId(), forecastPlay.getMatchType(), forecastPlay.getPlayId(), forecastPlay.getPlaceNum());
            Double profitValue = redisClient.hincrByFloat(key, forecastPlay.getScore().toString(), 0D);
            forecastMap.put(forecastPlay.getScore().toString(), BigDecimal.valueOf(profitValue).setScale(2, BigDecimal.ROUND_DOWN));
        }
        wsVo.setForecastMap(forecastMap);
        return wsVo;
    }


    /***
     * 合并早盘滚球
     */
    private BetForPlaceResWsVo toTal(BetForPlaceResWsVo one, BetForPlaceResWsVo two){
        BetForPlaceResWsVo merge = CopyUtils.clone(one, BetForPlaceResWsVo.class);
        merge.setMatchType(0);
        merge.setHomeBetAmount(one.getHomeBetAmount().add(two.getHomeBetAmount()));
        merge.setHomeBetAmountPay(one.getHomeBetAmountPay().add(two.getHomeBetAmountPay()));
        merge.setHomeBetAmountComplex(one.getHomeBetAmountComplex().add(two.getHomeBetAmountComplex()));
        merge.setAwayBetAmount(one.getAwayBetAmount().add(two.getAwayBetAmount()));
        merge.setAwayBetAmountPay(one.getAwayBetAmountPay().add(two.getAwayBetAmountPay()));
        merge.setAwayBetAmountComplex(one.getAwayBetAmountComplex().add(two.getAwayBetAmountComplex()));
        merge.setBetAmountEquilibriumValue(one.getBetAmountEquilibriumValue().add(two.getBetAmountEquilibriumValue()));
        merge.setBetAmountPayEquilibriumValue(one.getBetAmountPayEquilibriumValue().add(two.getBetAmountPayEquilibriumValue()));
        merge.setBetAmountComplexEquilibriumValue(one.getBetAmountComplexEquilibriumValue().add(two.getBetAmountComplexEquilibriumValue()));
        Map<String, BigDecimal> oneForecastMap = one.getForecastMap();
        Map<String, BigDecimal> twoForecastMap = two.getForecastMap();
        Map<String, BigDecimal> mergeForecastMap = new HashMap<>();

        oneForecastMap.forEach((key,value)->{
            mergeForecastMap.put(key, oneForecastMap.get(key).add(twoForecastMap.get(key)));
        });
        merge.setForecastMap(mergeForecastMap);
        return merge;
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
}
