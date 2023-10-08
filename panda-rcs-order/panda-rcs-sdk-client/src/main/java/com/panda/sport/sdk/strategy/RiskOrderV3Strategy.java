package com.panda.sport.sdk.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.panda.sport.data.rcs.dto.*;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.enums.DataSourceEnum;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.sdk.bean.MatrixBean;
import com.panda.sport.sdk.bean.NacosProperitesConfig;
import com.panda.sport.sdk.constant.SdkConstants;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.mq.Producer;
import com.panda.sport.sdk.service.impl.LimitConfigService;
import com.panda.sport.sdk.service.impl.PaidService;
import com.panda.sport.sdk.service.impl.ParamValidateService;
import com.panda.sport.sdk.service.impl.SeriesLimitService;
import com.panda.sport.sdk.service.impl.matrix.MatrixAdapter;
import com.panda.sport.sdk.service.impl.matrix.SecondCommon;
import com.panda.sport.sdk.util.PropertiesUtil;
import com.panda.sport.sdk.vo.MatrixForecastVo;
import com.panda.sport.sdk.vo.RcsBusinessPlayPaidConfigVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiskOrderV3Strategy implements IOrderStrategy {
    private static final Logger logger = LoggerFactory.getLogger(RiskOrderV3Strategy.class);
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
    LimitConfigService limitConfigService;
    @Inject
    SecondCommon secondCommon;

    @Override
    public Map<String, Object> checkOrder(OrderBean orderBean, MatrixForecastVo matrixForecastVo) {
        //获取商户信息
        RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit = limitConfigService.getBusinessLimit(Long.valueOf(orderBean.getExtendBean().getBusId()));
        logger.info("::{}::投注-获取商户信息成功", orderBean.getOrderNo());
        // 串关限额
        if (orderBean.getSeriesType() != 1) {
            return seriesLimitService.saveOrderAndValidateMaxPaid(orderBean, rcsQuotaBusinessLimit);
        }
        //封装矩阵数据
        MatrixBean matrixBean = matrixAdapter.process(orderBean.getExtendBean().getSportId(), orderBean.getExtendBean().getPlayId(), orderBean.getExtendBean());
        orderBean.getExtendBean().setRecType(matrixBean.getRecType());
        if (0 == matrixBean.getRecType()) {
            orderBean.getExtendBean().setRecVal(JSONObject.toJSONString(matrixBean.getMatrixValueArray()));
            orderBean.getItems().get(0).setRecVal(orderBean.getExtendBean().getRecVal());
            orderBean.getItems().get(0).setRecType(0);
        } else if (3 == matrixBean.getRecType()) {//如果是其他比分矩阵，重置类型，防止影响相关计算，后面还原
            orderBean.getExtendBean().setRecType(1);
        }
        //限额校验
        Boolean isSuccess = paidService.saveOrderAndValidateV4(orderBean.getExtendBean(), matrixBean.getStatusZip(), rcsQuotaBusinessLimit);
        Map<String, Object> resultMap = new HashMap<>(1);
        resultMap.put(orderBean.getOrderNo(), isSuccess);
        if (isSuccess) {
            //是否滚球
            if (orderScroll(orderBean) && !orderBean.getItems().get(0).getDataSourceCode().equals("GR")) {
                //是否秒接赛事
                if (secondCommon.secondRace(orderBean)) {
                    resultMap.put("status", 1);
                    resultMap.put("infoStatus", OrderInfoStatusEnum.EARLY_PASS.getCode());
                    resultMap.put("infoMsg", "赛事秒接");
                    resultMap.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
                    modifyStatus(orderBean, 1, 1, OrderInfoStatusEnum.EARLY_PASS.getCode());
                } else {
                    resultMap.put("status", 2);
                    resultMap.put("infoStatus", OrderInfoStatusEnum.RISK_PROCESSING.getCode());
                    resultMap.put("infoMsg", "风控接拒单处理中");
                    resultMap.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
                    modifyStatus(orderBean, 1, 0, OrderInfoStatusEnum.RISK_PROCESSING.getCode());
                }
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

        //这个需要后置处理，只计算矩阵，不参与矩阵计算
        if (3 == matrixBean.getRecType()) {
            orderBean.getExtendBean().setRecType(0);
            orderBean.getExtendBean().setRecVal(JSONObject.toJSONString(matrixBean.getMatrixValueArray()));
            orderBean.getItems().get(0).setRecVal(orderBean.getExtendBean().getRecVal());
        }
        paramValidateService.setResultToItemBean(orderBean.getExtendBean(), orderBean.getItems().get(0));
        producer.sendMsg(MqConstants.RCS_ORDER_UPDATE, BaseConstants.SAVE_ORDER_TAGS, orderBean.getOrderNo(), JSONObject.toJSONString(orderBean), orderBean.getOrderNo());
        return resultMap;
    }

    /**
     * 修改状态
     *
     * @param orderBean 订单信息
     * @param validateResult 校验状态
     * @param orderStatus 订单状态
     * @param infoStatus 处理状态
     */
    public void modifyStatus(OrderBean orderBean, int validateResult, int orderStatus, int infoStatus) {
        orderBean.setValidateResult(validateResult);
        orderBean.setOrderStatus(orderStatus);
        orderBean.setInfoStatus(infoStatus);
        orderBean.getExtendBean().setValidateResult(validateResult);
    }

    /**
     * 是否包含滚球
     *
     * @param orderBean 订单
     * @return 是否滚球
     */
    public boolean orderScroll(OrderBean orderBean) {
        //是否滚球
        boolean scrollFlag = false;
        for (OrderItem item : orderBean.getItems()) {
            //出现任何滚球赛事  需走滚球接拒单流程逻辑
            if (item.getMatchType() == 2) {//&& item.getSportId() == 1
                logger.info("::{}::当前订单存在滚球注单,需要等待处理", item.getOrderNo());
                return true;
            }
        }
        logger.info("::{}::滚球判断完成:{}", orderBean.getOrderNo(), scrollFlag);
        return scrollFlag;
    }

    /**
     * 检查是否是虚拟赛事
     * */
    public void virtuallyOrderHandler(OrderBean orderBean){
        List<OrderItem> items = orderBean.getItems();
        for (OrderItem orderItem : items) {
            boolean contains = SdkConstants.VIRSTUAL_SPORT.contains(Integer.valueOf(orderItem.getSportId()));
            orderItem.setSportId(contains ? -1 : orderItem.getSportId());
            orderItem.setPlayId(contains ? -1 : orderItem.getPlayId());
            orderItem.setTurnamentLevel(contains?-1:orderItem.getTurnamentLevel());
        }
    }

    /**
     * 新版本接口
     */
    @Override
    public List<RcsBusinessPlayPaidConfigVo> getMaxBetAmount(List<ExtendBean> extendBeanList, OrderBean orderBean) {
        if (orderBean.getExtendBean()!=null&& DataSourceEnum.RC.getDataSource().equals(orderBean.getExtendBean().getDataSourceCode())) {
            //红猫数据源开关
            boolean isOpen= NacosProperitesConfig.redCatLimitConfig.isOpen();
            if (!isOpen) {
                List<RcsBusinessPlayPaidConfigVo> list = new ArrayList<>();
                RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
                vo.setMinBet(0L);
                vo.setOrderMaxPay(0L);
                return list;
            }
        }
        //获取商户信息
        RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit = limitConfigService.getBusinessLimit(Long.valueOf(extendBeanList.get(0).getBusId()));
        logger.info("::{}::限额-商户信息:{}", orderBean.getUid(), JSON.toJSONString(rcsQuotaBusinessLimit));
        //串关
        if (orderBean.getSeriesType() != 1) {
            virtuallyOrderHandler(orderBean);//2412需求
            logger.info("::{}::限额-串关转换:{}",StringUtils.isBlank(orderBean.getOrderNo()) ? orderBean.getUid() : orderBean.getOrderNo(),JSON.toJSONString(orderBean.getItems()));
            return seriesLimitService.queryMaxBetMoneyBySelect(orderBean, true, rcsQuotaBusinessLimit);
        }
        //单关
        ExtendBean firstExtendBean = extendBeanList.get(0);
        //虚拟出来一个值，以1为单位
        firstExtendBean.setOrderMoney(100L);
        //如果是虚拟赛事则转变为其他赛种用于取通用限额配置
        boolean contains = SdkConstants.VIRSTUAL_SPORT.contains(Integer.valueOf(firstExtendBean.getSportId()));
        firstExtendBean.setSportId(contains?"-1":firstExtendBean.getSportId());
        firstExtendBean.setPlayId(contains?"-1":firstExtendBean.getPlayId());
        MatrixBean matrixBean = matrixAdapter.process(firstExtendBean.getSportId(), firstExtendBean.getPlayId(), firstExtendBean);
        if (0 == matrixBean.getRecType()) {
            firstExtendBean.setRecVal(JSONObject.toJSONString(matrixBean.getMatrixValueArray()));
        }
        firstExtendBean.setRecType(matrixBean.getRecType());
        firstExtendBean.setOrderMoney(null);
//        logger.info("::{}::限额-矩阵信息:{}", StringUtils.isBlank(orderBean.getOrderNo()) ? orderBean.getUid() : orderBean.getOrderNo(), JSONObject.toJSONString(matrixBean));
        //最大限额
        Long amount;
        RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
        vo.setMinBet(0L);
        amount = paidService.getUserSelectsMaxBetAmountV4(firstExtendBean, rcsQuotaBusinessLimit);
        vo.setOrderMaxPay(amount);
        List<RcsBusinessPlayPaidConfigVo> list = new ArrayList<>();
        list.add(vo);
        return list;
    }

    @Override
    public int orderType() {
        return 1;
    }
}
