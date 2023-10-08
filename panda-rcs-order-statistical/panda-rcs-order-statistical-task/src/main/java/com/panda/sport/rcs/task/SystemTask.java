package com.panda.sport.rcs.task;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.common.constants.RocketMQConstants;
import com.panda.sport.rcs.common.utils.ThreadUtil;
import com.panda.sport.rcs.common.vo.api.request.TUserGroupBetRateReqVo;
import com.panda.sport.rcs.common.vo.api.request.UserGroupBetRateReqVo;
import com.panda.sport.rcs.customdb.mapper.StaticsItemExtMapper;
import com.panda.sport.rcs.customdb.mapper.TagExtMapper;
import com.panda.sport.rcs.job.UserVisitJob;
import com.panda.sport.rcs.redis.service.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lithan
 */
@Component
public class SystemTask {

    Logger log = LoggerFactory.getLogger(SystemTask.class);

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    StaticsItemExtMapper staticsItemExtMapper;
    @Autowired
    RedisService redisService;
    @Autowired
    TagExtMapper tagExtMapper;

    @Scheduled(initialDelay = 5000, fixedDelay = 5 * 60 * 1000)
    public void doing() {
        try {
            log.info("当前待处理总数:" + ThreadUtil.size());
        } catch (Exception e) {
            log.info("当前待处理总数:{}:{}", e.getMessage(), e);
        }
    }

    @Scheduled(initialDelay = 5000, fixedDelay = 5 * 60 * 1000)
    public void tt() {
        String key = "rcs:statis:task:user:clear:tag:basketball:status";
        String isOpen = redisService.getString(key);
        if (StringUtils.isNotBlank(isOpen)) {
            log.info("清理货量任务已处理...跳过");
            return;
        }

        try {
            List<Long> userList = tagExtMapper.getUserByBasketTag();
            for (Long userId : userList) {
                //修改赛种货量百分比的对象
                UserGroupBetRateReqVo vo = new UserGroupBetRateReqVo();
                List<TUserGroupBetRateReqVo> userGroupBetRateList = new ArrayList<>();
                vo.setUserGroupBetRateList(userGroupBetRateList);
                List rateUserList = new ArrayList();
                rateUserList.add(userId);
                //发消息到trade，修改该玩家组内所有用户按照当前玩家组的“风控措施”配置进行更新
                Map<String, Object> rateMap = new HashMap<>();
                rateMap.put("userList", rateUserList);
                rateMap.put("betRateConfig", vo);
                rateMap.put("modifyUser", "SystemAuto");
                producerSendMessageUtils.sendMessage("USER_GROUP_BET_RATE_TOPIC", "USER_GROUP_BET_RATE_TAG", JSONObject.toJSONString(rateMap));
                log.info("变更篮球货量百分比发送:{}", JSONObject.toJSONString(rateMap));
            }
        } catch (Exception e) {
            log.info("手工修复-异常", e.getMessage(), e);
        } finally {
            redisService.set(key, "1");
        }
    }
}
