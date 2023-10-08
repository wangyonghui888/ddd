package com.panda.sport.rcs.mts.sportradar.mq.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.sport.data.rcs.api.CheckOddsStatusServer;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.vo.OddStatusMessagePrompt;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.mapper.TUserMapper;
import com.panda.sport.rcs.mapper.limit.RcsLabelLimitConfigMapper;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import com.panda.sport.rcs.mts.sportradar.builder.MtsSdkInit;
import com.panda.sport.rcs.mts.sportradar.builder.TicketBuilderHelper;
import com.panda.sport.rcs.mts.sportradar.constants.Constants;
import com.panda.sport.rcs.mts.sportradar.vo.ErrorMessagePrompt;
import com.panda.sport.rcs.mts.sportradar.vo.MtsMerchantOrder;
import com.panda.sport.rcs.mts.sportradar.vo.StandardMatchMessage;
import com.panda.sport.rcs.mts.sportradar.wrapper.MtsCommonService;
import com.panda.sport.rcs.mts.sportradar.wrapper.RcsMtsOrderExtService;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.sportradar.mts.sdk.api.AutoAcceptedOdds;
import com.sportradar.mts.sdk.api.Sender;
import com.sportradar.mts.sdk.api.Ticket;
import com.sportradar.mts.sdk.api.enums.OddsChangeType;
import com.sportradar.mts.sdk.api.interfaces.TicketSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisCluster;

import java.math.BigDecimal;
import java.util.*;

import static com.panda.sport.rcs.mts.sportradar.constants.Constants.*;

/**
 * 订单发送到MTS
 */
@Component
@Slf4j
public class OrderSendMtsConsumer extends ConsumerAdapter<Map<String, Object>> {

    @Autowired
    MtsCommonService mtsCommonService;

    @Autowired
    RedisClient redisClient;

    @Autowired
    JedisCluster jedisCluster;

    @Autowired
    TUserMapper userMapper;
    @Autowired
    private RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;

    @Autowired
    RcsLabelLimitConfigMapper labelLimitConfigMapper;

    @Autowired
    private TOrderDetailMapper orderDetailMapper;

    public OrderSendMtsConsumer() {
        super(MqConstants.RCS_VALIDATE_MTS_ORDER_JOIN, "");
    }

