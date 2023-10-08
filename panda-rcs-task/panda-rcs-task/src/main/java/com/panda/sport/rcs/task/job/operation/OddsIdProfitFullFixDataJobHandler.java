package com.panda.sport.rcs.task.job.operation;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsMarketOddsConfig;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.pojo.TSettle;
import com.panda.sport.rcs.pojo.statistics.RcsProfitMarket;
import com.panda.sport.rcs.task.service.profit.CalcProfitRectangleAdapter;
import com.panda.sport.rcs.task.wrapper.RcsMarketOddsConfigService;
import com.panda.sport.rcs.task.wrapper.StandardSportMarketOddsService;
import com.panda.sport.rcs.task.wrapper.order.ITOrderDetailService;
import com.panda.sport.rcs.task.wrapper.order.ITSettleService;
import com.panda.sport.rcs.task.wrapper.statistics.RcsProfitMarketService;
import com.panda.sport.rcs.utils.MarketValueUtils;
import com.panda.sport.rcs.profit.utils.ProfitUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.task.job.operation
 * @Description :  数据修复
 * @Date: 2020-01-06 12:19
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@JobHandler(value="oddsIdProfitFixDataJobHandler")
@Component
@Slf4j
public class OddsIdProfitFullFixDataJobHandler extends IJobHandler {
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ITOrderDetailService orderDetailService;

    @Autowired
    private RcsMarketOddsConfigService rcsMarketOddsConfigService;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    private RcsProfitMarketService rcsProfitMarketService;

    @Autowired
    private StandardSportMarketOddsService standardSportMarketOddsService;

    @Autowired
    private ITSettleService settleService;

    @Autowired
    private CalcProfitRectangleAdapter calcProfitRectangleAdapter;

    ConcurrentHashMap<String, RcsMarketOddsConfig> oddsMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Long> marketBetAmountMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, RcsProfitMarket> profitMarketMap = new ConcurrentHashMap<>();

    List<ProfitRectangleVo> profitRectangles = new ArrayList<>();

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("数据修复开始执行！");


        Long matchId = 0L;


        if (StringUtils.isNotBlank(param)) {
            try {
                matchId = Long.valueOf(param);
            } catch (Exception ex) {
                matchId = 0L;
            }
        }

        Integer limit = 1000;
        Long id = 0L;

        //redisClient.batchDel("Rcs:realVolume:matchId=*");
        handleSettled(matchId);

        List<TOrderDetail> list = orderDetailService.getTopById(id, matchId, limit);
        XxlJobLogger.log("查找出" + list.size() + " 条记录");

        BigDecimal matchTotalBetAmount = BigDecimal.ZERO;
        Integer matchTotalBetNums = 0;
        while (list != null && list.size() > 0) {
            for (TOrderDetail orderDetail : list) {
                if (id < orderDetail.getId()) {
                    id = orderDetail.getId();
                }


                ProfitRectangleVo rectangleVo = new ProfitRectangleVo();
                rectangleVo.setMarketId(orderDetail.getMarketId());
                rectangleVo.setMatchId(orderDetail.getMatchId());
                rectangleVo.setMatchType(orderDetail.getMatchType());
                rectangleVo.setPlayId(orderDetail.getPlayId());

                if (!profitRectangles.contains(rectangleVo)) {
                    profitRectangles.add(rectangleVo);
                }


                matchTotalBetNums = matchTotalBetNums + 1;
                matchTotalBetAmount.add(BigDecimal.valueOf(orderDetail.getBetAmount()));

                XxlJobLogger.log("查找出" + list.size() + " 条记录");
                handleProfitMarket(orderDetail);
                handleData(orderDetail);


                XxlJobLogger.log("查找出" + list.size() + " 条记录");
            }
            list = orderDetailService.getTopById(id, matchId, limit);
        }


