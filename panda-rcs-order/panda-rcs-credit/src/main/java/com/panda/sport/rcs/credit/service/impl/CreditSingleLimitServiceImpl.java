package com.panda.sport.rcs.credit.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.credit.constants.CreditRedisKey;
import com.panda.sport.rcs.credit.constants.ErrorCode;
import com.panda.sport.rcs.credit.service.AbstractCreditLimitService;
import com.panda.sport.rcs.credit.service.CreditLimitService;
import com.panda.sport.rcs.exeception.LogicException;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.pojo.limit.RedisUpdateVo;
import com.panda.sport.rcs.vo.RcsBusinessPlayPaidConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用单关限额服务
 * @Author : Paca
 * @Date : 2021-05-05 19:55
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service("creditSingleLimitService")
public class CreditSingleLimitServiceImpl extends AbstractCreditLimitService {

    @Override
    protected List<RcsBusinessPlayPaidConfigVo> queryBetLimit(OrderBean orderBean, List<OrderItem> orderItems) {
        Long tenantId = orderBean.getTenantId();
        String creditAgentId = orderBean.getAgentId();
        Long userId = orderBean.getUid();
        Integer seriesType = orderBean.getSeriesType();
        OrderItem orderItem = orderBean.getItems().get(0);

        String dateExpect = orderItem.getDateExpect();
        Integer sportId = orderItem.getSportId();
        Integer tournamentLevel = orderItem.getTurnamentLevel();
        Long matchId = orderItem.getMatchId();
        Integer playId = orderItem.getPlayId();
        Integer matchType = orderItem.getMatchType();

        // 代理单场赛事限额
        long singleMatchLimit = getAgentSingleMatchLimit(tenantId, creditAgentId, sportId, tournamentLevel);
        long singleMatchUsed = getAgentSingleMatchUsed(dateExpect, tenantId, creditAgentId, matchId);
        long singleMatch = singleMatchLimit - singleMatchUsed;
        log.info("信用额度::{}::获取限额,代理单场赛事限额剩余额度：{} = {} - {}", userId, singleMatch, singleMatchLimit, singleMatchUsed);
        int infoCode = ErrorCode.LIMIT_20001;
        long remain = singleMatch;
        if (remain > 0) {
            // 代理玩法累计限额
            long singlePlayLimit = getAgentSinglePlayLimit(tenantId, creditAgentId, sportId, tournamentLevel, playId, matchType);
            long singlePlayUsed = getAgentSinglePlayUsed(dateExpect, tenantId, creditAgentId, sportId, playId, matchType, matchId);
            long singlePlay = singlePlayLimit - singlePlayUsed;
            log.info("信用额度::{}::获取限额,代理玩法累计限额剩余额度：{} = {} - {}", userId, singlePlay, singlePlayLimit, singlePlayUsed);
            if (singlePlay <= 0L) {
                infoCode = ErrorCode.LIMIT_20002;
                remain = singlePlay;
            } else {
                if (singlePlay < remain) {
                    infoCode = ErrorCode.LIMIT_20002;
                    remain = singlePlay;
                }

                // 用户玩法累计限额
                long userSinglePlayLimit = getUserSinglePlayLimit(tenantId, creditAgentId, userId, sportId, tournamentLevel, playId, matchType);
                long userSinglePlayUsed = getUserSinglePlayUsed(dateExpect, userId, sportId, playId, matchType, matchId);
                long userSinglePlay = userSinglePlayLimit - userSinglePlayUsed;
                log.info("信用额度::{}::获取限额,用户玩法累计限额剩余额度：{} = {} - {}", userId, userSinglePlay, userSinglePlayLimit, userSinglePlayUsed);
                if (userSinglePlay <= 0L) {
                    infoCode = ErrorCode.LIMIT_20101;
                    remain = userSinglePlay;
                } else {
                    if (userSinglePlay < remain) {
                        infoCode = ErrorCode.LIMIT_20101;
                        remain = userSinglePlay;
                    }

                    // 用户玩法单注限额
                    long betLimit = getUserSinglePlayBetLimit(tenantId, creditAgentId, userId, sportId, tournamentLevel, playId, matchType);
                    log.info("信用额度::{}::获取限额,用户玩法单注限额：{} = {} - {}", userId, userSinglePlay, userSinglePlayLimit, userSinglePlayUsed);
                    if (betLimit <= 0L || betLimit < remain) {
                        infoCode = ErrorCode.LIMIT_20301;
                        remain = betLimit;
                    }
                    //bug-37387 信用网新增商户限额比例需求
                    //用户单注赔付限额 = M3 * P (详细见bug37387需求说明)
                    BigDecimal businessSinglePlayBetRatio = getBusinessSinglePlayBetRatio(tenantId, creditAgentId);
                    long betLimitAfterRatio = new BigDecimal(betLimit).multiply(businessSinglePlayBetRatio).longValue();
                    log.info("信用额度::{}::获取限额,用户单注赔付限额：{}", userId, betLimitAfterRatio);
                    if (betLimitAfterRatio <= 0L || betLimitAfterRatio < remain) {
                        infoCode = ErrorCode.LIMIT_20301;
                        remain = betLimitAfterRatio;
                    }
                }
            }
        }
        log.info("信用额度::{}::获取限额,获取到最终的限额：{}", userId, remain);
        long maxBetAmount;
        if (remain <= 0L) {
            maxBetAmount = 0L;
        } else {
            BigDecimal highOdds = getHighOdds();
            BigDecimal odds = getOdds(orderItem);
            BigDecimal euOdds = convertEuOdds(odds, highOdds);
            // 大于高赔取赔付，否则取投注
            maxBetAmount = new BigDecimal(remain).divide(euOdds.subtract(BigDecimal.ONE), 2, RoundingMode.DOWN).longValue();
            log.info("信用额度::{}::获取限额,赔率取值：{}，最终计算限额值：{}", userId, euOdds, maxBetAmount);
        }
        RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
        vo.setType(String.valueOf(seriesType));
        vo.setMinBet(1L);
        vo.setOrderMaxPay(maxBetAmount / 100);
        vo.setInfoErrorCode(infoCode);
        return Lists.newArrayList(vo);
    }