    @Override
    public Boolean handleMs(Map<String, Object> dataMap, Map<String, String> paramsMap) {
        String orderId = "";
        try {
            //串几关
            String seriesType = String.valueOf(dataMap.get("seriesNum"));
            String totalMoney = dataMap.get("totalMoney") == null ? "0" : String.valueOf(dataMap.get("totalMoney"));
            String ip = dataMap.get("ip") == null ? null : String.valueOf(dataMap.get("ip"));
            String deviceType = dataMap.get("deviceType") == null ? null : String.valueOf(dataMap.get("deviceType"));
            //是否自动接受赔率变化 1：自动接收更好的赔率 2：自动接受任何赔率变动 3：不自动接受赔率变动
            String acceptOdds = dataMap.get("acceptOdds") == null ? null : String.valueOf(dataMap.get("acceptOdds"));

            OddsChangeType oddsChangeType = buildOddsChandeType(acceptOdds);

            //注单列表
            List<ExtendBean> list = JSONObject.parseObject(JSONObject.toJSONString(dataMap.get("list")), new TypeReference<List<ExtendBean>>() {
            });

            //订单号 方便日志跟踪
            orderId = list.get(0).getItemBean().getOrderNo();
            int matchTYpe = list.get(0).getItemBean().getMatchType();
            log.info("::{}::OrderSendMtsConsumer 收到bean info ：{}", orderId, JSONObject.toJSON(list));
            if (StringUtils.isNotBlank(redisClient.get(String.format(MTS_ORDER_OPSTATUS, orderId)))) {
                log.info("::{}::订单mts已处理,跳过：{}", orderId);
                return true;
            }
            redisClient.setExpiry(String.format(MTS_ORDER_OPSTATUS, orderId), oddsChangeType.toString(), 5 * 60L);

            //记录订单的接受赔率模式
            redisClient.setExpiry(String.format(Constants.MTS_ORDER_ODDSCHANGETYPE, orderId), oddsChangeType.toString(), 20L);
            if (!CollectionUtils.isEmpty(list)) {
                //填充基础信息
                mtsCommonService.convertAllParam(list);
                //构建SDK对象
                TicketBuilderHelper ticketBuilderHelper = MtsSdkInit.getTicketBuilderHelper();
                Sender sender = ticketBuilderHelper.buildSender(deviceType, ip, list.get(0).getUserId(), "device1", 12092L);
                Ticket ticket;
                if (!seriesType.equals("1")) {
                    ticket = ticketBuilderHelper.getSeriesTicket(list, seriesType, totalMoney, sender, oddsChangeType);
                } else {
                    ticket = ticketBuilderHelper.getTicket(list.get(0), sender, oddsChangeType);
                }
                TicketSender ticketSender = MtsSdkInit.getTicketSender();
                saveMtsOrder(orderId, totalMoney, String.valueOf(list.get(0).getMtsAmount()), ticket.getJsonValue());

                //是否走缓存
                boolean flag = doCache(list, oddsChangeType.toString());
                //按照商户级别控制是否某个商户的注单都不提交mts  1不走mts
                String merchantMtsStatusList = redisClient.get(MTS_MERCHANT_SENDTICKET_STATUS);
                if (StringUtils.isBlank(merchantMtsStatusList)) {
                    merchantMtsStatusList = "[]";
                }
                JSONArray jsonArray = JSONArray.parseArray(merchantMtsStatusList);
                String merchantCode = userMapper.selectByUserId(Long.valueOf(list.get(0).getUserId())).getMerchantCode();
                boolean isMtsPa = jsonArray.contains(merchantCode);
                log.info("::{}::{}:{}商户配置是否走mts:{}", orderId, merchantCode, isMtsPa, merchantMtsStatusList);
                //足蓝不走
                if (list.get(0).getSportId().equals("1") || list.get(0).getSportId().equals("2")) {
                    isMtsPa = false;
                    log.info("::{}::足蓝不走-商户配置是否走mts:球种:{}", orderId, list.get(0).getSportId());
                }
                if (isMtsPa && list.size() == 1) {
                    MtsMerchantOrder mtsMerchantOrder = new MtsMerchantOrder();
                    mtsMerchantOrder.setOrderNo(orderId);
                    mtsMerchantOrder.setOrderTime(System.currentTimeMillis() + "");
                    List<TOrderDetail> tOrderDetailList = orderDetailMapper.queryOrderDetails(orderId);
                    mtsMerchantOrder.setTOrderDetailList(tOrderDetailList);

                    String orderDelayTime = redisClient.hGet(String.format("rcs:tournament:property:%s", String.valueOf(list.get(0).getTournamentId())), "orderDelayTime");
                    log.info("::{}::赛事:{}缓存等待时间={}", orderId, list.get(0).getItemBean().getMatchId(), orderDelayTime);
                    //获取联赛设置默认等待时间
                    RcsMatchMarketConfig config = rcsMatchMarketConfigMapper.queryMarketConfig(list.get(0).getItemBean());
                    if (config != null) {
                        orderDelayTime = config.getWaitSeconds().toString();
                        log.info("::{}::赛事:{}优先取的等待时间={}", orderId, list.get(0).getItemBean().getMatchId(), orderDelayTime);
                    }
                    if (StringUtils.isBlank(orderDelayTime)) {
                        orderDelayTime = "5";
                    }
                    if (matchTYpe == 1) {
                        orderDelayTime = "0";
                    }
                    log.info("::{}::赛事:{}确认等待时间={}", orderId, list.get(0).getItemBean().getMatchId(), orderDelayTime);
                    mtsMerchantOrder.setDelayTime(orderDelayTime);
                    redisClient.hSet(MTS_MERCHANT_SENDTICKET_LIST, orderId, JSONObject.toJSONString(mtsMerchantOrder));
                    log.info("::{}::mts订单商户不提交mts:加入自动检测：{}",orderId,JSONObject.toJSONString(mtsMerchantOrder));
                    doMtsMerchantOrder();
                } else if (flag) {
                    log.info("::{}::mts订单走缓存", orderId);
                    Long ticketId = redisClient.incrBy("rcs:mts:auto:ticketId", 1) * (-1);
                    String status = "ACCEPTED";
                    String orderNo = orderId;
                    List<AutoAcceptedOdds> autoAcceptedOddsList = null;
                    String jsonValue = "{}";
                    Integer reasonCode = -100;
                    String reasonMsg = "缓存接单成功";
                    Integer isCache = 0;
                    mtsCommonService.updateMtsOrder(ticketId.toString(), status, orderNo, autoAcceptedOddsList, jsonValue, reasonCode, reasonMsg, isCache);
                    log.info("::{}::mts订单走缓存完成:{}", orderId);
                } else {
                    log.info("::{}::mts订单验证请求参数:{}", orderId, JSONObject.toJSONString(ticket, SerializerFeature.DisableCircularReferenceDetect));
                    ticketSender.send(ticket);
                    log.info("::{}::向mts发送完成:" + orderId);
                }
            }
        } catch (Exception e) {
            log.info("::{}::MTS订单发送异常--{},{}",orderId,e.getMessage(), e);
            return true;
        }
        return true;
    }

