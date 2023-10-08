package com.panda.sport.rcs.task.mq.impl.match;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Maps;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.enums.TradeEnum;
import com.panda.sport.rcs.enums.TradeLevelEnum;
import com.panda.sport.rcs.enums.TraderLevelEnum;
import com.panda.sport.rcs.mongo.MarketCategory;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsTradeConfig;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
import com.panda.sport.rcs.task.config.RedissonManager;
import com.panda.sport.rcs.task.service.MatchServiceImpl;
import com.panda.sport.rcs.task.wrapper.CategoryService;
import com.panda.sport.rcs.task.wrapper.MongoService;
import com.panda.sport.rcs.task.wrapper.RcsLanguageInternationService;
import com.panda.sport.rcs.task.wrapper.StandardSportMarketCategoryService;
import com.panda.sport.rcs.vo.CategoryConVo;
import com.panda.sport.rcs.vo.MatchMarketTradeTypeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.common.MqConstants.TRADE_CONFIG_CHANGE;

/**
 * 盘口数据源改变更新mongodb
 *
 * @author enzo
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "MARKET_CONGIG_UPDTAE_TOPIC",
        consumerGroup = "rcs_task_MARKET_CONGIG_UPDTAE_TOPIC",
        consumeThreadMax = 512,
        consumeTimeout = 10000L)
public class MatchMarketConfigUpdateConsumer  implements RocketMQListener<String> {

    @Autowired
    MongoTemplate mongotemplate;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    private RedissonManager redissonManager;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private MatchServiceImpl matchService;
    @Autowired
    MongoService mongoService;
    @Autowired
    private StandardSportMarketCategoryService marketCategoryService;
    @Autowired
    private RcsLanguageInternationService languageService;

    @Override
    public void onMessage(String msg) {
        log.info("MongoDB变更：topic=MARKET_CONGIG_UPDTAE_TOPIC,msg={}", msg);
        try {
            MatchMarketTradeTypeVo marketTradeTypeVo = JsonFormatUtils.fromJson(msg, MatchMarketTradeTypeVo.class);
            Long sportId = marketTradeTypeVo.getSportId();
            Long matchId = marketTradeTypeVo.getMatchId();
            Integer level = marketTradeTypeVo.getLevel();
            Integer relevanceType = marketTradeTypeVo.getRelevanceType();
            log.info("MARKET_CONGIG_UPDTAE_TOPIC::{}::接收到消息{}",matchId,msg);
            if (relevanceType != null) {
                updateCategory(marketTradeTypeVo);
                return;
            }
            Map<Long, Integer> mainPlayStatusMap = marketTradeTypeVo.getMainPlayStatusMap();
            if (CollectionUtils.isNotEmpty(mainPlayStatusMap)) {
                updateMainPlayStatus( sportId,matchId, mainPlayStatusMap);
                return;
            }
            List<CategoryConVo> categoryConVos = categoryService.selectCategoryCon(sportId);
            if (TraderLevelEnum.MATCH.getLevel().equals(level)) {
                //1.更新赛事
                updateMatch(marketTradeTypeVo);
                if(null!=marketTradeTypeVo.getStatus()) return;
                Query query = new Query();
                query.addCriteria(Criteria.where("matchId").is(String.valueOf(matchId)));
                List<MarketCategory> marketCategories = mongotemplate.find(query, MarketCategory.class);
                if (marketCategories.size() > 0) {
                    List<Long> longs = marketCategories.stream().map(map -> map.getId()).collect(Collectors.toList());
                    for (Long categoryId : longs) {
                        marketTradeTypeVo.setCategoryId(categoryId);
                        //2.更新玩法
                        updateCategory(marketTradeTypeVo);
                    }
                }
            } else if (TraderLevelEnum.STATE.getLevel().equals(level)) {
                if(null!=marketTradeTypeVo.getStatus()) return;
                Long categorySetId = marketTradeTypeVo.getCategorySetId();

                CategoryConVo categoryConVo = categoryConVos.stream().filter(filter -> categorySetId.equals(filter.getId())).findFirst().orElse(null);
                //该玩法集下所有玩法
                List<Long> longs = categoryConVo.categoryIds();
                for (Long categoryId : longs) {
                    marketTradeTypeVo.setCategoryId(categoryId);
                    //1.更新玩法
                    updateCategory(marketTradeTypeVo);
                }

            } else if (TraderLevelEnum.PLAY.getLevel().equals(level)) {
                if(null != marketTradeTypeVo.getStatus()) return;
                //1.更新玩法
                updateCategory(marketTradeTypeVo);
            } else if (TradeLevelEnum.isBatchPlayLevel(level)) {
                if (marketTradeTypeVo.getTradeType() != null) {
                    // 批量玩法 操盘模式 修改
                    List<Long> playIds = marketTradeTypeVo.getPlayIds();
                    playIds.forEach(playId -> {
                        marketTradeTypeVo.setCategoryId(playId);
                        updateCategory(marketTradeTypeVo);
                    });
                }
            }
            
            if (marketTradeTypeVo.getTradeType() != null) {
                Query matchQuery = new Query();
                matchQuery.fields().include("matchId");
                matchQuery.fields().include("categoryCount");
                matchQuery.fields().include("period");
                MatchMarketLiveBean match = mongotemplate.findOne(matchQuery.addCriteria(Criteria.where("matchId").is(matchId)), MatchMarketLiveBean.class);
                if (null != match && marketTradeTypeVo.getAutoCount() != null ) {
                	match.setAutoCount(marketTradeTypeVo.getAutoCount());
                	match.setManualCount(marketTradeTypeVo.getManualCount());
                	match.setAutoAddCount(marketTradeTypeVo.getAutoAddCount());
                	
                	List<Integer> tradeType = new ArrayList<>();
                    if (marketTradeTypeVo.getAutoCount() > 0) tradeType.add(TradeEnum.AUTO.getCode());
                    if (marketTradeTypeVo.getManualCount() > 0) tradeType.add(TradeEnum.MANUAD.getCode());
                    if (marketTradeTypeVo.getAutoAddCount() > 0) tradeType.add(TradeEnum.AUTOADD.getCode());
                    match.setTradeType(tradeType);
                	
                    Map matchMap = new HashMap<>();
                    matchMap.put("matchId", matchId);
                    mongoService.upsert(matchMap, "match_market_live", match);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    void updateMatch(MatchMarketTradeTypeVo marketTradeTypeVo) {
        Long matchId = marketTradeTypeVo.getMatchId();
        RcsTradeConfig rcsTradeConfig = new RcsTradeConfig();
        rcsTradeConfig.setMatchId(String.valueOf(matchId));
        //赛事级别需要转换状态
        if (marketTradeTypeVo.getStatus() != null) rcsTradeConfig.setStatus(marketTradeTypeVo.getStatus());
        if (marketTradeTypeVo.getTradeType() != null) rcsTradeConfig.setDataSource(marketTradeTypeVo.getTradeType());
        producerSendMessageUtils.sendMessage(TRADE_CONFIG_CHANGE, null, String.valueOf(matchId), rcsTradeConfig);

    }

    void updateCategory(MatchMarketTradeTypeVo marketTradeTypeVo) {
        Integer newFlag = marketTradeTypeVo.getNewFlag();
        Long matchId = marketTradeTypeVo.getMatchId();
        Long playId = marketTradeTypeVo.getCategoryId();
        Integer status = marketTradeTypeVo.getStatus();
        Integer tradeType = marketTradeTypeVo.getTradeType();
        Integer relevanceType = marketTradeTypeVo.getRelevanceType();
        if (playId != null) {
            String lock = String.format("MONGODB_MARKET_%s_%s", matchId, playId);
            try {
                redissonManager.lock(lock);
                MarketCategory marketCategory = new MarketCategory();
                marketCategory.setMatchId(String.valueOf(matchId));
                marketCategory.setSportId(marketTradeTypeVo.getSportId());
                marketCategory.setId(playId);
                if (status != null){
                    marketCategory.setStatus(status);
                }
                if (tradeType != null){
                    marketCategory.setTradeType(tradeType);
                }else{
                    if (RcsConstant.BASKETBALL_SINGLE_DOUBLE_PLAY.contains(playId)) {
                        marketCategory.setTradeType(TradeEnum.AUTOADD.getCode());
                    }
                }
                if (relevanceType != null){
                    marketCategory.setRelevanceType(relevanceType);
                }

                //读库玩法表
                StandardSportMarketCategory marketCategoryTemp = marketCategoryService.queryCachedCategory(String.valueOf(marketTradeTypeVo.getSportId()), playId);
                //玩法名称
                if (null != marketCategoryTemp.getNameCode()) {
                    Map<String, String> marketI18Info = languageService.getCachedNamesMapByCode(marketCategoryTemp.getNameCode());
                    marketCategory.setNames(marketI18Info);
                }
                marketCategory.setUpdateTime(DateUtils.parseDate(System.currentTimeMillis(), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
                if(null!=newFlag && newFlag.equals(1)){
                	matchService.setTemplate(marketCategory);
                	
                    //新增盘口切换手动
                    Map map = new HashMap<>();
                    map.put("matchId", String.valueOf(matchId));
                    map.put("id", playId);
                    mongoService.upsert(map, "rcs_market_category", marketCategory);
                    log.info("::{}::mongo更新玩法,matchId:{},id:{},category:{}","updateCategory", matchId, playId, marketCategory);
                    return;
                }
                matchService.updateMongodbOdds(marketCategory);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                redissonManager.unlock(lock);
            }
        }
    }

    private void updateMainPlayStatus(Long sportId, Long matchId, Map<Long, Integer> mainPlayStatusMap) {
        mainPlayStatusMap.forEach((playId, mainPlayStatus) -> {
            String lock = String.format("MONGODB_MARKET_%s_%s", matchId, playId);
            try {
                redissonManager.lock(lock);
                MarketCategory marketCategory = new MarketCategory();
                marketCategory.setMatchId(String.valueOf(matchId));
                marketCategory.setSportId(sportId);
                marketCategory.setId(playId);

                // 读库玩法表
                StandardSportMarketCategory marketCategoryTemp = marketCategoryService.queryCachedCategory(String.valueOf(sportId), playId);
                // 玩法名称
                if (marketCategoryTemp != null && null != marketCategoryTemp.getNameCode()) {
                    Map<String, String> marketI18Info = languageService.getCachedNamesMapByCode(marketCategoryTemp.getNameCode());
                    marketCategory.setNames(marketI18Info);
                }

                matchService.setTemplate(marketCategory);

                marketCategory.setMainPlayStatus(mainPlayStatus);
                marketCategory.setUpdateTime(DateUtils.parseDate(System.currentTimeMillis(), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
                Map<String, Object> map = Maps.newHashMap();
                map.put("matchId", String.valueOf(matchId));
                map.put("id", playId);
                mongoService.upsert(map, "rcs_market_category", marketCategory);
                log.info("::{}::mongo更新玩法,matchId:{},id:{},category:{}", "updateMainPlayStatus", matchId, playId, marketCategory);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                redissonManager.unlock(lock);
            }
        });
    }

}
