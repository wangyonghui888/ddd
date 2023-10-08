package com.panda.sport.rcs.task.mq.impl.match;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.TOrderDetailExtMapper;
import com.panda.sport.rcs.pojo.odd.StandardMarketMessage;
import com.panda.sport.rcs.pojo.odd.StandardMarketOddsMessage;
import com.panda.sport.rcs.pojo.odd.StandardMatchMarketMessage;
import com.panda.sport.rcs.pojo.odd.StandardMatchMessage;
import com.panda.sport.rcs.pojo.odds.RcsStandardMarketDTO;
import com.panda.sport.rcs.task.config.RedissonManager;
import com.panda.sport.rcs.task.mq.bean.DataRealTimeMessageBean;
import com.panda.sport.rcs.task.utils.NameExpressionValueUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 赛事盘口赔率改变更新redis
 *
 * @author ENZO
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "STANDARD_MARKET_ODDS",
        consumerGroup = "STANDARD_MARKET_ODDS_TO_REDIS",
        consumeThreadMax = 512,
        consumeTimeout = 10000L)
public class MatchMarketOddsChange2RedisConsumer implements RocketMQListener<String> {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    protected TOrderDetailExtMapper tOrderDetailExtMapper;

    @Autowired
    private RedissonManager redissonManager;

    private String REDIS_MATCH_MARKET_ODDS = "rcs:playId:%s:redis:match:%s:odds";
    private String REDIS_MATCH_MARKET_ODDS_TIME = "rcs:redis:playId:%s:match:%s:odds:time";
    private String REDIS_MATCH_MARKET_ODDS_TIME_LOCK = "rcs:redis:playId:%s:match:%s:odds:time:lock";
    private static final String REDIS_MATCH_INFO = "rcs:redis:standard:match:%s";
    //1666需求
    private String REDIS_MATCH_MARKET_ODDS_NEW = "rcs:redis:playId:%s:match:%s:odds:new";

    @Override
    public void onMessage(String message) {
        String linkId = "";
        try {
            DataRealTimeMessageBean<StandardMatchMarketMessage> msg = JSONObject.parseObject(message, new TypeReference<DataRealTimeMessageBean<StandardMatchMarketMessage>>() {
            });
            StandardMatchMarketMessage matchMessage = msg.getData();
            Long datasourceTimeMsg = msg.getDataSourceTime();
            linkId = msg.getLinkId();
            Long matchId = matchMessage.getStandardMatchInfoId();
            //1666需求
            StandardMatchMessage standardMatchMessage = new StandardMatchMessage();
            BeanUtils.copyProperties(matchMessage, standardMatchMessage);
            String matchKey = String.format(REDIS_MATCH_INFO, standardMatchMessage.getStandardMatchInfoId());
            redisClient.set(matchKey, JSON.toJSONString(standardMatchMessage));
            redisClient.expireKey(matchKey, 7200);
            List<StandardMarketMessage> marketList = matchMessage.getMarketList();
            if (!CollectionUtils.isEmpty(marketList)) {
                Map<Long, List<StandardMarketMessage>> map = marketList.stream().collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));
                    for (Long playId : map.keySet()) {
                        // 增加时间戳校验
                        String dataSourceTime = redisClient.get(String.format(REDIS_MATCH_MARKET_ODDS_TIME, playId, matchId));
                        if (StringUtils.isNotBlank(dataSourceTime) && Long.parseLong(dataSourceTime) > datasourceTimeMsg) {
                            log.info("::{}::,过期赔率不处理", linkId);
                            continue;
                        }
                        redisClient.setExpiry(String.format(REDIS_MATCH_MARKET_ODDS_NEW, playId, matchId), JSON.toJSONString(map.get(playId)), 7200L);
                        redisClient.setExpiry(String.format(REDIS_MATCH_MARKET_ODDS_TIME, playId, matchId), String.valueOf(msg.getDataSourceTime()), 7200L);
                        log.info("::{}::缓存成功:dataSourceTime:{},val={}", linkId, datasourceTimeMsg, map.get(playId));
                    }
            }
        } catch (Exception e) {
            log.error("::{}::赔率下发更新redis异常:", linkId, e);
        }
    }
}
