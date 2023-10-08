package com.panda.sport.rcs.credit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.cache.redis.RedisUtils;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.credit.constants.CreditRedisKey;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 商户限额预警
 * @Author : Paca
 * @Date : 2021-03-05 21:12
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service
public class MerchantLimitWarnService {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    public void sendMsg(Long merchantId, String creditId, Long usedPaid, Long merchantLimit, String dateExpect, String orderNo) {
        BigDecimal usedAmountPercent = new BigDecimal(usedPaid).divide(new BigDecimal(merchantLimit), 4, RoundingMode.HALF_UP);
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
        log.info("商户限额预警：merchantId={},creditId={},usedPaid={},merchantLimit={},dateExpect={},orderNo={},field={}", merchantId, creditId, usedPaid, merchantLimit, dateExpect, orderNo, field);
        String signKey;
        if (StringUtils.isNotBlank(creditId)) {
            signKey = CreditRedisKey.getMerchantLimitWarnSignKey(dateExpect, creditId);
        } else {
            signKey = CreditRedisKey.getMerchantLimitWarnSignKey(dateExpect, String.valueOf(merchantId));
        }
        String sign = redisUtils.hget(signKey, field);
        if (!"1".equals(sign)) {
            redisUtils.hset(signKey, field, "1");
            redisUtils.expire(signKey, 30L, TimeUnit.DAYS);
//            BigDecimal usedAmount = new BigDecimal(usedPaid).divide(RcsConstant.HUNDRED, 2, RoundingMode.HALF_UP);
//            sendMerchantAccountAlert(merchantId, creditId, usedAmount, usedAmountPercent, dateExpect, orderNo, field);

            int businessSingleDayLimit = new BigDecimal(merchantLimit).divide(RcsConstant.HUNDRED, 2, RoundingMode.HALF_UP).intValue();
            sendMerchantLimitWarningData(merchantId, creditId, businessSingleDayLimit, usedAmountPercent, dateExpect, orderNo, field);
        }
    }

    /**
     * 向商户平台发送商户限额预警消息
     *
     * @param merchantId        商户ID
     * @param creditId          信用代理ID
     * @param usedAmount        商户已赔付金额，单位元
     * @param usedAmountPercent 商户已赔付金额达到配置的百分比
     * @param dateExpect        账务日
     * @param orderNo           订单号
     * @param flag              标志，100-达到100%，80-达到80%，60-达到60%
     */
//    private void sendMerchantAccountAlert(Long merchantId, String creditId, BigDecimal usedAmount, BigDecimal usedAmountPercent, String dateExpect, String orderNo, String flag) {
//        JSONObject json = new JSONObject();
//        json.put("merchantId", merchantId);
//        json.put("usedAmount", usedAmount);
//        json.put("usedAmountPercent", usedAmountPercent);
//        json.put("dateExpect", dateExpect);
//        json.put("timestamp", System.currentTimeMillis());
//        String tag;
//        if (StringUtils.isNotBlank(creditId)) {
//            json.put("creditId", creditId);
//            tag = String.format("%s_%s_%s_%s", dateExpect, flag, merchantId, creditId);
//        } else {
//            tag = String.format("%s_%s_%s", dateExpect, flag, merchantId);
//        }
//        producerSendMessageUtils.sendMessage("MERCHANT_ACCOUNT_ALERT", tag, orderNo, json);
//    }

    /**
     * 向风控发送商户限额预警消息
     *
     * @param merchantId             商户ID
     * @param creditId               信用代理ID
     * @param businessSingleDayLimit 商户单日限额，单位元
     * @param usedAmountPercent      商户已赔付金额达到配置的百分比
     * @param dateExpect             账务日
     * @param orderNo                订单号
     * @param flag                   标志，100-达到100%，80-达到80%，60-达到60%
     */
    private void sendMerchantLimitWarningData(Long merchantId, String creditId, Integer businessSingleDayLimit, BigDecimal usedAmountPercent, String dateExpect, String orderNo, String flag) {
        JSONObject json = new JSONObject();
        json.put("businessId", merchantId);
        json.put("businessSingleDayLimit", businessSingleDayLimit);
        json.put("amountUsed", usedAmountPercent);
        json.put("dateExpect", dateExpect);
        json.put("timestamp", System.currentTimeMillis());
        String tag;
        if (StringUtils.isNotBlank(creditId)) {
            json.put("creditId", creditId);
            tag = String.format("%s_%s_%s_%s", dateExpect, flag, merchantId, creditId);
        } else {
            tag = String.format("%s_%s_%s", dateExpect, flag, merchantId);
        }
        producerSendMessageUtils.sendMessage("rcs_merchant_limit_warning_data", tag, orderNo, json);
    }
}