        for (RcsMarketOddsConfig oddsConfig : oddsMap.values()) {
            Long loadId = System.currentTimeMillis();
            try {
                //redisClient.lock(getOddsIdTotalBetAmountCacheKey(oddsConfig.getMatchType(), oddsConfig.getMatchMarketId()), loadId, 1000);
                //写缓存
                redisClient.hSet(getOddsIdTotalBetAmountCacheKey(oddsConfig.getMatchId(), oddsConfig.getMatchType(), oddsConfig.getMatchMarketId()), oddsConfig.getMarketOddsId().toString(), String.valueOf(oddsConfig.getBetAmount1().longValue()));

                String obj = redisClient.hGet(getOddsIdTotalBetAmountCacheKey(oddsConfig.getMatchId(), oddsConfig.getMatchType(), oddsConfig.getMatchMarketId()), oddsConfig.getMarketOddsId().toString());
                redisClient.hSet(getOddsIdTotalBetTimesCacheKey(oddsConfig.getMatchId(), oddsConfig.getMatchType(), oddsConfig.getMatchMarketId()), oddsConfig.getMarketOddsId().toString(), oddsConfig.getBetOrderNum().toString());
                BigDecimal totalBetAmonut = oddsConfig.getPaidAmount().add(oddsConfig.getProfitValue1());
                redisClient.hSet(getMarketIdTotalBetAmonutCacheKey(oddsConfig.getMatchId(), oddsConfig.getMatchType()), oddsConfig.getMarketOddsId().toString(), oddsConfig.getBetAmount1().toString());
                redisClient.hSet(getOddsIdTotalPaidAmountCacheKey(oddsConfig.getMatchId(), oddsConfig.getMatchType(), oddsConfig.getMatchMarketId()), oddsConfig.getMarketOddsId().toString(), String.valueOf(totalBetAmonut.longValue()));
                redisClient.hSet(getOddsIdProfitValueCacheKey(oddsConfig.getMatchId(), oddsConfig.getMatchType(), oddsConfig.getMatchMarketId()), oddsConfig.getMarketOddsId().toString(), String.valueOf(oddsConfig.getProfitValue().longValue()));
            } finally {
                //redisClient.unlock(getOddsIdTotalBetAmountCacheKey(oddsConfig.getMatchType(),oddsConfig.getMatchMarketId()),loadId);
            }

            XxlJobLogger.log("更新缓存odss_id：" + oddsConfig.getMarketOddsId());
            //入库
            QueryWrapper<RcsMarketOddsConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("market_odds_id", oddsConfig.getMarketOddsId());
            queryWrapper.lambda().eq(RcsMarketOddsConfig::getMatchType, oddsConfig.getMatchType());
            RcsMarketOddsConfig oddsConfig1 = rcsMarketOddsConfigService.getOne(queryWrapper);

            oddsConfig.setPaidAmount(oddsConfig.getPaidAmount().divide(BigDecimal.valueOf(100)));
            oddsConfig.setProfitValue(oddsConfig.getProfitValue1());
            oddsConfig.setBetAmount(oddsConfig.getBetAmount1());

            if (oddsConfig1 == null) {
                rcsMarketOddsConfigService.save(oddsConfig);
            } else {
                oddsConfig.setId(oddsConfig1.getId());
                rcsMarketOddsConfigService.updateById(oddsConfig);
            }

            //发送mq
            producerSendMessageUtils.sendMessage(MqConstants.WS_ODDS_CHANGED_TOPIC, MqConstants.WS_ODDS_CHANGED_TAG, "", oddsConfig);

            Map<String, String> map = new HashMap();
            map.put("matchId", String.valueOf(oddsConfig.getMatchId()));
            map.put("playId", String.valueOf(oddsConfig.getMarketCategoryId()));
            map.put("marketId", String.valueOf(oddsConfig.getMatchMarketId()));
            map.put("oddsId", String.valueOf(oddsConfig.getMarketOddsId()));
            producerSendMessageUtils.sendMessage(MqConstants.ORDER_AMOUNT_CHANGE_TOPIC, map);
            XxlJobLogger.log("updateMatchOdds推送实货量、期望值{}", JsonFormatUtils.toJson(oddsConfig));
        }

        for (RcsProfitMarket rcsProfitMarket : profitMarketMap.values()) {
            rcsProfitMarketService.insertOrSave(rcsProfitMarket);
        }

        for (ProfitRectangleVo rectangleVo : profitRectangles) {
            calcProfitRectangleAdapter.calc(rectangleVo);
        }


