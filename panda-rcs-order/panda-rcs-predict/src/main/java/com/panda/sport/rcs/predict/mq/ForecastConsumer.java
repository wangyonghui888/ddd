package com.panda.sport.rcs.predict.mq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.log.annotion.monnitor.MonitorAnnotion;
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
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * forecast计算 消费
 * @author  lithan
 * @since  2021-2-21 10:31:19
 */
@Component
@Slf4j
@MonitorAnnotion(code = "MQ_ORDER_PREDICT_CALC")
@TraceCrossThread
@RocketMQMessageListener(
        topic = MqConstants.RCS_ORDER_REALTIMEVOLUME,
        consumerGroup = MqConstants.RCS_ORDER_REALTIMEVOLUME,
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class ForecastConsumer implements RocketMQListener<OrderBean>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(160);
        defaultMQPushConsumer.setConsumeThreadMax(320);
    }

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
            MDC.put("X-B3-TraceId", UUID.randomUUID().toString().replace(",", ""));

            String invalidOrder = "rcs:invalid:order:first:" + orderBean.getOrderNo();
            log.info("::{}::无效主动那数据,KEY", invalidOrder);
            if(redisClient.exist(invalidOrder)) {
                log.warn("::{}::无效主动那数据,订单号", orderBean.getOrderNo());
                return;
            }


            log.info("::{}::预测数据计算 收到订单号 消息详情: {}", orderBean.getOrderNo(), JSONObject.toJSONString(orderBean));

            if (orderBean.getValidateResult() != 1) {
                log.warn("::{}::预测数据计算 接收到订单状态为没有确认,订单号", orderBean.getOrderNo());
                return;
            }

            //测试商户 跳过
            RcsOperateMerchantsSet merchantsSet = RcsLocalCacheUtils.getValue("rcsOperateMerchantsSet:"+orderBean.getTenantId(),(k)->{
                LambdaQueryWrapper<RcsOperateMerchantsSet> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(RcsOperateMerchantsSet::getMerchantsId, String.valueOf(orderBean.getTenantId()));
                return rcsOperateMerchantsSetService.getOne(wrapper);
            }, 12 * 60 * 60 * 1000L);

            if (merchantsSet == null || merchantsSet.getStatus() == 0 || merchantsSet.getValidStatus() == 0){
                log.warn("::{}::预测数据计算 测试商户不做处理,订单号商户：" + orderBean.getOrderNo(), JSONObject.toJSONString(merchantsSet));
                return ;
            }

            //VIP用户 跳过
            if (orderBean.getVipLevel() != null && orderBean.getVipLevel() == 1) {
                log.warn("::{}::预测数据计算 VIP用户 跳过 vip:{}", orderBean.getOrderNo(), orderBean.getVipLevel());
                return;
            }

            //默认开启 设置为0 则关闭
            Integer calculateIsOpen = rcsPredictMysqlFrequencyNacosConfig.getCalcStatus();
            if (ObjectUtils.isEmpty(calculateIsOpen) && 0 == calculateIsOpen) {
                log.warn("::{}::预测数据计算,开关未开启", orderBean.getOrderNo());
                return;
            }
            //拒单不处理
            if (orderBean.getOrderStatus() != 1) {
                log.warn("::{}::预测数据计算 orderStatus不成功，不计算 ", orderBean.getOrderNo());
                return;
            }
            //拒单
            if (orderBean.getItems().get(0).getValidateResult() != 1 && orderBean.getValidateResult() != 1) {
                log.warn("::{}::预测数据计算getValidateResult 拒单", orderBean.getOrderNo());
                return ;
            }

            /**
             * 针对那种  取消订单先消费的  如果已经收到了取消订单  这里就不继续往下走
             */
            String cancelStatus = redisClient.get("rcs:predict:calculate:cancel:bet:" + orderBean.getOrderNo());
            if (!StringUtils.isBlank(cancelStatus)) {
                log.info("::{}::预测数据计算 先收到过  取消订单的mq，跳过处理 ", orderBean.getOrderNo());
            }
            //只计算一次
            boolean calculateStatus = redisClient.setNX("rcs:predict:calculate:bet:" + orderBean.getOrderNo(), "1", 24 * 60 * 60L);
            if (!calculateStatus) {
                log.warn("::{}::预测数据计算 订单已处理,不再做重复计算 ", orderBean.getOrderNo());
                return ;
            }

            if (orderBean.getSeriesType() != 1) {
                //串关
                predictService.calculateSeries(orderBean, 1);
            } else {
                //单关
                predictService.calculate(orderBean, 1);
            }
            log.info("::{}::预测数据计算完成 ", orderBean.getOrderNo());
        } catch (Exception e) {
            log.error("::{}::预测数据计算  MQ异常：{}{}", orderBean.getOrderNo(), e.getMessage(), e);
            redisClient.delete("Rcs:realVolume:queue:" + orderBean.getOrderNo());
        }
        return ;
    }

   // @PostConstruct
     private void test(){
       // String data = "{\"acceptOdds\":2,\"createTime\":1647006200097,\"currencyCode\":\"CNY\",\"deviceType\":2,\"handleStatus\":0,\"infoStatus\":0,\"ip\":\"165.84.166.145\",\"items\":[{\"betAmount\":1000,\"betAmount1\":10,\"betNo\":\"48815887628566\",\"betTime\":1647006200097,\"createTime\":1647006200097,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2022-03-11\",\"handleAfterOddsValue\":1.63,\"handleAfterOddsValue1\":1.63,\"handleStatus\":0,\"handledBetAmout\":10,\"marketId\":144944503023610913,\"marketType\":\"EU\",\"marketValue\":\"0.5\",\"matchId\":3122991,\"matchInfo\":\"结算3.11_20:05 v 结算3.11客队\",\"matchName\":\"结算3.11_20:05\",\"matchProcessId\":0,\"matchType\":2,\"maxWinAmount\":19.83,\"modifyTime\":1647006200097,\"modifyUser\":\"系统\",\"oddFinally\":\"1.63\",\"oddsValue\":163000.0,\"orderNo\":\"3294012400198658\",\"orderStatus\":0,\"otherOddsValue\":236000.0,\"otherScore\":\"0:0\",\"paidAmount\":1630.00,\"paidAmount1\":16.30,\"placeNum\":1,\"platform\":\"PA\",\"playId\":2,\"playName\":\"全场大小\",\"playOptions\":\"Over\",\"playOptionsId\":143158755610247456,\"playOptionsName\":\"大 0.5\",\"scoreBenchmark\":\"0:0\",\"sportId\":1,\"sportName\":\"足球\",\"subPlayId\":\"2\",\"tournamentId\":835439,\"tradeType\":0,\"turnamentLevel\":1,\"uid\":407419678185623552,\"validateResult\":0},{\"betAmount\":1000,\"betAmount1\":10,\"betNo\":\"41528407946121\",\"betTime\":1647006200097,\"createTime\":1647006200097,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2022-03-11\",\"handleAfterOddsValue\":1.83,\"handleAfterOddsValue1\":1.83,\"handleStatus\":0,\"handledBetAmout\":10,\"marketId\":145093001387778761,\"marketType\":\"EU\",\"marketValue\":\"3/3.5\",\"matchId\":3120745,\"matchInfo\":\"阿斯顿维拉U23 v 南安普顿U23\",\"matchName\":\"英格兰超级联赛U23\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":19.83,\"modifyTime\":1647006200097,\"modifyUser\":\"系统\",\"oddFinally\":\"1.83\",\"oddsValue\":183000.0,\"orderNo\":\"3294012400198658\",\"orderStatus\":0,\"otherOddsValue\":205000.0,\"otherScore\":\"\",\"paidAmount\":1830.00,\"paidAmount1\":18.30,\"placeNum\":1,\"platform\":\"PA\",\"playId\":2,\"playName\":\"全场大小\",\"playOptions\":\"Over\",\"playOptionsId\":148774061473384189,\"playOptionsName\":\"大 3/3.5\",\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"足球\",\"subPlayId\":\"2\",\"tournamentId\":821960,\"tradeType\":0,\"turnamentLevel\":1,\"uid\":407419678185623552,\"validateResult\":0}],\"modifyTime\":1647006200097,\"orderAmountTotal\":1000,\"orderNo\":\"3294012400198658\",\"orderStatus\":0,\"productAmountTotal\":1000,\"productCount\":1,\"seriesType\":2001,\"tenantId\":2,\"tenantName\":\"试玩商户\",\"uid\":407419678185623552,\"userFlag\":\"\",\"userTagLevel\":3,\"username\":\"\",\"validateResult\":2,\"vipLevel\":0}";

        String data="{\"acceptOdds\":2,\"createTime\":1647173682442,\"currencyCode\":\"CNY\",\"deviceType\":2,\"handleStatus\":0,\"infoStatus\":0,\"ip\":\"165.84.166.145\",\"items\":[{\"betAmount\":10000,\"betAmount1\":100,\"betNo\":\"47657822684859\",\"betTime\":1647173682442,\"createTime\":1647173682442,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2022-03-13\",\"handleAfterOddsValue\":1.58,\"handleAfterOddsValue1\":1.58,\"handleStatus\":0,\"handledBetAmout\":100,\"marketId\":145637344447964544,\"marketType\":\"EU\",\"marketValue\":\"-1\",\"marketValueNew\":\"-1\",\"matchId\":3125905,\"matchInfo\":\"结算3.13主队 v 结算3.13客队\",\"matchName\":\"结算3.13_13-2\",\"matchProcessId\":0,\"matchType\":2,\"maxWinAmount\":5800.0,\"modifyTime\":1647173682442,\"modifyUser\":\"系统\",\"oddFinally\":\"0.58\",\"oddsValue\":158000.0,\"orderNo\":\"3294347364888437\",\"orderStatus\":0,\"otherOddsValue\":228000.0,\"otherScore\":\"0:0\",\"paidAmount\":15800.00,\"paidAmount1\":158.00,\"placeNum\":1,\"platform\":\"PA\",\"playId\":4,\"playName\":\"全场让球\",\"playOptions\":\"1\",\"playOptionsId\":147207461239627623,\"playOptionsName\":\"结算3.13主队  -1\",\"scoreBenchmark\":\"0:0\",\"sportId\":1,\"sportName\":\"足球\",\"subPlayId\":\"4\",\"tournamentId\":835439,\"tradeType\":0,\"turnamentLevel\":1,\"uid\":401506900518244352,\"validateResult\":0}],\"modifyTime\":1647173682442,\"orderAmountTotal\":10000,\"orderNo\":\"3294347364888437\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"seriesType\":1,\"tenantId\":2,\"tenantName\":\"试玩商户\",\"uid\":401506900518244352,\"userFlag\":\"\",\"userTagLevel\":4,\"username\":\"\",\"validateResult\":2,\"vipLevel\":0}";
        OrderBean orderBean = JSONObject.parseObject(data, OrderBean.class);
        predictService.calculateSeries(orderBean,1);
    }
}
