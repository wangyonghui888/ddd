package com.panda.sport.rcs.oddin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.TemplateAcceptConfigServer;
import com.panda.sport.data.rcs.api.enums.MatchEventConfigEnum;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.data.rcs.vo.RcsTournamentTemplateAcceptConfig;
import com.panda.sport.data.rcs.vo.oddin.StandardMarketVo;
import com.panda.sport.data.rcs.vo.oddin.StandardMatchVo;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.oddin.entity.common.RcsTournamentTemplateAcceptConfigDto;
import com.panda.sport.rcs.oddin.entity.common.pojo.ErrorMessagePrompt;
import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrderTy;
import com.panda.sport.rcs.oddin.entity.ots.Enums;
import com.panda.sport.rcs.oddin.mapper.RcsOrderDetailMapper;
import com.panda.sport.rcs.oddin.service.IOrderAcceptService;
import com.panda.sport.rcs.oddin.service.handler.RcsOddinOrderTyHandler;
import com.panda.sport.rcs.oddin.service.handler.TicketOrderHandler;
import com.panda.sport.rcs.pojo.RcsLabelLimitConfig;
import com.panda.sport.rcs.pojo.RcsUserConfig;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.panda.sport.rcs.vo.odds.RcsStandardMarketDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.panda.sport.rcs.constants.RcsConstant.REDIS_MATCH_MARKET_ODDS_NEW;
import static com.panda.sport.rcs.constants.RedisKey.*;
import static com.panda.sport.rcs.oddin.common.Constants.*;


/**
 * @author Beulah
 * @date 2023/3/22 0:47
 * @description 订单实时接拒业务
 */
@Slf4j
@Service
public class OrderAcceptServiceImpl implements IOrderAcceptService {

    @Resource
    RedisClient redisClient;
    @Resource
    TOrderDetailMapper orderDetailMapper;
    @Resource
    RcsOddinOrderTyHandler rcsOddinOrderTyHandler;
    @Resource
    private RcsOrderDetailMapper rcsOrderDetailMapper;
    @Resource
    private TicketOrderHandler ticketOrderHandler;

    @Reference(check = false, lazy = true, retries = 3, timeout = 3000)
    TemplateAcceptConfigServer templateAcceptConfigServer;
    @Reference(check = false, lazy = true, retries = 3, timeout = 3000)
    LimitApiService limitApiService;


    /**
     * 订单是否被取消
     *
     * @param orderNo 订单号
     * @return 是否取消
     */
    @Override
    public boolean orderIsCanceled(String orderNo) {
        RcsOddinOrderTy order = rcsOddinOrderTyHandler.selectOne(orderNo);
        if (order == null) {
            return false;
        }

        if (Enums.CancelStatus.CANCEL_STATUS_CANCELED.toString().equalsIgnoreCase(order.getStatus())) {
            log.error("::{}::订单已取消,不做重复取消处理:{}", orderNo, JSONObject.toJSONString(order));
            return true;
        }
        return false;
    }

    /**
     * 出现任何滚球赛事  需走滚球接拒单流程逻辑
     *
     * @param list 订单
     * @return 是否滚球订单
     */
    @Override
    public boolean orderIsScroll(List<String> list) {
        boolean scrollFlag = false;
        QueryWrapper<TOrderDetail> qw = new QueryWrapper<>();
        qw.in("order_no", list);
        List<TOrderDetail> detailList = rcsOrderDetailMapper.selectList(qw);
        if (CollectionUtils.isNotEmpty(detailList)) {
            for (TOrderDetail detail : detailList) {
                if ("2".equals(detail.getMatchType())) {
                    log.info("::{}::当前订单存在滚球注单,需要等待处理{}", detail.getOrderNo(), JSONObject.toJSONString(detail));
                    return true;
                }
            }
        }
        return scrollFlag;
    }

