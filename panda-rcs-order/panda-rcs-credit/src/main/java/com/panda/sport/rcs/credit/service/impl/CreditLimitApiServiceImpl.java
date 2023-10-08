package com.panda.sport.rcs.credit.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.credit.CreditConfigDto;
import com.panda.sport.data.rcs.dto.credit.CreditSeriesConfigDto;
import com.panda.sport.data.rcs.dto.credit.CreditSingleMatchConfigDto;
import com.panda.sport.data.rcs.dto.credit.CreditSinglePlayBetConfigDto;
import com.panda.sport.data.rcs.dto.credit.CreditSinglePlayConfigDto;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSeriesLimit;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSingleMatchLimit;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSinglePlayLimit;
import com.panda.sport.rcs.cache.redis.RedisUtils;
import com.panda.sport.rcs.credit.constants.CreditRedisKey;
import com.panda.sport.rcs.credit.constants.ErrorCode;
import com.panda.sport.rcs.credit.utils.CreditBizUtils;
import com.panda.sport.rcs.entity.dto.CreditConfigHttpQueryDto;
import com.panda.sport.rcs.exeception.LogicException;
import com.panda.sport.rcs.pojo.credit.RcsCreditSinglePlayBetLimit;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.wrapper.credit.RcsCreditSeriesLimitService;
import com.panda.sport.rcs.wrapper.credit.RcsCreditSingleMatchLimitService;
import com.panda.sport.rcs.wrapper.credit.RcsCreditSinglePlayBetLimitService;
import com.panda.sport.rcs.wrapper.credit.RcsCreditSinglePlayLimitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用网额度管控
 * @Author : Paca
 * @Date : 2021-05-01 13:19
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service
public class CreditLimitApiServiceImpl {

    @Autowired
    private RcsCreditSeriesLimitService rcsCreditSeriesLimitService;
    @Autowired
    private RcsCreditSingleMatchLimitService rcsCreditSingleMatchLimitService;
    @Autowired
    private RcsCreditSinglePlayBetLimitService rcsCreditSinglePlayBetLimitService;
    @Autowired
    private RcsCreditSinglePlayLimitService rcsCreditSinglePlayLimitService;

    @Autowired
    private RedisUtils redisUtils;

