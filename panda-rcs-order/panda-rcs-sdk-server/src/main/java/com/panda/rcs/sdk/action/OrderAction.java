package com.panda.rcs.sdk.action;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.PreOrderRequest;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.data.rcs.dto.limit.MarkerPlaceLimitAmountReqVo;
import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;
import com.panda.sport.data.rcs.dto.oddin.TicketResultDto;
import com.panda.sport.sdk.constant.LimitRedisKeys;
import com.panda.sport.sdk.constant.RedisKeys;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.core.Sdk;
import com.panda.sport.sdk.listeners.OrderStatusHandler;
import com.panda.sport.sdk.service.impl.OrderPaidApiImpl;
import com.panda.sport.sdk.util.GuiceContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RestController
@RequestMapping(value = "/dubbo")
public class OrderAction {

    JedisClusterServer jedisClusterServer = GuiceContext.getInstance(JedisClusterServer.class);
    private OrderPaidApiImpl orderApi = Sdk.init().getOrderPaidApi(new OrderStatusHandler());
    private LimitApiService limitApiService = GuiceContext.getInstance(LimitApiService.class);

    /**
     * 未登录最大可投注金额
     */

    @RequestMapping(value = "/queryInitMaxBetMoneyBySelect")
    @ResponseBody
    public Response queryInitMaxBetMoneyBySelect(@RequestBody Request<OrderBean> requestParam) {
        return orderApi.queryInitMaxBetMoneyBySelect(requestParam);
    }

    /**
     * 用户点击投注选项返回当前选项最大可投注金额
     */

    @RequestMapping(value = "/queryMaxBetMoneyBySelect")
    @ResponseBody
    public Response queryMaxBetMoneyBySelect(@RequestBody Request<OrderBean> requestParam) {
        return orderApi.queryMaxBetMoneyBySelect(requestParam);
    }

    /**
     * 校验当前订单是否超过最大赔付金额
     */

    @ResponseBody
    @RequestMapping(value = "/validateOrderMaxPaid")
    public Response validateOrderMaxPaid(@RequestBody Request<OrderBean> requestParam) {
        return orderApi.validateOrderMaxPaid(requestParam);
    }

    /**
     * 派奖后做订单状态和返奖数据同步
     * 成功返回注单号
     */

    @RequestMapping(value = "/saveOrderAndValidateMaxPaid")
    @ResponseBody
    public Response saveOrderAndValidateMaxPaid(@RequestBody Request<OrderBean> requestParam) {
        return orderApi.saveOrderAndValidateMaxPaid(requestParam);
    }


    /**
     * 派奖后做订单状态和返奖数据同步
     * 成功返回注单号
     */

    @RequestMapping(value = "/updateOrderAfterRefund")
    @ResponseBody
    public Response updateOrderAfterRefund(@RequestBody Request<SettleItem> requestParam) {
        return orderApi.updateOrderAfterRefund(requestParam);
    }

    @RequestMapping(value = "/limitTest")
    @ResponseBody
    public Response limitTest(@RequestBody Request<MarkerPlaceLimitAmountReqVo> request) {
        Response response = limitApiService.getMarketPlaceLimit(request);
        return response;
    }

    /**
     * 提前结算
     *
     */

    @RequestMapping(value = "/preSettleOrder")
    @ResponseBody
    public Response<PreOrderRequest> preSettleOrder(@RequestBody Request<PreOrderRequest> requestParam) {
        return orderApi.preSettleOrder(requestParam);
    }

    /**
     * 用于限额 清理所有缓存
     *
     * @return
     */
    @RequestMapping(value = "/clear")
    public Response clearCache() {

        return Response.success();
    }

    /**
     * 用于限额 清理所有缓存
     *
     * @return
     */
    @RequestMapping(value = "/clearForeCast")
    public Response clearForeCast() {
        return Response.success();
    }

