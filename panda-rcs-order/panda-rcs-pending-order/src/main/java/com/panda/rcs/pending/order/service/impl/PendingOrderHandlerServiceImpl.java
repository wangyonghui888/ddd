package com.panda.rcs.pending.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.rcs.pending.order.constants.NumberConstant;
import com.panda.rcs.pending.order.constants.RedisKey;
import com.panda.rcs.pending.order.enums.MatchStatusEnum;
import com.panda.rcs.pending.order.enums.OrderStatusEnum;
import com.panda.rcs.pending.order.param.TournamentTemplateParam;
import com.panda.rcs.pending.order.pojo.*;
import com.panda.rcs.pending.order.service.IRcsTournamentTemplateService;
import com.panda.rcs.pending.order.service.PendingOrderHandlerService;
import com.panda.rcs.pending.order.service.RcsPendingOrderService;
import com.panda.rcs.pending.order.utils.CommonServer;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.TemplateAcceptConfigServer;
import com.panda.sport.data.rcs.dto.PendingOrderDto;
import com.panda.sport.data.rcs.dto.limit.RedisUpdateVo;
import com.panda.sport.data.rcs.dto.order.MatchEventInfoRes;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.enums.RedisCmdEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.redis.utils.RedisUtils;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 预约订单处理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PendingOrderHandlerServiceImpl implements PendingOrderHandlerService {

    private final RcsPendingOrderService pendingOrderService;

    @Autowired
    private RedisUtils redisUtils;

    private final ProducerSendMessageUtils sendMessage;

    private final IRcsTournamentTemplateService tournamentTemplateService;

    private final CommonServer commonServer;
    @Resource(name = "asyncPoolTaskExecutor")
    private  ThreadPoolTaskExecutor asyncPoolTaskExecutor;

    @Reference(check = false, lazy = true, retries = 3, timeout = 3000)
    private  TemplateAcceptConfigServer templateAcceptConfigServer;

    private final StandardMatchInfoMapper standardMatchInfoMapper;
    private static final List<Integer> PLAY_ID_LIST = Arrays.asList(3, 4, 19, 39, 46, 52, 58, 64, 69, 71, 113, 121, 128, 130, 143, 306, 308);
    private static final List<Integer> SPECIAL_GAME_PLAY_ID = Arrays.asList(141);
    private static final List<Integer> FIRST_HALF_PLAY_ID = Arrays.asList(17, 18, 19, 20, 21, 22, 23, 24, 29, 30, 42, 43, 69, 70, 87, 90, 97, 100, 105, 119, 120, 121, 122, 123, 124, 129, 130, 144, 228, 229, 230, 270, 308, 309, 311, 313, 316, 317, 319, 322, 323, 327, 328, 329, 332, 341, 345, 359);

    @Override
    public void handlerPendingOrder() {
        //获取预约中的订单赛事id
        List<Long> matchIds = pendingOrderService.selectPendingMatchIds();
        log.info("需要处理的预约订单赛事id集合大小:{} matchIds:{}", matchIds.size(), matchIds);
        for (Long matchId : matchIds) {
            //开启多线程执行任务
            asyncPoolTaskExecutor.execute(() -> {
                StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectById(matchId);
                if (Objects.isNull(standardMatchInfo)) {
                    log.info(":::赛事id:{} 暂无当前赛事状,跳过预约订单处理!", matchId);
                    //没有查询赛事的订单直接失败
                    processPendingOrder(matchId, OrderStatusEnum.FAIL);
                    return;
                }
                //判断赛事是否结束
                Integer matchStatus = standardMatchInfo.getMatchStatus();
                log.info(":::赛事id:{} 当前赛事状态:{},赛事信息:{}", matchId, matchStatus, JSON.toJSONString(standardMatchInfo));
                if (Arrays.asList(MatchStatusEnum.Ending.getCode(), MatchStatusEnum.Close.getCode()).contains(matchStatus)) {
                    //预约投注失败,处理失败订单
                    log.info(":::赛事id:{} ,赛事已经结束,修改预约订单状态!", matchId);
                    processPendingOrder(matchId, OrderStatusEnum.FAIL);
                    return;
                }

                //根据赛事id获取模板中的预约投注速率参数,默认100
                Integer matchType = matchStatus == NumberConstant.NUM_ZERO ? NumberConstant.NUM_ONE : NumberConstant.NUM_ZERO;
                Integer limit = tournamentTemplateService.getOrderRateLimit(matchId, matchType);
                //定时扫描预约订单,以盘口为单位
                List<RcsPendingOrder> orderList = pendingOrderService.selectPendingOrderList(matchId, limit);
                log.info("::赛事ID{}::预约速率:{},扫描订单长度:{}", matchId, limit, orderList.size());
                //统计发送了多少单
                LimitRateDto limitRateDto = new LimitRateDto();
                for (RcsPendingOrder rcsPendingOrder : orderList) {
                    log.info("::{}::订单详情信息:{},成功发送{}单，速率:{}", rcsPendingOrder.getOrderNo(), JSON.toJSONString(rcsPendingOrder), limitRateDto.getCountNum(), limit);
                    //bug43449 解决个别中文盘口影响后续数据处理的问题
                    try {
                        //速率如果达到了就就进入下一次
                        if (limitRateDto.getCountNum().intValue() == limit) {
                            continue;
                        }
                        //判断赛事是否进入了中场休息
                        if (firstHalfFinish(rcsPendingOrder)) {
                            continue;
                        }

                        //有盘口的情况
                        String matchMarketOddsStr = RcsLocalCacheUtils.getValueInfo(String.format(RedisKey.REDIS_MATCH_MARKET_ODDS_NEW, rcsPendingOrder.getPlayId(), rcsPendingOrder.getMatchId()));
                        if (StringUtils.isBlank(matchMarketOddsStr)) continue;
                        List<StandardMarketMessage> standardMarketMessageList = JSON.parseArray(matchMarketOddsStr, StandardMarketMessage.class);
                        //默认是不封盘
                        Integer isClose = NumberConstant.NUM_ZERO;
                        Integer checkOdds = NumberConstant.NUM_ZERO;
                        String marketValue = convertOdds(rcsPendingOrder.getMarketValue());
                        log.info("::{}::预约盘口值:{},预约盘口ID:{}", rcsPendingOrder.getOrderNo(), marketValue,rcsPendingOrder.getMarketId());
                        log.info("::{}::当前盘口信息列表:{}", rcsPendingOrder.getOrderNo(), JSON.toJSONString(standardMarketMessageList));
                        if (null != rcsPendingOrder.getMarketId()) {
                            Set<Long> set = standardMarketMessageList.stream().map(e->e.getId()).collect(Collectors.toSet());
                            if(!set.contains(rcsPendingOrder.getMarketId())) {
                                log.info("::{}::盘口值变化,取消预约订单。预约盘口ID不存在玩法当前盘口中：{}", rcsPendingOrder.getOrderNo(), marketValue);
                                rcsPendingOrder.setOrderStatus(OrderStatusEnum.FAIL.getCode());
                                rcsPendingOrder.setRemark("盘口值变化");
                                this.sendOrderMsg(Collections.singletonList(rcsPendingOrder));
                            }else {
                                for (StandardMarketMessage standardMarketMessage : standardMarketMessageList) {
                                    if (standardMarketMessage.getId().equals(rcsPendingOrder.getMarketId())) {
                                        log.info("::{}::盘口信息:{}", rcsPendingOrder.getOrderNo(), JSON.toJSONString(standardMarketMessage));
                                        //盘口值校验
                                        if(!StringUtils.isEmpty(standardMarketMessage.getAddition1()) && !StringUtils.isEmpty(marketValue)
                                                && !StringUtils.equalsIgnoreCase(standardMarketMessage.getAddition1(), marketValue)){
                                            log.info("::{}::盘口值变化,取消预约订单。预约盘口值:{},当前盘口值：{}", rcsPendingOrder.getOrderNo(), marketValue,standardMarketMessage.getAddition1());
                                            rcsPendingOrder.setOrderStatus(OrderStatusEnum.FAIL.getCode());
                                            rcsPendingOrder.setRemark("盘口值变化");
                                            this.sendOrderMsg(Collections.singletonList(rcsPendingOrder));
                                            continue;
                                        }
                                        isClose = standardMarketMessage.getStatus();
                                        for (StandardMarketOddsMessage standardMarketOddsMessage : standardMarketMessage.getMarketOddsList()) {
                                            if (standardMarketOddsMessage.getId().equals(rcsPendingOrder.getOddsId())) {
                                                checkOdds = standardMarketOddsMessage.getPaOddsValue();
                                            }
                                        }
                                    }
                                }
                            }

                            log.info("::{}::预约投注:有盘口值的情况:{}", rcsPendingOrder.getOrderNo(), checkOdds);
                        } else {//没有盘口值的情况
    //                        String marketValue = convertOdds(rcsPendingOrder.getMarketValue());
                            if (PLAY_ID_LIST.contains(rcsPendingOrder.getPlayId().intValue()) && StringUtils.equals(rcsPendingOrder.getOddType(), String.valueOf(NumberConstant.NUM_TWO))) {
                                marketValue = marketValue.startsWith("-") ? marketValue.replace("-", "") : "-" + marketValue;
                            }
                            log.info("::{}::没有盘口情况盘口值:{}", rcsPendingOrder.getOrderNo(), marketValue);
                            if (StringUtils.isNotBlank(marketValue)) {
                                for (StandardMarketMessage standardMarketMessage : standardMarketMessageList) {
                                    //71号玩法不判断盘口
                                    if (SPECIAL_GAME_PLAY_ID.contains(rcsPendingOrder.getPlayId().intValue())) {
                                        for (StandardMarketOddsMessage standardMarketOddsMessage : standardMarketMessage.getMarketOddsList()) {
                                            if (StringUtils.equalsIgnoreCase(standardMarketOddsMessage.getOddsType(), rcsPendingOrder.getOddType())) {
                                                this.setMarketInfo(rcsPendingOrder, standardMarketOddsMessage);
                                                checkOdds = standardMarketOddsMessage.getPaOddsValue();
                                                isClose = standardMarketMessage.getStatus();
                                            }
                                        }
                                    }
                                    if (StringUtils.equalsIgnoreCase(standardMarketMessage.getAddition1(), marketValue)) {
                                        for (StandardMarketOddsMessage standardMarketOddsMessage : standardMarketMessage.getMarketOddsList()) {
                                            if (StringUtils.equalsIgnoreCase(standardMarketOddsMessage.getOddsType(), rcsPendingOrder.getOddType())) {
                                                this.setMarketInfo(rcsPendingOrder, standardMarketOddsMessage);
                                                checkOdds = standardMarketOddsMessage.getPaOddsValue();
                                                isClose = standardMarketMessage.getStatus();
                                            }
                                        }
                                    }
                                }
                                log.info("::{}::预约投注,没有盘口ID的情况:{}", rcsPendingOrder.getOrderNo(), checkOdds);
                            }
                        }
                        //判断盘口是否已经结算
                        if (isSettlement(rcsPendingOrder)) {
                            continue;
                        }
                        log.info("::{}::盘口状态:{}", rcsPendingOrder.getOrderNo(), isClose);
                        //盘口不是封或者关状态才去检查
                        if (isClose == NumberConstant.NUM_ZERO) {
                            //检查订单是否成功
                            isSucceed(rcsPendingOrder, checkOdds, limitRateDto);
                        }
                    }catch (Exception e){
                        log.error("::{}::风控预约异常:{}", rcsPendingOrder.getOrderNo(), e.getMessage(), e);
                        rcsPendingOrder.setOrderStatus(OrderStatusEnum.FAIL.getCode());
                        rcsPendingOrder.setRemark("风控预约异常");
                        this.sendOrderMsg(Collections.singletonList(rcsPendingOrder));
                    }
                }
            });
        }
    }

    /**
     * 设置盘口id 和投注项id
     *
     * @param rcsPendingOrder           订单信息
     * @param standardMarketOddsMessage 投注项信息
     */
    private void setMarketInfo(RcsPendingOrder rcsPendingOrder, StandardMarketOddsMessage standardMarketOddsMessage) {
        rcsPendingOrder.setMarketId(standardMarketOddsMessage.getMarketId());
        rcsPendingOrder.setOddsId(standardMarketOddsMessage.getId());
    }

    /**
     * @param rcsPendingOrder
     * @param checkOdds
     * @return
     */
    private void isSucceed(RcsPendingOrder rcsPendingOrder, Integer checkOdds, LimitRateDto limitRateDto) {
        if (new BigDecimal(rcsPendingOrder.getOrderOdds()).compareTo(BigDecimal.valueOf(checkOdds)) <= NumberConstant.NUM_ZERO) {
            log.info("::{}::预约成功,订单信息:{}", rcsPendingOrder.getOrderNo(), rcsPendingOrder);
            //检查商户单场配置余额是否充足
            String busKey = String.format(RedisKey.RESERVE_REDIS_BUS_KEY, rcsPendingOrder.getMerchantId(), rcsPendingOrder.getMatchId());
            String busVal = redisUtils.get(busKey);
            Long busBetAmount = StringUtils.isNotBlank(busVal) ? Long.valueOf(busVal) : NumberConstant.LONG_ZERO;
            log.info("::{}::预约成功，商户单场key:{}商户单场累计限额:{}", rcsPendingOrder.getOrderNo(), busKey, busVal);
            TournamentTemplateParam tournamentTemplateParam = CommonParam.getTemplateConfig(rcsPendingOrder);
            TournamentTemplateVo templateVo = tournamentTemplateService.queryPendingOrder(tournamentTemplateParam);
            if (Objects.isNull(templateVo)) {
                log.info("::{}::模板配置没有找到:{}", rcsPendingOrder.getOrderNo(), JSON.toJSONString(tournamentTemplateParam));
                return;
            }
            log.info("::{}::预约成功，商户单场key:{},商户单场累计限额:{},模板配置额度:{}", rcsPendingOrder.getOrderNo(), busKey, busVal, JSON.toJSONString(templateVo));

            if (templateVo.getBusinesPendingOrderPayVal() > busBetAmount) {
                //累加
                long amount = redisUtils.incrBy(busKey, rcsPendingOrder.getBetAmount());
                List<RedisUpdateVo> redisUpdateList = commonServer.getUpdateList(rcsPendingOrder.getOrderNo());
                if (Objects.isNull(redisUpdateList)) {
                    redisUpdateList = new ArrayList<>();
                }
                redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.INCRBYFLOAT.getCmd(), busKey, "", String.valueOf(rcsPendingOrder.getBetAmount()), String.valueOf(amount)));
                commonServer.saveRedisUpdateRecord(rcsPendingOrder.getOrderNo(), redisUpdateList);
                limitRateDto.setCountNum(limitRateDto.getCountNum() + NumberConstant.NUM_ONE);
                rcsPendingOrder.setOrderStatus(OrderStatusEnum.SUCCESS.getCode());
                sendOrderMsg(Collections.singletonList(rcsPendingOrder));
            } else {
                log.info("::{}::商户单场可用额度已用完:{}", rcsPendingOrder.getOrderNo(), templateVo.getBusinesPendingOrderPayVal() - busBetAmount);
                rcsPendingOrder.setRemark("商户单场可用额度已用完");
                rcsPendingOrder.setOrderStatus(OrderStatusEnum.FAIL.getCode());
                sendOrderMsg(Collections.singletonList(rcsPendingOrder));
            }
        }
    }

    private boolean isSettlement(RcsPendingOrder rcsPendingOrder) {
        //先判断是否已经结算
        String settlementKey = RedisKey.getMatchMarketSettlement(rcsPendingOrder.getSportId(), rcsPendingOrder.getMatchId(), rcsPendingOrder.getMarketId(), rcsPendingOrder.getOddsId());
        String settlementVal = RcsLocalCacheUtils.getValueInfo(settlementKey);
        log.info("::{}::结算key:{},结算val:{}", rcsPendingOrder.getOrderNo(), settlementKey, settlementVal);
        if (StringUtils.isNotBlank(settlementVal) && Integer.valueOf(settlementVal) == NumberConstant.NUM_ONE) {
            rcsPendingOrder.setOrderStatus(OrderStatusEnum.FAIL.getCode());
            rcsPendingOrder.setRemark("盘口提前结算");
            this.sendOrderMsg(Collections.singletonList(rcsPendingOrder));
            return true;
        }
        return false;
    }

    //上半场结束
    private boolean firstHalfFinish(RcsPendingOrder rcsPendingOrder) {
        String periodStr = this.getMatchPeriod(rcsPendingOrder);
        if (StringUtils.isNotBlank(periodStr)) {
            Integer period = Integer.valueOf(periodStr);
            if (SportIdEnum.isFootball(rcsPendingOrder.getSportId()) || SportIdEnum.isBasketball(rcsPendingOrder.getSportId())) {
                if (Arrays.asList(302, 31).contains(period) && FIRST_HALF_PLAY_ID.contains(rcsPendingOrder.getPlayId().intValue())) {
                    //中场休息秒接
                    log.info("::{}::中场休息,上半场结束，通知业务上半场玩法失败。", rcsPendingOrder.getOrderNo());
                    rcsPendingOrder.setOrderStatus(OrderStatusEnum.FAIL.getCode());
                    rcsPendingOrder.setRemark("上半场结束");
                    this.sendOrderMsg(Collections.singletonList(rcsPendingOrder));
                    return true;
                }
            }
        }
        return false;
    }

    private String getMatchPeriod(RcsPendingOrder rcsPendingOrder) {
        Request<MatchEventInfoRes> request = new Request<>();
        MatchEventInfoRes matchEventInfoRes = new MatchEventInfoRes();
        matchEventInfoRes.setMatchId(rcsPendingOrder.getMatchId());
        request.setData(matchEventInfoRes);
        String val = templateAcceptConfigServer.queryMatchEventInfo(request).getData();
        log.info("::{}::获取到赛事阶段信息:{}", rcsPendingOrder.getOrderNo(), val);
        return val;
    }

    /**
     * 通过赛事 matchId 处理预约失败的注单
     */
    private void processPendingOrder(Long matchId, OrderStatusEnum orderStatusEnum) {
        LambdaQueryWrapper<RcsPendingOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RcsPendingOrder::getMatchId, matchId);
        queryWrapper.eq(RcsPendingOrder::getOrderStatus, OrderStatusEnum.PENDING.getCode());
        List<RcsPendingOrder> orderList = pendingOrderService.list(queryWrapper);
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }
        //更新预约订单状态为取消
        Date date = new Date();
        orderList.forEach(order -> {
            order.setOrderStatus(orderStatusEnum.getCode());
            order.setUpdateTime(date.getTime());
            order.setTriggerTime(date.getTime());
            order.setCancelTime(date.getTime());
            order.setRemark("赛事已经结束");
        });
        //告诉业务失败
        this.sendOrderMsg(orderList);
    }

    /**
     * 下发通知业务取消预约订单
     *
     * @param orderList 订单信息
     */
    private void sendOrderMsg(List<RcsPendingOrder> orderList) {
        //预约投注forecast,货量计算;
        for (RcsPendingOrder pendingOrder : orderList) {
            PendingOrderDto pendingOrderDto = BeanCopyUtils.copyProperties(pendingOrder, PendingOrderDto.class);
            sendMessage.sendMessage("RCS_PENDING_ORDER", "", pendingOrder.getOrderNo(), pendingOrderDto);
            log.info("::RCS_PENDING_ORDER::通知业务下发预约订单号:{},订单详情:{}", pendingOrderDto.getOrderNo(), JSON.toJSONString(pendingOrder));
        }
    }


    private String convertOdds(String oddsValue) {
        String newValue = "";
        String temp = "";
        if (oddsValue.startsWith("+")) {
            newValue = oddsValue.replace("+", "");
        } else if (oddsValue.startsWith("-")) {
            newValue = oddsValue.replace("-", "");
            temp = "-";
        }
        if (newValue.contains("/")) {
            String[] attr = newValue.split("/");
            BigDecimal odds = new BigDecimal(attr[0]).add(BigDecimal.valueOf(0.25));
            return temp + odds;
        }
        if (oddsValue.contains("/")) {
            String[] attr = oddsValue.split("/");
            BigDecimal odds = new BigDecimal(attr[0]).add(BigDecimal.valueOf(0.25));
            return String.valueOf(odds);
        }
        return StringUtils.isBlank(newValue) ? temp + oddsValue : temp + newValue;
    }
}
