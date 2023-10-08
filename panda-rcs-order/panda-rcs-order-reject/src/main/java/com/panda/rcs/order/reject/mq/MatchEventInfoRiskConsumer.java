package com.panda.rcs.order.reject.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;
import com.panda.merge.dto.Request;
import com.panda.rcs.order.reject.constants.NumberConstant;
import com.panda.rcs.order.reject.constants.RedisKey;
import com.panda.rcs.order.reject.entity.RcsTemplateEventInfoConfig;
import com.panda.rcs.order.reject.mapper.RcsTemplateEventInfoConfigMapper;
import com.panda.rcs.order.reject.service.RejectTemplateAcceptConfigServer;
import com.panda.rcs.order.reject.utils.RedisUtils;
import com.panda.rcs.order.reject.utils.SendMessageUtils;
import com.panda.sport.data.rcs.vo.MatchEventInfo;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.StandardMatchTeamRelationMapper;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardMatchTeamRelation;
import com.panda.sport.rcs.pojo.enums.MatchEventConfigEnum;
import com.panda.sport.rcs.pojo.reject.RcsGoalWarnSet;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 足球赛事事件广播
 *
 * @author admin
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "MATCH_EVENT_INFO_TO_REJECT",
        consumerGroup = "MATCH_EVENT_INFO_TO_REJECT_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchEventInfoRiskConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Resource
    RcsTemplateEventInfoConfigMapper rcsTemplateEventInfoConfigMapper;
    @Resource
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RejectTemplateAcceptConfigServer templateAcceptConfigServerImpl;

    @Autowired
    private SendMessageUtils sendMessage;

    @Resource
    private StandardMatchTeamRelationMapper standardMatchTeamRelationMapper;

    //缓存赛事信息
    private static Map<String, StandardMatchInfo> matchCache = new HashMap<>(1000);

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMqPushConsumer) {
        defaultMqPushConsumer.setConsumeThreadMin(64);
        defaultMqPushConsumer.setConsumeThreadMax(126);
    }

    @Override
    public void onMessage(String message) {
        String linkId = "nullStr";
        Long matchId = Long.parseLong("-1");
        try {
            Request<List<MatchEventInfo>> requests = JSON.parseObject(message, new TypeReference<Request<List<MatchEventInfo>>>() {
            });
            linkId = requests.getLinkId();
            List<MatchEventInfo> data = requests.getData();
            matchId = data.get(NumberConstant.NUM_ZERO).getStandardMatchId();
            if (!CollectionUtils.isEmpty(data)) {
                if (data.get(NumberConstant.NUM_ZERO).getSportId() != NumberConstant.NUM_ONE) {
                    log.info("linkId::{}::,赛事ID:{},不是足球事件", linkId, matchId);
                    return;
                }
                log.info("linkId::{}::,赛事ID:{},开始MATCH_EVENT_INFO_TO_RISK消费", linkId, matchId);
                for (MatchEventInfo matchEventInfo : data) {
                    if (matchEventInfo.getDataSourceCode().equalsIgnoreCase(MatchEventConfigEnum.EVENT_SOURCE_BC.getCode()) ||
                            matchEventInfo.getDataSourceCode().equalsIgnoreCase(MatchEventConfigEnum.EVENT_SOURCE_SR.getCode()) ||
                            matchEventInfo.getDataSourceCode().equalsIgnoreCase(MatchEventConfigEnum.EVENT_SOURCE_BG.getCode()) ||
                            matchEventInfo.getDataSourceCode().equalsIgnoreCase(MatchEventConfigEnum.EVENT_SOURCE_RB.getCode()) ||
                            matchEventInfo.getDataSourceCode().equalsIgnoreCase(MatchEventConfigEnum.EVENT_SOURCE_KO.getCode()) ||
                            matchEventInfo.getDataSourceCode().equalsIgnoreCase(MatchEventConfigEnum.EVENT_SOURCE_PD.getCode()) ||
                            matchEventInfo.getDataSourceCode().equalsIgnoreCase(MatchEventConfigEnum.EVENT_SOURCE_PD_TWO.getCode())) {
                        matchEventInfo.setCreateTime(System.currentTimeMillis());

                        StandardMatchInfo match = getMatchInfo(String.valueOf(matchId));
                        if (match == null) {
                            log.warn("linkId::{}::,赛事ID:{},不存在标准赛事表里", linkId, matchId);
                            return;
                        }

                        if (match.getSportId() != null && !"1".equals(String.valueOf(match.getSportId()))) {
                            log.warn("linkId::{}::,赛事ID:{},不是足球事件，暂时弃用", linkId, matchId);
                            return;
                        }

                        log.info("linkId::{}::,赛事ID:{},根据MQ更新足球赛事事件", linkId, matchId);
                        //当前事件处理
                        String redisKey = String.format(RedisKey.REDIS_EVENT_INFO, matchEventInfo.getDataSourceCode(), matchEventInfo.getStandardMatchId());
                        //获取到上一个当前事件
                        String obj = RcsLocalCacheUtils.getValueInfo(redisKey);
                        RcsLocalCacheUtils.timedCache.put(redisKey, JSON.toJSONString(matchEventInfo), RedisKey.ORDINARY_TIME_OUT);
                        //足球特殊事件处理
                        if (matchEventInfo.getSportId() == 1) {
                            String matchKey = String.format(RedisKey.MATCH_EVENT_KOALA_REDIS_KEY, matchEventInfo.getStandardMatchId());
                            if (StringUtils.equalsIgnoreCase(matchEventInfo.getEventCode(), "var_reason") && StringUtils.equalsIgnoreCase(matchEventInfo.getExtraInfo(), "1001")) {
                                RcsLocalCacheUtils.timedCache.put(matchKey, "var_reason", RedisKey.ODDS_CACHE_TIME_OUT);
                            } else if (StringUtils.equalsIgnoreCase(matchEventInfo.getExtraInfo(), "1001")) {
                                RcsLocalCacheUtils.timedCache.put(matchKey, "10001", RedisKey.ODDS_CACHE_TIME_OUT);
                            }
                        }
                        //缓存上一次事件,如果上次为空，则缓存当前事件到上一次事件
                        MatchEventInfo matchLastEventInfo = obj == null ? matchEventInfo : JSON.parseObject(obj, MatchEventInfo.class);
                        updateLastEventInfo(matchLastEventInfo);

                        //2536进球点注单预警&审核功能
                        goalBettingWarn(linkId, matchEventInfo, match);

                    } else {
                        log.info("linkId::{}::,赛事ID:{},其他数据源数据不处理", linkId, JSONObject.toJSONString(matchEventInfo));
                    }
                }
            }

        } catch (Exception e) {
            log.error("linkId::{}::,赛事ID:{},消费MATCH_EVENT_INFO_TO_RISK异常,参数={},", linkId, matchId, message, e);
        }
    }


    /**
     * 查询标准赛事
     *
     */
    private StandardMatchInfo getMatchInfo(String matchId) {
        if (matchCache.size() > 1000) {
            matchCache.clear();
        }
        if (matchCache.get(matchId) != null) {
            return matchCache.get(matchId);
        } else {
            StandardMatchInfo info = standardMatchInfoMapper.selectById(matchId);
            if (info != null) {
                matchCache.put(matchId, info);
            }
            return info;
        }
    }


    /**
     * 缓存上一次事件数据
     */
    private void updateLastEventInfo(MatchEventInfo event) {
        List<RcsTemplateEventInfoConfig> configs = rcsTemplateEventInfoConfigMapper.selectList(new LambdaQueryWrapper<RcsTemplateEventInfoConfig>().eq(RcsTemplateEventInfoConfig::getRejectType, 1).eq(RcsTemplateEventInfoConfig::getEventCode, event.getEventCode()));
        if (CollectionUtils.isNotEmpty(configs)) {
            String lastRedisKey = String.format(RedisKey.MATCH_LAST_TIME_EVENT_CODE, event.getDataSourceCode(), event.getStandardMatchId());
            RcsLocalCacheUtils.timedCache.put(lastRedisKey, JSON.toJSONString(event), RedisKey.ORDINARY_TIME_OUT);
            log.info("::{}::根据MQ更新上一次足球赛事事件::{}", lastRedisKey, JSONObject.toJSONString(event));
        }
    }

    /**
     * 2536进球点注单预警&审核功能
     * 1.判断预警设置条件当前事件code为goal(进球事件)
     * 2.判断当前事件赛事是否属于配置中的赛事
     * 3.取得注单时设置的缓存信息
     * 4.判断符合注单人数是否满足预警设置注单人数
     * 5.最后条件符合要求则发送给业务进行注单状态变更(变更为待审核)
     */
    private void goalBettingWarn(String linkId, MatchEventInfo event, StandardMatchInfo match) {
        long startTime = System.currentTimeMillis();
        //判断当前事件code为goal(进球事件)
        if (!"goal".equals(event.getEventCode())) {
            return;
        }
        Long standardTournamentId = match.getStandardTournamentId();

        //根据赛事id,主客队获取球队id
        String teamIdStr = RcsLocalCacheUtils.getValue(String.format(RedisKey.RCS_MATCH_POSITION_TEAM_ID, event.getStandardMatchId(), event.getHomeAway()), (k) -> {
            LambdaQueryWrapper<StandardMatchTeamRelation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StandardMatchTeamRelation::getStandardMatchId, event.getStandardMatchId());
            wrapper.eq(StandardMatchTeamRelation::getMatchPosition, event.getHomeAway());
            wrapper.select(StandardMatchTeamRelation::getStandardTeamId);
            StandardMatchTeamRelation teamInfo = standardMatchTeamRelationMapper.selectOne(wrapper);
            return Objects.nonNull(teamInfo) ? String.valueOf(teamInfo.getStandardTeamId()) : "";
        }, RedisKey.CACHE_TIME_OUT);
        log.info("::{}::进球点预警获取缓存赛事球队ID数据:{},主客场:{}", event.getStandardMatchId(), teamIdStr, event.getHomeAway());
        if (StringUtils.isBlank(teamIdStr)) {
            return;
        }

        //根据联赛id,赛事id,球队id查询预警设置缓存,缓存没有从数据库取
        RcsGoalWarnSet rcsGoalWarnSet = templateAcceptConfigServerImpl.getGoalWarnSet(standardTournamentId, event.getStandardMatchId(), teamIdStr);
        log.info("linkId::{}::,赛事ID:{},进球点预警获取预警设置,请求参数:tournamentId={},matchId={},teamId={},返回数据:{}", linkId, event.getStandardMatchId(), standardTournamentId, event.getStandardMatchId(), teamIdStr, JSONObject.toJSONString(rcsGoalWarnSet));
        if(Objects.isNull(rcsGoalWarnSet)){
            return;
        }

        //取得注单时设置的缓存信息
        String redisKey = String.format(RedisKey.RCS_GOAL_WARN_SET_USER, standardTournamentId, event.getStandardMatchId(), teamIdStr);
        Map<String, String> cacheMap = redisUtils.hgetAll(redisKey);
        if (Objects.nonNull(cacheMap) && cacheMap.size() > NumberConstant.NUM_ZERO) {
            //根据缓存数据统计符合要求的注单人数 进球事件时间 - 注单时间 <= 预警设置的进球前秒数*1000
            Long conformBetUserNum = cacheMap.entrySet().stream().filter(item -> (event.getEventTime() - Long.valueOf(item.getValue())) <= (Long.valueOf(rcsGoalWarnSet.getBeforeGoalSeconds()) * 1000)).count();
            log.info("linkId::{}::,赛事ID:{},进球点预警获取注单时设置的缓存,缓存key:{},缓存数据量:{},符合条件数据量:{}", linkId, event.getStandardMatchId(), redisKey, cacheMap.size(), conformBetUserNum);
            //判断符合注单人数是否满足预警设置注单人数
            if(conformBetUserNum >= Long.valueOf(rcsGoalWarnSet.getBetUserNum())){
                //发送给业务进行注单状态变更(变更为待审核)
                Map<String, Object> map = Maps.newHashMap();
                map.put("matchId", event.getStandardMatchId());
                map.put("startTime", (event.getEventTime() - (Long.valueOf(rcsGoalWarnSet.getBeforeGoalSeconds()) * 1000)));
                map.put("endTime", event.getEventTime());
                map.put("betStatus", 8);
                log.info("linkId::{}::,赛事ID:{},进球点预警发送给业务进行注单状态变更,发送数据:{},耗时:{}", linkId, event.getStandardMatchId(), JSONObject.toJSONString(map), System.currentTimeMillis() - startTime);
                sendMessage.sendMessage(RedisKey.RCS_GOAL_WARNING_TIME_PERIOD, RedisKey.RCS_GOAL_WARNING_TIME_PERIOD_GROUP, String.valueOf(event.getStandardMatchId()), map);
            }
        }else{
            log.info("linkId::{}::,赛事ID:{},进球点预警获取注单时设置的缓存,缓存key:{},没有获取到缓存数据,耗时:{}", linkId, event.getStandardMatchId(), redisKey, System.currentTimeMillis() - startTime);
        }
    }

    public static void main(String[] args) {


        String lastEventInfoStr = "{\n" +
                "\t\t\t\"id\": 1646845622129258498,\n" +
                "\t\t\t\"sportId\": 1,\n" +
                "\t\t\t\"canceled\": 0,\n" +
                "\t\t\t\"dataSourceCode\": \"BG\",\n" +
                "\t\t\t\"eventCode\": \"dangerous_attack\",\n" +
                "\t\t\t\"eventTime\": 1681473567076,\n" +
                "\t\t\t\"extraInfo\": \"\",\n" +
                "\t\t\t\"homeAway\": \"home\",\n" +
                "\t\t\t\"matchPeriodId\": 7,\n" +
                "\t\t\t\"player1Id\": null,\n" +
                "\t\t\t\"player1Name\": null,\n" +
                "\t\t\t\"player2Id\": null,\n" +
                "\t\t\t\"player2Name\": null,\n" +
                "\t\t\t\"secondsFromStart\": 3943,\n" +
                "\t\t\t\"standardMatchId\": 2460239,\n" +
                "\t\t\t\"standardTeamId\": 34728,\n" +
                "\t\t\t\"t1\": 51,\n" +
                "\t\t\t\"t2\": 27,\n" +
                "\t\t\t\"thirdEventId\": \"734\",\n" +
                "\t\t\t\"thirdMatchId\": \"1646395470708822017\",\n" +
                "\t\t\t\"thirdMatchSourceId\": \"10118038\",\n" +
                "\t\t\t\"thirdTeamId\": null,\n" +
                "\t\t\t\"remark\": null,\n" +
                "\t\t\t\"createTime\": 1681473568104,\n" +
                "\t\t\t\"modifyTime\": 1681473567884,\n" +
                "\t\t\t\"periodRemainingSeconds\": 0,\n" +
                "\t\t\t\"addition1\": null,\n" +
                "\t\t\t\"addition2\": null,\n" +
                "\t\t\t\"addition3\": null,\n" +
                "\t\t\t\"addition4\": null,\n" +
                "\t\t\t\"addition5\": null,\n" +
                "\t\t\t\"addition6\": null,\n" +
                "\t\t\t\"addition7\": null,\n" +
                "\t\t\t\"addition8\": null,\n" +
                "\t\t\t\"addition9\": null,\n" +
                "\t\t\t\"addition10\": null,\n" +
                "\t\t\t\"secondNum\": null,\n" +
                "\t\t\t\"firstT1\": null,\n" +
                "\t\t\t\"firstT2\": null,\n" +
                "\t\t\t\"secondT1\": null,\n" +
                "\t\t\t\"secondT2\": null,\n" +
                "\t\t\t\"firstNum\": null\n" +
                "\t\t}";
        MatchEventInfo lastMatchEventInfo = JSONObject.parseObject(lastEventInfoStr, MatchEventInfo.class);

        System.out.println(lastMatchEventInfo);
    }
}
