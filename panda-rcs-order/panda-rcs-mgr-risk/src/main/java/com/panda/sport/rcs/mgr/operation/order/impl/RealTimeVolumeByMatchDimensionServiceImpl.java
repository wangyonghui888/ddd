package com.panda.sport.rcs.mgr.operation.order.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mapper.RcsLockMapper;
import com.panda.sport.rcs.mgr.aspect.RcsLockSeriesTypeEnum;
import com.panda.sport.rcs.mgr.aspect.RcsLockable;
import com.panda.sport.rcs.mgr.operation.order.CalcOrder;
import com.panda.sport.rcs.mgr.operation.settlement.impl.CalSettleServiceImpl;
import com.panda.sport.rcs.mgr.operation.utils.BigDecimalUtil;
import com.panda.sport.rcs.mgr.paid.annotion.Order;
import com.panda.sport.rcs.pojo.statistics.RcsMatchDimensionStatistics;
import com.panda.sport.rcs.mgr.wrapper.ITOrderDetailService;
import com.panda.sport.rcs.mgr.wrapper.statistics.RcsMatchDimensionStatisticsService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.vo.statistics.SumMatchAmountVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.rpc.operation.order
 * @Description :  赛事维度-实时货量
 * @Date: 2019-11-5 15:00
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@Order(1)
public class RealTimeVolumeByMatchDimensionServiceImpl extends CalcOrderBase implements CalcOrder {
    /**
     * redis操作类
     */
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ITOrderDetailService orderDetailService;


    @Autowired
    private RcsMatchDimensionStatisticsService rcsMatchDimensionStatisticsService;

    @Autowired
    private CalSettleServiceImpl calSettleService;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    /**
     * @Description   统计
     * @Param [orderBean]
     * @Author toney
     * @Date  2019-11-5 15:00
     * @return void
     **/
    @Override
    @Transactional
    @RcsLockable(key = "realTime_volume_by_match_dimension",seriesType = RcsLockSeriesTypeEnum.Single)
    public void orderHandle(OrderBean orderBean, Integer type) {
        //只取单条数据,不要串关的
        if (orderBean.getSeriesType() != 1) {
            log.warn("::{}::赛事维度-实时货量：串关数据不处理",orderBean.getOrderNo());
            return;
        }
        //拒单
        if(orderBean.getOrderStatus()!=1){
            log.warn("::{}::期望值接收-赛事维度-拒单mq，不在计算：",orderBean.getOrderNo());
            return;
        }

        for(OrderItem item : orderBean.getItems()) {
            //判断是不是足球
            if(item.getSportId()!=1){
                continue;
            }

            //拒单
            if(item.getValidateResult() !=1){
                continue;
            }

            useCache(item);
        }
    }

    /**
     * 使用数据库
     * @param orderNo,matchId
     */
    public void useDb(String orderNo,Long matchId) {
        SumMatchAmountVo vo = null;
        try {
            //获取统计数据
            vo = orderDetailService.getMatchSumBetAmount(orderNo, matchId);
        } catch (Exception ex) {
            log.error("::{}:: 使用数据库错误信息：{}",orderNo,ex.getMessage());
            throw ex;
        }
        RcsMatchDimensionStatistics bean = new RcsMatchDimensionStatistics();

        if (vo.getBetAmount() == null) {
            vo.setBetAmount(BigDecimal.ZERO);
        }

        if (vo.getSettleAmount() == null) {
            vo.setSettleAmount(BigDecimal.ZERO);
        }

        if (vo.getPaiAmount() == null) {
            vo.setPaiAmount(BigDecimal.ZERO);
        }

        if (vo.getBetOrderNums() == null) {
            vo.setBetOrderNums(0L);
        }

        if (vo.getSettleOrderBetAmount() == null) {
            vo.setSettleOrderBetAmount(BigDecimal.ZERO);
        }


        bean.setMatchId(matchId);
        bean.setTotalValue(vo.getBetAmount());
        bean.setSettledProfitValue(vo.getSettleOrderBetAmount().subtract(vo.getSettleAmount()));
        bean.setSettledRealTimeValue(vo.getSettleAmount());
        bean.setTotalOrderNums(vo.getBetOrderNums());
        bean.setCreateTime(System.currentTimeMillis());
        bean.setModifyTime(System.currentTimeMillis());
        log.info("::{}::赛事维度期望盈利值,实体bean{}",orderNo,JsonFormatUtils.toJson(bean));

        try {
            rcsMatchDimensionStatisticsService.insertOrSave(bean);
            redisClient.hSet(getMatchTotalBetAmountCacheKey(matchId), matchTotalBetAmount, String.valueOf(bean.getTotalValue().longValue()));
            redisClient.hSet(getMatchTotalBetNumsCacheKey(matchId), matchTotalBetNums,
                    String.valueOf(bean.getTotalOrderNums()));
            redisClient.hSet(calSettleService.getMatchSettledTotalBetAmountCacheKey(matchId), matchSettleTotalBetAmount, String.valueOf(bean.getSettledRealTimeValue().longValue()));
            redisClient.hSet(calSettleService.getMatchSettledProfitCacheKey(matchId), matchSettleProfit, String.valueOf(bean.getSettledProfitValue().longValue()));

        } catch (Exception ex) {
            log.error("::{}::赛事维度-实货量统计-发送ws异常：{}",orderNo,ex.getMessage(), ex);
            throw ex;

        }
    }


