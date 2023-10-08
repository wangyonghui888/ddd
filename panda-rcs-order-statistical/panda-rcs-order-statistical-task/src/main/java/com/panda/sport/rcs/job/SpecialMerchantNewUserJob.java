package com.panda.sport.rcs.job;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.CommonUtils;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.common.constants.RocketMQConstants;
import com.panda.sport.rcs.customdb.mapper.TagExtMapper;
import com.panda.sport.rcs.db.entity.UserProfileTags;
import com.panda.sport.rcs.db.entity.UserProfileUserTagChangeRecord;
import com.panda.sport.rcs.db.mapper.UserProfileTagsMapper;
import com.panda.sport.rcs.db.service.IUserProfileUserTagChangeRecordService;
import com.panda.sport.rcs.redis.service.RedisService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 一、背景：部分商户盈利率低，风控抓出风险用户后，风险用户会换新账号来打，导致商户盈利率无法提升。
 * 二、需求：
 * 1、每天早上7点10分和晚上7点10分，分别跑两次脚本，针对指定商户的“新用户”标签的用户，将其标签变更为“风险商户新用户”（标签ID：234），并且将用户设置特殊赔率分组为2
 */

@JobHandler(value = "specialMerchantNewUserJob")
@Component
public class SpecialMerchantNewUserJob extends IJobHandler {

    Logger log = LoggerFactory.getLogger(SpecialMerchantNewUserJob.class);

    @Autowired
    RedisService redisService;
    @Autowired
    TagExtMapper tagExtMapper;
    @Autowired
    UserProfileTagsMapper userProfileTagsMapper;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    IUserProfileUserTagChangeRecordService userProfileUserTagChangeRecordService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("特别商户新用户标签任务。。。。");
        doTask();
        return ReturnT.SUCCESS;
    }

    private void doTask() {
        CommonUtils.mdcPut();
        String merchantCodes = redisService.getString("rcs:risk:user:odds:group:merchantCode:config");
        log.info("特别商户修改新标签redis配置商户:{}", merchantCodes);
        if (StringUtils.isBlank(merchantCodes) || Arrays.asList(merchantCodes.replaceAll("'", "").split(",")).isEmpty()) {
            log.info("特别商户修改新标签redis配置商户配置为空", merchantCodes);
            return;
        }
        UserProfileTags tag234 = userProfileTagsMapper.selectById(234);
        List<Map<String, Object>> userList = tagExtMapper.getSpecialMerchantNewUserUserId(merchantCodes, 230);
        for (Map<String, Object> user : userList) {
            Long userId = (Long) user.get("uid");
            String username = (String) user.get("username");
            String merchantCode = (String) user.get("merchantCode");
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("type", 1);
                map.put("remark", "特别商户变更用户对应标签");
                map.put("userId", userId);
                map.put("tagType", 2);
                map.put("tagId", 234);
                log.info("特别商户修改新标签发送:{}", JSONObject.toJSONString(map));
                producerSendMessageUtils.sendMessage(RocketMQConstants.USER_TAG_CHANGE_TOPIC, RocketMQConstants.USER_TAG_CHANGE_TAG, userId.toString(), JSONObject.toJSONString(map));
                Map<String, Object> changeMap = new HashMap<>();
                changeMap.put("userId", userId);
                changeMap.put("tagId", 234);
                producerSendMessageUtils.sendMessage("RCS_LIMIT_USER_TAG_CHANGE","",userId.toString(), JSONObject.toJSONString(changeMap));
                //
//                bug-34779
//                1、1756需求中，用户打上“风险商户新用户”标签后，需要单独设置用户的赔率组别为2，本次优化点为：用户打上“风险商户新用户”标签后，不再需要再设置用户赔率组别
//                2、将所有标签为234（风险商户新用户）、208（正常用户）用户的赔率分组清除
//                注意：第2点业务也需要处理

//                String oddsMst = "{\"rcsUserConfigVo\":{\"tagMarketLevelId\":\"12\",\"updateTime\":\"" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "\",\"userId\":" + userId + "}}";
//                producerSendMessageUtils.sendMessage("RCS_USER_TAG_MARKET_LEVEL_ID_TOPIC", "", userId.toString(), oddsMst);
//                log.info("业务赔率分组发送:{}", oddsMst);
//
//                Map<String, Object> groupMap = new HashMap<>();
//                groupMap.put("groupId", 12);
//                groupMap.put("userId", userId);
//                log.info("风控赔率分组发送:{}", JSONObject.toJSONString(groupMap));
//                producerSendMessageUtils.sendMessage("rcs_risk_user_odds_group_task_send", "", userId.toString(), JSONObject.toJSONString(groupMap));

//                bug-34779 end

                userProfileUserTagChangeRecordService.doLastTime(userId, 234L);
                UserProfileUserTagChangeRecord userProfileUserTagChangeRecord = new UserProfileUserTagChangeRecord();

                userProfileUserTagChangeRecord.setUserId(userId);
                userProfileUserTagChangeRecord.setChangeBefore(230L);
                userProfileUserTagChangeRecord.setChangeAfter(234L);
                userProfileUserTagChangeRecord.setChangeTag(234L);
                userProfileUserTagChangeRecord.setChangeDetail(userId + "用户变更了标签:" + tag234.getTagName());
                userProfileUserTagChangeRecord.setChangeTime(System.currentTimeMillis());

                userProfileUserTagChangeRecord.setRealityValue("[{\"result\":\"1756脚本新用户变更风险商户新用户@;@\",\"rule\":{ }}]");
                userProfileUserTagChangeRecord.setChangeSuggest("1");
                userProfileUserTagChangeRecord.setStatus(1);

                userProfileUserTagChangeRecord.setChangeManner("System");
                userProfileUserTagChangeRecord.setChangeReason("1756脚本新用户变更风险商户新用户");
                userProfileUserTagChangeRecord.setChangeType(1);


                userProfileUserTagChangeRecord.setTagType(tag234.getTagType());

                userProfileUserTagChangeRecord.setUserName(username);
                userProfileUserTagChangeRecord.setMerchantCode(merchantCode);
                userProfileUserTagChangeRecord.setOperateTime(System.currentTimeMillis());
                userProfileUserTagChangeRecordService.save(userProfileUserTagChangeRecord);
            } catch (Exception e) {
                log.info("特别商户修改新标签异常:{}:{}:{}", userId, e.getMessage(), e);
            }

        }
    }

}
