package com.panda.rcs.order.reject.mq.var;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.order.reject.constants.RedisKey;
import com.panda.rcs.order.reject.utils.SendMessageUtils;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.enums.OrderStatusEnum;
import com.panda.sport.rcs.enums.YesNoEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.util.List;

/**
 * 2576 处理VAR收单期间所有注单
 *
 * @author eamon
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "RCS_VAR_ORDER_REJECT",
        consumerGroup = "RCS_VAR_ORDER_REJECT_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class RcsVarOrderRejectConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {


    @Resource
    SendMessageUtils sendMessage;
    @Resource
    JedisCluster jedisCluster;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMqPushConsumer) {
        defaultMqPushConsumer.setConsumeThreadMin(64);
        defaultMqPushConsumer.setConsumeThreadMax(126);
    }

    @Override
    public void onMessage(String message) {
        String matchId = JSON.parseObject(message).getString("matchId");
        Integer orderStatus = Integer.valueOf(JSON.parseObject(message).getString("orderStatus"));
        try {
            String varOrderCountKey = String.format(RedisKey.REDIS_VAR_ORDER_LIST_COUNT, matchId);
            String varOrderCountValue = jedisCluster.get(varOrderCountKey);
            if (StringUtils.isBlank(varOrderCountValue)) {
                return;
            }
            //获取var注单数量
            long varOrderCount = Long.parseLong(varOrderCountValue);
            log.info("赛事ID::{}::,处理VAR收单期间所有注单,接拒状态:{},var注单数量:{}", matchId, orderStatus, varOrderCount);

            //总数 除以 每个list的size:100 向上取整
            long index = (int) Math.ceil((double) varOrderCount / 100);
            //从redis中获取VAR注单信息
            for (long i = 1; i <= index + 1; i++) {
                String varListIndexKey = String.format(RedisKey.REDIS_VAR_ORDER_LIST_MATCH_INDEX, matchId, i);
                List<String> varListIndexValue = jedisCluster.lrange(varListIndexKey, 0, -1);
                log.info("赛事ID::{}::,处理VAR收单期间所有注单,var注单信息:{}", matchId, varListIndexValue);
                if (StringUtils.isBlank(varListIndexValue.toString())) {
                    continue;
                }
                List<OrderBean> orderBeanList = JSONObject.parseArray(varListIndexValue.toString(), OrderBean.class);
//                log.info("赛事ID::{}::,处理VAR收单期间所有注单为orderStatus：{},var注单信息实体:{}", matchId, orderStatus, orderBeanList);

                for (OrderBean orderBean : orderBeanList) {
                    //修改所有注单接拒状态
                    orderBean.setOrderStatus(orderStatus);
                    //修改orderTypePA-5,新加一个字段int:varOrderReject
                    orderBean.setVarOrderReject(YesNoEnum.Y.getValue());
                    for (OrderItem item : orderBean.getItems()) {
                        item.setOrderStatus(orderStatus);
                        if (orderStatus.equals(OrderStatusEnum.ORDER_WAITING.getCode())) {
                            //修改所有注单时间为当前时间
                            item.setBetTime(System.currentTimeMillis());
                        }
                    }
                    //逐个发送VAR订单给风控接拒处理MQ:rcs_reject_bet_order
                    log.info("赛事ID::{}::,逐个发送VAR订单给风控接拒处理为orderStatus：{},var注单信息实体:{}", matchId,orderStatus, orderBean);
                    sendMessage.sendMessage("rcs_reject_bet_order", "VAR_ORDER_REJECT", orderBean.getOrderNo(), orderBean);
                }
                jedisCluster.del(varListIndexKey);
            }
            jedisCluster.del(varOrderCountKey);
            log.info("赛事ID::{}::,处理VAR收单期间所有注单完成", matchId);
        } catch (Exception e) {
            log.error("赛事ID::{}::,处理VAR收单注单异常{},", matchId, message, e);
        }
    }
}
