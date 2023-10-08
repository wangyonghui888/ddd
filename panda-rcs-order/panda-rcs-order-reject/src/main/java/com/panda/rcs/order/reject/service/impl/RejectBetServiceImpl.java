package com.panda.rcs.order.reject.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.rcs.order.reject.constants.NumberConstant;
import com.panda.rcs.order.reject.constants.RedisKey;
import com.panda.rcs.order.reject.entity.*;
import com.panda.rcs.order.reject.entity.enums.VarSwitchEnum;
import com.panda.rcs.order.reject.enums.CategorySetCodeEnum;
import com.panda.rcs.order.reject.mapper.MatchInfoMapper;
import com.panda.rcs.order.reject.service.CommonSendMsgServer;
import com.panda.rcs.order.reject.service.MatchInfoService;
import com.panda.rcs.order.reject.service.RejectBetService;
import com.panda.rcs.order.reject.service.RejectTemplateAcceptConfigServer;
import com.panda.rcs.order.reject.utils.RedisUtils;
import com.panda.rcs.order.reject.utils.SendMessageUtils;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.enums.MatchEventConfigEnum;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.data.rcs.api.tournament.TournamentTemplateByMatchService;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.PreOrderDetailRequest;
import com.panda.sport.data.rcs.dto.PreOrderRequest;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.data.rcs.dto.tournament.MatchTemplateDataReqVo;
import com.panda.sport.data.rcs.dto.tournament.MatchTemplateDataResVo;
import com.panda.sport.data.rcs.dto.tournament.MatchTemplatePlayMarginDataReqVo;
import com.panda.sport.data.rcs.dto.tournament.MatchTemplatePlayMarginDataResVo;
import com.panda.sport.data.rcs.vo.MatchEventInfo;
import com.panda.sport.data.rcs.vo.RcsTournamentTemplateAcceptConfig;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.enums.*;
import com.panda.sport.rcs.mapper.RcsMarketCategorySetRelationMapper;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.StandardMatchTeamRelationMapper;
import com.panda.sport.rcs.mapper.limit.RcsLabelLimitConfigMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplateRefMapper;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.pojo.reject.RcsGoalWarnSet;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.service.IRcsUserConfigNewService;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.JedisCluster;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.panda.rcs.order.reject.utils.DataUtils.timeStamp2Date;