    //自动检测mts待接单订单
//    @PostConstruct
    private void doMtsMerchantOrder() {
        try {
            boolean running = redisClient.setNX(MTS_MERCHANT_SENDTICKET_RUNIG, "1", 24 * 60 * 60);
            if (running) {
                log.info("商户不走mts自动处理,成功获取任务");
                Map<String, String> map = jedisCluster.hgetAll(MTS_MERCHANT_SENDTICKET_LIST);
                while (map.size() > 0) {
                    log.info("商户不走mts自动处理,本次待处理数量{}", JSONObject.toJSONString(map.size()));
                    checkOrder(map);
                    Thread.sleep(1000L);
                    map = jedisCluster.hgetAll(MTS_MERCHANT_SENDTICKET_LIST);
                }
                log.info("商户不走mts自动处理,成功结束任务{}");
                redisClient.delete(MTS_MERCHANT_SENDTICKET_RUNIG);
            }
        } catch (Exception e) {
            log.info("商户不走mts自动处理异常:{},{}", e.getMessage(), e);
            redisClient.delete(MTS_MERCHANT_SENDTICKET_RUNIG);
        } finally {
            redisClient.delete(MTS_MERCHANT_SENDTICKET_RUNIG);
        }
    }

    /**
     * 检查待接单的订单
     *
     * @param map
     */
    private void checkOrder(Map<String, String> map) {
        for (String key : map.keySet()) {
            String value = map.get(key);
            try {
                MtsMerchantOrder mtsMerchantOrder = JSONObject.parseObject(value, MtsMerchantOrder.class);
                log.info("::{}::自动检测:商户不走mts自动处理{}",mtsMerchantOrder.getOrderNo(),JSONObject.toJSONString(mtsMerchantOrder));
                //检测是否到了时间
                Long intervals = System.currentTimeMillis() - Long.valueOf(mtsMerchantOrder.getOrderTime());
                if (intervals > Long.valueOf(mtsMerchantOrder.getDelayTime()) * 1000) {
                    log.info("::{}::自动检测:商户不走mts自动处理,订单到达时间接单处理开始", mtsMerchantOrder.getOrderNo());
                } else {
                    ErrorMessagePrompt errorMessagePrompt = new ErrorMessagePrompt();
                    boolean checkSatus = dealWithData(mtsMerchantOrder.getTOrderDetailList(), errorMessagePrompt);
                    //更新为拒单
                    if (checkSatus) {
                        updateMtsPa(mtsMerchantOrder, "REJECTED", errorMessagePrompt.getHintMsg());
                    }
                    return;
                }
                //更新为接单
                updateMtsPa(mtsMerchantOrder, "ACCEPTED", "自动检测:商户自己接单成功");
            } catch (Exception e) {
                log.info("::{}::自动检测:商户不走mts自动处理,处理订单异常:{}:{}", key, e.getMessage(), e);
            }
        }
    }

    /**
     * @param mtsMerchantOrder 接拒单对象
     * @param mtsStatus        mts状态  ACCEPTED接单  REJECTED拒单
     * @param reasonMsg        接拒原因描述
     */
    private void updateMtsPa(MtsMerchantOrder mtsMerchantOrder, String mtsStatus, String reasonMsg) {
        Long ticketId = redisClient.incrBy("rcs:mts:auto:ticketId", 1) * (-1);
        List<AutoAcceptedOdds> autoAcceptedOddsList = null;
        String jsonValue = "{}";
        Integer reasonCode = -101;
        Integer isCache = 0;
        mtsCommonService.updateMtsOrder(ticketId.toString(), mtsStatus, mtsMerchantOrder.getOrderNo(), autoAcceptedOddsList, jsonValue, reasonCode, reasonMsg, isCache);
        redisClient.hashRemove(MTS_MERCHANT_SENDTICKET_LIST, mtsMerchantOrder.getOrderNo());
        log.info("::{}::自动检测:商户不走mts自动处理处理完成:{}:{}", mtsMerchantOrder.getOrderNo(), mtsStatus, reasonMsg);
    }

