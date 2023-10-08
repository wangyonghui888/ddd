package com.panda.sport.rcs.predict.mq;


import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.log.annotion.monnitor.MonitorAnnotion;
import com.panda.sport.rcs.predict.service.impl.football.snapshot.ForecastSnapshotServiceBo;
import com.panda.sport.rcs.predict.utils.RcsPredictMysqlFrequencyNacosConfig;
import com.panda.sport.rcs.predict.utils.RcsPredictNacosSnapshotConfig;
import com.panda.sport.rcs.predict.utils.RedisUtilsNxExtend;
import com.panda.sport.rcs.predict.utils.thread.ForecastSnapshotThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * forecast 快照相关计算
 *
 * @author joey
 * @since 2022-07-29
 */
@Component
@Slf4j
@MonitorAnnotion(code = "MQ_ORDER_PREDICT_CALC")
@TraceCrossThread
@RocketMQMessageListener(
        topic = MqConstants.RCS_ORDER_REALTIMEVOLUME,
        consumerGroup = "queue_realtimevolume_order_snapshot",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class ForecastSnapshotConsumer implements RocketMQListener<OrderBean>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private RcsPredictNacosSnapshotConfig rcsPredictNacosSnapshotConfig;

    @Autowired
    private ForecastSnapshotServiceBo forecastSnapshotServiceBo;


    private static String RCS_PREDICT_SNAPSHOT_REDIS_KEY = "rcs_predict_snapshot_match_id.%s";
    @Autowired
    private RcsPredictMysqlFrequencyNacosConfig rcsPredictMysqlFrequencyNacosConfig;
    @Autowired
    private RedisUtilsNxExtend redisUtilsNxExtend;

    @Override
    public void onMessage(OrderBean orderBean) {
        if (rcsPredictNacosSnapshotConfig.isForecastSnapshotOff()) {
            OrderItem item = orderBean.getItems().get(0);
            boolean nx = redisUtilsNxExtend.setNX(String.format(RCS_PREDICT_SNAPSHOT_REDIS_KEY, item.getMatchId()),
                    "1", rcsPredictMysqlFrequencyNacosConfig.getForecastInsertMysqlFrequency());
            if (SportIdEnum.isFootball(item.getSportId()) && nx) {
                log.info("::{}::forecast快照收到订单号", orderBean.getOrderNo());
                ForecastSnapshotThreadUtil.submit(() -> forecastSnapshotServiceBo.forecastSnapshot(item));
                log.info("::{}::forecast快照处理完成订单号 ", orderBean.getOrderNo());
            }
        } else {
            log.info(":::forecast快照订单号{}::: 快照入口关闭 不做处理!", orderBean.getOrderNo());
        }
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }
}
