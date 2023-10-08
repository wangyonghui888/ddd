package com.panda.sport.rcs.limit.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.limit.MatchLimitDataReqVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaUserSingleSiteQuotaVo;
import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.limit.service.LimitServiceImpl;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.StandardSportTournamentMapper;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.vo.LimitCacheClearVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.panda.sport.rcs.limit.constants.LimitConstants.AMOUNT_UNIT;

/**
 * @author :  lithan
 * @Description :  用户画像
 * @Date: 2020-10-15 14:46:43
 */
@Component
@Slf4j
public class MatchUserSigleLimitConsumer extends ConsumerAdapter<MatchLimitDataReqVo> {

    static final String RCS_MATCH_USER_SINGLE_LIMIT_KEY = "rcs_portrait_match_user_single_limit";
    static final String RCS_MATCH_USER_SINGLE_LIMIT_MATCH = "match_id.%s.match_type.%s";
    public MatchUserSigleLimitConsumer() {
        super("rcs_match_user_single_limit", "");
    }

    @Autowired
    LimitServiceImpl limitService;

    @Autowired
    RedisClient redisClient;


    @Override
    public Boolean handleMs(MatchLimitDataReqVo data, Map<String, String> paramsMap) {
        String earlyKey = String.format(RCS_MATCH_USER_SINGLE_LIMIT_MATCH, data.getMatchId(), 1);
        String liveKey = String.format(RCS_MATCH_USER_SINGLE_LIMIT_MATCH, data.getMatchId(), 2);
        try {
            log.info("用户画像用户单场读取开始 ：{}", JSONObject.toJSONString(data));
            // 用户单场限额
            RcsQuotaUserSingleSiteQuotaVo userSingleSiteQuotaVo = limitService.getRcsQuotaUserSingleSiteQuotaData(data);
            if (userSingleSiteQuotaVo == null) {
                log.warn("用户画像用户单场读取为空");
                redisClient.hSet(RCS_MATCH_USER_SINGLE_LIMIT_KEY, earlyKey, Long.MAX_VALUE + "");
                redisClient.hSet(RCS_MATCH_USER_SINGLE_LIMIT_KEY, liveKey, Long.MAX_VALUE+"");
                return true;
            }
            //金额单位 统一转换  元转分
            userSingleSiteQuotaVo.setEarlyUserSingleSiteQuota(userSingleSiteQuotaVo.getEarlyUserSingleSiteQuota().multiply(AMOUNT_UNIT));
            userSingleSiteQuotaVo.setLiveUserSingleSiteQuota(userSingleSiteQuotaVo.getLiveUserSingleSiteQuota().multiply(AMOUNT_UNIT));
            redisClient.hSet(RCS_MATCH_USER_SINGLE_LIMIT_KEY, earlyKey, userSingleSiteQuotaVo.getEarlyUserSingleSiteQuota().toString());
            redisClient.hSet(RCS_MATCH_USER_SINGLE_LIMIT_KEY, liveKey, userSingleSiteQuotaVo.getLiveUserSingleSiteQuota().toString());
            log.info("用户画像用户单场读取完成:{}",  JSONObject.toJSONString(userSingleSiteQuotaVo));
        } catch (Exception e) {
            redisClient.hSet(RCS_MATCH_USER_SINGLE_LIMIT_KEY, earlyKey, Long.MAX_VALUE+"");
            redisClient.hSet(RCS_MATCH_USER_SINGLE_LIMIT_KEY, liveKey, Long.MAX_VALUE+"");
            log.error("用户画像用户单场异常：{}{}", e.getMessage(), e);
        }
        return true;
    }
}