    /**
     * 用于限额 清理所有缓存
     *
     * @return
     */
    @RequestMapping(value = "/getCache")
    public List<String> clearCache(Integer busId, Integer matchId, String date, Long userId, Integer sportId, Integer level, Integer matchType) {
        List<String> list = new ArrayList<>();
        //商戶限额
        list.add("----------------商戶限额----------------------");
        String value = jedisClusterServer.get(LimitRedisKeys.MERCHANT_LIMIT_KEY + busId.toString());
        JSONObject json = JSONObject.parseObject(value);
        list.add("商戶限额:" + json.getLong("businessSingleDayLimit"));
        list.add("商戶用户限额比例:" + json.getLong("userQuotaRatio"));

        String valueSeries = jedisClusterServer.hget(LimitRedisKeys.MERCHANT_LIMIT_KEY, busId.toString());
        JSONObject jsonSeries = JSONObject.parseObject(valueSeries);
        list.add("商戶串关限额:" + jsonSeries.getLong("businessSingleDaySeriesLimit"));
        list.add("商戶用户串关限额比例:" + jsonSeries.getLong("businessSingleDaySeriesLimitProportion"));

        //商戶已用额度
        String cacheValue = jedisClusterServer.get(String.format(RedisKeys.PAID_DATE_BUS_REDIS_CACHE, date, busId));
        String seriesCacheValue = jedisClusterServer.get(String.format(RedisKeys.PAID_DATE_BUS_SERIES_REDIS_CACHE, date, busId));
        list.add("商戶已用额度:" + cacheValue);
        list.add("商戶已用串关额度:" + seriesCacheValue);
        //商户是否停止接单:
        String stopKey = String.format(RedisKeys.PAID_DATE_BUS_STOP_REDIS_CACHE, date, busId);
        list.add("商户是否停止接单度:" + jedisClusterServer.get(stopKey));


        list.add("--------用户单日---单关--------------------------");
        //用户单日-配置
        String userDayLimitKey = LimitRedisKeys.USER_DAY_LIMIT_KEY;
        String userDayLimitSport = jedisClusterServer.get(userDayLimitKey + sportId.toString());
        String userDayLimitAll = jedisClusterServer.get(userDayLimitKey + "-1");
        list.add("用户单日-配置-赛种:" + userDayLimitSport);
        list.add("用户单日-配置-总:" + userDayLimitAll);
        //用户单日-已用
        String dayCompensationKey = LimitRedisKeys.getDayCompensationKey(date, busId.toString(), userId.toString());
        String usedDay = jedisClusterServer.hget(dayCompensationKey, sportId.toString());
        String usedDayAll = jedisClusterServer.hget(dayCompensationKey, LimitRedisKeys.TOTAL_FIELD);
        list.add("用户单日-已用额度-赛种:" + usedDay);
        list.add("用户单日-已用额度-总:" + usedDayAll);

        list.add("----------用户单日---串关--------------------------");
        //用户单日-串关-配置
        String userDaySeriesLimitKey = LimitRedisKeys.USER_DAY_SERIES_LIMIT_KEY;
        String userDaySeriesLimiSport = jedisClusterServer.hget(userDaySeriesLimitKey, sportId.toString());
        String userDaySeriesLimiAll = jedisClusterServer.hget(userDaySeriesLimitKey, "-1");
        list.add("用户单日-串关-配置-赛种:" + userDaySeriesLimiSport);
        list.add("用户单日-串关-配置-总:" + userDaySeriesLimiAll);

        //用户单日-串关-已用
        String crossDayCompensationKey = LimitRedisKeys.getCrossDayCompensationKey(date, busId.toString(), userId.toString());
        String usedCrossDayCompensation = jedisClusterServer.hget(crossDayCompensationKey, sportId.toString());
        String usedCrossDayCompensationTotal = jedisClusterServer.hget(crossDayCompensationKey, LimitRedisKeys.TOTAL_FIELD);
        list.add("用户单日-串关-已用-赛种:" + usedCrossDayCompensation);
        list.add("用户单日-串关-已用-总:" + usedCrossDayCompensationTotal);

        //用户单场
        list.add("-----------用户单场---------------");
        String limitKey = LimitRedisKeys.getLimitKey(Integer.valueOf(sportId), level, LimitDataTypeEnum.USER_SINGLE_LIMIT);
        String singelField = String.format(LimitRedisKeys.SINGLE_USER_EARLY_PAYMENT_FIELD, matchId);
        String earlyUserSingleSiteQuota = jedisClusterServer.hget(limitKey, singelField);
        String liveField = String.format(LimitRedisKeys.SINGLE_USER_LIVE_PAYMENT_FIELD, matchId);
        String liveUserSingleSiteQuota = jedisClusterServer.hget(limitKey, liveField);
        list.add("用户单场-早盘:" + earlyUserSingleSiteQuota);
        list.add("用户单场-滚球:" + liveUserSingleSiteQuota);

        String prefix = "RCS:RISK:" + date + ":" + busId + ":" + sportId + ":";
        String suffix = "_{" + busId + "_" + matchId + "}";
        String userMatchKey = prefix + userId + ":" + matchId + ":" + matchType + suffix;
        BigDecimal userMatchAllPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(userMatchKey, "USER_MATCH_ALL_PAID")).orElse("0"));
        list.add("用户单场已用:" + userMatchAllPaidMoney);


        //商户单场
        list.add(" ------------商户单场--------------");
        limitKey = LimitRedisKeys.getLimitKey(sportId, level, LimitDataTypeEnum.MERCHANT_SINGLE_LIMIT);
        String earlyField = String.format(LimitRedisKeys.SINGLE_MERCHANTS_EARLY_PAYMENT_FIELD, matchId);
        String earlyMorningPaymentLimit = jedisClusterServer.hget(limitKey, earlyField);
        liveField = String.format(LimitRedisKeys.SINGLE_MERCHANTS_LIVE_PAYMENT_FIELD, matchId);
        String liveBallPayoutLimit = jedisClusterServer.hget(limitKey, liveField);
        list.add("商户单场-早盘:" + earlyMorningPaymentLimit);
        list.add("商户单场-滚球:" + liveBallPayoutLimit);
        String singleMatchInfoKey = prefix + matchId + ":" + matchType + ":V2" + suffix;
        String singleMatch = jedisClusterServer.hget(singleMatchInfoKey, "MAX_MATCH_PAID");
        list.add("商户单场-已用:" + liveBallPayoutLimit);

        return list;
    }

    @RequestMapping(value = "/getUserLimitReference")
    @ResponseBody
    public Response getUserLimitReference(@RequestBody Request<Long> request) {
        return orderApi.getUserLimitReference(request);
    }

}
