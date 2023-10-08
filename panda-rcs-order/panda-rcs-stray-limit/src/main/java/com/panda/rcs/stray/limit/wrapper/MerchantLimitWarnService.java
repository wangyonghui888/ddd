package com.panda.rcs.stray.limit.wrapper;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.stray.limit.entity.constant.RedisKeyConstant;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
@Slf4j
@Service
public class MerchantLimitWarnService {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    /**
     * 商户限额预警
     *
     * @param merchantId  商户ID
     * @param usedLimit   商户已用额度
     * @param configLimit 商户单日限额配置
     * @param dateExpect  结算时间账务日
     * @param orderNo     订单号
     * @param warnType    预警类型，0-商户单日总限额预警，1-商户单日串关限额预警
     */
    public void sendMsg(Long merchantId, Long usedLimit, Long configLimit, String dateExpect, String orderNo, Integer warnType, String busName) {
        BigDecimal usedAmountPercent = new BigDecimal(usedLimit).divide(new BigDecimal(configLimit), 4, RoundingMode.HALF_UP);
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
        log.info("商户限额预警：merchantId={},usedLimit={},configLimit={},dateExpect={},orderNo={},field={},warnType={}", merchantId, usedLimit, configLimit, dateExpect, orderNo, field, warnType);
        String signKey;
        if (NumberUtils.INTEGER_ONE.equals(warnType)) {
            signKey = RedisKeyConstant.getMerchantSeriesLimitWarnSignKey(dateExpect, merchantId);
        } else {
            signKey = RedisKeyConstant.getMerchantLimitWarnSignKey(dateExpect, merchantId);
        }
        // 每个百分比只预警一次
        String sign = redisClient.hGet(signKey, field);
        if (!"1".equals(sign)) {
            redisClient.hSet(signKey, field, "1");
            redisClient.expireKey(signKey, 7 * 24 * 60 * 60);
            int businessSingleDayLimit = new BigDecimal(configLimit).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP).intValue();
            sendMerchantLimitWarningData(merchantId, businessSingleDayLimit, usedAmountPercent, dateExpect, orderNo, field, warnType);
        }
        //推送mango/telegram 商户预警  需求-2088
        merchantOverDailyLimit(field, dateExpect, warnType, String.valueOf(merchantId), busName, configLimit,
                usedLimit, usedAmountPercent.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP).toPlainString());
    }

    @Value("${spring.profiles.active:pro}")
    private String env;

    private void merchantOverDailyLimit(String rateKey, String dateExpect, Integer warnType, String merchantsId, String merchantsName, Long limit, Long used, String rate) {
        String MERCHANT_DAILY_ALERT_KEY = "rcs:sdk:over:num:daily:%s:warnType:%s:merchantsId:%s";
        String alertKey = String.format(MERCHANT_DAILY_ALERT_KEY, dateExpect, warnType, merchantsId) + ":" + rateKey;
        Long num = redisClient.incrBy(alertKey, 1L);
        redisClient.expireKey(alertKey, 24 * 60 * 60);
        log.info("::{}::商户单日串关限额超出发送预警信息,key:{},num:{}", merchantsId, alertKey, num);
        if (("100".equals(rateKey) && num <= 2) || ("80".equals(rateKey) && num <= 1)) {
            List<String> list = new ArrayList<>();
            DecimalFormat format = new DecimalFormat("#,###");
            String str = null;
            if (warnType == 0) {
                str = "[AlertType]：MerchantOverSingleDayLimit商户单日单关限额告警\n" +
                        "[Env]：" + env + "\n" +
                        "[Alert Time]：" + DateUtils.transferLongToDateStrings(System.currentTimeMillis()) + "\n" +
                        "[Merchant ID]：" + merchantsId + "\n" +
                        "[Merchant Name]：" + merchantsName + "\n" +
                        "[MerchantSingleDayLimit]：" + format.format(new BigDecimal(limit).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)) + "\n" +
                        "[MerchantSingleDayLimitUsed]：" + format.format(new BigDecimal(used).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)) + "\n" +
                        "[MerchantSingleDayLimitUsed%]：" + rate + "%";
            } else {
                str = "[AlertType]：MerchantOverParlayDayLimit商户单日串关限额告警\n" +
                        "[Env]：" + env + "\n" +
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
            producerSendMessageUtils.sendMessage("PA_COMMON_WARN_INFO", warnType == 0 ? "MerchantOverSingleDayLimit" : "MerchantOverParlayDayLimit", job, param);
        }
    }


    /**
     * 向风控发送商户限额预警消息
     *
     * @param merchantId             商户ID
     * @param businessSingleDayLimit 商户单日限额配置，单位元
     * @param usedAmountPercent      商户已赔付金额达到配置的百分比
     * @param dateExpect             账务日
     * @param orderNo                订单号
     * @param flag                   标志，100-达到100%，80-达到80%，60-达到60%
     * @param warnType               预警类型，0-商户单日总限额预警，1-商户单日串关限额预警
     */
    private void sendMerchantLimitWarningData(Long merchantId, Integer businessSingleDayLimit, BigDecimal usedAmountPercent, String dateExpect, String orderNo, String flag, Integer warnType) {
        JSONObject json = new JSONObject();
        json.put("businessId", merchantId);
        json.put("businessSingleDayLimit", businessSingleDayLimit);
        json.put("amountUsed", usedAmountPercent);
        json.put("dateExpect", dateExpect);
        json.put("timestamp", System.currentTimeMillis());
        json.put("warnType", warnType);
        producerSendMessageUtils.sendMessage("rcs_merchant_limit_warning_data", flag + "_" + orderNo, dateExpect + "_" + merchantId, json);
    }
}