    public Response<CreditConfigDto> queryCreditLimitConfig(Request<CreditConfigDto> request) {
        MDC.put("X-B3-TraceId", request.getGlobalId());
        CreditConfigDto configDto = request.getData();
        if (configDto == null) {
            configDto = new CreditConfigDto();
        }
        Long merchantId = CreditBizUtils.getMerchantId(configDto.getMerchantId());
        String creditId = CreditBizUtils.getCreditId(configDto.getCreditId());
        Long userId = CreditBizUtils.getUserId(configDto);
        try {
            //串关限额
            List<RcsCreditSeriesLimit> rcsCreditSeriesLimitList = rcsCreditSeriesLimitService.querySeriesLimit(merchantId, creditId, userId);
            if (CollectionUtils.isEmpty(rcsCreditSeriesLimitList)) {
                if (userId == 0L) {
                    // 代理
                    log.info("信用网::{}::获取代理串关信用限额配置 商户ID： {}，代理ID :{}，用户ID：{}", userId, merchantId, creditId, userId);
                    rcsCreditSeriesLimitList = rcsCreditSeriesLimitService.querySeriesLimit(0L, "-1", userId);
                } else if (userId == -1L) {
                    // 用户通用
                    log.info("信用网::{}::获取用户通用串关信用限额配置 商户ID： {}，代理ID :{}，用户ID：{}", userId, merchantId, creditId, userId);
                    rcsCreditSeriesLimitList = rcsCreditSeriesLimitService.querySeriesLimit(0L, "-1", userId);
                } else {
                    //首先查询用户通用限额
                    rcsCreditSeriesLimitList = rcsCreditSeriesLimitService.querySeriesLimit(merchantId, creditId, -1L);
                    if (CollectionUtils.isEmpty(rcsCreditSeriesLimitList)) {
                        //如果用户通用限额为空，则查询用户通用默认限额
                        log.info("信用网::{}::获取用户默认串关信用限额配置 商户ID： {}，代理ID :{}，用户ID：{}", userId, merchantId, creditId, userId);
                        rcsCreditSeriesLimitList = rcsCreditSeriesLimitService.querySeriesLimit(0L, "-1", -1L);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(rcsCreditSeriesLimitList)) {
                log.info("信用网::{}::获取用户串关信用限额配置 商户ID： {}，代理ID :{}，用户ID：{}", userId, merchantId, creditId, userId);
                List<CreditSeriesConfigDto> seriesConfigList = rcsCreditSeriesLimitList.stream().map(CreditBizUtils::toCreditSeriesConfigDto).collect(Collectors.toList());
                configDto.setSeriesConfigList(seriesConfigList);
            }

            //赛事限额
            if (userId == 0L) {
                // 代理维度才有赛事单场限额
                List<RcsCreditSingleMatchLimit> rcsCreditSingleMatchLimitList = rcsCreditSingleMatchLimitService.querySingleMatchLimit(merchantId, creditId);
                if (CollectionUtils.isEmpty(rcsCreditSingleMatchLimitList)) {
                    log.info("信用网::{}::获取代理默认赛事单场限额 商户ID： {}，代理ID :{}，用户ID：{}", userId, merchantId, creditId, userId);
                    rcsCreditSingleMatchLimitList = rcsCreditSingleMatchLimitService.querySingleMatchLimit(0L, "-1");
                }
                if (CollectionUtils.isNotEmpty(rcsCreditSingleMatchLimitList)) {
                    log.info("信用网::{}::获取代理赛事单场限额 商户ID： {}，代理ID :{}，用户ID：{}", userId, merchantId, creditId, userId);
                    List<CreditSingleMatchConfigDto> singleMatchConfigList = rcsCreditSingleMatchLimitList.stream().map(CreditBizUtils::toCreditSingleMatchConfigDto).collect(Collectors.toList());
                    configDto.setSingleMatchConfigList(singleMatchConfigList);
                }
            }

            //玩法限额
            List<RcsCreditSinglePlayLimit> rcsCreditSinglePlayLimitList = rcsCreditSinglePlayLimitService.querySinglePlayLimit(merchantId, creditId, userId);
            if (CollectionUtils.isEmpty(rcsCreditSinglePlayLimitList)) {
                if (userId == 0L) {
                    // 代理
                    log.info("信用网::{}::获取代理默认单场玩法限额 商户ID： {}，代理ID :{}，用户ID：{}", userId, merchantId, creditId, userId);
                    rcsCreditSinglePlayLimitList = rcsCreditSinglePlayLimitService.querySinglePlayLimit(0L, "-1", userId);
                } else if (userId == -1L) {
                    // 用户通用
                    log.info("信用网::{}::获取用户通用单场玩法限额 商户ID： {}，代理ID :{}，用户ID：{}", userId, merchantId, creditId, userId);
                    rcsCreditSinglePlayLimitList = rcsCreditSinglePlayLimitService.querySinglePlayLimit(0L, "-1", userId);
                } else {
                    //首先查询用户通用限额
                    rcsCreditSinglePlayLimitList = rcsCreditSinglePlayLimitService.querySinglePlayLimit(merchantId, creditId, -1L);
                    if (CollectionUtils.isEmpty(rcsCreditSinglePlayLimitList)) {
                        //如果用户通用限额为空，则查询用户通用默认限额
                        log.info("信用网::{}::获取用户默认单场玩法限额 商户ID： {}，代理ID :{}，用户ID：{}", userId, merchantId, creditId, userId);
                        rcsCreditSinglePlayLimitList = rcsCreditSinglePlayLimitService.querySinglePlayLimit(0L, "-1", -1L);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(rcsCreditSinglePlayLimitList)) {
                log.info("信用网::{}::获取用户单场玩法限额 商户ID： {}，代理ID :{},，用户ID：{}", userId, merchantId, creditId, userId);
                List<CreditSinglePlayConfigDto> singlePlayConfigList = rcsCreditSinglePlayLimitList.stream().map(CreditBizUtils::toCreditSinglePlayConfigDto).collect(Collectors.toList());
                configDto.setSinglePlayConfigList(singlePlayConfigList);
            }

            //单注限额
            if (userId == -1L || userId > 0L) {
                // 用户维度才有玩法单注限额
                List<RcsCreditSinglePlayBetLimit> singlePlayBetLimitList = rcsCreditSinglePlayBetLimitService.querySinglePlayBetLimit(merchantId, creditId, userId);
                if (CollectionUtils.isEmpty(singlePlayBetLimitList)) {
                    if (userId == -1L) {
                        // 用户通用
                        log.info("信用网::{}::获取用户通用玩法单注限额 商户ID： {}，代理ID :{}，用户ID：{}", userId, merchantId, creditId, userId);
                        singlePlayBetLimitList = rcsCreditSinglePlayBetLimitService.querySinglePlayBetLimit(0L, "-1", userId);
                    } else {
                        //首先查询用户通用限额
                        singlePlayBetLimitList = rcsCreditSinglePlayBetLimitService.querySinglePlayBetLimit(merchantId, creditId, -1L);
                        if (CollectionUtils.isEmpty(singlePlayBetLimitList)) {
                            //如果用户通用限额为空，则查询用户通用默认限额
                            log.info("信用网::{}::获取用户默认玩法单注限额 商户ID： {}，代理ID :{}，用户ID：{}", userId, merchantId, creditId, userId);
                            singlePlayBetLimitList = rcsCreditSinglePlayBetLimitService.querySinglePlayBetLimit(0L, "-1", -1L);
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(singlePlayBetLimitList)) {
                    log.info("信用网::{}::获取用户玩法单注限额 商户ID： {}，代理ID :{}", userId, merchantId, creditId);
                    List<CreditSinglePlayBetConfigDto> singlePlayBetConfigList = singlePlayBetLimitList.stream().map(CreditBizUtils::toCreditSinglePlayBetConfigDto).collect(Collectors.toList());
                    configDto.setSinglePlayBetConfigList(singlePlayBetConfigList);
                }
            }
            log.info("信用网:查询信用网限额模板返回结果：{}", JSON.toJSONString(configDto));
            return Response.success(configDto);
        } catch (Exception e) {
            log.error("信用网:查询信用网限额模板异常", e);
            return Response.error(ErrorCode.CONFIG_QUERY_EXCEPTION, "查询信用网限额模板异常：" + e.getMessage(), configDto);
        }
    }

    public Response<Boolean> saveOrUpdateCreditLimitConfig(Request<CreditConfigHttpQueryDto> request) {
        MDC.put("X-B3-TraceId", request.getGlobalId());
        try {
            CreditConfigHttpQueryDto configDto = request.getData();
            Long merchantId = configDto.getMerchantId() == null ? 0L : configDto.getMerchantId();
            String creditId = configDto.getCreditId() == null ? "0" : configDto.getCreditId();
            Long userId = CreditBizUtils.getUserId(configDto);
            log.info("信用网::{}::限额模板配置处理开始 商户ID： {}，代理ID :{}", userId, merchantId, creditId);
            List<RcsCreditSeriesLimit> creditSeriesLimitList = CreditBizUtils.getCreditSeriesLimitList(configDto);
            if (CollectionUtils.isNotEmpty(creditSeriesLimitList)) {
                rcsCreditSeriesLimitService.batchInsertOrUpdate(creditSeriesLimitList);
                log.info("信用网::{}::限额模板配置-串关限额配置入库完成 商户ID： {}，代理ID :{}", request.getData().getUserId(), request.getData().getMerchantId(), request.getData().getCreditId());
            }
            List<RcsCreditSingleMatchLimit> creditSingleMatchLimit = CreditBizUtils.getCreditSingleMatchLimit(configDto);
            if (CollectionUtils.isNotEmpty(creditSingleMatchLimit)) {
                rcsCreditSingleMatchLimitService.batchInsertOrUpdate(creditSingleMatchLimit);
                log.info("saveOrUpdateCreditLimitConfig ::: 信用网限额模板单场赛事配置处理完成!");
            }
            List<RcsCreditSinglePlayLimit> creditSinglePlayLimit = CreditBizUtils.getCreditSinglePlayLimit(configDto);
            if (CollectionUtils.isNotEmpty(creditSinglePlayLimit)) {
                rcsCreditSinglePlayLimitService.batchInsertOrUpdate(creditSinglePlayLimit);
                log.info("saveOrUpdateCreditLimitConfig ::: 信用网限额模板玩法配置处理完成!");
            }
            List<RcsCreditSinglePlayBetLimit> singlePlayBetLimitList = CreditBizUtils.getCreditSinglePlayBetLimit(configDto);
            if (CollectionUtils.isNotEmpty(singlePlayBetLimitList)) {
                rcsCreditSinglePlayBetLimitService.batchInsertOrUpdate(singlePlayBetLimitList);
                log.info("saveOrUpdateCreditLimitConfig");
            }
            log.info("saveOrUpdateCreditLimitConfig ::: 信用网限额模板配置入库完成");

            // 缓存用户配置
            userLimitCache(configDto);
            return Response.success(true);
        } catch (LogicException e) {
            return Response.error(NumberUtils.toInt(e.getCode()), "保存或更新用户配置、用户通用配置异常：" + e.getMessage(), false);
        } catch (Exception e) {
            return Response.error(ErrorCode.CONFIG_UPDATE_EXCEPTION, "保存或更新信用限额配置异常：" + e.getMessage(), false);
        }
    }

    /**
     * 缓存用户配置
     * <li>merchantId = mid && creditId = cid && userId = -1    用户通用配置（每个信用代理一套通用配置）</li>
     * <li>merchantId = mid && creditId = cid && userId = uid  用户配置</li>
     *
     * @param queryDto
     */
    private void userLimitCache(CreditConfigHttpQueryDto queryDto) {
        final Long merchantId = queryDto.getMerchantId();
        final String creditId = queryDto.getCreditId();
        final Long userId = queryDto.getUserId() == null ? -1L : queryDto.getUserId();
        if (merchantId == null || StringUtils.isBlank(creditId)) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "设置用户限额，商户ID和信用代理ID不能为空");
        }

        List<CreditSeriesConfigDto> seriesConfigList = queryDto.getSeriesConfigList();
        if (CollectionUtils.isNotEmpty(seriesConfigList)) {
            Map<String, String> hashMap = seriesConfigList.stream().collect(Collectors.toMap(config -> String.valueOf(config.getSeriesType()), config -> CommonUtils.toString(config.getValue())));
            String key = CreditRedisKey.Limit.getUserSeriesKey(merchantId, creditId, userId);
            redisUtils.hmset(key, hashMap);
            log.info("信用额度，用户串关限额缓存：key={},hashMap={}", key, hashMap);
        }
        List<CreditSinglePlayConfigDto> singlePlayConfigList = queryDto.getSinglePlayConfigList();
        if (CollectionUtils.isNotEmpty(singlePlayConfigList)) {
            Map<Integer, List<CreditSinglePlayConfigDto>> mapBySportId = singlePlayConfigList.stream().collect(Collectors.groupingBy(CreditSinglePlayConfigDto::getSportId));
            mapBySportId.forEach((sportId, playClassifyList) -> {
                Map<Integer, List<CreditSinglePlayConfigDto>> mapByPlayClassify = playClassifyList.stream().collect(Collectors.groupingBy(CreditSinglePlayConfigDto::getPlayClassify));
                mapByPlayClassify.forEach((playClassify, betStageList) -> {
                    Map<String, List<CreditSinglePlayConfigDto>> mapByBetStage = betStageList.stream().collect(Collectors.groupingBy(CreditSinglePlayConfigDto::getBetStage));
                    mapByBetStage.forEach((betStage, list) -> {
                        Map<String, String> hashMap = list.stream().collect(Collectors.toMap(config -> String.valueOf(config.getTournamentLevel()), config -> CommonUtils.toString(config.getValue())));
                        String key = CreditRedisKey.Limit.getUserSinglePlayKey(merchantId, creditId, userId, sportId, playClassify, betStage);
                        redisUtils.hmset(key, hashMap);
                        log.info("信用额度，用户玩法累计限额缓存：key={},hashMap={}", key, hashMap);
                    });
                });
            });
        }
        List<CreditSinglePlayBetConfigDto> singlePlayBetConfigList = queryDto.getSinglePlayBetConfigList();
        if (CollectionUtils.isNotEmpty(singlePlayBetConfigList)) {
            Map<Integer, List<CreditSinglePlayBetConfigDto>> mapBySportId = singlePlayBetConfigList.stream().collect(Collectors.groupingBy(CreditSinglePlayBetConfigDto::getSportId));
            mapBySportId.forEach((sportId, playClassifyList) -> {
                Map<Integer, List<CreditSinglePlayBetConfigDto>> mapByPlayClassify = playClassifyList.stream().collect(Collectors.groupingBy(CreditSinglePlayBetConfigDto::getPlayClassify));
                mapByPlayClassify.forEach((playClassify, betStageList) -> {
                    Map<String, List<CreditSinglePlayBetConfigDto>> mapByBetStage = betStageList.stream().collect(Collectors.groupingBy(CreditSinglePlayBetConfigDto::getBetStage));
                    mapByBetStage.forEach((betStage, list) -> {
                        Map<String, String> hashMap = list.stream().collect(Collectors.toMap(config -> String.valueOf(config.getTournamentLevel()), config -> CommonUtils.toString(config.getValue())));
                        String key = CreditRedisKey.Limit.getUserSinglePlayBetKey(merchantId, creditId, userId, sportId, playClassify, betStage);
                        redisUtils.hmset(key, hashMap);
                        log.info("信用额度，用户玩法单注限额缓存：key={},hashMap={}", key, hashMap);
                    });
                });
            });
        }
    }

    public Long getMerchantIdByCreditId(String creidtId) {
        return rcsCreditSeriesLimitService.getMerchantIdByCreditId(creidtId);
    }

    public static void main(String[] args) {
        JSON.toJSONString(null);
    }
}
