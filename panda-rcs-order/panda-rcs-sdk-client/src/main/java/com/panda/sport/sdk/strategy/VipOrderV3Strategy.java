package com.panda.sport.sdk.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.sdk.bean.MatrixBean;
import com.panda.sport.sdk.constant.SdkConstants;
import com.panda.sport.sdk.mq.Producer;
import com.panda.sport.sdk.service.impl.LimitConfigService;
import com.panda.sport.sdk.service.impl.PaidService;
import com.panda.sport.sdk.service.impl.ParamValidateService;
import com.panda.sport.sdk.service.impl.SeriesLimitService;
import com.panda.sport.sdk.service.impl.matrix.MatrixAdapter;
import com.panda.sport.sdk.util.PropertiesUtil;
import com.panda.sport.sdk.vo.MatrixForecastVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class VipOrderV3Strategy extends RiskOrderV3Strategy{

    private static final Logger logger = LoggerFactory.getLogger(VipOrderV3Strategy.class);
    @Inject
    Producer producer;
    @Inject
    ParamValidateService paramValidateService;
//    @Inject
//    SeriesTradeService seriesTradeService;
    @Inject
    SeriesLimitService seriesLimitService;
    @Inject
    PaidService paidService;
    @Inject
    MatrixAdapter matrixAdapter;
    @Inject
    PropertiesUtil propertiesUtil;
    @Inject
    LimitConfigService limitConfigService;

    @Override
    public Map<String, Object> checkOrder(OrderBean orderBean, MatrixForecastVo matrixForecastVo) {
        //获取商户信息
        RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit = limitConfigService.getBusinessLimit(Long.valueOf(orderBean.getExtendBean().getBusId()));
        logger.info("::{}::额度查询-VIP，商户信息:{}", orderBean.getOrderNo(), JSON.toJSONString(rcsQuotaBusinessLimit));
        // 串关限额
        if (orderBean.getSeriesType() != 1) {
            return seriesLimitService.saveOrderAndValidateMaxPaid(orderBean,  rcsQuotaBusinessLimit);
        }

        Map<String, Object> resultMap = new HashMap<>(1);


        //MatrixBean matrixBean = matrixAdapter.process(orderBean.getExtendBean().getSportId(), orderBean.getExtendBean().getPlayId(), orderBean.getExtendBean());
        MatrixBean matrixBean = new MatrixBean(1);
        if (0 == matrixBean.getRecType()) {
            orderBean.getExtendBean().setRecVal(JSONObject.toJSONString(matrixBean.getMatrixValueArray()));
            orderBean.getItems().get(0).setRecVal(orderBean.getExtendBean().getRecVal());
        }
        orderBean.getExtendBean().setRecType(matrixBean.getRecType());

        Boolean isSuccess = true;
        //vip简单对比
        //isSuccess = paidService.getMaxBetAmount(orderBean.getExtendBean(), matrixBean.getStatusZip());
//        List<ExtendBean> extendBeanList = new ArrayList<>();
//        extendBeanList.add(orderBean.getExtendBean());
//        List<RcsBusinessPlayPaidConfigVo> list = getMaxBetAmount(extendBeanList, orderBean);
//        RcsBusinessPlayPaidConfigVo configVo = list.get(0);
//        isSuccess = configVo.getOrderMaxPay() > orderBean.getOrderAmountTotal()/100;
        resultMap.put(orderBean.getOrderNo(), isSuccess);

        if (isSuccess) {
            if (orderScroll(orderBean)) {
                resultMap.put("status", 2);
                resultMap.put("infoStatus", OrderInfoStatusEnum.RISK_PROCESSING.getCode());
                resultMap.put("infoMsg", "风控接拒单处理中");
                resultMap.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
                modifyStatus(orderBean, 1, 0, OrderInfoStatusEnum.RISK_PROCESSING.getCode());
            } else {
                resultMap.put("status", 1);
                resultMap.put("infoStatus", OrderInfoStatusEnum.EARLY_PASS.getCode());
                resultMap.put("infoMsg", "早盘接单");
                resultMap.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
                modifyStatus(orderBean, 1, 1, OrderInfoStatusEnum.EARLY_PASS.getCode());
            }
        } else {
            resultMap.put("status", 0);
            resultMap.put(orderBean.getOrderNo() + "_error_msg", "校验不通过");
            resultMap.put("infoStatus", OrderInfoStatusEnum.EARLY_REFUSE.getCode());
            resultMap.put("infoMsg", "早盘拒单:校验不通过");
            resultMap.put("infoCode", SdkConstants.ORDER_ERROR_CODE_RISK);
            modifyStatus(orderBean, 2, 2, OrderInfoStatusEnum.EARLY_REFUSE.getCode());
        }
        paramValidateService.setResultToItemBean(orderBean.getExtendBean(), orderBean.getItems().get(0));
        producer.sendMsg(MqConstants.RCS_ORDER_UPDATE, BaseConstants.SAVE_ORDER_TAGS, orderBean.getOrderNo(), JSONObject.toJSONString(orderBean), orderBean.getOrderNo());
        return resultMap;
    }




}
