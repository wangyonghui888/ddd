package com.panda.sport.rcs.task.job;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.pojo.RcsCurrencyRate;
import com.panda.sport.rcs.task.utils.HttpUtils;
import com.panda.sport.rcs.task.wrapper.RcsCurrencyRateService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

/**
 * @Description: 更新汇率
 * @Author: Vecotr
 * @Date: 2019/12/20
 */
@JobHandler(value = "currencyRateJobHandler")
@Component
@Slf4j
public class CurrencyRateJobHandler extends IJobHandler {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RcsCurrencyRateService rcsCurrencyRateService;

    private String RCS_CURRENCY_RATE = RedisKeys.RCS_CURRENCY_RATE;

    @Value("${rates.appId}")
    private String appId;

    public static final String url = "https://openexchangerates.org/api/latest.json?app_id=";

    @Override
    public ReturnT<String> execute(String s) {
        try {
            String newUrl = url + appId;
            String request = HttpUtils.getRequest(newUrl);
            if (StringUtils.isBlank(request)) {
                return SUCCESS;
            }
            log.info("汇率入库1:{}", JsonFormatUtils.toJson(request));
            JSONObject json = (JSONObject) JSONObject.parse(request);
            Map map = JSONObject.toJavaObject(json, Map.class);
            log.info("汇率入库2:{}", JsonFormatUtils.toJson(map));
            if (map != null) {
                Map<String, Object> rates = (Map<String, Object>) map.get("rates");
                ArrayList<RcsCurrencyRate> rcsCurrencyRates = new ArrayList<>();
                rates.forEach((k, v) -> {
                    RcsCurrencyRate rcsCurrencyRate = new RcsCurrencyRate();
                    rcsCurrencyRate.setCurrencyCode(k);
                    rcsCurrencyRate.setRate(new BigDecimal(String.valueOf(v)));
                    rcsCurrencyRate.setCreateTime(System.currentTimeMillis());
                    rcsCurrencyRate.setModifyTime(System.currentTimeMillis());
                    rcsCurrencyRates.add(rcsCurrencyRate);
                    redisClient.set(String.format(RCS_CURRENCY_RATE, k), String.valueOf(v));
                });
                rcsCurrencyRateService.batchSaveOrUpdate(rcsCurrencyRates);
            } else {
                log.error("货币汇率为空");
            }
        } catch (Exception e) {
            log.error("货币汇率任务错误" + e.getMessage(), e);
        }
        return SUCCESS;
    }


}
