package com.panda.sport.rcs.mq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.sport.rcs.RcsConsumer;
import com.panda.sport.rcs.db.entity.UserProfileTagUserRelation;
import com.panda.sport.rcs.db.service.IUserProfileTagUserRelationService;
import com.panda.sport.rcs.db.service.IUserProfileUserTagChangeRecordService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "rcs_user_tag_last_time_syn", consumerGroup = "rcs_user_tag_last_time_syn_group", consumeMode = ConsumeMode.CONCURRENTLY)
public class TagConsumer extends RcsConsumer<JSONObject> {

    private Logger log = LoggerFactory.getLogger(TagConsumer.class);

    @Autowired
    IUserProfileUserTagChangeRecordService userProfileUserTagChangeRecordService;

    @Autowired
    IUserProfileTagUserRelationService userProfileTagUserRelationService;

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(32);
        consumer.setConsumeThreadMax(64);
    }

    @Override
    protected String getTopic() {
        return "rcs_user_tag_last_time_syn";
    }

    @Override
    protected Boolean handleMs(JSONObject jsonObject) {
        log.info("rcs_user_tag_last_time_syn用户tag最后更新时间：{}", jsonObject);
        Long userId = jsonObject.getLong("userId");
        Long tagId = jsonObject.getLong("tagId");
        userProfileUserTagChangeRecordService.doLastTime(userId, tagId);

        LambdaQueryWrapper<UserProfileTagUserRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfileTagUserRelation::getUserId,userId.toString());
        userProfileTagUserRelationService.remove(wrapper);

        UserProfileTagUserRelation relation = new UserProfileTagUserRelation();
        relation.setStatus(1);
        relation.setTagId(tagId.longValue());
        relation.setUserId(userId.toString());
        userProfileTagUserRelationService.save(relation);
        return true;
    }
}
