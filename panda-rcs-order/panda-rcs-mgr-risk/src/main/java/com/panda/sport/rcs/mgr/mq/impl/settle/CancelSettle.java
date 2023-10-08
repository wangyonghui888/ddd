package com.panda.sport.rcs.mgr.mq.impl.settle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.base.Stopwatch;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.OrderStatusEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.TOrderMapper;
import com.panda.sport.rcs.mgr.constant.RcsCacheContant;
import com.panda.sport.rcs.mgr.enums.SettleOperateStatusEnum;
import com.panda.sport.rcs.mgr.mq.bean.CancelSettleBean;
import com.panda.sport.rcs.mgr.service.impl.ParamValidate;
import com.panda.sport.rcs.mgr.utils.RealTimeControlUtils;
import com.panda.sport.rcs.mgr.utils.StringUtil;
import com.panda.sport.rcs.mgr.wrapper.ITOrderService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.AmountTypeVo;
import com.panda.sport.rcs.pojo.enums.MatchEventConfigEnum;
import com.panda.sport.rcs.utils.RcsMonitorConsumerUtils;
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
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.mq.impl.settle
 * @Description :  取消结算
 * @Date: 2020-12-03 上午 9:58
 * 1、取消结算
 * 把t_settle表中的order_no的操作状态设置为2，操作赛事盈利表
 * 多线程操作，其中有一个失败，批量失败
 * 每次会比较时间戳，操作时间大于当前数据库时间才会操作
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service
@TraceCrossThread
@RocketMQMessageListener(
        topic = RcsConstant.RISK_ORDER_STATUS_OPERATE,
        consumerGroup = RcsConstant.RISK_ORDER_STATUS_OPERATE,
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class CancelSettle implements RocketMQListener<CancelSettleBean>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private ParamValidate paramValidate;

    @Autowired
    private ITOrderService orderService;

    @Autowired
    private TOrderMapper tOrderMapper;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RealTimeControlUtils realTimeControlUtils;

    @Autowired
    private ProducerSendMessageUtils sendMessage;
    private static String jumpPointsConfigKey = "rsc:jump:point:config:key";

    //投注成功，key：orderNo，value：sendTime
    private Map<String, Long> basketBetSuccessMap = new ConcurrentHashMap<String, Long>();

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Override
    public void onMessage(CancelSettleBean msg) {
        try {
            String traceId = StringUtil.getUUID();
            MDC.put("X-B3-TraceId", traceId);
            log.info("::{}::接收到CancelSettle结算mq,实体bean{}",msg.getOrderNo(),JSONObject.toJSONString(msg));
            Long filterAmount = realTimeControlUtils.getAutomaticFilterAmount();

            /**
             * 把多个订单号拆分
             */
            String[] orderNos = msg.getOrderNo().split(",");
            //如果操盘后台过滤的开关打开了
            String val = redisClient.get(jumpPointsConfigKey);
            if (StringUtils.isNotBlank(val)) {
                Map<String, String> map = JSON.parseObject(val, Map.class);
                //如果操盘后台过滤的开关打开了
                if (StringUtils.equals("1", map.get("key"))) {
                    String amount = map.get("amount");
                    if (orderNos.length == 1 && StringUtils.isNotBlank(amount)
                            && msg.getBetAmount() != null && Long.valueOf(amount) * 100 > new Double(Double.parseDouble(msg.getBetAmount())).longValue()) {
                        log.info("::{}::当前订单金额小于操作后台设置过滤金额，不处理，设置金额={}，订单金额={}", msg.getOrderNo(), Long.valueOf(amount) * 100, msg.getBetAmount());
                        return;
                    }
                }
            }

            if (orderNos.length == 1 && filterAmount != null && msg.getBetAmount() != null && filterAmount > new Double(Double.parseDouble(msg.getBetAmount())).longValue()) {
                log.info("::{}::当前订单金额小于设置过滤金额，不处理，设置金额={}，订单金额={}", msg.getOrderNo(), filterAmount, msg.getBetAmount());
                return;
            }
            if(SettleOperateStatusEnum.isBetSuccess(msg.getOperateType())){
                CancelSettleBean bean = new CancelSettleBean();
                bean.setOperateType(msg.getOperateType());
                bean.setOrderNo(msg.getOrderNo());
                bean.setSendTime(msg.getSendTime());
                betSuccessful(msg.getOrderNo(), msg.getSendTime());
            }
        } catch (Exception ex) {
            log.error("::{}:: 接收到结算mq:{}",msg.getOrderNo(),ex.getMessage(),ex);
        }
    }

    private boolean betSuccessful(String orderNo, Long sendTime) {
        OrderBean orderBean = getOrderBean(orderNo);
        if (ObjectUtils.isEmpty(orderBean)) {
            basketBetSuccessMap.put(orderNo, sendTime);
            log.warn(" ::{}::无订单信息 ", orderNo);
            return false;
        }
        if (null == orderBean || OrderStatusEnum.ORDER_WAITING.getCode().equals(orderBean.getOrderStatus())) {
            basketBetSuccessMap.put(orderNo, sendTime);
            log.warn(" ::{}::无订单信息或状态为空", orderNo);
            return false;
        }
        if (CollectionUtils.isEmpty(orderBean.getItems())) {
            basketBetSuccessMap.put(orderNo, sendTime);
            log.warn("::{}:: 无订单详细信息", orderNo);
            return false;
        }
        sendBasketBetMsg(orderBean);
        return true;
    }

    private void sendBasketBetMsg(OrderBean orderBean) {
        if(CollectionUtils.isEmpty(orderBean.getItems())){
            log.warn("::{}:: 订单明细为空不处理！", orderBean.getOrderNo());
            return;
        }
        orderBean.getItems().forEach(item->{
            item.setOddsValue(new BigDecimal(item.getOddsValue().toString()).multiply(new BigDecimal("100000")).doubleValue());
            if(null == item.getVolumePercentage()){
                String volumePercentageKey = String.format("rcs:order:volumePercentage:%s", orderBean.getOrderNo());
                if(redisClient.exist(volumePercentageKey)){
                    String volumePercentageValue = redisClient.get(volumePercentageKey);
                    AmountTypeVo amountTypeVo = JSONObject.parseObject(volumePercentageValue,AmountTypeVo.class);
                    item.setVolumePercentage(amountTypeVo.getVolumePercentage());
                }
            }
        });
        OrderItem orderItem =orderBean.getItems().get(0);
        // 其他球种投注成功才计算货量
        if (SportIdEnum.isBasketball(orderItem.getSportId()) ||
                RcsConstant.OTHER_CAN_TRADE_SPORT.contains(orderItem.getSportId())) {
//            if (NumberUtils.INTEGER_TWO.equals(orderBean.getItems().get(0).getSportId())){
            ExtendBean extendBean = paramValidate.buildExtendBean(orderBean, orderItem);
            orderBean.setExtendBean(extendBean);
            orderBean.setValidateResult(MatchEventConfigEnum.ORDER_CHECKED_VALIDATERESULT.getValue());
            orderBean.getExtendBean().setValidateResult(MatchEventConfigEnum.ORDER_CHECKED_VALIDATERESULT.getValue());
            RcsMonitorConsumerUtils.handleApi(RcsConstant.TAG_MQ_ORDER_PREDICT_CALC, () -> {
                sendMessage.sendMessage(MqConstants.RCS_ORDER_REALTIMEVOLUME, MqConstants.RCS_ORDER_REALTIMEVOLUME_TAG, orderBean.getOrderNo(), orderBean);
                log.info("::{}::篮球其他球总跳分跳水处理完成",orderBean.getOrderNo());
                return null;
            });
//            }
            // VIP等级：0-非VIP，1-VIP用户
            if (!NumberUtils.INTEGER_ONE.equals(orderBean.getVipLevel())) {
                sendMessage.sendMessage(RcsConstant.RISK_ORDER_TRIGGER_CHANGE, String.valueOf(System.currentTimeMillis()), orderBean.getOrderNo(), orderBean);
            }
        }
        // 其他球种 跳分
        if (RcsConstant.OTHER_BALL.contains(orderItem.getSportId())) {
            orderBean.setValidateResult(MatchEventConfigEnum.ORDER_CHECKED_VALIDATERESULT.getValue());
            // VIP等级：0-非VIP，1-VIP用户
            if (!NumberUtils.INTEGER_ONE.equals(orderBean.getVipLevel())) {
                sendMessage.sendMessage(RcsConstant.RISK_ORDER_TRIGGER_CHANGE, String.valueOf(System.currentTimeMillis()), orderBean.getOrderNo(), orderBean);
            }
        }
        //ws推送订单优化
        try {
            log.info("::{}::业务后置检查ws推送", orderBean.getOrderNo());
            String key = "rcs_risk_order_instant_ws:%s";
            key = String.format(key, orderBean.getOrderNo());
            String value = redisClient.get(key);
            if (StringUtils.isBlank(value)) {
                log.warn("::{}:: 业务后置检查未查到ws暂存数据", orderBean.getOrderNo());
                return;
            }
            OrderBean orderBeanWs = JSONObject.parseObject(value, OrderBean.class);
            //这种状态标识业务后置检查通过 用于推送ws判断通过
            orderBeanWs.setOrderStatus(100);
            orderService.sendOrderWs(orderBeanWs);
            log.info("::{}:: sendBasketBetMsg 处理结果完成", orderBean.getOrderNo());
        } catch (Exception ex) {
            log.error("::{}::sendBasketBetMsg::业务后置检查ws推送异常:{}", orderBean.getOrderNo(), ex.getMessage(), ex);
        }
    }
    
    
    /**
     * 根据订单号获取订单信息
     * @param orderNo
     * @return
     */
    public OrderBean getOrderBean(String orderNo) {
        
        try {
            Stopwatch started = Stopwatch.createStarted();
            //因redis保存的订单赔率是原始赔率，数据库中保存的赔率是除以100000后的值，因接拒3.0后不再将订单信息保存redis
            OrderBean orderBean = tOrderMapper.queryOrderAndDetailByOrderNo(orderNo);
            if (!ObjectUtils.isEmpty(orderBean)) {
                log.info("::{}:: getOrderBean 查询订单信息耗时：{}", orderBean.getOrderNo(),started.elapsed());
                return orderBean;
            }
            //todo 因后置检查比订单保存快，所以做了次操作
            Thread.sleep(1000);
            started.reset();
            orderBean = tOrderMapper.queryOrderAndDetailByOrderNo(orderNo);
            if (!ObjectUtils.isEmpty(orderBean)) {
                log.info("::{}:: getOrderBean 查询订单信息耗时：{}", orderBean.getOrderNo(),started.elapsed());
                return orderBean;
            }
            log.info("::{}:: getOrderBean 查询无此订单", orderNo);
        } catch (Exception e) {
            log.info("::{}:: getOrderBean 异常报错:", orderNo,e);
        }
        return null;
    }

}

