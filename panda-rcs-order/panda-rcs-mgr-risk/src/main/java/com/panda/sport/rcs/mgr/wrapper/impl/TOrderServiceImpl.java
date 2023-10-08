package com.panda.sport.rcs.mgr.wrapper.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.TemplateAcceptConfigServer;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.data.rcs.api.tournament.TournamentTemplateByMatchService;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.PreOrderRequest;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.data.rcs.vo.RcsTournamentTemplateAcceptConfig;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.enums.*;
import com.panda.sport.rcs.mapper.*;
import com.panda.sport.rcs.mapper.limit.RcsLabelLimitConfigMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplateAcceptEventMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplateRefMapper;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.constant.LimitRedisKeys;
import com.panda.sport.rcs.mgr.constant.RcsCacheContant;
import com.panda.sport.rcs.mgr.enums.MatchTypeEnum;
import com.panda.sport.rcs.mgr.enums.OrderHideCategoryEnum;
import com.panda.sport.rcs.mgr.enums.RedisCmdEnum;
import com.panda.sport.rcs.mgr.enums.SpecialEnum;
import com.panda.sport.rcs.mgr.mq.bean.HideOrderDTO;
import com.panda.sport.rcs.mgr.repository.TOrderDetailExtRepository;
import com.panda.sport.rcs.mgr.service.impl.ParamValidate;
import com.panda.sport.rcs.mgr.utils.RealTimeControlUtils;
import com.panda.sport.rcs.mgr.utils.RefreshScopeNacosConfig;
import com.panda.sport.rcs.mgr.utils.StringUtil;
import com.panda.sport.rcs.mgr.utils.TOrderDetailExtUtils;
import com.panda.sport.rcs.mgr.wrapper.*;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.pojo.dto.RcsHideRangeConfigDTO;
import com.panda.sport.rcs.pojo.enums.MatchEventConfigEnum;
import com.panda.sport.rcs.pojo.limit.RedisUpdateVo;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.pojo.vo.PauseOrderVo;
import com.panda.sport.rcs.pojo.vo.TUserBetRate;
import com.panda.sport.rcs.service.*;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import com.panda.sport.rcs.utils.RcsMonitorConsumerUtils;
import com.panda.sport.rcs.wrapper.limit.RcsQuotaBusinessLimitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.mgr.constant.RcsCacheContant.*;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-03
 */
@Service
@Slf4j
@RefreshScope
public class TOrderServiceImpl<slf4j> extends ServiceImpl<TOrderMapper, TOrder> implements ITOrderService {
    @Autowired
    TOrderMapper orderMapper;
    @Autowired
    RcsOrderSecondConfigMapper rcsOrderSecondConfigMapper;
    @Autowired
    TOrderDetailMapper orderDetailMapper;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    ParamValidate paramValidate;
    @Autowired
    RedisClient redisClient;
    @Autowired
    TOrderDetailExtMapper tOrderDetailExtMapper;
    @Autowired
    TOrderDetailExtRepository tOrderDetailExtRepository;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    RcsMatchOrderAcceptEventConfigMapper rcsMatchOrderAcceptEventConfigMapper;
    @Autowired
    RcsTournamentOrderAcceptEventConfigMapper rcsTournamentOrderAcceptEventConfigMapper;
    @Autowired
    ITOrderDetailService orderDetailService;
    @Autowired
    private RcsMarketCategorySetRelationMapper rcsMarketCategorySetRelationMapper;
    @Autowired
    RcsTournamentTemplateAcceptEventMapper templateEventMapper;
    @Autowired
    IRcsUserConfigNewService rcsUserConfigNewService;
    @Autowired
    private IRcsOperateMerchantsSetService rcsOperateMerchantsSetService;
    @Autowired
    private RcsLabelLimitConfigMapper labelLimitConfigMapper;
    @Autowired
    private RcsLabelSportVolumePercentageMapper labelSportVolumePercentageMapper;
    @Autowired
    private RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;
    @Autowired
    private MongoTemplate mongotemplate;
    @Autowired
    private RcsTournamentTemplateRefMapper templateRefMapper;
    @Autowired
    private MerchantsSinglePercentageMapper merchantsSinglePercentageMapper;
    @Autowired
    IMerchantsSinglePercentageService merchantsSinglePercentageService;
    @Autowired
    IRcsTournamentTemplateService rcsTournamentTemplateService;
    @Autowired
    TOrderDetailExtUtils tOrderDetailExtUtils;

    @Autowired
    private RealTimeControlUtils realTimeControlUtils;

    @Autowired
    private IRcsMerchantCommonConfigService rcsMerchantCommonConfigService;
    @Autowired
    private TUserBetRateMapper tUserBetRateMapper;
    private final String RISK_DYNAMIC_CONFIG_SWITCH = "rcs:trade:bet:volume:config";


    private final String CONFIG_STATUS = "status";

    private final String CONFIG_SPORTIDS = "sportIds";

    @Autowired
    private RcsMerchantsHideRangeConfigService rcsMerchantsHideRangeConfigService;

    @Autowired
    private RefreshScopeNacosConfig refreshScopeNacosConfig;

    @Autowired
    private RcsQuotaBusinessLimitService rcsQuotaBusinessLimitService;
    @Resource
    private IRcsHideRangeConfigService hideRangeConfigService;

    @Resource
    private LimitApiService limitApiService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrderAndItem(OrderBean orderBean, TOrder order) {
        long startTime = System.currentTimeMillis();
        try {
            //初始化状态
            orderMapper.insertAndUpdate(order);
            orderDetailMapper.insertOrderDetail(order.getOrderDetailList());
            Integer matchType = orderBean.getItems().get(0).getMatchType();
            Integer sportId = orderBean.getItems().get(0).getSportId();
            //足球的滚球盘  非足球的冠军盘 非vip用户
            if ((SportIdEnum.isFootball(sportId) && !MatchTypeEnum.isChampionMarket(matchType) && !Objects.equals(orderBean.getOrderStatus(), OrderInfoStatusEnum.EARLY_PASS.getCode()))
                    || MatchTypeEnum.isChampionMarket(matchType) && orderBean.getVipLevel() != 1) {
                //如果是保存，就发送到计算队列
                if (!Arrays.asList(realTimeControlUtils.getMerchantIds().split(",")).contains(Long.toString(orderBean.getTenantId()))) {
                    producerSendMessageUtils.sendMessage("RISK_ORDER_TRIGGER_CHANGE", String.valueOf(System.currentTimeMillis())
                            , orderBean.getOrderNo(), orderBean);
                }
            }
            String orderKey = String.format(RcsCacheContant.REDIS_MATCH_DETAIL_EXT_INFO_KEY, orderBean.getOrderNo());
            redisClient.setExpiry(orderKey, JSON.toJSONString(orderBean), 10 * 60L);

            if (!Arrays.asList(realTimeControlUtils.getMerchantIds().split(",")).contains(Long.toString(orderBean.getTenantId()))) {
                //藏单入库
                String volumePercentageKey = "rcs:order:volumePercentage:";
                String volumePercentageRedis = redisClient.get(volumePercentageKey + orderBean.getOrderNo());
                if (!StringUtils.isBlank(volumePercentageRedis)) {
                    AmountTypeVo amountTypeVo = JSONObject.parseObject(volumePercentageRedis, AmountTypeVo.class);
                    HideOrderDTO hideOrderDTO = new HideOrderDTO(amountTypeVo, orderBean.getOrderNo(), orderBean.getDeviceType(), amountTypeVo.getVolumePercentage(), orderBean.getVipLevel());
                    producerSendMessageUtils.sendMessage(Constants.RCS_HIDE_ORDER_SAVE, "", orderBean.getOrderNo(), hideOrderDTO);
                }
            }
        } catch (DuplicateKeyException e) {
            log.warn("::{}::订单保存,不再重复保存：{}", orderBean.getOrderNo(), e.getMessage());
        } catch (Exception e) {
            log.error("::{}::订单保存,信息失败Exception:{}", orderBean.getOrderNo(), e.getMessage(), e);
            throw e;
        }

        log.info("::{}::saveOrderAndItem订单保存:成功消费,耗时:{}", orderBean.getOrderNo(), System.currentTimeMillis() - startTime);
    }

