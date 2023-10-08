package com.panda.sport.rcs.credit.service.champion.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.rcs.cache.redis.RedisUtils;
import com.panda.sport.rcs.credit.constants.ChampionRedisKey;
import com.panda.sport.rcs.credit.constants.ErrorCode;
import com.panda.sport.rcs.credit.service.CreditLimitService;
import com.panda.sport.rcs.credit.service.champion.AbstractChampionLimitService;
import com.panda.sport.rcs.enums.RedisCmdEnum;
import com.panda.sport.rcs.pojo.limit.RedisUpdateVo;
import com.panda.sport.rcs.vo.RcsBusinessPlayPaidConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 冠军玩法限额服务
 * @Author : Paca
 * @Date : 2021-06-09 14:57
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service("championLimitService")
public class ChampionLimitServiceImpl extends AbstractChampionLimitService {

    @Autowired
    public ChampionLimitServiceImpl(RedisUtils redisUtils) {
        super(redisUtils);
    }

    @Override
    public List<RcsBusinessPlayPaidConfigVo> queryBetLimit(OrderBean orderBean, OrderItem orderItem) {
        Long tenantId = orderBean.getTenantId();
        String creditId = orderBean.getAgentId();
        Long userId = orderBean.getUid();
        Integer limitType = orderBean.getLimitType();
        if (limitType == 1) {
            creditId = "0";
        }
        Long matchId = orderItem.getMatchId();
        Long marketId = orderItem.getMarketId();
        Long optionId = orderItem.getPlayOptionsId();
        log.info("信用额度::{}::，冠军玩法获取限额，商户：{}，代理：{}，赛事：{}，盘口：{}，投注项：{}", userId, tenantId, creditId, matchId, marketId, optionId);

        RcsQuotaBusinessLimitResVo limitRatio = getLimitRatio(orderBean);
        BigDecimal merchantLimitRatio = limitRatio.getChampionBusinessProportion();
        BigDecimal userLimitRatio = limitRatio.getChampionUserProportion();
        log.info("信用额度::{}::，冠军玩法获取限额，玩法限额比例：{}，商户限额比例：{}，用户限额比例：{}", userId, limitRatio, merchantLimitRatio, userLimitRatio);

        // 用户单注投注赔付限额
        long userSingleBetLimit = getUserSingleBetLimit(matchId, marketId);
        userSingleBetLimit = new BigDecimal(userSingleBetLimit).multiply(userLimitRatio).longValue();
        log.info("信用额度::{}::，冠军玩法获取限额，用户单注投注赔付限额：{}", userId, userSingleBetLimit);
        int infoCode = ErrorCode.LIMIT_21102;
        long remain = userSingleBetLimit;

        // 用户玩法累计赔付限额
        long userPlayLimit = getUserPlayLimit(matchId, marketId);
        userPlayLimit = new BigDecimal(userPlayLimit).multiply(userLimitRatio).longValue();
        log.info("信用额度::{}::，冠军玩法获取限额，用户玩法赔付限额：{}", userId, userPlayLimit);
        Map<Long, Long> userBetUsedMap = getUserBetUsed(userId, matchId, marketId);
        Map<Long, Long> userPaymentUsedMap = getUserPaymentUsed(userId, matchId, marketId);
        long userPlayRemain = getRemain(userPlayLimit, userBetUsedMap, userPaymentUsedMap, optionId);
        log.info("信用额度::{}::，冠军玩法获取限额，用户玩法累计赔付剩余限额：{}", userId, userPlayRemain);
        if (userPlayRemain <= 0L) {
            infoCode = ErrorCode.LIMIT_21101;
            remain = userPlayRemain;
        } else {
            if (userPlayRemain < remain) {
                infoCode = ErrorCode.LIMIT_21101;
                remain = userPlayRemain;
            }

            // 用户单项赔付限额
            long userOptionLimit = getUserOptionLimit(matchId, marketId, optionId);
            if (userOptionLimit == 0L) {
                // 未配置则不校验，设置一个很大的值 避免数值过大超出计算范围  除以100000
                userOptionLimit = Long.MAX_VALUE / 100000;
            } else {
                userOptionLimit = new BigDecimal(userOptionLimit).multiply(userLimitRatio).longValue();
            }
            long userOptionUsed = userPaymentUsedMap.getOrDefault(optionId, 0L);
            long userOptionRemain = userOptionLimit - userOptionUsed;
            log.info("信用额度::{}::，冠军玩法获取限额，用户单项赔付限额：{}，已用：{}，剩余：{}", userId, userOptionLimit, userOptionUsed, userOptionRemain);
            if (userOptionRemain <= 0L) {
                infoCode = ErrorCode.LIMIT_21103;
                remain = userOptionRemain;
            } else {
                if (userOptionRemain < remain) {
                    infoCode = ErrorCode.LIMIT_21103;
                    remain = userOptionRemain;
                }

                // 商户玩法赔付限额
                long merchantPlayLimit = getMerchantPlayLimit(matchId, marketId);
                merchantPlayLimit = new BigDecimal(merchantPlayLimit).multiply(merchantLimitRatio).longValue();
                log.info("信用额度::{}::，冠军玩法获取限额，商户玩法赔付限额：{}", userId, merchantPlayLimit);
                Map<Long, Long> merchantBetUsedMap = getMerchantBetUsed(tenantId, creditId, matchId, marketId);
                Map<Long, Long> merchantPaymentUsedMap = getMerchantPaymentUsed(tenantId, creditId, matchId, marketId);
                long merchantPlayRemain = getRemain(merchantPlayLimit, merchantBetUsedMap, merchantPaymentUsedMap, optionId);
                log.info("信用额度::{}::，冠军玩法获取限额，商户玩法赔付剩余限额：{}", userId, merchantPlayRemain);
                if (merchantPlayRemain <= 0L || merchantPlayRemain < remain) {
                    infoCode = ErrorCode.LIMIT_21001;
                    remain = merchantPlayRemain;
                }
            }
        }

        long maxBetAmount;
        if (remain <= 0L) {
            maxBetAmount = 0L;
        } else {
            BigDecimal euOdds = CreditLimitService.getEuOdds(orderItem);
            // 大于高赔取赔付，否则取投注
            maxBetAmount = new BigDecimal(remain).divide(euOdds.subtract(BigDecimal.ONE), 2, RoundingMode.DOWN).longValue();
        }
        log.info("信用额度::{}::，冠军玩法获取限额，获取到最终最大限额：{}", userId, maxBetAmount);
        RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
        vo.setType(String.valueOf(orderBean.getSeriesType()));
        vo.setMinBet(1L);
        vo.setOrderMaxPay(maxBetAmount / 100);
        vo.setInfoErrorCode(infoCode);
        return Lists.newArrayList(vo);
    }

