package com.panda.sport.rcs.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.RcsConsumer;
import com.panda.sport.rcs.common.vo.api.request.UserBetTagChangeReqVo;
import com.panda.sport.rcs.customdb.mapper.StaticsItemExtMapper;
import com.panda.sport.rcs.db.entity.UserProfileTags;
import com.panda.sport.rcs.db.entity.UserProfileUserTagChangeRecord;
import com.panda.sport.rcs.db.mapper.UserProfileTagsMapper;
import com.panda.sport.rcs.db.service.IUserProfileUserTagChangeRecordService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "rcs_risk_manual_tag_log", consumerGroup = "rcs_risk_manual_tag_log_group", consumeMode = ConsumeMode.CONCURRENTLY)
public class TagLogConsumer extends RcsConsumer<UserProfileUserTagChangeRecord> {

    private Logger log = LoggerFactory.getLogger(TagLogConsumer.class);

    @Autowired
    IUserProfileUserTagChangeRecordService userProfileUserTagChangeRecordService;

    @Autowired
    private StaticsItemExtMapper staticsItemExtMapper;

    @Autowired
    private UserProfileTagsMapper userProfileTagsMapper;

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(32);
        consumer.setConsumeThreadMax(64);
    }

    @Override
    protected String getTopic() {
        return "rcs_risk_manual_tag_log";
    }

    @Override
    protected Boolean handleMs(UserProfileUserTagChangeRecord userProfileUserTagChangeRecord) {
        log.info("手工标签日志收到:{}", JSONObject.toJSONString(userProfileUserTagChangeRecord));
        try {
            //特殊兼容处理
            Long userLevel = staticsItemExtMapper.getUserBetTag(userProfileUserTagChangeRecord.getUserId());
            //如果是新增的 则记录  标签变更记录表
            userProfileUserTagChangeRecord.setChangeBefore(userLevel);
            UserProfileTags tag = userProfileTagsMapper.selectById(userLevel);
            userProfileUserTagChangeRecord.setChangeDetail(userProfileUserTagChangeRecord.getUserId() + "用户新增了标签:" + tag.getTagName());
//            userProfileUserTagChangeRecord.setRealityValue("{}");
            userProfileUserTagChangeRecordService.save(userProfileUserTagChangeRecord);
            log.info("手工标签日志记录完成:{}", JSONObject.toJSONString(userProfileUserTagChangeRecord));
        } catch (Exception e) {
            log.info("手工标签日志记录异常:{}:{}:{}", userProfileUserTagChangeRecord.getUserId(), e.getMessage(), e);
        }
        return true;
    }
}
