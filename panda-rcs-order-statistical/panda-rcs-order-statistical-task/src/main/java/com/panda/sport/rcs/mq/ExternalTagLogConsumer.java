package com.panda.sport.rcs.mq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.RcsConsumer;
import com.panda.sport.rcs.common.constants.Constants;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.vo.ExternalLogVo;
import com.panda.sport.rcs.common.vo.RcsQuotaBusinessLimitLog;
import com.panda.sport.rcs.db.entity.UserProfileTags;
import com.panda.sport.rcs.db.entity.UserProfileUserTagChangeRecord;
import com.panda.sport.rcs.db.service.IUserProfileTagsService;
import com.panda.sport.rcs.db.service.IUserProfileUserTagChangeRecordService;
import com.panda.sport.rcs.service.ITagService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.panda.sport.rcs.common.constants.Constants.RCS_BUSINESS_LOG_SAVE;

/**
 * 外部备注修改 2277需求
 * @author lithan
 * @date 2023-03-03 12:24:39
 */
@Component
@RocketMQMessageListener(topic = "rcs_risk_external_tag_log", consumerGroup = "rcs_risk_external_tag_log_group", consumeMode = ConsumeMode.CONCURRENTLY)
public class ExternalTagLogConsumer extends RcsConsumer<ExternalLogVo> {

    private Logger log = LoggerFactory.getLogger(ExternalTagLogConsumer.class);

    @Autowired
    IUserProfileUserTagChangeRecordService userProfileUserTagChangeRecordService;


    @Autowired
    IUserProfileTagsService userProfileTagsService;

    @Autowired
    ProducerSendMessageUtils producerSendMessageUtils;

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(32);
        consumer.setConsumeThreadMax(64);
    }

    @Override
    protected String getTopic() {
        return "rcs_risk_external_tag_log";
    }

    @Override
    protected Boolean handleMs(ExternalLogVo data) {
        log.info("外部备注修改日志收到:{}", JSONObject.toJSONString(data));
        try {
            UserProfileUserTagChangeRecord record = userProfileUserTagChangeRecordService.getById(data.getId());
            //后台日志记录
            List<RcsQuotaBusinessLimitLog> limitLogList=new ArrayList<>();
            UserProfileTags beforeTag = userProfileTagsService.getById(record.getChangeBefore());
            UserProfileTags afterTag = userProfileTagsService.getById(record.getChangeAfter());
            String paramName = beforeTag.getTagName() + " → " + afterTag.getTagName() + " - " + LocalDateTimeUtil.milliToDateTime(record.getChangeTime());
            String beforeRemark = record.getRealityValue().substring(record.getRealityValue().indexOf(":") + 2, record.getRealityValue().indexOf("@"));
            limitLogList.add(setBusinessLog(data.getUserId(), record.getUserName(), paramName, beforeRemark, data.getRemark(), data.getChangeMannerId()));
            log.info("外部备注修改处理条数{}",limitLogList.size());
            String arrString = JSONArray.toJSONString(limitLogList);
            producerSendMessageUtils.sendMessage(RCS_BUSINESS_LOG_SAVE, null, data.getUserId() + ":10080", arrString);

            //备注修改
            record.setRealityValue("[{\"result\":\"" + data.getRemark() + "@;@\",\"rule\":{ }}]");
            record.setChangeManner(data.getChangeManner());
            userProfileUserTagChangeRecordService.saveOrUpdate(record);
            log.info("外部备注修改日志记录完成:{}", JSONObject.toJSONString(data));
        } catch (Exception e) {
            log.info("外部备注修改日志记录异常:{}:{}:{}", data.getUserId(), e.getMessage(), e);
        }
        return true;
    }

    private RcsQuotaBusinessLimitLog setBusinessLog(Long userId,String userName,String paramName,String beforeVal,String afterVal,String opId) {
        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        limitLoglog.setOperateCategory("投注特征标签");
        limitLoglog.setObjectId(userId.toString());
        limitLoglog.setObjectName(userName);
        limitLoglog.setOperateType("10080");
        limitLoglog.setParamName(paramName);
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        limitLoglog.setUserId(opId);
        return limitLoglog;
    }

}
