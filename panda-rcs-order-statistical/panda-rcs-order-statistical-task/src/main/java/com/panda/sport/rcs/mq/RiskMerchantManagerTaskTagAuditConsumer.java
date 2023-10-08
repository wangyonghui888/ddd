package com.panda.sport.rcs.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.RcsConsumer;
import com.panda.sport.rcs.common.vo.AddUserTagVo;
import com.panda.sport.rcs.common.vo.api.request.UserBetTagChangeReqVo;
import com.panda.sport.rcs.db.service.IUserProfileUserTagChangeRecordService;
import com.panda.sport.rcs.service.ITagService;
import groovy.util.logging.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "rcs_risk_merchant_manager_task_tag_audit", consumerGroup = "rcs_risk_merchant_manager_task_tag_audit_group", consumeMode = ConsumeMode.CONCURRENTLY)
@Slf4j
public class RiskMerchantManagerTaskTagAuditConsumer extends RcsConsumer<AddUserTagVo> {

    private Logger log = LoggerFactory.getLogger(RiskMerchantManagerTaskTagAuditConsumer.class);

    @Autowired
    ITagService tagService;
    @Autowired
    IUserProfileUserTagChangeRecordService userProfileUserTagChangeRecordService;

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(32);
        consumer.setConsumeThreadMax(64);
    }

    @Override
    protected String getTopic() {
        return "rcs_risk_merchant_manager_task_tag_audit";
    }

    @Override
    protected Boolean handleMs(AddUserTagVo data) {
        try {
            log.info("商户同意自动化标签的变更收到:{}:{}", data.getUserId(), JSONObject.toJSONString(data));
            tagService.addUserTag(data.getUserId(), data.getTag(), data.getRuleResultList(), data.getChangeManner(), data.getRemark(), data.getChangeType());
            userProfileUserTagChangeRecordService.doLastTime(data.getUserId(),data.getTag().getId());
        } catch (Exception e) {
            log.info("商户同意自动化标签的变更处理异常:{}:{}", e.getMessage(), e);
        }
        return true;
    }
}
