package com.panda.sport.sdk.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.inject.Inject;
import com.panda.sport.data.rcs.api.OrderPaidApiService;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.credit.CreditLimitApiService;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.data.rcs.api.tournament.TournamentTemplateByMatchService;
import com.panda.sport.data.rcs.dto.*;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaUserSingleNoteVo;
import com.panda.sport.data.rcs.dto.limit.UserLimitReferenceResVo;
import com.panda.sport.data.rcs.dto.tournament.MatchTemplatePlayMarginDataReqVo;
import com.panda.sport.data.rcs.dto.tournament.MatchTemplatePlayMarginDataResVo;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.enums.OrderTypeEnum;
import com.panda.sport.rcs.enums.OrderStatusEnum;
import com.panda.sport.rcs.enums.PreSettleInfoStatusEnum;
import com.panda.sport.rcs.enums.limit.UserSpecialLimitType;
import com.panda.sport.rcs.log.annotion.monnitor.MonitorAnnotion;
import com.panda.sport.rcs.pojo.RcsMissedOrderConfigStatus;
import com.panda.sport.rcs.pojo.RcsOmitConfig;
import com.panda.sport.rcs.pojo.RcsSwitch;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.panda.sport.sdk.annotation.DubboService;
import com.panda.sport.sdk.bean.LNBasktballEnum;
import com.panda.sport.sdk.bean.MatrixBean;
import com.panda.sport.sdk.bean.SportIdEnum;
import com.panda.sport.sdk.constant.LimitRedisKeys;
import com.panda.sport.sdk.constant.SdkConstants;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.core.Sdk;
import com.panda.sport.sdk.exception.LogicException;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.mapper.RcsOmitConfigMapper;
import com.panda.sport.sdk.mapper.RcsSwitchMapper;
import com.panda.sport.sdk.mq.Producer;
import com.panda.sport.sdk.sdkenum.SwitchEnum;
import com.panda.sport.sdk.service.impl.matrix.MatrixAdapter;
import com.panda.sport.sdk.service.impl.matrix.SecondCommon;
import com.panda.sport.sdk.strategy.IOrderStrategy;
import com.panda.sport.sdk.strategy.RiskOrderV3Strategy;
import com.panda.sport.sdk.strategy.StrategyFactory;
import com.panda.sport.sdk.util.*;
import com.panda.sport.sdk.vo.MatrixForecastVo;
import com.panda.sport.sdk.vo.RcsBusinessPlayPaidConfigVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.redis.cache.RedisCache;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

import static com.panda.sport.sdk.constant.RedisKeys.REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY;
import static com.panda.sport.sdk.constant.RedisKeys.REDIS_RCS_SWITCH_CONFIG_KEY;
import static com.panda.sport.sdk.constant.SdkConstants.SWITCH_CODE;

/**
 * @Description 风控对外提供接口服务
 * 查询用户最大最小限额
 * 用户下单效验
 * @Param
 * @Author max
 * @Date 11:26 2019/12/11
 * @return
 **/
@DubboService
@Path("")
public class OrderPaidApiImpl implements OrderPaidApiService {
    private static final Logger logger = LoggerFactory.getLogger(OrderPaidApiImpl.class);
    @Inject
    Producer producer;
    @Inject
    ParamValidateService paramValidateService;
    @Inject
    SeriesLimitService seriesLimitService;
    @Inject
    PaidService paidService;
    @Inject
    RcsPaidConfigServiceImp rcsPaidConfigService;
    @Inject
    MatrixForecast matrixForecast;

    @Inject
    MatrixAdapter matrixAdapter;

    @Inject
    LimitConfigService limitConfigService;

    @Inject
    PropertiesUtil propertiesUtil;

    @Inject
    private LimitApiService limitApiService;
    @Inject
    private CreditLimitApiService creditLimitApiService;
    @Inject
    private JedisClusterServer jedisClusterServer;
    @Inject
    private TournamentTemplateByMatchService tournamentTemplateByMatchService;
    @Inject
    SecondCommon secondCommon;

    /**
     * @return Response
     * @Description 查询用户未登录最大最小限额
     * @Param [requestParam]
     * @Author max
     * @Date 14:52 2019/12/11
     **/
    @Override
    public Response queryInitMaxBetMoneyBySelect(Request<OrderBean> requestParam) {
        MDC.put("X-B3-TraceId", requestParam.getGlobalId());
        MDC.put("linkId", requestParam.getGlobalId());

        logger.info("未登录查询限额开始:{}", JSONObject.toJSONString(requestParam));
        Map<String, Object> result = new HashMap<>();

        List<RcsBusinessPlayPaidConfigVo> resultList = new ArrayList<RcsBusinessPlayPaidConfigVo>();
        OrderBean orderBean = requestParam.getData();
        try {
            //参数校验
            paramValidateService.checkInitMaxBetArguments(requestParam);
            //获取商户信息
            RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit = limitConfigService.getBusinessLimit(Long.valueOf(orderBean.getExtendBean().getBusId()));
            logger.info("未登录查询限额,商户信息:{}", JSON.toJSONString(rcsQuotaBusinessLimit));
            //串关
            if (requestParam.getData().getSeriesType() != 1) {
                resultList = seriesLimitService.queryMaxBetMoneyBySelect(orderBean, false, rcsQuotaBusinessLimit);
            } else {
                //最小值
                OrderItem item = requestParam.getData().getItems().get(0);
                ExtendBean extendBean = paramValidateService.buildExtendBean(orderBean, item);
                RcsQuotaUserSingleNoteVo rcsQuotaUserSingleNoteVo = limitConfigService.getRcsQuotaUserSingleNoteVoNew(extendBean, rcsQuotaBusinessLimit);

                BigDecimal limitAmount = rcsQuotaUserSingleNoteVo.getSinglePayLimit();

                RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
                vo.setPlayId((Long.valueOf(item.getPlayId())));
                vo.setMinBet(0L);
                BigDecimal oddsValue = new BigDecimal(item.getHandleAfterOddsValue().toString()).subtract(new BigDecimal("1"));
                if (oddsValue.compareTo(BigDecimal.ZERO) == 0) {
                    vo.setOrderMaxPay(limitAmount.longValue());
                } else {
                    vo.setOrderMaxPay(new BigDecimal(limitAmount.longValue()).divide(oddsValue, 2, RoundingMode.HALF_UP).longValue());
                }
                vo.setOrderMaxPay(vo.getOrderMaxPay() / 100);
                resultList.add(vo);

            }
            result.put("data", resultList);
        } catch (LogicException e) {
            logger.error("查询未登录最大最小限额异常:", e);
            return Response.error(500, e.getMessage());
        } catch (Exception e) {
            logger.error("查询未登录最大最小限额异常:", e);
            return Response.error(500, "查询未登录最大最小限额异常:");
        }
        logger.info("未登录查询限额返回:{}", JSONObject.toJSONString(result));
        return Response.success(result);
    }

