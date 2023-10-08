package com.panda.sport.rcs.credit.controller;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.credit.*;
import com.panda.sport.rcs.credit.utils.IPUtil;
import com.panda.sport.rcs.entity.dto.CreditConfigHttpQueryDto;
import com.panda.sport.rcs.entity.dto.CreditConfigRespone;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.rcs.credit.service.impl.CreditLimitApiServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "credit/merchantsCreditConfig")
public class MerchantsCreditConfigController {
    @Autowired
    private CreditLimitApiServiceImpl creditLimitApiService;

    @RequestMapping(value = "/getUserSpecialCreditLimitConfig", method = RequestMethod.POST)
    public Response<CreditConfigRespone> getUserSpecialCreditLimitConfig(@RequestBody CreditConfigHttpQueryDto configDto) {
        Long merchantId = configDto.getMerchantId();
        String creditId = configDto.getCreditId();
        Long userId = configDto.getUserId();
        if (merchantId == null || creditId == null) {
            log.info("信用网::{}::[获取用户信用限额配置]参数校验 merchantId, creditId为空请检查参数", userId);
            return Response.error(Response.FAIL, "查询参数有误：merchantId : " + merchantId + " creditId : " + creditId);
        }
        //log.info("getUserSpecialCreditLimitConfig-----获取信用限额配置查询参数查询参数-----merchantId： {}，creditId :{}, userId : {} ", merchantId, creditId, userId);
        log.info("信用网::{}::[获取用户信用限额配置] 商户ID： {}，代理ID :{}", userId, merchantId, creditId);
        Request<CreditConfigDto> request = new Request<>();
        request.setGlobalId(System.currentTimeMillis() + "");
        request.setData(configDto);
        creditLimitApiService.queryCreditLimitConfig(request);
        List<CreditSinglePlayConfigDto> playLimits = request.getData().getSinglePlayConfigList();
        List<CreditSeriesConfigDto> seriesConfigDtos = request.getData().getSeriesConfigList();
        List<CreditSinglePlayBetConfigDto> singlePlayBetConfigDtos = request.getData().getSinglePlayBetConfigList();
        //提供前端判断是否展示 “新增特殊限额”
        if (!CollectionUtils.isEmpty(playLimits) && !CollectionUtils.isEmpty(seriesConfigDtos)) {
            configDto.setHasInitSpecialFlag(Boolean.TRUE);
        } else {
            configDto.setHasInitSpecialFlag(Boolean.FALSE);
        }
        //若根据商户Id，creditId,userId 未获取到完整数据，则根据商户Id，creditId,userId = -1 在查一遍
        log.info("信用网::{}::[获取用户信用限额配置]设置userId=-1获取 商户ID： {}，代理ID :{}，用户ID:{}", userId, merchantId, creditId, -1);
        if (CollectionUtils.isEmpty(playLimits) || CollectionUtils.isEmpty(seriesConfigDtos)) {
            queryAgin(merchantId, creditId, -1L, request, playLimits, seriesConfigDtos, singlePlayBetConfigDtos);
        }
        playLimits = request.getData().getSinglePlayConfigList();
        seriesConfigDtos = request.getData().getSeriesConfigList();
        singlePlayBetConfigDtos = request.getData().getSinglePlayBetConfigList();
        log.info("getUserSpecialCreditLimitConfig-----查询数据：playLimits {}， seriesConfigDtos {} ", CollectionUtils.isEmpty(playLimits), CollectionUtils.isEmpty(seriesConfigDtos));
        if (CollectionUtils.isEmpty(playLimits) || CollectionUtils.isEmpty(seriesConfigDtos)) {
            queryAgin(0L, "0", 0L, request, playLimits, seriesConfigDtos, singlePlayBetConfigDtos);
        }
        //若还未查到对应配置，则富裕初始化值
        if (CollectionUtils.isEmpty(playLimits)) {
            for (CreditSinglePlayConfigDto playConfigDto : request.getData().getSinglePlayConfigList()) {
                playConfigDto.setValue(new BigDecimal(50000));
            }
        }
        if (CollectionUtils.isEmpty(seriesConfigDtos)) {
            request.getData().setSeriesConfigList(null);
        }
        if (CollectionUtils.isEmpty(singlePlayBetConfigDtos)) {
            request.getData().setSinglePlayBetConfigList(null);
        }
        ((CreditConfigHttpQueryDto) request.getData()).setUserId(userId);
        request.getData().setMerchantId(merchantId);
        request.getData().setCreditId(creditId);
        CreditConfigRespone responeVo = new CreditConfigRespone();
        BeanUtils.copyProperties(request.getData(), responeVo);
        return Response.success(responeVo);
    }

