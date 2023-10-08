package com.panda.sport.rcs.credit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.credit.CreditLimitApiService;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.credit.CreditAgentInfoDto;
import com.panda.sport.data.rcs.dto.credit.CreditConfigDto;
import com.panda.sport.data.rcs.dto.credit.CreditConfigSaveDto;
import com.panda.sport.data.rcs.dto.credit.CreditSeriesConfigDto;
import com.panda.sport.data.rcs.dto.credit.CreditSingleMatchConfigDto;
import com.panda.sport.data.rcs.dto.credit.CreditSinglePlayConfigDto;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSeriesLimit;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSingleMatchLimit;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSinglePlayLimit;
import com.panda.sport.rcs.cache.redis.RedisUtils;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.credit.constants.CreditRedisKey;
import com.panda.sport.rcs.credit.constants.ErrorCode;
import com.panda.sport.rcs.credit.matrix.MatrixAdapter;
import com.panda.sport.rcs.credit.matrix.MatrixBean;
import com.panda.sport.rcs.credit.matrix.ParamValidateService;
import com.panda.sport.rcs.credit.service.CreditLimitService;
import com.panda.sport.rcs.credit.utils.CreditBizUtils;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.exeception.LogicException;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.service.IRcsOperateMerchantsSetService;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.vo.RcsBusinessPlayPaidConfigVo;
import com.panda.sport.rcs.wrapper.credit.RcsCreditSeriesLimitService;
import com.panda.sport.rcs.wrapper.credit.RcsCreditSingleMatchLimitService;
import com.panda.sport.rcs.wrapper.credit.RcsCreditSinglePlayLimitService;
import com.panda.sport.rcs.wrapper.limit.RcsQuotaBusinessLimitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用网额度管控 api dubbo 接口实现
 * @Author : Paca
 * @Date : 2021-05-14 17:21
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service(connections = 5, retries = 0, timeout = 3000)
@Slf4j
@org.springframework.stereotype.Service
public class CreditApiServiceImpl implements CreditLimitApiService {

    @Autowired
    private RcsCreditSeriesLimitService rcsCreditSeriesLimitService;
    @Autowired
    private RcsCreditSingleMatchLimitService rcsCreditSingleMatchLimitService;
    @Autowired
    private RcsCreditSinglePlayLimitService rcsCreditSinglePlayLimitService;
    @Autowired
    private IRcsOperateMerchantsSetService rcsOperateMerchantsSetService;
    @Autowired
    @Qualifier("quotaBusinessLimitServiceImpl")
    private RcsQuotaBusinessLimitService rcsQuotaBusinessLimitService;

    @Autowired
    private Map<String, CreditLimitService> creditLimitServiceMap;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    MatrixAdapter matrixAdapter;

    @Autowired
    ParamValidateService paramValidateService;

