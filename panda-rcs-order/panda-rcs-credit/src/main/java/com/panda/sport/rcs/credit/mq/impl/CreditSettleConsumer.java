package com.panda.sport.rcs.credit.mq.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.rcs.cache.redis.RedisUtils;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.credit.constants.ChampionRedisKey;
import com.panda.sport.rcs.credit.service.impl.LimitConfigService;
import com.panda.sport.rcs.credit.service.impl.MerchantLimitWarnService;
import com.panda.sport.rcs.enums.RedisCmdEnum;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.mapper.TOrderMapper;
import com.panda.sport.rcs.pojo.TOrder;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.pojo.limit.RedisUpdateVo;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.vo.SettleItemPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用模式结算
 * @Author : Paca
 * @Date : 2021-05-28 11:03
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = RcsConstant.RCS_CREDIT_SETTLE_RESULT,
        consumerGroup =RcsConstant.RCS_CREDIT_SETTLE_RESULT,
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class CreditSettleConsumer implements RocketMQListener<SettleItem>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Autowired
    private MerchantLimitWarnService merchantLimitWarnService;
    @Autowired
    private LimitConfigService limitConfigService;

    @Autowired
    private TOrderMapper orderMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedisClient redisClient;

    public CreditSettleConsumer() {
//        super("RCS_CREDIT_SETTLE_RESULT", "");
    }

    @Override
    public void onMessage(SettleItem settleItem) {
        try {
            if (settleItem == null) {
                log.warn("::{}::信用模式结算，订单不存在！" + settleItem.getOrderNo());
                return;
            }
            String orderKey = String.format("rcs:match:event:detail:ext:info:list:%s", settleItem.getOrderNo());
            if(!redisClient.exist(orderKey)){
                log.warn(" ::{}::无订单信息 ", settleItem.getOrderNo());
                return;
            }
            String orderDetail=redisClient.get(orderKey);
            OrderBean orderBean= JSONObject.parseObject(orderDetail,OrderBean.class);
            boolean isChampion = championSettle(settleItem, orderBean);
            if (isChampion) {
                return;
            }
            creditSettle(settleItem, orderBean);
        } catch (Exception e) {
            log.error("::{}::信用模式结算异常 ERROR{}",settleItem.getOrderNo(),e.getMessage());
        }
        return;
    }

    private void creditSettle(SettleItem settleItem, OrderBean order) {
        String orderNo = settleItem.getOrderNo();
        Long merchantId = settleItem.getMerchantId();
        String creditId = order.getTenantId().toString();
        Long settleTime = settleItem.getSettleTime();
        String dateExpect = DateUtils.getDateExpect(settleTime);
        long profitAmount = getProfitAmount(settleItem);
        Long currentPaidAmount = limitConfigService.businessLimitIncrBy(settleTime, creditId, profitAmount);
        Long businessLimit = getBusinessLimit(creditId);
        // 商户限额预警消息
        merchantLimitWarnService.sendMsg(merchantId, creditId, currentPaidAmount, businessLimit, dateExpect, orderNo);
        boolean bool = currentPaidAmount >= businessLimit;
        log.info("信用额度-商户单日最大赔付判断{},{},{}", currentPaidAmount, businessLimit, bool);
        String stopKey = String.format(RedisKeys.PAID_DATE_BUS_STOP_REDIS_CACHE, dateExpect, creditId);
        if (bool) {
            redisUtils.set(stopKey, "1");
        } else {
            redisUtils.set(stopKey, "0");
        }
        redisUtils.expire(stopKey, 30L, TimeUnit.DAYS);
    }

    private boolean championSettle(SettleItem settleItem, OrderBean order) {
        String orderNo = settleItem.getOrderNo();
        Long userId = settleItem.getUid();
        Long tenantId = order.getTenantId();
        String creditId = order.getTenantId().toString();
        Integer limitType = order.getLimitType();
        if (1 == limitType) {
            creditId = "0";
        }
        Long settleTime = settleItem.getSettleTime();
        String dateExpect = DateUtils.getDateExpect(settleTime);
        long profitAmount = getProfitAmount(settleItem);
        if(CollectionUtils.isEmpty(order.getItems())){
            log.warn("::{}::订单明细项为空值！",settleItem.getOrderNo());
            return false;
        }
        OrderItem orderDetail = order.getItems().get(0);
        Integer matchType = orderDetail.getMatchType();
        if(null == matchType || matchType !=3)
        {
            log.warn("::{}:: 不是信用结算",settleItem.getOrderNo());
            return false;
        }
        // 先回滚额度，再计算输赢
        championLimitCallback(orderNo);
        Long matchId = orderDetail.getMatchId();
        Long marketId = orderDetail.getMarketId();
        Long optionId = orderDetail.getPlayOptionsId();
        String merchantPaymentKey = ChampionRedisKey.Used.getMerchantPaymentKey(tenantId, creditId, matchId, marketId);
        String userPaymentKey = ChampionRedisKey.Used.getUserPaymentKey(userId, matchId, marketId);
        redisUtils.hincrBy(merchantPaymentKey, String.valueOf(optionId), profitAmount);
        redisUtils.hincrBy(userPaymentKey, String.valueOf(optionId), profitAmount);
        redisUtils.expire(merchantPaymentKey, 180L, TimeUnit.DAYS);
        redisUtils.expire(userPaymentKey, 180L, TimeUnit.DAYS);
        if (NumberUtils.INTEGER_TWO.equals(limitType)) {
            // 信用模式
            creditSettle(settleItem, order);
        } else {
            // 标准模式
            Long currentPaidAmount = limitConfigService.businessLimitIncrBy(settleTime, String.valueOf(tenantId), profitAmount);
            Long businessLimit = getBusinessLimit(String.valueOf(tenantId));
            // 商户限额预警消息
            merchantLimitWarnService.sendMsg(tenantId, null, currentPaidAmount, businessLimit, dateExpect, orderNo);
            boolean bool = currentPaidAmount >= businessLimit;
            log.info("::{}::信用额度-商户单日最大赔付判断{},{},{}",orderNo,currentPaidAmount, businessLimit, bool);
            String stopKey = String.format(RedisKeys.PAID_DATE_BUS_STOP_REDIS_CACHE, dateExpect, tenantId);
            if (bool) {
                redisUtils.set(stopKey, "1");
            } else {
                redisUtils.set(stopKey, "0");
            }
            redisUtils.expire(stopKey, 30L, TimeUnit.DAYS);
        }
        log.info("::{}:: 信用结算完成！");
        return true;
    }

    private long getProfitAmount(SettleItem settleItem) {
        //走水或拒单
        if (settleItem.getOutCome() == 2 || settleItem.getOutCome() == 9) {
            return 0L;
        }
        //输 赢本金
        if (settleItem.getOutCome() == 3) {
            return settleItem.getBetAmount() * -1;
        }
        if (settleItem.getSettleAmount() == null) {
            settleItem.setSettleAmount(0L);
        }
        //4-赢,赢一半
        if (settleItem.getOutCome() == 4 || settleItem.getOutCome() == 5) {
            return settleItem.getSettleAmount() - settleItem.getBetAmount();
        }
        // 6-输一半
        if (settleItem.getOutCome() == 6) {
            return settleItem.getSettleAmount() * -1;
        }
        return settleItem.getSettleAmount() - settleItem.getBetAmount();
    }

    private Long getBusinessLimit(String creditId) {
        RcsQuotaBusinessLimitResVo businessLimit = limitConfigService.getBusinessLimit(creditId);
        if (businessLimit.getBusinessSingleDayLimit() == null) {
            // 默认1千万，单位分
            return 1000_0000_00L;
        }
        return businessLimit.getBusinessSingleDayLimit();
    }

    private void championLimitCallback(String orderNo) {
        String key = String.format(ChampionRedisKey.LIMIT_REDIS_UPDATE_RECORD_KEY, orderNo);
        String value = redisUtils.get(key);
        log.info("冠军玩法额度回滚：key={},value={}", key, value);
        if (StringUtils.isNotBlank(value)) {
            List<RedisUpdateVo> redisUpdateList = JSON.parseArray(value, RedisUpdateVo.class);
            if (CollectionUtils.isNotEmpty(redisUpdateList)) {
                redisUpdateList.forEach(vo -> {
                    String cacheKey = vo.getKey();
                    // 提前结算只回滚期望赔付
                    if (cacheKey.contains("rcs:champion:used:merchantPayment") || cacheKey.contains("rcs:champion:used:usedPayment")) {
                        BigDecimal v = CommonUtils.toBigDecimal(vo.getValue()).negate();
                        if (RedisCmdEnum.isIncrBy(vo.getCmd())) {
                            redisUtils.incrBy(cacheKey, v.longValue());
                        } else if (RedisCmdEnum.isIncrByFloat(vo.getCmd())) {
                            redisUtils.incrByFloat(cacheKey, v.doubleValue());
                        } else if (RedisCmdEnum.isHincrBy(vo.getCmd())) {
                            redisUtils.hincrBy(cacheKey, vo.getField(), v.longValue());
                        } else if (RedisCmdEnum.isHincrByFloat(vo.getCmd())) {
                            redisUtils.hincrByFloat(cacheKey, vo.getField(), v.doubleValue());
                        }
                        redisUtils.expire(cacheKey, 180L, TimeUnit.DAYS);
                    }
                });
            }
            // 回滚后删除备份
            redisUtils.del(key);
//            redisUtils.set(key + ":bak", value);
//            redisUtils.expire(key + ":bak", 30L, TimeUnit.DAYS);
        }
    }
}