    /**
     * 获取总货量
     * @param matchId
     * @return
     */
    public BigDecimal getTotalValue(Long matchId) {
        try {
            String reuslt = redisClient.hGet(getMatchTotalBetAmountCacheKey(matchId), matchTotalBetAmount);
            if (reuslt == null) {
                return BigDecimal.ZERO;
            }
            return (new BigDecimal(reuslt));
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * 获取订单总数
     * @param matchId
     * @return
     */
    public Long getTotalOrderNums(Long matchId){
        try{
        String reuslt = redisClient.hGet(getMatchTotalBetNumsCacheKey(matchId),matchTotalBetNums);
        if(reuslt == null){
            return 0L;
        }
        return Long.parseLong(reuslt);}catch (Exception ex){
            return 0L;
        }
    }


    public static String matchTotalBetAmount="1";
    public static String matchTotalBetNums="2";
    public static String matchSettleTotalBetAmount="3";
    public static String matchSettleProfit="4";

    /**
     * 初始化数据
     * @param orderItem
     */
    public void init(OrderItem orderItem) {
        Object redisObj = redisClient.hGet(getMatchTotalBetNumsCacheKey(orderItem.getMatchId()), matchTotalBetNums);
        if (redisObj == null) {
            QueryWrapper<RcsMatchDimensionStatistics> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("match_id", orderItem.getMatchId());
            RcsMatchDimensionStatistics bean = rcsMatchDimensionStatisticsService.getOne(queryWrapper);
            if (bean != null) {
                redisClient.hSet(getMatchTotalBetAmountCacheKey(bean.getMatchId()), matchTotalBetAmount,
                       String.valueOf(bean.getTotalValue().longValue()));

                redisClient.hSet(getMatchTotalBetNumsCacheKey(bean.getMatchId()), matchTotalBetNums,
                        bean.getTotalOrderNums().toString());

                redisClient.hSet(calSettleService.getMatchSettledTotalBetAmountCacheKey(bean.getMatchId()),matchSettleTotalBetAmount,
                        String.valueOf(bean.getSettledRealTimeValue().longValue()));

                redisClient.hSet(calSettleService.getMatchSettledProfitCacheKey(bean.getMatchId()), matchSettleProfit,
                        String.valueOf(bean.getSettledProfitValue().longValue()));

            }else{
                useDb(orderItem.getOrderNo(),orderItem.getMatchId());
            }
        }
    }
    /**
     * 使用缓存
     * @param item
     */
    private void useCache(OrderItem item){
        //init(item);

        //下注金额是X100的 总货量
        Long match_Total_Bet_Amount = 0L;
        //统计总注数
        Long totalOrderNums = 0L;

        try {
            totalOrderNums = redisClient.hincrBy(getMatchTotalBetAmountCacheKey(item.getMatchId()),matchTotalBetNums, 1);
            match_Total_Bet_Amount = redisClient.hincrBy(getMatchTotalBetAmountCacheKey(item.getMatchId()), matchTotalBetAmount,
                    item.getBetAmount());
        }
        catch (Exception ex){
            redisClient.hSet(getMatchTotalBetNumsCacheKey(item.getMatchId()), matchTotalBetNums,"0");
            redisClient.hSet(getMatchTotalBetAmountCacheKey(item.getMatchId()),matchTotalBetAmount,"0" );

            //init(item);


            totalOrderNums = redisClient.hincrBy(getMatchTotalBetNumsCacheKey(item.getMatchId()), matchTotalBetNums, 1);
            match_Total_Bet_Amount = redisClient.hincrBy(getMatchTotalBetAmountCacheKey(item.getMatchId()), matchTotalBetAmount,
                    item.getBetAmount());
        }

        RcsMatchDimensionStatistics bean = new RcsMatchDimensionStatistics();
        bean.setMatchId(item.getMatchId());
        bean.setTotalOrderNums(totalOrderNums);
        bean.setTotalValue(BigDecimal.valueOf(match_Total_Bet_Amount));
        bean.setModifyTime(System.currentTimeMillis());
        bean.setCreateTime(System.currentTimeMillis());
        bean.setSettledProfitValue(calSettleService.getSettledProfitValue(item.getMatchId()));
        bean.setSettledRealTimeValue(calSettleService.getSettledTotalBetAmount(item.getMatchId()));

//        rcsMatchDimensionStatisticsService.insertOrSave(bean);
        producerSendMessageUtils.sendMessage("MYSQL_DIMENSION_STATISTICS", bean);
        log.info("::{}::赛事维度期望盈利值,RcsMatchDimensionStatistics实体类{}",item.getOrderNo(),JsonFormatUtils.toJson(bean));

        //向ws发送消息
        rcsMatchDimensionStatisticsService.sendSysnData(bean);
    }


    /**
     * @Description   获取赛事维度实货量
     * @Param [matchId]
     * @Author  toney
     * @Date  10:54 2020/2/4
     * @return java.lang.String
     **/
    public String getMatchTotalBetAmountCacheKey(Long matchId){
        return String.format(RedisKeys.REAL_TIME_VOLUME_BY_MATCH_DIMENSION_REDIS_CACHE,matchId)+":matchTotalBetAmount";
    }
    /**
     * @Description    赛事维度-订单总笔数
     * @Param [matchId]
     * @Author  toney
     * @Date  10:55 2020/2/4
     * @return java.lang.String
     **/
    public String getMatchTotalBetNumsCacheKey(Long matchId){
        return String.format(RedisKeys.SUM_MATCH_ORDER_NUMS_REDIS_CACHE,matchId) +":matchTotalBetNums";
    }


}
