package com.panda.sport.rcs.mgr.mq.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.mapper.*;
import com.panda.sport.rcs.mgr.mq.bean.OrderBeanVo;
import com.panda.sport.rcs.mgr.operation.order.CalcOrderAdapter;
import com.panda.sport.rcs.mgr.repository.TOrderDetailExtRepository;
import com.panda.sport.rcs.mgr.service.impl.ParamValidate;
import com.panda.sport.rcs.mgr.service.limit.LimitCallbackService;
import com.panda.sport.rcs.mgr.utils.TOrderDetailExtUtils;
import com.panda.sport.rcs.mgr.wrapper.ITOrderService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.TOrder;
import com.panda.sport.rcs.pojo.TOrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**
 * 业务30秒超时 订单取消，调用结算接口，回滚矩阵
 * 业务拒单：1：滚球接拒单
 * 2：MTS订单拒单
 * 订单状态没有修改
 *
 * @author :  max
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "queue_order_refusal",
        consumerGroup = "queue_order_refusal_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class BusOrderRefusalConsumer implements RocketMQListener<JSONObject>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private ProducerSendMessageUtils sendMessage;
    @Autowired
    StandardSportMarketCategoryMapper standardSportMarketCategoryMapper;
    @Autowired
    private TOrderMapper orderMapper;
    @Autowired
    private TOrderDetailMapper detailMapper;
    @Autowired
    ParamValidate paramValidate;

    @Autowired
    private TOrderDetailExtMapper tOrderDetailExtMapper;
    @Autowired
    private TOrderDetailExtRepository tOrderDetailExtRepository;

    @Autowired
    private CalcOrderAdapter calcOrderAdapter;

    private RedisClient redisClient;

    private String orderRefusalCacheKey = "rcs:risk:refusal:order";

    private Boolean isStart = false;

    @Autowired
    private RcsLockMapper lockMapper;
    @Autowired
    private ITOrderService orderService;
    @Autowired
    private TOrderDetailExtUtils tOrderDetailExtUtils;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }
    @Autowired
    private LimitCallbackService limitCallbackService;

    public BusOrderRefusalConsumer(RedisClient redisClient) {
//        super(MqConstants.RCS_BUS_ORDER_REFUSAL, "");
        this.redisClient = redisClient;
        startUpdateThread();
    }

    @Override
    public void onMessage(JSONObject src) {
        log.info("业务拒单收到:{}", src);
        JSONArray jsonArr = src.getJSONArray("data");
        for (int i = 0; i < jsonArr.size(); i++) {
            try {
                JSONObject jsonObject = jsonArr.getJSONObject(i);
                if (jsonObject == null) {
                    continue;
                }
                String orderNo = jsonArr.getJSONObject(i).getString("orderNo");
                String msgInfo = jsonArr.getJSONObject(i).getString("msgInfo");
                String status = jsonArr.getJSONObject(i).getString("status");
                List<String> secondaryLabelIdsList = new ArrayList<String>();
                //二级tag
                String  secondaryLabelIdsStr= jsonArr.getJSONObject(i).getString("secondaryLabelIdsList");
                if (StringUtils.isNotBlank(secondaryLabelIdsStr)) {
                    JSONArray arrays = JSONArray.parseArray(secondaryLabelIdsStr);
                    if (arrays!=null&&arrays.size()>0) {
                        for (int j = 0; i < arrays.size(); i++) {
                            secondaryLabelIdsList.add(arrays.getString(i));
                        }
                    }
                }

                //业务取消 如果是mts的会传此字段 用于通知mts 参数
                String cancelStatus = jsonArr.getJSONObject(i).getString("cancelStatus");
                if(StringUtils.isNotBlank(cancelStatus)){
                    redisClient.setExpiry("rcs:orderCancel:" + orderNo, cancelStatus, 7 * 24 * 60 * 60L);
                }
                if (StringUtils.isBlank(msgInfo)) {
                    msgInfo = "30S自动接拒单没有响应，业务拒单";
                }
                QueryWrapper<TOrder> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(TOrder::getOrderNo, orderNo);
                TOrder order = orderMapper.selectOne(queryWrapper);
                //if ((order != null && order.getOrderStatus() == 1) || time >= 60 * 1000) {
                if (order != null) {
                    long time = System.currentTimeMillis() - order.getCreateTime();
                    log.info("::{}::业务拒单处理时间差{}:详情:{}", orderNo,time, JSONObject.toJSONString(order));
                    if (NumberUtils.INTEGER_TWO.equals(order.getLimitType())) {
                        log.info("::{}::信用模式回滚限额", orderNo);
                        orderService.creditLimitCallback(orderNo);
                    }

                    OrderBeanVo orderBean = new OrderBeanVo();
                    BeanUtils.copyProperties(order, orderBean);
                    QueryWrapper<TOrderDetail> orderDetailQueryWrapper = new QueryWrapper<>();
                    orderDetailQueryWrapper.lambda().eq(TOrderDetail::getOrderNo, orderNo);
                    List<TOrderDetail> orderDetailList = detailMapper.selectList(orderDetailQueryWrapper);
                    List<OrderItem> orderItemList = new ArrayList<>();
                    for (TOrderDetail orderDetail : orderDetailList) {
                        if (new Integer(3).equals(orderDetail.getMatchType())) {
                            log.info("::{}::冠军玩法回滚限额",orderNo);
                            orderService.championLimitCallback(orderNo);
                        }
                        OrderItem orderItem = new OrderItem();
                        BeanUtils.copyProperties(orderDetail, orderItem);
                        //这里和下单参数保持一致 计算的时候会用到
                        orderItem.setOddFinally(orderItem.getOddsValue().toString());
                        orderItem.setOddsValue(BigDecimal.valueOf(orderItem.getOddsValue()).multiply(BigDecimal.valueOf(100000)).doubleValue());
                        orderItem.setOrderStatus(2);
                        Integer orderStatus;
                        if (tOrderDetailExtUtils.isSaveToMongo()) {
                          orderStatus = tOrderDetailExtRepository.queryOrderStatus(orderItem.getBetNo());
                        } else {
                          orderStatus = tOrderDetailExtMapper.queryOrderStatus(orderItem.getBetNo());
                        }
                        //暂停处理的单子PauseTime=-1 前端计数使用
                        if(null!=orderStatus&&Arrays.asList(-1,8,9).contains(orderStatus))orderItem.setPauseTime(-1);
                        orderItemList.add(orderItem);
                    }
                    orderBean.setItems(orderItemList);
                    orderBean.setSecondaryLabelIdsList(secondaryLabelIdsList);

                    //货量/期望值 反计算
                    //calculate(orderBean);

                    //更新状态
                    orderBean.setOrderStatus(2);
                    if("-3".equals(status)) {//操盘手动取消注单
                        orderBean.setInfoStatus(OrderInfoStatusEnum.TRADE_CANCEL_ORDER.getCode());
                    }else {
                        orderBean.setInfoStatus(OrderInfoStatusEnum.BS_REFUSE.getCode());
                    }
                    orderBean.setReason(msgInfo);
                    Integer seriesType = order.getSeriesType();
                    if (seriesType != null && seriesType > 1) {
                        // 串关额度回滚
                        limitCallbackService.seriesLimitCallback(orderNo);
                    }
                    sendMessage.sendMessage(MqConstants.RCS_ORDER_UPDATE, "bus_risk_send", orderNo, orderBean);
                    log.info("::{}::业务拒单完成:{}", orderNo,JSONObject.toJSONString(orderBean));
                } else {
                    log.info("::{}::业务拒单没找到相关订单信息,bean:{}", orderNo, JsonFormatUtils.toJson(src));
                    redisClient.hSet(orderRefusalCacheKey, orderNo, src.toString());
                    redisClient.setExpiry(orderRefusalCacheKey + "_" + orderNo, "1", 60L);
                }
            } catch (BeansException e) {
                log.error("拒单处理异常第{}:{}",jsonArr.getJSONObject(i),e.getMessage());
            }
        }

        return ;
    }

    public void startUpdateThread() {
        log.info("启动startUpdateThread");
        if (isStart) return;

        isStart = true;
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Map<String, String> allMap = redisClient.hGetAllByJson(orderRefusalCacheKey, String.class);
                        if (allMap != null && allMap.size() > 0) {

                            List<String> removeList = new ArrayList<String>();
                            for (String orderNo : allMap.keySet()) {
                                String exist = redisClient.get(orderRefusalCacheKey + "_" + orderNo);
                                if(StringUtils.isBlank(exist)){
                                    redisClient.hashRemove(orderRefusalCacheKey, orderNo);
                                    log.info("::{}::未查到拒单超时", orderNo);
                                }
                                QueryWrapper<TOrder> queryWrapper = new QueryWrapper<>();
                                queryWrapper.lambda().eq(TOrder::getOrderNo, orderNo);
                                TOrder order = orderMapper.selectOne(queryWrapper);
                                if (order != null) {
                                    removeList.add(orderNo);
                                }
                            }

                            for (String orderNo : removeList) {
                                Long flag = redisClient.hashRemove(orderRefusalCacheKey, orderNo);
                                if (flag > 0) {
                                    JSONObject json = JSONObject.parseObject(allMap.get(orderNo));
                                    json.put("desc_rcs", "业务拒单，第一次没有查到订单，现在重发");
                                    onMessage(json);
                                }
                            }

                        }

                        Thread.currentThread().sleep(1000L);
                    } catch (Exception e) {
                        log.error("启动startUpdateThread异常{}",e.getMessage(), e);
                    }
                }
            }
        }).start();
    }

}