/**
 * @author :  koala
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.rcs.order.reject.service.impl
 * @Description :  简易投注接距逻辑
 * @Date: 2022-11-06 13:52
 * --------  ---------  --------------------------
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RejectBetServiceImpl implements RejectBetService {
    @Reference(check = false, lazy = true, retries = 3, timeout = 3000)
    private LimitApiService limitApiService;
    @Reference(check = false, lazy = true, retries = 3, timeout = 3000)
    private TournamentTemplateByMatchService tournamentTemplateByMatchService;
    private final MatchInfoMapper matchInfoMapper;
    private final RedisUtils redisUtils;

    private final JedisCluster jedisCluster;

    private final CommonSendMsgServer commonSendMsgServerImpl;
    private final RejectTemplateAcceptConfigServer templateAcceptConfigServerImpl;
    private final IRcsUserConfigNewService rcsUserConfigNewService;
    private final RcsLabelLimitConfigMapper labelLimitConfigMapper;
    private final RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;
    private final RcsTournamentTemplateRefMapper templateRefMapper;
    private final RcsMarketCategorySetRelationMapper rcsMarketCategorySetRelationMapper;
    private final MatchInfoService matchInfoService;
    private final SendMessageUtils sendMessage;
    private static final List<Integer> DISTANCE_PLAY_ID_LIST = Arrays.asList(1, 17, 25, 111, 119, 126, 129, 7, 20, 74, 3, 6, 8, 9, 13, 14, 16, 21, 22, 23, 27, 28, 29, 30, 31, 32, 35, 36, 44, 49, 50, 55, 56, 61, 62, 67, 68, 69, 70, 71, 72, 73, 85, 95, 101, 102, 103, 104, 105, 106, 107, 108, 112, 117, 120, 125, 137, 141, 147, 148, 149, 150, 151, 152, 159, 161, 166, 167, 170, 171, 174, 190, 197, 200, 204, 209, 210, 211, 212, 213, 216, 217, 218, 222, 223, 224, 225, 226, 227, 228, 230, 231, 235, 236, 237, 238, 239, 241, 260, 261, 265, 267, 273, 275, 277, 296, 297, 298, 340, 344, 345, 346, 347, 348, 349, 350, 351, 353, 360, 354, 355, 356, 357, 358);
    private static final List<Integer> sportList = Arrays.asList(2, 3, 4, 5, 7, 8, 9, 10);
    private static final List<String> VAR_EVENT_LIST = Arrays.asList("possible_var", "possible_video_assistant_referee", "var_reason", "var_reviewing", "video_assistant_referee");

    private static final List<String> thirdDataSources = Arrays.asList("BE");

    private static final List<String> SPECEVENT = Arrays.asList("penalty_awarded", "breakaway", "dfk", "danger_ball");
    private final StandardMatchTeamRelationMapper standardMatchTeamRelationMapper;
    private final StandardMatchInfoMapper standardMatchInfoMapper;


    public void updateThirdMaxTime(OrderItem orderItem) {
        MtsTemplateConfigVo mtsTemplateConfigVo = this.queryMtsConfig(orderItem);
        log.info("::{}::{}注单获取到MTS-1配置信息={}", orderItem.getOrderNo(), orderItem.getDataSourceCode(), JSONObject.toJSONString(mtsTemplateConfigVo));
        //默认3s
        long maxWaitTime = orderItem.getBetTime() + (long) MatchEventConfigEnum.EVENT_SAFETY.getValue() * MatchEventConfigEnum.ORDER_SECOND_UNIT.getValue();
        int minWaitTime = MatchEventConfigEnum.EVENT_SAFETY.getValue();
        //开关打开 并且有设置延时时间
        if (Objects.nonNull(mtsTemplateConfigVo) && mtsTemplateConfigVo.getMtsSwitch() == NumberConstant.NUM_ONE && Objects.nonNull(mtsTemplateConfigVo.getWaitTime())) {
            maxWaitTime = orderItem.getBetTime() + mtsTemplateConfigVo.getWaitTime().longValue() * MatchEventConfigEnum.ORDER_SECOND_UNIT.getValue();
            //小于等于3秒取设置值 否则最小接单时间为3秒
            if (mtsTemplateConfigVo.getWaitTime() <= MatchEventConfigEnum.EVENT_SAFETY.getValue()) {
                minWaitTime = mtsTemplateConfigVo.getWaitTime();
            }
        }
        orderItem.setMaxAcceptTime(maxWaitTime);
        orderItem.setMinWait(minWaitTime);
    }

    /**
     * 提前结算风控 接距逻辑
     *
     * @param orderBean 订单对象
     */
    @Override
    public void preSettleReject(PreOrderRequest orderBean) {
        try {
            String orderNo = orderBean.getOrderNo();
            log.info("::{}::提前结算投注订单:{}", orderNo, JSONObject.toJSONString(orderBean));

            for (PreOrderDetailRequest orderItem : orderBean.getDetailList()) {
                if (orderItem.getSportId() == SportIdEnum.FOOTBALL.getId().intValue() && orderItem.getMatchType() == NumberConstant.NUM_TWO) {
                    RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig = templateAcceptConfigServerImpl.queryAcceptConfig(orderItem);
                    this.updatePreSettleInfo(orderBean, orderItem, rcsTournamentTemplateAcceptConfig);
                }
            }
            //只要不是等待状态都发送给业务
            if (!Objects.equals(orderBean.getOrderStatus(), OrderStatusEnum.ORDER_WAITING.getCode())) {
                updateWaitTime(orderBean);
                this.sendOrderMsg(orderBean);
                log.info("::{}::提前結算投注开始发送" + (orderBean.getOrderStatus() == 1 ? "接单" : "拒单"), orderNo);
                return;
            }
            //  接单时间判断
            if (processWaitingTime(orderBean)) {
                updateWaitTime(orderBean);
                orderBean.setOrderStatus(OrderStatusEnum.ORDER_ACCEPT.getCode());
                log.info("::{}::提前結算到达最大等待时间开始发送订单信息", orderNo);
                this.sendOrderMsg(orderBean);
                return;
            }
            //修改订单信息
            sendMessage.sendDelayMessage(MqConstants.RCS_PRE_SETTLE_ORDER_REJECT, MqConstants.RCS_PRE_SETTLE_ORDER_REJECT_TAG,
                    orderBean.getOrderNo(), orderBean);

        } catch (Exception e) {
            log.error("::{}::处理提前結算订单异常：{}", orderBean.getOrderNo(), e.getMessage(), e);
        }
    }

    private void updateWaitTime(PreOrderRequest orderBean) {
        orderBean.setHandleStatus(NumberUtils.INTEGER_ONE);
        orderBean.setWaitTime((int) (System.currentTimeMillis() - orderBean.getReqTime()));
    }

    /**
     * 处理VR/赔率变化/盘口风控/数据商封盘/延时秒数 接距逻辑
     *
     * @param orderBean 订单对象
     */
    @Override
    public void contactService(OrderBean orderBean) {
        try {
            //1601需求 如果VAR处于等待状态就一直不处理
            String orderNo = orderBean.getOrderNo();
            log.info("::{}::简易投注订单", orderNo);
            for (OrderItem orderItem : orderBean.getItems()) {
                long matchId = orderItem.getMatchId();
                //dev-2576 系统和模板VAR收单开关都开启
                if (matchInfoService.getVarSwitchStatus(String.valueOf(matchId))) {
                    log.info("::{}::var收单开关打开", orderNo);
                    //判断缓存var收单状态:rcs:order:var:accept:=1
                    if (VarSwitchEnum.Open.getCode().equalsIgnoreCase(matchInfoService.getVarAccept(String.valueOf(matchId)))) {
                        log.info("::{}::var收单状态打开", orderNo);
                        String varOrderCountKey = String.format(RedisKey.REDIS_VAR_ORDER_LIST_COUNT, matchId);
                        //获取var注单数量
                        long varOrderCount = jedisCluster.incrBy(varOrderCountKey, 1);
                        log.info("::{}::var收单数量：{}", orderNo, varOrderCount);
                        //总数 除以 每个list的size:100 向上取整
                        long index = (int) Math.ceil((double) varOrderCount / 100);
                        log.info("::{}::var收单集合数量：{}", orderNo, index);

                        //缓存注单信息到redis，过期时间605S
                        String varListIndexKey = String.format(RedisKey.REDIS_VAR_ORDER_LIST_MATCH_INDEX, matchId, index);

                        long index1 = jedisCluster.lpush(varListIndexKey, JSON.toJSONString(orderBean));
                        log.info("::{}::var收单放入到集合数量：{}", orderNo, index1);

                        jedisCluster.expire(varListIndexKey, 605);
                        log.info("::{}::缓存注单到VAR收单redis：{}", orderNo, jedisCluster.lrange(varListIndexKey, 0, -1));
                        //发送VAR收单MQ消息到业务
                        this.sendVarMqToBss(orderNo, orderBean.getOrderGroup());
                        return;
                    }
                }
                String matchKey = String.format(RedisKey.MATCH_EVENT_KOALA_REDIS_KEY, matchId);
                String matchVal = RcsLocalCacheUtils.getValueInfo(matchKey);
                if (StringUtils.equalsIgnoreCase("var_reason", matchVal)) {
                    log.info("::{}::简易投注var_reason接距等待处理:{}", orderNo, matchVal);
                    sendMessage.sendDelayMessage(RedisKey.REJECT_BET_ORDER, "reject_bet", orderBean.getOrderNo(), orderBean);
                    return;
                }
            }

            for (OrderItem orderItem : orderBean.getItems()) {
                //初始化不是足球赛种的等待时间
                if (orderItem.getMaxAcceptTime() == null && orderItem.getMatchType() == NumberConstant.NUM_TWO && orderItem.getSportId() != SportIdEnum.FOOTBALL.getId().intValue()) {
                    this.initWaitTime(orderBean, orderItem);
                }
                //初始化足球接距逻辑和等待时间
                if (orderItem.getSportId() == SportIdEnum.FOOTBALL.getId().intValue() && orderItem.getMatchType() == NumberConstant.NUM_TWO) {
                    if (thirdDataSources.contains(orderItem.getDataSourceCode())) {
                        //需求2335 beter接拒特殊处理
                        this.updateThirdMaxTime(orderItem);
                    } else {
                        RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig = templateAcceptConfigServerImpl.queryAcceptConfig(orderItem);
                        this.updateOrderBeanInfo(orderBean, orderItem, rcsTournamentTemplateAcceptConfig);
                    }
                }
            }

            ErrorMessagePrompt errorMessagePrompt = new ErrorMessagePrompt();
            int state = processOrderStatus(orderBean, errorMessagePrompt);
            if (state == OrderStatusEnum.ORDER_WAITING.getCode()) {
                state = this.contactLogic(orderBean, errorMessagePrompt) ? OrderStatusEnum.ORDER_REJECT.getCode() : OrderStatusEnum.ORDER_WAITING.getCode();
            }

            //只要不是等待状态都发送给业务
            if (state != OrderStatusEnum.ORDER_WAITING.getCode()) {
                this.setUpHandStatus(orderBean, state);
                //组装orderbean
                orderBean.setOrderStatus(state);
                orderBean.setValidateResult(state);
                orderBean.setReason(errorMessagePrompt.getHintMsg());
                orderBean.setInfoStatus(errorMessagePrompt.getInfoStatus());
                log.info("::{}::简易投注开始发送" + (state == 1 ? "接单" : "拒单"), orderNo);
                this.sendOrderMsg(orderBean, errorMessagePrompt);
                return;
            }
            //  接单时间判断
            if (processWaitingTime(orderBean)) {
                this.setUpHandStatus(orderBean, OrderStatusEnum.ORDER_ACCEPT.getCode());
                if (Math.abs(System.currentTimeMillis() - orderBean.getItems().get(0).getBetTime()) >= 350 * 1000L) {
                    orderBean.setReason("到达最大等待时间305S");
                    orderBean.setValidateResult(OrderStatusEnum.ORDER_REJECT.getCode());
                    orderBean.setOrderStatus(OrderStatusEnum.ORDER_REJECT.getCode());
                    orderBean.setInfoStatus(OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
                    log.info("::{}::简易投注到达最大等待时间350S开始发送拒单信息", orderNo);
                } else {
                    orderBean.setReason("到达最大等待时间");
                    orderBean.setValidateResult(OrderStatusEnum.ORDER_ACCEPT.getCode());
                    orderBean.setOrderStatus(OrderStatusEnum.ORDER_ACCEPT.getCode());
                    orderBean.setInfoStatus(OrderInfoStatusEnum.SCROLL_PASS.getCode());
                    log.info("::{}::简易投注到达最大等待时间开始发送接单订单信息", orderNo);
                }
                this.sendOrderMsg(orderBean, errorMessagePrompt);
                return;
            }
            //修改订单信息
            sendMessage.sendDelayMessage(RedisKey.REJECT_BET_ORDER, "reject_bet", orderBean.getOrderNo(), orderBean);

        } catch (Exception e) {
            log.error("::{}::处理简易投注订单异常:", orderBean.getOrderNo(), e);
        }
    }

    /**
     * 发送VAR收单MQ消息到业务
     */
    private void sendVarMqToBss(String orderNo, String orderGroup) {
        JSONObject json = new JSONObject()
                .fluentPut("orderNo", orderNo)
                .fluentPut("orderGroup", orderGroup)
                .fluentPut("currentTime", System.currentTimeMillis());
        String topic = "VAR_STATUS_CHANGE_" + orderGroup;
        String tags = orderNo + "_" + orderGroup;
        String keys = orderNo;
        log.info("::{}::,发送VAR收单MQ消息到业务",
                orderNo);
        sendMessage.sendMessage(topic, tags, keys, json);
    }

    @Override
    public boolean checkOddsStatus(OrderBean orderBean, ErrorMessagePrompt errorMessagePrompt) {
        return contactLogic(orderBean, errorMessagePrompt);
    }

    /**
     * 处理赔率和盘口封盘，数据库商风控接距逻辑，判断是否拒单
     *
     * @param orderBean          订单对象
     * @param errorMessagePrompt 异常消息对象
     * @return 是否通过
     */
    private boolean contactLogic(OrderBean orderBean, ErrorMessagePrompt errorMessagePrompt) {
        for (OrderItem orderItem : orderBean.getItems()) {
            //如果不是滚球忽略掉
            if (orderItem.getMatchType() != NumberConstant.NUM_TWO) {
                continue;
            }
            errorMessagePrompt.setCurrentEvent(orderItem.getCurrentEvent());
            //赛事维度
            String matchInfoStr = RcsLocalCacheUtils.getValueInfo(String.format(com.panda.sport.rcs.constants.RedisKey.REDIS_MATCH_INFO, orderItem.getMatchId()));
            log.info("::{}::简易投注获取缓存赛事维度数据:" + (StringUtils.isBlank(matchInfoStr) ? "null" : "成功"), orderBean.getOrderNo());
            if (StringUtils.isNotBlank(matchInfoStr)) {
                StandardMatchMessage standardMatchMessage = JSON.parseObject(matchInfoStr, StandardMatchMessage.class);
                //收盘状态不拒单
                if (standardMatchMessage.getStatus() != NumberConstant.NUM_ZERO && standardMatchMessage.getStatus() != NumberConstant.NUM_THIRTEEN) {
                    errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
                    if (standardMatchMessage.getStatus() == NumberConstant.NUM_ONE) {
                        errorMessagePrompt.setHintMsg("赛事封盘拒单");
                        errorMessagePrompt.setCurrentEvent("match_handicap_status_suspended-PA");
                        log.info("::{}::简易投注赛事封盘拒单", orderBean.getOrderNo());
                    } else if (standardMatchMessage.getStatus() == NumberConstant.NUM_TWO) {
                        errorMessagePrompt.setHintMsg("赛事关盘拒单");
                        errorMessagePrompt.setCurrentEvent("match_handicap_status_deactivated-PA");
                        log.info("::{}::简易投注赛事关盘拒单", orderBean.getOrderNo());
                    } else if (standardMatchMessage.getStatus() == NumberConstant.NUM_ELEVEN) {
                        errorMessagePrompt.setHintMsg("赛事锁盘拒单");
                        errorMessagePrompt.setCurrentEvent("match_handicap_status_lock-PA");
                        log.info("::{}::简易投注赛事锁盘拒单", orderBean.getOrderNo());
                    }
                    errorMessagePrompt.setBetNo(orderItem.getBetNo());
                    return true;
                }
            }
            //盘口维度
            String matchMarketOddsStr = RcsLocalCacheUtils.getValueInfo(String.format(RedisKey.REDIS_MATCH_MARKET_ODDS_NEW, orderItem.getPlayId(), orderItem.getMatchId()));
            log.info("::{}::简易投注盘口维度数据:" + (StringUtils.isBlank(matchMarketOddsStr) ? "null" : "成功"), orderBean.getOrderNo());
            if (StringUtils.isNotBlank(matchMarketOddsStr)) {
                String oddsScopeValue = this.oddsRangeRedisNew(orderItem);
                log.info("::{}::简易投注配置的赔率:{},赛事ID:{}::", orderItem.getOrderNo(), oddsScopeValue, orderItem.getMatchId());
                List<StandardMarketMessageDto> rcsStandardMarketDTOS = JSON.parseArray(matchMarketOddsStr, StandardMarketMessageDto.class);
                for (StandardMarketMessageDto standardMarketMessage : rcsStandardMarketDTOS) {
                    if (orderItem.getSportId().longValue() == SportIdEnum.FOOTBALL.getId()) {
                        if (StringUtils.equalsIgnoreCase(String.valueOf(standardMarketMessage.getId()), String.valueOf(orderItem.getMarketId()))) {
                            //盘口状态有变化
                            if (standardMarketMessage.getStatus() != NumberConstant.NUM_ZERO || standardMarketMessage.getThirdMarketSourceStatus() != NumberConstant.NUM_ZERO) {
                                log.info("::{}::简易投注足球盘口有变化拒单::盘口ID:{}盘口状态:{},订单状态:{}", orderItem.getOrderNo(), standardMarketMessage.getId(), standardMarketMessage.getStatus(), orderItem.getOrderStatus());
                                this.errorMessage(standardMarketMessage, errorMessagePrompt);
                                errorMessagePrompt.setBetNo(orderItem.getBetNo());
                                return true;
                            }
                            MtsTemplateConfigVo mtsTemplateConfigVo = this.queryMtsConfig(orderItem);
                            //1682是否满足接距 MTS-1：模板配置不为空并且开关是打开的才走
                            if (Objects.nonNull(mtsTemplateConfigVo) && !"{}".equalsIgnoreCase(mtsTemplateConfigVo.toString())
                                    && Objects.nonNull(mtsTemplateConfigVo.getMtsSwitch())
                                    && Objects.nonNull(mtsTemplateConfigVo.getContactPercentage())
                                    && mtsTemplateConfigVo.getMtsSwitch() == NumberConstant.NUM_ONE) {
                                errorMessagePrompt.setMtsInfo(true);
                                BigDecimal oddsScope = mtsTemplateConfigVo.getContactPercentage().divide(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED), NumberConstant.NUM_FOUR, BigDecimal.ROUND_DOWN);
                                boolean flag = this.handlingOddsChanges(standardMarketMessage, orderItem, oddsScope, errorMessagePrompt);
                                if (flag) {
                                    errorMessagePrompt.setBetNo(orderItem.getBetNo());
                                    return true;
                                }
                            }
                        }
                    } else if (sportList.contains(orderItem.getSportId())) {
                        if (StringUtils.equalsIgnoreCase(String.valueOf(standardMarketMessage.getId()), String.valueOf(orderItem.getMarketId()))) {
                            //盘口状态有变化
                            if (!standardMarketMessage.getStatus().equals(NumberConstant.NUM_ZERO)) {
                                log.info("::{}::简易投注其他球种盘口有变化拒单::盘口ID:{}盘口状态:{},订单状态:{}", orderItem.getOrderNo(), standardMarketMessage.getId(), standardMarketMessage.getStatus(), orderItem.getOrderStatus());
                                this.errorMessage(standardMarketMessage, errorMessagePrompt);
                                errorMessagePrompt.setBetNo(orderItem.getBetNo());
                                return true;
                            }
                            //赔率有变化
                            if (StringUtils.isNotBlank(oddsScopeValue)) {
                                BigDecimal oddsScope = new BigDecimal(oddsScopeValue).divide(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED), NumberConstant.NUM_FOUR, BigDecimal.ROUND_DOWN);
                                boolean flag = this.handlingOddsChanges(standardMarketMessage, orderItem, oddsScope, errorMessagePrompt);
                                if (flag) {
                                    return true;
                                }
                            }

                        }
                        //0滚球 1早盘
                        Integer matchType = orderItem.getMatchType() == NumberConstant.NUM_TWO ? NumberConstant.NUM_ZERO : NumberConstant.NUM_ONE;
                        //盘口的位置有变化
                        if (StringUtils.equalsIgnoreCase(String.valueOf(standardMarketMessage.getMarketCategoryId()), String.valueOf(orderItem.getPlayId())) &&
                                StringUtils.equalsIgnoreCase(String.valueOf(standardMarketMessage.getChildMarketCategoryId()), orderItem.getSubPlayId()) &&
                                standardMarketMessage.getMarketType().equals(matchType) &&
                                orderItem.getPlaceNum().equals(standardMarketMessage.getPlaceNum()) &&
                                !StringUtils.equalsIgnoreCase(String.valueOf(orderItem.getMarketId()), String.valueOf(standardMarketMessage.getId()))) {
                            log.info("::{}::简易投注盘口位置有变化拒单::盘口ID:{},盘口位置:{},订单盘口位置:{}", orderBean.getOrderNo(), standardMarketMessage.getId(), standardMarketMessage.getPlaceNum(), orderItem.getPlaceNum());
                            errorMessagePrompt.setHintMsg("对应坑位的盘口值已变更拒单");
                            errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
                            errorMessagePrompt.setCurrentEvent(RedisKey.BET_ORDER_PLACE_NUM_CHANGE);
                            errorMessagePrompt.setBetNo(orderItem.getBetNo());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * 事件改变数据的状态
     *
     * @param orderBean          订单对象
     * @param errorMessagePrompt 异常编码对象
     * @return 状态编码
     */
    private Integer processOrderStatus(OrderBean orderBean, ErrorMessagePrompt errorMessagePrompt) {
        int countSuccess = 0;
        int countOneKey = 0;
        int countManual = 0;
        int countPause = 0;
        for (OrderItem orderItem : orderBean.getItems()) {
            errorMessagePrompt.setCurrentEvent(orderItem.getCurrentEvent());
            //处理状态OrderStatus： 0 待处理 1 接单  2拒单 3：一键秒接  4：手动接单 5：手动拒单 6:中场休息秒接 8:暂停接单 9:暂停拒单 10:忽略暂停注单
            if (orderItem.getOrderStatus() == OrderStatusEnum.ORDER_ACCEPT.getCode().intValue()) {
                errorMessagePrompt.setHintMsg("接拒单接单");
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_PASS.getCode());
                errorMessagePrompt.setBetNo(orderItem.getBetNo());
                countSuccess += 1;
            } else if (orderItem.getOrderStatus() == OrderStatusEnum.ORDER_REJECT.getCode().intValue()
                    && orderBean.getVarOrderReject().equals(YesNoEnum.Y.getValue())
                    && "ball_safe".equals(orderItem.getCurrentEvent())) {
                errorMessagePrompt.setCurrentEvent("var_reason");
                errorMessagePrompt.setHintMsg("VAR收单600S超时拒单订单号:" + orderItem.getOrderNo());
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
                errorMessagePrompt.setBetNo(orderItem.getBetNo());
                return OrderStatusEnum.ORDER_REJECT.getCode();
            } else if (orderItem.getOrderStatus() == OrderStatusEnum.ORDER_REJECT.getCode().intValue()) {
                errorMessagePrompt.setHintMsg(orderItem.getCurrentEvent() + "事件拒单订单号:" + orderItem.getOrderNo());
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
                errorMessagePrompt.setBetNo(orderItem.getBetNo());
                return OrderStatusEnum.ORDER_REJECT.getCode();
            } else if (orderItem.getOrderStatus() == NumberConstant.NUM_THREE) {//3 表示一键秒接
                errorMessagePrompt.setHintMsg("一键秒接");
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.ALL_PASS.getCode());
                errorMessagePrompt.setBetNo(orderItem.getBetNo());
                countOneKey += 1;
            } else if (orderItem.getOrderStatus() == NumberConstant.NUM_FOUR) {//4 手动接单
                errorMessagePrompt.setHintMsg("手动接单");
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_HAND_PASS.getCode());
                errorMessagePrompt.setBetNo(orderItem.getBetNo());
                countManual += 1;
            } else if (orderItem.getOrderStatus() == NumberConstant.NUM_FIVES) { //5 手动拒单
                errorMessagePrompt.setHintMsg("手动拒单");
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_HAND_REFUSE.getCode());
                errorMessagePrompt.setBetNo(orderItem.getBetNo());
                return OrderStatusEnum.ORDER_REJECT.getCode();
            } else if (orderItem.getOrderStatus() == NumberConstant.NUM_EIGHT) { //8 暂停接单
                errorMessagePrompt.setCurrentEvent(orderItem.getCurrentEvent());
                errorMessagePrompt.setHintMsg("暂停接单");
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_HAND_PASS.getCode());
                errorMessagePrompt.setBetNo(orderItem.getBetNo());
                countPause += 1;
            } else if (orderItem.getOrderStatus() == NumberConstant.NUM_NINE) { //9 暂停接单
                errorMessagePrompt.setHintMsg("暂停拒单");
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_HAND_REFUSE.getCode());
                errorMessagePrompt.setBetNo(orderItem.getBetNo());
                return OrderStatusEnum.ORDER_REJECT.getCode();
            } else if (orderItem.getOrderStatus() == NumberConstant.NUM_TEN) { //10 忽略暂停注单
                errorMessagePrompt.setHintMsg("忽略暂停注单");
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.PAUSE_ORDER.getCode());
                errorMessagePrompt.setBetNo(orderItem.getBetNo());
                return OrderStatusEnum.ORDER_WAITING.getCode();
            }
        }
        if (countSuccess == orderBean.getItems().size() || countOneKey == orderBean.getItems().size() || countManual == orderBean.getItems().size() || countPause == orderBean.getItems().size()) {
            return OrderStatusEnum.ORDER_ACCEPT.getCode();
        }
        return OrderStatusEnum.ORDER_WAITING.getCode();
    }

    private void errorMessage(StandardMarketMessageDto standardMarketMessage, ErrorMessagePrompt errorMessagePrompt) {
        errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
        if (standardMarketMessage.getThirdMarketSourceStatus() == 1) {
            errorMessagePrompt.setHintMsg("盘口封盘(数据商)拒单");
            errorMessagePrompt.setCurrentEvent("market_status_suspended-DS");
        }
        if (standardMarketMessage.getThirdMarketSourceStatus() == 2) {
            errorMessagePrompt.setHintMsg("盘口关盘(数据商)拒单");
            errorMessagePrompt.setCurrentEvent("market_status_deactivated-DS");
        }
        if (standardMarketMessage.getStatus() == 1 && standardMarketMessage.getThirdMarketSourceStatus() == 0) {
            errorMessagePrompt.setHintMsg("盘口封盘拒单");
            errorMessagePrompt.setCurrentEvent("market_status_suspended-PA");
        }
        if (standardMarketMessage.getStatus() == 2 && standardMarketMessage.getThirdMarketSourceStatus() == 0) {
            errorMessagePrompt.setHintMsg("盘口关盘拒单");
            errorMessagePrompt.setCurrentEvent("market_status_deactivated-PA");
        }
        if (standardMarketMessage.getStatus() == 11 && standardMarketMessage.getThirdMarketSourceStatus() == 0) {
            errorMessagePrompt.setHintMsg("盘口锁盘拒单");
            errorMessagePrompt.setCurrentEvent("market_status_locked-PA");
        }
    }

    /**
     * 处理赔率变化逻辑
     *
     * @param standardMarketMessage 坑位信息
     * @param orderItem             订单具体信息
     * @param oddsScope             配置赔率区间变化
     * @param errorMessagePrompt    返回的消息提示
     * @return 是否拒单
     */
    private boolean handlingOddsChanges(StandardMarketMessageDto standardMarketMessage, OrderItem orderItem, BigDecimal oddsScope, ErrorMessagePrompt errorMessagePrompt) {
        BigDecimal one = new BigDecimal(NumberConstant.NUM_ONE);
        BigDecimal orderOdds = BigDecimal.valueOf(orderItem.getHandleAfterOddsValue1());
        BigDecimal checkOdds = BigDecimal.ZERO;

        for (int j = NumberConstant.NUM_ZERO; j < standardMarketMessage.getMarketOddsList().size(); j++) {
            StandardMarketOddsMessageDto standardMarketOddsMessage = standardMarketMessage.getMarketOddsList().get(j);
            log.info("::{}::简易投注获取盘口投注项赔率::{}", orderItem.getOrderNo(), JSON.toJSONString(standardMarketOddsMessage));
            if (StringUtils.equalsIgnoreCase(String.valueOf(standardMarketOddsMessage.getId()), String.valueOf(orderItem.getPlayOptionsId()))) {
                //默认获取PA赔率
                checkOdds = new BigDecimal(standardMarketOddsMessage.getPaOddsValue()).divide(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED_THOUSAND), NumberConstant.NUM_FOUR, BigDecimal.ROUND_DOWN);
                //如果PA赔率大于数据商下发的赔率就使用数据商的赔率
                if (DISTANCE_PLAY_ID_LIST.contains(orderItem.getPlayId()) && StringUtils.isNotBlank(standardMarketMessage.getAddition5()) && new BigDecimal(standardMarketOddsMessage.getPaOddsValue()).compareTo(new BigDecimal(standardMarketMessage.getAddition5())) >= NumberConstant.NUM_ZERO) {
                    checkOdds = new BigDecimal(standardMarketMessage.getAddition5()).divide(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED_THOUSAND), NumberConstant.NUM_FOUR, BigDecimal.ROUND_DOWN);
                }
            }
        }
        log.info("::{}::简易投注盘口赔率::订单赔率:{},配置值赔率范围:{},checkOdds:{}", orderItem.getOrderNo(), orderOdds, oddsScope, checkOdds);
        if (one.divide(orderOdds, NumberConstant.NUM_FOUR, BigDecimal.ROUND_DOWN).subtract(one.divide(checkOdds, NumberConstant.NUM_FOUR, BigDecimal.ROUND_DOWN)).abs().compareTo(oddsScope) > NumberConstant.NUM_ZERO) {
            log.info("::{}::简易投注盘口赔率有变化拒单::订单赔率:{},配置值赔率范围:{},checkOdds:{}", orderItem.getOrderNo(), orderOdds, oddsScope, checkOdds);
            errorMessagePrompt.setHintMsg("赔率变动幅度过大拒单");
            errorMessagePrompt.setCurrentEvent("order_odds_change");
            errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
            errorMessagePrompt.setBetNo(orderItem.getBetNo());
            return true;
        }
        return false;
    }


    private void sendOrderMsg(OrderBean orderBean, ErrorMessagePrompt errorMessagePrompt) {
        //2576VAR收单 订单修改接单时间
        if (orderBean.getVarOrderReject().equals(YesNoEnum.Y.getValue())) {
            for (OrderItem item : orderBean.getItems()) {
                item.setBetTime(item.getCreateTime());
            }
        }
        //修改订单信息
        sendMessage.sendMessage(MqConstants.RCS_ORDER_UPDATE, "task_accept_order", orderBean.getOrderNo(), orderBean);
        //发送数据到业务
        this.notificationBusiness(orderBean, errorMessagePrompt);

        //2536 注单成功才处理
        if (OrderStatusEnum.ORDER_ACCEPT.getCode().equals(orderBean.getOrderStatus()) && OrderStatusEnum.ORDER_ACCEPT.getCode().equals(orderBean.getValidateResult())) {

            long startTime = System.currentTimeMillis();
            Boolean isSuccess = this.cacheGoalWarnInfo(orderBean);
            //处理成功打印耗时便于日志排查性能
            if (isSuccess) {
                long timeConsuming = System.currentTimeMillis() - startTime;
                log.info("::{}::进球点预警用户投注特征标签添加缓存成功,总耗时{}", orderBean.getOrderNo(), timeConsuming);
            }

        }
    }

    /**
     * 2536 判断注单是否满足要求,存入对应的缓存信息
     * 1.判断用户投注特征标签是否属于“进球点投注”和"UFO-进球点投注"
     * 2.根据赛事id取得联赛信息,球队信息
     * 3.根据联赛id,赛事id,球队id查询设置缓存,缓存没有从数据库取
     * 4.根据设置判断注单金额是否符合要求,记录每个满足条件的用户和时间存入缓存
     */
    public Boolean cacheGoalWarnInfo(OrderBean orderBean) {
        log.info("::{}::进球点预警用户投注特征标签:{}", orderBean.getOrderNo(), orderBean.getUserTagLevel());
        //判断用户投注特征标签是否属于“进球点投注”,"UFO-进球点投注","KY-进球点投注"
        if (!RcsConstant.RCS_REJECT_GOAL_WARN_USER_TAG_LEVELS.contains(orderBean.getUserTagLevel())) {
            return Boolean.FALSE;
        }
        for (OrderItem orderItem : orderBean.getItems()) {
            //根据注单赛事id取得联赛id
            String standardTournamentIdStr = RcsLocalCacheUtils.getValue(String.format(RedisKey.RCS_MATCH_TOURNAMENT_ID, orderItem.getMatchId()), (k) -> {
                LambdaQueryWrapper<StandardMatchInfo> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(StandardMatchInfo::getId, orderItem.getMatchId());
                wrapper.select(StandardMatchInfo::getStandardTournamentId);
                StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectOne(wrapper);
                return Objects.nonNull(standardMatchInfo) ? String.valueOf(standardMatchInfo.getStandardTournamentId()) : "";
            }, RedisKey.CACHE_TIME_OUT);
            log.info("::{}::进球点预警获取缓存赛事联赛ID数据:{}", orderBean.getOrderNo(), standardTournamentIdStr);
            if (StringUtils.isBlank(standardTournamentIdStr)) {
                return Boolean.FALSE;
            }
            Long standardTournamentId = Long.valueOf(standardTournamentIdStr);

            //根据注单赛事id取得球队id信息
            final String regex = ",";
            String teamIdJoin = RcsLocalCacheUtils.getValue(String.format(RedisKey.RCS_MATCH_TEAM_ID, orderItem.getMatchId()), (k) -> {
                LambdaQueryWrapper<StandardMatchTeamRelation> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(StandardMatchTeamRelation::getStandardMatchId, orderItem.getMatchId());
                wrapper.select(StandardMatchTeamRelation::getStandardTeamId);
                List<StandardMatchTeamRelation> list = standardMatchTeamRelationMapper.selectList(wrapper);
                if (CollectionUtils.isNotEmpty(list)) {
                    return list.stream().map(item -> String.valueOf(item.getStandardTeamId())).collect(Collectors.joining(regex));
                }
                return "";
            }, RedisKey.CACHE_TIME_OUT);
            log.info("::{}::进球点预警获取缓存赛事球队数据:{}", orderBean.getOrderNo(), teamIdJoin);
            if (StringUtils.isBlank(teamIdJoin)) {
                return Boolean.FALSE;
            }
            String[] teamIdArr = teamIdJoin.split(regex);
            if (NumberConstant.NUM_TWO != teamIdArr.length) {
                return Boolean.FALSE;
            }
            for (String teamId : teamIdArr) {
                //根据联赛id,赛事id,球队id查询设置缓存,缓存没有从数据库取
                RcsGoalWarnSet rcsGoalWarnSet = templateAcceptConfigServerImpl.getGoalWarnSet(standardTournamentId, orderItem.getMatchId(), teamId);
                log.info("::{}::进球点预警获取预警设置,请求参数:tournamentId={},matchId={},teamId={},返回数据:{}", orderBean.getOrderNo(), standardTournamentId, orderItem.getMatchId(), teamId, JSONObject.toJSONString(rcsGoalWarnSet));
                //根据设置判断注单金额是否符合要求,记录每个满足条件的用户和时间存入缓存
                if (Objects.nonNull(rcsGoalWarnSet) && orderItem.getBetAmount1().compareTo(new BigDecimal(rcsGoalWarnSet.getMaxAmount())) > 0) {
                    String redisKey = String.format(RedisKey.RCS_GOAL_WARN_SET_USER, standardTournamentId, orderItem.getMatchId(), teamId);
                    //同一用户两笔注单时取最近的注单时间缓存
                    String oldBetTime = redisUtils.hget(redisKey, String.valueOf(orderItem.getUid()));
                    String newBetTime = String.valueOf(orderItem.getBetTime());
                    if (StringUtils.isNotBlank(oldBetTime) && Long.valueOf(oldBetTime) > orderItem.getBetTime()) {
                        newBetTime = oldBetTime;
                    }
                    redisUtils.hset(redisKey, String.valueOf(orderItem.getUid()), newBetTime);
                    redisUtils.expire(redisKey, NumberConstant.REDIS_TIM_OUT, TimeUnit.MILLISECONDS);
//                    redisUtils.hset(redisKey, orderItem.getUid() + "_" + rcsGoalWarnSet.getBeforeGoalSeconds(), newBetTime);
//                    //以当前设置进球前秒数为过期时间
//                    redisUtils.expire(redisKey, Long.valueOf(rcsGoalWarnSet.getBeforeGoalSeconds()), TimeUnit.MILLISECONDS);
                }
            }
        }
        return Boolean.TRUE;
    }

    private void sendOrderMsg(PreOrderRequest orderBean) {
        //修改订单信息
        sendMessage.sendMessage(MqConstants.RCS_PRE_SETTLE_UPDATE, MqConstants.RCS_PRE_SETTLE_UPDATE_TAG,
                orderBean.getOrderNo(), orderBean);
        //发送数据到业务
        sendMessage.sendMessage(MqConstants.RCS_PRE_SETTLE_RETURN + "_" + orderBean.getUserGroup(), MqConstants.RCS_PRE_SETTLE_RETURN_TAG,
                orderBean.getOrderNo(), orderBean);
    }

    private void notificationBusiness(OrderBean orderBean, ErrorMessagePrompt errorMessagePrompt) {

        //通知业务处理注单状态
        Map<String, String> oddsRangeMap = new HashMap<>();
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderNo", orderBean.getOrderNo());
        map.put("status", orderBean.getOrderStatus());
        map.put("infoStatus", orderBean.getInfoStatus());
        map.put("infoMsg", orderBean.getReason());
        map.put("betNo", StringUtils.isNotBlank(errorMessagePrompt.getBetNo()) ? errorMessagePrompt.getBetNo() : orderBean.getItems().get(NumberUtils.INTEGER_ZERO).getBetNo());
        if (orderBean.getOrderStatus() == 2) { //拒单处理
            map.put("currentEvent", errorMessagePrompt.getCurrentEvent());
        }
        if (errorMessagePrompt.isMtsInfo()) {
            map.put("mtsIsCache", "3");
        }
        if (isPaInfo(orderBean.getItems())) {
            map.put("mtsIsCache", "4");
        }
        if (OrderTypeEnum.REDCAT.getPlatFrom().equalsIgnoreCase(orderBean.getItems().get(0).getDataSourceCode())) {
            map.put("mtsIsCache", MtsIsCacheEnum.REDCAT.getValue());
        }
        List<Integer> integerList = Arrays.asList(OrderTypeEnum.BTS_PA.getValue(), OrderTypeEnum.CTS_PA.getValue(),
                OrderTypeEnum.ODDIN_PA.getValue(), OrderTypeEnum.GTS_PA.getValue(), OrderTypeEnum.VIRTUAL_PA.getValue());
        // MTS数据源操盘暂时废弃不用考虑
        if (orderBean.getExtendBean() != null && !StringUtils.isEmpty(orderBean.getExtendBean().getRiskChannel()) &&
                integerList.contains(Integer.valueOf(orderBean.getExtendBean().getRiskChannel())) &&
                !CollectionUtils.isEmpty(orderBean.getItems()) &&
                !StringUtils.isEmpty(orderBean.getItems().get(0).getDataSourceCode()) &&
                !StringUtils.isEmpty(orderBean.getItems().get(0).getPlatform())) {
            if (OrderTypeEnum.BTS.getDataSource().equalsIgnoreCase(orderBean.getItems().get(0).getDataSourceCode())) {
                map.put("mtsIsCache", MtsIsCacheEnum.BTS_PA.getValue());
            } else if (OrderTypeEnum.ODDIN.getDataSource().equalsIgnoreCase(orderBean.getItems().get(0).getDataSourceCode())) {
                map.put("mtsIsCache", MtsIsCacheEnum.ODDIN_PA.getValue());
            } else if (OrderTypeEnum.GTS.getPlatFrom().equalsIgnoreCase(orderBean.getItems().get(0).getPlatform())) {
                map.put("mtsIsCache", MtsIsCacheEnum.GTS_PA.getValue());
            } else if (OrderTypeEnum.CTS.getPlatFrom().equalsIgnoreCase(orderBean.getItems().get(0).getPlatform())) {
                map.put("mtsIsCache", MtsIsCacheEnum.CTS_PA.getValue());
            } else if (OrderTypeEnum.VIRTUAL.getPlatFrom().equalsIgnoreCase(orderBean.getItems().get(0).getPlatform())) {
                map.put("mtsIsCache", MtsIsCacheEnum.VIRTUAL_PA.getValue());
            }
        }
        //给订单操盘方塞入特殊事件编码
        if (StringUtils.isNotBlank(orderBean.getCurrentEventCode())) {
            if ("penalty_awarded".equalsIgnoreCase(orderBean.getCurrentEventCode())) {
                map.put("mtsIsCache", "9");
            } else if ("breakaway".equalsIgnoreCase(orderBean.getCurrentEventCode())) {
                map.put("mtsIsCache", "10");
            } else if ("dfk".equalsIgnoreCase(orderBean.getCurrentEventCode())) {
                map.put("mtsIsCache", "11");
            } else if ("danger_ball".equalsIgnoreCase(orderBean.getCurrentEventCode())) {
                map.put("mtsIsCache", "12");
            }
        }
        //2576VAR收单 订单为PA-5
        if (orderBean.getVarOrderReject().equals(YesNoEnum.Y.getValue())) {
            map.put("mtsIsCache", MtsIsCacheEnum.PA_5.getValue());
        }
        if (OrderStatusEnum.ORDER_ACCEPT.getCode().equals(orderBean.getOrderStatus())) {
            map.put("infoCode", 0);
        } else if (OrderStatusEnum.ORDER_REJECT.getCode().equals(orderBean.getOrderStatus())) {
            map.put("infoCode", -1);
        }
        for (OrderItem orderItem : orderBean.getItems()) {
            String oddsRangeRedis = oddsRangeRedisNew(orderItem);
            if (StringUtils.isNotBlank(oddsRangeRedis)) {
                oddsRangeMap.put(String.valueOf(orderItem.getPlayOptionsId()), oddsRangeRedis);
            }
        }
        map.put("oddsRange", oddsRangeMap);
        map.put(orderBean.getOrderNo() + "_error_msg", orderBean.getReason());
        map.put("handleTime", System.currentTimeMillis());
        if (StringUtils.isBlank(orderBean.getOrderGroup())) {
            sendMessage.sendMessage(MqConstants.RCS_BUS_MTS_ORDER_STATUS + "," + orderBean.getOrderNo(), map);
        } else {
            String topic = RedisKey.RCS_BUS_MTS_ORDER_STATUS + "_" + orderBean.getOrderGroup();
            sendMessage.sendMessage(topic, "mtsOrder", orderBean.getOrderNo(), map);
        }
    }


    private boolean isPaInfo(List<OrderItem> orderItemList) {
        //盘口维度
        int count = NumberConstant.NUM_ZERO;
        for (OrderItem orderItem : orderItemList) {
            String matchMarketOddsStr = RcsLocalCacheUtils.getValueInfo(String.format(RedisKey.REDIS_MATCH_MARKET_ODDS_NEW, orderItem.getPlayId(), orderItem.getMatchId()));
            log.info("::{}::,PlayId:{},MatchId:{},缓存获取下发赔率源数据:" + (StringUtils.isBlank(matchMarketOddsStr) ? "null" : "成功"), orderItem.getOrderNo(), orderItem.getPlayId(), orderItem.getMatchId());
            if (StringUtils.isNotBlank(matchMarketOddsStr)) {
                List<StandardMarketMessage> rcsStandardMarketDTOS = JSON.parseArray(matchMarketOddsStr, StandardMarketMessage.class);
                for (StandardMarketMessage standardMarketMessage : rcsStandardMarketDTOS) {
                    if (StringUtils.equals(String.valueOf(standardMarketMessage.getId()), String.valueOf(orderItem.getMarketId()))
                            && Objects.nonNull(standardMarketMessage.getOldThirdMarketSourceStatus()) && (standardMarketMessage.getOldThirdMarketSourceStatus() == NumberConstant.NUM_ONE || standardMarketMessage.getOldThirdMarketSourceStatus() == NumberConstant.NUM_TWO)) {
                        count += NumberConstant.NUM_ONE;
                    }
                }
            }
        }
        return count == orderItemList.size();
    }

    private String oddsRangeRedisNew(OrderItem orderItem) {
        String oddsRange = "";
        try {
            Integer matchType = orderItem.getMatchType() == 2 ? 0 : 1;
            Request<MatchTemplatePlayMarginDataReqVo> requestParam = new Request<>();
            MatchTemplatePlayMarginDataReqVo reqVo = new MatchTemplatePlayMarginDataReqVo();
            reqVo.setSportId(orderItem.getSportId());
            reqVo.setMatchId(orderItem.getMatchId());
            reqVo.setMatchType(matchType);
            reqVo.setPlayId(orderItem.getPlayId());
            requestParam.setData(reqVo);
            Response<MatchTemplatePlayMarginDataResVo> response = this.queryMatchTemplatePlayMargin(requestParam, orderItem.getOrderNo());
            if (null == response || null == response.getData()) {
                log.info("::{}::数据库或缓存获取接距赔率变动配置信息为null,SportId:{},MatchType:{},playId:{},", orderItem.getOrderNo(), orderItem.getSportId(), orderItem.getMatchType(), orderItem.getPlayId());
                return oddsRange;
            }
            MatchTemplatePlayMarginDataResVo vo = response.getData();
            if (Objects.nonNull(vo.getOddsChangeStatus()) && vo.getOddsChangeStatus() == NumberConstant.NUM_ONE) {
                return String.valueOf(vo.getOddsChangeValue());
            }
        } catch (Exception e) {
            log.error("::{}::获取到接距赔率变动配置信息SportId:{},MatchType:{},playId:{},报错：{}", orderItem.getOrderNo(), orderItem.getSportId(), orderItem.getMatchType(), orderItem.getPlayId(), e.getMessage(), e);
        }
        return oddsRange;
    }

    @Autowired
    private RcsTournamentTemplatePlayMargainMapper playMargainMapper;

    public Response<MatchTemplatePlayMarginDataResVo> queryMatchTemplatePlayMargin(Request<MatchTemplatePlayMarginDataReqVo> requestParam, String orderNo) {
        try {
            MatchTemplatePlayMarginDataResVo resVo = new MatchTemplatePlayMarginDataResVo();
            String timeCacheKey = String.format("rcs_match_template_play_margin_data:%s:%s:%s", requestParam.getData().getMatchId(),
                    requestParam.getData().getMatchType(), requestParam.getData().getPlayId());
            //获取对应缓存数据
            Object resVoCache = RcsLocalCacheUtils.timedCache.get(timeCacheKey);
            if (!ObjectUtils.isEmpty(resVoCache)) {
                resVo = JSONObject.parseObject(JSONObject.toJSONString(resVoCache), MatchTemplatePlayMarginDataResVo.class);
                log.info("::{}::缓存获取到玩法配置：成功", orderNo);
                return Response.success(resVo);
            }
            //根据入参查出对应数据
            RcsMatchMarketConfig rcsMatchMarketConfig = new RcsMatchMarketConfig();
            rcsMatchMarketConfig.setMatchId(requestParam.getData().getMatchId());
            rcsMatchMarketConfig.setPlayId(Long.valueOf(requestParam.getData().getPlayId()));
            rcsMatchMarketConfig.setMatchType(requestParam.getData().getMatchType());
            RcsTournamentTemplatePlayMargain playMargin = playMargainMapper.selectPlayMarginByMatchInfoReject(rcsMatchMarketConfig);
            if (!ObjectUtils.isEmpty(playMargin)) {
                //若查询结果不为空，则进行构造出参
                resVo = BeanCopyUtils.copyProperties(playMargin, MatchTemplatePlayMarginDataResVo.class);
                resVo.setMatchId(requestParam.getData().getMatchId());
                resVo.setMatchType(requestParam.getData().getMatchType());
                resVo.setSportId(requestParam.getData().getSportId());
            } else {
                log.warn("::{}::数据库未查询到相关玩法配置，matchId={},playId={},matchType={}", orderNo, requestParam.getData().getMatchId(), requestParam.getData().getPlayId(), requestParam.getData().getMatchType());
                return Response.error(500, "未查询到对应数据");
            }
            log.info("::{}::数据库获取到玩法配置:{}", orderNo, JsonFormatUtils.toJson(resVo));
            //同步
            commonSendMsgServerImpl.sendMsg(timeCacheKey, resVo);

            return Response.success(resVo);
        } catch (Exception e) {
            log.warn("::{}::获取相关玩法配置异常，param={}", orderNo, JSONObject.toJSONString(requestParam), e);
            return Response.error(500, "未查询到对应数据");
        }
    }


    private void setUpHandStatus(OrderBean orderBean, int state) {
        //统一处理成风控拒单处理中
        long currentTimeMillis = System.currentTimeMillis();
        orderBean.getItems().forEach(s -> {
            s.setHandleStatus(NumberUtils.INTEGER_ONE);
            s.setValidateResult(state);
            s.setModifyTime(currentTimeMillis);
        });

    }

    private boolean processWaitingTime(OrderBean orderBean) {
        for (OrderItem orderItem : orderBean.getItems()) {
            if (!isTimeIn(orderItem)) {
                return false;
            }
        }
        return true;
    }

    private boolean processWaitingTime(PreOrderRequest orderBean) {
        return isTimeIn(orderBean);
    }

    /**
     * 查询 MTS-1配置
     *
     * @param orderItem 订单对象
     * @return MTS-1配置
     */
    public MtsTemplateConfigVo queryMtsConfig(OrderItem orderItem) {
        Integer matchType = orderItem.getMatchType() == 2 ? 0 : 1;
        Request<MatchTemplateDataReqVo> requestParam = new Request<>();
        MatchTemplateDataReqVo matchTemplateDataReqVo = new MatchTemplateDataReqVo();
        matchTemplateDataReqVo.setSportId(orderItem.getSportId());
        matchTemplateDataReqVo.setMatchId(orderItem.getMatchId());
        matchTemplateDataReqVo.setMatchType(matchType);
        requestParam.setData(matchTemplateDataReqVo);
        Response<MatchTemplateDataResVo> resVoResponse = tournamentTemplateByMatchService.queryMatchTemplateData(requestParam);
        if (Objects.isNull(resVoResponse) || resVoResponse.getData() == null) {
            log.info("::{}::没有找到简易足球投注模板配置", orderItem.getOrderNo());
            return null;
        }
        String config = resVoResponse.getData().getMtsConfigValue();
        log.info("::{}::RPC获取简易足球投注模板配置:{}" + (StringUtils.isBlank(config) ? "null" : "成功"), orderItem.getOrderNo(), JSON.toJSONString(config));
        return JSON.parseObject(config, MtsTemplateConfigVo.class);
    }

    /**
     * 判断接单时间是否到达
     *
     * @param orderItem 订单对象
     * @return 是或否
     */
    private boolean isTimeIn(OrderItem orderItem) {
        if (orderItem.getMatchType() != 2) {
            return true;
        }
        Long curTime = System.currentTimeMillis();
        Long minTime = orderItem.getBetTime() + orderItem.getMinWait() * 1000;
        log.info("::{}::赛事ID:{},当前时间:{},最小等待秒数:{},最小等待时间:{},最大等待时间:{}::", orderItem.getOrderNo(), orderItem.getMatchId(), timeStamp2Date(String.valueOf(curTime)), orderItem.getMinWait(), timeStamp2Date(String.valueOf(minTime)), timeStamp2Date(String.valueOf(orderItem.getMaxAcceptTime())));
        return curTime > minTime && curTime > orderItem.getMaxAcceptTime();
    }

    /**
     * 判断接单时间是否到达
     *
     * @param orderItem 订单对象
     * @return 是或否
     */
    private boolean isTimeIn(PreOrderRequest orderItem) {
        if (orderItem.getDetailList().get(0).getMatchType() != 2) {
            return true;
        }
        Long curTime = System.currentTimeMillis();
        Long minTime = orderItem.getReqTime() + orderItem.getDetailList().get(0).getMinWait() * 1000;
        log.info("::{}::提前結算赛事ID:{},当前时间:{},最小等待秒数:{},最小等待时间:{},最大等待时间:{}::", orderItem.getOrderNo(), orderItem.getDetailList().get(0).getMatchId(), timeStamp2Date(String.valueOf(curTime)), orderItem.getDetailList().get(0).getMinWait(), timeStamp2Date(String.valueOf(minTime)), timeStamp2Date(String.valueOf(orderItem.getDetailList().get(0).getMaxAcceptTime())));
        return curTime > minTime && curTime > orderItem.getDetailList().get(0).getMaxAcceptTime();
    }

    /**
     * 初始化订单信息
     *
     * @param orderBean 订单对象
     * @param orderItem 投注项对象
     * @param config    接距单配置
     */
    private void initWaitTime(OrderBean orderBean, OrderItem orderItem, RcsTournamentTemplateAcceptConfig config) {
        //拒单处理
        if (MatchEventConfigEnum.EVENT_REJECT.getCode().equalsIgnoreCase(config.getEventType())) {
            orderItem.setOrderStatus(MatchEventConfigEnum.ORDER_REJECT_ORDER_STATUS.getValue());
        } else if (MatchEventConfigEnum.EVENT_SAFETY.getCode().equalsIgnoreCase(config.getEventType())) {
            // 特殊会员需要延迟等待时间
            log.info("::{}::安全事件需要加上用户的延迟配置", orderBean.getOrderNo());
            RcsUserConfig userConfig = addExtraDelayTime(orderBean, orderItem);
            config.setMaxWaitTime(config.getMaxWaitTime() + userConfig.getBetExtraDelay());
            config.setMinWaitTime(config.getMinWaitTime() + userConfig.getBetExtraDelay());
        }
        Integer maxWaitTime = config.getMaxWaitTime();
        Integer minWaitTime = config.getMinWaitTime();
        orderItem.setCurrentEventType(config.getEventTypeNumber());
        orderItem.setCurrentEvent(config.getEventCode());
        orderItem.setMaxAcceptTime(orderItem.getBetTime() + maxWaitTime.longValue() * MatchEventConfigEnum.ORDER_SECOND_UNIT.getValue());
        orderItem.setMinWait(minWaitTime);
    }

    /**
     * 初始化订单信息
     *
     * @param orderBean 订单对象
     * @param orderItem 投注项对象
     * @param config    接距单配置
     */
    private void initSettleWaitTime(PreOrderRequest orderBean, PreOrderDetailRequest orderItem, RcsTournamentTemplateAcceptConfig config) {
        //拒单处理
        if (MatchEventConfigEnum.EVENT_REJECT.getCode().equalsIgnoreCase(config.getEventType())) {
            orderBean.setOrderStatus(MatchEventConfigEnum.ORDER_REJECT_ORDER_STATUS.getValue());
            orderBean.setInfoStatus(PreSettleInfoStatusEnum.REJECT_REFUSE.getCode());
            orderBean.setReason(PreSettleInfoStatusEnum.REJECT_REFUSE.getMode());
        } else if (MatchEventConfigEnum.EVENT_CLOSING.getCode().equalsIgnoreCase(config.getEventType())) {
            orderBean.setInfoStatus(PreSettleInfoStatusEnum.CLOSE_REFUSE.getCode());
            orderBean.setReason(PreSettleInfoStatusEnum.CLOSE_REFUSE.getMode());
        } else if (MatchEventConfigEnum.EVENT_SAFETY.getCode().equalsIgnoreCase(config.getEventType())) {
            orderBean.setInfoStatus(PreSettleInfoStatusEnum.SAFE_PASS.getCode());
            orderBean.setReason(PreSettleInfoStatusEnum.SAFE_PASS.getMode());
        } else if (MatchEventConfigEnum.EVENT_DANGER.getCode().equalsIgnoreCase(config.getEventType())) {
            orderBean.setInfoStatus(PreSettleInfoStatusEnum.DANGER_PASS.getCode());
            orderBean.setReason(PreSettleInfoStatusEnum.DANGER_PASS.getMode());
        }
        Integer maxWaitTime = config.getMaxWaitTime();
        Integer minWaitTime = config.getMinWaitTime();
        orderItem.setCurrentEventType(config.getEventTypeNumber());
        orderItem.setCurrentEvent(config.getEventCode());
        orderItem.setMaxAcceptTime(orderBean.getReqTime() + maxWaitTime.longValue() * MatchEventConfigEnum.ORDER_SECOND_UNIT.getValue());
        orderItem.setMinWait(minWaitTime);

    }

    /**
     * 其他赛事初始化等待时间
     *
     * @param orderBean 订单对象
     * @param orderItem 投注项对象
     */
    private void initWaitTime(OrderBean orderBean, OrderItem orderItem) {
        Integer sportId = orderItem.getSportId();
        RcsUserConfig userConfig = addExtraDelayTime(orderBean, orderItem);
        Integer userTime = userConfig.getBetExtraDelay();
        log.info("::{}::获取到用户设定延时配置秒数={}", orderBean.getOrderNo(), userTime);
        int waitTime;
        if (SportIdEnum.isBasketball(sportId) || SportIdEnum.isTennis(sportId)
                || SportIdEnum.isPingPong(sportId) || SportIdEnum.isVolleyBall(sportId)
                || SportIdEnum.isSnooker(sportId) || SportIdEnum.isBaseBall(sportId)
                || SportIdEnum.isBadminton(sportId) || SportIdEnum.isIceHockey(sportId)
                || SportIdEnum.isAmericanFootball(sportId)) {
            // 篮球等待时间
            int queryTime = queryWaitTime(orderItem) + userTime;
            waitTime = Math.min(queryTime, 14);
        } else {
            int queryTime = getOtherBallWaitTime(orderItem, RedisKey.BASKET_BALL_DEFAULT_WAIT_TIME) + userTime;
            waitTime = Math.min(queryTime, 14);
        }
        orderItem.setMaxAcceptTime(orderItem.getBetTime() + (long) waitTime * MatchEventConfigEnum.ORDER_SECOND_UNIT.getValue());
        orderItem.setMinWait(waitTime);
    }

    /**
     * 没有操盘赛种设置默认时间
     *
     * @param orderItem 投注项对象
     * @param waitTime  等待时间
     * @return 等待时间
     */
    private Integer getOtherBallWaitTime(OrderItem orderItem, Integer waitTime) {
        //获取联赛设置默认等待时间
        Integer defaultTime = templateRefMapper.queryDefaultTime(orderItem.getMatchId());
        if (defaultTime != null) {
            waitTime = defaultTime;
            log.info("::{}::订单,联赛默认等待时间={}", orderItem.getOrderNo(), defaultTime);
        }
        return waitTime;
    }

    /**
     * 查询等待时间
     *
     * @param orderItem 投注项对象
     * @return 等待时间
     */
    private Integer queryWaitTime(OrderItem orderItem) {
        Long sportId = orderItem.getSportId().longValue();
        Integer waitSeconds = RedisKey.BASKET_BALL_DEFAULT_WAIT_TIME;
        // 暂停设置暂停margin
        RcsMatchMarketConfig config = null;
        if (SportIdEnum.isBasketball(sportId) &&
                (!(RcsConstant.BASKETBALL_X_EU_PLAYS.contains(orderItem.getPlayId()) || RcsConstant.BASKETBALL_X_MY_PLAYS.contains(orderItem.getPlayId())))) {
            config = rcsMatchMarketConfigMapper.queryMarketConfigByIndex(orderItem);
        } else if (SportIdEnum.isTennis(sportId) || SportIdEnum.isPingPong(sportId)
                || SportIdEnum.isVolleyBall(sportId) || SportIdEnum.isSnooker(sportId) || SportIdEnum.isBaseBall(sportId)
                || SportIdEnum.isBadminton(sportId) || SportIdEnum.isIceHockey(sportId.intValue())) {
            config = rcsMatchMarketConfigMapper.queryMarketConfig(orderItem);
        } else if (SportIdEnum.isBasketball(sportId) &&
                (RcsConstant.BASKETBALL_X_EU_PLAYS.contains(orderItem.getPlayId()) || RcsConstant.BASKETBALL_X_MY_PLAYS.contains(orderItem.getPlayId()))) {
            config = rcsMatchMarketConfigMapper.queryMarketConfig(orderItem);
        }
        log.info("::{}::获取非足球赛事盘口配置信息：{}", orderItem.getOrderNo(), JSONObject.toJSON(config));
        if (!ObjectUtils.isEmpty(config)) {
            waitSeconds = config.getWaitSeconds();
        }

        if (SportIdEnum.isSnooker(sportId)) {
            String key = String.format(RedisKey.REDIS_MATCH_PERIOD, orderItem.getMatchId());
            String matchPeriod = RcsLocalCacheUtils.getValueInfo(key);
            if (StringUtils.isBlank(matchPeriod)) {
                matchPeriod = rcsMarketCategorySetRelationMapper.queryMatchPeriodInfo(orderItem.getMatchId());
            }
            if (Objects.nonNull(matchPeriod) && "445".equals(matchPeriod) && Objects.nonNull(config)) {
                waitSeconds = config.getTimeOutWaitSeconds();
            }
        }
        if (ObjectUtils.isEmpty(waitSeconds)) {
            waitSeconds = RedisKey.BASKET_BALL_DEFAULT_WAIT_TIME;
        }
        return waitSeconds;
    }


    /**
     * 查询会员接单的延迟时间
     *
     * @param orderBean 订单对象
     * @param orderItem 投注项对象
     * @return 延迟配置
     */
    private RcsUserConfig addExtraDelayTime(OrderBean orderBean, OrderItem orderItem) {
        Long uid = orderItem.getUid();
        String userConfigKey = String.format(RedisKey.SPECIAL_USER_CONFIG, uid);
        String redisStr = redisUtils.hget(userConfigKey, String.valueOf(orderItem.getSportId()));
        RcsUserConfig rcsUserConfig = new RcsUserConfig();
        if (StringUtils.isNotBlank(redisStr)) {
            rcsUserConfig = JSON.parseObject(redisStr, RcsUserConfig.class);
            log.info("::{}::redis缓存查询特殊用户延迟接单成功", orderBean.getOrderNo());
        } else {
            List<RcsUserConfig> rcsUserConfigs = rcsUserConfigNewService.getByUserId(uid);
            if (CollectionUtils.isNotEmpty(rcsUserConfigs)) {
                Map<String, RcsUserConfig> map = rcsUserConfigs.stream().collect(Collectors.toMap(e -> String.valueOf(e.getSportId()), e -> e));
                for (Map.Entry<String, RcsUserConfig> entry : map.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase(orderItem.getSportId().toString())) {
                        rcsUserConfig = entry.getValue();
                        log.info("::{}::数据库查询特殊用户延迟接单成功", orderBean.getOrderNo());
                    }
                    redisUtils.hset(userConfigKey, String.valueOf(entry.getKey()), JSON.toJSONString(entry.getValue()));
                    redisUtils.expire(userConfigKey, NumberConstant.LONG_THIRTY, TimeUnit.DAYS);
                }
            }
        }
        if (ObjectUtils.isEmpty(rcsUserConfig.getBetExtraDelay()) || rcsUserConfig.getBetExtraDelay() == NumberConstant.NUM_ZERO) {
            //标签延时
            rcsUserConfig.setBetExtraDelay(addLabelDelayTime(orderItem, orderBean.getUserTagLevel()));
        }
        if (ObjectUtils.isEmpty(rcsUserConfig.getBetExtraDelay()) || rcsUserConfig.getBetExtraDelay() == NumberConstant.NUM_ZERO) {
            //商户配置延时
            rcsUserConfig.setBetExtraDelay(rcsQuotaBusinessConfigDelayTime(orderBean.getTenantId(), orderItem));
            if (rcsUserConfig.getBetExtraDelay() != NumberConstant.NUM_ZERO) {
                log.info("::{}::商户配置特殊会员缓存有延迟接单={}", orderItem.getOrderNo(), JSON.toJSONString(rcsUserConfig));
            }
        }
        return rcsUserConfig;
    }


    //标签延时
    private Integer addLabelDelayTime(OrderItem orderItem, int userTagLevel) {
        String redisStr = RcsLocalCacheUtils.getValueInfo(String.format(RedisKey.LABEL_USER_CONFIG, userTagLevel));
        RcsLabelLimitConfig labelLimitConfig = new RcsLabelLimitConfig();
        if (StringUtils.isNotBlank(redisStr)) {
            List<RcsLabelLimitConfig> labelLimitConfigs = JSON.parseArray(redisStr, RcsLabelLimitConfig.class);
            labelLimitConfig = this.getRcsLabelLimitConfig(orderItem.getSportId(), labelLimitConfigs);
            log.info("::{}::缓存查询标签延迟接单成功", orderItem.getOrderNo());
        } else {
            List<RcsLabelLimitConfig> labelLimitConfigs = labelLimitConfigMapper.userLevelDelay(userTagLevel);
            if (CollectionUtils.isNotEmpty(labelLimitConfigs)) {
                String redisKey = String.format(RedisKey.LABEL_USER_CONFIG, userTagLevel);
                commonSendMsgServerImpl.sendMsg(redisKey, labelLimitConfigs);
                labelLimitConfig = this.getRcsLabelLimitConfig(orderItem.getSportId(), labelLimitConfigs);
                log.info("::{}::数据库查询标签延迟接单成功", orderItem.getOrderNo());
            }
        }
        Integer delayTime = ObjectUtils.isEmpty(labelLimitConfig.getBetExtraDelay()) ? NumberConstant.NUM_ZERO : labelLimitConfig.getBetExtraDelay();
        if (delayTime != NumberConstant.NUM_ZERO) {
            log.info("::{}::标签缓存有延迟接单={}", orderItem.getOrderNo(), JSON.toJSONString(labelLimitConfig));
        }
        return delayTime;
    }

    private RcsLabelLimitConfig getRcsLabelLimitConfig(Integer sportId, List<RcsLabelLimitConfig> labelLimitConfigs) {
        if (CollectionUtils.isNotEmpty(labelLimitConfigs)) {
            Map<String, RcsLabelLimitConfig> map = labelLimitConfigs.stream().collect(Collectors.toMap(e -> String.valueOf(e.getSportId()), e -> e));
            for (Map.Entry<String, RcsLabelLimitConfig> entry : map.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(sportId.toString())) {
                    return entry.getValue();
                }
            }
        }
        return new RcsLabelLimitConfig();
    }

    /**
     * 商户配置延时
     *
     * @param tenantId 商户id
     * @return 商户延迟时间
     */
    private Integer rcsQuotaBusinessConfigDelayTime(Long tenantId, OrderItem orderItem) {
        //根据用户商户 获取对应的商户信息
        Response<RcsQuotaBusinessLimitResVo> resVoResponse = limitApiService.getRcsQuotaBusinessLimit(String.valueOf(tenantId));
        if (null != resVoResponse && null != resVoResponse.getData()) {
            RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo = resVoResponse.getData();
            if (StringUtils.isNotBlank(rcsQuotaBusinessLimitResVo.getSportIds())) {
                List<String> sportIds = Arrays.asList(rcsQuotaBusinessLimitResVo.getSportIds().split(","));
                if (sportIds.contains(String.valueOf(orderItem.getSportId())) && !Objects.isNull(rcsQuotaBusinessLimitResVo.getDelay()) && rcsQuotaBusinessLimitResVo.getDelay() > NumberConstant.NUM_ZERO) {
                    log.info("::{}::数据库查询商户配置延时返回成功", orderItem.getOrderNo());
                    return rcsQuotaBusinessLimitResVo.getDelay();
                }
            }
        }
        return NumberConstant.NUM_ZERO;
    }

    /**
     * bug-41130 G01/K01数据源 Goal事件特殊接拒优化
     * bug-44126 R01进球特殊事件,只做进球类玩法集 增加RO1数据源
     */
    private boolean isBGKOEventCode(OrderBean orderBean, OrderItem orderItem, RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig, List<String> G01_K01_R01_EventCode) {
        //G01/K01/R01新的指令为Goal Confirmed 或 安全事件则接，并且把注单等待的状态解除
//        log.info("::{}::参数orderItem：{}，rcsTournamentTemplateAcceptConfig：{}，G01_K01_R01_EventCode：{}", orderBean.getOrderNo(), orderItem,
//                rcsTournamentTemplateAcceptConfig, G01_K01_R01_EventCode);

        if (G01_K01_R01_EventCode.contains(rcsTournamentTemplateAcceptConfig.getEventCode())) {
            String playSetId = templateAcceptConfigServerImpl.getPlayCollect(orderItem);
            MatchEventInfo lastMatchInfo = templateAcceptConfigServerImpl.getMatchEventInfo(RedisKey.MATCH_LAST_TIME_EVENT_CODE, playSetId, orderItem);
            String lastEventCode = Objects.nonNull(lastMatchInfo) ? lastMatchInfo.getEventCode() : "";
            MatchEventInfo currentMatchInfo = templateAcceptConfigServerImpl.getMatchEventInfo(RedisKey.REDIS_EVENT_INFO, playSetId, orderItem);
            String currentEventCode = Objects.nonNull(currentMatchInfo) ? currentMatchInfo.getEventCode() : "";
            log.info("::{}::{}数据源上一次的事件为：'{}';当前事件为：'{}'", orderBean.getOrderNo(), rcsTournamentTemplateAcceptConfig.getDataSource(),
                    lastEventCode, currentEventCode);
            if (lastEventCode.equalsIgnoreCase("goal")) {
                if (Arrays.asList("goal_confirm").contains(currentEventCode)) {
                    orderItem.setOrderStatus(1);
                    orderItem.setMaxAcceptTime(System.currentTimeMillis());
                    log.info("::{}::{}数据源新的事件为：'{}'秒接:{}", orderBean.getOrderNo(), rcsTournamentTemplateAcceptConfig.getDataSource(), currentEventCode, rcsTournamentTemplateAcceptConfig);
                    return true;
                } else if (Arrays.asList("canceled_goal", "goal").contains(currentEventCode)) {
                    orderItem.setOrderStatus(2);
                    orderItem.setCurrentEvent(rcsTournamentTemplateAcceptConfig.getEventCode());
                    orderItem.setCurrentEventType(rcsTournamentTemplateAcceptConfig.getEventTypeNumber());
                    log.info("::{}::{}数据源新的事件为:'{}'拒单:{}", orderBean.getOrderNo(), rcsTournamentTemplateAcceptConfig.getDataSource(), currentEventCode, rcsTournamentTemplateAcceptConfig);
                    return true;
                }
            } else {
                if (Arrays.asList("goal").contains(currentEventCode)) {
                    orderItem.setMaxAcceptTime(orderItem.getBetTime() + rcsTournamentTemplateAcceptConfig.getMaxWaitTime() * 1000);
                    orderItem.setCurrentEvent(rcsTournamentTemplateAcceptConfig.getEventCode());
                    orderItem.setCurrentEventType(rcsTournamentTemplateAcceptConfig.getEventTypeNumber());
                    log.info("::{}::{}数据源新的事件为:'{}',修改接单为时间:{},事件信息:{}", orderBean.getOrderNo(), rcsTournamentTemplateAcceptConfig.getDataSource(), rcsTournamentTemplateAcceptConfig.getEventCode(), timeStamp2Date(String.valueOf(orderItem.getMaxAcceptTime())), rcsTournamentTemplateAcceptConfig);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 处理当前订单的事件
     */
    private void updateOrderBeanInfo(OrderBean orderBean, OrderItem orderItem, RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig) {
        log.info("::{}::简易投注获取到事件配置信息:{}", orderItem.getOrderNo(), JSON.toJSONString(rcsTournamentTemplateAcceptConfig));
        //如果接单事件是null就去初始化一遍
        if (orderItem.getMaxAcceptTime() == null) {
            this.initWaitTime(orderBean, orderItem, rcsTournamentTemplateAcceptConfig);
            //设置最新的事件ID
            templateAcceptConfigServerImpl.setEventId(orderItem);
        }

        //当注单的eventId能够匹配上接拒事件参数
        log.info("::{}::当前orderItem.getEventId:{}，rcsTournamentTemplateAcceptConfig.getId：{}", orderItem.getOrderNo(),
                JSON.toJSONString(orderItem.getEventId()), JSON.toJSONString(rcsTournamentTemplateAcceptConfig.getId()));
        if (orderItem.getEventId() != null && rcsTournamentTemplateAcceptConfig.getId() != null) {

            //bug41130 G01/K01事件源Goal事件特殊接拒优化 特殊事件需要限制进球类玩法集
            //bug44126 R01进球特殊事件,只做进球类玩法集 增加RO1数据源
            String categorySetCode = templateAcceptConfigServerImpl.getCategoryPlaySetCodeById(orderItem.getOrderNo(), rcsTournamentTemplateAcceptConfig);
            boolean isFootballGoal = CategorySetCodeEnum.FOOTBALL_GOAL.name().equalsIgnoreCase(categorySetCode);
            List<String> G01_K01_R01_DataSource = Arrays.asList("BG", "KO", "RB");
            if (isFootballGoal && G01_K01_R01_DataSource.contains(rcsTournamentTemplateAcceptConfig.getDataSource()) && !orderItem.getEventId().equals(rcsTournamentTemplateAcceptConfig.getId())) {
                List<String> G01_K01_R01_SafeEventCode = Arrays.asList("goal_confirm");
                List<String> G01_K01_R01_RejEventCode = Arrays.asList("canceled_goal", "goal");
                if (isBGKOEventCode(orderBean, orderItem, rcsTournamentTemplateAcceptConfig, G01_K01_R01_SafeEventCode) || isBGKOEventCode(orderBean, orderItem, rcsTournamentTemplateAcceptConfig, G01_K01_R01_RejEventCode)) {
                    return;
                }
            }
            //当前为特殊事件
            if (SPECEVENT.contains(rcsTournamentTemplateAcceptConfig.getEventCode())) {
                String specKey = String.format(RedisKey.RCS_SPECIAL_EVENT_INFO, orderItem.getMatchId(), rcsTournamentTemplateAcceptConfig.getEventCode());
                String specVal = RcsLocalCacheUtils.getValueInfo(specKey);
                log.info("::{}::特殊事件缓存信息：key:{},value：{}", orderItem.getOrderNo(), specKey, specVal);
                if (StringUtils.isNotBlank(specVal) && specVal.equalsIgnoreCase("1")) {
                    //当前特殊事件两个开关都为开启状态 秒接
                    orderBean.setCurrentEventCode(rcsTournamentTemplateAcceptConfig.getEventCode());
                    orderItem.setOrderStatus(1);
                    orderItem.setMaxAcceptTime(System.currentTimeMillis());
                    log.info("::{}::特殊事件注单，特殊事件开关开启--秒接", orderItem.getOrderNo());
                    return;
                }
            }
            //当前为安全事件、注单事件为危险或者封盘、注单事件ID不等于当前事件ID  ——直接接单 事件类型 0-安全;1-危险;2-封盘;3拒单
            if (rcsTournamentTemplateAcceptConfig.getEventTypeNumber() == MatchEventConfigEnum.EVENT_SAFETY.getType().intValue() && Arrays.asList(1, 2).contains(orderItem.getCurrentEventType()) && !orderItem.getEventId().equals(rcsTournamentTemplateAcceptConfig.getId())) {
                orderItem.setOrderStatus(1);
                orderItem.setMaxAcceptTime(System.currentTimeMillis());
                log.info("::{}::当前为安全事件、注单事件为危险或者封盘、注单事件ID不等于当前事件ID::{}", orderBean.getOrderNo(), rcsTournamentTemplateAcceptConfig);
                return;
            }

            //当前为拒单事件 --直接拒单
            if (rcsTournamentTemplateAcceptConfig.getEventTypeNumber() == NumberConstant.NUM_THREE && !orderItem.getEventId().equals(rcsTournamentTemplateAcceptConfig.getId())) {
                orderItem.setOrderStatus(2);
                orderItem.setCurrentEvent(rcsTournamentTemplateAcceptConfig.getEventCode());
                orderItem.setCurrentEventType(rcsTournamentTemplateAcceptConfig.getEventTypeNumber());
                log.info("::{}::当前为拒单事件 --直接拒单::{}", orderBean.getOrderNo(), rcsTournamentTemplateAcceptConfig);
                return;
            }
            //当前为安全事件、注单事件ID不等于当前事件ID、当前时间大于注单时间+最小等待时间、注单事件类型为安全事件  ——接单
            if (rcsTournamentTemplateAcceptConfig.getEventTypeNumber() == MatchEventConfigEnum.EVENT_SAFETY.getType().intValue() && !orderItem.getEventId().equals(rcsTournamentTemplateAcceptConfig.getId()) && System.currentTimeMillis() > (orderItem.getBetTime() + orderItem.getMinWait() * 1000) && orderItem.getCurrentEventType() == MatchEventConfigEnum.EVENT_SAFETY.getType().intValue()) {
                orderItem.setOrderStatus(1);
                log.info("::{}::当前为安全事件、注单事件ID不等于当前事件ID、当前时间大于注单时间+最小等待时间、注单事件类型为安全事件  ——接单::{}", orderBean.getOrderNo(), rcsTournamentTemplateAcceptConfig);
                return;
            }
            //注单事件为安全、当前为危险事件或者封盘事件、注单事件ID不等于当前事件ID、走TMax
            if ((rcsTournamentTemplateAcceptConfig.getEventTypeNumber() == MatchEventConfigEnum.EVENT_DANGER.getType().intValue()
                    || rcsTournamentTemplateAcceptConfig.getEventTypeNumber() == MatchEventConfigEnum.EVENT_CLOSING.getType().intValue())
                    && !orderItem.getEventId().equals(rcsTournamentTemplateAcceptConfig.getId())
                    && orderItem.getCurrentEventType() == MatchEventConfigEnum.EVENT_SAFETY.getType().intValue()) {
                orderItem.setMaxAcceptTime(orderItem.getBetTime() + rcsTournamentTemplateAcceptConfig.getMaxWaitTime() * 1000);
                orderItem.setCurrentEvent(rcsTournamentTemplateAcceptConfig.getEventCode());
                orderItem.setCurrentEventType(rcsTournamentTemplateAcceptConfig.getEventTypeNumber());
                log.info("::{}::注单为安全事件，当前为危险或封盘事件，更改Tmin或Tmax,，更改注单事件状态::{}", orderBean.getOrderNo(), rcsTournamentTemplateAcceptConfig);
                return;
            }
            //注单为危险事件，当前为封盘事件，改为Tmax，更改注单事件状态
            if (orderItem.getCurrentEventType() == MatchEventConfigEnum.EVENT_DANGER.getType().intValue() && !orderItem.getEventId().equals(rcsTournamentTemplateAcceptConfig.getId()) && rcsTournamentTemplateAcceptConfig.getEventTypeNumber() == MatchEventConfigEnum.EVENT_CLOSING.getType().intValue()) {
                orderItem.setMaxAcceptTime(System.currentTimeMillis() + rcsTournamentTemplateAcceptConfig.getMaxWaitTime() * 1000);
                orderItem.setCurrentEvent(rcsTournamentTemplateAcceptConfig.getEventCode());
                orderItem.setCurrentEventType(rcsTournamentTemplateAcceptConfig.getEventTypeNumber());
                log.info("::{}::注单为危险事件，当前为封盘事件，改为Tmax，更改注单事件状态::{}", orderBean.getOrderNo(), rcsTournamentTemplateAcceptConfig);
            }
        }
    }

    public void setItemCurrentEvent(Integer orderStatus, PreSettleInfoStatusEnum preSettleInfoStatusEnum, Long maxAcceptTime,
                                    PreOrderRequest orderBean,
                                    PreOrderDetailRequest orderItem,
                                    RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig) {
        if (orderStatus != null) {
            orderBean.setOrderStatus(orderStatus);
        }
        if (preSettleInfoStatusEnum != null) {
            orderBean.setInfoStatus(preSettleInfoStatusEnum.getCode());
            orderBean.setReason(preSettleInfoStatusEnum.getMode());
        }
        orderItem.setMaxAcceptTime(maxAcceptTime);
        orderItem.setCurrentEvent(rcsTournamentTemplateAcceptConfig.getEventCode());
        orderItem.setCurrentEventType(rcsTournamentTemplateAcceptConfig.getEventTypeNumber());
        orderItem.setCurrentEventTime(rcsTournamentTemplateAcceptConfig.getEventTime());
        orderItem.setMaxWait(rcsTournamentTemplateAcceptConfig.getMaxWaitTime());
        orderItem.setMinWait(rcsTournamentTemplateAcceptConfig.getMinWaitTime());
        orderItem.setEventAxis((orderItem.getEventAxis() == null ? "" : orderItem.getEventAxis() + ";") +
                "currentTime:" + System.currentTimeMillis() +
                ",eventType:" + rcsTournamentTemplateAcceptConfig.getEventType() +
                ",eventCode:" + rcsTournamentTemplateAcceptConfig.getEventCode());
    }

    /**
     * 处理当前提前结算订单的事件
     */
    private void updatePreSettleInfo(PreOrderRequest orderBean, PreOrderDetailRequest orderItem,
                                     RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig) {
        log.info("::{}::提前結算获取到事件配置信息:{}", orderItem.getOrderNo(), JSON.toJSONString(rcsTournamentTemplateAcceptConfig));
        if (StringUtils.isNotBlank(rcsTournamentTemplateAcceptConfig.getEventCode()) && VAR_EVENT_LIST.contains(rcsTournamentTemplateAcceptConfig.getEventCode())) {
            this.setItemCurrentEvent(OrderStatusEnum.ORDER_REJECT.getCode(), PreSettleInfoStatusEnum.VAR_REFUSE, null, orderBean,
                    orderItem,
                    rcsTournamentTemplateAcceptConfig);
            return;
        }
        //如果接单事件是null就去初始化一遍
        if (orderItem.getMaxAcceptTime() == null) {
            this.initSettleWaitTime(orderBean, orderItem, rcsTournamentTemplateAcceptConfig);
            //设置最新的事件ID
            templateAcceptConfigServerImpl.setPreSettleEventId(orderItem);
        }
        //当注单的eventId能够匹配上接拒事件参数
        if (orderItem.getEventId() != null && rcsTournamentTemplateAcceptConfig.getId() != null) {

            //当前为安全事件、注单事件为危险或者封盘、注单事件ID不等于当前事件ID  ——直接接单 事件类型 0-安全;1-危险;2-封盘;3拒单
            if (rcsTournamentTemplateAcceptConfig.getEventTypeNumber() == MatchEventConfigEnum.EVENT_SAFETY.getType().intValue()
                    && Arrays.asList(1, 2).contains(orderItem.getCurrentEventType())
                    && !orderItem.getEventId().equals(rcsTournamentTemplateAcceptConfig.getId())) {
                this.setItemCurrentEvent(OrderStatusEnum.ORDER_ACCEPT.getCode(), PreSettleInfoStatusEnum.SAFE_PASS,
                        System.currentTimeMillis(), orderBean, orderItem, rcsTournamentTemplateAcceptConfig);
                log.info("::{}::提前結算当前为安全事件、注单事件为危险或者封盘、注单事件ID不等于当前事件ID::{}", orderBean.getOrderNo(), rcsTournamentTemplateAcceptConfig);
                return;
            }

            //当前为拒单事件 --直接拒单
            if (rcsTournamentTemplateAcceptConfig.getEventTypeNumber() == MatchEventConfigEnum.EVENT_REJECT.getType().intValue()
                    && !orderItem.getEventId().equals(rcsTournamentTemplateAcceptConfig.getId())) {
                this.setItemCurrentEvent(OrderStatusEnum.ORDER_REJECT.getCode(), PreSettleInfoStatusEnum.REJECT_REFUSE, null, orderBean, orderItem, rcsTournamentTemplateAcceptConfig);
                log.info("::{}::提前結算当前为拒单事件 --直接拒单::{}", orderBean.getOrderNo(), rcsTournamentTemplateAcceptConfig);
                return;
            }
            //当前为安全事件、注单事件ID不等于当前事件ID、当前时间大于注单时间+最小等待时间、注单事件类型为安全事件  ——接单
            if (rcsTournamentTemplateAcceptConfig.getEventTypeNumber() == MatchEventConfigEnum.EVENT_SAFETY.getType().intValue() && !orderItem.getEventId().equals(rcsTournamentTemplateAcceptConfig.getId()) && System.currentTimeMillis() > (orderBean.getReqTime() + orderItem.getMinWait() * 1000) && orderItem.getCurrentEventType() == MatchEventConfigEnum.EVENT_SAFETY.getType().intValue()) {
                this.setItemCurrentEvent(OrderStatusEnum.ORDER_ACCEPT.getCode(), PreSettleInfoStatusEnum.SAFE_PASS,
                        System.currentTimeMillis(), orderBean, orderItem, rcsTournamentTemplateAcceptConfig);
                log.info("::{}::提前結算当前为安全事件、注单事件ID不等于当前事件ID、当前时间大于注单时间+最小等待时间、注单事件类型为安全事件  ——接单::{}", orderBean.getOrderNo(), rcsTournamentTemplateAcceptConfig);
                return;
            }
            //注单事件为安全、当前为危险事件或者封盘事件、注单事件ID不等于当前事件ID、走TMax
            if ((rcsTournamentTemplateAcceptConfig.getEventTypeNumber() == MatchEventConfigEnum.EVENT_DANGER.getType().intValue()
                    || rcsTournamentTemplateAcceptConfig.getEventTypeNumber() == MatchEventConfigEnum.EVENT_CLOSING.getType().intValue())
                    && !orderItem.getEventId().equals(rcsTournamentTemplateAcceptConfig.getId())
                    && (orderItem.getCurrentEventType() == MatchEventConfigEnum.EVENT_SAFETY.getType().intValue()
                    || orderItem.getCurrentEventType() == MatchEventConfigEnum.EVENT_DANGER.getType().intValue())) {
                if (rcsTournamentTemplateAcceptConfig.getEventTypeNumber() == MatchEventConfigEnum.EVENT_DANGER.getType().intValue()) {
                    this.setItemCurrentEvent(null, PreSettleInfoStatusEnum.DANGER_PASS, orderBean.getReqTime() + rcsTournamentTemplateAcceptConfig.getMaxWaitTime() * 1000, orderBean, orderItem,
                            rcsTournamentTemplateAcceptConfig);
                    log.info("::{}::提前結算注单为安全事件，当前为危险事件，更改Tmin,，更改注单事件状态::{}", orderBean.getOrderNo(), rcsTournamentTemplateAcceptConfig);
                } else {
                    this.setItemCurrentEvent(null, PreSettleInfoStatusEnum.CLOSE_REFUSE, orderBean.getReqTime() + rcsTournamentTemplateAcceptConfig.getMaxWaitTime() * 1000, orderBean, orderItem,
                            rcsTournamentTemplateAcceptConfig);
                    log.info("::{}::提前結算注单为安全事件，当前为封盘事件，更改Tmax,，更改注单事件状态::{}", orderBean.getOrderNo(), rcsTournamentTemplateAcceptConfig);
                }
            }
        }
    }
}