    /**
     * 新限额版本 2020-09-24 之后
     *
     * @param requestParam
     * @return
     */
    @Override
    @MonitorAnnotion(code = "RPC_QUERY_MAXBET")
    public Response queryMaxBetMoneyBySelect(Request<OrderBean> requestParam) {
        Long startTime = System.currentTimeMillis();
        MDC.put("X-B3-TraceId", requestParam.getGlobalId());
        MDC.put("linkId", requestParam.getGlobalId() + " , " + requestParam.getData().getUid());
        OrderBean orderBean = requestParam.getData();
        //返回结果result对象
        Map<String, Object> result = new HashMap<>(2);
        logger.info("::{}::限额-最大限额开始:{}", orderBean.getUid(), JSONObject.toJSONString(requestParam));
        try {
            Response<String> limitResponse = limitApiService.queryOrderLimitKeyValue();
            String limitValue = limitResponse.getData();
            if (StringUtils.isNotBlank(limitValue)) {
                Map<String, String> map = JSON.parseObject(limitValue, Map.class);
                if (StringUtils.equals("1", map.get("key"))) {
                    List<RcsBusinessPlayPaidConfigVo> returnList = new ArrayList<>();
                    if (orderBean.getSeriesType() != 1) {
                        Map<Integer, Integer> tempMap = SeriesTypeUtils.getEmptyArray(orderBean.getItems().size());
                        for (Integer seriesType : tempMap.keySet()) {
                            returnList.add(this.returnData(String.valueOf(seriesType), Long.valueOf(map.get("amount"))));
                        }
                    } else {
                        RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
                        vo.setMinBet(0L);
                        vo.setOrderMaxPay(Long.valueOf(map.get("amount")));
                        returnList.add(vo);
                    }
                    result.put("data", returnList);
                    logger.info("::{}::紧急限额开关额度查询返回:{}", orderBean.getUid(), JSONObject.toJSONString(result));
                    return Response.success(result);
                }
            }
        } catch (Exception e) {
            logger.error("::{}::获取开关失败了,不影响业务:", requestParam.getGlobalId(), e);
        }

        try {
            //各种参数校验
            paramValidateService.checkMaxBetArguments(requestParam);
            //兼容
            getUserTag(orderBean);
            /**
             * bug34579、33713
             * 如果缓存为空，从数据库查一遍
             */
            limitConfigService.setUserSpecialBetLimitConfigCache(orderBean.getUid());
            orderBean.setLimitType(1);
            this.setOrderVipInfo(orderBean);
            if (isChampion(orderBean)) {
                return creditLimitApiService.queryMaxBetMoneyBySelect(requestParam);
            }
            List<ExtendBean> extendBeanList = new ArrayList<>();
            //注单列表
            List<OrderItem> orderItemList = orderBean.getItems();
            //构建ExtendBean对象
            for (OrderItem orderItem : orderItemList) {
                ExtendBean bean = paramValidateService.buildExtendBean(orderBean, orderItem);
                extendBeanList.add(bean);
            }

            logger.info("::{}::限额-构建ExtendBean对象:{}", orderBean.getUid(), JSONObject.toJSONString(extendBeanList));
            //选择限额策略
            IOrderStrategy orderStrategy = StrategyFactory.getOrderStrategy(orderBean);
            //获取最大投注限额

            List<RcsBusinessPlayPaidConfigVo> list = orderStrategy.getMaxBetAmount(extendBeanList, orderBean);
            //最高可投小于最低限额时，最高可投统一设置为0
            compareMinMax(list);
            result.put("data", list);
            logger.info("::{}::限额-耗时:{}毫秒,返回:{}", orderBean.getUid(), System.currentTimeMillis() - startTime, JSONObject.toJSONString(result));
            return Response.success(result);
        } catch (RcsServiceException e) {
            logger.error("::{}::额度查询异常:", orderBean.getUid(), e);
            return Response.error(500, e.getErrorMassage());
        } catch (LogicException e) {
            logger.error("::{}::额度查询异常:", orderBean.getUid(), e);
            return Response.error(500, e.getMsg());
        } catch (Exception e) {
            logger.error("::{}::额度查询异常:", orderBean.getUid(), e);
            return Response.error(500, "查询用户最大可投注金额异常");
        }
    }


    /**
     * 返回限额数据给业务
     *
     * @param seriesType 串关类型 2001 3001...
     * @param maxPay     最大投注金额
     * @return 限额数据
     */
    private RcsBusinessPlayPaidConfigVo returnData(String seriesType, Long maxPay) {
        RcsBusinessPlayPaidConfigVo rcsBusinessPlayPaidConfigVo = new RcsBusinessPlayPaidConfigVo();
        rcsBusinessPlayPaidConfigVo.setOrderMaxPay(maxPay);
        rcsBusinessPlayPaidConfigVo.setType(seriesType);
        rcsBusinessPlayPaidConfigVo.setMinBet(NumberConstant.LONG_ZERO);
        return rcsBusinessPlayPaidConfigVo;
    }

    /**
     * 用于兼容用户同步失败的情况,手工模拟同步 topic :panda_rcs_rpc_user
     * {"userId":383525861189988352,"username":"111111_ty4E6mKey9NH","createTime":1640436952089,"userLevel":1,"settleInAdvance":1,"merchantCode":"111111"}
     */
    private String getUserTag(OrderBean orderBean) {
        String userId = orderBean.getUid().toString();
        String userName = orderBean.getUsername();
        //先从缓存查
        String tagKey = LimitRedisKeys.getTagtKey();
        String tagId = RcsLocalCacheUtils.getValue(tagKey + userId, jedisClusterServer::get);
        if (StringUtils.isNotBlank(tagId)) {
            logger.info("::{}::缓存获取获取用户标签{}", userId, tagId);
            return tagId;
        }
        //RPC查询
        Request<Long> request = new Request<>();
        request.setData(Long.valueOf(userId));
        request.setGlobalId(MDC.get("X-B3-TraceId"));
        Response<Integer> response = limitApiService.getUserTag(request);
        logger.info("::{}::首次调用rpc获取获取用户标签：response={}", userId, JSON.toJSONString(response));
        if (response == null || response.getCode() != Response.SUCCESS) {
            logger.info("::{}::首次调用rpc获取取用户标签失败{},返回默认标签,执行插入", userId, userId);
            JSONObject json = new JSONObject();
            json.put("userId", userId);
            json.put("createTime", System.currentTimeMillis());
            json.put("userLevel", 208);
            json.put("settleInAdvance", 1);
            json.put("merchantCode", userName.substring(0, userName.indexOf("_")));
            json.put("username", userName);
            producer.sendMessage("panda_rcs_rpc_user", "rcs", userId, json.toJSONString(), new HashMap<>());
            logger.info("::{}::首次调用rpc获取取用户标签发送完成{},返回默认标签,执行插入", userId, userId);
            tagId = "1";
        } else {
            tagId = response.getData().toString();
        }
        //用户维度，设置过期时间30天
        jedisClusterServer.setex(tagKey + userId, 30 * 24 * 60 * 60, tagId);
        RcsLocalCacheUtils.timedCache.put(tagKey + userId, tagId);
        return tagId;
    }

