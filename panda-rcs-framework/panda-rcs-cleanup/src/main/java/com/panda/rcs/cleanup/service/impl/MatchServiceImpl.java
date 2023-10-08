package com.panda.rcs.cleanup.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Maps;
import com.panda.rcs.cleanup.entity.MatchTournamentVo;
import com.panda.rcs.cleanup.mapper.*;
import com.panda.rcs.cleanup.service.MatchService;
import com.panda.rcs.cleanup.utils.DataUtils;
import com.panda.rcs.cleanup.utils.MarketPlaceUtils;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MatchServiceImpl implements MatchService {

    private static final int delete_rows = 2000;

    @Value("${cleanup.delete.match.days:7}")
    private int deleteMatchDays;

    @Value("${cleanup.delete.match.rows:5}")
    private int deleteMatchLinkInfoRows;

    @Value("${cleanup.delete.order.rows:5000}")
    private int deleteOrderLinkInfoRows;

    @Value("${cleanup.delete.order.limit.rows:10000}")
    private int deleteOrderLimitRows;

    @Autowired
    private MatchMapper matchMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SettleMapper settleMapper;

    @Autowired
    private LoggingMapper loggingMapper;

    @Autowired
    RedisClient redisClient;

    @Autowired
    private MatchTournamentMapper matchTournamentMapper;

    @Override
    public void cleanupMatchBusiData() {
        try {
            List<Map<String, String>> matchIdLists = matchMapper.getExpiredMatchList(DataUtils.getTimestamp(deleteMatchDays));
            if (matchIdLists != null && matchIdLists.size() > 0) {
                log.info("::赛事关联信息清理::本次清理赛事总条数->{}", matchIdLists.size());
                int tempPage = 0;
                for (int i = 0; i < matchIdLists.size(); i++) {
                    boolean isEnd = (i == (matchIdLists.size() - 1));
                    if (i != 0 && i % deleteMatchLinkInfoRows == 0 || isEnd) {
                        try {
//                            List<Long> pageData = matchIdLists.subList(tempPage * deleteMatchLinkInfoRows, isEnd ? matchIdLists.size() : i);
                            List<Map<String, String>> pageMapData = matchIdLists.subList(tempPage * deleteMatchLinkInfoRows, isEnd ? matchIdLists.size() : i);
                            if (CollectionUtils.isEmpty(pageMapData)) {
                                log.info("没有需要清除的数据了");
                                return;
                            }
                            // 获取开赛时间
//                            Map<Long, List<Map<Long,Long>>> matchMap = pageMapData.stream().collect(Collectors.groupingBy(e -> MarketPlaceUtils.matchSportBeginTimeGroupKey(e)));
//                            Map<Long,Long> matchMap = pageMapData.stream().collect(Collectors.toMap(e -> e.get("id"),e ->e.get("beginTime")));
                            List<Long> pageData = pageMapData.stream().map(e -> Long.valueOf(e.get("id"))).collect(Collectors.toList());
                            clearTradeStatusCache(pageData);

                            int rows = matchMapper.deleteMatchEvent(pageData);
                            log.info("::赛事-第{}批->match_event_info数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMatchCollection(pageData);
                            log.info("::赛事-第{}批->rcs_match_collection数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteOrderBasketballMatrix(pageData);
                            log.info("::赛事-第{}批->rcs_order_basketball_matrix数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deletePredictBasketballMatrix(pageData);
                            log.info("::赛事-第{}批->rcs_predict_basketball_matrix数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deletePredictForecast(pageData);
                            log.info("::赛事-第{}批->rcs_predict_forecast数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deletePredictForecastPlay(pageData);
                            log.info("::赛事-第{}批->rcs_predict_forecast_play数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deletePredictBetOdds(pageData);
                            log.info("::赛事-第{}批->rcs_predict_bet_odds数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deletePredictBetStatis(pageData);
                            log.info("::赛事-第{}批->rcs_predict_bet_statis数据清理::，本次清理数据->{}", tempPage, rows);

                            try {
                                rows = orderMapper.deleteForecastSnapshotByMatchIds(pageData);
                                log.info("::赛事-第{}批->rcs_predict_forecast_snapshot数据清理::，本次清理数据->{}", tempPage, rows);
                            } catch (Exception e) {

                            }
                            rows = matchMapper.deleteMatchMarketConfig(pageData);
                            log.info("::赛事-第{}批->rcs_match_market_config数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMatchMarketConfigSub(pageData);
                            log.info("::赛事-第{}批->rcs_match_market_config_sub数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteTradeConfig(pageData);
                            log.info("::赛事-第{}批->rcs_trade_config数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMatchProfit(pageData);
                            log.info("::赛事-第{}批->rcs_match_profit数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteStandardPlaceRef(pageData);
                            log.info("::赛事-第{}批->rcs_standard_place_ref数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMarketNumStatis(pageData);
                            log.info("::赛事-第{}批->rcs_market_num_statis数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMarketOddsConfig(pageData);
                            log.info("::赛事-第{}批->rcs_market_odds_config数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMatchAutoSwitchLinked(pageData);
                            log.info("::赛事-第{}批->rcs_match_auto_switch_linked数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMatchDimensionStatistics(pageData);
                            log.info("::赛事-第{}批->rcs_match_dimension_statistics数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMatchMarketProbabilityConfig(pageData);
                            log.info("::赛事-第{}批->rcs_match_market_probability_config数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMatchOrderAcceptEventConfig(pageData);
                            log.info("::赛事-第{}批->rcs_match_order_accept_event_config数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMatchPlayConfig(pageData);
                            log.info("::赛事-第{}批->rcs_match_play_config数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMatrixInfo(pageData);
                            log.info("::赛事-第{}批->rcs_matrix_info数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteOrderSummary(pageData);
                            log.info("::赛事-第{}批->rcs_order_summary数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteFirstMarket(pageData);
                            log.info("::赛事-第{}批->rcs_first_market数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteStandardSportMarketSell(pageData);
                            log.info("::赛事-第{}批->rcs_standard_sport_market_sell数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteTradingAssignment(pageData);
                            log.info("::赛事-第{}批->rcs_trading_assignment数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteStatMatchIp(pageData);
                            log.info("::赛事-第{}批->stat_match_ip数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMatchMarketMarginConfig(pageData);
                            log.info("::赛事-第{}批->rcs_match_market_margin_config数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMatchPeriod(pageData);
                            log.info("::赛事-第{}批->match_period数据清理::，本次清理数据->{}", tempPage, rows);


                            rows = matchMapper.deleteMatchMonitorErrorLogByMatchIds(pageData);
                            log.info("::赛事-第{}批->rcs_match_monitor_error_log数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMatchMonitorMqLicenseByMatchIds(pageData);
                            log.info("::赛事-第{}批->rcs_match_monitor_mq_license数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMatchMonitorListByMatchIds(pageData);
                            log.info("::赛事-第{}批->rcs_match_monitor_list数据清理::，本次清理数据->{}", tempPage, rows);


                            rows = matchMapper.deleteMatchStatisticsInfo(pageData);
                            log.info("::赛事-第{}批->match_statistics_info数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMatchStatisticsInfoDetail(pageData);
                            log.info("::赛事-第{}批->match_statistics_info_detail数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMatchStatisticsInfoDetailSource(pageData);
                            log.info("::赛事-第{}批->match_statistics_info_detail_source数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteBroadCast(pageData);
                            log.info("::赛事-第{}批->rcs_broad_cast表数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteProfitRectangle(pageData);
                            log.info("::赛事-第{}批->rcs_profit_rectangle表数据清理::，本次清理数据->{}", tempPage, rows);


                            rows = orderMapper.deletePendingOrderByMatchIds(pageData);
                            log.info("::赛事-第{}批->rcs_pending_order表数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = orderMapper.deletePendingBetPredictByMatchIds(pageData);
                            log.info("::赛事-第{}批->rcs_predict_pending_bet_statis表数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = orderMapper.deletePendingForecastByMatchIds(pageData);
                            log.info("::赛事-第{}批->rcs_predict_pending_forecast表数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = orderMapper.deletePendingForecastPlayByMatchIds(pageData);
                            log.info("::赛事-第{}批->rcs_predict_pending_forecast_play表数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = loggingMapper.deleteOperateLogByMatchIds(pageData);
                            log.info("::赛事-第{}批->rcs_operate_log表数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteTraderWeightByMatchIds(pageData);
                            log.info("::赛事-第{}批->rcs_category_set_trader_weight表数据清理::，本次清理数据->{}", tempPage, rows);

                            rows = matchMapper.deleteMerchantsSinglePercentageByMatchIds(pageData);
                            log.info("::赛事-第{}批->merchants_single_percentage表数据清理::，本次清理数据->{}", tempPage, rows);

//                            List<Long> marketLists = matchMapper.getMarketIds(pageData);
                            List<Map<String, String>> markets = matchMapper.getMarketIds(pageData);

                            if (CollectionUtils.isNotEmpty(markets)) {
                                List<Long> marketLists = markets.stream().map(e -> Long.valueOf(e.get("id"))).collect(Collectors.toList());
                                rows = matchMapper.deleteMarketOddsByMarketIds(marketLists);
                                log.info("::赛事-第{}批->standard_sport_market_odds表数据清理::，本次清理数据->{}", tempPage, rows);
                                // 清理平衡值
                                clearBalance(pageMapData, markets);
                            }
                            rows = matchMapper.deleteStandardMarket(pageData);
                            log.info("::赛事-第{}批->standard_sport_market数据清理::，本次清理数据->{}", tempPage, rows);


                            /**
                             * 订单
                             */
                            List<String> orderNoLists = orderMapper.getOrderNoByMatchIds(pageData);
                            if (orderNoLists != null && orderNoLists.size() > 0) {
                                orderNoLists = orderNoLists.stream().distinct().collect(Collectors.toList());
                                int tempOPage = 0;
                                for (int oi = 0; oi < orderNoLists.size(); oi++) {
                                    boolean isOEnd = (oi == (orderNoLists.size() - 1));
                                    if (oi != 0 && oi % deleteOrderLinkInfoRows == 0 || isOEnd) {
                                        List<String> pageOData = orderNoLists.subList(tempOPage * deleteOrderLinkInfoRows, isOEnd ? orderNoLists.size() : oi);
                                        int rowsO = orderMapper.deleteOrderByOrderNo(pageOData);
                                        log.info("::赛事-第{}批::订单-第{}批->t_order数据清理::，本次清理数据->{}", tempPage, tempOPage, rowsO);

//                                        rowsO = settleMapper.deleteSettleByOrderNo(pageOData);
//                                        log.info("::赛事-第{}批::订单-第{}批->t_settle数据清理::，本次清理数据->{}", tempPage, tempOPage, rowsO);
//
//                                        rowsO = settleMapper.deleteSettleDetailByOrderNo(pageOData);
//                                        log.info("::赛事-第{}批::订单-第{}批->t_settle_detail数据清理::，本次清理数据->{}", tempPage, tempOPage, rowsO);
                                        Thread.sleep(300);
                                        tempOPage++;
                                    }
                                }
                            }

                            int orderRows = 0;
                            do {
                                orderRows = orderMapper.deleteOrderDetailByMatchIdsByLimit(pageData, deleteOrderLimitRows);
                                Thread.sleep(500);
                            } while (orderRows != 0);

                            /**
                             * 联赛模板
                             */
                            List<MatchTournamentVo> list = matchTournamentMapper.queryTournamentIdByMatchIds(pageData);
                            if (list != null && list.size() > 0) {
                                rows = matchTournamentMapper.deleteAcceptEventSettle(list);
                                log.info("::rcs_tournament_template_accept_event_settle数据清理::::第{}批，清理数据={}条", tempPage, rows);
                                rows = matchTournamentMapper.deleteAcceptConfigSettle(list);
                                log.info("::rcs_tournament_template_accept_config_settle数据清理::::第{}批，清理数据={}条", tempPage, rows);

                                rows = matchTournamentMapper.deleteAcceptEvent(list);
                                log.info("::rcs_tournament_template_accept_event数据清理::::第{}批，清理数据={}条", tempPage, rows);
                                rows = matchTournamentMapper.deleteAcceptConfig(list);
                                log.info("::rcs_tournament_template_accept_config数据清理::::第{}批，清理数据={}条", tempPage, rows);

                                rows = matchTournamentMapper.deleteTimeSharingNode(list);
                                log.info("::rcs_tournament_template_play_margain_ref数据清理::::第{}批，清理数据={}条", tempPage, rows);
                                rows = matchTournamentMapper.deletePlayMargain(list);
                                log.info("::rcs_tournament_template_play_margain数据清理::::第{}批，清理数据={}条", tempPage, rows);

                                rows = matchTournamentMapper.deleteTemplateEvent(list);
                                log.info("::rcs_tournament_template_event数据清理::::第{}批，清理数据={}条", tempPage, rows);

                                rows = matchTournamentMapper.deleteTemplateByMatchIds(pageData);
                                log.info("::rcs_tournament_template数据清理::::第{}批，清理数据={}条", tempPage, rows);
                            }

                            tempPage++;
                        } catch (Exception e) {
                            log.error("::清理异常-赛事Id={}，异常信息->{}", tempPage, e);
                        }
                    }
                }
                int rows = matchMapper.deleteMatch(DataUtils.getTimestamp(deleteMatchDays));
                log.info("::standard_match_info数据清理::，本次清理数据->{}", rows);
            }
        } catch (Exception e) {
            log.error("::清理异常->异常信息->", e);
        }

    }

    @Override
    public void cleanupNotExistMatchLinkData() {
        int rows = matchMapper.deleteMatchEventForNotMatchId();
        log.info("::MatchEvent数据清理::，本次清理数据->{}", rows);
        rows = matchMapper.deleteMatchPeriodForNotMatchId();
        log.info("::MatchPeriod数据清理::，本次清理数据->{}", rows);
        rows = matchMapper.deleteMatchStatisticsInfoForNotMatchId();
        log.info("::MatchStatisticsInfo数据清理::，本次清理数据->{}", rows);
        rows = matchMapper.deleteMatchStatisticsInfoDetailForNotMatchId();
        log.info("::MatchStatisticsInfoDetail数据清理::，本次清理数据->{}", rows);
        rows = matchMapper.deleteMatchStatisticsInfoDetailSourceForNotMatchId();
        log.info("::MatchStatisticsInfoDetailSource数据清理::，本次清理数据->{}", rows);
        rows = matchMapper.deleteMarketOdds();
        log.info("::投注项数据清理::，本次清理数据->{}", rows);
    }

    /**
     * @return void
     * @Description //清理平衡值
     * @Param [matchMap, pageData, marketLists]
     * @Author sean
     * @Date 2022/3/1
     **/
    private void clearBalance(List<Map<String, String>> pageMapData, List<Map<String, String>> markets) {
        try {
            log.info("删除redis 平衡值缓存");
            Map<String, String> mapKey = Maps.newHashMap();
            Map<String, String> matchBeginTimeMap = pageMapData.stream().collect(Collectors.toMap(e -> e.get("id"), e -> DataUtils.getDateExpect(Long.valueOf(e.get("begin_time")))));
            Map<String, String> matchSportIdMap = pageMapData.stream().collect(Collectors.toMap(e -> e.get("id"), e -> e.get("sport_id")));
            Map<String, List<Map<String, String>>> matchMap = markets.stream().collect(Collectors.groupingBy(m -> MarketPlaceUtils.matchPlaySubPlayGroupKey(m)));
            markets.forEach(e -> {
                Long marketId = Long.valueOf(e.get("id"));
                String key = "";
                String keyPlus = "";
                String suffixKey = "";
//                String dateExpect = DataUtils.getDateExpect(Long.valueOf(matchBeginTimeMap.get(e.get("match_id"))));
                Long sportId = Long.valueOf(matchSportIdMap.get(e.get("match_id")));
                // 盘口平衡值
                if (MarketPlaceUtils.MARKET_BALANCE_SPORT.contains(sportId)) {
                    if (!mapKey.containsKey(marketId.toString())) {
                        mapKey.put(marketId.toString(), marketId.toString());
                        suffixKey = "{" + marketId + "}";
                        key = String.format("rcs:odds:calc:%s:%s", matchBeginTimeMap.get(e.get("match_id")), marketId);
                        keyPlus = String.format("rcs:odds:calcPlus:%s:%s", matchBeginTimeMap.get(e.get("match_id")), marketId);
                        log.info("盘口缓存key={}，keyPlus={},suffixKey = {}", key, keyPlus, suffixKey);
                        redisClient.delete(key + suffixKey);
                        redisClient.delete(keyPlus + suffixKey);
                        redisClient.delete(key + ":count" + suffixKey);
                        redisClient.delete(key + ":lock" + suffixKey);
                    }
                } else if (MarketPlaceUtils.PLACE_BALANCE_SPORT.contains(sportId)) {
                    // 位置平衡值
                    if (!mapKey.containsKey(MarketPlaceUtils.matchPlaySubPlayGroupKey(e))) {
                        List<Map<String, String>> maps = matchMap.get(MarketPlaceUtils.matchPlaySubPlayGroupKey(e));
                        mapKey.put(MarketPlaceUtils.matchPlaySubPlayGroupKey(e), "1");
                        for (Map<String, String> map : maps) {
                            Integer count = NumberUtils.INTEGER_ONE;
                            // 子玩法位置跳分跳盘平衡值
                            suffixKey = "{" + String.format("%s_%s_%s_%s", map.get("match_id"), map.get("play_id"), map.get("sub_play_id"), count) + "}";
                            key = String.format("rcs:odds:calc:%s:%s", matchBeginTimeMap.get(map.get("match_id")), String.format("%s_%s_%s_%s", map.get("match_id"), map.get("play_id"), map.get("sub_play_id"), count));
                            keyPlus = String.format("rcs:odds:calcPlus:%s:%s", matchBeginTimeMap.get(map.get("match_id")), String.format("%s_%s_%s_%s", map.get("match_id"), map.get("play_id"), map.get("sub_play_id"), count));
                            String placeId = String.format("%s_%s_%s_%s", map.get("match_id"), map.get("play_id"), map.get("sub_play_id"), count);
                            deleteBanlanceByRedisKey(key, keyPlus, suffixKey, matchBeginTimeMap.get(map.get("match_id")), placeId);
                            // 玩法位置跳分调盘平衡值
                            suffixKey = "{" + String.format("%s_%s_%s", map.get("match_id"), map.get("play_id"), count) + "}";
                            key = String.format("rcs:odds:calc:%s:%s", matchBeginTimeMap.get(map.get("match_id")), String.format("%s_%s_%s", map.get("match_id"), map.get("play_id"), count));
                            keyPlus = String.format("rcs:odds:calcPlus:%s:%s", matchBeginTimeMap.get(map.get("match_id")), String.format("%s_%s_%s", map.get("match_id"), map.get("play_id"), count));
                            placeId = String.format("%s_%s_%s", map.get("match_id"), map.get("play_id"), count);
                            deleteBanlanceByRedisKey(key, keyPlus, suffixKey, matchBeginTimeMap.get(map.get("match_id")), placeId);
                            count++;
                        }
                    }
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    /**
     * @return void
     * @Description //删除rediskey
     * @Param [key, keyPlus, suffixKey, dateExpect, placeId]
     * @Author sean
     * @Date 2022/3/1
     **/
    private void deleteBanlanceByRedisKey(String key, String keyPlus, String suffixKey, String dateExpect, String placeId) {
        String keyjumpbet = String.format("rcs:odds:jumpMarket:%s:%s:bet{%s}", dateExpect, placeId, placeId);
        String keyjumpmix = String.format("rcs:odds:jumpMarket:%s:%s:mix{%s}", dateExpect, placeId, placeId);
        log.info("位置缓存key={}，keyPlus={},suffixKey = {}，keyjumpbet={}，keyjumpmix={}", key, keyPlus, suffixKey, keyjumpbet, keyjumpmix);
        redisClient.delete(key + suffixKey);
        redisClient.delete(keyPlus + suffixKey);
        redisClient.delete(keyjumpbet);
        redisClient.delete(keyjumpmix);
        redisClient.delete(key + ":count" + suffixKey);
        redisClient.delete(key + ":lock" + suffixKey);
    }

    @Override
    public void clearRedisByMatchId(Long matchId, int clearType) {
        log.info("清除操盘状态缓存：matchId=" + matchId);
        try {
            if (clearType == 0) {
                redisClient.delete(RedisKey.MainMarket.getAutoPlusMainMarketInfoKey(matchId));
                redisClient.delete(RedisKey.MainMarket.getLinkageMainMarketInfoKey(matchId));
                redisClient.delete(RedisKey.getPlaceholderMainPlayStatusKey(matchId));
                redisClient.delete(RedisKey.getMatchTradeStatusKey(matchId));
                redisClient.delete(RedisKey.getPlaySetCodeStatusKey(matchId));
                redisClient.delete(RedisKey.getAutoCloseStatusKey(matchId));
                redisClient.delete(RedisKey.getTradeModeKey(matchId));
                redisClient.delete(String.format("rcs:tradeStatus:play:%s", matchId));
                redisClient.delete(RedisKey.getChuZhangWarnSignKey(matchId, 1));
                redisClient.delete(RedisKey.getChuZhangWarnSignKey(matchId, 0));
                redisClient.delete(RedisKey.Config.getChuZhangSwitchKey(matchId, 1));
                redisClient.delete(RedisKey.Config.getChuZhangSwitchKey(matchId, 0));
                redisClient.delete(RedisKey.getChuZhangFrequencyKey(matchId));
            }
            for (long playId = 1L; playId <= 339L; playId++) {
                if (clearType == 0 || clearType == 1) {
                    redisClient.delete(RedisKey.getMarketPlaceStatusConfigKey(matchId, playId));
                }
                if (clearType == 0) {
                    redisClient.delete(RedisKey.getRelevanceTypeKey(matchId, playId));
                    redisClient.delete(RedisKey.getAutoPlusSwitchFlagKey(matchId, playId));
                    redisClient.delete(RedisKey.getLinkageSwitchFlagKey(matchId, playId));
                    redisClient.delete(RedisKey.getLinkageModeSignKey(matchId, playId));
                    redisClient.delete(RedisKey.getLinkageModeMarketKey(matchId, playId));
                    redisClient.delete(RedisKey.MainMarket.getLinkageDataSourceTimeKey(matchId, playId, playId));
                    redisClient.delete(String.format("rcs:marketSource:autoPlus:%s:%s", matchId, playId));
                }
            }
        } catch (Throwable t) {
            log.error("清除操盘状态缓存失败：matchId=" + matchId, t);
        }
    }

    @Override
    public void deleteAcceptConfig() {
        List<String> ids = matchMapper.queryMatchInfoId();
        try {
            if (!ids.isEmpty()) {
                for (String matchId : ids) {
                    String eventKey = String.format("rcs:match:event:play:collection:%s", matchId);
                    redisClient.delete(eventKey);
                    Thread.sleep(50L);
                }
            }
        } catch (Exception e) {
            log.error("删除接距配置错误:{}", e);
        }
    }

    /**
     * 清除操盘状态缓存
     *
     * @param matchIds
     */
    private void clearTradeStatusCache(List<Long> matchIds) {
        for (Long matchId : matchIds) {
            clearRedisByMatchId(matchId, 0);
        }
    }
}
