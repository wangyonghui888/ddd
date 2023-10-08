package com.panda.sport.sdk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.data.rcs.api.OrderLimitNewVersionApi;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.limit.*;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.dto.limit.SeriesPaymentAmount;
import com.panda.sport.rcs.dto.limit.UserDayLimit;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.enums.limit.RedisCmdEnum;
import com.panda.sport.sdk.constant.BaseConstants;
import com.panda.sport.sdk.constant.LimitRedisKeys;
import com.panda.sport.sdk.constant.RedisKeys;
import com.panda.sport.sdk.constant.SdkConstants;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.mq.Producer;
import com.panda.sport.sdk.sdkenum.SeriesEnum;
import com.panda.sport.sdk.service.impl.matrix.SecondCommon;
import com.panda.sport.sdk.util.*;
import com.panda.sport.sdk.vo.RcsBusinessPlayPaidConfigVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.constants.BaseConstants.SAVE_ORDER_TAGS;

/**
 * @Project Name: panda-rcs-order-group
 * @Package Name: com.panda.sport.sdk.service.impl
 * @Description : 串关限额
 * @Author : Paca
 * @Date : 2020-09-25 13:25
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Singleton
public class SeriesLimitService {

    private static final Logger log = LoggerFactory.getLogger(SeriesLimitService.class);

    @Inject
    private ParamValidateService paramValidate;
    @Inject
    private Producer producer;
    @Inject
    private LimitConfigService limitConfigService;
    @Inject
    private CommonService commonService;
    @Inject
    private JedisClusterServer jedisClusterServer;
    @Inject
    private MerchantLimitWarnService merchantLimitWarnService;
    @Inject
    SecondCommon secondCommon;
    @Inject
    private OrderLimitNewVersionApi orderLimitNewVersionApiImpl;

    private void checkOrderBean(OrderBean orderBean) {
        if (orderBean == null || orderBean.getItems() == null ||
                orderBean.getItems().size() < SeriesEnum.TWO.getSeriesNum() ||
                orderBean.getItems().size() > SeriesEnum.Ten.getSeriesNum()) {
            throw new RcsServiceException("参数错误，items参数与串关类型参数不匹配");
        }
        Integer seriesType = orderBean.getSeriesType();
        if (seriesType == null || seriesType < SeriesEnum.TWO.getSeriesJoin()) {
            throw new RcsServiceException("当前业务不支持单关");
        }
        // 获取M串N中的M
        Integer type = SeriesTypeUtils.getSeriesType(seriesType);
        if (type > orderBean.getItems().size() ||
                type < SeriesEnum.TWO.getSeriesNum() ||
                type > SeriesEnum.Ten.getSeriesNum()) {
            throw new RcsServiceException("参数错误，items与seriesType不匹配");
        }
    }

    public List<RcsBusinessPlayPaidConfigVo> queryMaxBetMoneyBySelect(OrderBean orderBean, boolean isLogin, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit) {
        checkOrderBean(orderBean);
        //1470 2.0 限额接口
        String switchVal = String.valueOf(rcsQuotaBusinessLimit.getStraySwitchVal());
        if (StringUtils.equalsIgnoreCase(switchVal, BaseConstants.MERCHANT_STRAY_SWITCH_NEW_VAL)) {
            Request<OrderBean> request = new Request<>();
            request.setData(orderBean);
            request.setGlobalId(MDC.get("X-B3-TraceId"));
            Response response = orderLimitNewVersionApiImpl.queryMaxBetAmountByOrder(request);
            List<RcsBusinessPlayPaidConfigVo> rcsBusinessPlayPaidConfigVos = JSONObject.parseArray(JSONObject.toJSONString(response.getData()), RcsBusinessPlayPaidConfigVo.class);
            log.info("::2.0串关限额调用RPC接口返回::{}", JSONObject.toJSONString(rcsBusinessPlayPaidConfigVos));
            return rcsBusinessPlayPaidConfigVos;
        }
        final Integer seriesType = orderBean.getSeriesType();
        // 获取M串N中的M
        final Integer seriesNum = SeriesTypeUtils.getSeriesType(seriesType);
        final String dateExpect = DateUtils.getDateExpect(System.currentTimeMillis());
        Long tenantId = orderBean.getTenantId();
        String businessId = String.valueOf(tenantId);
        List<Long> matchIds = orderBean.getItems().stream().map(OrderItem::getMatchId).collect(Collectors.toList());

        List<ExtendBean> extendBeanList = getExtendBean(orderBean);

        List<Long> paymentList = new ArrayList<>();
        // 商户可用赔付，单位：分
        long businessAvailablePayment = limitConfigService.getBusinessSeriesAvailablePayment(tenantId, dateExpect);
        paymentList.add(businessAvailablePayment);
        // 单注赔付限额->配置
        paymentList.add(getSeriesPaymentLimit(extendBeanList).longValue());
        // 单注赔付限额->特殊限额
        long singleNoteClaimLimit = Long.MAX_VALUE;
        String type = null;
        if (isLogin) {
            String userId = String.valueOf(orderBean.getUid());
            type = getUserSpecialLimitType(userId);

            // 用户单日可用赔付，单位：分
            paymentList.add(getUserDayAvailablePayment(extendBeanList, dateExpect, businessId, userId, type));

            if ("3".equals(type)) {
                // 单注赔付限额->特殊限额
                singleNoteClaimLimit = getSingleNoteClaimLimit(userId, new BigDecimal(Integer.MAX_VALUE)).longValue();
                paymentList.add(singleNoteClaimLimit);
            }

            // 赛事单场串关限额 剩余/可用
            paymentList.add(limitConfigService.getSeriesSingleMatchAvailableLimit(tenantId, userId, matchIds));
        }
        // 取最小值获得 可用赔付
        Long availablePayment = paymentList.stream().min(Long::compareTo).orElse(0L);
        log.info("串关额度-最小可用赔付：{}，各个维度可用赔付：{}", availablePayment, paymentList);
        // 单关->单注投注赔付限额
        BigDecimal singlePaymentLimit = getSinglePaymentLimit(extendBeanList, rcsQuotaBusinessLimit);
        // 最低/最高投注额限制
        BetAmountLimitVo betAmountLimit = getBetAmountLimit(extendBeanList);
        // 单注最低投注额，单位：分
        final long seriesMinBet = betAmountLimit.getSeriesMinBet().longValue();
        // 单注最高投注额，单位：分
        long seriesMaxBet = singlePaymentLimit.multiply(betAmountLimit.getSeriesMaxBetRatio()).longValue();
        if (isLogin) {
            seriesMaxBet = Math.min(seriesMaxBet, singleNoteClaimLimit);
        }

        List<RcsBusinessPlayPaidConfigVo> result = getSeriesLimit(orderBean.getItems(), availablePayment, seriesMinBet, seriesMaxBet, seriesNum);
        if (isLogin && !"4".equals(type) && !checkSingle(extendBeanList,rcsQuotaBusinessLimit)) {
            result.forEach(bean -> bean.setOrderMaxPay(0L));
        }
        return result;
    }

    public List<RcsBusinessPlayPaidConfigVo> queryMaxBetMoneyBySelectSpecialVip(OrderBean orderBean) {
        checkOrderBean(orderBean);
        final Integer seriesType = orderBean.getSeriesType();
        // 获取M串N中的M
        final Integer seriesNum = SeriesTypeUtils.getSeriesType(seriesType);
        final String dateExpect = DateUtils.getDateExpect(System.currentTimeMillis());
        final String businessId = String.valueOf(orderBean.getTenantId());
        final String userId = String.valueOf(orderBean.getUid());
        List<ExtendBean> extendBeanList = getExtendBean(orderBean);

        // 特殊VIP限额-串关-单注投注/赔付限额
        BigDecimal userSpecialLimit = getSingleNoteClaimLimit(userId, new BigDecimal(Integer.MAX_VALUE));

        // 单注最低投注额，单位：分
        final long seriesMinBet = getBetAmountLimit(extendBeanList).getSeriesMinBet().longValue();
        // 单注最高投注额，单位：分
        final long seriesMaxBet = userSpecialLimit.longValue();

        // 商户可用赔付，单位：分
        long businessAvailablePayment = limitConfigService.getBusinessSeriesAvailablePayment(orderBean.getTenantId(), dateExpect);
        // 单注赔付限额，单位：分
        long seriesPaymentLimit = userSpecialLimit.longValue();
        // 用户单日可用赔付，单位：分
        long userDayAvailablePayment = getUserDayAvailablePaymentSpecialVip(dateExpect, businessId, userId);
        List<Long> paymentList = new ArrayList<>();
        paymentList.add(businessAvailablePayment);
        paymentList.add(seriesPaymentLimit);
        paymentList.add(userDayAvailablePayment);
        // 取最小值获得 可用赔付
        Long availablePayment = paymentList.stream().min(Long::compareTo).orElse(0L);
        log.info("额度查询-串关-最小可用赔付：{}，各个维度可用赔付：{}", availablePayment, paymentList);
        return getSeriesLimit(orderBean.getItems(), availablePayment, seriesMinBet, seriesMaxBet, seriesNum);
    }

    /**
     * 获取商户是否为2.0串关限额
     *
     * @param orderBean
     * @return
     */
    private String getBusinessSwitchKey(OrderBean orderBean) {

        String switchKey = (String) orderLimitNewVersionApiImpl.queryBusinessSwitch(String.valueOf(orderBean.getTenantId())).getData();
        log.info("2.0串关调用RPC查询商户旧限额返回:{},商户ID:{}", switchKey, orderBean.getTenantId());
        return switchKey;
    }

    /**
     * 获取串关限额
     *
     * @param items            串关投注项信息
     * @param availablePayment 可用赔付
     * @param seriesMinBet     最低投注额
     * @param seriesMaxBet     最高投注额
     * @param seriesNum        M串N中的M
     * @return
     */
    private List<RcsBusinessPlayPaidConfigVo> getSeriesLimit(List<OrderItem> items, Long availablePayment, long seriesMinBet, long seriesMaxBet, int seriesNum) {
        List<RcsBusinessPlayPaidConfigVo> resultList = new ArrayList<>();
        Map<Integer, Long> maxBetAmountMap = getMaxBetAmountByPayment(availablePayment, items, seriesNum);
        log.info("串关额度-单注最低投注额：{}，单注最高投注额：{}，最大投注额：{}", seriesMinBet, seriesMaxBet, maxBetAmountMap);
        maxBetAmountMap.forEach((type, maxBetAmount) -> {
            long seriesMax = Math.min(maxBetAmount, seriesMaxBet);
            RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
            vo.setType(String.valueOf(type));
            vo.setMinBet(0L);
            vo.setOrderMaxPay(seriesMax / 100);
            resultList.add(vo);
        });
        return resultList;
    }

    /**
     * 通过 可用赔付额度 获取 最大投注额，单位：分 <br/>
     * 推导公式，例如3串4，欧赔分别为odds1、odds2、odds3，A为拆分后单个注单投注额，M为剩余赔付额度，已知M求A <br/>
     * 2串1：A*(odds1*odds2-1) + A*(odds1*odds3-1) + A*(odds2*odds3-1) = M <br/>
     * 3串1：A*(odds1*odds2*odds3-1) = M <br/>
     * 3串4：A*(odds1*odds2-1) + A*(odds1*odds3-1) + A*(odds2*odds3-1) + A*(odds1*odds2*odds3-1) = M
     *
     * @param availablePayment 可用赔付额度，单位：分
     * @param orderItemList    赛事投注项信息
     * @param seriesNum        串关关数，M串N中的M
     * @return
     * @author Paca
     */
    private Map<Integer, Long> getMaxBetAmountByPayment(Long availablePayment, List<OrderItem> orderItemList, int seriesNum) {
        Map<Integer, Long> resultMap = Maps.newHashMapWithExpectedSize(seriesNum);
        // 事串关组合
        Map<Integer, List<List<Integer>>> combinationMap = SpliteOrderUtils.combination(orderItemList.size());
        // 所有注单港赔之和，用来计算M串N
        BigDecimal allHkOddsSum = BigDecimal.ZERO;
        int sizeSum = 0;
        // 分组遍历，2串1一组，3串1一组，4串1一组......M串1一组
        for (Map.Entry<Integer, List<List<Integer>>> entry : combinationMap.entrySet()) {
            // key=M串1中的M，value=M串1组成的集合
            Integer key = entry.getKey();
            List<List<Integer>> value = entry.getValue();
            int size = value.size();
            sizeSum += size;
            // 每组注单港赔之和
            BigDecimal groupHkOddsSum = BigDecimal.ZERO;
            // 遍历M串1注单
            for (List<Integer> oddsIndexList : value) {
                // 单注注单港赔 = 投注项欧赔相乘 - 1
                BigDecimal betHkOdds = BigDecimal.ONE;
                // 遍历注单中所有投注项
                for (Integer oddsIndex : oddsIndexList) {
                    betHkOdds = betHkOdds.multiply(getOdds(orderItemList.get(oddsIndex)));
                }
                betHkOdds = betHkOdds.subtract(BigDecimal.ONE);
                groupHkOddsSum = groupHkOddsSum.add(betHkOdds);
            }
            // 计算M串1 中的 A，然后通过A计算总投注额
            long maxBetAmount = new BigDecimal(availablePayment * size).divide(groupHkOddsSum, BaseConstants.MONEY_SCALE, BigDecimal.ROUND_DOWN).longValue();
            resultMap.put(key * 1000 + 1, maxBetAmount);
            allHkOddsSum = allHkOddsSum.add(groupHkOddsSum);
        }
        // 计算M串N 中的 A，然后通过A计算总投注额
        if (seriesNum > SeriesEnum.TWO.getSeriesNum()) {
            SeriesEnum seriesEnum = SeriesEnum.getSeriesEnumBySeriesNum(seriesNum);
            long maxBetAmount = new BigDecimal(availablePayment * sizeSum).divide(allHkOddsSum, BaseConstants.MONEY_SCALE, BigDecimal.ROUND_DOWN).longValue();
            resultMap.put(seriesEnum.getSeriesJoin(), maxBetAmount);
        }
        return resultMap;
    }

    private BigDecimal getOdds(OrderItem orderItem) {
        Double oddsValue = orderItem.getOddsValue();
        if (oddsValue == null) {
            return BigDecimal.ONE;
        }
        return new BigDecimal(oddsValue + "").divide(BaseConstants.MULTIPLE, BaseConstants.MONEY_SCALE, BigDecimal.ROUND_DOWN);
    }

    public OrderCheckResultVo checkBetAmountAndPayment(OrderBean orderBean,RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit) {
        checkOrderBean(orderBean);
        //1470 2.0 限额接口
        String switchVal = this.getBusinessSwitchKey(orderBean);
        if (StringUtils.equalsIgnoreCase(switchVal, BaseConstants.MERCHANT_STRAY_SWITCH_NEW_VAL)) {
            Request<OrderBean> request = new Request<>();
            request.setData(orderBean);
            request.setGlobalId(MDC.get("X-B3-TraceId"));
            Response<OrderCheckResultVo> response = orderLimitNewVersionApiImpl.saveOrderCheckAmount(request);
//            log.info("2.0串关保存订单调用RPC返回:{}", JSONObject.toJSONString(response));
            OrderCheckResultVo orderCheckResultVo = response.getData();
            log.info("::{}::2.0串关保存订单调用RPC返回成功,对象转换成功",orderBean.getOrderNo());
            return orderCheckResultVo;
        }
        // 串关，用户单日限额，用户投注时所在的账务日
        final String dateExpect = DateUtils.getDateExpect(orderBean.getItems().get(0).getBetTime());
        //商户ID
        Long tenantId = orderBean.getTenantId();
        //用户ID
        String userId = String.valueOf(orderBean.getUid());
        String businessId = String.valueOf(tenantId);
        List<ExtendBean> extendBeanList = getExtendBean(orderBean);
        //注单总价
        final Long productAmountTotal = orderBean.getProductAmountTotal();
        //更新记录
        List<RedisUpdateVo> redisUpdateList = new ArrayList<>(20);

        // 最低/最高投注额限制，单位：分
        BetAmountLimitVo betAmountLimit = getBetAmountLimit(extendBeanList);

        // 单注最低投注额，单位：分
//        final long seriesMinBet = betAmountLimit.getSeriesMinBet().longValue();
//        if (productAmountTotal < seriesMinBet) {
//            String msg = String.format("下注金额%s小于最小投注限额%s", productAmountTotal, seriesMinBet);
//            log.warn(msg);
//            return new OrderCheckResultVo(false, msg, redisUpdateList);
//        }

        String type = getUserSpecialLimitType(userId);
        // 单注投注/赔付限额->特殊限额
        long singleNoteClaimLimit = Long.MAX_VALUE;
        if ("3".equals(type)) {
            singleNoteClaimLimit = getSingleNoteClaimLimit(userId, new BigDecimal(Integer.MAX_VALUE)).longValue();
        }

        // 单关单注投注赔付限额，单位：分
        BigDecimal singlePaymentLimit = getSinglePaymentLimit(extendBeanList,rcsQuotaBusinessLimit);
        // 单注最高投注额，单位：分
        long seriesMaxBet = singlePaymentLimit.multiply(betAmountLimit.getSeriesMaxBetRatio()).longValue();
        if ("3".equals(type)) {
            seriesMaxBet = Math.min(seriesMaxBet, singleNoteClaimLimit);
        }
        if (productAmountTotal > seriesMaxBet) {
            String msg = String.format("下注金额%s大于最大投注限额%s", productAmountTotal, seriesMaxBet);
            log.warn(msg);
            return new OrderCheckResultVo(false, msg, redisUpdateList);
        }

        SeriesPaymentAmount seriesPaymentAmount = calPaymentAmount(extendBeanList);
        long seriesPaymentTotal = seriesPaymentAmount.getSeriesPaymentTotal();

        long businessAvailablePayment = limitConfigService.getBusinessSeriesAvailablePayment(tenantId, dateExpect);
//        long businessAvailablePaymentOld = businessAvailablePayment;
//        businessAvailablePayment = new BigDecimal(businessAvailablePayment).multiply(BaseConstants.LIMIT_MULTIPLE).longValue();
//        log.info("商户限额扩大：{} = {} * {}", businessAvailablePayment, businessAvailablePaymentOld, BaseConstants.LIMIT_MULTIPLE.toPlainString());
        if (seriesPaymentTotal > businessAvailablePayment) {
            String msg = String.format("商户限额拒单，赔付：%s，可用：%s", seriesPaymentTotal, businessAvailablePayment);
            log.warn(msg);
            return new OrderCheckResultVo(false, msg, redisUpdateList);
        }

        // 赛事单场串关限额，只判断有没有
        List<Long> matchIds = orderBean.getItems().stream().map(OrderItem::getMatchId).collect(Collectors.toList());
        long seriesSingleMatchAvailableLimit = limitConfigService.getSeriesSingleMatchAvailableLimit(tenantId, userId, matchIds);
        if (seriesSingleMatchAvailableLimit <= 0) {
            String msg = String.format("赛事单场串关限额拒单，%s", seriesSingleMatchAvailableLimit);
            log.warn(msg);
            return new OrderCheckResultVo(false, msg, redisUpdateList);
        }
        long seriesSingleMatch = seriesPaymentTotal / orderBean.getItems().size();
        for (OrderItem orderItem : orderBean.getItems()) {
            Long matchId = orderItem.getMatchId();
            String key = String.format(RedisKeys.PAID_DATE_BUS_REDIS_MATCH_CACHE, userId, businessId, matchId);
            Double used = jedisClusterServer.incrByFloat(key, (double) seriesSingleMatch);
            redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.INCRBYFLOAT.getCmd(), key, null, String.valueOf(seriesSingleMatch), String.valueOf(used)));
        }

        // 单注赔付限额，单位：分
        long seriesPaymentLimit = getSeriesPaymentLimit(extendBeanList).longValue();
        if ("3".equals(type)) {
            seriesPaymentLimit = Math.min(seriesPaymentLimit, singleNoteClaimLimit);
        }
        long seriesPaymentLimitOld = seriesPaymentLimit;
        seriesPaymentLimit = new BigDecimal(seriesPaymentLimit).multiply(BaseConstants.LIMIT_MULTIPLE).longValue();
        log.info("单注赔付限额扩大：{} = {} * {}", seriesPaymentLimit, seriesPaymentLimitOld, BaseConstants.LIMIT_MULTIPLE.toPlainString());
        if (seriesPaymentTotal > seriesPaymentLimit) {
            redisCallback(redisUpdateList);
            String msg = String.format("单注赔付限额拒单，赔付：%s，可用：%s", seriesPaymentTotal, seriesPaymentLimit);
            log.warn(msg);
            return new OrderCheckResultVo(false, msg, redisUpdateList);
        }

        Integer seriesType = orderBean.getSeriesType();
        // 获取M串N中的M
        Integer seriesNum = SeriesTypeUtils.getSeriesType(seriesType);
        BigDecimal seriesUsedRatio = limitConfigService.getSeriesUsedRatio(seriesNum);
        seriesPaymentTotal = new BigDecimal(seriesPaymentTotal).multiply(seriesUsedRatio).longValue();
        // 用户单日限额
        UserDayLimit userDailyLimit = getUserDailyLimit(extendBeanList, dateExpect, businessId, userId);
        String userDailyField = String.valueOf(userDailyLimit.getSportId());
        // 单日串关赔付限额，单位：分
        long crossDayCompensation = userDailyLimit.getCrossDayCompensation().longValue();
        String crossDayCompensationKey = LimitRedisKeys.getCrossDayCompensationKey(dateExpect, businessId, userId);
        Long usedCrossDayCompensation = jedisClusterServer.hincrBy(crossDayCompensationKey, userDailyField, seriesPaymentTotal);
        redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBY.getCmd(), crossDayCompensationKey, userDailyField, String.valueOf(seriesPaymentTotal), String.valueOf(usedCrossDayCompensation)));
        long crossDayCompensationNew = new BigDecimal(crossDayCompensation - usedCrossDayCompensation + seriesPaymentTotal).multiply(BaseConstants.LIMIT_MULTIPLE).longValue();
        log.info("单日串关赔付限额扩大：newLimit={},limit={},used={},payment={},factor={}", crossDayCompensationNew, crossDayCompensation, usedCrossDayCompensation, seriesPaymentTotal, BaseConstants.LIMIT_MULTIPLE);
        if (seriesPaymentTotal > crossDayCompensationNew) {
            redisCallback(redisUpdateList);
            String msg = String.format("单日串关赔付限额拒单，赔付：%s，配置：%s，计算后：%s", seriesPaymentTotal, crossDayCompensation, usedCrossDayCompensation);
            log.warn(msg);
            return new OrderCheckResultVo(false, msg, redisUpdateList);
        }
        // 单日串关赔付总限额，单位：分
        long crossDayCompensationTotal = userDailyLimit.getCrossDayCompensationTotal().longValue();
        if ("3".equals(type)) {
            crossDayCompensationTotal = getSingleDayClaimLimit(userId, new BigDecimal(Integer.MAX_VALUE)).longValue();
        }
        Long usedCrossDayCompensationTotal = jedisClusterServer.hincrBy(crossDayCompensationKey, LimitRedisKeys.TOTAL_FIELD, seriesPaymentTotal);
        redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBY.getCmd(), crossDayCompensationKey, LimitRedisKeys.TOTAL_FIELD, String.valueOf(seriesPaymentTotal), String.valueOf(usedCrossDayCompensationTotal)));
        long crossDayCompensationTotalNew = new BigDecimal(crossDayCompensationTotal - usedCrossDayCompensationTotal + seriesPaymentTotal).multiply(BaseConstants.LIMIT_MULTIPLE).longValue();
        log.info("单日串关赔付总限额扩大：newLimit={},limit={},used={},payment={},factor={}", crossDayCompensationTotalNew, crossDayCompensationTotal, usedCrossDayCompensationTotal, seriesPaymentTotal, BaseConstants.LIMIT_MULTIPLE);
        if (seriesPaymentTotal > crossDayCompensationTotalNew) {
            redisCallback(redisUpdateList);
            String msg = String.format("单日串关赔付总限额拒单，赔付：%s，配置：%s，计算后：%s", seriesPaymentTotal, crossDayCompensationTotal, usedCrossDayCompensationTotal);
            log.warn(msg);
            return new OrderCheckResultVo(false, msg, redisUpdateList);
        }
        jedisClusterServer.expire(crossDayCompensationKey, 90 * 24 * 60 * 60);

        if (!checkSingle(extendBeanList,rcsQuotaBusinessLimit)) {
            redisCallback(redisUpdateList);
            return new OrderCheckResultVo(false, "单关限额拒单", redisUpdateList);
        }

        // 计入单关赔付限额
        List<SeriesPaymentAmount.SinglePaymentAmount> singlePaymentList = seriesPaymentAmount.getSinglePaymentList();
        for (SeriesPaymentAmount.SinglePaymentAmount bean : singlePaymentList) {
//            ExtendBean extendBean = new ExtendBean();
//            extendBean.setSportId(bean.getSportId());
//            extendBean.setTournamentLevel(NumberUtils.toInt(bean.getTournamentLevel(), -1));
//            extendBean.setMatchId(bean.getMatchId());
//            extendBean.setIsScroll(bean.getMatchType());
//            extendBean.setBusId(businessId);
            // 单关，取赛事日期所在的账务日
            String dateExpectSingle = bean.getDateExpect();
            double singlePayment = bean.getSinglePayment().doubleValue();
            String merchantSingleMatchHashKey = LimitRedisKeys.getMerchantSingleMatchHashKey(dateExpectSingle, businessId, bean.getSportId(), bean.getMatchId());
            String userSingleMatchHashKey = LimitRedisKeys.getUserSingleMatchHashKey(dateExpectSingle, businessId, bean.getSportId(), userId, bean.getMatchId(), bean.getMatchType());
            // 计入商户单场限额
            Double usedMerchantSingle = jedisClusterServer.hincrByFloat(merchantSingleMatchHashKey, LimitRedisKeys.MERCHANT_SINGLE_MATCH_HASH_FIELD, singlePayment);
            redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBYFLOAT.getCmd(), merchantSingleMatchHashKey, LimitRedisKeys.MERCHANT_SINGLE_MATCH_HASH_FIELD, String.valueOf(singlePayment), String.valueOf(usedMerchantSingle)));
            jedisClusterServer.expire(merchantSingleMatchHashKey, 90 * 24 * 60 * 60);
