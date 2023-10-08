package com.panda.sport.rcs.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.RcsConsumer;
import com.panda.sport.rcs.common.bean.RuleResult;
import com.panda.sport.rcs.common.vo.UserRuleCheckResultMqVo;
import com.panda.sport.rcs.db.service.IUserProfileTagUserRelationService;
import com.panda.sport.rcs.db.service.IUserProfileUserTagChangeRecordService;
import com.panda.sport.rcs.redis.service.RedisService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.panda.sport.rcs.common.constants.RedisConstants.DAY_TAST_USER_RULE_RESULT;

@Component
@RocketMQMessageListener(topic = "rcs_user_rule_check_result", consumerGroup = "rcs_user_rule_check_result_group", consumeMode = ConsumeMode.CONCURRENTLY)
public class UserRuleConsumer extends RcsConsumer<List<UserRuleCheckResultMqVo>> {

    private Logger log = LoggerFactory.getLogger(UserRuleConsumer.class);

    @Autowired
    IUserProfileUserTagChangeRecordService userProfileUserTagChangeRecordService;

    @Autowired
    IUserProfileTagUserRelationService userProfileTagUserRelationService;

    @Autowired
    RedisService redisService;


    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(32);
        consumer.setConsumeThreadMax(64);
    }

    @Override
    protected String getTopic() {
        return "rcs_user_rule_check_result";
    }

    @Override
    protected Boolean handleMs(List<UserRuleCheckResultMqVo> list) {
        log.info("用户规则结果收到批量:{}", JSONObject.toJSONString(list));
        for (UserRuleCheckResultMqVo vo : list) {
            try {
                log.info("处理用户规则:{}规则:{}用户规则结果收到:{}", vo.getUserId(), vo.getRuleCode(), JSONObject.toJSONString(vo));
                RuleResult<String> ruleResult = new RuleResult();
                ruleResult.setUserId(vo.getUserId());
                ruleResult.setRuleCode(vo.getRuleCode());
                ruleResult.setFlag(vo.getResult().equals("1"));
                ruleResult.setData(vo.getData());
                String resultKey = String.format(DAY_TAST_USER_RULE_RESULT, vo.getTagId(), vo.getUserId(), vo.getRuleCode());
                redisService.set(resultKey, ruleResult, 18 * 60 * 60);
            } catch (Exception e) {
                log.info("处理用户规则异常:{}规则:{}异常:{}:{}", vo.getUserId(), vo.getRuleCode(), e.getMessage(), e);
            }
        }

        return true;
    }
}
