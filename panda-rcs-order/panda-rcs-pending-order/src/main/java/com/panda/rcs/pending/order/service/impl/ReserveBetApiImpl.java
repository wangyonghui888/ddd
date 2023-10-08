package com.panda.rcs.pending.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.panda.rcs.pending.order.constants.ErrorEnum;
import com.panda.rcs.pending.order.constants.NumberConstant;
import com.panda.rcs.pending.order.constants.RedisKey;
import com.panda.rcs.pending.order.enums.OrderStatusEnum;
import com.panda.rcs.pending.order.param.TournamentTemplateParam;
import com.panda.rcs.pending.order.pojo.OrderBeanVo;
import com.panda.rcs.pending.order.pojo.RcsPendingOrder;
import com.panda.rcs.pending.order.pojo.TournamentTemplateVo;
import com.panda.rcs.pending.order.service.IRcsTournamentTemplateService;
import com.panda.rcs.pending.order.service.RcsPendingOrderService;
import com.panda.rcs.pending.order.utils.CommonServer;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.ReserveBetApi;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.PendingOrderDto;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.data.rcs.dto.limit.RedisUpdateVo;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.enums.LNBasktballEnum;
import com.panda.sport.rcs.enums.RedisCmdEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.RcsBusinessConfigMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
import com.panda.sport.rcs.pojo.limit.RcsBusinessPlayPaidConfigVo;
import com.panda.sport.rcs.redis.utils.RedisUtils;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author :  koala
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.rcs.pending.order.service.impl
 * @Description :  预约投注API实现类
 * @Date: 2022-05-05 18:51
 * --------  ---------  --------------------------
 */
@Slf4j
@org.springframework.stereotype.Service
@Service
@RequiredArgsConstructor
public class ReserveBetApiImpl implements ReserveBetApi {

    private final IRcsTournamentTemplateService iRcsTournamentTemplateService;

    @Autowired
    private RedisUtils redisUtils;
    @Reference(check = false, lazy = true, retries = 1, timeout = 5000)
    private LimitApiService limitApiService;
    private final RcsPendingOrderService pendingOrderService;
    private final IRcsTournamentTemplateService templateService;
    private final ProducerSendMessageUtils producerSendMessageUtils;
    private final CommonServer commonServer;
    private final RcsBusinessConfigMapper rcsBusinessConfigMapper;


