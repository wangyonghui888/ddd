package com.panda.sport.rcs.task.job.match;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.RedisCacheSyncBean;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.mongo.MatchTeamVo;
import com.panda.sport.rcs.mongo.RenewInfo;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.MatchPeriod;
import com.panda.sport.rcs.task.mq.impl.match.MatchPeriodChangeConsumer;
import com.panda.sport.rcs.task.service.MatchServiceImpl;
import com.panda.sport.rcs.task.wrapper.MatchEventInfoService;
import com.panda.sport.rcs.task.wrapper.MatchStatisticsInfoService;
import com.panda.sport.rcs.task.wrapper.MongoService;
import com.panda.sport.rcs.task.wrapper.RcsLanguageInternationService;
import com.panda.sport.rcs.task.wrapper.StandardMatchInfoService;
import com.panda.sport.rcs.task.wrapper.StandardSportTeamService;
import com.panda.sport.rcs.utils.WordToPinYinUtil;
import com.panda.sport.rcs.vo.I18nItemVo;
import com.panda.sport.rcs.vo.MarketLiveOddsQueryVo;
import com.panda.sport.rcs.vo.StandardMatchInfoVo;
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
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.panda.sport.rcs.constants.RedisKey.*;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.task.job
 * @Description :  TODO
 * @Date: 2019-10-26 15:40
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@JobHandler(value = "matchMarketLiveHandler")
@Component
@Slf4j
public class MatchMarketLiveJobHandler extends IJobHandler {

    @Autowired
    private StandardMatchInfoService matchInfoService;

    @Autowired
    private RcsLanguageInternationService languageService;

    @Autowired
    private MatchPeriodChangeConsumer matchPeriodChangeConsumer;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    MatchStatisticsInfoService matchStatisticsInfoService;

    @Autowired
    StandardSportTeamService standardSportTeamService;

    @Autowired
    MongoTemplate mongotemplate;

    @Autowired
    MongoService mongoService;

    @Autowired
    MatchServiceImpl matchService;

