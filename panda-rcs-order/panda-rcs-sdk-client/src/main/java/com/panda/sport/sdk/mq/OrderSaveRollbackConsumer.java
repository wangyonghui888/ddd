package com.panda.sport.sdk.mq;

import com.panda.sport.sdk.service.impl.LuaPaidService;
import com.panda.sport.sdk.util.GuiceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.stereotype.Component;

/**
 * 【下单限额累计值回滚】 刷新redis
 * 场景：下单的时候lua是先一个个维度累计相关额度的，如果计算到后面的维度发现不满足条件引起失败则来消费这个mq回滚累加的额度
 * 逻辑：通过code字段来标识lua中累计过几个维度 回滚的时候就根据code的值来回滚对应的维度即可
 * code:{-1：订单已存在  -2：用户玩法赔付拒单  -3：用户赛事限额拒单  -4：用户单日限额拒单 -5：单场赛事限额拒单   1：成功}
 *
 * 注意1：code =-4 是在java中计算的，不是lua ,所以如果是code = -4 ,也是需要在java中回滚
 * 注意2：code =1 全部维度都滚回
 * 注意3：如果code不在上面的状态中则会标识为-1 不会去处理这笔回滚
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "ORDER_SAVE_ROLLBACK",
        consumerGroup = "ORDER_SAVE_ROLLBACK_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class OrderSaveRollbackConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Override
    public void onMessage(String body) {
        try {
            LuaPaidService luaPaidService =  GuiceContext.getInstance(LuaPaidService.class);
            luaPaidService.rallBackShakey(body);
        } catch (Exception e) {
            log.error("下单限额累计值回滚处理异常:", e);
        }
    }
}
