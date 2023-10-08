package com.panda.sport.rcs.task.mq.impl.match;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainRefMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateComposeModel;
import com.panda.sport.rcs.task.wrapper.IRcsMatchMarketConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author Administrator
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "SYNC_TEMPLATE_PRE_TO_LIVE_TOPIC",
        consumerGroup = "SYNC_TEMPLATE_PRE_TO_LIVE_TAG",
        consumeThreadMax = 256,
        consumeTimeout = 10000L)
public class SyncTemplateForPreToLiveConsumer implements RocketMQListener<String> {

    @Autowired
    private RcsTournamentTemplatePlayMargainRefMapper playMargainRefMapper;

    @Autowired
    private IRcsMatchMarketConfigService iRcsMatchMarketConfigService;

    @Override
    public void onMessage(String message) {
        try {
            log.info("进入联赛模板同步-指定赛事,{}", JSONObject.toJSONString(message));
            //获取到赛前进滚球的赛事ID
            Long matchId = JSONObject.parseObject(message, Long.class);
            if(!ObjectUtils.isEmpty(matchId)){
                List<RcsTournamentTemplateComposeModel> list = playMargainRefMapper.selectTemplatesByMatchId(matchId);
                log.info("同步模板数据到赛事配置任务开始-指定赛事,{}", JSONObject.toJSONString(list));
                handleTemplateData(list);
                log.info("同步模板数据到赛事配置执行结束-指定赛事");
            }
        } catch (Exception e) {
            log.error(e.getMessage() + JSONObject.toJSONString(message), e);
        }
    }

    private void handleTemplateData(List<RcsTournamentTemplateComposeModel> list) {
        if (!CollectionUtils.isEmpty(list)) {
            for (RcsTournamentTemplateComposeModel model : list) {
                try {
                    iRcsMatchMarketConfigService.insertFromTemplate(model);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}