        redisClient.hSet(getMatchTotalBetAmountCacheKey(matchId), "match_Total_Bet_Amount", String.valueOf(matchTotalBetAmount.longValue() / 100));
        redisClient.hSet(getMatchTotalBetNumsCacheKey(matchId), "matchTotalBetNums", matchTotalBetNums.toString());

        XxlJobLogger.log("数据修复结束执行！");
        return SUCCESS;
    }

    @Autowired
    private StandardSportMarketOddsService oddsService;

    /**
     * @Description   处理期望值
     * @Param [orderDetail]
     * @Author  toney
     * @Date  11:23 2020/2/6
     * @return void
     **/
    private void handleProfitMarket(TOrderDetail orderDetail){
        //判断是不是足球
        if(orderDetail.getSportId()!=1){
            return;
        }
        if(!ProfitUtil.checkIsHandicap(orderDetail.getPlayId())) {
            return;
        }

        RcsProfitMarket profitMarket =  profitMarketMap.get(orderDetail.getMarketId()+"_"+orderDetail.getPlayId()+"_"+MarketValueUtils.mergeMarket(orderDetail.getMarketValue()).toString()+"_"+orderDetail.getMatchType());
        if(profitMarket == null){
            profitMarket = new RcsProfitMarket();
            profitMarket.setMatchId(orderDetail.getMatchId());
            profitMarket.setMatchType(orderDetail.getMatchType().toString());
            profitMarket.setCreateTime(new Date());
            profitMarket.setMarketValue(orderDetail.getMarketValue());
            profitMarket.setPlayId(orderDetail.getPlayId());
            profitMarket.setMarketValue(MarketValueUtils.mergeMarket(orderDetail.getMarketValue()).toString());
            profitMarket.setUpdateTime(new Date());
            profitMarket.setAddition1(BigDecimal.ZERO);
            profitMarket.setAddition2(BigDecimal.ZERO);
        }

        BigDecimal paiAmount = orderDetail.getPaidAmount().divide(BigDecimal.valueOf(100));
        BigDecimal betAmount =  orderDetail.getBetAmount1();
        BigDecimal profitValue = betAmount.subtract(paiAmount);

        StandardSportMarketOdds odds = getStandardSportMarketOdds(orderDetail.getPlayOptionsId());
        if(odds ==null){
            log.error("odds为空","统计期望详情报错", JsonFormatUtils.toJson(orderDetail));
            return;
        }


        if(ProfitUtil.checkGoalLine(orderDetail.getPlayId())){
            //大小球
            HandleGoalLine(betAmount,odds.getOddsType(),profitMarket,profitValue);
        }else if(ProfitUtil.checkAsianHandicap(orderDetail.getPlayId())){
            //让球
            HandleAsianHandicap(betAmount,odds.getOddsType(),profitMarket,profitValue);
        }
        profitMarket.setUpdateTime(new Date());
        profitMarket.setMatchType(orderDetail.getMatchType().toString());
        profitMarket.setProfitValue(profitMarket.getAddition1().add(profitMarket.getAddition2()));
        profitMarketMap.put(orderDetail.getMarketId()+"_"+orderDetail.getPlayId()+"_"+MarketValueUtils.mergeMarket(orderDetail.getMarketValue()).toString()+"_"+orderDetail.getMatchType(),profitMarket);
    }


    /**
     * @Description   亚洲让分盘
     * 让球: 4: 全场让球 19:亚盘让球-上半场
     * @Param [item, oddsType, rcsProfitMarket, profitValue]
     * @Author  toney
     * @Date  13:39 2019/12/12
     * @return void
     **/
    private void HandleAsianHandicap(BigDecimal betAmount,String oddsType,RcsProfitMarket rcsProfitMarket,BigDecimal profitValue){
        if("1".equals(oddsType)){
            rcsProfitMarket.setAddition1(rcsProfitMarket.getAddition1().
                    add(profitValue));
            rcsProfitMarket.setAddition2(rcsProfitMarket.getAddition2().
                    add(betAmount));
        }else if("2".equals(oddsType)){
            rcsProfitMarket.setAddition1(rcsProfitMarket.getAddition1().
                    add(betAmount));
            rcsProfitMarket.setAddition2(rcsProfitMarket.getAddition2().
                    add(profitValue));
        }
    }

    /**
     * @Description    * 大小球 2:全场大小 18进球数大小-上半场
     * Over大，Under小
     * @Param [betAmount, oddsType, rcsProfitMarket, profitValue]
     * @Author  toney
     * @Date  16:48 2019/12/16
     * @return void
     **/
    private void HandleGoalLine(BigDecimal betAmount,String oddsType,RcsProfitMarket rcsProfitMarket,BigDecimal profitValue){
        if("Over".equalsIgnoreCase(oddsType)){
            rcsProfitMarket.setAddition1(rcsProfitMarket.getAddition1().
                    add(profitValue));
            rcsProfitMarket.setAddition2(rcsProfitMarket.getAddition2().
                    add(betAmount));
        }else if("Under".equalsIgnoreCase(oddsType)){
            rcsProfitMarket.setAddition1(rcsProfitMarket.getAddition1().
                    add(betAmount));
            rcsProfitMarket.setAddition2(rcsProfitMarket.getAddition2().
                    add(profitValue));
        }
    }

    private void handleData(TOrderDetail orderDetail) {
        try {
            QueryWrapper<StandardSportMarketOdds> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(StandardSportMarketOdds::getMarketId, orderDetail.getMarketId());
            List<StandardSportMarketOdds> list = oddsService.list(queryWrapper);

            Long marketBetAmount = marketBetAmountMap.get(orderDetail.getMarketId().toString() + "_" + orderDetail.getMatchType());
            if (marketBetAmount == null) {
                marketBetAmount = 0L;
            }
            marketBetAmount += orderDetail.getBetAmount();
            marketBetAmountMap.put(orderDetail.getMarketId().toString() + "_" + orderDetail.getMatchType(), marketBetAmount);

            for (StandardSportMarketOdds odds : list) {
                RcsMarketOddsConfig oddsConfig = oddsMap.get(odds.getId().toString() + "_" + orderDetail.getMatchType());
                if (oddsConfig == null) {
                    oddsConfig = new RcsMarketOddsConfig();
                    oddsConfig.setSportId(orderDetail.getSportId().longValue());
                    oddsConfig.setMatchId(orderDetail.getMatchId());
                    oddsConfig.setMarketCategoryId(orderDetail.getPlayId());
                    oddsConfig.setMatchMarketId(orderDetail.getMarketId());
                    oddsConfig.setMarketOddsId(odds.getId());
                    oddsConfig.setMatchType(orderDetail.getMatchType().toString());
                    oddsConfig.setBetAmount(BigDecimal.ZERO);
                    oddsConfig.setBetOrderNum(BigDecimal.valueOf(0));
                    oddsConfig.setPaidAmount(BigDecimal.ZERO);
                    oddsConfig.setProfitValue(BigDecimal.ZERO);
                }





                if (odds.getId().compareTo(orderDetail.getPlayOptionsId()) == 0) {
                    oddsConfig.setPaidAmount(oddsConfig.getPaidAmount().add(orderDetail.getPaidAmount()));
                    oddsConfig.setBetOrderNum(oddsConfig.getBetOrderNum().add(BigDecimal.valueOf(1)));
                    oddsConfig.setBetAmount(oddsConfig.getBetAmount().add(BigDecimal.valueOf(orderDetail.getBetAmount())));
                    oddsConfig.setProfitValue(BigDecimal.valueOf(marketBetAmount).subtract(oddsConfig.getPaidAmount()));
                }else {
                    oddsConfig.setProfitValue(oddsConfig.getProfitValue().add(BigDecimal.valueOf(orderDetail.getBetAmount())));
                }

                oddsMap.put(odds.getId().toString() + "_" + orderDetail.getMatchType(), oddsConfig);
            }
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
        }
    }


    /**
     * 获取StandardSportMarketOdds
     * @param id
     * @return
     */
    private StandardSportMarketOdds getStandardSportMarketOdds(Long id){
        QueryWrapper<StandardSportMarketOdds> oddsQuery = new QueryWrapper<>();
        oddsQuery.eq("id",id);
        return standardSportMarketOddsService.getOne(oddsQuery);
    }

    /**
     * 获取盘口下投注项下期望值
     * @param marketId 盘口
     * @return
     */
    private String getOddsIdProfitValueCacheKey(Long matchId,String matchType,Long marketId){

        return String.format( RedisKeys.ODDS_ID_PROFIT_VALUE,matchId,matchType,marketId);
    }


    private String getOddsIdTotalBetTimesCacheKey(Long matchId,String matchType,Long marketId){
        return String.format(RedisKeys.ODDS_ID_TOTAL_BET_TIMES,matchId,matchType,marketId);
    }

    /**
     * 获取盘口下投注项下最大赔付额
     * @param marketId
     * @return
     */
    private String getOddsIdTotalPaidAmountCacheKey(Long matchId,String matchType,Long marketId){
        return String.format(RedisKeys.ODDS_ID_TOTAL_PAID_AMOUNT,matchId,matchType,marketId);

    }

    /**
     * 获取盘口下投注项下总货量
     * @return
     */
    private String getOddsIdTotalBetAmountCacheKey(Long matchId,String matchType,Long marketId){
        return String.format(RedisKeys.ODDS_ID_TOTAL_BET_AMOUNT,matchId,matchType,marketId);
    }

    /**
     * 获取盘口下总货量
     * @return
     */
    private String getMarketIdTotalBetAmonutCacheKey(Long matchId,String matchType){
        return String.format(RedisKeys.MARKET_ID_TOTAL_BET_AMONUT,matchId,matchType);
    }



    /**
     * @Description   获取赛事维度实货量
     * @Param [matchId]
     * @Author  toney
     * @Date  10:54 2020/2/4
     * @return java.lang.String
     **/
    public String getMatchTotalBetAmountCacheKey(Long matchId){
        return String.format(RedisKeys.REAL_TIME_VOLUME_BY_MATCH_DIMENSION_REDIS_CACHE,matchId);
    }
    /**
     * @Description    赛事维度-订单总笔数
     * @Param [matchId]
     * @Author  toney
     * @Date  10:55 2020/2/4
     * @return java.lang.String
     **/
    public String getMatchTotalBetNumsCacheKey(Long matchId){
        return String.format(RedisKeys.SUM_MATCH_ORDER_NUMS_REDIS_CACHE,matchId);
    }


    /**
     * @Description   赛事维度期望值缓存
     * @Param [matchId]
     * @Author  toney
     * @Date  11:20 2020/2/4
     * @return java.lang.String
     **/
    public String getMatchSettledProfitCacheKey(Long matchId){
        return String.format(RedisKeys.SUM_MATCH_ORDER_NUMS_REDIS_CACHE,matchId);
    }
    /**
     * @Description   已结算注单缓存
     * @Param [matchId]
     * @Author  toney
     * @Date  11:22 2020/2/4
     * @return java.lang.String
     **/
    public String getMatchSettledTotalBetAmountCacheKey(Long matchId){
        return String.format(RedisKeys.SUM_MATCH_ORDER_NUMS_REDIS_CACHE,matchId);
    }
    /**
     * @Description   处理已接收bug
     * @Param [matchId]
     * @Author  toney
     * @Date  21:16 2020/2/6
     * @return void
     **/
    private void handleSettled(Long matchId){
        try {
            List<TSettle> settleList = settleService.selectByMatchId(matchId);
            Long settleTotalBetAmount = 0L;
            Long settleProfitAmount = 0L;
            for (TSettle settle : settleList) {
                settleProfitAmount = settleProfitAmount + (settle.getBetAmount() - settle.getSettleAmount());
                settleTotalBetAmount += settle.getBetAmount();
            }
            settleProfitAmount = settleProfitAmount / 100;
            settleTotalBetAmount = settleTotalBetAmount / 100;

            redisClient.hSet(getMatchSettledProfitCacheKey(matchId),
                    "matchSettleProfit", settleProfitAmount.toString());

            redisClient.hSet(getMatchSettledTotalBetAmountCacheKey(matchId), "matchSettleTotalBetAmount",
                    settleTotalBetAmount.toString());


            XxlJobLogger.log("更新赛事:"+matchId+"==>已结算期望:"+settleProfitAmount+";已结算货量:"+settleTotalBetAmount );
        }
        catch (Exception ex){
            log.error("处理已经算错误",ex.getMessage(),ex);
        }
    }
}
