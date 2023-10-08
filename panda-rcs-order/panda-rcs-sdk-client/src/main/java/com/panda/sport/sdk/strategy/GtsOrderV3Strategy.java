package com.panda.sport.sdk.strategy;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.data.rcs.api.GtsApiService;
import com.panda.sport.data.rcs.api.MtsApiService;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.GtsGetMaxStakeDTO;
import com.panda.sport.data.rcs.dto.MtsgGetMaxStakeDTO;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.limit.OrderCheckResultVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.sdk.bean.MatrixBean;
import com.panda.sport.sdk.constant.LimitRedisKeys;
import com.panda.sport.sdk.constant.RcsCacheContant;
import com.panda.sport.sdk.constant.SdkConstants;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.mq.Producer;
import com.panda.sport.sdk.sdkenum.SeriesEnum;
import com.panda.sport.sdk.service.impl.*;
import com.panda.sport.sdk.service.impl.matrix.MatrixAdapter;
import com.panda.sport.sdk.util.GuiceContext;
import com.panda.sport.sdk.util.PropertiesUtil;
import com.panda.sport.sdk.util.RcsLocalCacheUtils;
import com.panda.sport.sdk.vo.MatrixForecastVo;
import com.panda.sport.sdk.vo.RcsBusinessPlayPaidConfigVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 风控渠道
 */
@Singleton
public class GtsOrderV3Strategy implements IOrderStrategy {

    private static final Logger logger = LoggerFactory.getLogger(GtsOrderV3Strategy.class);
    @Inject
    Producer producer;
    @Inject
    ParamValidateService paramValidateService;
    @Inject
    SeriesLimitService seriesLimitService;
    //	@Inject
//	MtsApiService mtsApiService;
    @Inject
    GtsApiService gtsApiService;
    @Inject
    PropertiesUtil propertiesUtil;
    @Inject
    LimitConfigService limitConfigService;
    @Inject
    MatrixAdapter matrixAdapter;
    @Inject
    PaidService paidService;
    @Inject
    JedisClusterServer jedisClusterServer;
    @Inject
    SpecialVipService specialVipService;