    //最高可投小于最低限额时，最高可投统一设置为0
    private void compareMinMax(List<RcsBusinessPlayPaidConfigVo> list) {
        for (RcsBusinessPlayPaidConfigVo vo : list) {
            vo.setMinBet(0L);
            if (vo.getOrderMaxPay() < 0L) {
                vo.setOrderMaxPay(0L);
            }
        }
    }

    /**
     * @return Response
     * @Description 校验当前订单是否超过最大赔付金额
     * 该方法已废弃,用户下注会进行效验 不用单独验证
     * @Param [requestParam]
     * @Author max
     * @Date 20:42 2019/12/10
     **/
    @Override
    public Response validateOrderMaxPaid(Request<OrderBean> requestParam) {
        Map<String, Object> result = new HashMap<>(1);
        result.put(requestParam.getData().getOrderNo(), true);
        return Response.success(result);
    }

    /**
     * @return Response
     * @Description 订单矩阵效验入库保存
     * @Param [requestParam] 订单参数
     * @Author max
     * @Date 20:47 2019/12/10
     **/
    @Override
    @MonitorAnnotion(code = "RPC_SAVE_ORDER")
    public Response saveOrderAndValidateMaxPaid(Request<OrderBean> requestParam) {
        Long startTime = System.currentTimeMillis();
        MDC.put("X-B3-TraceId", requestParam.getGlobalId());
        MDC.put("linkId", requestParam.getGlobalId() + " , " + requestParam.getData().getOrderNo() + " , " + requestParam.getData().getUid());

        logger.info("::{}::投注-订单校验保存,开始:{}", requestParam.getData().getOrderNo(), JSONObject.toJSONString(requestParam));
        OrderBean orderBean = requestParam.getData();
        String orderNo = orderBean.getOrderNo();

        //bug  44669 【日常】【生产】投注后，及时注单页面经常出现不存在的注单
        //这里缓存注单信息，并修改订单状态为拒绝 暂时给44669bug提供
        String saveOrderKey = "rcs:order:save:info:" + orderNo;
        orderBean.setOrderStatus(OrderStatusEnum.ORDER_REJECT.getCode());
        jedisClusterServer.setnx(saveOrderKey, JSONObject.toJSONString(orderBean), 5 * 60 * 1000);


        //设置注单默认状态  0 待处理 1：成功  2：失败
        orderBean.setValidateResult(0);
        orderBean.setOrderStatus(0);
        orderBean.setInfoStatus(OrderInfoStatusEnum.WAITING.getCode());
        //默认风控
        int orderType = 1;
        Map<String, Object> resultMap = new HashMap<>();
        try {
            this.setOrderVipInfo(orderBean);
            //参数基本校验
            paramValidateService.checkSaveArguments(requestParam);
            orderBean.setLimitType(1);//1 普通限额  2信用限额

            //冠军玩法 去panda-rcs-credit 服务做逻辑处理
            if (isChampion(orderBean)) {
                return creditLimitApiService.saveOrderAndValidateMaxPaid(requestParam);
            }
            //非冠军玩法 走下面投注逻辑
            ExtendBean extendBean = paramValidateService.buildExtendBean(orderBean, orderBean.getItems().get(0));
            orderBean.setExtendBean(extendBean);
            //选择策略 （MTS,VIP,RISK）
            IOrderStrategy orderStrategy = StrategyFactory.getOrderStrategy(orderBean);
            orderType = orderStrategy.orderType();
            orderType = setOrderType(orderBean, orderType, false);
            boolean flag = false;
            if (isMissingOrder(orderBean, orderType) == flag) {
                if (orderType != OrderTypeEnum.BTS_PA.getValue()||orderType!=OrderTypeEnum.REDCAT.getValue()) {
                    orderStrategy = GuiceContext.getInstance(RiskOrderV3Strategy.class);
                    orderType = setOrderType(orderBean, orderType, true);
                    extendBean.setRiskChannel(String.valueOf(orderType));
                    extendBean.getItemBean().setRiskChannel(orderType);
                }
                logger.info("{}第三方数据：{}源走内部接单逻辑,orderType:{}", orderNo, orderBean.getExtendBean().getDataSourceCode(), orderType);
            } else {
                logger.info("{}第三方数据：{}源走原有的投注逻辑,orderType:{}", orderNo, orderBean.getExtendBean().getDataSourceCode(), orderType);
                extendBean.setRiskChannel(String.valueOf(orderType));
            }

            logger.info("::{}::投注-订单验证保存,extendBean:{}", extendBean.getOrderId(), JSON.toJSONString(extendBean));
            //矩阵 参数封装
            MatrixForecastVo matrixForecastVo = MatrixForecastVo.getMatrixForecastBean(orderBean.getItems().get(0));
            //限额验证与订单入库
            resultMap = orderStrategy.checkOrder(orderBean, matrixForecastVo);
        } catch (Exception e) {
            orderBean.setOrderStatus(2);
            orderBean.setInfoStatus(2);
            orderBean.setReason("订单验证失败：" + e.getMessage());
            producer.sendMsg(MqConstants.RCS_ORDER_UPDATE, BaseConstants.SAVE_ORDER_TAGS,
                    orderBean.getOrderNo(), JSONObject.toJSONString(orderBean), orderBean.getOrderNo());
            logger.error("::{}::投注-订单校验保存,异常：{}", orderNo, e.getMessage());
            String errorMsg = "";
            int infoCode = SdkConstants.ORDER_ERROR_CODE_RISK;
            if (e instanceof LogicException) {
                errorMsg += e.getMessage();
                infoCode = Integer.valueOf(((LogicException) e).getCode());
            }
            if (e instanceof RcsServiceException) {
                errorMsg += e.getMessage();
                infoCode = ((RcsServiceException) e).getCode();
            }
            resultMap.put("status", 0);
            resultMap.put(orderNo, false);
            resultMap.put(orderNo + "_error_msg", errorMsg);
            resultMap.put("orderType", orderType);
            resultMap.put("infoStatus", orderBean.getInfoStatus());
            resultMap.put("infoCode", infoCode);
            resultMap.put("infoMsg", "风控拒单:" + errorMsg);
            resultMap.put("isVip", orderBean.getVipLevel());
            return Response.success(resultMap);
        }
        resultMap.put("orderType", orderType);
        resultMap.put("isVip", orderBean.getVipLevel());
        //赔率范围处理
        Map<String, String> oddsRange = new HashMap<>();
        String defaultRange = "";
        for (OrderItem item : orderBean.getItems()) {
            //rpc 调用
            MatchTemplatePlayMarginDataResVo result = null;
            try {
                Request<MatchTemplatePlayMarginDataReqVo> request = new Request<>();
                MatchTemplatePlayMarginDataReqVo vo = new MatchTemplatePlayMarginDataReqVo();
                vo.setMatchId(item.getMatchId());
                vo.setPlayId(item.getPlayId());
                vo.setMatchType(item.getMatchType() == 1 ? 1 : 0);
                vo.setSportId(item.getSportId());
                request.setData(vo);
                Response<MatchTemplatePlayMarginDataResVo> response = tournamentTemplateByMatchService.queryMatchTemplatePlayMarginData(request);
                result = response.getData();
            } catch (Exception e) {
                logger.error("::{}::投注-rpc获取赔率变动异常:{}", orderNo, e.getMessage(), e);
            }
            String oddsScopeMatchStatus = (result != null) ? RcsLocalCacheUtils.getValue(String.valueOf(result.getOddsChangeValue()), jedisClusterServer::get, 30 * 1000L) : null;
            logger.info("::{}::投注-订单校验赔率范围,赛事配置：{}", orderNo, oddsScopeMatchStatus);
            if (StringUtils.isBlank(oddsScopeMatchStatus) || oddsScopeMatchStatus.equals("null") || oddsScopeMatchStatus.equals("0")) {
                oddsRange.put(item.getPlayOptionsId().toString(), defaultRange);
                continue;
            }
            String oddsScopePlay = (result != null) ? RcsLocalCacheUtils.getValue(String.valueOf(result.getOddsChangeStatus()), jedisClusterServer::get, 30 * 1000L) : null;
            //玩法赔率接单范围获取
            logger.info("::{}::投注-订单校验赔率范围,玩法配置：{}", orderNo, oddsScopePlay);
            if (StringUtils.isNotBlank(oddsScopePlay) && !oddsScopePlay.equals("null")) {
                oddsRange.put(item.getPlayOptionsId().toString(), oddsScopePlay);
            } else {
                oddsRange.put(item.getPlayOptionsId().toString(), defaultRange);
            }
        }
        resultMap.put("oddsRange", oddsRange);
        logger.info("::{}::投注-订单校验保存结束,耗时:{}毫秒,返回:{}", orderNo, System.currentTimeMillis() - startTime, JSONObject.toJSONString(resultMap));
//        //bug  44669 【日常】【生产】投注后，及时注单页面经常出现不存在的注单
//        //这里缓存注单信息 暂时给44669bug提供
//        String saveOrderKey = "rcs:order:save:info:" + orderBean.getOrderNo();
//        jedisClusterServer.setnx(saveOrderKey,JSONObject.toJSONString(orderBean),5 * 60 * 1000);
        return Response.success(resultMap);
    }

