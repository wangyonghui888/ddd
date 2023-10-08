package com.panda.sport.rcs.mgr.controller;

import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.common.NumberUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.BusinessDayPaidStatus;
import com.panda.sport.rcs.mapper.RcsBusinessConfigMapper;
import com.panda.sport.rcs.mgr.wrapper.RcsBusinessDayPaidConfigService;
import com.panda.sport.rcs.mgr.wrapper.RcsCodeService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsBusinessDayPaidConfig;
import com.panda.sport.rcs.pojo.RcsCode;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.panda.sport.rcs.constants.RedisKeys.*;
import static com.panda.sport.rcs.mgr.mq.impl.BasicConfigProvider.msgConfTag;
import static com.panda.sport.rcs.mgr.wrapper.impl.RcsPaidConfigServiceImp.BUS_DAY_CONFIG_KEY;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.bootstrap.controller
 * @Description :  TODO
 * @Date: 2019-10-07 10:32
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@RestController
@RequestMapping(value = "businessDayPaid")
public class RcsBusinessDayPaidConfigController {

    @Autowired
    private RcsBusinessDayPaidConfigService rcsBusinessDayPaidConfigService;
    @Autowired
    private RcsBusinessConfigMapper rcsBusinessConfigMapper;

    @Autowired
    private RcsCodeService rcsCodeService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    ProducerSendMessageUtils sendMessage;
    private static final double MIN = 0.01;
    private static final double MAX = 1000;
    private static final double GAO_WEI_MAX = 100;

