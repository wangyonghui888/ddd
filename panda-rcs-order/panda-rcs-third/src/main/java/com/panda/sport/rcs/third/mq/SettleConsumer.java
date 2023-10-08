package com.panda.sport.rcs.third.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.third.entity.gts.GtsBetGeniusContentVo;
import com.panda.sport.rcs.third.entity.gts.GtsBetReceiverCache;
import com.panda.sport.rcs.third.entity.gts.GtsBetReceiverRequestVo;
import com.panda.sport.rcs.third.service.third.impl.GTSServiceImpl;
import com.panda.sport.rcs.utils.SeriesTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

import static com.panda.sport.rcs.third.common.Constants.GTS_SETTLE_INFO;


@Slf4j
@Component
@RocketMQMessageListener(topic = RcsConstant.OSMC_SETTLE_RESULT, consumerGroup = "OSMC_SETTLE_RESULT_THIRD_GROUP", messageModel = MessageModel.CLUSTERING, consumeMode = ConsumeMode.ORDERLY)
public class SettleConsumer implements RocketMQListener<SettleItem>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Autowired
    RedisClient redisClient;

    @Autowired
    GTSServiceImpl gtsService;


    @Override
    public void onMessage(SettleItem src) {
        if (src == null) {
            log.warn("::相订单信息为空不处理::");
            return;
        }
        String orderNo = src.getOrderNo();
        String receiveBetKey = String.format(GTS_SETTLE_INFO, orderNo);
        try {
            //不是gts的过滤
            String receiveBetStr = redisClient.get(receiveBetKey);
            if (StringUtils.isBlank(receiveBetStr)) {
                return;
            }
            log.info("::{}:: 接收到结算mq,实体bean={}", orderNo, JSONObject.toJSONString(src));
            if (CollectionUtils.isEmpty(src.getOrderDetailRisk())) {
                log.warn("::{}::结算的投注项信息为空,跳过", orderNo);
                return;
            }
            GtsBetReceiverCache cache = JSON.parseObject(receiveBetStr, new TypeReference<GtsBetReceiverCache>() {
            });
            log.info("::{}:: 订单结算mq,缓存数据={}", orderNo, JSONObject.toJSONString(cache));
            GtsBetReceiverRequestVo gtsBetReceiverRequestVo = new GtsBetReceiverRequestVo();
            BeanUtils.copyProperties(cache, gtsBetReceiverRequestVo);
            gtsBetReceiverRequestVo.setStatus("Settled");
            //gtsBetReceiverRequestVo.setBetPlacedTimestampUTC(getUtcTime());
            gtsBetReceiverRequestVo.setBetUpdatedTimestampUTC(getUtcTime());

            if (!CollectionUtils.isEmpty(gtsBetReceiverRequestVo.getLegs())) {
                gtsBetReceiverRequestVo.getLegs().forEach(e -> {
                    e.setStatus("Settled");
                });
            }
            //betResult
            //投注项结果  0-无结果 2-走水 3-输 4-赢 5-赢一半 6-输一半 7-赛事取消 8-赛事延期 11-比赛延迟 12-比赛中断 13-未知 15-比赛放弃
            //16-异常盘口 17未知赛事状态 18比赛取消 19比赛延期 20SR-其他 21SR-无进球球员 22SR-正确比分丢失 23SR-无法确认的赛果 24SR-格式变更
            //25SR-进球球员丢失 26SR-主动弃赛 27SR-并列获胜 28SR-中途弃赛 29SR-赔率错误 30SR-统计错误 31SR-投手变更
            Map<String, String> playOptionMap = cache.getPlayOptionMap();
            if (!CollectionUtils.isEmpty(playOptionMap)) {
                src.getOrderDetailRisk().forEach(f -> {
                    //找到对应的投注项
                    String thirdPlayOptionId = playOptionMap.get(f.getPlayOptionsId().toString());
                    gtsBetReceiverRequestVo.getLegs().forEach(h -> {
                        GtsBetGeniusContentVo betContent = h.getBetgeniusContent();
                        if (betContent.getSelectionId().equals(thirdPlayOptionId)) {
                            //结算的赔率
                            BigDecimal odds = new BigDecimal(f.getOddFinally());
                            switch (f.getBetResult()) {
                                case 4:
                                case 5:
                                case 6:
                                    h.setPayoutPrice(odds);
                                    break;
                                default:
                                    h.setPayoutPrice(new BigDecimal("0"));
                                    break;
                            }
                        }
                    });
                });
            }
            /**
             * 单关：
             * 	1.全赢情况下，结算的payout=赔付金额
             * 	2.半赢情况下  只要有赔付，即是是输半，状态也是给赢 ，payout为投注额的一半
             * 串关：
             * 	单式：
             * 		1.支付值大于本金，赢，payout传赔付值
             * 		2.支付值小于本金，输，payout=0
             * 	复试：
             * 		直接传赔付值，因为赔付值只要大于0，他就至少有一场是赢的，所以他是赢，如果
             * 		赔付值是0，就是输
             */
            //算出当时的zhe k
            BigDecimal total = new BigDecimal(src.getBetAmount()).divide(new BigDecimal("100"), 2, RoundingMode.DOWN);
            BigDecimal settleAmount = new BigDecimal(src.getSettleAmount()).divide(new BigDecimal("100"), 2, RoundingMode.DOWN);
            BigDecimal payOut = new BigDecimal("0");
            //盈利值
            BigDecimal profit = settleAmount.subtract(total);
            if (src.getSeriesType() != 1) {
                Integer count = SeriesTypeUtils.getCount(src.getSeriesType(), 1);
                if (count == 1) {
                    //单式
                    if (profit.compareTo(new BigDecimal("0")) > 0) {
                        payOut = settleAmount;
                    }
                } else {
                    //复式
                    payOut = settleAmount;
                }
            } else {
                //单关
                //盈利的情况下
                if (profit.compareTo(new BigDecimal("0")) > 0) {
                    payOut = settleAmount;
                } else {
                    //赔钱的情况下如果赔付大于0，payout就等于赔付/2,属于半赢情况
                    if (settleAmount.compareTo(new BigDecimal("0")) > 0) {
                        payOut = total.divide(new BigDecimal("2"), 2, RoundingMode.DOWN);
                    }
                }
            }
            gtsBetReceiverRequestVo.setPayout(payOut);
            gtsService.gtsSettleReceive(gtsBetReceiverRequestVo);
            log.info("::{}:: 订单结算通知完成", orderNo);
        } catch (Exception e) {
            log.error("::{}::订单结算处理异常", src.getOrderNo(), e);
        }
    }

    private static String getUtcTime() {
        LocalDateTime localDateTime = new Date(System.currentTimeMillis()).toInstant().atOffset(ZoneOffset.of("+0")).toLocalDateTime();
        return localDateTime.toString() + "Z";
    }


}
