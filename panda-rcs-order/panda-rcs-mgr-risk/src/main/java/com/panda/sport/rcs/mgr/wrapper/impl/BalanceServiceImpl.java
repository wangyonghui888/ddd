package com.panda.sport.rcs.mgr.wrapper.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.UuidUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.BalanceTypeEnum;
import com.panda.sport.rcs.enums.OddsTypeEnum;
import com.panda.sport.rcs.mgr.utils.StringUtil;
import com.panda.sport.rcs.mgr.wrapper.BalanceService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.vo.statistics.BalanceVo;
import com.panda.sport.rcs.vo.statistics.MarketBalanceVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author :  kimi
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.impl
 * @Description :  注单平衡值有变化的时候调用
 * @Date: 2019-10-30 16:37
 */
@Service
@Slf4j
public class BalanceServiceImpl implements BalanceService {

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    private RedisClient redisClient;

    @Override
    public void updateBalance(Long matchId, Long marketId, MarketBalanceVo vo,RcsMatchMarketConfig result) {

        log.info("::matchId{}:: 更新平衡值参数：marketId={};MarketBalanceVo={};result={}",matchId,marketId,JSONObject.toJSONString(vo),JSONObject.toJSONString(result));
        RcsMatchMarketConfig rcsMatchMarketConfig = new RcsMatchMarketConfig();
        rcsMatchMarketConfig.setAwayMarketValue(null);
        rcsMatchMarketConfig.setHomeMarketValue(null);
        rcsMatchMarketConfig.setOddsChange(null);
        rcsMatchMarketConfig.setBalance(vo.getBalanceValue());
        rcsMatchMarketConfig.setMarketId(marketId);
        rcsMatchMarketConfig.setMatchId(matchId);
        vo.setMarketId(marketId);

        long minValue = vo.getHomeAmount();
        if (vo.getAwayAmount() < minValue) {
            minValue = vo.getAwayAmount();
        }
        if (vo.getTieAmount() < minValue) {
            minValue = vo.getTieAmount();
        }
        rcsMatchMarketConfig.setHomeAmount(vo.getHomeAmount() - minValue);
        rcsMatchMarketConfig.setAwayAmount(vo.getAwayAmount() - minValue);
        rcsMatchMarketConfig.setTieAmount(vo.getTieAmount() - minValue);

        //调用websocket推送给前端
        //更新数据库
        BalanceVo balanceVo = new BalanceVo(matchId, marketId,
                vo.getBalanceValue(), rcsMatchMarketConfig.getHomeAmount(), rcsMatchMarketConfig.getAwayAmount(), rcsMatchMarketConfig.getTieAmount());
        balanceVo.setGlobalId(UuidUtils.generateUuid());
        balanceVo.setCurrentSide(vo.getCurrentSide());
        if (!ObjectUtils.isEmpty(result)){
            balanceVo.setMarketCategoryId(result.getPlayId());
            balanceVo.setTieMargin(result.getTieMargin());
            balanceVo.setAwayMargin(result.getAwayMargin());
            balanceVo.setHomeMargin(result.getHomeMargin());
            balanceVo.setAwayAutoChangeRate(result.getAwayAutoChangeRate());
            if (!ObjectUtils.isEmpty(result.getAwayAutoChangeRate())){
                balanceVo.setWaterValue(new BigDecimal(result.getAwayAutoChangeRate()).multiply(new BigDecimal(100)));
            }
        }
        balanceVo.setSportId(vo.getSportId());
        balanceVo.setMarketCategoryId(vo.getPlayId());
        balanceVo.setPlaceNum(vo.getPlaceNum());
        balanceVo.setPlaceNumId(vo.getPlaceNumId());
        String key = UuidUtils.generateUuid();
        log.info("::matchId{}:: 平衡值存入MQ消息队列:{}，key={}",matchId,balanceVo,key);
        producerSendMessageUtils.sendMessage(MqConstants.REALTIME_SYNC_BALANCE_TOPIC, MqConstants.REALTIME_SYNC_BALANCE_TAG, key, balanceVo);
    }

