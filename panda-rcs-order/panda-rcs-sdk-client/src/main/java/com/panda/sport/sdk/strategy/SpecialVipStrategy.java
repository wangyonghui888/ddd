package com.panda.sport.sdk.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.sdk.bean.MatrixBean;
import com.panda.sport.sdk.constant.SdkConstants;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.exception.LogicException;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.mq.Producer;
import com.panda.sport.sdk.service.impl.*;
import com.panda.sport.sdk.service.impl.matrix.MatrixAdapter;
import com.panda.sport.sdk.util.PropertiesUtil;
import com.panda.sport.sdk.vo.MatrixForecastVo;
import com.panda.sport.sdk.vo.RcsBusinessPlayPaidConfigVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecialVipStrategy implements IOrderStrategy {

    private static final Logger logger = LoggerFactory.getLogger(SpecialVipStrategy.class);
    @Inject
    Producer producer;
    @Inject
    ParamValidateService paramValidateService;
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
    @Inject
    LuaPaidService luaPaidService;
    @Inject
    SpecialVipService specialVipService;
    @Inject
    JedisClusterServer jedisClusterServer;


    @Override
    public Map<String, Object> checkOrder(OrderBean orderBean, MatrixForecastVo matrixForecastVo ) {
        //获取商户信息
        RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit = limitConfigService.getBusinessLimit(Long.valueOf(orderBean.getExtendBean().getBusId()));
        logger.info("::{}::额度查询-VIP，商户信息:{}", orderBean.getOrderNo(), JSON.toJSONString(rcsQuotaBusinessLimit));
        // 串关限额
        if (orderBean.getSeriesType() != 1) {
            return seriesLimitService.saveOrderAndValidateMaxPaid(orderBean,rcsQuotaBusinessLimit);
        }
        //单注投注赔付限额
        BigDecimal specialSingleLimit = limitConfigService.getSpecialUserBetAmount(orderBean.getExtendBean());
        if (specialSingleLimit.longValue() < Long.MAX_VALUE) {
            BigDecimal odds = new BigDecimal(orderBean.getItems().get(0).getHandleAfterOddsValue()).subtract(new BigDecimal("1"));
            specialSingleLimit = specialSingleLimit.divide(odds, 0, BigDecimal.ROUND_UP);
        }

        if (orderBean.getOrderAmountTotal() > specialSingleLimit.longValue()) {
            logger.info("::{}::单注金额超过限额", orderBean.getOrderNo());
            throw new RcsServiceException("单注金额超过限额");
        }

        Map<String, Object> resultMap = new HashMap<>(1);

        MatrixBean matrixBean = matrixAdapter.process(orderBean.getExtendBean().getSportId(), orderBean.getExtendBean().getPlayId(), orderBean.getExtendBean());
        if (0 == matrixBean.getRecType()) {
            orderBean.getExtendBean().setRecVal(JSONObject.toJSONString(matrixBean.getMatrixValueArray()));
            orderBean.getItems().get(0).setRecVal(orderBean.getExtendBean().getRecVal());
        }
        orderBean.getExtendBean().setRecType(matrixBean.getRecType());

        Map<String, Object> result = specialVipService.saveOrderV3(orderBean.getExtendBean(), matrixBean.getStatusZip());
        logger.info("::{}::VIP用户lua脚本执行结果:{}", orderBean.getOrderNo(), result);
        //此数据保存  用于mts下单后 回滚
        String luaCacheKey = "rcs:order:lua:result:" + orderBean.getExtendBean().getItemBean().getOrderNo();
        jedisClusterServer.setex(luaCacheKey, 7 * 24 * 60 * 60, JSONObject.toJSONString(result));
        //发送到队列做回滚
        if (!"1".equals(String.valueOf(result.get("code")))) {
            producer.sendMsg("ORDER_SAVE_ROLLBACK_VIP", JSONObject.toJSONString(result));
            throw new LogicException(Integer.parseInt(String.valueOf(result.get("code"))), String.valueOf(result.get("msg")));
        }
        Boolean isSuccess = true;
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

    @Override
    public List<RcsBusinessPlayPaidConfigVo> getMaxBetAmount(List<ExtendBean> extendBeanList, OrderBean orderBean) {
        // 串关限额处理
        if (orderBean.getSeriesType() != 1) {
            return seriesLimitService.queryMaxBetMoneyBySelectSpecialVip(orderBean);
        }
        //最大限额
        List<RcsBusinessPlayPaidConfigVo> list = new ArrayList<>();
        RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
        ExtendBean firstExtendBean = extendBeanList.get(0);
        vo.setMinBet(0L);
        //当前矩阵 虚拟出来一个值，以1为单位
        firstExtendBean.setOrderMoney(100L);
        MatrixBean matrixBean = matrixAdapter.process(firstExtendBean.getSportId(), firstExtendBean.getPlayId(), firstExtendBean);
        /*** 修改到redis时,默认值为 空字符串,因此此处给空字符串. 该变量其余地方没有使用 ***/
        if (0 == matrixBean.getRecType()) {
            firstExtendBean.setRecVal(JSONObject.toJSONString(matrixBean.getMatrixValueArray()));
        }
        firstExtendBean.setRecType(matrixBean.getRecType());
        firstExtendBean.setOrderMoney(null);//还原当前值
        logger.info("::{}::额度查询-特殊会员单注，矩阵:{}", orderBean.getOrderNo(), JSONObject.toJSONString(matrixBean));
        //用户特殊限额-单注赔付
        BigDecimal specialSingleLimit = limitConfigService.getSpecialUserBetAmount(firstExtendBean);
        //用户单场限额
        BigDecimal userSingeMatchMaxPay = luaPaidService.getSpecialMatchUserAmount(firstExtendBean);
        //用户单场累计赔付
        Long specialMatchLimit = specialVipService.getUserSelectsMaxBetAmountV4(firstExtendBean, userSingeMatchMaxPay);
        if (specialMatchLimit.compareTo(specialSingleLimit.longValue()) < 0) {
            specialSingleLimit = new BigDecimal(specialMatchLimit);
        }
        specialSingleLimit = specialSingleLimit.divide(new BigDecimal("100"));
        BigDecimal odds = new BigDecimal(orderBean.getItems().get(0).getOddFinally()).subtract(new BigDecimal("1"));
        specialSingleLimit = specialSingleLimit.divide(odds, 2, BigDecimal.ROUND_DOWN);
        vo.setOrderMaxPay(specialSingleLimit.longValue());
        list.add(vo);
        return list;
    }

    /**
     * 1:风控  2：MTS
     *
     * @return
     */
    @Override
    public int orderType() {
        return 1;
    }

    /**
     * 修改状态
     *
     * @param orderBean
     * @param validateResult
     * @param orderStatus
     * @param infoStatus
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
     * @param orderBean
     * @return
     */
    public boolean orderScroll(OrderBean orderBean) {
        //是否滚球
        boolean scrollflag = false;
        for (OrderItem item : orderBean.getItems()) {
            //出现任何滚球赛事  需走滚球接拒单流程逻辑
            if (item.getMatchType() == 2) {//&& item.getSportId() == 1
                logger.info("当前订单存在滚球注单，需要等待处理{}", JSONObject.toJSONString(orderBean));
                return true;
            }
        }
        logger.info("订单号：{}，滚球判断完成scrollflag:{}", orderBean.getOrderNo(), scrollflag);
        return scrollflag;
    }

}
