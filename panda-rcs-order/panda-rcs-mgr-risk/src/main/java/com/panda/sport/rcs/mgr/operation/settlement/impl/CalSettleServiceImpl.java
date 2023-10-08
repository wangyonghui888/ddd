package com.panda.sport.rcs.mgr.operation.settlement.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mgr.aspect.RcsLockSeriesTypeEnum;
import com.panda.sport.rcs.mgr.aspect.RcsLockable;
import com.panda.sport.rcs.mgr.operation.order.impl.RealTimeVolumeByMatchDimensionServiceImpl;
import com.panda.sport.rcs.mgr.operation.settlement.CalcSettled;
import com.panda.sport.rcs.mgr.operation.vo.PredictSettleOrderMqVo;
import com.panda.sport.rcs.mgr.predict.PredictRedisKeyUtil;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.pojo.statistics.RcsMatchDimensionStatistics;
import com.panda.sport.rcs.mgr.wrapper.statistics.RcsMatchDimensionStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.panda.sport.rcs.mgr.paid.annotion.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.rpc.operation.settlement.impl
 * @Description :  实货量统计
 * @Date: 2019-11-04 11:34
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Component
@Order(200)
public class CalSettleServiceImpl extends CalcSettledBase implements CalcSettled {
    /**
     * redis操作
     */
    @Autowired
    private RedisClient redisClient;
    /**
     * service操作类
     */
    @Autowired
    private RcsMatchDimensionStatisticsService service;

    @Autowired
    private RcsMatchDimensionStatisticsService rcsMatchDimensionStatisticsService;

    @Autowired
    private RealTimeVolumeByMatchDimensionServiceImpl realTimeVolumeByMatchDimensionService;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    /**
     * @Description   处理相关流程
     * @Param [settleItem, orderDetail]
     * @Author  toney
     * @Date  11:35 2019/11/4
     * @return void
     **/
    @Override
    @RcsLockable(key = "cal_settle",seriesType = RcsLockSeriesTypeEnum.Single)
    public void settleHandle(SettleItem settleItem, List<TOrderDetail> orderDetailList){
//        if(orderDetailList.size()>1){
//            log.info("期望值，结算信息是串关不统计"+ JsonFormatUtils.toJson(settleItem));
//            return;
//        }
//
//        String msg =String.format("已结算处理,开始,settleItem实体类%s,orderDetail实体类%s",
//                JsonFormatUtils.toJson(settleItem),
//                JsonFormatUtils.toJson(orderDetailList));
//        log.info(msg);
//
//        //redisClient.batchDel(getMatchSettledProfitCacheKey(orderDetailList.get(0).getMatchId()));
      // useCache(settleItem,orderDetailList.get(0));
        log.info("::{}::已结算处理，结束",settleItem.getOrderNo());
    }

    /**
     * 初始化数据
     * @param orderNo,matchId
     */
    private Boolean init(String orderNo,Long matchId) {
        //从redis集群中查询
        Object redisObj = null;
        String cacheKey = getMatchSettledProfitCacheKey(matchId);
        try {
            redisObj = redisClient.hGet(getMatchSettledProfitCacheKey(matchId), RealTimeVolumeByMatchDimensionServiceImpl.matchSettleProfit);
        } catch (Exception ex) {
            redisObj = null;
        }
        if (redisObj == null) {
            QueryWrapper<RcsMatchDimensionStatistics> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("match_id", matchId);
            RcsMatchDimensionStatistics bean = rcsMatchDimensionStatisticsService.getOne(queryWrapper);
            //不存在记录
            if (bean != null) {
                //总货量
                redisClient.hSet(realTimeVolumeByMatchDimensionService.getMatchTotalBetAmountCacheKey(bean.getMatchId()), RealTimeVolumeByMatchDimensionServiceImpl.matchTotalBetAmount,
                        String.valueOf(bean.getTotalValue().longValue()));
                //投注次数
                redisClient.hSet(realTimeVolumeByMatchDimensionService.getMatchTotalBetNumsCacheKey(bean.getMatchId()), RealTimeVolumeByMatchDimensionServiceImpl.matchTotalBetNums,
                        bean.getTotalOrderNums().toString());


                //已结算货量
                redisClient.hSet(getMatchSettledTotalBetAmountCacheKey(matchId), RealTimeVolumeByMatchDimensionServiceImpl.matchSettleTotalBetAmount,
                        String.valueOf(bean.getSettledRealTimeValue().longValue()));
                //已结算盈亏
                redisClient.hSet(getMatchSettledProfitCacheKey(matchId), RealTimeVolumeByMatchDimensionServiceImpl.matchSettleProfit,
                        String.valueOf(bean.getSettledProfitValue().longValue()));
                return false;
            } else {
                realTimeVolumeByMatchDimensionService.useDb(orderNo, matchId);
                return true;
            }
        }
        return false;
    }