    /**
     * 动态漏单方法
     *
     * @param orderBean
     * @param orderType
     * @return flag
     */
    private boolean isMissingOrder(OrderBean orderBean, int orderType) {
        Long merchantsId = orderBean.getTenantId();
        RcsOmitConfig rcsOmitConfig = null;
        boolean flag = true;
        //判断是否为内部接单risk,内部接单不走漏单逻辑
        if (orderType == OrderTypeEnum.RISK.getValue()) {
            logger.info("::{}::数据源的注单:{}不是第三方数据源,不执行漏单逻辑,直接内部接单", orderBean.getItems().get(0).getDataSourceCode(), orderBean.getOrderNo());
            return true;
        }
        //目前BTS是全部漏单的
        if (orderType == OrderTypeEnum.BTS_PA.getValue()) {
            logger.info("::{}::数据源的注单:{},目前全部不发往数据商,不执行漏单逻辑,全部直接内部接单", orderBean.getItems().get(0).getDataSourceCode(), orderBean.getOrderNo());
            return false;
        }
        //C01(RTS)都走第三方
        if (orderType == OrderTypeEnum.REDCAT.getValue()) {
            return true;
        }
        //获取动态漏单总开关状态
        String switchStatus = getMissOrderSwitchStatus(merchantsId);
        if (StringUtils.isNotEmpty(switchStatus) && SwitchEnum.OPEN.getId().equals(Integer.valueOf(switchStatus)) && ObjectUtils.isNotEmpty(orderBean)) {
            logger.info("商户:{}总开关状态为开,折扣功能失效,注单:{}执行动态漏单逻辑", orderBean.getOrderNo(), merchantsId);
            //获取对应商户的漏单配置数据
            rcsOmitConfig = getRcsOmitConfigValue(orderBean);
            if (ObjectUtil.isNotEmpty(rcsOmitConfig) && rcsOmitConfig.getBqStatus().equals(SwitchEnum.OPEN.getId())
                    || rcsOmitConfig.getQjStatus().equals(SwitchEnum.OPEN.getId())) {
                //标签开关和全局开关有一个开启就进入(1是开,2是关)
                List<String> levelIdList = null;
                //第一种情况:(标签开关是开,全局漏单开关是关,只要满足设定的标签100%不传输给数据商)
                if (rcsOmitConfig.getBqStatus().equals(SwitchEnum.OPEN.getId()) && rcsOmitConfig.getQjStatus().equals(SwitchEnum.CLOSE.getId())) {
                    levelIdList = getLevelId(rcsOmitConfig.getLevelId());
                    if (CollectionUtils.isNotEmpty(levelIdList) && levelIdList.contains(Integer.toString(orderBean.getUserTagLevel()))) {
                        logger.info("::{}::商户用户风控标签漏单开关开启,传输比例漏单开关关闭,用户风控标签100%漏单,走内部接单逻辑,折扣功能失效,注单用户标签为:{},注单号:{},", merchantsId, orderBean.getUserTagLevel(), orderBean.getOrderNo());
                        flag = false;
                    }
                    //第二种情况:(标签开关是关,全局开关是开)
                } else if (rcsOmitConfig.getBqStatus().equals(SwitchEnum.CLOSE.getId()) && rcsOmitConfig.getQjStatus().equals((SwitchEnum.OPEN.getId()))) {
                    if (!isMinMoney(rcsOmitConfig, orderBean)) {
                        logger.info("::{}::商户用户风控标签漏单开关关闭,传输比例漏单开关开启,商户金额配置区间传输比例随机漏单生效,走内部接单逻辑,折扣功能失效,注单号:{},", merchantsId, orderBean.getOrderNo());
                        flag = false;
                    }
                    //第三种情况(标签开关是开,全局开关是也开)
                } else if (rcsOmitConfig.getBqStatus().equals(SwitchEnum.OPEN.getId()) && rcsOmitConfig.getQjStatus().equals(SwitchEnum.OPEN.getId())) {
                    //获取所有得设定得标签ID
                    levelIdList = getLevelId(rcsOmitConfig.getLevelId());
                    //判断注单得用户的标签是否在设定的标签集合中(是否满足标签开关的逻辑,满足就不走全局漏单开关逻辑)
                    if (CollectionUtils.isNotEmpty(levelIdList) && levelIdList.contains(Integer.toString(orderBean.getUserTagLevel()))) {
                        logger.info("::{}::商户用户风控标签漏单开关开启,传输比例漏单开关开启,用户风控标签100%漏单,走内部接单逻辑,折扣功能失效,注单用户标签为:{},注单号:{},", merchantsId, orderBean.getUserTagLevel(), orderBean.getOrderNo());
                        //①走内部接单逻辑
                        flag = false;
                        //2.不满足再走全局漏单逻辑
                    } else {
                        if (!isMinMoney(rcsOmitConfig, orderBean)) {
                            logger.info("::{}::商户用户风控标签漏单开关开启,传输比例漏单开关开启,商户金额配置区间传输比例随机漏单生效,走内部接单逻辑,折扣功能失效,注单号:{},", merchantsId, orderBean.getOrderNo());
                            flag = false;
                        }
                    }
                }
            }
            if (flag) {
                logger.info("::{}::数据源的注单:{}不漏单也不打折,走正常的数据商投注流程,折扣功能失效", orderBean.getItems().get(0).getDataSourceCode(), orderBean.getOrderNo());
            } else {
                logger.info("::{}::数据源的注单:{}漏单成功,走内部接单投注流程,折扣功能失效", orderBean.getItems().get(0).getDataSourceCode(), orderBean.getOrderNo());
            }
            return flag;
        } else {
            logger.info("商户:{}总开关状态为关,折扣功能生效,不走动态漏单逻辑", merchantsId);
            return true;
        }
    }

