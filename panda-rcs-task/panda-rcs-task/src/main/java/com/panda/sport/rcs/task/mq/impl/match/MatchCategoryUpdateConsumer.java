package com.panda.sport.rcs.task.mq.impl.match;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.enums.*;
import com.panda.sport.rcs.mongo.MatchCatgorySetVo;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.task.mq.bean.MatchCategoryUpdateBean;
import com.panda.sport.rcs.task.service.MatchServiceImpl;
import com.panda.sport.rcs.task.wrapper.CategoryService;
import com.panda.sport.rcs.utils.ListUtils;
import com.panda.sport.rcs.vo.CategoryConVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 玩法集更新对应mongodb category_collection
 *
 * @author enzo
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "MONGODB_CATEGORY_LIVE",
        consumerGroup = "rcs_task_MONGODB_CATEGORY_LIVE",
        consumeThreadMax = 512,
        consumeTimeout = 10000L)
public class MatchCategoryUpdateConsumer implements RocketMQListener<String>{


    @Autowired
    private RedisClient redisClient;
    @Autowired
    private MongoTemplate mongotemplate;
    @Autowired
    private MatchServiceImpl matchService;
    @Autowired
    private CategoryService categoryService;


    @Override
    public void onMessage(String msg) {
        try {
            MatchCategoryUpdateBean categoryUpdateBean = JsonFormatUtils.fromJson(msg, MatchCategoryUpdateBean.class);
            //进入滚球需要更新相关数据
            Long matchId = categoryUpdateBean.getMatchId();
            log.info("MONGODB_CATEGORY_LIVE::{}::接收到消息{}",matchId,msg);
            matchService.updateCategories(matchId);
            List<Long> longList = categoryUpdateBean.getCategoryIds();
            if (CollectionUtils.isEmpty(longList)) return;
            Long sportId = categoryUpdateBean.getSportId();
            List<CategoryConVo> categoryConVos = categoryService.mainCategory(sportId);
            log.info("categoryConVos玩法集更新matchId:{},categoryConVos:{}", matchId, JsonFormatUtils.toJson(categoryConVos));
            List<Long> mainCategoryIds = categoryService.mainCategoryIds(sportId);
            if (!CollectionUtils.isEmpty(mainCategoryIds)) {
                List<Long> notMainIds = longList.stream().filter(categoryId -> !mainCategoryIds.contains(categoryId)).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(notMainIds)) {
                    handle(categoryUpdateBean, false);
                }
            }
            for (CategoryConVo categoryConVo : categoryConVos) {
                if (categoryConVo.getCategoryIds() == null) {
                    continue;
                }
                //此玩法集需要更新的玩法
                List<Long> collect = longList.stream().filter(categoryId -> categoryConVo.categoryIds().contains(categoryId)).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(collect)) {
                    categoryUpdateBean.setCateCollId(categoryConVo.getId());
                    categoryUpdateBean.setCategoryIds(categoryConVo.categoryIds());
                    handle(categoryUpdateBean, true);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handle(MatchCategoryUpdateBean categoryUpdateBean, boolean isMain) {

        Long matchId = categoryUpdateBean.getMatchId();
        Long sportId = categoryUpdateBean.getSportId();
        Long cateCollId = categoryUpdateBean.getCateCollId();
        try {

            //更新赛事表
            Query matchQuery = new Query();
            matchQuery.fields().include("matchId");
            matchQuery.fields().include("categoryCount");
            matchQuery.fields().include("period");
            matchQuery.fields().include("sportId");
            matchQuery.fields().include("setInfos");
            matchQuery.fields().include("scoreVos");
            matchQuery.fields().include("roundType");
            matchQuery.addCriteria(Criteria.where("matchId").is(matchId));

            MatchMarketLiveBean marketLive = mongotemplate.findOne(matchQuery, MatchMarketLiveBean.class);
            if (isMain) {
                if (marketLive == null) {
                    marketLive = new MatchMarketLiveBean();
                    marketLive.setMatchId(matchId);
                    List<MatchCatgorySetVo> setVos = new ArrayList<>();
                    if (SportIdEnum.isFootball(sportId)) {
                        setVos.add(new MatchCatgorySetVo().setCatgorySetId(cateCollId).setScore(getScore(matchId, cateCollId)).setSort(PeriodEnum.getSort(cateCollId)).setCategorySetShow(true));
                    }else {
                        setVos = matchService.transferSetInfos(setVos, sportId, null);
                    }
                    marketLive.setSetInfos(setVos);
                    marketLive.setSportId(sportId);
                    String oddsLiveTradeSealKey = String.format("rcs:match:oddsLive:trade:seal:%s",marketLive.getMatchId());
                    String oddsLiveTradeSeal = redisClient.get(oddsLiveTradeSealKey);
                    if(StringUtils.isNotBlank(oddsLiveTradeSeal)){
                        redisClient.delete(oddsLiveTradeSealKey);
                        log.info("::{}::MatchCategoryUpdateConsumer更新赛事数据:获取切滚球盘封盘状态", marketLive.getMatchId());
                        marketLive.setOperateMatchStatus(Integer.valueOf(oddsLiveTradeSeal));
                    }
                } else {
                    List<MatchCatgorySetVo> setVos = marketLive.getSetInfos() == null ? new ArrayList<>() : JSON.parseArray(JSONObject.toJSONString(marketLive.getSetInfos()), MatchCatgorySetVo.class);
                    //if(!SportIdEnum.isFootball(sportId)){
                        setVos = matchService.transferSetInfos(setVos, sportId, marketLive.getRoundType());
                    //}

                    if (SportTypeEnum.BASKETBALL.getCode().equals(sportId.intValue()) && !CollectionUtils.isEmpty(setVos) && marketLive.getPeriod() != null && marketLive.getPeriod() > 0) {
                        BasketballEnum enumByPeriod = BasketballEnum.getEnumByPeriod(marketLive.getPeriod());
                        setVos.stream().forEach(setVo -> {
                            setVo.setSort(PeriodEnum.getSort(setVo.getCatgorySetId()));
                        });
                        if (null != enumByPeriod && enumByPeriod.getSort() > 0) {
                            setVos = setVos.stream().filter(vo -> vo.getSort() >= enumByPeriod.getSort()).collect(Collectors.toList());
                        }
                    }
                    if (CollectionUtils.isEmpty(setVos)) {
                        setVos.add(new MatchCatgorySetVo().setCatgorySetId(cateCollId).setScore(getScore(matchId, cateCollId)).setSort(PeriodEnum.getSort(cateCollId)).setCategorySetShow(true));
                    }
                    MatchCatgorySetVo setVo1 = setVos.stream().filter(setVo -> setVo.getCatgorySetId().equals(cateCollId)).findFirst().orElse(null);
                    if (null == setVo1) {
                        setVos.add(new MatchCatgorySetVo().setCatgorySetId(cateCollId).setScore(getScore(matchId, cateCollId)).setSort(PeriodEnum.getSort(cateCollId)).setCategorySetShow(true));
                    }
                    ListUtils.sort(setVos, true, "sort");

                    if (compareEqual(setVos, marketLive.getSetInfos())) {
                        marketLive.setSetInfos(null);
                    } else {
                        marketLive.setSetInfos(setVos);
                    }

                }
            } else {
                if (marketLive == null) {
                    marketLive = new MatchMarketLiveBean();
                    marketLive.setMatchId(matchId);
                    marketLive.setSportId(sportId);
                }
                marketLive.setSetInfos(null);
            }

            matchService.updateTradeType(marketLive);
            matchService.upsertMatch(marketLive);

        } catch (Exception e) {
            log.error("::{}::MatchCategoryUpdateConsumer更新异常:", matchId, e);
        }
    }

    private boolean compareEqual(List<MatchCatgorySetVo> upList, List<MatchCatgorySetVo> oldList) {
        boolean result = false;
        List<Long> up = upList.stream().map(MatchCatgorySetVo::getCatgorySetId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(oldList)) {
            List<Long> old = oldList.stream().map(MatchCatgorySetVo::getCatgorySetId).collect(Collectors.toList());
            if (JsonFormatUtils.toJson(up).equals(JsonFormatUtils.toJson(old))) {
                result = true;
            }
        }
        return result;
    }


    String getScore(Long matchId, Long categorySetId) {
        if (categorySetId.equals(PeriodEnum.FULL_TIME_1.getCategorySetId())) {
            categorySetId = FootballCategorySet.FULL_TIME.getCategorySetId();
        } else if (categorySetId.equals(PeriodEnum.CORNER_KICK_1.getCategorySetId())) {
            categorySetId = FootballCategorySet.CORNER_KICK.getCategorySetId();
        } else if (categorySetId.equals(PeriodEnum.EXTRA_TIME_1.getCategorySetId())) {
            categorySetId = FootballCategorySet.EXTRA_TIME.getCategorySetId();
        } else if (categorySetId.equals(PeriodEnum.PENALTY_SHOOT_1.getCategorySetId())) {
            categorySetId = FootballCategorySet.PENALTY_SHOOT.getCategorySetId();
        }
        String key = String.format(RedisKey.RCS_TASK_MATCH_ALL_SCORE, matchId, categorySetId);
        return redisClient.get(key);
    }


}

