package com.panda.sport.sdk.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.data.rcs.api.PIMtsApiService;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;

import com.panda.sport.data.rcs.dto.limit.OrderCheckResultVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.enums.limit.UserSpecialLimitType;
import com.panda.sport.sdk.bean.MatrixBean;
import com.panda.sport.sdk.constant.LimitRedisKeys;
import com.panda.sport.sdk.constant.SdkConstants;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.mq.Producer;
import com.panda.sport.sdk.service.impl.LimitConfigService;
import com.panda.sport.sdk.service.impl.PaidService;
import com.panda.sport.sdk.service.impl.SeriesLimitService;
import com.panda.sport.sdk.service.impl.SpecialVipService;
import com.panda.sport.sdk.service.impl.matrix.MatrixAdapter;
import com.panda.sport.sdk.vo.MatrixForecastVo;
import com.panda.sport.sdk.vo.RcsBusinessPlayPaidConfigVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Singleton
public class MtsOrderWithPIStrategy implements IOrderStrategy {

    private static final Logger logger = LoggerFactory.getLogger(MtsOrderWithPIStrategy.class);
    @Inject
    Producer producer;
    @Inject
    SeriesLimitService seriesLimitService;
    @Inject
    MatrixAdapter matrixAdapter;
    @Inject
    PaidService paidService;
    @Inject
    JedisClusterServer jedisClusterServer;
    @Inject
    SpecialVipService specialVipService;
    @Inject
    PIMtsApiService piMtsApiService;

    @Inject
    LimitConfigService limitConfigService;

    @Override
    public Map<String, Object> checkOrder(OrderBean orderBean, MatrixForecastVo matrixForecastVo ) {
        Map<String, Object> resultMap = Maps.newHashMapWithExpectedSize(5);
        // 所属的特殊限额的种类  0 无  1标签限额  2特殊百分比限额 3特殊单注单场限额 4特殊vip限额
        String key = LimitRedisKeys.getUserSpecialLimitKey(orderBean.getUid().toString());
        String type = jedisClusterServer.hget(key, "type");
        //获取商户信息
        RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit = limitConfigService.getBusinessLimit(Long.valueOf(orderBean.getExtendBean().getBusId()));
        logger.info("::{}::额度查询-MTS，商户信息:{}", orderBean.getOrderNo(), JSON.toJSONString(rcsQuotaBusinessLimit));
        // 非冠军盘
        if (orderBean.getItems().get(0).getMatchType() != 3) {
            // 如果是单关 先走pa 记录额度
            if (orderBean.getSeriesType() == 1) {
                MatrixBean matrixBean =
                        matrixAdapter.process(
                                orderBean.getExtendBean().getSportId(),
                                orderBean.getExtendBean().getPlayId(),
                                orderBean.getExtendBean());
                orderBean.getExtendBean().setRecType(matrixBean.getRecType());
                if (0 == matrixBean.getRecType()) {
                    orderBean
                            .getExtendBean()
                            .setRecVal(JSONObject.toJSONString(matrixBean.getMatrixValueArray()));
                    orderBean.getItems().get(0).setRecVal(orderBean.getExtendBean().getRecVal());
                    orderBean.getItems().get(0).setRecType(0);
                }

                if (UserSpecialLimitType.VIP.getType().equals(type)) {
                    specialVipService.saveOrderV3(orderBean.getExtendBean(), matrixBean.getStatusZip());
                } else {
                    paidService.saveOrderAndValidateV4(orderBean.getExtendBean(), matrixBean.getStatusZip(), rcsQuotaBusinessLimit);
                }

            } else {
                // 串关，先校验pa额度
                OrderCheckResultVo checkResult =
                        seriesLimitService.checkBetAmountAndPayment(orderBean, type, rcsQuotaBusinessLimit);
                if (!checkResult.isPass()) {
                    throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, checkResult.getMsg());
                }
            }
        }

        Optional.ofNullable(orderBean.getItems())
                .map(o -> o.get(0))
                .ifPresent(
                        o -> {
                            o.setMatchId(piMtsApiService.getThirdMatchSourceId(o.getMatchId()).getData());
                            o.setMarketId(piMtsApiService.getThirdMarketSourceId(o.getMarketId()).getData());
                            o.setPlayOptions(
                                    piMtsApiService.getThirdPlayOptionSourceId(o.getPlayOptionsId()).getData());
                        });

        producer.sendMsg(
                "PI_ORDER_UPDATE",
                "PI_SAVE_ORDER",
                orderBean.getOrderNo(),
                JSONObject.toJSONString(orderBean),
                orderBean.getOrderNo());
        resultMap.put("status", 2);
        resultMap.put("infoStatus", OrderInfoStatusEnum.MTS_PROCESSING.getCode());
        resultMap.put("infoMsg", "MTS处理中");
        resultMap.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
        resultMap.put(orderBean.getOrderNo(), true);
        return resultMap;
    }

    @Override
    public List<RcsBusinessPlayPaidConfigVo> getMaxBetAmount(
            List<ExtendBean> extendBeanList, OrderBean orderBean) {
        OrderItem orderItem = orderBean.getItems().get(0);
        Response<String> maxBetAmount =
                piMtsApiService.getMaxBetAmount(orderItem.getMarketId(), orderItem.getPlayOptionsId());
        RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
        vo.setOrderMaxPay(Long.valueOf(maxBetAmount.getData()));
        return Collections.singletonList(vo);
    }

    @Override
    public int orderType() {
        return 2;
    }
}
