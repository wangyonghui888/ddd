package com.panda.sport.rcs.mgr.utils;

import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainMapper;
import com.panda.sport.rcs.mgr.service.impl.MatchStatisticsInfoDetailService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Description   //操盘的一些校验
 * @Param
 * @Author  sean
 * @Date   2021/1/9
 * @return
 **/
@Service
@Slf4j
public class TradeVerificationService {
    @Autowired
    private MatchStatisticsInfoDetailService matchStatisticsInfoDetailService;

    /**
     * @Description   //计算主盘值
     * @Param [addition1, totalChange, marketAdjustRange, addtion5, playId]
     * @Author  sean
     * @Date   2021/3/9
     * @return java.math.BigDecimal
     **/
    public static BigDecimal getNewMainMarketValue(BigDecimal addition1, BigDecimal totalChange,BigDecimal marketAdjustRange,String addtion5,Long playId) {
        log.info("addition1={},times={},addtion5={},playId={}",addition1.toString(),totalChange.toString(),addtion5,playId);
        if (totalChange.doubleValue() == 0){
            return addition1;
        }
        BigDecimal n1 = addition1.add(totalChange);
        if ("39".equalsIgnoreCase(playId.toString())){
            if (marketAdjustRange.doubleValue() % 1 == 0){
                marketAdjustRange = BigDecimal.ONE;
            }else {
                marketAdjustRange = new BigDecimal("0.5");
            }
            BigDecimal times = totalChange.abs().divide(marketAdjustRange);
            marketAdjustRange = totalChange.divide(times);
            n1 = addition1;
            for (int i=1;i<=times.intValue();i++){
                n1 = getNewMainMarket(n1,marketAdjustRange,addtion5);
            }
        }
        log.info("getNewMainMarketValue result = {}",n1.toString());
        return n1;
    }
    /**
     * @Description   //计算一次的结果
     * @Param [m1, flag, addtion1]
     * @Author  sean
     * @Date   2021/3/9
     * @return java.math.BigDecimal
     **/
    private static BigDecimal getNewMainMarket(BigDecimal addition1, BigDecimal marketAdjustRange,String addtion5) {
        if (addition1.compareTo(BigDecimal.ZERO) == 0 && new BigDecimal(addtion5).doubleValue()%1 !=0){
            if (marketAdjustRange.doubleValue() > 0){
                addition1 = new BigDecimal("0.5");
            }else {
                addition1 = new BigDecimal("-0.5");
            }
        }
        BigDecimal n1 = addition1.add(marketAdjustRange);
        if (n1.abs().compareTo(new BigDecimal("0.5")) == 0 ){
            if (marketAdjustRange.abs().compareTo(new BigDecimal("0.5")) == 0){
                if (marketAdjustRange.doubleValue() >0){
                    n1 = n1.add(new BigDecimal("0.5"));
                }else {
                    n1 = n1.subtract(new BigDecimal("0.5"));
                }
            }else {
                if (n1.doubleValue() >0){
                    n1 = n1.subtract(new BigDecimal("0.5"));
                }else {
                    n1 = n1.add(new BigDecimal("0.5"));
                }
            }
        }
        return n1;
    }
    /**
     * @Description   //获取当前比分
     * @Param [matchId, playId]
     * @Author  sean
     * @Date   2021/4/15
     * @return int
     **/
    public int getBasketballScoreSum(Long matchId, Long playId) {
        ScoreTypeEnum scoreTypeEnum = ScoreTypeEnum.getScoreTypeEnum(2L, playId);
        if (scoreTypeEnum == null) {
            return 0;
        }
        MatchStatisticsInfoDetail scoreInfo = matchStatisticsInfoDetailService.getByScoreType(matchId, scoreTypeEnum);
        if (scoreInfo == null) {
            return 0;
        }
        return scoreInfo.getT1() + scoreInfo.getT2();
    }
}
