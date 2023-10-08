package com.panda.rcs.cleanup.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MatchMapper {

    /**
     * 查询过期赛事Id
     * @param expiredTime 过期时间戳
     * @return
     */
    List<Map<String,String>> getExpiredMatchList(@Param("expiredTime") Long expiredTime);

    /**
     * 删除已经完赛的赛事信息（指定天数）
     * @param expiredTime 过期时间戳
     * @return
     */
    int deleteMatch(@Param("expiredTime") Long expiredTime);

    /**
     * 删除完赛赛事事件数据
     * @param matchIds 完赛赛事Id集合
     * @return
     */
    int deleteMatchEvent(@Param("matchIds") List<Long> matchIds);

    /**
     * 删除完赛的赛事收藏关联数据
     * @param matchIds
     * @return
     */
    int deleteMatchCollection(@Param("matchIds") List<Long> matchIds);

    /**
     * 删除篮球订单矩阵数据
     * @param matchIds
     * @return
     */
    int deleteOrderBasketballMatrix(@Param("matchIds") List<Long> matchIds);

    /**
     * 删除篮球货量矩阵数据
     * @param matchIds
     * @return
     */
    int deletePredictBasketballMatrix(@Param("matchIds") List<Long> matchIds);

    /**
     * 删除基准分forecast表
     * @param matchIds
     * @return
     */
    int deletePredictForecast(@Param("matchIds") List<Long> matchIds);

    /**
     * 删除基准分forecast表-玩法级别
     * @param matchIds
     * @return
     */
    int deletePredictForecastPlay(@Param("matchIds") List<Long> matchIds);

    /**
     * 删除投注项货量表
     * @param matchIds
     * @return
     */
    int deletePredictBetOdds(@Param("matchIds") List<Long> matchIds);

    /**
     * 删除投注统计货量表
     * @param matchIds
     * @return
     */
    int deletePredictBetStatis(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteTradeConfig(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteStandardPlaceRef(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteStandardMarket(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteStatMatchIp(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteMatchMarketConfig(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteMatchMarketConfigSub(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteMatchMarketMarginConfig(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteMatchProfit(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteMatchPeriod(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteMatchStatisticsInfo(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteMatchStatisticsInfoDetail(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteMarketNumStatis(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteMarketOddsConfig(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteMatchAutoSwitchLinked(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteMatchDimensionStatistics(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteMatchMarketProbabilityConfig(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteMatchOrderAcceptEventConfig(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteMatchPlayConfig(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteMatrixInfo(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteOrderSummary(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteFirstMarket(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteStandardSportMarketSell(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteTradingAssignment(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteMatchStatisticsInfoDetailSource(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteBroadCast(@Param("matchIds") List<Long> matchIds);

    /**
     *
     * @param matchIds
     * @return
     */
    int deleteProfitRectangle(@Param("matchIds") List<Long> matchIds);

    /**
     *赛事阶段首次删除赛事表不存在的数据
     * @return
     */
    int deleteMatchPeriodForNotMatchId();

    /**
     *赛事阶段首次删除赛事表不存在的数据
     * @return
     */
    int deleteMatchStatisticsInfoForNotMatchId();

    /**
     *赛事阶段首次删除赛事表不存在的数据
     * @return
     */
    int deleteMatchStatisticsInfoDetailForNotMatchId();

    /**
     *赛事阶段首次删除赛事表不存在的数据
     * @return
     */
    int deleteMatchStatisticsInfoDetailSourceForNotMatchId();

    /**
     *赛事阶段首次删除赛事表不存在的数据
     * @return
     */
    int deleteMatchEventForNotMatchId();

    /**
     * 删除盘口表中不存在的投注项信息
     * @return
     */
    int deleteMarketOdds();

    /**
     * 根据赛事Id查询盘口Id
     * @param matchIds
     * @return
     */
//    List<Long> getMarketIds(@Param("matchIds") List<Long> matchIds);
    List<Map<String,String>> getMarketIds(@Param("matchIds") List<Long> matchIds);

    /**
     * 根据盘口Id删除投注项信息
     * @param marketIds
     * @return
     */
    int deleteMarketOddsByMarketIds(@Param("marketIds") List<Long> marketIds);

    /**
     * 查询范围内的赛事
     *
     * @param matchIdStart
     * @param matchIdEnd
     * @return
     */
    List<Long> selectMatchIds(@Param("matchIdStart") long matchIdStart, @Param("matchIdEnd") long matchIdEnd);

    int deleteTraderWeightByMatchIds(@Param("matchIds") List<Long> matchIds);

    int deleteMatchMonitorErrorLogByMatchIds(@Param("matchIds") List<Long> matchIds);


    int deleteMatchMonitorMqLicenseByMatchIds(@Param("matchIds") List<Long> matchIds);


    int deleteMatchMonitorListByMatchIds(@Param("matchIds") List<Long> matchIds);

    int deleteMerchantsSinglePercentageByMatchIds(@Param("matchIds") List<Long> matchIds);

    int deleteMerchantsSinglePercentageInit();

    List<String> queryMatchInfoId();
}