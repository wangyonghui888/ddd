package com.panda.sport.sdk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.panda.sport.sdk.constant.BaseConstants;
import com.panda.sport.sdk.constant.LimitRedisKeys;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.mq.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 商户限额预警
 * @Author : Paca
 * @Date : 2021-03-05 21:12
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Singleton
public class MerchantLimitWarnService {

    private static final Logger log = LoggerFactory.getLogger(MerchantLimitWarnService.class);

    @Inject
    private Producer producer;
    @Inject
    private JedisClusterServer jedisClusterServer;

    public void sendMsg(Long merchantId, Long usedPaid, Long merchantLimit, String dateExpect, String orderNo, String busName) {
        BigDecimal usedAmountPercent = new BigDecimal(usedPaid).divide(new BigDecimal(merchantLimit), BaseConstants.PERCENT_SCALE, RoundingMode.HALF_UP);
        String field = null;
        if (usedAmountPercent.compareTo(BigDecimal.ONE) >= 0) {
            // 达到100%
            field = "100";
        } else if (usedAmountPercent.compareTo(new BigDecimal("0.8")) >= 0) {
            // 达到80%
            field = "80";
        } else if (usedAmountPercent.compareTo(new BigDecimal("0.6")) >= 0) {
            // 达到60%
            field = "60";
        }
        if (StringUtils.isBlank(field)) {
            return;
        }
        log.info("商户限额预警：merchantId={},usedPaid={},merchantLimit={},dateExpect={},orderNo={},field={}", merchantId, usedPaid, merchantLimit, dateExpect, orderNo, field);
        String signKey = LimitRedisKeys.getMerchantLimitWarnSignKey(dateExpect, merchantId);
        String sign = jedisClusterServer.hget(signKey, field);
        if (!"1".equals(sign)) {
            jedisClusterServer.hset(signKey, field, "1");
            int businessSingleDayLimit = new BigDecimal(merchantLimit).divide(BaseConstants.HUNDRED, BaseConstants.MONEY_SCALE, RoundingMode.HALF_UP).intValue();
            sendMerchantLimitWarningData(merchantId, businessSingleDayLimit, usedAmountPercent, dateExpect, orderNo, field);
        }
        //推送mango/telegram 商户预警  需求-2088
        MerchantOverDailyLimit(field, dateExpect, 0, String.valueOf(merchantId), busName, merchantLimit, usedPaid, usedAmountPercent.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP).toPlainString());

    }

    public void sendSeriesMsg(Long merchantId, Long usedPaid, Long merchantSeriesLimit, String dateExpect, String orderNo, String busName) {
        BigDecimal usedAmountPercent = new BigDecimal(usedPaid).divide(new BigDecimal(merchantSeriesLimit), BaseConstants.PERCENT_SCALE, RoundingMode.HALF_UP);
        String field = null;
        if (usedAmountPercent.compareTo(BigDecimal.ONE) >= 0) {
            // 达到100%
            field = "100";
        } else if (usedAmountPercent.compareTo(new BigDecimal("0.8")) >= 0) {
            // 达到80%
            field = "80";
        } else if (usedAmountPercent.compareTo(new BigDecimal("0.6")) >= 0) {
            // 达到60%
            field = "60";
        }
        if (StringUtils.isBlank(field)) {
            return;
        }
        log.info("商户串关限额预警：merchantId={},usedPaid={},merchantSeriesLimit={},dateExpect={},orderNo={},field={}", merchantId, usedPaid, merchantSeriesLimit, dateExpect, orderNo, field);
        String signKey = LimitRedisKeys.getMerchantSeriesLimitWarnSignKey(dateExpect, merchantId);
        String sign = jedisClusterServer.hget(signKey, field);
        if (!"1".equals(sign)) {
            jedisClusterServer.hset(signKey, field, "1");
            int businessSingleDayLimit = new BigDecimal(merchantSeriesLimit).divide(BaseConstants.HUNDRED, BaseConstants.MONEY_SCALE, RoundingMode.HALF_UP).intValue();
            sendMerchantLimitWarningData(merchantId, businessSingleDayLimit, usedAmountPercent, dateExpect, orderNo, field);
        }
        //推送mango/telegram 商户预警  需求-2088
        MerchantOverDailyLimit(field, dateExpect, 1, String.valueOf(merchantId), busName, merchantSeriesLimit, usedPaid, field);

    }

    @Value("${spring.profiles.active:Prod}")
    private String env;


    private void MerchantOverDailyLimit(String rateKey, String dateExpect, Integer warnType, String merchantsId, String merchantsName, Long limit, Long used, String rate) {
        String MERCHANT_DAILY_ALERT_KEY = "rcs:sdk:over:num:daily:%s:warnType:%s:merchantsId:%s";
        String alertKey = String.format(MERCHANT_DAILY_ALERT_KEY, dateExpect, warnType, merchantsId) + ":" + rateKey;
        Long num = jedisClusterServer.incrBy(alertKey, 1L);
        jedisClusterServer.expire(alertKey, 24 * 60 * 60);
        String activeProfiles = SpringContextUtils.getApplicationContext().getEnvironment().getProperty("spring.profiles.active");
        if (StringUtils.isBlank(activeProfiles)) {
            activeProfiles = "pro";
        }
        log.info("::{}::商户单日单关限额超出发送预警信息,key:{},num:{},activeProfiles:{}", merchantsId, alertKey, num, activeProfiles);
        if (("100".equals(rateKey) && num <= 2) || ("80".equals(rateKey) && num <= 1)) {
            List<String> list = new ArrayList<>();
            DecimalFormat format = new DecimalFormat("#,###");
            String str = null;
            if (warnType == 0) {
                str = "[AlertType]：MerchantOverSingleDayLimit商户单日单关限额告警\n" +
                        "[Env]：" + (activeProfiles == null ? "Pro" : activeProfiles) + "\n" +
                        "[Alert Time]：" + com.panda.sport.rcs.common.DateUtils.transferLongToDateStrings(System.currentTimeMillis()) + "\n" +
                        "[Merchant ID]：" + merchantsId + "\n" +
                        "[Merchant Name]：" + merchantsName + "\n" +
                        "[MerchantSingleDayLimit]：" + format.format(new BigDecimal(limit).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)) + "\n" +
                        "[MerchantSingleDayLimitUsed]：" + format.format(new BigDecimal(used).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)) + "\n" +
                        "[MerchantSingleDayLimitUsed%]：" + rate + "%";
            } else {
                str = "[AlertType]：MerchantOverParlayDayLimit商户单日串关限额告警\n" +
                        "[Env]：" + (activeProfiles == null ? "Pro" : activeProfiles) + "\n" +
                        "[Alert Time]：" + DateUtils.transferLongToDateStrings(System.currentTimeMillis()) + "\n" +
                        "[Merchant ID]：" + merchantsId + "\n" +
                        "[Merchant Name]：" + merchantsName + "\n" +
                        "[MerchantParlayDayLimit]：" + format.format(new BigDecimal(limit).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)) + "\n" +
                        "[MerchantParlayDayLimitUsed]：" + format.format(new BigDecimal(used).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)) + "\n" +
                        "[MerchantParlayDayLimitUsed%]：" + rate + "%";
            }
            list.add(str);
            JSONObject param = new JSONObject(new LinkedHashMap<>());
            param.put("data", list);
            //需求编号
            param.put("dataSourceCode", "2088");
            //linkId
            String job = "ScoreAlertJob:" + merchantsId;
            param.put("linkId", job);
            //推送融合，进行mango/telegram预警
            producer.sendMsg("PA_COMMON_WARN_INFO", warnType == 0 ? "MerchantOverSingleDayLimit" : "MerchantOverParlayDayLimit", job, String.valueOf(param));
        }
    }

    /**
     * 向商户平台发送商户限额预警消息
     *
     * @param merchantId        商户ID
     * @param usedAmount        商户已赔付金额，单位元
     * @param usedAmountPercent 商户已赔付金额达到配置的百分比
     * @param dateExpect        账务日
     * @param orderNo           订单号
     * @param flag              标志，100-达到100%，80-达到80%，60-达到60%
     */
