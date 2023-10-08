package com.panda.sport.rcs.task.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.UuidUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.TradeMarketUiConfigDTO;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.common.NumberUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.enums.*;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplateMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainMapper;
import com.panda.sport.rcs.mongo.MarketCategory;
import com.panda.sport.rcs.mongo.MatchCatgorySetVo;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.mongo.MatchMarketVo;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.pojo.dto.ClearDTO;
import com.panda.sport.rcs.pojo.dto.ClearSubDTO;
import com.panda.sport.rcs.pojo.dto.MarketCountDTO;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.task.config.RedissonManager;
import com.panda.sport.rcs.task.mq.bean.MatchCategoryUpdateBean;
import com.panda.sport.rcs.task.utils.BallHeadConfigUtils;
import com.panda.sport.rcs.task.wrapper.*;
import com.panda.sport.rcs.utils.DataRealtimeApiUtils;
import com.panda.sport.rcs.vo.CategoryTemplateVo;
import com.panda.sport.rcs.vo.MatchStatusAndDataSuorceVo;
import com.panda.sport.rcs.vo.TournamentTemplateCategoryVo;
import com.panda.sport.rcs.vo.TournamentTemplatePlayVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.common.MqConstants.WS_STATISTICS_NOTIFY_TOPIC;
import static com.panda.sport.rcs.constants.CommonConstants.*;
import static com.panda.sport.rcs.constants.RedisKey.*;
import static com.panda.sport.rcs.constants.RedisKeys.ROLL_INIT_BETAMOUNT;

@Component
@Slf4j
public class MatchServiceImpl {

    @Autowired
    MongoTemplate mongotemplate;

    @Autowired
    MongoService mongoService;

    @Autowired
    RedisClient redisClient;

    @Autowired
    RcsCodeService rcsCodeService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    private RedissonManager redissonManager;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;

    @Autowired
    private RcsMarketOddsConfigService marketOddsConfigService;

    @Autowired
    ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    private RcsTournamentTemplatePlayMargainMapper marketCountMapper;

    @Autowired
    StandardSportMarketCategoryService standardSportMarketCategoryService;
    @Autowired
    private RcsTournamentTemplateMapper templateMapper;
    @Autowired
    private ProducerSendMessageUtils sendMessage;

    @Reference(check = false, lazy = true, retries = 3, timeout = 5000)
    private ITradeMarketConfigApi tradeMarketConfigApi;


