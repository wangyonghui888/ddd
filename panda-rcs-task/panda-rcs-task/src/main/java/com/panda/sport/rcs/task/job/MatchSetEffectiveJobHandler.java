package com.panda.sport.rcs.task.job;

import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.constants.CommonConstants.MATCH_LIVE_SET_TOPIC;
import static com.panda.sport.rcs.constants.RedisKey.EXPRIY_TIME_2_HOURS;
import static com.panda.sport.rcs.constants.RedisKey.RCS_TASK_MATCH_SET_EFFECTIVE;

/**
 * 定时赛事设置生效
 *
 * @author enzo
 */
@JobHandler(value = "matchSetEffectiveJobHandler")
@Component
@Slf4j
public class MatchSetEffectiveJobHandler extends IJobHandler {

    @Autowired
    MongoTemplate mongotemplate;

    @Autowired
    RedisClient redisClient;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Override
    public ReturnT<String> execute(String s) {
    	String linkId = "matchSetEffectiveJobHandler";
        try {
        	log.info("::{}::-开始执行扫描赛事数据" ,linkId);
            Query query = new Query();
            Criteria criteria = new Criteria();
            criteria.and("liveOddBusiness").is(1);
            criteria.andOperator(Criteria.where("matchStartTime").lt(DateUtils.transferLongToDateStrings(System.currentTimeMillis())),
                    Criteria.where("matchStartTime").gte(DateUtils.transferLongToDateStrings(System.currentTimeMillis() - 1000 * 60)));
            query.addCriteria(criteria);
            List<MatchMarketLiveBean> matchMarketLiveBeans = mongotemplate.find(query, MatchMarketLiveBean.class);
            //清除赛事相关设置信息
            if (!CollectionUtils.isEmpty(matchMarketLiveBeans)) {
                List<Long> matchIds = matchMarketLiveBeans.stream().map(map -> map.getMatchId()).collect(Collectors.toList());
                matchIds.stream().forEach(matchId -> {
                    String key = RCS_TASK_MATCH_SET_EFFECTIVE + matchId;
                    if (!redisClient.exist(key)) {
                        producerSendMessageUtils.sendMessage(MATCH_LIVE_SET_TOPIC, null, String.valueOf(matchId), matchId);
                        redisClient.setExpiry(RCS_TASK_MATCH_SET_EFFECTIVE, matchId, EXPRIY_TIME_2_HOURS);
                    }
                });
                log.info("::{}::-扫描赛事生效ID:{}",linkId,JsonFormatUtils.toJson(matchIds));

            }

        } catch (Exception e) {
        	log.info("::{}::-赛事十五分钟扫描赛事错误:{}" ,linkId, e.getMessage(), e);
        }
        return SUCCESS;
    }


}