    @Override
    public void queryBalance(Integer balanceType, String dateExpect, String keySuffix, Integer balanceOption, BalanceVo balanceVo) {
        String jumpKey;
        if (NumberUtils.INTEGER_TWO.equals(balanceType)) {
            // 跳盘平衡值
            balanceVo.setJumpMarketBalance(0L);
            // 累计差额计算方式，0-投注额差值，1-投注/赔付混合差值
            if (NumberUtils.INTEGER_ZERO.equals(balanceOption)) {
                jumpKey = String.format("rcs:odds:jumpMarket:%s:%s:bet{%s}", dateExpect, keySuffix, keySuffix);
            } else {
                jumpKey = String.format("rcs:odds:jumpMarket:%s:%s:mix{%s}", dateExpect, keySuffix, keySuffix);
            }
        } else {
            // 跳赔平衡值
            balanceVo.setBalanceValue(0L);
            // 累计差额计算方式，0-投注额差值，1-投注/赔付混合差值
            if (NumberUtils.INTEGER_ZERO.equals(balanceOption)) {
                jumpKey = String.format(RedisKeys.CALC_AMOUNT_ODDS_CHANGE + "{%s}", dateExpect, keySuffix, keySuffix);
            } else {
                jumpKey = String.format(RedisKeys.CALC_AMOUNT_ODDS_CHANGE_PLUS + "{%s}", dateExpect, keySuffix, keySuffix);
            }
        }
        Map<String, String> jumpMap = (Map<String, String>) redisClient.hGetAllToObj(jumpKey);
        if (CollectionUtils.isEmpty(jumpMap)) {
            String oddsType = getHomeOddsTypeByPlayId(balanceVo.getMarketCategoryId());
            if (NumberUtils.INTEGER_TWO.equals(balanceType)) {
                balanceVo.setJumpMarketOddsType(oddsType);
            } else {
                balanceVo.setCurrentSide(oddsType);
            }
            return;
        }
        BigDecimal jumpTotal = BigDecimal.ZERO;
        BigDecimal jumpMax = BigDecimal.ZERO;
        String oddsType = null;
        for (Map.Entry<String, String> entry : jumpMap.entrySet()) {
            // 投注项类型
            String key = entry.getKey();
            // 投注项累计值
            BigDecimal value = StringUtil.toBigDecimal(entry.getValue());
            jumpTotal = jumpTotal.add(value);
            if (value.compareTo(jumpMax) > 0) {
                jumpMax = value;
                oddsType = entry.getKey();
            }
        }
        if (StringUtils.isBlank(oddsType)) {
            oddsType = getHomeOddsTypeByPlayId(balanceVo.getMarketCategoryId());
        }
        long balance = jumpMax.subtract(jumpTotal.subtract(jumpMax)).longValue();
        if (NumberUtils.INTEGER_TWO.equals(balanceType)) {
            // 跳盘平衡值
            balanceVo.setJumpMarketBalance(balance);
            balanceVo.setJumpMarketOddsType(oddsType);
        } else {
            // 跳赔平衡值
            balanceVo.setBalanceValue(balance);
            balanceVo.setCurrentSide(oddsType);
        }
    }

    public String getHomeOddsTypeByPlayId(Long playId) {
        if (Lists.newArrayList(39L, 19L, 143L, 46L, 52L, 58L, 64L).contains(playId)) {
            return BaseConstants.ODD_TYPE_1;
        }
        if (Lists.newArrayList(38L, 18L, 26L, 45L, 51L, 57L, 63L, 87L, 88L, 97L, 98L, 145L, 146L, 198L, 199L).contains(playId)) {
            return BaseConstants.ODD_TYPE_OVER;
        }
        if (Lists.newArrayList(40L, 42L, 75L, 47L, 53L, 59L, 65L).contains(playId)) {
            return BaseConstants.ODD_TYPE_ODD;
        }
        return BaseConstants.ODD_TYPE_1;
    }

