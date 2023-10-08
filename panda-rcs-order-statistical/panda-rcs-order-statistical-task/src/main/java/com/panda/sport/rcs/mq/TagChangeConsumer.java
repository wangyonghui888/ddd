package com.panda.sport.rcs.mq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.RcsConsumer;
import com.panda.sport.rcs.common.constants.RocketMQConstants;
import com.panda.sport.rcs.common.vo.DataSyncUserAutoTag;
import com.panda.sport.rcs.common.vo.api.request.UserChangeTagVo;
import com.panda.sport.rcs.customdb.mapper.TagExtMapper;
import com.panda.sport.rcs.db.entity.UserProfileTagUserRelation;
import com.panda.sport.rcs.db.entity.UserProfileTags;
import com.panda.sport.rcs.db.entity.UserProfileUserTagChangeRecord;
import com.panda.sport.rcs.db.mapper.UserProfileTagsMapper;
import com.panda.sport.rcs.db.service.IUserProfileTagUserRelationService;
import com.panda.sport.rcs.db.service.IUserProfileUserTagChangeRecordService;
import com.panda.sport.rcs.redis.service.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.mq.TagChangeConsumer.RCS_USER_AUTO_TAG;

/**
 * 需求1912 自动化标签
 * http://lan-confluence.sportxxxr1pub.com/pages/viewpage.action?pageId=64616690
 *
 * @description:
 * @author: magic
 * @create: 2022-06-16 10:15
 **/
@Component
@RocketMQMessageListener(topic = RCS_USER_AUTO_TAG, consumerGroup = RCS_USER_AUTO_TAG + "_GROUP", consumeMode = ConsumeMode.CONCURRENTLY)
public class TagChangeConsumer extends RcsConsumer<List<DataSyncUserAutoTag>> {

