package com.panda.sport.rcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.predict.RcsPredictBetStatisMapper;
import com.panda.sport.rcs.mapper.predict.pending.RcsPredictPendingBetStatisMapper;
import com.panda.sport.rcs.pojo.vo.api.response.BetForMarketResVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictBetStatis;
import com.panda.sport.rcs.pojo.vo.predict.pending.RcsPredictPendingBetStatis;
import com.panda.sport.rcs.service.IRcsPredictBetStatisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 预测货量表 服务实现类
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
@Service
public class RcsPredictBetStatisServiceImpl extends ServiceImpl<RcsPredictBetStatisMapper, RcsPredictBetStatis> implements IRcsPredictBetStatisService {
    @Autowired
    private RcsPredictBetStatisMapper mapper;
    @Autowired
    private RcsPredictPendingBetStatisMapper rcsPredictPendingBetStatisMapper;

    @Override
    public List<BetForMarketResVo> selectBetForMarket(Long matchId, Integer playId, Integer sportId, Integer matchType, String oddsItemForHome, String oddsItemForAway) {
        return mapper.selectBetForMarket(matchId,playId,sportId,matchType,oddsItemForHome,oddsItemForAway);
    }

    @Override
    public List<BetForMarketResVo> selectPendingBetForMarket(Long matchId, Integer playId, Integer sportId, Integer matchType, String oddsItemForHome, String oddsItemForAway) {
        // List<BetForMarketResVo> list = rcsPredictPendingBetStatisMapper.selectBetForMarket(matchId, playId, sportId, matchType, oddsItemForHome, oddsItemForAway);
        List<RcsPredictPendingBetStatis> rcsPredictPendingBetStatisHomeList = rcsPredictPendingBetStatisMapper.selectList(new LambdaQueryWrapper<RcsPredictPendingBetStatis>().
                eq(RcsPredictPendingBetStatis::getMatchId, matchId).
                eq(RcsPredictPendingBetStatis::getPlayId, playId).
                eq(RcsPredictPendingBetStatis::getMatchType, matchType).
                eq(RcsPredictPendingBetStatis::getOddsItem, oddsItemForHome));

        List<RcsPredictPendingBetStatis> rcsPredictPendingBetStatisAwayList = rcsPredictPendingBetStatisMapper.selectList(new LambdaQueryWrapper<RcsPredictPendingBetStatis>().
                eq(RcsPredictPendingBetStatis::getMatchId, matchId).
                eq(RcsPredictPendingBetStatis::getPlayId, playId).
                eq(RcsPredictPendingBetStatis::getMatchType, matchType).
                eq(RcsPredictPendingBetStatis::getOddsItem, oddsItemForAway));

        Map<String, RcsPredictPendingBetStatis> collectHome = rcsPredictPendingBetStatisHomeList.stream().
                collect(Collectors.groupingBy(RcsPredictPendingBetStatis::getMarketValueComplete, Collectors.collectingAndThen(Collectors.toMap(e -> e, e -> e),
                        e -> e.values().stream().max(Comparator.comparing(RcsPredictPendingBetStatis::getCreateTime)).get())));

        Map<String, RcsPredictPendingBetStatis> collectAway = rcsPredictPendingBetStatisAwayList.stream().
                collect(Collectors.groupingBy(RcsPredictPendingBetStatis::getMarketValueComplete, Collectors.collectingAndThen(Collectors.toMap(e -> e, e -> e),
                        e -> e.values().stream().max(Comparator.comparing(RcsPredictPendingBetStatis::getCreateTime)).get())));

        Map<String, BetForMarketResVo> betForMarketResVoMap = new HashMap<>();
        collectHome.forEach((key, item) -> {
            BetForMarketResVo betForMarketResVo = new BetForMarketResVo();
            betForMarketResVo.setBetScore(item.getBetScore());
            betForMarketResVo.setMarketId(item.getMarketId());
            betForMarketResVo.setMatchType(item.getMatchType());
            betForMarketResVo.setHomeBetAmount(item.getBetAmount());
            betForMarketResVo.setHomeBetAmountPay(item.getBetAmountPay());
            betForMarketResVo.setHomeBetAmountComplex(item.getBetAmountComplex());
            betForMarketResVo.setMarketValueComplete(item.getMarketValueComplete());
            betForMarketResVo.setMarketValueCurrent(item.getMarketValueComplete());
            betForMarketResVo.setAwayBetAmount(new BigDecimal(0));
            betForMarketResVo.setAwayBetAmountPay(new BigDecimal(0));
            betForMarketResVo.setAwayBetAmountComplex(new BigDecimal(0));
            betForMarketResVoMap.put(item.getMarketValueComplete(), betForMarketResVo);
        });
        collectAway.forEach((key, item) -> {
            BetForMarketResVo betForMarketResVo = betForMarketResVoMap.get(key);
            if (ObjectUtils.isEmpty(betForMarketResVo)) {
                betForMarketResVo = new BetForMarketResVo();
                betForMarketResVo.setBetScore(item.getBetScore());
                betForMarketResVo.setMatchType(item.getMatchType());
                betForMarketResVo.setMarketValueComplete(item.getMarketValueComplete());
                betForMarketResVo.setMarketValueCurrent(item.getMarketValueComplete());
                betForMarketResVo.setHomeBetAmount(new BigDecimal(0));
                betForMarketResVo.setHomeBetAmountPay(new BigDecimal(0));
                betForMarketResVo.setHomeBetAmountComplex(new BigDecimal(0));
            }
            betForMarketResVo.setMarketId(item.getMarketId());
            betForMarketResVo.setAwayBetAmount(item.getBetAmount());
            betForMarketResVo.setAwayBetAmountPay(item.getBetAmountPay());
            betForMarketResVo.setAwayBetAmountComplex(item.getBetAmountComplex());
            betForMarketResVoMap.put(item.getMarketValueComplete(), betForMarketResVo);
        });
        betForMarketResVoMap.forEach((k, v) -> {
            v.setBetAmountEquilibriumValue(v.getHomeBetAmount().subtract(v.getAwayBetAmount()));
            v.setBetAmountPayEquilibriumValue(v.getHomeBetAmountPay().subtract(v.getAwayBetAmountPay()));
            v.setBetAmountComplexEquilibriumValue(v.getHomeBetAmountComplex().subtract(v.getAwayBetAmountComplex()));
        });
        return new ArrayList<>(betForMarketResVoMap.values());
    }

}