    /**
     * 获取对应商户的动态漏单配置
     *
     * @param orderBean
     * @return rcsOmitConfig
     */
    public RcsOmitConfig getRcsOmitConfigValue(OrderBean orderBean) {
        RcsOmitConfig rcsOmitConfig = null;
        String rcsOmitConfigValue = null;
        try {
            //先从缓存中取到对应
            String missedOrderConfigurationKey = LimitRedisKeys.getMissedOrderConfigurationKey(orderBean.getTenantId());
            rcsOmitConfigValue = RcsLocalCacheUtils.getValue(REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY, orderBean.getTenantId().toString(), jedisClusterServer::hget);
            //缓存中没有,去redis中查
            if (StringUtils.isEmpty(rcsOmitConfigValue)) {
                rcsOmitConfigValue = jedisClusterServer.hget(REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY, orderBean.getTenantId().toString());
                //redis中获取数据不为空
                if (StringUtils.isNotEmpty(rcsOmitConfigValue)) {
                    rcsOmitConfig = JSONObject.parseObject(rcsOmitConfigValue, RcsOmitConfig.class);
                    //重新插入本地缓存
                    RcsLocalCacheUtils.timedCache.put(REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY + rcsOmitConfig.getMerchantsId(), JSON.toJSONString(rcsOmitConfig));
                } else {
                    //db中查找数据
                    RcsOmitConfigMapper rcsOmitConfigMapper = SpringContextUtils.getBeanByClass(RcsOmitConfigMapper.class);
                    //根据商户ID获取对应商户的配置数据
                    rcsOmitConfig = (rcsOmitConfigMapper.selectOne(new LambdaQueryWrapper<RcsOmitConfig>().eq(RcsOmitConfig::getMerchantsId, orderBean.getTenantId())));
                    jedisClusterServer.hset(REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY, missedOrderConfigurationKey, JSON.toJSONString(rcsOmitConfig));
                    jedisClusterServer.expire(missedOrderConfigurationKey, 60 * 60 * 24 * 10);//TTL设置10天
                    RcsLocalCacheUtils.timedCache.put(REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY + rcsOmitConfig.getMerchantsId(), JSON.toJSONString(rcsOmitConfig));
                }
            } else {
                rcsOmitConfig = JSONObject.parseObject(rcsOmitConfigValue, RcsOmitConfig.class);
            }

        } catch (Exception e) {
            logger.info("缓存和db中都没有对应的商户:{}的配置相关信息,注单号:{},既不漏单,也不打折,走正常的数据商注单逻辑", orderBean.getTenantId(), orderBean.getOrderNo());
        }
        logger.info("商户:{}全局漏单配置相关信息:{}", orderBean.getTenantId(), rcsOmitConfig);
        return rcsOmitConfig;
    }

    /**
     * 获取动态漏单总开关(1.开,2关)
     *
     * @param merchantsId
     * @return switchStatus
     */
    private String getMissOrderSwitchStatus(Long merchantsId) {
        String discountSwitchKey = String.format(REDIS_RCS_SWITCH_CONFIG_KEY, SWITCH_CODE);
        String switchStatus = jedisClusterServer.get(discountSwitchKey);
        RcsSwitch rcsSwitch = null;
        if (StringUtils.isEmpty(switchStatus)) {
            RcsSwitchMapper rcsSwitchMapper = SpringContextUtils.getBeanByClass(RcsSwitchMapper.class);
            rcsSwitch = rcsSwitchMapper.selectOne(new LambdaQueryWrapper<RcsSwitch>()
                    .eq(RcsSwitch::getSwitchCode, SWITCH_CODE));
            if (ObjectUtils.isNotEmpty(rcsSwitch)) {
                switchStatus = String.valueOf(rcsSwitch.getSwitchStatus());
                jedisClusterServer.setnx(discountSwitchKey, switchStatus, 60 * 60 * 24 * 10);
            } else {
                //提示获取总开关数据缓存和db中都没有数据
                throw new RcsServiceException("商户" + merchantsId + "折扣利率(动态漏单总开关)全局开关缓存和db中都没有数据");
            }
        }
        logger.info("商户:{}总开关的状态:{}", merchantsId, switchStatus);
        return switchStatus;
    }

    /**
     * 获取商户配置所有的用户标签
     *
     * @param levelId
     * @return levelIdList
     */
    private List<String> getLevelId(String levelId) {
        List<String> levelIdList = new ArrayList<>();
        if (StringUtils.isEmpty(levelId)) {
            return levelIdList;
        }
        String[] levelIdArray = levelId.split(",");
        for (String levelIds : levelIdArray) {
            String levelIdValue = levelIds;
            levelIdList.add(levelIdValue);
        }
        return levelIdList;
    }