    private boolean dealWithData(List<TOrderDetail> tOrderDetailList, ErrorMessagePrompt errorMessagePrompt) {
        for (int n = 0; n < tOrderDetailList.size(); n++) {
            TOrderDetail tOrderDetail = tOrderDetailList.get(n);
            //赛事维度
            String matchInfoStr = redisClient.get(String.format(Constants.REDIS_MATCH_INFO, tOrderDetail.getMatchId()));
            log.info("::{}::1666需求赛事维度数据::{}",tOrderDetail.getOrderNo(),matchInfoStr);
            if (StringUtils.isNotBlank(matchInfoStr)) {
                StandardMatchMessage standardMatchMessage = JSONObject.parseObject(matchInfoStr, StandardMatchMessage.class);
                //收盘状态不拒单
                if (standardMatchMessage.getStatus() != 0 && standardMatchMessage.getStatus() != 13) {
                    if (standardMatchMessage.getStatus() == 1) {
                        errorMessagePrompt.setHintMsg("赛事封盘拒单");
                    } else if (standardMatchMessage.getStatus() == 2) {
                        errorMessagePrompt.setHintMsg("赛事关盘拒单");
                    } else if (standardMatchMessage.getStatus() == 11) {
                        errorMessagePrompt.setHintMsg("赛事锁盘拒单");
                    }
                    return true;
                }
            }
            //盘口维度
            String matchMarketOddsStr = redisClient.get(String.format(Constants.REDIS_MATCH_MARKET_ODDS_NEW, tOrderDetail.getPlayId(),tOrderDetail.getMatchId()));
            if (StringUtils.isNotBlank(matchMarketOddsStr)) {
                String oddsScopeValue = "";
                //根据联赛等级设置的 赔率范围
                String tournamentScope = redisClient.hGet(String.format("rcs:tournament:property:%s", String.valueOf(tOrderDetail.getTournamentId())), "MTSOddsChangeValue");
                String oddsChangeStatus = redisClient.hGet(String.format("rcs:tournament:property:%s", String.valueOf(tOrderDetail.getTournamentId())), "oddsChangeStatus");
                if (StringUtils.isNotBlank(oddsChangeStatus) && oddsChangeStatus.equals("1")) {
                    oddsScopeValue = tournamentScope;
                }
                log.info("::{}::1666根据联赛等级设置的赔率 开关|范围:{}:{}:{}:{}::::", tOrderDetail.getOrderNo(), tOrderDetail.getBetNo(), oddsChangeStatus, oddsScopeValue);
                //玩法级别的配置 开关
                String oddsScopeMatchStatus = "rcs:risk:order:oddsScope:match.%s.match_type.%s";
                oddsScopeMatchStatus = String.format(oddsScopeMatchStatus, tOrderDetail.getMatchId(), tOrderDetail.getMatchType() == 1 ? 1 : 0);
                oddsScopeMatchStatus = redisClient.get(oddsScopeMatchStatus);
                log.info("::{}::1666玩法级别开关:{}:{}:{}::::", tOrderDetail.getOrderNo(), tOrderDetail.getBetNo(), oddsScopeMatchStatus);
                if (StringUtils.isNotBlank(oddsScopeMatchStatus) && oddsScopeMatchStatus.equals("1")) {
                    oddsScopeValue = redisClient.get(String.format(Constants.ODDS_SCOPE_KEY, tOrderDetail.getMatchId(), tOrderDetail.getPlayId(), tOrderDetail.getMatchType() == 2 ? 0 : 1));
                }
                log.info("::{}::1666需求配置的赔率:{},赛事ID:{}::",tOrderDetail.getOrderNo(),oddsScopeValue, tOrderDetail.getMatchId());
                List<StandardMarketMessage> rcsStandardMarketDTOS = JSONObject.parseArray(matchMarketOddsStr, StandardMarketMessage.class);
                log.info("::{}::1666需求订单详细表::{},{}", tOrderDetail.getOrderNo(),JSONObject.toJSONString(tOrderDetail), JSONObject.toJSONString(rcsStandardMarketDTOS));
                for (int i = 0; i < rcsStandardMarketDTOS.size(); i++) {
                    StandardMarketMessage standardMarketMessage = rcsStandardMarketDTOS.get(i);
                    log.info("::{}::1666需求下发消息::{}", tOrderDetail.getOrderNo(),JSONObject.toJSONString(standardMarketMessage));

                    if (tOrderDetail.getSportId().longValue() == SportIdEnum.FOOTBALL.getId() || tOrderDetail.getSportId().longValue() == SportIdEnum.BASKETBALL.getId()) {
                        if (StringUtils.equals(String.valueOf(standardMarketMessage.getId()),String.valueOf(tOrderDetail.getMarketId()))) {
                            //盘口状态有变化
                            if (!standardMarketMessage.getStatus().equals(0) || standardMarketMessage.getThirdMarketSourceStatus() != 0) {
                                log.info("::{}::1666需求足球盘口有变化拒单::盘口ID:{}盘口状态:{},订单状态:{}", tOrderDetail.getOrderNo(), standardMarketMessage.getId(), standardMarketMessage.getStatus(), tOrderDetail.getOrderStatus());
                                this.errorMessage(standardMarketMessage, errorMessagePrompt);
                                return true;
                            }
                        }
                    } else {
                        if (StringUtils.equals(String.valueOf(standardMarketMessage.getId()), String.valueOf(tOrderDetail.getMarketId()))) {
                            //盘口状态有变化
                            if (!standardMarketMessage.getStatus().equals(0)) {
                                log.info("::{}::1666需求其他球种盘口有变化拒单::盘口ID:{}盘口状态:{},订单状态:{}", tOrderDetail.getOrderNo(), standardMarketMessage.getId(), standardMarketMessage.getStatus(), tOrderDetail.getOrderStatus());
                                this.errorMessage(standardMarketMessage, errorMessagePrompt);
                                return true;
                            }
                            //赔率有变化
                            if (StringUtils.isNotBlank(oddsScopeValue)) {
                                BigDecimal oddsScope = new BigDecimal(oddsScopeValue).divide(new BigDecimal(100), 4, BigDecimal.ROUND_DOWN);
                                BigDecimal one = new BigDecimal(1);
                                BigDecimal orderOdds =  BigDecimal.valueOf(tOrderDetail.getOddsValue());
                                BigDecimal checkOdds = BigDecimal.ZERO;
                                for (int j = 0; j < standardMarketMessage.getMarketOddsList().size(); j++) {
                                    StandardMarketOddsMessage standardMarketOddsMessage = standardMarketMessage.getMarketOddsList().get(j);
                                    log.info("::{}::1666需求获取盘口投注项赔率::{}", tOrderDetail.getOrderNo(), JSONObject.toJSONString(standardMarketOddsMessage));
                                    if (StringUtils.equals(String.valueOf(standardMarketOddsMessage.getId()), String.valueOf(tOrderDetail.getPlayOptionsId()))) {
                                        checkOdds = new BigDecimal(standardMarketOddsMessage.getOddsValue()).divide(new BigDecimal(100000), 4, BigDecimal.ROUND_DOWN);
                                    }
                                }
                                log.info("::{}::1666需求盘口赔率::订单赔率:{},配置值赔率范围:{},checkOdds:{}", tOrderDetail.getOrderNo(), orderOdds, oddsScope, checkOdds);
                                if (one.divide(orderOdds, 4, BigDecimal.ROUND_DOWN).subtract(one.divide(checkOdds, 4, BigDecimal.ROUND_DOWN)).abs().compareTo(oddsScope) > 0) {
                                    log.info("::{}::1666需求盘口赔率有变化拒单::订单赔率:{},配置值赔率范围:{},checkOdds:{}", tOrderDetail.getOrderNo(), orderOdds, oddsScope, checkOdds);
                                    errorMessagePrompt.setHintMsg("赔率变动幅度过大拒单");
                                    return true;
                                }
                            }
                        }
                        Integer matchType = tOrderDetail.getMatchType() == 1 ? 1 : 0;
                        //盘口的位置有变化
                        if (StringUtils.equalsIgnoreCase(String.valueOf(standardMarketMessage.getMarketCategoryId()), String.valueOf(tOrderDetail.getPlayId())) &&
                                StringUtils.equalsIgnoreCase(String.valueOf(standardMarketMessage.getChildMarketCategoryId()), tOrderDetail.getSubPlayId()) &&
                                standardMarketMessage.getMarketType().equals(matchType) &&
                                tOrderDetail.getPlaceNum().equals(standardMarketMessage.getPlaceNum()) &&
                                !StringUtils.equalsIgnoreCase(String.valueOf(tOrderDetail.getMarketId()), String.valueOf(standardMarketMessage.getId()))) {
                            log.info("::{}::1666需求盘口位置有变化拒单::盘口ID:{},盘口位置:{},订单盘口位置:{}", tOrderDetail.getOrderNo(), standardMarketMessage.getId(), standardMarketMessage.getPlaceNum(), tOrderDetail.getPlaceNum());
                            errorMessagePrompt.setHintMsg("对应坑位的盘口值已变更拒单");
                            return true;
                        }
                    }
                }
            }


        }
        return false;
    }

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

