package com.panda.sport.sdk.strategy;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.third.ThirdApiService;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.bts.ThirdBetParamDto;
import com.panda.sport.data.rcs.dto.limit.OrderCheckResultVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.enums.MtsIsCacheEnum;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.enums.OrderTypeEnum;
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
import com.panda.sport.sdk.util.RcsLocalCacheUtils;
import com.panda.sport.sdk.vo.MatrixForecastVo;
import com.panda.sport.sdk.vo.RcsBusinessPlayPaidConfigVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Beulah
 * @date 2023/3/20 15:59
 * @description 第三方赛事策略
 */
@Singleton
public class ThirdOrderV3Strategy implements IOrderStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ThirdOrderV3Strategy.class);

    @Inject
    JedisClusterServer jedisClusterServer;
    @Inject
    LimitConfigService limitConfigService;
    @Inject
    ThirdApiService thirdApiService;
    @Inject
    SeriesLimitService seriesLimitService;
    @Inject
    Producer producer;
    @Inject
    MatrixAdapter matrixAdapter;
    @Inject
    PaidService paidService;
    @Inject
    SpecialVipService specialVipService;
    @Inject
    ParamValidateService paramValidateService;


    /**
     * 获取最大额度
     */
    @Override
    public List<RcsBusinessPlayPaidConfigVo> getMaxBetAmount(List<ExtendBean> extendBeanList, OrderBean orderBean) {
        Integer seriesType = orderBean.getSeriesType();
        //单关
        if (seriesType == 1) {
            return getSingleMaxBetAmount(extendBeanList, orderBean);
        }
        //串关
        return getStrayMaxBetAmount(extendBeanList, orderBean);
    }

    /**
     * 单关限额
     *
     * @param extendBeanList 订单列表
     * @param orderBean      主订单
     * @return 限额响应
     */
    private List<RcsBusinessPlayPaidConfigVo> getSingleMaxBetAmount(List<ExtendBean> extendBeanList, OrderBean orderBean) {
        String userId = orderBean.getUid().toString();
        String type = getUserType(userId);
        //根据赛事判断 走哪方数据商
        String thirdMark = getThirdMark(extendBeanList.get(0).getItemBean().getPlatform(),extendBeanList.get(0).getItemBean().getDataSourceCode());
        //是否单关合并
        boolean isOpenMiltSingle = true;
        List<String> platFroms = Arrays.asList(OrderTypeEnum.GTS.getPlatFrom(), OrderTypeEnum.CTS.getPlatFrom());
        if (platFroms.contains(thirdMark)) {
            //响应慢的操盘方单关合并不走第三方请求 （OpenMiltSingle 0否 1是）
            if (orderBean.getOpenMiltSingle() != null && orderBean.getOpenMiltSingle() == 1) {
                logger.warn("::{}::额度查询-获取{}单注最大限额,单关合并模式不走三方请求", userId, thirdMark);
                isOpenMiltSingle = false;
            }
        }
        RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
        List<RcsBusinessPlayPaidConfigVo> list = new ArrayList<>();
        if (isOpenMiltSingle) {
            Long amount = getThirdMaxBet(extendBeanList, 1, false, userId, thirdMark);
            logger.info("::{}::额度查询-获取{}单注最大限额:{}", userId, thirdMark, amount);
            vo.setOrderMaxPay(amount);
            vo.setMinBet(0L);
            if (orderBean.getItems().get(0).getMatchType() == 3) {
                logger.warn("::{}::额度查询-获取{}单注最大限额,冠军赛事不往下走", userId, thirdMark);
                list.add(vo);
                return list;
            }
        }

        //2.获取pa单关限额
        IOrderStrategy orderStrategy;
        if (StringUtils.isNotBlank(type) && "4".equals(type)) {
            orderStrategy = GuiceContext.getInstance(SpecialVipStrategy.class);
        } else {
            orderStrategy = GuiceContext.getInstance(RiskOrderV3Strategy.class);
        }
        List<RcsBusinessPlayPaidConfigVo> paMaxBetAmountList = orderStrategy.getMaxBetAmount(extendBeanList, orderBean);
        Long paMaxBet = paMaxBetAmountList.get(0).getOrderMaxPay();
        logger.info("::{}::额度查询-获取pa单注最大限额：{},策略：{}", userId, paMaxBet, orderStrategy);
        if (isOpenMiltSingle) {
            //3.pa与bts取小
            if (paMaxBet < vo.getOrderMaxPay()) {
                vo.setOrderMaxPay(paMaxBet);
                logger.info("::{}::额度查询-取pa限额:{}", userId, paMaxBet);
            }
        } else {
            vo.setOrderMaxPay(paMaxBet);
        }
        list.add(vo);
        return list;
    }

    /**
     * 串关限额
     *
     * @param extendBeanList 订单列表
     * @param orderBean      主订单
     * @return 限额响应
     */
    private List<RcsBusinessPlayPaidConfigVo> getStrayMaxBetAmount(List<ExtendBean> extendBeanList, OrderBean orderBean) {
        String userId = orderBean.getUid().toString();
        String type = getUserType(userId);
        Integer seriesType = orderBean.getSeriesType();
        //根据赛事判断 走哪方数据商
        String thirdMark = getThirdMark(extendBeanList.get(0).getItemBean().getPlatform(),extendBeanList.get(0).getItemBean().getDataSourceCode());
        //串关数 如  2001 中的2  40011中的4  10001013中的10
        int seriesNum = Objects.requireNonNull(SeriesEnum.getSeriesEnumBySeriesJoin(seriesType)).getSeriesNum();
        logger.info("::{}::额度查询-获取{}串关限额,串关类型:{},串关数:{}", userId, thirdMark, seriesType, seriesNum);
        List<RcsBusinessPlayPaidConfigVo> paSeriesBetList;
        if (StringUtils.isNotBlank(type) && "4".equals(type)) {
            paSeriesBetList = seriesLimitService.queryMaxBetMoneyBySelectSpecialVip(orderBean);
            logger.info("::{}::额度查询-获取pa-vip串关限额:{}", userId, JSONObject.toJSONString(paSeriesBetList));
        } else {
            //test环境
            RcsQuotaBusinessLimitResVo businessLimit = limitConfigService.getBusinessLimit(orderBean.getTenantId());
            paSeriesBetList = seriesLimitService.queryMaxBetMoneyBySelect(orderBean, true, businessLimit);
            logger.info("::{}::额度查询-获取pa串关限额:{}", userId, JSONObject.toJSONString(paSeriesBetList));
        }
        //按串关类型分组
        Map<String, RcsBusinessPlayPaidConfigVo> paLimitMap = paSeriesBetList.stream().collect(Collectors.toMap(RcsBusinessPlayPaidConfigVo::getType, Function.identity()));
        Long amount;
        //获取所有N串1的限额
        RcsBusinessPlayPaidConfigVo vo;
        //响应对象
        List<RcsBusinessPlayPaidConfigVo> list = new ArrayList<>();

        for (int i = 2; i <= seriesNum; i++) {
            amount = getThirdMaxBet(extendBeanList, i, false, userId, thirdMark);
            logger.info("::{}::获取{}-{}001最大限额:{}", userId, thirdMark, i, amount);
            vo = new RcsBusinessPlayPaidConfigVo();
            vo.setMinBet(0L);
            vo.setOrderMaxPay(amount);
            vo.setType(i + "001");
            list.add(vo);
        }
        //如果>2 ,则存在N串M ,获取N串M的限额 如3串4 - 3004
        if (seriesNum > 2) {
            amount = getThirdMaxBet(extendBeanList, seriesNum, true, userId, thirdMark);
            logger.info("::{}::获取{}-{}最大限额:{}", userId, thirdMark, seriesType, amount);
            vo = new RcsBusinessPlayPaidConfigVo();
            vo.setMinBet(0L);
            vo.setOrderMaxPay(amount);
            vo.setType(seriesType.toString());
            list.add(vo);
        }
        list.forEach(limit -> {
            if (paLimitMap.containsKey(limit.getType())) {
                RcsBusinessPlayPaidConfigVo paidConfigVo = paLimitMap.get(limit.getType());
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
        return list;
    }

    /**
     * 投注校验
     *
     * @param orderBean        注单信息
     * @param matrixForecastVo 矩阵
     * @return 校验结果
     */
    @Override
    public Map<String, Object> checkOrder(OrderBean orderBean, MatrixForecastVo matrixForecastVo) {
        Integer matchType = orderBean.getItems().get(0).getMatchType();
        String sportId = orderBean.getExtendBean().getSportId();
        String playId = orderBean.getExtendBean().getPlayId();
        //获取商户限额配置
        RcsQuotaBusinessLimitResVo businessLimit = limitConfigService.getBusinessLimit(orderBean.getTenantId());
        //获取用户特殊限额类型
        String type = getUserType(orderBean.getUid().toString());
        Map<String, Object> resultMap = new HashMap<>();
        //非冠军盘 需要pa校验额度
        if (matchType != 3) {
            if (orderBean.getSeriesType() == 1) {
                //计算矩阵
                MatrixBean matrixBean = matrixAdapter.process(sportId, playId, orderBean.getExtendBean());
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
                OrderCheckResultVo checkResult = seriesLimitService.checkBetAmountAndPayment(orderBean, businessLimit);
                if (!checkResult.isPass()) {
                    throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, checkResult.getMsg());
                }
            }
        }
        //根据赛事判断 走哪方数据商
        String thirdMark = getThirdMark(orderBean.getExtendBean().getItemBean().getPlatform(),orderBean.getExtendBean().getDataSourceCode());
        thirdSaveOrder(orderBean, businessLimit, thirdMark);
        resultMap.put("status", 2);
        resultMap.put("infoStatus", OrderInfoStatusEnum.MTS_PROCESSING.getCode());
        resultMap.put("infoMsg", thirdMark + "处理中");
        resultMap.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
        resultMap.put(orderBean.getOrderNo(), true);
        return resultMap;
    }

    /**
     * 订单处理
     * 订单校验状态 ValidateResult   0:待处理 1：成功  2：失败 3：已取消
     * 订单处理状态 OrderStatus   0 待处理  1：成功  2：拒绝
     * 订单结果标识 InfoStatus    0：待处理 1：赛前订单接单通过 2：赛前订单拒单（需要备注原因）  3：MTS接单 4：MTS拒单 5：业务拒单 6：接拒单处理中 7：MTS处理中  8：接拒单成功 9：接拒单拒绝 10:滚球手工接拒单-接单 11:滚球手工接拒单-拒单 12：一键秒接 13：操盘取消注单
     */
    private void thirdSaveOrder(OrderBean orderBean, RcsQuotaBusinessLimitResVo businessLimit, String third) {
        //串关限额
        List<ExtendBean> list = new ArrayList<>();
        if (orderBean.getExtendBean().getSeriesType() != 1) {
            for (int i = 0; i < orderBean.getItems().size(); i++) {
                ExtendBean bean = paramValidateService.buildExtendBean(orderBean, orderBean.getItems().get(i));
                bean.setValidateResult(1);
                //去掉矩阵数据，减少传输
                bean.setRecVal(null);
                bean.getItemBean().setRecVal(null);
                list.add(bean);
            }
        } else {
            orderBean.getExtendBean().setValidateResult(1);
            orderBean.getExtendBean().setRecVal(null);
            orderBean.getExtendBean().getItemBean().setRecVal(null);
            list.add(orderBean.getExtendBean());
        }
        orderBean.setValidateResult(1);
        orderBean.setOrderStatus(0);
        orderBean.setInfoStatus(OrderInfoStatusEnum.MTS_PROCESSING.getCode());

        //发送到风控topic入库
        producer.sendMsg(MqConstants.RCS_ORDER_UPDATE, third, orderBean.getOrderNo(), JSONObject.toJSONString(orderBean), orderBean.getOrderNo());
        //熔断第三方请求
        String mtsCacheTag = getMtsCacheTag();
        if ("1".equals(mtsCacheTag)) {
            return;
        }
        //发送订单到third消费
        Map<String, Object> map = new HashMap<>();
        map.put("linkId", MDC.get("linkId"));
        map.put("merchantCode", businessLimit.getParentName());
        map.put("third", third);
        map.put("list", list);
        map.put("paTotalAmount", orderBean.getProductAmountTotal());
        //1：自动接收更好的赔率 2：自动接受任何赔率变动 3：不自动接受赔率变动
        map.put("acceptOdds", orderBean.getAcceptOdds());
        map.put("deviceType", orderBean.getDeviceType());
        map.put("currency", orderBean.getCurrencyCode());
        map.put("seriesType", orderBean.getExtendBean().getSeriesType());
        map.put("ip", orderBean.getIp());
        map.put("orderGroup", orderBean.getOrderGroup());
        map.put("secondaryLabelIdsList", orderBean.getSecondaryLabelIdsList());
        producer.sendMsg("rcs_risk_third_order", third + "_SAVE_ORDER", orderBean.getOrderNo(), JSONObject.toJSONString(map), orderBean.getOrderNo());
    }

    private String getMtsCacheTag() {
        return RcsCacheContant.MTS_CIRCUIT_TAG_CACHE.get(RcsCacheContant.MTS_CIRCUIT_TAG, cacheKey -> {
            String cacheVal = jedisClusterServer.get(cacheKey);
            if (com.panda.sport.rcs.utils.StringUtils.isBlank(cacheVal)) cacheVal = "0";
            return cacheVal;
        });
    }

    /**
     * 获取第三方最大限额
     */
    private Long getThirdMaxBet(List<ExtendBean> extendBeanList, Integer seriesNum, boolean flag, String userId, String third) {
        //设置参数
        StopWatch sw = new StopWatch();
        sw.start(third + "获取三方限额");
        Request<ThirdBetParamDto> request = new Request<>();
        request.setGlobalId(MDC.get("X-B3-TraceId"));
        request.setData(new ThirdBetParamDto(extendBeanList, seriesNum, flag, third));
        //1.获取third单注限额
        try {
            //应急开关 - 熔断三方请求
            String mtsCacheTag = getMtsCacheTag();
            if ("0".equals(mtsCacheTag)) {
                Response<Long> btsResult = thirdApiService.getMaxBetAmount(request);
                return btsResult.getData();
            }
        } catch (Exception e) {
            logger.error("::{}::额度查询-获取{}单注最大限额异常:{}", userId, third, e.getMessage(), e);
        } finally {
            sw.stop();
            logger.info("::{}::额度查询-获取{}单注最大限额耗时:{}", userId, third, sw.prettyPrint());
        }
        return 2000L;
    }

    /**
     * 获取用户特殊限额类型
     * 0 无  1标签限额  2特殊百分比限额 3特殊单注单场限额 4特殊vip限额
     */
    private String getUserType(String userId) {
        String key = LimitRedisKeys.getUserSpecialLimitKey(userId);
        return RcsLocalCacheUtils.getValue(key, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, jedisClusterServer::hget);
    }

    @Override
    public int orderType() {
        return 1;
    }

    /**
     * 获取三方标志
     *
     * @param platform 操盘平台或者数据源
     * @param dataSourceCode 数据源
     * @return 自定义三方标志
     */
    private String getThirdMark(String platform,String dataSourceCode) {
        String thirdMark = OrderTypeEnum.BTS.getPlatFrom();
        if (OrderTypeEnum.GTS.getPlatFrom().equalsIgnoreCase(platform)) {
            thirdMark = OrderTypeEnum.GTS.getPlatFrom();
        }
        if (OrderTypeEnum.CTS.getPlatFrom().equalsIgnoreCase(platform)) {
            thirdMark = OrderTypeEnum.CTS.getPlatFrom();
        }
        //红猫默认使用数据源标识 后期有操盘平台在改
        if(OrderTypeEnum.REDCAT.getDataSource().equalsIgnoreCase(dataSourceCode)){
            thirdMark = OrderTypeEnum.REDCAT.getPlatFrom();
        }
        return thirdMark;
    }

}