//            RcsQuotaMerchantSingleFieldLimitVo merchantSingleFieldLimitVo = limitConfigService.getRcsQuotaMerchantSingleFieldLimitData(extendBean);
//            BigDecimal merchantSingleMatchMaxPay = new BigDecimal(merchantSingleFieldLimitVo.getEarlyMorningPaymentLimit());
//            if ("1".equals(extendBean.getIsScroll())) {
//                merchantSingleMatchMaxPay = new BigDecimal(merchantSingleFieldLimitVo.getLiveBallPayoutLimit());
//            }
//            if (usedMerchantSingle > merchantSingleMatchMaxPay.doubleValue()) {
//                redisCallback(redisUpdateList);
//                String msg = String.format("商户单场限额拒单，赔付：%s，配置：%s，计算后：%s", singlePayment, merchantSingleMatchMaxPay.toPlainString(), usedMerchantSingle);
//                log.warn(msg);
//                return new OrderCheckResultVo(false, msg, redisUpdateList);
//            }
            // 计入用户单场限额
            Double usedUserSingle = jedisClusterServer.hincrByFloat(userSingleMatchHashKey, LimitRedisKeys.USER_SINGLE_MATCH_HASH_FIELD, singlePayment);
            redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBYFLOAT.getCmd(), userSingleMatchHashKey, LimitRedisKeys.USER_SINGLE_MATCH_HASH_FIELD, String.valueOf(singlePayment), String.valueOf(usedUserSingle)));
