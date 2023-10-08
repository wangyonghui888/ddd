package com.panda.sport.rcs.mq;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.RcsConsumer;
import com.panda.sport.rcs.customdb.mapper.TagExtMapper;
import com.panda.sport.rcs.job.SpecialMerchantNewUserJob;
import com.panda.sport.rcs.redis.service.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 1756 特殊标签管控-商户维护
 *
 * @description:
 * @author: magic
 * @create: 2022-06-05 10:15
 **/
@Component
@RocketMQMessageListener(topic = "MERCHANT_RISK_STATUS_INFO", consumerGroup = "merchant_risk_status_info_group", consumeMode = ConsumeMode.CONCURRENTLY)
public class SpecialMerchantNewUserConsumer extends RcsConsumer<List<JSONObject>> {

    @Autowired
    RedisService redisService;
    @Autowired
    TagExtMapper tagExtMapper;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    private Logger log = LoggerFactory.getLogger(SpecialMerchantNewUserConsumer.class);

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(32);
        consumer.setConsumeThreadMax(64);
    }

    @Override
    protected String getTopic() {
        return "MERCHANT_RISK_STATUS_INFO";
    }

    @Autowired
    SpecialMerchantNewUserJob specialMerchantNewUserJob;

    /**
     * 前端修改标记商户，业务将商户发送过来 进行1756处理SpecialMerchantNewUserJob
     * 对比上一次的数据如果有删除的 将商户下234标签的用户赔率分组设置为0
     *
     * @param dataList
     * @return
     */
    @Override
    protected Boolean handleMs(List<JSONObject> dataList) {
        Set<String> merchantCodes = dataList.stream().map(e -> e.getString("merchantCode")).collect(Collectors.toSet());
        log.info("merchant_risk_status_info 特殊标签管控-商户维护变更：{}", String.join(",", merchantCodes));
        String configCode = redisService.getString("rcs:risk:user:odds:group:merchantCode:config");
        if (StringUtils.isNotBlank(configCode)) {
            log.info("merchant_risk_status_info 特殊标签管控-商户维护历史：:{}", configCode);
            //因为缓存里面的数据是这样的格式 所以需要做字符串截取操作
            //"\"'3A13','3A14','3B742','3M157','3C674','3G213','3A62','3A95','3I118','3A96','3A86','3A97','3A98','3A99','3A11','639752','589256'\""

            List<String> deleteList = Stream.of(configCode.replaceAll("'", "").split(",")).filter(e -> !merchantCodes.contains(e)).collect(Collectors.toList());
            log.info("merchant_risk_status_info 特殊标签管控-商户维护删除的：:{}", String.join(",", deleteList));
            if (!deleteList.isEmpty()) {
                //删除该商户下投注特征标签为“风险商户新用户”的用户的特殊赔率分组。 删除就是设置为0，不需要变更标签
                List<Map<String, Object>> userList = tagExtMapper.getSpecialMerchantNewUserUserId(deleteList.stream().map(e -> "'" + e + "'").collect(Collectors.joining(",")), 234);
                for (Map<String, Object> user : userList) {
                    Long userId = (Long) user.get("uid");
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("type", 1);
//                        map.put("remark", "特别商户变更用户对应标签");
//                        map.put("userId", userId);
//                        map.put("tagType", 2);
//                        map.put("tagId", 234);
//                        log.info("特别商户修改新标签发送:{}", JSONObject.toJSONString(map));
//                        producerSendMessageUtils.sendMessage(RocketMQConstants.USER_TAG_CHANGE_TOPIC, RocketMQConstants.USER_TAG_CHANGE_TAG, JSONObject.toJSONString(map));

                    String oddsMst = "{\"rcsUserConfigVo\":{\"tagMarketLevelId\":\"0\",\"updateTime\":\"" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "\",\"userId\":" + userId + "}}";
                    producerSendMessageUtils.sendMessage("RCS_USER_TAG_MARKET_LEVEL_ID_TOPIC", "",userId.toString(), oddsMst);
                    log.info("业务赔率分组发送:{}", oddsMst);

                    Map<String, Object> groupMap = new HashMap<>();
                    groupMap.put("groupId", 0);
                    groupMap.put("userId", userId);
                    log.info("风控赔率分组发送:{}", JSONObject.toJSONString(groupMap));
                    producerSendMessageUtils.sendMessage("rcs_risk_user_odds_group_task_send", "", userId.toString(), JSONObject.toJSONString(groupMap));
                }
            }
        }
        redisService.set("rcs:risk:user:odds:group:merchantCode:config", merchantCodes.stream().map(e -> "'" + e + "'").collect(Collectors.joining(",")));
        //不主动执行，由定时任务自动执行
//        try {
//            specialMerchantNewUserJob.execute(merchantCodes.stream().map(e -> "'" + e + "'").collect(Collectors.joining(",")));
//        } catch (Exception e) {
//            log.error("merchant_risk_status_info 特殊标签管控-商户维护变更执行失败：{}", e.getMessage(), e);
//        }
        return true;
    }
}
