package com.panda.sport.rcs.predict.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.log.annotion.monnitor.MonitorAnnotion;
import com.panda.sport.rcs.mapper.predict.RcsPredictBetOddsMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictBetOdds;
import com.panda.sport.rcs.predict.common.ForecastPlayIds;
import com.panda.sport.rcs.predict.utils.PredictRedisKeyUtil;
import com.panda.sport.rcs.predict.vo.RcsPredictBetOddsVo;
import com.panda.sport.rcs.predict.vo.RcsPredictOddsPlaceNumMqVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author :  myname
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.sport.rcs.predict.mq
 * @Description :  TODO
 * @Date: 2022-03-13 11:48
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@MonitorAnnotion(code = "RCS_EVENT_TO_ORDER_TOPIC")
@TraceCrossThread
@RocketMQMessageListener(
        topic = ForecastPlayIds.RCS_EVENT_TO_ORDER_TOPIC,
        consumerGroup = ForecastPlayIds.RCS_EVENT_TO_ORDER_TOPIC,
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class MatchEventConsumer implements RocketMQListener<MatchEventInfoMessage>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    private RcsPredictBetOddsMapper rcsPredictBetOddsMapper;

    private static final String RCS_SPORT_ID_STANDARD_MATCH_ID_REDIS_KEY = "RCS_SPORT_ID_STANDARD_MATCH_ID_REDIS_KEY:%s:%s";
    private static final String RCS_GOAL_TIME_REDIS_KEY = "rcs:goal:time:redis:key:matchId:%s:sportId:%s";
    //玩法ID
    private static List<Integer> PLAY_ID_KEYS = Lists.newArrayList(2, 4, 18, 19);
    private static Long endTime = 20000L;

    @Override
    public void onMessage(MatchEventInfoMessage data) {
        log.info("足球取消或者进球赛事事件收到信息:{}", JSONObject.toJSONString(data));
        //放入缓存标识
        String key = String.format(RCS_SPORT_ID_STANDARD_MATCH_ID_REDIS_KEY, data.getSportId(), data.getStandardMatchId());
        if (StringUtils.equals(String.valueOf(data.getSportId()), "1") && StringUtils.equals("goal", data.getEventCode()) && data.getCanceled() == 0) {
            log.info("赛事:{},开始进球处理", data.getStandardMatchId());
            //设置3天过期
            redisClient.setExpiry(key, 1, endTime);
            //推送数据给前端
            this.sendWsMessage(data, 0);
        } else if (StringUtils.equals(String.valueOf(data.getSportId()), "1") && StringUtils.equals("goal", data.getEventCode()) && data.getCanceled() == 1) {
            log.info("赛事:{},开始取消进球处理", data.getStandardMatchId());
            //取消进球处理
            this.sendWsMessage(data, 1);
            //删除进球标识
            redisClient.delete(key);
        }
    }

    private void sendWsMessage(MatchEventInfoMessage data, Integer type) {
        OrderItem orderItem = new OrderItem();
        orderItem.setMatchId(data.getStandardMatchId());
        orderItem.setMatchType(2);
        for (Integer playId : PLAY_ID_KEYS) {
            orderItem.setPlayId(playId);
            //  String marketIdKey = PredictRedisKeyUtil.getMarketIdCommonKey(orderItem);
            String placeNumKey = PredictRedisKeyUtil.getPlaceNumCommonKey(orderItem);
            // Map<String, RcsPredictOddsPlaceNumMqVo> marketIdMap = redisClient.hGetAll(marketIdKey, RcsPredictOddsPlaceNumMqVo.class);
            Map<String, RcsPredictOddsPlaceNumMqVo> placeNumMap = redisClient.hGetAll(placeNumKey, RcsPredictOddsPlaceNumMqVo.class);
            log.info("::赛事:{},玩法:{},开始处理type:{},推送数据:{}", data.getStandardMatchId(), playId, type, JSON.toJSONString(placeNumMap));
//            if (Objects.nonNull(marketIdMap) && marketIdMap.size() > 0) {
//                for (String marketKey : marketIdMap.keySet()) {
//                    RcsPredictOddsPlaceNumMqVo rcsPredictOddsPlaceNumMqVo = marketIdMap.get(marketKey);
//                    //清空零时数据并且发送WS给前端
//                    this.emptyData(rcsPredictOddsPlaceNumMqVo, type);
//                }
//            }
            if (Objects.nonNull(placeNumMap) && placeNumMap.size() > 0) {
                for (String placeKey : placeNumMap.keySet()) {
                    RcsPredictOddsPlaceNumMqVo rcsPredictOddsPlaceNumMqVo = placeNumMap.get(placeKey);
                    this.emptyData(rcsPredictOddsPlaceNumMqVo, type);
                }
            }
            this.updateDataForMySql(orderItem.getMatchId(), playId, type);
            //删除2个接口
            // redisClient.delete(marketIdKey);
            //   redisClient.delete(placeNumKey);
        }
    }

    private void emptyData(RcsPredictOddsPlaceNumMqVo rcsPredictOddsPlaceNumMqVo, Integer type) {
        if (Objects.nonNull(rcsPredictOddsPlaceNumMqVo) && Objects.nonNull(rcsPredictOddsPlaceNumMqVo.getList())) {
            if (type == 0) {
                List<RcsPredictBetOddsVo> rcsPredictBetOddsVoList = rcsPredictOddsPlaceNumMqVo.getList();
                rcsPredictBetOddsVoList.forEach(rcsPredictBetOddsVo -> {
                    rcsPredictBetOddsVo.setBetAmountComplexTemp(BigDecimal.ZERO);
                    rcsPredictBetOddsVo.setBetAmountPayTemp(BigDecimal.ZERO);
                    rcsPredictBetOddsVo.setBetAmountTemp(BigDecimal.ZERO);
                    rcsPredictBetOddsVo.setBetOrderNumTemp(BigDecimal.ZERO);
                });
            } else if (type == 1) {
                List<RcsPredictBetOddsVo> rcsPredictBetOddsVoList = rcsPredictOddsPlaceNumMqVo.getList();
                rcsPredictBetOddsVoList.forEach(rcsPredictBetOddsVo -> {
                    rcsPredictBetOddsVo.setBetAmountComplexTemp(rcsPredictBetOddsVo.getBetAmountComplex());
                    rcsPredictBetOddsVo.setBetAmountPayTemp(rcsPredictBetOddsVo.getBetAmountPay());
                    rcsPredictBetOddsVo.setBetAmountTemp(rcsPredictBetOddsVo.getBetAmount());
                    rcsPredictBetOddsVo.setBetOrderNumTemp(rcsPredictBetOddsVo.getBetOrderNum());
                    setRedisTempKey(rcsPredictOddsPlaceNumMqVo, rcsPredictBetOddsVo); // 同时恢复 temp 相关key
                });
            }
            producerSendMessageUtils.sendMessage("rcs_predict_odds_placeNum_ws", String.valueOf(type), rcsPredictOddsPlaceNumMqVo.getMatchId() + "_" + rcsPredictOddsPlaceNumMqVo.getPlayId(), rcsPredictOddsPlaceNumMqVo);
        }
    }


    private void setRedisTempKey(RcsPredictOddsPlaceNumMqVo rcsPredictOddsPlaceNumMqVo, RcsPredictBetOddsVo rcsPredictBetOddsVo) {
        OrderItem item = new OrderItem();
        item.setMatchId(rcsPredictOddsPlaceNumMqVo.getMatchId());
        item.setMatchType(rcsPredictOddsPlaceNumMqVo.getMatchType());
        item.setPlayId(rcsPredictOddsPlaceNumMqVo.getPlayId());
        item.setSubPlayId(rcsPredictOddsPlaceNumMqVo.getSubPlayId());
        item.setPlaceNum(rcsPredictOddsPlaceNumMqVo.getDataTypeValue().intValue());

        String palceNumTotalBetAmountTempKey = PredictRedisKeyUtil.getPalceNumTotalBetAmountTempKey(item, rcsPredictOddsPlaceNumMqVo.getSeriesType().toString());
        String palceNumTotalBetAmountPayTempKey = PredictRedisKeyUtil.getPalceNumTotalBetAmountPayTempKey(item, rcsPredictOddsPlaceNumMqVo.getSeriesType().toString());
        String palceNumTotalBetAmountComplexTempKey = PredictRedisKeyUtil.getPalceNumTotalBetAmountComplexTempKey(item, rcsPredictOddsPlaceNumMqVo.getSeriesType().toString());
        String palceNumBetNumTempKey = PredictRedisKeyUtil.getPalceNumTotalBetNumTempKey(item, rcsPredictOddsPlaceNumMqVo.getSeriesType().toString());
        String format = String.format("sub_play_id.%s.place_num.%s.play_options.%s", rcsPredictOddsPlaceNumMqVo.getSubPlayId(), rcsPredictBetOddsVo.getDataTypeValue(), rcsPredictBetOddsVo.getOddsType());

        redisClient.hSet(palceNumTotalBetAmountTempKey, format, (rcsPredictBetOddsVo.getBetAmount().longValue() * 100L) + "");
        redisClient.hSet(palceNumTotalBetAmountPayTempKey, format, (rcsPredictBetOddsVo.getBetAmountPay().longValue() * 100L) + "");
        redisClient.hSet(palceNumTotalBetAmountComplexTempKey, format, (rcsPredictBetOddsVo.getBetAmountComplex().longValue() * 100L) + "");
        redisClient.hSet(palceNumBetNumTempKey, format, rcsPredictBetOddsVo.getBetOrderNum().longValue() + "");
    }

    private void updateDataForMySql(Long matchId, Integer playId, Integer type) {
        //清空数据库数据
        RcsPredictBetOdds rcsPredictBetOdds = new RcsPredictBetOdds();
        if (type == 0) {
            rcsPredictBetOdds.setBetAmountComplexTemp(BigDecimal.ZERO);
            rcsPredictBetOdds.setBetOrderNumTemp(BigDecimal.ZERO);
            rcsPredictBetOdds.setBetAmountTemp(BigDecimal.ZERO);
            rcsPredictBetOdds.setBetAmountPayTemp(BigDecimal.ZERO);
            rcsPredictBetOddsMapper.update(rcsPredictBetOdds, new QueryWrapper<RcsPredictBetOdds>().lambda().eq(RcsPredictBetOdds::getMatchId, matchId).eq(RcsPredictBetOdds::getPlayId, playId));
        } else if (type == 1) {
            rcsPredictBetOdds.setMatchId(matchId);
            rcsPredictBetOdds.setPlayId(playId);
            rcsPredictBetOddsMapper.updateData(rcsPredictBetOdds);
        }

    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(64);
        consumer.setConsumeThreadMax(256);
    }
}
