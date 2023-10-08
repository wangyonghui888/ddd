package com.panda.sport.rcs.third.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderTypeEnum;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.third.entity.common.ThirdOrderExt;
import com.panda.sport.rcs.third.enums.OrderStatusEnum;
import com.panda.sport.rcs.third.factory.ThirdStrategyFactory;
import com.panda.sport.rcs.third.service.handler.IOrderHandlerService;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

import static com.panda.sport.rcs.third.common.Constants.*;


/**
 * @author Beulah
 * @date 2023/3/21 18:31
 * @description 第三方订单处理
 */
@Component
@Slf4j
@RocketMQMessageListener(topic = RCS_RISK_THIRD_ORDER, consumerGroup = "rcs_risk_third_order_group",
        messageModel = MessageModel.CLUSTERING, consumeMode = ConsumeMode.CONCURRENTLY)
public class ThirdOrderConsumer implements RocketMQListener<JSONObject>, RocketMQPushConsumerLifecycleListener {

    @Resource
    RedisClient redisClient;
    @Resource(name = "orderHandlerServiceImpl")
    IOrderHandlerService orderHandlerService;

    @Resource(name = "betPoolExecutor")
    private ThreadPoolExecutor betPoolExecutor;
    @Autowired
    JedisCluster jedisCluster;
    @Autowired
    ProducerSendMessageUtils sendMessage;


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }


    @Override
    public void onMessage(JSONObject dataMap) {
        //转换订单参数
        ThirdOrderExt ext = null;
        try {
            try {
                ext = JSONObject.parseObject(JSONObject.toJSONString(dataMap), ThirdOrderExt.class);
            } catch (Exception e) {
                log.error("注单信息转换异常,入参消息= {}", JSONObject.toJSONString(dataMap), e);
            }
            if (ext == null || CollectionUtils.isEmpty(ext.getList())) {
                log.warn("注单信息转换为null,请检查!入参消息= {}", JSONObject.toJSONString(dataMap));
                return;
            }
            MDC.put(LINKID, ext.getLinkId());
            ExtendBean firstExtendBean = ext.getList().get(0);
            String orderId = firstExtendBean.getOrderId();
            String third = ext.getThird();
            log.info("::{}::{}订单处理 收到:{}", orderId, third, JSONObject.toJSON(ext));

            String lockKey=String.format(THIRD_ORDER_NO_REDIS_LOCK,orderId);
            String orderKey=String.format(THIRD_ORDER_NO_REDIS,orderId);
            boolean getLock= jedisCluster.setnx(lockKey,"1").equals(1L);
            if (!getLock) {
                //没有拿到锁，重新消费
                sendMessage.sendMessage(RCS_RISK_THIRD_ORDER,third + "_SAVE_ORDER",orderId,JSONObject.toJSONString(dataMap));
                log.info("::{}::业务主动取消注单没有拿到锁，重新消费", orderId);
                return;
            }
            //拿到锁
            jedisCluster.expire(lockKey,THIRD_ORDER_NO_KEY_EXPIRED);
            //从redis获取缓存数据
            String ordered_mark=redisClient.get(orderKey);
            if(StringUtils.isNotBlank(ordered_mark)&&ordered_mark.equals(String.valueOf(OrderStatusEnum.REJECTED.getCode()))){
                log.info("::{}::此注单没有投注，只有取消注单，不需要处理", orderId);
                return;
            }
            //没有拒单，说明没有取消过，进行投注操作
            redisClient.setExpiry(orderKey,OrderStatusEnum.WAITING.getCode(),THIRD_ORDER_NO_EXPIRED);
            StopWatch sw = new StopWatch();
            sw.start(third + ":" + orderId + "处理");
            if (isRepeatOrder(third, orderId)) {
                return;
            }
            try {
                //1.订单入库
                ext.setBusId(firstExtendBean.getBusId());
                ext.setOrderNo(orderId);
                ThirdStrategyFactory.getThirdStrategy(third).saveOrder(ext);
                //2.是否走缓存 概率性接单
                boolean isCache = orderHandlerService.orderIsCache(ext);
                if (isCache) {
                    orderHandlerService.orderByCache(ext);
                    return;
                }
                //3.订单发往第三方
                ThirdOrderExt finalExt = ext;
                if(OrderTypeEnum.CTS.getPlatFrom().equals(third)){
                    //BC特殊处理
                    betPoolExecutor.execute(()->{
                        ThirdStrategyFactory.getThirdStrategy(third).placeBet(finalExt);
                    });
                }else{
                    betPoolExecutor.execute(()->{
                        orderHandlerService.orderByThird(finalExt);
                    });
                }
            } catch (Exception e) {
                log.info("::{}::{}订单消费[rcs_risk_third_order]处理 异常", orderId, third, e);
            } finally {
                sw.stop();
                log.info("::{}::{}订单消费[rcs_risk_third_order]处理 耗时:{}", orderId, third, sw.getTotalTimeMillis());
                //删除锁
                jedisCluster.del(lockKey);
            }

        } catch (Exception ex) {
            log.error("投注订单消费[rcs_risk_third_order]处理发生异常，消费数据:{}",JSONObject.toJSONString(dataMap),ex);
        }finally {
            MDC.remove(LINKID);
        }

    }


    /**
     * 检查订单是否重复消费
     *
     * @param third   操盘平台
     * @param orderId 订单号
     */
    private boolean isRepeatOrder(String third, String orderId) {
        try {
            String repeat = String.format(THIRD_ORDER_REPEAT_STATUS, orderId);
            if (StringUtils.isNotBlank(redisClient.get(repeat))) {
                log.warn("::{}::{}订单已处理,跳过", orderId, third);
                return true;
            }
            //设置3天过期
            redisClient.setExpiry(repeat, third, 3 * 24 * 60 * 60L);
            log.info("::{}::订单数据缓存redis成功key={}", orderId, repeat);
        } catch (Exception e) {
            log.error("::{}::{}订单处理,检查订单是否重复处理异常:", orderId, third, e);
            return true;
        }
        return false;
    }

}
