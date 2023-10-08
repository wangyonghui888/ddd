package com.panda.sport.rcs.third.mq;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.MtsIsCacheEnum;
import com.panda.sport.rcs.third.common.ThirdReceivedConstants;
import com.panda.sport.rcs.third.entity.common.ThirdOrderExt;
import com.panda.sport.rcs.third.entity.redCat.RedCatOrderConfirmResponseData;
import com.panda.sport.rcs.third.enums.ThirdOrderStatusEnum;
import com.panda.sport.rcs.third.service.handler.IOrderHandlerService;
import com.panda.sport.rcs.third.service.reject.IOrderAcceptService;
import com.panda.sport.rcs.third.service.third.impl.RedCatServiceImpl;
import com.panda.sport.rcs.third.util.encrypt.ZipStringUtils;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.Async;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.panda.sport.rcs.enums.OrderInfoStatusEnum.EARLY_REFUSE;
import static com.panda.sport.rcs.enums.OrderInfoStatusEnum.SCROLL_REFUSE;
import static com.panda.sport.rcs.third.common.Constants.*;

/**
 * 数据商通知我方订单确认消费
 *
 * @author vere
 * @version 1.0.0
 * @date 2023-05-24
 */
@Configuration
@Slf4j
public class RedCatNotifyConsumer {
    @Autowired
    RedisClient redisClient;

    @Autowired
    JedisCluster jedisCluster;

    @Autowired
    IOrderHandlerService handlerService;
    @Autowired
    IOrderAcceptService acceptService;

    @Resource
    RedCatServiceImpl redCatService;

    @Resource(name = "redCatNotifyPoolExecutor")
    private ThreadPoolExecutor redCatNotifyPoolExecutor;

    /**
     * 红猫获取订单确认消息
     */
    private static final String RED_CAT_BET_RECEIVED = "RED_CAT_BET_RECEIVED";
    /**
     * 心跳key
     */
    private static final String HEARTBEAT="HEARTBEAT";
    /**
     * 投注状态key
     */
    private static final String BET_STATUS="BETSTATUS";

    private static final String SYSTEM_STATUS_KEY="success";
    private static final String SYSTEM_STATUS_SUCCESS_VALUE="true";

    /**
     * 收到通知订单缓存时间
     */
    private static final Long RECEIVED_ORDER_EXPIRED=1800L;