    /**
     * 查询信用代理最大限额、信用代理限额
     *
     * @param request
     * @return
     */
    @Override
    public Response<CreditConfigDto> queryCreditLimitConfig(Request<CreditConfigDto> request) {
        MDC.put("X-B3-TraceId", request.getGlobalId());
        log.info("信用额度，查询信用代理最大限额、信用代理限额：" + JSON.toJSONString(request));
        CreditConfigDto configDto = request.getData();
        if (configDto == null) {
            configDto = new CreditConfigDto();
        }
        Long merchantId = CreditBizUtils.getMerchantId(configDto.getMerchantId());
        String creditId = CreditBizUtils.getCreditId(configDto.getCreditId());
        long userId = 0L;
        try {
            List<RcsCreditSeriesLimit> rcsCreditSeriesLimitList = rcsCreditSeriesLimitService.querySeriesLimit(merchantId, creditId, userId);
            if (CollectionUtils.isNotEmpty(rcsCreditSeriesLimitList)) {
                log.info("信用额度::{}::，查询信用代理最大限额、信用代理限额 - 串关限额，商户ID:{},代理ID:{}：，result:{}", userId, merchantId, creditId, JSON.toJSONString(rcsCreditSeriesLimitList));
                List<CreditSeriesConfigDto> seriesConfigList = rcsCreditSeriesLimitList.stream().map(CreditBizUtils::toCreditSeriesConfigDto).collect(Collectors.toList());
                configDto.setSeriesConfigList(seriesConfigList);
            }
            List<RcsCreditSingleMatchLimit> rcsCreditSingleMatchLimitList = rcsCreditSingleMatchLimitService.querySingleMatchLimit(merchantId, creditId);
            if (CollectionUtils.isNotEmpty(rcsCreditSingleMatchLimitList)) {
                log.info("信用额度::{}::，查询信用代理最大限额、信用代理限额 - 赛事单场限额，商户ID:{},代理ID:{}：，result:{}", userId, merchantId, creditId, JSON.toJSONString(rcsCreditSingleMatchLimitList));
                List<CreditSingleMatchConfigDto> singleMatchConfigList = rcsCreditSingleMatchLimitList.stream().map(CreditBizUtils::toCreditSingleMatchConfigDto).collect(Collectors.toList());
                configDto.setSingleMatchConfigList(singleMatchConfigList);
            }
            List<RcsCreditSinglePlayLimit> rcsCreditSinglePlayLimitList = rcsCreditSinglePlayLimitService.querySinglePlayLimit(merchantId, creditId, userId);
            if (CollectionUtils.isNotEmpty(rcsCreditSinglePlayLimitList)) {
                log.info("信用额度::{}::，查询信用代理最大限额、信用代理限额 - 单场玩法限额，商户ID:{},代理ID:{}：，result:{}", userId, merchantId, creditId, JSON.toJSONString(rcsCreditSinglePlayLimitList));
                List<CreditSinglePlayConfigDto> singlePlayConfigList = rcsCreditSinglePlayLimitList.stream().map(CreditBizUtils::toCreditSinglePlayConfigDto).collect(Collectors.toList());
                configDto.setSinglePlayConfigList(singlePlayConfigList);
            }
            log.info("信用额度::{}::，查询信用网限额模板，商户ID:{},代理ID:{}：，result:{}", userId, merchantId, creditId, JSON.toJSONString(configDto));
            //log.info("查询信用网限额模板返回结果：{}", JSON.toJSONString(configDto));
            return Response.success(configDto);
        } catch (Exception e) {
            log.error("查询信用网限额模板异常", e);
            return Response.error(ErrorCode.CONFIG_QUERY_EXCEPTION, "查询信用网限额模板异常：" + e.getMessage(), configDto);
        }
    }

