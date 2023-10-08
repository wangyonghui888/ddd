package com.panda.sport.rcs.limit.controller;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.ImmutableMap;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.limit.service.MerchantLimitServiceImpl;
import com.panda.sport.rcs.limit.vo.AvailableLimitQueryReqVo;
import com.panda.sport.rcs.limit.vo.MerchantAvailableLimitResVo;
import com.panda.sport.rcs.limit.vo.MerchantDailyLimitReqVo;
import com.panda.sport.rcs.limit.vo.MerchantDailyLimitResVo;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import com.panda.sport.rcs.wrapper.limit.RcsQuotaBusinessLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 商户限额
 * @Author : Paca
 * @Date : 2021-11-27 14:41
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@RestController
@RequestMapping("/limit/merchant")
public class MerchantLimitController {

    @Autowired
    private RcsQuotaBusinessLimitService rcsQuotaBusinessLimitService;

    @Autowired
    private MerchantLimitServiceImpl merchantLimitService;

    /**
     * 商户单日限额监控
     *
     * @param reqVo
     * @return
     */
    @PostMapping("/dailyLimitMonitor")
    public HttpResponse<List<MerchantDailyLimitResVo>> dailyLimitMonitor(@RequestBody MerchantDailyLimitReqVo reqVo) {
        List<RcsQuotaBusinessLimit> list = rcsQuotaBusinessLimitService.listByBusinessIds(reqVo.getBusinessIds());
        if (CollectionUtils.isEmpty(list)) {
            return HttpResponse.failToMsg("未查询到商户信息");
        }
        int percentage90 = 0;
        int percentage90Series = 0;
        List<MerchantDailyLimitResVo> resultList = new ArrayList<>(list.size());
        for (RcsQuotaBusinessLimit config : list) {
            // 单关已用额度
            BigDecimal businessUsedLimit = merchantLimitService.getBusinessDailyUsedLimit(System.currentTimeMillis(), config.getBusinessId());
            // 已用额度 / 配置额度 = 已用比例
            BigDecimal percentage = businessUsedLimit.divide(new BigDecimal(config.getBusinessSingleDayLimit()), 4, RoundingMode.HALF_UP);
            if (percentage.compareTo(new BigDecimal("0.9")) >= 0) {
                percentage90++;
            }

            // 串关已用额度
            BigDecimal businessSeriesUsedLimit = merchantLimitService.getBusinessSeriesDailyUsedLimit(System.currentTimeMillis(), config.getBusinessId());
            // 如果为空 ，默认为单关的一半
            if (config.getBusinessSingleDaySeriesLimit() == null) {
                config.setBusinessSingleDaySeriesLimit(config.getBusinessSingleDayLimit() / 2);
            }
            // 已用额度 / 配置额度 = 已用比例
            BigDecimal percentageSeries = businessSeriesUsedLimit.divide(new BigDecimal(config.getBusinessSingleDaySeriesLimit()), 4, RoundingMode.HALF_UP);
            if (percentageSeries.compareTo(new BigDecimal("0.9")) >= 0) {
                percentage90Series++;
            }
            if (percentage.compareTo(reqVo.getPercentageOfUsedQuota()) >= 0 || percentageSeries.compareTo(reqVo.getPercentageOfUsedQuota()) >= 0) {
                MerchantDailyLimitResVo resVo = new MerchantDailyLimitResVo();
                resVo.setBusinessId(config.getBusinessId());
                resVo.setBusinessName(config.getBusinessName());
                resVo.setDailyLimit(config.getBusinessSingleDayLimit());
                resVo.setBusinessUsedLimit(businessUsedLimit);
                resVo.setPercentageOfUsedQuota(percentage);

                resVo.setDailySeriesLimit(config.getBusinessSingleDaySeriesLimit());
                resVo.setBusinessSeriesUsedLimit(businessSeriesUsedLimit);
                resVo.setPercentageSeriesOfUsedQuota(percentageSeries);
                resultList.add(resVo);
            }
        }

        return HttpResponse.success(resultList, ImmutableMap.of("percentage90", percentage90, "percentage90Series", percentage90Series));
    }

    /**
     * 商户可用额度查询
     *
     * @param reqVo
     * @return
     */
    @PostMapping("/availableLimitQuery")
    public HttpResponse<MerchantAvailableLimitResVo> availableLimitQuery(@RequestBody AvailableLimitQueryReqVo reqVo) {
            String linkId = CommonUtils.mdcPut();
        try {
            MerchantAvailableLimitResVo resVo = merchantLimitService.getMerchantAvailableLimit(reqVo);
            return HttpResponse.success(resVo, linkId);
        } catch (RcsServiceException e) {
            return HttpResponse.failToMsg(e.getErrorMassage(), linkId);
        } catch (Exception e) {
            return HttpResponse.failToMsg("商户可用额度查询异常", linkId);
        } finally {
            CommonUtils.mdcRemove();
        }
    }
}
