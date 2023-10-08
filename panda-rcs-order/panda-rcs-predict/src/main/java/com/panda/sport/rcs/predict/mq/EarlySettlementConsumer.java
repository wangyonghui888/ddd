package com.panda.sport.rcs.predict.mq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.pojo.RcsOperateMerchantsSet;
import com.panda.sport.rcs.predict.service.PredictService;
import com.panda.sport.rcs.predict.utils.RcsPredictMysqlFrequencyNacosConfig;
import com.panda.sport.rcs.service.IRcsOperateMerchantsSetService;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


/**
 * 订单提前结算
 *
 * @author joey
 * @since 2022-4-18 10:31:19
 */
@Slf4j
@Service
@TraceCrossThread
@RocketMQMessageListener(
        topic = RcsConstant.RCS_FORECAST_PRE_SETTLE_ORDER,
        consumerGroup = RcsConstant.PRE_SETTLE_HANDLE_STATUS_GROUP,
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class EarlySettlementConsumer implements RocketMQListener<OrderBean>, RocketMQPushConsumerLifecycleListener {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private PredictService predictService;

    @Autowired
    private IRcsOperateMerchantsSetService rcsOperateMerchantsSetService;

    @Autowired
    private RcsPredictMysqlFrequencyNacosConfig rcsPredictMysqlFrequencyNacosConfig;

    @Override
    public void onMessage(OrderBean orderBean) {
        try {
            log.info("::{}::订单提前结算业务推送提前结算订单,消息体{},提前结算状态:{}", orderBean.getOrderNo(), JSONObject.toJSONString(orderBean), orderBean.getItems().get(0).getOrderStatus());
            if (orderBean.getItems().get(0).getOrderStatus() != 1) {
                log.warn("::{}::订单提前结算订单状态异常 {}，流程结束！", orderBean.getOrderNo(), orderBean.getItems().get(0).getOrderStatus());
                return;
            }
            if (orderBean.getValidateResult() != 1) {
                log.warn("::{}::订单提前结算预测数据计算 接收到订单状态为没有确认,订单号", orderBean.getOrderNo());
                return;
            }

            //测试商户 跳过
            RcsOperateMerchantsSet merchantsSet = RcsLocalCacheUtils.getValue("rcsOperateMerchantsSet:" + orderBean.getTenantId(), (k) -> {
                LambdaQueryWrapper<RcsOperateMerchantsSet> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(RcsOperateMerchantsSet::getMerchantsId, String.valueOf(orderBean.getTenantId()));
                return rcsOperateMerchantsSetService.getOne(wrapper);
            }, 12 * 60 * 60 * 1000L);

            if (merchantsSet == null || merchantsSet.getStatus() == 0 || merchantsSet.getValidStatus() == 0) {
                log.warn("::{}::订单提前结算预测数据计算 测试商户不做处理,订单号商户：" + orderBean.getOrderNo(), JSONObject.toJSONString(merchantsSet));
                return;
            }

            //VIP用户 跳过
            if (orderBean.getVipLevel() != null && orderBean.getVipLevel() == 1) {
                log.warn("::{}::订单提前结算预测数据计算 VIP用户 跳过 vip:{}", orderBean.getOrderNo(), orderBean.getVipLevel());
                return;
            }

            //默认开启 设置为0 则关闭
            Integer calculateIsOpen = rcsPredictMysqlFrequencyNacosConfig.getCalcStatus();
            if (ObjectUtils.isEmpty(calculateIsOpen) && 0 == calculateIsOpen) {
                log.warn("::{}::订单提前结算预测数据计算,开关未开启", orderBean.getOrderNo());
                return;
            }
            //拒单不处理
            if (orderBean.getOrderStatus() != 1) {
                log.warn("::{}::订单提前结算预测数据计算 orderStatus不成功，不计算 ", orderBean.getOrderNo());
                return;
            }
            //拒单
            if (orderBean.getItems().get(0).getValidateResult() != 1 && orderBean.getValidateResult() != 1) {
                log.warn("::{}::订单提前结算预测数据计算getValidateResult 拒单", orderBean.getOrderNo());
                return;
            }


            boolean calculateStatus = redisClient.setNX("rcs:predict:calculate:cancel:bet:" + orderBean.getOrderNo(), "1", 3 * 24 * 60 * 60L);
            /**
             * 货量 期望 回滚
             * 说明:如先成功 再失败:calculateStatus会标志为1  则做回滚
             *     如先失败 再成功:calculateStatus为空,不做回滚,  再成功更新的时候,上面就return了 不做下面的逻辑(也不会增加货量)
             */
            String status = redisClient.get("rcs:predict:calculate:bet:" + orderBean.getOrderNo());
            if (StringUtils.isBlank(status)) {
                log.info("预测数据计算 业务拒单反计算-跳过,之前并无成功计算:{}", JSONObject.toJSONString(orderBean));
                return;
            }

            orderBean.getItems().forEach(item -> item.setOddsValue(BigDecimal.valueOf(item.getOddsValue()).multiply(BigDecimal.valueOf(100000)).doubleValue()));
            if (calculateStatus) {
                //单关
                predictService.calculate(orderBean, -1);
            } else {
                log.info("预测数据计算 反计算订单已处理,不再做重复计算,订单号:" + orderBean.getOrderNo());
            }
        } catch (Exception e) {
            log.info("预测数据计算 反计算订单已处理,异常！,订单号:{},异常信息：{}", orderBean.getOrderNo(), e.getMessage());
        }
        log.info("::{}::提前结算完成", orderBean.getOrderNo());
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }
}