//            RcsQuotaUserSingleSiteQuotaVo userSingleSiteQuotaVo = limitConfigService.getRcsQuotaUserSingleSiteQuotaData(extendBean);
//            BigDecimal userSingeMatchMaxPay = userSingleSiteQuotaVo.getEarlyUserSingleSiteQuota();
//            if ("1".equals(extendBean.getIsScroll())) {
//                userSingeMatchMaxPay = userSingleSiteQuotaVo.getLiveUserSingleSiteQuota();
//            }
//            if (usedUserSingle > userSingeMatchMaxPay.doubleValue()) {
//                redisCallback(redisUpdateList);
//                String msg = String.format("用户单场限额拒单，赔付：%s，配置：%s，计算后：%s", singlePayment, userSingeMatchMaxPay.toPlainString(), usedUserSingle);
//                log.warn(msg);
//                return new OrderCheckResultVo(false, msg, redisUpdateList);
//            }
            // 计入用户玩法累计赔付限额
            String field = String.format(LimitRedisKeys.USER_SINGLE_MATCH_PLAY_HASH_FIELD, bean.getPlayId(), bean.getMatchType(), bean.getPlayType());
            Double usedUserPlay = jedisClusterServer.hincrByFloat(userSingleMatchHashKey, field, singlePayment);
            redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBYFLOAT.getCmd(), userSingleMatchHashKey, field, String.valueOf(singlePayment), String.valueOf(usedUserPlay)));
            jedisClusterServer.expire(userSingleMatchHashKey, 90 * 24 * 60 * 60);
        }

        String seriesRedisUpdateRecordKey = String.format(LimitRedisKeys.SERIES_REDIS_UPDATE_RECORD_KEY, orderBean.getOrderNo());
        jedisClusterServer.set(seriesRedisUpdateRecordKey, JSON.toJSONString(redisUpdateList));
        log.info("额度查询-订单redis缓存：key=" + seriesRedisUpdateRecordKey);
        jedisClusterServer.expire(seriesRedisUpdateRecordKey, Long.valueOf(TimeUnit.DAYS.toSeconds(90L)).intValue());
        return new OrderCheckResultVo(true, "串关订单校验成功", redisUpdateList);
    }

    private OrderCheckResultVo checkBetAmountAndPaymentSpecialVip(OrderBean orderBean) {
        checkOrderBean(orderBean);
        // 串关，用户单日限额，用户投注时所在的账务日
        final String dateExpect = DateUtils.getDateExpect(orderBean.getItems().get(0).getBetTime());
        Long tenantId = orderBean.getTenantId();
        String userId = String.valueOf(orderBean.getUid());
        String businessId = String.valueOf(tenantId);

        List<ExtendBean> extendBeanList = getExtendBean(orderBean);
        final Long productAmountTotal = orderBean.getProductAmountTotal();

        List<RedisUpdateVo> redisUpdateList = new ArrayList<>(20);

        // 单注最低投注额，单位：分
//        final long seriesMinBet = getBetAmountLimit(extendBeanList).getSeriesMinBet().longValue();
//        if (productAmountTotal < seriesMinBet) {
//            String msg = String.format("下注金额%s小于最小投注限额%s", productAmountTotal, seriesMinBet);
//            log.warn(msg);
//            return new OrderCheckResultVo(false, msg, redisUpdateList);
//        }

        // 特殊VIP限额-串关-单注投注/赔付限额
        BigDecimal userSpecialLimit = getSingleNoteClaimLimit(userId, new BigDecimal(Integer.MAX_VALUE));

        // 单注最高投注额，单位：分
        final long seriesMaxBet = userSpecialLimit.longValue();
        if (productAmountTotal > seriesMaxBet) {
            String msg = String.format("下注金额%s大于最大投注限额%s", productAmountTotal, seriesMaxBet);
            log.warn(msg);
            return new OrderCheckResultVo(false, msg, redisUpdateList);
        }

        SeriesPaymentAmount seriesPaymentAmount = calPaymentAmount(extendBeanList);
        // 串关订单总赔付数额，单位：分
        long seriesPaymentTotal = seriesPaymentAmount.getSeriesPaymentTotal();

        long businessAvailablePayment = limitConfigService.getBusinessSeriesAvailablePayment(tenantId, dateExpect);
//        long businessAvailablePaymentOld = businessAvailablePayment;
//        businessAvailablePayment = new BigDecimal(businessAvailablePayment).multiply(BaseConstants.LIMIT_MULTIPLE).longValue();
//        log.info("商户限额扩大：{} = {} * {}", businessAvailablePayment, businessAvailablePaymentOld, BaseConstants.LIMIT_MULTIPLE.toPlainString());
        if (seriesPaymentTotal > businessAvailablePayment) {
            String msg = String.format("商户限额拒单，赔付：%s，可用：%s", seriesPaymentTotal, businessAvailablePayment);
            log.warn(msg);
            return new OrderCheckResultVo(false, msg, redisUpdateList);
        }

        // 单注赔付限额，单位：分
        long seriesPaymentLimit = userSpecialLimit.longValue();
        long seriesPaymentLimitOld = seriesPaymentLimit;
        seriesPaymentLimit = new BigDecimal(seriesPaymentLimit).multiply(BaseConstants.LIMIT_MULTIPLE).longValue();
        log.info("单注赔付限额扩大：{} = {} * {}", seriesPaymentLimit, seriesPaymentLimitOld, BaseConstants.LIMIT_MULTIPLE.toPlainString());
        if (seriesPaymentTotal > seriesPaymentLimit) {
            String msg = String.format("单注赔付限额拒单，赔付：%s，可用：%s", seriesPaymentTotal, seriesPaymentLimit);
            log.warn(msg);
            return new OrderCheckResultVo(false, msg, redisUpdateList);
        }

        String crossDayCompensationKey = LimitRedisKeys.getCrossDayCompensationKey(dateExpect, businessId, userId);
        // 单日串关赔付总限额，单位：分
        long crossDayCompensationTotal = getSingleDayClaimLimit(userId, new BigDecimal(Integer.MAX_VALUE)).longValue();
        Long usedCrossDayCompensationTotal = jedisClusterServer.hincrBy(crossDayCompensationKey, LimitRedisKeys.TOTAL_FIELD, seriesPaymentTotal);
        redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBY.getCmd(), crossDayCompensationKey, LimitRedisKeys.TOTAL_FIELD, String.valueOf(seriesPaymentTotal), String.valueOf(usedCrossDayCompensationTotal)));
        jedisClusterServer.expire(crossDayCompensationKey, 90 * 24 * 60 * 60);
        long crossDayCompensationTotalNew = new BigDecimal(crossDayCompensationTotal - usedCrossDayCompensationTotal + seriesPaymentTotal).multiply(BaseConstants.LIMIT_MULTIPLE).longValue();
        log.info("单日串关赔付总限额扩大：newLimit={},limit={},used={},payment={},factor={}", crossDayCompensationTotalNew, crossDayCompensationTotal, usedCrossDayCompensationTotal, seriesPaymentTotal, BaseConstants.LIMIT_MULTIPLE);
        if (seriesPaymentTotal > crossDayCompensationTotalNew) {
            redisCallback(redisUpdateList);
            String msg = String.format("单日串关赔付总限额拒单，赔付：%s，配置：%s，计算后：%s", seriesPaymentTotal, crossDayCompensationTotal, usedCrossDayCompensationTotal);
            log.warn(msg);
            return new OrderCheckResultVo(false, msg, redisUpdateList);
        }

        return new OrderCheckResultVo(true, "串关订单校验成功", redisUpdateList);
    }

    public OrderCheckResultVo checkBetAmountAndPayment(OrderBean orderBean, String userSpecialLimitType,RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit) {
        if ("4".equals(userSpecialLimitType)) {
            return checkBetAmountAndPaymentSpecialVip(orderBean);
        } else {
            return checkBetAmountAndPayment(orderBean,rcsQuotaBusinessLimit);
        }
    }

    public Map<String, Object> saveOrderAndValidateMaxPaid(OrderBean orderBean,RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit) {
        String userId = String.valueOf(orderBean.getUid());
        OrderCheckResultVo resultVo = checkBetAmountAndPayment(orderBean, getUserSpecialLimitType(userId),rcsQuotaBusinessLimit);
        return result(orderBean, resultVo.isPass(), resultVo.getMsg());
    }

    private Map<String, Object> result(OrderBean orderBean, boolean isPass, String msg) {
        Map<String, Object> result = Maps.newHashMap();
        result.put(orderBean.getOrderNo(), isPass);
        result.put(orderBean.getOrderNo() + "_error_msg", msg);
        orderBean.setValidateResult(isPass ? 1 : 2);
        for (OrderItem orderItem : orderBean.getItems()) {
            orderItem.setValidateResult(orderBean.getValidateResult());
        }
        // status返回到业务端状态 0 失败  1成功  2 待处理
        result.put("status", 1);
        orderBean.getExtendBean().setValidateResult(1);
        orderBean.setValidateResult(1);
        orderBean.setOrderStatus(1);
        orderBean.setInfoStatus(OrderInfoStatusEnum.EARLY_PASS.getCode());

        if (isPass) {
            //如果是滚球订单
            if (isScrollOrder(orderBean)) {
                if (secondCommon.secondRace(orderBean)) {
                    result.put("status", 1);
                    result.put("infoStatus", OrderInfoStatusEnum.EARLY_PASS.getCode());
                    result.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
                    result.put("infoMsg", "风控秒接");
                } else {
                    orderBean.getExtendBean().setValidateResult(1);
                    orderBean.setValidateResult(1);
                    orderBean.setOrderStatus(0);
                    orderBean.setInfoStatus(OrderInfoStatusEnum.RISK_PROCESSING.getCode());
                    result.put("status", 2);
                    result.put("infoStatus", OrderInfoStatusEnum.RISK_PROCESSING.getCode());
                    result.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
                    result.put("infoMsg", "风控接拒单处理中");
                }

            } else {
                result.put("status", 1);
                result.put("infoStatus", OrderInfoStatusEnum.EARLY_PASS.getCode());
                result.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
                result.put("infoMsg", "早盘接单.");
            }
        } else {
            orderBean.getExtendBean().setValidateResult(2);
            orderBean.setValidateResult(2);
            orderBean.setOrderStatus(2);
            orderBean.setInfoStatus(OrderInfoStatusEnum.EARLY_REFUSE.getCode());
            result.put(orderBean.getOrderNo() + "_error_msg", msg);
            result.put("status", 0);
            result.put("infoStatus", OrderInfoStatusEnum.EARLY_REFUSE.getCode());
            result.put("infoMsg", "风控早盘拒单:" + msg);
            result.put("infoCode", SdkConstants.ORDER_ERROR_CODE_RISK);
        }
        producer.sendMsg(MqConstants.RCS_ORDER_UPDATE, SAVE_ORDER_TAGS, orderBean.getOrderNo(), JSONObject.toJSONString(orderBean), orderBean.getOrderNo());
        return result;
    }

    private void redisCallback(List<RedisUpdateVo> redisUpdateList) {
        if (CollectionUtils.isEmpty(redisUpdateList)) {
            return;
        }
        redisUpdateList.forEach(vo -> {
            BigDecimal value = StringUtil.toBigDecimal(vo.getValue(), BigDecimal.ZERO).negate();
            exeIncrByCmd(vo.getCmd(), vo.getKey(), vo.getField(), value);
        });
    }

    private void exeIncrByCmd(String cmd, String key, String field, BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        if (RedisCmdEnum.isIncrBy(cmd)) {
            jedisClusterServer.incrBy(key, value.longValue());
        } else if (RedisCmdEnum.isIncrByFloat(cmd)) {
            jedisClusterServer.incrByFloat(key, value.doubleValue());
        } else if (RedisCmdEnum.isHincrBy(cmd)) {
            jedisClusterServer.hincrBy(key, field, value.longValue());
        } else if (RedisCmdEnum.isHincrByFloat(cmd)) {
            jedisClusterServer.hincrByFloat(key, field, value.doubleValue());
        }
        jedisClusterServer.expire(key, 90 * 24 * 60 * 60);
    }

    private SeriesPaymentAmount calPaymentAmount(List<ExtendBean> extendBeanList) {
        ExtendBean extendBean = extendBeanList.get(0);
        Integer seriesType = extendBean.getSeriesType();
        // 获取M串N中的M
        Integer type = SeriesTypeUtils.getSeriesType(seriesType);
        // 获取M串N中的N
        int count = SeriesTypeUtils.getCount(seriesType, type);
        // 单注投注额，单位：分
        long singleBetAmount = extendBean.getOrderMoney();
        // 总赔付，单位：分
        long paymentTotal = 0L;
        // 计入单关的赔付金额
        Map<String, Long> singlePaymentMap = Maps.newHashMapWithExpectedSize(extendBeanList.size());
        // 各投注项计入单关限额的投注比例，key=赛种_联赛等级_赛事ID
        Map<String, Map<Integer, BigDecimal>> seriesRatioMap = getSeriesRatio(extendBeanList);
        // 赛事串关组合
        Map<Integer, List<List<Integer>>> combinationMap = SpliteOrderUtils.combination(extendBeanList.size());
        // 分组遍历，2串1一组，3串1一组，4串1一组......M串1一组
        for (Map.Entry<Integer, List<List<Integer>>> entry : combinationMap.entrySet()) {
            // key=M串1中的M，value=M串1组成的集合
            Integer key = entry.getKey();
            // M串1
            if (count == 1 && !type.equals(key)) {
                continue;
            }
            List<List<Integer>> value = entry.getValue();
            // 遍历M串1注单
            for (List<Integer> matchIndexList : value) {
                // 单注注单欧赔 = 投注项欧赔相乘
                BigDecimal betEuroOdds = BigDecimal.ONE;
                // 遍历注单中所有投注项
                for (Integer index : matchIndexList) {
                    ExtendBean bean = extendBeanList.get(index);
                    BigDecimal odds = getOdds(bean.getItemBean());
                    betEuroOdds = betEuroOdds.multiply(odds);

                    // 计入串关限额的投注比例
                    BigDecimal seriesRatio = BigDecimal.ONE;
                    String seriesRatioKey = String.format("%s_%s_%s", bean.getSportId(), bean.getTournamentLevel(), bean.getMatchId());
                    Map<Integer, BigDecimal> configMap = seriesRatioMap.get(seriesRatioKey);
                    if (configMap != null) {
                        seriesRatio = configMap.getOrDefault(key, BigDecimal.ONE);
                    }
                    // 计入单关的赔付限额 = 单注投注额 * 比例 * 投注项港赔
                    long singlePayment = new BigDecimal(singleBetAmount).multiply(seriesRatio).multiply(odds.subtract(BigDecimal.ONE)).longValue();
                    String mapKey = String.format("%s_%s_%s_%s_%s_%s_%s", bean.getSportId(), bean.getTournamentLevel(), bean.getMatchId(), bean.getIsScroll(), bean.getPlayId(), bean.getPlayType(), bean.getDateExpect());
                    long singlePaymentSum = singlePaymentMap.getOrDefault(mapKey, 0L);
                    singlePaymentSum = singlePaymentSum + singlePayment;
                    singlePaymentMap.put(mapKey, singlePaymentSum);
                }
                long payment = new BigDecimal(singleBetAmount).multiply(betEuroOdds.subtract(BigDecimal.ONE)).longValue();
                paymentTotal = paymentTotal + payment;
            }
        }
        log.info("串关额度-串关总赔付：{}，计入单关的赔付限额：{}", paymentTotal, singlePaymentMap);
        SeriesPaymentAmount result = new SeriesPaymentAmount();
        result.setSeriesPaymentTotal(paymentTotal);
        List<SeriesPaymentAmount.SinglePaymentAmount> singlePaymentList = new ArrayList<>(extendBeanList.size());
        singlePaymentMap.forEach((k, v) -> {
            String[] array = k.split("_");
            SeriesPaymentAmount.SinglePaymentAmount singlePaymentAmount = new SeriesPaymentAmount.SinglePaymentAmount();
            singlePaymentAmount.setSportId(array[0]);
            singlePaymentAmount.setTournamentLevel(array[1]);
            singlePaymentAmount.setMatchId(array[2]);
            singlePaymentAmount.setMatchType(array[3]);
            singlePaymentAmount.setPlayId(array[4]);
            singlePaymentAmount.setPlayType(array[5]);
            singlePaymentAmount.setDateExpect(array[6]);
            singlePaymentAmount.setSinglePayment(v);
            singlePaymentList.add(singlePaymentAmount);
        });
        result.setSinglePaymentList(singlePaymentList);
        return result;
    }

    /**
     * 是否包含滚球
     *
     * @param orderBean
     * @return
     */
    private boolean isScrollOrder(OrderBean orderBean) {
        // 出现任何滚球赛事  需走滚球接拒单流程逻辑
        for (OrderItem item : orderBean.getItems()) {
            if (item.getMatchType() == 2) {
                log.info("::{}::当前订单存在滚球注单，需要等待处理",orderBean.getOrderNo());
                return true;
            }
        }

        log.info("订单号：{}，非滚球订单", orderBean.getOrderNo());
        return false;
    }

    private List<ExtendBean> getExtendBean(OrderBean orderBean) {
        List<OrderItem> orderItemList = orderBean.getItems();
        List<ExtendBean> resultList = new ArrayList<>(orderItemList.size());
        orderItemList.forEach(orderItem -> resultList.add(paramValidate.buildExtendBean(orderBean, orderItem)));
        return resultList;
    }

    /**
     * 用户单日可用赔付，单位：分
     *
     * @param extendBeanList
     * @param dateExpect
     * @param businessId
     * @param userId
     * @return
     * @author Paca
     */
    private long getUserDayAvailablePayment(List<ExtendBean> extendBeanList, String dateExpect, String businessId, String userId, String type) {
        UserDayLimit userDailyLimit = getUserDailyLimit(extendBeanList, dateExpect, businessId, userId);
        log.info("串关额度-用户单日可用赔付，额度配置：businessId={},userId={},result={}", businessId, userId, userDailyLimit);
        // 用户单日限额，单位：分
        long crossDayCompensation = userDailyLimit.getCrossDayCompensation().longValue();
        long crossDayCompensationTotal = userDailyLimit.getCrossDayCompensationTotal().longValue();
        if ("3".equals(type)) {
            long singleDayClaimLimit = getSingleDayClaimLimit(userId, new BigDecimal(Integer.MAX_VALUE)).longValue();
            crossDayCompensationTotal = Math.min(crossDayCompensationTotal, singleDayClaimLimit);
        }
        String sportId = String.valueOf(userDailyLimit.getSportId());

        // 已用额度，单位：分
        String crossDayCompensationKey = LimitRedisKeys.getCrossDayCompensationKey(dateExpect, businessId, userId);
        Map<String, String> seriesMap = jedisClusterServer.hgetAllNoLog(crossDayCompensationKey);
        log.info("串关额度-用户单日可用赔付，已用单日串关额度：key={},result={}", crossDayCompensationKey, seriesMap);
        long usedCrossDayCompensation = StringUtil.toLong(seriesMap.get(sportId), 0L);
        long usedCrossDayCompensationTotal = StringUtil.toLong(seriesMap.get(LimitRedisKeys.TOTAL_FIELD), 0L);

        // 取剩余额度最小值
        List<Long> remainQuotaLimit = new ArrayList<>();
        long seriesAvailable = crossDayCompensation - usedCrossDayCompensation;
        log.info("串关额度-用户单日串关赔付：配置：{}，已用：{}，剩余可用：{}", crossDayCompensation, usedCrossDayCompensation, seriesAvailable);
        remainQuotaLimit.add(seriesAvailable);
        long seriesAvailableTotal = crossDayCompensationTotal - usedCrossDayCompensationTotal;
        log.info("串关额度-用户单日串关总赔付：配置：{}，已用：{}，剩余可用：{}", crossDayCompensationTotal, usedCrossDayCompensationTotal, seriesAvailableTotal);
        remainQuotaLimit.add(seriesAvailableTotal);
        return remainQuotaLimit.stream().min(Long::compareTo).orElse(0L);
    }

    private long getUserDayAvailablePaymentSpecialVip(String dateExpect, String businessId, String userId) {
        // 特殊VIP限额-串关-单日赔付限额
        long crossDayCompensationTotal = getSingleDayClaimLimit(userId, new BigDecimal(Integer.MAX_VALUE)).longValue();
        // 已用额度，单位：分
        String crossDayCompensationKey = LimitRedisKeys.getCrossDayCompensationKey(dateExpect, businessId, userId);
        Map<String, String> seriesMap = jedisClusterServer.hgetAllNoLog(crossDayCompensationKey);
        long usedCrossDayCompensationTotal = StringUtil.toLong(seriesMap.get(LimitRedisKeys.TOTAL_FIELD), 0L);
        long result = crossDayCompensationTotal - usedCrossDayCompensationTotal;
        log.info("额度查询-串关-用户单日可用赔付：key={},seriesMap={},result={}", crossDayCompensationKey, seriesMap, result);
        return result;
    }

    /**
     * 获取用户单日限额，单位：分
     *
     * @param extendBeanList
     * @param dateExpect
     * @param businessId
     * @param userId
     * @return
     * @author Paca
     */
    private UserDayLimit getUserDailyLimit(List<ExtendBean> extendBeanList, String dateExpect, String businessId, String userId) {
        // 获取 剩余串关赔付限额 最小值对应的 单日赔付限额配置和单日串关赔付限额配置
        Set<String> sportIds = extendBeanList.stream().map(ExtendBean::getSportId).collect(Collectors.toSet());
        List<UserDayLimit> userDayLimitList = limitConfigService.getUserDailyLimit(businessId, sportIds, userId);
        log.info("串关额度-获取用户单日限额，额度配置：businessId={},sportIds={},result={}", businessId, sportIds, userDayLimitList);
        // 剩余单日串关赔付限额最小值
        long remainCrossDayCompensationMin = Long.MAX_VALUE;
        UserDayLimit config = new UserDayLimit(BigDecimal.ZERO);
        String key = LimitRedisKeys.getCrossDayCompensationKey(dateExpect, businessId, userId);
        Map<String, String> map = jedisClusterServer.hgetAllNoLog(key);
        log.info("串关额度-获取用户单日限额，已用串关额度：key={},result={}", key, map);
        for (UserDayLimit userDayLimit : userDayLimitList) {
            // 已用额度，单位：分
            long usedQuota = StringUtil.toLong(map.get(String.valueOf(userDayLimit.getSportId())), 0L);
            // 单日串关赔付限额，转换成分
            long crossDayCompensation = userDayLimit.getCrossDayCompensation().longValue();
            // 剩余额度，单位：分
            long remainQuota = crossDayCompensation - usedQuota;
            if (remainQuota <= remainCrossDayCompensationMin) {
                remainCrossDayCompensationMin = remainQuota;
                config = userDayLimit;
            }
        }
        return config;
    }

    private BetAmountLimitVo getBetAmountLimit(List<ExtendBean> extendBeanList) {
        List<BetAmountLimitVo> limitList = new ArrayList<>(extendBeanList.size());
        extendBeanList.forEach(extendBean -> limitList.add(limitConfigService.getBetAmountLimit(extendBean)));
        log.info("串关额度-最低/最高投注额限制：{}", limitList);
        BetAmountLimitVo limit = new BetAmountLimitVo(BigDecimal.ZERO);
        // 取多场赛事的最小值
        limit.setSingleMinBet(limitList.stream().min(Comparator.comparing(BetAmountLimitVo::getSingleMinBet)).orElse(limit).getSingleMinBet());
        limit.setSeriesMinBet(limitList.stream().min(Comparator.comparing(BetAmountLimitVo::getSeriesMinBet)).orElse(limit).getSeriesMinBet());
        limit.setSeriesMaxBetRatio(limitList.stream().min(Comparator.comparing(BetAmountLimitVo::getSeriesMaxBetRatio)).orElse(limit).getSeriesMaxBetRatio());
        return limit;
    }

    private BigDecimal getSeriesPaymentLimit(List<ExtendBean> extendBeanList) {
        List<BigDecimal> limitList = new ArrayList<>(extendBeanList.size());
        extendBeanList.forEach(extendBean -> limitList.add(limitConfigService.getSeriesPaymentLimit(extendBean)));
        log.info("串关额度-串关单注赔付限额：{}", limitList);
        return limitList.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    }

    private String getUserSpecialLimitType(String userId) {
        // 用户特殊限额类型,0-无,1-标签限额,2-特殊百分比限额,3-特殊单注单场限额,4-特殊vip限额
        String userSpecialLimitKey = LimitRedisKeys.getUserSpecialLimitKey(userId);
        String userSpecialLimitType = RcsLocalCacheUtils.getValue(userSpecialLimitKey, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, jedisClusterServer::hget);

        log.info("额度查询-串关-用户特殊限额类型：{}", userSpecialLimitType);
        return userSpecialLimitType;
    }

    private BigDecimal getSingleNoteClaimLimit(String userId, BigDecimal defaultValue) {
        String key = LimitRedisKeys.getUserSpecialLimitKey(userId);
        String field = LimitRedisKeys.getSingleNoteClaimLimitField("2", "-1");
        String value = RcsLocalCacheUtils.getValue(key, field, jedisClusterServer::hget);

        log.info("额度查询-串关-用户特殊限额-单注投注/赔付限额：{}", value);
        if (StringUtils.isNotBlank(value)) {
            try {
                return new BigDecimal(value);
            } catch (Exception ignored) {
            }
        }
        return defaultValue;
    }

    private BigDecimal getSingleDayClaimLimit(String userId, BigDecimal defaultValue) {
        String key = LimitRedisKeys.getUserSpecialLimitKey(userId);
        String field = LimitRedisKeys.getSingleGameClaimLimitField("2", "-1");
        String value = RcsLocalCacheUtils.getValue(key, field, jedisClusterServer::hget);

        log.info("额度查询-串关-用户特殊限额-单日赔付限额：{}", value);
        if (StringUtils.isNotBlank(value)) {
            try {
                return new BigDecimal(value);
            } catch (Exception ignored) {
            }
        }
        return defaultValue;
    }

    private Map<String, Map<Integer, BigDecimal>> getSeriesRatio(List<ExtendBean> extendBeanList) {
        Map<String, Map<Integer, BigDecimal>> resultMap = Maps.newHashMapWithExpectedSize(extendBeanList.size());
        extendBeanList.forEach(extendBean -> {
            Integer sportId = Integer.valueOf(extendBean.getSportId());
            Integer tournamentLevel = extendBean.getTournamentLevel();
            Long matchId = Long.valueOf(extendBean.getMatchId());
            String key = String.format("%s_%s_%s", sportId, tournamentLevel, matchId);
            Map<Integer, BigDecimal> seriesRatioMap = limitConfigService.getSeriesRatio(sportId, tournamentLevel, matchId);
            resultMap.put(key, seriesRatioMap);
        });
        log.info("串关额度-计入单关限额的投注比例：{}", resultMap);
        return resultMap;
    }

    private BigDecimal getSinglePaymentLimit(List<ExtendBean> extendBeanList, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit) {
        List<BigDecimal> limitList = new ArrayList<>(extendBeanList.size());
        final RcsQuotaBusinessLimitResVo rcsQuotaBusiness = rcsQuotaBusinessLimit;
        extendBeanList.forEach(extendBean -> limitList.add(limitConfigService.getMaxBetAndMaxPaidLimit(extendBean, rcsQuotaBusiness)));
        log.info("串关额度-单关单注投注赔付限额：{}", limitList);
        return limitList.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    }

    public void rollback(String userId, String orderNo, long profitAmount, Long settleTime, Long businessId) {
        // 特殊VIP不计入商户单日
        if (!commonService.isSpecialVipLimit(userId)) {
            if (commonService.isNewMerchantLimitMode(String.valueOf(businessId))) {
                return;
            }
            String dateExpect = DateUtils.getDateExpect(settleTime);
            // 累加商户单日已用限额
            Long currentPaidAmount = limitConfigService.businessLimitIncrBy(settleTime, String.valueOf(businessId), profitAmount);
            // 获取商户单日限额配置
            Long businessLimit = limitConfigService.getBusinessLimit(businessId).getBusinessSingleDayLimit();
            // 商户限额预警消息
            merchantLimitWarnService.sendMsg(businessId, currentPaidAmount, businessLimit, dateExpect, orderNo, null);
            boolean bool = currentPaidAmount >= businessLimit;
            log.info("串关额度-商户单日最大赔付判断{},{},{}", currentPaidAmount, businessLimit, bool);
            String stopKey = String.format(RedisKeys.PAID_DATE_BUS_STOP_REDIS_CACHE, dateExpect, businessId);
            if (bool) {
                jedisClusterServer.set(stopKey, BaseConstants.MERCHANT_STOP_ORDER_SIGN);
            } else {
                jedisClusterServer.set(stopKey, "0");
            }
            jedisClusterServer.expire(stopKey, 26 * 60 * 60);

            handleBusinessSeriesLimit(orderNo, profitAmount, settleTime, businessId, null);
        }

        // 串关回滚
        String seriesRedisUpdateRecordKey = String.format(LimitRedisKeys.SERIES_REDIS_UPDATE_RECORD_KEY, orderNo);
        String record = jedisClusterServer.get(seriesRedisUpdateRecordKey);
        if (StringUtils.isBlank(record)) {
            return;
        }
        List<RedisUpdateVo> redisUpdateList = JSON.parseArray(record, RedisUpdateVo.class);
        redisUpdateList.forEach(vo -> {
            String key = vo.getKey();
            String field = vo.getField();
            BigDecimal value = StringUtil.toBigDecimal(vo.getValue(), BigDecimal.ZERO).negate();
            String cmd = vo.getCmd();
            if (key.contains("rcs:limit:crossDayCompensation") || key.contains("rcs:limit:dayCompensation")) {
                // 单日已用额度按照实际赔付来
                exeIncrByCmd(cmd, key, field, value.add(new BigDecimal(profitAmount)));
            } else {
                if (profitAmount <= 0) {
                    // 输、走水，已用额度回滚
                    exeIncrByCmd(cmd, key, field, value);
                }
            }
        });
        // 回滚后删除
        jedisClusterServer.delete(seriesRedisUpdateRecordKey);
    }

    private void handleBusinessLimit(String orderNo, long profitAmount, Long settleTime, Long businessId) {
        String dateExpect = DateUtils.getDateExpect(settleTime);
        // 累加商户单日已用限额
        Long currentPaidAmount = limitConfigService.businessLimitIncrBy(settleTime, String.valueOf(businessId), profitAmount);
        // 获取商户单日限额配置
        Long businessLimit = limitConfigService.getBusinessLimit(businessId).getBusinessSingleDayLimit();
        // 商户限额预警消息
        merchantLimitWarnService.sendMsg(businessId, currentPaidAmount, businessLimit, dateExpect, orderNo, null);
        String stopKey = String.format(RedisKeys.PAID_DATE_BUS_STOP_REDIS_CACHE, dateExpect, businessId);
        if (currentPaidAmount >= businessLimit) {
            jedisClusterServer.set(stopKey, BaseConstants.MERCHANT_STOP_ORDER_SIGN);
        } else {
            jedisClusterServer.set(stopKey, "0");
        }
        jedisClusterServer.expire(stopKey, 30 * 24 * 60 * 60);
    }

    private void handleBusinessSeriesLimit(String orderNo, long profitAmount, Long settleTime, Long businessId, String busName) {
        String dateExpect = DateUtils.getDateExpect(settleTime);
        // 累加商户单日 串关 已用限额
        Long currentPaidAmount = limitConfigService.businessSeriesLimitIncrBy(settleTime, String.valueOf(businessId), profitAmount);
        // 获取商户单日 串关 限额配置
        Long businessLimit = limitConfigService.getBusinessLimit(businessId).getBusinessSingleDaySeriesLimit();
        // 商户串关限额预警消息
        merchantLimitWarnService.sendSeriesMsg(businessId, currentPaidAmount, businessLimit, dateExpect, orderNo, busName);
    }

    private boolean checkSingle(List<ExtendBean> extendBeanList,RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit) {
        for (ExtendBean extendBean : extendBeanList) {
            String userSingleMatchHashKey = LimitRedisKeys.getUserSingleMatchHashKey(extendBean.getDateExpect(), extendBean.getBusId(), extendBean.getSportId(), extendBean.getUserId(), extendBean.getMatchId(), extendBean.getIsScroll());

            RcsQuotaUserSingleSiteQuotaVo userSingleSiteQuotaVo = limitConfigService.getRcsQuotaUserSingleSiteQuotaData(extendBean);
            BigDecimal userSingeLimit;
            if ("1".equals(extendBean.getIsScroll())) {
                userSingeLimit = userSingleSiteQuotaVo.getLiveUserSingleSiteQuota();
            } else {
                userSingeLimit = userSingleSiteQuotaVo.getEarlyUserSingleSiteQuota();
            }
            String singleUsed = jedisClusterServer.hget(userSingleMatchHashKey, LimitRedisKeys.USER_SINGLE_MATCH_HASH_FIELD);
            BigDecimal userSingeUsed = StringUtil.toBigDecimal(singleUsed, BigDecimal.ZERO);
            if (userSingeLimit.compareTo(userSingeUsed) <= 0) {
                log.warn("额度查询-单关用户单场可用额度不足：limit={},key={},field={},value={}", userSingeLimit.toPlainString(), userSingleMatchHashKey, LimitRedisKeys.USER_SINGLE_MATCH_HASH_FIELD, singleUsed);
                return false;
            }

            BigDecimal userPlayLimit = limitConfigService.getRcsQuotaUserSingleNoteVoNew(extendBean,rcsQuotaBusinessLimit).getCumulativeCompensationPlaying();
            String field = String.format(LimitRedisKeys.USER_SINGLE_MATCH_PLAY_HASH_FIELD, extendBean.getPlayId(), extendBean.getIsScroll(), extendBean.getPlayType());
            String playValue = jedisClusterServer.hget(userSingleMatchHashKey, field);
            BigDecimal userPlayUsed = StringUtil.toBigDecimal(playValue, BigDecimal.ZERO);
            if (userPlayLimit.compareTo(userPlayUsed) <= 0) {
                log.warn("额度查询-单关用户玩法可用额度不足：limit={},key={},field={},value={}", userPlayLimit.toPlainString(), userSingleMatchHashKey, field, playValue);
                return false;
            }
        }
        return true;
    }

}
