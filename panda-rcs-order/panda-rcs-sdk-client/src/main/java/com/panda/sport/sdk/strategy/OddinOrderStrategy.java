package com.panda.sport.sdk.strategy;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.third.OddinApiService;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.data.rcs.dto.oddin.OddinOrderInfoDto;
import com.panda.sport.data.rcs.dto.oddin.TicketDto;
import com.panda.sport.data.rcs.dto.oddin.entity.Bet;
import com.panda.sport.data.rcs.dto.oddin.entity.BetStake;
import com.panda.sport.data.rcs.dto.oddin.entity.TicketCustomer;
import com.panda.sport.data.rcs.dto.oddin.entity.TicketSelection;
import com.panda.sport.data.rcs.vo.oddin.*;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.enums.OrderTypeEnum;
import com.panda.sport.rcs.enums.oddin.AcceptOddsChangeEnum;
import com.panda.sport.rcs.enums.oddin.BetStakeTypeEnum;
import com.panda.sport.rcs.enums.oddin.TicketChannelEnum;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.panda.sport.sdk.constant.LimitRedisKeys;
import com.panda.sport.sdk.constant.RcsCacheContant;
import com.panda.sport.sdk.constant.SdkConstants;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.mapper.StandardSportMarketOddsMapper;
import com.panda.sport.sdk.mq.Producer;
import com.panda.sport.sdk.sdkenum.SeriesEnum;
import com.panda.sport.sdk.service.impl.*;
import com.panda.sport.sdk.service.impl.matrix.MatrixAdapter;
import com.panda.sport.sdk.util.GuiceContext;
import com.panda.sport.sdk.util.RcsLocalCacheUtils;
import com.panda.sport.sdk.vo.MatrixForecastVo;
import com.panda.sport.sdk.vo.RcsBusinessPlayPaidConfigVo;
import com.panda.sport.sdk.vo.StandardSportMarketOdds;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.cache.CaCheKeyConstants.ODDIN_ORDER_INFO_KEY;

/**
 * @author conway
 * @date 2023/5/17 11:40
 * @description oddin赛事策略
 */
@Singleton
public class OddinOrderStrategy implements IOrderStrategy {

    private static final Logger logger = LoggerFactory.getLogger(OddinOrderStrategy.class);

    public static final String STANDAR_MATCH_MARKET_INFO_OF_ODDIN = "rcs:redis:standard:match:market:oddin:%s";
    public static final String RCS_RISK_ODDIN_TICKET = "rcs_risk_oddin_ticket";
    public static final Integer ODDIN_ORDER_INFO_CACHE_TIME = 6 * 60;
    @Inject
    JedisClusterServer jedisClusterServer;
    @Inject
    LimitConfigService limitConfigService;

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
    @Inject
    OddinApiService oddinApiService;


    /**
     * 获取最大额度
     */
    @Override
    public List<RcsBusinessPlayPaidConfigVo> getMaxBetAmount(List<ExtendBean> extendBeanList, OrderBean orderBean) {
        Integer seriesType = orderBean.getSeriesType();
        //单关
        if (seriesType == 1) {
            return getSingleMaxBetAmount(extendBeanList, orderBean);
        } else {//串关
            logger.error("::目前只支持单关注单::-orderNo：{}-seriesType:{}-orderBean:{}", orderBean.getOrderNo(), orderBean.getSeriesType(), orderBean);
            throw new RcsServiceException("OD目前只支持单关注单");
        }

    }