    @Override
    protected Map<String, Object> checkOrder(OrderBean orderBean, List<RedisUpdateVo> redisUpdateList) {
        log.info("信用网订单::{}::，单关额度校验开始：{}", orderBean.getOrderNo(), JSON.toJSONString(redisUpdateList));
        Long tenantId = orderBean.getTenantId();
        String creditAgentId = orderBean.getAgentId();
        Long userId = orderBean.getUid();
        OrderItem orderItem = orderBean.getItems().get(0);
        String dateExpect = orderItem.getDateExpect();

        Integer sportId = orderItem.getSportId();
        Integer tournamentLevel = orderItem.getTurnamentLevel();
        Long matchId = orderItem.getMatchId();
        Integer playId = orderItem.getPlayId();
        Integer matchType = orderItem.getMatchType();
        Long betAmount = orderItem.getBetAmount();
        BigDecimal odds = getOdds(orderItem);

        BigDecimal highOdds = getHighOdds();
        // 大于高赔取赔付，否则取投注
        long payment = getBetPayment(betAmount, odds, highOdds);
        log.info("信用网订单::{}::，该单关注单最大赔付：{}", orderBean.getOrderNo(), payment);
        long betLimit = getUserSinglePlayBetLimit(tenantId, creditAgentId, userId, sportId, tournamentLevel, playId, matchType);
        log.info("信用网订单::{}::，用户玩法单注最终限额：{}", orderBean.getOrderNo(), betLimit);
        //bug-37387 信用网新增商户限额比例需求
        //用户单注赔付限额 = M3 * P (详细见bug37387需求说明)
        BigDecimal businessSinglePlayBetRatio = getBusinessSinglePlayBetRatio(tenantId, creditAgentId);
        betLimit = new BigDecimal(betLimit).multiply(businessSinglePlayBetRatio).longValue();
        log.info("信用网订单::{}::，商户代理单注限额比例：{}，用户玩法单注最终限额：{}", orderBean.getOrderNo(), businessSinglePlayBetRatio, betLimit);
        long betLimitNew = new BigDecimal(betLimit).multiply(RcsConstant.LIMIT_MULTIPLE_SINGLE).longValue();
        log.info("信用网订单::{}::，用户玩法单注限额扩大：{}倍，新额度：{}，原额度：{}", orderBean.getOrderNo(), RcsConstant.LIMIT_MULTIPLE_SINGLE, betLimitNew, betLimit);
        if (payment > betLimitNew) {
            log.info("信用网订单::{}::，用户玩法单注限额拒单：新额度：{} < 注单赔付额度：{}", orderBean.getOrderNo(), betLimitNew, payment);
            return checkOrderResult(orderBean, ErrorCode.LIMIT_20301, "信用额度，用户玩法单注限额拒单");
        }
        long singleMatchUsed = incrAgentSingleMatchUsed(dateExpect, tenantId, creditAgentId, matchId, payment, redisUpdateList);
        long singleMatchLimit = getAgentSingleMatchLimit(tenantId, creditAgentId, sportId, tournamentLevel);
        long singleMatchLimitNew = new BigDecimal(singleMatchLimit - singleMatchUsed + payment).multiply(RcsConstant.LIMIT_MULTIPLE_SINGLE).longValue();
        log.info("信用网订单::{}::，代理单场赛事限额扩大：{}倍，新额度：{}，原额度：{}，已用额度：{}，注单赔付额度：{}", orderBean.getOrderNo(), RcsConstant.LIMIT_MULTIPLE_SINGLE, singleMatchLimitNew, singleMatchLimit, singleMatchUsed, payment);
        if (payment > singleMatchLimitNew) {
            redisCallback(redisUpdateList);
            log.info("信用网订单::{}::，代理单场赛事限额拒单，扩大后额度：{} < 注单赔付额度：{} ", orderBean.getOrderNo(), singleMatchLimitNew, payment);
            return checkOrderResult(orderBean, ErrorCode.LIMIT_20001, "信用额度，代理单场赛事限额拒单");
        }
        long singlePlayUsed = incrAgentSinglePlayUsed(dateExpect, tenantId, creditAgentId, sportId, playId, matchType, matchId, payment, redisUpdateList);
        long singlePlayLimit = getAgentSinglePlayLimit(tenantId, creditAgentId, sportId, tournamentLevel, playId, matchType);
        long singlePlayLimitNew = new BigDecimal(singlePlayLimit - singlePlayUsed + payment).multiply(RcsConstant.LIMIT_MULTIPLE_SINGLE).longValue();
        log.info("信用网订单::{}::，代理玩法累计限额扩大：{}倍，新额度：{}，原额度：{}，已用额度：{}，注单赔付额度：{}", orderBean.getOrderNo(), RcsConstant.LIMIT_MULTIPLE_SINGLE, singlePlayLimitNew, singlePlayLimit, singlePlayUsed, payment);
        if (payment > singlePlayLimitNew) {
            redisCallback(redisUpdateList);
            log.info("信用网订单::{}::，代理玩法累计限额拒单，扩大后额度：{} < 注单赔付额度：{}", orderBean.getOrderNo(), singlePlayLimitNew, payment);
            return checkOrderResult(orderBean, ErrorCode.LIMIT_20002, "信用额度，代理玩法累计限额拒单");
        }
        long userSinglePlayUsed = incrUserSinglePlayUsed(dateExpect, userId, sportId, playId, matchType, matchId, payment, redisUpdateList);
        long userSinglePlayLimit = getUserSinglePlayLimit(tenantId, creditAgentId, userId, sportId, tournamentLevel, playId, matchType);
        long userSinglePlayLimitNew = new BigDecimal(userSinglePlayLimit - userSinglePlayUsed + payment).multiply(RcsConstant.LIMIT_MULTIPLE_SINGLE).longValue();
        log.info("信用网订单::{}::，用户玩法累计限额扩大：{}倍，新额度：{}，原额度：{}，已用额度：{}，注单赔付额度：{}", orderBean.getOrderNo(), RcsConstant.LIMIT_MULTIPLE_SINGLE, userSinglePlayLimitNew, userSinglePlayLimit, userSinglePlayUsed, payment);
        if (payment > userSinglePlayLimitNew) {
            redisCallback(redisUpdateList);
            log.info("信用网订单::{}::，用户玩法累计限额拒单：扩大后额度：{} < 注单赔付额度：{}", orderBean.getOrderNo(), userSinglePlayLimitNew, payment);
            return checkOrderResult(orderBean, ErrorCode.LIMIT_20101, "信用额度，用户玩法累计限额拒单");
        }
        return checkOrderResult(orderBean, ErrorCode.LIMIT_SUCCESS, "单关订单校验成功");
    }

