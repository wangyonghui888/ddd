package com.panda.sport.rcs.task.mq.impl.match;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.common.util.UuidUtils;
import com.google.common.collect.Lists;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.common.NumberUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.enums.TradeStatusEnum;
import com.panda.sport.rcs.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.mongo.MarketCategory;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.mongo.MatchMarketOddsVo;
import com.panda.sport.rcs.mongo.MatchMarketVo;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
import com.panda.sport.rcs.pojo.odd.StandardMarketMessage;
import com.panda.sport.rcs.pojo.odd.StandardMarketOddsMessage;
import com.panda.sport.rcs.pojo.odd.StandardMatchMarketMessage;
import com.panda.sport.rcs.task.config.RedissonManager;
import com.panda.sport.rcs.task.mq.bean.DataRealTimeMessageBean;
import com.panda.sport.rcs.task.mq.bean.MatchCategoryUpdateBean;
import com.panda.sport.rcs.task.service.MatchServiceImpl;
import com.panda.sport.rcs.task.utils.CommonUtil;
import com.panda.sport.rcs.task.utils.NameExpressionValueUtils;
import com.panda.sport.rcs.task.wrapper.*;
import com.panda.sport.rcs.utils.ListUtils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 赛事盘口赔率改变更新mongodb
 *
 * @author ENZO
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "STANDARD_MARKET_ODDS",
        consumerGroup = "rcs_task_STANDARD_MARKET_ODDS",
        consumeThreadMax = 512,
        consumeTimeout = 10000L)
public class MatchMarketOddsChangeV2Consumer implements RocketMQListener<String> {

    @Autowired
    MongoTemplate mongotemplate;

    @Autowired
    private RcsLanguageInternationService rcsLanguageService;

    @Autowired
    private StandardSportMarketService standardSportMarketService;

    @Autowired
    private StandardSportMarketCategoryService marketCategoryService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private MatchServiceImpl matchServiceImpl;

    @Autowired
    private RedissonManager redissonManager;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    StandardSportMarketMapper standardSportMarketMapper;

    @Autowired
    private RcsOddsConvertMappingService rcsOddsConvertMappingService;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    private String REDIS_CACHE_CATEGORY_KEY = "rcs:category_name_code:%s:%s";

    private static String ODDS_MD5_CACHE_KEY = "rcs:task:odd:cache:md5:%s";

//    private static String RCS_MARKET_STATUS_CONFIG = "rcs:marketStatusConfig:%s:%s";
    // 可操盘球种
    private static List<String> TRADE_SPORT = Lists.newArrayList("1","2","3","4","5","7","8","9","10");
    /**
     * 占位符主玩法状态缓存key
     */
    private static final String PLACEHOLDER_MAIN_PLAY_STATUS_KEY = "rcs:tradeStatus:placeholderMainPlay:%s";