    @RequestMapping(value = "getList")
    public HttpResponse<List<RcsBusinessDayPaidConfig>> getRcsBusinessPlayPaidConfigList() {
        List<RcsBusinessDayPaidConfig> rcsBusinessDayPaidConfigs = rcsBusinessDayPaidConfigService.queryBusDayConifgs();
        rcsBusinessDayPaidConfigs.stream().forEach(model -> {
            RcsCode rcsCode = rcsCodeService.getBusiness(model.getBusinessId());
            if (rcsCode != null) {
                model.setBusinessName(rcsCode.getChildKey());
            }
            if (BusinessDayPaidStatus.OVERLOAD.getCode().equals(model.getStatus())) {
                model.setStatus(getDayPaidStatus(model));
            }
        });
        log.info("::businessDayPaid:: getList 输出参数  {}",rcsBusinessDayPaidConfigs.size());
        return HttpResponse.success(rcsBusinessDayPaidConfigs);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public HttpResponse update(@RequestBody RcsBusinessDayPaidConfig rcsBusinessDayPaidConfig) {
         log.info("::businessDayPaid{}:: 输入参数{}",rcsBusinessDayPaidConfig.getBusinessId(),rcsBusinessDayPaidConfig);
        try {

            Long dayPaid = rcsCodeService.getRcsCodeList("amountSet", "dayPaid");
            BigDecimal bigDecimal = new BigDecimal(dayPaid);
            double stopRate = rcsBusinessDayPaidConfig.getStopRate().doubleValue();
            if (stopRate < MIN || stopRate > MAX) {
                return HttpResponse.fail("制动比例超过范围");
            }
            double warnLevel1Rate = rcsBusinessDayPaidConfig.getWarnLevel1Rate().doubleValue();
            if (warnLevel1Rate < MIN || warnLevel1Rate > GAO_WEI_MAX) {
                return HttpResponse.fail("高危比例超过范围");
            }
            double warnLevel2Rate = rcsBusinessDayPaidConfig.getWarnLevel2Rate().doubleValue();
            if (warnLevel2Rate < MIN || warnLevel2Rate > GAO_WEI_MAX) {
                return HttpResponse.fail("危险比例超过范围");
            }
            rcsBusinessDayPaidConfig.setStopVal(bigDecimal.multiply(rcsBusinessDayPaidConfig.getStopRate().divide(new BigDecimal(100))));
            rcsBusinessDayPaidConfig.setWarnLevel1Val(rcsBusinessDayPaidConfig.getStopVal().multiply(rcsBusinessDayPaidConfig.getWarnLevel1Rate().divide(new BigDecimal(100))));
            rcsBusinessDayPaidConfig.setWarnLevel2Val(rcsBusinessDayPaidConfig.getStopVal().multiply(rcsBusinessDayPaidConfig.getWarnLevel2Rate().divide(new BigDecimal(100))));
            if (BusinessDayPaidStatus.MANUAL.getCode().equals(rcsBusinessDayPaidConfig.getStatus())) {
                rcsBusinessDayPaidConfig.setExpireTime(DateUtils.getTomorrowMidDay());
            }
            rcsBusinessDayPaidConfigService.updateRcsBusinessDayPaidConfig(rcsBusinessDayPaidConfig);
        } catch (Exception e) {
            log.error("::RcsBusinessDayPaidConfig{}:: Update ERROR{}",rcsBusinessDayPaidConfig.getBusinessId(),e.getMessage(), e);
            return HttpResponse.fail("修改失败");
        }
        if (redisClient.exist(PAID_CONFIG_REDIS_CACHE + "BusDay")) {
            redisClient.delete(PAID_CONFIG_REDIS_CACHE + "BusDay");
        }

        sendMessage.sendMessage(msgConfTag, BUS_DAY_CONFIG_KEY, rcsBusinessDayPaidConfig.getBusinessId().toString(), rcsBusinessDayPaidConfig);

        return HttpResponse.success("修改成功");
    }

    @RequestMapping(value = "getDayPaid")
    public HttpResponse<RcsBusinessDayPaidConfig> getDayPaid(@RequestParam("businessId") long businessId) {
        return HttpResponse.success(rcsBusinessDayPaidConfigService.getDayPaid(businessId));
    }

    @RequestMapping(value = "getRedisBus")
    public HttpResponse getRedisBus(@RequestParam("businessId") long businessId) {
        String redisKey = String.format(PAID_DATE_BUS_REDIS_CACHE, DateUtils.getTimeExpect(System.currentTimeMillis()), businessId);
        BigDecimal dayPaid = NumberUtils.getBigDecimal(redisClient.get(redisKey));
        return HttpResponse.success(dayPaid);
    }

    @RequestMapping(value = "getBusValue")
    public HttpResponse getBusValue(@RequestParam("businessId") long businessId) {
        String redisKey = String.format(PAID_DATE_BUS_STOP_REDIS_CACHE, DateUtils.getTimeExpect(System.currentTimeMillis()), businessId);
        BigDecimal dayPaid = NumberUtils.getBigDecimal(redisClient.get(redisKey));
        return HttpResponse.success(dayPaid);
    }

    @RequestMapping(value = "updateRedisBus")
    public HttpResponse updateRedisBus(@RequestParam("businessId") long businessId, @RequestParam("busValue") BigDecimal busValue) {
        String redisKey = "";
        try {
            redisKey = String.format(PAID_DATE_BUS_REDIS_CACHE, DateUtils.getTimeExpect(System.currentTimeMillis()), businessId);
            redisClient.set(redisKey, busValue);
        } catch (Exception e) {
            log.error("::updateRedisBus{}:: 修改redis值：{}失败{}",businessId,redisKey,e.getMessage(),e);
            return HttpResponse.fail("修改失败");
        }
        return HttpResponse.success("修改成功");
    }

    public Integer getDayPaidStatus(RcsBusinessDayPaidConfig rcsBusinessDayPaidConfig) {
        //从redis中获取商户当日的最大赔付
        String redisKey = String.format(PAID_DATE_BUS_REDIS_CACHE, DateUtils.getTimeExpect(System.currentTimeMillis(), "yyyy-MM-dd"), rcsBusinessDayPaidConfig.getBusinessId());
        BigDecimal dayPaid = NumberUtils.getBigDecimal(redisClient.get(redisKey)).divide(new BigDecimal("100"), 2, RoundingMode.FLOOR);
        if (dayPaid.compareTo(rcsBusinessDayPaidConfig.getStopVal()) >= 0) {
            return BusinessDayPaidStatus.OVERLOAD.getCode();
        }
        if (dayPaid.compareTo(rcsBusinessDayPaidConfig.getWarnLevel1Val()) >= 0) {
            return BusinessDayPaidStatus.HIGHRISK.getCode();
        }
        if (dayPaid.compareTo(rcsBusinessDayPaidConfig.getWarnLevel2Val()) >= 0) {
            return BusinessDayPaidStatus.DANGER.getCode();
        }
        if (dayPaid.compareTo(BigDecimal.ZERO) >= 0) {
            return BusinessDayPaidStatus.NORMAL.getCode();
        }
        return BusinessDayPaidStatus.HEALTH.getCode();
    }

}
