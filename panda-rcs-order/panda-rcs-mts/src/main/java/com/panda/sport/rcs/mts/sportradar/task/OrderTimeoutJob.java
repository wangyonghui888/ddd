package com.panda.sport.rcs.mts.sportradar.task;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.mapper.TOrderMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.mts.sportradar.builder.TicketBuilderHelper;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketResponseHandler;
import com.panda.sport.rcs.mts.sportradar.wrapper.ITOrderDetailService;
import com.panda.sport.rcs.mts.sportradar.wrapper.RcsMtsOrderExtService;
import com.panda.sport.rcs.pojo.RcsMtsOrderExt;
import com.panda.sport.rcs.pojo.TOrder;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.sportradar.mts.sdk.api.TicketCancel;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 定时扫描MTS超时订单
 *
 * @author carver
 */
@Slf4j
//@Component
public class OrderTimeoutJob {

    @Autowired
    private ITOrderDetailService itOrderDetailService;

    @Autowired
    private ProducerSendMessageUtils sendMessage;

    @Autowired
    private RcsMtsOrderExtService rcsMtsOrderExtService;


    @Autowired
    private TOrderMapper orderMapper;

    @Autowired
    TOrderDetailMapper orderDetailMapper;


    /**
     * 以下的方法将以一个固定延迟时间15秒钟调用一次执行，这个周期是以上一个调用任务的完成时间为基准，在上一个任务完成之后，15s后再次执行
     */
//    @Scheduled(fixedDelay = 5000)
    public void scheduledOrderTimeoutData() {
        try {
            // 1.获取MTS待处理的订单
            QueryWrapper<TOrderDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(TOrderDetail::getRiskChannel, 2);
            queryWrapper.lambda().eq(TOrderDetail::getOrderStatus, 0);
            queryWrapper.lambda().gt(TOrderDetail::getBetTime, System.currentTimeMillis() - (12 * 60 * 60 * 1000L));
            log.info("执行定时任务.......queryWrapper:{}",queryWrapper);
            List<TOrderDetail> tOrderDetailList = itOrderDetailService.list(queryWrapper);
            if (tOrderDetailList != null && tOrderDetailList.size() > 0 ) {
            	tOrderDetailList.forEach(tOrderDetail -> {
            		try {
            			Long createTime = tOrderDetail.getCreateTime();
                        Long current = System.currentTimeMillis();
                        int second = compareTimeSecond(current, createTime);
                        //超过15秒待处理的MTS订单数据
                        if (second >= 15) {
                        	log.info("tOrderDetail:{}",JSONObject.toJSONString(tOrderDetailList));
                            String orderNo = tOrderDetail.getOrderNo();
                            //0：待处理  1：已接单  2：拒单
                            int rcsOrderStatus = 2;
                            // 2.发送MQ，异步通知业务处理注单状态,拒单
                            Map<String, Object> map = Maps.newHashMap();
                            map.put("orderNo", orderNo);
                            map.put("status", rcsOrderStatus);
                            map.put("infoStatus", OrderInfoStatusEnum.MTS_REFUSE.getCode());
                            //只有此处为-4  业务那边需要特殊处理
                            map.put("infoCode", -4);
                            map.put("infoMsg", "MTS拒单:15秒未响应");
                            map.put("handleTime",System.currentTimeMillis());
                            sendMessage.sendMessage(MqConstants.RCS_BUS_MTS_ORDER_STATUS, map);
                            
                            // 5.记录订单记录
                            LambdaQueryWrapper<RcsMtsOrderExt> wrapper = new LambdaQueryWrapper<>();
                            wrapper.eq(RcsMtsOrderExt::getOrderNo, orderNo);
                            RcsMtsOrderExt rcsMtsOrderExt = rcsMtsOrderExtService.getOne(wrapper);
                            if(rcsMtsOrderExt != null) {
                            	 // 3.通知MTS此单为拒单
                                TicketCancelSender ticketCancelSender = TicketResponseHandler.getTicketCancelSender();
                                BuilderFactory builderFactory = TicketResponseHandler.getBuilderFactory();
                                TicketCancel ticketCancel = new TicketBuilderHelper(builderFactory).getTicketCancel(orderNo, "102");
                                ticketCancelSender.send(ticketCancel);
                            }else {//订单没有初始化，说明订单没有发送到MTS
                            	log.warn("当前订单初始化失败，未发送MTS，{}，此处发送取消消息",orderNo);
                            }
                            // 4.更新MTS注单状态，拒单
                            /*itOrderDetailService.modifyMtsOrder(orderNo, rcsOrderStatus);*/
                            if (rcsMtsOrderExt == null) {
                                rcsMtsOrderExt = new RcsMtsOrderExt();
                                rcsMtsOrderExt.setOrderNo(orderNo);
                                rcsMtsOrderExt.setStatus("REJECTED");
                                rcsMtsOrderExt.setResult("超时15s未处理的订单，定时扫描将此单设置为拒单！");

                                rcsMtsOrderExt.setCancelStatus(1);
                                rcsMtsOrderExt.setCancelId(102);
                                rcsMtsOrderExt.setCancelResult("");
                                rcsMtsOrderExtService.addMtsOrder(rcsMtsOrderExt);
                            } else {
                                rcsMtsOrderExt.setCancelStatus(1);
                                rcsMtsOrderExt.setCancelId(102);
                                rcsMtsOrderExt.setResult(rcsMtsOrderExt.getResult() + ",超时15s更新!");
                                rcsMtsOrderExtService.updateById(rcsMtsOrderExt);
                            }

                            //构建订单详情信息
//                            OrderItem orderItem = BeanCopyUtils.copyProperties(tOrderDetail, OrderItem.class);
//                            List<OrderItem> orderItemList = Lists.newArrayList();
//                            orderItem.setOddsValue(orderItem.getOddsValue() * 100000);
//                            orderItem.setModifyTime(System.currentTimeMillis());
//                            orderItemList.add(orderItem);

                            LambdaQueryWrapper<TOrderDetail> orderDetailWrapper = new LambdaQueryWrapper<>();
                            orderDetailWrapper.eq(TOrderDetail::getOrderNo, orderNo);
                            List<TOrderDetail> orderDetailList = orderDetailMapper.selectList(orderDetailWrapper);

                            //构建订单详情信息
                            List<OrderItem> list = BeanCopyUtils.copyPropertiesList(orderDetailList,OrderItem.class);
                            for (OrderItem item : list) {
                                item.setOddsValue(new BigDecimal(String.valueOf(item.getOddsValue())).multiply(new BigDecimal("100000")).doubleValue());
                                item.setValidateResult(rcsOrderStatus);
                                item.setOrderStatus(rcsOrderStatus);
                                item.setModifyTime(System.currentTimeMillis());
                            }

                            //构建订单信息
                            QueryWrapper<TOrder> orderQueryWrapper = new QueryWrapper<>();
                            orderQueryWrapper.lambda().eq(TOrder::getOrderNo,orderNo);
                            TOrder order =orderMapper.selectOne(orderQueryWrapper);
                            OrderBean orderBean = new OrderBean();
                            BeanCopyUtils.copyProperties(order,orderBean);
                            orderBean.setOrderStatus(rcsOrderStatus);
                            orderBean.setReason("超时15s未处理的订单，定时扫描将此单设置为拒单！");
                            orderBean.setInfoStatus(OrderInfoStatusEnum.MTS_REFUSE.getCode());
                            orderBean.setOrderNo(orderNo);
                            orderBean.setItems(list);
                            log.info("MTS 15秒拒单:{}", JSONObject.toJSONString(orderBean));
                            sendMessage.sendMessage(MqConstants.RCS_ORDER_UPDATE + ",," + orderNo,orderBean);
                        }
            		}catch (Exception e) {
            			log.error(e.getMessage(),e);
					}
            		
            	});
            }
        } catch (Exception e) {
        	log.error(e.getMessage(),e);
        }
    }

    private static int compareTimeSecond(long date1, long date2) {
        long cha = Math.abs(date1 - date2);
        return (int) (cha / 1000);
    }
}
