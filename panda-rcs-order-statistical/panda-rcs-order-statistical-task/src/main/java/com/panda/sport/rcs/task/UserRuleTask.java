package com.panda.sport.rcs.task;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.common.data.CommonData;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.vo.rule.RuleParameterVo;
import com.panda.sport.rcs.redis.service.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.panda.sport.rcs.common.constants.RocketMQConstants.USER_RULE_CHECK_LAST_TIME;
import static com.panda.sport.rcs.common.constants.RocketMQConstants.USER_RULE_CHECK_SEND;

/**
 * task  Service
 *
 * @author :  lithan
 * @date: 2022-4-9 13:17:52
 */
@Service
@Lazy
public class UserRuleTask {

    Logger log = LoggerFactory.getLogger(UserRuleTask.class);

    //一次发多少条数据
    final Integer num = 100;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    RedisService redisService;

    /**
     * 用户规则 发送
     */

    @Scheduled(initialDelay = 5000, fixedDelay = 3 * 60 * 1000)
    private void initRuleData() {
        try {
            //最后发送时间
            String lastTimeStr = redisService.getString(USER_RULE_CHECK_LAST_TIME);
            Long lastTime = 0L;
            if (StringUtils.isNotBlank(lastTimeStr)) {
                lastTime = Long.valueOf(lastTimeStr);
            }
            //是否超过3分钟
            Long dif = System.currentTimeMillis() - lastTime;
            boolean timeOut = dif - (3 * 60 * 1000) > 0;
            log.info("用户规则任务时间信息：{}:{}分钟:超时执行{}:长度:{}", LocalDateTimeUtil.milliToLocalDateTime(lastTime), dif / 1000 / 50, timeOut, CommonData.userRuledataMap.size());
            //超过条数,或者过了3分钟
            while (CommonData.userRuledataMap.size() > 0 && (CommonData.userRuledataMap.size() >= num || timeOut)) {
                send();
                Thread.sleep(100L);
            }
        } catch (Exception e) {
            log.error("用户规则任务异常:{}:{}:{}", CommonData.userRuledataMap.size(), e.getMessage(), e);
        }
    }

    /**
     * 发送到大数据
     * @return
     */
    private void send() {
        List<RuleParameterVo> list = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, RuleParameterVo> entry : CommonData.userRuledataMap.entrySet()) {
            list.add(entry.getValue());
            CommonData.userRuledataMap.remove(entry.getKey());
            i++;
            if (i == num) {
                break;
            }
        }
        producerSendMessageUtils.sendMessage(USER_RULE_CHECK_SEND, JSONObject.toJSONString(list));
        redisService.set(USER_RULE_CHECK_LAST_TIME, System.currentTimeMillis());
        log.info("用户规则任务大数据验证发送：{}剩余:{}内容:{}", list.size(), CommonData.userRuledataMap.size(), JSONObject.toJSONString(list));
    }
}