    @Override
    public void onMessage(String message) {
        StopWatch sw = new StopWatch("STANDARD_MARKET_ODDS赔率消费mongo处理"+ UuidUtils.generateUuid());
        Long matchId = null;
        String sportId = null;
        String linkId ="";
        List<Long> marketCategories = new ArrayList<Long>();
        sw.start("封装处理赔率");
        try {
            DataRealTimeMessageBean<StandardMatchMarketMessage> msg = JSONObject.parseObject(message, new TypeReference<DataRealTimeMessageBean<StandardMatchMarketMessage>>() {});
            if (StringUtils.isNotEmpty(msg.getDataType())&&msg.getDataType().equalsIgnoreCase("HeartBeat")) {
                return;
            }
            Long datasourceTime = msg.getDataSourceTime();
            linkId = msg.getLinkId();
            if (datasourceTime == null) {
                log.warn("::{}::没有带时间戳，当前数据不做更新！",linkId);
                return;
            }
            log.info("STANDARD_MARKET_ODDS::{}::收到消息{}",linkId,message);
            StandardMatchMarketMessage msgBean = msg.getData();

            matchId = msgBean.getStandardMatchInfoId();
            sportId = String.valueOf(msgBean.getSportId());
            List<StandardMarketMessage> marketList = msgBean.getMarketList();
            if (CollectionUtils.isEmpty(marketList)) {
                marketList = new ArrayList<>();
            }
            //赔率更新
            Map<String, Map<String, StandardMarketMessage>> groupMap = getPlayGroup(marketList, datasourceTime, matchId,linkId);
            sw.stop();
            sw.start("上游赔率下发更新mongo");
            Map<String, StandardMatchInfo> matchInfoMap = new HashMap<String, StandardMatchInfo>();
            log.info("::{}::groupMap{}",linkId,groupMap);
            for (String playId : groupMap.keySet()) {
                String lock = String.format("MONGODB_MARKET_%s_%s", matchId, playId);
                try {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("matchId").is(String.valueOf(matchId)).and("id").is(Long.parseLong(playId)));
                    Map<String, StandardMarketMessage> msgMarketMap = groupMap.get(playId);
                    Map<String, StandardMarketMessage> bakMarketMap = new HashMap();

                    redissonManager.lock(lock);
                    MarketCategory category = mongotemplate.findOne(query, MarketCategory.class);
                    log.info("::{}::category:{}",linkId,category);
                    Boolean isSave = false;
                    if (category == null) {
                        //第一次做新增 构建category数据
                        for (String marketId : msgMarketMap.keySet()) {
                            StandardMarketMessage msgMarket = msgMarketMap.get(marketId);
                            if (msgMarket.getMarketOddsList() == null || msgMarket.getMarketOddsList().size() <= 0) {
                                continue;
                            }

                            category = buildMarketCategory(msgMarket, msgBean.getStandardMatchInfoId(), sportId);
                            category.setMarketSource(msgMarket.getMarketSource());
                            isSave = true;
                            break;
                        }
                        //赛事不存在，不在处理
                        if (category == null) {
                            continue;
                        }
                    }
                    log.info("::{}::category2:{}",linkId,category);
                    //对数据做更新
                    category.setSportId(msgBean.getSportId());
                    putCatgoryInfo(matchId, category, matchInfoMap);
                    log.info("::{}::category3:{}",linkId,category);
                    List<MatchMarketVo> currentMarketList = new ArrayList<>();

                    if (category.getMatchMarketVoList() == null) {
                        category.setMatchMarketVoList(new ArrayList<>());
                    }
                    log.info("::{}::category4:{}",linkId,category);
                    for (MatchMarketVo vo : category.getMatchMarketVoList()) {
                        String marketId = String.valueOf(vo.getId());

                        if (!msgMarketMap.containsKey(marketId)) {
                            continue;
                        }

                        //当前数据源队列数据
                        StandardMarketMessage msgMarket = msgMarketMap.remove(marketId);
                        //bug-44150 putMonitorInfo(bakMarketMap,marketId,msgMarket);
                        // pastatus 为12 表示盘口弃用
                        if (msgMarket.getPaStatus() != null && msgMarket.getPaStatus() == 12) {
                        	log.info("linkId::{}::,赛事id:{},玩法id:{}当前盘口paStatus为12,盘口弃用",linkId, matchId, msgMarket.getMarketCategoryId());
                        	continue;
                        }
                        
                        if (!isUpdateByTime(datasourceTime, vo)) {
                            currentMarketList.add(vo);
                            continue;
                        }
    
                        MatchMarketVo buildVo = null;
                        if (msgMarket.getMarketOddsList() == null || msgMarket.getMarketOddsList().size() <= 0) {
                            buildVo = vo;
                        }else {
                            buildVo = buildMarketVo(msgMarket, matchId, sportId);
                        }

                        //将当前盘口的旧位置设置进去，后面做位置改变对比
                        buildVo.setMarketIndex(vo.getPlaceNum());
                        //累封和防封
                        log.info("::{}::{}::累封防封1:{},玩法：{}",linkId,matchId,msgMarket.getPlaceNumStatusDisplay(),msgMarket.getMarketCategoryId());
                        buildVo.setPlaceNumStatusDisplay(null == msgMarket.getPlaceNumStatusDisplay() ? 0:msgMarket.getPlaceNumStatusDisplay());
                        currentMarketList.add(buildVo);
                        category.setMarketSource(buildVo.getMarketSource());
                    }
                    log.info("::{}::category5:{}",linkId,category);
                    for (String marketId : msgMarketMap.keySet()) {//新增
                        MatchMarketVo buildVo = buildMarketVo(msgMarketMap.get(marketId), matchId, sportId);
                        buildVo.setUpdateTime(datasourceTime);
                        currentMarketList.add(buildVo);
                        category.setMarketSource(buildVo.getMarketSource());
                        Integer tradeType = msgMarketMap.get(marketId).getTradeType();
                        if (tradeType != null) {
                        	category.setTradeType(tradeType);
                        }
                    }
                    log.info("::{}::category6:{}",linkId,category);
//                    category.setMatchMarketVoList(currentMarketList);

//                    不做数据删除
//                    if (category.getMatchMarketVoList() == null || category.getMatchMarketVoList().size() <= 0) {
//                        mongotemplate.remove(query, MarketCategory.class);
//                        log.info("当前玩法没有合法数据，做移除操作{}",JSONObject.toJSONString(category));
//                        continue;
//                    }

                    log.info("::{}::category7:{}",linkId,category);
                    ListUtils.sort(currentMarketList, true, "placeNum");
                    log.info("::{}::发送位置改变信息：{}",linkId,JSONObject.toJSONString(currentMarketList));
                    isChangeMarketIndex(currentMarketList, linkId, sportId, String.valueOf(matchId));
                    log.info("::{}::category9:{}",linkId,category);
                    category.setMatchMarketVoList(currentMarketList);
                    marketCategories.add(category.getId());
                    if (isSave) {
                        log.info("::{}::category10:{}",linkId,category);
                        mongotemplate.save(category);
                    } else {
                        log.info("::{}::category11:{}",linkId,category);
                        matchServiceImpl.updateMongodbOdds(category, true,linkId);
                        //判断是否触发变化判断
                        /*  //bug-44150
                        if(msgCheck(msg) && bakMarketMap.keySet().size()>0){
                            monitorOddsChange(category,bakMarketMap,linkId,matchId);
                        }*/
                    }
                    log.info("::{}::category12:{}",linkId,category);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    redissonManager.unlock(lock);
                }
            }

        } catch (Exception e) {
            log.error("赛事盘口赔率改变更新mongodb错误2" + e.getMessage(), e);
        } finally {
            if (matchId != null && sportId != null && !CollectionUtils.isEmpty(marketCategories)) {
                matchServiceImpl.sendMqUpdateColl(new MatchCategoryUpdateBean(Long.parseLong(sportId), matchId), marketCategories);
            }
            if(null != matchId){
                log.info("::{}::更新赛事赔率完成推送30005,赛事:{}",linkId,matchId);
                producerSendMessageUtils.sendMessage("STANDARD_MARKET_ODDS_PUSH_WS",linkId,matchId.toString(),message);
            }
        }
        sw.stop();
        log.info("::{}::更新赛事赔率完成,耗时:{}"+sw.prettyPrint(),linkId,sw.getTotalTimeMillis());
    }

    private void putMonitorInfo(Map<String, StandardMarketMessage> bakMarketMap, String marketId, StandardMarketMessage msgMarket,String linkId) {
        // bug 44150  只针对 全场让分 4、全场大小 2、半场让分19、半场大小 18 处理
       /* if(!"LS".equals(msgMarket.getDataSourceCode())){
            return ;
        }*/
        log.info("::{}::putMonitorInfo:id={}", linkId,msgMarket.getMarketCategoryId());
        if(msgMarket.getMarketCategoryId()==2||msgMarket.getMarketCategoryId()==4||msgMarket.getMarketCategoryId()==19||msgMarket.getMarketCategoryId()==18){
            bakMarketMap.put(marketId,msgMarket);
        }
    }

    /**
     * 把赔率数据发给MQ,由专有的监控类去处理
     * @param category
     * @param bakMarketMap
     * @param linkId
     * @param matchId
     */
    private void monitorOddsChange(MarketCategory category, Map<String, StandardMarketMessage> bakMarketMap,String linkId, Long matchId) {
        log.info("::{}::monitorOddsChange::beginSend",linkId);
        JSONObject jb = new JSONObject();
        jb.put("linkId",linkId);
        jb.put("category",JSONObject.toJSONString(category));
        jb.put("lastStandardMarketMap",JSONObject.toJSONString(bakMarketMap));
        producerSendMessageUtils.sendMessage("MONITOR_ODDS_SPECIAL_CHANGE",linkId,matchId.toString(),jb);
        log.info("::{}::monitorOddsChange::finish",linkId);
    }
    private boolean msgCheck(DataRealTimeMessageBean<StandardMatchMarketMessage> msg ) {
        StandardMatchMarketMessage ls = new StandardMatchMarketMessage();
        //2.监听事件   L01,足球，早盘
        log.info("::{}::msgCheck:matchType={},sportId={}",msg.getData().getStandardMatchInfoId(), msg.getData().getMatchType(),msg.getData().getSportId());
        //if ( 0 != msg.getData().getMatchType() && SportIdEnum.isFootball(msg.getData().getSportId())) {  早盘赔率测试数据太难做了，先把功能走通
        if ( SportIdEnum.isFootball(msg.getData().getSportId())) {
            return true;
        }
        return false;
    }
    /**
     * @Description   //球头变动推送消息
     * @Param [collect, tag, sportId, matchId]
     * @Author  sean
     * @Date   2021/11/27
     * @return void
     **/
    private void isChangeMarketIndex(List<MatchMarketVo> collect, String tag, String sportId, String matchId) {
        if (collect == null || collect.size() <= 0) {
            return;
        }
        if (TRADE_SPORT.contains(sportId)) {
            List<MatchMarketVo> changeList = new ArrayList<MatchMarketVo>();
            collect.forEach(vo -> {
                if (vo.getMarketIndex() != null && vo.getMarketIndex() == vo.getPlaceNum()) return;

                vo.setMarketIndex(vo.getPlaceNum());
                changeList.add(vo);
            });

            if (changeList.size() > 0) {
                //发送盘口变更消息
                Map<String, String> properties = new HashMap<String, String>();
                properties.put("matchId", matchId);
                producerSendMessageUtils.sendMessage("TRADE_MARKET_INDEX_CHANGE", tag, tag, changeList, properties);
            }
        }else{
            log.info("::{}::不是可操盘球种",tag);
        }
    }

    /**
     * 构建盘口数据的加密key
     *
     * @param @return 设定文件
     * @return String    返回类型
     * @throws
     * @Title: buildMarketBeanSign
     * @Description: TODO
     */
    private String buildMarketBeanSign(StandardMarketMessage bean) {
        StringBuilder marketSb = new StringBuilder().append(bean.getPlaceNum()).append(":").append(bean.getId()).append(bean.getAddition1()).append(bean.getAddition2())
                .append(bean.getStatus()).append(bean.getMarketType()).append(bean.getMarketCategoryId()).append(bean.getThirdMarketSourceStatus()).append(bean.getOldThirdMarketSourceStatus())
                .append(bean.getPlaceNum()).append(bean.getPaStatus()).append(bean.getPaStatusReason()).append(bean.getPlaceNumStatus()).append(bean.getDataSourceCode());

        //构建赔率的数据
        if (bean.getMarketOddsList() != null && bean.getMarketOddsList().size() > 0) {
            bean.getMarketOddsList().forEach(oddBean -> marketSb.append("-").append(oddBean.getId()).append(oddBean.getActive()).append(oddBean.getStatus()).append(oddBean.getPaOddsValue()));
        }

        return marketSb.toString();
    }

    /**
     * 以玩法分组数据
     *
     * @param list
     * @param datasourceTime
     * @param matchId
     * @return
     */
    private Map<String, Map<String, StandardMarketMessage>> getPlayGroup(List<StandardMarketMessage> list, Long datasourceTime, Long matchId,String linkedId) {
        log.info("::{}::MarketList:{}",linkedId,list);
        Map<String, Map<String, StandardMarketMessage>> result = new HashMap<String, Map<String, StandardMarketMessage>>();
        if (list == null || list.size() <= 0) return result;

        Map<String, List<StandardMarketMessage>> allDataMap = list.stream().collect(Collectors.groupingBy(vo -> String.valueOf(vo.getMarketCategoryId())));

        for (String playId : allDataMap.keySet()) {
            allDataMap.get(playId).forEach(vo -> {
                if (vo.getThirdMarketSourceStatus() == null) {
                    vo.setThirdMarketSourceStatus(TradeStatusEnum.OPEN.getStatus());
                }
                if (vo.getPaStatus() == null) {
                    vo.setPaStatus(TradeStatusEnum.OPEN.getStatus());
                }
                if (vo.getPlaceNumStatus() == null) {
                    vo.setPlaceNumStatus(TradeStatusEnum.OPEN.getStatus());
                }
                if (vo.getStatus() == null) {
                    vo.setStatus(TradeStatusEnum.OPEN.getStatus());
                }
            });
            List<String> signList = allDataMap.get(playId).stream()
                    .map(vo -> buildMarketBeanSign(vo)).collect(Collectors.toList());
            signList.sort((a, b) -> a.split(":")[0].compareTo(b.split(":")[0]));
            String marketBeanSign = JSONObject.toJSONString(signList);

            String cacheKey = String.format("rcs:task:odds:cache:sign:%s:%s", matchId, playId);
            //以XX-XX格式存储，前一个是时间搓，后一个是md5值
            String cacheVal = redisClient.get(cacheKey);
            if (StringUtils.isBlank(cacheVal)) cacheVal = "0-0";
            log.info("::{}::cacheVal:{}",linkedId,cacheVal);
            String[] redisMd5Arr = cacheVal.split("-");
            if (Long.parseLong(redisMd5Arr[0]) > datasourceTime) {
                log.warn("::{}::缓存时间大于datasourceTime-{}当前玩法:{}不处理;marketBeanSign:{}" ,linkedId,datasourceTime,playId,marketBeanSign);
                continue;
            }

            String marketMd5Sign = DigestUtils.md5DigestAsHex(marketBeanSign.getBytes());
//            log.info("::{}::marketMd5Sign:{}",linkedId,marketMd5Sign);
//            if (marketMd5Sign.equals(redisMd5Arr[1])) {
//                log.warn("::{}::缓存加密数据一致，不做处理;marketBeanSign:{}redisMd5Arr:{}当前玩法:{}",linkedId,marketMd5Sign,redisMd5Arr[1],playId);
//                continue;
//            }
            String newMarketSign = datasourceTime + "-" + marketMd5Sign;
            redisClient.setExpiry(cacheKey, newMarketSign, 300L);
            Map<String, StandardMarketMessage> marketMap = new HashMap<>();
            allDataMap.get(playId).forEach(vo -> {
                //只有开盘和封盘才会存放
                if (TradeStatusEnum.isOpen(vo.getThirdMarketSourceStatus()) ||
                        TradeStatusEnum.isSeal(vo.getThirdMarketSourceStatus())) {
                    marketMap.put(String.valueOf(vo.getId()), vo);
                }
            });
            log.info("::{}::playId:{}",linkedId,playId);
            result.put(playId, marketMap);
        }

        return result;
    }

    private void putCatgoryInfo(Long matchId, MarketCategory category, Map<String, StandardMatchInfo> matchInfoMap) {
        if (category.getMatchStartTime() == null) {//第一次没有赛事数据，后面做更新
            if (!matchInfoMap.containsKey(String.valueOf(matchId))) {
                StandardMatchInfo standardMatchInfo = standardMatchInfoService.selectOne(matchId);
                matchInfoMap.put(String.valueOf(matchId), standardMatchInfo);
            }
            StandardMatchInfo standardMatchInfo = matchInfoMap.get(String.valueOf(matchId));
            if (standardMatchInfo != null) {
                category.setMatchStartTime(DateUtils.transferLongToDateStrings(standardMatchInfo.getBeginTime()));
                category.setSportId(standardMatchInfo.getSportId());
            }

        }
        if (CollectionUtils.isEmpty(category.getNames())) {
            //读库玩法表
            StandardSportMarketCategory marketCategory = marketCategoryService.queryCachedCategory(String.valueOf(category.getSportId()), category.getId());

            //玩法名称
            Long categoryNameCode = null;
            if (null != marketCategory) {
                category.setType(marketCategory.getType());
                categoryNameCode = marketCategory.getNameCode();
            }
            if (null != categoryNameCode) {
                Map<String, String> marketI18Info = rcsLanguageService.getCachedNamesMapByCode(categoryNameCode);
                category.setNames(marketI18Info);
            }
        }
    }

    private String getCategoryNameCode(String sportId, String playId) {
        String categoryNameCode = redisClient.get(String.format(REDIS_CACHE_CATEGORY_KEY, sportId, playId));
        if (StringUtils.isBlank(categoryNameCode)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("marketCategoryId", playId);
            map.put("sportId", sportId);
            Map<String, Object> info = standardSportMarketService.queryMatchMarketInfo(map);
            if (info != null && info.containsKey("market_name_code")) {
                categoryNameCode = String.valueOf(info.get("market_name_code"));
                redisClient.setExpiry(String.format(REDIS_CACHE_CATEGORY_KEY, sportId, playId), categoryNameCode, 60 * 5l);
            }
        }
        return categoryNameCode;
    }

    private MarketCategory buildMarketCategory(StandardMarketMessage marketMsg, Long matchId, String sportId) {
        MarketCategory bean = new MarketCategory();
        bean.setId(marketMsg.getMarketCategoryId());
        //读库玩法表
        StandardSportMarketCategory marketCategory = marketCategoryService.queryCachedCategory(sportId, marketMsg.getMarketCategoryId());
        bean.setType(marketCategory.getType());
        //玩法名称
        Long categoryNameCode = null;
        if (null != marketCategory)
            categoryNameCode = marketCategory.getNameCode();
        if (null != categoryNameCode) {
            Map<String, String> marketI18Info = rcsLanguageService.getCachedNamesMapByCode(categoryNameCode);
            bean.setNames(marketI18Info);
        }
        //盘口
        bean.setMatchMarketVoList(new ArrayList<MatchMarketVo>());
        bean.setMatchId(String.valueOf(matchId));
        //更新玩法时间
        bean.setUpdateTime(DateUtils.parseDate(new Date().getTime(), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));

        //设定比赛开始时间和赛种
        Query matchQuery = new Query();
        matchQuery.fields().include("matchId");
        matchQuery.fields().include("sportId");
        matchQuery.fields().include("matchStartTime");
        matchQuery.addCriteria(Criteria.where("matchId").is(matchId));
        MatchMarketLiveBean match = mongotemplate.findOne(matchQuery, MatchMarketLiveBean.class);
        if (null != match) {
            bean.setMatchStartTime(match.getMatchStartTime());
            bean.setSportId(match.getSportId());
        } else {
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.selectOne(matchId);
            if (standardMatchInfo != null) {
                bean.setMatchStartTime(DateUtils.transferLongToDateStrings(standardMatchInfo.getBeginTime()));
                bean.setSportId(standardMatchInfo.getSportId());
            }
        }
        bean.setMarketSource(marketMsg.getMarketSource());
        //构造盘口数据   最新版本不在这里构建盘口vo数据
//        MatchMarketVo vo = buildMarketVo(marketMsg, new HashMap<String, MatchMarketOddsVo>(), matchId, sportId);
//        bean.getMatchMarketVoList().add(vo);
        //设置玩法模板ID
        matchServiceImpl.setTemplate(bean);
//        bean.setMainPlayStatus(getPlaceholderMainPlayStatusFromRedis(matchId, marketMsg.getMarketCategoryId()));
        return bean;
    }

    public MatchMarketVo buildMarketVo(StandardMarketMessage marketMsg, Long matchId, String sportId) {
        StandardSportMarketCategory marketCategory = marketCategoryService.queryCachedCategory(sportId, marketMsg.getMarketCategoryId());
        MatchMarketVo vo = new MatchMarketVo();
        if (null != marketCategory&&null != marketCategory.getNameCode()) {
            Map<String, String> marketI18Info = rcsLanguageService.getCachedNamesMapByCode(marketCategory.getNameCode());
            vo.setNames(marketI18Info);
        }
        vo.setId(marketMsg.getId());
        vo.setMarketId(vo.getId());
        vo.setMarketCategoryId(marketMsg.getMarketCategoryId());
        vo.setStatus(marketMsg.getStatus());
        vo.setOddsFieldsList(new ArrayList<MatchMarketOddsVo>());
        vo.setMarketType(marketMsg.getMarketType());
        vo.setAddition1(marketMsg.getAddition1());
        vo.setAddition2(marketMsg.getAddition2());
        vo.setExtraInfo(marketMsg.getExtraInfo());
        vo.setAddition3(marketMsg.getAddition3());
        vo.setAddition4(marketMsg.getAddition4());
        vo.setAddition5(marketMsg.getAddition5());
        vo.setOddsName(marketMsg.getOddsName());
        BigDecimal margin = null;
        if (null != marketMsg.getMarketOddsList() && marketMsg.getMarketOddsList().size() > 0) {
            List<MatchMarketOddsVo> oddsFieldsList = new ArrayList<>();
            for (StandardMarketOddsMessage marketOdds : marketMsg.getMarketOddsList()) {
                MatchMarketOddsVo odds = new MatchMarketOddsVo();
                if (null != marketOdds.getOddsFieldsTemplateId()) {
                    String nameCode = standardSportMarketService.queryOddTemplateInfo(marketOdds.getOddsFieldsTemplateId());
                    if (StringUtils.isNotBlank(nameCode) && !CommonUtil.isBlankOrNull(nameCode)) {
                        odds.setNames(rcsLanguageService.getCachedNamesMapByCode(Long.parseLong(nameCode)));
                    }
                    odds.setNameCode(marketOdds.getOddsFieldsTemplateId());
                }
                odds.setName(marketOdds.getName());
                odds.setId(marketOdds.getId());
                odds.setNameExpressionValue(NameExpressionValueUtils.getNameExpressionValue(vo.getMarketCategoryId().intValue(), marketOdds.getOddsType(), marketMsg.getAddition1()));
                odds.setTargetSide(marketOdds.getTargetSide());
                odds.setFieldOddsValue(String.valueOf(marketOdds.getPaOddsValue()));
                odds.setFieldOddsOriginValue(marketOdds.getPaOddsValue());
                if (marketOdds.getPaOddsValue() != null) {
                    String nextLevelOdds = rcsOddsConvertMappingService.getNextLevelOdds(new BigDecimal(marketOdds.getPaOddsValue()).divide(new BigDecimal("100000"), 2, RoundingMode.DOWN).toPlainString());
                    odds.setNextLevelOddsValue(nextLevelOdds);
                }
                odds.setOddsType(marketOdds.getOddsType());
                odds.setActive(marketOdds.getActive());
                odds.setFieldOddsOriginValue(marketOdds.getOriginalOddsValue());
                odds.setMarketDiffValue(marketOdds.getMarketDiffValue() == null ? 0 : marketOdds.getMarketDiffValue());
                odds.setOrderOdds(marketOdds.getOrderOdds());
                odds.setMargin(marketOdds.getMargin());
                odds.setAddition1(marketOdds.getAddition1());
                odds.setAddition2(marketOdds.getAddition2());
                odds.setAddition3(marketOdds.getAddition3());
                odds.setAddition4(marketOdds.getAddition4());
                odds.setAddition5(marketOdds.getAddition5());
                odds.setProbability(marketOdds.getProbability());
                odds.setProbabilityOdds(marketOdds.getProbabilityOdds());
                odds.setMarginProbabilityOdds(marketOdds.getMarginProbabilityOdds());
                odds.setAnchor(marketOdds.getAnchor());
                odds.setStatus(marketOdds.getStatus());
                oddsFieldsList.add(odds);
            }
            if(!CollectionUtils.isEmpty(oddsFieldsList)){
                ListUtils.sort(oddsFieldsList, true, "orderOdds");
                vo.setOddsFieldsList(oddsFieldsList);
            }
        }
        if (NumberUtils.isNumber(vo.getAddition1())) {
            vo.setSortHandle(Math.abs(new BigDecimal(vo.getAddition1()).doubleValue()));
        } else {
            vo.setSortHandle(0d);
        }
        vo.setMarketId(marketMsg.getId());
        vo.setThirdMarketSourceStatus(marketMsg.getThirdMarketSourceStatus());
        vo.setOldThirdMarketSourceStatus(marketMsg.getOldThirdMarketSourceStatus());
        vo.setPaStatus(marketMsg.getPaStatus());
        vo.setStatus(marketMsg.getStatus());
        vo.setPlaceNumStatus(marketMsg.getPlaceNumStatus());
        //状态值不相等重新赋值
        if(marketMsg.getStatus() != null && marketMsg.getPlaceNumStatus() != null
                && marketMsg.getStatus().compareTo(marketMsg.getPlaceNumStatus()) != 0 && marketMsg.getPlaceNumStatus() == 0){
            vo.setPlaceNumStatus(marketMsg.getStatus());
        }
        String paStatusReason = marketMsg.getPaStatusReason();
        if (StringUtils.isNotEmpty(paStatusReason)) {
        	paStatusReason = paStatusReason.replace("\\\"", "\"");
        }
        vo.setPaStatusReason(paStatusReason);
        vo.setPlaceNumId(marketMsg.getPlaceNumId());
        vo.setMarketHeadGap(marketMsg.getMarketHeadGap());
        vo.setPlaceNum(marketMsg.getPlaceNum());
        vo.setDataSourceCode(marketMsg.getDataSourceCode());
        //2384 L01站点需求 internalDataSourceCode
        if("LS".equals(marketMsg.getDataSourceCode())){
            vo.setInternalDataSourceCode(marketMsg.getInternalDataSourceCode());
            log.info("::{}::数据站点信息:{}",matchId,marketMsg.getInternalDataSourceCode());
        }
        vo.setChildMarketCategoryId(marketMsg.getChildMarketCategoryId() == null ? null : String.valueOf(marketMsg.getChildMarketCategoryId()));
        vo.setMarketSource(marketMsg.getMarketSource());
        log.info("::{}::累封防封2:{},玩法：{}",matchId,vo.getPlaceNumStatusDisplay(),vo.getMarketCategoryId());
        vo.setPlaceNumStatusDisplay(null == vo.getPlaceNumStatusDisplay() ? 0:vo.getPlaceNumStatusDisplay());
        return vo;
    }

    /**
     * true 表示没有变化
     * false 有变化需要更新
     *
     * @param bean
     * @param datasourceTime
     * @return
     */
    public Boolean compareMarketBean(StandardMarketMessage bean, Long datasourceTime) {
        StringBuilder marketSb = new StringBuilder("").append(bean.getId()).append(bean.getAddition1()).append(bean.getAddition2())
                .append(bean.getStatus()).append(bean.getMarketType()).append(bean.getMarketCategoryId()).append(bean.getThirdMarketSourceStatus())
                .append(bean.getOddsMetric() == null ? Integer.MAX_VALUE : bean.getPlaceNum());
        String marketMd5 = DigestUtils.md5DigestAsHex(marketSb.toString().getBytes());

        String cacheKey = String.format(ODDS_MD5_CACHE_KEY, bean.getId());

        String md5 = redisClient.get(cacheKey);
        if (md5 == null) md5 = "0-0";

        String[] redisMd5Arr = md5.split("-");

        if (Long.parseLong(redisMd5Arr[0]) > datasourceTime) {
            return true;
        }

        if (bean.getMarketOddsList() == null || bean.getMarketOddsList().size() <= 0) {//只对比盘口MD5值
            if (marketMd5.equals(redisMd5Arr[1])) return true;

            String val = datasourceTime + "-" + marketMd5 + (redisMd5Arr.length == 2 ? "" : redisMd5Arr[2]);
            redisClient.setExpiry(cacheKey, val, 300l);
            return false;
        }

        StringBuilder oddsSb = new StringBuilder("");
        bean.getMarketOddsList().forEach(oddBean -> {
            oddsSb.append(oddBean.getId()).append(oddBean.getActive()).append(oddBean.getPaOddsValue());
        });
        String allMd5 = marketMd5 + "-" + DigestUtils.md5DigestAsHex(oddsSb.toString().getBytes());
        if (allMd5.equals(redisMd5Arr[1] + "-" + (redisMd5Arr.length == 2 ? "" : redisMd5Arr[2]))) return true;

        allMd5 = datasourceTime + "-" + allMd5;
        redisClient.setExpiry(cacheKey, allMd5, 300l);
        return false;
    }

    private Boolean isUpdateByTime(Long currentDataTime, MatchMarketVo category) {
        try {
            if (category.getUpdateTime() == null || category.getUpdateTime() < currentDataTime) {
                category.setUpdateTime(currentDataTime);
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("当前消息已经失效，不在使用当前数据：currentDataTime:{}，category：{}", currentDataTime, JSONObject.toJSONString(category));
        return false;
    }

    private Integer getPlaceholderMainPlayStatusFromRedis(Long matchId, Long playId) {
        String key = String.format(PLACEHOLDER_MAIN_PLAY_STATUS_KEY, matchId);
        String value = redisClient.hGet(key, String.valueOf(playId));
        log.info("获取占位符主玩法状态：matchId={},playId={},key={},value={}", matchId, playId, key, value);
        return org.apache.commons.lang3.math.NumberUtils.toInt(value, TradeStatusEnum.OPEN.getStatus());
    }
}