    @Override
    public void clearAllBalance(Object ... args) {
        Integer balanceType = (Integer)args[0];
        Long sportId = (Long) args[1];
        Long matchId = (Long) args[2];
        Long playId = (Long) args[3];
        String dateExpect = (String)args[4];
        String subPlayId = (String)args[5];
        log.info("清除玩法下所有平衡值：balanceType={},sportId={},matchId={},playId={},dateExpect={}", balanceType, sportId, matchId, playId, dateExpect);

        BalanceVo balanceVo = new BalanceVo();
        balanceVo.setSportId(sportId);
        balanceVo.setMatchId(matchId);
        balanceVo.setMarketCategoryId(playId);
        balanceVo.setSubPlayId(subPlayId);
        if (new Long(2L).equals(sportId)) {
            // 篮球
            for (int placeNum = 1; placeNum <= RcsConstant.DEFAULT_MARKET_PLACE_AMOUNT; placeNum++) {
                String keySuffix = String.format("%s_%s_%s", matchId, playId, placeNum);
                if (RcsConstant.BASKETBALL_X_MY_PLAYS.contains(playId.intValue()) || RcsConstant.BASKETBALL_X_EU_PLAYS.contains(playId.intValue())){
                    keySuffix = String.format("%s_%s_%s_%s", matchId, playId, subPlayId,placeNum);
                }
                String jumpBetKey;
                String jumpMixKey;
                if (BalanceTypeEnum.isJumpMarket(balanceType)) {
                    // 跳盘平衡值
                    jumpBetKey = String.format("rcs:odds:jumpMarket:%s:%s:bet{%s}", dateExpect, keySuffix, keySuffix);
                    jumpMixKey = String.format("rcs:odds:jumpMarket:%s:%s:mix{%s}", dateExpect, keySuffix, keySuffix);
                } else {
                    // 跳赔平衡值
                    jumpBetKey = String.format(RedisKeys.CALC_AMOUNT_ODDS_CHANGE + "{%s}", dateExpect, keySuffix, keySuffix);
                    jumpMixKey = String.format(RedisKeys.CALC_AMOUNT_ODDS_CHANGE_PLUS + "{%s}", dateExpect, keySuffix, keySuffix);
                }
                redisClient.delete(jumpBetKey);
                redisClient.delete(jumpMixKey);
            }
            // -1清除所有位置平衡值
            balanceVo.setPlaceNum(-1);
            if (BalanceTypeEnum.isJumpMarket(balanceType)) {
                // 跳盘平衡值
                balanceVo.setJumpMarketBalance(0L);
                balanceVo.setJumpMarketOddsType(OddsTypeEnum.getHomeOddsTypeByPlayId(playId));
            } else {
                // 跳赔平衡值
                balanceVo.setBalanceValue(0L);
                balanceVo.setCurrentSide(OddsTypeEnum.getHomeOddsTypeByPlayId(playId));
            }
            balanceVo.setGlobalId(balanceVo.generateKey());
            // 平衡值推送WS到前端
            producerSendMessageUtils.sendMessage(MqConstants.REALTIME_SYNC_BALANCE_TOPIC, MqConstants.REALTIME_SYNC_BALANCE_TAG, balanceVo.getGlobalId(), balanceVo);
        } else {
            // 足球
//            List<Long> marketIdList = standardSportMarketService.getMarketIdList(matchId, playId);
//            if (CollectionUtils.isEmpty(marketIdList)) {
//                return;
//            }
//            for (Long marketId : marketIdList) {
//                // 跳赔平衡值
//                balanceVo.setType(BalanceTypeEnum.JUMP_ODDS.getType());
//                balanceVo.setMarketId(marketId);
//                balanceVo.setBalanceValue(0L);
//                String keySuffix = String.valueOf(marketId);
//                String jumpBetKey = RedisKey.getJumpOddsBetKey(dateExpect, keySuffix);
//                String jumpMixKey = RedisKey.getJumpOddsMixKey(dateExpect, keySuffix);
//                redisUtils.del(jumpBetKey);
//                redisUtils.del(jumpMixKey);
//                balanceVo.setGlobalId(balanceVo.generateKey());
//                // 平衡值推送WS到前端
//                producerSendMessageUtils.sendMessage(MqConstants.REALTIME_SYNC_BALANCE_TOPIC, MqConstants.REALTIME_SYNC_BALANCE_TAG, balanceVo.getGlobalId(), balanceVo);
//            }
        }
    }
}
