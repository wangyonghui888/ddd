package com.panda.sport.rcs.third.mq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.third.entity.common.ThirdOrderExt;
import com.panda.sport.rcs.third.enums.OrderStatusEnum;
import com.panda.sport.rcs.third.factory.ThirdStrategyFactory;
import com.panda.sport.rcs.third.service.third.ThirdOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import static com.panda.sport.rcs.third.common.Constants.*;


/**
 * @author Beulah
 * @date 2023/3/21 18:31
 * @description 业务主动拒单, 风控消费进行注单状态变更
 */
@Component
@Slf4j
@RocketMQMessageListener(topic = QUEUE_REJECT_THIRD_ORDER, consumerGroup = "queue_reject_third_order_group",
        messageModel = MessageModel.CLUSTERING, consumeMode = ConsumeMode.CONCURRENTLY)
public class BusRejectThirdOrderConsumer implements RocketMQListener<OrderBean>, RocketMQPushConsumerLifecycleListener {


    @Resource
    RedisClient redisClient;
    @Autowired
    JedisCluster jedisCluster;
    @Resource(name = "cancelPoolExecutor")
    private ThreadPoolExecutor cancelPoolExecutor;
    @Autowired
    ProducerSendMessageUtils sendMessage;

    @Override
    public void onMessage(OrderBean orderBean) {
        if (ObjectUtils.isEmpty(orderBean)) {
            log.warn("收到业务主动拒单数据为空");
            return;
        }

        String orderNo = orderBean.getItems().get(0).getOrderNo();
        try {

            log.info("::{}::业务主动取消注单:{}", orderNo, JSONObject.toJSONString(orderBean));
            String third = getThirdMark(orderBean);
            if (third == null) {
                return;
            }
            String lockKey=String.format(THIRD_ORDER_NO_REDIS_LOCK,orderNo);
            String orderKey=String.format(THIRD_ORDER_NO_REDIS,orderNo);
            boolean getLock= jedisCluster.setnx(lockKey,"1").equals(1L);
            if (!getLock) {
                //没有拿到锁，重新消费
                sendMessage.sendMessage(QUEUE_REJECT_THIRD_ORDER,"ORDER_CANCEL",orderNo,orderBean);
                log.info("::{}::业务主动取消注单没有拿到锁，重新消费", orderNo);
                return;
            }
            //拿到锁
            jedisCluster.expire(lockKey,THIRD_ORDER_NO_KEY_EXPIRED);
            //从redis获取缓存数据
            redisClient.setExpiry(orderKey,OrderStatusEnum.REJECTED.getCode(),THIRD_ORDER_NO_EXPIRED);
            jedisCluster.del(lockKey);
            ThirdOrderService thirdStrategy = ThirdStrategyFactory.getThirdStrategy(third);
            String remark = StringUtils.isBlank(orderBean.getReason()) ? "业务主动拒单" : orderBean.getReason();
            //修改三方注单表状态
            String thirdOrderNo = thirdStrategy.updateThirdOrderStatus(orderBean, remark);
            List<ExtendBean> extendBeanList = new ArrayList<>();
            List<OrderItem> orderItemList = orderBean.getItems();
            for (OrderItem orderItem : orderItemList) {
                ExtendBean bean = buildExtendBean(orderBean, orderItem);
                extendBeanList.add(bean);
            }
            Integer seriesType = orderBean.getSeriesType();
            BigDecimal totalAmount = new BigDecimal(orderBean.getProductAmountTotal());
            ThirdOrderExt thirdOrderExt = new ThirdOrderExt();
            thirdOrderExt.setOrderNo(orderNo);
            thirdOrderExt.setThirdOrderNo(thirdOrderNo);
            thirdOrderExt.setThird(third);
            thirdOrderExt.setOrderStatus(2);
            thirdOrderExt.setList(extendBeanList);
            thirdOrderExt.setBusId(extendBeanList.get(0).getBusId());
            thirdOrderExt.setPaTotalAmount(totalAmount);
            thirdOrderExt.setSeriesType(seriesType);
            String orderIsRequestFailed = redisClient.get(String.format(ORDER_REQUEST_FAILED, orderNo));
            if (StringUtils.isBlank(orderIsRequestFailed) || !"1".equals(orderIsRequestFailed)) {
                //通知数据商取消注单
                cancelPoolExecutor.execute(() -> {
                    thirdStrategy.orderCancel(thirdOrderExt);
                });
            }
            //标识已取消
            String canceledKey = String.format(THIRD_ORDER_CANCELED, orderNo);
            redisClient.setExpiry(canceledKey, 1, 2 * 60L);
        } catch (Exception e) {
            log.error("::{}::业务主动取消注单异常:{}", orderNo, e.getMessage(), e);
        }
    }


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    /**
     * 构建ExtendBean 从sdk拷贝的方法
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
        extend.setIsScroll(String.valueOf(item.getMatchType()).equals("2") ? "1" : "0");
        //冠军盘标识
        extend.setIsChampion(item.getMatchType() == 3 ? 1 : 0);
        extend.setMatchId(String.valueOf(item.getMatchId()));
        extend.setPlayId(item.getPlayId() + "");
        extend.setSelectId(String.valueOf(item.getPlayOptionsId()));
        extend.setSportId(String.valueOf(item.getSportId()));
        extend.setUserId(String.valueOf(item.getUid()));
        extend.setOdds(String.valueOf(item.getHandleAfterOddsValue()));
        extend.setMarketId(item.getMarketId().toString());
        if (item.getBetAmount() != null) {
            extend.setOrderMoney(item.getBetAmount());
            extend.setCurrentMaxPaid(new BigDecimal(item.getBetAmount()).multiply((new BigDecimal(extend.getOdds()).subtract(new BigDecimal(1)))).longValue());
        } else {
            extend.setOrderMoney(0L);
            extend.setCurrentMaxPaid(0L);
        }
        extend.setTournamentLevel(item.getTurnamentLevel());
        extend.setTournamentId(item.getTournamentId());
        extend.setDateExpect(item.getDateExpect());
        extend.setDataSourceCode(item.getDataSourceCode());

        extend.setItemBean(item);

        if (StringUtils.isBlank(extend.getHandicap())) {
            extend.setHandicap("0");
        }
        if (StringUtils.isBlank(extend.getCurrentScore())) {
            extend.setCurrentScore("0:0");
        }
        extend.setSubPlayId(item.getSubPlayId());
        extend.setUserTagLevel(bean.getUserTagLevel());
        return extend;
    }

    private String getThirdMark(OrderBean orderBean) {
        String third = redisClient.get(String.format(THIRD_ORDER_REPEAT_STATUS, orderBean.getOrderNo()));
        if (Objects.nonNull(third)) {
            return third;
        }
        log.warn("::{}::未匹配到对应的操盘平台,不往下处理", orderBean.getOrderNo());
        return null;
    }


}
