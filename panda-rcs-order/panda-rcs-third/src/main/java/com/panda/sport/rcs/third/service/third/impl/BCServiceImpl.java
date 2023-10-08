package com.panda.sport.rcs.third.service.third.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.bts.ThirdBetParamDto;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.MtsIsCacheEnum;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.enums.OrderTypeEnum;
import com.panda.sport.rcs.enums.YesNoEnum;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import com.panda.sport.rcs.service.RcsSwitchService;
import com.panda.sport.rcs.third.config.BetGuardApiConfig;
import com.panda.sport.rcs.third.entity.betguard.dto.*;
import com.panda.sport.rcs.third.entity.common.ThirdOrderExt;
import com.panda.sport.rcs.third.entity.common.ThirdResultVo;
import com.panda.sport.rcs.third.entity.common.pojo.RcsCtsOrderExt;
import com.panda.sport.rcs.third.enums.OrderStatusEnum;
import com.panda.sport.rcs.third.factory.ThirdStrategyFactory;
import com.panda.sport.rcs.third.mapper.RcsCtsOrderExtMapper;
import com.panda.sport.rcs.third.service.handler.IOrderHandlerService;
import com.panda.sport.rcs.third.service.third.ThirdOrderBaseService;
import com.panda.sport.rcs.third.service.third.ThirdOrderService;
import com.panda.sport.rcs.third.util.cache.RcsLocalCacheUtils;
import com.panda.sport.rcs.third.util.encrypt.HMACSHA256Util;
import com.panda.sport.rcs.third.util.encrypt.JwtUtils;
import com.panda.sport.rcs.third.util.http.HttpUtil;
import com.panda.sport.rcs.utils.SeriesTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.panda.sport.rcs.third.common.Constants.*;
import static com.panda.sport.rcs.third.common.NumberConstant.CTS_DEFAULT_DISCOUNT;
import static com.panda.sport.rcs.third.common.NumberConstant.GTS_DEFAULT_DISCOUNT;
import static com.panda.sport.rcs.third.common.ThirdUrl.*;

/**
 * @author Beulah
 * @date 2023/3/28 19:09
 * @description bc-betguard api封装
 */
@Slf4j
@Service
public class BCServiceImpl extends ThirdOrderBaseService implements ThirdOrderService, InitializingBean {


    @Resource
    BetGuardApiConfig config;
    @Resource
    RedisClient redisClient;

    @Resource
    IOrderHandlerService iOrderHandlerService;

    @Resource
    RcsCtsOrderExtMapper rcsCtsOrderExtMapper;

    @Resource
    RcsSwitchService rcsSwitchService;


    @Override
    public void afterPropertiesSet() throws Exception {
        ThirdStrategyFactory.register(OrderTypeEnum.CTS.getPlatFrom(), this);
    }


    @Override
    public Long getMaxBetAmount(ThirdBetParamDto dto) {
        //注单信息
        List<ExtendBean> extendBeanList = dto.getExtendBeanList();
        String userId = extendBeanList.get(0) == null ? null : extendBeanList.get(0).getUserId();
        Map<String, Object> params = new TreeMap<>();
        // 0不接受变更 1接收更好 2任意
        params.put("AcceptTypeId", 2);
        long amount = 100000000000L;
        params.put("Amount", numberChange(Long.toString(amount)));
        params.put("AuthToken", getToken(userId));
        int bcSeriesType = 1;  //单关
        if (dto.getN() >= 2) {
            if (dto.getFlag()) {
                bcSeriesType = 3;   //串关 3004
            } else {
                bcSeriesType = 2;  //多项单注 2001  3001
            }
        }
        params.put("BetType", bcSeriesType);
        params.put("Currency", "CNY");
        //投注项转换
        ThirdOrderExt ext = new ThirdOrderExt();
        ext.setList(extendBeanList);
        convertThirdParam(ext);
        params.put("Selections", convertSelectId(ext));
        try {
            params.put("RequestHash", createSign(params, userId));
            ThirdResultVo bet = createBet(userId, params, null);
            if (Objects.nonNull(bet) && Objects.nonNull(bet.getMaxAmount())) {
                return bet.getMaxAmount();
            }
        } catch (Exception e) {
            log.error("::{}::CTS限额-签名异常, 默认限额:{}", userId, config.getDefaultLimit(), e);
        }
        return config.getDefaultLimit();
    }