    /**
     * 判断注单金额是否在商户设定得金额区间
     *
     * @param rcsOmitConfig
     * @param orderBean
     * @return flag
     */
    private boolean isMinMoney(RcsOmitConfig rcsOmitConfig, OrderBean orderBean) {
        boolean flag = false;
        if (ObjectUtil.isNotEmpty(orderBean.getItems()) && ObjectUtil.isNotEmpty(rcsOmitConfig)) {
            //注单金额/100
            BigInteger betAmount = BigInteger.valueOf(orderBean.getItems().get(0).getBetAmount()).divide(BigInteger.valueOf(100));
            //开始区间配置金额
            BigInteger minMoney = rcsOmitConfig.getMinMoney();
            //结束区间配置金额
            BigInteger maxMoney = rcsOmitConfig.getMaxMoney();
            //默认比例100%
            BigDecimal defaultScale = new BigDecimal(100).setScale(2, RoundingMode.HALF_UP);
            //1.配置的投注开启区间和结束区间为null,或者都为0,直接按照传输比例给数据商todo
            if ((Objects.isNull(minMoney) && Objects.isNull(maxMoney)) ||
                    (ObjectUtil.isNotEmpty(minMoney) && minMoney.compareTo(BigInteger.ZERO) == 0) &&
                            (ObjectUtil.isNotEmpty(maxMoney) && maxMoney.compareTo(BigInteger.ZERO) == 0)) {
                flag = calculateRandomProbability(rcsOmitConfig, orderBean, defaultScale);
                //2.开始区间=null,结束区间>0
            } else if (ObjectUtil.isEmpty(minMoney) && (ObjectUtils.isNotEmpty(maxMoney) && maxMoney.compareTo(BigInteger.ZERO) > 0)
                    || (minMoney.compareTo(BigInteger.ZERO) == 0) && (maxMoney.compareTo(BigInteger.ZERO) > 0)) {
                //判断注单的金额是否<=配置的结束区间金额
                if (betAmount.compareTo(maxMoney) <= 0) {
                    //根据传输比例跟随机概率比较是否随机数>=配置的比例
                    flag = calculateRandomProbability(rcsOmitConfig, orderBean, defaultScale);
                } else {
                    flag = true;
                }
                //3.开始区间>0，结束区间null
            } else if ((ObjectUtils.isNotEmpty(minMoney) && minMoney.compareTo(BigInteger.ZERO) > 0) && ObjectUtil.isEmpty(maxMoney)
                    || (maxMoney.compareTo(BigInteger.ZERO) == 0) && (minMoney.compareTo(BigInteger.ZERO) > 0)) {
                //判断投注金额是否>=开始区间配置
                if (betAmount.compareTo(minMoney) >= 0) {
                    //根据漏单比例跟随机概率比较是否随机数>=配置的比例
                    flag = calculateRandomProbability(rcsOmitConfig, orderBean, defaultScale);
                } else {
                    flag = true;
                }
                //4.开始区间>0&&结束区间>0
            } else if (minMoney.compareTo(BigInteger.ZERO) > 0 && maxMoney.compareTo(BigInteger.ZERO) > 0) {
                if (betAmount.compareTo(minMoney) >= 0 && betAmount.compareTo(maxMoney) <= 0) {
                    flag = calculateRandomProbability(rcsOmitConfig, orderBean, defaultScale);
                } else {
                    flag = true;
                }
            }
            logger.info("商户:{}:配置的开始区间:{}:结束区间:{},注单金额:{}", orderBean.getTenantId(), minMoney, maxMoney, betAmount);

        }
        return flag;
    }

    /**
     * 注单传输随机概率计算
     *
     * @param rcsOmitConfig
     * @param orderBean
     * @param defaultScale
     * @return
     */
    private boolean calculateRandomProbability(RcsOmitConfig rcsOmitConfig, OrderBean orderBean, BigDecimal defaultScale) {
        boolean flag = false;
        if (ObjectUtils.isNotEmpty(rcsOmitConfig) && ObjectUtils.isNotEmpty(orderBean) && orderBean.getItems().size() > 0 && null != defaultScale) {
            //配置的传输比例
            BigDecimal targetLeakageRate = rcsOmitConfig.getVolumePercentage().setScale(2, RoundingMode.HALF_UP);
            String dataSourceCode = orderBean.getItems().get(0).getDataSourceCode();
            //判断传输比例是否为100%
            if (defaultScale.compareTo(targetLeakageRate) == 0) {
                logger.info("商户:{}传输比例为100%,数据源:{}的注单:{}传给数据商", orderBean.getTenantId(), dataSourceCode, orderBean.getOrderNo());
                flag = true;
                //漏单比例为0(区间有值在区间范围得全部不传数据商,不在区间范围得全部传,如果区间是NULL,漏单比例为0,全部不传数据商
            } else if (new BigDecimal(0).setScale(2, RoundingMode.HALF_UP).compareTo(targetLeakageRate) == 0) {
                logger.info("商户:{}传输比例为0%,区间为NULL值或者注单金额在区间范围的注单,不传数据商,数据源:{}的注单:{}漏单", orderBean.getTenantId(), dataSourceCode, orderBean.getOrderNo());
                return false;
            } else {
                Random random = new Random();
                //生成0-1的随机数
                BigDecimal randomNumber = BigDecimal.valueOf(random.nextDouble()).setScale(2, RoundingMode.HALF_UP);
                if (randomNumber.multiply(new BigDecimal(100)).compareTo(targetLeakageRate) <= 0) {
                    flag = true;
                }
                logger.info("商户:{}配置的传输的比例为:{}:随机算法传输的概率为:{},如果随机算法概率<=配置传输比例,注单:{}传输给数据商", orderBean.getTenantId(), targetLeakageRate, randomNumber, orderBean.getOrderNo());

            }
        }
        return flag;
    }


    /**
     * @return Response
     * @Description 提供给业务-提前结算接口
     * @Param [requestParam] 订单参数
     * @Author Eamon
     * @Date 2023年7月1日21:48:31
     **/
    /*@Override*/
    @MonitorAnnotion(code = "RPC_PRE_SETTLE")
    public Response<PreOrderRequest> preSettleOrder(Request<PreOrderRequest> requestParam) {
        long startTime = System.currentTimeMillis();
        MDC.put("X-B3-TraceId", requestParam.getGlobalId());
        MDC.put("linkId",
                requestParam.getGlobalId() + " , " + requestParam.getData().getOrderNo() + " , " + requestParam.getData().getUserId());

        logger.info("::{}::提前结算-订单校验保存,开始:{}", requestParam.getData().getOrderNo(), JSONObject.toJSONString(requestParam));
        PreOrderRequest preOrderBean = requestParam.getData();
        String orderNo = preOrderBean.getOrderNo();
        //设置注单默认状态  0 待处理 1：成功  2：失败
        preOrderBean.setOrderStatus(OrderStatusEnum.ORDER_WAITING.getCode());
        preOrderBean.setInfoStatus(PreSettleInfoStatusEnum.WAITING.getCode());
        Map<String, Object> resultMap = new HashMap<>(8);
        //默认风控
        try {
            //参数基本校验
            paramValidateService.checkPreSettleArguments(requestParam);
            preOrderBean.setReqTime(System.currentTimeMillis());
            //秒接判断,提前结算订单入库
            checkPreSettleOrder(preOrderBean, resultMap);
        } catch (Exception e) {
            logger.error("::{}::投注-订单校验保存,异常：{}", orderNo, e.getMessage(), e);
            String reason = PreSettleInfoStatusEnum.CHECK_FAIL.getMode();
            if (e instanceof LogicException) {
                reason += e.getMessage();
            }
            if (e instanceof RcsServiceException) {
                reason += e.getMessage();
            }
            preOrderBean.setOrderStatus(2);
            preOrderBean.setInfoStatus(PreSettleInfoStatusEnum.CHECK_FAIL.getCode());
            preOrderBean.setReason(reason);
            producer.sendMsg(MqConstants.RCS_PRE_SETTLE_UPDATE, MqConstants.RCS_PRE_SETTLE_UPDATE_TAG, orderNo, JSONObject.toJSONString(preOrderBean), orderNo);
            return Response.success(preOrderBean);
        }
        logger.info("::{}::投注-订单校验保存结束,耗时:{}毫秒,返回:{}", orderNo, System.currentTimeMillis() - startTime, JSONObject.toJSONString(resultMap));
        return Response.success(preOrderBean);
    }