    public static final String RCS_USER_AUTO_TAG = "rcs_user_auto_tag";
    private Logger log = LoggerFactory.getLogger(TagChangeConsumer.class);

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(32);
        consumer.setConsumeThreadMax(64);
    }

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
    @Autowired
    IUserProfileTagUserRelationService userProfileTagUserRelationService;

    @Override
    protected String getTopic() {
        return RCS_USER_AUTO_TAG;
    }

    /**
     * 1）首先判断用户是否已经有建议标签（“进球点”），注意，这里不要用“用户是x标签用户”，因为后续需要对用户进行多标签扩展，因此，这里程序逻辑一定要用“用户有当前通知的标签”来判断；如果用户已经拥有当前通知的x标签（进球点），则流程结束，不再执行后续步骤，反之，进入下一步；
     * 2）判断用户是否是“新用户”或者“正常用户”标签：
     * >A、如果不是，只是发出“投注特征标签预警消息”；
     * >B、如果是新用户或者正常用户标签，则再判断商户自行风控开关是否打开：
     * >>a. 如果是：则发出标签变更的商户风控消息，显示在商户后台“平台用户风控”和操盘后台“用户管控审核-商户管控”中，进入商户自行风控流程；
     * >>b.如果否：则自动给用户打上当前建议的标签（如进球点），并记录标签变更日志，日志中操作人为“System”，备注字段为大数据传过来的备注字段。
     *
     * @param
     * @return
     */
    @Override
    protected Boolean handleMs(List<DataSyncUserAutoTag> list) {
        log.info("大数据自动化标签接受数据:{}", JSONObject.toJSONString(list));
        list.parallelStream().forEach(e -> {
            Long userId = e.getUserId();
            Long tagId = e.getTagId();
            String remark = "";
            if (CollectionUtils.isNotEmpty(e.getRuleValues())) {
                remark = e.getRuleValues().stream().map(o -> "【" + o.getRuleCode() + "】" + o.getValue()).collect(Collectors.joining("   "));
            }
            Map<String, Object> oldUser = tagExtMapper.getUserTag(userId);
            if (oldUser == null) {
                log.info("用户:{}不存在", userId);
                return;
            }
            String merchantCode = (String) oldUser.get("merchantCode");
            String username = (String) oldUser.get("username");
            Long oldUserTagId = oldUser.get("userLevel") == null ? null : ((Integer) oldUser.get("userLevel")).longValue();
            if (oldUserTagId != null && oldUserTagId.compareTo(tagId) == 0) {
                //符合标签，不做处理
                log.info("标签没有变更不做处理");
            } else {
                UserProfileTags tag = userProfileTagsMapper.selectById(tagId);
                if (tag == null) {
                    log.info("标签:{}不存在", tagId);
                    return;
                }
                //是否是 208正常用户 230新用户
                if (oldUserTagId != null && (Arrays.asList(208L, 230L).contains(oldUserTagId))) {
                    //判断商户自行风控开关是否打开
                    // 商户风控开关 0关 1开
                    String merchantsData = redisService.getString("rcs:riskstatus:merchants:data");
                    String riskStatus = "0";
                    Map<String, String> merchantsDataMap = JSONObject.parseObject(merchantsData, Map.class);
                    if (merchantsDataMap != null && merchantsDataMap.get(merchantCode) != null) {
                        riskStatus = merchantsDataMap.get(merchantCode);
                    }
                    if ("1".equals(riskStatus)) {
                        log.info("商户风控开关开启 发送到商户后台商户决策");

                        //如果是：则发出标签变更的商户风控消息，显示在商户后台“平台用户风控”和操盘后台“用户管控审核-商户管控”中，进入商户自行风控流程；
                        UserChangeTagVo userChangeTagVo = new UserChangeTagVo();
                        userChangeTagVo.setUserId(userId);
                        userChangeTagVo.setTagId(tag.getId().intValue());
                        userChangeTagVo.setTagName(tag.getTagName());
                        userChangeTagVo.setRemark(remark);
                        userChangeTagVo.setSubmitType(1);
                        producerSendMessageUtils.sendMessage(RocketMQConstants.RCS_RISK_MERCHANT_MANAGER_TASK_AUTO_TAG, "", userId.toString(), JSONObject.toJSONString(userChangeTagVo));
                    } else {
                        log.info("商户风控开关没有开启 直接生效");
                        //商户风控开关 0关 1开 如果否：则自动给用户打上当前建议的标签（如进球点），并记录标签变更日志，日志中操作人为“System”，备注字段为大数据传过来的备注字段。
                        userProfileUserTagChangeRecordService.doLastTime(userId, tag.getId());

                        Map<String, Object> map = new HashMap<>();
                        map.put("type", 1);
                        map.put("remark", remark);
                        map.put("userId", userId);
                        map.put("tagType", tag.getTagType());
                        map.put("tagId", tag.getId());
                        log.info("大数据标签计算同步变更:{}", JSONObject.toJSONString(map));
                        producerSendMessageUtils.sendMessage(RocketMQConstants.USER_TAG_CHANGE_TOPIC, RocketMQConstants.USER_TAG_CHANGE_TAG, userId.toString(), JSONObject.toJSONString(map));
                        Map<String, Object> changeMap = new HashMap<>();
                        changeMap.put("userId", userId);
                        changeMap.put("tagId", tag.getId());
                        producerSendMessageUtils.sendMessage("RCS_LIMIT_USER_TAG_CHANGE","",userId.toString(), JSONObject.toJSONString(changeMap));
                        UserProfileUserTagChangeRecord userProfileUserTagChangeRecord = new UserProfileUserTagChangeRecord();

                        userProfileUserTagChangeRecord.setUserId(userId);
                        userProfileUserTagChangeRecord.setChangeBefore(oldUserTagId);
                        userProfileUserTagChangeRecord.setChangeAfter(tagId);
                        userProfileUserTagChangeRecord.setChangeTag(tagId);
                        userProfileUserTagChangeRecord.setChangeDetail(userId + " 自动化标签-实时标签V1.0变更标签为:" + tag.getTagName());
                        userProfileUserTagChangeRecord.setChangeTime(System.currentTimeMillis());

                        userProfileUserTagChangeRecord.setRealityValue("[{\"result\":\"" + remark + "@;@\",\"rule\":{ }}]");
                        userProfileUserTagChangeRecord.setChangeSuggest("1");
                        userProfileUserTagChangeRecord.setStatus(1);

                        userProfileUserTagChangeRecord.setChangeManner("System");
                        userProfileUserTagChangeRecord.setChangeReason(remark);
                        userProfileUserTagChangeRecord.setChangeType(1);


                        userProfileUserTagChangeRecord.setTagType(tag.getTagType());

                        userProfileUserTagChangeRecord.setUserName(username);
                        userProfileUserTagChangeRecord.setMerchantCode(merchantCode);
                        userProfileUserTagChangeRecord.setOperateTime(System.currentTimeMillis());
                        //添加备注1
                        if (!Objects.isNull(e.getRemark1())){
                            userProfileUserTagChangeRecord.setRemark1(e.getRemark1());
                        }
                        userProfileUserTagChangeRecordService.save(userProfileUserTagChangeRecord);
                    }
                } else {
                    log.info("不是新用户和正常用用户，添加投注特征标签预警消息日志");
                    //同一个用户、同一个标签，一天只有一条
                    UserProfileUserTagChangeRecord query = new UserProfileUserTagChangeRecord();
                    query.setChangeSuggest("1");
                    query.setChangeType(2);
                    query.setUserId(userId);
                    query.setChangeAfter(tagId);
                    long count = userProfileUserTagChangeRecordService.count(new LambdaQueryWrapper<>(query)
                            .ge(UserProfileUserTagChangeRecord::getChangeTime,
                                    LocalDate.now().atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli())
                            .lt(UserProfileUserTagChangeRecord::getChangeTime,
                                    LocalDate.now().plusDays(1).atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli())
                    );
                    if (count > 0) {
                        log.info("用户：{},标签:{},今天已经添加投注特征标签预警消息日志", userId, tagId);
                    } else {
                        //是否是 208正常用户 230新用户 如果不是，只是发出“投注特征标签预警消息”；
                        UserProfileTagUserRelation userProfileTagUserRelation = new UserProfileTagUserRelation();
                        userProfileTagUserRelation.setTagId(tag.getId());
                        userProfileTagUserRelation.setUserId(String.valueOf(userId));
                        //规则类型 1基本属性类 2投注特征类 3访问特征类 4财务特征类  ,访问特征类、财务特征类标签，若计算结果符合标签判断条件，则自动变更用户的这两类标签
                        userProfileTagUserRelation.setStatus(0);
                        userProfileTagUserRelationService.save(userProfileTagUserRelation);

                        UserProfileUserTagChangeRecord userProfileUserTagChangeRecord = new UserProfileUserTagChangeRecord();
                        userProfileUserTagChangeRecord.setUserId(userId);
                        userProfileUserTagChangeRecord.setChangeBefore(oldUserTagId);
                        userProfileUserTagChangeRecord.setChangeAfter(tagId);
                        userProfileUserTagChangeRecord.setChangeTag(tagId);
                        userProfileUserTagChangeRecord.setChangeDetail(userId + "自动化标签-用户变更了标签:" + tag.getTagName());
                        userProfileUserTagChangeRecord.setChangeTime(System.currentTimeMillis());

                        userProfileUserTagChangeRecord.setRealityValue("[{\"result\":\"" + remark + "@;@\",\"rule\":{ }}]");
                        userProfileUserTagChangeRecord.setChangeSuggest("1");
                        userProfileUserTagChangeRecord.setStatus(0);

                        userProfileUserTagChangeRecord.setChangeManner("System");
                        userProfileUserTagChangeRecord.setChangeReason(remark);
                        userProfileUserTagChangeRecord.setChangeType(2);


                        userProfileUserTagChangeRecord.setTagType(tag.getTagType());

                        userProfileUserTagChangeRecord.setUserName(username);
                        userProfileUserTagChangeRecord.setMerchantCode(merchantCode);
                        userProfileUserTagChangeRecord.setOperateTime(System.currentTimeMillis());
                        //添加备注1
                        if (!Objects.isNull(e.getRemark1())){
                            userProfileUserTagChangeRecord.setRemark1(e.getRemark1());
                        }
                        userProfileUserTagChangeRecordService.save(userProfileUserTagChangeRecord);
                    }
                }
            }
        });
        return true;
    }
}