    @Override
    public ThirdResultVo placeBet(ThirdOrderExt ext) {
        MDC.put(LINKID, ext.getLinkId());
        String orderNo = ext.getOrderNo();
        if (CollectionUtils.isEmpty(ext.getList())) {
            log.error("::{}::注单信息不能为空", orderNo);
            return null;
        }
        //组装参数
        Map<String, Object> params = new TreeMap<>();
        // 0不接受变更 1接收更好 2任意
        params.put("AcceptTypeId", ext.getAcceptOdds() == 3 ? 0 : ext.getAcceptOdds());
        params.put("Amount", numberChange(discountAmount(ext).toString()));
        params.put("AuthToken", getToken(ext.getList().get(0).getUserId()));
        if (ext.getSeriesType() != 1) {
            Integer count = SeriesTypeUtils.getCount(ext.getSeriesType(), 1);
            if (count == 1) {
                params.put("BetType", 2); //单式
            } else {
                params.put("BetType", 3); //复式
            }
        } else {
            params.put("BetType", 1); //单关
        }
        params.put("Currency", ext.getCurrency());
        //投注项转换
        convertThirdParam(ext);
        params.put("Selections", convertSelectId(ext));
        params.put("RequestHash", createSign(params, orderNo));
        ThirdResultVo bet = createBet(ext.getOrderNo(), params, ext);
        MDC.remove(LINKID);
        return bet;
    }


