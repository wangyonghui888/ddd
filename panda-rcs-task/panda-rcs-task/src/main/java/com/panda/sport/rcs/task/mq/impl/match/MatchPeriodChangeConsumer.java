package com.panda.sport.rcs.task.mq.impl.match;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.UuidUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.enums.*;
import com.panda.sport.rcs.mapper.RcsTradeConfigMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainRefMapper;
import com.panda.sport.rcs.mongo.*;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.MatchPeriod;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.RcsTradeConfig;
import com.panda.sport.rcs.pojo.dto.MatchTemplatePlayConfigDTO;
import com.panda.sport.rcs.task.config.RedissonManager;
import com.panda.sport.rcs.task.service.MatchServiceImpl;
import com.panda.sport.rcs.task.service.profit.ScoreMonitoringService;
import com.panda.sport.rcs.task.wrapper.CategoryService;
import com.panda.sport.rcs.task.wrapper.IRcsMatchMarketConfigService;
import com.panda.sport.rcs.task.wrapper.MatchPeriodService;
import com.panda.sport.rcs.task.wrapper.StandardSportMarketCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.constants.RedisKey.*;

/**
 * 赛事比赛阶段 match_period更新
 *
 * @author black
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "MATCH_PERIOD_CHANGE",
        consumerGroup = "rcs_task_MATCH_PERIOD_CHANGE",
        consumeThreadMax = 512,
        consumeTimeout = 10000L)
public class MatchPeriodChangeConsumer implements RocketMQListener<MatchPeriod> {


    @Autowired
    MongoTemplate mongotemplate;
    @Autowired
    MatchPeriodService matchPeriodService;
    @Autowired
    MatchServiceImpl matchService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    private ScoreMonitoringService scoreMonitoringService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private RedissonManager redissonManager;
    @Autowired
    ProducerSendMessageUtils mqMessage;
    @Autowired
    private RcsTradeConfigMapper rcsTradeConfigMapper;
    @Autowired
    private StandardSportMarketCategoryService sportMarketCategoryService;
    @Autowired
    private IRcsMatchMarketConfigService rcsMatchMarketConfigService;
    @Autowired
    private RcsTournamentTemplatePlayMargainRefMapper templatePlayMargainRefMapper;

    @Override
    public void onMessage(MatchPeriod matchPeriod) {
        StopWatch sw = new StopWatch("赛事阶段、比分、时间更新流程" + UuidUtils.generateUuid());
        Long matchId = matchPeriod.getStandardMatchId();
        String lock = String.format("MONGODB_MATCH_UPDATE_%s", matchId);
        try {
            log.info("::{}::MatchPeriodChangeConsumer接收参数:{}", matchId, JSONObject.toJSONString(matchPeriod));
            sw.start("比分差值改变盘口值并且向融合发送");
            Map<Long, String> mapScore = new HashMap<>();
            Integer period = matchPeriod.getPeriod();
            Long sportId = matchPeriod.getSportId();
            String score = matchPeriod.getScore();
            String setScore = matchPeriod.getSetScore();
            String periodScore = matchPeriod.getPeriodScore();
            String extraTimeScore = matchPeriod.getExtraTimeScore();
            String cornerScore = matchPeriod.getCornerScore();
            String yellowCardScore = matchPeriod.getYellowCardScore();
            String penaltyScore = matchPeriod.getPenaltyShootout();
            String eventCode = matchPeriod.getEventCode();
            Long eventTime = matchPeriod.getEventTime();
            Long secondsFromStart = matchPeriod.getSecondsFromStart();
            String periodRedCardScore = matchPeriod.getRedCardScore();
            String servesFirst = matchPeriod.getServesFirst();
            Integer isErrorEndEvent = matchPeriod.getIsErrorEndEvent();
            Map<String, Map<String, String>> scoreMap = matchPeriod.getScoreMap();
            fifteenMinutesPlayClose(sportId, matchId, secondsFromStart);
            if (SportIdEnum.isPingpong(sportId)){
                //乒乓球达到设置的比分关盘处理
                pingpongAchieveScorePlayClose(matchId,scoreMap,period);
            }
            if ("null:null".equals(score)) return;
            if (!sportId.equals(SportIdEnum.TENNIS.getId())) {
                scoreMonitoringService.scoreMonitoring(matchPeriod);
            }
            sw.stop();
            redissonManager.lock(lock);
            sw.start("阶段更新查询赛事");
            Query query = new Query();
            query.addCriteria(Criteria.where("matchId").is(matchId));
            // 当前在mongo里的赛事数据
            MatchMarketLiveBean one = mongotemplate.findOne(query, MatchMarketLiveBean.class);
            sw.stop();
            if (one != null) {

                String oldScore = one.getScore();
                String oldCornerScore = one.getCornerScore();
                Update update = new Update();
                if (null != period) {
                    sw.start("更新赛事玩法数量");
                    update.set("period", period);
                    //计算玩法数量
                    one.setPeriod(period);
                    matchService.updateTradeType(one);
                    update.set("categoryCount", one.getCategoryCount());
                    update.set("autoCount", one.getAutoCount());
                    update.set("manualCount", one.getManualCount());
                    update.set("autoAddCount", one.getAutoAddCount());
                    sw.stop();
                }

                sw.start("赛事信息更新字段");
                if (SportIdEnum.isTennis(sportId) || SportIdEnum.isPingpong(sportId)
                        || SportIdEnum.isVolleyball(sportId) || SportIdEnum.isSnooker(sportId)
                        || SportIdEnum.isBaseBall(sportId)||SportIdEnum.isBadminton(sportId)||SportIdEnum.isIceHockey(sportId)) {
                    List<Integer> periods = new ArrayList<>();
                    if (SportIdEnum.isTennis(sportId)||SportIdEnum.isBadminton(sportId)) {
                        periods = Arrays.asList(8, 9, 10, 11, 12);
                    } else if (SportIdEnum.isPingpong(sportId) || SportIdEnum.isVolleyball(sportId)) {
                        periods = Arrays.asList(8, 9, 10, 11, 12, 441, 442);
                    }
                    if (periods.contains(period) || SportIdEnum.isSnooker(sportId)||SportIdEnum.isIceHockey(sportId)) {
                        List<ScoreVo> newScoreVos = transferScoreVo(scoreMap, update);
                        if (!CollectionUtils.isEmpty(newScoreVos)) {
                            List<ScoreVo> scoreVos = one.getScoreVos();
                            if (CollectionUtils.isEmpty(scoreVos)) {
                                scoreVos = new ArrayList<>();
                                scoreVos.addAll(newScoreVos);
                            } else {
                                for (ScoreVo newVo : newScoreVos) {
                                    ScoreVo vo = scoreVos.stream().filter(fi -> fi.getPeriod() != null && fi.getPeriod().equals(newVo.getPeriod())).findFirst().orElse(null);
                                    if (vo == null) {
                                        scoreVos.add(newVo);
                                    } else {
                                        scoreVos.remove(vo);
                                        if (StringUtils.isNotBlank(newVo.getSetScore())) {
                                            vo.setSetScore(newVo.getSetScore());
                                        }
                                        if (StringUtils.isNotBlank(newVo.getQiangScore())) {
                                            vo.setQiangScore(newVo.getQiangScore());
                                        }
                                        if (StringUtils.isNotBlank(newVo.getCurrentScore())) {
                                            vo.setCurrentScore(newVo.getCurrentScore());
                                        }
                                        scoreVos.add(vo);
                                    }
                                }
                            }
                            update.set("scoreVos", scoreVos);
                        }
                        //棒球得分处理
                    } else if (SportIdEnum.isBaseBall(sportId)) {
                        List<BaseBallScoreVo> scoreVos = transferBaseBallScore(scoreMap, update, one.getBaseBallScoreVos());
                        if (!CollectionUtils.isEmpty(scoreVos)) {
                            update.set("baseBallScoreVos", scoreVos);
                        }
                    }

                    if (StringUtils.isNotBlank(servesFirst)) {
                        update.set("servesFirst", servesFirst);
                    }

                    Integer setNum = matchPeriod.getSetNum();
                    if (setNum != null) update.set("setNum", setNum);
                } else {
                    if (!StringUtils.isBlank(score)) {
                        mapScore.put(PeriodEnum.FULL_TIME_1.getCategorySetId(), score);
                        update.set("score", score);
                    }

                    if (!StringUtils.isBlank(cornerScore)) {
                        mapScore.put(PeriodEnum.CORNER_KICK_1.getCategorySetId(), cornerScore);
                        update.set("cornerScore", cornerScore);
                        footballRedisScore(matchId, FootballCategorySet.CORNER_KICK.getCategorySetId(), cornerScore);
                    }

                    if (!StringUtils.isBlank(yellowCardScore)) {
                        update.set("yellowCardScore", yellowCardScore);
                    }

                    if (!StringUtils.isBlank(extraTimeScore)) {
                        mapScore.put(PeriodEnum.EXTRA_TIME_1.getCategorySetId(), extraTimeScore);
                        update.set("extraTimeScore", extraTimeScore);
                        footballRedisScore(matchId, FootballCategorySet.EXTRA_TIME.getCategorySetId(), extraTimeScore);
                    }
                    if (!StringUtils.isBlank(eventCode)) {
                        update.set("eventCode", eventCode);
                    }
                    if (null != secondsFromStart) {
                        update.set("secondsMatchStart", secondsFromStart);
                    }
                    if (null != eventTime) {
                        update.set("eventTime", eventTime);
                    }
                    if (null != isErrorEndEvent) {
                        update.set("isErrorEndEvent", isErrorEndEvent);
                    }
                    //红牌比分
                    String redCardScore = null;
                    if (!StringUtils.isBlank(periodRedCardScore)) {
                        update.set("redCardScore", periodRedCardScore);
                        redCardScore = periodRedCardScore;
                        footballRedisScore(matchId, FootballCategorySet.PENALTY_CARD.getCategorySetId(), redCardScore);
                    }
                    if (!StringUtils.isBlank(matchPeriod.getYellowRedCardScore()) && !"0:0".equals(matchPeriod.getYellowRedCardScore())) {
                        update.set("yellowRedCardScore", matchPeriod.getYellowRedCardScore());
                        redCardScore = matchPeriod.getYellowRedCardScore();
                    }

                    if (StringUtils.isNotBlank(redCardScore)&&redCardScore.contains(":")) {
                        String[] redCardSplit = redCardScore.split(":");
                        Integer homeRedCard = Integer.parseInt(redCardSplit[0]);
                        Integer awayRedCard = Integer.parseInt(redCardSplit[1]);
                        List<MatchTeamVo> teamList = one.getTeamList();
                        if(CollectionUtils.isNotEmpty(teamList)){
                            teamList.get(0).setRedCardNum(homeRedCard);
                            teamList.get(1).setRedCardNum(awayRedCard);
                            update.set("teamList", teamList);
                        }
                    }


                    if (StringUtils.isNotBlank(score)) {
                        String current = StringUtils.isEmpty(oldScore) ? getName(score, null)
                                : getName(score, oldScore);
                        if (!StringUtils.isBlank(current)) update.set("recentScoreTeam", current);
                    }
                    if (StringUtils.isNotBlank(cornerScore)) {
                        String current = StringUtils.isEmpty(oldCornerScore) ? getName(cornerScore, null)
                                : getName(cornerScore, oldCornerScore);
                        if (!StringUtils.isBlank(current)) update.set("recentCornerScoreTeam", current);
                    }
                    period = period == null ? 0 : period;
                    PeriodEnum periodEnum = PeriodEnum.transferCategorySetId(sportId.intValue(), period == null ? 0 : period);
                    if (sportId.equals(SportTypeEnum.BASKETBALL.getCode().longValue())) {
                        //篮球比分
                        Long categorySetId = 0L;
                        if (null != periodEnum) {
                            categorySetId = periodEnum.getCategorySetId();
                        }
                        if (StringUtils.isNotBlank(setScore)) mapScore.put(categorySetId, setScore);
                        if (StringUtils.isNotBlank(score))
                            mapScore.put(PeriodEnum.FULL_TIME_2.getCategorySetId(), score);
                        if (StringUtils.isNotBlank(periodScore)) {
                            //上半场、下半场比分计算
                            if (Arrays.asList(13, 301, 14, 302, 1).contains(period)) {
                                mapScore.put(PeriodEnum.FIRST_HALF_2.getCategorySetId(), periodScore);
                            } else {
                                mapScore.put(PeriodEnum.SECOND_HALF_2.getCategorySetId(), periodScore);
                            }
                        }
                    }
                    if (sportId.equals(SportTypeEnum.FOOTBALL.getCode().longValue())) {

                        if (StringUtils.isNotBlank(score)) {
                            scoreClear(matchId, score, RCS_TASK_GOAL_SCORE, period);
                            footballRedisScore(matchId, FootballCategorySet.FULL_TIME.getCategorySetId(), score);
                        }
                        if (StringUtils.isNotBlank(redCardScore)) {
                            scoreClear(matchId, redCardScore, RCS_TASK_RED_SCORE, 0);
                        }
                        if (StringUtils.isNotBlank(cornerScore)) {
                            scoreClear(matchId, cornerScore, RCS_TASK_CORNER_SCORE, 0);
                        }
                        if (StringUtils.isNotBlank(yellowCardScore)) {
                            scoreClear(matchId, yellowCardScore, RCS_TASK_YELLOW_SCORE, 0);
                        }
                        if (StringUtils.isNotBlank(penaltyScore)) {
                            mapScore.put(PeriodEnum.PENALTY_SHOOT_1.getCategorySetId(), penaltyScore);
                            scoreClear(matchId, penaltyScore, RCS_TASK_KICK_SCORE, 0);
                            footballRedisScore(matchId, FootballCategorySet.PENALTY_SHOOT.getCategorySetId(), penaltyScore);
                        }

                        if (StringUtils.isNotBlank(setScore) && !"0:0".equals(setScore)) {
                            if (Arrays.asList(6).contains(period)) {
                                footballRedisScore(matchId, FootballCategorySet.FIRST_HALF.getCategorySetId(), setScore);
                            } else {
                                footballRedisScore(matchId, FootballCategorySet.SECOND_HALF.getCategorySetId(), setScore);
                            }
                        }

                        if (redCardScore == null) redCardScore = "0:0";
                        //罚牌比分 一张红牌2分 1张黄牌1分
                        redCardScore = (Integer.parseInt(redCardScore.split(":")[0]) * 2) + ":" + (Integer.parseInt(redCardScore.split(":")[1]) * 2);
                        String cardScore = getCardScoreNew(redCardScore, one.getYellowCardScore());
                        update.set("cardScore", cardScore);
                        mapScore.put(PeriodEnum.PENALTY_CARD_1.getCategorySetId(), cardScore);
                        matchPeriod.setCardScore(cardScore);
                    }
                    List<MatchCatgorySetVo> setInfos = one.getSetInfos();
                    if (period > 0) {
                        if(CollectionUtils.isEmpty(setInfos)&&sportId.intValue()==SportTypeEnum.FOOTBALL.getCode().intValue()){
                            //bug-37789 在没玩法下发时  没有玩法集  这里创建玩集并设置比分
                            List<MatchCatgorySetVo> setVos = new ArrayList<>();
                            setInfos = matchService.transferSetInfos(setVos, SportTypeEnum.FOOTBALL.getCode().longValue(), null);
                        }
                        if (!CollectionUtils.isEmpty(setInfos)) {
                            setInfos.stream().forEach(setVo -> {
                                setVo.setSort(PeriodEnum.getSort(setVo.getCatgorySetId()));
                                String s = mapScore.get(setVo.getCatgorySetId());
                                if (StringUtils.isNotBlank(s)) setVo.setScore(s);

                            });
                            if (null != periodEnum && periodEnum.getSort() > 0) {
                                setInfos = setInfos.stream().filter(vo -> vo.getSort() >= periodEnum.getSort()).collect(Collectors.toList());
                            }
                            update.set("setInfos", setInfos);
                        }
                    }
                }

                matchService.updateMongo(query, update);

                //mongotemplate.updateFirst(query, update, MatchMarketLiveBean.class);
    
                String riskManageCode = matchService.liveRiskManageCode(matchId);
                Boolean flag = true;
                if (matchPeriod.getSportId().equals(SportTypeEnum.FOOTBALL.getCode().longValue()) && "BTS".equals(riskManageCode)){
                    flag = false;
                }
                if (matchPeriod.getSportId().equals(SportTypeEnum.FOOTBALL.getCode().longValue()) && "OTS".equals(riskManageCode)){
                    flag = false;
                }
                if(flag){
                    sendCloseMarket(matchPeriod);
                }
                sw.stop();
                log.info("::{}::赛事阶段、比分、时间更新流程完成{},耗时" + sw.prettyPrint(), matchId, JSONObject.toJSONString(update), sw.getTotalTimeMillis());
            } else {
                //查不到数据，需要将当前数据暂时缓存下来，防止后面赛事出来，数据丢失
                String redisKey = "rcs:task:score:" + matchId;
                String redisVal = redisClient.get(redisKey);
                List<MatchPeriod> periodList = new ArrayList<MatchPeriod>();
                if (!StringUtils.isBlank(redisVal)) {
                    periodList = JSONObject.parseArray(redisVal, MatchPeriod.class);
                }
                periodList.add(matchPeriod);
                //缓存两个小时
                redisClient.setExpiry(redisKey, JSONObject.toJSONString(periodList), EXPRIY_TIME_2_HOURS);
            }
            //决胜局球头
            matchService.ballHeadConfigHandler(matchPeriod);
        } catch (Exception e) {
            log.error("MatchPeriodChangeConsumer接收更新比分异常,matchId:" + matchId + e.getMessage(), e);
        } finally {
            redissonManager.unlock(lock);
        }
    }


    public String getName(String newScore, String oldScore) {
        if (oldScore == null) {
            String[] scoreSplit = newScore.split(":");
            Integer score1 = Integer.parseInt(scoreSplit[0]);
            Integer score2 = Integer.parseInt(scoreSplit[1]);
            if (score1 > score2) return "home";
            if (score1 < score2) return "away";
            return null;
        }
        String[] scoreSplit = newScore.split(":");
        String[] periodScoreSplit = oldScore.split(":");
        Integer score1 = Integer.parseInt(scoreSplit[0]);
        Integer score2 = Integer.parseInt(scoreSplit[1]);
        Integer periodScore1 = Integer.parseInt(periodScoreSplit[0]);
        Integer periodScore2 = Integer.parseInt(periodScoreSplit[1]);
        if (score1 + score2 <= periodScore1 + periodScore2) {
            return null;
        }
        if (score1 > periodScore1) return "home";
        if (score2 > periodScore2) return "away";

        return null;
    }

    String getCardScoreNew(String redCardScore, String yellowRedCardScore) {
        if (StringUtils.isBlank(redCardScore)) redCardScore = "0:0";
        if (StringUtils.isBlank(yellowRedCardScore)) yellowRedCardScore = "0:0";

        String[] redCardSplit = redCardScore.split(":");
        Integer homeRedCard = Integer.parseInt(redCardSplit[0]);
        Integer awayRedCard = Integer.parseInt(redCardSplit[1]);

        String[] yellowCardSplit = yellowRedCardScore.split(":");
        Integer homeYellowCard = Integer.parseInt(yellowCardSplit[0]);
        Integer awayYellowCard = Integer.parseInt(yellowCardSplit[1]);
        return (homeRedCard + homeYellowCard) + ":" + (awayRedCard + awayYellowCard);
    }

    void scoreClear(Long matchId, String score, String redisKey, Integer period) {
        log.info("scoreClear 入参：matchId={},score={},redisKey={},period={}", matchId, score, redisKey, period);
        String riskManageCode = matchService.liveRiskManageCode(matchId);
        if (StringUtils.isNotBlank(riskManageCode) && "MTS".equals(riskManageCode)) {
            log.info("操盘模式：{}不封盘！",riskManageCode);
            return;
        }
        if (StringUtils.isNotBlank(riskManageCode) && "BTS".equals(riskManageCode)) {
            log.info("操盘模式：{}不封盘！",riskManageCode);
            return;
        }
        if (StringUtils.isNotBlank(riskManageCode) && "OTS".equals(riskManageCode)) {
            log.info("操盘模式：{}不封盘！",riskManageCode);
            return;
        }
        if (StringUtils.isNotBlank(riskManageCode) && "GTS".equals(riskManageCode)) {
            log.info("操盘模式：{}不封盘！",riskManageCode);
            return;
        }
        if (StringUtils.isNotBlank(score)) {
            if (!redisClient.setNX(redisKey + matchId + score, "1", 10L)) return;
            String redisVal = redisClient.get(redisKey + matchId);
            log.info("scoreClear redis获取比分：key={},value={}", redisKey + matchId, redisVal);
            if (StringUtils.isBlank(redisVal) && "0:0".equals(score)) return;
            if (!score.equals(redisVal)) {
                String playSetCode = "";
                if (redisKey.equals(RCS_TASK_GOAL_SCORE) && period != null
                        && Arrays.asList(32, 33, 41, 42, 110).contains(period)) {
                    playSetCode = "FOOTBALL_OVERTIME";
                } else if (redisKey.equals(RCS_TASK_GOAL_SCORE)) {
                    playSetCode = "FOOTBALL_GOAL";
                } else if (redisKey.equals(RCS_TASK_RED_SCORE)) {
                    playSetCode = "FOOTBALL_PENALTY_CARD";
                } else if (redisKey.equals(RCS_TASK_CORNER_SCORE)) {
                    playSetCode = "FOOTBALL_CORNER";
                } else if (redisKey.equals(RCS_TASK_KICK_SCORE)) {
                    playSetCode = "FOOTBALL_PENALTY_SHOOTOUT";
                } else if (redisKey.equals(RCS_TASK_YELLOW_SCORE)) {
                    playSetCode = "FOOTBALL_PENALTY_CARD";
                }

                //缓存两个小时
                redisClient.setExpiry(redisKey + matchId, score, EXPRIY_TIME_2_HOURS);
                log.info("清楚水差平衡值score:{},redisKey:{}", matchId, score, redisKey + matchId);
                List<Long> categoryIds = sportMarketCategoryService.queryCategoryIds(playSetCode);
                if (!CollectionUtils.isEmpty(categoryIds)) {
                    matchService.clearMarketDiffValue(matchId, categoryIds);
                }
                //比分變動玩法集封盤
                if (StringUtils.isNotBlank(playSetCode)) {
                    JSONObject obj = new JSONObject()
                            .fluentPut("tradeLevel", TradeLevelEnum.PLAY_SET_CODE.getLevel())
                            .fluentPut("matchId", matchId)
                            .fluentPut("playSetCode", playSetCode)
                            .fluentPut("status", NumberUtils.INTEGER_ONE.toString())
                            .fluentPut("linkedType", 17)
                            .fluentPut("remark", "比分变动玩法集封盘");

                    String linkIdCloseStatus = matchId + "_" + score + "_close";
                    Request<JSONObject> request = new Request<>();
                    request.setData(obj);
                    request.setLinkId(linkIdCloseStatus);
                    request.setDataSourceTime(System.currentTimeMillis());
                    mqMessage.sendMessage("RCS_TRADE_UPDATE_MARKET_STATUS", score, String.valueOf(matchId), request);
                }

            }
        }
    }

    void footballRedisScore(Long matchId, Long categorySetId, String score) {
        String key = String.format(RedisKey.RCS_TASK_MATCH_ALL_SCORE, matchId, categorySetId);
        redisClient.setExpiry(key, score, EXPRIY_TIME_2_HOURS);
    }

    void sendCloseMarket(MatchPeriod matchPeriod) {
        Long sportId = matchPeriod.getSportId();
        Long matchId = matchPeriod.getStandardMatchId();
        Integer period = matchPeriod.getPeriod();
        String score = matchPeriod.getScore();
        String cornerScore = matchPeriod.getCornerScore();
        String cardScore = matchPeriod.getCardScore();
        String yellowRedCardScore = matchPeriod.getYellowRedCardScore();
        String redCardScore = matchPeriod.getRedCardScore();
        String extraTimeScore = matchPeriod.getExtraTimeScore();
        String penaltyShootout = matchPeriod.getPenaltyShootout();
        Long secondsFromStart = matchPeriod.getSecondsFromStart();
        int FIFTEEN_MINS = 15 * 60;
        String setScore = matchPeriod.getSetScore();
        String periodScore = matchPeriod.getPeriodScore();
        String periodKey = RCS_TASK_MATCH_PERIOD + matchId;

        String periodValue = redisClient.get(periodKey);
        log.info("sendCloseMarket redis取值：key={},value={}", periodKey, periodValue);
        boolean isupdate = false;
        if (sportId.equals(SportTypeEnum.FOOTBALL.getCode().longValue())) {
            if (StringUtils.isNotBlank(periodValue)) {
                MatchPeriod matchPeriodRedis = JsonFormatUtils.fromJson(periodValue, MatchPeriod.class);
                if (StringUtils.isNotBlank(score) && !"0:0".equals(score) && !score.equals(matchPeriodRedis.getScore())) {
                    sendMqClose(score, Arrays.asList(336L, 28L, 30L, 31L, 148L, 357L), matchPeriod);
                }
                if (StringUtils.isNotBlank(cornerScore) && !"0:0".equals(cornerScore) && !cornerScore.equals(matchPeriodRedis.getCornerScore())) {
                    sendMqClose(cornerScore, Arrays.asList(225L, 120L, 125L, 230L), matchPeriod);
                }
                if (StringUtils.isNotBlank(cardScore) && !"0:0".equals(cardScore) && !cardScore.equals(matchPeriodRedis.getCardScore())) {
                    sendMqClose(cardScore, Arrays.asList(224L), matchPeriod);
                }
                if (StringUtils.isNotBlank(extraTimeScore) && !"0:0".equals(extraTimeScore) && !extraTimeScore.equals(matchPeriodRedis.getExtraTimeScore())) {
                    sendMqClose(extraTimeScore, Arrays.asList(235L), matchPeriod);
                }
                if (StringUtils.isNotBlank(penaltyShootout) && !"0:0".equals(penaltyShootout) && !penaltyShootout.equals(matchPeriodRedis.getPenaltyShootout())) {

                    sendMqClose(penaltyShootout, Arrays.asList(133L, 237L), matchPeriod);
                }
                isupdate = true;
            } else {
                if (StringUtils.isNotBlank(score) && !"0:0".equals(score)) {
                    sendMqClose(score, Arrays.asList(336L, 28L, 30L, 31L, 148L, 357L), matchPeriod);
                }
                if (StringUtils.isNotBlank(cornerScore) && !"0:0".equals(cornerScore)) {
                    sendMqClose(cornerScore, Arrays.asList(225L, 120L, 125L, 230L), matchPeriod);
                }
                if (StringUtils.isNotBlank(cardScore) && !"0:0".equals(cardScore)) {
                    sendMqClose(cardScore, Arrays.asList(224L), matchPeriod);
                }
                if (StringUtils.isNotBlank(extraTimeScore) && !"0:0".equals(extraTimeScore)) {
                    sendMqClose(extraTimeScore, Arrays.asList(235L), matchPeriod);
                }
                if (StringUtils.isNotBlank(penaltyShootout) && !"0:0".equals(penaltyShootout)) {
                    sendMqClose(penaltyShootout, Arrays.asList(133L, 237L), matchPeriod);
                }

            }

//            if (secondsFromStart != null) {
//                int intValue = secondsFromStart.intValue();
//                int pa = intValue / FIFTEEN_MINS;
//                int left = 60;
//                if (secondsFromStart < FIFTEEN_MINS) {
//                    left = FIFTEEN_MINS - intValue;
//                } else {
//                    left = FIFTEEN_MINS - intValue % FIFTEEN_MINS;
//                }
//                if (left < 60) {
//                    sendMqClose(score, Arrays.asList(32L, 33L, 34L, 233L, 231L, 232L), matchPeriod);
//                }
//                isupdate = true;
//            }
        }
        if (sportId.equals(SportTypeEnum.BASKETBALL.getCode().longValue())) {
            if (StringUtils.isNotBlank(periodValue)) {
                MatchPeriod matchPeriodRedis = JsonFormatUtils.fromJson(periodValue, MatchPeriod.class);
                if (StringUtils.isNotBlank(score) && !"0:0".equals(score) && !score.equals(matchPeriodRedis.getScore())) {
                    sendMqClose(score, Arrays.asList(201L, 214L), matchPeriod);
                }
                if ((StringUtils.isNotBlank(setScore) && !"0:0".equals(setScore) && !setScore.equals(matchPeriodRedis.getSetScore())) ||
                        (period != null && !period.equals(matchPeriodRedis.getPeriod()))) {
                    sendMqClose(setScore, Arrays.asList(145L, 146L, 147L, 215L), matchPeriod);
                }
                isupdate = true;
            } else {
                if (StringUtils.isNotBlank(score) && !"0:0".equals(score)) {
                    sendMqClose(score, Arrays.asList(201L, 214L), matchPeriod);
                }
                if ((StringUtils.isNotBlank(setScore) && !"0:0".equals(setScore)) || (period != null)) {
                    sendMqClose(setScore, Arrays.asList(145L, 146L, 147L, 215L), matchPeriod);
                }
            }
        }
        if (isupdate) {
            if (StringUtils.isNotBlank(periodValue)) {
                MatchPeriod matchPeriodRedis = JsonFormatUtils.fromJson(periodValue, MatchPeriod.class);
                if (period != null) matchPeriodRedis.setPeriod(period);
                if (secondsFromStart != null) matchPeriodRedis.setSecondsFromStart(secondsFromStart);
                if (StringUtils.isAllBlank(score)) matchPeriodRedis.setScore(score);
                if (StringUtils.isAllBlank(cornerScore)) matchPeriodRedis.setCornerScore(score);
                if (StringUtils.isAllBlank(cardScore)) matchPeriodRedis.setCardScore(cardScore);
                if (StringUtils.isAllBlank(yellowRedCardScore)) matchPeriodRedis.setYellowCardScore(yellowRedCardScore);
                if (StringUtils.isAllBlank(redCardScore)) matchPeriodRedis.setRedCardScore(redCardScore);
                if (StringUtils.isAllBlank(extraTimeScore)) matchPeriodRedis.setExtraTimeScore(extraTimeScore);
                if (StringUtils.isAllBlank(penaltyShootout)) matchPeriodRedis.setPenaltyShootout(penaltyShootout);
                if (StringUtils.isAllBlank(setScore)) matchPeriodRedis.setSetScore(setScore);
                if (StringUtils.isAllBlank(periodScore)) matchPeriodRedis.setPeriodScore(periodScore);
                matchPeriod = matchPeriodRedis;
            }
        }
        redisClient.setExpiry(periodKey, JsonFormatUtils.toJson(matchPeriod), EXPRIY_TIME_2_HOURS);


    }

    void sendMqClose(String score, List<Long> categoryIds, MatchPeriod matchPeriod) {
        log.info("sendMqClose 入参：score={},categoryIds={},matchPeriod={}", score, categoryIds, JsonFormatUtils.toJson(matchPeriod));
        //matchPeriod.setScore(score);
        matchPeriod.setCategoryIds(categoryIds);
        List<Long> manualList = new ArrayList<>();
        Long matchId = matchPeriod.getStandardMatchId();
        categoryIds.forEach(id -> {
            RcsTradeConfig rcsTradeConfig = rcsTradeConfigMapper.selectRcsTradeConfig(String.valueOf(matchId), id.toString(), "");
            if (rcsTradeConfig != null && rcsTradeConfig.getDataSource() != null && rcsTradeConfig.getDataSource().equals(DataSourceTypeEnum.MANUAL.getValue())) {
                manualList.add(id);
            }
        });
        log.info("手动操盘关盘" + manualList.toString());
        if (manualList.size() > 0) {
            JSONObject obj = new JSONObject().fluentPut("tradeLevel", TradeLevelEnum.SCORE_EVENT.getLevel())
                    .fluentPut("matchId", matchId)
                    .fluentPut("playIdList", manualList)
                    .fluentPut("status", NumberUtils.INTEGER_TWO.toString())
                    .fluentPut("linkedType", 17)
                    .fluentPut("remark", "进球手动玩法数据源关盘")
                    .fluentPut("matchPeriod", matchPeriod);

            String linkIdCloseStatus = matchId + "_" + score + "_close";
            Request<JSONObject> request = new Request<>();
            request.setData(obj);
            request.setLinkId(linkIdCloseStatus);
            request.setDataSourceTime(System.currentTimeMillis());
            mqMessage.sendMessage("RCS_TRADE_UPDATE_MARKET_STATUS", score, String.valueOf(matchId), request);
        }

        //带X玩法关盘
        //mqMessage.sendMessage("RCS_TASK_DATASOURCE_MARKET_CLOSE", "", String.valueOf(matchPeriod.getStandardMatchId()), matchPeriod);
    }

    List<BaseBallScoreVo> transferBaseBallScore(Map<String, Map<String, String>> scoreMap, Update update, List<BaseBallScoreVo> baseBallScoreVos) {
        List<BaseBallScoreVo> newScoreVos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(scoreMap)) {
            for (Map.Entry<String, Map<String, String>> entry : scoreMap.entrySet()) {
                String key = entry.getKey();
                Map<String, String> map = entry.getValue();
                String matchScore = map.get("matchScore");
                if ("0".equals(key) && StringUtils.isNotBlank(matchScore)) {
                	update.set("score", matchScore);
                }
                BaseBallScoreVo vo = new BaseBallScoreVo();
                vo.setPeriod(Integer.valueOf(key));
                    if(map.containsKey("firstBase")){
                        vo.setFirstBase(Integer.valueOf(map.get("firstBase").substring(0, map.get("firstBase").indexOf(":"))));
                    }
                    if(map.containsKey("secondBase")){
                        vo.setSecondBase(Integer.valueOf(map.get("secondBase").substring(0, map.get("secondBase").indexOf(":"))));
                    }
                    if(map.containsKey("thirdBase")){
                        vo.setThirdBase(Integer.valueOf(map.get("thirdBase").substring(0, map.get("thirdBase").indexOf(":"))));
                    }
                vo.setMatchScore(matchScore);
                vo.setSetScore(map.get("setScore"));
                newScoreVos.add(vo);
            }
            if (CollectionUtils.isEmpty(baseBallScoreVos)) {
                baseBallScoreVos = new ArrayList<>();
                baseBallScoreVos.addAll(newScoreVos);
            } else {
            	// 更新的阶段比分
                for (BaseBallScoreVo newVo : newScoreVos) {
                	boolean isUpdate = false;
                	// 已经存在的阶段比分
					for (BaseBallScoreVo baseBallScoreVo : baseBallScoreVos) {
						// 如果当前的比赛阶段比分已经存在，重新set
						if (baseBallScoreVo.getPeriod() != null
								&& baseBallScoreVo.getPeriod().equals(newVo.getPeriod())) {
							if (StringUtils.isNotEmpty(newVo.getMatchScore()) && !newVo.getMatchScore().equalsIgnoreCase(baseBallScoreVo.getMatchScore())) {
								baseBallScoreVo.setMatchScore(newVo.getMatchScore());
							}
							if (StringUtils.isNotEmpty(newVo.getSetScore()) && !newVo.getSetScore().equalsIgnoreCase(baseBallScoreVo.getSetScore())) {
								baseBallScoreVo.setSetScore(newVo.getSetScore());
							}
							if (newVo.getFirstBase() != null && !newVo.getFirstBase().equals(baseBallScoreVo.getFirstBase())) {
								baseBallScoreVo.setFirstBase(newVo.getFirstBase());
							}
							if (newVo.getSecondBase() != null && !newVo.getSecondBase().equals(baseBallScoreVo.getSecondBase())) {
								baseBallScoreVo.setSecondBase(newVo.getSecondBase());
							}
							if (newVo.getThirdBase() != null && !newVo.getThirdBase().equals(baseBallScoreVo.getThirdBase())) {
								baseBallScoreVo.setThirdBase(newVo.getThirdBase());
							}
							// 当前newScoreVos数据是否已经更新
							isUpdate = true;
							break;
						}
					}
					if (!isUpdate) {
						baseBallScoreVos.add(newVo);
					}
                }
            }
        }
        return baseBallScoreVos;
    }


    List<ScoreVo> transferScoreVo(Map<String, Map<String, String>> scoreMap, Update update) {
        List<ScoreVo> scoreVos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(scoreMap)) {
            for (Map.Entry<String, Map<String, String>> entry : scoreMap.entrySet()) {
                String key = entry.getKey();

                Map<String, String> map = entry.getValue();

                /**
                 currentScore 小分 matchScore 盘分 setScore 局比分 qiangScore  抢分
                 */
                String currentScore = map.get("currentScore");
                String matchScore = map.get("matchScore");
                String setScore1 = map.get("setScore");
                String qiangScore = map.get("qiangScore");
                if ("0".equals(key)) {
                    if (StringUtils.isNotBlank(matchScore)) update.set("score", matchScore);
                } else {
                    ScoreVo scoreVo = new ScoreVo();
                    if (StringUtils.isNotBlank(currentScore)) {
                        String[] split = currentScore.trim().split(":");
                        if (Integer.parseInt(split[0]) > 40 || Integer.parseInt(split[1]) > 40) {
                            String home = split[0];
                            String away = split[1];
                            if (Integer.parseInt(split[0]) > 40) {
                                home = "A";
                            }
                            if (Integer.parseInt(split[1]) > 40) {
                                away = "A";
                            }
                            scoreVo.setCurrentScore(home + ":" + away);
                        } else {
                            scoreVo.setCurrentScore(currentScore);
                        }
                    }
                    if (StringUtils.isNotBlank(matchScore)) scoreVo.setMatchScore(matchScore);
                    if (StringUtils.isNotBlank(setScore1)) scoreVo.setSetScore(setScore1);
                    if (StringUtils.isNotBlank(qiangScore)) scoreVo.setQiangScore(qiangScore);
                    scoreVo.setPeriod(Integer.parseInt(key));
                    scoreVos.add(scoreVo);
                }
            }
            return scoreVos;
        }
        return scoreVos;
    }

    /**
     * 乒乓球分数自动关盘
     * @param matchId
     * @param scoreMap
     * @param period
     */
    private void pingpongAchieveScorePlayClose(Long matchId,Map<String, Map<String, String>> scoreMap,Integer period){
        try{
            log.info("乒乓球分数自动关盘,matchId:{},scoreMap:{},period:{}",matchId,JSONObject.toJSONString(scoreMap),period);
            String periodStr = period.toString();
            List<Integer> scores = new ArrayList<>();
            String setScore = "";
            if(scoreMap != null && scoreMap.get(periodStr) != null && scoreMap.get(periodStr).get("setScore") != null){
                setScore = scoreMap.get(periodStr).get("setScore");
                if(StringUtils.isNotBlank(setScore)) {
                    //获取比分 如 4:5
                    scores = Arrays.asList(setScore.trim().split(":")).stream().map(Integer::parseInt).collect(Collectors.toList());
                }
            }
            if (CollectionUtils.isEmpty(scores)){
                return;
            }

            int firstNum = getPlayNum(periodStr);
            //X玩法id
            List<Long> playIds = Lists.newArrayList(175L, 176L, 177L, 178L, 179L, 203L);
            //查询子玩法配置分数与状态
            log.info("乒乓球分数自动关盘获取配置参数:{}",matchId);
            List<MatchTemplatePlayConfigDTO> matchTemplatePlayConfigDTOS = templatePlayMargainRefMapper.selectTemplatePlayMatchId(matchId,playIds);
            log.info("乒乓球分数自动关盘获取配置结果:{}",JSONObject.toJSONString(matchTemplatePlayConfigDTOS));

            Map<String, MatchTemplatePlayConfigDTO> playMap = new HashMap<>();
            if(matchTemplatePlayConfigDTOS.size() >0){
                playMap = matchTemplatePlayConfigDTOS.stream().collect(Collectors.toMap(x -> x.getPlayId()+"#"+getPlayNum(x.getTimeVal().toString()),y ->y));
            }

            List<String> playTypes = Arrays.asList("05", "10", "15");
            for (Long playId : playIds) {
                MatchTemplatePlayConfigDTO configDTO = playMap.get(playId+"#"+firstNum);
                Integer idx = firstNum;
                while (configDTO == null){
                    configDTO = playMap.get(playId+"#"+ --idx);
                    if(idx == 0 && configDTO == null){
                        break;
                    }
                }
                List<String> subPlayIds = new ArrayList<>();
                String firstNumStr = String.format("%02d", firstNum);
                String subPlayIdStr = playId + firstNumStr;
                if(Arrays.asList(179L,203L).contains(playId)){
                    subPlayIds = playTypes.stream().map(x -> subPlayIdStr + x).collect(Collectors.toList());
                }else{
                    subPlayIds = Arrays.asList(subPlayIdStr);
                }

                //达到关盘分数
                Integer achieveCloseScore = configDTO.getAchieveCloseScore();
                //是否自动关盘
                Integer isAutoCloseScoreConfig = configDTO.getIsAutoCloseScoreConfig();

                if (null != isAutoCloseScoreConfig && isAutoCloseScoreConfig.intValue() == 0 && null != achieveCloseScore){
                    //比分情况 其中一方比分达到设定值，且其中一方小于设定值才生效
                    Integer maxScore = scores.stream().max(Comparator.comparing(Integer::intValue)).get();
                    boolean flag = maxScore.compareTo(achieveCloseScore) == 0;

                    String redisKey = String.format(RedisKey.RCS_TASK_MATCH_PINGPONG_ACHIEVE_CLOSE_SCORE,matchId,playId,subPlayIdStr);
                    String redisVal = redisClient.get(redisKey);
                    if (flag && StringUtils.isBlank(redisVal)){
                        redisClient.setExpiry(redisKey, "1", EXPRIY_TIME_2_HOURS);
                        for (String subPlayId : subPlayIds) {
                            //达到关盘设置分数
                            JSONObject obj = new JSONObject()
                                    .fluentPut("tradeLevel", TradeLevelEnum.PLAY.getLevel())
                                    .fluentPut("matchId", matchId)
                                    .fluentPut("playId", playId)
                                    .fluentPut("subPlayId", subPlayId)
                                    .fluentPut("status", TradeStatusEnum.CLOSE.getStatus())
                                    .fluentPut("linkedType", 21)
                                    .fluentPut("remark", "乒乓球达到设定的分数进行关盘处理,当前比分为"+setScore+",配置的分数为"+achieveCloseScore);
                            Request<JSONObject> request = new Request<>();
                            request.setData(obj);
                            request.setLinkId(matchId + "_" + playId + "_" + subPlayId);
                            request.setDataSourceTime(System.currentTimeMillis());
                            log.info("乒乓球分数自动关盘发送消息:{}",JSONObject.toJSONString(request));
                            mqMessage.sendMessage("RCS_TRADE_UPDATE_MARKET_STATUS", matchId + "_PERIOD_ACHIEVE_SCORE_CLOSE", request.getLinkId(), request);
                        }
                   }
                }
            }

        }catch (Exception e){
            log.error("MatchPeriodChangeConsumer乒乓球达到设定的分数进行关盘处理异常,matchId:" + matchId + e.getMessage(), e);
        }
    }

    /**
     * 转换局数
     * @param period
     * @return
     */
    private Integer getPlayNum(String period){
        Integer firstNum = 0;
        if (period.equals("8") || period.equals("0")) {
            firstNum = 1;
        } else if (period.equals("9")) {
            firstNum = 2;
        } else if (period.equals("10")) {
            firstNum = 3;
        } else if (period.equals("11")) {
            firstNum = 4;
        }else if (period.equals("12")) {
            firstNum = 5;
        }else if (period.equals("441")) {
            firstNum = 6;
        }else if (period.equals("442")) {
            firstNum = 7;
        }
        return firstNum;
    }

    private void fifteenMinutesPlayClose(Long sportId, Long matchId, Long secondsFromStart) {
        if (!SportIdEnum.isFootball(sportId) || matchId == null || secondsFromStart == null) {
            return;
        }
        if (!triggerCloseTimeRange(secondsFromStart, 1) &&
                !triggerCloseTimeRange(secondsFromStart, 2) &&
                !triggerCloseTimeRange(secondsFromStart, 3) &&
                !triggerCloseTimeRange(secondsFromStart, 4) &&
                !triggerCloseTimeRange(secondsFromStart, 5) &&
                !triggerCloseTimeRange(secondsFromStart, 6)) {
            return;
        }
        String key = "rcs:task:15MinutesSubPlayCloseFlag:" + matchId;
        Map<String, String> hashMap = (Map<String, String>) redisClient.hGetAllToObj(key);
        log.info("Redis获取15分钟子玩法关盘标志：key={},hashMap={}", key, JSON.toJSONString(hashMap));
        if (CollectionUtils.isEmpty(hashMap)) {
            hashMap = Maps.newHashMap();
        }
        int stage = 0;
        for (int i = 6; i > 0; i--) {
            if (triggerCloseTimeRange(secondsFromStart, i)) {
                String flag = hashMap.get(String.valueOf(i));
                if (StringUtils.isBlank(flag)) {
                    // 赛事进行到第i阶段尾声 且 第i阶段玩法未关盘
                    stage = i;
                    break;
                }
            }
        }
        if (stage <= 0) {
            return;
        }
        redisClient.hSet(key, String.valueOf(stage), "1");
        redisClient.expireKey(key, (int) TimeUnit.DAYS.toSeconds(1));

        List<Long> playIds = Lists.newArrayList(32L, 33L, 34L, 231L, 232L, 233L);
        for (Long playId : playIds) {
            long subPlayId = playId * 100 + stage;
            JSONObject obj = new JSONObject()
                    .fluentPut("tradeLevel", TradeLevelEnum.PLAY.getLevel())
                    .fluentPut("matchId", matchId)
                    .fluentPut("playId", playId)
                    .fluentPut("subPlayId", subPlayId)
                    .fluentPut("status", TradeStatusEnum.CLOSE.getStatus())
                    .fluentPut("linkedType", 20)
                    .fluentPut("remark", "15分钟子玩法关盘");
            Request<JSONObject> request = new Request<>();
            request.setData(obj);
            request.setLinkId(matchId + "_" + playId + "_" + subPlayId);
            request.setDataSourceTime(System.currentTimeMillis());
            mqMessage.sendMessage("RCS_TRADE_UPDATE_MARKET_STATUS", secondsFromStart + "_" + stage, request.getLinkId(), request);
        }
    }

    private boolean triggerCloseTimeRange(Long secondsFromStart, int stage) {
        // 阶段结束前一分钟，结束后两分钟，共三分钟内
        return secondsFromStart >= TimeUnit.MINUTES.toSeconds(stage * 15 - 1) &&
                secondsFromStart <= TimeUnit.MINUTES.toSeconds(stage * 15 + 2);
    }

    private Map<Long, Integer> getTradeMode(Long matchId, Collection<Long> playIds) {
        List<String> playIdList = null;
        if (CollectionUtils.isNotEmpty(playIds)) {
            playIdList = playIds.stream().map(String::valueOf).collect(Collectors.toList());
        }
        List<RcsTradeConfig> list = rcsTradeConfigMapper.getTradeMode(matchId.toString(), playIdList);
        if (CollectionUtils.isEmpty(list)) {
            return Maps.newHashMap();
        }
        return list.stream().collect(Collectors.toMap(config -> NumberUtils.toLong(config.getTargerData()), RcsTradeConfig::getDataSource));
    }



}