    private boolean isEarly(OrderBean orderBean) {
        for (OrderItem orderItem : orderBean.getItems()) {
            if (orderItem.getMatchType() == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void creditLimitCallback(String orderNo) {
        // 信用模式额度回滚
        String creditKey = "rcs:limit:redisUpdateRecord:credit:" + orderNo;
        redisCallback(creditKey);
    }

    private static OrderItem buildOrderItem(TOrderDetail tOrderDetail) {
        OrderItem orderItem = new OrderItem();
        BeanUtils.copyProperties(tOrderDetail, orderItem);
        orderItem.setOddFinally(orderItem.getOddsValue().toString());
        orderItem.setVolumePercentage(tOrderDetail.getVolumePercentage());
        return orderItem;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePreOrderAndItemStatus(PreOrderRequest orderBean, RcsPreOrderDetailExt order) {
        long startTime = System.currentTimeMillis();

        if (orderBean.getOrderStatus() == 0) {
            //如果是等待的状态就发送到接距topic
            producerSendMessageUtils.sendMessage(MqConstants.RCS_PRE_SETTLE_ORDER_REJECT,
                    MqConstants.RCS_PRE_SETTLE_ORDER_REJECT_TAG, orderBean.getOrderNo(), orderBean);
        } else {
            //只有不是秒接秒拒，才发送mq通知业务
            producerSendMessageUtils.sendMessage(MqConstants.RCS_PRE_SETTLE_RETURN + "_" + orderBean.getUserGroup(),
                    MqConstants.RCS_PRE_SETTLE_RETURN_TAG, orderBean.getOrderNo(), orderBean);
        }
        log.info("::{}::updateOrderAndItemStatus订单更新:成功消费,耗时:{}", order.getOrderNo(), System.currentTimeMillis() - startTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderAndItemStatus(OrderBean orderBean, TOrder order) {
        Long startTime = System.currentTimeMillis();

        log.info("::{}::开始处理主要更新或者保存业务开始时间:{}", orderBean.getOrderNo(), startTime);
        if (orderBean.getInfoStatus().equals(OrderInfoStatusEnum.MTS_PASS.getCode())) {
            updateOrderDetailOdds(orderBean);
            //更新当前赔率
            orderBean.getItems().forEach(consumer -> {
                orderBean.getOddsChangeList().forEach(oddsChange -> {
                    if (String.valueOf(oddsChange.get("betNo")).equals(consumer.getBetNo())) {
                        BigDecimal oddsNew = new BigDecimal(String.valueOf(oddsChange.get("usedOdds"))).multiply(new BigDecimal(BaseConstants.MULTIPLE_VALUE));
                        consumer.setOddsValue(oddsNew.doubleValue());
                        consumer.setOddFinally(String.valueOf(oddsChange.get("usedOdds")));
                    }
                });
            });
        }
        int orderStatus = orderBean.getOrderStatus();
        int infoStatus = orderBean.getInfoStatus();

        /*if(OrderInfoStatusEnum.PAUSE_ORDER.getCode().equals(infoStatus)){
            infoStatus = OrderInfoStatusEnum.RISK_PROCESSING.getCode();
            log.info("忽略暂停注单{},恢复到待处理状态",order.getOrderNo());
        }*/
        List<TOrderDetail> dts = Lists.newArrayList();
        dts.addAll(order.getOrderDetailList());

        RcsOperateMerchantsSet merchantsSet = RcsLocalCacheUtils.getValue("rcsOperateMerchantsSet:" + orderBean.getTenantId(), (k) -> {
            LambdaQueryWrapper<RcsOperateMerchantsSet> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RcsOperateMerchantsSet::getMerchantsId, String.valueOf(orderBean.getTenantId()));
            return rcsOperateMerchantsSetService.getOne(wrapper);
        }, 12 * 60 * 60 * 1000L);
        log.info("::{}::获取商户信息:{}", orderBean.getOrderNo(), merchantsSet);

        //测试商户 跳过
        if (merchantsSet == null || Objects.isNull(merchantsSet.getStatus()) || merchantsSet.getStatus() == 0
                || Objects.isNull(merchantsSet.getValidStatus()) || merchantsSet.getValidStatus() == 0) {
            for (TOrderDetail detail : order.getOrderDetailList()) {
                log.info("::{}::测试商户矩阵重置", detail.getOrderNo());
                detail.setRecVal(null);
                detail.setRecType(null);
            }
        }
        //3.滚球接拒单，保存ext扩展表
//        Integer passStatus = 0;
//        if (infoStatus == OrderInfoStatusEnum.RISK_PROCESSING.getCode() ||
//                infoStatus == OrderInfoStatusEnum.PAUSE_ORDER.getCode()) {
//            passStatus = getOrderStatus(orderBean, passStatus);
//        }
        //1666需求当订单保存的时候才设置
        //orderScroll(orderBean);
        //4.接单,发送实货量计算....并推送早盘mq。。。MTS操盘不做更新
        if (orderStatus == OrderStatusEnum.ORDER_ACCEPT.getCode()) {
            Integer sportId = orderBean.getItems().get(0).getSportId();
            // 篮球投注成功才计算货量
            OrderBean orderMsgBean = orderBean;
            List<OrderItem> orderItemList = order.getOrderDetailList().stream().map(TOrderServiceImpl::buildOrderItem).collect(Collectors.toList());
            orderItemList.forEach(item -> item.setOddsValue(new BigDecimal(item.getOddsValue().toString()).multiply(new BigDecimal("100000")).doubleValue()));
            orderMsgBean.setItems(orderItemList);
            ExtendBean extendBean = paramValidate.buildExtendBean(orderMsgBean, orderMsgBean.getItems().get(0));
            orderMsgBean.setExtendBean(extendBean);
            orderMsgBean.setValidateResult(MatchEventConfigEnum.ORDER_CHECKED_VALIDATERESULT.getValue());
            orderMsgBean.getExtendBean().setValidateResult(MatchEventConfigEnum.ORDER_CHECKED_VALIDATERESULT.getValue());//信用模式注单 不计算货量
            boolean isCreditOrder = NumberUtils.INTEGER_TWO.equals(orderBean.getLimitType());
            if (!isCreditOrder) {
                RcsMonitorConsumerUtils.handleApi(RcsConstant.TAG_MQ_ORDER_PREDICT_CALC, () -> {
                    if (!Arrays.asList(realTimeControlUtils.getMerchantIds().split(",")).contains(Long.toString(orderBean.getTenantId()))) {
                        producerSendMessageUtils.sendMessage(MqConstants.RCS_ORDER_REALTIMEVOLUME, MqConstants.RCS_ORDER_REALTIMEVOLUME_TAG, orderMsgBean.getOrderNo(), orderMsgBean);
                    }
                    return null;
                });
            }
            orderBean.setExtendBean(extendBean);
            //商户单场预警
            merchantsSingleCacl(orderBean, merchantsSet.getMerchantsCode());
        }

        log.info("::{}::推送ws订单", orderBean.getOrderNo());
        if (orderBean.getOrderStatus().equals(OrderStatusEnum.ORDER_WAITING.getCode())) {
            //一键秒接推送
//            OrderBean tempBean = JSONObject.parseObject(JSONObject.toJSONString(orderBean), OrderBean.class);
//            //暂停注单忽略暂停之后 订单转态还原到待处理
//            log.info("::{}::orderStatus:{},开始推送WS", tempBean.getOrderNo(), tempBean.getOrderStatus());
//            if (OrderInfoStatusEnum.PAUSE_ORDER.getCode().equals(tempBean.getInfoStatus())) {
//                tempBean.setIsUpdateOdds(true);
//                tempBean.setInfoStatus(OrderInfoStatusEnum.RISK_PROCESSING.getCode());
//            }
//
//            if (passStatus == 1) {
//                tempBean.setOrderStatus(OrderStatusEnum.ORDER_ACCEPT.getCode());
//                tempBean.setInfoStatus(OrderInfoStatusEnum.ALL_PASS.getCode());
//            }
//            if (passStatus == 2) {
//                tempBean.setOrderStatus(OrderStatusEnum.ORDER_ACCEPT.getCode());
//                tempBean.setInfoStatus(OrderInfoStatusEnum.SCROLL_PASS.getCode());
//            }
//            //暂停接拒
//            if (passStatus == 3) {
//                tempBean.setInfoStatus(OrderInfoStatusEnum.PAUSE_ORDER.getCode());
//                List<OrderItem> items = tempBean.getItems();
//                if (CollectionUtils.isNotEmpty(items)) {
//                    int asInt = tempBean.getItems().stream().filter(fi -> null != fi.getPauseTime() && fi.getPauseTime() > 0).mapToInt(OrderItem::getPauseTime).max().getAsInt();
//                    items.stream().forEach(e -> e.setPauseTime(asInt));
//                }
//            }

            sendOrderWs(orderBean);
        } else if (orderBean.getOrderStatus().equals(OrderStatusEnum.ORDER_ACCEPT.getCode()) || orderBean.getOrderStatus().equals(OrderStatusEnum.ORDER_REJECT.getCode())) {
            log.info("::{}::orderStatus:{},开始推送WS", orderBean.getOrderNo(), orderBean.getOrderStatus());
            if (!orderBean.getInfoStatus().equals(OrderInfoStatusEnum.EARLY_PASS.getCode()) && !orderBean.getInfoStatus().equals(OrderInfoStatusEnum.EARLY_REFUSE.getCode())) {
                orderBean.setIsUpdateOdds(true);
            }
            if (!OrderInfoStatusEnum.ALL_PASS.getCode().equals(orderBean.getInfoStatus())) {
                sendOrderWs(orderBean);
            }
        }


        //7只要是拒单就回滚pa额度
        if (orderBean.getOrderStatus().equals(OrderStatusEnum.ORDER_REJECT.getCode())) {
            if (infoStatus == OrderInfoStatusEnum.EARLY_REFUSE.getCode()) {
                log.info("::{}::早盘拒单，不需要往下处理", orderBean.getOrderNo());
                return;
            }

            //业务拒单，MTS单，也需要拒单
            if (infoStatus == OrderInfoStatusEnum.BS_REFUSE.getCode()) {
                if (orderBean.getItems() != null && orderBean.getItems().size() > NumberUtils.INTEGER_ZERO) {
                    Integer riskChannel = orderBean.getItems().get(NumberUtils.INTEGER_ZERO).getRiskChannel();
                    //MTS 订单需要处理
                    if (riskChannel.intValue() == NumberUtils.INTEGER_TWO) {
                        producerSendMessageUtils.sendMessage("queue_reject_mts_order,rejectOrder", orderBean);
                    }
                }
                /**
                 * 货量 期望 回滚
                 * 说明:如先成功 再失败:calculateStatus会标志为1  则做回滚
                 *     如先失败 再成功:calculateStatus为空,不做回滚,  再成功更新的时候,上面就return了 不做下面的逻辑(也不会增加货量)
                 */
                Integer nowStatus = orderBean.getOrderStatus();
                orderBean.setOrderStatus(1);
                List<OrderItem> orderItemList = order.getOrderDetailList().stream().map(TOrderServiceImpl::buildOrderItem).collect(Collectors.toList());
                orderItemList.forEach(item -> item.setOddsValue(new BigDecimal(item.getOddsValue().toString()).multiply(new BigDecimal("100000")).doubleValue()));
                orderBean.setItems(orderItemList);
                calculate(orderBean);
                orderBean.setOrderStatus(nowStatus);
            }
            if (CollectionUtils.isNotEmpty(orderBean.getItems())) {
                OrderItem orderItem = orderBean.getItems().get(0);
                if (new Integer(3).equals(orderItem.getMatchType())) {
                    log.info("::{}::冠军玩法回滚限额", orderBean.getOrderNo());
                    championLimitCallback(orderBean.getOrderNo());
                }
            }
            if (NumberUtils.INTEGER_TWO.equals(orderBean.getLimitType())) {
                // 信用模式拒单，额度回滚
                creditLimitCallback(orderBean.getOrderNo());
            } else {
                String rollbackTopic = "ORDER_SAVE_ROLLBACK";
                String specialKey = RcsConstant.RCS_TRADE_USER_SPECIAL_BET_LIMIT_CONFIG + orderBean.getUid();
                String specialType = RcsLocalCacheUtils.getValue(specialKey, "type", redisClient::hGet);
                if (StringUtils.isNotBlank(specialType) && specialType.equals("4")) {
                    rollbackTopic = "ORDER_SAVE_ROLLBACK_VIP";
                }
                log.info("::{}::拒单 回滚pa额度:vipLevel={}:specialType={}", orderBean.getOrderNo(), orderBean.getVipLevel(), specialType);
                if (orderBean.getSeriesType() == 1) {
                    //此数据保存  用于mts下单后 回滚
                    String luaCacheKey = "rcs:order:lua:result:" + orderBean.getOrderNo();
                    String luaResult = redisClient.get(luaCacheKey);
                    if (StringUtils.isNotBlank(luaResult) && orderBean.getVipLevel() != 1) {
                        Map<String, Object> result = JSONObject.parseObject(luaResult, Map.class);
                        //发送到队列做回滚
                        if ("1".equals(String.valueOf(result.get("code")))) {
                            log.info("::{}::拒单 回滚pa额度, 发送成功", orderBean.getOrderNo());
                            result.put("orderStatus", OrderStatusEnum.ORDER_REJECT.getCode());
                            producerSendMessageUtils.sendMessage(rollbackTopic, result);
                        }
                    }
                } else {
                    // 串关回滚
                    String key = "rcs:limit:redisUpdateRecord:series:" + orderBean.getOrderNo();
                    redisCallback(key);
                }
            }
        }
        //如果是等待的状态就发送到接距topic
        // 等待状态并且不是第三方接拒
        if (orderBean.getOrderStatus() == 0 && !isMtsWithThird(orderBean)
                || (orderBean.getOrderStatus() == 0 && orderBean.getExtendBean() != null && orderBean.getExtendBean().getRiskChannel().equals(OrderTypeEnum.GTS_PA.getValue().toString()))
                || (orderBean.getOrderStatus() == 0 && orderBean.getExtendBean() != null && orderBean.getExtendBean().getRiskChannel().equals(OrderTypeEnum.ODDIN_PA.getValue().toString()))
                || (orderBean.getOrderStatus() == 0 && orderBean.getExtendBean() != null && orderBean.getExtendBean().getRiskChannel().equals(OrderTypeEnum.BTS_PA.getValue().toString()))
                /*|| (orderBean.getOrderStatus() == 0 &&  orderBean.getExtendBean() !=null && orderBean.getExtendBean().getRiskChannel().equals(OrderTypeEnum.REDCAT_PA.getValue().toString()))*/
                || (orderBean.getOrderStatus() == 0 && orderBean.getExtendBean() != null && orderBean.getExtendBean().getRiskChannel().equals(OrderTypeEnum.CTS_PA.getValue().toString()))
                || (orderBean.getOrderStatus() == 0 && orderBean.getExtendBean() != null && orderBean.getExtendBean().getRiskChannel().equals(OrderTypeEnum.VIRTUAL_PA.getValue().toString()))) {
            producerSendMessageUtils.sendMessage("rcs_reject_bet_order", "reject_bet", orderBean.getOrderNo(), orderBean);
        }
        log.info("::{}::updateOrderAndItemStatus订单更新:成功消费,耗时:{}", order.getOrderNo(), System.currentTimeMillis() - startTime);
    }

    /**
     * 判断是否是走第三方接拒逻辑
     */
    public Boolean isMtsWithThird(OrderBean orderBean) {
        List<OrderItem> orderItemList = orderBean.getItems();
        //是否第三方数据源赛事 或者是GTS操盘
        boolean isAllData = true;
        //串关是否包含其他操盘的赛事
        int isContainsGTSPlatform = 0;
        int isContainsCTSPlatform = 0;
        int isContainsREDCATPlatform = 0;
        int isContainsOTSPlatform = 0;
        boolean isAutoTradeType = true;
        for (OrderItem orderItem : orderItemList) {
            //是否指定的三方操盘平台
            String platform = orderItem.getPlatform();
            boolean isThirdPlatForm = OrderTypeEnum.GTS.getPlatFrom().equalsIgnoreCase(platform)
                    || OrderTypeEnum.CTS.getPlatFrom().equalsIgnoreCase(platform)
                    || OrderTypeEnum.REDCAT.getDataSource().equalsIgnoreCase(orderItem.getDataSourceCode())
                    || OrderTypeEnum.ODDIN.getPlatFrom().equalsIgnoreCase(platform);
            if (!isThirdPlatForm) {
                isAllData = false;
            }
            //手动操盘不走第三方 0自动 1手动
            if ("1".equalsIgnoreCase(String.valueOf(orderItem.getTradeType()))) {
                isAutoTradeType = false;
            }
            //串关包含不同的操盘赛事 不走第三方
            if (OrderTypeEnum.GTS.getPlatFrom().equalsIgnoreCase(platform)) {
                isContainsGTSPlatform++;
            }
            if (OrderTypeEnum.CTS.getPlatFrom().equalsIgnoreCase(platform)) {
                isContainsCTSPlatform++;
            }
            if (OrderTypeEnum.REDCAT.getDataSource().equalsIgnoreCase(orderItem.getDataSourceCode())) {
                isContainsREDCATPlatform++;
            }
            if (OrderTypeEnum.ODDIN.getPlatFrom().equalsIgnoreCase(orderItem.getPlatform())) {
                isContainsOTSPlatform++;
            }
        }
        //是单关并且不是自动操盘或者是内部商户，不走第三方
        int size = orderItemList.size();
        if (size == 1) {
            if (!isAutoTradeType || isSubmitThird(orderBean)) {
                return false;
            }
        } else {
            boolean isContainsOther = (isContainsGTSPlatform != 0 && isContainsGTSPlatform != size)
                    || (isContainsCTSPlatform != 0 && isContainsCTSPlatform != size)
                    || (isContainsREDCATPlatform != 0 && isContainsREDCATPlatform != size);
            if (isContainsOther) {
                log.info("::{}::串关包含其他操盘方赛事不走第三方", orderBean.getOrderNo());
                return false;
            }
        }
        //第三方数据源并且自动操盘
        boolean isMtsWithThird = isAllData && isAutoTradeType;
        log.info("::{}::订单走{}接拒", orderBean.getOrderNo(), isMtsWithThird ? "third" : "reject");
        return isMtsWithThird;
    }

    /**
     * 内部商户接单方式判断
     *
     * @param orderBean
     * @return
     */
    private boolean isSubmitThird(OrderBean orderBean) {
        //特殊商户的注单都不提交数据商 只走内部接单
        String merchantList = redisClient.get("rcs:third:merchant:status:list");
        if (StringUtils.isBlank(merchantList)) {
            merchantList = "[]";
        }
        //获取商户限额配置
        RcsQuotaBusinessLimitResVo businessLimit = getBusinessLimit(orderBean.getTenantId());
        boolean isSubmitThird = JSONArray.parseArray(merchantList).contains(businessLimit.getParentName());
        //足球 篮球默认不走内部
        Integer sportId = orderBean.getItems().get(0).getSportId();
        if (Arrays.asList(1, 2).contains(sportId)) {
            isSubmitThird = false;
            log.info("::{}::{}默认发往第三方投注", orderBean.getOrderNo(), sportId == 1 ? "足球" : "篮球");
        }
        return isSubmitThird;
    }

    /**
     * rpc获取商户限额
     *
     * @param businessId 商户ID
     * @return 商户信息
     * @author beulah
     */
    public RcsQuotaBusinessLimitResVo getBusinessLimit(final Long businessId) {
        String key = LimitRedisKeys.MERCHANT_LIMIT_KEY + businessId;
        return RcsLocalCacheUtils.getValue(key, (k) -> {
            Response<RcsQuotaBusinessLimitResVo> response = null;
            try {
                response = limitApiService.getRcsQuotaBusinessLimit(businessId.toString());
            } catch (Exception e) {
                log.error("::{}::获取商户配置信息RPC异常:{}", businessId.toString(), e);
            }
            if (response == null || response.getCode() != 200) {
                log.error("::{}::获取商户配置信息错误:{}", businessId.toString(), JSONObject.toJSONString(response));
            }
            return response.getData();
        }, 60 * 1000L);
    }

    //商户单场限额记录处理
    private void merchantsSingleCacl(OrderBean orderBean, String merchantsCode) {
        try {
            log.info("::{}::商户单场限额百分比预警开始", orderBean.getOrderNo());
            Integer sportId = orderBean.getItems().get(0).getSportId();
            String merchantsSingleKey = "rcs:risk:merchants_single_percentage.match_id.%s.merchants_id.%s.match_type.%s";
            Long merchantsId = orderBean.getTenantId();
            Long matchId = orderBean.getItems().get(0).getMatchId();
            Integer matchType = orderBean.getItems().get(0).getMatchType();
            merchantsSingleKey = String.format(merchantsSingleKey, matchId, merchantsId, matchType);
            String merchantsSingleStatus = RcsLocalCacheUtils.getValue(merchantsSingleKey, redisClient::get, 10 * 60 * 1000L);

            if (matchType == 3) {
                log.info("::{}::商户单场限额百分比订单号冠军跳过", orderBean.getOrderNo());
                return;
            }

            MerchantsSinglePercentage singlePercentage = new MerchantsSinglePercentage();
            singlePercentage.setMatchId(matchId);
            singlePercentage.setMerchantsId(merchantsId);
            singlePercentage.setMatchType(matchType);
            singlePercentage.setSportId(sportId);
            singlePercentage.setMerchantsName(merchantsCode);
            if (StringUtils.isBlank(merchantsSingleStatus)) {
                log.info("::{}::商户单场限额百分比订单号首次入库", orderBean.getOrderNo());
                singlePercentage.setMatchInfo(orderBean.getItems().get(0).getMatchInfo());
                LambdaQueryWrapper<RcsTournamentTemplate> templateWrapper = new LambdaQueryWrapper<>();
                templateWrapper.eq(RcsTournamentTemplate::getSportId, sportId);
                templateWrapper.eq(RcsTournamentTemplate::getType, 3);
                templateWrapper.eq(RcsTournamentTemplate::getTypeVal, matchId);
                templateWrapper.eq(RcsTournamentTemplate::getMatchType, matchType == 2 ? 0 : 1);
                RcsTournamentTemplate rcsTournamentTemplate = rcsTournamentTemplateService.getOne(templateWrapper);
                if (rcsTournamentTemplate == null) {
                    log.info("::{}::商户单场限额百分比RcsTournamentTemplate不存在", orderBean.getOrderNo());
                    return;
                }
                singlePercentage.setMatchLimit(rcsTournamentTemplate.getBusinesMatchPayVal());
                try {
                    merchantsSinglePercentageMapper.insert(singlePercentage);
                } catch (Exception e) {
                    log.info("::{}::商户单场限额百分比订单兼容redis过期特殊情况:{}", orderBean.getOrderNo(), merchantsSingleStatus);
                }
                redisClient.setExpiry(merchantsSingleKey, "1", 3 * 30 * 24 * 60 * 60L);
            }

            merchantsSinglePercentageService.add(merchantsSingleKey, singlePercentage);
            log.info("::{}::商户单场限额百分比订单号完成:{}", orderBean.getOrderNo(), merchantsSingleStatus);
        } catch (Exception e) {
            log.error("::{}::商户单场限额百分比更新异常：{}", orderBean.getOrderNo(), e.getMessage(), e);
        }
    }

    @Override
    public void championLimitCallback(String orderNo) {
        // 冠军玩法额度回滚
        String creditKey = "rcs:limit:redisUpdateRecord:champion:" + orderNo;
        redisCallback(creditKey);
    }

    private void redisCallback(String key) {
        String value = redisClient.get(key);
        if (StringUtils.isNotBlank(value)) {
            log.info("额度回滚：key=" + key);
            List<RedisUpdateVo> redisUpdateList = JSON.parseArray(value, RedisUpdateVo.class);
            if (CollectionUtils.isNotEmpty(redisUpdateList)) {
                redisUpdateList.forEach(vo -> {
                    if (RedisCmdEnum.isIncrBy(vo.getCmd())) {
                        redisClient.incrBy(vo.getKey(), -1 * StringUtil.toLong(vo.getValue(), 0L));
                    } else if (RedisCmdEnum.isIncrByFloat(vo.getCmd())) {
                        redisClient.incrByFloat(vo.getKey(), -1 * StringUtil.toDouble(vo.getValue(), 0.0D));
                    } else if (RedisCmdEnum.isHincrBy(vo.getCmd())) {
                        redisClient.hincrBy(vo.getKey(), vo.getField(), -1 * StringUtil.toLong(vo.getValue(), 0L));
                    } else if (RedisCmdEnum.isHincrByFloat(vo.getCmd())) {
                        redisClient.hincrByFloat(vo.getKey(), vo.getField(), -1 * StringUtil.toDouble(vo.getValue(), 0.0D));
                    }
                });
            }
            // 回滚后删除备份
            redisClient.delete(key);
//            redisClient.set(key + ":bak", value);
//            redisClient.expireKey(key + ":bak", Long.valueOf(TimeUnit.DAYS.toSeconds(7L)).intValue());
        }
    }

    /**
     * 货量 期望值反计算
     *
     * @param orderBean
     */
    private void calculate(OrderBean orderBean) {
        try {
            //boolean calculateStatus = redisClient.setNX("rcs:predict:calculate:cancel:bet:" + orderBean.getOrderNo(), "1", 3 * 24 * 60 * 60L);
//            boolean calculateStatus = lockMapper.saveLock("rcs:predict:calculate:cancel:bet:" + orderBean.getOrderNo()) > 0;
//            if (calculateStatus) {
            log.info("预测数据计算 业务拒单反计算:{}", JSONObject.toJSONString(orderBean));
//                //拒单实货量 forecast等 反计算
//                predictService.calculate(orderBean, -1);
//                //原逻辑 保留计算玩法级别的forecast数据--------
//                calcOrderAdapter.calc(orderBean, -1);
            producerSendMessageUtils.sendMessage("rcs_forecast_cancel_order", orderBean);
//            } else {
//                log.error("预测数据计算 反计算订单已处理,不再做重复计算,订单号:" + orderBean.getOrderNo());
//            }
        } catch (Exception e) {
            log.error("预测数据计算 反计算订单异常,订单号{},{}", orderBean.getOrderNo(), e.getMessage(), e);
        }
    }


    private void updateOrderDetailOdds(OrderBean orderBean) {
        if (orderBean.getOddsChangeList() == null || orderBean.getOddsChangeList().size() <= NumberUtils.INTEGER_ZERO)
            return;

        for (Map<String, Object> info : orderBean.getOddsChangeList()) {
            orderDetailMapper.updateOrderDetailOdds(info);
        }
    }


    @Override
    public void sendOrderWs(OrderBean orderBean) {
        try {
            if (orderBean.getOrderStatus() == 1) {
                String reason = orderBean.getReason();
                if (StringUtils.isNotBlank(reason) &&
                        ("中场休息秒接".equals(reason) || "即将开赛秒接".equals(reason))) {
                    return;
                }
                String key = "rcs_risk_order_instant_ws:%s";
                key = String.format(key, orderBean.getOrderNo());
                redisClient.setExpiry(key, JSONObject.toJSONString(orderBean), 10 * 60L);
                log.info("即时注单ws:::{}::暂存成功", orderBean.getOrderNo());
            } else if (orderBean.getOrderStatus() == 100) {
                orderBean.setOrderStatus(1);
                log.info("即时注单ws:::{}::业务后置通过,推送开始", orderBean.getOrderNo());
            }

            if (orderBean.getVipLevel() == 1) {
                log.info("::{}::发送即时注单ws  vip跳过", orderBean.getOrderNo());
                return;
            }
            BigDecimal volume = getVolumePercentage(orderBean, true);
            if (volume.compareTo(BigDecimal.ZERO) == 0) {
                log.info("::{}::即时注单-标签货量P<0%，不显示", orderBean.getOrderNo());
                return;
            }
            Long orderAmountTotal = orderBean.getOrderAmountTotal();
            orderBean.setOrderAmountTotal(new BigDecimal(orderBean.getOrderAmountTotal()).multiply(volume).longValue());
            if (orderBean.getIsUpdateOdds() != null && orderBean.getIsUpdateOdds()) {
                producerSendMessageUtils.sendMessage(MqConstants.WS_ORDER_BET_RECORD_TOPIC, MqConstants.WS_ORDER_BET_RECORD_TAG, orderBean.getOrderNo(), orderBean);
            } else {
                RcsMonitorConsumerUtils.handleApi(RcsConstant.TAG_MQ_ORDER_INFO_WS, () -> {
                    producerSendMessageUtils.sendMessage(MqConstants.WS_ORDER_BET_RECORD_TOPIC, MqConstants.WS_ORDER_BET_RECORD_TAG, orderBean.getOrderNo(), orderBean);
                    return null;
                });
            }
            orderBean.setOrderAmountTotal(orderAmountTotal);
            log.info("::{}::发送订单到ws:{}", orderBean.getOrderNo(), orderBean);
        } catch (Exception e) {
            log.info("::{}::发送即时注单异常:{},{}", orderBean.getOrderNo(), e.getMessage(), e);
        }

    }


    @Override
    @Transactional
    public TOrder queryOrderInfo(String orderNo, OrderBean orderBeans) {
        TOrder tOrder = orderMapper.getOrderAndDetailByOrderNo(orderNo);
        if (tOrder == null) {
            log.info("::{}::OrderUpdateConsumer,没有找到订单", orderBeans.getOrderNo());
            return null;
        }
        if (orderBeans.getItems() == null || orderBeans.getItems().size() <= NumberUtils.INTEGER_ZERO) {
            List<TOrderDetail> tOrderDetails = tOrder.getOrderDetailList();
            List<OrderItem> itemList = new ArrayList<>();
            tOrderDetails.forEach(m -> {
                OrderItem orderItem = new OrderItem();
                BeanCopyUtils.copyProperties(m, orderItem);
                itemList.add(orderItem);
            });
            orderBeans.setItems(itemList);
        }
        //如果盘口位置为空 兼容处理
        for (OrderItem item : orderBeans.getItems()) {
            if (item.getPlaceNum() == null) {
                QueryWrapper<TOrderDetail> wrapper = new QueryWrapper<TOrderDetail>();
                wrapper.lambda().eq(TOrderDetail::getOrderNo, orderBeans.getOrderNo());
                wrapper.lambda().eq(TOrderDetail::getBetNo, item.getBetNo());
                TOrderDetail orderDetail = orderDetailService.getOne(wrapper);
                item.setPlaceNum(orderDetail.getPlaceNum());
            }
            Integer orderStatus;
            if (tOrderDetailExtUtils.isSaveToMongo()) {
                orderStatus = tOrderDetailExtRepository.queryOrderStatus(item.getBetNo());
            } else {
                orderStatus = tOrderDetailExtMapper.queryOrderStatus(item.getBetNo());
            }
            //暂停处理的单子PauseTime=-1 前端计数使用
            if (null != orderStatus && Arrays.asList(8, 9).contains(orderStatus)) item.setPauseTime(-1);
        }
        return tOrder;
    }

    @Override
    public TOrder getOrderInfo(String orderNo) {

        return orderMapper.getOrderAndDetailByOrderNo(orderNo);
    }


    /**
     * 获取用户货量
     *
     * @param orderBean
     * @return
     */
    @Override
    public BigDecimal getVolumePercentage(OrderBean orderBean, boolean isSave) {
        log.info("::{}::VolumePercentage 设备类型:{}", orderBean.getOrderNo(), orderBean.getDeviceType() == 1 ? "1:手机" : "2:PC");
        String volumePercentageKey = String.format("rcs:order:volumePercentage:%s", orderBean.getOrderNo());
        String volumePercentageValue = redisClient.get(volumePercentageKey);
        AmountTypeVo amountTypeVo;
        if (!StringUtils.isBlank(volumePercentageValue)) {
            log.info("::{}::getVolumePercentage,直接从redis缓存返回货量:{}", orderBean.getOrderNo(), volumePercentageValue);
            amountTypeVo = JSONObject.parseObject(volumePercentageValue, AmountTypeVo.class);
            return amountTypeVo.getVolumePercentage();
        }
        amountTypeVo = getVolumePercentage(orderBean, 1);
        redisClient.setExpiry(volumePercentageKey, JSONObject.toJSONString(amountTypeVo), 10 * 60L);
        return amountTypeVo.getVolumePercentage();
    }

    public AmountTypeVo getVolumePercentage(OrderBean orderBean, Integer version) {
        AmountTypeVo amountTypeVo = new AmountTypeVo();
        //为了避免外面逻辑报错，则设置一个默认值用于返回
        BigDecimal volumePercentage = new BigDecimal("0");
        orderBean.setSportId(orderBean.getItems().get(0).getSportId());

        // 1.优先取商户特殊货量百分比配置
        String businessBetPercent = getMerchantBetPercent(orderBean.getTenantId());

        String dynamicConfigSwitchValue = getDynamicConfigSwitchStatus();
        String sportIdsSwitchValue = getSportConfigSwitchStatus();
        String merchantSwitchValue = setBetVolumeSwitch(orderBean.getTenantId());
        String hideVolumeKey = String.format(Constants.RCS_DYNAMIC_HIDE_ORDER_RATE, orderBean.getUid(), orderBean.getSportId());
        String hideVolumeValue = redisClient.get(hideVolumeKey);
        if (StringUtils.isEmpty(hideVolumeValue)) {
            hideVolumeValue = "0";
        }
        log.info("::{}::setDynamicVolume::merchantSwitch,sportIdsSwitchValue:{},merchantSwitchValue:{},hideVolumeValue:{},dynamicConfigSwitchValue:{}",
                orderBean.getOrderNo(), sportIdsSwitchValue, merchantSwitchValue, hideVolumeValue, dynamicConfigSwitchValue);
        List<String> sportIds = Arrays.asList(sportIdsSwitchValue.split(","));
        if (StringUtils.isNotBlank(businessBetPercent) && new BigDecimal(businessBetPercent).compareTo(BigDecimal.ZERO) == 0) {
            log.info("商户特殊货量配置,orderNo:{}", orderBean.getOrderNo());
            // 最终货量
            volumePercentage = new BigDecimal(1).subtract(new BigDecimal(hideVolumeValue)).multiply(new BigDecimal(businessBetPercent));
            amountTypeVo.setVolumePercentage(volumePercentage);
            // 动态藏单比例
            amountTypeVo.setDynamicVolumePercentage(new BigDecimal(hideVolumeValue));
            // 商户藏单比例
            amountTypeVo.setMerchantVolumePercentage(new BigDecimal(1).subtract(volumePercentage).subtract(new BigDecimal(hideVolumeValue)));
            //设备藏单比例
            amountTypeVo.setEquipmentVolumePercentage(BigDecimal.ZERO);
            amountTypeVo.setSpecial(SpecialEnum.Special.getId());
            amountTypeVo.setCategory(OrderHideCategoryEnum.MERCHANT.getId());
            return amountTypeVo;
        }

        //2.优先判断特殊赛种货量百分比，有则返回，没有则往下走判断商户限额模式
        String userVolumePercentageValue = getSportUserTag(orderBean, orderBean.getSportId());
        log.info("::{}::getVolumePercentage::获取货量赛种比例参数 ,用户货量Value{},货量{},VIP类型{}", orderBean.getOrderNo(), userVolumePercentageValue, volumePercentage, orderBean.getVipLevel());
        //判断当前赛种是否有设置当前货量百分比
        if (StringUtils.isNotBlank(userVolumePercentageValue)) {
            volumePercentage = new BigDecimal(userVolumePercentageValue).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN);
            log.info("::{}::getVolumePercentage::用户货量值{} 缓存用户货量 {}", orderBean.getOrderNo(), volumePercentage, userVolumePercentageValue);
            amountTypeVo.setVolumePercentage(volumePercentage);
            amountTypeVo.setSpecial(SpecialEnum.Special.getId());
            amountTypeVo.setCategory(OrderHideCategoryEnum.USER.getId());
            return amountTypeVo;
        } else {
            Integer sportId = 0;
            //判断是否有设置全局货量百分比（全部赛种 sportId = 0）
            userVolumePercentageValue = getSportUserTag(orderBean, sportId);
            if (StringUtils.isNotBlank(userVolumePercentageValue)) {
                volumePercentage = new BigDecimal(userVolumePercentageValue).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN);
                log.info("::{}:: getVolumePercentage::获取货量返回赛种全局{}", orderBean.getOrderNo(), volumePercentage);
                amountTypeVo.setVolumePercentage(volumePercentage);
                amountTypeVo.setSpecial(SpecialEnum.Special.getId());
                amountTypeVo.setCategory(OrderHideCategoryEnum.USER.getId());
                return amountTypeVo;
            }
        }
        //用户标签货量百分比获取
        RcsLabelSportVolumePercentage labelLimitConfig = setLabelLimitConfig(orderBean.getUserTagLevel(), orderBean.getSportId());
        log.info("::{}:: getVolumePercentage userTagLevel{}sportId{}返回labelLimitConfig{}",
                orderBean.getOrderNo(), orderBean.getUserTagLevel(), orderBean.getSportId(), JSONObject.toJSONString(labelLimitConfig));
        //货量百分比 仅用于  特殊限额管控中未设置“特殊VIP限额”的用户。
        if (1 != orderBean.getVipLevel() && Objects.nonNull(labelLimitConfig) && null != labelLimitConfig.getVolumePercentage()) {
            volumePercentage = labelLimitConfig.getVolumePercentage().divide(BigDecimal.valueOf(100));
            log.info("::{}:: getVolumePercentage::获取货量返回标签{}", orderBean.getOrderNo(), volumePercentage);
            amountTypeVo.setVolumePercentage(volumePercentage);
            amountTypeVo.setSpecial(SpecialEnum.Special.getId());
            amountTypeVo.setCategory(OrderHideCategoryEnum.LABEL.getId());
            return amountTypeVo;
        }

        //3.注单金额属于藏单区间 货量=0&藏单类型=金额区间
        //注单金额
        Long productAmountTotal = orderBean.getProductAmountTotal();
        //获取藏单金额配置
        String hideConfigDTO = null;

        hideConfigDTO = RcsLocalCacheUtils.getValue(RedisKey.REDIS_HIDE_RANGE_CONFIG, RedisKey.getCacheKey(orderBean.getTenantId() + "", orderBean.getSportId()), redisClient::hGet);
        if (StringUtils.isBlank(hideConfigDTO)) {
            RcsMerchantsHideRangeConfig one = rcsMerchantsHideRangeConfigService.getOne(Wrappers.<RcsMerchantsHideRangeConfig>lambdaQuery().eq(RcsMerchantsHideRangeConfig::getMerchantsId, orderBean.getTenantId())
                    .eq(RcsMerchantsHideRangeConfig::getSportId, orderBean.getSportId()));
            if (Objects.nonNull(one)) {
                redisClient.hSet(RedisKey.REDIS_HIDE_RANGE_CONFIG, RedisKey.getCacheKey(one.getMerchantsId() + "", one.getSportId()), JSON.toJSONString(one));
                //存2小时
                redisClient.expireKey(RedisKey.REDIS_HIDE_RANGE_CONFIG, 15 * 24 * 60 * 60);
                hideConfigDTO = JSON.toJSONString(one);
            }
        }
        log.info("::{}:: getHideList::获取货量返回藏单金额配置{},下注金额:{},赛种:{}", orderBean.getOrderNo(), JSON.toJSONString(hideConfigDTO), productAmountTotal, orderBean.getSportId());
        if (StringUtils.isNotBlank(hideConfigDTO)) {
            RcsMerchantsHideRangeConfig rcsMerchantsHideRangeConfig = JSONObject.parseObject(hideConfigDTO, RcsMerchantsHideRangeConfig.class);
            Integer hideStatus = rcsMerchantsHideRangeConfig.getStatus();
            BigDecimal hideAmount = new BigDecimal(rcsMerchantsHideRangeConfig.getHideMoney());
            //判断开关为开 并且 在藏单配置区间
            if (hideStatus.compareTo(SwitchEnum.OPEN.getId()) == 0 && BigDecimal.valueOf(productAmountTotal).compareTo(hideAmount.multiply(BigDecimal.valueOf(100))) <= 0) {
                amountTypeVo.setVolumePercentage(new BigDecimal("0.001"));
                amountTypeVo.setSpecial(SpecialEnum.ordinary.getId());
                amountTypeVo.setCategory(OrderHideCategoryEnum.AMOUNT.getId());
                log.info("::{}:: getHideList::获取返回货量值{}", orderBean.getOrderNo(), amountTypeVo.getVolumePercentage().toString());
                return amountTypeVo;
            }
        }

        //4.动态货量开关为开 货量百分比=1-动态藏单比例 藏单类型=动态藏单
        if (StringUtils.isNotBlank(dynamicConfigSwitchValue) && sportIds.contains(orderBean.getSportId().toString())
                && "1".equals(dynamicConfigSwitchValue) && "1".equals(merchantSwitchValue)) {
            volumePercentage = BigDecimal.ONE.subtract(new BigDecimal(hideVolumeValue));
            amountTypeVo.setVolumePercentage(volumePercentage);
            amountTypeVo.setSpecial(SpecialEnum.Special.getId());
            amountTypeVo.setCategory(OrderHideCategoryEnum.DYNAMIC.getId());
        } else {
            //5.兜底逻辑  藏单类型=设备
            if (StringUtils.isEmpty(businessBetPercent)) {
                businessBetPercent = "0";
            } else {
                businessBetPercent = "1";
            }
            // 最终货量
            volumePercentage = new BigDecimal(businessBetPercent).multiply(getMinVolumeByDeviceType(orderBean.getDeviceType()));
            amountTypeVo.setVolumePercentage(volumePercentage);
            // 动态藏单比例
            amountTypeVo.setDynamicVolumePercentage(BigDecimal.ZERO);
            //设备藏单比例
            BigDecimal subtract = new BigDecimal(1).subtract(new BigDecimal(businessBetPercent));
            amountTypeVo.setEquipmentVolumePercentage(new BigDecimal(1).subtract(volumePercentage).subtract(subtract));
            //商户藏单比例
            amountTypeVo.setMerchantVolumePercentage(subtract);
            amountTypeVo.setSpecial(SpecialEnum.Special.getId());
            amountTypeVo.setCategory(OrderHideCategoryEnum.EQUIPMENT.getId());
        }


        log.info("::{}::getVolumePercentage::获取货量返回{}", orderBean.getOrderNo(), JSONObject.toJSONString(amountTypeVo));
        return amountTypeVo;
    }


    /**
     * 特殊用户货量设置
     */
    private String getSportUserTag(OrderBean orderBean, Integer sportId) {
        String userVolumePercentageKey = RcsConstant.RCS_TRADE_USER_SPORT_BET_LIMIT_CONFIG + orderBean.getUid();
        String sSportId = sportId.toString();
        String userVolumePercentageValue = redisClient.hGet(userVolumePercentageKey, sSportId);
        if (StringUtils.isEmpty(userVolumePercentageValue)) {
            TUserBetRate userBetRate = getUserLabelLimitConfig(orderBean.getUid(), orderBean.getSportId());
            if (Objects.nonNull(userBetRate)) {
                redisClient.hSet(userVolumePercentageKey, sSportId, userBetRate.getBetRate().toString());
                userVolumePercentageValue = userBetRate.getBetRate().toString();
            }
        }
        return userVolumePercentageValue;
    }

    /**
     * 用户特殊货量
     */
    private TUserBetRate getUserLabelLimitConfig(Long userId, Integer sportId) {
        LambdaQueryWrapper<TUserBetRate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TUserBetRate::getUserId, userId);
        wrapper.eq(TUserBetRate::getSportId, sportId);
        return tUserBetRateMapper.selectOne(wrapper);
    }

    /**
     * 获取动态球种配置
     */
    private String getSportConfigSwitchStatus() {
        String sportConfigSwitchValue = redisClient.hGet(RISK_DYNAMIC_CONFIG_SWITCH, CONFIG_SPORTIDS);
        if (StringUtils.isEmpty(sportConfigSwitchValue)) {
            List<RcsMerchantCommonConfig> rcsMerchantCommonConfigList = rcsMerchantCommonConfigService.list(new LambdaQueryWrapper<>());
            if (CollectionUtils.isNotEmpty(rcsMerchantCommonConfigList)) {
                RcsMerchantCommonConfig rcsMerchantCommonConfig = rcsMerchantCommonConfigList.get(0);
                redisClient.hSet(RISK_DYNAMIC_CONFIG_SWITCH, CONFIG_SPORTIDS, rcsMerchantCommonConfig.getSportIds());
                sportConfigSwitchValue = rcsMerchantCommonConfig.getSportIds();
//               RedisCacheSyncBean syncBean = RedisCacheSyncBean.build(RISK_DYNAMIC_CONFIG_SWITCH,CONFIG_SPORTIDS,rcsMerchantCommonConfig.getSportIds(),TIMES_REDIS);
//               producerSendMessageUtils.sendMessage("RCS_TRADE_REDIS_CACHE_SYNC", null, RISK_DYNAMIC_CONFIG_SWITCH, syncBean);
            }
        }
        return sportConfigSwitchValue;
    }

    /**
     * 获取配置状态
     */
    private String getDynamicConfigSwitchStatus() {
        String dynamicConfigSwitchValue = redisClient.hGet(RISK_DYNAMIC_CONFIG_SWITCH, CONFIG_STATUS);
        if (StringUtils.isEmpty(dynamicConfigSwitchValue)) {
            List<RcsMerchantCommonConfig> rcsMerchantCommonConfigList = rcsMerchantCommonConfigService.list(new LambdaQueryWrapper<>());
            if (CollectionUtils.isNotEmpty(rcsMerchantCommonConfigList)) {
                RcsMerchantCommonConfig rcsMerchantCommonConfig = rcsMerchantCommonConfigList.get(0);
                redisClient.hSet(RISK_DYNAMIC_CONFIG_SWITCH, CONFIG_STATUS, rcsMerchantCommonConfig.getBetVolumeStatus().toString());
                dynamicConfigSwitchValue = rcsMerchantCommonConfig.getBetVolumeStatus().toString();
//                RedisCacheSyncBean syncBean =  RedisCacheSyncBean.build(RISK_DYNAMIC_CONFIG_SWITCH,CONFIG_STATUS,rcsMerchantCommonConfig.getBetVolumeStatus().toString(),TIMES_REDIS);
//                producerSendMessageUtils.sendMessage("RCS_TRADE_REDIS_CACHE_SYNC", null, RISK_DYNAMIC_CONFIG_SWITCH, syncBean);
            }
        }
        return dynamicConfigSwitchValue;
    }

    /**
     * 商户货量百分比
     */
    private String getMerchantBetPercent(Long businessID) {
        String businessBetPercentKey = RcsConstant.RCS_TRADE_BUSINESS_BET_PERCENT + businessID.toString();
        String businessBetPercent = redisClient.get(businessBetPercentKey);
        if (StringUtils.isEmpty(businessBetPercent)) {
            RcsQuotaBusinessLimit businessLimit = rcsQuotaBusinessLimitService.getByBusinessId(businessID.toString());
            if (Objects.nonNull(businessLimit) && null != businessLimit.getBusinessBetPercent()) {
                redisClient.set(businessBetPercentKey, businessLimit.getBusinessBetPercent().toString());
                businessBetPercent = businessLimit.getBusinessBetPercent().toString();
            }
        }
        return businessBetPercent;
    }

    /**
     * 投注货量状态开关
     */
    private String setBetVolumeSwitch(Long businessID) {
        String merchantSwitchKey = Constants.RCS_RISK_BET_VOLUME_SWITCH + businessID;
        String merchantSwitchValue = redisClient.get(merchantSwitchKey);
        if (StringUtils.isEmpty(merchantSwitchValue)) {
            RcsQuotaBusinessLimit businessLimit = rcsQuotaBusinessLimitService.getByBusinessId(businessID.toString());
            if (Objects.nonNull(businessLimit) && null != businessLimit.getBetVolumeStatus()) {
                redisClient.set(merchantSwitchKey, businessLimit.getBetVolumeStatus());
                merchantSwitchValue = businessLimit.getBetVolumeStatus().toString();
            }
        }
        return merchantSwitchValue;
    }

    /**
     * 标签货量设置
     */
    public RcsLabelSportVolumePercentage setLabelLimitConfig(Integer userTagLevel, Integer sportId) {
        String userTagLevelKey = String.format("risk:user:tag:level:%s", userTagLevel.toString() + sportId.toString());
        String userTagLevelValue = redisClient.get(userTagLevelKey);
        if (!StringUtils.isBlank(userTagLevelValue)) {
            return JSONObject.parseObject(userTagLevelValue, RcsLabelSportVolumePercentage.class);
        }
        RcsLabelSportVolumePercentage rcsLabelSportVolumePercentage = getLabelLimitConfig(userTagLevel, sportId);
        if (Objects.isNull(rcsLabelSportVolumePercentage)) {
            rcsLabelSportVolumePercentage = getLabelLimitConfig(userTagLevel, 0);
            //在切换全部赛种配置时，trade服务负责删除指定赛种的配置，让这边一直走sportId=0的配置，知道指定赛种重新被配置
            userTagLevelKey = String.format("risk:user:tag:level:%s", userTagLevel.toString() + 0);
        }
        if (Objects.nonNull(rcsLabelSportVolumePercentage)) {
            redisClient.setExpiry(userTagLevelKey, JSONObject.toJSONString(rcsLabelSportVolumePercentage), 30 * 60L);
        }
        return rcsLabelSportVolumePercentage;
    }

    /**
     * 查询标签货量
     */
    private RcsLabelSportVolumePercentage getLabelLimitConfig(Integer userTagLevel, Integer sportId) {
        LambdaQueryWrapper<RcsLabelSportVolumePercentage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsLabelSportVolumePercentage::getTagId, userTagLevel);
        wrapper.eq(RcsLabelSportVolumePercentage::getSportId, sportId);
        return labelSportVolumePercentageMapper.selectOne(wrapper);
    }

    public BigDecimal getMinVolumeByDeviceType(Integer deviceType) {
        BigDecimal volumePercentage;
        switch (deviceType) {
            case 1:  // 1h5
                volumePercentage = new BigDecimal(refreshScopeNacosConfig.getDeviceVolumeH5());
                break;
            case 2: // 2pc
                volumePercentage = new BigDecimal(refreshScopeNacosConfig.getDeviceVolumePc());
                break;
            default: // 3app
                volumePercentage = new BigDecimal(refreshScopeNacosConfig.getDeviceVolumeApp());
                break;
        }
        return volumePercentage;
    }


    String getMatchPeriod(OrderItem orderItem) {
        String periodRediskey = String.format(RCS_DATA_KEYCACHE_MATCHTEMPINFO, orderItem.getMatchId());
        String period = redisClient.hGet(periodRediskey, "period");
        log.info("::{}::赛事阶段缓存periodRediskey :{},value={}", orderItem.getOrderNo(), periodRediskey, period);
        return period;
    }

}