    private boolean doCache(List<ExtendBean> list, String oddsChangeType) {
        OrderItem orderItem = list.get(0).getItemBean();
        String orderNo = orderItem.getOrderNo();

        if (list.size() > 1) {
            log.info("::{}::MTS订单缓存流程跳过:非单关",orderNo);
            return false;
        }

        //标签延迟的 也不走缓存哦 31241

        LambdaQueryWrapper<RcsLabelLimitConfig> limitConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
        limitConfigLambdaQueryWrapper.eq(RcsLabelLimitConfig::getTagId, list.get(0).getUserTagLevel());
        List<RcsLabelLimitConfig> delayList = labelLimitConfigMapper.selectList(limitConfigLambdaQueryWrapper);
        if (ObjectUtils.isNotEmpty(delayList) && ObjectUtils.isNotEmpty(delayList.get(0).getBetExtraDelay()) && delayList.get(0).getBetExtraDelay() > 0) {
            Integer delayTime = delayList.get(0).getBetExtraDelay();
            log.info("::{}::MTS订单缓存流程:标签{}延期时间{}", orderNo, list.get(0).getUserTagLevel(), delayTime);
            if (null != delayTime) {
                return false;
            }
        }

        Long optionId = orderItem.getPlayOptionsId();
        String oddFinally = orderItem.getOddFinally();
        String mtsOrderCache = String.format(Constants.MTS_ORDER_CACHE, optionId, oddFinally, oddsChangeType);
        mtsOrderCache = redisClient.get(mtsOrderCache);
        if (StringUtils.isBlank(mtsOrderCache)) {
            log.info("::{}::MTS订单缓存流程跳过:无缓存",orderNo);
            return false;
        }
        //缓存存在的情况  概率性接单
        String mtsOrderRate = redisClient.get(Constants.MTS_ORDER_RATE);
        if (StringUtils.isEmpty(mtsOrderRate)) {
            mtsOrderRate = "70";
        }
        Random rd = new Random();
        int num = rd.nextInt(100);
        if (num > Integer.valueOf(mtsOrderRate)) {
            log.info("::{}::MTS订单缓存流程跳过:随机未命中:mtsOrderRate={}:num={}", orderNo,mtsOrderRate, num);
            return false;
        }
        log.info("::{}::MTS订单缓存流程通过:mtsOrderRate={}:num={}",orderNo,mtsOrderRate, num);
        return true;
    }

