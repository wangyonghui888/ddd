////package com.panda.sport.sdk.service.impl;
////
////import java.math.BigDecimal;
////import java.util.ArrayList;
////import java.util.HashMap;
////import java.util.List;
////import java.util.Map;
////
////import com.panda.sport.sdk.constant.SdkConstants;
////import org.slf4j.Logger;
////import org.slf4j.LoggerFactory;
////
////import com.alibaba.fastjson.JSONObject;
////import com.google.inject.Inject;
////import com.google.inject.Singleton;
////import com.panda.sport.data.rcs.api.Request;
////import com.panda.sport.data.rcs.api.Response;
////import com.panda.sport.data.rcs.dto.ExtendBean;
////import com.panda.sport.data.rcs.dto.OrderBean;
////import com.panda.sport.data.rcs.dto.OrderItem;
////import com.panda.sport.rcs.common.MqConstants;
////import com.panda.sport.rcs.constants.BaseConstants;
////import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
////import com.panda.sport.rcs.pojo.RcsBusinessMatchPaidConfig;
////import com.panda.sport.rcs.pojo.RcsBusinessSingleBetConfig;
////import com.panda.sport.sdk.exception.RcsServiceException;
////import com.panda.sport.sdk.mq.Producer;
////import com.panda.sport.sdk.util.SeriesTypeUtils;
////import com.panda.sport.sdk.util.SpliteOrderUtils;
////import com.panda.sport.sdk.vo.RcsBusinessConPlayConfig;
////import com.panda.sport.sdk.vo.RcsBusinessPlayPaidConfigVo;
////
/////**
//// * 串关限额需求
//// *
//// * @author black
//// */
////
////@Singleton
////public class SeriesTradeService {
////    private static final Logger log = LoggerFactory.getLogger(SeriesTradeService.class);
////
////    @Inject
////    ParamValidateService paramValidate;
////    @Inject
////    RcsPaidConfigServiceImp configService;
////    @Inject
////    Producer producer;
////    @Inject
////    RcsPaidConfigServiceImp rcsPaidConfigService;
////
////    public List<RcsBusinessPlayPaidConfigVo> queryMaxBetMoneyBySelect(OrderBean orderBean) {
////        //其它体育种类默认映射到足球
//////        orderBean.setSportId(1);
//////        for (OrderItem item : orderBean.getItems()) {
//////            item.setSportId(1);
//////        }
////        Map<String, Object> result = new HashMap<String, Object>();
////        List<RcsBusinessPlayPaidConfigVo> resultList = new ArrayList<RcsBusinessPlayPaidConfigVo>();
////        //各种判断
////        if (orderBean == null || orderBean.getItems() == null || orderBean.getItems().size() < 2) {
////            throw new RcsServiceException("参数错误，items参数与串关类型参数不匹配");
////        }
////        Integer seriesType = orderBean.getSeriesType();
////        if (seriesType == 1 || seriesType < 100) {
////            throw new RcsServiceException("当前业务不支持单关");
////        }
////        //获取M串N中的M
////        Integer type = SeriesTypeUtils.getSeriesType(seriesType);
////        if (type > orderBean.getItems().size()) {
////            throw new RcsServiceException("参数错误，items与seriesType不匹配");
////        }
////
////        List<Map<String, Object>> itemList = getMinTradeMoneyByOrder(orderBean);
////
////        //单注串关最大赔付值
////        RcsBusinessConPlayConfig conPlay1Config = getConPlayConfig(String.valueOf(orderBean.getTenantId()), "1");
////        //单注串关最低投注额
////        RcsBusinessConPlayConfig conPlay2Config = getConPlayConfig(String.valueOf(orderBean.getTenantId()), "2");
////        //单注串关限额占单关限额比例
////        RcsBusinessConPlayConfig conPlay3Config = getConPlayConfig(String.valueOf(orderBean.getTenantId()), "3");
////
////        Integer count = SeriesTypeUtils.getCount(seriesType, type);
////        if (count == 1) {//只返回对应的N串1的类型最大最小值
////            RcsBusinessPlayPaidConfigVo vo = getSpliteOrderByType(type, itemList, conPlay1Config, conPlay2Config, conPlay3Config);
////            putBean(vo, orderBean.getProductAmountTotal());
////            resultList.add(vo);
////        } else {//需要拆分所有单
////            RcsBusinessPlayPaidConfigVo allVo = new RcsBusinessPlayPaidConfigVo();
////            allVo.setMinBet(conPlay2Config.getPlayValue().longValue());
////            allVo.setOrderMaxPay(Long.MAX_VALUE);
////            allVo.setType(String.valueOf(seriesType));
////            for (int i = 2; i <= type; i++) {//从二串一开始计算
////                RcsBusinessPlayPaidConfigVo vo = getSpliteOrderByType(i, itemList, conPlay1Config, conPlay2Config, conPlay3Config);
////                allVo.setOrderMaxPay(Math.min(allVo.getOrderMaxPay(), vo.getOrderMaxPay()));
////                putBean(vo, orderBean.getProductAmountTotal());
////                resultList.add(vo);
////            }
////            putBean(allVo, orderBean.getProductAmountTotal());
////            resultList.add(allVo);
////        }
////        result.put("data", resultList);
////        return resultList;
////    }
////
////    private void putBean(RcsBusinessPlayPaidConfigVo vo, Long betAmount) {
////        if (betAmount != null) {//下单校验，带入真正的下单金额
////            Boolean isPass = true;
////            if (betAmount > vo.getOrderMaxPay() * 100) {
////                vo.setErrorMsg(String.format("下注金额%s大于最大限额%s", betAmount/100.0, vo.getOrderMaxPay()));
////                isPass = false;
////            }
////            if (betAmount < vo.getMinBet() * 100) {
////                vo.setErrorMsg(String.format("下注金额%s小于最小限额%s", betAmount/100.0, vo.getMinBet()));
////                isPass = false;
////            }
////            vo.setPass(isPass);
////        }
////    }
////
////    public Map<String, Object> saveOrderAndValidateMaxPaid(OrderBean orderBean) {
////        Map<String, Object> result = new HashMap<String, Object>(1);
////        List<RcsBusinessPlayPaidConfigVo> li = queryMaxBetMoneyBySelect(orderBean);
////        for (RcsBusinessPlayPaidConfigVo vo : li) {
////            if (String.valueOf(orderBean.getSeriesType()).equals(vo.getType())) {
////                result.put(orderBean.getOrderNo(), vo.getPass());
////                result.put(orderBean.getOrderNo() + "_error_msg", vo.getErrorMsg());
////                orderBean.setValidateResult(vo.getPass() ? 1 : 2);
////                for (OrderItem orderItem : orderBean.getItems()) {
////                    orderItem.setValidateResult(orderBean.getValidateResult());
////                }
////                // status返回到业务端状态 0 失败  1成功  2 待处理
////                result.put("status", 1);
////                orderBean.getExtendBean().setValidateResult(1);
////                orderBean.setValidateResult(1);
////                orderBean.setOrderStatus(1);
////                orderBean.setInfoStatus(OrderInfoStatusEnum.EARLY_PASS.getCode());
////
////                if (vo.getPass()) {
////                    //如果是滚球订单
////                    if (isScrollOrder(orderBean)) {
////                        orderBean.getExtendBean().setValidateResult(1);
////                        orderBean.setValidateResult(1);
////                        orderBean.setOrderStatus(0);
////                        orderBean.setInfoStatus(OrderInfoStatusEnum.RISK_PROCESSING.getCode());
////                        result.put("status", 2);
////                        result.put("infoStatus", OrderInfoStatusEnum.RISK_PROCESSING.getCode());
////                        result.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
////                        result.put("infoMsg", "风控接拒单处理中");
////                    }else {
////                        result.put("status", 1);
////                        result.put("infoStatus", OrderInfoStatusEnum.EARLY_PASS.getCode());
////                        result.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
////                        result.put("infoMsg", "早盘接单.");
////                    }
////                } else {
////                    orderBean.getExtendBean().setValidateResult(2);
////                    orderBean.setValidateResult(2);
////                    orderBean.setOrderStatus(2);
////                    orderBean.setInfoStatus(OrderInfoStatusEnum.EARLY_REFUSE.getCode());
////                    result.put(orderBean.getOrderNo() + "_error_msg", vo.getErrorMsg());
////                    result.put("status", 0);
////                    result.put("infoStatus", OrderInfoStatusEnum.EARLY_REFUSE.getCode());
////                    result.put("infoMsg", "风控早盘拒单:"+vo.getErrorMsg());
////                    result.put("infoCode", SdkConstants.ORDER_ERROR_CODE_RISK);
////                }
////                producer.sendMsg(MqConstants.RCS_ORDER_UPDATE, BaseConstants.SAVE_ORDER_TAGS, JSONObject.toJSONString(orderBean));
////                break;
////            }
////        }
////        return result;
////    }
////
////    /**
////     * 是否包含滚球
////     *
////     * @param orderBean
////     * @return
////     */
////    private boolean isScrollOrder(OrderBean orderBean) {
////        //出现任何滚球赛事  需走滚球接拒单流程逻辑
////        for (OrderItem item : orderBean.getItems()) {
////            if (item.getMatchType() == 2 ) {//&& item.getSportId() == 1
////                log.info("当前订单存在滚球注单，需要等待处理{}", JSONObject.toJSONString(orderBean));
////                return true;
////            }
////        }
////
////        log.info("订单号：{}，非滚球订单", orderBean.getOrderNo());
////        return false;
////    }
////
////    public Response validateOrderMaxPaid(Request<OrderBean> requestParam) {
////        Map<String, Object> result = new HashMap<String, Object>();
////        try {
////            List<RcsBusinessPlayPaidConfigVo> li = queryMaxBetMoneyBySelect(requestParam.getData());
////            for (RcsBusinessPlayPaidConfigVo vo : li) {
////                if (String.valueOf(requestParam.getData().getSeriesType()).equals(vo.getType())) {
////                    result.put(requestParam.getData().getOrderNo(), vo.getPass());
////                    result.put(requestParam.getData().getOrderNo() + "_error_msg", vo.getErrorMsg());
////                    break;
////                }
////            }
////        } catch (RcsServiceException e) {
////            return Response.error(e.getCode(), e.getMessage());
////        } catch (Exception e) {
////            log.info(e.getMessage(), e);
////            return Response.error(Response.FAIL, "服务处理失败");
////        }
////
////        return Response.success(result);
////    }
////
////    /**
////     * @return
////     * @Description 3个维度验证串关限额 返回最小值
////     * 1、单注串关限额占单关限额的比例 rcs_singleBet_config。order_max_value * 串关比例
////     * 2、单场串关最大赔付值 rcs_business_match_paid_config.match_max_con_pay_val
////     * 3、串关限额 单注串关最大赔付值
////     * @Param
////     * @Author max
////     * @Date 18:22 2020/2/20
////     **/
////    private RcsBusinessPlayPaidConfigVo getSpliteOrderByType(int index, List<Map<String, Object>> itemList, RcsBusinessConPlayConfig conPlay1Config, RcsBusinessConPlayConfig conPlay2Config,
////                                                             RcsBusinessConPlayConfig conPlay3Config) {
////        RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
////        vo.setType(index * 100 + "1");
////        vo.setOrderMaxPay(Long.MAX_VALUE);
////        vo.setMinBet(conPlay2Config.getPlayValue().longValue());
////        SpliteOrderUtils.spliteOrder(itemList, index, 0, 0, new ArrayList<Map<String, Object>>(), new SpliteOrderUtils.ApiCall<Map<String, Object>>() {
////            @Override
////            public void execute(List<Map<String, Object>> list) {
////                BigDecimal oddsBigDicimal = new BigDecimal(1);
////                //获取当前串关中单注玩法
////                Integer minMoney = Integer.MAX_VALUE;
////                //获取赛事联赛单注串关最大赔付
////                Integer minConMoney = Integer.MAX_VALUE;
////
////                for (Map<String, Object> info : list) {
////                    minMoney = Math.min(minMoney, Integer.parseInt(String.valueOf(info.get("money"))));
////                    minConMoney = Math.min(minConMoney, Integer.parseInt(String.valueOf(info.get("conMaxMoney"))));
////                    oddsBigDicimal = oddsBigDicimal.multiply(new BigDecimal(String.valueOf(info.get("odds")))).divide(new BigDecimal("100000"));
////                }
////                minConMoney = Math.min(conPlay1Config.getPlayValue().intValue(), minConMoney);
////                oddsBigDicimal = oddsBigDicimal.subtract(new BigDecimal("1"));
////                BigDecimal tempMoney = new BigDecimal(String.valueOf(minConMoney)).divide(oddsBigDicimal, 2, BigDecimal.ROUND_HALF_UP);
////
////                BigDecimal tempMoney2 = new BigDecimal(minMoney).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP)
////                        .multiply(conPlay3Config.getPlayValue()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
////
////                vo.setOrderMaxPay(Math.min(vo.getOrderMaxPay(), Math.min(tempMoney.longValue(), tempMoney2.longValue())));
////            }
////        });
////
////        return vo;
////    }
////
////    /**
////     * 获取串关配置 rcs_business_con_play_config
////     *
////     * @param busId
////     * @param type
////     * @return
////     */
////    private RcsBusinessConPlayConfig getConPlayConfig(String busId, String type) {
////
////        //获取配置数据
////        RcsBusinessConPlayConfig config = configService.getConPlayConfig(busId, type);
////        //数据库没有配置数据，返回0
////        if (config == null) {
////            log.warn(String.format("没有找到商户id:%s;type:%s;串关额度管理的配置项",
////                    busId, type));
////            return null;
////        }
////        return config;
////
////    }
////
////    /**
////     * 获取配置中每个赛事对于最小投注金额
////     */
////    private List<Map<String, Object>> getMinTradeMoneyByOrder(OrderBean orderBean) {
////        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
////        for (OrderItem item : orderBean.getItems()) {
////            Map<String, Object> map = new HashMap<String, Object>();
////            //Map<String, Object> info = paramValidate.getMatchInfo(String.valueOf(item.getMatchId()),item.getMarketId().toString(),item.getPlayOptionsId().toString());
////            Integer tournamentLevel = item.getTurnamentLevel();//未评级
//////			if(info != null && !info.containsKey("tournamentLevel")) {//获取联赛级别
//////				tournamentLevel = Integer.parseInt(String.valueOf(info.get("tournamentLevel")));
//////			}
////            ExtendBean extendBean = paramValidate.buildExtendBean(orderBean, item);
////
////            //extendBean.setSportId("1");//重置为足球，其余体种都按照足球计算
////            RcsBusinessSingleBetConfig config = getPlayConfig(extendBean, tournamentLevel);
////
////            //获取联赛单关最大赔付  sportId 全部默认转换到足球
////            RcsBusinessMatchPaidConfig matchConfig = configService.getMatchPaidConfig(String.valueOf(orderBean.getTenantId()), extendBean.getSportId(),String.valueOf(tournamentLevel));
////            if (matchConfig == null) {
////                matchConfig = configService.getMatchPaidConfig(String.valueOf(orderBean.getTenantId()), "1",String.valueOf(tournamentLevel));
////            }
////            //取不到 用默认值2000
////            map.put("money", config == null ? 200000 : config.getOrderMaxValue().intValue());
////            map.put("odds", item.getOddsValue());
////            map.put("matchId", item.getMatchId());
////            map.put("conMaxMoney", matchConfig == null || matchConfig.getMatchMaxConPayVal() == null ? 2000 : matchConfig.getMatchMaxConPayVal().divide(new BigDecimal("100")).intValue());
////            result.add(map);
////        }
////
////        if (result.size() != orderBean.getItems().size()) {
////            throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_RISK, "每个赛事只能串一单");
////        }
////
////        return result;
////    }
////
////    private RcsBusinessSingleBetConfig getPlayConfig(ExtendBean bean, Integer tournamentLevel) {
////        RcsBusinessSingleBetConfig playConfig = configService.getSingleBetConfig(bean.getBusId(), bean.getSportId(),
////                bean.getIsScroll(), bean.getPlayType(), bean.getPlayId(), String.valueOf(tournamentLevel));
////        //取不到用足球的
////        if (playConfig == null) {
////             log.info("单注最大限额未查询到sportId={},playId={},tournamentLevel={}",bean.getSportId(),bean.getPlayId(),tournamentLevel);
////             playConfig = configService.getSingleBetConfig(bean.getBusId(), "1",
////                    bean.getIsScroll(), bean.getPlayType(), bean.getPlayId(), String.valueOf(tournamentLevel));
////            log.info("单注最大限额改为足球查询结果:" + JSONObject.toJSONString(playConfig));
////        }
//<<<<<<< HEAD
//        Map<String, Object> result = new HashMap<String, Object>();
//        List<RcsBusinessPlayPaidConfigVo> resultList = new ArrayList<RcsBusinessPlayPaidConfigVo>();
//        //各种判断
//        if (orderBean == null || orderBean.getItems() == null || orderBean.getItems().size() < 2) {
//            throw new RcsServiceException("参数错误，items参数与串关类型参数不匹配");
//        }
//        Integer seriesType = orderBean.getSeriesType();
//        if (seriesType == 1 || seriesType < 100) {
//            throw new RcsServiceException("当前业务不支持单关");
//        }
//        //获取M串N中的M
//        Integer type = SeriesTypeUtils.getSeriesType(seriesType);
//        if (type > orderBean.getItems().size()) {
//            throw new RcsServiceException("参数错误，items与seriesType不匹配");
//        }
//
//        List<Map<String, Object>> itemList = getMinTradeMoneyByOrder(orderBean);
//
//        //单注串关最大赔付值
//        RcsBusinessConPlayConfig conPlay1Config = getConPlayConfig(String.valueOf(orderBean.getTenantId()), "1");
//        //单注串关最低投注额
//        RcsBusinessConPlayConfig conPlay2Config = getConPlayConfig(String.valueOf(orderBean.getTenantId()), "2");
//        //单注串关限额占单关限额比例
//        RcsBusinessConPlayConfig conPlay3Config = getConPlayConfig(String.valueOf(orderBean.getTenantId()), "3");
//
//        Integer count = SeriesTypeUtils.getCount(seriesType, type);
//        if (count == 1) {//只返回对应的N串1的类型最大最小值
//            RcsBusinessPlayPaidConfigVo vo = getSpliteOrderByType(type, itemList, conPlay1Config, conPlay2Config, conPlay3Config);
//            putBean(vo, orderBean.getProductAmountTotal());
//            resultList.add(vo);
//        } else {//需要拆分所有单
//            RcsBusinessPlayPaidConfigVo allVo = new RcsBusinessPlayPaidConfigVo();
//            allVo.setMinBet(conPlay2Config.getPlayValue().longValue());
//            allVo.setOrderMaxPay(Long.MAX_VALUE);
//            allVo.setType(String.valueOf(seriesType));
//            for (int i = 2; i <= type; i++) {//从二串一开始计算
//                RcsBusinessPlayPaidConfigVo vo = getSpliteOrderByType(i, itemList, conPlay1Config, conPlay2Config, conPlay3Config);
//                allVo.setOrderMaxPay(Math.min(allVo.getOrderMaxPay(), vo.getOrderMaxPay()));
//                putBean(vo, orderBean.getProductAmountTotal());
//                resultList.add(vo);
//            }
//            putBean(allVo, orderBean.getProductAmountTotal());
//            resultList.add(allVo);
//        }
//        result.put("data", resultList);
//        return resultList;
//    }
//
//    private void putBean(RcsBusinessPlayPaidConfigVo vo, Long betAmount) {
//        if (betAmount != null) {//下单校验，带入真正的下单金额
//            Boolean isPass = true;
//            if (betAmount > vo.getOrderMaxPay() * 100) {
//                vo.setErrorMsg(String.format("下注金额%s大于最大限额%s", betAmount/100.0, vo.getOrderMaxPay()));
//                isPass = false;
//            }
//            if (betAmount < vo.getMinBet() * 100) {
////                vo.setErrorMsg(String.format("下注金额%s小于最小限额%s", betAmount/100.0, vo.getMinBet()));
////                isPass = false;
//            }
//            vo.setPass(isPass);
//        }
//    }
//
//    public Map<String, Object> saveOrderAndValidateMaxPaid(OrderBean orderBean) {
//        Map<String, Object> result = new HashMap<String, Object>(1);
//        List<RcsBusinessPlayPaidConfigVo> li = queryMaxBetMoneyBySelect(orderBean);
//        for (RcsBusinessPlayPaidConfigVo vo : li) {
//            if (String.valueOf(orderBean.getSeriesType()).equals(vo.getType())) {
//                result.put(orderBean.getOrderNo(), vo.getPass());
//                result.put(orderBean.getOrderNo() + "_error_msg", vo.getErrorMsg());
//                orderBean.setValidateResult(vo.getPass() ? 1 : 2);
//                for (OrderItem orderItem : orderBean.getItems()) {
//                    orderItem.setValidateResult(orderBean.getValidateResult());
//                }
//                // status返回到业务端状态 0 失败  1成功  2 待处理
//                result.put("status", 1);
//                orderBean.getExtendBean().setValidateResult(1);
//                orderBean.setValidateResult(1);
//                orderBean.setOrderStatus(1);
//                orderBean.setInfoStatus(OrderInfoStatusEnum.EARLY_PASS.getCode());
//
//                if (vo.getPass()) {
//                    //如果是滚球订单
//                    if (isScrollOrder(orderBean)) {
//                        orderBean.getExtendBean().setValidateResult(1);
//                        orderBean.setValidateResult(1);
//                        orderBean.setOrderStatus(0);
//                        orderBean.setInfoStatus(OrderInfoStatusEnum.RISK_PROCESSING.getCode());
//                        result.put("status", 2);
//                        result.put("infoStatus", OrderInfoStatusEnum.RISK_PROCESSING.getCode());
//                        result.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
//                        result.put("infoMsg", "风控接拒单处理中");
//                    }else {
//                        result.put("status", 1);
//                        result.put("infoStatus", OrderInfoStatusEnum.EARLY_PASS.getCode());
//                        result.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
//                        result.put("infoMsg", "早盘接单.");
//                    }
//                } else {
//                    orderBean.getExtendBean().setValidateResult(2);
//                    orderBean.setValidateResult(2);
//                    orderBean.setOrderStatus(2);
//                    orderBean.setInfoStatus(OrderInfoStatusEnum.EARLY_REFUSE.getCode());
//                    result.put(orderBean.getOrderNo() + "_error_msg", vo.getErrorMsg());
//                    result.put("status", 0);
//                    result.put("infoStatus", OrderInfoStatusEnum.EARLY_REFUSE.getCode());
//                    result.put("infoMsg", "风控早盘拒单:"+vo.getErrorMsg());
//                    result.put("infoCode", SdkConstants.ORDER_ERROR_CODE_RISK);
//                }
//                producer.sendMsg(MqConstants.RCS_ORDER_UPDATE, BaseConstants.SAVE_ORDER_TAGS, JSONObject.toJSONString(orderBean));
//                break;
//            }
//        }
//        return result;
//    }
//
//    /**
//     * 是否包含滚球
//     *
//     * @param orderBean
//     * @return
//     */
//    private boolean isScrollOrder(OrderBean orderBean) {
//        //出现任何滚球赛事  需走滚球接拒单流程逻辑
//        for (OrderItem item : orderBean.getItems()) {
//            if (item.getMatchType() == 2 ) {//&& item.getSportId() == 1
//                log.info("当前订单存在滚球注单，需要等待处理{}", JSONObject.toJSONString(orderBean));
//                return true;
//            }
//        }
//
//        log.info("订单号：{}，非滚球订单", orderBean.getOrderNo());
//        return false;
//    }
//
//    public Response validateOrderMaxPaid(Request<OrderBean> requestParam) {
//        Map<String, Object> result = new HashMap<String, Object>();
//        try {
//            List<RcsBusinessPlayPaidConfigVo> li = queryMaxBetMoneyBySelect(requestParam.getData());
//            for (RcsBusinessPlayPaidConfigVo vo : li) {
//                if (String.valueOf(requestParam.getData().getSeriesType()).equals(vo.getType())) {
//                    result.put(requestParam.getData().getOrderNo(), vo.getPass());
//                    result.put(requestParam.getData().getOrderNo() + "_error_msg", vo.getErrorMsg());
//                    break;
//                }
//            }
//        } catch (RcsServiceException e) {
//            return Response.error(e.getCode(), e.getMessage());
//        } catch (Exception e) {
//            log.info(e.getMessage(), e);
//            return Response.error(Response.FAIL, "服务处理失败");
//        }
//
//        return Response.success(result);
//    }
//
//    /**
//     * @return
//     * @Description 3个维度验证串关限额 返回最小值
//     * 1、单注串关限额占单关限额的比例 rcs_singleBet_config。order_max_value * 串关比例
//     * 2、单场串关最大赔付值 rcs_business_match_paid_config.match_max_con_pay_val
//     * 3、串关限额 单注串关最大赔付值
//     * @Param
//     * @Author max
//     * @Date 18:22 2020/2/20
//     **/
//    private RcsBusinessPlayPaidConfigVo getSpliteOrderByType(int index, List<Map<String, Object>> itemList, RcsBusinessConPlayConfig conPlay1Config, RcsBusinessConPlayConfig conPlay2Config,
//                                                             RcsBusinessConPlayConfig conPlay3Config) {
//        RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
//        vo.setType(index * 100 + "1");
//        vo.setOrderMaxPay(Long.MAX_VALUE);
//        vo.setMinBet(conPlay2Config.getPlayValue().longValue());
//        SpliteOrderUtils.spliteOrder(itemList, index, 0, 0, new ArrayList<Map<String, Object>>(), new SpliteOrderUtils.ApiCall<Map<String, Object>>() {
//            @Override
//            public void execute(List<Map<String, Object>> list) {
//                BigDecimal oddsBigDicimal = new BigDecimal(1);
//                //获取当前串关中单注玩法
//                Integer minMoney = Integer.MAX_VALUE;
//                //获取赛事联赛单注串关最大赔付
//                Integer minConMoney = Integer.MAX_VALUE;
//
//                for (Map<String, Object> info : list) {
//                    minMoney = Math.min(minMoney, Integer.parseInt(String.valueOf(info.get("money"))));
//                    minConMoney = Math.min(minConMoney, Integer.parseInt(String.valueOf(info.get("conMaxMoney"))));
//                    oddsBigDicimal = oddsBigDicimal.multiply(new BigDecimal(String.valueOf(info.get("odds")))).divide(new BigDecimal("100000"));
//                }
//                minConMoney = Math.min(conPlay1Config.getPlayValue().intValue(), minConMoney);
//                oddsBigDicimal = oddsBigDicimal.subtract(new BigDecimal("1"));
//                BigDecimal tempMoney = new BigDecimal(String.valueOf(minConMoney)).divide(oddsBigDicimal, 2, BigDecimal.ROUND_HALF_UP);
//
//                BigDecimal tempMoney2 = new BigDecimal(minMoney).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP)
//                        .multiply(conPlay3Config.getPlayValue()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
//
//                vo.setOrderMaxPay(Math.min(vo.getOrderMaxPay(), Math.min(tempMoney.longValue(), tempMoney2.longValue())));
//            }
//        });
//
//        return vo;
//    }
//
//    /**
//     * 获取串关配置 rcs_business_con_play_config
//     *
//     * @param busId
//     * @param type
//     * @return
//     */
//    private RcsBusinessConPlayConfig getConPlayConfig(String busId, String type) {
//
//        //获取配置数据
//        RcsBusinessConPlayConfig config = configService.getConPlayConfig(busId, type);
//        //数据库没有配置数据，返回0
//        if (config == null) {
//            log.warn(String.format("没有找到商户id:%s;type:%s;串关额度管理的配置项",
//                    busId, type));
//            return null;
//        }
//        return config;
//
//    }
//
//    /**
//     * 获取配置中每个赛事对于最小投注金额
//     */
//    private List<Map<String, Object>> getMinTradeMoneyByOrder(OrderBean orderBean) {
//        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
//        for (OrderItem item : orderBean.getItems()) {
//            Map<String, Object> map = new HashMap<String, Object>();
//            //Map<String, Object> info = paramValidate.getMatchInfo(String.valueOf(item.getMatchId()),item.getMarketId().toString(),item.getPlayOptionsId().toString());
//            Integer tournamentLevel = item.getTurnamentLevel();//未评级
////			if(info != null && !info.containsKey("tournamentLevel")) {//获取联赛级别
////				tournamentLevel = Integer.parseInt(String.valueOf(info.get("tournamentLevel")));
////			}
//            ExtendBean extendBean = paramValidate.buildExtendBean(orderBean, item);
//
//            //extendBean.setSportId("1");//重置为足球，其余体种都按照足球计算
//            RcsBusinessSingleBetConfig config = getPlayConfig(extendBean, tournamentLevel);
//
//            //获取联赛单关最大赔付  sportId 全部默认转换到足球
//            RcsBusinessMatchPaidConfig matchConfig = configService.getMatchPaidConfig(String.valueOf(orderBean.getTenantId()), extendBean.getSportId(),String.valueOf(tournamentLevel));
//            if (matchConfig == null) {
//                matchConfig = configService.getMatchPaidConfig(String.valueOf(orderBean.getTenantId()), "1",String.valueOf(tournamentLevel));
//            }
//            //取不到 用默认值2000
//            map.put("money", config == null ? 200000 : config.getOrderMaxValue().intValue());
//            map.put("odds", item.getOddsValue());
//            map.put("matchId", item.getMatchId());
//            map.put("conMaxMoney", matchConfig == null || matchConfig.getMatchMaxConPayVal() == null ? 2000 : matchConfig.getMatchMaxConPayVal().divide(new BigDecimal("100")).intValue());
//            result.add(map);
//        }
//
//        if (result.size() != orderBean.getItems().size()) {
//            throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_RISK, "每个赛事只能串一单");
//        }
//
//        return result;
//    }
//
//    private RcsBusinessSingleBetConfig getPlayConfig(ExtendBean bean, Integer tournamentLevel) {
//        RcsBusinessSingleBetConfig playConfig = configService.getSingleBetConfig(bean.getBusId(), bean.getSportId(),
//                bean.getIsScroll(), bean.getPlayType(), bean.getPlayId(), String.valueOf(tournamentLevel));
//        //取不到用足球的
//        if (playConfig == null) {
//             log.info("单注最大限额未查询到sportId={},playId={},tournamentLevel={}",bean.getSportId(),bean.getPlayId(),tournamentLevel);
//             playConfig = configService.getSingleBetConfig(bean.getBusId(), "1",
//                    bean.getIsScroll(), bean.getPlayType(), bean.getPlayId(), String.valueOf(tournamentLevel));
//            log.info("单注最大限额改为足球查询结果:" + JSONObject.toJSONString(playConfig));
//        }
//        return playConfig;
//    }
//
//    public static void main(String[] args) {
//        System.out.println(Integer.parseInt("05"));
//    }
//

