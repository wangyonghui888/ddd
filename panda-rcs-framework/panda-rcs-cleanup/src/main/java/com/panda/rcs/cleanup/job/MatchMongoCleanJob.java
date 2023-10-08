package com.panda.rcs.cleanup.job;

import com.mongodb.client.result.DeleteResult;
import com.panda.rcs.cleanup.utils.DataUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@JobHandler(value = "matchMongoCleanJob")
public class MatchMongoCleanJob extends IJobHandler {

    @Autowired
    MongoTemplate mongotemplate;
    /**
     * mongo赛事表
     */
    private static final String MATCH_MARKET_LIVE = "match_market_live";
    /**
     * mongo赔率表
     */
    private static final String RCS_MARKET_CATEGORY = "rcs_market_category";
    /**
     * 百家赔表
     */
    private static final String  MULTIPLE_ODDS = "multiple_odds";

    long EXPRIY_TIME_15_DAYS = TimeUnit.DAYS.toMillis(15);

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Integer limit = 100000;
        Long starTime = System.currentTimeMillis();
        if (StringUtils.isNotBlank(s)) {
            limit = Integer.parseInt(s.trim());
        }
        try {
            String dateStrings = DataUtils.transferLongToDateStrings(starTime - EXPRIY_TIME_15_DAYS);
            XxlJobLogger.log("清理mongodb--" + dateStrings + "以前的数据");
            log.info("清理mongodb--" + dateStrings + "以前的数据");

            Query categoryQuery = new Query();
            Criteria categoryCriteria = new Criteria();
            Criteria matchStartTimeCriteria = Criteria.where("matchStartTime").lte(dateStrings);
            Criteria updateTimeCriteria = Criteria.where("updateTime").lte(dateStrings);
            Criteria matchStartTimenull = Criteria.where("matchStartTime").is(null);
            Criteria updateTimenull = Criteria.where("updateTime").is(null);
            categoryCriteria.orOperator(matchStartTimeCriteria, updateTimeCriteria, matchStartTimenull, updateTimenull);
            categoryQuery.addCriteria(categoryCriteria).limit(limit);
            DeleteResult categoryremove = mongotemplate.remove(categoryQuery, RCS_MARKET_CATEGORY);
            XxlJobLogger.log("{}表清理完成,删除条数:{}", RCS_MARKET_CATEGORY, categoryremove.getDeletedCount());
            log.info("{}表清理完成,删除条数:{}", RCS_MARKET_CATEGORY, categoryremove.getDeletedCount());


            Query matchQuery = new Query();
            Criteria matchCriteria = new Criteria();
            matchCriteria.orOperator(matchStartTimeCriteria, matchStartTimenull);
            matchQuery.addCriteria(matchCriteria).limit(limit);
            DeleteResult matchremove = mongotemplate.remove(matchQuery, MATCH_MARKET_LIVE);
            XxlJobLogger.log("{}表清理完成,删除条数:{}", MATCH_MARKET_LIVE, matchremove.getDeletedCount());
            log.info("{}表清理完成,删除条数:{}", MATCH_MARKET_LIVE, matchremove.getDeletedCount());

            Query multiOddsQuery = new Query();
            long multiOddsTime = starTime-15*24*60*60*1000;
            Criteria  multiOddsClearTimeCriteria = Criteria.where("updateTime").lte(multiOddsTime);
            multiOddsQuery.addCriteria(multiOddsClearTimeCriteria).limit(limit);
            DeleteResult mutilOddsRemove = mongotemplate.remove(multiOddsQuery, MULTIPLE_ODDS);
            XxlJobLogger.log("{}表清理完成,删除条数:{}", MULTIPLE_ODDS, mutilOddsRemove.getDeletedCount());
            log.info("{}表清理完成,删除条数:{}", MULTIPLE_ODDS, mutilOddsRemove.getDeletedCount());
        } catch (Exception e) {
            log.error("清理mongodb失败" + e.getMessage(), e);
            XxlJobLogger.log("清理mongodb失败" + e.getMessage(), e);
            return ReturnT.FAIL;
        }

        return ReturnT.SUCCESS;
    }

}