    /**
     * 检查滚球秒接场景
     *
     * @param list 订单
     * @return 是否秒接
     */
    @Override
    public boolean checkScrollSpeedAccept(List<ExtendBean> list, String third) {
        for (ExtendBean item : list) {
            //早盘 秒接
            if (Objects.nonNull(item.getIsScroll()) && "0".equals(item.getIsScroll())) {
                log.info("::{}::赛事属于早盘阶段:{},秒接", list.get(0).getOrderId(), item.getIsScroll());
                return true;
            }
            //赛事阶段判断:中场休息/即将开赛 秒接
            if (isSpeedAcceptByMatchPeriod(item, third)) {
                return true;
            }
            //判断赛事延时配置是否为0 ，设置0秒也秒接
            if (matchDelayConfig(list)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 赛事状态检查
     *
     * @param matchId            赛事id
     * @param orderNo            注单号
     * @param errorMessagePrompt 错误提示
     * @return 赛事状态是否变更接拒
     */
    @Override
    public boolean checkMatchStatus(String matchId, String orderNo, ErrorMessagePrompt errorMessagePrompt, String third) {
        try {
            String matchKey = String.format(REDIS_MATCH_INFO, matchId);
            String matchInfoStr = RcsLocalCacheUtils.getValue(matchKey, redisClient::get);
            if (StringUtils.isBlank(matchInfoStr)) {
                log.info("::{}::赛事状态检查,获取赛事数据为空::key:{}", orderNo, matchKey);
                return false;
            }
            StandardMatchVo standardMatchMessage = JSON.parseObject(matchInfoStr, StandardMatchVo.class);
            log.info("::{}::赛事状态检查,获取到赛事数据::{}", orderNo, JSONObject.toJSONString(standardMatchMessage));
            if (standardMatchMessage.getStatus() != 0 && standardMatchMessage.getStatus() != 13) {
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
                if (standardMatchMessage.getStatus() == 1) {
                    errorMessagePrompt.setHintMsg("赛事封盘拒单");
                    errorMessagePrompt.setCurrentEvent("match_handicap_status_suspended-PA");
                    log.info("::{}::{}投注赛事封盘拒单", orderNo, third);
                } else if (standardMatchMessage.getStatus() == 2) {
                    errorMessagePrompt.setHintMsg("赛事关盘拒单");
                    errorMessagePrompt.setCurrentEvent("match_handicap_status_deactivated-PA");
                    log.info("::{}::{}投注赛事关盘拒单", orderNo, third);
                } else if (standardMatchMessage.getStatus() == 11) {
                    errorMessagePrompt.setHintMsg("赛事锁盘拒单");
                    errorMessagePrompt.setCurrentEvent("match_handicap_status_lock-PA");
                    log.info("::{}::{}投注赛事锁盘拒单", orderNo, third);
                }
                errorMessagePrompt.setBetNo(orderNo);
                return true;
            }
        } catch (Exception e) {
            log.error("::{}::{}赛事状态检查异常::", orderNo, third, e);
        }
        return false;
    }


    /**
     * 赛事阶段检查
     *
     * @param orderItem 订单
     * @return 是否中场休息秒接
     */
    @Override
    public boolean isSpeedAcceptByMatchPeriod(ExtendBean orderItem, String third) {
        Long sportId = Long.valueOf(orderItem.getSportId());
        String orderNo = orderItem.getOrderId();
        String matchId = orderItem.getMatchId();
        try {
            String key = String.format(RCS_DATA_THIRD_MATCH_INFO, matchId);
            String period = RcsLocalCacheUtils.getValue(key, "period", redisClient::hGet, 10 * 1000L);
            log.info("::{}::赛事秒接缓存::key={},value={}", orderNo, key, period);
            if (speedSportList.contains(sportId)) {
                if (StringUtils.isNotBlank(period)) {
                    Integer periodId = Integer.parseInt(period);
                    if (SportIdEnum.isFootball(sportId) || SportIdEnum.isBasketball(sportId)) {
                        if (Arrays.asList(302, 31).contains(periodId)) {
                            log.info("::{}::数据商{}球种:{}赛事:{}触发中场休息秒接", orderNo, third, sportId, matchId);
                            return true;
                        }
                    } else if (SportIdEnum.isTennis(sportId) || SportIdEnum.isPingPong(sportId) || SportIdEnum.isVolleyBall(sportId) || SportIdEnum.isBadminton(sportId.intValue())) {
                        if (Arrays.asList(301, 302, 303, 304, 305, 306, 800, 900, 1000, 1100, 1200).contains(periodId)) {
                            log.info("::{}::数据商{}球种:{}赛事:{}触发中场休息秒接", orderNo, third, sportId, matchId);
                            return true;
                        }
                    }
                }
            }
            if (matchIsReadyStart(matchId, orderNo)) {
                log.info("::{}::即将开赛秒接", orderNo);
                return true;
            }
        } catch (NumberFormatException e) {
            log.error("::{}::赛事阶段检查异常:", orderNo, e);
        }
        return false;
    }


    /**
     * 是否即将开赛
     *
     * @param matchId 赛事id
     * @return 是否即将开赛
     */
    @Override
    public boolean matchIsReadyStart(String matchId, String orderNo) {
        try {
            String matchIsReadyStartKey = RCS_TASK_MATCH_INFO_CACHE + matchId;
            String value = RcsLocalCacheUtils.getValue(matchIsReadyStartKey, redisClient::get, 2 * 1000L);
            log.info("::{}::赛事秒接是否即将开赛缓存key:{},value:{}", orderNo, matchIsReadyStartKey, value);
            if (StringUtils.isBlank(value)) {
                return false;
            }
            MatchMarketLiveBean match = JSON.parseObject(value, MatchMarketLiveBean.class);
            if (match != null) {
                long timeMillis = System.currentTimeMillis();
                long beginTime = DateUtils.tranferStringToDate(match.getMatchStartTime()).getTime();
                Integer matchStatus = match.getMatchStatus();
                Integer period = match.getPeriod();
                //到时间 ，赛事状态还是未开赛
                boolean isOverTime = (timeMillis > beginTime && matchStatus == 0);
                //赛事状态以开赛 赛事阶段还是早盘
                boolean isOverStatus = (period == 0 && matchStatus == 1);
                //赛事阶段已经是滚球 赛事状态还是未开赛
                boolean isOverMatchType = (matchStatus == 0 && period > 0);
                return isOverTime || isOverMatchType || isOverStatus;
            }
        } catch (Exception e) {
            log.error("::{}::检查是否即将开赛异常::赛事:{}:", orderNo, matchId, e);
        }
        return false;
    }

    /**
     * 赛事模板延时配置 设置0秒接单
     *
     * @param list 订单
     * @return 是否0秒可以接单
     */
    @Override
    public boolean matchDelayConfig(List<ExtendBean> list) {
        //联赛赛事模板
        ExtendBean extendBean = list.get(0);
        String orderId = extendBean.getOrderId();

        try {
            Request<OrderItem> request = new Request<>();
            request.setData(extendBean.getItemBean());
            Response<String> response = templateAcceptConfigServer.queryMatchDelaySeconds(request);
            String redisStr = response.getData();
            String sportId = extendBean.getSportId();
            String redisValue = RcsLocalCacheUtils.getValue(String.format(ORDER_LABEL_DELAY_CONFIG, extendBean.getUserTagLevel()), sportId, redisClient::hGet, 5 * 60 * 1000L);
            String userLabelKey = String.format(USER_LABEL_CONFIG, extendBean.getUserId());
            String userLabel = redisClient.hGet(userLabelKey, sportId);
            if (StringUtils.isBlank(userLabel)) {
                limitApiService.queryRcsUserConfig(extendBean.getUserId());
                userLabel = redisClient.hGet(userLabelKey, sportId);
            }
            Response<RcsQuotaBusinessLimitResVo> busConfig = limitApiService.getRcsQuotaBusinessLimit(extendBean.getBusId());
            //新增商户配置
            RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit = busConfig.getData();
            log.info("::{}::赛事ID:{},用户ID:{},赛事模板秒接值:{},标签值:{},用户特殊标签值:{},商户配置:{}", orderId, extendBean.getMatchId(), extendBean.getUserId(), redisStr, redisValue, userLabel, rcsQuotaBusinessLimit);
            if (StringUtils.isNotBlank(redisStr)) {
                RcsTournamentTemplateAcceptConfigDto config = JSONObject.parseObject(redisStr, RcsTournamentTemplateAcceptConfigDto.class);
                if ((Objects.nonNull(config.getNormal()) && config.getNormal() == 0 && this.isSafe(list)) || (Objects.nonNull(config.getWaitSeconds()) && config.getWaitSeconds() == 0)) {
                    RcsLabelLimitConfig rcsLabelLimitConfig = JSONObject.parseObject(redisValue, RcsLabelLimitConfig.class);
                    RcsUserConfig rcsUserConfig = JSONObject.parseObject(userLabel, RcsUserConfig.class);
                    if ((Objects.isNull(rcsLabelLimitConfig) || Objects.isNull(rcsLabelLimitConfig.getBetExtraDelay()) || rcsLabelLimitConfig.getBetExtraDelay() == 0) && (Objects.isNull(rcsUserConfig) || Objects.isNull(rcsUserConfig.getBetExtraDelay()) || rcsUserConfig.getBetExtraDelay() == 0) && (Objects.isNull(rcsQuotaBusinessLimit) || Objects.isNull(rcsQuotaBusinessLimit.getDelay()) || rcsQuotaBusinessLimit.getDelay() == 0)) {
                        log.info("::{}::模板配置触发秒接", orderId);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.info("::{}::检查模板延迟配置异常:", orderId, e);
        }
        return false;
    }


    /**
     * 盘口状态检查
     *
     * @param tOrderDetail       订单
     * @param errorMessagePrompt 错误提示
     * @return 盘口是否变动接拒
     */
    @Override
    public boolean checkMarketStatus(ExtendBean tOrderDetail, ErrorMessagePrompt errorMessagePrompt, String third, Integer orderStatus) {
        String orderNo = tOrderDetail.getOrderId();
        try {
            String marketKey = String.format(REDIS_MATCH_MARKET_ODDS_NEW, tOrderDetail.getPlayId(), tOrderDetail.getMatchId());
            String matchMarketOddsStr = RcsLocalCacheUtils.getValue(marketKey, redisClient::get);
            if (StringUtils.isBlank(matchMarketOddsStr)) {
                log.warn("::{}::{}赛事盘口状态检查,获取到盘口数据为空::key:{}", orderNo, third, marketKey);
                return false;
            }
            Long sportId = Long.valueOf(tOrderDetail.getSportId());
            Long orderMarketId = Long.valueOf(tOrderDetail.getMarketId());
            List<StandardMarketMessage> rcsStandardMarketDTOS = JSONObject.parseArray(matchMarketOddsStr, StandardMarketMessage.class);
            log.info("::{}::{}赛事盘口状态检查,获取到盘口数据::{}", orderNo, third, JSONObject.toJSONString(rcsStandardMarketDTOS));
            for (StandardMarketMessage standardMarket : rcsStandardMarketDTOS) {
                //足篮单独判断
                if (SportIdEnum.FOOTBALL.getId().equals(sportId) || SportIdEnum.BASKETBALL.getId().equals(sportId)) {
                    if (orderMarketId.equals(standardMarket.getId())) {
                        if (!standardMarket.getStatus().equals(0) || standardMarket.getThirdMarketSourceStatus() != 0) {
                            log.info("::{}::{}球种:{}盘口有变化拒单::盘口ID:{}盘口状态:{},订单状态:{}", orderNo, third, sportId, standardMarket.getId(), standardMarket.getStatus(), orderStatus);
                            this.errorMessage(standardMarket, errorMessagePrompt);
                            return true;
                        }
                    }
                } else {
                    if (orderMarketId.equals(standardMarket.getId())) {
                        //判断盘口状态是否改变
                        if (!standardMarket.getStatus().equals(0)) {
                            log.info("::{}::{}其他球种:{}盘口有变化拒单::盘口ID:{}盘口状态:{},订单状态:{}", orderNo, third, sportId, standardMarket.getId(), standardMarket.getStatus(), orderStatus);
                            this.errorMessage(standardMarket, errorMessagePrompt);
                            return true;
                        }
                        //判断赔率变动幅度是否超出限制
                        String oddsScopeValue = getOddsScope(orderNo, tOrderDetail.getTournamentId(), tOrderDetail.getMatchId(), tOrderDetail.getPlayId(), tOrderDetail.getItemBean().getMatchType(), third);
                        if (StringUtils.isNotBlank(oddsScopeValue)) {
                            BigDecimal oddsScope = new BigDecimal(oddsScopeValue).divide(new BigDecimal(100), 4, RoundingMode.DOWN);
                            BigDecimal one = new BigDecimal(1);
                            BigDecimal orderOdds = new BigDecimal(tOrderDetail.getOdds());
                            BigDecimal checkOdds = BigDecimal.ZERO;
                            for (int j = 0; j < standardMarket.getMarketOddsList().size(); j++) {
                                StandardMarketOddsMessage standardMarketOdds = standardMarket.getMarketOddsList().get(j);
                                if (StringUtils.equals(String.valueOf(standardMarketOdds.getId()), String.valueOf(tOrderDetail.getSelectId()))) {
                                    log.info("::{}::{}获取到盘口投注项::{}", orderNo, third, JSONObject.toJSONString(standardMarketOdds));
                                    checkOdds = new BigDecimal(standardMarketOdds.getOddsValue()).divide(new BigDecimal(100000), 4, RoundingMode.DOWN);
                                }
                            }
                            log.info("::{}::{}赔率校验::订单赔率:{},配置值赔率范围:{},checkOdds:{}", orderNo, third, orderOdds, oddsScope, checkOdds);
                            if (one.divide(orderOdds, 4, RoundingMode.DOWN).subtract(one.divide(checkOdds, 4, RoundingMode.DOWN)).abs().compareTo(oddsScope) > 0) {
                                log.info("::{}::{}盘口赔率变动幅度过大拒单", orderNo, third);
                                errorMessagePrompt.setHintMsg("赔率变动幅度过大拒单");
                                return true;
                            }
                        }
                    }
                    Integer matchType = Integer.valueOf(tOrderDetail.getIsScroll());
                    //盘口的位置有变化
                    if (StringUtils.equalsIgnoreCase(String.valueOf(standardMarket.getMarketCategoryId()), String.valueOf(tOrderDetail.getPlayId())) && StringUtils.equalsIgnoreCase(String.valueOf(standardMarket.getChildMarketCategoryId()), tOrderDetail.getSubPlayId()) && standardMarket.getMarketType().equals(matchType) && tOrderDetail.getItemBean().getPlaceNum().equals(standardMarket.getPlaceNum()) && !StringUtils.equalsIgnoreCase(String.valueOf(tOrderDetail.getMarketId()), String.valueOf(standardMarket.getId()))) {
                        log.info("::{}::{}盘口位置有变化拒单::盘口ID:{},盘口位置:{},订单盘口位置:{}", orderNo, third, standardMarket.getId(), standardMarket.getPlaceNum(), tOrderDetail.getItemBean().getPlaceNum());
                        errorMessagePrompt.setHintMsg("对应坑位的盘口值已变更拒单");
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("::{}::{}赛事盘口状态检查异常::", orderNo, third, e);
        }
        return false;
    }


    /**
     * 赛事、盘口状态检查
     *
     * @param orderStatus        订单状态
     * @param orderNo            注单号
     * @param errorMessagePrompt 错误提示
     * @return 赛事、盘口状态接拒
     */
    @Override
    public boolean matchAndMarketCheck(String orderStatus, String orderNo, ErrorMessagePrompt errorMessagePrompt, String third) {
        log.info("::{}::赛事、盘口状态判断开始", orderNo);
        try {
            if (!ACCEPTED.equals(orderStatus)) {
                log.info("::{}::赛事、盘口状态判断,订单状态：{},跳过:", orderNo, orderStatus);
                return false;
            }
            List<TOrderDetail> tOrderDetailList = orderDetailMapper.queryOrderDetails(orderNo);
            for (TOrderDetail tOrderDetail : tOrderDetailList) {
                //检查赛事状态
                if (checkMatchStatus(String.valueOf(tOrderDetail.getMatchId()), tOrderDetail.getOrderNo(), errorMessagePrompt, third)) {
                    return true;
                }
                //检查盘口状态
                String marketKey = String.format(REDIS_MATCH_MARKET_ODDS_NEW, tOrderDetail.getPlayId(), tOrderDetail.getMatchId());
                String matchMarketOddsStr = RcsLocalCacheUtils.getValue(marketKey, redisClient::get);
                if (StringUtils.isBlank(matchMarketOddsStr)) {
                    log.warn("::{}::赛事、盘口状态判断获取到盘口数据为空,跳过", orderNo);
                    continue;
                }
                List<RcsStandardMarketDTO> rcsStandardMarketDTOS = JSONObject.parseArray(matchMarketOddsStr, RcsStandardMarketDTO.class);
                log.info("::{}::赛事、盘口状态判断获取到盘口数据:{}", orderNo, JSONObject.toJSONString(rcsStandardMarketDTOS));
                for (RcsStandardMarketDTO rcsStandardMarketDTO : rcsStandardMarketDTOS) {
                    if (rcsStandardMarketDTO.getId().equals(String.valueOf(tOrderDetail.getMarketId()))) {
                        if (!rcsStandardMarketDTO.getStatus().equals(0) || rcsStandardMarketDTO.getThirdMarketSourceStatus() != 0) {
                            log.info("::{}::{}盘口有变化拒单::盘口ID:{}盘口状态:{},订单状态:{}", tOrderDetail.getOrderNo(), third, rcsStandardMarketDTO.getId(), rcsStandardMarketDTO.getStatus(), tOrderDetail.getOrderStatus());
                            errorMessagePrompt.setHintMsg("盘口状态变化拒单");
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("::{}::订单赛事、盘口状态判断异常:", orderNo, e);
        }
        return false;
    }


    /**
     * 滚球实时接拒
     *
     * @param tOrderDetailList   订单列表
     * @param errorMessagePrompt 错误提示
     * @return 是否接拒
     */
    @Override
    public boolean dealWithData(List<ExtendBean> tOrderDetailList, ErrorMessagePrompt errorMessagePrompt, String third, Integer OrderStatus) {
        for (ExtendBean tOrderDetail : tOrderDetailList) {
            //检查赛事状态
            if (checkMatchStatus(tOrderDetail.getMatchId(), tOrderDetail.getOrderId(), errorMessagePrompt, third)) {
                return true;
            }
            //盘口维度
            if (checkMarketStatus(tOrderDetail, errorMessagePrompt, third, OrderStatus)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 赔率变动范围
     *
     * @param list 订单列表
     * @return 赔率变动范围
     */
    @Override
    public Map<String, String> queryOddsRange(List<OrderItem> list, String third) {
        Map<String, String> oddsRange = new HashMap<>();
        //获取注单号
        String orderNo = list.get(0).getOrderNo();
        String defaultRange = "";
        try {
            for (OrderItem item : list) {
                //投注项id
                String payOptionsId = String.valueOf(item.getPlayOptionsId());
                //判断赛事赔率开关是否开启
                String oddsScopeMatchStatus = "rcs:risk:order:oddsScope:match.%s.match_type.%s";
                oddsScopeMatchStatus = String.format(oddsScopeMatchStatus, item.getMatchId(), item.getMatchType() == 1 ? 1 : 0);
                oddsScopeMatchStatus = redisClient.get(oddsScopeMatchStatus);
                log.info("::{}::{}赛事级别赔率变动范围开关:{}", item.getOrderNo(), third, oddsScopeMatchStatus);
                if (StringUtils.isBlank(oddsScopeMatchStatus) || oddsScopeMatchStatus.equals("null") || oddsScopeMatchStatus.equals("0")) {
                    //根据联赛等级设置的 赔率范围
                    defaultRange = getTournamentOddScope(item.getTournamentId(), orderNo, third);
                    oddsRange.put(payOptionsId, defaultRange);
                    continue;
                }
                //玩法赔率接单范围获取
                String oddsScopePlay = "rcs:risk:order:oddsScope:match.%s.play_id.%s.match_type.%s";
                oddsScopePlay = String.format(oddsScopePlay, item.getMatchId(), item.getPlayId(), item.getMatchType() == 1 ? 1 : 0);
                oddsScopePlay = redisClient.get(oddsScopePlay);
                log.info("::{}::{}玩法级别赔率变动范围:{}", item.getOrderNo(), third, oddsScopePlay);
                if (StringUtils.isNotBlank(oddsScopePlay) && !oddsScopePlay.equals("null")) {
                    oddsRange.put(payOptionsId, oddsScopePlay);
                } else {
                    //根据联赛等级设置的 赔率范围
                    defaultRange = getTournamentOddScope(item.getTournamentId(), orderNo, third);
                    oddsRange.put(payOptionsId, defaultRange);
                }
            }
        } catch (Exception e) {
            log.error("::{}::{}获取盘口赔率变动范围异常:", orderNo, third, e);
        }
        return oddsRange;
    }


    /**
     * 如果不是安全事件直接返回false
     *
     * @param list 订单信息
     * @return 是或否
     */
    @Override
    public boolean isSafe(List<ExtendBean> list) {
        for (ExtendBean orderItem : list) {
            if (SportIdEnum.isFootball(Long.valueOf(orderItem.getSportId()))) {
                Request<OrderItem> request = new Request<>();
                request.setData(orderItem.getItemBean());
                RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig = templateAcceptConfigServer.queryAcceptConfig(request).getData();
                if (!MatchEventConfigEnum.EVENT_SAFETY.getCode().equalsIgnoreCase(rcsTournamentTemplateAcceptConfig.getEventType())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 错误信息封装
     *
     * @param standardMarketMessage 标准盘口错误
     * @param errorMessagePrompt    错误提示
     */
    private void errorMessage(StandardMarketMessage standardMarketMessage, ErrorMessagePrompt errorMessagePrompt) {
        if (standardMarketMessage.getThirdMarketSourceStatus() == 1) {
            errorMessagePrompt.setHintMsg("盘口封盘(数据商)拒单");
        }
        if (standardMarketMessage.getThirdMarketSourceStatus() == 2) {
            errorMessagePrompt.setHintMsg("盘口关盘(数据商)拒单");
        }
        if (standardMarketMessage.getStatus() == 1 && standardMarketMessage.getThirdMarketSourceStatus() == 0) {
            errorMessagePrompt.setHintMsg("盘口封盘拒单");
        }
        if (standardMarketMessage.getStatus() == 2 && standardMarketMessage.getThirdMarketSourceStatus() == 0) {
            errorMessagePrompt.setHintMsg("盘口关盘拒单");
        }
        if (standardMarketMessage.getStatus() == 11 && standardMarketMessage.getThirdMarketSourceStatus() == 0) {
            errorMessagePrompt.setHintMsg("盘口锁盘拒单");
        }
    }


    /**
     * 获取盘口赔率变动范围
     *
     * @param orderNo      订单号
     * @param tournamentId 联赛id
     * @param matchId      赛事id
     * @param playId       玩法id
     * @param matchType    赛事阶段
     * @param third        三方标志
     * @return 赔率变动范围
     */
    @Override
    public String getOddsScope(String orderNo, Long tournamentId, String matchId, String playId, Integer matchType, String third) {
        String oddsScopeValue = "";
        try {
            //检查赛事玩法赔率变动设置
            String k = "rcs:risk:order:oddsScope:match.%s.match_type.%s";
            k = String.format(k, matchId, matchType == 1 ? 1 : 0);
            String v = redisClient.get(k);
            log.info("::{}::{}获取到赛事玩法设置的赔率范围开关:{}::key:{}", orderNo, third, v, k);
            if (StringUtils.isNotBlank(v) && "1".equals(v)) {
                String playOddsKey = String.format(ODDS_SCOPE_KEY, matchId, playId, matchType == 2 ? 0 : 1);
                oddsScopeValue = redisClient.get(playOddsKey);
                log.info("::{}::{}获取到赛事玩法设置的赔率范围:{}::key:{}", orderNo, third, oddsScopeValue, playOddsKey);
                return oddsScopeValue;
            }
            //根据联赛等级设置的 赔率范围
            oddsScopeValue = getTournamentOddScope(tournamentId, orderNo, third);
        } catch (Exception e) {
            log.info("::{}::{}获取盘口赔率变动范围异常:", orderNo, third, e);
        }
        return oddsScopeValue;
    }


    /**
     * 获取联赛得赔率变动范围
     *
     * @param tournamentId 联赛id
     * @param orderNo      订单
     * @param third        三方标志
     * @return 赔率范围
     */
    private String getTournamentOddScope(Long tournamentId, String orderNo, String third) {
        String defaultRange = "";
        try {
            String tournamentKey = String.format("rcs:tournament:property:%s", tournamentId);
            String oddsChangeStatus = redisClient.hGet(tournamentKey, "oddsChangeStatus");
            log.info("::{}::{}获取到联赛设置的赔率范围开关:{}::key:{}", orderNo, third, oddsChangeStatus, tournamentKey + "--->" + "oddsChangeStatus");
            if (StringUtils.isNotBlank(oddsChangeStatus) && oddsChangeStatus.equals("1")) {
                String tournamentValue = String.format("rcs:tournament:property:%s", tournamentId);
                defaultRange = redisClient.hGet(tournamentValue, "MTSOddsChangeValue");
                log.info("::{}::{}获取联赛设置的赔率范围:{}::key:{}", orderNo, third, defaultRange, tournamentValue + "--->" + "MTSOddsChangeValue");
            }
        } catch (Exception e) {
            log.info("::{}::{}获取联赛等级设置的赔率异常:", orderNo, third, e);
        }
        return defaultRange;
    }

    /**
     * 从缓存中获取大数据下发得盘口赛事相关数据(赛事,盘口状态检查)
     *
     * @param matchId            赛事id
     * @param orderNo            注单id
     * @param marketId           盘口id
     * @param errorMessagePrompt 错误提示
     * @return 赛事状态盘口变更拒单
     */
    @Override
    public boolean checkMatchAndMarketStatus(String matchId, String marketId, String orderNo, ErrorMessagePrompt errorMessagePrompt, String third) {
        try {
            //获取盘口数据key
            String matchKey = String.format(STANDAR_MATCH_MARKET_INFO_OF_ODDIN, matchId);
            RedisClient redisClient = SpringContextUtils.getBeanByClass(RedisClient.class);
            String matchInfoStr = redisClient.get(matchKey);
            if (StringUtils.isBlank(matchInfoStr)) {
                log.info("::{}::{}::赛事状态检查,获取赛事数据为空::key:{}", ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, matchKey);
                errorMessagePrompt.setHintMsg("赛事不存在拒单");
                errorMessagePrompt.setCurrentEvent("match_handicap_status_deactivated-PA");
                return true;
            }
            StandardMatchVo standardMatchMessage = JSON.parseObject(matchInfoStr, StandardMatchVo.class);
            log.info("::{}::{}::赛事状态检查,获取到赛事数据::{}",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, JSONObject.toJSONString(standardMatchMessage));
            errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
            //赛事维度的判断
            if (standardMatchMessage.getStatus() != 0 && standardMatchMessage.getStatus() != 13) {
                if (standardMatchMessage.getStatus() == 1) {
                    errorMessagePrompt.setHintMsg("赛事封盘拒单");
                    errorMessagePrompt.setCurrentEvent("match_handicap_status_suspended-PA");
                    log.info("::{}::{}::{}投注赛事封盘拒单",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, third);
                } else if (standardMatchMessage.getStatus() == 2) {
                    errorMessagePrompt.setHintMsg("赛事关盘拒单");
                    errorMessagePrompt.setCurrentEvent("match_handicap_status_deactivated-PA");
                    log.info("::{}::{}::{}投注赛事关盘拒单",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, third);
                } else if (standardMatchMessage.getStatus() == 11) {
                    errorMessagePrompt.setHintMsg("赛事锁盘拒单");
                    errorMessagePrompt.setCurrentEvent("match_handicap_status_lock-PA");
                    log.info("::{}::{}::{}投注赛事锁盘拒单",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, third);
                }
                errorMessagePrompt.setBetNo(orderNo);
                return true;
            } else {
                //盘口维度的判断(封盘/关盘等等)
                List<StandardMarketVo> marketList = standardMatchMessage.getMarketList();
                log.info("::{}::{}::盘口状态检查,获取到赛盘口列表数据::{}",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, JSONObject.toJSONString(marketList));
                if (CollectionUtils.isNotEmpty(marketList)) {
                    for (StandardMarketVo marketVo : marketList) {
                        log.info("::{}::{}::盘口状态检查,盘口Id：{}:缓存中的盘口id：{}：：缓存中的盘口状态:{}::具体盘口数据::{}",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, marketId, marketVo.getMarketId(), marketVo.getStatus(), JSONObject.toJSONString(marketVo));
                        if (marketVo.getMarketId() != null && marketId.equals(marketVo.getMarketId().toString())) {
                            log.info("::{}::{}::{}:校验盘口状态：{}",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, marketVo.getMarketId(), marketVo.getStatus());
                            if (0 != marketVo.getStatus()) {
                                log.info("::{}::{}::{}:校验盘口状态：{}：：盘口状态不等于0",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, marketVo.getMarketId(), marketVo.getStatus());
                                if (marketVo.getStatus() == 1) {
                                    log.info("::{}::{}::{}::{}::投注盘口状态为1，封盘拒单",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, third, marketVo.getStatus());
                                    errorMessagePrompt.setHintMsg("盘口封盘拒单");
                                    errorMessagePrompt.setCurrentEvent("match_handicap_status_suspended-PA");
                                } else if (marketVo.getStatus() == 2) {
                                    log.info("::{}::{}::{}::{}::投注盘口状态为2，关盘拒单",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, third, marketVo.getStatus());
                                    errorMessagePrompt.setHintMsg("盘口关盘拒单");
                                    errorMessagePrompt.setCurrentEvent("match_handicap_status_deactivated-PA");
                                } else if (marketVo.getStatus() == 11) {
                                    log.info("::{}::{}::{}::{}::投注盘口状态为11，锁盘拒单",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, third, marketVo.getStatus());
                                    errorMessagePrompt.setHintMsg("盘口锁盘拒单");
                                    errorMessagePrompt.setCurrentEvent("match_handicap_status_lock-PA");
                                }
                                errorMessagePrompt.setBetNo(orderNo);
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("::{}::{}::{}赛事状态检查异常::",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, third, e);
        }
        return false;
    }
}