    private void saveOrUpdateParamCheck(Request<CreditConfigSaveDto> request) {
        if (request == null || request.getData() == null) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "参数错误");
        }
        CreditConfigSaveDto configDto = request.getData();
        List<CreditAgentInfoDto> creditAgentInfoList = configDto.getCreditAgentInfoList();
        if (CollectionUtils.isEmpty(creditAgentInfoList)) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "信用代理信息不能为空");
        }
        if (configDto.getMerchantId() == null) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "商户ID不能为空");
        }
        for (CreditAgentInfoDto dto : creditAgentInfoList) {
            if (StringUtils.isBlank(dto.getParentCreditId())) {
                dto.setParentCreditId("0");
            }
            if (StringUtils.isBlank(dto.getCreditId()) || StringUtils.isBlank(dto.getCreditName()) || StringUtils.isBlank(dto.getParentCreditId())) {
                throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "信用代理信息不完整");
            }
        }
    }

    /**
     * 保存或更新代理配置
     *
     * @param request
     * @return
     */
    @Override
    public Response<Boolean> saveOrUpdateCreditLimitConfig(Request<CreditConfigSaveDto> request) {
        try {
            saveOrUpdateParamCheck(request);
            MDC.put("X-B3-TraceId", request.getGlobalId());
            log.info("信用额度，保存或更新代理配置：" + JSON.toJSONString(request));
            CreditConfigSaveDto configSaveDto = request.getData();
            Long merchantId = configSaveDto.getMerchantId();
            long userId = 0L;
            List<CreditSeriesConfigDto> seriesConfigList = configSaveDto.getSeriesConfigList();
            List<CreditSingleMatchConfigDto> singleMatchConfigList = configSaveDto.getSingleMatchConfigList();
            List<CreditSinglePlayConfigDto> singlePlayConfigList = configSaveDto.getSinglePlayConfigList();
            configSaveDto.getCreditAgentInfoList().forEach(creditAgentInfoDto -> {
                String creditId = creditAgentInfoDto.getCreditId();

                if (CollectionUtils.isNotEmpty(seriesConfigList)) {
                    List<RcsCreditSeriesLimit> seriesLimitList = seriesConfigList.stream().map(config -> CreditBizUtils.toRcsCreditSeriesLimit(merchantId, creditId, userId, config)).collect(Collectors.toList());
                    rcsCreditSeriesLimitService.batchInsertOrUpdate(seriesLimitList);
                    Map<String, String> hashMap = seriesLimitList.stream().collect(Collectors.toMap(config -> String.valueOf(config.getSeriesType()), config -> CommonUtils.toString(config.getValue())));
                    String key = CreditRedisKey.Limit.getSeriesKey(merchantId, creditId);
                    redisUtils.hmset(key, hashMap);
                    log.info("信用额度，代理串关限额缓存：key={},hashMap={}", key, hashMap);
                }

                if (CollectionUtils.isNotEmpty(singleMatchConfigList)) {
                    List<RcsCreditSingleMatchLimit> singleMatchLimitList = singleMatchConfigList.stream().map(config -> CreditBizUtils.toRcsCreditSingleMatchLimit(merchantId, creditId, config)).collect(Collectors.toList());
                    rcsCreditSingleMatchLimitService.batchInsertOrUpdate(singleMatchLimitList);
                    Map<Integer, List<RcsCreditSingleMatchLimit>> mapBySportId = singleMatchLimitList.stream().collect(Collectors.groupingBy(RcsCreditSingleMatchLimit::getSportId));
                    mapBySportId.forEach((sportId, list) -> {
                        Map<String, String> hashMap = list.stream().collect(Collectors.toMap(config -> String.valueOf(config.getTournamentLevel()), config -> CommonUtils.toString(config.getValue())));
                        String key = CreditRedisKey.Limit.getSingleMatchKey(merchantId, creditId, sportId);
                        redisUtils.hmset(key, hashMap);
                        log.info("信用额度，代理单场赛事限额缓存：key={},hashMap={}", key, hashMap);
                    });
                }

                if (CollectionUtils.isNotEmpty(singlePlayConfigList)) {
                    List<RcsCreditSinglePlayLimit> singlePlayLimitList = singlePlayConfigList.stream().map(config -> CreditBizUtils.toRcsCreditSinglePlayLimit(merchantId, creditId, userId, config)).collect(Collectors.toList());
                    rcsCreditSinglePlayLimitService.batchInsertOrUpdate(singlePlayLimitList);
                    Map<Integer, List<RcsCreditSinglePlayLimit>> mapBySportId = singlePlayLimitList.stream().collect(Collectors.groupingBy(RcsCreditSinglePlayLimit::getSportId));
                    mapBySportId.forEach((sportId, playClassifyList) -> {
                        Map<Integer, List<RcsCreditSinglePlayLimit>> mapByPlayClassify = playClassifyList.stream().collect(Collectors.groupingBy(RcsCreditSinglePlayLimit::getPlayClassify));
                        mapByPlayClassify.forEach((playClassify, betStageList) -> {
                            Map<String, List<RcsCreditSinglePlayLimit>> mapByBetStage = betStageList.stream().collect(Collectors.groupingBy(RcsCreditSinglePlayLimit::getBetStage));
                            mapByBetStage.forEach((betStage, list) -> {
                                Map<String, String> hashMap = list.stream().collect(Collectors.toMap(config -> String.valueOf(config.getTournamentLevel()), config -> CommonUtils.toString(config.getValue())));
                                String key = CreditRedisKey.Limit.getSinglePlayKey(merchantId, creditId, sportId, playClassify, betStage);
                                redisUtils.hmset(key, hashMap);
                                log.info("信用额度，代理玩法累计限额缓存：key={},hashMap={}", key, hashMap);
                            });
                        });
                    });
                }

                saveCreditAgent(merchantId, creditAgentInfoDto);
            });
            return Response.success(true);
        } catch (LogicException e) {
            return Response.error(NumberUtils.toInt(e.getCode()), "保存或更新商户配置、代理配置异常：" + e.getMessage(), false);
        } catch (Exception e) {
            return Response.error(ErrorCode.CONFIG_UPDATE_EXCEPTION, "保存或更新商户配置、代理配置异常：" + e.getMessage(), false);
        }
    }

    private void saveCreditAgent(Long merchantId, CreditAgentInfoDto agentInfo) {
        String creditId = agentInfo.getCreditId();
        String creditName = agentInfo.getCreditName();
        String parentCreditId = agentInfo.getParentCreditId();
        boolean isNew = rcsOperateMerchantsSetService.insertCreditAgentIfAbsent(merchantId, creditId, creditName, parentCreditId);
        rcsQuotaBusinessLimitService.insertCreditAgentIfAbsent(merchantId, creditId, creditName, parentCreditId);
//        if (isNew) {
//            log.info("保存代理信息到商户库：agentInfo={}", JSON.toJSONString(agentInfo));
//            Long userId = -1L;
//            // 查询用户通用限额默认值
//            List<RcsCreditSeriesLimit> defaultSeriesLimitList = rcsCreditSeriesLimitService.querySeriesLimit(0L, "0", userId);
//            if (CollectionUtils.isNotEmpty(defaultSeriesLimitList)) {
//                defaultSeriesLimitList = defaultSeriesLimitList.stream().peek(config -> {
//                    config.setMerchantId(merchantId);
//                    config.setCreditId(creditId);
//                    config.setCreateTime(null);
//                    config.setUpdateTime(null);
//                }).collect(Collectors.toList());
//                // 设置代理用户通用限额
//                rcsCreditSeriesLimitService.batchInsertOrUpdate(defaultSeriesLimitList);
//                // 缓存
//                Map<String, String> hashMap = defaultSeriesLimitList.stream().collect(Collectors.toMap(config -> String.valueOf(config.getSeriesType()), config -> config.getValue().toPlainString()));
//                String key = CreditRedisKey.Limit.getUserSeriesKey(merchantId, creditId, userId);
//                redisUtils.hmset(key, hashMap);
//                log.info("用户串关通用限额缓存：key={},hashMap={}", key, hashMap);
//            }
//
//            // 查询用户通用限额默认值
//            List<RcsCreditSinglePlayLimit> defaultSinglePlayLimitList = rcsCreditSinglePlayLimitService.querySinglePlayLimit(0L, "0", userId);
//            if (CollectionUtils.isNotEmpty(defaultSinglePlayLimitList)) {
//                defaultSinglePlayLimitList = defaultSinglePlayLimitList.stream().peek(config -> {
//                    config.setMerchantId(merchantId);
//                    config.setCreditId(creditId);
//                    config.setCreateTime(null);
//                    config.setUpdateTime(null);
//                }).collect(Collectors.toList());
//                // 设置代理用户通用限额
//                rcsCreditSinglePlayLimitService.batchInsertOrUpdate(defaultSinglePlayLimitList);
//                // 缓存
//                Map<Integer, List<RcsCreditSinglePlayLimit>> mapBySportId = defaultSinglePlayLimitList.stream().collect(Collectors.groupingBy(RcsCreditSinglePlayLimit::getSportId));
//                mapBySportId.forEach((sportId, tournamentLevelList) -> {
//                    Map<Integer, List<RcsCreditSinglePlayLimit>> mapByTournamentLevel = tournamentLevelList.stream().collect(Collectors.groupingBy(RcsCreditSinglePlayLimit::getTournamentLevel));
//                    mapByTournamentLevel.forEach((tournamentLevel, playClassifyList) -> {
//                        Map<Integer, List<RcsCreditSinglePlayLimit>> mapByPlayClassify = playClassifyList.stream().collect(Collectors.groupingBy(RcsCreditSinglePlayLimit::getPlayClassify));
//                        mapByPlayClassify.forEach((playClassify, list) -> {
//                            Map<String, String> hashMap = list.stream().collect(Collectors.toMap(RcsCreditSinglePlayLimit::getBetStage, config -> config.getValue().toPlainString()));
//                            String key = CreditRedisKey.Limit.getUserSinglePlayKey(merchantId, creditId, userId, sportId, tournamentLevel, playClassify);
//                            redisUtils.hmset(key, hashMap);
//                            log.info("用户玩法通用限额缓存：key={},hashMap={}", key, hashMap);
//                        });
//                    });
//                });
//            }
//        }
    }

    @Override
    public Response queryMaxBetMoneyBySelect(Request<OrderBean> request) {
        try {
            StopWatch sw = new StopWatch();
            sw.start();
            paramCheck(request);
            MDC.put("X-B3-TraceId", request.getGlobalId());
            log.info("信用额度，查询最大最小投注金额开始：{}", JSON.toJSONString(request));
            OrderBean orderBean = request.getData();
            CreditLimitService.setLimitType(orderBean);
            List<RcsBusinessPlayPaidConfigVo> resultList = getService(orderBean).queryBetLimit(orderBean);
            for (RcsBusinessPlayPaidConfigVo vo : resultList) {
                if (vo.getOrderMaxPay() < 0L) {
                    vo.setOrderMaxPay(0L);
                }
            }
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("data", resultList);
            sw.stop();
            log.info("信用额度，查询最大最小投注金额耗时：{}", sw.getTotalTimeMillis());
            return Response.success(resultMap);
        } catch (LogicException e) {
            e.printStackTrace();
            log.error("信用额度，查询最大最小投注金额异常", e);
            return Response.error(Response.FAIL, e.getMsg());
        } catch (RcsServiceException e) {
            log.error("信用额度，查询最大最小投注金额，业务异常", e);
            return Response.error(Response.FAIL, e.getErrorMassage());
        } catch (Exception e) {
            log.error("信用额度，查询最大最小投注金额异常", e);
            return Response.error(Response.FAIL, "查询最大最小投注金额异常");
        }
    }

    @Override
    public Response saveOrderAndValidateMaxPaid(Request<OrderBean> request) {
        // 默认风控
        int orderType = 1;
        try {
            paramCheck(request);
            MDC.put("X-B3-TraceId", request.getGlobalId());
            OrderBean orderBean = request.getData();
            log.info("信用额度::{}::，订单校验保存开始：{}", orderBean.getOrderNo(), JSON.toJSONString(request));
            CreditLimitService.setLimitType(orderBean);
            // 校验结果，0-待处理，1-成功，2-失败
            orderBean.setValidateResult(0);
            orderBean.setOrderStatus(0);
            orderBean.setInfoStatus(OrderInfoStatusEnum.WAITING.getCode());
            CreditLimitService creditLimitService = getService(orderBean);
            orderType = creditLimitService.orderType();
            for (OrderItem orderItem : orderBean.getItems()) {
                orderItem.setRiskChannel(orderType);
            }
            // 保存订单
            // producerSendMessageUtils.sendMessage(MqConstants.RCS_ORDER_UPDATE, BaseConstants.SAVE_ORDER_TAGS, orderBean.getOrderNo(), orderBean);
            //矩阵赋值
            //信用商户不需要计算矩阵数据
            /*String businessVolumePercent = redisUtils.get(CreditRedisKey.RCS_TRADE_CREDIT_BUSINESS_VOLUME_PERCENT);
            if(!"0".equals(businessVolumePercent)){
                caclMatrix(orderBean);
            }*/
            // 校验
            StopWatch sw  = new StopWatch();sw.start();
            Map<String, Object> resultMap = creditLimitService.checkOrder(orderBean);
            sw.stop();
            log.info("信用额度::{}::，订单校验保存结束耗时：{}", orderBean.getOrderNo(),sw.getTotalTimeMillis());
            resultMap.put("orderType", orderType);
            return Response.success(resultMap);
        } catch (Exception e) {
            OrderBean orderBean;
            if (request != null && request.getData() != null) {
                orderBean = request.getData();
            } else {
                orderBean = new OrderBean();
            }
            log.error("订单::" + orderBean.getOrderNo() + "::校验保存异常", e);
            orderBean.setOrderStatus(2);
            orderBean.setInfoStatus(2);
            orderBean.setReason("订单验证失败：" + e.getMessage());
            if (StringUtils.isNotBlank(orderBean.getOrderNo())) {
                producerSendMessageUtils.sendMessage(MqConstants.RCS_ORDER_UPDATE, BaseConstants.SAVE_ORDER_TAGS, orderBean.getOrderNo(), orderBean);
            }

            String errorMsg;
            int infoCode;
            if (e instanceof LogicException) {
                errorMsg = ((LogicException) e).getMsg();
                infoCode = Integer.valueOf(((LogicException) e).getCode());
            } else if (e instanceof RcsServiceException) {
                errorMsg = ((RcsServiceException) e).getErrorMassage();
                infoCode = ((RcsServiceException) e).getCode();
            } else {
                errorMsg = e.getMessage();
                infoCode = -1;
            }
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("status", 0);
            resultMap.put("infoCode", infoCode);
            resultMap.put("infoMsg", "风控拒单：" + errorMsg);
            resultMap.put("orderType", orderType);
            if (StringUtils.isNotBlank(orderBean.getOrderNo())) {
                resultMap.put(orderBean.getOrderNo(), false);
                resultMap.put(orderBean.getOrderNo() + "_error_msg", errorMsg);
            }
            resultMap.put("infoStatus", orderBean.getInfoStatus());
            resultMap.put("isVip", orderBean.getVipLevel());
            return Response.success(resultMap);
        }
    }

    private void paramCheck(Request<OrderBean> request) {
        if (request == null || request.getData() == null) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "参数错误");
        }
    }

    private CreditLimitService getService(OrderBean orderBean) {
        CreditLimitService creditLimitService = creditLimitServiceMap.get(CreditLimitService.getServiceName(orderBean));
        log.info("额度查询，service={}", creditLimitService.getClass().getName());
        return creditLimitService;
    }

    /**
     * 矩阵赋值
     *
     * @param orderBean
     */
    private void caclMatrix(OrderBean orderBean) {
        try {
            if (orderBean.getSeriesType() != 1) {
                return;
            }
            ExtendBean extendBean = paramValidateService.buildExtendBean(orderBean, orderBean.getItems().get(0));
            orderBean.setExtendBean(extendBean);
            MatrixBean matrixBean = matrixAdapter.process(orderBean.getExtendBean().getSportId(), orderBean.getExtendBean().getPlayId(), orderBean.getExtendBean());
            if (0 == matrixBean.getRecType()) {
                orderBean.getExtendBean().setRecVal(JSONObject.toJSONString(matrixBean.getMatrixValueArray()));
                orderBean.getItems().get(0).setRecVal(orderBean.getExtendBean().getRecVal());
            }
            orderBean.getExtendBean().setRecType(matrixBean.getRecType());
        } catch (Exception e) {
            log.info("信用网矩阵计算异常:{},{},{}", orderBean.getOrderNo(), e.getMessage(), e);
        }
    }

}
