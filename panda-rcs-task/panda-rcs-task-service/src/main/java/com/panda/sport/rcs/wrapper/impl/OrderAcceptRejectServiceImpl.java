package com.panda.sport.rcs.wrapper.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderDetailPO;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.enums.OrderStatusEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.StandardSportMarketCategoryMapper;
import com.panda.sport.rcs.mapper.TOrderDetailExtMapper;
import com.panda.sport.rcs.mapper.TOrderMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.pojo.odd.StandardMarketMessage;
import com.panda.sport.rcs.pojo.odd.StandardMarketOddsMessage;
import com.panda.sport.rcs.pojo.odd.StandardMatchMessage;
import com.panda.sport.rcs.repository.TOrderDetailExtRepository;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import com.panda.sport.rcs.utils.TOrderDetailExtUtils;
import com.panda.sport.rcs.vo.ErrorMessagePrompt;
import com.panda.sport.rcs.vo.MtsTemplateConfigVo;
import com.panda.sport.rcs.wrapper.OrderAcceptRejectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.constants.RedisKey.ODDSSCOPE_MATCH_PALY;
import static com.panda.sport.rcs.constants.RedisKey.ODDSSCOPE_MATCH_SWITCH;

/**
 * 接拒单服务
 *
 * @author carver
 */
@Component
@Slf4j
public class OrderAcceptRejectServiceImpl implements OrderAcceptRejectService {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ProducerSendMessageUtils sendMessage;
    @Autowired
    StandardSportMarketCategoryMapper standardSportMarketCategoryMapper;
    @Autowired
    private TOrderMapper orderMapper;
    @Autowired
    TOrderDetailExtMapper tOrderDetailExtMapper;
    @Autowired
    TOrderDetailExtRepository tOrderDetailExtRepository;
    @Autowired
    TOrderDetailExtUtils tOrderDetailExtUtils;
    //接距球种，2.篮球 3.棒球 5.网球 7.斯诺克 8.乒乓球 9.排球
    private static final List<Integer> sportList = Arrays.asList(2, 3, 5, 7, 8, 9, 10);
    private static final List<Integer> DISTANCE_PLAY_ID_LIST = Arrays.asList(1, 17, 25, 111, 119, 126, 129, 7, 20, 74, 3, 6, 8, 9, 13, 14, 16, 21, 22, 23, 27, 28, 29, 30, 31, 32, 35, 36, 44, 49, 50, 55, 56, 61, 62, 67, 68, 69, 70, 71, 72, 73, 85, 95, 101, 102, 103, 104, 105, 106, 107, 108, 112, 117, 120, 125, 137, 141, 147, 148, 149, 150, 151, 152, 159, 161, 166, 167, 170, 171, 174, 190, 197, 200, 204, 209, 210, 211, 212, 213, 216, 217, 218, 222, 223, 224, 225, 226, 227, 228, 230, 231, 235, 236, 237, 238, 239, 241, 260, 261, 265, 267, 273, 275, 277, 296, 297, 298, 340, 344, 345, 346, 347, 348, 349, 350, 351, 353, 360, 354, 355, 356, 357, 358, 363, 364, 365, 366, 361, 362);
    private static final String RCS_BUS_MTS_ORDER_STATUS = "queue_mts_order";


