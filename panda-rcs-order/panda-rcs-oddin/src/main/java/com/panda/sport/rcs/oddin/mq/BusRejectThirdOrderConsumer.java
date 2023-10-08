package com.panda.sport.rcs.oddin.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.enums.OrderTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.oddin.CancelOrderDto;
import com.panda.sport.rcs.mapper.TOrderMapper;
import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrderTy;
import com.panda.sport.rcs.oddin.entity.ots.Enums;
import com.panda.sport.rcs.oddin.enums.DataSourceEnum;
import com.panda.sport.rcs.oddin.service.TicketOrderService;
import com.panda.sport.rcs.oddin.service.handler.RcsOddinOrderTyHandler;
import com.panda.sport.rcs.pojo.TOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

import static com.panda.sport.rcs.oddin.common.Constants.QUEUE_REJECT_ORDER;


/**
 * @author Beulah
 * @date 2023/3/21 18:31
 * @description 业务主动拒单, 风控消费进行注单状态变更
 */
@Component
@Slf4j
@RocketMQMessageListener(topic = QUEUE_REJECT_ORDER, consumerGroup = "queue_reject_oddin_order_group",
        messageModel = MessageModel.CLUSTERING, consumeMode = ConsumeMode.CONCURRENTLY)
public class BusRejectThirdOrderConsumer implements RocketMQListener<OrderBean>, RocketMQPushConsumerLifecycleListener {


    @Resource
    RcsOddinOrderTyHandler rcsOddinOrderTyHandler;
    @Resource
    private TOrderMapper orderMapper;

    @Resource
    TicketOrderService ticketOrderService;


    @Override
    public void onMessage(OrderBean orderBean) {
        if (ObjectUtils.isEmpty(orderBean)) {
            log.warn("收到业务主动拒单数据为空");
            return;
        }
        String orderNo = orderBean.getItems().get(0).getOrderNo();
        try {
            log.info("::{}::业务主动取消注单:{}", orderNo, JSONObject.toJSONString(orderBean));
            //0：待处理  1：已接单  2：拒单
            RcsOddinOrderTy ext = rcsOddinOrderTyHandler.selectOne(orderNo);
            if (ext == null) {
                log.error("::{}::订单不存在，不做取消处理:{}", orderNo, JSONObject.toJSONString(orderBean));
                return;
            }
            String third = ext.getThirdName();
            if (!"ODDIN".equalsIgnoreCase(third)) {
                return;
            }

            String status = ext.getStatus();
            //幂等校验
            if (Enums.CancelStatus.CANCEL_STATUS_CANCELED.toString().equalsIgnoreCase(status)) {
                log.error("::{}::{}订单已取消,不做重复取消处理:{}", orderNo, third, JSONObject.toJSONString(orderBean));
                return;
            }
            ext.setStatus(Enums.CancelStatus.CANCEL_STATUS_CANCELED.toString());
            String remark = StringUtils.isBlank(orderBean.getReason()) ? "业务主动拒单" : orderBean.getReason();
            rcsOddinOrderTyHandler.update(ext);
            log.info("::{}::业务主动取消注单,更新第三方{}订单表完成", orderNo, third);

            //更新主订单表状态
            LambdaQueryWrapper<TOrder> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(TOrder::getOrderNo, orderNo);
            TOrder order = orderMapper.selectOne(orderWrapper);
            order.setReason(remark);
            orderMapper.updateById(order);
            log.info("::{}::拒单原因:业务主动拒单更新主订单表完成", orderNo);
            //如果是漏单的注单,取消注单就不会把该注单发往数据商
            if(orderBean.getExtendBean().getRiskChannel().equals(OrderTypeEnum.ODDIN_PA.getValue().toString())){
                log.info(orderNo+"::注单取消漏单已生效");
                return;
            }
            //通知第三方取消订单
            CancelOrderDto cancelOrderDto = new CancelOrderDto();
            cancelOrderDto.setId(orderNo);
            cancelOrderDto.setCancelReason(5);
            cancelOrderDto.setCancelReasonDetail("CANCEL_REASON_REGULATO");
            cancelOrderDto.setSourceId(DataSourceEnum.TY.getCode());
            Request<CancelOrderDto> cancelRequest = new Request<>();
            cancelRequest.setData(cancelOrderDto);
            //通知数据商取消注单
            ticketOrderService.cancelOrder(cancelRequest);

        } catch (Exception e) {
            log.error("::{}::业务主动取消注单异常:", orderNo, e);
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


}