    public void checkPreSettleOrder(PreOrderRequest preOrderBean, Map<String, Object> resultMap) {
        resultMap.put(preOrderBean.getOrderNo(), true);
        if (preOrderScroll(preOrderBean)) {
            Long matchId = preOrderBean.getDetailList().get(0).getMatchId();
            String period = secondCommon.getMatchPeriodByMatchId(matchId);
            if ("31".equalsIgnoreCase(period)) {
                /*中场休息秒接*/
                resultMap.put("status", OrderStatusEnum.ORDER_ACCEPT.getCode());
                resultMap.put("infoStatus", PreSettleInfoStatusEnum.HALFTIME_PASS.getCode());
                resultMap.put("infoMsg", PreSettleInfoStatusEnum.HALFTIME_PASS.getMode());
                resultMap.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
                preOrderBean.setOrderStatus(NumberUtils.INTEGER_ONE);
                preOrderBean.setInfoStatus(PreSettleInfoStatusEnum.HALFTIME_PASS.getCode());
            } else if (secondCommon.recentGoing(matchId)) {
                /*即将开赛秒接*/
                resultMap.put("status", OrderStatusEnum.ORDER_ACCEPT.getCode());
                resultMap.put("infoStatus", PreSettleInfoStatusEnum.UPCOMING_PASS.getCode());
                resultMap.put("infoMsg", PreSettleInfoStatusEnum.UPCOMING_PASS.getMode());
                resultMap.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
                preOrderBean.setOrderStatus(NumberUtils.INTEGER_ONE);
                preOrderBean.setInfoStatus(PreSettleInfoStatusEnum.UPCOMING_PASS.getCode());
            } else {
                /* 风控接拒处理中*/
                resultMap.put("status", OrderStatusEnum.ORDER_WAITING.getCode());
                resultMap.put("infoStatus", PreSettleInfoStatusEnum.RISK_PROCESSING.getCode());
                resultMap.put("infoMsg", PreSettleInfoStatusEnum.RISK_PROCESSING.getMode());
                resultMap.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
                preOrderBean.setOrderStatus(NumberUtils.INTEGER_ZERO);
                preOrderBean.setInfoStatus(PreSettleInfoStatusEnum.RISK_PROCESSING.getCode());
            }
        } else {
            /* 早盘秒接*/
            resultMap.put("status", OrderStatusEnum.ORDER_ACCEPT.getCode());
            resultMap.put("infoStatus", PreSettleInfoStatusEnum.EARLY_PASS.getCode());
            resultMap.put("infoMsg", PreSettleInfoStatusEnum.EARLY_PASS.getMode());
            resultMap.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
            preOrderBean.setOrderStatus(NumberUtils.INTEGER_ONE);
            preOrderBean.setInfoStatus(PreSettleInfoStatusEnum.EARLY_PASS.getCode());
        }
        /*提前结算订单入库,如果是滚球，走事件延时接拒*/
        producer.sendMsg(MqConstants.RCS_PRE_SETTLE_UPDATE, MqConstants.RCS_PRE_SETTLE_UPDATE_TAG + "_SAVE", preOrderBean.getOrderNo(), JSONObject.toJSONString(preOrderBean), preOrderBean.getOrderNo());
    }

    /**
     * 提前结算子项是否包含滚球
     *
     * @param orderBean 订单
     * @return 是否滚球
     */
    public boolean preOrderScroll(PreOrderRequest orderBean) {
        //是否滚球
        boolean scrollFlag = false;
        for (PreOrderDetailRequest item : orderBean.getDetailList()) {
            //出现任何滚球赛事  需走滚球接拒单流程逻辑
            if (item.getMatchType() == 2) {//&& item.getSportId() == 1
                logger.info("::{}::当前订单存在滚球注单,需要等待处理{}", item.getBetNo(), JSONObject.toJSONString(orderBean));
                return true;
            }
        }
        logger.info("::{}::滚球判断完成:{}", orderBean.getOrderNo(), scrollFlag);
        return scrollFlag;
    }

    /**
     * 特殊限额的种类  0 无  1标签限额  2特殊百分比限额 3特殊单注单场限额 4特殊vip限额
     *
     * @param orderBean
     */
    private void setOrderVipInfo(OrderBean orderBean) {
        orderBean.setVipLevel(0);
        String key = LimitRedisKeys.getUserSpecialLimitKey(orderBean.getUid().toString());
        String type = RcsLocalCacheUtils.getValue(key, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, jedisClusterServer::hget);
        if (StringUtils.isNotBlank(type) && type.equals(UserSpecialLimitType.VIP.getType())) {
            orderBean.setVipLevel(1);
        }
    }

    /**
     * 取消订单
     *
     * @param requestParam 请求参数
     * @return Response
     */
    @Override
    public Response rejectOrder(Request<OrderBean> requestParam) {
        MDC.put("X-B3-TraceId", requestParam.getGlobalId());
        logger.info("method rejectOrder：args：{}", JSONObject.toJSONString(requestParam));
        try {
            OrderBean orderBean = requestParam.getData();
            //send MQ
            producer.sendMsg("queue_reject_mts_order", "rejectOrder", JSONObject.toJSONString(orderBean));
        } catch (Exception ex) {
            logger.error("rejectOrder error", ex);
        }
        return Response.success(requestParam.getData().getOrderNo());
    }