    @Override
    protected void queryCheckOrderBean(OrderBean orderBean) {
        commonCheckOrderBean(orderBean);
        if (orderBean.getItems().size() > 1) {
            throw new RcsServiceException("单关协议格式异常，不支持多个投注项");
        }
    }

    @Override
    protected void queryCheckOrderItem(OrderItem orderItem) {
        commonCheckOrderItem(orderItem);
    }

    @Override
    protected void orderCheckOrderBean(OrderBean orderBean) {
        // 查询校验的参数，下单必校验
        queryCheckOrderBean(orderBean);
        if (StringUtils.isBlank(orderBean.getOrderNo())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "订单号orderNo不能为空！");
        }
        if (CreditLimitService.checkNo(orderBean.getUid())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "用户uid不能为空！");
        }
    }

    @Override
    protected void orderCheckOrderItem(OrderItem orderItem) {
        // 查询校验的参数，下单必校验
        queryCheckOrderItem(orderItem);
        if (StringUtils.isBlank(orderItem.getBetNo())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items注单编号betNo不能为空！");
        }
        if (StringUtils.isBlank(orderItem.getOrderNo())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items订单号orderNo不能为空！");
        }
        if (CreditLimitService.checkNo(orderItem.getUid())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items用户uid不能为空！");
        }
    }

    @Override
    protected String getHighOddsKey() {
        return CreditRedisKey.SINGLE_HIGH_ODDS_KEY;
    }

    @Override
    public int orderType() {
        return 1;
    }
}
