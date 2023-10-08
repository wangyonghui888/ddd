package com.panda.sport.rcs.mq;

import com.panda.sport.rcs.RcsConsumer;
import com.panda.sport.rcs.common.vo.api.request.UserBetTagChangeReqVo;
import com.panda.sport.rcs.db.service.IUserProfileUserTagChangeRecordService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "rcs_risk_merchant_manager_audit_tag", consumerGroup = "rcs_risk_merchant_manager_audit_group", consumeMode = ConsumeMode.CONCURRENTLY)
public class RiskMerchantManagerAuditConsumer extends RcsConsumer<UserBetTagChangeReqVo> {

    private Logger log = LoggerFactory.getLogger(RiskMerchantManagerAuditConsumer.class);

    @Autowired
    IUserProfileUserTagChangeRecordService userProfileUserTagChangeRecordService;

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(32);
        consumer.setConsumeThreadMax(64);
    }

    @Override
    protected String getTopic() {
        return "rcs_risk_merchant_manager_audit_tag";
    }

    @Override
    protected Boolean handleMs(UserBetTagChangeReqVo data) {
        userProfileUserTagChangeRecordService.editRecord(data);
        return true;
    }
}