    private long getRemain(long limit, Map<Long, Long> betUsedMap, Map<Long, Long> paymentUsedMap, Long optionId) {
        // 其它投注项投注额之和
        long otherOptionBetSum = 0L;
        if (CollectionUtils.isNotEmpty(betUsedMap)) {
            for (Map.Entry<Long, Long> entry : betUsedMap.entrySet()) {
                if (!optionId.equals(entry.getKey())) {
                    otherOptionBetSum += entry.getValue();
                }
            }
        }
        // 当前投注项赔付之和
        long currentOptionPaymentSum = paymentUsedMap.getOrDefault(optionId, 0L);
        log.info("冠军玩法额度：limit={},otherOptionBetSum={},currentOptionPaymentSum={}", limit, otherOptionBetSum, currentOptionPaymentSum);
        return limit + otherOptionBetSum - currentOptionPaymentSum;
    }

    @Override
    public Map<String, Object> checkOrder(OrderBean orderBean, List<RedisUpdateVo> redisUpdateList) {
        log.info("冠军玩法下单::{}::，校验额度开始：{}", orderBean.getOrderNo(), JSON.toJSONString(redisUpdateList));
        Long tenantId = orderBean.getTenantId();
        String creditId = orderBean.getAgentId();
        Long userId = orderBean.getUid();
        Integer limitType = orderBean.getLimitType();
        if (limitType == 1) {
            creditId = "0";
        }
        OrderItem orderItem = orderBean.getItems().get(0);
        Long matchId = orderItem.getMatchId();
        Long marketId = orderItem.getMarketId();
        Long optionId = orderItem.getPlayOptionsId();

        BigDecimal euOdds = CreditLimitService.getEuOdds(orderItem);
        Long betAmount = orderItem.getBetAmount();
        long payment = new BigDecimal(betAmount).multiply(euOdds.subtract(BigDecimal.ONE)).longValue();
        log.info("冠军玩法下单::{}::，用户该笔订单最大赔付：{}", orderBean.getOrderNo(), payment);

        String merchantBetKey = ChampionRedisKey.Used.getMerchantBetKey(tenantId, creditId, matchId, marketId);
        String merchantPaymentKey = ChampionRedisKey.Used.getMerchantPaymentKey(tenantId, creditId, matchId, marketId);
        String userBetKey = ChampionRedisKey.Used.getUserBetKey(userId, matchId, marketId);
        String userPaymentKey = ChampionRedisKey.Used.getUserPaymentKey(userId, matchId, marketId);
        List<String> keys = Lists.newArrayList(merchantBetKey, merchantPaymentKey, userBetKey, userPaymentKey);

        RcsQuotaBusinessLimitResVo limitRatio = getLimitRatio(orderBean);
        BigDecimal merchantLimitRatio = limitRatio.getChampionBusinessProportion();
        BigDecimal userLimitRatio = limitRatio.getChampionUserProportion();
        log.info("冠军玩法下单::{}::，玩法限额比例：{}，商户限额比例：{}，用户限额比例：{}", orderBean.getOrderNo(), limitRatio, merchantLimitRatio, userLimitRatio);
        // 用户单注投注赔付限额
        long userSingleBetLimit = getUserSingleBetLimit(matchId, marketId);
        userSingleBetLimit = new BigDecimal(userSingleBetLimit).multiply(userLimitRatio).longValue();
        log.info("冠军玩法下单::{}::，最终用户单注赔付限额：{}", orderBean.getOrderNo(), userSingleBetLimit);
        // 用户玩法累计赔付限额
        long userPlayLimit = getUserPlayLimit(matchId, marketId);
        userPlayLimit = new BigDecimal(userPlayLimit).multiply(userLimitRatio).longValue();
        log.info("冠军玩法下单::{}::，最终用户玩法赔付限额：{}", orderBean.getOrderNo(), userPlayLimit);
        // 用户单项赔付限额
        long userOptionLimit = getUserOptionLimit(matchId, marketId, optionId);
        if (userOptionLimit == 0L) {
            // 未配置则不校验，设置一个很大的值  避免数值过大超出计算范围  除以100000
            userOptionLimit = Long.MAX_VALUE/100000;
        } else {
            userOptionLimit = new BigDecimal(userOptionLimit).multiply(userLimitRatio).longValue();
        }
        log.info("冠军玩法下单::{}::，最终单项赔付限额：{}", orderBean.getOrderNo(), userOptionLimit);
        // 商户玩法赔付限额
        long merchantPlayLimit = getMerchantPlayLimit(matchId, marketId);
        merchantPlayLimit = new BigDecimal(merchantPlayLimit).multiply(merchantLimitRatio).longValue();
        log.info("冠军玩法下单::{}::，最终商户玩法赔付限额：{}", orderBean.getOrderNo(), merchantPlayLimit);

        List<String> args = Lists.newArrayList();
        args.add(tenantId.toString());
        args.add(creditId);
        args.add(userId.toString());
        args.add(matchId.toString());
        args.add(marketId.toString());
        args.add(optionId.toString());
        args.add(betAmount.toString());
        args.add(payment + "");
        args.add(merchantPlayLimit + "");
        args.add(userPlayLimit + "");
        args.add(userSingleBetLimit + "");
        args.add(userOptionLimit + "");

        if (userSingleBetLimit < payment) {
            log.warn("冠军玩法下单::{}::，用户单注限额拒单，limit={}，payment={}", orderBean.getOrderNo(), userSingleBetLimit, payment);
            return checkOrderResult(orderBean, ErrorCode.LIMIT_21102, "冠军玩法额度查询，用户单注限额拒单");
        }
        JSONArray jsonArray = executeLua(keys, args);
        if (jsonArray != null && jsonArray.size() == 6) {
            log.info("冠军玩法下单::{}::，lua计算最终返回结果：{}", orderBean.getOrderNo(), JSON.toJSONString(jsonArray));
            int infoCode = jsonArray.getIntValue(0);
            String msg = jsonArray.getString(1);
            long merchantBet = jsonArray.getLongValue(2);
            long merchantPayment = jsonArray.getLongValue(3);
            long userBet = jsonArray.getLongValue(4);
            long userPayment = jsonArray.getLongValue(5);
            redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBY.getCmd(), merchantBetKey, optionId.toString(), String.valueOf(betAmount), String.valueOf(merchantBet)));
            redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBY.getCmd(), merchantPaymentKey, optionId.toString(), String.valueOf(payment), String.valueOf(merchantPayment)));
            redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBY.getCmd(), userBetKey, optionId.toString(), String.valueOf(betAmount), String.valueOf(userBet)));
            redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBY.getCmd(), userPaymentKey, optionId.toString(), String.valueOf(payment), String.valueOf(userPayment)));
            return checkOrderResult(orderBean, infoCode, msg);
        }
        return checkOrderResult(orderBean, ErrorCode.LIMIT_FAILURE, "冠军玩法订单校验失败");
    }

}
