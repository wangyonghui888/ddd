package com.panda.sport.rcs.task.job;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.mongo.MarketCategory;
import com.panda.sport.rcs.task.wrapper.RcsMatchCollectionService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

/**
 * 定时清除mongodb数据
 * @author Administrator
 *
 */
@JobHandler(value = "matchStatusJobHandler")
@Component
@Slf4j
public class MatchStatusJobHandler extends IJobHandler {


    @Autowired
    RcsMatchCollectionService  rcsMatchCollectionService;

    @Autowired
    MongoTemplate mongotemplate;

    private static final String MATCH_MARKET_LIVE = "match_market_live";

    private static final String RCS_MARKET_CATEGORY = "rcs_market_category";

    private static final String MATCH_SET = "match_set";

    private static final String MATCH_MARKET_CONFIG = "rcs_match_market_config";

    @Override
    public ReturnT<String> execute(String s)  {
    	String linkId = "matchStatusJobHandler";
        try {
        	log.info("::{}::-开始执行定时清除赛事结束数据" ,linkId);
            log.info("赛事状态调整");
            Query query = new Query();
            //1.清除mongodb赛事数据 赛事状态结束 赛事4小时
            Criteria criteria = new Criteria();
            Integer [] a= {3,4};
            Criteria matchStatusCriteria = Criteria.where("matchStatus").in(Arrays.asList(a)).is(null);
            //足蓝保留4小时
            Criteria matchStartTimeCriteria = Criteria.where("sportId").in(Arrays.asList(1,2)).and("matchStartTime").lte(DateUtils.transferLongToDateStrings(System.currentTimeMillis() - 1000 * 60 * 60 * 4));
            //网球保留一天
            Criteria otherSportCriteria = Criteria.where("sportId").in(Arrays.asList(5)).and("matchStartTime").lte(DateUtils.transferLongToDateStrings(System.currentTimeMillis() - 1000 * 60 * 60 * 24));
            criteria.orOperator(matchStatusCriteria,matchStartTimeCriteria,otherSportCriteria);
            query.addCriteria(criteria);
            List<MatchMarketLiveBean> matchMarketLiveBeans = mongotemplate.find(query, MatchMarketLiveBean.class);
            //清除赛事相关设置信息
            if(!CollectionUtils.isEmpty(matchMarketLiveBeans)){
                List<Long> matchIds = matchMarketLiveBeans.stream().map(map -> map.getMatchId()).collect(Collectors.toList());
                log.info("::{}::-清除赛事:{}", linkId, JsonFormatUtils.toJson(matchIds));
                Query removeQuery= new Query();
                removeQuery.addCriteria(Criteria.where("matchId").in(matchIds));
                mongotemplate.remove(removeQuery,MATCH_SET);
                mongotemplate.remove(removeQuery,MATCH_MARKET_CONFIG);
            }
            mongotemplate.remove(query,MATCH_MARKET_LIVE);


            //2.清除mongodb盘口数据 赛事结束4小时
            Query query2 = new Query();
            Criteria criteriaMarket = new Criteria();

            criteriaMarket.orOperator(matchStartTimeCriteria,otherSportCriteria);
            query2.addCriteria(criteriaMarket);
            mongotemplate.remove(query2,RCS_MARKET_CATEGORY);
            log.info("::{}::-结束执行定时清除赛事结束数据", linkId);
            //清除没有赛事基本信息的赔率数据 rcs_market_category
           /* Criteria criteris = Criteria.where("matchStartTime").is(null);
            Query cleanNoMatchTimeQuery = new Query(criteris);
            cleanNoMatchTimeQuery.addCriteria(Criteria.where("crtTime").lt(DateUtils.parseDate(DateUtils.addNDay(new Date(), -20).getTime(), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS)));
            mongotemplate.remove(cleanNoMatchTimeQuery,MarketCategory.class);*/

        } catch (Exception e) {
            log.info("::{}::-赛事状态调整错误:{}", linkId,e.getMessage(), e);
        }
        return SUCCESS;
    }

    public static void main(String[] args) {
        Criteria criteris = Criteria.where("matchStartTime").is(null).and("crtTime").lt(DateUtils.addNDay(new Date(), -1));
        Query cleanNoMatchTimeQuery = new Query(criteris);
        System.out.println(JSONObject.toJSONString(cleanNoMatchTimeQuery,SerializerFeature.IgnoreErrorGetter));
    }
}