    @Autowired
    MatchEventInfoService matchEventInfoService;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    private static final String collectionName = "match_market_live";

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Map map = new HashMap<>();
        map.put("tableName", collectionName);
        Long currentTime = System.currentTimeMillis();
        try {
            XxlJobLogger.log("MatchMarketLiveJobHandler---------------->>执行开始");
            if (StringUtils.isNotBlank(s)) {
                mongoService.upsert(map, "renew_info", initRenewInfo(collectionName, Long.parseLong(s)));
                XxlJobLogger.log("MatchMarketLiveJobHandler---------------->>重置更新时间：" + s);
            }
            MarketLiveOddsQueryVo marketLiveOddsQueryVo = new MarketLiveOddsQueryVo();
            //查找更新数据
            RenewInfo one = mongotemplate.findOne(new Query().addCriteria(Criteria.where("tableName").is(collectionName)), RenewInfo.class);
            if (one != null) marketLiveOddsQueryVo.setUpdateTimeMillis(one.getUpdateTime() - 30 * 1000);
            else marketLiveOddsQueryVo.setUpdateTimeMillis(0l);

            marketLiveOddsQueryVo.setCurrentTimeMillis(currentTime);
            XxlJobLogger.log("MatchMarketLiveJobHandler---------------->>执行参数：{}", JSONObject.toJSONString(marketLiveOddsQueryVo));
            List<StandardMatchInfoVo> sportMatchInfos = matchInfoService.queryMatchesNoLimitByV2(marketLiveOddsQueryVo);

            if (sportMatchInfos.size() > 0) {
                for (StandardMatchInfoVo model : sportMatchInfos) {
                    try {
                        // 非滚球已经开赛的不显示
                        MatchMarketLiveBean marketLiveBean = new MatchMarketLiveBean();
                        marketLiveBean.setMatchId(model.getId());
                        marketLiveBean.setPreMatchBusiness(model.getPreMatchBusiness());
                        Integer matchStatus = model.getMatchStatus();
                        marketLiveBean.setMatchStatus(matchStatus);

                        if (Arrays.asList(1,2,10).contains(matchStatus)) {
                            marketLiveBean.setRiskManagerCode(model.getLiveRiskManagerCode());
                            marketLiveBean.setSort(0);
                        } else if(matchStatus==6){
                            marketLiveBean.setSort(1);
                        }else {
                            marketLiveBean.setRiskManagerCode(model.getPreRiskManagerCode());
                            marketLiveBean.setSort(0);
                        }

                        marketLiveBean.setEventTime(model.getEventTime());
                        marketLiveBean.setEventCode(model.getEventCode());
                        marketLiveBean.setMatchManageId(model.getMatchManageId());
                        marketLiveBean.setStandardTournamentId(model.getStandardTournamentId());
                        marketLiveBean.setMatchStartTime(DateUtils.transferLongToDateStrings(model.getBeginTime()));
                        marketLiveBean.setMatchStartDate(DateUtils.getDateExpect(model.getBeginTime()));
                        marketLiveBean.setPeriod(model.getMatchPeriodId().intValue());
                        marketLiveBean.setSecondsMatchStart(model.getSecondsMatchStart());
                        marketLiveBean.setPreMatchTime(model.getPreMatchTime());
                        marketLiveBean.setLiveOddBusiness(model.getLiveOddBusiness());
                        marketLiveBean.setOddsLive(model.getOddsLive());
                        marketLiveBean.setMatchType(model.getMatchType());
                        marketLiveBean.setEndTime(model.getEndTime());
                        marketLiveBean.setThirdMatchListStr(model.getThirdMatchListStr());
                        // 设置中立场
                        marketLiveBean.setNeutralGround(model.getNeutralGround());
                        marketLiveBean.setTournamentLevel(model.getTournamentLevel());
                        marketLiveBean.setTournamentNameCode(model.getNameCode());
                        marketLiveBean.setSportId(model.getSportId());
                        marketLiveBean.setMatchSnapshot(0);
                        // 国际化
                        marketLiveBean.setTournamentNames(languageService.getCachedNamesByCode(model.getNameCode()));

                        //设置开售信息
                        marketLiveBean.setPreTraderName(model.getPreTrader());
                        marketLiveBean.setPreTraderId(model.getPreTraderId());
                        marketLiveBean.setLiveTraderName(model.getLiveTrader());
                        marketLiveBean.setLiveTraderId(model.getLiveTraderId());
                        marketLiveBean.setMarketCount(model.getMarketCount());
                        marketLiveBean.setLiveMarketCount(model.getLiveMarketCount());
                        marketLiveBean.setRoundType(model.getRoundType());
                        List<MatchTeamVo> teamList = standardSportTeamService.queryTeamList(model.getId());

                        if (!CollectionUtils.isEmpty(teamList)) {
                            if (marketLiveBean.getTeamList() == null || marketLiveBean.getTeamList().size() <= 0) {
                                marketLiveBean.setTeamList(teamList);
                            } else {
                                for (int i = 0; i < teamList.size(); i++) {
                                    if (marketLiveBean.getTeamList().size() > i) {
                                        marketLiveBean.getTeamList().get(i).setNames(teamList.get(i).getNames());
                                    }
                                }
                            }
                        }

                        if (StringUtils.isBlank(marketLiveBean.getNameConcat())) {
                            String nameConcat = "";
                            if (marketLiveBean.getTournamentNames() != null) {
                                nameConcat = nameConcat + getNamePingYing(marketLiveBean.getTournamentNames());
                            }
                            if (marketLiveBean.getTeamList() != null) {
                                for (MatchTeamVo teamVo : marketLiveBean.getTeamList()) {
                                    if (!CollectionUtils.isEmpty(teamVo.getNames())) {
                                        String text = teamVo.getNames().containsKey("zs") && !StringUtils.isBlank(String.valueOf(teamVo.getNames().get("zs")))
                                                ? String.valueOf(teamVo.getNames().get("zs")) : String.valueOf(teamVo.getNames().get("en"));
                                        nameConcat = nameConcat + WordToPinYinUtil.getFirshChar(text);
                                    }
                                }
                            }
                            marketLiveBean.setNameConcat(nameConcat);
                        }

                        String oddsLiveTradeSealKey = String.format("rcs:match:oddsLive:trade:seal:%s",marketLiveBean.getMatchId());
                        String oddsLiveTradeSeal = redisClient.get(oddsLiveTradeSealKey);
                        if(StringUtils.isNotBlank(oddsLiveTradeSeal)){
                            redisClient.delete(oddsLiveTradeSealKey);
                            log.info("::{}::MatchMarketLiveJobHandler更新赛事数据:获取切滚球盘封盘状态", marketLiveBean.getMatchId());
                            marketLiveBean.setOperateMatchStatus(Integer.valueOf(oddsLiveTradeSeal));
                        }
                        matchService.upsertMatch(marketLiveBean);
                        /*Map map1 = new HashMap<>();
                        map1.put("matchId", marketLiveBean.getMatchId());
                        mongoService.upsert(map1, collectionName, marketLiveBean);*/
                        String key=RCS_TASK_MATCH_INFO_CACHE + marketLiveBean.getMatchId();
                        redisClient.setExpiry(RCS_TASK_MATCH_LIVE + marketLiveBean.getMatchId(), marketLiveBean.getMatchStatus(), EXPRIY_TIME_2_HOURS);
                        redisClient.setExpiry(key, JsonFormatUtils.toJson(marketLiveBean), EXPRIY_TIME_2_HOURS);
                        //发送数据到order的redis
                        RedisCacheSyncBean syncBean = RedisCacheSyncBean.build(key,key,JsonFormatUtils.toJson(marketLiveBean),60*60L);
                        producerSendMessageUtils.sendMessage("RCS_RISK_REDIS_CACHE_SYNC", null, key, syncBean);
                        //发送数据到order的本地缓存
                        JSONArray data = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("key", key);
                        jsonObject.put("value", JsonFormatUtils.toJson(marketLiveBean));
                        jsonObject.put("type", key);
                        data.add(jsonObject);
                        producerSendMessageUtils.sendMessage("rcs_local_cache_clear_sdk", null, key, data);
                        if (Arrays.asList(1, 2, 10).contains(marketLiveBean.getMatchStatus()))
                            matchService.updateCategories(marketLiveBean.getMatchId());
                        //不是赛前，需要特殊处理
                        if (!"0".equals(String.valueOf(matchStatus))) {
                            handlePeriodCacheList(model.getId());
                        }
                        XxlJobLogger.log("MatchMarketLiveJobHandler更新赛事数据 = {}", JSONObject.toJSONString(marketLiveBean));
                        log.info("::{}::MatchMarketLiveJobHandler更新赛事数据 = {}", marketLiveBean.getMatchId(),JSONObject.toJSONString(marketLiveBean));

                    } catch (Exception e) {
                        log.error(model.getId() + "存入失败" + e.getMessage(), e);
                    }
                }
                mongoService.upsert(map, "renew_info", initRenewInfo(collectionName, currentTime));
            }
            XxlJobLogger.log("MatchMarketLiveJobHandler---------------->>执行结束");
        } catch (Exception e) {
            log.error("存入失败" + e.getMessage(), e);
        }
        return SUCCESS;
    }

    /**
     * 处理缓存中未处理的比分事件，防止有部分赛事在比分之后才到下游
     *
     * @param @param id    赛事id
     * @return void    返回类型
     * @throws
     * @Title: handlePeriodCacheList
     * @Description: TODO
     */
    private void handlePeriodCacheList(Long matchId) {
        String redisKey = "rcs:task:score:" + matchId;
        String redisVal = redisClient.get(redisKey);

        if (StringUtils.isBlank(redisVal)) {
            return;
        }
        List<MatchPeriod> periodList = JSONObject.parseArray(redisVal, MatchPeriod.class);
        periodList.forEach(periodBean -> {
            log.info("有赛事未处理比分事件,现在处理：{}", JSONObject.toJSONString(periodBean));
            matchPeriodChangeConsumer.onMessage(periodBean);
            log.info("有赛事未处理比分事件,处理完成：{}", JSONObject.toJSONString(periodBean));
        });

        redisClient.delete(redisKey);
    }

    private String getNamePingYing(List<I18nItemVo> list) {
        Map<String, I18nItemVo> map = new HashMap<String, I18nItemVo>();
        list.forEach(i18nItemVo -> map.put(i18nItemVo.getLanguageType(), i18nItemVo));

        if (map.containsKey("zs")) {
            return WordToPinYinUtil.getFirshChar(map.get("zs").getText());
        }

        if (map.containsKey("zh")) {
            return WordToPinYinUtil.getFirshChar(map.get("zh").getText());
        }

        if (map.containsKey("en")) {
            return WordToPinYinUtil.getFirshChar(map.get("en").getText());
        }
        return "";
    }

    public static RenewInfo initRenewInfo(String collectionName, Long endTime) {
        RenewInfo renewInfo = new RenewInfo();
        renewInfo.setTableName(collectionName);
        renewInfo.setUpdateTime(endTime);
        return renewInfo;
    }


}