    /**
     * 请求BC
     *
     * @param orderNo 注单号
     * @param params  入参
     * @return 结果
     */
    public ThirdResultVo createBet(String orderNo, Map<String, Object> params, ThirdOrderExt ext) {
        String url = null;
        String logText = ext == null ? "CTS获取限额" : "CTS请求投注";
        ThirdResultVo resultVo = new ThirdResultVo();
        try {
            url = config.getUrl() + BC_CREATE_BET_URL;
            log.info("::{}::{}参数:{}", orderNo, logText, JSONObject.toJSONString(params));
            //注单id线程中
            MDC.put("orderNo", orderNo);
            String result = HttpUtil.post(url, params, true, new HashMap<>());
            MDC.remove("orderNo");
            log.info("::{}::{}返回:{}", orderNo, logText, result);
            resultVo.setThirdRes(JSONObject.toJSONString(result));
            if (StringUtils.isBlank(result)) {
                if (ext == null) {
                    log.warn("::{}::{}没有返回值,默认限额:{}", orderNo, logText, config.getDefaultLimit());
                    resultVo.setMaxAmount(config.getDefaultLimit());
                    return resultVo;
                }
                log.warn("::{}::{}没有返回值,拒单", orderNo, logText);
                resultVo.setThirdNo(orderNo);
                resultVo.setThirdOrderStatus(2);
                //重试3次未成功 标识未请求未提交
                if (orderNo.equals(MDC.get("retryFailed"))) {
                    //缓存20s 等待业务取消
                    log.warn("::{}::投注请求未发送成功,取消不用通知数据商", orderNo);
                    redisClient.setExpiry(String.format(ORDER_REQUEST_FAILED, orderNo), 1, 20L);
                    MDC.remove("retryFailed");
                }
                return resultVo;
            }
            JSONObject json = JSONObject.parseObject(result);
            if (ext == null) {
                String betData = json.getString("Data");
                if (StringUtils.isBlank(betData)) {
                    resultVo.setMaxAmount(2000L);
                    log.info("::{}::{}未返回数据,默认{}", orderNo, logText, config.getDefaultLimit());
                } else {
                    JSONObject data = JSONObject.parseObject(betData);
                    JSONObject errorData = JSONObject.parseObject(data.getString("ErrorData"));
                    Double maxAllowedBetStake = Double.valueOf(errorData.getString("MaxAllowedBetStake") == null ? String.valueOf(config.getDefaultLimit()) : errorData.getString("MaxAllowedBetStake"));
                    Double minAllowedBetStake = Double.valueOf(errorData.getString("MinAllowedBetStake") == null ? String.valueOf(config.getDefaultLimit()) : errorData.getString("MinAllowedBetStake"));
                    resultVo.setMaxAmount(maxAllowedBetStake.longValue());
                    resultVo.setMinAmount(minAllowedBetStake.longValue());
                    log.info("::{}::{}成功,投注限额为:{}", orderNo, logText, errorData);
                }
            } else {
                String statusCode = json.getString("StatusCode");
                if (!"0".equals(statusCode)) {
                    log.info("::{}::{}失败,原因={}", orderNo, logText, statusCode);
                    resultVo.setThirdOrderStatus(2);
                    resultVo.setReasonMsg(statusCode);
                    ext.setOrderStatus(2);
                    ext.setThirdOrderStatus(2);
                    String canceledKey = String.format(THIRD_ORDER_CANCELED, orderNo);
                    redisClient.setExpiry(canceledKey, 1, 2 * 60L);
                    iOrderHandlerService.updateOrder(ext, OrderInfoStatusEnum.MTS_REFUSE.getCode(), statusCode, MtsIsCacheEnum.CTS.getValue());
                } else {
                    log.info("::{}::{}請求返回成功", orderNo, logText);
                    //将订单投注队列等待
                    JSONObject bcReturn = JSONObject.parseObject(json.getString("Data"));
                    String betId = bcReturn.getString("BetId");
                    ext.setThirdOrderNo(betId);
                    String bcOrder = String.format(THIRD_BC_ORDER_CACHE, betId);
                    redisClient.setExpiry(bcOrder, ext, 60 * 60 * 3L);
                    //默认三方是接单
                    ext.setThirdOrderStatus(1);
                    //更新订单
                    iOrderHandlerService.orderByPa(ext);
                    resultVo.setThirdOrderStatus(1);
                    resultVo.setThirdNo(betId);
                }
            }
            resultVo.setErrorCode(json.getString("StatusCode"));
            return resultVo;
        } catch (Exception e) {
            if (ext == null) {
                log.error("::{}::{}API:{} 异常, params:{}, 取默认限额:{}", orderNo, logText, url, JSONObject.toJSONString(params), config.getDefaultLimit(), e);
                resultVo.setMaxAmount(config.getDefaultLimit());
                return resultVo;
            }
            log.error("::{}::{}API:{} 异常, params:{}, 拒单", orderNo, logText, url, JSONObject.toJSONString(params), e);
            resultVo.setThirdNo(orderNo);
            resultVo.setThirdOrderStatus(2);
        }
        return resultVo;

    }


    @Override
    public Boolean orderConfirm(ThirdOrderExt ext) {
        if (OrderStatusEnum.REJECTED.getCode() == ext.getOrderStatus()) {
            //后置检查拒单 取消
            orderCancel(ext);
        }
        return true;
    }


    /**
     * 取消注单
     */
    @Override
    public void orderCancel(ThirdOrderExt ext) {
        MDC.put(LINKID, ext.getOrderNo());
        String orderNo = ext.getOrderNo();
        String url = null;
        Map<String, Object> params = new HashMap<>();
        if (ext.getThirdOrderNo() == null) {
            //数据商未返回结果，我方已经取消
        }
        try {
            params.put("BetId", Long.valueOf(ext.getThirdOrderNo()));
            params.put("RequestHash", createSign(params, orderNo));
            url = config.getUrl() + BC_CANCEL_URL;
            String result = HttpUtil.post(url, params, true, new HashMap<>());
            log.info("::{}::CTS取消注单参数:{}, 返回:{}", orderNo, JSONObject.toJSONString(params), result);
            if (StringUtils.isNotBlank(result)) {
                JSONObject json = JSONObject.parseObject(result);
                if (json.getString("StatusCode").equalsIgnoreCase("0")) {
                    log.info("::{}::CTS取消注单成功", orderNo);
                } else {
                    log.info("::{}::CTS取消注单失败", orderNo);
                    updateOrderCancelFailedReason(ext, json.toJSONString());
                }
            } else {
                log.info("::{}::CTS取消注单没有返回值,请重试", orderNo);
            }
        } catch (Exception e) {
            log.error("::{}::CTS取消注单异常, url={}, params={}", orderNo, url, JSONObject.toJSONString(params), e);
        }
        MDC.remove(LINKID);
    }

