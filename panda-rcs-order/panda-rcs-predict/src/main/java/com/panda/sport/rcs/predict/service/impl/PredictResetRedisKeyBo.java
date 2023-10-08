package com.panda.sport.rcs.predict.service.impl;


import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.predict.RcsPredictBetOddsMapper;
import com.panda.sport.rcs.mapper.predict.RcsPredictBetStatisMapper;
import com.panda.sport.rcs.mapper.predict.RcsPredictForecastMapper;
import com.panda.sport.rcs.mapper.predict.RcsPredictForecastPlayMapper;
import com.panda.sport.rcs.pojo.vo.api.request.QueryBetForMarketReqVo;
import com.panda.sport.rcs.pojo.vo.api.request.QueryForecastPlayReqVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictBetOdds;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictBetStatis;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecast;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecastPlay;
import com.panda.sport.rcs.predict.utils.PredictRedisKeyUtil;
import com.panda.sport.rcs.predict.utils.RcsPredictNacosSnapshotConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PredictResetRedisKeyBo {

    @Autowired
    private RcsPredictBetStatisMapper rcsPredictBetStatisMapper;

    @Autowired
    private RcsPredictBetOddsMapper rcsPredictBetOddsMapper;

    @Autowired
    private RcsPredictForecastPlayMapper rcsPredictForecastPlayMapper;

    @Autowired
    private RcsPredictForecastMapper rcsPredictForecastMapper;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RcsPredictNacosSnapshotConfig rcsPredictNacosSnapshotConfig;

    /**
     * redis 过期 坑位货量 重新设置缓存
     */
    public void resetPlaceNumRedisKey(OrderItem item, Integer seriesType) {
        if (!rcsPredictNacosSnapshotConfig.isForecastExpiryOff()) {
            return;
        }
        // temp key
        String palceNumTotalBetAmountTempKey = PredictRedisKeyUtil.getPalceNumTotalBetAmountTempKey(item, seriesType.toString());
        String palceNumTotalBetAmountPayTempKey = PredictRedisKeyUtil.getPalceNumTotalBetAmountPayTempKey(item, seriesType.toString());
        String palceNumTotalBetAmountComplexTempKey = PredictRedisKeyUtil.getPalceNumTotalBetAmountComplexTempKey(item, seriesType.toString());
        String palceNumBetNumTempKey = PredictRedisKeyUtil.getPalceNumTotalBetNumTempKey(item, seriesType.toString());

        //坑位 投注项总投注
        String palceNumTotalBetAmountKey = PredictRedisKeyUtil.getPalceNumTotalBetAmountKey(item, seriesType.toString());
        //纯赔付货量
        String palceNumTotalBetAmountPayKey = PredictRedisKeyUtil.getPalceNumTotalBetAmountPayKey(item, seriesType.toString());
        //混合型货量
        String palceNumTotalBetAmountComplexKey = PredictRedisKeyUtil.getPalceNumTotalBetAmountComplexKey(item, seriesType.toString());
        //盘口位置 投注项 总投注笔数
        String palceNumBetNumKey = PredictRedisKeyUtil.getPalceNumTotalBetNumKey(item, seriesType.toString());

        String unique = "matchId.%s.playId.%s.matchType.%s.dataType.%s.dataTypeValue.%s.oddsType.%s.subPlayId.%s.seriesType.%s";
        String format = String.format(unique, item.getMatchId(), item.getPlayId(), item.getMatchType(),
                2, item.getPlaceNum(), item.getPlayOptions(),
                item.getSubPlayId(), seriesType);
        RcsPredictBetOdds rcsPredictBetOdds = rcsPredictBetOddsMapper.selectOne(new LambdaQueryWrapper<RcsPredictBetOdds>().eq(RcsPredictBetOdds::getHashUnique, DigestUtil.md5Hex(format)));
        if (!redisClient.exist(palceNumBetNumKey) && !ObjectUtils.isEmpty(rcsPredictBetOdds) && rcsPredictBetOdds.getBetOrderNum().intValue() >= 1) {
            redisClient.hincrBy(palceNumTotalBetAmountTempKey, PredictRedisKeyUtil.getCommonKey(item, item.getPlayOptions()), rcsPredictBetOdds.getBetAmountTemp().multiply(new BigDecimal(100)).longValue());
            redisClient.hincrBy(palceNumTotalBetAmountPayTempKey, PredictRedisKeyUtil.getCommonKey(item, item.getPlayOptions()), rcsPredictBetOdds.getBetAmountPayTemp().multiply(new BigDecimal(100)).longValue());
            redisClient.hincrBy(palceNumTotalBetAmountComplexTempKey, PredictRedisKeyUtil.getCommonKey(item, item.getPlayOptions()), rcsPredictBetOdds.getBetAmountComplexTemp().multiply(new BigDecimal(100)).longValue());
            redisClient.hincrBy(palceNumBetNumTempKey, PredictRedisKeyUtil.getCommonKey(item, item.getPlayOptions()), rcsPredictBetOdds.getBetOrderNumTemp().longValue());

            redisClient.hincrBy(palceNumTotalBetAmountKey, item.getPlayOptions(), rcsPredictBetOdds.getBetAmount().multiply(new BigDecimal(100)).longValue());
            redisClient.hincrBy(palceNumTotalBetAmountPayKey, item.getPlayOptions(), rcsPredictBetOdds.getBetAmountPay().multiply(new BigDecimal(100)).longValue());
            redisClient.hincrBy(palceNumTotalBetAmountComplexKey, item.getPlayOptions(), rcsPredictBetOdds.getBetAmountComplex().multiply(new BigDecimal(100)).longValue());
            redisClient.hincrBy(palceNumBetNumKey, item.getPlayOptions(), rcsPredictBetOdds.getBetOrderNum().longValue());
        }
    }

    /**
     * redis 过期 盘口货量  重新设置缓存
     *
     * @param item
     * @param seriesType
     */
    public void resetOddsDataRedisKey(OrderItem item, Integer seriesType) {
        if (!rcsPredictNacosSnapshotConfig.isForecastExpiryOff()) {
            return;
        }
        String oddsTotalBetAmountTempKey = PredictRedisKeyUtil.getOddsTotalBetAmountTempKey(item, seriesType.toString());
        String oddsTotalBetAmountPayTempKey = PredictRedisKeyUtil.getOddsTotalBetAmountPayTempKey(item, seriesType.toString());
        String oddsTotalBetAmountComplexTempKey = PredictRedisKeyUtil.getOddsTotalBetAmountComplexTempKey(item, seriesType.toString());
        String oddsTotalBetNumTempKey = PredictRedisKeyUtil.getOddsTotalBetNumTempKey(item, seriesType.toString());

        //投注项总投注金额
        String oddsTotalBetAmountKey = PredictRedisKeyUtil.getOddsTotalBetAmountKey(item, seriesType.toString());
        //纯赔付货量
        String oddsTotalBetAmountPayKey = PredictRedisKeyUtil.getOddsTotalBetAmountPayKey(item, seriesType.toString());
        //混合型货量
        String oddsTotalBetAmountComplexKey = PredictRedisKeyUtil.getOddsTotalBetAmountComplexKey(item, seriesType.toString());
        //投注项总投注笔数
        String oddsTotalBetNumKey = PredictRedisKeyUtil.getOddsTotalBetNumKey(item, seriesType.toString());

        //投注项最大赔付
        String oddsTotalPaidAmonutKey = PredictRedisKeyUtil.getOddsTotalPaidAmonutKey(item, seriesType.toString());

        String unique = "matchId.%s.playId.%s.matchType.%s.dataType.%s.dataTypeValue.%s.oddsType.%s.subPlayId.%s.seriesType.%s";
        String format = String.format(unique, item.getMatchId(), item.getPlayId(), item.getMatchType(),
                1, item.getMarketId(), item.getPlayOptions(),
                item.getSubPlayId(), seriesType);
        RcsPredictBetOdds rcsPredictBetOdds = rcsPredictBetOddsMapper.selectOne(new LambdaQueryWrapper<RcsPredictBetOdds>().eq(RcsPredictBetOdds::getHashUnique, DigestUtil.md5Hex(format)));
        if (!redisClient.exist(oddsTotalBetNumKey) && !ObjectUtils.isEmpty(rcsPredictBetOdds) && rcsPredictBetOdds.getBetOrderNum().intValue() >= 1) {
            redisClient.hincrBy(oddsTotalBetAmountTempKey, PredictRedisKeyUtil.getOddsTotalCommonKey(item, item.getPlayOptions()), rcsPredictBetOdds.getBetAmountTemp().multiply(new BigDecimal(100)).longValue());
            redisClient.hincrBy(oddsTotalBetAmountPayTempKey, PredictRedisKeyUtil.getOddsTotalCommonKey(item, item.getPlayOptions()), rcsPredictBetOdds.getBetAmountPayTemp().multiply(new BigDecimal(100)).longValue());
            redisClient.hincrBy(oddsTotalBetAmountComplexTempKey, PredictRedisKeyUtil.getOddsTotalCommonKey(item, item.getPlayOptions()), rcsPredictBetOdds.getBetAmountComplexTemp().multiply(new BigDecimal(100)).longValue());
            redisClient.hincrBy(oddsTotalBetNumTempKey, PredictRedisKeyUtil.getOddsTotalCommonKey(item, item.getPlayOptions()), rcsPredictBetOdds.getBetOrderNumTemp().longValue());

            redisClient.hincrBy(oddsTotalBetAmountKey, item.getPlayOptions(), rcsPredictBetOdds.getBetAmount().multiply(new BigDecimal(100)).longValue());
            redisClient.hincrBy(oddsTotalBetAmountPayKey, item.getPlayOptions(), rcsPredictBetOdds.getBetAmountPay().multiply(new BigDecimal(100)).longValue());
            redisClient.hincrBy(oddsTotalBetAmountComplexKey, item.getPlayOptions(), rcsPredictBetOdds.getBetAmountComplex().multiply(new BigDecimal(100)).longValue());
            redisClient.hincrBy(oddsTotalBetNumKey, item.getPlayOptions(), rcsPredictBetOdds.getBetOrderNum().longValue());
            redisClient.hincrBy(oddsTotalPaidAmonutKey, item.getPlayOptions(), rcsPredictBetOdds.getPaidAmount().multiply(new BigDecimal(100)).longValue());
        }

    }


    /**
     * redis 过期 最小维度重新设置缓存
     */
    public void resetStatisRedisKey(String key) {
        if (!rcsPredictNacosSnapshotConfig.isForecastExpiryOff()) {
            return;
        }
        RcsPredictBetStatis rcsPredictBetStatis = rcsPredictBetStatisMapper.selectOne(new LambdaQueryWrapper<RcsPredictBetStatis>()
                .eq(RcsPredictBetStatis::getHashUnique, DigestUtil.md5Hex(key)));
        boolean exist = redisClient.exist(key);
        if (!exist && !ObjectUtils.isEmpty(rcsPredictBetStatis) && rcsPredictBetStatis.getBetNum() >= 1) {
            //累加 总货量 纯赔付额
            redisClient.hincrBy(key, "totalBetAmountPay", new BigDecimal(rcsPredictBetStatis.getBetAmountPay().longValue()).multiply(new BigDecimal(100)).longValue());
            //累加 总货量  混合型
            redisClient.hincrBy(key, "totalBetAmountComplex", new BigDecimal(rcsPredictBetStatis.getBetAmountComplex().longValue()).multiply(new BigDecimal(100)).longValue());
            //累加 注单数量
            redisClient.hincrBy(key, "totalBetNum", rcsPredictBetStatis.getBetNum());
            //累加 赔率和
            redisClient.hincrByFloat(key, "oddsSum", new BigDecimal(rcsPredictBetStatis.getOddsSum().longValue()).multiply(new BigDecimal(100000)).doubleValue());
            //累加 总货量
            redisClient.hincrBy(key, "totalBetAmount", new BigDecimal(rcsPredictBetStatis.getBetAmount().longValue()).multiply(new BigDecimal(100)).longValue());
        }
    }


    /**
     * redis 过期 玩法级别forecast / 坑位级别 重新设置缓存
     */
    public void resetPlayForecastRedisKey(OrderItem item, Integer play, Integer placeNum) {
        if (!rcsPredictNacosSnapshotConfig.isForecastExpiryOff()) {
            return;
        }
        String key = String.format("rcs:profit:match:%s:%s:%s:%s", item.getMatchId(), item.getMatchType(), play, placeNum);
        boolean exist = redisClient.exist(key);
        if (!exist) {
            QueryForecastPlayReqVo queryForecastPlayReqVo = new QueryForecastPlayReqVo();
            queryForecastPlayReqVo.setHashUnique(DigestUtil.md5Hex(key));

            List<RcsPredictForecastPlay> rcsPredictForecastPlayList = rcsPredictForecastPlayMapper.selectList(queryForecastPlayReqVo);
            if (!CollectionUtils.isEmpty(rcsPredictForecastPlayList))
                rcsPredictForecastPlayList.forEach(list -> redisClient.hincrByFloat(key, list.getScore().toString(), list.getProfitValue().doubleValue()));
        }
    }


    /**
     * forecast计算 (只包含让球 和大小的) (最小维度)
     */
    public void resetForecastStatisRedisKey(OrderItem orderItem) {
        if (!rcsPredictNacosSnapshotConfig.isForecastExpiryOff()) {
            return;
        }
        String key = String.format("rcs:risk:predict:forecast.match_id.%s.match_type.%s.play_id.%s.market_id.%s.odds_item.%s.bet_score.%s",
                orderItem.getMatchId(), orderItem.getMatchType(), orderItem.getPlayId(), orderItem.getMarketId(), orderItem.getPlayOptions(), StringUtils.isEmpty(orderItem.getScoreBenchmark()) ? "0:0" : orderItem.getScoreBenchmark());
        boolean exist = redisClient.exist(key);
        if (!exist) {
            QueryBetForMarketReqVo queryBetForMarketReqVo = new QueryBetForMarketReqVo();
            queryBetForMarketReqVo.setHashUnique(DigestUtil.md5Hex(key));
            List<RcsPredictForecast> rcsPredictForecastPlayList = rcsPredictForecastMapper.selectList(queryBetForMarketReqVo);
            if (!CollectionUtils.isEmpty(rcsPredictForecastPlayList))
                rcsPredictForecastPlayList.forEach(list -> redisClient.hincrByFloat(key, list.getForecastScore().toString(), list.getProfitAmount().doubleValue()));
        }
    }


}
