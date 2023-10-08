package com.panda.sport.rcs.task.mq.impl.match;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.task.mq.bean.StandardMatchSwitchStatusMessage;
import com.panda.sport.rcs.task.service.MatchServiceImpl;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import static com.panda.sport.rcs.constants.RedisKey.EXPRIY_TIME_2_HOURS;
import static com.panda.sport.rcs.constants.RedisKey.RCS_TASK_MATCH_LIVE;

/**
 * @author Administrator
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "STANDARD_MATCH_SWITCH_STATUS",
        consumerGroup = "rcs_task_STANDARD_MATCH_SWITCH_STATUS",
        consumeThreadMax = 256,
        consumeTimeout = 10000L)
public class SwitchPreStatusConsumer implements RocketMQListener<String> {
    @Autowired
    MongoTemplate mongotemplate;

    @Autowired
    MatchServiceImpl matchService;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;

    @Override
    public void onMessage(String message) {
        Long matchId = null;
        try {
            Request<StandardMatchSwitchStatusMessage> msg = JSONObject.parseObject(message, new TypeReference<Request<StandardMatchSwitchStatusMessage>>() {});
            StandardMatchSwitchStatusMessage data = msg.getData();
            Integer oddsLive = data.getOddsLive();
            matchId = data.getStandardMatchId();
            //下发滚球标识
            redisClient.setExpiry(String.format("rcs:matchInfo:oddsLive:status:%s",matchId), oddsLive, EXPRIY_TIME_2_HOURS);
            log.info("::{}::STANDARD_MATCH_SWITCH_STATUS赛前状态切换:{}",matchId, message);
            Query query = new Query();
            query.addCriteria(Criteria.where("matchId").is(matchId));
            MatchMarketLiveBean one = mongotemplate.findOne(query, MatchMarketLiveBean.class);
            log.info("STANDARD_MATCH_SWITCH_STATUS::{}::mango数据:{}",matchId, JSONObject.toJSONString(one));
            StandardMatchInfo matchInfo = new StandardMatchInfo();
            if (oddsLive != null && (oddsLive == 1 || oddsLive == 0) && one != null) {
                Update update = new Update();
                update.set("oddsLive", oddsLive);
                if (oddsLive == 1) {
                    update.set("matchStatus", 1);
                    matchInfo.setMatchStatus(1);
                    matchInfo.setOddsLive(1);
                    //kir-赛前进滚球时，修改联赛模板默认参数（bug 23014）
                    //producerSendMessageUtils.sendMessage("SYNC_TEMPLATE_PRE_TO_LIVE_TOPIC", matchId);
                } else if (oddsLive == 0) {
                    update.set("matchStatus", 0);
                    matchInfo.setMatchStatus(0);
                    matchInfo.setOddsLive(0);
                }
                //赛事状态缓存
                redisClient.setExpiry(RCS_TASK_MATCH_LIVE + matchId, 1, EXPRIY_TIME_2_HOURS);
                //update.set("operateMatchStatus", MarketStatusEnum.SEAL.getState());
                //更新赛事表
                standardMatchInfoMapper.updateById(matchInfo);
                //更新mongo
                matchService.updateMongo(query, update);
//                mongotemplate.updateFirst(query, update, MatchMarketLiveBean.class);
                //滚球操作
                matchService.updateCategories(matchId);
            }else{
                //赛事还未同步 缓存早盘切滚球封盘状态
                redisClient.setExpiry(String.format("rcs:match:oddsLive:trade:seal:%s",matchId), 1, EXPRIY_TIME_2_HOURS);
                log.info("::{}::STANDARD_MATCH_SWITCH_STATUS赛前状态切换错误:mongodb赛事不存在",matchId);
            }
        } catch (Exception e) {
            log.error("::{}::STANDARD_MATCH_SWITCH_STATUS赛前状态切换异常:{}",matchId,JSONObject.toJSONString(message), e);
        }
    }
}

/*    @Autowired
    MongoTemplate mongotemplate;

    @Autowired
    MatchServiceImpl matchService;

    @Autowired
    private RedisClient redisClient;

    public SwitchPreStatusConsumer() {
        super("STANDARD_MATCH_SWITCH_STATUS", "");
    }

    @Override
    @Trace
    public Boolean handleMs(Request<StandardMatchSwitchStatusMessage> msg, Map<String, String> paramsMap) throws Exception {
        try {
            log.info("赛前状态切换:{}", JsonFormatUtils.toJson(msg));
            StandardMatchSwitchStatusMessage data = msg.getData();
            Integer oddsLive = data.getOddsLive();
            Long matchId = data.getStandardMatchId();

            Query query = new Query();
            query.addCriteria(Criteria.where("matchId").is(matchId));
            MatchMarketLiveBean one = mongotemplate.findOne(query, MatchMarketLiveBean.class);
            if (oddsLive != null && (oddsLive == 1 || oddsLive == 0) && one != null) {
                Update update = new Update();
                update.set("oddsLive", oddsLive);
                if(oddsLive==1){
                    update.set("matchStatus", 1);
                }else if(oddsLive==0){
                    update.set("matchStatus", 0);
                }
                //update.set("operateMatchStatus", MarketStatusEnum.SEAL.getState());
                //赛事状态缓存
                redisClient.setExpiry(RCS_TASK_MATCH_LIVE + matchId, 1, EXPRIY_TIME_2_HOURS);
                //更新mongo
                matchService.updateMongo(query,update);
                //mongotemplate.updateFirst(query, update, MatchMarketLiveBean.class);
                //滚球操作
                matchService.updateCategories(matchId);
            }
        } catch (Exception e) {
            log.error(e.getMessage() + JsonFormatUtils.toJson(msg), e);
        }
        return true;
    }
}*/