    /**
     * 单关限额
     *
     * @param extendBeanList 订单列表
     * @param orderBean      主订单
     * @return 限额响应
     */
    private List<RcsBusinessPlayPaidConfigVo> getSingleMaxBetAmount(List<ExtendBean> extendBeanList, OrderBean orderBean) {
        logger.info("::{}::单关限额方法", orderBean.getItems().get(0).getDataSourceCode());
        //获取用户ID
        String userId = orderBean.getUid().toString();
        String type = getUserType(userId);
        //根据赛事判断 走哪方数据商
        String thirdDataSource = orderBean.getItems().get(0).getDataSourceCode();
        //是否单关合并 0否 1是
        RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
        List<RcsBusinessPlayPaidConfigVo> list = new ArrayList<>();
        Long amount = 0L;
        Long paMaxBet = 0L;
        amount = getThirdMaxBet(extendBeanList, orderBean, userId, thirdDataSource);
        logger.info("::{}::额度查询-获取{}单注最大限额:{}", userId, thirdDataSource, amount);
        vo.setOrderMaxPay(amount);
        vo.setMinBet(0L);
        //冠军盘不往下走
        if (orderBean.getItems().get(0).getMatchType() == 3) {
            list.add(vo);
            return list;
        }
        //2.获取pa单关限额
        IOrderStrategy orderStrategy;
        if (StringUtils.isNotBlank(type) && "4".equals(type)) {
            orderStrategy = GuiceContext.getInstance(SpecialVipStrategy.class);
        } else {
            orderStrategy = GuiceContext.getInstance(RiskOrderV3Strategy.class);
        }
        List<RcsBusinessPlayPaidConfigVo> paMaxBetAmountList = orderStrategy.getMaxBetAmount(extendBeanList, orderBean);
        paMaxBet = paMaxBetAmountList.get(0).getOrderMaxPay();
        logger.info("::{}::额度查询-获取pa单注最大限额：{},策略：{}", userId, paMaxBet, orderStrategy);
        //3.pa与oddin取小
        logger.info("======pa与oddin限额比较取小值====pa::{}::_oddin::{}::", paMaxBet, vo.getOrderMaxPay());
        if (paMaxBet < vo.getOrderMaxPay()) {
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
        //串关数 如  2001 中的2  40011中的4  10001013中的10
        int seriesNum = Objects.requireNonNull(SeriesEnum.getSeriesEnumBySeriesJoin(seriesType)).getSeriesNum();
        List<RcsBusinessPlayPaidConfigVo> paSeriesBetList;
        if (StringUtils.isNotBlank(type) && "4".equals(type)) {
            paSeriesBetList = seriesLimitService.queryMaxBetMoneyBySelectSpecialVip(orderBean);
        } else {
            RcsQuotaBusinessLimitResVo businessLimit = limitConfigService.getBusinessLimit(orderBean.getTenantId());
            paSeriesBetList = seriesLimitService.queryMaxBetMoneyBySelect(orderBean, true, businessLimit);
        }
        //按串关类型分组
        Map<String, RcsBusinessPlayPaidConfigVo> paLimitMap = paSeriesBetList.stream().collect(Collectors.toMap(RcsBusinessPlayPaidConfigVo::getType, Function.identity()));
        Long amount;
        //获取所有N串1的限额
        RcsBusinessPlayPaidConfigVo vo;
        //响应对象
        List<RcsBusinessPlayPaidConfigVo> list = new ArrayList<>();
        //根据赛事判断 走哪方数据商
        String dataSourceCode = orderBean.getItems().get(0).getDataSourceCode();
        /*for (int i = 2; i <= seriesNum; i++) {
            amount = getThirdMaxBet(extendBeanList);
            logger.info("::{}::获取{}-{}001最大限额:{}", userId, thirdDataSource, i, amount);
            vo = new RcsBusinessPlayPaidConfigVo();
            vo.setMinBet(0L);
            vo.setOrderMaxPay(amount);
            vo.setType(i + "001");
            list.add(vo);
        }*/
        //如果>2 ,则存在N串M ,获取N串M的限额 如3串4 - 3004
       /* if (seriesNum > 2) {
            amount = getThirdMaxBet(extendBeanList, seriesNum, true, userId, thirdDataSource);
            logger.info("::{}::获取{}-{}最大限额:{}", userId, thirdDataSource, seriesType, amount);
            vo = new RcsBusinessPlayPaidConfigVo();
            vo.setMinBet(0L);
            vo.setOrderMaxPay(amount);
            vo.setType(seriesType.toString());
            list.add(vo);
        }*/
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

        if (orderBean.getSeriesType() != 1) {
            logger.error("::目前只支持单关注单::-orderNo：{}-seriesType:{}-orderBean:{}", orderBean.getOrderNo(), orderBean.getSeriesType(), orderBean);
            throw new RcsServiceException("OD目前只支持单关注单");
        }
        //获取商户限额配置 test环境用
        RcsQuotaBusinessLimitResVo businessLimit = limitConfigService.getBusinessLimit(orderBean.getTenantId());

        Map<String, Object> resultMap = new HashMap<>();
        //非冠军盘 需要pa校验额度

        //根据赛事判断 走哪方数据商
//        String dataSourceCode = orderBean.getExtendBean().getItemBean().getDataSourceCode();
        String dataSourceCode = orderBean.getItems().get(0).getDataSourceCode();
        //订单入库
        oddinSaveOrder(orderBean, businessLimit, dataSourceCode);
        //请求oddin接口
        TicketDto dto = new TicketDto();
        //组装参数
        getTicketDto(dto, orderBean);
        //异步由oddIn处理不要阻塞主流程
        producer.sendMsg(RCS_RISK_ODDIN_TICKET, "rcs_risk_oddin_ticket_group", dto.getId(), JSONObject.toJSONString(dto), dto.getId());
//        producer.sendMsg("rcs_risk_oddin_order", "ODDIN_SAVE_ORDER", orderBean.getOrderNo(), JSONObject.toJSONString(request), orderBean.getOrderNo());
        /**
         * todo
         * 需要把注单数据通过mq发送到大数据提供的mq
         */
        resultMap.put("status", 2);
        resultMap.put("infoStatus", OrderInfoStatusEnum.MTS_PROCESSING.getCode());
        resultMap.put("infoMsg", dataSourceCode + "处理中");
        resultMap.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
        resultMap.put(orderBean.getOrderNo(), true);
        return resultMap;
    }

    private void getTicketDto(TicketDto ticketDto, OrderBean orderBean) {


        //将订单中的一些注单后需要用到的数据缓存起来
        setOrderInfo2Cache(orderBean);
        //注单ID
        ticketDto.setId(orderBean.getOrderNo());
        //数据源
        ticketDto.setSourceId(2);
        //订单分组
        ticketDto.setOrderGroup(orderBean.getOrderGroup());
        //盘口类型 1:早盘 2:滚球 3:冠军盘
        ticketDto.setMatchType(orderBean.getItems().get(0).getMatchType());
        //赛事编号
        ticketDto.setMatchId(orderBean.getItems().get(0).getMatchId());
        //盘口ID
        ticketDto.setMarketId(orderBean.getItems().get(0).getMarketId());
        //请求限额,投注的UTC时间
        Timestamp d = new Timestamp(System.currentTimeMillis());
        ticketDto.setTimestamp(d);
        //目前只支持单关传1
        ticketDto.setTotalCombinations(1);
        //支持语言
        ticketDto.setCurrency(orderBean.getCurrencyCode());
        //自动接受赔率的变化
        int acceptOdds = 0;
        if (1 == orderBean.getAcceptOdds()) {
            acceptOdds = 3;
        } else if (2 == orderBean.getAcceptOdds()) {
            acceptOdds = 2;
        } else if (3 == orderBean.getAcceptOdds()) {
            acceptOdds = 1;
        }
        ticketDto.setAccept_odds_change(AcceptOddsChangeEnum.getEnum(acceptOdds));
        //来源渠道
        ticketDto.setChannel(TicketChannelEnum.getEnum(orderBean.getChannelCode()));
        //用户信息
        TicketCustomer customer = new TicketCustomer();
        //用户ID
        customer.setId(orderBean.getUid().toString());
        //支持的语言
        customer.setLanguage("zh");

        //注单列表
        Bet bet = new Bet();
        List<Bet> betList = new ArrayList<>();
        //投注实体
        BetStake betStake = new BetStake();
        //投注金额
        betStake.setValue(new BigDecimal(orderBean.getOrderAmountTotal()).divide(new BigDecimal("100")).intValue());
        //股权类型
        betStake.setType(BetStakeTypeEnum.BET_STAKE_TYPE_SUM);

        Integer[] systems = new Integer[]{1};
        //投注数组
        bet.setSystems(systems);
        //获取selectionID
        String selectionId = getSelectionId(orderBean);
        bet.setSelections(selectionId);
        //投注本金
        bet.setStake(betStake);
        betList.add(bet);
        TicketSelection selection = new TicketSelection();
        selection.setId(selectionId);
        selection.setForeign(false);
        //赔率
        selection.setOdds(orderBean.getExtendBean().getOdds());
        Map<String, TicketSelection> selectionsMap = new HashMap<>();
        selectionsMap.put(selection.getId(), selection);
        //赋值选集
        ticketDto.setSelections(selectionsMap);
        //赋值用户信息
        ticketDto.setCustomer(customer);
        //赋值投注信息
        ticketDto.setBets(betList);
        //商户ID
        ticketDto.setLocation_id(orderBean.getTenantId());
    }

    /**
     * 将女足订单中投注后需要用到的数据缓存起来，用的时候直接用，不需要去数据库查询
     *
     * @param orderBean
     */
    private void setOrderInfo2Cache(OrderBean orderBean) {
        OddinOrderInfoDto dto = new OddinOrderInfoDto();
        //将注单用户的二级标签id list存入缓存
        List<String> userSecondLabelIdsList = orderBean.getSecondaryLabelIdsList();
        if (CollectionUtils.isNotEmpty(userSecondLabelIdsList)) {
            dto.setUserSecondLabelIdsList(userSecondLabelIdsList);
        }

        //是否是滚球
        boolean isScrollOrder = false;
        List<OrderItem> extList = orderBean.getItems();
        if (CollectionUtils.isNotEmpty(extList)) {
            for (OrderItem item : extList) {
                if ("2".equals(item.getMatchType())) {
                    isScrollOrder = true;
                    break;
                }
            }
        }
        dto.setScrollOrder(isScrollOrder);
        //用户组
        dto.setOrderGroup(orderBean.getOrderGroup());
        //盘口id
        dto.setMarketId(orderBean.getItems().get(0).getMarketId());
        //赛事id
        dto.setMatchId(orderBean.getItems().get(0).getMatchId());
        //赛事类型
        dto.setMatchType(orderBean.getItems().get(0).getMatchType());
        //注单对象
        dto.setOrderBean(orderBean);
        //全局唯一链路id
        String cacheKey = String.format(ODDIN_ORDER_INFO_KEY, orderBean.getOrderNo());
        jedisClusterServer.setex(cacheKey, ODDIN_ORDER_INFO_CACHE_TIME, JSONObject.toJSONString(dto));
    }

    /**
     * 从盘口表获取extraInfo值作为selectionId
     *
     * @param orderBean
     * @return selectionId
     */
    private String getSelectionId(OrderBean orderBean) {
        String selectionId = null;
        try {
            String matchId = orderBean.getItems().get(0).getMatchId().toString();
            Long marketId = orderBean.getItems().get(0).getMarketId();
            Long playOptionsId = orderBean.getItems().get(0).getPlayOptionsId();
            String matchKey = String.format(STANDAR_MATCH_MARKET_INFO_OF_ODDIN, matchId);
            String matchInfoStr = jedisClusterServer.get(matchKey);
            if (StringUtils.isNotBlank(matchInfoStr)) {
                logger.info("::{}::matchId:{}::key:{}:获取赛事数据为::{}", orderBean.getOrderNo(), matchId, matchKey, JSONObject.toJSONString(matchInfoStr));
                StandardMatchVo standardMatchMessage = JSON.parseObject(matchInfoStr, StandardMatchVo.class);
                if (standardMatchMessage != null) {
                    //获取所有的盘口列表
                    List<StandardMarketVo> marketList = standardMatchMessage.getMarketList();
                    if (CollectionUtils.isNotEmpty(marketList)) {
                        for (StandardMarketVo marketVo : marketList) {
                            //只有当盘口ID相同并且投注项ID是匹配的只能获取到对应投注项的selectionID
                            if (marketVo.getMarketId().equals(marketId)) {
                                //获取盘口下多个投注项的数据
                                List<StandardMarketOddsVo> oddsVoList = marketVo.getMarketOddsList();
                                if (CollectionUtils.isNotEmpty(oddsVoList)) {
                                    //获取每个投注项下的selectionId
                                    for (StandardMarketOddsVo oddsVo : oddsVoList) {
                                        if (oddsVo.getId().equals(playOptionsId)) {
                                            selectionId = oddsVo.getExtraInfo();
                                            logger.info("::从缓存获取selectionId::orderNo::{}::matchId:{}::marketId::{}::selectionId::{}", orderBean.getOrderNo(), matchId, marketId, selectionId);
                                            return selectionId;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //缓存中没有数据根据投注项ID从数据库中查
            StandardSportMarketOddsMapper standardSportMarketOddsMapper = SpringContextUtils.getBeanByClass(StandardSportMarketOddsMapper.class);
            StandardSportMarketOdds standardSportMarketOdds = standardSportMarketOddsMapper.selectById(orderBean.getItems().get(0).getPlayOptionsId());
            if (ObjectUtil.isNotEmpty(standardSportMarketOdds)) {
                selectionId = standardSportMarketOdds.getExtraInfo();
            }
            if (StringUtils.isBlank(selectionId)) {
                logger.error("orderNo：{}-playOptionId:{}获取盘口数据中的ExtraInfo数据为空", orderBean.getOrderNo(), orderBean.getItems().get(0).getPlayOptionsId());
                throw new RcsServiceException("selectionId为空");
            }
        } catch (Exception e) {
            logger.error("orderNo：{}-orderBean:{}获取盘口数据出错,异常信息:{}", orderBean.getOrderNo(), orderBean, e.getStackTrace());
        }
        return selectionId;
    }

    /**
     * 订单处理
     * 订单校验状态 ValidateResult   0:待处理 1：成功  2：失败 3：已取消
     * 订单处理状态 OrderStatus   0 待处理  1：成功  2：拒绝
     * 订单结果标识 InfoStatus    0：待处理 1：赛前订单接单通过 2：赛前订单拒单（需要备注原因）  3：MTS接单 4：MTS拒单 5：业务拒单 6：接拒单处理中 7：MTS处理中  8：接拒单成功 9：接拒单拒绝 10:滚球手工接拒单-接单 11:滚球手工接拒单-拒单 12：一键秒接 13：操盘取消注单
     */
    private void oddinSaveOrder(OrderBean orderBean, RcsQuotaBusinessLimitResVo businessLimit, String dataSourceCode) {
        //单关限额
        List<ExtendBean> list = new ArrayList<>();
        orderBean.getExtendBean().setValidateResult(1);
        orderBean.getExtendBean().setRecVal(null);
        orderBean.getExtendBean().getItemBean().setRecVal(null);
        list.add(orderBean.getExtendBean());
        orderBean.setValidateResult(1);
        orderBean.setOrderStatus(0);
        orderBean.setInfoStatus(OrderInfoStatusEnum.OD_PROCESSING.getCode());

        //发送到风控topic入库
        producer.sendMsg(MqConstants.RCS_ORDER_UPDATE, dataSourceCode, orderBean.getOrderNo(), JSONObject.toJSONString(orderBean), orderBean.getOrderNo());

//        //发送订单到third消费
//        Map<String, Object> map = new HashMap<>();
//        map.put("linkId", MDC.get("linkId"));
//        map.put("merchantCode", businessLimit.getParentName());
//        map.put("third", dataSourceCode);
//        map.put("list", list);
//        map.put("paTotalAmount", orderBean.getProductAmountTotal());
//        //1：自动接收更好的赔率 2：自动接受任何赔率变动 3：不自动接受赔率变动
//        map.put("acceptOdds", orderBean.getAcceptOdds());
//        map.put("deviceType", orderBean.getDeviceType());
//        map.put("currency", orderBean.getCurrencyCode());
//        map.put("seriesType", orderBean.getExtendBean().getSeriesType());
//        map.put("ip", orderBean.getIp());
//        producer.sendMsg("rcs_risk_third_order", dataSourceCode + "_SAVE_ORDER", orderBean.getOrderNo(), JSONObject.toJSONString(map), orderBean.getOrderNo());
    }

    private String getMtsCacheTag() {
        return RcsCacheContant.MTS_CIRCUIT_TAG_CACHE.get(RcsCacheContant.MTS_CIRCUIT_TAG, cacheKey -> {
            String cacheVal = jedisClusterServer.get(cacheKey);
            if (com.panda.sport.rcs.utils.StringUtils.isBlank(cacheVal)) {
                cacheVal = "0";
            }
            return cacheVal;
        });
    }

    /**
     * 获取第三方最大限额
     */
    private Long getThirdMaxBet(List<ExtendBean> extendBeanList, OrderBean orderBean, String userId, String thirdDataSource) {
        Request<TicketDto> request = new Request<>();
        TicketDto ticketDto = parameterConversion(extendBeanList, orderBean);
        request.setGlobalId(MDC.get("X-B3-TraceId"));
        //设定限额响应的UID
        ticketDto.setId(request.getGlobalId());
        request.setData((ticketDto));
        Long maxCount = 0L;
        //1.获取third单注限额
        ResponseReoffer reoffer = new ResponseReoffer();
        try {
            Response<TicketStateVo> response = oddinApiService.getMaxBetAmount(request);
            System.out.println(response.getData());
            TicketStateVo ticketStateVo = response.getData();
            if (MapUtils.isNotEmpty(ticketStateVo.getBet_info())) {
                for (Map.Entry<String, TicketResponseBetInfo> entry : ticketStateVo.getBet_info().entrySet()) {
                    reoffer.setStake(entry.getValue().getReoffer().getStake());
                    float floatValue = reoffer.getStake();
                    maxCount = (long) floatValue;
                }
                return maxCount;
            }
        } catch (Exception e) {
            logger.error("::{}::额度查询-获取{}单注最大限额异常:{}", e.getMessage(), thirdDataSource, e.getStackTrace());
        }
        return 2000L;

    }

    private TicketDto parameterConversion(List<ExtendBean> extendBeanList, OrderBean orderBean) {
        TicketDto ticketDto = new TicketDto();
       /* //注单ID
        ticketDto.setId(orderBean.getOrderGroup());*/
        //2代表得是体育得限额请求
        ticketDto.setSourceId(2);
        Timestamp d = new Timestamp(System.currentTimeMillis());
        //请求限额的时间
        ticketDto.setTimestamp(d);
        ticketDto.setTotalCombinations(1);
        //币种
        ticketDto.setCurrency("CNY");
        //自动接受赔率的变化
        ticketDto.setAccept_odds_change(AcceptOddsChangeEnum.ACCEPT_ODDS_CHANGE_ANY);
        //来源渠道
        ticketDto.setChannel(TicketChannelEnum.getEnum(orderBean.getDeviceType()));
        TicketCustomer ticketCustomer = new TicketCustomer();
        //获取用户ID
        ticketCustomer.setId(orderBean.getUid().toString());
        //用户IP地址
        ticketCustomer.setIp(orderBean.getIp());
        //支持的语言
        ticketCustomer.setLanguage("zh");
        //设备类型
        ticketCustomer.setDevice_id(orderBean.getDeviceType().toString());
        //用户名字
        ticketCustomer.setNickname(orderBean.getUsername());
        //注单列表
        List<Bet> betList = new ArrayList<>();
        Integer[] systems = new Integer[]{1};
        //先直接获取数据支撑mq下发的Seletion ID,没有再查数据库
        String selectionId = getSelectionId(orderBean);
        TicketSelection selection = null;
        for (ExtendBean e : extendBeanList) {
            Bet bet = new Bet();
            BetStake betStake = new BetStake();
            betStake.setValue(Math.toIntExact(e.getOrderMoney()));
            betStake.setType(BetStakeTypeEnum.BET_STAKE_TYPE_SUM);
            bet.setSystems(systems);
            bet.setSelections(selectionId);
            bet.setStake(betStake);
            betList.add(bet);
            selection = new TicketSelection();
            selection.setId(selectionId);
            selection.setForeign(false);
            selection.setOdds(e.getOdds());
        }
        Map<String, TicketSelection> selectionsMap = new HashMap<>();
        selectionsMap.put(selection.getId(), selection);
        ticketDto.setSelections(selectionsMap);
        //赋值用户信息
        ticketDto.setCustomer(ticketCustomer);
        //赋值投注信息
        ticketDto.setBets(betList);
        logger.info("请求oddin参数组装{}", ticketDto);
        return ticketDto;
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
        return OrderTypeEnum.ODDIN.getValue();
    }

}