    @Override
    public Response queryMaxBetAmountByOrder(Request<PendingOrderDto> request) {
        MDC.put("X-B3-TraceId", request.getGlobalId());
        log.info("查询预约投注限额开始:{}", JSON.toJSONString(request));
        this.queryValidateParam(request);
        //获取配置信息
        RcsPendingOrder order = BeanCopyUtils.copyProperties(request.getData(), RcsPendingOrder.class);
        TournamentTemplateParam tournamentTemplateParam = CommonParam.getTemplateConfig(order);
        TournamentTemplateVo tournamentTemplateVo = iRcsTournamentTemplateService.queryPendingOrder(tournamentTemplateParam);
        if (Objects.isNull(tournamentTemplateVo)) {
            log.info("::{}::模板配置没有找到:{}", order.getOrderNo(), JSON.toJSONString(tournamentTemplateParam));
            return Response.error(NumberConstant.NUM_MINUS_ONE, "模板配置没有找到");
        }
        log.info("预约投注模板配置值:{}", JSON.toJSONString(tournamentTemplateVo));
        RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo = this.getBusinessLimit(order.getMerchantId());
        Integer matchType = order.getMatchType() == NumberConstant.NUM_ONE ? NumberConstant.NUM_ONE : NumberConstant.NUM_ZERO;
        String playType = order.getMatchType() == NumberConstant.NUM_THREE ? String.valueOf(NumberConstant.NUM_ZERO) : redisUtils.hget(RedisKey.RCS_STANDARD_SPORT_CATEGORY_ALL, String.format(RedisKey.RCS_STANDARD_SPORT_CATEGORY_FIELD, order.getSportId(), order.getPlayId()));
        BigDecimal odds = new BigDecimal(order.getOrderOdds()).divide(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED_THOUSAND), 2, BigDecimal.ROUND_DOWN);
        //用户单场和用户玩法key
        String userSingleMatchHashKey = RedisKey.getUserSingleMatchHashKey(order.getDateExpect(), String.valueOf(order.getMerchantId()), String.valueOf(order.getSportId()), String.valueOf(order.getUserId()), String.valueOf(order.getMatchId()), String.valueOf(matchType));
        //用户单场已用限额
        String userSingKey = RedisKey.USER_SINGLE_MATCH_HASH_FIELD;
        String userSingStr = redisUtils.hget(userSingleMatchHashKey, userSingKey);
        log.info("预约投注限额查询，用户单场已用限额:{},查询key:{},filed:{}", userSingStr, userSingleMatchHashKey, userSingKey);
        BigDecimal userSingTotal = StringUtils.isNotBlank(userSingStr) ? new BigDecimal(userSingStr) : BigDecimal.ZERO;
        //用户单场预约限额
        BigDecimal userSingleAmount = new BigDecimal(String.valueOf(tournamentTemplateVo.getUserPendingOrderPayVal())).subtract(userSingTotal).multiply(rcsQuotaBusinessLimitResVo.getUserQuotaRatio()).setScale(NumberConstant.NUM_TWO, BigDecimal.ROUND_DOWN);
        //用户玩法已用限额
        String userPlayKey = String.format(RedisKey.USER_SINGLE_MATCH_PLAY_HASH_FIELD, order.getPlayId(), matchType, playType);
        String userPlayStr = redisUtils.hget(userSingleMatchHashKey, userPlayKey);
        BigDecimal userPlayTotal = StringUtils.isNotBlank(userPlayStr) ? new BigDecimal(userPlayStr) : BigDecimal.ZERO;
        log.info("预约投注查询限额用户玩法累计金额:{}", userPlayTotal);
        //用户玩法限额
        BigDecimal userPlayAmount = new BigDecimal(String.valueOf(tournamentTemplateVo.getRcsMargainRefVo().getPendingOrderPayVal())).subtract(userPlayTotal).multiply(rcsQuotaBusinessLimitResVo.getUserQuotaRatio()).setScale(NumberConstant.NUM_TWO, BigDecimal.ROUND_DOWN);
        //单注投注赔付限额