    /**
     * 派奖后做订单状态和返奖数据同步
     * 成功返回注单号
     */
    @Override
    public Response updateOrderAfterRefund(Request<SettleItem> requestParam) {
        MDC.put("X-B3-TraceId", requestParam.getGlobalId());
        logger.info("SDK结算派彩处理开始：{}", JSONObject.toJSONString(requestParam));
        if (requestParam == null || requestParam.getData() == null) {
            throw new RuntimeException("参数为空:" + JSONObject.toJSONString(requestParam));
        }
        SettleItem settleItem = requestParam.getData();
        if (settleItem.getSeriesType() == null) {
            throw new RuntimeException("串关类型不能为空!");
        }

        if (settleItem.getMerchantId() == null) {
            throw new RuntimeException("商户id不能为空!");
        }

        //if (settleItem.getChannelCode() == null) settleItem.setChannelCode(ChannelCodeEnum.PA.getCode());
        //单关 进行矩阵校验
        //&& settleItem.getChannelCode() == ChannelCodeEnum.PA.getCode()
        if (1 == settleItem.getSeriesType()) {

            ExtendBean extendBean = new ExtendBean();
            extendBean.setBusId(settleItem.getMerchantId().toString());
            OrderDetailsDTO orderDetailsDTO = settleItem.getOrderDetailRisk().get(0);

            paramValidateService.checkSettleArguments(orderDetailsDTO);

            extendBean.setOrderId(orderDetailsDTO.getOrderNo());//订单id
            extendBean.setMatchId(orderDetailsDTO.getMatchId().toString());//赛事id
            extendBean.setIsScroll(orderDetailsDTO.getMatchType() == 2 ? "1" : "0");//是否滚球  0:赛前 1：滚球
            extendBean.setPlayId(orderDetailsDTO.getPlayId().toString());//玩法id  -1表示其他玩法
            extendBean.setSportId(orderDetailsDTO.getSportId().toString());//体育类型
            extendBean.setUserId(orderDetailsDTO.getUid().toString());//用户ID
            extendBean.setMarketId(orderDetailsDTO.getMarketId().toString());//盘口id
            extendBean.setTournamentLevel(orderDetailsDTO.getTournamentLevel());
            extendBean.setOrderMoney(orderDetailsDTO.getBetAmount());
            BigDecimal odds = new BigDecimal(orderDetailsDTO.getOddsValue()).divide(new BigDecimal("100000")).setScale(2, RoundingMode.DOWN);
            extendBean.setOdds(odds.toString());
//            if ("1".equals(String.valueOf(orderDetailsDTO.getSportId()))) {
            OrderItem item = new OrderItem();
            item.setPlayId(orderDetailsDTO.getPlayId().intValue());
            item.setPlayOptionsId(orderDetailsDTO.getPlayOptionsId());
            item.setPlayOptions(orderDetailsDTO.getPlayOptions());
            item.setOddsValue(orderDetailsDTO.getOddsValue());
            item.setBetAmount(orderDetailsDTO.getBetAmount());
            item.setMarketType(orderDetailsDTO.getMarketType());
            if (!StringUtils.isEmpty(orderDetailsDTO.getMarketValueNew())) {
                item.setMarketValue(orderDetailsDTO.getMarketValueNew());
            } else {
                item.setMarketValue(orderDetailsDTO.getMarketValue());
            }
            //item.setMarketValue(MarketValueUtils.mergeMarketString(item.getMarketValue()));
            item.setScoreBenchmark(orderDetailsDTO.getScoreBenchmark());
            item.setMatchType(orderDetailsDTO.getMatchType());
            item.setMaxWinAmount(orderDetailsDTO.getMaxWinAmount().doubleValue());
            extendBean.setItemBean(item);

            paramValidateService.setProfitAmount(settleItem, settleItem.getOrderDetailRisk().get(0), extendBean);
            extendBean.setPlayType(rcsPaidConfigService.getPlayProcess(String.valueOf(settleItem.getOrderDetailRisk().get(0).getSportId()), String.valueOf(settleItem.getOrderDetailRisk().get(0).getPlayId())));

            extendBean.setDateExpect(DateUtils.getDateExpect(orderDetailsDTO.getBeginTime()));//赛事所属时间期号
            extendBean.setSelectId(orderDetailsDTO.getPlayOptionsId().toString());

            MatrixBean matrixBean = matrixAdapter.process(extendBean.getSportId(), extendBean.getPlayId(), extendBean);
            extendBean.setRecType(matrixBean.getRecType());
            if (matrixBean.getRecType() == 0) {
                extendBean.setRecVal(JSONObject.toJSONString(matrixBean.getMatrixValueArray()));
            }
            //获取商户配置
            RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit = null;
            try {
                rcsQuotaBusinessLimit = limitApiService.getRcsQuotaBusinessLimit(extendBean.getBusId()).getData();
            } catch (Exception e) {
                logger.error("::{}::获取商户：{} 配置信息RPC异常:{}", orderDetailsDTO.getOrderNo(), extendBean.getBusId(), e.getMessage(), e);
                throw new RcsServiceException("获取商户配置异常");
            }
            paidService.prizeHandle(settleItem, extendBean, matrixBean.getStatusZip(), rcsQuotaBusinessLimit);
        }
        logger.info("SDK结算派彩处理完成:{}", JSONObject.toJSONString(settleItem));
        return Response.success(requestParam.getData().getOrderNo());
    }

    /**
     * 获取用户投注限额 上限 参考值
     *
     * @return
     */
    @Override
    public Response<UserLimitReferenceResVo> getUserLimitReference(Request<Long> request) {
        if (request.getData() == null) {
            return Response.fail("商户ID不能为空");
        }
        Response<UserLimitReferenceResVo> response = limitApiService.getUserLimitReference(request);
        logger.info("查询用户投注限额上限参考值:{}", JSONObject.toJSONString(response));
        return response;
    }

    /**
     * SDK 初始化参数加载
     *
     * @return Response
     */
    @Override
    @POST
    @Path("/loadSdkConfig")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    public Response loadSdkConfig() {
        try {
            Sdk.loadSdkConfig();
        } catch (Exception ex) {
            logger.error("加载SDK初始参数失败", ex);
        }
        return Response.success();
    }

    /**
     * 是否冠军玩法
     * matchType:1 ：早盘 ，2： 滚球盘，3： 冠军盘
     *
     * @param orderBean
     * @return
     */
    private boolean isChampion(OrderBean orderBean) {
        if (orderBean != null && CollectionUtils.isNotEmpty(orderBean.getItems())) {
            Integer matchType = orderBean.getItems().get(0).getMatchType();
            return matchType != null && matchType == 3;
        }
        return false;
    }


    private int setOrderType(OrderBean orderBean, int orderType, boolean flag) {
        List<OrderItem> items = orderBean.getItems();
        if (org.springframework.util.CollectionUtils.isEmpty(items)) {
            return orderType;
        }
        int btsCount = 0;
        int gtsCount = 0;
        int ctsCount = 0;
        int rtsCount = 0;
        int otsCount = 0;
        int virCount = 0;
        for (OrderItem e : items) {
            if (OrderTypeEnum.BTS.getDataSource().equalsIgnoreCase(e.getDataSourceCode())) {
                btsCount++;
            }
            if (OrderTypeEnum.GTS.getPlatFrom().equalsIgnoreCase(e.getPlatform())) {
                gtsCount++;
            }
            if (OrderTypeEnum.CTS.getPlatFrom().equalsIgnoreCase(e.getPlatform())) {
                ctsCount++;
            }
            if (OrderTypeEnum.REDCAT.getDataSource().equalsIgnoreCase(e.getDataSourceCode())) {
                rtsCount++;
            }
            if (OrderTypeEnum.ODDIN.getPlatFrom().equalsIgnoreCase(e.getPlatform())) {
                otsCount++;
            }
            if (OrderTypeEnum.VIRTUAL.getPlatFrom().equalsIgnoreCase(e.getPlatform())) {
                virCount++;
            }
        }
        //bts目前全部走内部接单(全部漏单)
        if (btsCount == items.size()) {
            return OrderTypeEnum.BTS_PA.getValue();
        }
        if (gtsCount == items.size()) {
            return flag == true ? OrderTypeEnum.GTS_PA.getValue() : OrderTypeEnum.GTS.getValue();
        }
        if (ctsCount == items.size()) {
            return flag == true ? OrderTypeEnum.CTS_PA.getValue() : OrderTypeEnum.CTS.getValue();
        }
        //rts目前全部都数据商
        if (rtsCount == items.size()) {
            return OrderTypeEnum.REDCAT.getValue();
        }
        if (otsCount == items.size()) {
            return flag == true ? OrderTypeEnum.ODDIN_PA.getValue() : OrderTypeEnum.ODDIN.getValue();
        }
        if (virCount == items.size()) {
            return flag == true ? OrderTypeEnum.VIRTUAL_PA.getValue() : OrderTypeEnum.VIRTUAL.getValue();

        }
        return orderType;
    }
}