    @Override
    public void updateOrderCancelFailedReason(ThirdOrderExt ext, String reason) {
        try {
            LambdaQueryWrapper<RcsCtsOrderExt> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RcsCtsOrderExt::getOrderNo, ext.getOrderNo());
            RcsCtsOrderExt orderExt = rcsCtsOrderExtMapper.selectOne(wrapper);
            if (orderExt == null) {
                throw new RcsServiceException("订单信息未找到");
            }
            orderExt.setRemark("业务取消但数据商接口错误,错误原因:" + reason);
            rcsCtsOrderExtMapper.updateById(orderExt);
            log.info("::{}::CTS订单取消失败原因更新完成", ext.getOrderNo());
        } catch (Exception ex) {
            log.error("::{}::CTS订单取消更新数据库发生异常", ext.getOrderNo());
        }
    }

    /**
     * BC调用PM的BetResulted失败后 调用此方法通知BC
     */
    public void resendFailedTransfers(FilterTransferDto filterTransferModel) {
        String url = null;
        Map<String, Object> params = new TreeMap<>();
        try {
            params.put("StartDateStamp", filterTransferModel.getStartDateStamp());
            params.put("EndDateStamp", filterTransferModel.getEndDateStamp());
            params.put("State", -1);
            params.put("BetIds", filterTransferModel.getBetIds());
            params.put("DocumentId", filterTransferModel.getDocumentId());
            params.put("RequestHash", createSign(params, String.valueOf(filterTransferModel.getBetIds().get(0))));
            url = config.getUrl() + BET_RESEND_FAILED_TRANSFERS_URL;
            String result = HttpUtil.post(url, JSONObject.toJSONString(filterTransferModel), new HashMap<>());
            log.info("::{}::BC调用PM的BetResulted失败后重新发送:{},返回:{}", JSONObject.toJSONString(filterTransferModel.getBetIds()), JSONObject.toJSONString(filterTransferModel), result);
            if (StringUtils.isNotBlank(result)) {
                JSONObject json = JSONObject.parseObject(result);
                if (json.getString("StatusCode").equalsIgnoreCase("0")) {
                    log.info("::{}::BC调用PM的BetResulted失败后重新发送成功", JSONObject.toJSONString(filterTransferModel.getBetIds()));
                } else {
                    log.warn("::{}::BC调用PM的BetResulted失败后重新发送失败", JSONObject.toJSONString(filterTransferModel.getBetIds()));
                }
            } else {
                log.info("::{}::BC调用PM的BetResulted失败后重新发送返回值，请重试", JSONObject.toJSONString(filterTransferModel.getBetIds()));
            }
        } catch (Exception e) {
            log.error("::{}::BC调用PM的BetResulted失败后重新请求异常,url：{},params:{},e:{}", JSONObject.toJSONString(filterTransferModel.getBetIds()), url, JSONObject.toJSONString(params), e);
            throw new RcsServiceException("BC取消注单异常:" + e.getMessage());
        }
    }

    /**
     * 标记投注为现金支付
     */
    public void markBetAsCashOut(FilterCashoutDto filterCashOutModel) {
        String url = null;
        Map<String, Object> params = new TreeMap<>();
        try {
            params.put("BetId", filterCashOutModel.getBetId());
            params.put("Price", filterCashOutModel.getPrice());
            url = config.getUrl() + BC_MARK_BET_AS_CASHOUT_URL;
            String result = HttpUtil.post(url, params, true, new HashMap<>());
            log.info("【markBetAsCashOut】:{},返回:{}", JSONObject.toJSONString(params), result);
            if (StringUtils.isNotBlank(result)) {
                JSONObject json = JSONObject.parseObject(result);
                if (json.getString("StatusCode").equalsIgnoreCase("0")) {
                    log.info("::{}::BC调用PM的BetResulted失败后重新发送成功", JSONObject.toJSONString(params));
                } else {
                    log.info("::{}::BC调用PM的BetResulted失败后重新发送失败", JSONObject.toJSONString(params));
                }
            } else {
                log.info("::{}::BC调用PM的BetResulted失败后重新发送返回值，请重试", JSONObject.toJSONString(params));
            }
        } catch (Exception e) {
            log.error("::{}::BC调用PM的BetResulted失败后重新请求异常,url：{},params:{},e:{}", JSONObject.toJSONString(params), url, JSONObject.toJSONString(params), e);
            throw new RcsServiceException("BC取消注单异常:" + e.getMessage());
        }
    }

    /**
     * 标记投注为现金支付
     */
    public void checkAndMarkBetAsCashOut(FilterCashoutRequestDto filterCashOutRequestModel) {
        String url = null;
        Map<String, Object> params = new HashMap<>();
        try {
            params.put("BetId", filterCashOutRequestModel.getBetId());
            params.put("Price", filterCashOutRequestModel.getPrice());
            url = config.getUrl() + BC_MARK_BET_AS_CASHOUT_URL;
            String result = HttpUtil.post(url, params, true, null);
            log.info("BC调用PM的BetResulted失败后重新发送:{},返回:{}", JSONObject.toJSONString(params), result);
            if (StringUtils.isNotBlank(result)) {
                JSONObject json = JSONObject.parseObject(result);
                if (json.getString("StatusCode").equalsIgnoreCase("0")) {
                    log.info("::{}::BC调用PM的BetResulted失败后重新发送成功", JSONObject.toJSONString(params));
                } else {
                    log.info("::{}::BC调用PM的BetResulted失败后重新发送失败", JSONObject.toJSONString(params));
                }
            } else {
                log.info("::{}::BC调用PM的BetResulted失败后重新发送返回值，请重试", JSONObject.toJSONString(params));
            }
        } catch (Exception e) {
            log.error("::{}::BC调用PM的BetResulted失败后重新请求异常,url：{},params:{},e:{}", JSONObject.toJSONString(params), url, JSONObject.toJSONString(params), e);
            throw new RcsServiceException("BC取消注单异常:" + e.getMessage());
        }
    }


    @Override
    public ThirdOrderExt convertThirdParam(ThirdOrderExt ext) {
        ext.getList().forEach(e -> {
            //获取第三方赛事数据转换
            String selectKey = String.format(CTS_THIRD_SELECTID, e.getSelectId());
            Object o = RcsLocalCacheUtils.timedCache.get(selectKey);
            if (Objects.nonNull(o)) {
                e.setSelectId(o.toString());
            } else {
                StandardSportMarketOdds standardSportMarketOdds = getStandardSportMarketOdds(e.getSelectId());
                e.setSelectId(standardSportMarketOdds.getThirdOddsFieldSourceId());
                //缓存5分钟
                RcsLocalCacheUtils.timedCache.put(selectKey, standardSportMarketOdds.getThirdOddsFieldSourceId(), 5 * 60 * 1000L);
            }
        });
        return ext;
    }

    /**
     * 投注项转换
     *
     * @param ext
     * @return
     */
    private List<Map<String, Object>> convertSelectId(ThirdOrderExt ext) {
        List<Map<String, Object>> list = new ArrayList<>();
        ext.getList().forEach(e -> {
            Map<String, Object> selections = new HashMap<>();
            selections.put("Price", numberChange(e.getOdds()));
            selections.put("SelectionId", Long.valueOf(e.getSelectId()));
            list.add(selections);
        });
        return list;
    }

    @Override
    public String updateThirdOrderStatus(OrderBean orderBean, String reason) {
        //0：待处理  1：已接单  2：拒单
        LambdaQueryWrapper<RcsCtsOrderExt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsCtsOrderExt::getOrderNo, orderBean.getOrderNo());
        RcsCtsOrderExt ext = rcsCtsOrderExtMapper.selectOne(wrapper);
        if (ext == null) {
            throw new RcsServiceException("订单信息未找到");
        }
        String third = ext.getThirdName();
        Integer cancel = ext.getCancelStatus();
        //幂等校验
        if (cancel == 1) {
            throw new RcsServiceException("订单已取消,不做重复取消处理");
        }
        ext.setStatus(ext.getStatus() + ",REJECTED");
        ext.setCancelStatus(1);
        ext.setCancelId(102);
        ext.setRemark(ext.getRemark() + "," + reason);
        rcsCtsOrderExtMapper.updateById(ext);
        log.info("::{}::业务主动取消注单,更新[rcs_cts_order_ext]表完成", ext.getOrderNo());
        return ext.getThirdNo();
    }

    @Override
    public boolean orderIsCanceled(String orderNo) {
        LambdaQueryWrapper<RcsCtsOrderExt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsCtsOrderExt::getOrderNo, orderNo);
        RcsCtsOrderExt rcsCtsOrderExt = rcsCtsOrderExtMapper.selectOne(wrapper);
        if (rcsCtsOrderExt == null) {
            return false;
        }
        return rcsCtsOrderExt.getCancelStatus() == 1;
    }

    @Override
    public void saveOrder(ThirdOrderExt thirdOrderExt) {
        String orderNo = thirdOrderExt.getList().get(0).getOrderId();
        String third = thirdOrderExt.getThird();
        try {
            RcsCtsOrderExt ext = new RcsCtsOrderExt();
            ext.setOrderNo(orderNo);
            ext.setStatus("INIT");
            if (thirdOrderExt.getPaTotalAmount() != null) {
                //分转为元
                BigDecimal paTotalAmount = thirdOrderExt.getPaTotalAmount().divide(new BigDecimal("100"), 2, RoundingMode.FLOOR);
                ext.setPaAmount(paTotalAmount.toPlainString());
            }
            BigDecimal thirdAmount = discountAmount(thirdOrderExt);
            ext.setResult(thirdOrderExt.getThirdResJson());
            ext.setCreTime(new Date());
            ext.setThirdName(third);
            ext.setThirdNo(thirdOrderExt.getThirdOrderNo());
            ext.setRemark("订单保存");
            ext.setCtsAmount(thirdAmount.toPlainString());
            rcsCtsOrderExtMapper.insert(ext);
        } catch (Exception e) {
            log.error("::{}::投注-CTS订单入库处理异常:", orderNo, e);
        }
        log.info("::{}::投注-CTS订单入库处理完成", orderNo);
    }

    @Override
    public void updateOrder(ThirdOrderExt orderExt) {
        String orderNo = null;
        try {
            orderNo = orderExt.getList().get(0).getOrderId();
            LambdaQueryWrapper<RcsCtsOrderExt> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RcsCtsOrderExt::getOrderNo, orderNo);
            //防止首次入库失败
            RcsCtsOrderExt ext = rcsCtsOrderExtMapper.selectOne(wrapper);
            if (ext == null) {
                saveOrder(orderExt);
                return;
            }
            ext.setThirdNo(orderExt.getThirdOrderNo());
            String orderStatus = orderExt.getThirdOrderStatus() == 1 ? ACCEPTED : REJECTED;
            if (ext.getStatus() == null) {
                ext.setStatus(orderStatus);
            } else {
                ext.setStatus(ext.getStatus() + "," + orderStatus);
            }
            String thirdResJson = orderExt.getThirdResJson();
            if (ext.getResult() == null) {
                ext.setResult(thirdResJson);
            } else {
                ext.setResult(ext.getResult() + ", " + thirdResJson);
            }
            ext.setUpdateTime(new Date());
            ext.setRemark(ext.getRemark() + ", 更新");
            rcsCtsOrderExtMapper.updateById(ext);
        } catch (Exception e) {
            log.error("::{}::投注-CTS订单更新处理异常:", orderNo, e);
        }
        log.info("::{}::投注-CTS订单更新处理完成", orderNo);
    }

    @Override
    public BigDecimal discountAmount(ThirdOrderExt ext) {
        String val = null;
        //加入一个判断(打折开关是否开启,如果没有开启,就不走打折逻辑)
        String switchStatus = rcsSwitchService.getMissOrderSwitchStatus();
        if (StringUtils.isNotEmpty(switchStatus) && switchStatus.equals(YesNoEnum.Y.getValue().toString())) {
            val = GTS_DEFAULT_DISCOUNT;
            log.info("::{}商户折扣功能关闭，采用默认比例:{}", ext.getBusId(), val);
            return new BigDecimal(val);
        }
        try {
            String busDiscountKey = String.format(CTS_AMOUNT_RATE, ext.getBusId());
            val = redisClient.get(busDiscountKey);
            log.info("::{}::{}投注获取到商户: {} 对应折扣率为:{}", ext.getOrderNo(), ext.getThird(), ext.getBusId(), val);
            if (StringUtils.isBlank(val)) {
                String busAllDiscountKey = CTS_AMOUNT_RATE_ALL;
                val = redisClient.get(busAllDiscountKey);
                log.info("::{}::{}投注获取到商户: {} 通用折扣率为:{}", ext.getOrderNo(), ext.getThird(), ext.getBusId(), val);
            }
            if (StringUtils.isBlank(val)) {
                val = CTS_DEFAULT_DISCOUNT;
                log.info("::{}::{}投注获取到商户: {} 默认折扣率为:{}", ext.getOrderNo(), ext.getThird(), ext.getBusId(), val);
            }
        } catch (Exception e) {
            val = CTS_DEFAULT_DISCOUNT;
            log.info("::{}::{}投注获取商户: {} 折扣率异常, 使用默认折扣率:{}", ext.getOrderNo(), ext.getThird(), ext.getBusId(), val, e);
        }
        return ext.getPaTotalAmount().divide(new BigDecimal("100"), 2, RoundingMode.FLOOR).multiply(new BigDecimal(val));
    }


    /**
     * 创建令牌
     *
     * @param userId 用户id
     */
    public String getToken(String userId) {
        Object token = RcsLocalCacheUtils.timedCache.get(String.format(USER_TOKEN_CACHE_KEY, userId));
        if (Objects.isNull(token)) {
            //创建令牌
            token = JwtUtils.getJwtToken(userId, "panda_rcs_third");
            //放入缓存
            RcsLocalCacheUtils.timedCache.put(String.format(USER_TOKEN_CACHE_KEY, userId), token);
        }
        return token.toString();
    }

    /**
     * 生成签名
     */
    public String createSign(Map<String, Object> params, String logKey) {
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        String data = gson.toJson(params);
        try {
            String sign = HMACSHA256Util.calculateHMac(config.getSharedKey(), data);
            //log.info("::{}::BC投注-参数原串={},签名={}", logKey, data, sign);
            return sign;
        } catch (Exception e) {
            log.error("::{}::投注-CTS签名异常, 参数原串={}", logKey, data, e);
            throw new RcsServiceException("投注-CTS签名异常");
        }
    }

    /**
     * 数字进行特殊转换
     */
    public BigDecimal numberChange(String number) {
        if (number.contains(".")) {
            int index = number.indexOf(".");
            int num = number.length() - index - 1;
            if (num >= 3) {
                number = number.substring(0, index + 3);
            }
        }
        return new BigDecimal(number).divide(new BigDecimal("1")).setScale(2);
    }


}