//    private void sendMerchantAccountAlert(Long merchantId, BigDecimal usedAmount, BigDecimal usedAmountPercent, String dateExpect, String orderNo, String flag) {
//        JSONObject json = new JSONObject();
//        json.put("merchantId", merchantId);
//        json.put("usedAmount", usedAmount);
//        json.put("usedAmountPercent", usedAmountPercent);
//        json.put("dateExpect", dateExpect);
//        json.put("timestamp", System.currentTimeMillis());
//        producer.sendMsg("MERCHANT_ACCOUNT_ALERT", flag + "_" + orderNo, dateExpect + "_" + merchantId, json.toString());
//    }

    /**
     * 向风控发送商户限额预警消息
     *
     * @param merchantId             商户ID
     * @param businessSingleDayLimit 商户单日限额，单位元
     * @param usedAmountPercent      商户已赔付金额达到配置的百分比
     * @param dateExpect             账务日
     * @param orderNo                订单号
     * @param flag                   标志，100-达到100%，80-达到80%，60-达到60%
     */
    private void sendMerchantLimitWarningData(Long merchantId, Integer businessSingleDayLimit, BigDecimal usedAmountPercent, String dateExpect, String orderNo, String flag) {
        JSONObject json = new JSONObject();
        json.put("businessId", merchantId);
        json.put("businessSingleDayLimit", businessSingleDayLimit);
        json.put("amountUsed", usedAmountPercent);
        json.put("dateExpect", dateExpect);
        json.put("timestamp", System.currentTimeMillis());
        producer.sendMsg("rcs_merchant_limit_warning_data", flag + "_" + orderNo, dateExpect + "_" + merchantId, json.toString());
    }
}