    /**
     * 获取比赛已结算期望值
     * @param matchId
     * @return
     */
    public BigDecimal getSettledProfitValue(Long matchId) {
        try {
            Object settledProfit = redisClient.hGet(getMatchSettledProfitCacheKey(matchId),
                    RealTimeVolumeByMatchDimensionServiceImpl.matchSettleProfit);

            if (settledProfit == null) {
                return BigDecimal.ZERO;
            }
            return (new BigDecimal(settledProfit.toString()));
        } catch (Exception ex) {
            log.error("::{}:: 获取比赛已结算期望值ERROR {}",matchId,ex.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * 获取已结算货量
     * @param matchId
     * @return
     */
    public BigDecimal getSettledTotalBetAmount(Long matchId){
        try {
            String realValume = redisClient.hGet(getMatchSettledTotalBetAmountCacheKey(matchId),
                    RealTimeVolumeByMatchDimensionServiceImpl.matchSettleTotalBetAmount);
            if (realValume == null) {
                return BigDecimal.ZERO;
            }
            return (new BigDecimal(realValume));
        }catch (Exception ex){
            log.error("::{}:: 获取已结算货量ERROR {}",matchId,ex.getMessage());
            return BigDecimal.ZERO;
        }
    }
    /**
     * 缓存
     * @param settleItem
     * @param orderDetail
     */

    @Transactional
    void useCache(SettleItem settleItem, TOrderDetail orderDetail){
        log.info("useCache 结算单号::{}:: 预测计算-订单结算:SettleItem对象:{} TOrderDetail对象:{}",settleItem.getOrderNo()
                ,JSONObject.toJSONString(settleItem),JSONObject.toJSONString(orderDetail));
        PredictSettleOrderMqVo mqVo = new PredictSettleOrderMqVo();
        mqVo.setOrderDetail(orderDetail);
        mqVo.setSettleItem(settleItem);
        producerSendMessageUtils.sendMessage(RcsConstant.RCS_PREDICT_SETTLE_ORDER, "", orderDetail.getOrderNo(), mqVo);
    }
    
    /**
     * @Description   赛事维度期望值缓存
     * @Param [matchId]
     * @Author  toney
     * @Date  11:20 2020/2/4
     * @return java.lang.String
     **/
    public String getMatchSettledProfitCacheKey(Long matchId){
        return String.format(RedisKeys.SUM_MATCH_ORDER_NUMS_REDIS_CACHE,matchId) +":matchSettleProfit";
    }
    /**
     * @Description   已结算注单缓存
     * @Param [matchId]
     * @Author  toney
     * @Date  11:22 2020/2/4
     * @return java.lang.String
     **/
    public String getMatchSettledTotalBetAmountCacheKey(Long matchId){
        return String.format(RedisKeys.SUM_MATCH_ORDER_NUMS_REDIS_CACHE,matchId) +":matchSettleTotalBetAmount";
    }
}
