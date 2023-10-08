package com.panda.sport.rcs.oddin.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.oddin.CancelOrderDto;
import com.panda.sport.data.rcs.vo.oddin.RejectReasonVo;
import com.panda.sport.data.rcs.vo.oddin.TicketVo;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.oddin.config.NacosParameter;
import com.panda.sport.rcs.oddin.service.TicketOrderService;
import com.panda.sport.rcs.oddin.service.handler.IOrderHandlerService;
import com.panda.sport.rcs.oddin.util.SendMessageUtils;
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

import static com.panda.sport.rcs.oddin.common.Constants.TIME_OUT_FOUR_SECONDS_REJECT_CODE;
import static com.panda.sport.rcs.oddin.common.Constants.TIME_OUT_FOUR_SECONDS_REJECT_RESON;

/**
 * @author Beulah
 * @date 2023/6/3 11:54
 * @description 早盘检查，超时风控拒单 通知业务与oddin
 */
@Component
@Slf4j
@RocketMQMessageListener(topic = "rcs_risk_oddin_pre_order_reject", consumerGroup = "rcs_risk_oddin_pre_order_reject_group",
        messageModel = MessageModel.CLUSTERING, consumeMode = ConsumeMode.CONCURRENTLY)
public class PreMatchOrderOverTimeConsumer implements RocketMQListener<JSONObject>, RocketMQPushConsumerLifecycleListener {


    @Resource
    private NacosParameter nacosParameter;
    @Resource
    private IOrderHandlerService orderHandlerService;
    @Resource
    private SendMessageUtils rocketProducer;
    @Resource
    private TicketOrderService ticketOrderService;
    @Resource
    private RedisClient redisClient;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);

    }


    @Override
    public void onMessage(JSONObject message) {
        try {
            //转换订单参数
            CancelOrderDto dto = JSONObject.parseObject(message.toJSONString(), CancelOrderDto.class);
            log.info("==监听orderNo:{}早盘注单超时撤单===={}", dto.getId(), JSONObject.toJSONString(dto));
            //从redis中获取订单状态，如果不存在，即已经处理完成
            String redisKey = com.panda.sport.rcs.enums.DataSourceEnum.OD.getDataSource().concat("-").concat(dto.getId());
            String status = redisClient.get(redisKey);
            //说明体育早盘注单的回调响应已经处理了
            if (StringUtils.isBlank(status)) {
                return;
            }
            log.info("==从redis中获取key:{}的orderNo:{}的注单状态：{}", redisKey, dto.getId(), status);
            //获取注单耗时
            Long castTime = System.currentTimeMillis() - dto.getBetTime();
            log.info("===监听早盘注单到响应耗时===orderNo:{}下单已经过了:{}毫秒还未有数据返回===", dto.getId(), castTime);
            //判断是否超过了早盘的超时撤单的设定时间
            if (castTime > nacosParameter.getEarlayCancelTime() * 1000) {
                log.info("==早盘orderNo:{}的注单超时，撤销订单：{}", dto.getId(), JSONObject.toJSONString(dto));
                //超时拒单通知业务与oddin撤单
                Request<CancelOrderDto> cancelRequest = new Request<>();
                cancelRequest.setData(dto);
                //通知数据商取消注单
                ticketOrderService.cancelOrder(cancelRequest);
                TicketVo ticketVo = new TicketVo();
                ticketVo.setId(dto.getId());
                RejectReasonVo rejectReasonVo = new RejectReasonVo();
                rejectReasonVo.setCode(TIME_OUT_FOUR_SECONDS_REJECT_CODE);
                rejectReasonVo.setMessage(TIME_OUT_FOUR_SECONDS_REJECT_RESON);
                ticketVo.setReject_reson(rejectReasonVo);
                orderHandlerService.orderByThird(ticketVo);
                //删除redis中数据
                redisClient.delete(redisKey);
            } else {
                //重新发送到延迟队列
                rocketProducer.sendDelayMessage("rcs_risk_oddin_pre_order_reject", "pre_order_check", dto.getId(), dto);
            }
        } catch (Exception e) {
            log.error("::早盘投注 ::{}-超时检查,处理异常--", message, e);
        }

    }

}
