package com.panda.sport.rcs.virtual.mq.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.sport.rcs.pojo.virtual.RcsVirtualOrderExt;
import com.panda.sport.rcs.service.IRcsVirtualOrderExtService;
import com.panda.sport.rcs.virtual.service.VirtualDataServiceImpl;
import com.panda.sport.rcs.virtual.service.VirtualServiceImpl;
import com.panda.sport.rcs.virtual.third.client.model.VirtualCancelOrderDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author :  lithan
 * @Description :  虚拟赛事 注单mq消费
 * @Date: 2020-12-22 21:30:37
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "rcs_virtual_panda_cancel_order",
        consumerGroup = "rcs_virtual_panda_cancel_order",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class VirtualCancelOrderConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Autowired
    IRcsVirtualOrderExtService virtualOrderExtService;
    @Autowired
    VirtualDataServiceImpl dataService;
    @Autowired
    VirtualServiceImpl virtualService;

    public VirtualCancelOrderConsumer() {
//        super("rcs_virtual_panda_cancel_order", "");
    }

    @Override
    public void onMessage(String jsonStr) {
        String linkId = "virtualOrderCancel";
        try {
            log.info("虚拟赛事取消注单mq消费 ：{}", jsonStr);
            VirtualCancelOrderDto cancelOrderDto = JSONObject.parseObject(jsonStr, VirtualCancelOrderDto.class);
            linkId = cancelOrderDto.getLinkId();
            log.info("::{}::虚拟赛事取消注单mq消费 ：{}", linkId,jsonStr);
            List<String> orderNoes = cancelOrderDto.getData().stream().map(VirtualCancelOrderDto.Data::getOrderNo).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(orderNoes)) {

                LambdaQueryWrapper<RcsVirtualOrderExt> wrapper = new LambdaQueryWrapper<>();
                wrapper.in(RcsVirtualOrderExt::getOrderNo, orderNoes);
                List<RcsVirtualOrderExt> exts = virtualOrderExtService.list(wrapper);
                if (CollectionUtils.isEmpty(exts)) {
                    return ;
                }
                exts = exts.stream().filter(i -> i.getOrderStatus() != 3).collect(Collectors.toList());
                //订单不存在或者订单取消成功的无需再次取消
                if (CollectionUtils.isEmpty(exts)) {
                    return ;
                }
                List<Long> ticketIds = exts.stream().map(RcsVirtualOrderExt::getTicketId).collect(Collectors.toList());
                List<String> orderNosss = exts.stream().map(RcsVirtualOrderExt::getOrderNo).collect(Collectors.toList());
                Map<String, Object> cancelMap = virtualService.ticketCancel(ticketIds);
                log.info("::{}::三方取消虚拟赛事注单返回结果={}",linkId, JSON.toJSONString(cancelMap));
                if (Integer.valueOf(cancelMap.get("code").toString()) == 1) {
                    dataService.updateThirdOrder(orderNosss, exts.get(0).getRemark() + "|panda主动取消:第三方取消成功:" + cancelMap.get("message").toString(), 3, "CANCELLED");

                } else {
                    dataService.updateThirdOrder(orderNosss, exts.get(0).getRemark() + "|panda主动取消:第三方取消失败:" + cancelMap.get("message").toString(), null, null);
                }
            }
        } catch (Exception e) {
            log.error("::{}::虚拟赛事取消注单mq消费异常：{}{}", linkId,e.getMessage(), e);
        }
        return ;
    }
}
