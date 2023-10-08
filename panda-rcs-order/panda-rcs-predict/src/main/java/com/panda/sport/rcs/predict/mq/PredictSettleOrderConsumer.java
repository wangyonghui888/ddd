//
//package com.panda.sport.rcs.predict.mq;
//
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.panda.sport.data.rcs.dto.SettleItem;
//import com.panda.sport.rcs.core.cache.client.RedisClient;
//import com.panda.sport.rcs.core.utils.JsonFormatUtils;
//import com.panda.sport.rcs.mapper.TOrderDetailMapper;
//import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
//import com.panda.sport.rcs.pojo.TOrderDetail;
//import com.panda.sport.rcs.pojo.statistics.RcsMatchDimensionStatistics;
//import com.panda.sport.rcs.predict.service.impl.PredictCommonServiceImpl;
//import com.panda.sport.rcs.predict.utils.PredictRedisKeyUtil;
//import com.panda.sport.rcs.predict.vo.PredictSettleOrderMqVo;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
//import org.apache.rocketmq.spring.annotation.ConsumeMode;
//import org.apache.rocketmq.spring.annotation.MessageModel;
//import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
//import org.apache.rocketmq.spring.core.RocketMQListener;
//import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
//import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.UUID;
//
///*
// * forecast计算 消费
// * @author  lithan
// * @since  2021-2-21 10:31:19
// * */
//
//@Component
//@Slf4j
//@TraceCrossThread
//@RocketMQMessageListener(
//        topic = "rcs_predict_settle_order",
//        consumerGroup = "rcs_predict_settle_order",
//        messageModel = MessageModel.CLUSTERING,
//        consumeMode = ConsumeMode.ORDERLY)
//public class PredictSettleOrderConsumer implements RocketMQListener<PredictSettleOrderMqVo>, RocketMQPushConsumerLifecycleListener {
//
//    @Override
//    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
//        defaultMQPushConsumer.setConsumeThreadMin(64);
//        defaultMQPushConsumer.setConsumeThreadMax(256);
//    }
//
//    @Autowired
//    private RedisClient redisClient;
//
//    @Autowired
//    private ProducerSendMessageUtils producerSendMessageUtils;
//
//    @Autowired
//    PredictCommonServiceImpl predictCommonService;
//
//    @Autowired
//    TOrderDetailMapper detailMapper;
//
//    public PredictSettleOrderConsumer() {
//        // super("rcs_predict_settle_order", "");
//    }
//
//
//    @Override
//    public void onMessage(PredictSettleOrderMqVo mqVo) {
//        try {
//            MDC.put("X-B3-TraceId", UUID.randomUUID().toString().replace("-", ""));
//            log.info("::{}::赛事id:{},数据预测已结算收到:{}",mqVo.getOrderDetail().getOrderNo(),mqVo.getOrderDetail().getMatchId(),JSONObject.toJSONString(mqVo));
//            SettleItem settleItem = mqVo.getSettleItem();
//            TOrderDetail orderDetail = mqVo.getOrderDetail();
//
////            LambdaQueryWrapper<TOrder> tOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
////            tOrderLambdaQueryWrapper.eq(TOrder::getOrderNo, orderDetail.getOrderNo());
////            TOrder order = orderMapper.selectOne(tOrderLambdaQueryWrapper);
////            BigDecimal percentage = predictCommonService.getUserTagPercentage(orderDetail.getUid(), order.getTenantId());
////              固定从订单表 取货量百分比
////            LambdaQueryWrapper<TOrderDetail> queryWrapper = new LambdaQueryWrapper<>();
////            queryWrapper.eq(TOrderDetail::getOrderNo, orderDetail.getOrderNo());
////            TOrderDetail volDetail = detailMapper.selectOne(queryWrapper);
//            BigDecimal percentage = orderDetail.getVolumePercentage();
//            //统计比赛事已结算总货量
//            Long sumSettleBetAmount = 0L;
//            try {
//                //统计比赛事已结算总货量key
//                String getMatchSettledTotalBetAmonutKey = PredictRedisKeyUtil.getMatchSettledTotalBetAmonutKey();
//                //sumSettleBetAmount = redisClient.hincrBy(getMatchSettledTotalBetAmonutKey, orderDetail.getMatchId().toString(), BigDecimal.valueOf(orderDetail.getBetAmount()).multiply(percentage).longValue());
//
//                /*
//                 * redis大key优化
//                 */
//                //货量
//                long volumeBetAmount = BigDecimal.valueOf(orderDetail.getBetAmount()).multiply(percentage).longValue();
//                log.info("::{}::赛事id:{},betAmount:{},percentage:{},赛事货量:{}",orderDetail.getOrderNo(),orderDetail.getMatchId(),orderDetail.getBetAmount(),percentage,volumeBetAmount);
//
//                //赛事投注货量key
//                String volumeBetAmountKey = PredictRedisKeyUtil.MATCH_TOTAL_SETTLED_VOLUME_KEY + orderDetail.getMatchId();
//                String totalVolumeBetAmount = redisClient.get(volumeBetAmountKey);
//                log.info("::{}::赛事id:{},赛事累计投注货量before:{}",orderDetail.getOrderNo(),totalVolumeBetAmount);
//
//                //兼容老数据
//                if (StringUtils.isBlank(totalVolumeBetAmount)){
//                    //从大key里面拿到以前的数据
//                   totalVolumeBetAmount = redisClient.hGet(getMatchSettledTotalBetAmonutKey, orderDetail.getMatchId().toString());
//                   log.info("::{}::赛事id:{},旧缓存取值，赛事累计投注货量:{}",orderDetail.getOrderNo(),orderDetail.getMatchId(),totalVolumeBetAmount);
//                   if (StringUtils.isNotBlank(totalVolumeBetAmount)){
//                       redisClient.set(volumeBetAmountKey,totalVolumeBetAmount);
//                   }
//                }
//                sumSettleBetAmount = redisClient.incrBy(volumeBetAmountKey,volumeBetAmount);
//                log.info("::{}::赛事id:{},赛事累计投注货量after:{}",orderDetail.getOrderNo(),orderDetail.getMatchId(),sumSettleBetAmount);
//            }catch (Exception ex){
//                log.error("::{}::获取结算缓存报错",orderDetail.getOrderNo(),ex);
//            }
//            //已结算盈亏key
//            String matchsettledProfitKey = PredictRedisKeyUtil.getMatchsettledProfitKey();
//            //Long settledProfit = redisClient.hincrBy(matchsettledProfitKey, orderDetail.getMatchId().toString(), BigDecimal.valueOf(settleItem.getBetAmount() - settleItem.getSettleAmount()).multiply(percentage).longValue());
//
//            //累计已结算盈亏
//            Long sumMatchSettledProfitValue = 0L;
//            //赛事已结算盈亏
//            Long matchSettledProfitValue = BigDecimal.valueOf(settleItem.getBetAmount() - settleItem.getSettleAmount()).multiply(percentage).longValue();
//            log.info("::{}::赛事id:{},betAmount:{},percentage:{},赛事已结算盈亏:{}",
//                    orderDetail.getOrderNo(),orderDetail.getMatchId(),settleItem.getBetAmount(),percentage,matchSettledProfitValue);
//
//            String newMatchsettledProfitKey =  PredictRedisKeyUtil.MATCH_SETTLED_PROFIT_KEY + orderDetail.getMatchId();
//            String totalMatchSettledProfitValue = redisClient.get(newMatchsettledProfitKey);
//            log.info("::{}::赛事id:{},赛事累计已结算盈亏before:{}",orderDetail.getOrderNo(),orderDetail.getMatchId(),totalMatchSettledProfitValue);
//
//            if (StringUtils.isBlank(totalMatchSettledProfitValue)){
//                //从旧的大key缓存里面拿数据
//                totalMatchSettledProfitValue = redisClient.hGet(matchsettledProfitKey, orderDetail.getMatchId().toString());
//                log.info("::{}::赛事id:{},赛事累计已结算盈亏,旧缓存取值:{}",orderDetail.getOrderNo(),orderDetail.getMatchId(),totalMatchSettledProfitValue);
//                if (StringUtils.isNotBlank(totalMatchSettledProfitValue)){
//                    redisClient.set(newMatchsettledProfitKey,totalMatchSettledProfitValue);
//                }
//            }
//            sumMatchSettledProfitValue = redisClient.incrBy(newMatchsettledProfitKey,matchSettledProfitValue);
//            log.info("::{}::赛事id:{},赛事累计已结算盈亏after:{}",orderDetail.getOrderNo(),orderDetail.getMatchId(),sumMatchSettledProfitValue);
//
//            RcsMatchDimensionStatistics rcsMatchDimensionStatistics = new RcsMatchDimensionStatistics();
//            rcsMatchDimensionStatistics.setMatchId(orderDetail.getMatchId());
//            rcsMatchDimensionStatistics.setSettledRealTimeValue(BigDecimal.valueOf(sumSettleBetAmount));
//            rcsMatchDimensionStatistics.setSettledProfitValue(BigDecimal.valueOf(sumMatchSettledProfitValue));
//
//            //获取赛事总货量 PredictServiceImpl类里计算
//            String matchTotalBetAmonutKey = PredictRedisKeyUtil.getMatchTotalBetAmonutKey();
//            //Long matchTotalBetAmonut = Long.valueOf(redisClient.hincrBy(matchTotalBetAmonutKey, orderDetail.getMatchId().toString(),0L));
//
//            long matchTotalBetAmonut = 0;
//            //赛事总投注新key
//            String newMatchTotalBetAmonutKey =  PredictRedisKeyUtil.MATCH_TOTAL_BET_AMONUT_KEY + orderDetail.getMatchId();
//            String newMatchTotalBetAmonut = redisClient.get(newMatchTotalBetAmonutKey);
//            log.info("::{}::赛事id:{},赛事预测已结算，赛事总投注:{}",orderDetail.getOrderNo(),orderDetail.getMatchId(),newMatchTotalBetAmonut);
//            //兼容旧数据
//            if (StringUtils.isBlank(newMatchTotalBetAmonut)){
//                newMatchTotalBetAmonut = redisClient.hGet(matchTotalBetAmonutKey,orderDetail.getMatchId().toString());
//                log.info("::{}::赛事id:{},赛事预测已结算，赛事总投注old:{}",orderDetail.getOrderNo(),orderDetail.getMatchId(),newMatchTotalBetAmonut);
//            }
//            if (StringUtils.isNotBlank(newMatchTotalBetAmonut)){
//                matchTotalBetAmonut = Long.valueOf(newMatchTotalBetAmonut);
//            }
//            rcsMatchDimensionStatistics.setTotalValue(new BigDecimal(matchTotalBetAmonut));
//
//            //赛事总投注笔数 PredictServiceImpl类里计算
//            String matchTotalBetNumKey = PredictRedisKeyUtil.getMatchTotalBetNumKey();
//            //Long matchTotalBetTimes = Long.valueOf(redisClient.hincrBy(matchTotalBetNumKey, orderDetail.getMatchId().toString(),0L));
//            String newMatchTotalBetNumKey = PredictRedisKeyUtil.MATCH_TOTAL_BET_NUM_KEY + orderDetail.getMatchId();
//            long matchTotalBetTimes = 0;
//            String totalBetNum = redisClient.get(newMatchTotalBetNumKey);
//            log.info("::{}::赛事id:{},赛事预测已结算，赛事总数量:{}",orderDetail.getOrderNo(),orderDetail.getMatchId(),totalBetNum);
//            //兼容老数据
//            if (StringUtils.isBlank(totalBetNum)){
//                totalBetNum = redisClient.hGet(matchTotalBetNumKey,orderDetail.getMatchId().toString());
//                log.info("::{}::赛事id:{},赛事预测已结算，赛事总数量old:{}",orderDetail.getOrderNo(),orderDetail.getMatchId(),totalBetNum);
//            }
//            if (StringUtils.isNotBlank(totalBetNum)){
//                matchTotalBetTimes = Long.valueOf(totalBetNum);
//            }
//            rcsMatchDimensionStatistics.setTotalOrderNums(matchTotalBetTimes);
//
//            //异步存库
//            HashMap<String, String> mqMap = new HashMap<>();
//            mqMap.put("time", "" + System.currentTimeMillis());
//            String hashKey = rcsMatchDimensionStatistics.getMatchId().toString();
//            producerSendMessageUtils.sendMsg("mq_data_rcs_match_dimension_statistics", "", "", JSONObject.toJSONString(rcsMatchDimensionStatistics), mqMap, hashKey);
////            rcsMatchDimensionStatisticsService.sendSysnData(rcsMatchDimensionStatistics);
//            log.info("数据预测已结算,入库:{}", JsonFormatUtils.toJson(rcsMatchDimensionStatistics));
//        } catch (Exception e) {
//            log.error("::{}::数据预测已结算MQ异常", mqVo.getOrderDetail().getOrderNo(),e);
//            redisClient.delete("Rcs:realVolume:queue:" + mqVo.getOrderDetail().getOrderNo());
//        }
//        return ;
//    }
//}