    @Override
    public Map<String, Object> checkOrder(OrderBean orderBean, MatrixForecastVo matrixForecastVo) {
        Map<String, Object> resultMap = new HashMap<>();
        Integer vipLevel = orderBean.getVipLevel();
        if (vipLevel == null) {
            vipLevel = 0;
        }
        //所属的特殊限额的种类  0 无  1标签限额  2特殊百分比限额 3特殊单注单场限额 4特殊vip限额
        String key = LimitRedisKeys.getUserSpecialLimitKey(orderBean.getUid().toString());
        String type = RcsLocalCacheUtils.getValue(key, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, jedisClusterServer::hget);
        RcsQuotaBusinessLimitResVo businessLimit = limitConfigService.getBusinessLimit(orderBean.getTenantId());
        //非冠军盘
        if (orderBean.getItems().get(0).getMatchType() != 3) {
            //如果是单关 先走pa 记录额度
            if (orderBean.getSeriesType() == 1) {
                MatrixBean matrixBean = matrixAdapter.process(orderBean.getExtendBean().getSportId(), orderBean.getExtendBean().getPlayId(), orderBean.getExtendBean());
                orderBean.getExtendBean().setRecType(matrixBean.getRecType());
                if (0 == matrixBean.getRecType()) {
                    orderBean.getExtendBean().setRecVal(JSONObject.toJSONString(matrixBean.getMatrixValueArray()));
                    orderBean.getItems().get(0).setRecVal(orderBean.getExtendBean().getRecVal());
                    orderBean.getItems().get(0).setRecType(0);
                }

                if (StringUtils.isNotBlank(type) && type.equals("4")) {
                    specialVipService.saveOrderV3(orderBean.getExtendBean(), matrixBean.getStatusZip());
                } else {
                    paidService.saveOrderAndValidateV4(orderBean.getExtendBean(), matrixBean.getStatusZip(), businessLimit);
                }

            } else {
                // 串关，先校验pa额度
                OrderCheckResultVo checkResult = seriesLimitService.checkBetAmountAndPayment(orderBean, businessLimit);
                if (!checkResult.isPass()) {
                    throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, checkResult.getMsg());
                }
            }
        }
        producer.sendMsg(MqConstants.RCS_ORDER_UPDATE, BaseConstants.SAVE_ORDER_TAGS, orderBean.getOrderNo(), JSONObject.toJSONString(orderBean), orderBean.getOrderNo());
        gtsBetTicket(orderBean, matrixForecastVo);
        resultMap.put("status", 2);
        resultMap.put("infoStatus", OrderInfoStatusEnum.MTS_PROCESSING.getCode());
        resultMap.put("infoMsg", "GTS处理中");
        resultMap.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
        resultMap.put(orderBean.getOrderNo(), true);
        return resultMap;
    }

    /**
     * @return java.lang.Boolean
     * @Description mts 风控下单验证
     * @Param [extendBean]
     * mtsBet mts下注开关 1开 0关
     * 篮球、足球不支持的玩法 走MTS
     * @Author max
     * @Date 16:06 2019/12/19
     **/
    private void gtsBetTicket(OrderBean orderBean, MatrixForecastVo matrixForecastVo) {
        logger.info("下单验证-----MTS");
        //串关限额
        List<ExtendBean> list = new ArrayList<>();
        if (orderBean.getExtendBean().getSeriesType() != 1) {
            for (int i = 0; i < orderBean.getItems().size(); i++) {
                ExtendBean bean = paramValidateService.buildExtendBean(orderBean, orderBean.getItems().get(i));
//                paramValidateService.putBeanVal(bean, matrixForecastVo);
                bean.setValidateResult(1);
                list.add(bean);
            }
        } else {
            orderBean.getExtendBean().setValidateResult(1);
            list.add(orderBean.getExtendBean());
        }

        //状态更新
        orderBean.setValidateResult(1);
        orderBean.setOrderStatus(0);
        orderBean.setInfoStatus(OrderInfoStatusEnum.MTS_PROCESSING.getCode());
        producer.sendMsg(MqConstants.RCS_ORDER_UPDATE, "", orderBean.getOrderNo(), JSONObject.toJSONString(orderBean), orderBean.getOrderNo());

        //发送MTS订单MQ消息
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("seriesNum", orderBean.getSeriesType());
        map.put("ip", orderBean.getIp());
        map.put("totalMoney", orderBean.getProductAmountTotal());
        map.put("deviceType", orderBean.getDeviceType());
        map.put("acceptOdds", orderBean.getAcceptOdds());
        producer.sendMsg("rcs_riks_gts_order", "", orderBean.getOrderNo(), JSONObject.toJSONString(map), orderBean.getOrderNo());
    }

    @Override
    public List<RcsBusinessPlayPaidConfigVo> getMaxBetAmount(List<ExtendBean> extendBeanList, OrderBean orderBean) {
        //所属的特殊限额的种类  0 无  1标签限额  2特殊百分比限额 3特殊单注单场限额 4特殊vip限额
        String key = LimitRedisKeys.getUserSpecialLimitKey(orderBean.getUid().toString());
        String type = RcsLocalCacheUtils.getValue(key, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, jedisClusterServer::hget);
        RcsQuotaBusinessLimitResVo businessLimit = limitConfigService.getBusinessLimit(orderBean.getTenantId());
        //最小值
//		BetAmountLimitVo betAmountLimitVo = limitConfigService.getBetAmountLimit(extendBeanList.get(0));
        //最大限额
        Long amount;
        //串关限额  如:40011
        Integer seriesType = orderBean.getSeriesType();
        ExtendBean firstExtendBean = extendBeanList.get(0);
        List<RcsBusinessPlayPaidConfigVo> list = new ArrayList<>();
        //设置最小值
        RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
        if (seriesType != 1) {
            // GTS串关，也需要走pa限额
            List<RcsBusinessPlayPaidConfigVo> paList;
            if ("4".equals(type)) {
                paList = seriesLimitService.queryMaxBetMoneyBySelectSpecialVip(orderBean);
            } else {
                paList = seriesLimitService.queryMaxBetMoneyBySelect(orderBean, true, businessLimit);
            }
            Map<String, RcsBusinessPlayPaidConfigVo> paMap = paList.stream().collect(Collectors.toMap(RcsBusinessPlayPaidConfigVo::getType, Function.identity()));
            //取得串关数 如  2001 中的2  40011中的4  10001013中的10
            int seriesNum = SeriesEnum.getSeriesEnumBySeriesJoin(seriesType).getSeriesNum();
            //查询所有N串1的额度
            for (int i = 2; i <= seriesNum; i++) {
                Request<GtsGetMaxStakeDTO> request = new Request<>();
                request.setGlobalId(MDC.get("X-B3-TraceId") + i);
                request.setData(new GtsGetMaxStakeDTO(extendBeanList, i, false));
                try {
                    String mtsCircuitTag = getMtsCacheTag();
                    if ("0".equals(mtsCircuitTag)) amount = gtsApiService.getMaxStake(request).getData();
                    else amount = 2000L;
                } catch (Exception e) {
                    logger.info("获取mts额度异常{},{}", e.getMessage(), e);
                    amount = 2000L;
                }
                logger.info(String.format("%s 获取mts N串1 最大限额:%s,requestId:%s", i, amount, request.getGlobalId()));
                vo = new RcsBusinessPlayPaidConfigVo();
                vo.setMinBet(0L);
                vo.setOrderMaxPay(amount);
                vo.setType(i + "001");
                list.add(vo);
            }
            if (seriesNum > 2) {
                //增加N串M的额度
                Request<GtsGetMaxStakeDTO> request = new Request<GtsGetMaxStakeDTO>();
                request.setGlobalId(MDC.get("X-B3-TraceId") + seriesType);
                request.setData(new GtsGetMaxStakeDTO(extendBeanList, seriesNum, true));
                try {
                    String mtsCircuitTag = getMtsCacheTag();
                    if ("0".equals(mtsCircuitTag)) amount = gtsApiService.getMaxStake(request).getData();
                    else amount = 2000L;
                } catch (Exception e) {
                    logger.info("获取mts额度异常{},{}", e.getMessage(), e);
                    amount = 2000L;
                }
                logger.info("获取mts N串M 最大限额:{},requestId:{}", amount, request.getGlobalId());
                vo = new RcsBusinessPlayPaidConfigVo();
                vo.setMinBet(0L);
                vo.setOrderMaxPay(amount);
                vo.setType(seriesType.toString());
                list.add(vo);
            }
            list.forEach(limit -> {
                if (paMap.containsKey(limit.getType())) {
                    RcsBusinessPlayPaidConfigVo paidConfigVo = paMap.get(limit.getType());
                    if (paidConfigVo.getMinBet() > limit.getMinBet()) {
                        // 最低投注额取最大值
                        limit.setMinBet(paidConfigVo.getMinBet());
                    }
                    if (paidConfigVo.getOrderMaxPay() < limit.getOrderMaxPay()) {
                        // 最高投注额取最小值
                        limit.setOrderMaxPay(paidConfigVo.getOrderMaxPay());
                    }
                }
            });
            logger.info("获取gts单注最大限额返回" + JSONObject.toJSONString(list));
        } else { //单注
            Request<GtsGetMaxStakeDTO> request = new Request<GtsGetMaxStakeDTO>();
            request.setGlobalId(MDC.get("X-B3-TraceId") + seriesType);
            firstExtendBean.getItemBean().setBetAmount(10000L);
            request.setData(new GtsGetMaxStakeDTO(extendBeanList, 1, false));
            try {
                String mtsCircuitTag = getMtsCacheTag();
                if ("0".equals(mtsCircuitTag))
                    amount = gtsApiService.getMaxStake(request).getData();
                else amount = 2000L;
            } catch (Exception e) {
                logger.info("获取gts额度异常{},{}", e.getMessage(), e);
                amount = 2000L;
            }
            logger.info("额度查询-获取gts单注最大限额:{},requestId:{}", amount, request.getGlobalId());
            vo.setOrderMaxPay(amount);
            vo.setMinBet(0L);

            List<RcsBusinessPlayPaidConfigVo> pandaList = new ArrayList<>();
            IOrderStrategy orderStrategy;

            //如果是冠军盘
            if (orderBean.getItems().get(0).getMatchType() == 3) {
                list.add(vo);
                return list;
            }
            //特殊vip
            if (StringUtils.isNotBlank(type) && type.equals("4")) {
                orderStrategy = GuiceContext.getInstance(SpecialVipStrategy.class);
            } else {
                orderStrategy = GuiceContext.getInstance(RiskOrderV3Strategy.class);
            }
            pandaList = orderStrategy.getMaxBetAmount(extendBeanList, orderBean);
            RcsBusinessPlayPaidConfigVo pandaLimitVo = pandaList.get(0);
            logger.info("额度查询-获取pa单注最大限额:{},requestId:{}:{}", pandaLimitVo.getOrderMaxPay(), request.getGlobalId(), orderStrategy);
            if (pandaLimitVo.getOrderMaxPay() < vo.getOrderMaxPay()) {
                vo.setOrderMaxPay(pandaLimitVo.getOrderMaxPay());
            }
            list.add(vo);
        }
        return list;
    }

    @Override
    public int orderType() {
        return 12;
    }


    private String getMtsCacheTag() {
        return RcsCacheContant.MTS_CIRCUIT_TAG_CACHE.get(RcsCacheContant.MTS_CIRCUIT_TAG, cacheKey -> {
            String cacheVal = jedisClusterServer.get(cacheKey);
            if (com.panda.sport.rcs.utils.StringUtils.isBlank(cacheVal)) cacheVal = "0";
            return cacheVal;
        });
    }
}