    @JmsListener(destination = "${redcat.destination}", containerFactory = "topicJmsListenerContainerFactory", subscription = "${redcat.subscription}")
    public void onMessageMarketOdds(byte[] msg) {
        Stopwatch stopwatch=Stopwatch.createStarted();
        String messages = new String(msg);
        JSONObject jsonObject = JSONObject.parseObject(messages);
        String messageType = jsonObject.getString("messageType");
        if (messageType.equals(HEARTBEAT)) {
            return;
        }
        log.info("::【{}】=>收到订单确认消息通知::{}", RED_CAT_BET_RECEIVED, messages);
        redCatNotifyPoolExecutor.execute(()->handlerMessage(messageType,jsonObject,messages));
        log.info("::【{}】=>收到订单确认消息主线程消费完成：【{}】，耗时:【{}】", RED_CAT_BET_RECEIVED, messages,stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }


    /**
     * 处理消息通知
     * @param messageType
     * @param jsonObject
     * @param messages
     */
    public void handlerMessage(String messageType,JSONObject jsonObject,String messages){
        Stopwatch stopwatch=Stopwatch.createStarted();
        if (messageType.equals(BET_STATUS)) {
            //投注状态
            String systemStatus = jsonObject.getString(SYSTEM_STATUS_KEY);
            if (systemStatus.equals(SYSTEM_STATUS_SUCCESS_VALUE)) {
                //系统状态良好
                RedCatOrderConfirmResponseData data = jsonObject.getObject("bet", RedCatOrderConfirmResponseData.class);
                String receivedOrderKey=String.format(RCS_THIRD_ORDER_RECEIVED_REDIS,data.getBetId());
                String receivedOrderLock=String.format(RCS_THIRD_ORDER_RECEIVED_LOCK,data.getBetId());
                String receivedOrder=redisClient.get(receivedOrderKey);
                if (StringUtils.isNotBlank(receivedOrder)) {
                    //已处理过注单，不再处理
                    log.info("::{}::【{}】=>已处理过注单，不再处理", RED_CAT_BET_RECEIVED,data.getBetId());
                    return;
                }
                //存入临时缓存，避免同一个注单消费处理
                String redisKey = String.format(RED_CAT_BET_PLACED_ORDER_NO, data.getBetId());
                Long lock =jedisCluster.setnx(receivedOrderLock,"1");
                if (lock.intValue()==1) {
                    //加锁成功
                    jedisCluster.expire(receivedOrderLock,2);
                    redisClient.setExpiry(receivedOrderKey,1,RECEIVED_ORDER_EXPIRED);
                    log.info("::{}::【{}】=> 消费过的注单存入redis缓存,避免重复消费",RED_CAT_BET_RECEIVED,data.getBetId());
                }else{
                    //加锁失败，说明有重复的消息下发，直接扔掉不处理,等待延时释放
                    log.info("::{}::【{}】=> 避免重复消费锁占用，等待自动释放",RED_CAT_BET_RECEIVED,data.getBetId());
                    return;
                }
                try {
                    //先从本地缓存取数据
                    String values= RcsLocalCacheUtils.getValueInfo(redisKey);
                    if (StringUtils.isEmpty(values)) {
                        //如果本地缓存不存在则从redis取数据
                        values = redisClient.get(redisKey);
                    }
                    if (StringUtils.isNotBlank(values)) {
                        //进行解压缩操作
                        values= ZipStringUtils.gunzip(values);
                        //订单确认结果处理
                        Stopwatch confirmWatch=Stopwatch.createStarted();
                        handlerResult(data, values);
                        log.info("::{}::【{}】=>处理订单结果完成，耗时:【{}】",RED_CAT_BET_RECEIVED,data.getBetId(),confirmWatch.elapsed(TimeUnit.MILLISECONDS));

                    } else {
                        log.error("::{}::【{}】=>没有获取到redis注单信息", data.getBetId(), redisKey, RED_CAT_BET_RECEIVED);
                    }

                } catch (Exception ex) {
                    log.error("::{}::【{}】=>系统内部故障", data.getBetId(), RED_CAT_BET_RECEIVED, ex);
                }
                jedisCluster.del(receivedOrderLock);
                log.info("::{}::【{}】=> 避免重复消费锁占用，手动释放完成",RED_CAT_BET_RECEIVED,data.getBetId());

            } else {
                //系统故障
                log.error("::【{}】=>出现系统故障，消息内容:{}", RED_CAT_BET_RECEIVED, messages);
            }

        }
        log.info("::{}::【{}】=> 投注通知处理完成，耗时:{}",RED_CAT_BET_RECEIVED,messages,stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }


    /**
     * 处理订单返回结果
     * @param data 响应报文
     * @param orderValues 缓存注单信息
     */
    private void handlerResult(RedCatOrderConfirmResponseData data, String orderValues) {
        ThirdOrderExt ext = JSONObject.parseObject(orderValues, ThirdOrderExt.class);
        ext.setThirdOrderNo(ext.getThirdOrderNo());
        switch (data.getBetStatus()) {
            //已经处理
            case ThirdReceivedConstants.RedCatMessage.ACCEPTED:
                //检查业务是否主动取消了注单
                String canceledKey = String.format(THIRD_ORDER_CANCELED, ext.getOrderNo());
                String cancelValue = redisClient.get(canceledKey);
                if (StringUtils.isNotBlank(cancelValue)&&Integer.valueOf(cancelValue)==1) {
                   //业务主动取消
                    log.info("::{}::【{}】=>业务主动取消，已经发起过数据商取消操作", data.getBetId(),RED_CAT_BET_RECEIVED);
                    break;
                }
                ext.setOrderStatus(ThirdOrderStatusEnum.SUCCESS.getType());
                ext.setThirdOrderStatus(ThirdOrderStatusEnum.SUCCESS.getType());
                //投注成功处理
                Stopwatch successWatch=Stopwatch.createStarted();
                log.info("::{}::【{}】=>开始处理投注成功状态", data.getBetId(),RED_CAT_BET_RECEIVED);
                handlerService.orderByPa(ext);
                log.info("::{}::【{}】=>处理完成投注成功状态,耗时:【{}】", data.getBetId(),RED_CAT_BET_RECEIVED,successWatch.elapsed(TimeUnit.MILLISECONDS));
                break;
            default:
                ext.setOrderStatus(ThirdOrderStatusEnum.REJECTED.getType());
                ext.setThirdOrderStatus(ThirdOrderStatusEnum.REJECTED.getType());
                //投注拒单处理
                Stopwatch rejectedWatch=Stopwatch.createStarted();
                log.info("::{}::【{}】=>开始处理投注失败为拒单状态", data.getBetId(),RED_CAT_BET_RECEIVED);
                orderReject(ext, MtsIsCacheEnum.REDCAT,data.getMessage());
                log.info("::{}::【{}】=>处理完成投注失败为拒单状态,耗时:{}", data.getBetId(),RED_CAT_BET_RECEIVED,rejectedWatch.elapsed(TimeUnit.MILLISECONDS));
                break;
        }
    }

    /**
     * 接拒逻辑处理
     * @param ext 注单信息
     * @param thirdIsCacheEnum 第三方枚举
     */
    public void orderReject(ThirdOrderExt ext, MtsIsCacheEnum thirdIsCacheEnum,String infoMsg) {
        ext.setOrderStatus(2);
        redCatService.updateOrder(ext);
        Integer infoStatus = EARLY_REFUSE.getCode();
        if ("1".equalsIgnoreCase(ext.getList().get(0).getIsScroll())) {
            infoStatus = SCROLL_REFUSE.getCode();
            infoMsg = SCROLL_REFUSE.getMode()+"原因:"+infoMsg;
        }
        handlerService.updateOrder(ext, infoStatus, infoMsg, thirdIsCacheEnum.getValue());
    }



}
