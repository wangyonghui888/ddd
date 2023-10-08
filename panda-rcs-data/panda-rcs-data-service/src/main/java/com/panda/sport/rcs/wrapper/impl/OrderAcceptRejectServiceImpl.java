//package com.panda.sport.rcs.wrapper.impl;
//
//import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.panda.sport.data.rcs.dto.ExtendBean;
//import com.panda.sport.data.rcs.dto.OrderBean;
//import com.panda.sport.data.rcs.dto.OrderDetailPO;
//import com.panda.sport.data.rcs.dto.OrderItem;
//import com.panda.sport.data.rcs.dto.SettleItem;
//import com.panda.sport.rcs.common.MqConstants;
//import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
//import com.panda.sport.rcs.enums.OrderStatusEnum;
//import com.panda.sport.rcs.mapper.StandardSportMarketCategoryMapper;
//import com.panda.sport.rcs.mapper.TOrderDetailExtMapper;
//import com.panda.sport.rcs.mapper.TOrderMapper;
//import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
//import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
//import com.panda.sport.rcs.pojo.TOrder;
//import com.panda.sport.rcs.pojo.TOrderDetail;
//import com.panda.sport.rcs.pojo.TOrderDetailExt;
//import com.panda.sport.rcs.wrapper.OrderAcceptRejectService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
///**
// * 接拒单服务
// *
// * @author carver
// */
//@Component
//@Slf4j
//public class OrderAcceptRejectServiceImpl implements OrderAcceptRejectService {
//    @Autowired
//    private ProducerSendMessageUtils sendMessage;
//    @Autowired
//    StandardSportMarketCategoryMapper standardSportMarketCategoryMapper;
//    @Autowired
//    private TOrderMapper orderMapper;
//    @Autowired
//    TOrderDetailExtMapper tOrderDetailExtMapper;
//
//
//    /**
//     * @return void
//     * @Description 发送mq
//     * @Param [vo]
//     * @Author toney
//     * @Date 10:24 2020/2/1
//     **/
//    @Override
//    public void sendMessage(List<String> ids, int state, Map<String, String> mapRefuse) {
//        if (ids == null || ids.size() == 0) return;
//        List<TOrder> orderList = orderMapper.queryByOrderDetailExtAndIds(ids);
//        for (TOrder order : orderList) {
//        	if(!"0".equals(String.valueOf(order.getOrderStatus()))) {//0 待处理
//        		continue;
//        	}
//            OrderBean orderBean = new OrderBean();
//            BeanUtils.copyProperties(order, orderBean);
//            List<OrderItem> orderItemList = new ArrayList<>();
//            List<OrderDetailPO> orderDetailsList = Lists.newArrayList();
//            for (TOrderDetail orderDetail : order.getOrderDetailList()) {
//                OrderItem orderItem = new OrderItem();
//                BeanUtils.copyProperties(orderDetail, orderItem);
//                orderItem.setTradeType(0);
//                //orderItem.setOddsValue(orderItem.getOddsValue() * 100000);
//                BigDecimal oddsValue = new BigDecimal(orderItem.getOddsValue().toString()).multiply(new BigDecimal(100000));
//                orderItem.setOddsValue(oddsValue.doubleValue());
//                orderItem.setValidateResult(state);
//                orderItemList.add(orderItem);
//                orderBean.setExtendBean(buildExtendBean(orderBean, orderItem));
//
//                OrderDetailPO orderDetailPO = new OrderDetailPO();
//                orderDetailPO.setBetNo(orderItem.getBetNo());
//                orderDetailPO.setPlayId(orderItem.getPlayId().longValue());
//                orderDetailPO.setMarketId(orderItem.getMarketId());
//                orderDetailsList.add(orderDetailPO);
//            }
//            orderBean.setItems(orderItemList);
//            orderBean.setOrderStatus(state);
//            if (OrderStatusEnum.ORDER_ACCEPT.getCode() == state) {
//                //滚球接单
//                orderBean.getExtendBean().setValidateResult(1);
//                orderBean.setValidateResult(1);
//                orderBean.setInfoStatus(OrderInfoStatusEnum.SCROLL_PASS.getCode());
//            } else if (OrderStatusEnum.ORDER_REJECT.getCode() == state) {
//                //滚球拒单
//                String reason = mapRefuse.getOrDefault(orderBean.getOrderNo(), "滚球拒单");
//                orderBean.setInfoStatus(OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
//                orderBean.setReason(reason);
//            }
//            //修改订单信息
//            sendMessage.sendMessage(MqConstants.RCS_ORDER_UPDATE, "task_accept_order", orderBean.getOrderNo(), orderBean);
//            //通知业务处理注单状态
//            Map<String, Object> map = Maps.newHashMap();
//            map.put("orderNo", orderBean.getOrderNo());
//            map.put("status", state);
//            sendMessage.sendMessage(MqConstants.RCS_BUS_MTS_ORDER_STATUS, map);
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void updateOrderDetailExtStatus(Set<String> rejectOrders, Set<Long> acceptIds) {
//        //修改订单状态
//        if (rejectOrders.size() > 0) {
//            UpdateWrapper<TOrderDetailExt> rejectUpdateWrapper = new UpdateWrapper<>();
//            rejectUpdateWrapper.setSql("update_time = now()");
//            rejectUpdateWrapper.in("order_no", rejectOrders);
//            tOrderDetailExtMapper.update(getUpdateCondition(2), rejectUpdateWrapper);
//        }
//        if (acceptIds.size() > 0) {
//            UpdateWrapper<TOrderDetailExt> acceptUpdateWrapper = new UpdateWrapper<>();
//            acceptUpdateWrapper.setSql("update_time = now()");
//            acceptUpdateWrapper.ne("order_status", 2).in("id", acceptIds);
//            tOrderDetailExtMapper.update(getUpdateCondition(1), acceptUpdateWrapper);
//        }
//    }
//
//    private TOrderDetailExt getUpdateCondition(Integer orderStatus) {
//        TOrderDetailExt ext = new TOrderDetailExt();
//        ext.setOrderStatus(orderStatus);
//        ext.setHandleStatus(1);
//        ext.setUpdateTime(new Date());
//        return ext;
//    }
//
//    /**
//     * @return com.panda.sport.data.rcs.dto.ExtendBean
//     * @Description 根据orderItem 获取扩展 orderBean
//     * @Param [bean, item]
//     * @Author max
//     * @Date 11:15 2019/12/11
//     **/
//    public ExtendBean buildExtendBean(OrderBean bean, OrderItem item) {
//        ExtendBean extend = new ExtendBean();
//        extend.setSeriesType(bean.getSeriesType());
//        extend.setItemId(item.getBetNo());
//        extend.setOrderId(item.getOrderNo());
//        extend.setBusId(String.valueOf(bean.getTenantId()));
//        extend.setHandicap(item.getMarketValue());
//        extend.setCurrentScore(item.getScoreBenchmark());
//        //item  1 ：早盘 ，2： 滚球盘， 3： 冠军盘
//        extend.setIsScroll(String.valueOf(item.getMatchType()).equals("1") ? "0" : "1");
//        extend.setMatchId(String.valueOf(item.getMatchId()));
//        extend.setPlayId(item.getPlayId() + "");
//        extend.setSelectId(String.valueOf(item.getPlayOptionsId()));
//        extend.setSportId(String.valueOf(item.getSportId()));
//        extend.setUserId(String.valueOf(item.getUid()));
//        extend.setOdds(String.valueOf(item.getHandleAfterOddsValue()));
//        extend.setMarketId(item.getMarketId().toString());
//
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("id", item.getPlayId());
//        params.put("sportId", "1");
//        StandardSportMarketCategory standardSportMarketCategory = standardSportMarketCategoryMapper.queryCategoryInfoByMap(params);
//        if (standardSportMarketCategory != null) {
//            //阶段
//            extend.setPlayType(standardSportMarketCategory.getTheirTime().toString());
//        }
//        if (item.getBetAmount() != null) {
//            extend.setOrderMoney(item.getBetAmount());
//            extend.setCurrentMaxPaid(new BigDecimal(item.getBetAmount()).multiply((new BigDecimal(extend.getOdds()).subtract(new BigDecimal(1)))).longValue());
//        } else {
//            extend.setOrderMoney(0L);
//            extend.setCurrentMaxPaid(0L);
//        }
//        extend.setItemBean(item);
//
//        if (StringUtils.isBlank(extend.getHandicap())) {
//            extend.setHandicap("0");
//        }
//        if (StringUtils.isBlank(extend.getCurrentScore())) {
//            extend.setCurrentScore("0:0");
//        }
//        return extend;
//    }
//}