        BigDecimal singlePayAmount = this.getSinglePayAmount(order, tournamentTemplateVo, rcsQuotaBusinessLimitResVo);
        log.info("预约投注,用户单场:{},用户玩法:{},单注投注:{}", userSingleAmount, userPlayAmount, singlePayAmount);
        List<BigDecimal> exchangeList = Arrays.asList(userSingleAmount, userPlayAmount, singlePayAmount);
        BigDecimal minAmount = exchangeList.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        //港赔
        BigDecimal oddValue = odds.subtract(BigDecimal.ONE);
        minAmount = minAmount.divide(oddValue, NumberConstant.NUM_TWO, BigDecimal.ROUND_DOWN);
        log.info("预约投注,欧赔:{},港赔:{},除港赔后:{}", odds, oddValue, minAmount);
        BigDecimal finallyAmount = this.min(minAmount, singlePayAmount).compareTo(BigDecimal.ZERO) <= NumberConstant.NUM_ZERO ? BigDecimal.ZERO : this.min(minAmount, singlePayAmount).divide(BigDecimal.valueOf(NumberConstant.NUM_ONE_HUNDRED), NumberConstant.NUM_ZERO, BigDecimal.ROUND_DOWN);
        RcsBusinessPlayPaidConfigVo configVo = this.returnData("", finallyAmount);
        log.info("预约投注查询限额返回给业务:{}", JSON.toJSONString(configVo));
        return Response.success(configVo);
    }

    /**
     * 订单入库校验
     *
     * @param request 查询参数
     * @return 返回入库
     */
    @Override
    public Response<?> saveOrderCheckAmount(Request<PendingOrderDto> request) {
        MDC.put("X-B3-TraceId", request.getGlobalId());
        log.info("预约投注注单校验及保存入库参数:{}", JSON.toJSONString(request));

        try {
            //各种参数校验
            validateParam(request);
            OrderBean orderBean = buildOrderBean(request.getData());
            PendingOrderDto pendingOrderDto = request.getData();
            RcsPendingOrder order = BeanCopyUtils.copyProperties(pendingOrderDto, RcsPendingOrder.class);
            TournamentTemplateParam tournamentTemplateParam = CommonParam.getTemplateConfig(order);
            TournamentTemplateVo templateVo = iRcsTournamentTemplateService.queryPendingOrder(tournamentTemplateParam);
            if (Objects.isNull(templateVo)) {
                log.info("::{}::模板配置没有找到:{}", order.getOrderNo(), JSON.toJSONString(tournamentTemplateParam));
                return Response.error(-1, "模板配置没有找到");
            }
            log.info("::{}::模板配置:{}", order.getOrderNo(), JSON.toJSONString(templateVo));
            //预约注单笔数校验,是否超过10笔(模板配置参数)
            long count = pendingOrderService.selectPendingCountByMatchId(order.getMatchId(), order.getUserId(), order.getMatchType());
            if (count >= templateVo.getUserPendingOrderCount()) {
                log.info("预约注单可预约的笔数：{},当前赛事已经预约的笔数{}", templateVo.getUserPendingOrderCount(), count);
                return Response.error(ErrorEnum.PEN_COUNT_ERROR.getCode(), ErrorEnum.PEN_COUNT_ERROR.getMsg());
            }
            //预约投注限额校验
            if (!checkUserAmount(order, templateVo)) {
                log.info("当前用户：{} 赛事id：{} 可下注额已超限制额度", order.getUserId(), order.getMarketId());
                return Response.error(ErrorEnum.AMOUNT_ERROR.getCode(), ErrorEnum.AMOUNT_ERROR.getMsg());
            }
            try {
                //风控预约注单入库;
                setOrderTime(order);
                pendingOrderService.save(order);
                //构造及时注单,推送客户端;
                OrderBeanVo orderBeanVo = new OrderBeanVo();
                BeanUtils.copyProperties(orderBean, orderBeanVo);
                orderBeanVo.setOrderStatus(OrderStatusEnum.SUCCESS.getCode());
                orderBeanVo.setIsPendingOrder(NumberConstant.NUM_ONE);
                producerSendMessageUtils.sendMessage(MqConstants.WS_ORDER_BET_RECORD_TOPIC, MqConstants.WS_ORDER_BET_RECORD_TAG, order.getOrderNo(), orderBeanVo);
                //预约投注forecast
                producerSendMessageUtils.sendMessage("queue_realtimevolume_order_pending", "", pendingOrderDto.getOrderNo(), pendingOrderDto);
                return Response.success(order.getOrderNo());
            } catch (Exception e) {
                log.error("预约投注注单校验及保存入库异常:{}", ExceptionUtils.getStackTrace(e));
                return Response.error(NumberConstant.NUM_MINUS_ONE, "预约投注注单风控服务入库异常!");
            }
        } catch (RcsServiceException e) {
            log.error("预约投注注单校验及保存入库异常:{}", ExceptionUtils.getStackTrace(e));
            return Response.error(NumberConstant.NUM_MINUS_ONE, e.getErrorMassage());
        } catch (Exception e) {
            log.error("预约投注注单校验及保存入库异常:{}", ExceptionUtils.getStackTrace(e));
            return Response.error(NumberConstant.NUM_MINUS_ONE, "预约投注处理异常!");
        }
    }

    /**
     * 检查订单信息
     *
     * @param rcsPendingOrder      订单对象
     * @param tournamentTemplateVo 模板配置
     * @return 返回是否检查通过
     */
    private boolean checkUserAmount(RcsPendingOrder rcsPendingOrder, TournamentTemplateVo tournamentTemplateVo) {
        List<RedisUpdateVo> redisUpdateList = new ArrayList<>();
        BigDecimal odds = new BigDecimal(rcsPendingOrder.getOrderOdds()).divide(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED_THOUSAND), 2, BigDecimal.ROUND_DOWN);
        Integer matchType = rcsPendingOrder.getMatchType() == NumberConstant.NUM_ONE ? NumberConstant.NUM_ONE : NumberConstant.NUM_ZERO;
        double singlePayment = Double.parseDouble(String.valueOf(new BigDecimal(rcsPendingOrder.getBetAmount()).multiply(odds.subtract(BigDecimal.ONE)).setScale(NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN)));
        //用户单场和用户玩法key
        String userSingleMatchHashKey = RedisKey.getUserSingleMatchHashKey(rcsPendingOrder.getDateExpect(), String.valueOf(rcsPendingOrder.getMerchantId()), String.valueOf(rcsPendingOrder.getSportId()), String.valueOf(rcsPendingOrder.getUserId()), String.valueOf(rcsPendingOrder.getMatchId()), String.valueOf(matchType));
        //计算用户单场限额
        double usedUserSingle = redisUtils.hincrByFloat(userSingleMatchHashKey, RedisKey.USER_SINGLE_MATCH_HASH_FIELD, singlePayment);
        redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBYFLOAT.getCmd(), userSingleMatchHashKey, RedisKey.USER_SINGLE_MATCH_HASH_FIELD, String.valueOf(singlePayment), String.valueOf(usedUserSingle)));
        log.info("::预约投注保存订单,单场限额---累计金额:{},结束金额:{},配置金额:{}", singlePayment, usedUserSingle, tournamentTemplateVo.getUserPendingOrderPayVal());
        //额度已经用完
        if (new BigDecimal(String.valueOf(tournamentTemplateVo.getUserPendingOrderPayVal())).multiply(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED)).compareTo(BigDecimal.valueOf(usedUserSingle)) < NumberConstant.NUM_ZERO) {
            //用户单场赔付额度已用完
            commonServer.redisCallback(redisUpdateList);
            log.info("预约投注用户单场赔付额度已用完");
            return false;
        }
        String playType = rcsPendingOrder.getMatchType() == NumberConstant.NUM_THREE ? String.valueOf(NumberConstant.NUM_ZERO) : redisUtils.hget(RedisKey.RCS_STANDARD_SPORT_CATEGORY_ALL, String.format(RedisKey.RCS_STANDARD_SPORT_CATEGORY_FIELD, rcsPendingOrder.getSportId(), rcsPendingOrder.getPlayId()));
        //计算用户玩法限额
        //需求2607 Ln(4)：如果是篮球，且是Ln模式,那么联控玩法跟随主控玩法限额值
        String playId = rcsPendingOrder.getPlayId().toString();
        if(rcsPendingOrder.getSportId().equals(SportIdEnum.BASKETBALL.getId().toString())){
            String key = String.format(RedisKey.TRADING_TYPE_KEY, rcsPendingOrder.getMatchId(),rcsPendingOrder.getPlayId(),rcsPendingOrder.getMatchType().toString());
            log.info("::{}::预约投注-单场LN模式下联控玩法额度跟随主控玩法KEY: {}",key);
            if(redisUtils.exists(key)) {
                String lnValue = redisUtils.get(key);
                if (lnValue.equals("4")) {
                    playId = LNBasktballEnum.getNameById(rcsPendingOrder.getPlayId().intValue()).toString();

                    //并且playType要替换成主控
                    //阶段  冠军玩法走mts/虚拟赛事 可以不设置此字段
                    if (rcsPendingOrder.getMatchType() != 3 && !NumberConstant.VIRSTUAL_SPORT.contains(rcsPendingOrder.getSportId())) {
                        playType = limitApiService.queryPlayInfoById(Integer.valueOf(rcsPendingOrder.getSportId().toString()), Integer.valueOf(playId)).getData();
                    }
                    log.info("::{}::预约投注-单场LN模式下联控玩法额度跟随主控玩法:赛事阶段:{}", playId,playType);
                }
            }
        }
        String field = String.format(RedisKey.USER_SINGLE_MATCH_PLAY_HASH_FIELD, playId, matchType, playType);
        double usedUserPlay = redisUtils.hincrByFloat(userSingleMatchHashKey, field, singlePayment);
        redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBYFLOAT.getCmd(), userSingleMatchHashKey, field, String.valueOf(singlePayment), String.valueOf(usedUserPlay)));
        log.info("::预约投注保存订单,玩法累计限额---累计金额:{},结束金额:{},配置金额:{}", singlePayment, usedUserPlay, tournamentTemplateVo.getRcsMargainRefVo().getPendingOrderPayVal());
        if (CommonUtils.toBigDecimal(String.valueOf(usedUserPlay), BigDecimal.ZERO).compareTo(new BigDecimal(String.valueOf(tournamentTemplateVo.getRcsMargainRefVo().getPendingOrderPayVal())).multiply(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED))) > NumberConstant.NUM_ZERO) {
            //用户单关玩法赔付额度已用完
            commonServer.redisCallback(redisUpdateList);
            log.info("预约投注用户玩法赔付额度已用完");
            return false;
        }
        log.info("::{}::预约投注保存订单校验通过", rcsPendingOrder.getOrderNo());
        commonServer.saveRedisUpdateRecord(rcsPendingOrder.getOrderNo(), redisUpdateList);
        return true;
    }

    @Override
    public Response cancelOrder(Request<PendingOrderDto> request) {
        MDC.put("X-B3-TraceId", request.getGlobalId());
        log.info("业务回调预约投注开始:{}", JSON.toJSONString(request));
        PendingOrderDto pendingOrderDto = request.getData();
        RcsPendingOrder pendingOrder = RcsPendingOrder.builder()
                .orderStatus(pendingOrderDto.getOrderStatus())
                .cancelTime(new Date().getTime()).orderNo(pendingOrderDto.getOrderNo())
                .remark(pendingOrderDto.getRemark()).build();
        pendingOrderService.updateById(pendingOrder);
        //成功不处理回滚限额
        if (pendingOrderDto.getOrderStatus() != NumberConstant.NUM_ONE) {
            //回滚限额
            commonServer.revertLimitBetAccount(pendingOrder.getOrderNo());
        }
        //预约投注forecast
        producerSendMessageUtils.sendMessage("queue_realtimevolume_order_pending", "", pendingOrderDto.getOrderNo(), pendingOrderDto);
        return Response.success();
    }


    private BigDecimal getSinglePayAmount(RcsPendingOrder rcsPendingOrder, TournamentTemplateVo tournamentTemplateVo, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo) {
        //单注投注赔付限额
        BigDecimal singlePayAmount = new BigDecimal(String.valueOf(tournamentTemplateVo.getRcsMargainRefVo().getSinglePayLimit())).multiply(rcsQuotaBusinessLimitResVo.getUserQuotaBetRatio()).setScale(NumberConstant.NUM_TWO, BigDecimal.ROUND_DOWN);

        String type = this.getUserSpecialLimitType(String.valueOf(rcsPendingOrder.getUserId()));
        if (type.equals(String.valueOf(NumberConstant.NUM_TWO))) {
            String key = RedisKey.getUserSpecialLimitKey(String.valueOf(rcsPendingOrder.getUserId()));
            String value = RcsLocalCacheUtils.getValue(key, RedisKey.USER_SPECIAL_LIMIT_PERCENTAGE_FIELD, redisUtils::hget);

            log.info("预约投注额度查询-用户特殊限额等于2：{}", value);
            return StringUtils.isNotBlank(value) ? new BigDecimal(value).multiply(singlePayAmount).setScale(NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN) : singlePayAmount;
        }
        return singlePayAmount;
    }

    //获取用户type值
    private String getUserSpecialLimitType(String userId) {
        // 用户特殊限额类型,0-无,1-标签限额,2-特殊百分比限额,3-特殊单注单场限额,4-特殊vip限额
        String userSpecialLimitKey = RedisKey.getUserSpecialLimitKey(userId);
        String userSpecialLimitType = RcsLocalCacheUtils.getValue(userSpecialLimitKey, RedisKey.USER_SPECIAL_LIMIT_TYPE_FIELD, redisUtils::hget);

        log.info("额度查询-串关-用户特殊限额类型：{}", userSpecialLimitType);
        return userSpecialLimitType;
    }


    /**
     * 根据商户ID获取商户的限额信息
     *
     * @param businessId 商户ID
     * @return 商户限额信息
     */
    private RcsQuotaBusinessLimitResVo getBusinessLimit(Long businessId) {
        // 先从缓存取
        String key =String.format(RedisKey.MERCHANT_LIMIT_KEY,businessId) ;
        String value = RcsLocalCacheUtils.getValue(key, redisUtils::get);
        log.info("预约投注,Redis获取商户限额：key={},field={},value={}", key, businessId, value);
        if (StringUtils.isNotBlank(value)) {
            return JSON.parseObject(value, RcsQuotaBusinessLimitResVo.class);
        }
        // 缓存没有调用rpc接口查询
        Response<RcsQuotaBusinessLimitResVo> response = limitApiService.getRcsQuotaBusinessLimit(businessId.toString());
        log.info("预约投注,调用rpc获取商户限额：response={}", JSON.toJSONString(response));
        if (response == null || response.getCode() != Response.SUCCESS || response.getData() == null) {
            throw new RcsServiceException(NumberConstant.NUM_MINUS_ONE, "调用rpc获取商户限额失败");
        }
        RcsQuotaBusinessLimitResVo responseData = response.getData();
        redisUtils.setex(key, responseData, 30L, TimeUnit.DAYS);
        return responseData;
    }

    private static OrderBean buildOrderBean(PendingOrderDto dto) {
        OrderBean orderBean = new OrderBean();
        orderBean.setSportId(dto.getSportId().intValue());
        orderBean.setOrderNo(dto.getOrderNo());
        orderBean.setUid(dto.getUserId());
        orderBean.setProductCount(1L);
        orderBean.setSeriesType(NumberConstant.NUM_ONE);
        orderBean.setProductAmountTotal(dto.getBetAmount());
        orderBean.setOrderAmountTotal(dto.getBetAmount());
        orderBean.setTenantId(dto.getMerchantId());
        orderBean.setCurrencyCode("");
        orderBean.setIpArea(dto.getIpArea());
        orderBean.setIp(dto.getIp());
        orderBean.setUserTagLevel(dto.getUserTagLevel());
        orderBean.setDeviceType(dto.getDeviceType());
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderNo(dto.getOrderNo());
        orderItem.setUid(dto.getUserId());
        orderItem.setSportId(dto.getSportId().intValue());
        orderItem.setPlayId(dto.getPlayId().intValue());
        orderItem.setMatchId(dto.getMatchId());
        orderItem.setMatchType(dto.getMatchType());
        orderItem.setMarketValue(dto.getMarketValue());
        orderItem.setBetAmount(dto.getBetAmount());
        orderItem.setMarketId(dto.getMarketId());
        orderItem.setOddsValue(Double.valueOf(dto.getOrderOdds()));
        orderItem.setOriginOdds(new BigDecimal(dto.getOrderOdds()).divide(BigDecimal.valueOf(100000), 2, BigDecimal.ROUND_DOWN).doubleValue());
        orderItem.setOddsType(dto.getOddType());
        orderItem.setPlayOptionsId(dto.getOddsId());
        orderItem.setTournamentId(dto.getTournamentId());
        orderItem.setDateExpect(dto.getDateExpect());
        orderItem.setMatchInfo(dto.getMatchInfo());
        orderItem.setMatchName(dto.getMatchName());
        orderItem.setBetTime(dto.getBetTime());
        orderItem.setPlayOptionsName(dto.getPlayOptionsName());
        orderItem.setPlayName(dto.getPlayName());
        orderItem.setPlayOptions(dto.getOddType());
        //orderItem.setSeriesType(NumberConstant.NUM_ONE);
        orderBean.setItems(Collections.singletonList(orderItem));
        return orderBean;
    }

    /**
     * 返回限额数据给业务
     *
     * @param seriesType 串关类型 2001 3001...
     * @param maxPay     最大投注金额
     * @return 限额数据
     */
    private RcsBusinessPlayPaidConfigVo returnData(String seriesType, BigDecimal maxPay) {
        RcsBusinessPlayPaidConfigVo rcsBusinessPlayPaidConfigVo = new RcsBusinessPlayPaidConfigVo();
        rcsBusinessPlayPaidConfigVo.setOrderMaxPay(Long.valueOf(String.valueOf(maxPay)));
        rcsBusinessPlayPaidConfigVo.setType(seriesType);
        rcsBusinessPlayPaidConfigVo.setMinBet(NumberConstant.LONG_ZERO);
        return rcsBusinessPlayPaidConfigVo;
    }

    private BigDecimal min(BigDecimal a, BigDecimal b) {
        return (a.compareTo(b) <= NumberConstant.NUM_ZERO) ? a : b;
    }



    private String getPlayType(RcsPendingOrder order) {
        String key = String.format(RedisKey.RCS_STANDARD_SPORT_CATEGORY_ALL, order.getPlayId(), order.getSportId());
        String value=RcsLocalCacheUtils.getValue(key,redisUtils::get);
        if(StringUtils.isBlank(value)){
            StandardSportMarketCategory standardSportMarketCategory = rcsBusinessConfigMapper.queryPlayById(order.getPlayId().intValue(), order.getSportId().intValue());
            if (Objects.nonNull(standardSportMarketCategory)) {
                redisUtils.setex(key, String.valueOf(standardSportMarketCategory.getTheirTime()), NumberConstant.NUM_SEVEN, TimeUnit.DAYS);
            }
        }
        return value;
    }


    /**
     * 设置订单时间
     */
    private void setOrderTime(RcsPendingOrder order) {
        Date date = new Date();
        order.setCreateTime(date.getTime());
        order.setUpdateTime(date.getTime());
    }

    private void queryValidateParam(Request<PendingOrderDto> requestParam) {
        if (null == requestParam || null == requestParam.getData()) {
            throw new RcsServiceException("参数错误!");
        }
        PendingOrderDto paramData = requestParam.getData();
        this.commonValidateParam(paramData);
    }


    private void commonValidateParam(PendingOrderDto paramData) {
        if (paramData.getUserId() == null) {
            throw new RcsServiceException("用户ID不能为空!");
        }
        if (paramData.getMerchantId() == null) {
            throw new RcsServiceException("商户ID不能为空!");
        }
        if (paramData.getMatchId() == null) {
            throw new RcsServiceException("赛事ID不能为空!");
        }
        if (paramData.getMatchType() == null) {
            throw new RcsServiceException("赛事类型matchType不能为空!");
        }
        if (paramData.getSportId() == null) {
            throw new RcsServiceException("球种Id不能为空!");
        }
        if (paramData.getPlayId() == null) {
            throw new RcsServiceException("标准玩法id不能为空!");
        }
        if (paramData.getOrderOdds() == null) {
            throw new RcsServiceException("预约盘口赔率不能为空!");
        }
        if (paramData.getDateExpect() == null) {
            throw new RcsServiceException("赛事所属时间期号不能为空!");
        }
        if (null == paramData.getMarketId() && StringUtils.isBlank(paramData.getMarketValue())) {
            throw new RcsServiceException("盘口ID或者盘口值不能为空!");
        }
    }

    /**
     * 预约注单参数校验
     *
     * @param requestParam
     */
    private void validateParam(Request<PendingOrderDto> requestParam) {
        if (null == requestParam || null == requestParam.getData()) {
            throw new RcsServiceException("参数错误!");
        }
        PendingOrderDto paramData = requestParam.getData();
        if (StringUtils.isBlank(paramData.getOrderNo())) {
            throw new RcsServiceException("订单号不能为空!");
        }
        this.commonValidateParam(paramData);
        if (paramData.getBetAmount() == null) {
            throw new RcsServiceException("下注金额不能为空!");
        }
        if (paramData.getOddType() == null) {
            throw new RcsServiceException("投注项类型不能为空!");
        }
    }
}