    /**
     * @return void
     * @Description 发送mq
     * @Param [vo]
     * @Author toney
     * @Date 10:24 2020/2/1
     **/
    @Override
    @Transactional
    public void sendMessage(List<String> ids, int state, Map<String, String> mapRefuse) {
        if (ids == null || ids.size() == 0) return;
        Map<String, List<TOrderDetailExtDO>> tOrderDetailExtMap = new HashMap<>();
        List<TOrder> orderList;
        if (tOrderDetailExtUtils.isSaveToMongo()) {
            orderList = orderMapper.queryByOrderDetailAndIds(ids);
            tOrderDetailExtMap = getMongoOrderDetailExt(ids);
        } else {
            orderList = orderMapper.queryByOrderDetailExtAndIds(ids);
        }

        for (TOrder order : orderList) {
            if (!"0".equals(String.valueOf(order.getOrderStatus()))) {//0 待处理
                continue;
            }

            //接单，串关要所有订单都接单才发送
            if (state == 1 && order.getSeriesType() != 1) {
                Boolean flag;
                if (tOrderDetailExtUtils.isSaveToMongo()) {
                    flag = checkOrderDetailExtAllAcceptMongo(tOrderDetailExtMap.getOrDefault(order.getOrderNo(), new ArrayList<>()));
                } else {
                    flag = checkOrderDetailExtAllAccept(order.getOrderDetailExtList());
                }

                if (!flag) {
                    continue;
                }
            }


            OrderBean orderBean = new OrderBean();
            BeanUtils.copyProperties(order, orderBean);
            List<OrderItem> orderItemList = new ArrayList<>();
            List<OrderDetailPO> orderDetailsList = Lists.newArrayList();

            for (TOrderDetail orderDetail : order.getOrderDetailList()) {
                OrderItem orderItem = new OrderItem();
                BeanUtils.copyProperties(orderDetail, orderItem);
                orderItem.setTradeType(0);
                //orderItem.setOddsValue(orderItem.getOddsValue() * 100000);
                BigDecimal oddsValue = new BigDecimal(orderItem.getOddsValue().toString()).multiply(new BigDecimal(100000));
                orderItem.setOddsValue(oddsValue.doubleValue());
                orderItem.setValidateResult(state);
                orderItemList.add(orderItem);
                orderBean.setExtendBean(buildExtendBean(orderBean, orderItem));

                OrderDetailPO orderDetailPO = new OrderDetailPO();
                orderDetailPO.setBetNo(orderItem.getBetNo());
                orderDetailPO.setPlayId(orderItem.getPlayId().longValue());
                orderDetailPO.setMarketId(orderItem.getMarketId());
                orderDetailsList.add(orderDetailPO);
            }
            orderBean.setItems(orderItemList);
            orderBean.setOrderStatus(state);
            if (OrderStatusEnum.ORDER_ACCEPT.getCode() == state) {
                //滚球接单
                orderBean.getExtendBean().setValidateResult(1);
                orderBean.setValidateResult(1);
                orderBean.setInfoStatus(OrderInfoStatusEnum.SCROLL_PASS.getCode());
            } else if (OrderStatusEnum.ORDER_REJECT.getCode() == state) {
                //滚球拒单
                String reason = mapRefuse.getOrDefault(orderBean.getOrderNo(), "滚球拒单");
                orderBean.setInfoStatus(OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
                orderBean.setReason(reason);
            }
            //修改订单信息
            sendMessage.sendMessage(MqConstants.RCS_ORDER_UPDATE, "task_accept_order", orderBean.getOrderNo(), orderBean);
            //通知业务处理注单状态
            Map<String, Object> map = Maps.newHashMap();
            map.put("orderNo", orderBean.getOrderNo());
            map.put("status", state);
            if (OrderStatusEnum.ORDER_ACCEPT.getCode() == state) {
                map.put("infoStatus", OrderInfoStatusEnum.SCROLL_PASS.getCode());
                map.put("infoMsg", "滚球接单成功");
                map.put("infoCode", 0);
            } else if (OrderStatusEnum.ORDER_REJECT.getCode() == state) {
                map.put("infoStatus", OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
                map.put("infoMsg", "滚球拒单");
                map.put("infoCode", -1);
            }
            map.put(orderBean.getOrderNo() + "_error_msg", orderBean.getReason());
            map.put("handleTime", System.currentTimeMillis());
            sendMessage.sendMessage(MqConstants.RCS_BUS_MTS_ORDER_STATUS + "," + orderBean.getOrderNo(), map);
        }
    }


    @Deprecated
    private Boolean checkOrderDetailExtAllAccept(List<TOrderDetailExt> orderDetailExtList) {
        Boolean flag = true;
        for (TOrderDetailExt ext : orderDetailExtList) {
            if (!ext.getOrderStatus().equals(1)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private Boolean checkOrderDetailExtAllAcceptMongo(List<TOrderDetailExtDO> orderDetailExtList) {
        Boolean flag = true;
        for (TOrderDetailExtDO ext : orderDetailExtList) {
            if (!ext.getOrderStatus().equals(1)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateOrderDetailExtStatus(Set<String> rejectOrders, Set<Long> acceptIds) {
        Integer count = NumberUtils.INTEGER_ZERO;
        //修改订单状态
        if (rejectOrders.size() > 0) {
            UpdateWrapper<TOrderDetailExt> rejectUpdateWrapper = new UpdateWrapper<>();
            rejectUpdateWrapper.setSql("update_time = now()");
            rejectUpdateWrapper.in("order_no", rejectOrders);
            count += tOrderDetailExtMapper.update(getUpdateCondition(2), rejectUpdateWrapper);
        }
        if (acceptIds.size() > 0) {
            UpdateWrapper<TOrderDetailExt> acceptUpdateWrapper = new UpdateWrapper<>();
            acceptUpdateWrapper.setSql("update_time = now()");
            acceptUpdateWrapper.ne("order_status", 2).in("id", acceptIds);
            count += tOrderDetailExtMapper.update(getUpdateCondition(1), acceptUpdateWrapper);
        }
        return count;
    }

    @Override
    public Integer updateOrderDetailExtStatusByOrderNo(TOrderDetailExt tOrderDetailExt) {
        return tOrderDetailExtMapper.updateOrderDetailExtStatusByOrderNo(tOrderDetailExt);

    }

    @Override
    public Integer updateOrderDetailExtStatusByOrderNoList(Integer state, List<String> orderList) {
        return tOrderDetailExtMapper.updateOrderDetailExtStatusByOrderNoList(state, orderList);
    }

    @Override
    public Integer updateOrderDetailExtStatusByOrderNoMongo(TOrderDetailExtDO tOrderDetailExt) {
        return tOrderDetailExtRepository.updateOrderDetailExtStatusByOrderNo(tOrderDetailExt);
    }

    private TOrderDetailExt getUpdateCondition(Integer orderStatus) {
        TOrderDetailExt ext = new TOrderDetailExt();
        ext.setOrderStatus(orderStatus);
        ext.setHandleStatus(1);
//        ext.setUpdateTime(new Date());
        return ext;
    }

    /**
     * @return com.panda.sport.data.rcs.dto.ExtendBean
     * @Description 根据orderItem 获取扩展 orderBean
     * @Param [bean, item]
     * @Author max
     * @Date 11:15 2019/12/11
     **/
    public ExtendBean buildExtendBean(OrderBean bean, OrderItem item) {
        ExtendBean extend = new ExtendBean();
        extend.setSeriesType(bean.getSeriesType());
        extend.setItemId(item.getBetNo());
        extend.setOrderId(item.getOrderNo());
        extend.setBusId(String.valueOf(bean.getTenantId()));
        extend.setHandicap(item.getMarketValue());
        extend.setCurrentScore(item.getScoreBenchmark());
        //item  1 ：早盘 ，2： 滚球盘， 3： 冠军盘
        extend.setIsScroll(String.valueOf(item.getMatchType()).equals("1") ? "0" : "1");
        extend.setMatchId(String.valueOf(item.getMatchId()));
        extend.setPlayId(item.getPlayId() + "");
        extend.setSelectId(String.valueOf(item.getPlayOptionsId()));
        extend.setSportId(String.valueOf(item.getSportId()));
        extend.setUserId(String.valueOf(item.getUid()));
        extend.setOdds(String.valueOf(item.getHandleAfterOddsValue()));
        extend.setMarketId(item.getMarketId().toString());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", item.getPlayId());
        params.put("sportId", "1");
        StandardSportMarketCategory standardSportMarketCategory = standardSportMarketCategoryMapper.queryCategoryInfoByMap(params);
        if (standardSportMarketCategory != null) {
            //阶段
            extend.setPlayType(standardSportMarketCategory.getTheirTime().toString());
        }
        if (item.getBetAmount() != null) {
            extend.setOrderMoney(item.getBetAmount());
            extend.setCurrentMaxPaid(new BigDecimal(item.getBetAmount()).multiply((new BigDecimal(extend.getOdds()).subtract(new BigDecimal(1)))).longValue());
        } else {
            extend.setOrderMoney(0L);
            extend.setCurrentMaxPaid(0L);
        }
        extend.setItemBean(item);

        if (StringUtils.isBlank(extend.getHandicap())) {
            extend.setHandicap("0");
        }
        if (StringUtils.isBlank(extend.getCurrentScore())) {
            extend.setCurrentScore("0:0");
        }
        return extend;
    }

    @Override
    @Deprecated
    @Transactional
    public void sendMessage(List<TOrder> list, List<TOrderDetailExt> tOrderDetailExtList) {
        Map<String, TOrderDetailExt> extMap = Maps.newHashMap();
        Map<String, TOrder> pauseMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(tOrderDetailExtList)) {
            extMap = tOrderDetailExtList.stream().collect(Collectors.toMap(e -> e.getBetNo(), e -> e));
        }

        if (!CollectionUtils.isEmpty(list)) {
            pauseMap = list.stream().collect(Collectors.toMap(e -> e.getOrderNo(), e -> e, (key1, key2) -> key1));
            log.info("暂停注单自动接单" + JsonFormatUtils.toJson(pauseMap));
        }

        if (list == null || list.size() <= 0) return;

        Map<String, List<TOrder>> orderMap = list.stream().collect(Collectors.groupingBy(bean -> bean.getOrderNo()));
        List<String> oids = new ArrayList<String>();
        oids.addAll(orderMap.keySet());
        List<TOrder> orderList = orderMapper.queryByOrderDetailExtAndIds(oids);

        String currentEvent = "";
        String betNo = "";
        Map oddsRangeMap = new HashMap();
        for (TOrder order : orderList) {
            try {
                if (!"0".equals(String.valueOf(order.getOrderStatus()))) {//0 待处理
                    continue;
                }
                List<TOrder> handleOrderList = orderMap.get(order.getOrderNo());
                if (handleOrderList == null) {
                    log.warn("订单数据不应该查到，不处理当前订单：{},order:{}", JSONObject.toJSONString(list), JSONObject.toJSONString(order));
                    continue;
                }

                TOrder handleOrder = handleOrderList.get(0);

                if (order.getSeriesType() != 1) {//串关

                    if (!CollectionUtils.isEmpty(pauseMap)) {
                        TOrder tOrder = pauseMap.get(order.getOrderNo());
                        if (tOrder != null) {
                            order.setInfoStatus(tOrder.getInfoStatus());
                        }
                    }

                    if (OrderInfoStatusEnum.PAUSE_ORDER.getCode().equals(order.getInfoStatus())) {
                        handleOrder.setOrderStatus(OrderStatusEnum.ORDER_WAITING.getCode());
                        handleOrder.setInfoStatus(OrderInfoStatusEnum.PAUSE_ORDER.getCode());
                        handleOrder.setReason("忽略暂停接单");
                    } else {
                        //接单，串关要所有订单都接单才发送
                        int accCount = 0;
                        int rejCount = 0;
                        for (TOrderDetailExt ext : order.getOrderDetailExtList()) {
                            //0 待处理 1 接单  2拒单 3：一键秒接  4：手动接单   5：手动拒单 6:中场休息秒接
                            if (Arrays.asList(2, 5, 9).contains(ext.getOrderStatus())) {//拒单
                                rejCount++;
                                TOrderDetailExt e = extMap.get(ext.getBetNo());
                                if (!ObjectUtils.isEmpty(e)) {
                                    currentEvent = e.getCurrentEvent();
                                    betNo = e.getBetNo();
                                }
                            } else if (Arrays.asList(1, 3, 4, 6, 7, 8).contains(ext.getOrderStatus())) {//接单
                                accCount++;
                            }
                        }
                        if (rejCount > 0) {//有一个拒单，当前订单就拒单
                            handleOrder.setOrderStatus(OrderStatusEnum.ORDER_REJECT.getCode());
                            handleOrder.setInfoStatus(OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
                            handleOrder.setReason("串关滚球拒单");
                        } else if (accCount == order.getOrderDetailExtList().size()) {//全部接单才能接单
                            handleOrder.setOrderStatus(OrderStatusEnum.ORDER_ACCEPT.getCode());
                            handleOrder.setInfoStatus(OrderInfoStatusEnum.SCROLL_PASS.getCode());
                            handleOrder.setReason("串关滚球接单");
                        } else {
                            log.warn("串关接拒单，还需要确认其他订单，当前不处理：{}", JSONObject.toJSONString(orderList));
                            continue;
                        }
                    }
                } else {
                    currentEvent = order.getOrderDetailExtList().get(NumberUtils.INTEGER_ZERO).getCurrentEvent();
                    betNo = order.getOrderDetailExtList().get(NumberUtils.INTEGER_ZERO).getBetNo();
                }

                Integer state = handleOrder.getOrderStatus();
                OrderBean orderBean = new OrderBean();
                BeanUtils.copyProperties(order, orderBean);
                List<OrderItem> orderItemList = new ArrayList<>();
                List<OrderDetailPO> orderDetailsList = Lists.newArrayList();
                long currentTimeMillis = System.currentTimeMillis();
                orderBean.setModifyTime(currentTimeMillis);
                for (TOrderDetail orderDetail : order.getOrderDetailList()) {
                    OrderItem orderItem = new OrderItem();
                    BeanUtils.copyProperties(orderDetail, orderItem);
                    orderItem.setTradeType(0);
                    BigDecimal oddsValue = new BigDecimal(orderItem.getOddsValue().toString()).multiply(new BigDecimal(100000));
                    orderItem.setOddsValue(oddsValue.doubleValue());
                    orderItem.setValidateResult(state);
                    // 推送ws需要更新时间
                    orderItem.setModifyTime(currentTimeMillis);
                    //暂停注单处理标识
                    if (!CollectionUtils.isEmpty(pauseMap)) {
                        TOrder tOrder = pauseMap.get(orderItem.getOrderNo());
                        if (tOrder != null) {
                            orderItem.setPauseTime(tOrder.isPause() ? -1 : null);
                        }
                    }
                    orderItemList.add(orderItem);
                    orderBean.setExtendBean(buildExtendBean(orderBean, orderItem));

                    OrderDetailPO orderDetailPO = new OrderDetailPO();
                    orderDetailPO.setBetNo(orderItem.getBetNo());
                    orderDetailPO.setPlayId(orderItem.getPlayId().longValue());
                    orderDetailPO.setMarketId(orderItem.getMarketId());
                    orderDetailsList.add(orderDetailPO);
                    String oddsRangeRedis = oddsRangeRedis(orderDetail);
                    if (StringUtils.isNotBlank(oddsRangeRedis)) {
                        oddsRangeMap.put(String.valueOf(orderDetail.getPlayOptionsId()), oddsRangeRedis);
                    }
                }
                orderBean.setItems(orderItemList);
                orderBean.setOrderStatus(state);
                if (OrderStatusEnum.ORDER_ACCEPT.getCode() == state) {
                    //滚球接单
                    orderBean.getExtendBean().setValidateResult(1);
                    orderBean.setValidateResult(1);
                    orderBean.setInfoStatus(handleOrder.getInfoStatus());
                    orderBean.setReason(handleOrder.getReason());
                } else if (OrderStatusEnum.ORDER_REJECT.getCode() == state) {
                    //滚球拒单
                    String reason = handleOrder.getReason() == null ? "滚球拒单" : handleOrder.getReason();
                    orderBean.setInfoStatus(handleOrder.getInfoStatus());
                    orderBean.setReason(reason);
                } else if (OrderStatusEnum.ORDER_WAITING.getCode() == state) {
                    String reason = handleOrder.getReason() == null ? "忽略暂停" : handleOrder.getReason();
                    orderBean.setReason(reason);
                    orderBean.setInfoStatus(OrderInfoStatusEnum.PAUSE_ORDER.getCode());
                }
                //修改订单信息
                sendMessage.sendMessage(MqConstants.RCS_ORDER_UPDATE, "task_accept_order", orderBean.getOrderNo(), orderBean);

                if (!OrderStatusEnum.ORDER_WAITING.getCode().equals(state)) {
                    //通知业务处理注单状态
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("orderNo", orderBean.getOrderNo());
                    map.put("status", state);
                    map.put("infoStatus", orderBean.getInfoStatus());
                    map.put("infoMsg", orderBean.getReason());
                    map.put("betNo", betNo);
                    if (OrderInfoStatusEnum.SCROLL_REFUSE.getCode() == orderBean.getInfoStatus()) {
                        map.put("currentEvent", currentEvent);
                    }

                    if (OrderStatusEnum.ORDER_ACCEPT.getCode() == state) {
                        map.put("infoCode", 0);
                    } else if (OrderStatusEnum.ORDER_REJECT.getCode() == state) {
                        map.put("infoCode", -1);
                    }
                    map.put(orderBean.getOrderNo() + "_error_msg", orderBean.getReason());
                    map.put("handleTime", currentTimeMillis);

                    map.put("oddsRange", oddsRangeMap);
                    if (!CollectionUtils.isEmpty(pauseMap)) {
                        TOrder tOrder = pauseMap.get(orderBean.getOrderNo());
                        if (tOrder != null && tOrder.isAfterCheck() == true) {
                            map.put("infoStatus", OrderInfoStatusEnum.ALL_PASS.getCode());
                        }
                    }
                    sendMessage.sendMessage(MqConstants.RCS_BUS_MTS_ORDER_STATUS + "," + orderBean.getOrderNo(), map);
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void sendMessageNew(List<TOrderDetailExt> tOrderDetailExtList, List<String> acceptOrderList, List<String> rejectOrderList) {
        try {
            //1601需求 如果VAR处于等待状态就一直不处理

            String orderNo = tOrderDetailExtList.get(0).getOrderNo();
            for (TOrderDetailExt tOrderDetailExt : tOrderDetailExtList) {
                long matchId = tOrderDetailExt.getMatchId();
                String matchKey = String.format(RedisKey.MATCH_EVENT_KOALA_REDIS_KEY, matchId);
                String matchVal = RcsLocalCacheUtils.getValue(matchKey, redisClient::get, 2 * 1000L);           //redisClient.get(matchKey);
                log.info("::{}::VAR接距等待处理:{}", orderNo, matchVal);
                if (StringUtils.isNotBlank(matchVal) && StringUtils.equalsIgnoreCase("var_reason", matchVal)) {
                    return;
                }
            }
            //如果是拒单直接发送给业务和修改订单状态
            String orderBeanKey = String.format(RedisKey.REDIS_MATCH_DETAIL_EXT_INFO_KEY, orderNo);
            String orderBeanStr = redisClient.get(orderBeanKey);
            OrderBean orderBean = JSON.parseObject(orderBeanStr, OrderBean.class);
            //如果是空的就去数据库查一遍，兼容老数据
            if (Objects.isNull(orderBean)) {
                List<String> oids = Arrays.asList(orderNo);
                TOrder tOrder = orderMapper.queryByOrderDetailAndIds(oids).get(NumberUtils.INTEGER_ZERO);
                if (tOrder.getOrderStatus() == OrderStatusEnum.ORDER_ACCEPT.getCode().intValue()) {
                    acceptOrderList.add(orderNo);
                }
                if (tOrder.getOrderStatus() == OrderStatusEnum.ORDER_REJECT.getCode().intValue()) {
                    rejectOrderList.add(orderNo);
                }
                return;
            }
            log.info("::{}::1666需求转换获取主订单信息:{}", orderNo, JSON.toJSONString(orderBean));
            ErrorMessagePrompt errorMessagePrompt = new ErrorMessagePrompt();
            int state = processOrderStatus(tOrderDetailExtList, errorMessagePrompt);
            log.info("::{}::检查数据库状态:{}", orderNo, state);
            //如果是等待的状态去检查实时接距
            if (state == OrderStatusEnum.ORDER_WAITING.getCode()) {
                state = this.dealWithData(tOrderDetailExtList, errorMessagePrompt) ? OrderStatusEnum.ORDER_REJECT.getCode() : state;
            }
            //只要不是等待状态都发送给业务
            if (state != OrderStatusEnum.ORDER_WAITING.getCode()) {
                this.setUpHandStatus(orderBean, state);
                //组装orderbean
                orderBean.setOrderStatus(state);
                orderBean.getExtendBean().setValidateResult(state);
                orderBean.setValidateResult(state);
                orderBean.setReason(errorMessagePrompt.getHintMsg());
                orderBean.setInfoStatus(errorMessagePrompt.getInfoStatus());
                log.info("::{}::1666需求开始发送拒单", orderNo);
                this.sendOrderMsg(orderBean, tOrderDetailExtList, errorMessagePrompt, state, acceptOrderList, rejectOrderList);
                redisClient.setExpiry(String.format(RedisKey.LOCK_PROCESSED_KEY, orderNo), orderNo, 30L);
                return;
            }
            //  接单时间判断
            if (processWaitingTime(tOrderDetailExtList)) {
                this.setUpHandStatus(orderBean, OrderStatusEnum.ORDER_ACCEPT.getCode());
                log.info("::{}::到达最大等待时间开始修改订单信息", orderNo);
                orderBean.setReason("到达最大等待时间");
                orderBean.getExtendBean().setValidateResult(OrderStatusEnum.ORDER_ACCEPT.getCode());
                orderBean.setValidateResult(OrderStatusEnum.ORDER_ACCEPT.getCode());
                orderBean.setOrderStatus(OrderStatusEnum.ORDER_ACCEPT.getCode());
                orderBean.setInfoStatus(OrderInfoStatusEnum.SCROLL_PASS.getCode());
                this.sendOrderMsg(orderBean, tOrderDetailExtList, errorMessagePrompt, OrderStatusEnum.ORDER_ACCEPT.getCode(), acceptOrderList, rejectOrderList);
                redisClient.setExpiry(String.format(RedisKey.LOCK_PROCESSED_KEY, orderNo), orderNo, 30L);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    private void sendOrderMsg(OrderBean orderBean, List<TOrderDetailExt> tOrderDetailExtList, ErrorMessagePrompt errorMessagePrompt, Integer state, List<String> acceptOrderList, List<String> rejectOrderList) {
        String orderNo = orderBean.getOrderNo();
        //修改ext订单信息
        TOrderDetailExt updateOrder = new TOrderDetailExt();
        updateOrder.setOrderStatus(state);
        updateOrder.setOrderNo(orderNo);
        if (state == OrderStatusEnum.ORDER_ACCEPT.getCode().intValue()) {
            acceptOrderList.add(orderNo);
        }
        if (state == OrderStatusEnum.ORDER_REJECT.getCode().intValue()) {
            rejectOrderList.add(orderNo);
        }

        //this.updateOrderDetailExtStatusByOrderNo(updateOrder);
        log.info("::{}::1666需求开始修改ext表:{}", orderNo, JSON.toJSONString(updateOrder));
        //修改订单信息
        sendMessage.sendMessage(MqConstants.RCS_ORDER_UPDATE, "task_accept_order", orderNo, orderBean);
        //发送数据到业务
        this.notificationBusiness(orderBean, tOrderDetailExtList, errorMessagePrompt);
        //删除缓存
        String orderKey = String.format(RedisKey.REDIS_MATCH_DETAIL_EXT_INFO_KEY, orderNo);
        redisClient.delete(orderKey);
    }

    /**
     * 处理时间
     *
     * @return
     */
    public boolean processWaitingTime(List<TOrderDetailExt> tOrderDetailExtList) {
        for (TOrderDetailExt tOrderDetailExt : tOrderDetailExtList) {
            if (!isTimeIn(tOrderDetailExt)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 处理EXT表本身状态
     *
     * @param tOrderDetailExtList
     * @return 状态编码
     */
    private Integer processOrderStatus(List<TOrderDetailExt> tOrderDetailExtList, ErrorMessagePrompt errorMessagePrompt) {
        int countSuccess = 0;
        int countOneKey = 0;
        int countManual = 0;
        int countPause = 0;
        for (TOrderDetailExt tOrderDetailExt : tOrderDetailExtList) {
            //拒单 ，业务拒单，会把order表状态修改，但是不会修改ext表，在这里兼容
//            TOrder torder = orderMapper.selectById(tOrderDetailExt.getOrderNo());
//            if (torder.getOrderStatus() == 2) {
//                tOrderDetailExt.setOrderStatus(2);
//            }
            errorMessagePrompt.setCurrentEvent(tOrderDetailExt.getCurrentEvent());
            if (tOrderDetailExt.getOrderStatus() == 1) {
                errorMessagePrompt.setHintMsg("接拒单接单");
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_PASS.getCode());
                errorMessagePrompt.setBetNo(tOrderDetailExt.getBetNo());
                countSuccess += 1;
            }
            if (tOrderDetailExt.getOrderStatus() == 2) {
                errorMessagePrompt.setHintMsg(tOrderDetailExt.getCurrentEvent() + "事件拒单订单号:" + tOrderDetailExt.getOrderNo());
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_PASS.getCode());
                errorMessagePrompt.setBetNo(tOrderDetailExt.getBetNo());
                return OrderStatusEnum.ORDER_REJECT.getCode();
            }
            //3 表示一键秒接
            if (tOrderDetailExt.getOrderStatus() == 3) {
                errorMessagePrompt.setHintMsg("一键秒接");
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.ALL_PASS.getCode());
                errorMessagePrompt.setBetNo(tOrderDetailExt.getBetNo());
                countOneKey += 1;
            }
            //手动接单
            if (tOrderDetailExt.getOrderStatus() == 4) {
                errorMessagePrompt.setHintMsg("手动接单");
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_HAND_PASS.getCode());
                errorMessagePrompt.setBetNo(tOrderDetailExt.getBetNo());
                countManual += 1;
            }
            //手动拒单
            if (tOrderDetailExt.getOrderStatus() == 5) {
                errorMessagePrompt.setHintMsg("手动拒单");
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_HAND_REFUSE.getCode());
                errorMessagePrompt.setBetNo(tOrderDetailExt.getBetNo());
                return OrderStatusEnum.ORDER_REJECT.getCode();
            }
            //9 暂停接单
            if (tOrderDetailExt.getOrderStatus() == 8) {
                errorMessagePrompt.setCurrentEvent(tOrderDetailExt.getCurrentEvent());
                errorMessagePrompt.setHintMsg("暂停接单");
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_HAND_PASS.getCode());
                errorMessagePrompt.setBetNo(tOrderDetailExt.getBetNo());
                countPause += 1;
            }
            //9 暂停拒单
            if (tOrderDetailExt.getOrderStatus() == 9) {
                errorMessagePrompt.setHintMsg("暂停拒单");
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_HAND_REFUSE.getCode());
                errorMessagePrompt.setBetNo(tOrderDetailExt.getBetNo());
                return OrderStatusEnum.ORDER_REJECT.getCode();
            }
            if (tOrderDetailExt.getOrderStatus() == 10) {
                errorMessagePrompt.setHintMsg("忽略暂停注单");
                errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.PAUSE_ORDER.getCode());
                errorMessagePrompt.setBetNo(tOrderDetailExt.getBetNo());
                return OrderStatusEnum.ORDER_WAITING.getCode();
            }
        }

        if (countSuccess == tOrderDetailExtList.size() || countOneKey == tOrderDetailExtList.size() ||
                countManual == tOrderDetailExtList.size() || countPause == tOrderDetailExtList.size()) {
            return OrderStatusEnum.ORDER_ACCEPT.getCode();
        }
        return OrderStatusEnum.ORDER_WAITING.getCode();
    }


    private void sendOrderData(TOrder order, OrderBean orderBean) {
        List<OrderItem> orderItemList = new ArrayList<>();
        long currentTimeMillis = System.currentTimeMillis();
        for (TOrderDetail orderDetail : order.getOrderDetailList()) {
            OrderItem orderItem = new OrderItem();
            BeanUtils.copyProperties(orderDetail, orderItem);
            orderItem.setTradeType(0);
            BigDecimal oddsValue = new BigDecimal(orderItem.getOddsValue().toString()).multiply(new BigDecimal(100000));
            orderItem.setOddsValue(oddsValue.doubleValue());
            orderItem.setValidateResult(orderBean.getValidateResult());
            // 推送ws需要更新时间
            orderItem.setModifyTime(currentTimeMillis);
            orderItemList.add(orderItem);
            orderBean.setExtendBean(buildExtendBean(orderBean, orderItem));
        }
        orderBean.setItems(orderItemList);
    }


    private void setUpHandStatus(OrderBean orderBean, int state) {
        //统一处理成风控拒单处理中
        // orderBean.setInfoStatus(OrderInfoStatusEnum.RISK_PROCESSING.getCode());
        long currentTimeMillis = System.currentTimeMillis();
        orderBean.getItems().forEach(s -> {
            s.setHandleStatus(NumberUtils.INTEGER_ONE);
            s.setValidateResult(state);
            s.setModifyTime(currentTimeMillis);
            s.setValidateResult(state);
            orderBean.setExtendBean(buildExtendBean(orderBean, s));
        });

    }


    private void notificationBusiness(OrderBean orderBean, List<TOrderDetailExt> tOrderDetailExtList, ErrorMessagePrompt errorMessagePrompt) {

        //通知业务处理注单状态
        Map<String, String> oddsRangeMap = new HashMap();
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderNo", orderBean.getOrderNo());
        map.put("status", orderBean.getOrderStatus());
        map.put("infoStatus", orderBean.getInfoStatus());
        map.put("infoMsg", orderBean.getReason());
        map.put("betNo", StringUtils.isNotBlank(errorMessagePrompt.getBetNo()) ? errorMessagePrompt.getBetNo() : tOrderDetailExtList.get(NumberUtils.INTEGER_ZERO).getBetNo());
        if (orderBean.getOrderStatus() == 2) { //拒单处理
            map.put("currentEvent", errorMessagePrompt.getCurrentEvent());
        }
        if (errorMessagePrompt.isMtsInfo()) {
            map.put("mtsIsCache", "3");
        }
        if (isPaInfo(tOrderDetailExtList)) {
            map.put("mtsIsCache", "4");
        }
        if (OrderStatusEnum.ORDER_ACCEPT.getCode().equals(orderBean.getOrderStatus())) {
            map.put("infoCode", 0);
        } else if (OrderStatusEnum.ORDER_REJECT.getCode().equals(orderBean.getOrderStatus())) {
            map.put("infoCode", -1);
        }
        for (TOrderDetailExt tOrderDetailExt : tOrderDetailExtList) {
            String oddsRangeRedis = oddsRangeRedisNew(tOrderDetailExt);
            if (StringUtils.isNotBlank(oddsRangeRedis)) {
                oddsRangeMap.put(String.valueOf(tOrderDetailExt.getPlayOptionsId()), oddsRangeRedis);
            }
        }
        map.put("oddsRange", oddsRangeMap);
        map.put(orderBean.getOrderNo() + "_error_msg", orderBean.getReason());
        map.put("handleTime", System.currentTimeMillis());
        if(StringUtils.isBlank(orderBean.getOrderGroup())){
            sendMessage.sendMessage(MqConstants.RCS_BUS_MTS_ORDER_STATUS + "," + orderBean.getOrderNo(), map);
        }else {
            String topic = RCS_BUS_MTS_ORDER_STATUS + "_" + orderBean.getOrderGroup();
            sendMessage.sendMessage(topic, "mtsOrder", orderBean.getOrderNo(), map);
        }
    }

    /**
     * k
     *
     * @return boolean
     * @Description //是否满足接单条件
     * @Param [tOrderDetailExt]
     * @Author sean
     * @Date 2020/11/7
     **/
    private boolean isTimeIn(TOrderDetailExt tOrderDetailExt) {
        Long curTime = System.currentTimeMillis();
        Long minTime = tOrderDetailExt.getBetTime() + tOrderDetailExt.getMinWait() * 1000;
        log.info("::{}::当前时间:{},最小等待秒数:{},最小等待时间:{},最大等待时间:{}::", tOrderDetailExt.getOrderNo(), curTime,
                tOrderDetailExt.getMinWait(), minTime, tOrderDetailExt.getMaxAcceptTime());
        return curTime > minTime && curTime > tOrderDetailExt.getMaxAcceptTime();
    }


    /**
     * 处理VAR等待接单，盘口变化 盘口位置变化，盘口赔率变化逻辑
     *
     * @param tOrderDetailExtList 订单扩展信息数据
     * @param errorMessagePrompt  拒单描述
     * @return 是否拒单
     */
    private boolean dealWithData(List<TOrderDetailExt> tOrderDetailExtList, ErrorMessagePrompt errorMessagePrompt) {
        for (TOrderDetailExt tOrderDetailExt : tOrderDetailExtList) {
            if (StringUtils.isBlank(tOrderDetailExt.getMarketId())) {
                continue;
            }
            errorMessagePrompt.setCurrentEvent(tOrderDetailExt.getCurrentEvent());
            //赛事维度
            String matchInfoStr = RcsLocalCacheUtils.getValue(String.format(RedisKey.REDIS_MATCH_INFO, tOrderDetailExt.getMatchId()), redisClient::get, 1000L); //redisClient.get(String.format(RedisKey.REDIS_MATCH_INFO, tOrderDetailExt.getMatchId()));
            log.info("::1666需求赛事维度数据::{}", matchInfoStr);
            if (StringUtils.isNotBlank(matchInfoStr)) {
                StandardMatchMessage standardMatchMessage = JSONObject.parseObject(matchInfoStr, StandardMatchMessage.class);
                //收盘状态不拒单
                if (standardMatchMessage.getStatus() != 0 && standardMatchMessage.getStatus() != 13) {
                    errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
                    if (standardMatchMessage.getStatus() == 1) {
                        errorMessagePrompt.setHintMsg("赛事封盘拒单");
                        errorMessagePrompt.setCurrentEvent("match_handicap_status_suspended-PA");
                        log.info("::1666需求赛事封盘拒单");
                    } else if (standardMatchMessage.getStatus() == 2) {
                        errorMessagePrompt.setHintMsg("赛事关盘拒单");
                        errorMessagePrompt.setCurrentEvent("match_handicap_status_deactivated-PA");
                        log.info("::1666需求赛事关盘拒单");
                    } else if (standardMatchMessage.getStatus() == 11) {
                        errorMessagePrompt.setHintMsg("赛事锁盘拒单");
                        errorMessagePrompt.setCurrentEvent("match_handicap_status_lock-PA");
                        log.info("::1666需求赛事锁盘拒单");
                    }
                    errorMessagePrompt.setBetNo(tOrderDetailExt.getBetNo());
                    return true;
                }
            }
            //盘口维度
            String matchMarketOddsStr = RcsLocalCacheUtils.getValue(String.format(RedisKey.REDIS_MATCH_MARKET_ODDS_NEW, tOrderDetailExt.getPlayId(),tOrderDetailExt.getMatchId()), redisClient::get,1000L); //redisClient.get(String.format(RedisKey.REDIS_MATCH_MARKET_ODDS_NEW, tOrderDetailExt.getPlayId(),tOrderDetailExt.getMatchId()));
            if (StringUtils.isNotBlank(matchMarketOddsStr)) {
                String oddsScopeValue = RcsLocalCacheUtils.getValue(String.format(RedisKey.ODDS_SCOPE_KEY, tOrderDetailExt.getMatchId(), tOrderDetailExt.getPlayId(), tOrderDetailExt.getMatchType() == 2 ? 0 : 1), redisClient::get, 1000L);//redisClient.get(String.format(RedisKey.ODDS_SCOPE_KEY, tOrderDetailExt.getMatchId(), tOrderDetailExt.getPlayId(), tOrderDetailExt.getMatchType() == 2 ? 0 : 1));
                log.info("::{}::1666需求配置的赔率:{},赛事ID:{}::", tOrderDetailExt.getOrderNo(), oddsScopeValue, tOrderDetailExt.getMatchId());
                List<StandardMarketMessage> rcsStandardMarketDTOS = JSONObject.parseArray(matchMarketOddsStr, StandardMarketMessage.class);
                log.info("::{}::1666需求订单详细表::{},{}", tOrderDetailExt.getOrderNo(), JSONObject.toJSONString(tOrderDetailExt), JSONObject.toJSONString(rcsStandardMarketDTOS));
                for (int i = 0; i < rcsStandardMarketDTOS.size(); i++) {
                    StandardMarketMessage standardMarketMessage = rcsStandardMarketDTOS.get(i);
                    log.info(":::{}:1666需求下发消息::{}", tOrderDetailExt.getOrderNo(), JSONObject.toJSONString(standardMarketMessage));
                    if (tOrderDetailExt.getSportId().longValue() == SportIdEnum.FOOTBALL.getId()) {
                        if (String.valueOf(standardMarketMessage.getId()).equals(tOrderDetailExt.getMarketId())) {
                            //盘口状态有变化
                            if (standardMarketMessage.getStatus() != 0 || standardMarketMessage.getThirdMarketSourceStatus() != 0) {
                                log.info("::{}::1666需求足球盘口有变化拒单::盘口ID:{}盘口状态:{},订单状态:{}", tOrderDetailExt.getOrderNo(), standardMarketMessage.getId(), standardMarketMessage.getStatus(), tOrderDetailExt.getOrderStatus());
                                this.errorMessage(standardMarketMessage, errorMessagePrompt);
                                errorMessagePrompt.setBetNo(tOrderDetailExt.getBetNo());
                                return true;
                            }
                            //1682是否满足接距 MTS-1：风控侧实时拒，赔率变动，盘口状态变动;
                            MtsTemplateConfigVo mtsTemplateConfigVo = this.queryMtsConfig(tOrderDetailExt);
                            log.info("::{}::1682需求模板配置::{}", tOrderDetailExt.getOrderNo(), JSON.toJSONString(mtsTemplateConfigVo));
                            if (Objects.nonNull(mtsTemplateConfigVo) && mtsTemplateConfigVo.getMtsSwitch() == 1 && Objects.nonNull(mtsTemplateConfigVo.getContactPercentage())) {
                                errorMessagePrompt.setMtsInfo(true);
                                BigDecimal oddsScope = mtsTemplateConfigVo.getContactPercentage().divide(new BigDecimal(100), 4, BigDecimal.ROUND_DOWN);
                                boolean flag = this.handlingOddsChanges(standardMarketMessage, tOrderDetailExt, oddsScope, errorMessagePrompt);
                                if (flag) {
                                    errorMessagePrompt.setBetNo(tOrderDetailExt.getBetNo());
                                    return true;
                                }
                            }
                        }
                    } else if (sportList.contains(tOrderDetailExt.getSportId())) {
                        if (String.valueOf(standardMarketMessage.getId()).equals(tOrderDetailExt.getMarketId())) {
                            //盘口状态有变化
                            if (!standardMarketMessage.getStatus().equals(0)) {
                                log.info("::{}::1666需求其他球种盘口有变化拒单::盘口ID:{}盘口状态:{},订单状态:{}", tOrderDetailExt.getOrderNo(), standardMarketMessage.getId(), standardMarketMessage.getStatus(), tOrderDetailExt.getOrderStatus());
                                this.errorMessage(standardMarketMessage, errorMessagePrompt);
                                errorMessagePrompt.setBetNo(tOrderDetailExt.getBetNo());
                                return true;
                            }
                            //赔率有变化
                            if (StringUtils.isNotBlank(oddsScopeValue)) {
                                BigDecimal oddsScope = new BigDecimal(oddsScopeValue).divide(new BigDecimal(100), 4, BigDecimal.ROUND_DOWN);
                                boolean flag = this.handlingOddsChanges(standardMarketMessage, tOrderDetailExt, oddsScope, errorMessagePrompt);
                                if (flag) return true;
                            }

                        }
                        Integer matchType = tOrderDetailExt.getMatchType() == 2 ? 0 : 1;
                        //盘口的位置有变化
                        if (StringUtils.equalsIgnoreCase(String.valueOf(standardMarketMessage.getMarketCategoryId()), String.valueOf(tOrderDetailExt.getPlayId())) &&
                                StringUtils.equalsIgnoreCase(String.valueOf(standardMarketMessage.getChildMarketCategoryId()), tOrderDetailExt.getSubPlayId()) &&
                                standardMarketMessage.getMarketType().equals(matchType) &&
                                tOrderDetailExt.getPlaceNum().equals(standardMarketMessage.getPlaceNum()) &&
                                !StringUtils.equalsIgnoreCase(tOrderDetailExt.getMarketId(), String.valueOf(standardMarketMessage.getId()))) {
                            log.info("::{}::1666需求盘口位置有变化拒单::盘口ID:{},盘口位置:{},订单盘口位置:{}", tOrderDetailExt.getOrderNo(), standardMarketMessage.getId(), standardMarketMessage.getPlaceNum(), tOrderDetailExt.getPlaceNum());
                            errorMessagePrompt.setHintMsg("对应坑位的盘口值已变更拒单");
                            errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
                            errorMessagePrompt.setCurrentEvent("bet_order_place_num_change");
                            errorMessagePrompt.setBetNo(tOrderDetailExt.getBetNo());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void errorMessage(StandardMarketMessage standardMarketMessage, ErrorMessagePrompt errorMessagePrompt) {
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

    private MtsTemplateConfigVo queryMtsConfig(TOrderDetailExt tOrderDetail) {
        Integer matchType = tOrderDetail.getMatchType();
        //先从缓存读取
        String key = String.format(RedisKey.REDIS_MTS_CONTACT_CONFIG_KEY, tOrderDetail.getMatchId(), matchType == 2 ? 0 : 1);
        String redisConfig = RcsLocalCacheUtils.getValue(key, redisClient::get, 1000L); //redisClient.get(key);
        if (StringUtils.isNotBlank(redisConfig)) {
            return JSONObject.parseObject(redisConfig, MtsTemplateConfigVo.class);
        }

        String config = standardSportMarketCategoryMapper.queryMtsConfigVal(tOrderDetail.getSportId(), tOrderDetail.getMatchId(), tOrderDetail.getMatchType());
        if (StringUtils.isNotBlank(config)) {
            redisClient.set(key, config);
            //设置过期时间3小时
            redisClient.expireKey(key, 3 * 60 * 60);
            return JSONObject.parseObject(config, MtsTemplateConfigVo.class);
        }
        return null;
    }

    /**
     * 处理赔率变化逻辑
     *
     * @param standardMarketMessage 坑位信息
     * @param tOrderDetailExt       订单扩展表
     * @param oddsScope             配置赔率区间变化
     * @param errorMessagePrompt    返回的消息提示
     * @return 是否拒单
     */
    private boolean handlingOddsChanges(StandardMarketMessage standardMarketMessage, TOrderDetailExt tOrderDetailExt, BigDecimal oddsScope, ErrorMessagePrompt errorMessagePrompt) {
        BigDecimal one = new BigDecimal(1);
        BigDecimal orderOdds = tOrderDetailExt.getOddsValue().divide(new BigDecimal(100000), 4, BigDecimal.ROUND_DOWN);
        BigDecimal checkOdds = BigDecimal.ZERO;

        for (int j = 0; j < standardMarketMessage.getMarketOddsList().size(); j++) {
            StandardMarketOddsMessage standardMarketOddsMessage = standardMarketMessage.getMarketOddsList().get(j);
            log.info("::{}1666需求获取盘口投注项赔率::{}", tOrderDetailExt.getOrderNo(), JSON.toJSONString(standardMarketOddsMessage));
            if (String.valueOf(standardMarketOddsMessage.getId()).equals(tOrderDetailExt.getPlayOptionsId())) {
                //默认获取PA赔率
                checkOdds = new BigDecimal(standardMarketOddsMessage.getPaOddsValue()).divide(new BigDecimal(100000), 4, BigDecimal.ROUND_DOWN);
                //如果PA赔率大于数据商下发的赔率就使用数据商的赔率
                if (DISTANCE_PLAY_ID_LIST.contains(tOrderDetailExt.getPlayId()) && StringUtils.isNotBlank(standardMarketMessage.getAddition5()) && new BigDecimal(standardMarketOddsMessage.getPaOddsValue()).compareTo(new BigDecimal(standardMarketMessage.getAddition5())) >= 0) {
                    checkOdds = new BigDecimal(standardMarketMessage.getAddition5()).divide(new BigDecimal(100000), 4, BigDecimal.ROUND_DOWN);
                }
            }
        }
        log.info("::{}::1666需求盘口赔率::订单赔率:{},配置值赔率范围:{},checkOdds:{}", tOrderDetailExt.getOrderNo(), orderOdds, oddsScope, checkOdds);
        if (one.divide(orderOdds, 4, BigDecimal.ROUND_DOWN).subtract(one.divide(checkOdds, 4, BigDecimal.ROUND_DOWN)).abs().compareTo(oddsScope) > 0) {
            log.info("::{}::1666需求盘口赔率有变化拒单::订单赔率:{},配置值赔率范围:{},checkOdds:{}", tOrderDetailExt.getOrderNo(), orderOdds, oddsScope, checkOdds);
            errorMessagePrompt.setHintMsg("赔率变动幅度过大拒单");
            errorMessagePrompt.setCurrentEvent("order_odds_change");
            errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
            errorMessagePrompt.setBetNo(tOrderDetailExt.getBetNo());
            return true;
        }
        return false;
    }

    private boolean isPaInfo(List<TOrderDetailExt> tOrderDetailExtList) {
        //盘口维度
        int count = 0;
        String redisKey = "rcs:redis:match:odds:new:%s";
        for (TOrderDetailExt tOrderDetailExt : tOrderDetailExtList) {
            String matchMarketOddsStr = redisClient.hGet(String.format(redisKey, tOrderDetailExt.getMatchId()), tOrderDetailExt.getPlayId().toString());
            log.info("::{}::下发赔率源数据:{}", tOrderDetailExt.getOrderNo(), matchMarketOddsStr);
            if (StringUtils.isNotBlank(matchMarketOddsStr)) {
                List<StandardMarketMessage> rcsStandardMarketDTOS = JSONObject.parseArray(matchMarketOddsStr, StandardMarketMessage.class);
                for (int i = 0; i < rcsStandardMarketDTOS.size(); i++) {
                    StandardMarketMessage standardMarketMessage = rcsStandardMarketDTOS.get(i);
                    if (StringUtils.equals(String.valueOf(standardMarketMessage.getId()), String.valueOf(tOrderDetailExt.getMarketId()))
                            && Objects.nonNull(standardMarketMessage.getOldThirdMarketSourceStatus()) && (standardMarketMessage.getOldThirdMarketSourceStatus() == 1 || standardMarketMessage.getOldThirdMarketSourceStatus() == 2)) {
                        count += 1;
                    }
                }
            }
        }
        return count == tOrderDetailExtList.size();
    }

    private String oddsRangeRedis(TOrderDetail orderDetail) {
        String oddsRange = "";
        try {
            Long matchId = orderDetail.getMatchId();
            Integer playId = orderDetail.getPlayId();
            Integer matchType = orderDetail.getMatchType();
            String betNo = orderDetail.getBetNo();
            if (matchType == 1) {
                matchType = 1;
            } else if (matchType == 2) {
                matchType = 0;
            } else {
                return oddsRange;
            }
            String matchSwitchKey = String.format(ODDSSCOPE_MATCH_SWITCH, matchId, matchType);
            String oddsRangeKey = String.format(ODDSSCOPE_MATCH_PALY, matchId, playId, matchType);
            String matchSwitch = redisClient.get(matchSwitchKey);
            log.info("::{}::赔率范围取值key:{}--value:{}", betNo, matchSwitchKey, matchSwitch);
            if ("1".equals(matchSwitch)) {
                oddsRange = redisClient.get(oddsRangeKey);
                log.info("::{}::赔率范围取值key:{}--value:{}", betNo, oddsRangeKey, oddsRange);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return oddsRange;
    }


    private String oddsRangeRedisNew(TOrderDetailExt tOrderDetailExt) {
        String oddsRange = "";
        try {
            Long matchId = tOrderDetailExt.getMatchId();
            Integer playId = tOrderDetailExt.getPlayId();
            Integer matchType = tOrderDetailExt.getMatchType();
            String betNo = tOrderDetailExt.getBetNo();
            if (matchType == 1) {
                matchType = 1;
            } else if (matchType == 2) {
                matchType = 0;
            } else {
                return oddsRange;
            }
            String matchSwitchKey = String.format(ODDSSCOPE_MATCH_SWITCH, matchId, matchType);
            String oddsRangeKey = String.format(ODDSSCOPE_MATCH_PALY, matchId, playId, matchType);
            String matchSwitch = RcsLocalCacheUtils.getValue(matchSwitchKey, redisClient::get,60 * 1000L); //redisClient.get(matchSwitchKey);
            log.info("::{}::赔率范围取值key:{}--value:{}", betNo, matchSwitchKey, matchSwitch);
            if ("1".equals(matchSwitch)) {
                oddsRange = RcsLocalCacheUtils.getValue(oddsRangeKey, redisClient::get, 60 * 1000L); //redisClient.get(oddsRangeKey);
                log.info("::{}::赔率范围取值key:{}--value:{}", betNo, oddsRangeKey, oddsRange);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return oddsRange;
    }


    private Map<String, List<TOrderDetailExtDO>> getMongoOrderDetailExt(List<String> ids) {
        List<TOrderDetailExtDO> orderDetailExtDOList = tOrderDetailExtRepository.getOrderDetailExtByIds(ids);
        Map<String, List<TOrderDetailExtDO>> tOrderDetailExtMap = orderDetailExtDOList.stream().collect(Collectors.groupingBy(ext -> ext.getOrderNo()));
        return tOrderDetailExtMap;
    }
}
