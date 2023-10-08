package com.panda.sport.rcs.task.mq.impl.tourTemplate;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateComposeModel;
import com.panda.sport.rcs.task.job.tourTemplate.CommonRunMethod;
import com.panda.sport.rcs.task.mq.RcsConsumer;
import com.xxl.job.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "BASKETBALL_SYNC_TEMPLATE",
        consumerGroup = "BASKETBALL_SYNC_TEMPLATE",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class ImproveBasketballSyncTemplateConsumer  extends RcsConsumer<List<RcsTournamentTemplateComposeModel>>  {
    @Autowired
    CommonRunMethod commonRunMethod;
    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(1);
        consumer.setConsumeThreadMax(1);
    }
    @Override
    protected String getTopic() {
        return "BASKETBALL_SYNC_TEMPLATE";
    }
    @Override
    public Boolean handleMs(List<RcsTournamentTemplateComposeModel> list) {
        String linkId= "BASKETBALL_SYNC_TEMPLATE" + UUID.randomUUID().toString().replace("-", "") ;
        log.info("::{}::-Consumer开始执行足球定时任务同步模板:{}",linkId, DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        try {
            commonRunMethod.handleBasketBallTemplateDataThread(linkId,list);
        }catch (Exception e) {
            log.error("::{}::-执行足球定时任务同步模板错误:{}",linkId, e.getMessage(),e);
            // throw new RcsServiceException("执行足球定时任务同步模板错误:"+e);
        }
        log.info("::{}::-Consumer结束执行足球定时任务同步模板:{}",linkId,DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        return true;
    }
}