    private void queryAgin(Long merchantId, String creditId, Long userId, Request<CreditConfigDto> request, List<CreditSinglePlayConfigDto> playLimits, List<CreditSeriesConfigDto> seriesConfigDtos, List<CreditSinglePlayBetConfigDto> singlePlayBetConfigDtos) {
        CreditConfigHttpQueryDto defaultConfigDto = new CreditConfigHttpQueryDto();
        defaultConfigDto.setUserId(userId);
        defaultConfigDto.setMerchantId(merchantId);
        defaultConfigDto.setCreditId(creditId);
        request.setData(defaultConfigDto);
        creditLimitApiService.queryCreditLimitConfig(request);
        //若原有查询有配置值，这里保存原有值
        if (!CollectionUtils.isEmpty(playLimits)) {
            request.getData().setSinglePlayConfigList(playLimits);
        }
        if (!CollectionUtils.isEmpty(seriesConfigDtos)) {
            request.getData().setSeriesConfigList(seriesConfigDtos);
        }
        if (!CollectionUtils.isEmpty(singlePlayBetConfigDtos)) {
            request.getData().setSinglePlayBetConfigList(singlePlayBetConfigDtos);
        }
    }

    @RequestMapping(value = "/getMerchantMatchCreditConfig", method = RequestMethod.POST)
    public Response<CreditConfigHttpQueryDto> getMerchantMatchCreditConfig(@RequestBody CreditConfigHttpQueryDto configDto) {
        log.info("getMerchantMatchCreditConfig ----限额配置查询参数查询参数-----merchantId： {}，creditId :{}, userId : {} ", configDto.getMerchantId(), configDto.getCreditId(), configDto.getUserId());
        Request<CreditConfigDto> request = new Request<>();
        request.setGlobalId(System.currentTimeMillis() + "");
        request.setData(configDto);
        //用于用户中心查询用户所在代理信用限额配置
        if (checkParams4BusinessSearching(configDto)) {
            Long merchantId = creditLimitApiService.getMerchantIdByCreditId(configDto.getCreditId());
            log.info("getMerchantMatchCreditConfig---用户所在商户：merchantId {}", merchantId);
            configDto.setMerchantId(merchantId);
        }
        creditLimitApiService.queryCreditLimitConfig(request);
        List<CreditSinglePlayConfigDto> playLimits = request.getData().getSinglePlayConfigList();
        List<CreditSeriesConfigDto> seriesConfigDtos = request.getData().getSeriesConfigList();
        List<CreditSingleMatchConfigDto> matchLimits = request.getData().getSingleMatchConfigList();
        //如果是用户id参数大于，为查询用户限额配置
        if (configDto.getUserId() != null && configDto.getUserId() > 0) {
            return Response.success(configDto);
        }
        if ((CollectionUtils.isEmpty(playLimits) || CollectionUtils.isEmpty(seriesConfigDtos)) && (configDto.getMerchantId() != null && configDto.getMerchantId() != 0)) {
            CreditConfigHttpQueryDto defaultConfigDto = new CreditConfigHttpQueryDto();
            defaultConfigDto.setUserId(0L);
            defaultConfigDto.setMerchantId(0L);
            defaultConfigDto.setCreditId("0");
            request.setData(defaultConfigDto);
            creditLimitApiService.queryCreditLimitConfig(request);
            if (needSetDefaultPlayLimit(configDto) && CollectionUtils.isEmpty(playLimits)) {
                for (CreditSinglePlayConfigDto playConfigDto : request.getData().getSinglePlayConfigList()) {
                    playConfigDto.setValue(new BigDecimal(50000));
                }
                configDto.setSinglePlayConfigList(defaultConfigDto.getSinglePlayConfigList());
            }
            if (CollectionUtils.isEmpty(matchLimits)) {
                configDto.setSingleMatchConfigList(defaultConfigDto.getSingleMatchConfigList());
            }
        }
        return Response.success(configDto);
    }

    private boolean needSetDefaultPlayLimit(CreditConfigHttpQueryDto configDto) {
        if (configDto.getUserId() == null || configDto.getUserId().compareTo(0L) != 0) {
            return true;
        }
        String originalCreditId = configDto.getCreditId();
        if (StringUtils.isBlank(originalCreditId)) {
            return true;
        }
        if (StringUtils.isNotBlank(originalCreditId) && "0".equals(originalCreditId)) {
            return true;
        }
        return false;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Response<String> saveCreditLimitTemplate(@RequestBody CreditConfigHttpQueryDto configDto, HttpServletRequest requestIP) {
        log.info("保存获取信用限额配置查询参数数据： {}", JSONObject.toJSONString(configDto));
        try {
            //这个无日志操作记录
            //configDto.setIp(IPUtil.getRequestIp(requestIP));
            Request<CreditConfigHttpQueryDto> request = new Request<>();
            request.setGlobalId(System.currentTimeMillis() + "");
            request.setData(configDto);
            creditLimitApiService.saveOrUpdateCreditLimitConfig(request);
            return Response.success("商户信用额度配置保存成功！");
        } catch (Exception e) {
            return Response.fail("商户信用额度配置保存失败！");
        }
    }

    private Boolean checkParams4BusinessSearching(CreditConfigHttpQueryDto configDto) {
        if (configDto.getMerchantId() != null) {
            return Boolean.FALSE;
        }
        if (configDto.getUserId() == null || configDto.getUserId() != 0) {
            return Boolean.FALSE;
        }
        if (configDto.getCreditId() == null || "0".equals(configDto.getCreditId())) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

}
