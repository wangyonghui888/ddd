package com.panda.sport.rcs.task.mq.predict;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.enums.TradeLevelEnum;
import com.panda.sport.rcs.enums.TradeStatusEnum;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplateMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.dto.TournamentTemplateMarginDto;
import com.panda.sport.rcs.predict.vo.RcsPredictBetOddsVo;
import com.panda.sport.rcs.predict.vo.RcsPredictOddsPlaceNumMqVo;
import com.panda.sport.rcs.task.mq.RcsConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * @Project Name : panda-rcs-task
 * @Package Name : panda-rcs-task
 * @Description : 出涨封盘
 * @Author : Paca
 * @Date : 2022-03-11 20:54
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "rcs_predict_odds_placeNum_ws",
        consumerGroup = "RCS_TASK_predict_odds_placeNum_ws_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class ChuZhangConsumer extends RcsConsumer<RcsPredictOddsPlaceNumMqVo> {

    @Autowired
    private RcsTournamentTemplateMapper rcsTournamentTemplateMapper;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    private RedisClient redisClient;

    @Override
    protected String getTopic() {
        return "rcs_predict_odds_placeNum_ws";
    }

    private boolean checkMsg(RcsPredictOddsPlaceNumMqVo message) {
        if (!NumberUtils.INTEGER_TWO.equals(message.getDataType())) {
            log.warn("不是位置货量");
            return false;
        }
        List<RcsPredictBetOddsVo> list = message.getList();
        if (CollectionUtils.isEmpty(list)) {
            log.warn("货量信息为空");
            return false;
        }
        if (list.size() != 2) {
            log.warn("不是两项盘");
            return false;
        }
        if (SportIdEnum.BASKETBALL.isNo(list.get(0).getSportId())) {
            log.warn("不是篮球货量");
            return false;
        }
        return true;
    }

    @Override
    protected Boolean handleMs(RcsPredictOddsPlaceNumMqVo message) {
        if (!checkMsg(message)) {
            return false;
        }
        Integer matchType = message.convertMatchType();
        Long matchId = message.getMatchId();
        Integer playId = message.getPlayId();
        String subPlayId = message.getSubPlayId();
        int placeNum = message.getDataTypeValue().intValue();
        if (!isOpen(matchId, playId, matchType)) {
            log.warn("出涨封盘未开");
            return false;
        }
        TournamentTemplateMarginDto config;
        if (StringUtils.equals(playId.toString(), subPlayId)) {
            config = rcsTournamentTemplateMapper.selectRcsTournamentTemplateMarket(matchType, 3, matchId.intValue(), playId, placeNum);
        } else {
            config = rcsTournamentTemplateMapper.selectSubPlayChuZhangConfig(matchId, playId, subPlayId, placeNum, matchType);
            if (config == null) {
                config = rcsTournamentTemplateMapper.selectSubPlayChuZhangConfig(matchId, playId, "-1", placeNum, matchType);
            }
        }
        if (config == null) {
            log.error("未查询到出涨配置");
            return false;
        }
        BigDecimal baseValue = new BigDecimal(config.getMarketWarn() * config.getMaxSingleBetAmount()).abs();
        RcsPredictBetOddsVo home = message.getList().get(0);
        RcsPredictBetOddsVo away = message.getList().get(1);
        BigDecimal betDiff = home.getBetAmount().subtract(away.getBetAmount()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        if (betDiff.abs().compareTo(baseValue) < 0) {
            return true;
        } else if (betDiff.compareTo(BigDecimal.ZERO) > 0) {
            // 主出涨
            calChuZhang(message, home.getOddsType(), away.getOddsType(), baseValue, betDiff);
        } else {
            // 客出涨
            calChuZhang(message, away.getOddsType(), home.getOddsType(), baseValue, betDiff);
        }
        return true;
    }

    private void calChuZhang(RcsPredictOddsPlaceNumMqVo message, String oddsType, String otherOddsType, BigDecimal baseValue, BigDecimal betDiff) {
        betDiff = betDiff.abs();
        Integer matchType = message.convertMatchType();
        Long matchId = message.getMatchId();
        Integer playId = message.getPlayId();
        String subPlayId = message.getSubPlayId();
        int placeNum = message.getDataTypeValue().intValue();
        String key = RedisKey.getChuZhangFrequencyKey(matchId);
        String hashKey = RedisKey.getChuZhangFrequencyHashKey(playId, subPlayId, placeNum, matchType, oddsType);
        long hashValue = redisClient.hincrBy(key, hashKey, 1);
        log.info("出涨频次：key={},hashKey={},hashValue={}", key, hashKey, hashValue);
        if (betDiff.compareTo(baseValue.multiply(new BigDecimal(hashValue))) >= 0) {
            // 触发出涨
            String otherHashKey = RedisKey.getChuZhangFrequencyHashKey(playId, subPlayId, placeNum, matchType, otherOddsType);
            redisClient.hashRemove(key, otherHashKey);
            basketballSeal(matchId, playId, subPlayId, placeNum, message.generateLinkId());
        } else {
            // 还原出涨频次
            redisClient.hincrBy(key, hashKey, -1);
        }
    }

    private boolean isOpen(Long matchId, Integer playId, Integer matchType) {
        String chuZhangSwitchKey = RedisKey.Config.getChuZhangSwitchKey(matchId, matchType);
        Map<String, String> hashMap = (Map<String, String>) redisClient.hGetAllToObj(chuZhangSwitchKey);
        log.info("出涨封盘开关：key={},hashMap={}", chuZhangSwitchKey, JSON.toJSONString(hashMap));
        if (CollectionUtils.isEmpty(hashMap)) {
            return false;
        }
        // 赛事开关默认开
        String matchSwitch = hashMap.getOrDefault(matchId.toString(), "1");
        if (!"1".equals(matchSwitch)) {
            return false;
        }
        // 玩法开关默认关
        String playSwitch = hashMap.getOrDefault(playId.toString(), "0");
        if (!"1".equals(playSwitch)) {
            return false;
        }
        return true;
    }

    private void basketballSeal(Long matchId, Integer playId, String subPlayId, int placeNum, String linkId) {
        // 封盘
        JSONObject obj = new JSONObject()
                .fluentPut("tradeLevel", TradeLevelEnum.PLAY.getLevel())
                .fluentPut("matchId", matchId)
                .fluentPut("playId", playId)
                .fluentPut("subPlayId", subPlayId)
                .fluentPut("status", TradeStatusEnum.SEAL.getStatus())
                .fluentPut("linkedType", 121)
                .fluentPut("remark", "出涨封盘")
                .fluentPut("linkId", linkId);
        Request<JSONObject> request = new Request<>();
        request.setData(obj);
        request.setLinkId(linkId);
        request.setDataSourceTime(System.currentTimeMillis());
        String tag = String.format("%s_%s_%s_%s", matchId, playId, subPlayId, placeNum);
        producerSendMessageUtils.sendMessage("RCS_TRADE_UPDATE_MARKET_STATUS", tag, linkId, request);
    }
}