    public void updateMongodbOdds(MarketCategory category, boolean isUpdateVoList,String linkedId) {
        StopWatch sw = new StopWatch("mongo更新赛事玩法"+UuidUtils.generateUuid());
        try {
            sw.start("mongo更新玩法字段");
            String matchId = category.getMatchId();
            Long categoryId = category.getId();
            Query query = new Query();
            query.addCriteria(Criteria.where("matchId").is(matchId).and("id").is(categoryId));
            setTemplate(category);
            Update update = new Update();
            List<MatchMarketVo> collect = category.getMatchMarketVoList();
            if (!CollectionUtils.isEmpty(collect)) {
                update.set("matchMarketVoList", collect);
            } else {
                //如果为空，并且已经指定了更新状态
                if (isUpdateVoList) update.set("matchMarketVoList", new ArrayList<MatchMarketVo>());
            }

            if (category.getMatchStartTime() != null) update.set("matchStartTime", category.getMatchStartTime());
            if (category.getSportId() != null) update.set("sportId", category.getSportId());
            if (category.getTemplateId() != null) update.set("templateId", category.getTemplateId());
            if (category.getOrderNo() != null) update.set("orderNo", category.getOrderNo());
            if (category.getStatus() != null) update.set("status", category.getStatus());
            if (category.getTradeType() != null) update.set("tradeType", category.getTradeType());
            if (category.getMainPlayStatus() != null) update.set("mainPlayStatus", category.getMainPlayStatus());
            if (category.getRelevanceType() != null) update.set("relevanceType", category.getRelevanceType());
            if (!CollectionUtils.isEmpty(category.getNames())) update.set("names", category.getNames());
            if (null != category.getMarketCount()) update.set("marketCount", category.getMarketCount());
            if (null != category.getOddsAdjustRange()) update.set("oddsAdjustRange", category.getOddsAdjustRange());
            if (null != category.getMarketAdjustRange())
                update.set("marketAdjustRange", category.getMarketAdjustRange());
            if (null != category.getMargain()) update.set("margain", category.getMargain());
            if (!StringUtils.isBlank(category.getMarketType())) update.set("marketType", category.getMarketType());
            if (null != category.getMarketSource()) update.set("marketSource", category.getMarketSource());
            if (null != category.getCategoryPreStatus()) update.set("categoryPreStatus", category.getCategoryPreStatus());
            if (null != category.getCashOutStatus()) update.set("cashOutStatus", category.getCashOutStatus());
            update.set("updateTime", DateUtils.parseDate(System.currentTimeMillis(), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
            mongotemplate.updateFirst(query, update, MarketCategory.class);
            sw.stop();
            //更新赛事表玩法手动自动数量
            if (category.getTradeType() != null) {
                sw.start("赛事更新玩法数量");
                Query matchQuery = new Query();
                matchQuery.fields().include("matchId");
                matchQuery.fields().include("categoryCount");
                matchQuery.fields().include("period");
                MatchMarketLiveBean match = mongotemplate.findOne(matchQuery.addCriteria(Criteria.where("matchId").is(Long.parseLong(matchId))), MatchMarketLiveBean.class);
                if (null != match) {
                    updateTradeType(match);
                    Map matchMap = new HashMap<>();
                    matchMap.put("matchId", Long.parseLong(matchId));
                    mongoService.upsert(matchMap, "match_market_live", match);
                }
                sw.stop();
            }
            log.info("::{}::mongo更新玩法,matchId:{},id:{},category:{}",linkedId, matchId, categoryId, category);
            log.info("::{}::mongo更新赛事玩法完成,耗时:{}"+sw.prettyPrint(),linkedId,sw.getTotalTimeMillis());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    public void updateMongodbOdds(MarketCategory category) {
        updateMongodbOdds(category, false,"1");
    }


    public void sendMqUpdateColl(MatchCategoryUpdateBean updateBean, List<Long> categories) {
        updateBean.setCategoryIds(categories);
        producerSendMessageUtils.sendMessage(MATCH_CATEGORY_LIVE_UPDATE_TOPIC, null, String.valueOf(updateBean.getMatchId()), updateBean);
    }

    public void setTemplate(MarketCategory category) {
        if (category != null) {
            //玩法模板
            CategoryTemplateVo templateVo = standardSportMarketCategoryService.queryCategoryTemplate(category.getSportId(), category.getId());
            if (templateVo != null) {
                category.setTemplateId(templateVo.getTemplateId());
                category.setOrderNo(templateVo.getOrderNo());
            }
            //联赛设置模板
            MarketCountDTO marketCountDTO = marketCountMapper.marketCountByPlayId(Long.parseLong(category.getMatchId()), category.getId().intValue());
            if (null != marketCountDTO) {
                category.setMarketCount(marketCountDTO.getMarketCount() == null ? 3 : marketCountDTO.getMarketCount());
                if (StringUtils.isNotBlank(marketCountDTO.getMarketType()))
                    category.setMarketType(marketCountDTO.getMarketType());
                if (null != marketCountDTO.getMarketAdjustRange())
                    category.setMarketAdjustRange(marketCountDTO.getMarketAdjustRange());
                if (null != marketCountDTO.getOddsAdjustRange())
                    category.setOddsAdjustRange(marketCountDTO.getOddsAdjustRange());
                if (StringUtils.isNotBlank(marketCountDTO.getMargain()))
                    category.setMargain(marketCountDTO.getMargain());
                if (StringUtils.isNotBlank(marketCountDTO.getDataSource()))
                    category.setDataSource(marketCountDTO.getDataSource());
            }
        }
    }

    public Boolean isLive(Long matchId) {
        try {
            if (matchId == null) {
                return false;
            }
            String value = redisClient.get(RCS_TASK_MATCH_LIVE + matchId);
            log.info("STANDARD_MATCH_SWITCH_STATUS::{}::redis数据:{}",matchId,value);
            if (StringUtils.isNotBlank(value)) {
                if (Arrays.asList(1, 2, 10).contains(Integer.parseInt(value))) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }


    public void updateTradeType(MatchMarketLiveBean match) {
        if (match == null || null == match.getMatchId()) return;
        log.info("::{}::updateTradeType更新赛事玩法数量:" ,match.getMatchId(), JSONObject.toJSONString(match));
        Query query = new Query();
        query.fields().include("matchId");
        query.fields().include("sportId");
        query.fields().include("id");
        query.fields().include("tradeType");
        Criteria criteria = new Criteria();
        criteria.and("matchId").is(String.valueOf(match.getMatchId()));

        criteria.and("matchMarketVoList.oddsFieldsList.id").gt(0L);
        //数据源展示开、封状态
        criteria.and("matchMarketVoList.thirdMarketSourceStatus").in(Arrays.asList(0, 1));
        query.addCriteria(criteria);
        List<MarketCategory> marketCategories = mongotemplate.find(query, MarketCategory.class);
        marketCategories.stream().forEach(model -> {
            if (null == model.getTradeType()) model.setTradeType(0);
        });
        /*String json = JsonFormatUtils.toJson(marketCategories);
        String key = RCS_TASK_MATCH_CATEGORY_CACHE + match.getMatchId();
        String value = redisClient.get(key);
        if (StringUtils.isNotBlank(value) && value.equals(json)) {
            log.warn("赛事玩法数无需更新:" + match.getMatchId() + JsonFormatUtils.toJson(match));
            return;
        }
        redisClient.setExpiry(key, json, EXPRIY_TIME_5_MINS);*/
        if (!CollectionUtils.isEmpty(marketCategories)) {
            Integer categoryCount = marketCategories.size();
            Integer autoCount = 0;
            Integer manualCount = 0;
            Integer autoAddCount = 0;
            for (MarketCategory category : marketCategories) {
                if (TradeEnum.AUTO.getCode().equals(category.getTradeType())) {
                    autoCount++;
                } else if (TradeEnum.MANUAD.getCode().equals(category.getTradeType())) {
                    manualCount++;
                } else if (TradeEnum.AUTOADD.getCode().equals(category.getTradeType())) {
                    autoAddCount++;
                }
            }
            match.setAutoCount(autoCount);
            match.setManualCount(manualCount);
            match.setAutoAddCount(autoAddCount);

            if (null != match.getCategoryCount() && !categoryCount.equals(match.getCategoryCount())) {
                sendMqCategoryCount(match, categoryCount);
            }
            match.setCategoryCount(categoryCount);
            List<Integer> tradeType = new ArrayList<>();
            if (autoCount > 0) tradeType.add(TradeEnum.AUTO.getCode());
            if (manualCount > 0) tradeType.add(TradeEnum.MANUAD.getCode());
            if (autoAddCount > 0) tradeType.add(TradeEnum.AUTOADD.getCode());
            match.setTradeType(tradeType);
        } else {
            match.setAutoCount(0);
            match.setManualCount(0);
            match.setAutoAddCount(0);
            match.setCategoryCount(0);
            sendMqCategoryCount(match, 0);
        }
    }

    private void sendMqCategoryCount(MatchMarketLiveBean match, Integer categoryCount) {
        JSONObject object = new JSONObject();
        object.put("standardMatchId", match.getMatchId());
        object.put("categoryCount", categoryCount);
        object.put("channel", -1);
        producerSendMessageUtils.sendMessage(WS_STATISTICS_NOTIFY_TOPIC, null, String.valueOf(match.getMatchId()), object);
    }

    public void updateCategories(Long matchId) {
        if (!isLive(matchId)) return;
        if (!redisClient.exist(String.format(ROLL_INIT_BETAMOUNT, matchId))) {
            //重置操盘平台
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.selectOne(matchId);
            log.info("STANDARD_MATCH_SWITCH_STATUS::{}::重置操盘平台:{}",matchId,JSONObject.toJSONString(standardMatchInfo));
            if (standardMatchInfo != null) {
                Update update = new Update();
                update.set("riskManagerCode", standardMatchInfo.getLiveRiskManagerCode());

                //滚球重置操盘人数
                //update.set("traderNum", 1);

                mongotemplate.updateFirst(new Query().addCriteria(Criteria.where("matchId").is(matchId)), update, MatchMarketLiveBean.class);
                Long sportId = standardMatchInfo.getSportId();
                //发送MQ，赛前十五分钟设置生效
                if (Arrays.asList(1L, 2L, 3L, 5L, 7L, 8L, 9L, 10L).contains(sportId)) {
                    producerSendMessageUtils.sendMessage(MATCH_LIVE_SET_TOPIC, null, String.valueOf(matchId), matchId);
                    log.info("::{}::发送MONGODB_MATCH_SET消息", matchId);
                }
                MatchStatusAndDataSuorceVo matchStatusAndDataSuorceVo = new MatchStatusAndDataSuorceVo();
                matchStatusAndDataSuorceVo.setMatchId(matchId);
                matchStatusAndDataSuorceVo.setRiskManagerCode(standardMatchInfo.getLiveRiskManagerCode());
                producerSendMessageUtils.sendMessage(MqConstants.DATA_SOURCE_DURING_GAME_PLAY_TOPIC, MqConstants.DATA_SOURCE_DURING_GAME_PLAY_TAG, String.valueOf(matchId),
                        matchStatusAndDataSuorceVo);
                //早盘切滚球在trade项目中处理
               /* if(sportId == 1L && "OTS".equals(standardMatchInfo.getLiveRiskManagerCode())){
                    //发送赛事级别封盘消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("tradeLevel", TradeLevelEnum.MATCH.getLevel());
                    jsonObject.put("sportId", SportIdEnum.BASKETBALL.getId());
                    jsonObject.put("matchId", matchId);
                    jsonObject.put("status", TradeStatusEnum.OPEN.getStatus());
                    jsonObject.put("remark", "OTS滚球标识，赛事自动开盘");
                    Request<JSONObject> requestDTO = new Request<>();
                    requestDTO.setData(jsonObject);
                    requestDTO.setLinkId(matchId + "_OTS_AUTO_OPEN");
                    requestDTO.setDataSourceTime(System.currentTimeMillis());
                    log.error("赛事：{}，OTS赛事自动",matchId);
                    producerSendMessageUtils.sendMessage("RCS_TRADE_UPDATE_MARKET_STATUS", matchId + "_OTS_AUTO_OPEN", requestDTO.getLinkId(), requestDTO);
                }*/
            }
            //滚球期望值、实货量清零
            zeroCargo(matchId);
            //今日-->滚球 平衡值清零
            sendMQMessage(matchId);
        }
    }

    /**
     * @return void
     * @Description //发送消息清空配置
     * @Param [matchId, marketId]
     * @Author Sean
     * @Date 11:25 2020/3/12
     **/
    public void sendMQMessage(Long matchId) {

        Long startTime = System.currentTimeMillis();
        log.info("清除货量数据start : matchId={}", matchId);
        QueryWrapper<StandardMatchInfo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StandardMatchInfo::getId, matchId);
//        wrapper.lambda().select(StandardMatchInfo::getBeginTime);
        StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectOne(wrapper);
        if (ObjectUtils.isEmpty(standardMatchInfo)) {
            log.error("赛事表没有这个赛事{}", matchId);
            return;
        }
        if (ObjectUtils.isEmpty(standardMatchInfo.getBeginTime())) {
            standardMatchInfo.setBeginTime(System.currentTimeMillis());
        }
//        String dateExpect = DateUtils.getDateExpect(standardMatchInfo.getBeginTime());
//        QueryWrapper<StandardSportMarket> queryWrapper = new QueryWrapper();
//        queryWrapper.lambda().eq(StandardSportMarket::getStandardMatchInfoId, matchId);
//        List<StandardSportMarket> list = standardSportMarketMapper.selectList(queryWrapper);
//        for (StandardSportMarket market : list) {
//            String key = String.format(RedisKeys.CALC_AMOUNT_ODDS_CHANGE, dateExpect, market.getId());
//            String keyPlus = String.format(CALC_AMOUNT_ODDS_CHANGE_PLUS, dateExpect, market.getId());
//            String suffixKey = "{" + market.getId() + "}";
//            redisClient.delete(key + suffixKey);
//            redisClient.delete(keyPlus + suffixKey);
//            redisClient.delete(key + ":count" + suffixKey);
//            redisClient.delete(key + ":lock" + suffixKey);
//        }
//        rcsMatchMarketConfigMapper.updateAllMarketToZeroByMatchId(map);
//        log.info("平衡值存入MQ消息队列:{}",balanceVo);
        String key = UuidUtils.generateUuid();
        producerSendMessageUtils.sendMessage(RCS_CLEAR_MARKET_CONFIG_TOPIC, RCS_CLEAR_MARKET_CONFIG_TAG, key, standardMatchInfo);
        log.info("::{}::清除早盘数据end : time:{},key={}", matchId, (System.currentTimeMillis() - startTime), key);

    }

    void zeroCargo(Long matchId) {
        List<MarketCountDTO> marketCountDTOS = marketCountMapper.marketCountByMatchId(matchId);
        Map<Integer, MarketCountDTO> dtoMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(marketCountDTOS)) {
            dtoMap = marketCountDTOS.stream().collect(Collectors.toMap(e -> e.getPlayId(), e -> e));
        }
        Query categoryQuery = Query.query(Criteria.where("matchId").is(String.valueOf(matchId)));
        List<MarketCategory> marketCategories = mongotemplate.find(categoryQuery, MarketCategory.class);
        if (!CollectionUtils.isEmpty(marketCategories)) {
            for (MarketCategory category : marketCategories) {
                String lock = String.format("MONGODB_MARKET_%s_%s", matchId, category.getId());
                try {
                    Query query = Query.query(Criteria.where("matchId").is(String.valueOf(matchId)).and("id").is(category.getId()));
                    redissonManager.lock(lock);
                    MarketCategory marketCategory = mongotemplate.findOne(query, MarketCategory.class);
                    List<MatchMarketVo> matchMarketVoList = marketCategory.getMatchMarketVoList();
                    if (!CollectionUtils.isEmpty(matchMarketVoList)) {
                        matchMarketVoList.stream().forEach(marketVo -> {
                            marketVo.setMarketHeadGap(0.0);
                            marketVo.getOddsFieldsList().stream().forEach(matchMarketOddsFieldVo -> {
                                RcsMarketOddsConfig marketOddsConfig = new RcsMarketOddsConfig();
                                marketOddsConfig.setMarketOddsId(matchMarketOddsFieldVo.getId());
                                marketOddsConfig.setMatchType("2");
                                RcsMarketOddsConfig marketOdd = marketOddsConfigService.getMarketOdds(marketOddsConfig);
                                if (marketOdd != null) {
                                    matchMarketOddsFieldVo.setBetAmount(NumberUtils.getBigDecimal(marketOdd.getBetAmount()));
                                    matchMarketOddsFieldVo.setProfitValue(NumberUtils.getBigDecimal(marketOdd.getProfitValue()));
                                    matchMarketOddsFieldVo.setBetNum(NumberUtils.getBigDecimal(marketOdd.getBetOrderNum()));
                                } else {
                                    matchMarketOddsFieldVo.setProfitValue(BigDecimal.ZERO);
                                    matchMarketOddsFieldVo.setBetAmount(BigDecimal.ZERO);
                                    matchMarketOddsFieldVo.setBetNum(BigDecimal.ZERO);
                                }
                                matchMarketOddsFieldVo.setMarketDiffValue(0.0);
                            });
                        });
                    }

                    Update categoryUpdate = new Update();
                    MarketCountDTO dto = dtoMap.get(category.getId().intValue());
                    if (null != dto) {
                        if (null != dto.getMarketCount())
                            categoryUpdate.set("marketCount", dto.getMarketCount());
                        if (null != dto.getOddsAdjustRange())
                            categoryUpdate.set("oddsAdjustRange", dto.getOddsAdjustRange());
                        if (null != dto.getMarketAdjustRange())
                            categoryUpdate.set("marketNearOddsDiff", dto.getMarketAdjustRange());
                        if (StringUtils.isNotBlank(dto.getMarketType()))
                            categoryUpdate.set("marketType", dto.getMarketType());

                    }
                    categoryUpdate.set("forecast", null);
                    categoryUpdate.set("matchMarketVoList", matchMarketVoList);
                    mongotemplate.updateFirst(query, categoryUpdate, MarketCategory.class);
                    log.info("::{}::mongo更新玩法,matchId:{},id:{},category:{}","zeroCargo", matchId, category.getId(), category);

                } catch (Exception e) {
                    log.error("mongo更新滚球初始化实货量异常" + e.getMessage(), e);
                } finally {
                    redissonManager.unlock(lock);
                }
            }
            //页面清除货量
            JSONObject message = new JSONObject();
            message.put("matchId", matchId);
            message.put("clearZero", 1);
            producerSendMessageUtils.sendMessage(WS_MARKET_PLACE_DATA_TOPIC, "1", String.valueOf(matchId), message);
            redisClient.setExpiry(String.format(ROLL_INIT_BETAMOUNT, matchId), matchId, EXPRIY_TIME_5_HOURS);
        }
    }

    public List<MatchCatgorySetVo> transferSetInfos(List<MatchCatgorySetVo> setInfos, Long sportId,Integer roundType) {
       if(roundType==null||roundType==0){
           roundType = 5;
       }
        if (SportIdEnum.isBasketball(sportId)) {
            BasketballEnum[] values = BasketballEnum.values();
            if (CollectionUtils.isEmpty(setInfos)) {
                setInfos = new ArrayList<>();
                for (BasketballEnum b : values) {
                    setInfos.add(new MatchCatgorySetVo().setCatgorySetId(b.getCategorySetId()));
                }
            } else {
                for (BasketballEnum b : values) {
                    MatchCatgorySetVo setVo = setInfos.stream().filter(vo -> vo.getCatgorySetId().equals(b.getCategorySetId())).findFirst().orElse(null);
                    if (setVo == null) {
                        setInfos = new ArrayList<>(setInfos);
                        setInfos.add(new MatchCatgorySetVo().setCatgorySetId(b.getCategorySetId()));
                    }
                }
            }
        }else {
            if(SportIdEnum.isFootball(sportId)) {
                roundType= 4;
            }
            Long min = sportId*10000+1;
            Long max = min+roundType;
            if(CollectionUtils.isEmpty(setInfos)){
                setInfos = new ArrayList<>();
                for (Long i = min; i <=max ; i++) {
                    Long setId= i;
                    MatchCatgorySetVo setVo = setInfos.stream().filter(vo -> vo.getCatgorySetId().equals(setId)).findFirst().orElse(null);
                    if (setVo == null) {
                        setInfos.add(new MatchCatgorySetVo().setCatgorySetId(setId).setSort(PeriodEnum.getSort(setId)));
                    }
                }
            }else {
                for (Long i = min; i <=max ; i++) {
                    Long setId= i;
                    MatchCatgorySetVo setVo = setInfos.stream().filter(vo -> vo.getCatgorySetId().equals(setId)).findFirst().orElse(null);
                    if (setVo == null) {
                        setInfos.add(new MatchCatgorySetVo().setCatgorySetId(setId).setSort(PeriodEnum.getSort(setId)));
                    }
                }
            }
        }

        return setInfos;
    }

    /**
     * 比分更新对应玩法清水差
     *
     * @param matchId
     * @param categoryIds
     */
    public void clearMarketDiffValue(Long matchId, List<Long> categoryIds) {
        Criteria criteria = new Criteria();
        criteria.and("matchId").is(String.valueOf(matchId));
        if (!CollectionUtils.isEmpty(categoryIds)) {
            criteria.and("id").in(categoryIds);
        }
        Query categoryQuery = Query.query(criteria);
        List<MarketCategory> marketCategories = mongotemplate.find(categoryQuery, MarketCategory.class);
        if (CollectionUtils.isEmpty(marketCategories)) return;
        List<Long> ids = marketCategories.stream().map(map -> map.getId()).collect(Collectors.toList());
        List<ClearSubDTO> configs = new ArrayList<>();
        for (Long id : ids) {
            ClearSubDTO config = new ClearSubDTO();
            config.setMatchId(matchId);
            config.setPlayId(id);
            configs.add(config);
        }
        ClearDTO clearDTO = new ClearDTO();
        clearDTO.setType(0);
        clearDTO.setClearType(3);
        clearDTO.setMatchId(matchId);
        clearDTO.setList(configs);
        if (CollectionUtils.isEmpty(configs)) return;
        producerSendMessageUtils.sendMessage("RCS_CLEAR_MATCH_MARKET_TAG", null, UuidUtils.generateUuid(), clearDTO);

        /*JSONObject message = new JSONObject();
        message.put("matchId", matchId);
        message.put("clearZero", 2);
        message.put("categoryIds",ids);
        producerSendMessageUtils.sendMessage(WS_MARKET_PLACE_DATA_TOPIC, "2", String.valueOf(matchId), message);*/
        if (!CollectionUtils.isEmpty(marketCategories)) {
            for (MarketCategory category : marketCategories) {
                String lock = String.format("MONGODB_MARKET_%s_%s", matchId, category.getId());
                try {
                    Query query = Query.query(Criteria.where("matchId").is(String.valueOf(matchId)).and("id").is(category.getId()));
                    redissonManager.lock(lock);
                    MarketCategory marketCategory = mongotemplate.findOne(query, MarketCategory.class);
                    if (marketCategory != null) {
                        List<MatchMarketVo> matchMarketVoList = marketCategory.getMatchMarketVoList();
                        if (!CollectionUtils.isEmpty(matchMarketVoList)) {
                            matchMarketVoList.stream().forEach(marketVo -> {
                                marketVo.getOddsFieldsList().stream().forEach(oddsVo -> {
                                    oddsVo.setMarketDiffValue(0.0);
                                });
                            });
                            Update categoryUpdate = new Update();
                            categoryUpdate.set("matchMarketVoList", matchMarketVoList);
                            mongotemplate.updateFirst(query, categoryUpdate, MarketCategory.class);
                            log.info("::{}::mongo更新玩法,matchId:{},id:{},category:{}","clearMarketDiffValue", matchId, category.getId(), category);
                        }
                    }
                } catch (Exception e) {
                    log.error("足球赛事清水差" + e.getMessage(), e);
                } finally {
                    redissonManager.unlock(lock);
                }

            }
        }
    }

    public String liveRiskManageCode(Long matchId) {
        String key = String.format(MATCH_LIVE_RISK_MANAGERCODE, matchId);
        String value = redisClient.get(key);
        if (StringUtils.isBlank(value)) {
            MatchMarketLiveBean match = mongotemplate.findOne(new Query().addCriteria(Criteria.where("matchId").is(matchId)), MatchMarketLiveBean.class);
            if (match != null && StringUtils.isNotBlank(match.getRiskManagerCode())) {
                redisClient.setExpiry(key, match.getRiskManagerCode(), EXPRIY_TIME_2_HOURS);
                return match.getRiskManagerCode();
            }
        }
        return value;
    }

    public void upsertMatch(MatchMarketLiveBean marketLiveBean) {
        Map map = new HashMap<>();
        map.put("matchId", marketLiveBean.getMatchId());
        mongoService.upsert(map, "match_market_live", marketLiveBean);
        log.info("::{}::Match更新赛事数据 = {}",marketLiveBean.getMatchId(), JSONObject.toJSONString(marketLiveBean));
    }


    public void updateMongo(Query query, Update update) {
        mongotemplate.updateFirst(query, update, MatchMarketLiveBean.class);
        log.info("Match更新赛事数据 query= {},update={}", JSONObject.toJSONString(query), JSONObject.toJSONString(update));
    }

    /**
     * 比赛阶段判断，排球到决胜局后发送球头配置给融合
     */
    public void ballHeadConfigHandler(MatchPeriod matchPeriod) {
        try {
            if (matchPeriod.getSportId().equals(SportIdEnum.VOLLEYBALL.getId()) || matchPeriod.getSportId().equals(SportIdEnum.ICE_HOCKEY.getId())) {

            StandardMatchInfo matchInfo = standardMatchInfoService.getById(matchPeriod.getStandardMatchId());
            log.info(":{}::球头配置发送:Message:{}->{}", matchPeriod.getStandardMatchId(), JSONObject.toJSON(matchPeriod), JSONObject.toJSON(matchInfo));
            int roundType = matchInfo.getRoundType();

            matchInfo.setMatchPeriodId(Long.parseLong(String.valueOf(matchPeriod.getPeriod())));
            if(matchPeriod.getSportId().equals(SportIdEnum.VOLLEYBALL.getId())){
                if ((3 == roundType && 10== matchPeriod.getPeriod())
                        || (5 == roundType && 12== matchPeriod.getPeriod())
                        || (7 == roundType && 442== matchPeriod.getPeriod())
                ){

//                if ((3 == roundType && Long.valueOf(PeriodEnum.VOLLEYBALL_THREE_9.getPeriod()).equals(matchPeriod.getPeriod()))
//                        || (5 == roundType && Long.valueOf(PeriodEnum.VOLLEYBALL_FIVE_9.getPeriod()).equals(matchPeriod.getPeriod()))
//                        || (7 == roundType && Long.valueOf(PeriodEnum.VOLLEYBALL_SEVEN_9.getPeriod()).equals(matchPeriod.getPeriod()))) {
                    //是决胜局
                    LambdaQueryWrapper<RcsTournamentTemplate> templateWrapper = new LambdaQueryWrapper<>();
                    templateWrapper.eq(RcsTournamentTemplate::getType, 3);
                    templateWrapper.eq(RcsTournamentTemplate::getTypeVal, matchInfo.getId());
                    templateWrapper.eq(RcsTournamentTemplate::getMatchType, 0);
                    RcsTournamentTemplate tournamentTemplate = templateMapper.selectOne(templateWrapper);
                    LambdaQueryWrapper<RcsTournamentTemplatePlayMargain> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(RcsTournamentTemplatePlayMargain::getTemplateId, tournamentTemplate.getId());
                    // 253 254才有决胜局的配置
                    wrapper.in(RcsTournamentTemplatePlayMargain::getPlayId, Arrays.asList(253, 254));
                    List<RcsTournamentTemplatePlayMargain> playMargains = marketCountMapper.selectList(wrapper);
                    if (CollUtil.isEmpty(playMargains)) {
                        log.info(":{}::球头配置发送:Message:Margain is null", matchPeriod.getStandardMatchId());
                        return;
                    }
                    List<TournamentTemplateCategoryVo> categoryList = playMargains.stream().map(item -> {
                        TournamentTemplateCategoryVo vo = BeanCopyUtils.copyProperties(item, TournamentTemplateCategoryVo.class);
                        BallHeadConfigUtils.setVoParam(matchInfo, vo, item);
                        return vo;
                    }).collect(Collectors.toList());
                    TournamentTemplatePlayVo playVo = getTournamentTemplatePlayVo(tournamentTemplate);
                    playVo.setCategoryList(categoryList);

                    String linkIdByMq = com.panda.sport.rcs.utils.CommonUtils.getLinkId("play_template_update");
                    Request request = new Request();
                    request.setData(playVo);
                    request.setGlobalId(linkIdByMq);
                    log.info("::{}::{}::球头配置发送:Message:{}", linkIdByMq, matchPeriod.getStandardMatchId(), JSONObject.toJSON(request));
                    sendMessage.sendMessage("Tournament_Template_Play", linkIdByMq, String.valueOf(playVo.getStandardMatchId()), JSONObject.toJSON(request));

                    if(CollUtil.isNotEmpty(categoryList)) {
                        //综合球种才有球头配置
                        if (!tournamentTemplate.getSportId().equals(1)) {

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                log.error(e.getMessage());
                            }

                            for (TournamentTemplateCategoryVo tournamentTemplateCategoryVo : categoryList) {
                                ///修改球头配置后需要根据配置判断是否封盘，所以要调用融合这个接口
                                TradeMarketUiConfigDTO dto1 = new TradeMarketUiConfigDTO();
                                dto1.setStandardMatchInfoId(tournamentTemplate.getTypeVal());
                                //补充玩法ID
                                dto1.setStandardCategoryId(tournamentTemplateCategoryVo.getPlayId());
                                //此处是滚球
                                dto1.setMarketType(0);
                                dto1.setPlaceNum(1);

                                //触发赔率下发
                                DataRealtimeApiUtils.handleApi(dto1, new DataRealtimeApiUtils.ApiCall() {
                                    @Override
                                    public <R> Response<R> callApi(com.panda.merge.dto.Request request) {
                                        log.info("sleep to for call 球头配置发送 触发赔率！");

                                        return tradeMarketConfigApi.putTradeMarketUiConfig(request);
                                    }
                                });
                            }

                        }
                    }

                }
            }

            //冰球
            else if (matchPeriod.getSportId().equals(SportIdEnum.ICE_HOCKEY.getId())) {

                //是决胜局
                LambdaQueryWrapper<RcsTournamentTemplate> templateWrapper = new LambdaQueryWrapper<>();
                templateWrapper.eq(RcsTournamentTemplate::getType, 3);
                templateWrapper.eq(RcsTournamentTemplate::getTypeVal, matchInfo.getId());
                templateWrapper.eq(RcsTournamentTemplate::getMatchType, 0);
                RcsTournamentTemplate tournamentTemplate = templateMapper.selectOne(templateWrapper);
                LambdaQueryWrapper<RcsTournamentTemplatePlayMargain> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(RcsTournamentTemplatePlayMargain::getTemplateId, tournamentTemplate.getId());
                // 冰球加时赛玩法
                wrapper.in(RcsTournamentTemplatePlayMargain::getPlayId, Arrays.asList(2, 4));
                List<RcsTournamentTemplatePlayMargain> playMargains = marketCountMapper.selectList(wrapper);
                if (CollUtil.isEmpty(playMargains)) {
                    log.info(":{}::球头配置发送:Message:Margain is null", matchPeriod.getStandardMatchId());
                    return;
                }
                List<TournamentTemplateCategoryVo> categoryList = playMargains.stream().map(item -> {
                    TournamentTemplateCategoryVo vo = BeanCopyUtils.copyProperties(item, TournamentTemplateCategoryVo.class);
                    BallHeadConfigUtils.setVoParam(matchInfo, vo, item);
                    return vo;
                }).collect(Collectors.toList());
                TournamentTemplatePlayVo playVo = getTournamentTemplatePlayVo(tournamentTemplate);
                playVo.setCategoryList(categoryList);

                String linkIdByMq = com.panda.sport.rcs.utils.CommonUtils.getLinkId("play_template_update");
                Request request = new Request();
                request.setData(playVo);
                request.setGlobalId(linkIdByMq);
                log.info("::{}::{}::球头配置发送:Message:{}", linkIdByMq, matchPeriod.getStandardMatchId(), JSONObject.toJSON(request));
                sendMessage.sendMessage("Tournament_Template_Play", linkIdByMq, String.valueOf(playVo.getStandardMatchId()), JSONObject.toJSON(request));

                if(CollUtil.isNotEmpty(categoryList)) {
                    //综合球种才有球头配置
                    if (!tournamentTemplate.getSportId().equals(1)) {

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            log.error(e.getMessage());
                        }

                        for (TournamentTemplateCategoryVo tournamentTemplateCategoryVo : categoryList) {
                            ///修改球头配置后需要根据配置判断是否封盘，所以要调用融合这个接口
                            TradeMarketUiConfigDTO dto1 = new TradeMarketUiConfigDTO();
                            dto1.setStandardMatchInfoId(tournamentTemplate.getTypeVal());
                            //补充玩法ID
                            dto1.setStandardCategoryId(tournamentTemplateCategoryVo.getPlayId());
                            //此处是滚球
                            dto1.setMarketType(0);
                            dto1.setPlaceNum(1);

                            //触发赔率下发
                            DataRealtimeApiUtils.handleApi(dto1, new DataRealtimeApiUtils.ApiCall() {
                                @Override
                                public <R> Response<R> callApi(com.panda.merge.dto.Request request) {
                                    log.info("sleep to for call");

                                    return tradeMarketConfigApi.putTradeMarketUiConfig(request);
                                }
                            });
                        }

                    }
                }
            }
            }


        }catch (Exception e){
            log.error("::{}::球头配置发送异常::{}" ,matchPeriod.getStandardMatchId(), e.getMessage(), e);

        }
    }

    private TournamentTemplatePlayVo getTournamentTemplatePlayVo(RcsTournamentTemplate param) {
        TournamentTemplatePlayVo playVo = new TournamentTemplatePlayVo();
        DataSourceCodeVo weight = JSONObject.parseObject(param.getDataSourceCode(), DataSourceCodeVo.class);
        playVo.setStandardMatchId(param.getTypeVal());
        playVo.setMatchType(param.getMatchType());
        playVo.setBcWeight(weight.getBc());
        playVo.setBgWeight(weight.getBg());
        playVo.setSrWeight(weight.getSr());
        playVo.setTxWeight(weight.getTx());
        playVo.setRbWeight(weight.getRb());
        playVo.setPdWeight(weight.getPd());
        playVo.setAoWeight(weight.getAo());
        playVo.setPiWeight(weight.getPi());
        playVo.setLsWeight(weight.getLs());
        return playVo;
    }
}