    /**
     * 是否自动接受赔率变化 1：自动接收更好的赔率 2：自动接受任何赔率变动 3：不自动接受赔率变动
     *
     * @param acceptOdds
     * @return
     */
    private OddsChangeType buildOddsChandeType(String acceptOdds) {
        if ("1".equals(acceptOdds)) {
            return OddsChangeType.HIGHER;
        } else if ("2".equals(acceptOdds)) {
            return OddsChangeType.ANY;
        } else if ("3".equals(acceptOdds)) {
            return OddsChangeType.NONE;
        }
        return OddsChangeType.NONE;
    }

    private void saveMtsOrder(String orderNo, String paMount, String mtsAmount, String requestJson) {
        RcsMtsOrderExtService rcsMtsOrderExtService = SpringContextUtils.getBeanByClass(RcsMtsOrderExtService.class);
        RcsMtsOrderExt rcsMtsOrderExt = new RcsMtsOrderExt();
        rcsMtsOrderExt.setOrderNo(orderNo);
        rcsMtsOrderExt.setRequestJson(requestJson);
        rcsMtsOrderExt.setStatus("INIT");
        rcsMtsOrderExt.setPaAmount(paMount);
        rcsMtsOrderExt.setMtsAmount(mtsAmount);
        rcsMtsOrderExtService.addMtsOrder(rcsMtsOrderExt);
    }
}
