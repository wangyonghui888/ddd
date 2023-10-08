package com.panda.sport.rcs.task.mq.orderSummary;

import com.panda.sport.rcs.pojo.MatchEventInfo;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateComposeModel;
import com.panda.sport.rcs.task.config.RedissonManager;
import com.panda.sport.rcs.task.wrapper.IRcsMatchMarketConfigService;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "RCS_TASK_FOOTBALL_SYNC_TEMPLATE_TOPIC",
        consumerGroup = "RCS_TASK_FOOTBALL_SYNC_TEMPLATE_GROUP",
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
public class FootballSyncTemplateConsumer implements RocketMQListener<RcsTournamentTemplateComposeModel>, RocketMQPushConsumerLifecycleListener {

    private final String lockKey = "rcs_topic_tournament_template_margin_ref_lock_foot:%s:%s";
    private final IRcsMatchMarketConfigService rcsMatchMarketConfigServiceImpl;
    private final RedissonManager redissonManager;

    public FootballSyncTemplateConsumer(IRcsMatchMarketConfigService rcsMatchMarketConfigServiceImpl, RedissonManager redissonManager) {
        this.rcsMatchMarketConfigServiceImpl = rcsMatchMarketConfigServiceImpl;
        this.redissonManager = redissonManager;
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(512);
    }

    @Override
    public void onMessage(RcsTournamentTemplateComposeModel message) {
        String playId = String.valueOf(message.getPlayId());
        String keyName=String.format(lockKey,message.getMatchId(),playId);
        log.info("::koala同步足球模板消费锁::{}",keyName);
        try {
            redissonManager.lock(keyName, 30);
            log.info("::开始同步足球模板消费开始时间::{}",DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            rcsMatchMarketConfigServiceImpl.insertFromTemplate(message);
            log.info("::结束同步足球模板消费开始时间::{}",DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            log.error("::koala同步足球模板消费异常{}::", e);
        } finally {
            redissonManager.unlock(keyName);
        }

    }
}
