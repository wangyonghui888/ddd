package com.panda.sport.rcs.mts.sportradar.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.TOrderMapper;
import com.panda.sport.rcs.mts.sportradar.builder.TicketBuilderHelper;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketResponseHandler;
import com.panda.sport.rcs.mts.sportradar.wrapper.ITOrderDetailService;
import com.panda.sport.rcs.mts.sportradar.wrapper.RcsMtsOrderExtService;
import com.panda.sport.rcs.pojo.RcsMtsOrderExt;
import com.panda.sport.rcs.pojo.TOrder;
import com.sportradar.mts.sdk.api.TicketCancel;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelSender;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 业务主动拒单
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "queue_reject_mts_order",
        consumerGroup = "queue_reject_mts_order",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class OrderRejectConsumer implements RocketMQListener<OrderBean>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Autowired
    private ITOrderDetailService itOrderDetailService;
    @Autowired
    private RcsMtsOrderExtService rcsMtsOrderExtService;
    @Autowired
    private TOrderMapper orderMapper;
    @Autowired
    RedisClient redisClient;

    public OrderRejectConsumer(@Value("${rocketmq.reject_mts_order.config}") String consumerConfig) {
//        super(consumerConfig, "rocketmq.reject_mts_order.config");
    }

    @Override
    public void onMessage(OrderBean orderBean) {
        try {
            log.info("::{}::,{}OrderRejectConsumer 业务主动拒单bean info ：{}", orderBean.getItems().get(0).getOrderNo(),this.getClass(), JSONObject.toJSON(orderBean));
            if (ObjectUtils.isEmpty(orderBean)) {
                log.info("OrderRejectConsumer业务主动拒单数据异常：");
                return ;
            }
            //订单号
            String orderNo = orderBean.getItems().get(0).getOrderNo();
            //0：待处理  1：已接单  2：拒单
            int rcsOrderStatus = 2;
            
            LambdaQueryWrapper<RcsMtsOrderExt> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RcsMtsOrderExt::getOrderNo, orderNo);
            RcsMtsOrderExt rcsMtsOrderExt = rcsMtsOrderExtService.getOne(wrapper);
            if(rcsMtsOrderExt == null ) {
            	log.error("::{}::订单不存在，MTS不做取消处理：{},",orderNo,JSONObject.toJSONString(orderBean));
            	return ;
            }
            
            Integer cancel = rcsMtsOrderExt.getCancelStatus();
            if(cancel == 1) {
            	log.error("::{}::订单已取消，MTS不做重复取消处理：{},",orderNo,JSONObject.toJSONString(orderBean));
            	return ;
            }
            Integer cancelStatus = 102;
            String redisCancelStatus = redisClient.get("rcs:orderCancel:" + orderNo);
            log.info("::{}::mts拒单状态参数redisCancelStatus:",orderNo,redisCancelStatus);
            if(StringUtils.isNotBlank(redisCancelStatus)){
                cancelStatus = Integer.valueOf(redisCancelStatus);
            }
            rcsMtsOrderExt.setStatus(rcsMtsOrderExt.getStatus() + ",REJECTED");
            rcsMtsOrderExt.setCancelStatus(1);
            rcsMtsOrderExt.setCancelId(cancelStatus);
            rcsMtsOrderExt.setResult(rcsMtsOrderExt.getResult() + ",业务主动拒单更新");
            rcsMtsOrderExtService.updateById(rcsMtsOrderExt);

            //通知MTS此单为拒单 跟业务沟通 cancelStatus为19表示betcancel的取消订单 不需要调用mts
            TicketCancelSender ticketCancelSender = TicketResponseHandler.getTicketCancelSender();
            BuilderFactory builderFactory = TicketResponseHandler.getBuilderFactory();
            TicketCancel ticketCancel = new TicketBuilderHelper(builderFactory).getTicketCancel(orderNo, cancelStatus.toString());
            ticketCancelSender.send(ticketCancel);
            log.info("::{}::已经向mts发送取消订单", orderNo);
            //更新MTS注单状态，拒单
            itOrderDetailService.modifyMtsOrder(orderNo, rcsOrderStatus);
            
//            if (rcsMtsOrderExt == null) {
//                // 5.记录订单记录
//                rcsMtsOrderExt = new RcsMtsOrderExt();
//                rcsMtsOrderExt.setOrderNo(orderNo);
//                rcsMtsOrderExt.setStatus("REJECTED");
//                rcsMtsOrderExt.setResult("业务主动拒单！");
//                rcsMtsOrderExt.setCancelStatus(1);
//                rcsMtsOrderExt.setCancelId(102);
//                rcsMtsOrderExt.setCancelResult("");
//                rcsMtsOrderExtService.addMtsOrder(rcsMtsOrderExt);
//            } else {
//                
//            }
            log.info(rcsMtsOrderExt + "业务主动拒单处理完成,订单号::{}::", orderNo);

            //更新订单拒单原因
            LambdaQueryWrapper<TOrder> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(TOrder::getOrderNo, orderNo);
            TOrder order = orderMapper.selectOne(orderWrapper);
            order.setReason("业务主动拒单");
            orderMapper.updateById(order);
            log.info("::{}::拒单原因:业务主动拒单处理完成", orderNo);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ;
        }
        return ;
    }
}
